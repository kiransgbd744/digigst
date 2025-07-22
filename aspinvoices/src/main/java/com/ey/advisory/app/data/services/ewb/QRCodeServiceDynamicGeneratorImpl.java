/**
 * 
 */
package com.ey.advisory.app.data.services.ewb;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Sujith.Nanga
 *
 */

@Slf4j
@Component
public class QRCodeServiceDynamicGeneratorImpl implements QRCodeServiceDynamicGenerator {

	@Override
	public Image getImage(String qrCode) {
		Image image = null;
		if (qrCode == null || qrCode.isEmpty())
			return image;

		ByteArrayOutputStream jpgOutputStream = null;
		QRCodeWriter qrCodeWriter = new QRCodeWriter();

		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(qrCode,
					BarcodeFormat.QR_CODE, 400, 400);
			jpgOutputStream = new ByteArrayOutputStream();
			MatrixToImageWriter
					.writeToStream(bitMatrix, "JPG", jpgOutputStream);

		} catch (Exception ex) {
			LOGGER.error("Error While parsing ewb date or Error while "
					+ "generating QR code for EWB", ex);
			return image;
		}

		byte[] jpgData = jpgOutputStream.toByteArray();
		ByteArrayInputStream bis = new ByteArrayInputStream(jpgData);
		Iterator<?> readers = ImageIO.getImageReadersByFormatName("jpg");
		// ImageIO is a class containing static methods for locating
		// ImageReaders
		// and ImageWriters, and performing simple encoding and decoding.
		ImageReader reader = (ImageReader) readers.next();
		Object source1 = bis;
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(source1);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			image = reader.read(0, param);
		} catch (Exception ex) {
			LOGGER.error("Error while generating image input stream", ex);
			return image;
		}

		return image;
	}

}
 
