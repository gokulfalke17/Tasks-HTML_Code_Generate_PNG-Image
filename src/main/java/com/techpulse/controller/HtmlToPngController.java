package com.techpulse.controller;

import com.techpulse.service.HtmlToPngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/html")
public class HtmlToPngController {

    @Autowired
    private HtmlToPngService htmlToPngService;

    @Value("${html2png.output-dir}")
    private String outputDir;

    @PostMapping("/to-png")
    public ResponseEntity<String> generatePng(
            @RequestParam(defaultValue = "800") int width,
            @RequestParam(defaultValue = "600") int height,
            @RequestParam(defaultValue = "generated.png") String fileName,
            @RequestBody String htmlContent
    ) {
        try {
            File pngFile = htmlToPngService.convertHtmlToPngFile(htmlContent, width, height, fileName);
            return ResponseEntity.ok("PNG generated: " + pngFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PNG");
        }
    }

    //get only that png image and display in browser
   /* @GetMapping("/get-png/{fileName}")
    public ResponseEntity<byte[]> getPng(@PathVariable String fileName) {
        try {
            File file = Path.of(outputDir, fileName).toFile();
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            byte[] data = Files.readAllBytes(file.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("inline", fileName);
            return ResponseEntity.ok().headers(headers).body(data);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }*/

    //get downloadable png image file from server
    @GetMapping("/get-png/{fileName}")
    public ResponseEntity<byte[]> getPng(@PathVariable String fileName) {
        try {
            File file = Path.of(outputDir, fileName).toFile();

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            byte[] data = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);

            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + fileName + "\"");

            headers.setContentLength(data.length);

            return new ResponseEntity<>(data, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
