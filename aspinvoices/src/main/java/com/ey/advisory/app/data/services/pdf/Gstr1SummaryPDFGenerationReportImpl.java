/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.ey.advisory.admin.data.entities.client.GSTNDetailEntity;
import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.data.daos.client.simplified.UpdatedMofifiedDateFetchDaoImpl;
import com.ey.advisory.app.data.entities.client.DongleMappingEntity;
import com.ey.advisory.app.data.entities.client.GstnSubmitEntity;
import com.ey.advisory.app.data.entities.client.GstrReturnStatusEntity;
import com.ey.advisory.app.data.repositories.client.DongleMappingRepository;
import com.ey.advisory.app.data.repositories.client.GstnSubmitRepository;
import com.ey.advisory.app.data.repositories.client.GstrReturnStatusRepository;
import com.ey.advisory.app.data.services.anx1.Gstr1ProcessedRecordsFetchService;
import com.ey.advisory.app.docs.dto.Gstr1CompleteSummaryDto;
import com.ey.advisory.app.docs.dto.Gstr1PdfProcResultDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1FlagRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryPdfRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenDocRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenNilRespDto;
import com.ey.advisory.app.docs.dto.simplified.Gstr1SummaryScreenRespDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenAdvReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenDocReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenHSNReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenReqRespHandler;
import com.ey.advisory.app.services.search.docsummarysearch.Gstr1SummaryScreenSezReqRespHandler;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.common.service.GstnApi;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
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
 * @author Laxmi.Salukuti
 *
 */

@Slf4j
@Component("Gstr1SummaryPDFGenerationReportImpl")
public class Gstr1SummaryPDFGenerationReportImpl
		implements Gstr1SummaryPDFGenerationReport {

	@Autowired
	@Qualifier("UpdatedMofifiedDateFetchDaoImpl")
	UpdatedMofifiedDateFetchDaoImpl dateFetchDao;

	@Autowired
	@Qualifier("Gstr1SummaryScreenReqRespHandler")
	Gstr1SummaryScreenReqRespHandler gstr1ReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenAdvReqRespHandler")
	Gstr1SummaryScreenAdvReqRespHandler gstr1AdvReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenSezReqRespHandler")
	Gstr1SummaryScreenSezReqRespHandler gstr1SezReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenDocReqRespHandler")
	Gstr1SummaryScreenDocReqRespHandler gstr1DocReqRespHandler;

	@Autowired
	@Qualifier("Gstr1SummaryScreenHSNReqRespHandler")
	Gstr1SummaryScreenHSNReqRespHandler gstr1HsnReqRespHandler;

	@Autowired
	@Qualifier("Gstr1ProcessedRecordsFetchService")
	Gstr1ProcessedRecordsFetchService gstr1ProcessedRecordsFetchService;

	@Autowired
	@Qualifier("UpdatedMofifiedDateFetchDaoImpl")
	UpdatedMofifiedDateFetchDaoImpl dateFetchDao1;

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
	@Qualifier("GstnApi")
	private GstnApi gstnApi;
	
	@Autowired
	GSTNDetailRepository gSTNDetailRepository;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;
	
	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;
	
	@Autowired
	DongleMappingRepository dongleMappingRepository;

	private static final String MONTH = "(M)";
	private static final String BLANK = "-";

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");

	@Override
	public JasperPrint generatePdfGstr1Report(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult) {

		String taxPeriod = annexure1SummaryRequest.getTaxPeriod();
		boolean isGstr1a = annexure1SummaryRequest.getIsGstr1a();
		String fYear = GenUtil.getFinancialYearByTaxperiod(taxPeriod);
		String month = taxPeriod.substring(0, 2);
		boolean isDigigst = annexure1SummaryRequest.getIsDigigst();
		Boolean rateIncludedInHsn = gstnApi
				.isRateIncludedInHsn(annexure1SummaryRequest.getTaxPeriod());

		Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
				.getDataSecAttrs();

		String gstin = null;
		List<String> gstinList = null;
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstin = key;
					if (!dataSecAttrs.get(OnboardingConstant.GSTIN).isEmpty()
							&& dataSecAttrs.get(OnboardingConstant.GSTIN)
									.size() > 0) {
						gstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
					}
				}
			}
		}
		String sgstin = gstinList.get(0);

		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());
		String[] strArray = getMonthNameAndFinYear(taxPeriod);

		JasperPrint jasperPrint = null;
		String source = null;
		if(Boolean.TRUE.equals(isGstr1a)){
			source = "jasperReports/GSTR1ASummaryTemplat.jrxml";
		} else {
			source = "jasperReports/GSTR1SummaryTemplat.jrxml";
		}

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

		String gstr1Month = null;
		String monthDesc = monthMap.get(month);
		if (monthDesc != null) {
			gstr1Month = monthDesc.concat(MONTH);
		}

		/*
		 * String lastUpdatedDate = dateFetchDao
		 * .loadBasicSummarySection(annexure1SummaryRequest);
		 */

		List<Gstr1SummaryScreenRespDto> outwardSummaryResponse = gstr1ReqRespHandler
				.handleGstr1ReqAndResp(annexure1SummaryRequest, gstnResult);

		List<Gstr1SummaryScreenRespDto> hsnSummaryResponse = gstr1HsnReqRespHandler
				.handleGstr1HsnReqAndResp(annexure1SummaryRequest, gstnResult);

		List<Gstr1SummaryScreenRespDto> sezSummaryResponse = gstr1SezReqRespHandler
				.handleGstr1SezReqAndResp(annexure1SummaryRequest);

		List<Gstr1SummaryScreenRespDto> advSummaryResponse = gstr1AdvReqRespHandler
				.handleGstr1AdvReqAndResp(annexure1SummaryRequest, gstnResult);

		List<Gstr1SummaryScreenNilRespDto> nilSummaryResponse = gstr1DocReqRespHandler
				.handleGstr1NilReqAndResp(annexure1SummaryRequest, gstnResult);

		List<Gstr1SummaryScreenDocRespDto> docSummaryResponse = gstr1DocReqRespHandler
				.handleGstr1DocReqAndResp(annexure1SummaryRequest, gstnResult);

		Gstr1FlagRespDto loadHsnFlagection = dateFetchDao
				.loadHsnFlagection(annexure1SummaryRequest);

		boolean hsnUserInput = loadHsnFlagection.getIsHsnUserInput();
		boolean nilUserInput = loadHsnFlagection.getIsHsnUserInput();

		Boolean naConsideredAsUqcValueInHsn = gstnApi
				.isNAConsideredAsUqcValueInHsn(taxPeriod);

		GstrReturnStatusEntity gstrReturnStatusEntity = null;
		// For water mark
		if(Boolean.TRUE.equals(isGstr1a)){
			gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							sgstin, taxPeriod, APIConstants.GSTR1A_RETURN_TYPE);
		} else {
			gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
							sgstin, taxPeriod, APIConstants.GSTR1_RETURN_TYPE);
		}

		if (gstrReturnStatusEntity == null || !(gstrReturnStatusEntity
				.getStatus().equalsIgnoreCase("Filed"))) {

			List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

			try {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.GSTR1_PDF_RET);
				returnFilingGstnResponseDtoList = gstnReturnFilingStatus
						.callGstnApi(Arrays.asList(sgstin), strArray[1], false);

				for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

					if(Boolean.TRUE.equals(isGstr1a)){
						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.GSTR1A_RETURN_TYPE)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(taxPeriod)) {

							filingStatus = returnFilingGstnResponseDto.getStatus();
							String strDate = returnFilingGstnResponseDto
									.getFilingDate() + " 00:00:00";

							LocalDateTime dateTime = LocalDateTime.parse(strDate,
									formatter2);
							LocalDateTime istDate = EYDateUtil
									.toISTDateTimeFromUTC(dateTime);
							filingDate = istDate.toString();
						}
					} else {
						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.GSTR1_RETURN_TYPE)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(taxPeriod)) {

							filingStatus = returnFilingGstnResponseDto.getStatus();
							String strDate = returnFilingGstnResponseDto
									.getFilingDate() + " 00:00:00";

							LocalDateTime dateTime = LocalDateTime.parse(strDate,
									formatter2);
							LocalDateTime istDate = EYDateUtil
									.toISTDateTimeFromUTC(dateTime);
							filingDate = istDate.toString();
						}
					}
					
				}
			} catch (Exception e) {
				LOGGER.error(
						"Not able to generate Public Auth Token while Fetching Filling Status");
			}
		} else {
			filingStatus = gstrReturnStatusEntity.getStatus();
			if (filingStatus.equalsIgnoreCase("Filed")) {

				/*LocalDateTime dateTime = gstrReturnStatusEntity.getUpdatedOn();
				filingDate = getStandardTime(dateTime);*/
				
								filingDate =gstrReturnStatusEntity.getFilingDate().toString() ;
			}
		}

		if (!filingStatus.equalsIgnoreCase("Filed")) {
			Optional<GstnSubmitEntity> gstnSubmitStatus = null;
			if(Boolean.TRUE.equals(isGstr1a)){
				gstnSubmitStatus = gstnSubmitRepository
						.findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
								sgstin, taxPeriod, APIConstants.GSTR1A_RETURN_TYPE);
			} else {
				gstnSubmitStatus = gstnSubmitRepository
						.findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
								sgstin, taxPeriod, APIConstants.GSTR1_RETURN_TYPE);
			}
			if (gstnSubmitStatus.isPresent()) {
				String submitStatus = gstnSubmitStatus.get().getGstnStatus();
				if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
					filingStatus = "SUBMITTED";
					LocalDateTime dateTime = gstnSubmitStatus.get()
							.getCreatedOn();
					filingDate = getStandardTime(dateTime);

				}
			}
		}

		try {
			Map<String, Object> parameters = new HashMap<>();

			Map<String, List<Gstr1SummaryScreenRespDto>> outwardDocs = outwardSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> b2bStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2blist = outwardDocs.get("B2B");
			for (Gstr1SummaryScreenRespDto b2b : b2blist) {
				Gstr1SummaryPdfRespDto b2bObj = new Gstr1SummaryPdfRespDto();

				b2bObj.setAspCount(String.valueOf(
						isDigigst ? b2b.getAspCount() : b2b.getGstnCount()));
				b2bObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2b.getAspTaxableValue()
								: b2b.getGstnTaxableValue()));
				b2bObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2b.getAspInvoiceValue()
								: b2b.getGstnInvoiceValue()));
				b2bObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2b.getAspTaxPayble() : b2b.getGstnTaxPayble()));
				b2bObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2b.getAspIgst() : b2b.getGstnIgst()));
				b2bObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2b.getAspCgst() : b2b.getGstnCgst()));
				b2bObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2b.getAspSgst() : b2b.getGstnSgst()));
				b2bObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2b.getAspCess() : b2b.getGstnCess()));
				b2bStringList.add(b2bObj);
			}
			List<Gstr1SummaryPdfRespDto> b2baStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2balist = outwardDocs.get("B2BA");
			for (Gstr1SummaryScreenRespDto b2ba : b2balist) {
				Gstr1SummaryPdfRespDto b2baObj = new Gstr1SummaryPdfRespDto();
				b2baObj.setAspCount(String.valueOf(
						isDigigst ? b2ba.getAspCount() : b2ba.getGstnCount()));
				b2baObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2ba.getAspTaxableValue()
								: b2ba.getGstnTaxableValue()));
				b2baObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2ba.getAspInvoiceValue()
								: b2ba.getGstnInvoiceValue()));
				b2baObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2ba.getAspTaxPayble() : b2ba.getGstnTaxPayble()));
				b2baObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2ba.getAspIgst() : b2ba.getGstnIgst()));
				b2baObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2ba.getAspCgst() : b2ba.getGstnCgst()));
				b2baObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2ba.getAspSgst() : b2ba.getGstnSgst()));
				b2baObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2ba.getAspCess() : b2ba.getGstnCess()));
				b2baStringList.add(b2baObj);

			}
			List<Gstr1SummaryPdfRespDto> b2clStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2cllist = outwardDocs.get("B2CL");
			for (Gstr1SummaryScreenRespDto b2cl : b2cllist) {
				Gstr1SummaryPdfRespDto b2clObj = new Gstr1SummaryPdfRespDto();
				b2clObj.setAspCount(String.valueOf(
						isDigigst ? b2cl.getAspCount() : b2cl.getGstnCount()));
				b2clObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2cl.getAspTaxableValue()
								: b2cl.getGstnTaxableValue()));
				b2clObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2cl.getAspInvoiceValue()
								: b2cl.getGstnInvoiceValue()));
				b2clObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2cl.getAspTaxPayble() : b2cl.getGstnTaxPayble()));
				b2clObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2cl.getAspIgst() : b2cl.getGstnIgst()));
				b2clObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2cl.getAspCgst() : b2cl.getGstnCgst()));
				b2clObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2cl.getAspSgst() : b2cl.getGstnSgst()));
				b2clObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2cl.getAspCess() : b2cl.getGstnCess()));
				b2clStringList.add(b2clObj);

			}
			List<Gstr1SummaryPdfRespDto> b2claStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2clalist = outwardDocs
					.get("B2CLA");
			for (Gstr1SummaryScreenRespDto b2cla : b2clalist) {
				Gstr1SummaryPdfRespDto b2claObj = new Gstr1SummaryPdfRespDto();
				b2claObj.setAspCount(String.valueOf(isDigigst
						? b2cla.getAspCount() : b2cla.getGstnCount()));
				b2claObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2cla.getAspTaxableValue()
								: b2cla.getGstnTaxableValue()));
				b2claObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2cla.getAspInvoiceValue()
								: b2cla.getGstnInvoiceValue()));
				b2claObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2cla.getAspTaxPayble() : b2cla.getGstnTaxPayble()));
				b2claObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2cla.getAspIgst() : b2cla.getGstnIgst()));
				b2claObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2cla.getAspCgst() : b2cla.getGstnCgst()));
				b2claObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2cla.getAspSgst() : b2cla.getGstnSgst()));
				b2claObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2cla.getAspCess() : b2cla.getGstnCess()));
				b2claStringList.add(b2claObj);
			}
			List<Gstr1SummaryPdfRespDto> expStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> explist = outwardDocs
					.get("EXPORTS");
			for (Gstr1SummaryScreenRespDto exp : explist) {
				Gstr1SummaryPdfRespDto expObj = new Gstr1SummaryPdfRespDto();
				expObj.setAspCount(String.valueOf(
						isDigigst ? exp.getAspCount() : exp.getGstnCount()));
				expObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? exp.getAspTaxableValue()
								: exp.getGstnTaxableValue()));
				expObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? exp.getAspInvoiceValue()
								: exp.getGstnInvoiceValue()));
				expObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? exp.getAspTaxPayble() : exp.getGstnTaxPayble()));
				expObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? exp.getAspIgst() : exp.getGstnIgst()));
				expObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? exp.getAspCgst() : exp.getGstnCgst()));
				expObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? exp.getAspSgst() : exp.getGstnSgst()));
				expObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? exp.getAspCess() : exp.getGstnCess()));
				expStringList.add(expObj);
			}
			List<Gstr1SummaryPdfRespDto> expaStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> expalist = outwardDocs
					.get("EXPORTS-A");
			for (Gstr1SummaryScreenRespDto expa : expalist) {
				Gstr1SummaryPdfRespDto expaObj = new Gstr1SummaryPdfRespDto();
				expaObj.setAspCount(String.valueOf(
						isDigigst ? expa.getAspCount() : expa.getGstnCount()));
				expaObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? expa.getAspTaxableValue()
								: expa.getGstnTaxableValue()));
				expaObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? expa.getAspInvoiceValue()
								: expa.getGstnInvoiceValue()));
				expaObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? expa.getAspTaxPayble() : expa.getGstnTaxPayble()));
				expaObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? expa.getAspIgst() : expa.getGstnIgst()));
				expaObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? expa.getAspCgst() : expa.getGstnCgst()));
				expaObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? expa.getAspSgst() : expa.getGstnSgst()));
				expaObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? expa.getAspCess() : expa.getGstnCess()));
				expaStringList.add(expaObj);
			}
			List<Gstr1SummaryPdfRespDto> cdnrStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> cdnrlist = outwardDocs.get("CDNR");
			for (Gstr1SummaryScreenRespDto cdnr : cdnrlist) {
				Gstr1SummaryPdfRespDto cdnrObj = new Gstr1SummaryPdfRespDto();
				cdnrObj.setAspCount(String.valueOf(
						isDigigst ? cdnr.getAspCount() : cdnr.getGstnCount()));
				cdnrObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? cdnr.getAspTaxableValue()
								: cdnr.getGstnTaxableValue()));
				cdnrObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? cdnr.getAspInvoiceValue()
								: cdnr.getGstnInvoiceValue()));
				cdnrObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? cdnr.getAspTaxPayble() : cdnr.getGstnTaxPayble()));
				cdnrObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? cdnr.getAspIgst() : cdnr.getGstnIgst()));
				cdnrObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? cdnr.getAspCgst() : cdnr.getGstnCgst()));
				cdnrObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? cdnr.getAspSgst() : cdnr.getGstnSgst()));
				cdnrObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? cdnr.getAspCess() : cdnr.getGstnCess()));
				cdnrStringList.add(cdnrObj);
			}
			List<Gstr1SummaryPdfRespDto> cdnraStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> cdnralist = outwardDocs
					.get("CDNRA");
			for (Gstr1SummaryScreenRespDto cdnra : cdnralist) {
				Gstr1SummaryPdfRespDto cdnraObj = new Gstr1SummaryPdfRespDto();
				cdnraObj.setAspCount(String.valueOf(isDigigst
						? cdnra.getAspCount() : cdnra.getGstnCount()));
				cdnraObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? cdnra.getAspTaxableValue()
								: cdnra.getGstnTaxableValue()));
				cdnraObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? cdnra.getAspInvoiceValue()
								: cdnra.getGstnInvoiceValue()));
				cdnraObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? cdnra.getAspTaxPayble() : cdnra.getGstnTaxPayble()));
				cdnraObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? cdnra.getAspIgst() : cdnra.getGstnIgst()));
				cdnraObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? cdnra.getAspCgst() : cdnra.getGstnCgst()));
				cdnraObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? cdnra.getAspSgst() : cdnra.getGstnSgst()));
				cdnraObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? cdnra.getAspCess() : cdnra.getGstnCess()));
				cdnraStringList.add(cdnraObj);
			}
			List<Gstr1SummaryPdfRespDto> cdnurStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> cdnurlist = outwardDocs
					.get("CDNUR");
			for (Gstr1SummaryScreenRespDto cdnur : cdnurlist) {
				Gstr1SummaryPdfRespDto cdnurObj = new Gstr1SummaryPdfRespDto();
				cdnurObj.setAspCount(String.valueOf(isDigigst
						? cdnur.getAspCount() : cdnur.getGstnCount()));
				cdnurObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? cdnur.getAspTaxableValue()
								: cdnur.getGstnTaxableValue()));
				cdnurObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? cdnur.getAspInvoiceValue()
								: cdnur.getGstnInvoiceValue()));
				cdnurObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? cdnur.getAspTaxPayble() : cdnur.getGstnTaxPayble()));
				cdnurObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? cdnur.getAspIgst() : cdnur.getGstnIgst()));
				cdnurObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? cdnur.getAspCgst() : cdnur.getGstnCgst()));
				cdnurObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? cdnur.getAspSgst() : cdnur.getGstnSgst()));
				cdnurObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? cdnur.getAspCess() : cdnur.getGstnCess()));
				cdnurStringList.add(cdnurObj);
			}
			List<Gstr1SummaryPdfRespDto> cdnuraStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> cdnuralist = outwardDocs
					.get("CDNURA");
			for (Gstr1SummaryScreenRespDto cdnura : cdnuralist) {
				Gstr1SummaryPdfRespDto cdnuraObj = new Gstr1SummaryPdfRespDto();
				cdnuraObj.setAspCount(String.valueOf(isDigigst
						? cdnura.getAspCount() : cdnura.getGstnCount()));
				cdnuraObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? cdnura.getAspTaxableValue()
								: cdnura.getGstnTaxableValue()));
				cdnuraObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? cdnura.getAspInvoiceValue()
								: cdnura.getGstnInvoiceValue()));
				cdnuraObj.setAspTaxPayble(GenUtil
						.formatCurrency(isDigigst ? cdnura.getAspTaxPayble()
								: cdnura.getGstnTaxPayble()));
				cdnuraObj.setAspIgst(GenUtil.formatCurrency(isDigigst
						? cdnura.getAspIgst() : cdnura.getGstnIgst()));
				cdnuraObj.setAspCgst(GenUtil.formatCurrency(isDigigst
						? cdnura.getAspCgst() : cdnura.getGstnCgst()));
				cdnuraObj.setAspSgst(GenUtil.formatCurrency(isDigigst
						? cdnura.getAspSgst() : cdnura.getGstnSgst()));
				cdnuraObj.setAspCess(GenUtil.formatCurrency(isDigigst
						? cdnura.getAspCess() : cdnura.getGstnCess()));
				cdnuraStringList.add(cdnuraObj);
			}
			List<Gstr1SummaryPdfRespDto> b2csStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2cslist = outwardDocs.get("B2CS");
			for (Gstr1SummaryScreenRespDto b2cs : b2cslist) {
				Gstr1SummaryPdfRespDto b2csObj = new Gstr1SummaryPdfRespDto();
				b2csObj.setAspCount(String.valueOf(
						isDigigst ? b2cs.getAspCount() : b2cs.getGstnCount()));
				b2csObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2cs.getAspTaxableValue()
								: b2cs.getGstnTaxableValue()));
				b2csObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2cs.getAspInvoiceValue()
								: b2cs.getGstnInvoiceValue()));
				b2csObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2cs.getAspTaxPayble() : b2cs.getGstnTaxPayble()));
				b2csObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2cs.getAspIgst() : b2cs.getGstnIgst()));
				b2csObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2cs.getAspCgst() : b2cs.getGstnCgst()));
				b2csObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2cs.getAspSgst() : b2cs.getGstnSgst()));
				b2csObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2cs.getAspCess() : b2cs.getGstnCess()));
				b2csStringList.add(b2csObj);
			}
			List<Gstr1SummaryPdfRespDto> b2csaStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> b2csalist = outwardDocs
					.get("B2CSA");
			for (Gstr1SummaryScreenRespDto b2csa : b2csalist) {
				Gstr1SummaryPdfRespDto b2csaObj = new Gstr1SummaryPdfRespDto();
				b2csaObj.setAspCount(String.valueOf(isDigigst
						? b2csa.getAspCount() : b2csa.getGstnCount()));
				b2csaObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? b2csa.getAspTaxableValue()
								: b2csa.getGstnTaxableValue()));
				b2csaObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? b2csa.getAspInvoiceValue()
								: b2csa.getGstnInvoiceValue()));
				b2csaObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? b2csa.getAspTaxPayble() : b2csa.getGstnTaxPayble()));
				b2csaObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? b2csa.getAspIgst() : b2csa.getGstnIgst()));
				b2csaObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? b2csa.getAspCgst() : b2csa.getGstnCgst()));
				b2csaObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? b2csa.getAspSgst() : b2csa.getGstnSgst()));
				b2csaObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? b2csa.getAspCess() : b2csa.getGstnCess()));
				b2csaStringList.add(b2csaObj);
			}

			Map<String, List<Gstr1SummaryScreenRespDto>> sezResp = sezSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> sezwpStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> sezwplist = sezResp.get("SEZWP");
			for (Gstr1SummaryScreenRespDto sezwp : sezwplist) {
				Gstr1SummaryPdfRespDto sezwpObj = new Gstr1SummaryPdfRespDto();
				sezwpObj.setAspCount(String.valueOf(isDigigst
						? sezwp.getAspCount() : sezwp.getGstnCount()));
				sezwpObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? sezwp.getAspTaxableValue()
								: sezwp.getGstnTaxableValue()));
				sezwpObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? sezwp.getAspInvoiceValue()
								: sezwp.getGstnInvoiceValue()));
				sezwpObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? sezwp.getAspTaxPayble() : sezwp.getGstnTaxPayble()));
				sezwpObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? sezwp.getAspIgst() : sezwp.getGstnIgst()));
				sezwpObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? sezwp.getAspCgst() : sezwp.getGstnCgst()));
				sezwpObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? sezwp.getAspSgst() : sezwp.getGstnSgst()));
				sezwpObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? sezwp.getAspCess() : sezwp.getGstnCess()));
				sezwpStringList.add(sezwpObj);
			}
			List<Gstr1SummaryPdfRespDto> sezwopStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> sezwoplist = sezResp.get("SEZWOP");
			for (Gstr1SummaryScreenRespDto sezwop : sezwoplist) {
				Gstr1SummaryPdfRespDto sezwopObj = new Gstr1SummaryPdfRespDto();
				sezwopObj.setAspCount(String.valueOf(isDigigst
						? sezwop.getAspCount() : sezwop.getGstnCount()));
				sezwopObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? sezwop.getAspTaxableValue()
								: sezwop.getGstnTaxableValue()));
				sezwopObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? sezwop.getAspInvoiceValue()
								: sezwop.getGstnInvoiceValue()));
				sezwopObj.setAspTaxPayble(GenUtil
						.formatCurrency(isDigigst ? sezwop.getAspTaxPayble()
								: sezwop.getGstnTaxPayble()));
				sezwopObj.setAspIgst(GenUtil.formatCurrency(isDigigst
						? sezwop.getAspIgst() : sezwop.getGstnIgst()));
				sezwopObj.setAspCgst(GenUtil.formatCurrency(isDigigst
						? sezwop.getAspCgst() : sezwop.getGstnCgst()));
				sezwopObj.setAspSgst(GenUtil.formatCurrency(isDigigst
						? sezwop.getAspSgst() : sezwop.getGstnSgst()));
				sezwopObj.setAspCess(GenUtil.formatCurrency(isDigigst
						? sezwop.getAspCess() : sezwop.getGstnCess()));
				sezwopStringList.add(sezwopObj);
			}

			Map<String, List<Gstr1SummaryScreenRespDto>> hsnResp = hsnSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> hsnAspStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> hsnAspList = hsnResp.get("HSN_ASP");
			for (Gstr1SummaryScreenRespDto hsnAsp : hsnAspList) {
				Gstr1SummaryPdfRespDto hsnAspObj = new Gstr1SummaryPdfRespDto();

				hsnAspObj.setAspCount(String.valueOf(isDigigst
						? hsnAsp.getAspCount() : hsnAsp.getGstnCount()));
				hsnAspObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? hsnAsp.getAspTaxableValue()
								: hsnAsp.getGstnTaxableValue()));

				if (!rateIncludedInHsn) {
					hsnAspObj.setAspInvoiceValue(GenUtil.formatCurrency(
							isDigigst ? hsnAsp.getAspInvoiceValue()
									: hsnAsp.getGstnInvoiceValue()));
				} else {
					hsnAspObj.setAspInvoiceValue(BLANK);
				}
				hsnAspObj.setAspTaxPayble(GenUtil
						.formatCurrency(isDigigst ? hsnAsp.getAspTaxPayble()
								: hsnAsp.getGstnTaxPayble()));
				hsnAspObj.setAspIgst(GenUtil.formatCurrency(isDigigst
						? hsnAsp.getAspIgst() : hsnAsp.getGstnIgst()));
				hsnAspObj.setAspCgst(GenUtil.formatCurrency(isDigigst
						? hsnAsp.getAspCgst() : hsnAsp.getGstnCgst()));
				hsnAspObj.setAspSgst(GenUtil.formatCurrency(isDigigst
						? hsnAsp.getAspSgst() : hsnAsp.getGstnSgst()));
				hsnAspObj.setAspCess(GenUtil.formatCurrency(isDigigst
						? hsnAsp.getAspCess() : hsnAsp.getGstnCess()));
				hsnAspStringList.add(hsnAspObj);
			}

			List<Gstr1SummaryPdfRespDto> hsnUIStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> hsnUIList = hsnResp.get("HSN_UI");
			for (Gstr1SummaryScreenRespDto hsnUI : hsnUIList) {
				Gstr1SummaryPdfRespDto hsnUIObj = new Gstr1SummaryPdfRespDto();
				hsnUIObj.setAspCount(String.valueOf(isDigigst
						? hsnUI.getAspCount() : hsnUI.getGstnCount()));
				hsnUIObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? hsnUI.getAspTaxableValue()
								: hsnUI.getGstnTaxableValue()));
				if (!rateIncludedInHsn) {
					hsnUIObj.setAspInvoiceValue(GenUtil.formatCurrency(
							isDigigst ? hsnUI.getAspInvoiceValue()
									: hsnUI.getGstnInvoiceValue()));
				} else {
					hsnUIObj.setAspInvoiceValue(BLANK);
				}
				hsnUIObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? hsnUI.getAspTaxPayble() : hsnUI.getGstnTaxPayble()));
				hsnUIObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? hsnUI.getAspIgst() : hsnUI.getGstnIgst()));
				hsnUIObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? hsnUI.getAspCgst() : hsnUI.getGstnCgst()));
				hsnUIObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? hsnUI.getAspSgst() : hsnUI.getGstnSgst()));
				hsnUIObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? hsnUI.getAspCess() : hsnUI.getGstnCess()));
				hsnUIStringList.add(hsnUIObj);
			}
			Map<String, List<Gstr1SummaryScreenRespDto>> advResp = advSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> atStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> atlist = advResp.get("ADV REC");
			for (Gstr1SummaryScreenRespDto at : atlist) {
				Gstr1SummaryPdfRespDto atObj = new Gstr1SummaryPdfRespDto();
				atObj.setAspCount(String.valueOf(
						isDigigst ? at.getAspCount() : at.getGstnCount()));
				atObj.setAspTaxableValue(GenUtil.formatCurrency(isDigigst
						? at.getAspTaxableValue() : at.getGstnTaxableValue()));
				atObj.setAspInvoiceValue(GenUtil.formatCurrency(isDigigst
						? at.getAspInvoiceValue() : at.getGstnInvoiceValue()));
				atObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? at.getAspTaxPayble() : at.getGstnTaxPayble()));
				atObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? at.getAspIgst() : at.getGstnIgst()));
				atObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? at.getAspCgst() : at.getGstnCgst()));
				atObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? at.getAspSgst() : at.getGstnSgst()));
				atObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? at.getAspCess() : at.getGstnCess()));
				atStringList.add(atObj);
			}
			List<Gstr1SummaryPdfRespDto> ataStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> atalist = advResp.get("ADV REC-A");
			for (Gstr1SummaryScreenRespDto ata : atalist) {
				Gstr1SummaryPdfRespDto ataObj = new Gstr1SummaryPdfRespDto();
				ataObj.setAspCount(String.valueOf(
						isDigigst ? ata.getAspCount() : ata.getGstnCount()));
				ataObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? ata.getAspTaxableValue()
								: ata.getGstnTaxableValue()));
				ataObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? ata.getAspInvoiceValue()
								: ata.getGstnInvoiceValue()));
				ataObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? ata.getAspTaxPayble() : ata.getGstnTaxPayble()));
				ataObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? ata.getAspIgst() : ata.getGstnIgst()));
				ataObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? ata.getAspCgst() : ata.getGstnCgst()));
				ataObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? ata.getAspSgst() : ata.getGstnSgst()));
				ataObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? ata.getAspCess() : ata.getGstnCess()));
				ataStringList.add(ataObj);
			}
			List<Gstr1SummaryPdfRespDto> txpStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> txplist = advResp.get("ADV ADJ");
			for (Gstr1SummaryScreenRespDto txp : txplist) {
				Gstr1SummaryPdfRespDto txpObj = new Gstr1SummaryPdfRespDto();
				txpObj.setAspCount(String.valueOf(
						isDigigst ? txp.getAspCount() : txp.getGstnCount()));
				txpObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? txp.getAspTaxableValue()
								: txp.getGstnTaxableValue()));
				txpObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? txp.getAspInvoiceValue()
								: txp.getGstnInvoiceValue()));
				txpObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? txp.getAspTaxPayble() : txp.getGstnTaxPayble()));
				txpObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? txp.getAspIgst() : txp.getGstnIgst()));
				txpObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? txp.getAspCgst() : txp.getGstnCgst()));
				txpObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? txp.getAspSgst() : txp.getGstnSgst()));
				txpObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? txp.getAspCess() : txp.getGstnCess()));
				txpStringList.add(txpObj);
			}
			List<Gstr1SummaryPdfRespDto> txpaStringList = new ArrayList<>();
			List<Gstr1SummaryScreenRespDto> txpalist = advResp.get("ADV ADJ-A");
			for (Gstr1SummaryScreenRespDto txpa : txpalist) {
				Gstr1SummaryPdfRespDto txpaObj = new Gstr1SummaryPdfRespDto();
				txpaObj.setAspCount(String.valueOf(
						isDigigst ? txpa.getAspCount() : txpa.getGstnCount()));
				txpaObj.setAspTaxableValue(GenUtil
						.formatCurrency(isDigigst ? txpa.getAspTaxableValue()
								: txpa.getGstnTaxableValue()));
				txpaObj.setAspInvoiceValue(GenUtil
						.formatCurrency(isDigigst ? txpa.getAspInvoiceValue()
								: txpa.getGstnInvoiceValue()));
				txpaObj.setAspTaxPayble(GenUtil.formatCurrency(isDigigst
						? txpa.getAspTaxPayble() : txpa.getGstnTaxPayble()));
				txpaObj.setAspIgst(GenUtil.formatCurrency(
						isDigigst ? txpa.getAspIgst() : txpa.getGstnIgst()));
				txpaObj.setAspCgst(GenUtil.formatCurrency(
						isDigigst ? txpa.getAspCgst() : txpa.getGstnCgst()));
				txpaObj.setAspSgst(GenUtil.formatCurrency(
						isDigigst ? txpa.getAspSgst() : txpa.getGstnSgst()));
				txpaObj.setAspCess(GenUtil.formatCurrency(
						isDigigst ? txpa.getAspCess() : txpa.getGstnCess()));
				txpaStringList.add(txpaObj);
			}

			Map<Object, List<Gstr1SummaryScreenDocRespDto>> docIssuedResp = docSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> docIssuedStringList = new ArrayList<>();
			List<Gstr1SummaryScreenDocRespDto> docIssuedlist = docIssuedResp
					.get("DOC ISSUED");
			for (Gstr1SummaryScreenDocRespDto docIssued : docIssuedlist) {
				Gstr1SummaryPdfRespDto docIssuedObj = new Gstr1SummaryPdfRespDto();
				docIssuedObj.setTotal(String.valueOf(docIssued.getTotal()));
				docIssuedObj.setAspTotal(String.valueOf(isDigigst
						? docIssued.getAspTotal() : docIssued.getGstnTotal()));
				docIssuedObj.setAspNetIssued(
						String.valueOf(isDigigst ? docIssued.getAspNetIssued()
								: docIssued.getGstnNetIssued()));
				docIssuedObj.setAspCancelled(
						String.valueOf(isDigigst ? docIssued.getAspCancelled()
								: docIssued.getGstnCancelled()));
				docIssuedStringList.add(docIssuedObj);
			}

			Map<Object, List<Gstr1SummaryScreenNilRespDto>> nilResp = nilSummaryResponse
					.stream()
					.collect(Collectors.groupingBy(doc -> doc.getTaxDocType()));

			List<Gstr1SummaryPdfRespDto> nilAspStringList = new ArrayList<>();
			List<Gstr1SummaryScreenNilRespDto> nilAspList = nilResp
					.get("ASP_NILEXTNON");
			for (Gstr1SummaryScreenNilRespDto nilAsp : nilAspList) {
				Gstr1SummaryPdfRespDto nilAspObj = new Gstr1SummaryPdfRespDto();
				nilAspObj.setTotal(String.valueOf(nilAsp.getTotal()));

				BigDecimal nilRated = nilAsp.getAspNitRated() != null
						? nilAsp.getAspNitRated() : BigDecimal.ZERO;

				BigDecimal gstnNilRated = nilAsp.getGstnNitRated() != null
						? nilAsp.getGstnNitRated() : BigDecimal.ZERO;

				nilAspObj.setAspNitRated(GenUtil
						.formatCurrency(isDigigst ? nilRated : gstnNilRated));

				BigDecimal aspExmptd = nilAsp.getAspExempted() != null
						? nilAsp.getAspExempted() : BigDecimal.ZERO;

				BigDecimal gstnExmptd = nilAsp.getGstnExempted() != null
						? nilAsp.getGstnExempted() : BigDecimal.ZERO;

				nilAspObj.setAspExempted(GenUtil
						.formatCurrency(isDigigst ? aspExmptd : gstnExmptd));

				BigDecimal aspNonGst = nilAsp.getAspNonGst() != null
						? nilAsp.getAspNonGst() : BigDecimal.ZERO;

				BigDecimal gstnNonGst = nilAsp.getGstnNonGst() != null
						? nilAsp.getGstnNonGst() : BigDecimal.ZERO;

				nilAspObj.setAspNonGst(GenUtil
						.formatCurrency(isDigigst ? aspNonGst : gstnNonGst));
				nilAspStringList.add(nilAspObj);
			}

			List<Gstr1SummaryPdfRespDto> nilUIStringList = new ArrayList<>();
			List<Gstr1SummaryScreenNilRespDto> nilUIList = nilResp
					.get("UI_NILEXTNON");
			for (Gstr1SummaryScreenNilRespDto nilUI : nilUIList) {
				Gstr1SummaryPdfRespDto nilUIObj = new Gstr1SummaryPdfRespDto();
				nilUIObj.setTotal(String.valueOf(nilUI.getTotal()));

				BigDecimal nilRated = nilUI.getAspNitRated() != null
						? nilUI.getAspNitRated() : BigDecimal.ZERO;
				BigDecimal gstnNilRated = nilUI.getGstnNitRated() != null
						? nilUI.getGstnNitRated() : BigDecimal.ZERO;

				nilUIObj.setAspNitRated(GenUtil
						.formatCurrency(isDigigst ? nilRated : gstnNilRated));

				BigDecimal aspExmptd = nilUI.getAspExempted() != null
						? nilUI.getAspExempted() : BigDecimal.ZERO;

				BigDecimal gstnExmptd = nilUI.getGstnExempted() != null
						? nilUI.getGstnExempted() : BigDecimal.ZERO;

				nilUIObj.setAspExempted(GenUtil
						.formatCurrency(isDigigst ? aspExmptd : gstnExmptd));

				BigDecimal aspNonGst = nilUI.getAspNonGst() != null
						? nilUI.getAspNonGst() : BigDecimal.ZERO;

				BigDecimal gstnNonGst = nilUI.getGstnNonGst() != null
						? nilUI.getGstnNonGst() : BigDecimal.ZERO;

				nilUIObj.setAspNonGst(GenUtil
						.formatCurrency(isDigigst ? aspNonGst : gstnNonGst));
				nilUIStringList.add(nilUIObj);
			}
			String regName = regRepo.findRegTypeByGstinForPdf(sgstin);

			String RegisterName = "";
			if (regName != null) {
				RegisterName = regName;
			}

			parameters.put("B2B",
					new JRBeanCollectionDataSource(b2bStringList));
			parameters.put("B2BA",
					new JRBeanCollectionDataSource(b2baStringList));
			parameters.put("B2CL",
					new JRBeanCollectionDataSource(b2clStringList));
			parameters.put("B2CLA",
					new JRBeanCollectionDataSource(b2claStringList));
			parameters.put("EXP",
					new JRBeanCollectionDataSource(expStringList));
			parameters.put("EXPA",
					new JRBeanCollectionDataSource(expaStringList));
			parameters.put("CDNR",
					new JRBeanCollectionDataSource(cdnrStringList));
			parameters.put("CDNRA",
					new JRBeanCollectionDataSource(cdnraStringList));
			parameters.put("CDNUR",
					new JRBeanCollectionDataSource(cdnurStringList));
			parameters.put("CDNURA",
					new JRBeanCollectionDataSource(cdnuraStringList));
			parameters.put("B2CS",
					new JRBeanCollectionDataSource(b2csStringList));
			parameters.put("B2CSA",
					new JRBeanCollectionDataSource(b2csaStringList));
			parameters.put("SEWP",
					new JRBeanCollectionDataSource(sezwpStringList));
			parameters.put("SEWOP",
					new JRBeanCollectionDataSource(sezwopStringList));
			if (hsnUserInput) {
				parameters.put("HSN",
						new JRBeanCollectionDataSource(hsnUIStringList));
			} else {
				parameters.put("HSN",
						new JRBeanCollectionDataSource(hsnAspStringList));
			}

			parameters.put("DOC_ISSUE",
					new JRBeanCollectionDataSource(docIssuedStringList));
			if (nilUserInput) {
				parameters.put("NIL",
						new JRBeanCollectionDataSource(nilUIStringList));
			} else {
				parameters.put("NIL",
						new JRBeanCollectionDataSource(nilAspStringList));
			}
			parameters.put("AT", new JRBeanCollectionDataSource(atStringList));
			parameters.put("ATA",
					new JRBeanCollectionDataSource(ataStringList));
			parameters.put("TXPD",
					new JRBeanCollectionDataSource(txpStringList));
			parameters.put("TXPDA",
					new JRBeanCollectionDataSource(txpaStringList));
			parameters.put("gstin", gstinList.get(0));
			parameters.put("Digigst", isDigigst ? "* All figures are as per DigiGST processed data"
					: "* All figures are as per Latest Update GSTN Data");

			parameters.put("legarNameofRgP", RegisterName);
			parameters.put("gstr1Year", fYear);
			parameters.put("gstr1Month", gstr1Month);
			parameters.put("aggrPendingTrurnOver", BLANK);
			parameters.put("aggrAprilToJune", BLANK);

			parameters.put("currentDateTime", filingDate);

			File file = ResourceUtils.getFile("classpath:" + source);

			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FinalU.PNG";
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

	private StoredProcedureQuery procCall(String gstin, int derivedRetPeriod, boolean isGstr1a) {
		StoredProcedureQuery storedProc = null;
		if(Boolean.TRUE.equals(isGstr1a)){
			 storedProc = entityManager.createStoredProcedureQuery("GET_GSTR1A_PDF_DWNLD_REPORT");
		} else {
			storedProc = entityManager.createStoredProcedureQuery("GET_GSTR1_PDF_DWNLD_REPORT");
		}
	   

	    if (LOGGER.isDebugEnabled()) {
	        String msg = String.format("About to execute GET_GSTR1_PDF_DWNLD_REPORT Proc with GSTIN: %s and Derived Ret Period: %d", gstin, derivedRetPeriod);
	        LOGGER.debug(msg);
	    }

	    storedProc.registerStoredProcedureParameter("GSTIN", String.class, ParameterMode.IN);
	    storedProc.setParameter("GSTIN", gstin);

	    storedProc.registerStoredProcedureParameter("DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);
	    storedProc.setParameter("DERIVED_RET_PERIOD", derivedRetPeriod);

	    return storedProc;
	}
	
	private Gstr1PdfProcResultDto convert(Object[] arr) {
		Gstr1PdfProcResultDto dto = new Gstr1PdfProcResultDto();

	    dto.setSection((arr[0] != null) ? arr[0].toString() : null);
	    dto.setSubSection((arr[1] != null) ? arr[1].toString() : null);
	    dto.setNoOfRecords((arr[2] != null) ? arr[2].toString() : null);
	    dto.setTaxableValue((arr[3] != null) ? arr[3].toString() : null);
	    dto.setIgstAmt((arr[4] != null) ? arr[4].toString() : null);
	    dto.setCgstAmt((arr[5] != null) ? arr[5].toString() : null);
	    dto.setSgstAmt((arr[6] != null) ? arr[6].toString() : null);
	    dto.setCessAmt((arr[7] != null) ? arr[7].toString() : null);

	    return dto;
	}
	@Override
	public JasperPrint generatePdfGstr1ReportNew(
			Annexure1SummaryReqDto annexure1SummaryRequest,
			List<? extends Gstr1CompleteSummaryDto> gstnResult, String sgstin) {

		String isVerified = annexure1SummaryRequest.getIsVerified();
		String groupCode = TenantContext.getTenantId();
		String taxPeriod = annexure1SummaryRequest.getTaxPeriod();
		boolean isGstr1a = annexure1SummaryRequest.getIsGstr1a();
		Integer derivedTaxPeriod = GenUtil.getDerivedTaxPeriod(taxPeriod);
		String fYear = GenUtil.getFinancialYearByTaxperiod(taxPeriod);
		String month = taxPeriod.substring(0, 2);
		Map<String, List<String>> dataSecAttrs = annexure1SummaryRequest
				.getDataSecAttrs();

		String gstinKey = null;
		List<String> singleGstin = Arrays.asList(sgstin);
		if (!dataSecAttrs.isEmpty()) {
			for (String key : dataSecAttrs.keySet()) {

				if (key.equalsIgnoreCase(OnboardingConstant.GSTIN)) {
					gstinKey = key;
					dataSecAttrs.put(gstinKey, singleGstin);
				}
			}
		}

		StoredProcedureQuery storedProc = procCall(sgstin, derivedTaxPeriod, isGstr1a);
		
		List<Object[]> results = storedProc.getResultList();
		
		Map<String, List<Gstr1PdfProcResultDto>> groupedData = new HashMap<>();
		if (results != null && !results.isEmpty()) {
			 groupedData = results.stream()
			        .map(this::convert)
			        .collect(Collectors.groupingBy(Gstr1PdfProcResultDto::getSection));
		}
		
		Map<String, Object> parameters = new HashMap<>();
		
		List<Gstr1PdfProcResultDto> section4AData = groupedData.get("4A");
        if (section4AData != null && !section4AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section4AData) {
                
                String noOfRecords = data.getNoOfRecords();
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cgstAmt = data.getCgstAmt();
                String sgstAmt = data.getSgstAmt();
                String cessAmt = data.getCessAmt();
                parameters.put("4aTotalRecords", noOfRecords);
                parameters.put("4aTaxableValue", taxableValue);
                parameters.put("4aIgst", igstAmt);
                parameters.put("4aCgst", cgstAmt);
                parameters.put("4aSgst", sgstAmt);
                parameters.put("4aCess", cessAmt);
            }
        }
        
        List<Gstr1PdfProcResultDto> section4BData = groupedData.get("4B");
        if (section4BData != null && !section4BData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section4BData) {
                String noOfRecords = data.getNoOfRecords();
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cgstAmt = data.getCgstAmt();
                String sgstAmt = data.getSgstAmt();
                String cessAmt = data.getCessAmt();
                
                parameters.put("4bTotalRecords", noOfRecords);
                parameters.put("4bTaxableValue", taxableValue);
                parameters.put("4bIgst", igstAmt);
                parameters.put("4bCgst", cgstAmt);
                parameters.put("4bSgst", sgstAmt);
                parameters.put("4bCess", cessAmt);
            }
        }
        List<Gstr1PdfProcResultDto> section5Data = groupedData.get("5");
        if (section5Data != null && !section5Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section5Data) {
                String noOfRecords = data.getNoOfRecords();
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cessAmt = data.getCessAmt();
                
                parameters.put("5TotalRecords", noOfRecords);
                parameters.put("5TaxableValue", taxableValue);
                parameters.put("5Igst", igstAmt);
                parameters.put("5Cess", cessAmt);
            }
        }
        
        List<Gstr1PdfProcResultDto> section6AData = groupedData.get("6A");
        if (section6AData != null && !section6AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section6AData) {
                String sectionSubSection = data.getSubSection();
                if ("TOTAL".equals(sectionSubSection)) {
                    String noOfRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("6aTotalRecords", noOfRecords);
                    parameters.put("6aTotalTaxableValue", taxableValue);
                    parameters.put("6aTotalIgst", igstAmt);
                    parameters.put("6aTotalCess", cessAmt);
                } else if ("EXPWP".equals(sectionSubSection)) {
                    String expRecords = data.getNoOfRecords();
                    String expTaxableValue = data.getTaxableValue();
                    String expIgst = data.getIgstAmt();
                    String expCess = data.getCessAmt();
                    parameters.put("6aExpRecords", expRecords);
                    parameters.put("6aExpTaxableValue", expTaxableValue);
                    parameters.put("6aExpIgst", expIgst);
                    parameters.put("6aExpCess", expCess);
                } else if ("EXPWOP".equals(sectionSubSection)) {
                    String expoRecords = data.getNoOfRecords();
                    String expoTaxableValue = data.getTaxableValue();
                    parameters.put("6aExpoRecords", expoRecords);
                    parameters.put("6aExpoTaxableValue", expoTaxableValue);
                }
            }
        }
        List<Gstr1PdfProcResultDto> section6BData = groupedData.get("6B");
        if (section6BData != null && !section6BData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section6BData) {
                String sectionSubSection = data.getSubSection();
                if ("TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String totalTaxableValue = data.getTaxableValue();
                    String totalIgst = data.getIgstAmt();
                    String totalCess = data.getCessAmt();
                    parameters.put("6bTotalRecords", totalRecords);
                    parameters.put("6bTotalTaxableValue", totalTaxableValue);
                    parameters.put("6bTotalIgst", totalIgst);
                    parameters.put("6bTotalCess", totalCess);
                } else if ("SEZWP".equals(sectionSubSection)) {
                    String sezRecords = data.getNoOfRecords();
                    String sezTaxableValue = data.getTaxableValue();
                    String sezIgst = data.getIgstAmt();
                    String sezCess = data.getCessAmt();
                    parameters.put("6bSezRecords", sezRecords);
                    parameters.put("6bSezTaxableValue", sezTaxableValue);
                    parameters.put("6bSezIgst", sezIgst);
                    parameters.put("6bSezCess", sezCess);
                } else if ("SEZWOP".equals(sectionSubSection)) {
                    String sezoRecords = data.getNoOfRecords();
                    String sezoTaxableValue = data.getTaxableValue();
                    parameters.put("6bSezoRecords", sezoRecords);
                    parameters.put("6bSezoTaxableValue", sezoTaxableValue);
                }
            }
        }

        List<Gstr1PdfProcResultDto> section6CData = groupedData.get("6C");
        if (section6CData != null && !section6CData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section6CData) {
                String noOfRecords = data.getNoOfRecords();
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cgstAmt = data.getCgstAmt();
                String sgstAmt = data.getSgstAmt();
                String cessAmt = data.getCessAmt();
                
                parameters.put("6cTotalRecords", noOfRecords);
                parameters.put("6cTotalTaxableValue", taxableValue);
                parameters.put("6cTotalIgst", igstAmt);
                parameters.put("6cTotalCgst", cgstAmt);
                parameters.put("6cTotalSgst", sgstAmt);
                parameters.put("6cTotalCess", cessAmt);
            }
        }
        
        List<Gstr1PdfProcResultDto> section7Data = groupedData.get("7");
        if (section7Data != null && !section7Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section7Data) {
                String noOfRecords = data.getNoOfRecords();
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cgstAmt = data.getCgstAmt();
                String sgstAmt = data.getSgstAmt();
                String cessAmt = data.getCessAmt();
                
                parameters.put("7TotalRecords", noOfRecords);
                parameters.put("7TaxableValue", taxableValue);
                parameters.put("7Igst", igstAmt);
                parameters.put("7Cgst", cgstAmt);
                parameters.put("7Sgst", sgstAmt);
                parameters.put("7Cess", cessAmt);
            }
        }
        
        List<Gstr1PdfProcResultDto> section8Data = groupedData.get("8");
        if (section8Data != null && !section8Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section8Data) {
                String sectionSubSection = data.getSubSection();
                if ("TOTAL".equals(sectionSubSection)) {
                    String totalTaxableValue = data.getTaxableValue();
                    parameters.put("8TotalTaxableValue", totalTaxableValue != null ? totalTaxableValue : "-");
                } else if ("NIL".equals(sectionSubSection)) {
                    String nilTaxableValue = data.getTaxableValue();
                    parameters.put("8NilTaxableValue", nilTaxableValue != null ? nilTaxableValue : "-");
                } else if ("Exempted".equals(sectionSubSection)) {
                    String exemptedTaxableValue = data.getTaxableValue();
                    parameters.put("8ExemptedTaxableValue", exemptedTaxableValue != null ? exemptedTaxableValue : "-");
                } else if ("Non-GST".equals(sectionSubSection)) {
                    String nonGstTaxableValue = data.getTaxableValue();
                    parameters.put("8NonGstTaxableValue", nonGstTaxableValue != null ? nonGstTaxableValue : "-");
                }
            }
        }
        List<Gstr1PdfProcResultDto> section9AB2BRegularData = groupedData.get("9A B2B Regular");
        if (section9AB2BRegularData != null && !section9AB2BRegularData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9AB2BRegularData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aB2bRegularTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aB2bRegularTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aB2bRegularIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aB2bRegularCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9aB2bRegularSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9aB2bRegularCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTotalRecords = data.getNoOfRecords();
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCgstAmt = data.getCgstAmt();
                    String originalSgstAmt = data.getSgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aB2bRegularOriginalTotalRecords", originalTotalRecords != null ? originalTotalRecords : "-");
                    parameters.put("9aB2bRegularOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aB2bRegularOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aB2bRegularOriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                    parameters.put("9aB2bRegularOriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                    parameters.put("9aB2bRegularOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                }
            }
        }
        List<Gstr1PdfProcResultDto> section9AB2BReverseData = groupedData.get("9A B2B Reverse charge");
        if (section9AB2BReverseData != null && !section9AB2BReverseData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9AB2BReverseData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aB2bReverseTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aB2bReverseTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aB2bReverseIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aB2bReverseCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9aB2bReverseSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9aB2bReverseCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTotalRecords = data.getNoOfRecords();
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCgstAmt = data.getCgstAmt();
                    String originalSgstAmt = data.getSgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aB2bReverseOriginalTotalRecords", originalTotalRecords != null ? originalTotalRecords : "-");
                    parameters.put("9aB2bReverseOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aB2bReverseOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aB2bReverseOriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                    parameters.put("9aB2bReverseOriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                    parameters.put("9aB2bReverseOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                }
            }
        }
        List<Gstr1PdfProcResultDto> section9AB2CLData = groupedData.get("9A B2CL (Large)");
        if (section9AB2CLData != null && !section9AB2CLData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9AB2CLData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aB2bB2clTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aB2bB2clTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aB2bB2clIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aB2bB2clCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTotalRecords = data.getNoOfRecords();
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aB2bB2clOriginalTotalRecords", originalTotalRecords != null ? originalTotalRecords : "-");
                    parameters.put("9aB2bB2clOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aB2bB2clOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aB2bB2clOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section9ADataEXPWP = groupedData.get("9A (EXPWP/EXPWOP)");
        if (section9ADataEXPWP != null && !section9ADataEXPWP.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9ADataEXPWP) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aExpwpTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aExpwpTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aExpwpIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aExpwpCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aExpwpOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aExpwpOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aExpwpOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                } else if ("EXPWP".equals(sectionSubSection)) {
                    String expTotalRecords = data.getNoOfRecords();
                    String expTaxableValue = data.getTaxableValue();
                    String expIgstAmt = data.getIgstAmt();
                    String expCessAmt = data.getCessAmt();
                    parameters.put("9aExpwpExpTotalRecords", expTotalRecords != null ? expTotalRecords : "-");
                    parameters.put("9aExpwpExpTaxableValue", expTaxableValue != null ? expTaxableValue : "-");
                    parameters.put("9aExpwpExpIgst", expIgstAmt != null ? expIgstAmt : "-");
                    parameters.put("9aExpwpExpCess", expCessAmt != null ? expCessAmt : "-");
                } else if ("EXPWOP".equals(sectionSubSection)) {
                    String expOpTotalRecords = data.getNoOfRecords();
                    String expOpTaxableValue = data.getTaxableValue();
                    parameters.put("9aExpwpExpopTotalRecords", expOpTotalRecords != null ? expOpTotalRecords : "-");
                    parameters.put("9aExpwpExpopTaxableValue", expOpTaxableValue != null ? expOpTaxableValue : "-");
                }
            }
        }

        List<Gstr1PdfProcResultDto> section9ADataSEZWP = groupedData.get("9A (SEZWP/SEZWOP)");
        if (section9ADataSEZWP != null && !section9ADataSEZWP.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9ADataSEZWP) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aSezwpTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aSezwpTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aSezwpIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aSezwpCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aSezwpOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aSezwpOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aSezwpOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                } else if ("SEZWP".equals(sectionSubSection)) {
                    String sezTotalRecords = data.getNoOfRecords();
                    String sezTaxableValue = data.getTaxableValue();
                    String sezIgstAmt = data.getIgstAmt();
                    String sezCessAmt = data.getCessAmt();
                    parameters.put("9aSezwpSezTotalRecords", sezTotalRecords != null ? sezTotalRecords : "-");
                    parameters.put("9aSezwpSezTaxableValue", sezTaxableValue != null ? sezTaxableValue : "-");
                    parameters.put("9aSezwpSezIgst", sezIgstAmt != null ? sezIgstAmt : "-");
                    parameters.put("9aSezwpSezCess", sezCessAmt != null ? sezCessAmt : "-");
                } else if ("SEZWOP".equals(sectionSubSection)) {
                    String sezOpTotalRecords = data.getNoOfRecords();
                    String sezOpTaxableValue = data.getTaxableValue();
                    parameters.put("9aSezwpSezopTotalRecords", sezOpTotalRecords != null ? sezOpTotalRecords : "-");
                    parameters.put("9aSezwpSezopTaxableValue", sezOpTaxableValue != null ? sezOpTaxableValue : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section9AData = groupedData.get("9A (DE)");
        if (section9AData != null && !section9AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9AData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9aDeTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9aDeTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9aDeIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9aDeCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9aDeSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9aDeCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCgstAmt = data.getCgstAmt();
                    String originalSgstAmt = data.getSgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("9aDeOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("9aDeOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("9aDeOriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                    parameters.put("9aDeOriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                    parameters.put("9aDeOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                }
            }
        }

        List<Gstr1PdfProcResultDto> section9BData = groupedData.get("9B CDNR");
        if (section9BData != null && !section9BData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9BData) {
                String sectionSubSection = data.getSubSection();
                if ("Total(Debit notes - Credit notes) CDNR".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnrTdccTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnrTdccTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnrTdccIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnrTdccCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9bCdnrTdccSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9bCdnrTdccCess", cessAmt != null ? cessAmt : "-");
                } else if ("Net Total B2B Regular".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnrNtbrTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnrNtbrTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnrNtbrIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnrNtbrCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9bCdnrNtbrSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9bCdnrNtbrCess", cessAmt != null ? cessAmt : "-");
                } else if ("Net Total B2B Reverse charge".equals(sectionSubSection)) {
                	String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnrNtbrcTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnrNtbrcTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnrNtbrcIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnrNtbrcCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9bCdnrNtbrcSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9bCdnrNtbrcCess", cessAmt != null ? cessAmt : "-");
                } else if ("Net Total SEZWP/SEZWOP".equals(sectionSubSection)) {
                	String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnrNtssTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnrNtssTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnrNtssIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnrNtssCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9bCdnrNtssSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9bCdnrNtssCess", cessAmt != null ? cessAmt : "-");
                } else if ("Net Total DE".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnrNtdeTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnrNtdeTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnrNtdeIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnrNtdeCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("9bCdnrNtdeSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("9bCdnrNtdeCess", cessAmt != null ? cessAmt : "-");
                }
            }
        }

        List<Gstr1PdfProcResultDto> section9BCDNURData = groupedData.get("9B CDNUR");
        if (section9BCDNURData != null && !section9BCDNURData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9BCDNURData) {
            	String subsection = data.getSubSection();
            	
            	if ("Total(Debit notes - Credit notes) CDNUR".equals(subsection)) {
            		 String totalRecords = data.getNoOfRecords();
                     String taxableValue = data.getTaxableValue();
                     String igstAmt = data.getIgstAmt();
                     String cessAmt = data.getCessAmt();
                     
                     // Set parameters for Total(Debit notes - Credit notes) CDNUR
                     parameters.put("9bCdnurTdccTotalRecords", totalRecords != null ? totalRecords : "-");
                     parameters.put("9bCdnurTdccTaxableValue", taxableValue != null ? taxableValue : "-");
                     parameters.put("9bCdnurTdccIgst", igstAmt != null ? igstAmt : "-");
                     parameters.put("9bCdnurTdccCess", cessAmt != null ? cessAmt : "-");
            	}
               
                // Check for B2CL subsection
                if ("B2CL".equals(subsection)) {
                    String b2clTotalRecords = data.getNoOfRecords();
                    String b2clTaxableValue = data.getTaxableValue();
                    String b2clIgst = data.getIgstAmt();
                    String b2clCess = data.getCessAmt();
                    parameters.put("9bCdnurB2clTotalRecords", b2clTotalRecords != null ? b2clTotalRecords : "-");
                    parameters.put("9bCdnurB2clTaxableValue", b2clTaxableValue != null ? b2clTaxableValue : "-");
                    parameters.put("9bCdnurB2clIgst", b2clIgst != null ? b2clIgst : "-");
                    parameters.put("9bCdnurB2clCess", b2clCess != null ? b2clCess : "-");
                }
                
                // Check for EXPWP subsection
                if ("EXPWP".equals(subsection)) {
                    String expwpTotalRecords = data.getNoOfRecords();
                    String expwpTaxableValue = data.getTaxableValue();
                    String expwpIgst = data.getIgstAmt();
                    String expwpCess = data.getCessAmt();
                    parameters.put("9bCdnurExpwpTotalRecords", expwpTotalRecords != null ? expwpTotalRecords : "-");
                    parameters.put("9bCdnurExpwpTaxableValue", expwpTaxableValue != null ? expwpTaxableValue : "-");
                    parameters.put("9bCdnurExpwpIgst", expwpIgst != null ? expwpIgst : "-");
                    parameters.put("9bCdnurExpwpCess", expwpCess != null ? expwpCess : "-");
                }
                
                // Check for EXPWOP subsection
                if ("EXPWOP".equals(subsection)) {
                    String expwopTotalRecords = data.getNoOfRecords();
                    String expwopTaxableValue = data.getTaxableValue();
                    parameters.put("9bCdnurExpwopTotalRecords", expwopTotalRecords != null ? expwopTotalRecords : "-");
                    parameters.put("9bCdnurExpwopTaxableValue", expwopTaxableValue != null ? expwopTaxableValue : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section9CCdnraData = groupedData.get("9C CDNRA");
        if (section9CCdnraData != null && !section9CCdnraData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9CCdnraData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String tdccTotalRecords = data.getNoOfRecords();
                    String tdccTaxableValue = data.getTaxableValue();
                    String tdccIgst = data.getIgstAmt();
                    String tdccCgst = data.getCgstAmt();
                    String tdccSgst = data.getSgstAmt();
                    String tdccCess = data.getCessAmt();
                    parameters.put("9bCdnraTdccTotalRecords", tdccTotalRecords != null ? tdccTotalRecords : "-");
                    parameters.put("9bCdnraTdccTaxableValue", tdccTaxableValue != null ? tdccTaxableValue : "-");
                    parameters.put("9bCdnraTdccIgst", tdccIgst != null ? tdccIgst : "-");
                    parameters.put("9bCdnraTdccCgst", tdccCgst != null ? tdccCgst : "-");
                    parameters.put("9bCdnraTdccSgst", tdccSgst != null ? tdccSgst : "-");
                    parameters.put("9bCdnraTdccCess", tdccCess != null ? tdccCess : "-");
                } else if ("Total(Debit notes - Credit notes) CDNRA".equals(sectionSubSection)) {
                    String tdccAmenTotalRecords = data.getNoOfRecords();
                    String tdccAmenTaxableValue = data.getTaxableValue();
                    String tdccAmenIgst = data.getIgstAmt();
                    String tdccAmenCgst = data.getCgstAmt();
                    String tdccAmenSgst = data.getSgstAmt();
                    String tdccAmenCess = data.getCessAmt();
                    parameters.put("9bCdnraTdccAmenTotalRecords", tdccAmenTotalRecords != null ? tdccAmenTotalRecords : "-");
                    parameters.put("9bCdnraTdccAmenTaxableValue", tdccAmenTaxableValue != null ? tdccAmenTaxableValue : "-");
                    parameters.put("9bCdnraTdccAmenIgst", tdccAmenIgst != null ? tdccAmenIgst : "-");
                    parameters.put("9bCdnraTdccAmenCgst", tdccAmenCgst != null ? tdccAmenCgst : "-");
                    parameters.put("9bCdnraTdccAmenSgst", tdccAmenSgst != null ? tdccAmenSgst : "-");
                    parameters.put("9bCdnraTdccAmenCess", tdccAmenCess != null ? tdccAmenCess : "-");
                } else if ("Net total B2B Regular".equals(sectionSubSection)) {
                    String b2bRegularTotalRecords = data.getNoOfRecords();
                    String b2bRegularTaxableValue = data.getTaxableValue();
                    String b2bRegularIgst = data.getIgstAmt();
                    String b2bRegularCgst = data.getCgstAmt();
                    String b2bRegularSgst = data.getSgstAmt();
                    String b2bRegularCess = data.getCessAmt();
                    parameters.put("9bCdnraNetTotalB2BRegularRecords", b2bRegularTotalRecords != null ? b2bRegularTotalRecords : "-");
                    parameters.put("9bCdnraNetTotalB2BRegularTaxableValue", b2bRegularTaxableValue != null ? b2bRegularTaxableValue : "-");
                    parameters.put("9bCdnraNetTotalB2BRegularIgst", b2bRegularIgst != null ? b2bRegularIgst : "-");
                    parameters.put("9bCdnraNetTotalB2BRegularCgst", b2bRegularCgst != null ? b2bRegularCgst : "-");
                    parameters.put("9bCdnraNetTotalB2BRegularSgst", b2bRegularSgst != null ? b2bRegularSgst : "-");
                    parameters.put("9bCdnraNetTotalB2BRegularCess", b2bRegularCess != null ? b2bRegularCess : "-");
                } else if ("Net total B2B Reverse charge".equals(sectionSubSection)) {
                	String b2bReverseTotalRecords = data.getNoOfRecords();
                    String b2bReverseTaxableValue = data.getTaxableValue();
                    String b2bReverseIgst = data.getIgstAmt();
                    String b2bReverseCgst = data.getCgstAmt();
                    String b2bReverseSgst = data.getSgstAmt();
                    String b2bReverseCess = data.getCessAmt();
                    
                    parameters.put("9bCdnraB2bReverseTotalRecords", b2bReverseTotalRecords != null ? b2bReverseTotalRecords : "-");
                    parameters.put("9bCdnraB2bReverseTaxableValue", b2bReverseTaxableValue != null ? b2bReverseTaxableValue : "-");
                    parameters.put("9bCdnraB2bReverseIgst", b2bReverseIgst != null ? b2bReverseIgst : "-");
                    parameters.put("9bCdnraB2bReverseCgst", b2bReverseCgst != null ? b2bReverseCgst : "-");
                    parameters.put("9bCdnraB2bReverseSgst", b2bReverseSgst != null ? b2bReverseSgst : "-");
                    parameters.put("9bCdnraB2bReverseCess", b2bReverseCess != null ? b2bReverseCess : "-");
                } else if ("Net total SEZWP/SEZWOP".equals(sectionSubSection)) {
                	 String sezwpSezwopTotalRecords = data.getNoOfRecords();
                	 String sezwpSezwopTaxableValue = data.getTaxableValue();
                	 String sezwpSezwopIgst = data.getIgstAmt();
                	 String sezwpSezwopCess = data.getCessAmt();
                	    
                	 parameters.put("9bCdnraSezwpSezwopTotalRecords", sezwpSezwopTotalRecords != null ? sezwpSezwopTotalRecords : "-");
                	 parameters.put("9bCdnraSezwpSezwopTaxableValue", sezwpSezwopTaxableValue != null ? sezwpSezwopTaxableValue : "-");
                	 parameters.put("9bCdnraSezwpSezwopIgst", sezwpSezwopIgst != null ? sezwpSezwopIgst : "-");
                	 parameters.put("9bCdnraSezwpSezwopCess", sezwpSezwopCess != null ? sezwpSezwopCess : "-");
                } else if ("Net total DE".equals(sectionSubSection)) {
                	 String netTotalDeTotalRecords = data.getNoOfRecords();
                	 String netTotalDeTaxableValue = data.getTaxableValue();
                	 String netTotalDeIgst = data.getIgstAmt();
                	 String netTotalDeCgst = data.getCgstAmt();
                	 String netTotalDeSgst = data.getSgstAmt();
                	 String netTotalDeCess = data.getCessAmt();
                	    
                	 parameters.put("9bCdnraNetTotalDeTotalRecords", netTotalDeTotalRecords != null ? netTotalDeTotalRecords : "-");
                	 parameters.put("9bCdnraNetTotalDeTaxableValue", netTotalDeTaxableValue != null ? netTotalDeTaxableValue : "-");
                	 parameters.put("9bCdnraNetTotalDeIgst", netTotalDeIgst != null ? netTotalDeIgst : "-");
                	 parameters.put("9bCdnraNetTotalDeCgst", netTotalDeCgst != null ? netTotalDeCgst : "-");
                	 parameters.put("9bCdnraNetTotalDeSgst", netTotalDeSgst != null ? netTotalDeSgst : "-");
                	 parameters.put("9bCdnraNetTotalDeCess", netTotalDeCess != null ? netTotalDeCess : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section9CData = groupedData.get("9C CDNURA");
        if (section9CData != null && !section9CData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9CData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL CDNURA".equals(sectionSubSection) ) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("9bCdnuraTdccTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("9bCdnuraTdccTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnuraTdccIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnuraTdccCess", cessAmt != null ? cessAmt : "-");
                   
                }
                if("Total(Debit notes - Credit notes) CDNURA".equals(sectionSubSection)){
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cessAmt = data.getCessAmt();
                	parameters.put("9bCdnuraTdccAmenTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("9bCdnuraTdccAmenIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("9bCdnuraTdccAmenCess", cessAmt != null ? cessAmt : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section9CDNURAData = groupedData.get("9C CDNURA Unregistered Type");
        if (section9CDNURAData != null && !section9CDNURAData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section9CDNURAData) {
                String sectionSubSection = data.getSubSection();

                    if ("B2CL".equals(sectionSubSection)) {
                    	String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("9bCdnuraB2clTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("9bCdnuraB2clTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("9bCdnuraB2clIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("9bCdnuraB2clCess", cessAmt != null ? cessAmt : "-");
                    } else if ("EXPWP".equals(sectionSubSection)) {
                    	String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("9bCdnuraExpwpTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("9bCdnuraExpwpTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("9bCdnuraExpwpIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("9bCdnuraExpwpCess", cessAmt != null ? cessAmt : "-");
                    } else if ("EXPWOP".equals(sectionSubSection)) {
                    	String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        parameters.put("9bCdnuraExpwopTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("9bCdnuraExpwopTaxableValue", taxableValue != null ? taxableValue : "-");
                        
                    }
            }
        }

        List<Gstr1PdfProcResultDto> section10Data = groupedData.get("10");
        if (section10Data != null && !section10Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section10Data) {
                String sectionSubSection = data.getSubSection();
                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                    	 String totalRecords = data.getNoOfRecords();
                         String taxableValue = data.getTaxableValue();
                         String igstAmt = data.getIgstAmt();
                         String cgstAmt = data.getCgstAmt();
                         String sgstAmt = data.getSgstAmt();
                         String cessAmt = data.getCessAmt();

                        parameters.put("10TotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("10TaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("10Igst", igstAmt != null ? igstAmt : "-");
                        parameters.put("10Cgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("10Sgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("10Cess", cessAmt != null ? cessAmt : "-");
                    } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                        String originalTaxableValue = data.getTaxableValue();
                        String originalIgstAmt = data.getIgstAmt();
                        String originalCgstAmt = data.getCgstAmt();
                        String originalSgstAmt = data.getSgstAmt();
                        String originalCessAmt = data.getCessAmt();

                        parameters.put("10OriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                        parameters.put("10OriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                        parameters.put("10OriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                        parameters.put("10OriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                        parameters.put("10OriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                    }
            }
        }

        List<Gstr1PdfProcResultDto> section11A1Data = groupedData.get("11A(1)");
        if (section11A1Data != null && !section11A1Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section11A1Data) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    parameters.put("11A1TotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("11A1TaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("11A1Igst", igstAmt != null ? igstAmt : "-");
                    parameters.put("11A1Cgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("11A1Sgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("11A1Cess", cessAmt != null ? cessAmt : "-");
            }
        }
        
        List<Gstr1PdfProcResultDto> section11B1Data = groupedData.get("11B(1)");
        if (section11B1Data != null && !section11B1Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section11B1Data) {
                
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    
                    parameters.put("11B1TotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("11B1TaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("11B1Igst", igstAmt != null ? igstAmt : "-");
                    parameters.put("11B1Cgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("11B1Sgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("11B1Cess", cessAmt != null ? cessAmt : "-");
            }
        }
        
        List<Gstr1PdfProcResultDto> section11AData = groupedData.get("11A");
        if (section11AData != null && !section11AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section11AData) {
                String sectionSubSection = data.getSubSection();
                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                    	 String totalRecords = data.getNoOfRecords();
                         String taxableValue = data.getTaxableValue();
                         String igstAmt = data.getIgstAmt();
                         String cgstAmt = data.getCgstAmt();
                         String sgstAmt = data.getSgstAmt();
                         String cessAmt = data.getCessAmt();
                        parameters.put("11AAmenTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("11AAmenTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("11AAmenIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("11AAmenCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("11AAmenSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("11AAmenCess", cessAmt != null ? cessAmt : "-");
                    } else if ("TOTAL".equals(sectionSubSection)) {
                    	 String totalRecords = data.getNoOfRecords();
                         String taxableValue = data.getTaxableValue();
                         String igstAmt = data.getIgstAmt();
                         String cgstAmt = data.getCgstAmt();
                         String sgstAmt = data.getSgstAmt();
                         String cessAmt = data.getCessAmt();
                        parameters.put("11ATotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("11ATotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("11ATotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("11ATotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("11ATotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("11ATotalCess", cessAmt != null ? cessAmt : "-");
                    }
            }
        }

        List<Gstr1PdfProcResultDto> section11BData = groupedData.get("11B");
        if (section11BData != null && !section11BData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section11BData) {
                String sectionSubSection = data.getSubSection();

                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("11BAmenTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("11BAmenTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("11BAmenIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("11BAmenCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("11BAmenSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("11BAmenCess", cessAmt != null ? cessAmt : "-");
                    } else if ("TOTAL".equals(sectionSubSection)) {
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("11BTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("11BTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("11BTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("11BTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("11BTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("11BTotalCess", cessAmt != null ? cessAmt : "-");
                    }
            }
        }

        List<Gstr1PdfProcResultDto> section12Data = groupedData.get("12");
        if (section12Data != null && !section12Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section12Data) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    parameters.put("12TotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("12TaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("12Igst", igstAmt != null ? igstAmt : "-");
                    parameters.put("12Cgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("12Sgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("12Cess", cessAmt != null ? cessAmt : "-");
                }
        }
        
//12 B2B
        
        List<Gstr1PdfProcResultDto> section12B2BData = groupedData.get("12(B2B)");
        if (section12B2BData != null && !section12B2BData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section12B2BData) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    parameters.put("12B2BTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("12B2BTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("12B2BIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("12B2BCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("12B2BSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("12B2BCess", cessAmt != null ? cessAmt : "-");
                }
        }
        
        //12 B2C
        
        List<Gstr1PdfProcResultDto> section12B2CData = groupedData.get("12(B2C)");
        if (section12B2CData != null && !section12B2CData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section12B2BData) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    parameters.put("12B2CTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("12B2CTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("12B2CIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("12B2CCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("12B2CSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("12B2CCess", cessAmt != null ? cessAmt : "-");
                }
        }

        List<Gstr1PdfProcResultDto> section13Data = groupedData.get("13");
        if (section13Data != null && !section13Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section13Data) {
                    String totalRecords = data.getNoOfRecords();
                    parameters.put("13TotalRecords", totalRecords != null ? totalRecords : "-");
            }
        }

        List<Gstr1PdfProcResultDto> section14Data = groupedData.get("14");
        if (section14Data != null && !section14Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section14Data) {
                String sectionSubSection = data.getSubSection();
                if ("TOTAL".equals(sectionSubSection)) {
                	   String totalRecords = data.getNoOfRecords();
                       String taxableValue = data.getTaxableValue();
                       String igstAmt = data.getIgstAmt();
                       String cgstAmt = data.getCgstAmt();
                       String sgstAmt = data.getSgstAmt();
                       String cessAmt = data.getCessAmt();
                    parameters.put("14TotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("14TotalTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("14TotalIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("14TotalCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("14TotalSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("14TotalCess", cessAmt != null ? cessAmt : "-");
                } else if ("(a) Liable to collect tax u/s 52".equals(sectionSubSection)) {
                	   String totalRecords = data.getNoOfRecords();
                       String taxableValue = data.getTaxableValue();
                       String igstAmt = data.getIgstAmt();
                       String cgstAmt = data.getCgstAmt();
                       String sgstAmt = data.getSgstAmt();
                       String cessAmt = data.getCessAmt();
                    parameters.put("14alctTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("14alctTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("14alctTotalIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("14alctTotalCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("14alctTotalSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("14alctTotalCess", cessAmt != null ? cessAmt : "-");
                } else if ("(b) Liable to pay tax u/s 9(5)".equals(sectionSubSection)) {
                	   String totalRecords = data.getNoOfRecords();
                       String taxableValue = data.getTaxableValue();
                       String igstAmt = data.getIgstAmt();
                       String cgstAmt = data.getCgstAmt();
                       String sgstAmt = data.getSgstAmt();
                       String cessAmt = data.getCessAmt();
                    parameters.put("14blptTotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("14blptTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("14blptTotalIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("14blptTotalCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("14blptTotalSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("14blptTotalCess", cessAmt != null ? cessAmt : "-");
                }
            }
        }

        List<Gstr1PdfProcResultDto> section14AData = groupedData.get("14A");
        if (section14AData != null && !section14AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section14AData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();
                    parameters.put("14ATotalRecords", totalRecords != null ? totalRecords : "-");
                    parameters.put("14ATotalTaxableValue", taxableValue != null ? taxableValue : "-");
                    parameters.put("14ATotalIgst", igstAmt != null ? igstAmt : "-");
                    parameters.put("14ATotalCgst", cgstAmt != null ? cgstAmt : "-");
                    parameters.put("14ATotalSgst", sgstAmt != null ? sgstAmt : "-");
                    parameters.put("14ATotalCess", cessAmt != null ? cessAmt : "-");
                } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String originalRecords = data.getNoOfRecords();
                    String originalTaxableValue = data.getTaxableValue();
                    String originalIgstAmt = data.getIgstAmt();
                    String originalCgstAmt = data.getCgstAmt();
                    String originalSgstAmt = data.getSgstAmt();
                    String originalCessAmt = data.getCessAmt();
                    parameters.put("14AOriginalRecords", originalRecords != null ? originalRecords : "-");
                    parameters.put("14AOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                    parameters.put("14AOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                    parameters.put("14AOriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                    parameters.put("14AOriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                    parameters.put("14AOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                }
            }
        }
        
        List<Gstr1PdfProcResultDto> section14ALctData = groupedData.get("14(a) Liable to collect tax u/s 52");
        if (section14ALctData != null && !section14ALctData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section14ALctData) {
                String sectionSubSection = data.getSubSection();
                  
                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                    	  String totalRecords = data.getNoOfRecords();
                          String taxableValue = data.getTaxableValue();
                          String igstAmt = data.getIgstAmt();
                          String cgstAmt = data.getCgstAmt();
                          String sgstAmt = data.getSgstAmt();
                          String cessAmt = data.getCessAmt();
                        parameters.put("14AlctAmenTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("14AlctAmenTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("14AlctAmenIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("14AlctAmenCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("14AlctAmenSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("14AlctAmenCess", cessAmt != null ? cessAmt : "-");
                    } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                        String originalTotalRecords = data.getNoOfRecords();
                        String originalTaxableValue = data.getTaxableValue();
                        String originalIgstAmt = data.getIgstAmt();
                        String originalCgstAmt = data.getCgstAmt();
                        String originalSgstAmt = data.getSgstAmt();
                        String originalCessAmt = data.getCessAmt();

                        parameters.put("14AlctAmenOriginalTotalRecords", originalTotalRecords != null ? originalTotalRecords : "-");
                        parameters.put("14AlctAmenOriginalTaxableValue", originalTaxableValue != null ? originalTaxableValue : "-");
                        parameters.put("14AlctAmenOriginalIgst", originalIgstAmt != null ? originalIgstAmt : "-");
                        parameters.put("14AlctAmenOriginalCgst", originalCgstAmt != null ? originalCgstAmt : "-");
                        parameters.put("14AlctAmenOriginalSgst", originalSgstAmt != null ? originalSgstAmt : "-");
                        parameters.put("14AlctAmenOriginalCess", originalCessAmt != null ? originalCessAmt : "-");
                    }
            }
        }

        List<Gstr1PdfProcResultDto> section14BlptData = groupedData.get("14(b) Liable to pay tax u/s 9(5)");
        if (section14BlptData != null && !section14BlptData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section14BlptData) {
                String sectionSubSection = data.getSubSection();

                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("14BlptAmenTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("14BlptAmenTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("14BlptAmenTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("14BlptAmenTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("14BlptAmenTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("14BlptAmenTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("14BlptAmenOriginalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("14BlptAmenOriginalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("14BlptAmenOriginalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("14BlptAmenOriginalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("14BlptAmenOriginalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("14BlptAmenOriginalCess", cessAmt != null ? cessAmt : "-");
                    }
            }
        }
        
        List<Gstr1PdfProcResultDto> section15Data = groupedData.get("15");
        if (section15Data != null && !section15Data.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section15Data) {
                String sectionSubSection = data.getSubSection();
                 

                    if ("TOTAL".equals(sectionSubSection)) {
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15TotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15TotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15TotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15TotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15TotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15TotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("For Registered Recipients".equals(sectionSubSection)) { 
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15FrrTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15FrrTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15FrrTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15FrrTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15FrrTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15FrrTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("Regular".equals(sectionSubSection)) {   
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15RTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15RTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15RTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15RTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15RTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15RTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("DE".equals(sectionSubSection)) {   
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15DeTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15DeTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15DeTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15DeTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15DeTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15DeTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("SEZWP".equals(sectionSubSection)) { 
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15SezwpTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15SezwpTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15SezwpTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15SezwpTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15SezwpTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15SezwpTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("SEZWOP".equals(sectionSubSection)) {  
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15SezwpopTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15SezwpopTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15SezwpopTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15SezwpopTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15SezwpopTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15SezwpopTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("For Unregistered Recipient".equals(sectionSubSection)) {   
                        String totalRecords = data.getNoOfRecords();
                        String taxableValue = data.getTaxableValue();
                        String igstAmt = data.getIgstAmt();
                        String cgstAmt = data.getCgstAmt();
                        String sgstAmt = data.getSgstAmt();
                        String cessAmt = data.getCessAmt();
                        parameters.put("15FurTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15FurTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15FurTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15FurTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15FurTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15FurTotalCess", cessAmt != null ? cessAmt : "-");
                    }
            }
        }
        
        List<Gstr1PdfProcResultDto> section15AIData = groupedData.get("15A (I)");
        if (section15AIData != null && !section15AIData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section15AIData) {
                String sectionSubSection = data.getSubSection();
                
                if ("AMD_TOTAL".equals(sectionSubSection) || "(AMD-ORG)DIFFAMT".equals(sectionSubSection) ||
                    "Regular".equals(sectionSubSection) || "DE".equals(sectionSubSection) ||
                    "SEZWP".equals(sectionSubSection) || "SEZWOP".equals(sectionSubSection)) {
                    
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                        parameters.put("15A1TotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1TotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1TotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1TotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1TotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1TotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                        parameters.put("15A1OriginalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1OriginalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1OriginalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1OriginalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1OriginalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1OriginalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("Regular".equals(sectionSubSection)) {
                        parameters.put("15A1RegularRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1RegularTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1RegularIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1RegularCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1RegularSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1RegularCess", cessAmt != null ? cessAmt : "-");
                    } else if ("DE".equals(sectionSubSection)) {
                        parameters.put("15A1DeRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1DeTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1DeIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1DeCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1DeSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1DeCess", cessAmt != null ? cessAmt : "-");
                    } else if ("SEZWP".equals(sectionSubSection)) {
                        parameters.put("15A1SezwpTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1SezwpTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1SezwpTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1SezwpTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1SezwpTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1SezwpTotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("SEZWOP".equals(sectionSubSection)) {
                        parameters.put("15A1SezwpopTotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A1SezwpopTotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A1SezwpopTotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A1SezwpopTotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A1SezwpopTotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A1SezwpopTotalCess", cessAmt != null ? cessAmt : "-");
                    }
                }
            }
        }
        List<Gstr1PdfProcResultDto> section15AData = groupedData.get("15A (II)");
        if (section15AData != null && !section15AData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : section15AData) {
                String sectionSubSection = data.getSubSection();
                if ("AMD_TOTAL".equals(sectionSubSection) || "(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                    String totalRecords = data.getNoOfRecords();
                    String taxableValue = data.getTaxableValue();
                    String igstAmt = data.getIgstAmt();
                    String cgstAmt = data.getCgstAmt();
                    String sgstAmt = data.getSgstAmt();
                    String cessAmt = data.getCessAmt();

                    if ("AMD_TOTAL".equals(sectionSubSection)) {
                        parameters.put("15A2TotalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A2TotalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A2TotalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A2TotalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A2TotalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A2TotalCess", cessAmt != null ? cessAmt : "-");
                    } else if ("(AMD-ORG)DIFFAMT".equals(sectionSubSection)) {
                        parameters.put("15A2OriginalRecords", totalRecords != null ? totalRecords : "-");
                        parameters.put("15A2OriginalTaxableValue", taxableValue != null ? taxableValue : "-");
                        parameters.put("15A2OriginalIgst", igstAmt != null ? igstAmt : "-");
                        parameters.put("15A2OriginalCgst", cgstAmt != null ? cgstAmt : "-");
                        parameters.put("15A2OriginalSgst", sgstAmt != null ? sgstAmt : "-");
                        parameters.put("15A2OriginalCess", cessAmt != null ? cessAmt : "-");
                    }
                }
            }
        }
        List<Gstr1PdfProcResultDto> totalLiabilityData = groupedData.get("Total Liability");
        if (totalLiabilityData != null && !totalLiabilityData.isEmpty()) {
            for (Gstr1PdfProcResultDto data : totalLiabilityData) {
                String taxableValue = data.getTaxableValue();
                String igstAmt = data.getIgstAmt();
                String cgstAmt = data.getCgstAmt();
                String sgstAmt = data.getSgstAmt();
                String cessAmt = data.getCessAmt();

                parameters.put("TotalLiabilityTaxableValue", taxableValue != null ? taxableValue : "-");
                parameters.put("TotalLiabilityIgst", igstAmt != null ? igstAmt : "-");
                parameters.put("TotalLiabilityCgst", cgstAmt != null ? cgstAmt : "-");
                parameters.put("TotalLiabilitySgst", sgstAmt != null ? sgstAmt : "-");
                parameters.put("TotalLiabilityCess", cessAmt != null ? cessAmt : "-");
            }
        }

		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());
		String[] strArray = getMonthNameAndFinYear(taxPeriod);

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside generateGstr1SummaryPdfReport  method trying to get the jrxml path";
			LOGGER.debug(msg);
		}

		JasperPrint jasperPrint = null;
		String source = null;
		if(Boolean.TRUE.equals(isGstr1a)){
			source = "jasperReports/GSTR1A_SummaryNew.jrxml";
		} else {
			source = "jasperReports/GSTR1_SummaryNew.jrxml";
		}
		

		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside generateGstr1SummaryPdfReport  method trying to get the jrxml path completed:"
					+ source;
			LOGGER.debug(msg);
		}

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

		String gstr1Month = null;
		String monthDesc = monthMap.get(month);
		if (monthDesc != null) {
			gstr1Month = monthDesc;
		}


		GstrReturnStatusEntity gstrReturnStatusEntity;

		if (Boolean.TRUE.equals(isGstr1a)) {
		    gstrReturnStatusEntity = gstrReturnStatusRepository
		            .findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
		                    sgstin, taxPeriod, APIConstants.GSTR1A_RETURN_TYPE);
		} else {
		    gstrReturnStatusEntity = gstrReturnStatusRepository
		            .findByGstinAndTaxPeriodAndReturnTypeAndIsCounterPartyGstinFalse(
		                    sgstin, taxPeriod, APIConstants.GSTR1_RETURN_TYPE);
		}



		if (gstrReturnStatusEntity == null || !(gstrReturnStatusEntity
				.getStatus().equalsIgnoreCase("Filed"))) {

			List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

			try {
				PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
						PublicApiConstants.GSTR1_PDF_RET_ZIP);
				returnFilingGstnResponseDtoList = gstnReturnFilingStatus
						.callGstnApi(Arrays.asList(sgstin), strArray[1], false);

				for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

					if (returnFilingGstnResponseDto.getRetType()
							.equalsIgnoreCase(APIConstants.GSTR1_RETURN_TYPE)
							&& returnFilingGstnResponseDto.getRetPeriod()
									.equalsIgnoreCase(taxPeriod)) {

						filingStatus = returnFilingGstnResponseDto.getStatus();
						String strDate = returnFilingGstnResponseDto
								.getFilingDate() + " 00:00:00";

						LocalDateTime dateTime = LocalDateTime.parse(strDate,
								formatter2);
						LocalDateTime istDate = EYDateUtil
								.toISTDateTimeFromUTC(dateTime);
						filingDate = istDate.toString();
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
				filingDate = getStandardTime(dateTime);
			}
		}
		
		GSTNDetailEntity gstnDetailEntity = gSTNDetailRepository
				.findByGstinAndIsDeleteFalse(sgstin);

		if (!filingStatus.equalsIgnoreCase("Filed")) {
			Optional<GstnSubmitEntity> gstnSubmitStatus = null;
			if(Boolean.TRUE.equals(isGstr1a)){
				 gstnSubmitStatus = gstnSubmitRepository
						.findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
								sgstin, taxPeriod, APIConstants.GSTR1A_RETURN_TYPE);
			} else {
				 gstnSubmitStatus = gstnSubmitRepository
						.findTop1ByGstinAndRetPeriodAndReturnTypeAndIsDeleteFalseOrderByIdDesc(
								sgstin, taxPeriod, APIConstants.GSTR1_RETURN_TYPE);
			}
			if (gstnSubmitStatus.isPresent()) {
				String submitStatus = gstnSubmitStatus.get().getGstnStatus();
				if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
					filingStatus = "SUBMITTED";
					LocalDateTime dateTime = gstnSubmitStatus.get()
							.getCreatedOn();
					filingDate = getStandardTime(dateTime);

				}
			}
		}

		try {
			
			TaxPayerDetailsDto apiResp =null;
			if(Strings.isNullOrEmpty(gstnDetailEntity.getLegalName())  
					&& Strings.isNullOrEmpty(gstnDetailEntity.getTradeName())){
				try {
					apiResp = taxPayerService
							.getTaxPayerDetails(sgstin, groupCode);
				} catch (Exception e) {
					LOGGER.error(
							"Not able to taxpayer details from gstn");
				}
				gSTNDetailRepository.updateLegalAndTradeName(
						apiResp.getLegalBussNam(), 
						apiResp.getTradeName(), sgstin, 
						gstnDetailEntity.getEntityId(), groupCode);
				
				if(apiResp.getLegalBussNam() != null){
					parameters.put("legalName", apiResp.getLegalBussNam());
				}
				
				if(apiResp.getTradeName() != null){
					parameters.put("tradeName", apiResp.getTradeName());
				}
			}
			
			
			if(gstnDetailEntity != null){
				if(gstnDetailEntity.getLegalName() != null){
					parameters.put("legalName", gstnDetailEntity.getLegalName());
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
			
			List<DongleMappingEntity> dongleMapping = dongleMappingRepository
					.findByGstinAndIsActiveTrue(sgstin);
			
			if(!dongleMapping.isEmpty() || dongleMapping != null){
				for(DongleMappingEntity dongle: dongleMapping){
					if(dongle.getGstin().equalsIgnoreCase(sgstin)){
						if (filingStatus.equalsIgnoreCase("Filed")) {
							parameters.put("authorisedSignatory", dongle.getAuthorisedName());
							parameters.put("designation", dongle.getDesignation());
						}
						
					}
				}
			}
			


			 String regName = regRepo.findRegTypeByGstinForPdf(sgstin);

			String RegisterName = "";
			if (regName != null) {
				RegisterName = regName;
			}


			parameters.put("gstin", sgstin);
			parameters.put("legarNameofRgP", RegisterName);
			parameters.put("year", fYear);
			parameters.put("period", gstr1Month);
			parameters.put("aggrPendingTrurnOver", BLANK);
			parameters.put("aggrAprilToJune", BLANK);

			parameters.put("currentDateTime", filingDate);
			parameters.put("Digigst",
					"* All figures are as per DigiGST processed data");

			File file = ResourceUtils.getFile("classpath:" + source);

			if (filingStatus.equalsIgnoreCase("Filed")) {
				image = "FinalU.PNG";
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

			JasperReport jasperReport = JasperCompileManager
					.compileReport(file.toString());
			if (LOGGER.isDebugEnabled()) {
				String msg = "filling values to jasper report begin";
				LOGGER.debug(msg);
			}
			jasperPrint = JasperFillManager.fillReport(jasperReport, parameters,
					new JREmptyDataSource());
			if (LOGGER.isDebugEnabled()) {
				String msg = "filling values to jasper report ends";
				LOGGER.debug(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = "jasper print retuned successfilly";
				LOGGER.debug(msg);
			}
			return jasperPrint;
		} catch (Exception ex) {
			String errMsg = String.format(
					"Exception occured while genearting pdf for Gstin %s",
					sgstin);
			LOGGER.error(errMsg, ex);
			throw new AppException(errMsg, ex);
		}

	}

}
