/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.simplified.Itc04PdfReportDaoImpl;
import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.gstr6a.Itc04PdfReportDto;
import com.ey.advisory.app.docs.dto.simplified.ITC04SummaryRespDto;
import com.ey.advisory.app.services.search.docsummarysearch.ITC04SummaryScreenReqRespHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.ITC04RequestDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * @author Balakrishna.S
 *
 */
@Component("ITC04PDFGenerationReportImpl")
@Slf4j
public class ITC04PDFGenerationReportImpl implements ITC04PDFGenerationReport {

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");

	@Autowired
	@Qualifier("ITC04SummaryScreenReqRespHandler")
	ITC04SummaryScreenReqRespHandler reqResp;

	@Autowired
	@Qualifier("Itc04PdfReportDaoImpl")
	Itc04PdfReportDaoImpl daoImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository regRepo;

	@Autowired
	GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	GstnReturnFilingStatus gstnReturnFilingStatus;
	@Autowired
	GstnSubmitRepository gstnSubmitRepository;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository legalName;



	@Override
	public JasperPrint generatePdfGstr6Report(
			ITC04RequestDto request,String gstn) {

		String taxPeriod = request.getTaxPeriod();
		String quarter = taxPeriod.substring(0, 2);

		List<Long> entityId = request.getEntityId();
		Boolean isDigigst = request.getIsDigigst();
		Map<String, List<String>> dataSecAttrs = request.getDataSecAttrs();
		
		ITC04RequestDto itc04SummaryRequest = new ITC04RequestDto(taxPeriod);
		itc04SummaryRequest.setTaxPeriod(taxPeriod);
	
			List<String> singleGstin = Arrays.asList(gstn);
			dataSecAttrs.put(OnboardingConstant.GSTIN, singleGstin);
			itc04SummaryRequest.setDataSecAttrs(dataSecAttrs);
		
		

		String fYear = GenUtil.getFinancialYearByTaxperiod(taxPeriod);

		String qr = null;

		if (quarter.equalsIgnoreCase("13")) {
			qr = "Apr-Jun";
		} else if (quarter.equalsIgnoreCase("14")) {
			qr = "Jul-Sep";
		} else if (quarter.equalsIgnoreCase("15")) {
			qr = "Oct-Dec";
		} else if (quarter.equalsIgnoreCase("16")) {
			qr = "Jan-Mar";
		} else if (quarter.equalsIgnoreCase("17")) {
			qr = "Apr-Sep";
		} else if (quarter.equalsIgnoreCase("18")) {
			qr = "Oct-Mar";
		}

		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());

	
	
		JasperPrint jasperPrint = null;
		String source = "jasperReports/ITC04.jrxml";

		try {
					
			// Section 4 Getting from Query
			List<Itc04PdfReportDto> sec5List = daoImpl
					.loadBasicSummarySection(itc04SummaryRequest);

			if (sec5List.isEmpty()) {
				Itc04PdfReportDto defaultValue = new Itc04PdfReportDto();

				defaultValue.setNoofRecords("0");
				defaultValue.setAspCess("0.0");
				defaultValue.setTaxableValue("0.0");
				defaultValue.setTotalCentralTax("0.0");
				defaultValue.setTotalIntigratedValue("0.0");
				defaultValue.setTotalStateTax("0.0");
				sec5List.add(defaultValue);
			}
			
			Itc04PdfReportDto a4 = new Itc04PdfReportDto();
			List<Itc04PdfReportDto> a4List = new ArrayList<>();
			
			Itc04PdfReportDto a5 = new Itc04PdfReportDto();
			List<Itc04PdfReportDto> a5List = new ArrayList<>();

			Itc04PdfReportDto b5 = new Itc04PdfReportDto();
			List<Itc04PdfReportDto> b5List = new ArrayList<>();

			Itc04PdfReportDto c5 = new Itc04PdfReportDto();
			List<Itc04PdfReportDto> c5List = new ArrayList<>();

			List<ITC04SummaryRespDto> handleItc04ReqAndResp = reqResp
					.handleItc04ReqAndResp(itc04SummaryRequest);

			// For water mark

			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							gstn, taxPeriod,
							APIConstants.ITC04_RETURN_TYPE);

			if (gstrReturnStatusEntity == null
					|| !(gstrReturnStatusEntity.getStatus().equalsIgnoreCase("Filed"))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.ITC04_PDF_RET);

					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstn), fYear,
									false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.ITC04_RETURN_TYPE)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(taxPeriod)) {

							filingStatus = returnFilingGstnResponseDto
									.getStatus();
							filingDate = returnFilingGstnResponseDto
									.getFilingDate() + " 00:00:00";

						}

					}

				} catch (Exception e) {
					LOGGER.error(
							"Not able to generate Public Auth Token while Fetching Filling Status");
				}

			} else {

				filingStatus = gstrReturnStatusEntity.getStatus();
				if (filingStatus.equalsIgnoreCase("Filed")) {

					
					LocalDateTime dateTime = gstrReturnStatusEntity.getUpdatedOn();
					filingDate =  getStandardTime(dateTime);
					
					filingDate = gstrReturnStatusEntity.getFilingDate()
							.format(formatter2).toString() + " 00:00:00";

				}
			}
			if (!filingStatus.equalsIgnoreCase("Filed")) {
				GstnSubmitEntity gstnSubmitStatus = gstnSubmitRepository
						.findGstnStatusForSingleGstin(gstn, taxPeriod,
								APIConstants.GSTR1_RETURN_TYPE);
				if (gstnSubmitStatus != null) {
					String submitStatus = gstnSubmitStatus.getGstnStatus();
					if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
						filingStatus = "SUBMITTED";
						LocalDateTime dateTime = gstnSubmitStatus.getCreatedOn();
						filingDate = getStandardTime(dateTime);

					}
				}
			}
			
	
			
			//4
			ITC04SummaryRespDto itc04SummaryRespDto1 = handleItc04ReqAndResp
					.get(0);
			if(isDigigst==false)
			{	Itc04PdfReportDto a4newList=new Itc04PdfReportDto();
				String aspCount = String.valueOf((isDigigst?itc04SummaryRespDto1.getAspCount():itc04SummaryRespDto1.getGstnCount()));
				// itc04SummaryRespDto.getAspTaxableValue();

				a4newList.setNoofRecords(aspCount);
				String igst = sec5List.get(0).getTotalIntigratedValue();
				String totalCentralTax = sec5List.get(0).getTotalCentralTax();
				String totalStateTax = sec5List.get(0).getTotalStateTax();
				String aspCess = sec5List.get(0).getAspCess();
				a4newList.setAspCess(aspCess);
				a4newList.setTaxableValue(GenUtil
						.formatCurrency((isDigigst?itc04SummaryRespDto1.getAspTaxableValue():itc04SummaryRespDto1.getGstnTaxableValue())));
				a4newList.setTotalCentralTax(totalCentralTax);
				a4newList.setTotalIntigratedValue(igst);
				a4newList.setTotalStateTax(totalStateTax);
				
				
				a4List.add(a4newList);
			}

			// 5A
			ITC04SummaryRespDto itc04SummaryRespDto = handleItc04ReqAndResp
					.get(1);

			String aspCount = String.valueOf((isDigigst?itc04SummaryRespDto.getAspCount():itc04SummaryRespDto.getGstnCount()));
			// itc04SummaryRespDto.getAspTaxableValue();

			a5.setNoofRecords(aspCount);
			a5.setTaxableValue(GenUtil
					.formatCurrency((isDigigst?itc04SummaryRespDto.getAspTaxableValue():itc04SummaryRespDto.getGstnTaxableValue())));
			a5List.add(a5);

			// 5B
			ITC04SummaryRespDto itc04SummaryRespDto2 = handleItc04ReqAndResp
					.get(2);

			String aspCount5B = String
					.valueOf((isDigigst?itc04SummaryRespDto2.getAspCount():itc04SummaryRespDto2.getGstnCount()));

			b5.setNoofRecords(aspCount5B);
			b5.setTaxableValue(GenUtil
					.formatCurrency((isDigigst?itc04SummaryRespDto2.getAspTaxableValue():itc04SummaryRespDto2.getGstnTaxableValue())));
			b5List.add(b5);

			// 5C
			ITC04SummaryRespDto itc04SummaryRespDto3 = handleItc04ReqAndResp
					.get(3);

			String aspCount5C = String
					.valueOf((isDigigst?itc04SummaryRespDto3.getAspCount():itc04SummaryRespDto3.getGstnCount()));

			c5.setNoofRecords(aspCount5C);
			c5.setTaxableValue(GenUtil
					.formatCurrency((isDigigst?itc04SummaryRespDto3.getAspTaxableValue():itc04SummaryRespDto3.getGstnTaxableValue())));
			c5List.add(c5);

		//String regName = regRepo.findRegTypeByGstinForPdf(gstn);
			String regName = legalName.entityNameById(entityId);
			Map<String, Object> parameters = new HashMap<>();

			parameters.put("gstin", gstn);
			if(regName != null){
			parameters.put("legalNameofregis", regName);
			}else {
			parameters.put("legalNameofregis", "-");
			}
			if(isDigigst)
			{
				String digigst="DigiGST processed data";
				parameters.put("Digigst", digigst);
			}
			else
			{
				String gstn1="Update GSTN Data";
				parameters.put("Digigst", gstn1);
			}
			parameters.put("itcYear", fYear);
			parameters.put("itcquater", qr);
			if(isDigigst)
			{
			parameters.put("B2B", new JRBeanCollectionDataSource(sec5List));
			}
			else
			{
				parameters.put("B2B", new JRBeanCollectionDataSource(a4List));
			}
			parameters.put("B2CL", new JRBeanCollectionDataSource(a5List));
			parameters.put("B2CLA", new JRBeanCollectionDataSource(b5List));
			parameters.put("B2BA", new JRBeanCollectionDataSource(c5List));

			
		/*	String dateTime = formatter1
					.format(EYDateUtil
							.toISTDateTimeFromUTC(LocalDateTime.now()))
					.toString();*/
					parameters.put("currentDateTime", filingDate);

			File file = ResourceUtils.getFile("classpath:" + source);

			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FILED-WM.jpg";
			} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {
				image = "SUBMITTED-WM.jpg";
			} else {
				image = "DRAFT-WM.jpg";
			}

			File imgFile = ResourceUtils
					.getFile("classpath:jasperReports/" + image);
			byte[] blob = Files.readAllBytes(Paths.get(imgFile.getPath()));
			ByteArrayInputStream bis = new ByteArrayInputStream(blob);
			BufferedImage bImage2 = ImageIO.read(bis);
			parameters.put("bgIMG", bImage2);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());
		} catch (Exception ex) {
			LOGGER.error("Exception occured while genearting pdf..", ex);
		}

		return jasperPrint;
	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

}
