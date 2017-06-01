package declaratiiavere;

import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.InputStream;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import static declaratiiavere.TextFileToObjectMappingConstants.*;

/**
 * Caches the text file to object mapping information from text-file-to-object-mapping.xml. Provides
 * methods for accessing the cached information, accesible only to classes from same package.
 *
 * @author Razvan Dani
 */
class TextFileToObjectMappingCache {

    private static Map<FileType, TextFileMappingInfo> textFileMappingInfoByFileTypeMap; // contains FileType's as keys and TextFileMappingInfo objects as values
    private static Set<String> possibleTrueValues; // Set with String's that are mapped to boolean true value.

    static {
        cacheFileMappingInfo();
        cachePossibleTrueValues();
    }

    /**
     * Returns the TextFileMappingInfo object representing the file-to-object mapping for the given FileType.
     *
     * @param fileType The FileType
     * @return The TextFileMappingInfo object
     */
    static TextFileMappingInfo getTextFileMappingInfo(FileType fileType) {
        return textFileMappingInfoByFileTypeMap.get(fileType);
    }

    /**
     * Returns true if the fieldValue parameter is one of:  "Y", "Yes", "1", "true"
     *
     * @param fieldValue the field value to check
     * @return boolean      true if fieldValue parameter has one of the mentioned value
     */
    static boolean isTrueValue(String fieldValue) {
        return possibleTrueValues.contains((fieldValue != null) ? fieldValue.toLowerCase() : null);
    }

    /**
     * Parses the text-file-to-object-mapping.xml file, and caches the data read from it.
     */
    private static void cacheFileMappingInfo() {
        try {
            textFileMappingInfoByFileTypeMap = new HashMap<TextFileToObjectMappingConstants.FileType, TextFileMappingInfo>();

            ClassLoader classLoader = TextFileToObjectMappingCache.class.getClassLoader();
            InputStream textFileToObjectMappingXmlStream = classLoader.getResourceAsStream(TEXT_FILE_TO_OBJECT_MAPPING_XML_FILE_NAME);

            SAXBuilder builder = new SAXBuilder(false);
            Document doc = builder.build(textFileToObjectMappingXmlStream);
            Element rootElement = doc.getRootElement();
            Iterator mappingElementIterator = rootElement.getChildren("mapping").iterator();

            List<TextFileMappingInfo.FieldInfo> fieldInfoList;

            while (mappingElementIterator.hasNext()) {
                Element mappingElement = (Element) mappingElementIterator.next();

                TextFileMappingInfo textFileMappingInfo = new TextFileMappingInfo();
                FileFormat fileFormat = FileFormat.getConstantByValue(mappingElement.getAttribute("fileFormat").getValue());
                String className = mappingElement.getAttribute("className").getValue();
                Class beanClass = Class.forName(className);
                textFileMappingInfo.setBeanClass(beanClass);

                if (mappingElement.getAttribute("fileEncoding") != null
                        && !mappingElement.getAttribute("fileEncoding").getValue().equals("")) {
                    textFileMappingInfo.setFileEncoding(mappingElement.getAttribute("fileEncoding").getValue());
                }

                if (mappingElement.getAttribute("fileCellDelimiter") != null
                        && !mappingElement.getAttribute("fileCellDelimiter").getValue().equals("")) {
                    textFileMappingInfo.setCellDelimiter(mappingElement.getAttribute("fileCellDelimiter").getValue().toCharArray()[0]);
                }

                if (mappingElement.getAttribute("fileCellQualifier") != null
                        && !mappingElement.getAttribute("fileCellQualifier").getValue().equals("")) {
                    textFileMappingInfo.setCellQualifier(mappingElement.getAttribute("fileCellQualifier").getValue().toCharArray()[0]);
                }

                if (mappingElement.getAttribute("hasTitleRecord") != null) {
                    textFileMappingInfo.setHasTitleRecord(mappingElement.getAttribute("hasTitleRecord").getValue().toLowerCase().equals("true"));
                }

                if (mappingElement.getAttribute("tableNumber") != null) {
                    textFileMappingInfo.setTableNumber(Integer.parseInt(mappingElement.getAttribute("tableNumber").getValue()));
                }

                if (mappingElement.getAttribute("childNumber") != null) {
                    textFileMappingInfo.setChildNumber(Integer.parseInt(mappingElement.getAttribute("childNumber").getValue()));
                }

                if (mappingElement.getAttribute("tabName") != null
                        && !mappingElement.getAttribute("tabName").getValue().equals("")) {
                    textFileMappingInfo.setFileEncoding(mappingElement.getAttribute("tabName").getValue());
                }

                fieldInfoList = new ArrayList<TextFileMappingInfo.FieldInfo>();
                Iterator allChildrenIterator = mappingElement.getChildren().iterator();
                int emptyElementIndex = 0;

                while (allChildrenIterator.hasNext()) {
                    Element element = (Element) allChildrenIterator.next();

                    TextFileMappingInfo.FieldInfo fieldInfo = new TextFileMappingInfo.FieldInfo();

                    if (element.getName().equals("fieldMapping")) {
                        String fieldName = element.getAttribute("fileFieldName").getValue();
                        Attribute attributeName = element.getAttribute("objectAttributeName");
                        fieldInfo.setFieldName(fieldName);
                        Method getMethod = beanClass.getMethod(makeGetMethodName(attributeName.getValue()));
                        Method setMethod = beanClass.getMethod(makeSetMethodName(attributeName.getValue()), getMethod.getReturnType());
                        fieldInfo.setSetterMethod(setMethod);
                        fieldInfo.setGetterMethod(getMethod);
                        TextFileFieldAttributeType attributeType =
                                TextFileFieldAttributeType.getConstantByValue(getMethod.getReturnType().getName());
                        fieldInfo.setAttributeType(attributeType);

                        if (attributeType == TextFileFieldAttributeType.DATE) {
                            Attribute datePatternAttribute = element.getAttribute("datePattern");

                            if (datePatternAttribute != null) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat(datePatternAttribute.getValue(), Locale.ENGLISH);
                                dateFormat.setLenient(false);
                                fieldInfo.setDateFormat(dateFormat);
                            } else {
                                throw new RuntimeException("Error at parsing the configuration file: no datePattern defined for the Date field..");
                            }
                        }

                        if (element.getAttribute("writeBooleanTrueValue") != null
                                && !element.getAttribute("writeBooleanTrueValue").getValue().equals("")) {
                            fieldInfo.setWriteBooleanTrueValue(element.getAttribute("writeBooleanTrueValue").getValue());
                        }

                        if (element.getAttribute("writeBooleanFalseValue") != null
                                && !element.getAttribute("writeBooleanFalseValue").getValue().equals("")) {
                            fieldInfo.setWriteBooleanFalseValue(element.getAttribute("writeBooleanFalseValue").getValue());
                        }

                        if (element.getAttribute("bigDecimalScale") != null
                                && !element.getAttribute("bigDecimalScale").getValue().equals("")) {
                            fieldInfo.setScale(new Integer(element.getAttribute("bigDecimalScale").getValue()));
                        }

                        if (element.getAttribute("columnWidth") != null
                                && !element.getAttribute("columnWidth").getValue().equals("")) {
                            fieldInfo.setColumnWidth(new Integer(element.getAttribute("columnWidth").getValue()));
                        }
                    } else if (element.getName().equals("emptyField")) {
                        fieldInfo.setFieldName("ANONYMOUS_" + emptyElementIndex);
                        emptyElementIndex++;
                    }

                    if (element.getAttribute("fieldLength") != null
                            && !element.getAttribute("fieldLength").getValue().equals("")) {
                        fieldInfo.setFieldLength(new Integer(element.getAttribute("fieldLength").getValue()));
                    }

                    if (element.getAttribute("leftPaddingCharacter") != null
                            && !element.getAttribute("leftPaddingCharacter").getValue().equals("")) {
                        fieldInfo.setLeftPaddingCharacter(element.getAttribute("leftPaddingCharacter").getValue().charAt(0));
                    }

                    fieldInfoList.add(fieldInfo);
                }

                textFileMappingInfo.setFieldInfoList(fieldInfoList);

                textFileMappingInfo.setFlatPackMappingXmlContent(getFlatPackMappingXmlContent(fieldInfoList));

                FileType fileType = FileType.getConstantByValue(mappingElement.getAttribute("fileType").getValue());
                textFileMappingInfoByFileTypeMap.put(fileType, textFileMappingInfo);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs and returns the XML content representing the FlatPack API XML mapping where the field names are specified,
     * in the correct order, along with the field lengths if the format of the file is fixed length.
     *
     * @param fieldInfoList List of TextFileMappingInfo.FieldInfo
     * @return
     */
    private static String getFlatPackMappingXmlContent(List<TextFileMappingInfo.FieldInfo> fieldInfoList) {
        Element rootElement = new Element("PZMAP");

        for (TextFileMappingInfo.FieldInfo fieldInfo : fieldInfoList) {
            Element columnElement = new Element("COLUMN");
            columnElement.setAttribute("name", fieldInfo.getFieldName());

            if (fieldInfo.getFieldLength() != null) {
                columnElement.setAttribute("length", fieldInfo.getFieldLength().toString());
            }

            rootElement.addContent(columnElement);
        }

        XMLOutputter xmlOutputter = new XMLOutputter();
        DocType docType = new DocType("PZMAP", "");

        return xmlOutputter.outputString(new Document(rootElement, docType));
    }

    private static void cachePossibleTrueValues() {
        possibleTrueValues = new HashSet<String>();
        possibleTrueValues.add("y");
        possibleTrueValues.add("yes");
        possibleTrueValues.add("1");
        possibleTrueValues.add("true");
    }

    /**
     * Generates the get method name for an attribute name
     *
     * @param attributeName The name of the attribute
     * @return String           The name of the get method
     */
    private static String makeGetMethodName(String attributeName) {
        return "get" + attributeName.replaceFirst(attributeName.substring(0, 1), attributeName.substring(0, 1).toUpperCase());
    }

    /**
     * Generates the set method name for an attribute name
     *
     * @param attributeName The name of the attribute
     * @return String           The name of the set method
     */
    private static String makeSetMethodName(String attributeName) {
        return "set" + attributeName.replaceFirst(attributeName.substring(0, 1), attributeName.substring(0, 1).toUpperCase());
    }
}


