package com.techpulse.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

@Service
public class HtmlToPngService {

    @Value("${html2png.width}")
    private int defaultWidth;

    @Value("${html2png.height}")
    private int defaultHeight;

    @Value("${html2png.output-dir}")
    private String outputDir;

    @PostConstruct
    public void init() throws IOException {
        Path dir = Path.of(outputDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    public File convertHtmlToPngFile(String htmlContent, int width, int height, String fileName) throws IOException {
        if (width <= 0) width = defaultWidth;
        if (height <= 0) height = defaultHeight;

        ByteArrayOutputStream pdfOs = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(pdfOs);
        builder.run();

        PDDocument document = PDDocument.load(pdfOs.toByteArray());
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);
        document.close();

        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        resized.getGraphics().drawImage(image, 0, 0, width, height, null);

        File outputFile = new File(outputDir + File.separator + fileName);
        ImageIO.write(resized, "png", outputFile);
        return outputFile;
    }
}
