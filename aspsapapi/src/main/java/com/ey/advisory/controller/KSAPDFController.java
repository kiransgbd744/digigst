package com.ey.advisory.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * 
 * 
 * @author Jithendra.B
 *
 */
@RestController
@Slf4j
public class KSAPDFController {

	@GetMapping(value = "/ui/ksaPDFReport", produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public void downloadKSAPDFReport(HttpServletRequest request,
			HttpServletResponse response) {
		try {

			JasperPrint jasperPrint = generateKSAPdfReport();

			if (jasperPrint != null) {
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern("yyyyMMddHHmmss");
				String timeMilli = dtf.format(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

				response.setContentType("application/x-download");
				response.addHeader("Content-disposition",
						"attachment; filename=" + "EY_" + "KSA_SAMPLE_" + timeMilli
								+ ".pdf");

				OutputStream out;
				out = response.getOutputStream();
				JasperExportManager.exportReportToPdfStream(jasperPrint, out);
			}
		} catch (Exception ex) {
			String msg = " Exception while Downloading the Gstr9 PDF ";
			LOGGER.error(msg, ex);
		}
	}

	private JasperPrint generateKSAPdfReport() {

		JasperPrint jasperPrint = null;
		// String pdfFileName = "C:/Users/TN668PP/OneDrive -
		// EY/Desktop/KSA/1.pdf";
		// String source =
		// "C:/Users/TN668PP/JaspersoftWorkspace/KSA/SampleKSA.jrxml";
		String source = "jasperReports/SampleKSA.jrxml";
		try {
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("header",
					"ﺷﺮﻛﺔ ﺍﺭﻧﺴﺖ ﻭﻳﻮﻧﻎ ﻭﺷﺮﻛﺎﻫﻢ (ﻣﺤﺎﺳﺒﻮﻥ ﻗﺎﻧﻮﻧﻴﻮﻥ)");
			parameters.put("header2",
					"ﺑﺮﺝ ﺍﻟﻔﻴﺼﻠﻴﺔ ﺍﻟﺪﻭﺭ ١٤ ، ﻃﺮﻳﻖ ﺍﻟﻤﻠﻚ ﻓﻬﺪ ﺹ.ﺏ. ٢٧٣٢  11461 ﺍﻟﺮﻳﺎﺽ، ﺍﻟﻤﻤﻠﻜﺔ ﺍﻟﻌﺮﺑﻴﺔ ﺍﻟﺴﻌﻮﺩﻳﺔ 966112734740 +");
			parameters.put("header3",
					"ﺭﻗﻢ ﺍﻟﺘﺴﺠﻴﻞ ﺍﻟﻀﺮﻳﺒﻲ ﻟﻠﻤﻮﺭﺩ: 300067861500003");
			parameters.put("midHead", "ﻓﺎﺗﻮﺭﺓ ﺿﺮﻳﺒﻴﺔ");
			parameters.put("taxInvNo", "SA08S300040231");
			parameters.put("taxInvNoAr", " ﻓﺎﺗﻮﺭﺓ ﺿﺮﻳﺒﻴﺔ ﺭﻗﻢ:");
			parameters.put("note", "ﻳﺮﺟﻰ ﺍﻗﺘﺒﺎﺱ ﻫﺬﺍ ﺍﻟﺮﻗﻢ ﻋﻨﺪ ﺍﻟﺪﻓﻊ");

			parameters.put("date", "١٩/٠٥/٢٠٢١");
			parameters.put("dateInAr", "ﺗﺎﺭﻳﺦ ﺍﻟﻔﺎﺗﻮﺭﺓ :");

			parameters.put("custNo", "E-40013333/001200002");
			parameters.put("custNoInAr", "ﺭﻗﻢ ﺍﻟﻌﻤﻴﻞ / ﺭﻗﻢ ﺍﻟﻌﻘﺪ:");

			parameters.put("purOrdrNoAr", "ﺭﻗﻢ ﻣﺮﺟﻊ ﺃﻣﺮ ﺍﻟﺸﺮﺍء:");

			parameters.put("addressEng", "ABCD \nPO Box 1234 Riyadh, KSA");

			parameters.put("tableHead",
					"ﺗﺠﺪﻭﻥ ﻃﻴﻪ ﺗﻔﺎﺻﻴﻞ ﺍﻷﺗﻌﺎﺏ ﺍﻟﻤﻬﻨﻴﺔ ﻋﻦ ﺍﻟﺨﺪﻣﺎﺕ ﺍﻟﻤﻘﺪﻣﺔ. ﻫﺬﻩ ﺍﻟﻔﺎﺗﻮﺭﺓ ﻣﺴﺘﺤﻘﺔ ﺍﻟﺴﺪﺍﺩ ﺑﻤﺠﺮﺩ ﺍﺳﺘﻼﻣﻬﺎ.");

			File imgFile = ResourceUtils
					.getFile("classpath:jasperReports/" + "KSA-EY.png");

			byte[] blob = Files.readAllBytes(Paths.get(imgFile.getPath()));
			ByteArrayInputStream bis = new ByteArrayInputStream(blob);
			BufferedImage bImage2 = ImageIO.read(bis);
			parameters.put("bgStatusImage", bImage2);

			File file = ResourceUtils.getFile("classpath:" + source);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());

		} catch (Exception e) {
		    String msg = "Exception occurred while generating GSTR9 Summary PDF";
		    LOGGER.error(msg, e);
		    throw new AppException(msg, e);
		}

		return jasperPrint;

	}
}
