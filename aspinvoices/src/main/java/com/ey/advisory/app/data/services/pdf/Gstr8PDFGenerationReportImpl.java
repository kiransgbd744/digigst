/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import com.ey.advisory.app.data.services.anx1.Gstr8ReviewSummaryFetchService;
import com.ey.advisory.app.data.views.client.Gstr8ReviewSummaryRespDto;
import com.ey.advisory.app.docs.dto.gstr1.Gstr8ReviewSummaryReqDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.util.OnboardingConstant;
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

/**
 * @author Ravindra V S
 *
 */
@Component("Gstr8PDFGenerationReportImpl")
@Slf4j
public class Gstr8PDFGenerationReportImpl implements Gstr8PDFGenerationReport {

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
	
	@Autowired
	@Qualifier("Gstr8ReviewSummaryFetchService")
	Gstr8ReviewSummaryFetchService gstr8ReviewSummaryFetchService;

	@Override
	public JasperPrint generatePdfGstr8Report(
			Gstr2AProcessedRecordsReqDto request1,String gstn) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside generatePdfGstr8Report request:%s ", request1.toString());
			LOGGER.debug(msg);
		}


		String retunPeriod = request1.getRetunPeriod();
		String fYear = GenUtil.getFinancialYearByTaxperiod(retunPeriod);
		Map<String, List<String>> dataSecAttrs = request1.getDataSecAttrs();
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside generatePdfGstr8Report :%s ", retunPeriod + fYear);
			LOGGER.debug(msg);
		}

		List<Long> entityId = request1.getEntityId();
		
		Gstr2AProcessedRecordsReqDto request = new Gstr2AProcessedRecordsReqDto();
		request.setRetunPeriod(retunPeriod);
		boolean isDigigst = request1.getIsDigigst();
		List<String> singleGstin = Arrays.asList(gstn);
		dataSecAttrs.put(OnboardingConstant.GSTIN, singleGstin);
		request.setDataSecAttrs(dataSecAttrs);
	
		
		String filingStatus = "Draft";
		String image = null;
		
		Gstr8ReviewSummaryReqDto reqDto = new Gstr8ReviewSummaryReqDto();
		
		reqDto.setGstin(gstn);
		reqDto.setTaxPeriod(retunPeriod);
		
		if (LOGGER.isDebugEnabled()) {
			String msg = String
					.format("Inside generatePdfGstr8Report : %s", reqDto.toString());
			LOGGER.debug(msg);
		}
		
		List<Gstr8ReviewSummaryRespDto> response = gstr8ReviewSummaryFetchService
				.getReviewSummary(reqDto);
		
		if (response != null && !response.isEmpty()) {
	
		    for (Gstr8ReviewSummaryRespDto dto : response) {
		    	if (LOGGER.isDebugEnabled()) {
					String msg = String
							.format("Inside generatePdfGstr8Report : %s", dto.toString());
					LOGGER.debug(msg);
				}
		    }
		} 


		JasperPrint jasperPrint = null;
		String source = "jasperReports/Gstr8.jrxml";
		try {
			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeContainingIgnoreCaseAndIsCounterPartyGstinFalse(
							gstn, retunPeriod,
							APIConstants.GSTR8);

			
			if (gstrReturnStatusEntity == null
					|| !(gstrReturnStatusEntity.getStatus().equalsIgnoreCase("Filed"))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTR8_PDF_RET);
					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstn), fYear,
									false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.GSTR8)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(retunPeriod)) {

							filingStatus = returnFilingGstnResponseDto
									.getStatus();

						}

					}

				} catch (Exception e) {
					LOGGER.error(
							"Not able to generate Public Auth Token while Fetching Filling Status");
				}

			} else {

				filingStatus = gstrReturnStatusEntity.getStatus(); 
			}
			if (!filingStatus.equalsIgnoreCase("Filed")) {
				GstnSubmitEntity gstnSubmitStatus = gstnSubmitRepository
						.findGstnStatusForSingleGstin(gstn, retunPeriod,
								APIConstants.GSTR8);
				if (gstnSubmitStatus != null) {
					String submitStatus = gstnSubmitStatus.getGstnStatus();
					if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
						filingStatus = "SUBMITTED";
					}
				}
			}
			
			String regName = legalName.entityNameById(entityId);
			String month = convertMonth(retunPeriod);

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
				parameters.put("legalNameofregis",
						apiResp.getLegalBussNam());
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
			parameters.put("gsrt8Year", fYear);
			parameters.put("gstr8Month", month);
			
			File file = ResourceUtils.getFile("classpath:" + source);

			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FinalU.PNG";
			} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {
				image = "SubmittedU.PNG";
			} else {
				image = "DraftU.PNG";
			}
			for (Gstr8ReviewSummaryRespDto dto : response) {
	            String section = dto.getSection();
	            switch (section) {
	                case "TCS":
	                    populateTCSParameters(dto, parameters, isDigigst);
	                    break;
	                case "URD":
	                    populateURDParameters(dto, parameters, isDigigst);
	                    break;
	                case "TCSA":
	                    populateTCSAParameters(dto, parameters, isDigigst);
	                    break;
	                case "URDA":
	                    populateURDAParameters(dto, parameters, isDigigst);
	                    break;
	                default:
	                    break;
	            }
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
	
	private static void populateTCSParameters(Gstr8ReviewSummaryRespDto dto, Map<String, Object> parameters, Boolean isDigigst) {
		if(isDigigst){
			parameters.put("tcsNoOfRecords", dto.getAspCount().toString());
	        parameters.put("tcsGrossSuppliesMade", dto.getAspGrossSuppliesMade().toString());
	        parameters.put("tcsSuppliesReturned", dto.getAspGrossSuppliesReturned().toString());
	        parameters.put("tcsNetAmount", dto.getAspNetSupplies().toString());
	        parameters.put("tcsIntegratedTax", dto.getAspIgst().toString());
	        parameters.put("tcsCentralTax", dto.getAspCgst().toString());
	        parameters.put("tcsStateTax", dto.getAspSgst().toString());
		} else {
			parameters.put("tcsNoOfRecords", dto.getGstnCount().toString());
	        parameters.put("tcsGrossSuppliesMade", dto.getGstnGrossSuppliesMade().toString());
	        parameters.put("tcsSuppliesReturned", dto.getGstnGrossSuppliesReturned().toString());
	        parameters.put("tcsNetAmount", dto.getGstnNetSupplies().toString());
	        parameters.put("tcsIntegratedTax", dto.getGstnIgst().toString());
	        parameters.put("tcsCentralTax", dto.getGstnCgst().toString());
	        parameters.put("tcsStateTax", dto.getGstnSgst().toString());
		}
        
    }

    private static void populateURDParameters(Gstr8ReviewSummaryRespDto dto, Map<String, Object> parameters, Boolean isDigigst) {
    	if(isDigigst){
    		 parameters.put("urdNoOfRecords", dto.getAspCount().toString());
    	     parameters.put("urdGrossSuppliesMade", dto.getAspGrossSuppliesMade().toString());
    	     parameters.put("urdSuppliesReturned", dto.getAspGrossSuppliesReturned().toString());
    	     parameters.put("urdNetValueOfSupplies", dto.getAspNetSupplies().toString());
    	} else {
    		 parameters.put("urdNoOfRecords", dto.getGstnCount().toString());
	   	     parameters.put("urdGrossSuppliesMade", dto.getGstnGrossSuppliesMade().toString());
	   	     parameters.put("urdSuppliesReturned", dto.getGstnGrossSuppliesReturned().toString());
	   	     parameters.put("urdNetValueOfSupplies", dto.getGstnNetSupplies().toString());
    	}
       
    }

    private static void populateTCSAParameters(Gstr8ReviewSummaryRespDto dto, Map<String, Object> parameters, Boolean isDigigst) {
    	if(isDigigst){
    		parameters.put("tcsaNoOfRecords", dto.getAspCount().toString());
            parameters.put("tcsaGrossSuppliesMade", dto.getAspGrossSuppliesMade().toString());
            parameters.put("tcsaSuppliesReturned", dto.getAspGrossSuppliesReturned().toString());
            parameters.put("tcsaNetAmount", dto.getAspNetSupplies().toString());
            parameters.put("tcsaIntegratedTax", dto.getAspIgst().toString());
            parameters.put("tcsaCentralTax", dto.getAspCgst().toString());
            parameters.put("tcsaStateTax", dto.getAspSgst().toString());
    	} else {
    		parameters.put("tcsaNoOfRecords", dto.getGstnCount().toString());
            parameters.put("tcsaGrossSuppliesMade", dto.getGstnGrossSuppliesMade().toString());
            parameters.put("tcsaSuppliesReturned", dto.getGstnGrossSuppliesReturned().toString());
            parameters.put("tcsaNetAmount", dto.getGstnNetSupplies().toString());
            parameters.put("tcsaIntegratedTax", dto.getGstnIgst().toString());
            parameters.put("tcsaCentralTax", dto.getGstnCgst().toString());
            parameters.put("tcsaStateTax", dto.getGstnSgst().toString());
    	}
        
    }

    private static void populateURDAParameters(Gstr8ReviewSummaryRespDto dto, Map<String, Object> parameters, Boolean isDigigst) {
    	if(isDigigst){
    		 parameters.put("urdaNoOfRecords", dto.getAspCount().toString());
    	     parameters.put("urdaGrossSuppliesMade", dto.getAspGrossSuppliesMade().toString());
    	     parameters.put("urdaSuppliesReturned", dto.getAspGrossSuppliesReturned().toString());
    	     parameters.put("urdaNetValueOfSupplies", dto.getAspNetSupplies().toString());
    	}else {
    		parameters.put("urdaNoOfRecords", dto.getGstnCount().toString());
	        parameters.put("urdaGrossSuppliesMade", dto.getGstnGrossSuppliesMade().toString());
	        parameters.put("urdaSuppliesReturned", dto.getGstnGrossSuppliesReturned().toString());
	        parameters.put("urdaNetValueOfSupplies", dto.getGstnNetSupplies().toString());
    	}
       
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

}
