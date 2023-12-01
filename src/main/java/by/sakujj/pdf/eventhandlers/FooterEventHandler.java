package by.sakujj.pdf.eventhandlers;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.properties.TextAlignment;

public class FooterEventHandler implements IEventHandler {

    private final Document doc;
    private final PdfFont font;

    private final float fontSize;
    private final float footerFromBottom;

    public FooterEventHandler(Document doc, float fontSize, PdfFont font, float footerFromBottom) {
        this.doc = doc;
        this.font = font;
        this.fontSize = fontSize;
        this.footerFromBottom = footerFromBottom;
    }

    public void handleEvent(Event currentEvent) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
        Rectangle pageSize = docEvent.getPage().getPageSize();

        float footerX = ((pageSize.getLeft() + doc.getLeftMargin())
                + (pageSize.getRight() - doc.getRightMargin())) / 2;
        float footerY = pageSize.getBottom() + footerFromBottom;
        Canvas canvas = new Canvas(docEvent.getPage(), pageSize);
        canvas
                .setFont(font)
                .setFontSize(fontSize)
                .showTextAligned(String.valueOf(doc.getPdfDocument().getNumberOfPages()), footerX, footerY, TextAlignment.CENTER)
                .close();
    }
}

