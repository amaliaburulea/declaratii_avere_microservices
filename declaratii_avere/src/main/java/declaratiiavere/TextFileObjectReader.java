package declaratiiavere;

import net.sf.flatpack.DataError;
import net.sf.flatpack.DataSet;
import net.sf.flatpack.Parser;
import net.sf.flatpack.brparse.BuffReaderDelimParser;
import net.sf.flatpack.brparse.BuffReaderFixedParser;
import net.sf.flatpack.brparse.BuffReaderParseFactory;
import net.sf.flatpack.util.FPConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

import static declaratiiavere.TextFileToObjectMappingConstants.*;

/**
 * This class reads information from a text files, converted into java bean objects,
 * based on the mapping file text-file-to-object-mapping.xml.
 *
 * @author Anca Tirnovan
 * @since Marmot
 */
public class TextFileObjectReader {
    private String fileLocation; // the location of the file
    private Parser parser; // Parser object, used to parse the file
    private DataSet dataSet; // DataSet object, used to read the values from the text file
    private Map<String, Integer> columnNameByColumnPositionMap; // map containing the column names if exist by column position in file
    private TextFileMappingInfo textFileMappingInfo; // The TextFileMappingInfo object representing the file-to-object mapping for the file
    private Object lastReadBean; // the last bean that was read by readNextObject, exposed via getCurrentObject
    private Set<String> ignoreReadFieldNameSet = new LinkedHashSet<String>(); // Set of field names to ignore reading, populated dynamically when reading fails for the 1st time for a field
    private Integer objectCount;

    /**
     * @param fileType     - The type of the file
     * @param fileLocation - The location (path and name) of the file about to be read
     */
    public TextFileObjectReader(FileType fileType, String fileLocation) {
        this.fileLocation = fileLocation;
        this.textFileMappingInfo = TextFileToObjectMappingCache.getTextFileMappingInfo(fileType);
    }


    /**
     * Starts reading from file
     */
    public void startReading() {
        try {
            // reset the read object count
            objectCount = 0;
            startReadingTextFile();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops reading from file and closes the parser.
     */
    public void stopReading() {

        try {
            stopReadingTextFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Logs any errors that occured during reading through the file.
     * Not null only in case of Text files.<p/>
     */
    public void logDataSetErrors() {
        if (dataSet != null && dataSet.getErrorCount() > 0 && dataSet.getErrors() != null) {

            for (DataError dataError : (List<DataError>) dataSet.getErrors()) {
                String logMessage = "Line = " + dataError.getLineNo() + ", Description =  " + dataError.getErrorDesc();
                System.out.println("ERROR logMessage = " + logMessage);
            }
        }
    }

    /**
     * Reads the next object if end of file is not reached, otherwise returns null. The Object returned (if not null) is
     * an instance of the class specified in the mapping xml.
     *
     * @return the Object read or null if end of file is reached.
     */
    public Object readNextObject() {
        Object readBean;

        try {
            readBean = readNextObjectFromTextFile();

            if (readBean != null && objectCount != null) {
                objectCount++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return readBean;
    }

    /**
     * @return the last bean that was read by readNextObject, exposed via getCurrentObject
     */
    public Object getLastReadObject() {

        if (lastReadBean == null) {
            readNextObject();
        }

        return lastReadBean;
    }

    /**
     * Obtains the processed file location including file name.<p/>
     *
     * @return The file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Indicates how many records where read from the file.<p/>
     *
     * @return The number of records read from file
     */
    public Integer getObjectCount() {
        return objectCount;
    }

    private void setReadBeanAttribute(Object readBean, String fieldStringValue, TextFileFieldAttributeType attributeType,
                                      Method setterMethod, String fieldName, SimpleDateFormat dateFormat) {
        try {
            if (attributeType == TextFileFieldAttributeType.STRING) {
                if (OU.e2null(textFileMappingInfo.getFileEncoding()) != null
                        && !textFileMappingInfo.isHasTitleRecord()) {
                    String encodedStringValue =
                            new String(fieldStringValue.getBytes(), textFileMappingInfo.getFileEncoding());
                    setterMethod.invoke(readBean, encodedStringValue);
                } else {
                    setterMethod.invoke(readBean, fieldStringValue);
                }
            } else if (attributeType == TextFileFieldAttributeType.INTEGER) {
                try {
                    if (OU.e2null(fieldStringValue) != null) {
                        setterMethod.invoke(readBean, new Integer(fieldStringValue));
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println(fieldStringValue + " is not a valid integer value for field " + fieldName);
                }
            } else if (attributeType == TextFileFieldAttributeType.BIG_DECIMAL) {
                if (OU.e2null(fieldStringValue) != null) {
                    try {
                        setterMethod.invoke(readBean, new BigDecimal(fieldStringValue));
                    } catch (NumberFormatException nfe) {
                        System.out.println(dataSet.getString(fieldName) + " is not a valid decimal value for field " + fieldName);
                    }
                }
            } else if (attributeType == TextFileFieldAttributeType.DATE) {
                if (dateFormat == null) {
                    System.out.println("datePattern is required in the mapping xml for date fields");
                }

                try {
                    setterMethod.invoke(readBean, dataSet.getDate(fieldName, dateFormat));
                } catch (ParseException pe) {
                    System.out.println(dataSet.getString(fieldName) + " is not a valid date value for field " + fieldName);
                }
            } else if (attributeType == TextFileFieldAttributeType.BOOLEAN
                    || attributeType == TextFileFieldAttributeType.BOOLEAN_PRIMITIVE) {
                String booleanValue = dataSet.getString(fieldName);

                if (TextFileToObjectMappingCache.isTrueValue(booleanValue)) {
                    setterMethod.invoke(readBean, Boolean.TRUE);
                } else {
                    setterMethod.invoke(readBean, Boolean.FALSE);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts reading from the text file.<p/>
     *
     * @throws FileNotFoundException        -
     * @throws UnsupportedEncodingException -
     */
    private void startReadingTextFile() throws FileNotFoundException, UnsupportedEncodingException {
        try {
            Reader textFileReader = null;

            // instantiate the textFileReader if appropriate / applicable
            if (textFileMappingInfo.isHasTitleRecord()) {
                if (OU.e2null(textFileMappingInfo.getFileEncoding()) != null) {
                    textFileReader = new InputStreamReader(new FileInputStream(fileLocation), textFileMappingInfo.getFileEncoding());
                } else {
                    textFileReader = new FileReader(fileLocation);
                }
            }

            // construct the parser for each case

            if (textFileMappingInfo.isHasTitleRecord()) {
                parser = BuffReaderParseFactory.getInstance().newDelimitedParser(textFileReader,
                        textFileMappingInfo.getCellDelimiter(),
                        (textFileMappingInfo.getCellQualifier() != null) ?
                                textFileMappingInfo.getCellQualifier() : FPConstants.NO_QUALIFIER);
            } else {
                parser =
                        BuffReaderParseFactory.getInstance().newDelimitedParser(
                                new ByteArrayInputStream(textFileMappingInfo.getFlatPackMappingXmlContent().getBytes()),
                                new FileInputStream(fileLocation),
                                textFileMappingInfo.getCellDelimiter(),
                                (textFileMappingInfo.getCellQualifier() != null) ?
                                        textFileMappingInfo.getCellQualifier() : FPConstants.NO_QUALIFIER,
                                false); //don't ignore the first record
                // NOTE: the newDelimitedParse method that uses Readers and is not deprecated, simply doesn't work
                // with whatever Readers wehere used in the attempts to parse FlatPack mappingxml
                // NOTE also that fileEconding cannot be used at this point and is handled (for delimited files
                // without title record) when actually reading the String fields in readNextObject
            }
            // obtain the dataSet
            dataSet = parser.parse();
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        } catch (UnsupportedEncodingException uee) {
            throw uee;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The metod closed the text file parser and logs errors found.<p/>
     */
    private void stopReadingTextFile() {
        try {
            logDataSetErrors();

            if (parser instanceof BuffReaderDelimParser) {
                ((BuffReaderDelimParser) parser).close();
            } else if (parser instanceof BuffReaderFixedParser) {
                ((BuffReaderFixedParser) parser).close();
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Reads next object from the text file into the mapped bean.<p/>
     *
     * @return The object conteining the current record form the file
     * @throws InstantiationException -
     * @throws IllegalAccessException -
     */
    private Object readNextObjectFromTextFile() throws InstantiationException, IllegalAccessException {
        Object readBean;

        if (dataSet.next()) {
            readBean = textFileMappingInfo.getBeanClass().newInstance();

            for (TextFileMappingInfo.FieldInfo fieldInfo : textFileMappingInfo.getFieldInfoList()) {
                String fieldName = fieldInfo.getFieldName();

                if (!ignoreReadFieldNameSet.contains(fieldName)) {
                    TextFileFieldAttributeType attributeType = fieldInfo.getAttributeType();
                    Method setterMethod = fieldInfo.getSetterMethod();
                    String fieldStringValue = null;

                    try {
                        fieldStringValue = dataSet.getString(fieldName);
                    } catch (Exception e) {
                        System.out.println("Cannot read value for field name " + fieldName + " in file " + fileLocation +
                                ".The field probably does not existing in the file. In rest of records reading this field will be skipped");
                        e.printStackTrace();
                        ignoreReadFieldNameSet.add(fieldName);

                    }

                    if (fieldStringValue != null) {
                        setReadBeanAttribute(readBean, fieldStringValue, attributeType, setterMethod, fieldName, fieldInfo.getDateFormat());
                    }
                }
            }

            this.lastReadBean = readBean;
        } else {
            // if there aren't any rows in the file, return null
            readBean = null;
        }

        return readBean;
    }


}

