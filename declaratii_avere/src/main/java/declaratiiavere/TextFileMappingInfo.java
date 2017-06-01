package declaratiiavere;

import java.util.List;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import static declaratiiavere.TextFileToObjectMappingConstants.*;

/**
 * Java bean that encapsulates the file-to-object mapping for one type of file.
 *
 * @author Razvan Dani
 */
class TextFileMappingInfo {
    private boolean hasTitleRecord; // if true, it means the file has a title record that contains the column names,
    // can be true only for delimited format
    private String fileEncoding; // optional, when set, this encoding will be used when reading String's
    private Class beanClass; // The class of the Java Bean objects associated with this file mapping
    private List<FieldInfo> fieldInfoList; // The List of TextFileFieldInfo representing the information for each field from the file
    private Character cellDelimiter; // The cell delimiter, required and relevant for delimited text files
    private Character cellQualifier; // The cell qualifier, optional and relevant for delimited text files
    private String flatPackMappingXmlContent; // Except when reading delimited files that have title record, where this xml content is irrelevant,
    // the FlatPack API needs a mapping xml
    // where the names and the order of the fields is specified,
    // plus the length of the fields in case of fixed length files
    private Integer tableNumber;    // indicates the number of the table on the html page (starts from 1)
    private Integer childNumber;    // html format only - indicates how deep is the text nested inside the td element
    // e.g.: if the text is put inside a span element then childNumber value will be 1,
    //       if the text is directly in the td element the childNumber value will be 0.
    private String tabName;       // indicates the name of the excel sheet

    boolean isHasTitleRecord() {
        return hasTitleRecord;
    }

    void setHasTitleRecord(boolean hasTitleRecord) {
        this.hasTitleRecord = hasTitleRecord;
    }

    String getFileEncoding() {
        return fileEncoding;
    }

    void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    Class getBeanClass() {
        return beanClass;
    }

    void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    List<FieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    void setFieldInfoList(List<FieldInfo> fieldInfoList) {
        this.fieldInfoList = fieldInfoList;
    }

    Character getCellDelimiter() {
        return cellDelimiter;
    }

    void setCellDelimiter(Character cellDelimiter) {
        if(cellDelimiter.equals('T')){
            cellDelimiter = '\t';
        }
        this.cellDelimiter = cellDelimiter;
    }

    Character getCellQualifier() {
        return cellQualifier;
    }

    void setCellQualifier(Character cellQualifier) {
        this.cellQualifier = cellQualifier;
    }

    String getFlatPackMappingXmlContent() {
        return flatPackMappingXmlContent;
    }

    void setFlatPackMappingXmlContent(String flatPackMappingXmlContent) {
        this.flatPackMappingXmlContent = flatPackMappingXmlContent;
    }

    Integer getTableNumber() {
        return tableNumber;
    }

    void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    Integer getChildNumber() {
        return childNumber;
    }

    void setChildNumber(Integer childNumber) {
        this.childNumber = childNumber;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }


    /**
     * Java bean that encapsulates the information for a field in a text file.
     *
     * @author Razvan Dani
     */
    static class FieldInfo {
        private String fieldName;  // the name of the field or Anonymous_1, Anonymous_2... if isEmpty = true
        private boolean isEmpty;   //true, if it is an empty field
        private Method setterMethod; // the set method corresponding for this field
        private Method getterMethod; // the get method corresponding for this field
        private TextFileFieldAttributeType attributeType; // The TextFileFieldAttributeType for the method
        private SimpleDateFormat dateFormat; // the date format in case the field is of Date type
        private Integer fieldLength; // the length of the field, required and relevant for fixed length format files
        private Character leftPaddingCharacter; // optional and relevant for fixed length format, when specified the cell is
        // filled to the left with the specified padding character to fill the lenght
        private String writeBooleanTrueValue; // relevant for boolean and Boolean attributes writen to text files, indicates
        // what should be written when the boolean is true. Default is 1 when writting true booleans
        private String writeBooleanFalseValue; // relevant for boolean and Boolean attributes writen to text files, indicates
        // what should be written when the boolean is false. Default is 0 when writting false booleans
        private Integer scale; //relevant for BigDecimal attributes
        // the width of the column in the result file
        private Integer columnWidth;

        String getFieldName() {
            return fieldName;
        }

        void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        boolean isEmpty() {
            return isEmpty;
        }

        void setIsEmpty(boolean isEmpty) {
            this.isEmpty = isEmpty;
        }

        SimpleDateFormat getDateFormat() {
            return dateFormat;
        }

        void setDateFormat(SimpleDateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }

        Method getSetterMethod() {
            return setterMethod;
        }

        void setSetterMethod(Method setterMethod) {
            this.setterMethod = setterMethod;
        }

        Method getGetterMethod() {
            return getterMethod;
        }

        void setGetterMethod(Method getterMethod) {
            this.getterMethod = getterMethod;
        }

        TextFileFieldAttributeType getAttributeType() {
            return attributeType;
        }

        void setAttributeType(TextFileFieldAttributeType attributeType) {
            this.attributeType = attributeType;
        }

        Integer getFieldLength() {
            return fieldLength;
        }

        void setFieldLength(Integer fieldLength) {
            this.fieldLength = fieldLength;
        }

        Character getLeftPaddingCharacter() {
            return leftPaddingCharacter;
        }

        void setLeftPaddingCharacter(Character leftPaddingCharacter) {
            this.leftPaddingCharacter = leftPaddingCharacter;
        }

        String getWriteBooleanTrueValue() {
            return writeBooleanTrueValue;
        }

        void setWriteBooleanTrueValue(String writeBooleanTrueValue) {
            this.writeBooleanTrueValue = writeBooleanTrueValue;
        }

        String getWriteBooleanFalseValue() {
            return writeBooleanFalseValue;
        }

        void setWriteBooleanFalseValue(String writeBooleanFalseValue) {
            this.writeBooleanFalseValue = writeBooleanFalseValue;
        }

        Integer getScale() {
            return scale;
        }

        void setScale(Integer scale) {
            this.scale = scale;
        }

        public Integer getColumnWidth() {
            return columnWidth;
        }

        public void setColumnWidth(Integer columnWidth) {
            this.columnWidth = columnWidth;
        }
    }
}


