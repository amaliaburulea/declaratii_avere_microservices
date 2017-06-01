package declaratiiavere;

/**
 * Constants interface for text file to object mapping.
 *
 * @author Razvan Dani
 */
public interface TextFileToObjectMappingConstants {

    // the name of the xml file where the mapping info is stored
    String TEXT_FILE_TO_OBJECT_MAPPING_XML_FILE_NAME = "text-file-to-object-mapping.xml";

    /**
     * Enum that indicate the valid file types that are mapped to objects and can be read or written using
     * the TextFileObjectReader / TextFileObjectWritter.
     */
    enum FileType {
        REVENUE_DECLARATION_INFO("RevenueDeclarationInfo");

        String value;

        FileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static FileType getConstantByValue(String value) {
            FileType fileTypeForValue = null;

            for (FileType fileType : FileType.values()) {
                if (fileType.getValue().equals(value)) {
                    fileTypeForValue = fileType;
                    break;
                }
            }

            if (fileTypeForValue == null) {
                throw new IllegalArgumentException("Value " + value + " is not valid");
            }

            return fileTypeForValue;
        }
    }

    /**
     * Enum that indicates whether the text file is in using a cell delimiter or is a fixed length file.
     */
    enum FileFormat {
        DELIMITED("delimited");

        String value;

        FileFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static FileFormat getConstantByValue(String value) {
            FileFormat fileFormatForValue = null;

            for (FileFormat fileFormat : FileFormat.values()) {
                if (fileFormat.getValue().equals(value)) {
                    fileFormatForValue = fileFormat;
                    break;
                }
            }

            if (fileFormatForValue == null) {
                throw new IllegalArgumentException("Value " + value + " is not valid");
            }

            return fileFormatForValue;
        }
    }

    /**
     * Enum for possible types that can be used for the attributes of the java beans mapped to text files.
     */
    enum TextFileFieldAttributeType {
        STRING("java.lang.String"),
        BIG_DECIMAL("java.math.BigDecimal"),
        DATE("java.util.Date"),
        BOOLEAN("java.lang.Boolean"),
        BOOLEAN_PRIMITIVE("boolean"),
        INTEGER("java.lang.Integer");

        private String value;

        TextFileFieldAttributeType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }

        static TextFileFieldAttributeType getConstantByValue(String value) {
            TextFileFieldAttributeType textFileFieldAttributeTypeForValue = null;

            for (TextFileFieldAttributeType textFileFieldAttributeType : TextFileFieldAttributeType.values()) {
                if (textFileFieldAttributeType.getValue().equals(value)) {
                    textFileFieldAttributeTypeForValue = textFileFieldAttributeType;
                    break;
                }
            }

            if (textFileFieldAttributeTypeForValue == null) {
                throw new IllegalArgumentException("Value " + value + " is not valid");
            }

            return textFileFieldAttributeTypeForValue;
        }
    }

    /**
     * Enum for possible levels or errors when reading from text files.
     */
    enum FileReadingErrorLevel {
        WARNING(1),
        MODERATE(2),
        SEVERE(3);

        private Integer value;

        FileReadingErrorLevel(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }
    }

    /**
     * Enum that indicate the valid file names that are mapped to objects and can be read or written using
     * the TextFileObjectReader / TextFileObjectWritter.
     */
    enum FileName {
        REVENUE_DECLARATION("revenue_declaration.csv");

        String value;

        FileName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static FileName getConstantByValue(String value) {
            FileName fileNameForValue = null;

            for (FileName fileName : FileName.values()) {
                if (fileName.getValue().equals(value)) {
                    fileNameForValue = fileName;
                    break;
                }
            }

            if (fileNameForValue == null) {
                throw new IllegalArgumentException("Value " + value + " is not valid");
            }

            return fileNameForValue;
        }
    }
}

