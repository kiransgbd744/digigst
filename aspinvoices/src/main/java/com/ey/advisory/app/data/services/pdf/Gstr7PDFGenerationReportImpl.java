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

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.DongleMappingEntity;
import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.DongleMappingRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.services.anx1.Gstr7ReviewSummaryFetchService;
import com.ey.advisory.app.data.views.client.Gstr7PdfReportDto;
import com.ey.advisory.app.data.views.client.Gstr7ReviewSummaryRespDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr2AProcessedRecordsReqDto;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.google.common.base.Strings;

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
@Component("Gstr7PDFGenerationReportImpl")
@Slf4j
public class Gstr7PDFGenerationReportImpl implements Gstr7PDFGenerationReport {

	
	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");
	
	@Autowired
	@Qualifier("Gstr7ReviewSummaryFetchService")
	Gstr7ReviewSummaryFetchService gstr7Service;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository regRepo;
	
	@Autowired
	GstrReturnStatusRepository gstrReturnStatusRepository;
	
	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository legalName;

	@Autowired
	GstnReturnFilingStatus gstnReturnFilingStatus;
	
	@Autowired
	GstnSubmitRepository gstnSubmitRepository;
	
	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;
	
	@Autowired
	DongleMappingRepository dongleMappingRepository;

	@Override
	public JasperPrint generatePdfGstr7Report(
			Gstr2AProcessedRecordsReqDto request1,String gstn) {

		String retunPeriod = request1.getRetunPeriod();
		String fYear = GenUtil.getFinancialYearByTaxperiod(retunPeriod);
		Map<String, List<String>> dataSecAttrs = request1.getDataSecAttrs();

		List<Long> entityId = request1.getEntityId();
		
		Gstr2AProcessedRecordsReqDto request = new Gstr2AProcessedRecordsReqDto();
		request.setRetunPeriod(request1.getRetunPeriod());
		boolean isDigigst = request1.getIsDigigst();
		List<String> singleGstin = Arrays.asList(gstn);
		dataSecAttrs.put(OnboardingConstant.GSTIN, singleGstin);
		request.setDataSecAttrs(dataSecAttrs);
	
		
		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());
		
		List<Gstr7ReviewSummaryRespDto> reviewSummary = gstr7Service
				.getReviewSummary(request);

		Gstr7PdfReportDto table3 = new Gstr7PdfReportDto();

		List<Gstr7PdfReportDto> table3List = new ArrayList<>();
		Gstr7ReviewSummaryRespDto gstr7ReviewSummaryRespDto = reviewSummary
				.get(0);

		table3.setTotalCentralTax(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDto.getAspCgst():gstr7ReviewSummaryRespDto.getGstnCgst())));
		table3.setNoofRecords((isDigigst?gstr7ReviewSummaryRespDto.getAspCount().toString():gstr7ReviewSummaryRespDto.getGstnCount().toString()));
		table3.setTotalIntigratedValue(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDto.getAspIgst():gstr7ReviewSummaryRespDto.getGstnIgst())));
		table3.setTotalStateTax(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDto.getAspSgst():gstr7ReviewSummaryRespDto.getGstnSgst())));
		table3.setTaxableValue(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDto.getAspTotalAmount():gstr7ReviewSummaryRespDto.getGstnTotalAmount())));

		table3List.add(table3);

		Gstr7PdfReportDto table4 = new Gstr7PdfReportDto();

		List<Gstr7PdfReportDto> table4List = new ArrayList<>();
		Gstr7ReviewSummaryRespDto gstr7ReviewSummaryRespDtoT4 = reviewSummary
				.get(1);

		table4.setTotalCentralTax(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDtoT4.getAspCgst():gstr7ReviewSummaryRespDtoT4.getGstnCgst())));
		table4.setNoofRecords((isDigigst?gstr7ReviewSummaryRespDtoT4.getAspCount().toString():gstr7ReviewSummaryRespDtoT4.getGstnCount().toString()));
		table4.setTotalIntigratedValue((isDigigst?gstr7ReviewSummaryRespDtoT4.getAspIgst().toString():gstr7ReviewSummaryRespDtoT4.getGstnIgst().toString()));
		table4.setTotalStateTax(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDtoT4.getAspSgst():gstr7ReviewSummaryRespDtoT4.getGstnSgst())));
		table4.setTaxableValue(GenUtil
				.formatCurrency((isDigigst?gstr7ReviewSummaryRespDtoT4.getAspTotalAmount():gstr7ReviewSummaryRespDtoT4.getGstnTotalAmount())));

		table4List.add(table4);

		JasperPrint jasperPrint = null;
		String source = "jasperReports/Gstr7.jrxml";
		try {

			
			// For water mark

			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeContainingIgnoreCaseAndIsCounterPartyGstinFalse(
							gstn, retunPeriod,
							APIConstants.GSTR7);

			
			if (gstrReturnStatusEntity == null
					|| !(gstrReturnStatusEntity.getStatus().equalsIgnoreCase("Filed"))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTR7_PDF_RET);
					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstn), fYear,
									false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.GSTR7)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(retunPeriod)) {

							filingStatus = returnFilingGstnResponseDto
									.getStatus();
							filingDate = returnFilingGstnResponseDto
									.getFilingDate();

						}

					}

				} catch (Exception e) {
					LOGGER.error(
							"Not able to generate Public Auth Token while Fetching Filling Status");
				}

			} else {

				filingStatus = gstrReturnStatusEntity.getStatus();
				if (filingStatus.equalsIgnoreCase("Filed")) {

					
					filingDate = gstrReturnStatusEntity.getFilingDate().toString();

				} 
			}
			if (!filingStatus.equalsIgnoreCase("Filed")) {
				GstnSubmitEntity gstnSubmitStatus = gstnSubmitRepository
						.findGstnStatusForSingleGstin(gstn, retunPeriod,
								APIConstants.GSTR7);
				if (gstnSubmitStatus != null) {
					String submitStatus = gstnSubmitStatus.getGstnStatus();
					if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
						filingStatus = "SUBMITTED";
						LocalDateTime dateTime = gstnSubmitStatus.getCreatedOn();
						filingDate = getStandardTime(dateTime);
					}
				}
			}
			
		//	String regName = regRepo.findRegTypeByGstinForPdf(gstn);
			String regName = legalName.entityNameById(entityId);
			String month = convertMonth(request.getRetunPeriod());

			Map<String, Object> parameters = new HashMap<>();

			parameters.put("gstin", gstn);
			if (regName == null) {
				parameters.put("legalNameofregis", "-");
			} else {
				parameters.put("legalNameofregis", regName);
			}
			
			String groupCode = TenantContext.getTenantId();
			GSTNDetailEntity gstnDetailEntity = regRepo
					.findByGstinAndIsDeleteFalse(gstn);
			TaxPayerDetailsDto apiResp = null;
			if (Strings.isNullOrEmpty(gstnDetailEntity.getLegalName())
					&& Strings.isNullOrEmpty(gstnDetailEntity.getTradeName())) {
				try {
					apiResp = taxPayerService.getTaxPayerDetails(gstn,
							groupCode);
				} catch (Exception e) {
					LOGGER.error("Not able to taxpayer details from gstn");
				}
				regRepo.updateLegalAndTradeName(
						apiResp.getLegalBussNam(), apiResp.getTradeName(), gstn,
						gstnDetailEntity.getEntityId(), groupCode);
			}

			if (gstnDetailEntity != null) {
				if (gstnDetailEntity.getLegalName() != null) {
					parameters.put("legalNameofregis",
							gstnDetailEntity.getLegalName());
				}

				if (gstnDetailEntity.getTradeName() != null) {
					parameters.put("tradeName",
							gstnDetailEntity.getTradeName());
				}
			}

			if (gstrReturnStatusEntity != null
					&& gstrReturnStatusEntity.getArnNo() != null) {
				parameters.put("arn", gstrReturnStatusEntity.getArnNo());
			}

			if (gstrReturnStatusEntity != null
					&& gstrReturnStatusEntity.getFilingDate() != null) {
				String fmtLocalDate = EYDateUtil
						.fmtLocalDate(gstrReturnStatusEntity.getFilingDate());
				parameters.put("filingDate", fmtLocalDate);
			}

			if (!Strings.isNullOrEmpty(request1.getIsVerified())) {
				if (request1.getIsVerified().equalsIgnoreCase("YES")) {
					parameters.put("verification",
							"I hearby solemnly affirm and declare that the information given herein above is "
							+ "true and correct to the best of my knowledge and belief and nothing has been concealed therefrom.");
				} else {
					parameters.put("verification", "");
				}
			}
			
			List<DongleMappingEntity> dongleMapping = dongleMappingRepository
					.findByGstinAndIsActiveTrue(gstn);
			
			if(!dongleMapping.isEmpty() || dongleMapping != null){
				for(DongleMappingEntity dongle: dongleMapping){
					if(dongle.getGstin().equalsIgnoreCase(gstn)){
						if (filingStatus.equalsIgnoreCase("Filed")) {
							parameters.put("authorisedSignatory", dongle.getAuthorisedName());
							parameters.put("designation", dongle.getDesignation());
						}
						
					}
				}
			}
			
			parameters.put("Digigst", isDigigst?"* All figures are as per DigiGST processed data":"* All figures are as per Update GSTN Data");
	//		parameters.put("tradename", "-");
			parameters.put("gstr7Year", fYear);
			parameters.put("gstr7mon", month);
			//parameters.put("arn", "-");
			parameters.put("dateOfArn",filingDate);
			parameters.put("B2B", new JRBeanCollectionDataSource(table3List));
			parameters.put("B2BA", new JRBeanCollectionDataSource(table4List));
			parameters.put("currentDateTime", filingDate);
			
			File file = ResourceUtils.getFile("classpath:" + source);

			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FinalU.PNG";
			} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {
				image = "SubmittedU.PNG";
			} else {
				image = "DraftU.PNG";
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

	public String convertMonth(String taxPeriod) {

		String month = taxPeriod.substring(0, 2);

		Map<String, String> monthMap = new HashMap();

		monthMap.put("01", "January");
		monthMap.put("02", "February");
		monthMap.put("03", "March");
		monthMap.put("04", "April");
		monthMap.put("05", "May");
		monthMap.put("06", "June");
		monthMap.put("07", "July");
		monthMap.put("08", "August");
		monthMap.put("09", "September");
		monthMap.put("10", "October");
		monthMap.put("11", "November");
		monthMap.put("12", "December");


		String monthDesc = monthMap.get(month);
		return monthDesc;

	}
	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

}
