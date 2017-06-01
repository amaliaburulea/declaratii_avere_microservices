package declaratiiavere;

/**
 * Created by razvan.dani on 23.04.2017.
 */
public class Import {
    public static void main(String[] args) {
        TextFileObjectReader textFileObjectReader = new TextFileObjectReader(TextFileToObjectMappingConstants.FileType.REVENUE_DECLARATION_INFO,
                "C:\\Users\\dr\\declaratii_avere\\x.csv");
        textFileObjectReader.startReading();

        RevenueDeclarationInfo revenueDeclarationInfo = null;

        while ((revenueDeclarationInfo = (RevenueDeclarationInfo)
                textFileObjectReader.readNextObject()) != null) {
            System.out.println("revenueDeclarationInfo = " + revenueDeclarationInfo);
        }

        textFileObjectReader.stopReading();
    }
}
