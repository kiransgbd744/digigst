package com.ey.advisory.app.data.services.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.entities.client.DongleMappingEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.DongleMappingRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstnSaveToAspRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BMonthlyTrendTaxAmountRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BSetOffEntityComputeDetailsRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BEcoDtlsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BElgItcDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BIntLateFeeDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BInterStateSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BOutInwSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSecDetailsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3bpdfDTO;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputDto;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardDao;
import com.ey.advisory.app.gstr3b.Gstr3BGstinDashboardService;
import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffEntity;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspEntity;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.app.gstr3b.ItcPaidInnerDto;
import com.ey.advisory.app.gstr3b.dto.AmountTypeDetail;
import com.ey.advisory.app.gstr3b.dto.PaidCashDetails;
import com.ey.advisory.app.gstr3b.dto.PaidItcDetails;
import com.ey.advisory.app.gstr3b.dto.TaxPayableDetails;
import com.ey.advisory.app.gstr3b.dto.TaxPaymentDetailsInvoice;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BRequestDtoConverterImpl;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveDataFetcher;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIResponse;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityComputeDetailsEntity;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Component("Gstr3BSummaryPDFGenerationReportImpl")
public class Gstr3BSummaryPDFGenerationReportImpl
		implements Gstr3BSummaryPDFGenerationReport {

	@Autowired
	@Qualifier("saveGstr3BDataFetcherImpl")
	private Gstr3BSaveDataFetcher saveGstr3BData;

	@Autowired
	@Qualifier("gstr3BRequestDtoConverterImpl")
	private Gstr3BRequestDtoConverterImpl gstr3BConverter;

	@Autowired
	Gstr3BSaveChangesLiabilitySetOffRepository gstr3BSaveChangesLiabilitySetOffRepository;

	@Autowired
	Gstr3BSetOffEntityComputeDetailsRepository gstr3BSetOffEntityComputeDetailsRepository;

	@Autowired
	Gstr3BGstnSaveToAspRepository saveToAspRepository;

	@Autowired
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	GstnReturnFilingStatus gstnReturnFilingStatus;

	@Autowired
	EntityInfoDetailsRepository entityInfoDetailsRepository;

	@Autowired
	private Gstr3BGstinDashboardDao gstnDao;
	
	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;
	
	@Autowired
	Gstr3BMonthlyTrendTaxAmountRepository gstr3BMonthlyTrendTaxAmountRepository;
	
	@Autowired
	DongleMappingRepository dongleMappingRepository;
	
	@Autowired
	private Gstr3BGstinDashboardService dashBoardService;


	private static final String NET_LIABILITY = "NET_LIABILITY";
	private static final String ADDNL_CASH_REQ = "ADDNL_CASH_REQ";
	private static final String NET_GST_LIABILITY = "NET_GST_LIABILITY";
	private static final String OTHER_REVERSE_TAX_PAYABLE = "3.1(a)";
	private static final String REVERSE_TAX_PAYABLE = "3.1(d)";
	private static final String INTEREST = "5.1(a)";
	private static final String LATE_FEES = "5.1(b)";

	private static final String OUTINWSUPLABELA = "(a) Outward taxable supplies (other than zero rated,nil rated and exempted)";
	private static final String OUTINWSUPLABELB = "(b) Outward taxable supplies (zero rated)";
	private static final String OUTINWSUPLABELC = "(c) Other outward supplies (Nil rated, exempted)";
	private static final String OUTINWSUPLABELD = "(d) Inward supplies (liable to reverse charge)";
	private static final String OUTINWSUPLABELE = "(e) Non-GST outward supplies";

	private static final String SUPPNOTIFIEDA = "(i) Taxable supplies on which electronic commerce operator pays tax u/s 9(5) [to be furnished by electronic commerce operator]";
	private static final String SUPPNOTIFIEDB = "(ii) Taxable supplies made by registered person through electronic commerce operator, on which electronic commerce operator is required to pay tax u/s 9(5) [to be furnished by registered person making supplies through electronic commerce operator]";

	private static final String INTERSTATESUPPA = "Supplies made to Unregistered Persons";
	private static final String INTERSTATESUPPB = "Supplies made to Composition Taxable Persons";
	private static final String INTERSTATESUPPC = "Supplies made to UIN holders";

	private static final String ITCELGA = "A. ITC Available (whether in full or part)";
	private static final String ITCELGB = "B. ITC Reversed";
	private static final String ITCELGC = "C. Net ITC available (A-B)";
	private static final String ITCELGD = "(D) Other Details";

	private static final String INWARDSUPPLA = "From a supplier under composition  scheme, Exempt and Nil rated supply";
	private static final String INWARDSUPPLB = "Non GST supply";

	private static final String INTERESTLATEFEEA = "Interest Paid";
	private static final String INTERESTLATEFEEB = "Late fee";
	private static final String INTERESTLATEFEEC = "System computed Interest";

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	@Override
	public JasperPrint generateGstr3BSummaryPdfReport(String gstin,
			String taxPeriod, Boolean isDigigst, String isVerified) {

		JasperPrint jasperPrint = null;
		String source = getJasperReportSource(taxPeriod);
		boolean newSource = "jasperReports/GSTR3BSummeryTemplateNew.jrxml"
				.equalsIgnoreCase(source);
		Gstr3BSavetoGstnDTO dto = null;
		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());
		JsonObject jobj = new JsonObject();
		jobj.addProperty(APIConstants.GSTIN, gstin);
		jobj.addProperty(APIConstants.RETPERIOD, taxPeriod);

		try {
			List<Object[]> docs=null;
			if(isDigigst)
			{
				docs = saveGstr3BData.findInvoiceLevelData(
						jobj.toString(), TenantContext.getTenantId());
				
			}
			else
			{
				docs = saveGstr3BData.findInvoiceGstnLevelData(
						jobj.toString(), TenantContext.getTenantId());
			}
						
			if (docs != null && !docs.isEmpty()) {

				dto = gstr3BConverter.convertToGstr3BObject(docs,
						Gstr3BConstants.GSTR3B, TenantContext.getTenantId());

			}
			else
			{
				dto=new Gstr3BSavetoGstnDTO();
				dto.setGstin(gstin);
				dto.setRetPeriod(taxPeriod);
				Gstr3BOutInwSuppDTO gstr3BOutInwSuppDTO= new Gstr3BOutInwSuppDTO();
				gstr3BOutInwSuppDTO.setIsupRev(defaultToZero());
				gstr3BOutInwSuppDTO.setOsupDet(defaultToZero());
				gstr3BOutInwSuppDTO.setOsupNilExmp(defaultToZero());
				gstr3BOutInwSuppDTO.setOsupNongst(defaultToZero());
				gstr3BOutInwSuppDTO.setOsupZero(defaultToZero());
			
				Gstr3BEcoDtlsDTO gstr3BEcoDtlsDTO= new Gstr3BEcoDtlsDTO();
				gstr3BEcoDtlsDTO.setEcoRegSup(defaultToZero());
				gstr3BEcoDtlsDTO.setEcoSup(defaultToZero());
				
				Gstr3BInterStateSuppDTO gstr3BInterStateSuppDTO= new Gstr3BInterStateSuppDTO();
				gstr3BInterStateSuppDTO.setCompDetails(defaultToZeroList());
				gstr3BInterStateSuppDTO.setUinDetails(defaultToZeroList());
				gstr3BInterStateSuppDTO.setUnregDetails(defaultToZeroList());
				
				Gstr3BElgItcDTO gstr3BElgItcDTO = new Gstr3BElgItcDTO();
				gstr3BElgItcDTO.setItc_avl(defaultToZeroList());
				gstr3BElgItcDTO.setItcInelg(defaultToZeroList());
				gstr3BElgItcDTO.setItcNet(defaultToZero());
				gstr3BElgItcDTO.setItcRev(defaultToZeroList());
				
				
			//	Gstr3BInwSuppDTO gstr3BInwSuppDTO = new Gstr3BInwSuppDTO();
				//gstr3BInwSuppDTO.setIsupDetails(defaultToZeroList());
				
				Gstr3BIntLateFeeDTO gstr3BIntLateFeeDTO =new Gstr3BIntLateFeeDTO();
				gstr3BIntLateFeeDTO.setIntrDetails(defaultToZero());
				gstr3BIntLateFeeDTO.setLateFeeDetails(defaultToZero());
				
				TaxPaymentDetailsInvoice taxPaymentDetailsInvoice = new TaxPaymentDetailsInvoice();
				taxPaymentDetailsInvoice.setTaxPayable(taxPayableDefaultToZeroList());
				taxPaymentDetailsInvoice.setPaidItc(paidItcDetailsZero());
				taxPaymentDetailsInvoice.setPaidCash(paidCashDetails());
				dto.setEcoDtls(gstr3BEcoDtlsDTO);
				dto.setInterSup(gstr3BInterStateSuppDTO);
				dto.setIntrLtfee(gstr3BIntLateFeeDTO);
				dto.setInwardSup(null);
				dto.setItcElg(gstr3BElgItcDTO);
				dto.setSupDetails(gstr3BOutInwSuppDTO);
				dto.setTxPmt(taxPaymentDetailsInvoice);
			}
			
			String[] strArray = getMonthNameAndFinYear(taxPeriod);

			GSTNDetailEntity gstnDetailEntity = gSTNDetailRepository
					.findByGstinAndIsDeleteFalse(gstin);
			EntityInfoEntity entityInfoEntity = entityInfoDetailsRepository
					.findEntityByEntityId(gstnDetailEntity.getEntityId());

			Gstr3BSaveChangesLiabilitySetOffEntity liabilitySetOffEntity = gstr3BSaveChangesLiabilitySetOffRepository
					.findByGstinAndTaxPeriodAndIsActive(taxPeriod, gstin);
			
			Optional<Gstr3bMonthlyTrendTaxAmountsEntity> liabilitySetOffEntityGstin = gstr3BMonthlyTrendTaxAmountRepository.
					findBySuppGstinAndTaxPeriodAndIsActiveTrue(gstin,taxPeriod);

			List<Gstr3BSetOffEntityComputeDetailsEntity> computeDetailsEntityList = gstr3BSetOffEntityComputeDetailsRepository
					.findByGstinAndTaxPeriodAndIsDelete(gstin, taxPeriod,
							false);
			List<Gstr3bGstnSaveToAspEntity> saveToAspEntityList = saveToAspRepository
					.findOtherLiabilityAndReverseChargeTaxAndInterestAndLateFee(
							taxPeriod, gstin);

			if (dto == null && liabilitySetOffEntity == null
					&& CollectionUtils.isEmpty(computeDetailsEntityList)
					&& CollectionUtils.isEmpty(saveToAspEntityList)) {
				return null;
			}
			

			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeContainingIgnoreCaseAndIsCounterPartyGstinFalse(
							gstin, taxPeriod, Gstr3BConstants.GSTR3B);
			
			GstrReturnStatusEntity gstrReturnStatusEntitySubm = gstrReturnStatusRepository
					.findSubmittedRecords(gstin, taxPeriod,
							Gstr3BConstants.GSTR3B);

			if (gstrReturnStatusEntity == null || !(gstrReturnStatusEntity
					.getStatus().equalsIgnoreCase("Filed")
					|| gstrReturnStatusEntity.getStatus()
							.equalsIgnoreCase("SUBMITTED"))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTR3B_PDF_RET);
					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstin), strArray[1],
									false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(Gstr3BConstants.GSTR3B)
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
					
					/*	filingDate = gstrReturnStatusEntity.getFilingDate()
							.format(formatter2).toString() + " 00:00:00";*/

					filingDate = gstrReturnStatusEntity.getFilingDate().toString();

				} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {

					if (gstrReturnStatusEntitySubm != null) {
						filingDate = getStandardTime(
								gstrReturnStatusEntitySubm.getUpdatedOn());
					}
				}

			}

			List<Gstr3bpdfDTO> outInwSuppDTOList = new ArrayList<>();
			List<Gstr3bpdfDTO> suppNotiDTOList = new ArrayList<>();
			List<Gstr3bpdfDTO> interSupDTOList = new ArrayList<>();
			List<Gstr3bpdfDTO> ItcElgDTOList = new ArrayList<>();
				
			List<Gstr3bpdfDTO> inwSuppDTOList = new ArrayList<>();
			List<Gstr3bpdfDTO> intrLateDTOList = new ArrayList<>();
			List<Gstr3bpdfDTO> supplList = new ArrayList<>();

			Gstr3bpdfDTO outInwSuppDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO outInwSuppDTOb = new Gstr3bpdfDTO();
			Gstr3bpdfDTO outInwSuppDTOc = new Gstr3bpdfDTO();
			Gstr3bpdfDTO outInwSuppDTOd = new Gstr3bpdfDTO();
			Gstr3bpdfDTO outInwSuppDTOe = new Gstr3bpdfDTO();

			Gstr3bpdfDTO suppNotiDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO suppNotiDTOb = new Gstr3bpdfDTO();

			Gstr3bpdfDTO interSupDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO interSupDTOb = new Gstr3bpdfDTO();
			Gstr3bpdfDTO interSupDTOc = new Gstr3bpdfDTO();

			Gstr3bpdfDTO ItcElgDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO ItcElgDTOb = new Gstr3bpdfDTO();
			
			Gstr3bpdfDTO ItcElgDTOd = new Gstr3bpdfDTO();
			//Itc available a
			Gstr3bpdfDTO impgDto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO impsDto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO isrcDto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO isdDto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO othDto = new Gstr3bpdfDTO();
			//Itc available b
			Gstr3bpdfDTO rulDto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO othRevDto = new Gstr3bpdfDTO();
			//Itc available c
			Gstr3bpdfDTO ItcElgDTOc = new Gstr3bpdfDTO();
			//Itc available d
			Gstr3bpdfDTO rul4d2Dto = new Gstr3bpdfDTO();
			Gstr3bpdfDTO rul4d1Dto = new Gstr3bpdfDTO();
			
			Gstr3bpdfDTO inwSuppDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO inwSuppDTOb = new Gstr3bpdfDTO();

			Gstr3bpdfDTO intrLateDTOa = new Gstr3bpdfDTO();
			Gstr3bpdfDTO intrLateDTOb = new Gstr3bpdfDTO();
			Gstr3bpdfDTO intrLateDTOc = new Gstr3bpdfDTO();

			Map<String, Object> parameters = new HashMap<>();

			parameters.put("year", strArray[1]);
			parameters.put("period", strArray[0]);
			parameters.put("gstin", dto.getGstin());
			parameters.put("statusTimeStamp", filingDate);
			String groupCode = TenantContext.getTenantId();
			
			TaxPayerDetailsDto apiResp =null;
			if(Strings.isNullOrEmpty(gstnDetailEntity.getLegalName())  
					&& Strings.isNullOrEmpty(gstnDetailEntity.getTradeName())){
				try {
					apiResp = taxPayerService
							.getTaxPayerDetails(gstin, groupCode);
				} catch (Exception e) {
					LOGGER.error(
							"Not able to taxpayer details from gstn");
				}
				gSTNDetailRepository.updateLegalAndTradeName(
						apiResp.getLegalBussNam(), 
						apiResp.getTradeName(), gstin, 
						gstnDetailEntity.getEntityId(), groupCode);
			}
			
			
			if(gstnDetailEntity != null){
				if(gstnDetailEntity.getLegalName() != null){
					parameters.put("legalBussName", gstnDetailEntity.getLegalName());
				}
				
				if(gstnDetailEntity.getTradeName() != null){
					parameters.put("tradeName", gstnDetailEntity.getTradeName());
				}
			}
			
			if(gstrReturnStatusEntity != null && gstrReturnStatusEntity.getArnNo() != null){
					parameters.put("arnNo", gstrReturnStatusEntity.getArnNo());
			}
			
			if(gstrReturnStatusEntity != null && gstrReturnStatusEntity.getFilingDate() != null){
				String fmtLocalDate = EYDateUtil.fmtLocalDate(gstrReturnStatusEntity.getFilingDate());
				parameters.put("filingDate", fmtLocalDate);
			}
			
			
			String verification = "";
			if(isVerified.equalsIgnoreCase("Yes")){
				verification = "I hereby solemnly affirm and declare that the "
						+ "information given herein above is true and correct to "
						+ "the best of my knowledge and belief and "
						+ "nothing has been concealed there from.";
			}
			parameters.put("verification", verification);
			
			String amountsInfo = "";
			if (isDigigst) {
				amountsInfo = "(Amounts as per GSTN Data)";
			}
			parameters.put("amountsInfo", amountsInfo);
			
			List<DongleMappingEntity> dongleMapping = dongleMappingRepository
					.findByGstinAndIsActiveTrue(gstin);
			
			if(!dongleMapping.isEmpty() || dongleMapping != null){
				for(DongleMappingEntity dongle: dongleMapping){
					if(dongle.getGstin().equalsIgnoreCase(gstin)){
						if (filingStatus.equalsIgnoreCase("Filed")) {
							parameters.put("authorisedSignatory", dongle.getAuthorisedName());
							parameters.put("designation", dongle.getDesignation());
						}
						
					}
				}
			}
			/*if(isDigigst)
			{
				String digigst="DigiGST processed data";
				parameters.put("Digigst", digigst);
			}
			else
			{
				String gstn1="Update GSTN Data";
				parameters.put("Digigst", gstn1);
			}*/
			
			parameters.put("Digigst", isDigigst?"* All figures are as per DigiGST processed data":"* All figures are as per Latest Update GSTN Data");

			if (gstnDetailEntity != null) {
				parameters.put("legalName", gstnDetailEntity.getLegalName());
			}

			List<Gstr3BGstinAspUserInputDto> userInputDto = gstnDao
					.getUserInputDtoforSavePst(taxPeriod, gstin,
							Arrays.asList(Gstr3BConstants.Table7_1));

			if (!userInputDto.isEmpty() && userInputDto != null) {
				for (Gstr3BGstinAspUserInputDto userInput : userInputDto) {
					Gstr3bpdfDTO savePPLiaDto = new Gstr3bpdfDTO();
					savePPLiaDto.setLabel(userInput.getUserRetPeriod());
					savePPLiaDto.setIgst(
							convertBigDecimalToString(userInput.getIgst()));
					savePPLiaDto.setCgst(
							convertBigDecimalToString(userInput.getCgst()));
					savePPLiaDto.setSgst(
							convertBigDecimalToString(userInput.getSgst()));
					savePPLiaDto.setCess(
							convertBigDecimalToString(userInput.getCess()));
					supplList.add(savePPLiaDto);
				}
			}

			parameters.put("spplList",
					new JRBeanCollectionDataSource(supplList));

			if (dto.getSupDetails() != null) {
				outInwSuppDTOa.setLabel(OUTINWSUPLABELA);

				if (dto.getSupDetails().getOsupDet() != null) {
					outInwSuppDTOa
							.setTotalTaxableValue(convertBigDecimalToString(dto
									.getSupDetails().getOsupDet().getTxval()));
					outInwSuppDTOa.setIgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupDet().getIamt()));

					outInwSuppDTOa.setCgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupDet().getCamt()));

					outInwSuppDTOa.setSgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupDet().getSamt()));

					outInwSuppDTOa.setCess(convertBigDecimalToString(
							dto.getSupDetails().getOsupDet().getCsamt()));
				}
				outInwSuppDTOList.add(outInwSuppDTOa);

				outInwSuppDTOb.setLabel(OUTINWSUPLABELB);
				if (dto.getSupDetails().getOsupZero() != null) {
					outInwSuppDTOb
							.setTotalTaxableValue(convertBigDecimalToString(dto
									.getSupDetails().getOsupZero().getTxval()));
					outInwSuppDTOb.setIgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupZero().getIamt()));

					outInwSuppDTOb.setCgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupZero().getCamt()));

					outInwSuppDTOb.setSgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupZero().getSamt()));

					outInwSuppDTOb.setCess(convertBigDecimalToString(
							dto.getSupDetails().getOsupZero().getCsamt()));

				}
				outInwSuppDTOList.add(outInwSuppDTOb);

				outInwSuppDTOc.setLabel(OUTINWSUPLABELC);

				if (dto.getSupDetails().getOsupNilExmp() != null) {

					outInwSuppDTOc.setTotalTaxableValue(
							convertBigDecimalToString(dto.getSupDetails()
									.getOsupNilExmp().getTxval()));
					outInwSuppDTOc.setIgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNilExmp().getIamt()));

					outInwSuppDTOc.setCgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNilExmp().getCamt()));

					outInwSuppDTOc.setSgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNilExmp().getSamt()));

					outInwSuppDTOc.setCess(convertBigDecimalToString(
							dto.getSupDetails().getOsupNilExmp().getCsamt()));
				}

				outInwSuppDTOList.add(outInwSuppDTOc);

				outInwSuppDTOd.setLabel(OUTINWSUPLABELD);

				if (dto.getSupDetails().getIsupRev() != null) {

					outInwSuppDTOd
							.setTotalTaxableValue(convertBigDecimalToString(dto
									.getSupDetails().getIsupRev().getTxval()));
					outInwSuppDTOd.setIgst(convertBigDecimalToString(
							dto.getSupDetails().getIsupRev().getIamt()));

					outInwSuppDTOd.setCgst(convertBigDecimalToString(
							dto.getSupDetails().getIsupRev().getCamt()));

					outInwSuppDTOd.setSgst(convertBigDecimalToString(
							dto.getSupDetails().getIsupRev().getSamt()));

					outInwSuppDTOd.setCess(convertBigDecimalToString(
							dto.getSupDetails().getIsupRev().getCsamt()));

				}

				outInwSuppDTOList.add(outInwSuppDTOd);

				outInwSuppDTOe.setLabel(OUTINWSUPLABELE);

				if (dto.getSupDetails().getOsupNongst() != null) {

					outInwSuppDTOe.setTotalTaxableValue(
							convertBigDecimalToString(dto.getSupDetails()
									.getOsupNongst().getTxval()));
					outInwSuppDTOe.setIgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNongst().getIamt()));

					outInwSuppDTOe.setCgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNongst().getCamt()));

					outInwSuppDTOe.setSgst(convertBigDecimalToString(
							dto.getSupDetails().getOsupNongst().getSamt()));

					outInwSuppDTOe.setCess(convertBigDecimalToString(
							dto.getSupDetails().getOsupNongst().getCsamt()));

				}
				outInwSuppDTOList.add(outInwSuppDTOe);
			} else {
				outInwSuppDTOa.setLabel(OUTINWSUPLABELA);
				outInwSuppDTOb.setLabel(OUTINWSUPLABELB);
				outInwSuppDTOc.setLabel(OUTINWSUPLABELC);
				outInwSuppDTOd.setLabel(OUTINWSUPLABELD);
				outInwSuppDTOe.setLabel(OUTINWSUPLABELE);
				Collections.addAll(outInwSuppDTOList, outInwSuppDTOa,
						outInwSuppDTOb, outInwSuppDTOc, outInwSuppDTOd,
						outInwSuppDTOe);
			}

			parameters.put("outInwSuppList",
					new JRBeanCollectionDataSource(outInwSuppDTOList));

			if (dto.getEcoDtls() != null) {
				suppNotiDTOa.setLabel(SUPPNOTIFIEDA);

				if (dto.getEcoDtls().getEcoSup() != null) {
					suppNotiDTOa.setTotalTaxableValue(convertBigDecimalToString(
							dto.getEcoDtls().getEcoSup().getTxval()));
					suppNotiDTOa.setIgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoSup().getIamt()));
					suppNotiDTOa.setCgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoSup().getCamt()));
					suppNotiDTOa.setSgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoSup().getSamt()));
					suppNotiDTOa.setCess(convertBigDecimalToString(
							dto.getEcoDtls().getEcoSup().getCsamt()));
				}
				suppNotiDTOList.add(suppNotiDTOa);

				suppNotiDTOb.setLabel(SUPPNOTIFIEDB);
				if (dto.getEcoDtls().getEcoRegSup() != null) {
					suppNotiDTOb.setTotalTaxableValue(convertBigDecimalToString(
							dto.getEcoDtls().getEcoRegSup().getTxval()));
					suppNotiDTOb.setIgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoRegSup().getIamt()));
					suppNotiDTOb.setCgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoRegSup().getCamt()));
					suppNotiDTOb.setSgst(convertBigDecimalToString(
							dto.getEcoDtls().getEcoRegSup().getSamt()));
					suppNotiDTOb.setCess(convertBigDecimalToString(
							dto.getEcoDtls().getEcoRegSup().getCsamt()));
				}
				suppNotiDTOList.add(suppNotiDTOb);

			} else {
				suppNotiDTOa.setLabel(SUPPNOTIFIEDA);
				suppNotiDTOb.setLabel(SUPPNOTIFIEDB);
				Collections.addAll(suppNotiDTOList, suppNotiDTOa, suppNotiDTOb);
			}

			parameters.put("suppNotifiedList",
					new JRBeanCollectionDataSource(suppNotiDTOList));

			if (dto.getInterSup() != null) {

				interSupDTOa.setLabel(INTERSTATESUPPA);
				if (!CollectionUtils
						.isEmpty(dto.getInterSup().getUnregDetails())) {

					BigDecimal interStateSuppliesATaxableTax = BigDecimal.ZERO;
					BigDecimal interStateSuppliesAIntegratedTax = BigDecimal.ZERO;

					for (Gstr3BSecDetailsDTO unReg : dto.getInterSup()
							.getUnregDetails()) {

						interStateSuppliesATaxableTax = interStateSuppliesATaxableTax
								.add(returnZeroIfNull(unReg.getTxval()));
						interStateSuppliesAIntegratedTax = interStateSuppliesAIntegratedTax
								.add(returnZeroIfNull(unReg.getIamt()));
					}
					interSupDTOa.setTotalTaxableValue(convertBigDecimalToString(
							interStateSuppliesATaxableTax));
					interSupDTOa.setIgst(convertBigDecimalToString(
							interStateSuppliesAIntegratedTax));
				}
				interSupDTOList.add(interSupDTOa);

				interSupDTOb.setLabel(INTERSTATESUPPB);
				if (!CollectionUtils
						.isEmpty(dto.getInterSup().getCompDetails())) {

					BigDecimal interStateSuppliesBTaxableTax = BigDecimal.ZERO;
					BigDecimal interStateSuppliesBIntegratedTax = BigDecimal.ZERO;

					for (Gstr3BSecDetailsDTO compDetails : dto.getInterSup()
							.getCompDetails()) {
						interStateSuppliesBTaxableTax = interStateSuppliesBTaxableTax
								.add(returnZeroIfNull(compDetails.getTxval()));
						interStateSuppliesBIntegratedTax = interStateSuppliesBIntegratedTax
								.add(returnZeroIfNull(compDetails.getIamt()));

					}

					interSupDTOb.setTotalTaxableValue(convertBigDecimalToString(
							interStateSuppliesBTaxableTax));
					interSupDTOb.setIgst(convertBigDecimalToString(
							interStateSuppliesBIntegratedTax));

				}
				interSupDTOList.add(interSupDTOb);
				interSupDTOc.setLabel(INTERSTATESUPPC);

				if (!CollectionUtils
						.isEmpty(dto.getInterSup().getUinDetails())) {

					BigDecimal interStateSuppliesCTaxableTax = BigDecimal.ZERO;
					BigDecimal interStateSuppliesCIntegratedTax = BigDecimal.ZERO;

					for (Gstr3BSecDetailsDTO unReg : dto.getInterSup()
							.getUinDetails()) {
						interStateSuppliesCTaxableTax = interStateSuppliesCTaxableTax
								.add(returnZeroIfNull(unReg.getTxval()));
						interStateSuppliesCIntegratedTax = interStateSuppliesCIntegratedTax
								.add(returnZeroIfNull(unReg.getIamt()));
					}

					interSupDTOc.setTotalTaxableValue(convertBigDecimalToString(
							interStateSuppliesCTaxableTax));
					interSupDTOc.setIgst(convertBigDecimalToString(
							interStateSuppliesCIntegratedTax));

				}
				interSupDTOList.add(interSupDTOc);

			} else {
				interSupDTOa.setLabel(INTERSTATESUPPA);
				interSupDTOb.setLabel(INTERSTATESUPPB);
				interSupDTOc.setLabel(INTERSTATESUPPC);
				Collections.addAll(interSupDTOList, interSupDTOa, interSupDTOb,
						interSupDTOc);

			}

			parameters.put("interSupList",
					new JRBeanCollectionDataSource(interSupDTOList));

			if (dto.getItcElg() != null) {
				ItcElgDTOa.setLabel(ITCELGA);
				ItcElgDTOa.setIgst("");
				ItcElgDTOa.setCgst("");
				ItcElgDTOa.setSgst("");
				ItcElgDTOa.setCess("");
				if (!CollectionUtils.isEmpty(dto.getItcElg().getItc_avl())) {

					/*BigDecimal eligibleItcAIntegratedTax = BigDecimal.ZERO;
					BigDecimal eligibleItcACentralTax = BigDecimal.ZERO;
					BigDecimal eligibleItcAStateTax = BigDecimal.ZERO;
					BigDecimal eligibleItcACessTax = BigDecimal.ZERO;*/

					for (Gstr3BSecDetailsDTO itcAvl : dto.getItcElg()
							.getItc_avl()) {
						if(itcAvl != null && itcAvl.getTy() != null){
							if(itcAvl.getTy().equalsIgnoreCase("IMPG")){
								//Gstr3bpdfDTO impgDto = new Gstr3bpdfDTO();
								impgDto.setLabel("(1) Import of goods");
								impgDto.setIgst(convertBigDecimalToString(itcAvl.getIamt()));
								impgDto.setCgst(convertBigDecimalToString(itcAvl.getCamt()));
								impgDto.setSgst(convertBigDecimalToString(itcAvl.getSamt()));
								impgDto.setCess(convertBigDecimalToString(itcAvl.getCsamt()));
								//ItcElgDTOListA.add(impgDto);
							}
							
							if(itcAvl.getTy().equalsIgnoreCase("IMPS")){
								//Gstr3bpdfDTO impsDto = new Gstr3bpdfDTO();
								impsDto.setLabel("(2) Import of services");
								impsDto.setIgst(convertBigDecimalToString(itcAvl.getIamt()));
								impsDto.setCgst(convertBigDecimalToString(itcAvl.getCamt()));
								impsDto.setSgst(convertBigDecimalToString(itcAvl.getSamt()));
								impsDto.setCess(convertBigDecimalToString(itcAvl.getCsamt()));
								//ItcElgDTOListA.add(impsDto);
							}
							
							if(itcAvl.getTy().equalsIgnoreCase("ISRC")){
								//Gstr3bpdfDTO isrcDto = new Gstr3bpdfDTO();
								isrcDto.setLabel("(3) Inward supplies liable to reverse charge (other than 1 & 2 above) ");
								isrcDto.setIgst(convertBigDecimalToString(itcAvl.getIamt()));
								isrcDto.setCgst(convertBigDecimalToString(itcAvl.getCamt()));
								isrcDto.setSgst(convertBigDecimalToString(itcAvl.getSamt()));
								isrcDto.setCess(convertBigDecimalToString(itcAvl.getCsamt()));
								//ItcElgDTOListA.add(isrcDto);
							}
							
							if(itcAvl.getTy().equalsIgnoreCase("ISD")){
								//Gstr3bpdfDTO isdDto = new Gstr3bpdfDTO();
								isdDto.setLabel("(4) Inward supplies from ISD");
								isdDto.setIgst(convertBigDecimalToString(itcAvl.getIamt()));
								isdDto.setCgst(convertBigDecimalToString(itcAvl.getCamt()));
								isdDto.setSgst(convertBigDecimalToString(itcAvl.getSamt()));
								isdDto.setCess(convertBigDecimalToString(itcAvl.getCsamt()));
								//ItcElgDTOListA.add(isdDto);
							}
							
							if(itcAvl.getTy().equalsIgnoreCase("OTH")){
								//Gstr3bpdfDTO othDto = new Gstr3bpdfDTO();
								othDto.setLabel("(5) All other ITC ");
								othDto.setIgst(convertBigDecimalToString(itcAvl.getIamt()));
								othDto.setCgst(convertBigDecimalToString(itcAvl.getCamt()));
								othDto.setSgst(convertBigDecimalToString(itcAvl.getSamt()));
								othDto.setCess(convertBigDecimalToString(itcAvl.getCsamt()));
								//ItcElgDTOListA.add(othDto);
							}
						}
						
						/*eligibleItcAIntegratedTax = eligibleItcAIntegratedTax
								.add(returnZeroIfNull(itcAvl.getIamt()));
						eligibleItcACentralTax = eligibleItcACentralTax
								.add(returnZeroIfNull(itcAvl.getCamt()));
						eligibleItcAStateTax = eligibleItcAStateTax
								.add(returnZeroIfNull(itcAvl.getSamt()));
						eligibleItcACessTax = eligibleItcACessTax
								.add(returnZeroIfNull(itcAvl.getCsamt()));*/
						
					}

				/*	ItcElgDTOa.setIgst(convertBigDecimalToString(
							eligibleItcAIntegratedTax));
					ItcElgDTOa.setCgst(
							convertBigDecimalToString(eligibleItcACentralTax));
					ItcElgDTOa.setSgst(
							convertBigDecimalToString(eligibleItcAStateTax));
					ItcElgDTOa.setCess(
							convertBigDecimalToString(eligibleItcACessTax));*/

				}
				ItcElgDTOList.add(ItcElgDTOa);
				ItcElgDTOList.add(impgDto);
				ItcElgDTOList.add(impsDto);
				ItcElgDTOList.add(isrcDto);
				ItcElgDTOList.add(isdDto);
				ItcElgDTOList.add(othDto);
				
				ItcElgDTOb.setLabel(ITCELGB);
				ItcElgDTOb.setIgst("");
				ItcElgDTOb.setCgst("");
				ItcElgDTOb.setSgst("");
				ItcElgDTOb.setCess("");

				if (!CollectionUtils.isEmpty(dto.getItcElg().getItcRev())) {
				/*	BigDecimal eligibleItcBIntegratedTax = BigDecimal.ZERO;
					BigDecimal eligibleItcBCentralTax = BigDecimal.ZERO;
					BigDecimal eligibleItcBStateTax = BigDecimal.ZERO;
					BigDecimal eligibleItcBCessTax = BigDecimal.ZERO;*/

					for (Gstr3BSecDetailsDTO itcRev : dto.getItcElg()
							.getItcRev()) {
						if(itcRev != null && itcRev.getTy() != null){
							if(itcRev.getTy().equalsIgnoreCase("RUL")){
								//Gstr3bpdfDTO rulDto = new Gstr3bpdfDTO();
								rulDto.setLabel("(1) As per rules 38,42 & 43 of CGST Rules and section 17(5)");
								rulDto.setIgst(convertBigDecimalToString(itcRev.getIamt()));
								rulDto.setCgst(convertBigDecimalToString(itcRev.getCamt()));
								rulDto.setSgst(convertBigDecimalToString(itcRev.getSamt()));
								rulDto.setCess(convertBigDecimalToString(itcRev.getCsamt()));
								//ItcElgDTOListB.add(rulDto);
							}
							
							if(itcRev.getTy().equalsIgnoreCase("OTH")){
								//Gstr3bpdfDTO othRevDto = new Gstr3bpdfDTO();
								othRevDto.setLabel("(2) Others");
								othRevDto.setIgst(convertBigDecimalToString(itcRev.getIamt()));
								othRevDto.setCgst(convertBigDecimalToString(itcRev.getCamt()));
								othRevDto.setSgst(convertBigDecimalToString(itcRev.getSamt()));
								othRevDto.setCess(convertBigDecimalToString(itcRev.getCsamt()));
								//ItcElgDTOListB.add(othRevDto);
							}
						}
						
						

						/*eligibleItcBIntegratedTax = eligibleItcBIntegratedTax
								.add(returnZeroIfNull(itcRev.getIamt()));
						eligibleItcBCentralTax = eligibleItcBCentralTax
								.add(returnZeroIfNull(itcRev.getCamt()));
						eligibleItcBStateTax = eligibleItcBStateTax
								.add(returnZeroIfNull(itcRev.getSamt()));
						eligibleItcBCessTax = eligibleItcBCessTax
								.add(returnZeroIfNull(itcRev.getCsamt()));*/

					}

					/*ItcElgDTOb.setIgst(convertBigDecimalToString(
							eligibleItcBIntegratedTax));
					ItcElgDTOb.setCgst(
							convertBigDecimalToString(eligibleItcBCentralTax));
					ItcElgDTOb.setSgst(
							convertBigDecimalToString(eligibleItcBStateTax));
					ItcElgDTOb.setCess(
							convertBigDecimalToString(eligibleItcBCessTax));*/

				}
				ItcElgDTOList.add(ItcElgDTOb);
				ItcElgDTOList.add(rulDto);
				ItcElgDTOList.add(othRevDto);
				ItcElgDTOc.setLabel(ITCELGC);
				if (dto.getItcElg().getItcNet() != null) {

					ItcElgDTOc.setIgst(convertBigDecimalToString(
							dto.getItcElg().getItcNet().getIamt()));
					ItcElgDTOc.setCgst(convertBigDecimalToString(
							dto.getItcElg().getItcNet().getCamt()));
					ItcElgDTOc.setSgst(convertBigDecimalToString(
							dto.getItcElg().getItcNet().getSamt()));
					ItcElgDTOc.setCess(convertBigDecimalToString(
							dto.getItcElg().getItcNet().getCsamt()));
					//ItcElgDTOListC.add(ItcElgDTOc);

				}
				ItcElgDTOList.add(ItcElgDTOc);
				ItcElgDTOd.setLabel(ITCELGD);
				ItcElgDTOb.setIgst("");
				ItcElgDTOb.setCgst("");
				ItcElgDTOb.setSgst("");
				ItcElgDTOb.setCess("");
				if (!CollectionUtils.isEmpty(dto.getItcElg().getItcInelg())) {
				/*	BigDecimal eligibleItcDIntegratedTax = BigDecimal.ZERO;
					BigDecimal eligibleItcDCentralTax = BigDecimal.ZERO;
					BigDecimal eligibleItcDStateTax = BigDecimal.ZERO;
					BigDecimal eligibleItcDCessTax = BigDecimal.ZERO;*/

					for (Gstr3BSecDetailsDTO itcInelg : dto.getItcElg()
							.getItcInelg()) {
						if(itcInelg != null && itcInelg.getTy() != null){
							if(itcInelg.getTy().equalsIgnoreCase("RUL")){
								//Gstr3bpdfDTO rul4d1Dto = new Gstr3bpdfDTO();
								rul4d1Dto.setLabel("(1) ITC reclaimed which was reversed under Table 4(B)(2) in earlier tax period");
								rul4d1Dto.setIgst(convertBigDecimalToString(itcInelg.getIamt()));
								rul4d1Dto.setCgst(convertBigDecimalToString(itcInelg.getCamt()));
								rul4d1Dto.setSgst(convertBigDecimalToString(itcInelg.getSamt()));
								rul4d1Dto.setCess(convertBigDecimalToString(itcInelg.getCsamt()));
								//ItcElgDTOListD.add(rul4d1Dto);
							}
							
							if(itcInelg.getTy().equalsIgnoreCase("OTH")){
								//Gstr3bpdfDTO rul4d2Dto = new Gstr3bpdfDTO();
								rul4d2Dto.setLabel("(2) Ineligible ITC under section 16(4) & ITC restricted due to PoS rules");
								rul4d2Dto.setIgst(convertBigDecimalToString(itcInelg.getIamt()));
								rul4d2Dto.setCgst(convertBigDecimalToString(itcInelg.getCamt()));
								rul4d2Dto.setSgst(convertBigDecimalToString(itcInelg.getSamt()));
								rul4d2Dto.setCess(convertBigDecimalToString(itcInelg.getCsamt()));
								//ItcElgDTOListD.add(rul4d2Dto);ItcElgDTOListD.add(rul4d1Dto);
							}
						}
						
						
						/*eligibleItcDIntegratedTax = eligibleItcDIntegratedTax
								.add(returnZeroIfNull(itcInelg.getIamt()));
						eligibleItcDCentralTax = eligibleItcDCentralTax
								.add(returnZeroIfNull(itcInelg.getCamt()));
						eligibleItcDStateTax = eligibleItcDStateTax
								.add(returnZeroIfNull(itcInelg.getSamt()));
						eligibleItcDCessTax = eligibleItcDCessTax
								.add(returnZeroIfNull(itcInelg.getCsamt()));*/
						
						

					}

					/*ItcElgDTOd.setIgst(convertBigDecimalToString(
							eligibleItcDIntegratedTax));
					ItcElgDTOd.setCgst(
							convertBigDecimalToString(eligibleItcDCentralTax));
					ItcElgDTOd.setSgst(
							convertBigDecimalToString(eligibleItcDStateTax));
					ItcElgDTOd.setCess(
							convertBigDecimalToString(eligibleItcDCessTax));*/

				}
				ItcElgDTOList.add(ItcElgDTOd);
				ItcElgDTOList.add(rul4d1Dto);
				ItcElgDTOList.add(rul4d2Dto);
				
			} else {
				ItcElgDTOa.setLabel(ITCELGA);
				ItcElgDTOb.setLabel(ITCELGB);
				ItcElgDTOc.setLabel(ITCELGC);
				ItcElgDTOd.setLabel(ITCELGD);
				Collections.addAll(ItcElgDTOList, ItcElgDTOa, 
						impgDto, impsDto, isrcDto, isdDto, othDto, 
						ItcElgDTOb, rulDto, othRevDto,
						ItcElgDTOc, ItcElgDTOd, rul4d1Dto, rul4d2Dto);

			}

			parameters.put("ItcElgList",
					new JRBeanCollectionDataSource(ItcElgDTOList));
			/*parameters.put("ItcElgDTOListA",
					new JRBeanCollectionDataSource(ItcElgDTOListA));
			parameters.put("ItcElgDTOListB",
					new JRBeanCollectionDataSource(ItcElgDTOListB));
			parameters.put("ItcElgDTOListC",
					new JRBeanCollectionDataSource(ItcElgDTOListC));
			parameters.put("ItcElgDTOListD",
					new JRBeanCollectionDataSource(ItcElgDTOListD));*/

			if (dto.getInwardSup() != null) {
				inwSuppDTOa.setLabel(INWARDSUPPLA);
				inwSuppDTOb.setLabel(INWARDSUPPLB);
				if (!CollectionUtils
						.isEmpty(dto.getInwardSup().getIsupDetails())) {

					for (Gstr3BSecDetailsDTO isupDetails : dto.getInwardSup()
							.getIsupDetails()) {
						if (isupDetails.getTy().equalsIgnoreCase("GST")) {

							inwSuppDTOa.setInterStateSupplies(
									convertBigDecimalToString(
											isupDetails.getInter()));
							inwSuppDTOa.setIntraStateSupplies(
									convertBigDecimalToString(
											isupDetails.getIntra()));
						} else if (isupDetails.getTy()
								.equalsIgnoreCase("NONGST")) {

							inwSuppDTOb.setInterStateSupplies(
									convertBigDecimalToString(
											isupDetails.getInter()));
							inwSuppDTOb.setIntraStateSupplies(
									convertBigDecimalToString(
											isupDetails.getIntra()));

						}
					}
				}
				Collections.addAll(inwSuppDTOList, inwSuppDTOa, inwSuppDTOb);
			} else {
				inwSuppDTOa.setLabel(INWARDSUPPLA);
				inwSuppDTOb.setLabel(INWARDSUPPLB);
				Collections.addAll(inwSuppDTOList, inwSuppDTOa, inwSuppDTOb);

			}
			parameters.put("inwSuppList",
					new JRBeanCollectionDataSource(inwSuppDTOList));

			if (dto.getIntrLtfee() != null) {
				intrLateDTOa.setLabel(INTERESTLATEFEEC);
				
				intrLateDTOList.add(intrLateDTOa);
				intrLateDTOb.setLabel(INTERESTLATEFEEA);
				if (dto.getIntrLtfee().getIntrDetails() != null) {
					intrLateDTOb.setIgst(convertBigDecimalToString(
							dto.getIntrLtfee().getIntrDetails().getIamt()));
					intrLateDTOb.setCgst(convertBigDecimalToString(
							dto.getIntrLtfee().getIntrDetails().getCamt()));
					intrLateDTOb.setSgst(convertBigDecimalToString(
							dto.getIntrLtfee().getIntrDetails().getSamt()));
					intrLateDTOb.setCess(convertBigDecimalToString(
							dto.getIntrLtfee().getIntrDetails().getCsamt()));

				}
				intrLateDTOList.add(intrLateDTOb);
				intrLateDTOc.setLabel(INTERESTLATEFEEB);
				if (dto.getIntrLtfee().getLateFeeDetails() != null) {

					intrLateDTOc.setIgst(convertBigDecimalToString(
							dto.getIntrLtfee().getLateFeeDetails().getIamt()));
					intrLateDTOc.setCgst(convertBigDecimalToString(
							dto.getIntrLtfee().getLateFeeDetails().getCamt()));
					intrLateDTOc.setSgst(convertBigDecimalToString(
							dto.getIntrLtfee().getLateFeeDetails().getSamt()));
					intrLateDTOc.setCess(convertBigDecimalToString(
							dto.getIntrLtfee().getLateFeeDetails().getCsamt()));

				}
				intrLateDTOList.add(intrLateDTOc);
			} else {
				intrLateDTOb.setLabel(INTERESTLATEFEEA);
				intrLateDTOc.setLabel(INTERESTLATEFEEB);
				Collections.addAll(intrLateDTOList, intrLateDTOa, intrLateDTOb, intrLateDTOc);

			}
			parameters.put("intrLateList",
					new JRBeanCollectionDataSource(intrLateDTOList));

			if(isDigigst){
				if (liabilitySetOffEntity != null) {
					parameters.put("iPdIgst", convertBigDecimalToString(
							liabilitySetOffEntity.getIPDIgst()));
					parameters.put("iPdCgst", convertBigDecimalToString(
							liabilitySetOffEntity.getIPDCgst()));
					parameters.put("iPdSgst", convertBigDecimalToString(
							liabilitySetOffEntity.getIPDSgst()));
					parameters.put("iPdCess", null);
					parameters.put("cPdIgst", convertBigDecimalToString(
							liabilitySetOffEntity.getCPDIgst()));
					parameters.put("cPdCgst", convertBigDecimalToString(
							liabilitySetOffEntity.getCPDCgst()));
					parameters.put("cPdSgst", null);
					parameters.put("cPdCess", null);
					parameters.put("sPdIgst", convertBigDecimalToString(
							liabilitySetOffEntity.getSPDIgst()));
					parameters.put("sPdCgst", null);
					parameters.put("sPdSgst", convertBigDecimalToString(
							liabilitySetOffEntity.getSPDSgst()));
					parameters.put("sPdCess", null);
					parameters.put("csPdIgst", null);
					parameters.put("csPdCgst", null);
					parameters.put("csPdSgst", null);
					parameters.put("csPdCess", convertBigDecimalToString(
							liabilitySetOffEntity.getCsPdCess()));
					
					if (newSource) {
						// --3b pdf start 1--digigst pdf
						// adjustment of negative--other than reverse charge
						parameters.put("aonlOrcIgst",
								convertBigDecimalToString1(liabilitySetOffEntity
										.getI_adjNegative2i() != null
												? liabilitySetOffEntity
														.getI_adjNegative2i()
												: null));

						parameters.put("aonlOrcCgst",
								convertBigDecimalToString1(liabilitySetOffEntity
										.getC_adjNegative2i() != null
												? liabilitySetOffEntity
														.getC_adjNegative2i()
												: null));

						parameters.put("aonlOrcSgst",
								convertBigDecimalToString1(liabilitySetOffEntity
										.getS_adjNegative2i() != null
												? liabilitySetOffEntity
														.getS_adjNegative2i()
												: null));

						parameters.put("aonlOrcCess",
								convertBigDecimalToString1(liabilitySetOffEntity
										.getCs_adjNegative2i() != null
												? liabilitySetOffEntity
														.getCs_adjNegative2i()
												: null));

						// adjustment of negative--reverse charge
						parameters.put("aonlRcIgst", convertBigDecimalToString1(
								liabilitySetOffEntity.getI_adjNegative8A()));
						parameters.put("aonlRcCgst", convertBigDecimalToString1(
								liabilitySetOffEntity.getC_adjNegative8A()));
						parameters.put("aonlRcSgst", convertBigDecimalToString1(
								liabilitySetOffEntity.getS_adjNegative8A()));
						parameters.put("aonlRcCess", convertBigDecimalToString1(
								liabilitySetOffEntity.getCs_adjNegative8A()));
					}
				}
			} else {
				if (liabilitySetOffEntityGstin != null) {
					parameters.put("iPdIgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(liabilitySetOffEntityGstin.get().getIgstVsIgstOthers())
					                : null);
					parameters.put("iPdCgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getIgstVsCgstOthers())
					        		: null);
					parameters.put("iPdSgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getIgstVsSgstOthers())
					        		: null);
					parameters.put("iPdCess", null);
					parameters.put("cPdIgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getCgstVsIgstOthers())
					        		: null);
					parameters.put("cPdCgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getCgstVsCgstOthers())
					        		: null);
					parameters.put("cPdSgst", null);
					parameters.put("cPdCess", null);
					parameters.put("sPdIgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getSgstVsIgstOthers())
					        		: null);
					parameters.put("sPdCgst", null);
					parameters.put("sPdSgst", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getSgstVsSgstOthers())
					        		: null);
					parameters.put("sPdCess", null);
					parameters.put("csPdIgst", null);
					parameters.put("csPdCgst", null);
					parameters.put("csPdSgst", null);
					parameters.put("csPdCess", liabilitySetOffEntityGstin.isPresent()
					        ? convertBigDecimalToString(
							liabilitySetOffEntityGstin.get().getCessVsCessOthers())
					        		: null);
				}
			}
			

			if (!CollectionUtils.isEmpty(computeDetailsEntityList)) {

				for (Gstr3BSetOffEntityComputeDetailsEntity computeDetailsEntity : computeDetailsEntityList) {

					BigDecimal additionalCashIgst = BigDecimal.ZERO;
					BigDecimal additionalCashCgst = BigDecimal.ZERO;
					BigDecimal additionalCashSgst = BigDecimal.ZERO;
					BigDecimal additionalCashCess = BigDecimal.ZERO;

					BigDecimal netGstLiabilityIgst = BigDecimal.ZERO;
					BigDecimal netGstLiabilityCgst = BigDecimal.ZERO;
					BigDecimal netGstLiabilitySgst = BigDecimal.ZERO;
					BigDecimal netGstLiabilityCess = BigDecimal.ZERO;

					BigDecimal utilizableCashIgst = BigDecimal.ZERO;
					BigDecimal utilizableCashCgst = BigDecimal.ZERO;
					BigDecimal utilizableCashSgst = BigDecimal.ZERO;
					BigDecimal utilizableCashCess = BigDecimal.ZERO;
					if(isDigigst){
						if (computeDetailsEntity.getSection()
								.equalsIgnoreCase(NET_LIABILITY)) {
							parameters.put("othrRevPdCashIgst",
									convertBigDecimalToString(
											computeDetailsEntity.getIgst()));
							parameters.put("othrRevPdCashCgst",
									convertBigDecimalToString(
											computeDetailsEntity.getCgst()));
							parameters.put("othrRevPdCashSgst",
									convertBigDecimalToString(
											computeDetailsEntity.getSgst()));
							parameters.put("othrRevPdCashCess",
									convertBigDecimalToString(
											computeDetailsEntity.getCess()));
						}
					} else {
						if(liabilitySetOffEntityGstin != null){
							parameters.put("othrRevPdCashIgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get().getTaxCashOthrsIgst())
							        		: null);
							parameters.put("othrRevPdCashCgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get().getTaxCashOthrsCgst())
							        		: null);
							parameters.put("othrRevPdCashSgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get().getTaxCashOthrsSgst())
							        		: null);
							parameters.put("othrRevPdCashCess",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get().getTaxCashOthrsCess())
							        		: null);
						}
					}
					

					if (computeDetailsEntity.getSection()
							.equalsIgnoreCase(ADDNL_CASH_REQ)) {

						additionalCashIgst = computeDetailsEntity.getIgst();
						additionalCashCgst = computeDetailsEntity.getCgst();
						additionalCashSgst = computeDetailsEntity.getSgst();
						additionalCashCess = computeDetailsEntity.getCess();

						parameters.put("additionalCashIgst",
								convertBigDecimalToString(additionalCashIgst));
						parameters.put("additionalCashCgst",
								convertBigDecimalToString(additionalCashCgst));
						parameters.put("additionalCashSgst",
								convertBigDecimalToString(additionalCashSgst));
						parameters.put("additionalCashCess",
								convertBigDecimalToString(additionalCashCess));

					}

					if (computeDetailsEntity.getSection()
							.equalsIgnoreCase(NET_GST_LIABILITY)) {

						netGstLiabilityIgst = computeDetailsEntity.getIgst();
						netGstLiabilityCgst = computeDetailsEntity.getCgst();
						netGstLiabilitySgst = computeDetailsEntity.getSgst();
						netGstLiabilityCess = computeDetailsEntity.getCess();
					}

					utilizableCashIgst = calculateUtilizableCash(
							netGstLiabilityIgst, additionalCashIgst);
					utilizableCashCgst = calculateUtilizableCash(
							netGstLiabilityCgst, additionalCashCgst);
					utilizableCashSgst = calculateUtilizableCash(
							netGstLiabilitySgst, additionalCashSgst);
					utilizableCashCess = calculateUtilizableCash(
							netGstLiabilityCess, additionalCashCess);

					parameters.put("utilizableCashIgst",
							convertBigDecimalToString(utilizableCashIgst));
					parameters.put("utilizableCashCgst",
							convertBigDecimalToString(utilizableCashCgst));
					parameters.put("utilizableCashSgst",
							convertBigDecimalToString(utilizableCashSgst));
					parameters.put("utilizableCashCess",
							convertBigDecimalToString(utilizableCashCess));

				}

			}
			Gstr3bGstnSaveToAspEntity currentMonthItc = saveToAspRepository
					.findByGstinAndTaxPeriodAndSectionName(gstin, taxPeriod);
			ItcPaidInnerDto itcPaidDto = writeToItcPaidInnerDto(saveToAspEntityList, 
					currentMonthItc);
			if (!CollectionUtils.isEmpty(saveToAspEntityList)) {
				for (Gstr3bGstnSaveToAspEntity saveToAspEntity : saveToAspEntityList) {

					if(isDigigst){
						if (saveToAspEntity.getSectionName()
								.contentEquals(OTHER_REVERSE_TAX_PAYABLE)) {
							parameters.put("othrRevIgst", convertBigDecimalToString(
									itcPaidDto.getOthrcIgst()));
							parameters.put("othrRevCgst", convertBigDecimalToString(
									itcPaidDto.getOthrcCgst()));
							parameters.put("othrRevSgst", convertBigDecimalToString(
									itcPaidDto.getOthrcSgst()));
							parameters.put("othrRevCess", convertBigDecimalToString(
									itcPaidDto.getOthrcCess()));

						}
						
						//--3b pdf part 2
						BigDecimal othrRevIgst = itcPaidDto
								.getOthrcIgst() != null
										? itcPaidDto.getOthrcIgst()
										: BigDecimal.ZERO;
						BigDecimal othrRevCgst = itcPaidDto
								.getOthrcCgst() != null
										? itcPaidDto.getOthrcCgst()
										: BigDecimal.ZERO;
						BigDecimal othrRevSgst = itcPaidDto
								.getOthrcSgst() != null
										? itcPaidDto.getOthrcSgst()
										: BigDecimal.ZERO;
						BigDecimal othrRevCess = itcPaidDto
								.getOthrcCess() != null
										? itcPaidDto.getOthrcCess()
										: BigDecimal.ZERO;

						if (newSource) {
							BigDecimal anlOrcIgst = liabilitySetOffEntity
									.getI_adjNegative2i();
							BigDecimal anlOrcCgst = liabilitySetOffEntity
									.getC_adjNegative2i();
							BigDecimal anlOrcSgst = liabilitySetOffEntity
									.getS_adjNegative2i();
							BigDecimal anlOrcCess = liabilitySetOffEntity
									.getCs_adjNegative2i();

							BigDecimal netIgst = othrRevIgst
									.subtract(anlOrcIgst != null
											? anlOrcIgst
											: BigDecimal.ZERO);
							BigDecimal netCgst = othrRevCgst
									.subtract(anlOrcCgst != null
											? anlOrcCgst
											: BigDecimal.ZERO);
							BigDecimal netSgst = othrRevSgst
									.subtract(anlOrcSgst != null
											? anlOrcSgst
											: BigDecimal.ZERO);
							BigDecimal netCess = othrRevCess
									.subtract(anlOrcCess != null
											? anlOrcCess
											: BigDecimal.ZERO);

							parameters.put("ntpOtrcIgst",
									convertBigDecimalToString1(netIgst));
							parameters.put("ntpOtrcCgst",
									convertBigDecimalToString(netCgst));
							parameters.put("ntpOtrcSgst",
									convertBigDecimalToString(netSgst));
							parameters.put("ntpOtrcCess",
									convertBigDecimalToString(netCess));

						}
						
						if (saveToAspEntity.getSectionName()
								.contentEquals(REVERSE_TAX_PAYABLE)) {

							parameters.put("revPdCashIgst",
									convertBigDecimalToString(
											itcPaidDto.getRctpIgst()));
							parameters.put("revPdCashCgst",
									convertBigDecimalToString(
											itcPaidDto.getRctpCgst()));
							parameters.put("revPdCashSgst",
									convertBigDecimalToString(
											itcPaidDto.getRctpSgst()));
							parameters.put("revPdCashCess",
									convertBigDecimalToString(
											itcPaidDto.getRctpCess()));

						}
						
						// --3b pdf start 3
						// net tax payable --other than Rev Charge mapping
						BigDecimal revPdCashIgst = itcPaidDto
								.getRctpIgst() != null
										? itcPaidDto.getRctpIgst()
										: BigDecimal.ZERO;
						BigDecimal revPdCashCgst = itcPaidDto
								.getRctpCgst() != null
										? itcPaidDto.getRctpCgst()
										: BigDecimal.ZERO;
						BigDecimal revPdCashSgst = itcPaidDto
								.getRctpSgst() != null
										? itcPaidDto.getRctpSgst()
										: BigDecimal.ZERO;
						BigDecimal revPdCashCess = itcPaidDto
								.getRctpCess() != null
										? itcPaidDto.getRctpCess()
										: BigDecimal.ZERO;

						if (newSource) {
							BigDecimal anlRcIgst = liabilitySetOffEntity
									.getI_adjNegative8A();
							BigDecimal anlRcCgst = liabilitySetOffEntity
									.getC_adjNegative8A();
							BigDecimal anlRcSgst = liabilitySetOffEntity
									.getS_adjNegative8A();
							BigDecimal anlRcCess = liabilitySetOffEntity
									.getCs_adjNegative8A();

							// Perform subtraction: revPdCash - anlRc
							BigDecimal netRevPdIgst = revPdCashIgst
									.subtract(anlRcIgst != null
											? anlRcIgst
											: BigDecimal.ZERO);
							BigDecimal netRevPdCgst = revPdCashCgst
									.subtract(anlRcCgst != null
											? anlRcCgst
											: BigDecimal.ZERO);
							BigDecimal netRevPdSgst = revPdCashSgst
									.subtract(anlRcSgst != null
											? anlRcSgst
											: BigDecimal.ZERO);
							BigDecimal netRevPdCess = revPdCashCess
									.subtract(anlRcCess != null
											? anlRcCess
											: BigDecimal.ZERO);

							// net tax payable --other than Rev Charge mapping
							parameters.put("ntprcIgst",
									convertBigDecimalToString1(netRevPdIgst));
							parameters.put("ntprcCgst",
									convertBigDecimalToString1(netRevPdCgst));
							parameters.put("ntprcSgst",
									convertBigDecimalToString1(netRevPdSgst));
							parameters.put("ntprcCess",
									convertBigDecimalToString1(netRevPdCess));
							// --3b pdf end 3
						}
						
						if (saveToAspEntity.getSectionName()
								.contentEquals(INTEREST)) {

							parameters.put("interestPdCashIgst",
									convertBigDecimalToString(
											itcPaidDto.getIpIgst()));
							parameters.put("interestPdCashCgst",
									convertBigDecimalToString(
											itcPaidDto.getIpCgst()));
							parameters.put("interestPdCashSgst",
									convertBigDecimalToString(
											itcPaidDto.getIpSgst()));
							parameters.put("interestPdCashCess",
									convertBigDecimalToString(
											itcPaidDto.getIpCess()));

						}
						if (saveToAspEntity.getSectionName()
								.contentEquals(LATE_FEES)) {

							parameters.put("lateFeePdCashIgst",
									convertBigDecimalToString(
											itcPaidDto.getLateFeeIgst()));
							parameters.put("lateFeePdCashCgst",
									convertBigDecimalToString(
											itcPaidDto.getLateFeeCgst()));
							parameters.put("lateFeePdCashSgst",
									convertBigDecimalToString(
											itcPaidDto.getLateFeeSgst()));
							parameters.put("lateFeePdCashCess",
									convertBigDecimalToString(
											itcPaidDto.getLateFeeCess()));

						}
					} else {
						if (liabilitySetOffEntityGstin != null) {
							parameters.put("interestPdCashIgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getIntrstCashOthrsIgst())
							        		: null);
							parameters.put("interestPdCashCgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getIntrstCashOthrsCgst())
							        		: null);
							parameters.put("interestPdCashSgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getIntrstCashOthrsSgst())
							        		: null);
							parameters.put("interestPdCashCess",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getIntrstCashOthrsCess())
							        		: null);
						}
						if (liabilitySetOffEntityGstin != null) {
							parameters.put("lateFeePdCashIgst",null);
							parameters.put("lateFeePdCashCgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getLateFeeOthrsCgst())
							        		: null);
							parameters.put("lateFeePdCashSgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getLateFeeOthrsSgst())
							        		: null);
							parameters.put("lateFeePdCashCess",null);

						}
						if (liabilitySetOffEntityGstin != null) {

							parameters.put("revPdCashIgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getTaxCashRevIgst())
							        		: null);
							parameters.put("revPdCashCgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getTaxCashRevCgst())
							        		: null);
							parameters.put("revPdCashSgst",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getTaxCashRevSgst())
							        		: null);
							parameters.put("revPdCashCess",
									liabilitySetOffEntityGstin.isPresent()
							        ? convertBigDecimalToString(
											liabilitySetOffEntityGstin.get()
											.getTaxCashRevCess())
							        		: null);

						}
						if (liabilitySetOffEntityGstin != null) {

							parameters.put("othrRevIgst", convertBigDecimalToString(
									liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getTaxPayIgst() : BigDecimal.ZERO));
							parameters.put("othrRevCgst", convertBigDecimalToString(
									liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getTaxPayCgst() : BigDecimal.ZERO));
							parameters.put("othrRevSgst", convertBigDecimalToString(
									liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getTaxPaySgst() : BigDecimal.ZERO));
							parameters.put("othrRevCess", convertBigDecimalToString(
									liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getTaxPayCess() : BigDecimal.ZERO));

						}
						
						// other than than rev chanrge tax payable--values
						// extracting-- for gstn pdf

						//3b pdf part 4--
						// other than than rev chanrge tax payable--values
						// extracting-- for gstn pdf

						BigDecimal orcTaxPayIgst = BigDecimal.ZERO;
						BigDecimal orcTaxPayCgst = BigDecimal.ZERO;
						BigDecimal orcTaxPaySgst = BigDecimal.ZERO;
						BigDecimal orcTaxPayCess = BigDecimal.ZERO;

						if (liabilitySetOffEntityGstin != null
								&& liabilitySetOffEntityGstin.isPresent()) {
							orcTaxPayIgst = liabilitySetOffEntityGstin.get()
									.getTaxPayIgst();
							orcTaxPayCgst = liabilitySetOffEntityGstin.get()
									.getTaxPayCgst();
							orcTaxPaySgst = liabilitySetOffEntityGstin.get()
									.getTaxPaySgst();
							orcTaxPayCess = liabilitySetOffEntityGstin.get()
									.getTaxPayCess();
						}
						// rc tax payable tax payable--values extracting-- for
						// gstn pdf
						BigDecimal rcTaxPayIgst = BigDecimal.ZERO;
						BigDecimal rcTaxPayCgst = BigDecimal.ZERO;
						BigDecimal rcTaxPaySgst = BigDecimal.ZERO;
						BigDecimal rcTaxPayCess = BigDecimal.ZERO;

						if (liabilitySetOffEntityGstin != null) {

							rcTaxPayIgst = liabilitySetOffEntityGstin
									.isPresent()
											? liabilitySetOffEntityGstin.get()
													.getTaxPayIgst()
											: BigDecimal.ZERO;

							rcTaxPayCgst = liabilitySetOffEntityGstin
									.isPresent()
											? liabilitySetOffEntityGstin.get()
													.getTaxPayCgst()
											: BigDecimal.ZERO;

							rcTaxPaySgst = liabilitySetOffEntityGstin
									.isPresent()
											? liabilitySetOffEntityGstin.get()
													.getTaxPaySgst()
											: BigDecimal.ZERO;

							rcTaxPayCess = liabilitySetOffEntityGstin
									.isPresent()
											? liabilitySetOffEntityGstin.get()
													.getTaxPayCess()
											: BigDecimal.ZERO;

						}
						if (newSource) {
							// 3b pdf start 4-------gstn pdf---------gstn
							// level---------Adv of neg liability-------------
							BigDecimal aonlOrcIgstValues = BigDecimal.ZERO;
							BigDecimal aonlOrcSgstValues = BigDecimal.ZERO;
							BigDecimal aonlOrcCgstValues = BigDecimal.ZERO;
							BigDecimal aonlOrcCessValues = BigDecimal.ZERO;
							// reverse charge

							BigDecimal aonlRcIgstValues = BigDecimal.ZERO;
							BigDecimal aonlRcSgstValues = BigDecimal.ZERO;
							BigDecimal aonlRcCgstValues = BigDecimal.ZERO;
							BigDecimal aonlRcCessValues = BigDecimal.ZERO;
							if (!isDigigst) {

								APIResponse apiResponse = dashBoardService
										.getGSTR3BSummary(taxPeriod, gstin);

								if (LOGGER.isDebugEnabled()) {
									LOGGER.debug(
											" api response gstr3b summary - "
													+ apiResponse
															.getResponse());
								}
								String getGstnData = null;
								if (apiResponse.isSuccess()) {
									getGstnData = apiResponse.getResponse();

								}

								if (getGstnData != null) {
									// String getGstnData = "{ \"gstin\":
									// \"29HJKPS9689A8Z4\", \"ret_period\":
									// \"072016\", \"sup_details\": {
									// \"osup_det\": { \"txval\": 250, \"iamt\":
									// 100, \"camt\": 200, \"samt\": 300,
									// \"csamt\": 400 }, \"osup_zero\": {
									// \"txval\": 250, \"iamt\": 100, \"csamt\":
									// 400 }, \"osup_nil_exmp\": { \"txval\":
									// 250 }, \"isup_rev\": { \"txval\": 250,
									// \"iamt\": 100, \"camt\": 200, \"samt\":
									// 300, \"csamt\": 400 }, \"osup_nongst\": {
									// \"txval\": 250 } }, \"inter_sup\": {
									// \"unreg_details\": [ { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 }, {
									// \"pos\": \"07\", \"txval\": 250,
									// \"iamt\": 200 }, { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 } ],
									// \"comp_details\": [ { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 }, {
									// \"pos\": \"07\", \"txval\": 250,
									// \"iamt\": 200 }, { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 } ],
									// \"uin_details\": [ { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 }, {
									// \"pos\": \"07\", \"txval\": 250,
									// \"iamt\": 200 }, { \"pos\": \"07\",
									// \"txval\": 250, \"iamt\": 200 } ] },
									// \"eco_dtls\": { \"eco_sup\": { \"txval\":
									// 250, \"iamt\": 100, \"camt\": 200,
									// \"samt\": 300, \"csamt\": 400 },
									// \"eco_reg_sup\": { \"txval\": 250 } },
									// \"itc_elg\": { \"itc_avl\": [ { \"ty\":
									// \"IMPG\", \"iamt\": 136.53, \"camt\":
									// 274, \"samt\": 162.99, \"csamt\": 103 },
									// { \"ty\": \"IMPS\", \"iamt\": 136.53,
									// \"camt\": 274, \"samt\": 162.99,
									// \"csamt\": 103 }, { \"ty\": \"ISRC\",
									// \"iamt\": 136.53, \"camt\": 274,
									// \"samt\": 162.99, \"csamt\": 103 }, {
									// \"ty\": \"ISD\", \"iamt\": 136.53,
									// \"camt\": 274, \"samt\": 162.99,
									// \"csamt\": 103 }, { \"ty\": \"OTH\",
									// \"iamt\": 136.53, \"camt\": 274,
									// \"samt\": 162.99, \"csamt\": 103 } ],
									// \"itc_rev\": [ { \"ty\": \"RUL\",
									// \"iamt\": 136.53, \"camt\": 274,
									// \"samt\": 162.99, \"csamt\": 103 }, {
									// \"ty\": \"OTH\", \"iamt\": 136.53,
									// \"camt\": 274, \"samt\": 162.99,
									// \"csamt\": 103 } ], \"itc_net\": {
									// \"iamt\": 136.53, \"camt\": 274,
									// \"samt\": 162.99, \"csamt\": 103 },
									// \"itc_inelg\": [ { \"ty\": \"RUL\",
									// \"iamt\": 136.53, \"camt\": 274,
									// \"samt\": 162.99, \"csamt\": 103 }, {
									// \"ty\": \"OTH\", \"iamt\": 136.53,
									// \"camt\": 274, \"samt\": 162.99,
									// \"csamt\": 103 } ] }, \"inward_sup\": {
									// \"isup_details\": [ { \"ty\": \"GST\",
									// \"inter\": 100, \"intra\": 200 }, {
									// \"ty\": \"NONGST\", \"inter\": 300,
									// \"intra\": 400 } ] }, \"tx_pmt\": {
									// \"tx_py\": [ { \"trans_typ\": 30002,
									// \"tran_desc\": \"Other than Reverse
									// Charge\", \"liab_ldg_id\": 11191,
									// \"sgst\": { \"intr\": 100, \"tx\": 100,
									// \"fee\": 100 }, \"cgst\": { \"intr\":
									// 100, \"tx\": 100, \"fee\": 100 },
									// \"cess\": { \"intr\": 100, \"tx\": 100 },
									// \"igst\": { \"intr\": 100, \"tx\": 100 }
									// }, { \"trans_typ\": 30003, \"tran_desc\":
									// \"Reverse Charge\", \"liab_ldg_id\":
									// 11191, \"sgst\": { \"intr\": 100, \"tx\":
									// 100, \"fee\": 100 }, \"cgst\": {
									// \"intr\": 100, \"tx\": 100, \"fee\": 100
									// }, \"cess\": { \"intr\": 100, \"tx\": 100
									// }, \"igst\": { \"intr\": 100, \"tx\": 100
									// } } ], \"adjnegliab\": [ { \"trans_typ\":
									// 30002, \"tran_desc\": \"Other than
									// Reverse Charge\", \"liab_ldg_id\": 11191,
									// \"sgst\": { \"intr\": 100, \"tx\": 100,
									// \"fee\": 100 }, \"cgst\": { \"intr\":
									// 100, \"tx\": 100, \"fee\": 100 },
									// \"cess\": { \"intr\": 100, \"tx\": 100 },
									// \"igst\": { \"intr\": 100, \"tx\": 100 }
									// }, { \"trans_typ\": 30003, \"tran_desc\":
									// \"Reverse Charge\", \"liab_ldg_id\":
									// 11191, \"sgst\": { \"intr\": 100, \"tx\":
									// 100, \"fee\": 100 }, \"cgst\": {
									// \"intr\": 100, \"tx\": 100, \"fee\": 100
									// }, \"cess\": { \"intr\": 100, \"tx\": 100
									// }, \"igst\": { \"intr\": 100, \"tx\": 100
									// } } ], \"nettaxpay\": [ { \"trans_typ\":
									// 30002, \"tran_desc\": \"Other than
									// Reverse Charge\", \"liab_ldg_id\": 11191,
									// \"sgst\": { \"intr\": 100, \"tx\": 100,
									// \"fee\": 100 }, \"cgst\": { \"intr\":
									// 100, \"tx\": 100, \"fee\": 100 },
									// \"cess\": { \"intr\": 100, \"tx\": 100 },
									// \"igst\": { \"intr\": 100, \"tx\": 100 }
									// }, { \"trans_typ\": 30003, \"tran_desc\":
									// \"Reverse Charge\", \"liab_ldg_id\":
									// 11191, \"sgst\": { \"intr\": 100, \"tx\":
									// 100, \"fee\": 100 }, \"cgst\": {
									// \"intr\": 100, \"tx\": 100, \"fee\": 100
									// }, \"cess\": { \"intr\": 100, \"tx\": 100
									// }, \"igst\": { \"intr\": 100, \"tx\": 100
									// } } ], \"pdcash\": [ { \"liab_ldg_id\":
									// 1233, \"trans_typ\": 30002, \"ipd\":
									// 3517817, \"cpd\": 3517817, \"spd\":
									// 3517817, \"cspd\": 3517817, \"i_intrpd\":
									// 20, \"c_intrpd\": 30, \"s_intrpd\": 10,
									// \"cs_intrpd\": 15, \"c_lfeepd\": 13,
									// \"s_lfeepd\": 13 }, { \"liab_ldg_id\":
									// 1233, \"trans_typ\": 30003, \"ipd\":
									// 3517817, \"cpd\": 3517817, \"spd\":
									// 3517817, \"cspd\": 3517817, \"i_intrpd\":
									// 20, \"c_intrpd\": 30, \"s_intrpd\": 10,
									// \"cs_intrpd\": 15, \"c_lfeepd\": 13,
									// \"s_lfeepd\": 13 } ], \"pditc\": {
									// \"liab_ldg_id\": 12321, \"trans_typ\":
									// 30002, \"i_pdi\": 3517817, \"i_pdc\":
									// 2290459, \"i_pds\": 2290459, \"c_pdi\":
									// 3517817, \"c_pdc\": 2290459, \"s_pdi\":
									// 3517817, \"s_pds\": 2290459, \"cs_pdcs\":
									// 2290459 }, \"pdnls\": [ { \"trans_typ\":
									// 30002, \"tran_desc\": \"Other than
									// Reverse Charge\", \"liab_ldg_id\": 11191,
									// \"ipd\": 100, \"spd\": 101, \"cpd\": 102,
									// \"cspd\": 103 }, { \"trans_typ\": 30003,
									// \"tran_desc\": \"Reverse Charge\",
									// \"liab_ldg_id\": 11191, \"ipd\": 104,
									// \"spd\": 105, \"cpd\": 106, \"cspd\": 107
									// } ] }, \"intr_ltfee\": {
									// \"intr_details\": { \"iamt\": 10,
									// \"camt\": 20, \"samt\": 30, \"csamt\": 40
									// }, \"ltfee_details\": { \"camt\": 50,
									// \"samt\": 60 } }, \"liab_breakup\": [ {
									// \"ret_period\": \"032020\",
									// \"liability\": { \"iamt\": 1000,
									// \"camt\": 150, \"samt\": 200, \"csamt\":
									// 300 } }, { \"ret_period\": \"022020\",
									// \"liability\": { \"iamt\": 1000,
									// \"camt\": 150, \"samt\": 200, \"csamt\":
									// 300 } } ] }";

									JsonObject jsonObject = JsonParser
											.parseString(getGstnData)
											.getAsJsonObject();

									JsonObject txPmt = jsonObject
											.getAsJsonObject("tx_pmt");
									JsonArray pdnlsArray = txPmt
											.getAsJsonArray("pdnls");

									int otherThanReverseCharge = 30002;
									int reverseCharge = 30003;

									List<JsonObject> otherThanReverseChargePdnls = new ArrayList<>();
									List<JsonObject> reverseChargePdnls = new ArrayList<>();

									for (JsonElement element : pdnlsArray) {
										JsonObject pdnlsEntry = element
												.getAsJsonObject();
										int transType = pdnlsEntry
												.get("trans_typ").getAsInt();

										if (transType == otherThanReverseCharge) {
											otherThanReverseChargePdnls
													.add(pdnlsEntry);
										} else if (transType == reverseCharge) {
											reverseChargePdnls.add(pdnlsEntry);
										}
									}

									for (JsonObject pdnls : otherThanReverseChargePdnls) {
										aonlOrcIgstValues = aonlOrcIgstValues
												.add(pdnls.get("ipd")
														.getAsBigDecimal());
										aonlOrcSgstValues = aonlOrcSgstValues
												.add(pdnls.get("spd")
														.getAsBigDecimal());
										aonlOrcCgstValues = aonlOrcCgstValues
												.add(pdnls.get("cpd")
														.getAsBigDecimal());
										aonlOrcCessValues = aonlOrcCessValues
												.add(pdnls.get("cspd")
														.getAsBigDecimal());
									}

									parameters.put("aonlOrcIgst",
											convertBigDecimalToString1(
													aonlOrcIgstValues != null
															? aonlOrcIgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlOrcSgst",
											convertBigDecimalToString1(
													aonlOrcSgstValues != null
															? aonlOrcSgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlOrcCgst",
											convertBigDecimalToString1(
													aonlOrcCgstValues != null
															? aonlOrcCgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlOrcCess",
											convertBigDecimalToString1(
													aonlOrcCessValues != null
															? aonlOrcCessValues
															: BigDecimal.ZERO));

									for (JsonObject pdnls : reverseChargePdnls) {
										aonlRcIgstValues = aonlRcIgstValues
												.add(pdnls.get("ipd")
														.getAsBigDecimal());
										aonlRcSgstValues = aonlRcSgstValues
												.add(pdnls.get("spd")
														.getAsBigDecimal());
										aonlRcCgstValues = aonlRcCgstValues
												.add(pdnls.get("cpd")
														.getAsBigDecimal());
										aonlRcCessValues = aonlRcCessValues
												.add(pdnls.get("cspd")
														.getAsBigDecimal());
									}

									parameters.put("aonlRcIgst",
											convertBigDecimalToString1(
													aonlRcIgstValues != null
															? aonlRcIgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlRcSgst",
											convertBigDecimalToString1(
													aonlRcSgstValues != null
															? aonlRcSgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlRcCgst",
											convertBigDecimalToString1(
													aonlRcCgstValues != null
															? aonlRcCgstValues
															: BigDecimal.ZERO));

									parameters.put("aonlRcCess",
											convertBigDecimalToString1(
													aonlRcCessValues != null
															? aonlRcCessValues
															: BigDecimal.ZERO));

								}

								// net tax payable other than reverse charge =
								// tax
								// payable - adjust of negative liab for other
								// than
								// reverse charge
								BigDecimal netOrcIgst = orcTaxPayIgst
										.subtract(aonlOrcIgstValues != null
												? aonlOrcIgstValues
												: BigDecimal.ZERO);
								BigDecimal netOrcCgst = orcTaxPayCgst
										.subtract(aonlOrcCgstValues != null
												? aonlOrcCgstValues
												: BigDecimal.ZERO);
								BigDecimal netOrcSgst = orcTaxPaySgst
										.subtract(aonlOrcSgstValues != null
												? aonlOrcSgstValues
												: BigDecimal.ZERO);
								BigDecimal netOrcCess = orcTaxPayCess
										.subtract(aonlOrcCessValues != null
												? aonlOrcCessValues
												: BigDecimal.ZERO);

								parameters.put("ntpOtrcIgst",
										convertBigDecimalToString1(netOrcIgst));
								parameters.put("ntpOtrcCgst",
										convertBigDecimalToString1(netOrcCgst));
								parameters.put("ntpOtrcSgst",
										convertBigDecimalToString1(netOrcSgst));
								parameters.put("ntpOtrcCess",
										convertBigDecimalToString1(netOrcCess));

								// net tax payable for reverse charge--gstn pdf
								BigDecimal netRcIgst = rcTaxPayIgst
										.subtract(aonlRcIgstValues != null
												? aonlRcIgstValues
												: BigDecimal.ZERO);
								BigDecimal netRcCgst = rcTaxPayCgst
										.subtract(aonlRcCgstValues != null
												? aonlRcCgstValues
												: BigDecimal.ZERO);
								BigDecimal netRcSgst = rcTaxPaySgst
										.subtract(aonlRcSgstValues != null
												? aonlRcSgstValues
												: BigDecimal.ZERO);
								BigDecimal netRcCess = rcTaxPayCess
										.subtract(aonlRcCessValues != null
												? aonlRcCessValues
												: BigDecimal.ZERO);

								parameters.put("ntprcIgst",
										convertBigDecimalToString1(netRcIgst));
								parameters.put("ntprcCgst",
										convertBigDecimalToString1(netRcCgst));
								parameters.put("ntprcSgst",
										convertBigDecimalToString1(netRcSgst));
								parameters.put("ntprcCess",
										convertBigDecimalToString1(netRcCess));

								// -----------------//3b pdf end 4--gstn pdf
							}
						}
					}
					

					

				}
			}
			/*if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "filed.JPG";
			} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {
				image = "SUBMITTED.JPG";
			} else {
				image = "DRAFT-WM1.jpg";
			}*/
			
			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FILED-WM.jpg";
			} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {
				image = "SUBMITTED-WM.jpg";
			} else {
				image = "DRAFT-WM1.jpg";
			}

			File imgFile = ResourceUtils
					.getFile("classpath:jasperReports/" + image);
			byte[] blob = Files.readAllBytes(Paths.get(imgFile.getPath()));
			ByteArrayInputStream bis = new ByteArrayInputStream(blob);
			BufferedImage bImage2 = ImageIO.read(bis);
			parameters.put("bgStatusImage", bImage2);

			File file = ResourceUtils.getFile("classpath:" + source);

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());

		} catch (Exception ex) {
			String msg = "Exception occured while genearting 3B Summery pdf";
			LOGGER.error("Exception occured while genearting 3B Summery pdf..",
					ex);
			throw new AppException(msg);
		}

		return jasperPrint;
	}

	@Override
	public String generateBulkGstr3BSummaryPdfReport(JsonArray gstinArray,
			HttpServletResponse response, String taxPeriod, String isVerified) {

		Boolean isDigigst=true;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		File folderToZip = null;
		File zipPath = null;
		try {
			folderToZip = createTempDir();
			for (JsonElement gstin : gstinArray) {

				String timeMilli = dtf.format(
						EYDateUtil.toISTDateTimeFromUTC(LocalDateTime.now()));

				JasperPrint jprint = generateGstr3BSummaryPdfReport(
						gstin.getAsString(), taxPeriod, isDigigst, isVerified);

				if (jprint == null)
					continue;

				String pdfFileName = folderToZip.getAbsolutePath()
						+ File.separator + "GSTR3B_" + gstin.getAsString() + "_"
						+ taxPeriod + "_" + timeMilli + ".pdf";

				JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);

			}

			if (folderToZip.isDirectory() && folderToZip.list().length > 0) {
				zipPath = zipPdfFolder(folderToZip);
			} else {
				return null;
			}

			InputStream inputStream = new FileInputStream(zipPath);

			response.setContentType("application/zip");

			response.setHeader("Content-disposition",
					"attachment; filename=" + "GSTR3B_Summary" + ".zip");

			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception ex) {
			String msg = "Error while creating Gstr3B Summary bulk report";
			LOGGER.error(msg, ex);
			throw new AppException(msg);

		} finally {
			if (folderToZip != null && folderToZip.exists()) {
				try {
					FileUtils.deleteDirectory(folderToZip);
					if (zipPath != null && zipPath.exists()) {
						FileUtils.forceDelete(zipPath);
					}
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format(
								"Deleted the Temp directory/Folder '%s'",
								folderToZip.getAbsolutePath()));
					}
				} catch (Exception ex) {
					String msg = String.format(
							"Failed to remove the temp "
									+ "directory created for zip: '%s'. This will "
									+ "lead to clogging of disk space.",
							folderToZip.getAbsolutePath());
					LOGGER.error(msg, ex);
				}
			}

		}
		return "SUCCESS";
	}

	private File zipPdfFolder(File folderToZip) {
		String zipName = folderToZip + ".zip";
		try (ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(Paths.get(zipName).toFile()));) {

			Files.walkFileTree(Paths.get(folderToZip.getAbsolutePath()),
					new SimpleFileVisitor<Path>() {
						public FileVisitResult visitFile(Path file,
								BasicFileAttributes attrs) throws IOException {
							zos.putNextEntry(new ZipEntry(
									Paths.get(folderToZip.getAbsolutePath())
											.relativize(file).toString()));
							Files.copy(file, zos);
							zos.closeEntry();
							return FileVisitResult.CONTINUE;
						}
					});
		} catch (Exception ex) {
			String msg = "Error while creating Gstr3BZip";
			LOGGER.error(msg, ex);
			throw new AppException(msg);
		}
		return new File(zipName);
	}

	private String convertBigDecimalToString(BigDecimal amt) {

		if (amt != null) {
			return amt.toString();
		}
		return "-";
	}

	private BigDecimal returnZeroIfNull(BigDecimal amt) {
		if (amt == null) {
			return BigDecimal.ZERO;
		}
		return amt;
	}

	private BigDecimal calculateUtilizableCash(BigDecimal netGstLiability,
			BigDecimal additionalCash) {

		BigDecimal diffrence = returnZeroIfNull(netGstLiability)
				.subtract(returnZeroIfNull(additionalCash));
		diffrence = diffrence.setScale(2, RoundingMode.HALF_UP);

		return diffrence;
	}

	private String[] getMonthNameAndFinYear(String taxPeriod) {
		int month = Integer.parseInt(taxPeriod.substring(0, 2));
		int year = Integer.parseInt(taxPeriod.substring(2, 6));
		String monthString;
		String finYear;
		switch (month) {
		case 1:
			monthString = "January";
			break;
		case 2:
			monthString = "February";
			break;
		case 3:
			monthString = "March";
			break;
		case 4:
			monthString = "April";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "June";
			break;
		case 7:
			monthString = "July";
			break;
		case 8:
			monthString = "August";
			break;
		case 9:
			monthString = "September";
			break;
		case 10:
			monthString = "October";
			break;
		case 11:
			monthString = "November";
			break;
		case 12:
			monthString = "December";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		if (month < 4) {
			finYear = String.valueOf((year - 1)) + "-"
					+ String.valueOf(year).substring(2, 4);
		} else {
			finYear = String.valueOf(year) + "-"
					+ String.valueOf(year + 1).substring(2, 4);
		}
		return new String[] { monthString, finYear };
	}

	private String getStandardTime(LocalDateTime dateStr) {
		LocalDateTime ist = EYDateUtil.toISTDateTimeFromUTC(dateStr);
		String formattedDate = formatter1.format(ist).toString();
		return formattedDate;
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("GSTR3BSummeryPDF").toFile();
	}

	private Gstr3BSecDetailsDTO defaultToZero()
	{
		Gstr3BSecDetailsDTO gstr3BSecDetailsDTO = new Gstr3BSecDetailsDTO();
		gstr3BSecDetailsDTO.setCamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setCsamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setIamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setInter(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setIntra(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setSamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setTxval(BigDecimal.ZERO);
		return gstr3BSecDetailsDTO;
		
	}
	
	private List<Gstr3BSecDetailsDTO> defaultToZeroList()
	{
		List<Gstr3BSecDetailsDTO> gstr3BSecDetailsRetList= new ArrayList<>();
		Gstr3BSecDetailsDTO gstr3BSecDetailsDTO = new Gstr3BSecDetailsDTO();
		gstr3BSecDetailsDTO.setCamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setCsamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setIamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setInter(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setIntra(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setSamt(BigDecimal.ZERO);
		gstr3BSecDetailsDTO.setTxval(BigDecimal.ZERO);
		gstr3BSecDetailsRetList.add(gstr3BSecDetailsDTO);
		return gstr3BSecDetailsRetList;
		
	}
	
	private List<TaxPayableDetails> taxPayableDefaultToZeroList()
	{
		List<TaxPayableDetails> taxPayableDetailsList = new ArrayList<TaxPayableDetails>();
		TaxPayableDetails taxPayableDetails =new TaxPayableDetails();
		taxPayableDetails.setCess(amountTypeDetailZero());
		taxPayableDetails.setCgst(amountTypeDetailZero());
		taxPayableDetails.setIgst(amountTypeDetailZero());
		taxPayableDetails.setSgst(amountTypeDetailZero());
		taxPayableDetailsList.add(taxPayableDetails);
		return taxPayableDetailsList;
		
	}
	
	private AmountTypeDetail amountTypeDetailZero()
	{
		AmountTypeDetail amountTypeDetail = new AmountTypeDetail();
		amountTypeDetail.setTax(BigDecimal.ZERO);
		return amountTypeDetail;
		
	}
	
	private PaidItcDetails paidItcDetailsZero()
	{
		PaidItcDetails paidItcDetails = new PaidItcDetails();
		paidItcDetails.setCessPaidUsingCess(BigDecimal.ZERO);
		paidItcDetails.setCGSTPaidUsingCGST(BigDecimal.ZERO);
		paidItcDetails.setCGSTPaidUsingIGST(BigDecimal.ZERO);
		paidItcDetails.setIGSTPaidUsingCGST(BigDecimal.ZERO);
		paidItcDetails.setIGSTPaidUsingIGST(BigDecimal.ZERO);
		paidItcDetails.setIGSTPaidUsingSGST(BigDecimal.ZERO);
		paidItcDetails.setSGSTPaidUsingIGST(BigDecimal.ZERO);
		paidItcDetails.setSGSTPaidUsingSGST(BigDecimal.ZERO);
		return paidItcDetails;
		
	}
	
	private List<PaidCashDetails> paidCashDetails()
	{
		List<PaidCashDetails> paidCashDetailsList= new ArrayList<>();
		PaidCashDetails paidCashDetails = new PaidCashDetails();
		paidCashDetails.setCessIntPaid(BigDecimal.ZERO);
		paidCashDetails.setCessLateFeePaid(BigDecimal.ZERO);
		paidCashDetails.setCessPaid(BigDecimal.ZERO);
		paidCashDetails.setCgstIntPaid(BigDecimal.ZERO);
		paidCashDetails.setCgstLateFeePaid(BigDecimal.ZERO);
		paidCashDetails.setCgstPaid(BigDecimal.ZERO);
		paidCashDetails.setIgstIntPaid(BigDecimal.ZERO);
		paidCashDetails.setIgstLateFeePaid(BigDecimal.ZERO);
		paidCashDetails.setIgstPaid(BigDecimal.ZERO);
		paidCashDetails.setSgstIntPaid(BigDecimal.ZERO);
		paidCashDetails.setSgstLateFeePaid(BigDecimal.ZERO);
		paidCashDetails.setSgstPaid(BigDecimal.ZERO);
		paidCashDetailsList.add(paidCashDetails);
		return paidCashDetailsList;
		
	}
	
	private ItcPaidInnerDto writeToItcPaidInnerDto(
			List<Gstr3bGstnSaveToAspEntity> get3BData, Gstr3bGstnSaveToAspEntity itc4cData) {

		ItcPaidInnerDto innerDto = new ItcPaidInnerDto();
		
		BigDecimal zeroVal = BigDecimal.ZERO;
		
		BigDecimal igstB = BigDecimal.ZERO;
		BigDecimal cgstB = BigDecimal.ZERO;
		BigDecimal sgstB = BigDecimal.ZERO;
		BigDecimal cessB = BigDecimal.ZERO;

		BigDecimal rcigst = BigDecimal.ZERO;
		BigDecimal rccgst = BigDecimal.ZERO;
		BigDecimal rcsgst = BigDecimal.ZERO;
		BigDecimal rccess = BigDecimal.ZERO;
		
		//as per current month ITC -4C 
		
		BigDecimal itc4Cgst = itc4cData != null && itc4cData.getCgst() != null
				&& itc4cData.getCgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getCgst().abs()
						: zeroVal;
		BigDecimal itc4Sgst = itc4cData != null && itc4cData.getSgst() != null
				&& itc4cData.getSgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getSgst().abs()
						: zeroVal;
		BigDecimal itc4Igst = itc4cData != null && itc4cData.getIgst() != null
				&& itc4cData.getIgst().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getIgst().abs()
						: zeroVal;
		BigDecimal itc4Cess = itc4cData != null && itc4cData.getCess() != null
				&& itc4cData.getCess().compareTo(BigDecimal.ZERO) < 0
						? itc4cData.getCess().abs()
						: zeroVal;

		// 3.1(b)
		for (Gstr3bGstnSaveToAspEntity gstnData : get3BData) {

			if (gstnData != null
					&& gstnData.getSectionName().equalsIgnoreCase("3.1(b)")) {

				igstB = gstnData.getIgst() != null ? gstnData.getIgst()
						: BigDecimal.ZERO;
				cgstB = gstnData.getCgst() != null ? gstnData.getCgst()
						: BigDecimal.ZERO;
				sgstB = gstnData.getSgst() != null ? gstnData.getSgst()
						: BigDecimal.ZERO;
				cessB = gstnData.getCess() != null ? gstnData.getCess()
						: BigDecimal.ZERO;
			}

			if (gstnData != null && gstnData.getSectionName()
					.equalsIgnoreCase(Gstr3BConstants.Table3_1_1_A)) {

				rcigst = gstnData.getIgst() != null ? gstnData.getIgst()
						: BigDecimal.ZERO;
				rccgst = gstnData.getCgst() != null ? gstnData.getCgst()
						: BigDecimal.ZERO;
				rcsgst = gstnData.getSgst() != null ? gstnData.getSgst()
						: BigDecimal.ZERO;
				rccess = gstnData.getCess() != null ? gstnData.getCess()
						: BigDecimal.ZERO;
			}
		}

		try {
			for (Gstr3bGstnSaveToAspEntity gstnData : get3BData) {

				// 3.1(a) + 3.1(b)- column 2
				
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(a)")) {

					innerDto.setOthrcIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getIgst().add(igstB).add(itc4Igst))
							: BigDecimal.ZERO);
					innerDto.setOthrcCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCgst().add(cgstB).add(itc4Cgst))
							: BigDecimal.ZERO);
					innerDto.setOthrcSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getSgst().add(sgstB).add(itc4Sgst))
							: BigDecimal.ZERO);
					innerDto.setOthrcCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCess().add(cessB).add(itc4Cess))
							: BigDecimal.ZERO);
				}

				// 3.1(d) + 3.1.1(a) column 8-9
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("3.1(d)")) {
					innerDto.setRctpIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getIgst().add(rcigst))
							: BigDecimal.ZERO);
					innerDto.setRctpCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCgst().add(rccgst))
							: BigDecimal.ZERO);
					innerDto.setRctpSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getSgst().add(rcsgst))
							: BigDecimal.ZERO);
					innerDto.setRctpCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(
									gstnData.getCess().add(rccess))
							: BigDecimal.ZERO);
				}

				// column 10-11
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(a)")) {
					innerDto.setIpIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getIgst())
							: BigDecimal.ZERO);
					innerDto.setIpCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getCgst())
							: BigDecimal.ZERO);
					innerDto.setIpSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getSgst())
							: BigDecimal.ZERO);
					innerDto.setIpCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(gstnData.getCess())
							: BigDecimal.ZERO);
				}

				// column 12-13
				if (gstnData != null && gstnData.getSectionName()
						.equalsIgnoreCase("5.1(b)")) {
					innerDto.setLateFeeIgst(gstnData.getIgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getIgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeCgst(gstnData.getCgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getCgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeSgst(gstnData.getSgst() != null
							? GenUtil.roundOffTheAmount(gstnData.getSgst())
							: BigDecimal.ZERO);
					innerDto.setLateFeeCess(gstnData.getCess() != null
							? GenUtil.roundOffTheAmount(gstnData.getCess())
							: BigDecimal.ZERO);
				}

			}

		} catch (Exception ex) {
			LOGGER.error("Exception while populating the GSTN reponse to "
					+ " PaidThroughItcDto Dto", ex);
		}
		return innerDto;

	}
	
	public String getJasperReportSource(String taxPeriod) {
		if (taxPeriod != null && taxPeriod.length() == 6) {
			int month = Integer.parseInt(taxPeriod.substring(0, 2));
			int year = Integer.parseInt(taxPeriod.substring(2, 6));

			int cutoffMonth = 9; // April
			int cutoffYear = 2024; // Year 2024

			if (year > cutoffYear
					|| (year == cutoffYear && month >= cutoffMonth)) {
				return "jasperReports/GSTR3BSummeryTemplateNew.jrxml";
			} else {
				return "jasperReports/GSTR3BSummeryTemplateOld.jrxml";
			}
		} else {
			throw new IllegalArgumentException(
					"Invalid taxPeriod: " + taxPeriod);
		}
	}
	private String convertBigDecimalToString1(BigDecimal amt) {
		if (amt == null || BigDecimal.ZERO.compareTo(amt) == 0) {
			return "0.00";
		}
		return amt.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
}
