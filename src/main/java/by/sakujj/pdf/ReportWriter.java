package by.sakujj.pdf;

import by.sakujj.exceptions.PDFException;
import by.sakujj.pdf.eventhandlers.BackgroundPDFEventHandler;
import by.sakujj.pdf.eventhandlers.FooterEventHandler;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ReportWriter {
    public static void writePDF(String tableTitle,
                                List<List<Map.Entry<String, String>>> reportTables,
                                ReportConfig config,
                                String backgroundPdfPath,
                                OutputStream outputStream) {
        PdfDocument pdfDoc = null;
        PdfDocument backgroundPdfDoc = null;
        Document document = null;
        try {
            PdfWriter resultPDFWriter = new PdfWriter(outputStream).setSmartMode(true);
            PdfReader backgroundPDFReader = new PdfReader(backgroundPdfPath);

            pdfDoc = new PdfDocument(resultPDFWriter);
            backgroundPdfDoc = new PdfDocument(backgroundPDFReader);

            PdfFont boldTextFont = createFont(config.getBoldFontPath());
            PdfFont plainTextFont = createFont(config.getPlainFontPath());
            PdfPage backgroundPdfPage = backgroundPdfDoc.getPage(1);
            document = getDocumentWithSetMargins(backgroundPdfPage, config, pdfDoc);

            addDocumentEventHandlers(config, document, plainTextFont, backgroundPdfPage);
            addMultipleTablesToDocument(
                    tableTitle,
                    reportTables,
                    document,
                    boldTextFont,
                    plainTextFont,
                    config.getDefaultFontSize()
            );
        } catch (Exception e) {
            throw new PDFException(e);
        } finally {
            if (backgroundPdfDoc != null) {
                backgroundPdfDoc.close();
            }

            if (document != null) {
                document.close();
            }

            if (pdfDoc != null && !pdfDoc.isClosed()) {
                pdfDoc.close();
            }
        }
    }

    private static void addDocumentEventHandlers(ReportConfig config,
                                                 Document document,
                                                 PdfFont plainTextFont,
                                                 PdfPage backgroundPdfPage) throws IOException {
        PdfDocument pdfDoc = document.getPdfDocument();
        float defaultFontSize = config.getDefaultFontSize();
        float footerFromBottom = config.getFooterFromBottom();
        pdfDoc.addEventHandler(
                PdfDocumentEvent.START_PAGE,
                new FooterEventHandler(
                        document,
                        defaultFontSize,
                        plainTextFont,
                        footerFromBottom
                )
        );
        pdfDoc.addEventHandler(
                PdfDocumentEvent.START_PAGE,
                new BackgroundPDFEventHandler(pdfDoc, backgroundPdfPage)
        );
    }

    private static void addMultipleTablesToDocument(String documentTitle,
                                                    List<List<Map.Entry<String, String>>> reportTables,
                                                    Document document,
                                                    PdfFont boldTextFont,
                                                    PdfFont plainTextFont,
                                                    float defaultFontSize) {
        Paragraph title = new Paragraph()
                .setFont(boldTextFont)
                .setFontSize(defaultFontSize + 2)
                .setTextAlignment(TextAlignment.CENTER)
                .add(documentTitle);
        Paragraph blank = new Paragraph().add("");
        document.add(title);
        document.add(blank);

        for (int j = 0; j < reportTables.size() - 1; j++) {
            addReportTableToDocument(reportTables, document, plainTextFont, defaultFontSize, j);
            document.add(blank);
        }
        addReportTableToDocument(reportTables, document, plainTextFont, defaultFontSize, reportTables.size() - 1);

    }

    private static void addReportTableToDocument(List<List<Map.Entry<String, String>>> reportTables, Document document, PdfFont plainTextFont, float defaultFontSize, int j) {
        Paragraph orderNumber = new Paragraph()
                .setFont(plainTextFont)
                .setFontSize(defaultFontSize)
                .setTextAlignment(TextAlignment.CENTER);
        orderNumber.add("#" + (j + 1));
        document.add(orderNumber);

        Table table = new Table(2);
        table
                .setFont(plainTextFont)
                .setFontSize(defaultFontSize);

        List<Map.Entry<String, String>> reportTable = reportTables.get(j);
        for (Map.Entry<String, String> row : reportTable) {
            table.addCell(row.getKey());
            table.addCell(row.getValue());
        }
        for (int i = 0; i < reportTables.get(j).size(); i++) {
            Cell cell = table.getCell(i, 0);
            cell.setPadding(5);
            cell.setTextAlignment(TextAlignment.CENTER);
        }
        for (int i = 0; i < reportTable.size(); i++) {
            Cell cell = table.getCell(i, 1);
            cell.setPadding(5);
        }
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);
        document.add(table);
    }

    private static Document getDocumentWithSetMargins(PdfPage backgroundPdfPage, ReportConfig reportPDFConfig, PdfDocument pdfDoc) {
        Rectangle rectangle = backgroundPdfPage.getPageSize();

        float topMarginToHeightRatio = reportPDFConfig.getTopMarginToHeightRatio();
        float bottomMarginToHeightRatio = reportPDFConfig.getBottomMarginToHeightRatio();
        float leftMarginToWidthRatio = reportPDFConfig.getLeftMarginToWidthRatio();
        float rightMarginToWidthRatio = reportPDFConfig.getRightMarginToWidthRatio();

        float topMargin = rectangle.getHeight() * topMarginToHeightRatio;
        float bottomMargin = rectangle.getHeight() * bottomMarginToHeightRatio;
        float leftMargin = rectangle.getWidth() * leftMarginToWidthRatio;
        float rightMargin = rectangle.getWidth() * rightMarginToWidthRatio;

        Document document = new Document(pdfDoc);
        document.setMargins(
                topMargin,
                rightMargin,
                bottomMargin,
                leftMargin
        );
        return document;
    }

    private PdfFont createFont(String path) {
        try {
            FontProgram fontProgram = FontProgramFactory.createFont(path);
            return PdfFontFactory.createFont(fontProgram);

        } catch (IOException e) {
            throw new PDFException(e);
        }
    }
}
