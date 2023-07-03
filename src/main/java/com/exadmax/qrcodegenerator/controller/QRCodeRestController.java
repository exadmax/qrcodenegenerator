package com.exadmax.qrcodegenerator.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

@RestController
@RequestMapping("/api/qrcode")
public class QRCodeRestController {

	@GetMapping(value = "/gerar", produces = MediaType.IMAGE_PNG_VALUE)
	public ResponseEntity<byte[]> generateQRCode(String text) {
	    try {
	        String encodedText = text;
	        
	        // Verifique se o texto é uma URL válida
	        if (isValidUrl(text)) {
	            encodedText = text;
	        } else {
	            // Encode o texto para evitar problemas com caracteres especiais
	            encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
	        }

	        QRCodeWriter qrCodeWriter = new QRCodeWriter();
	        Hashtable<EncodeHintType, ErrorCorrectionLevel> hints = new Hashtable<>();
	        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	        BitMatrix bitMatrix = qrCodeWriter.encode(encodedText, BarcodeFormat.QR_CODE, 200, 200, hints);

	        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
	        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
	        byte[] pngData = pngOutputStream.toByteArray();

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_PNG);
	        return new ResponseEntity<>(pngData, headers, HttpStatus.OK);
	    } catch (WriterException | IOException e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	private boolean isValidUrl(String url) {
	    try {
	        new URL(url).toURI();
	        return true;
	    } catch (MalformedURLException | URISyntaxException e) {
	        return false;
	    }
	}

}
