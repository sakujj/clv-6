package unit.pdf;

import by.sakujj.pdf.ReportPDF;
import by.sakujj.pdf.ReportPDFPrinter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.ReportPDFTestBuilder;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ReportPDFPrinterTests {

    private static final String BACKGROUND_PDF_PATH = "pdf-resources/Clevertec_Template.pdf";
    private static final String OUTPUT_FOLDER_PATH = "pdf-output-for-tests-ReportPDFPrinterTests";

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    @BeforeAll
    static void createOutputFolder() {
        File outputFolder = new File(OUTPUT_FOLDER_PATH);
        outputFolder.mkdirs();
    }

    @AfterAll
    static void cleanUpOutputFolder() {
        File outputFolder = new File(OUTPUT_FOLDER_PATH);
        deleteFolder(outputFolder);
    }

    @Test
    void shouldCreatePDF() {
        // given
        ReportPDF report = ReportPDFTestBuilder.aReportPDF().build();
        String documentSavePath = OUTPUT_FOLDER_PATH
                + "/"
                + "shouldCreatePDF-"
                + LocalDateTime.MIN
                + ".pdf";

        // when
        ReportPDFPrinter.printToPDF(
                report,
                BACKGROUND_PDF_PATH,
                documentSavePath);

        // then
        File printedReport = new File(documentSavePath);
        assertThat(printedReport.exists()).isTrue();
    }

    @Test
    void shouldContainContentThatWasIntendedToBePrinted() throws IOException {
        // given
        ReportPDF report = ReportPDFTestBuilder.aReportPDF().build();
        String documentSavePath = OUTPUT_FOLDER_PATH
                + "/"
                + "shouldContainContentThatWasIntendedToBePrinted-"
                + LocalDateTime.MIN
                + ".pdf";
        ReportPDFPrinter.printToPDF(
                report,
                BACKGROUND_PDF_PATH,
                documentSavePath);
        PdfDocument pdfDocument = null;
        StringBuilder containedText = new StringBuilder();
        int numberOfPages = -1;

        // when
        try {
            PdfReader reader = new PdfReader(documentSavePath);
            pdfDocument = new PdfDocument(reader);
            numberOfPages = pdfDocument.getNumberOfPages();

            for (int i = 1; i <= numberOfPages; i++) {
                SimpleTextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
                containedText.append(PdfTextExtractor.getTextFromPage(pdfDocument.getPage(i), strategy));
            }
        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        }

        // then
        for (int i = 0; i < report.getReportInfoList().size(); i++) {
            String expectedReportInfo = report.getReportInfoList().get(i);
            final String actualText = containedText.toString();
            expectedReportInfo
                    .lines()
                    .forEach(expectedLine -> assertThat(actualText).contains(expectedLine));
        }
        for (int i = 1; i <= numberOfPages; i++) {
            String expectedPageNumber = String.valueOf(i);
            assertThat(containedText).contains(expectedPageNumber);
        }
    }

}
