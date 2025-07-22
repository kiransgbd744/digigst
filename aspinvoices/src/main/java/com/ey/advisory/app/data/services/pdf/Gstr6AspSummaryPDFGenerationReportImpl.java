/**
 * 
 */
package com.ey.advisory.app.data.services.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6PdfReportDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseDto;
import com.ey.advisory.app.docs.dto.gstr6a.Gstr6ReviewSummaryResponseItemDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsDto;
import com.ey.advisory.app.gstr.taxpayerdetail.TaxPayerDetailsService;
import com.ey.advisory.app.services.daos.gstr6a.Gstr6ReviewSummaryServiceImpl;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GstnReturnFilingStatus;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Annexure1SummaryReqDto;
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
@Slf4j
@Component("Gstr6AspSummaryPDFGenerationReportImpl")
public class Gstr6AspSummaryPDFGenerationReportImpl
		implements Gstr6AspSummaryPDFGenerationReport {

	private static final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy HH:mm:ss");

	private static final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("dd-MM-yyyy ");

	@Autowired
	@Qualifier("Gstr6ReviewSummaryServiceImpl")
	private Gstr6ReviewSummaryServiceImpl gstr6RevSumSerImpl;

	@Autowired
	@Qualifier("GSTNDetailRepository")
	GSTNDetailRepository gSTNDetailRepository;

	@Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository legalName;
	
	@Autowired
	@Qualifier("taxPayerDetailsServiceImpl")
	TaxPayerDetailsService taxPayerService;

	@Autowired
	GstrReturnStatusRepository gstrReturnStatusRepository;

	@Autowired
	GstnReturnFilingStatus gstnReturnFilingStatus;

	@Autowired
	GstnSubmitRepository gstnSubmitRepository;
	
	@Autowired
	DongleMappingRepository dongleMappingRepository;

	@Override
	public JasperPrint generatePdfGstr6Report(
			Gstr2AProcessedRecordsReqDto reqDto, String gstn) {
		// TODO Auto-generated method stub

		JasperPrint jasperPrint = null;
		String source = "jasperReports/GSTR6.jrxml";

		List<Long> entityId = reqDto.getEntityId();
		boolean isDigigst = reqDto.getIsDigigst();
		String filingStatus = "Draft";
		String image = null;
		String filingDate = getStandardTime(LocalDateTime.now());

		String fYear = GenUtil
				.getFinancialYearByTaxperiod(reqDto.getRetunPeriod());

		String month = convertMonth(reqDto.getRetunPeriod());

		Annexure1SummaryReqDto request = new Annexure1SummaryReqDto();
		request.setTaxPeriod(reqDto.getRetunPeriod());

		Map<String, List<String>> dataSecAttrs = reqDto.getDataSecAttrs();

		List<String> singleGstin = Arrays.asList(gstn);
		dataSecAttrs.put(OnboardingConstant.GSTIN, singleGstin);
		request.setDataSecAttrs(dataSecAttrs);

		List<Gstr6ReviewSummaryResponseDto> respDtos = gstr6RevSumSerImpl
				.getGstr6RevSummary(request);

		Gstr6PdfReportDto b2bDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> b2bList = new ArrayList<>();

		Gstr6PdfReportDto b2baDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> b2baList = new ArrayList<>();

		Gstr6PdfReportDto cdnDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> cdnList = new ArrayList<>();

		Gstr6PdfReportDto cdnaDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> cdnaList = new ArrayList<>();

		Gstr6PdfReportDto eitDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> eitList = new ArrayList<>();

		Gstr6PdfReportDto disDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> disList = new ArrayList<>();

		Gstr6PdfReportDto rdisDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> rdisList = new ArrayList<>();

		Gstr6PdfReportDto lateFeeDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> lateFeeList = new ArrayList<>();

		Gstr6PdfReportDto rdistDto = new Gstr6PdfReportDto();
		List<Gstr6PdfReportDto> rdistList = new ArrayList<>();

		BigInteger b2bCount = BigInteger.ZERO;
		BigDecimal b2bInvValue = BigDecimal.ZERO;
		BigDecimal b2bIgst = BigDecimal.ZERO;
		BigDecimal b2bSgst = BigDecimal.ZERO;
		BigDecimal b2bCgst = BigDecimal.ZERO;
		BigDecimal b2bCess = BigDecimal.ZERO;

		BigInteger b2baCount = BigInteger.ZERO;
		BigDecimal b2baInvValue = BigDecimal.ZERO;
		BigDecimal b2baIgst = BigDecimal.ZERO;
		BigDecimal b2baSgst = BigDecimal.ZERO;
		BigDecimal b2baCgst = BigDecimal.ZERO;
		BigDecimal b2baCess = BigDecimal.ZERO;

		BigInteger cdnCount = BigInteger.ZERO;
		BigDecimal cdnInvValue = BigDecimal.ZERO;
		BigDecimal cdnIgst = BigDecimal.ZERO;
		BigDecimal cdnSgst = BigDecimal.ZERO;
		BigDecimal cdnCgst = BigDecimal.ZERO;
		BigDecimal cdnCess = BigDecimal.ZERO;

		BigInteger cdnaCount = BigInteger.ZERO;
		BigDecimal cdnaInvValue = BigDecimal.ZERO;
		BigDecimal cdnaIgst = BigDecimal.ZERO;
		BigDecimal cdnaSgst = BigDecimal.ZERO;
		BigDecimal cdnaCgst = BigDecimal.ZERO;
		BigDecimal cdnaCess = BigDecimal.ZERO;

		BigInteger eitCount = BigInteger.ZERO;
		BigDecimal eitEligible = BigDecimal.ZERO;
		BigDecimal eitIneligible = BigDecimal.ZERO;
		BigDecimal eitAspTaxbValue = BigDecimal.ZERO;
		BigDecimal eitAspTotalTax = BigDecimal.ZERO;
		BigDecimal eitAspInvValue = BigDecimal.ZERO;

		BigInteger disCount = BigInteger.ZERO;
		BigDecimal disEligible = BigDecimal.ZERO;
		BigDecimal disIneligible = BigDecimal.ZERO;
		BigDecimal disAspTaxbValue = BigDecimal.ZERO;
		BigDecimal disAspTotalTax = BigDecimal.ZERO;
		BigDecimal disAspInvValue = BigDecimal.ZERO;

		BigInteger dcrCount = BigInteger.ZERO;
		BigDecimal dcrEligible = BigDecimal.ZERO;
		BigDecimal dcrIneligible = BigDecimal.ZERO;
		BigDecimal dcrAspTaxbValue = BigDecimal.ZERO;
		BigDecimal dcrAspTotalTax = BigDecimal.ZERO;
		BigDecimal dcrAspInvValue = BigDecimal.ZERO;

		BigInteger rdisCount = BigInteger.ZERO;
		BigDecimal rdisEligible = BigDecimal.ZERO;
		BigDecimal rdisIneligible = BigDecimal.ZERO;
		BigDecimal rdisAspTaxbValue = BigDecimal.ZERO;
		BigDecimal rdisAspTotalTax = BigDecimal.ZERO;
		BigDecimal rdisAspInvValue = BigDecimal.ZERO;

		BigInteger rdcrCount = BigInteger.ZERO;
		BigDecimal rdcrEligible = BigDecimal.ZERO;
		BigDecimal rdcrIneligible = BigDecimal.ZERO;
		BigDecimal rdcrAspTaxbValue = BigDecimal.ZERO;
		BigDecimal rdcrAspTotalTax = BigDecimal.ZERO;
		BigDecimal rdcrAspInvValue = BigDecimal.ZERO;

		BigInteger lfCount = BigInteger.ZERO;
		BigDecimal lfInvValue = BigDecimal.ZERO;
		BigDecimal lfIgst = BigDecimal.ZERO;
		BigDecimal lfSgst = BigDecimal.ZERO;
		BigDecimal lfCgst = BigDecimal.ZERO;
		BigDecimal lfCess = BigDecimal.ZERO;

		try {
			Map<String, Object> parameters = new HashMap<>();

			for (Gstr6ReviewSummaryResponseDto respDto : respDtos) {

				if (respDto.getDocType().equalsIgnoreCase("B2B (Section 3)")) {

					b2bCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					b2bInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());
					b2bIgst = (isDigigst ? respDto.getAspIgst()
							: respDto.getGstnIgst());
					b2bSgst = (isDigigst ? respDto.getAspSgst()
							: respDto.getGstnSgst());
					b2bCgst = (isDigigst ? respDto.getAspCgst()
							: respDto.getGstnCgst());
					b2bCess = (isDigigst ? respDto.getAspCess()
							: respDto.getGstnCess());

				}
				if (respDto.getDocType()
						.equalsIgnoreCase("B2BA (Section 6A)")) {

					b2baCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					b2baInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());
					b2baIgst = (isDigigst ? respDto.getAspIgst()
							: respDto.getGstnIgst());
					b2baSgst = (isDigigst ? respDto.getAspSgst()
							: respDto.getGstnSgst());
					b2baCgst = (isDigigst ? respDto.getAspCgst()
							: respDto.getGstnCgst());
					b2baCess = (isDigigst ? respDto.getAspCess()
							: respDto.getGstnCess());

				}
				if (respDto.getDocType().equalsIgnoreCase("CDN (Section 6B)")) {

					cdnCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					cdnInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());
					cdnIgst = (isDigigst ? respDto.getAspIgst()
							: respDto.getGstnIgst());
					cdnSgst = (isDigigst ? respDto.getAspSgst()
							: respDto.getGstnSgst());
					cdnCgst = (isDigigst ? respDto.getAspCgst()
							: respDto.getGstnCgst());
					cdnCess = (isDigigst ? respDto.getAspCess()
							: respDto.getGstnCess());

				}
				if (respDto.getDocType()
						.equalsIgnoreCase("CDNA (Section 6C)")) {

					cdnaCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					cdnaInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());
					cdnaIgst = (isDigigst ? respDto.getAspIgst()
							: respDto.getGstnIgst());
					cdnaSgst = (isDigigst ? respDto.getAspSgst()
							: respDto.getGstnSgst());
					cdnaCgst = (isDigigst ? respDto.getAspCgst()
							: respDto.getGstnCgst());
					cdnaCess = (isDigigst ? respDto.getAspCess()
							: respDto.getGstnCess());

				}
				if (respDto.getDocType().equalsIgnoreCase(
						"Eligible / Ineligible ITC (Section 4)")) {

					eitCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					eitAspTaxbValue = (isDigigst ? respDto.getAspTaxbValue()
							: respDto.getGstnTaxbValue());
					eitAspTotalTax = (isDigigst ? respDto.getAspTotTax()
							: respDto.getGstnTotTax());
					eitAspInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());

					List<Gstr6ReviewSummaryResponseItemDto> itemsEit = respDto
							.getItems();

					for (Gstr6ReviewSummaryResponseItemDto item : itemsEit) {

						if (item.getDocType().equalsIgnoreCase("Eligible")) {
							eitEligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						} else if (item.getDocType()
								.equalsIgnoreCase("Ineligible")) {
							// eitIneligible = eligibleIneligibleum(item);
							eitIneligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						}

					}

				}
				if (respDto.getDocType().equalsIgnoreCase(
						"Distribution - Invoices (Section 5)")) {

					disCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());

					List<Gstr6ReviewSummaryResponseItemDto> itemsDis = respDto
							.getItems();

					for (Gstr6ReviewSummaryResponseItemDto item : itemsDis) {

						if (item.getDocType().equalsIgnoreCase("Eligible")) {
							disEligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						} else if (item.getDocType()
								.equalsIgnoreCase("Ineligible")) {
							disIneligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						}

					}

				}
				if (respDto.getDocType().equalsIgnoreCase(
						"Distribution - Credit Notes (Section 8)")) {

					dcrCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());

					List<Gstr6ReviewSummaryResponseItemDto> itemsDcr = respDto
							.getItems();

					for (Gstr6ReviewSummaryResponseItemDto item : itemsDcr) {

						if (item.getDocType().equalsIgnoreCase("Eligible")) {
							dcrEligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						} else if (item.getDocType()
								.equalsIgnoreCase("Ineligible")) {
							dcrIneligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						}

					}

				}
				if (respDto.getDocType().equalsIgnoreCase(
						"Redistribution - Invoices (Section 9)")) {

					rdisCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());

					List<Gstr6ReviewSummaryResponseItemDto> itemsrdis = respDto
							.getItems();

					for (Gstr6ReviewSummaryResponseItemDto item : itemsrdis) {

						if (item.getDocType().equalsIgnoreCase("Eligible")) {
							rdisEligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						} else if (item.getDocType()
								.equalsIgnoreCase("Ineligible")) {
							rdisIneligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						}

					}

				}

				if (respDto.getDocType().equalsIgnoreCase(
						"Redistribution - Invoices (Section 9)")) {

					rdcrCount = (isDigigst ? respDto.getAspCount()
							: respDto.getGstnCount());
					rdcrAspTaxbValue = (isDigigst ? respDto.getAspTaxbValue()
							: respDto.getGstnTaxbValue());
					rdcrAspTotalTax = (isDigigst ? respDto.getAspTotTax()
							: respDto.getGstnTotTax());
					rdcrAspInvValue = (isDigigst ? respDto.getAspInvValue()
							: respDto.getGstnInvValue());

					List<Gstr6ReviewSummaryResponseItemDto> itemsrdcr = respDto
							.getItems();

					for (Gstr6ReviewSummaryResponseItemDto item : itemsrdcr) {

						if (item.getDocType().equalsIgnoreCase("Eligible")) {
							rdcrEligible = eligibleIneligibleum(item);
						} else if (item.getDocType()
								.equalsIgnoreCase("Ineligible")) {
							rdcrIneligible = (isDigigst ? item.getAspTotTax()
									: item.getGstnTotTax());
						}

					}

				}

			}

			// table 3
			b2bDto.setNoofRecords(b2bCount.toString());
			b2bDto.setTotalInvoiceValue(GenUtil.formatCurrency(b2bInvValue));
			b2bDto.setTotalIntigratedValue(GenUtil.formatCurrency(b2bIgst));
			b2bDto.setTotalStateTax(GenUtil.formatCurrency(b2bSgst));
			b2bDto.setTotalCentralTax(GenUtil.formatCurrency(b2bCgst));
			b2bDto.setAspCess(GenUtil.formatCurrency(b2bCess));

			// CDN
			cdnDto.setNoofRecords(cdnCount.toString());
			cdnDto.setTotalInvoiceValue(GenUtil.formatCurrency(cdnInvValue));
			cdnDto.setTotalIntigratedValue(GenUtil.formatCurrency(cdnIgst));
			cdnDto.setTotalStateTax(GenUtil.formatCurrency(cdnSgst));
			cdnDto.setTotalCentralTax(GenUtil.formatCurrency(cdnCgst));
			cdnDto.setAspCess(GenUtil.formatCurrency(cdnCess));

			// CDNA
			cdnaDto.setNoofRecords(cdnaCount.toString());
			cdnaDto.setTotalInvoiceValue(GenUtil.formatCurrency(cdnaInvValue));
			cdnaDto.setTotalIntigratedValue(GenUtil.formatCurrency(cdnaIgst));
			cdnaDto.setTotalStateTax(GenUtil.formatCurrency(cdnaSgst));
			cdnaDto.setTotalCentralTax(GenUtil.formatCurrency(cdnaCgst));
			cdnaDto.setAspCess(GenUtil.formatCurrency(cdnaCess));

			// b2ba
			b2baDto.setNoofRecords(b2baCount.toString());
			b2baDto.setTotalInvoiceValue(GenUtil.formatCurrency(b2baInvValue));
			b2baDto.setTotalIntigratedValue(GenUtil.formatCurrency(b2baIgst));
			b2baDto.setTotalStateTax(GenUtil.formatCurrency(b2baSgst));
			b2baDto.setTotalCentralTax(GenUtil.formatCurrency(b2baCgst));
			b2baDto.setAspCess(GenUtil.formatCurrency(b2baCess));

			// table 4

			eitDto.setTotalITC(
					GenUtil.formatCurrency(eitEligible.add(eitIneligible)));
			eitDto.setTotalEligibleITC(GenUtil.formatCurrency(eitEligible));
			eitDto.setTotalIneligibleITC(GenUtil.formatCurrency(eitIneligible));

			// 5 & 8

			disDto.setNoofRecords(disCount.add(dcrCount).toString());
			disDto.setTotalEligibleITC(
					GenUtil.formatCurrency(disEligible.add(dcrEligible)));
			disDto.setTotalIneligibleITC(
					GenUtil.formatCurrency(disIneligible.add(dcrIneligible)));

			// TABLE 9 two sections

			rdisDto.setNoofRecords(rdisCount.add(rdcrCount).toString());
			rdisDto.setTotalEligibleITC(
					GenUtil.formatCurrency(rdisEligible.add(rdcrEligible)));
			rdisDto.setTotalIneligibleITC(
					GenUtil.formatCurrency(rdisIneligible.add(rdcrIneligible)));

			// Late Fee

			lateFeeDto.setNoofRecords("0");
			lateFeeDto.setTotalInvoiceValue("0.00");
			lateFeeDto.setTotalIntigratedValue("0.00");
			lateFeeDto.setTotalStateTax("0.00");
			lateFeeDto.setTotalCentralTax("0.00");
			lateFeeDto.setAspCess("0.00");

			b2bList.add(b2bDto);
			b2baList.add(b2baDto);
			cdnList.add(cdnDto);
			cdnaList.add(cdnaDto);
			lateFeeList.add(lateFeeDto);

			eitList.add(eitDto);
			disList.add(disDto);
			rdisList.add(rdisDto);

			// For water mark

			GstrReturnStatusEntity gstrReturnStatusEntity = gstrReturnStatusRepository
					.findByGstinAndTaxPeriodAndReturnTypeContainingIgnoreCaseAndIsCounterPartyGstinFalse(
							gstn, reqDto.getRetunPeriod(), APIConstants.GSTR6);

			if (gstrReturnStatusEntity == null || !(gstrReturnStatusEntity
					.getStatus().equalsIgnoreCase("Filed"))) {

				List<ReturnFilingGstnResponseDto> returnFilingGstnResponseDtoList;

				try {
					PublicApiContext.setContextMap(PublicApiConstants.SOURCE,
							PublicApiConstants.GSTR6_PDF_RET);
					returnFilingGstnResponseDtoList = gstnReturnFilingStatus
							.callGstnApi(Arrays.asList(gstn), fYear, false);

					for (ReturnFilingGstnResponseDto returnFilingGstnResponseDto : returnFilingGstnResponseDtoList) {

						if (returnFilingGstnResponseDto.getRetType()
								.equalsIgnoreCase(APIConstants.GSTR6)
								&& returnFilingGstnResponseDto.getRetPeriod()
										.equalsIgnoreCase(
												reqDto.getRetunPeriod())) {

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

					/*
					 * LocalDateTime dateTime = gstrReturnStatusEntity
					 * .getUpdatedOn(); filingDate = getStandardTime(dateTime);
					 */

					filingDate = gstrReturnStatusEntity.getFilingDate()
							.toString();
				}
			}
			if (!filingStatus.equalsIgnoreCase("Filed")) {
				GstnSubmitEntity gstnSubmitStatus = gstnSubmitRepository
						.findGstnStatusForSingleGstin(gstn,
								reqDto.getRetunPeriod(),
								APIConstants.GSTR1_RETURN_TYPE);
				if (gstnSubmitStatus != null) {
					String submitStatus = gstnSubmitStatus.getGstnStatus();
					if (APIConstants.P.equalsIgnoreCase(submitStatus)) {
						filingStatus = "SUBMITTED";
						LocalDateTime dateTime = gstnSubmitStatus
								.getCreatedOn();
						filingDate = getStandardTime(dateTime);
					}
				}
			}

			String regName = legalName.entityNameById(entityId);

			if (regName != null) {
				parameters.put("legalNameofregis", regName);
			} else {
				parameters.put("legalNameofregis", "-");
			}
			String groupCode = TenantContext.getTenantId();
			GSTNDetailEntity gstnDetailEntity = gSTNDetailRepository
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
				gSTNDetailRepository.updateLegalAndTradeName(
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

			if (!Strings.isNullOrEmpty(reqDto.getIsVerified())) {
				if (reqDto.getIsVerified().equalsIgnoreCase("YES")) {
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

			parameters.put("Digigst",
					isDigigst
							? "* All figures are as per DigiGST processed data"
							: "* All figures are as per Update GSTN Data");

			// gstin
			parameters.put("gstin", gstn);
			parameters.put("gsrt6Year", fYear);
			parameters.put("gstr6Month", month);

			// table 3 B2B
			parameters.put("B2B", new JRBeanCollectionDataSource(b2bList));

			// B2BA
			parameters.put("B2BA", new JRBeanCollectionDataSource(b2baList));

			// Table 5 & 8
			parameters.put("SEWP", new JRBeanCollectionDataSource(disList));

			// table 4
			parameters.put("EXP", new JRBeanCollectionDataSource(eitList));

			// table 3 CDN & CDNA
			parameters.put("CDNR", new JRBeanCollectionDataSource(cdnList));
			parameters.put("CDNRA", new JRBeanCollectionDataSource(cdnaList));

			// TABLE 9

			parameters.put("B2CL", new JRBeanCollectionDataSource(rdisList));

			// Late Fee

			parameters.put("B2CS", new JRBeanCollectionDataSource(lateFeeList));

			File file = ResourceUtils.getFile("classpath:" + source);

			parameters.put("currentDateTime", filingDate);

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

	public BigDecimal eligibleIneligibleum(
			Gstr6ReviewSummaryResponseItemDto itemsb2b) {

		BigDecimal eligible = BigDecimal.ZERO;

		// if (itemsb2b.getDocType().equalsIgnoreCase("Eligible")) {

		eligible = eligible.add(itemsb2b.getAspTotTax())
				.add(itemsb2b.getAspIgst()).add(itemsb2b.getAspCgst())
				.add(itemsb2b.getAspSgst()).add(itemsb2b.getAspCess());

		// }
		/*
		 * if (itemsb2b.getDocType().equalsIgnoreCase("Ineligible")) {
		 * 
		 * eligible = eligible.add(itemsb2b.getAspTaxbValue())
		 * .add(itemsb2b.getAspTaxbValue())
		 * .add(itemsb2b.getAspTotTax()).add(itemsb2b.getAspIgst())
		 * .add(itemsb2b.getAspCgst()).add(itemsb2b.getAspSgst())
		 * .add(itemsb2b.getAspCess());
		 * 
		 * }
		 */

		return eligible;

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
