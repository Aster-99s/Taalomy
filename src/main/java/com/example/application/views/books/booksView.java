package com.example.application.views.books;

import com.example.application.views.MainLayout;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.RolesAllowed;

import java.util.concurrent.atomic.AtomicInteger;

@PageTitle("مكتبتي")
@Route(value = "Books", layout = MainLayout.class)
@RolesAllowed("USER")
public class booksView extends Main{
    private booksView() {
        //RTL Support
        final UI ui = UI.getCurrent();
        ui.setDirection(Direction.RIGHT_TO_LEFT);
        addClassName("books-view");




        //  PDF viewer

        PdfViewer pdfViewer = new PdfViewer();
        StreamResource resource = new StreamResource("5ap-ar.pdf", () -> getClass().getResourceAsStream("/PDFs/5ap-ar.pdf"));
        pdfViewer.setSrc(resource);
        pdfViewer.setAddDownloadButton(false);
        pdfViewer.setCustomTitle("اللغة العربية");
        pdfViewer.setAddPrintButton(true);
        pdfViewer.setSizeFull();
        setHeight("100%");
        //
        add(pdfViewer);

    }
}
