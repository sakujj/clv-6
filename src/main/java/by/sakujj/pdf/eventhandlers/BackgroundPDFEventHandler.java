package by.sakujj.pdf.eventhandlers;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;

import java.io.IOException;

public class BackgroundPDFEventHandler implements IEventHandler {
    PdfXObject backgroundPDFXObject;

    public BackgroundPDFEventHandler(PdfDocument pdf, PdfPage backgroundPdfPage) throws IOException {
        backgroundPDFXObject = backgroundPdfPage.copyAsFormXObject(pdf);
    }

    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdf = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
        pdfCanvas.addXObject(backgroundPDFXObject);
    }

}
