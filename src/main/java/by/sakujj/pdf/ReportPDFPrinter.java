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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class ReportPDFPrinter {
    public static void printToPDF(ReportPDF report,
                                  String backgroundPdfPath,
                                  String documentSavePath) {
        PdfDocument pdfDoc = null;
        PdfDocument backgroundPdfDoc = null;
        Document document = null;
        try {
            PdfWriter resultPDFWriter = new PdfWriter(documentSavePath)
                    .setSmartMode(true);
            PdfReader backgroundPDFReader = new PdfReader(backgroundPdfPath);

            pdfDoc = new PdfDocument(resultPDFWriter);
            backgroundPdfDoc = new PdfDocument(backgroundPDFReader);

            ReportPDFConfig reportPDFConfig = report.getConfig();
            PdfFont boldTextFont = createFont(reportPDFConfig.getBoldFontPath());
            PdfFont plainTextFont = createFont(reportPDFConfig.getPlainFontPath());
            float defaultFontSize = reportPDFConfig.getDefaultFontSize();


            PdfPage backgroundPdfPage = backgroundPdfDoc.getPage(1);
            document = getDocumentWithSetMargins(backgroundPdfPage, reportPDFConfig, pdfDoc);

            float footerFromBottom = reportPDFConfig.getFooterFromBottom();
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

            addReportToDocument(report, document, boldTextFont,  plainTextFont, defaultFontSize);
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

    private static void addReportToDocument(ReportPDF report, Document document,
                                            PdfFont boldTextFont, PdfFont plainTextFont, float defaultFontSize) {
        Paragraph title = new Paragraph()
                .setFont(boldTextFont)
                .setFontSize(defaultFontSize + 2)
                .setTextAlignment(TextAlignment.CENTER);
        title.add(report.getMethodName());

        Paragraph blank = new Paragraph().add("");

        document.add(title);
        document.add(blank);

        for (String reportInfo : report.getReportInfoList()) {
            Paragraph paragraph = getDefaultParagraph(plainTextFont, defaultFontSize);
            paragraph.add(reportInfo);
            paragraph.add(blank);
            document.add(paragraph);
        }
    }

    private static Paragraph getDefaultParagraph(PdfFont plainTextFont, float defaultFontSize) {
        return new Paragraph()
                .setTextAlignment(TextAlignment.JUSTIFIED)
                .setFont(plainTextFont)
                .setFontSize(defaultFontSize);
    }

    private static Document getDocumentWithSetMargins(PdfPage backgroundPdfPage, ReportPDFConfig reportPDFConfig, PdfDocument pdfDoc) {
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
