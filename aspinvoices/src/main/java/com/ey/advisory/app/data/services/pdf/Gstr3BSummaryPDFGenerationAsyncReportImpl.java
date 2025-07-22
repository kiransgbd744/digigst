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
import com.ey.advisory.app.gstr3b.Gstr3BSaveChangesLiabilitySetOffEntity;
import com.ey.advisory.app.gstr3b.Gstr3bGstnSaveToAspEntity;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.app.gstr3b.dto.AmountTypeDetail;
import com.ey.advisory.app.gstr3b.dto.PaidCashDetails;
import com.ey.advisory.app.gstr3b.dto.PaidItcDetails;
import com.ey.advisory.app.gstr3b.dto.TaxPayableDetails;
import com.ey.advisory.app.gstr3b.dto.TaxPaymentDetailsInvoice;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BRequestDtoConverterImpl;
import com.ey.advisory.app.services.savetogstn.jobs.gstr3B.Gstr3BSaveDataFetcher;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.ReturnFilingGstnResponseDto;
import com.ey.advisory.gstnapi.PublicApiConstants;
import com.ey.advisory.gstnapi.PublicApiContext;
import com.ey.advisory.services.gstr3b.entity.setoff.Gstr3BSetOffEntityComputeDetailsEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Slf4j
@Component("Gstr3BSummaryPDFGenerationAsyncReportImpl")
public class Gstr3BSummaryPDFGenerationAsyncReportImpl
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

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");

	@Override
	public JasperPrint generateGstr3BSummaryPdfReport(String gstin,
			String taxPeriod, Boolean isDigigst, String isVerified) {

		JasperPrint jasperPrint = null;
		//String source = "jasperReports/GSTR3BSummeryTemplate.jrxml";
		String source =   "jasperReports/GSTR3BSummeryTemplateNeww.jrxml";
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
					
						filingDate = gstrReturnStatusEntity.getFilingDate()
							.format(formatter2).toString() + " 00:00:00";

					filingDate = gstrReturnStatusEntity.getFilingDate().toString();

				} else if (filingStatus.equalsIgnoreCase("SUBMITTED")) {

					if (gstrReturnStatusEntitySubm != null) {
						filingDate = getStandardTime(
								gstrReturnStatusEntitySubm.getUpdatedOn());
					}
				}

			}

			
			//filingStatus="Filed";
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
			try {
				apiResp = taxPayerService
						.getTaxPayerDetails(gstin, groupCode);
			} catch (Exception e) {
				LOGGER.error(
						"Not able to taxpayer details from gstn");
			}
			
			if(apiResp != null){
				if(apiResp.getLegalBussNam() != null){
					parameters.put("legalBussName", apiResp.getLegalBussNam());
				}
				
				if(apiResp.getTradeName() != null){
					parameters.put("tradeName", apiResp.getTradeName());
				}
			}
			
			if(gstrReturnStatusEntity != null && gstrReturnStatusEntity.getArnNo() != null){
					parameters.put("arnNo", gstrReturnStatusEntity.getArnNo());
			}
			
			
			String verification = "";
			if(isVerified.equalsIgnoreCase("Yes")){
				verification = "I hereby solemnly affirm and declare that the "
						+ "information given herein above is true and correct to "
						+ "the best of my knowledge and belief and "
						+ "nothing has been concealed there from.";
			}
			parameters.put("verification", verification);
			
			List<DongleMappingEntity> dongleMapping = dongleMappingRepository
					.findByGstinAndIsActiveTrue(gstin);
			
			if(!dongleMapping.isEmpty() || dongleMapping != null){
				dongleMapping.forEach(dongle -> {
					if(dongle.getGstin().equalsIgnoreCase(gstin)){
						parameters.put("authorisedSignatory", dongle.getAuthorisedName());
						parameters.put("designation", dongle.getDesignation());
					}
				});
			}
			
			parameters.put("Digigst", isDigigst?"* All figures are as per DigiGST processed data":"* All figures are as per Latest Update GSTN Data");

			if (entityInfoEntity != null) {
				parameters.put("legalName", entityInfoEntity.getEntityName());
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

			if (!CollectionUtils.isEmpty(saveToAspEntityList)) {
				for (Gstr3bGstnSaveToAspEntity saveToAspEntity : saveToAspEntityList) {

					if(isDigigst){
						if (saveToAspEntity.getSectionName()
								.contentEquals(OTHER_REVERSE_TAX_PAYABLE)) {

							parameters.put("othrRevIgst", convertBigDecimalToString(
									saveToAspEntity.getIgst()));
							parameters.put("othrRevCgst", convertBigDecimalToString(
									saveToAspEntity.getCgst()));
							parameters.put("othrRevSgst", convertBigDecimalToString(
									saveToAspEntity.getSgst()));
							parameters.put("othrRevCess", convertBigDecimalToString(
									saveToAspEntity.getCess()));

						}
						if (saveToAspEntity.getSectionName()
								.contentEquals(REVERSE_TAX_PAYABLE)) {

							parameters.put("revPdCashIgst",
									convertBigDecimalToString(
											saveToAspEntity.getIgst()));
							parameters.put("revPdCashCgst",
									convertBigDecimalToString(
											saveToAspEntity.getCgst()));
							parameters.put("revPdCashSgst",
									convertBigDecimalToString(
											saveToAspEntity.getSgst()));
							parameters.put("revPdCashCess",
									convertBigDecimalToString(
											saveToAspEntity.getCess()));

						}
						if (saveToAspEntity.getSectionName()
								.contentEquals(INTEREST)) {

							parameters.put("interestPdCashIgst",
									convertBigDecimalToString(
											saveToAspEntity.getIgst()));
							parameters.put("interestPdCashCgst",
									convertBigDecimalToString(
											saveToAspEntity.getCgst()));
							parameters.put("interestPdCashSgst",
									convertBigDecimalToString(
											saveToAspEntity.getSgst()));
							parameters.put("interestPdCashCess",
									convertBigDecimalToString(
											saveToAspEntity.getCess()));

						}
						if (saveToAspEntity.getSectionName()
								.contentEquals(LATE_FEES)) {

							parameters.put("lateFeePdCashIgst",
									convertBigDecimalToString(
											saveToAspEntity.getIgst()));
							parameters.put("lateFeePdCashCgst",
									convertBigDecimalToString(
											saveToAspEntity.getCgst()));
							parameters.put("lateFeePdCashSgst",
									convertBigDecimalToString(
											saveToAspEntity.getSgst()));
							parameters.put("lateFeePdCashCess",
									convertBigDecimalToString(
											saveToAspEntity.getCess()));

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

							BigDecimal othrRevIgst = BigDecimal.ZERO;
							BigDecimal othrRevCgst = BigDecimal.ZERO;
							BigDecimal othrRevSgst = BigDecimal.ZERO;
							BigDecimal othrRevCess = BigDecimal.ZERO;

							parameters.put("othrRevIgst", convertBigDecimalToString(
									othrRevIgst
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getIgstVsIgstOthers() : BigDecimal.ZERO
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getIgstVsCgstOthers() : BigDecimal.ZERO
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getIgstVsSgstOthers() : BigDecimal.ZERO)))));
							parameters.put("othrRevCgst", convertBigDecimalToString(
									othrRevCgst
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getCgstVsIgstOthers() : BigDecimal.ZERO
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getCgstVsCgstOthers() : BigDecimal.ZERO))));
							parameters.put("othrRevSgst", convertBigDecimalToString(
									othrRevSgst
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getSgstVsIgstOthers() : BigDecimal.ZERO)
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getSgstVsSgstOthers() : BigDecimal.ZERO)));
							parameters.put("othrRevCess", convertBigDecimalToString(
									othrRevCess
									.add(liabilitySetOffEntityGstin.isPresent()
									        ? liabilitySetOffEntityGstin.get().getCessVsCessOthers() : BigDecimal.ZERO)));

						}
					}
					

					

				}
			}
			File file = ResourceUtils.getFile("classpath:" + source);
			
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"inside Gstr3BSummaryPDFGenerationAsyncReportImpl  calling file path ",
						file);
				LOGGER.debug(msg);
			}
			
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
			parameters.put("bgStatusImage", bImage2);

			

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

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"jasper report created successfully ");
			LOGGER.debug(msg);
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
		List<TaxPayableDetails> taxPayableDetailsList = new ArrayList();
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
	
	
}
