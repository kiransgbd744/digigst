package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BImpgsezHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BIsdaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BItcItemRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2bHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingB2baHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnrHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingCdnraHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BLinkingEcomaHeaderRepository;
import com.ey.advisory.app.data.repositories.client.Gstr2GetGstr2BSuppSmryRepository;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2RespParserImpl")
public class Gstr2RespParserImpl implements Gstr2RespParser {

	@Autowired
	@Qualifier("Gstr2BParseServiceDefault")
	private Gstr2BParseServiceDefault parseService;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2bHeaderRepository")
	private Gstr2GetGstr2BLinkingB2bHeaderRepository b2bHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingB2baHeaderRepository")
	private Gstr2GetGstr2BLinkingB2baHeaderRepository b2bAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnrHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnrHeaderRepository cdnrHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingCdnraHeaderRepository")
	private Gstr2GetGstr2BLinkingCdnraHeaderRepository cdnrAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdHeaderRepository")
	private Gstr2GetGstr2BIsdHeaderRepository isdHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BIsdaHeaderRepository")
	private Gstr2GetGstr2BIsdaHeaderRepository isdAHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgHeaderRepository")
	private Gstr2GetGstr2BImpgHeaderRepository impgHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BImpgsezHeaderRepository")
	private Gstr2GetGstr2BImpgsezHeaderRepository impgSezHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BItcItemRepository")
	private Gstr2GetGstr2BItcItemRepository itcSummaryRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BSuppSmryRepository")
	private Gstr2GetGstr2BSuppSmryRepository suppSummaryRepo;
	
	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomHeaderRepository ecomHeaderRepo;

	@Autowired
	@Qualifier("Gstr2GetGstr2BLinkingEcomaHeaderRepository")
	private Gstr2GetGstr2BLinkingEcomaHeaderRepository ecomaHeaderRepo;

	public void pasrseResp(boolean isSingleProcess, Gstr2BGETDataDto reqDto,
			Long invocationId) {

		List<Gstr2GetGstr2BItcItemEntity> itcSummryList = new ArrayList<>();
		List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList = new ArrayList<>();
		String status = isSingleProcess ? "SAVED" : "ONHOLD";
		 boolean isDelete = isSingleProcess ? Boolean.FALSE : Boolean.TRUE;
		String checkSum = reqDto.getChecksum();
		if (reqDto.getDetailedData() == null) {
			String msg = String.format(
					"GSTR2B :: No Data Availble to persist %s id{} ",
					invocationId);
			LOGGER.error(msg);

		}

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		String gstin = reqDto.getDetailedData().getRGstin();
		String taxPeriod = reqDto.getDetailedData().getTaxpeiod();
		String genDateString = reqDto.getDetailedData().getGenDate();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate genDateLocal = genDateString != null
				? LocalDate.parse(genDateString, formatter) : null;
		LocalDateTime genDate = genDateLocal != null
				? genDateLocal.atStartOfDay() : null;

		String version = reqDto.getDetailedData().getVersion();

		ITCSummary itcSummary = reqDto.getDetailedData().getItcSummary();
		SupplierWiseSummary suppSummary = reqDto.getDetailedData()
				.getSuppSummary();
		DocDetails docDetails = reqDto.getDetailedData().getDocDetails();
		DocDetails rejectedDocDetails = reqDto.getDetailedData().getDocRejdata();
		
		//Akhilesh
		RejectedSupplierWiseSummary rejSuppSummary = reqDto.getDetailedData()
		        .getRejSuppSummary();

		if (itcSummary != null) {

			Gstr2GetGstr2BItcItemEntity itcEntity = new Gstr2GetGstr2BItcItemEntity();

			ITCAvailableSummary itcAvailableSummary = itcSummary
					.getItcAvailableSummary();
			ITCUnAvailableSummary itcUnAvailableSummary = itcSummary
					.getItcUnAvailableSummary();
			
			ITCRejectedSummary itcRejectedSummary = itcSummary
					.getItcRejectedSummary();
			
			if (itcRejectedSummary != null) {

				itcEntity.setItcSummary(APIConstants.GSTR2B_ITC_Rejected);

				Imports imports = itcRejectedSummary.getImports();
				ISDSupplies isdSup = itcRejectedSummary.getIsdSup();
				ReverseAndNonReverseChargeSupplies nonRevSup = itcRejectedSummary
						.getNonRevSup();
				OtherSupplies otherSup = itcRejectedSummary.getOtherSup();
				ReverseAndNonReverseChargeSupplies revSup = itcRejectedSummary
						.getRevSup();

				if (imports != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(imports.getIgst());
						innerEntity.setCgstAmt(imports.getCgst());
						innerEntity.setSgstAmt(imports.getSgst());
						innerEntity.setCessAmt(imports.getCess());
						itcSummryList.add(innerEntity);
					}

					IMPGA impgAItem = imports.getImpgAItem();
					IMPGASez impgASezItem = imports.getImpgASezItem();
					IMPG impgItem = imports.getImpgItem();
					IMPGSez impgSezItem = imports.getImpgSezItem();

					if (impgItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPG);
						innerEntity.setIgstAmt(impgItem.getIgst());
						innerEntity.setCgstAmt(impgItem.getCgst());
						innerEntity.setSgstAmt(impgItem.getSgst());
						innerEntity.setCessAmt(impgItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGA);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(impgAItem.getIgst());
						innerEntity.setCgstAmt(impgAItem.getCgst());
						innerEntity.setSgstAmt(impgAItem.getSgst());
						innerEntity.setCessAmt(impgAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgSezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGSEZ);
						innerEntity.setIgstAmt(impgSezItem.getIgst());
						innerEntity.setCgstAmt(impgSezItem.getCgst());
						innerEntity.setSgstAmt(impgSezItem.getSgst());
						innerEntity.setCessAmt(impgSezItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgASezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_IMPGASEZ);
						innerEntity.setIgstAmt(impgASezItem.getIgst());
						innerEntity.setCgstAmt(impgASezItem.getCgst());
						innerEntity.setSgstAmt(impgASezItem.getSgst());
						innerEntity.setCessAmt(impgASezItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}

				if (isdSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(null);
						innerEntity.setIgstAmt(isdSup.getIgst());
						innerEntity.setCgstAmt(isdSup.getCgst());
						innerEntity.setSgstAmt(isdSup.getSgst());
						innerEntity.setCessAmt(isdSup.getCess());
						itcSummryList.add(innerEntity);
					}
					ISDA isdAItem = isdSup.getIsdAItem();
					ISD isdItem = isdSup.getIsdItem();
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (nonRevSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(nonRevSup.getIgst());
						innerEntity.setCgstAmt(nonRevSup.getCgst());
						innerEntity.setSgstAmt(nonRevSup.getSgst());
						innerEntity.setCessAmt(nonRevSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = nonRevSup.getB2bItem();
					B2BA b2bAItem = nonRevSup.getB2bAItem();
					CDNR cdnrItem = nonRevSup.getCdnrItem();
					CDNRA cdnrAItem = nonRevSup.getCdnrAItem();
					ECOM ecomItem = nonRevSup.getEcomItem();
					ECOMA ecomAItem = nonRevSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}

				}
				if (otherSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(null);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(otherSup.getIgst());
						innerEntity.setCgstAmt(otherSup.getCgst());
						innerEntity.setSgstAmt(otherSup.getSgst());
						innerEntity.setCessAmt(otherSup.getCess());
						itcSummryList.add(innerEntity);
					}

					ISD isdItem = otherSup.getIsdItem();
					ISDA isdAItem = otherSup.getIsdAItem();

					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);
					}

					CDNR cdnrItem = otherSup.getCdnrItem();
					CDNRA cdnrAItem = otherSup.getCdnrAItem();
					CDNRRev cdnrRevItem = otherSup.getCdnrRevItem();
					CDNRARev cdnrARevItem = otherSup.getCdnrARevItem();

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrRevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrRevItem.getIgst());
						innerEntity.setCgstAmt(cdnrRevItem.getCgst());
						innerEntity.setSgstAmt(cdnrRevItem.getSgst());
						innerEntity.setCessAmt(cdnrRevItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrARevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_CDNRAREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrARevItem.getIgst());
						innerEntity.setCgstAmt(cdnrARevItem.getCgst());
						innerEntity.setSgstAmt(cdnrARevItem.getSgst());
						innerEntity.setCessAmt(cdnrARevItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (revSup != null) {

					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(revSup.getIgst());
						innerEntity.setCgstAmt(revSup.getCgst());
						innerEntity.setSgstAmt(revSup.getSgst());
						innerEntity.setCessAmt(revSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = revSup.getB2bItem();
					B2BA b2bAItem = revSup.getB2bAItem();
					CDNR cdnrItem = revSup.getCdnrItem();
					CDNRA cdnrAItem = revSup.getCdnrAItem();
					ECOM ecomItem = revSup.getEcomItem();
					ECOMA ecomAItem = revSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryRejectedHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryRejectedHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}
				}
			}

			if (itcAvailableSummary != null) {

				Imports imports = itcAvailableSummary.getImports();
				ISDSupplies isdSup = itcAvailableSummary.getIsdSup();
				ReverseAndNonReverseChargeSupplies nonRevSup = itcAvailableSummary
						.getNonRevSup();
				OtherSupplies otherSup = itcAvailableSummary.getOtherSup();
				ReverseAndNonReverseChargeSupplies revSup = itcAvailableSummary
						.getRevSup();

				if (imports != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(imports.getIgst());
						innerEntity.setCgstAmt(imports.getCgst());
						innerEntity.setSgstAmt(imports.getSgst());
						innerEntity.setCessAmt(imports.getCess());
						itcSummryList.add(innerEntity);
					}

					IMPGA impgAItem = imports.getImpgAItem();
					IMPGASez impgASezItem = imports.getImpgASezItem();
					IMPG impgItem = imports.getImpgItem();
					IMPGSez impgSezItem = imports.getImpgSezItem();

					if (impgItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPG);
						innerEntity.setIgstAmt(impgItem.getIgst());
						innerEntity.setCgstAmt(impgItem.getCgst());
						innerEntity.setSgstAmt(impgItem.getSgst());
						innerEntity.setCessAmt(impgItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGA);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(impgAItem.getIgst());
						innerEntity.setCgstAmt(impgAItem.getCgst());
						innerEntity.setSgstAmt(impgAItem.getSgst());
						innerEntity.setCessAmt(impgAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgSezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGSEZ);
						innerEntity.setIgstAmt(impgSezItem.getIgst());
						innerEntity.setCgstAmt(impgSezItem.getCgst());
						innerEntity.setSgstAmt(impgSezItem.getSgst());
						innerEntity.setCessAmt(impgSezItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgASezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_IMPGASEZ);
						innerEntity.setIgstAmt(impgASezItem.getIgst());
						innerEntity.setCgstAmt(impgASezItem.getCgst());
						innerEntity.setSgstAmt(impgASezItem.getSgst());
						innerEntity.setCessAmt(impgASezItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}

				if (isdSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setIgstAmt(isdSup.getIgst());
						innerEntity.setCgstAmt(isdSup.getCgst());
						innerEntity.setSgstAmt(isdSup.getSgst());
						innerEntity.setCessAmt(isdSup.getCess());
						itcSummryList.add(innerEntity);
					}
					ISDA isdAItem = isdSup.getIsdAItem();
					ISD isdItem = isdSup.getIsdItem();
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (nonRevSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(nonRevSup.getIgst());
						innerEntity.setCgstAmt(nonRevSup.getCgst());
						innerEntity.setSgstAmt(nonRevSup.getSgst());
						innerEntity.setCessAmt(nonRevSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = nonRevSup.getB2bItem();
					B2BA b2bAItem = nonRevSup.getB2bAItem();
					CDNR cdnrItem = nonRevSup.getCdnrItem();
					CDNRA cdnrAItem = nonRevSup.getCdnrAItem();
					ECOM ecomItem = nonRevSup.getEcomItem();
					ECOMA ecomAItem = nonRevSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}

				}
				if (otherSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(null);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(otherSup.getIgst());
						innerEntity.setCgstAmt(otherSup.getCgst());
						innerEntity.setSgstAmt(otherSup.getSgst());
						innerEntity.setCessAmt(otherSup.getCess());
						itcSummryList.add(innerEntity);
					}

					CDNR cdnrItem = otherSup.getCdnrItem();
					CDNRA cdnrAItem = otherSup.getCdnrAItem();
					CDNRRev cdnrRevItem = otherSup.getCdnrRevItem();
					CDNRARev cdnrARevItem = otherSup.getCdnrARevItem();
					ISD isdItem = otherSup.getIsdItem();
					ISDA isdAItem = otherSup.getIsdAItem();

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrRevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrRevItem.getIgst());
						innerEntity.setCgstAmt(cdnrRevItem.getCgst());
						innerEntity.setSgstAmt(cdnrRevItem.getSgst());
						innerEntity.setCessAmt(cdnrRevItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrARevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_CDNRAREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrARevItem.getIgst());
						innerEntity.setCgstAmt(cdnrARevItem.getCgst());
						innerEntity.setSgstAmt(cdnrARevItem.getSgst());
						innerEntity.setCessAmt(cdnrARevItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (revSup != null) {

					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(revSup.getIgst());
						innerEntity.setCgstAmt(revSup.getCgst());
						innerEntity.setSgstAmt(revSup.getSgst());
						innerEntity.setCessAmt(revSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = revSup.getB2bItem();
					B2BA b2bAItem = revSup.getB2bAItem();
					CDNR cdnrItem = revSup.getCdnrItem();
					CDNRA cdnrAItem = revSup.getCdnrAItem();
					ECOM ecomItem = revSup.getEcomItem();
					ECOMA ecomAItem = revSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}
				}

			}

			if (itcUnAvailableSummary != null) {

				itcEntity.setItcSummary(APIConstants.GSTR2B_ITC_UnAvailable);

				Imports imports = itcUnAvailableSummary.getImports();
				ISDSupplies isdSup = itcUnAvailableSummary.getIsdSup();
				ReverseAndNonReverseChargeSupplies nonRevSup = itcUnAvailableSummary
						.getNonRevSup();
				OtherSupplies otherSup = itcUnAvailableSummary.getOtherSup();
				ReverseAndNonReverseChargeSupplies revSup = itcUnAvailableSummary
						.getRevSup();

				if (imports != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(imports.getIgst());
						innerEntity.setCgstAmt(imports.getCgst());
						innerEntity.setSgstAmt(imports.getSgst());
						innerEntity.setCessAmt(imports.getCess());
						itcSummryList.add(innerEntity);
					}

					IMPGA impgAItem = imports.getImpgAItem();
					IMPGASez impgASezItem = imports.getImpgASezItem();
					IMPG impgItem = imports.getImpgItem();
					IMPGSez impgSezItem = imports.getImpgSezItem();

					if (impgItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPG);
						innerEntity.setIgstAmt(impgItem.getIgst());
						innerEntity.setCgstAmt(impgItem.getCgst());
						innerEntity.setSgstAmt(impgItem.getSgst());
						innerEntity.setCessAmt(impgItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGA);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setIgstAmt(impgAItem.getIgst());
						innerEntity.setCgstAmt(impgAItem.getCgst());
						innerEntity.setSgstAmt(impgAItem.getSgst());
						innerEntity.setCessAmt(impgAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgSezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_IMPGSEZ);
						innerEntity.setIgstAmt(impgSezItem.getIgst());
						innerEntity.setCgstAmt(impgSezItem.getCgst());
						innerEntity.setSgstAmt(impgSezItem.getSgst());
						innerEntity.setCessAmt(impgSezItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (impgASezItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.Imports);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_IMPGASEZ);
						innerEntity.setIgstAmt(impgASezItem.getIgst());
						innerEntity.setCgstAmt(impgASezItem.getCgst());
						innerEntity.setSgstAmt(impgASezItem.getSgst());
						innerEntity.setCessAmt(impgASezItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}

				if (isdSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(null);
						innerEntity.setIgstAmt(isdSup.getIgst());
						innerEntity.setCgstAmt(isdSup.getCgst());
						innerEntity.setSgstAmt(isdSup.getSgst());
						innerEntity.setCessAmt(isdSup.getCess());
						itcSummryList.add(innerEntity);
					}
					ISDA isdAItem = isdSup.getIsdAItem();
					ISD isdItem = isdSup.getIsdItem();
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(APIConstants.ISDSupplies);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (nonRevSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(nonRevSup.getIgst());
						innerEntity.setCgstAmt(nonRevSup.getCgst());
						innerEntity.setSgstAmt(nonRevSup.getSgst());
						innerEntity.setCessAmt(nonRevSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = nonRevSup.getB2bItem();
					B2BA b2bAItem = nonRevSup.getB2bAItem();
					CDNR cdnrItem = nonRevSup.getCdnrItem();
					CDNRA cdnrAItem = nonRevSup.getCdnrAItem();
					ECOM ecomItem = nonRevSup.getEcomItem();
					ECOMA ecomAItem = nonRevSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.NonReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}

				}
				if (otherSup != null) {
					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(null);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(otherSup.getIgst());
						innerEntity.setCgstAmt(otherSup.getCgst());
						innerEntity.setSgstAmt(otherSup.getSgst());
						innerEntity.setCessAmt(otherSup.getCess());
						itcSummryList.add(innerEntity);
					}

					ISD isdItem = otherSup.getIsdItem();
					ISDA isdAItem = otherSup.getIsdAItem();

					if (isdItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISD);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdItem.getIgst());
						innerEntity.setCgstAmt(isdItem.getCgst());
						innerEntity.setSgstAmt(isdItem.getSgst());
						innerEntity.setCessAmt(isdItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (isdAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ISDA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(isdAItem.getIgst());
						innerEntity.setCgstAmt(isdAItem.getCgst());
						innerEntity.setSgstAmt(isdAItem.getSgst());
						innerEntity.setCessAmt(isdAItem.getCess());
						itcSummryList.add(innerEntity);
					}

					CDNR cdnrItem = otherSup.getCdnrItem();
					CDNRA cdnrAItem = otherSup.getCdnrAItem();
					CDNRRev cdnrRevItem = otherSup.getCdnrRevItem();
					CDNRARev cdnrARevItem = otherSup.getCdnrARevItem();

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrRevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrRevItem.getIgst());
						innerEntity.setCgstAmt(cdnrRevItem.getCgst());
						innerEntity.setSgstAmt(cdnrRevItem.getSgst());
						innerEntity.setCessAmt(cdnrRevItem.getCess());
						itcSummryList.add(innerEntity);
					}
					if (cdnrARevItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity
								.setInvoiceType(APIConstants.GSTR2B_CDNRAREV);
						innerEntity.setItcDetails(APIConstants.OtherSupplies);
						innerEntity.setIgstAmt(cdnrARevItem.getIgst());
						innerEntity.setCgstAmt(cdnrARevItem.getCgst());
						innerEntity.setSgstAmt(cdnrARevItem.getSgst());
						innerEntity.setCessAmt(cdnrARevItem.getCess());
						itcSummryList.add(innerEntity);
					}

				}
				if (revSup != null) {

					if (true) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(revSup.getIgst());
						innerEntity.setCgstAmt(revSup.getCgst());
						innerEntity.setSgstAmt(revSup.getSgst());
						innerEntity.setCessAmt(revSup.getCess());
						itcSummryList.add(innerEntity);

					}
					B2B b2bItem = revSup.getB2bItem();
					B2BA b2bAItem = revSup.getB2bAItem();
					CDNR cdnrItem = revSup.getCdnrItem();
					CDNRA cdnrAItem = revSup.getCdnrAItem();
					ECOM ecomItem = revSup.getEcomItem();
					ECOMA ecomAItem = revSup.getEcomAItem();

					if (b2bItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2B);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bItem.getIgst());
						innerEntity.setCgstAmt(b2bItem.getCgst());
						innerEntity.setSgstAmt(b2bItem.getSgst());
						innerEntity.setCessAmt(b2bItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (b2bAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_B2BA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(b2bAItem.getIgst());
						innerEntity.setCgstAmt(b2bAItem.getCgst());
						innerEntity.setSgstAmt(b2bAItem.getSgst());
						innerEntity.setCessAmt(b2bAItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNR);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrItem.getIgst());
						innerEntity.setCgstAmt(cdnrItem.getCgst());
						innerEntity.setSgstAmt(cdnrItem.getSgst());
						innerEntity.setCessAmt(cdnrItem.getCess());
						itcSummryList.add(innerEntity);

					}

					if (cdnrAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();

						setITCSummaryUnAvailHeaderData(now, userName, gstin,
								taxPeriod, genDate, version, checkSum,
								innerEntity, invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_CDNRA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(cdnrAItem.getIgst());
						innerEntity.setCgstAmt(cdnrAItem.getCgst());
						innerEntity.setSgstAmt(cdnrAItem.getSgst());
						innerEntity.setCessAmt(cdnrAItem.getCess());
						itcSummryList.add(innerEntity);

					}
					if (ecomItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOM);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomItem.getIgst());
						innerEntity.setCgstAmt(ecomItem.getCgst());
						innerEntity.setSgstAmt(ecomItem.getSgst());
						innerEntity.setCessAmt(ecomItem.getCess());
						itcSummryList.add(innerEntity);

					}
					
					if (ecomAItem != null) {
						Gstr2GetGstr2BItcItemEntity innerEntity = new Gstr2GetGstr2BItcItemEntity();
						setITCSummaryUnAvailHeaderData(now, userName, gstin, taxPeriod,
								genDate, version, checkSum, innerEntity,
								invocationId, status, isDelete);
						innerEntity.setInvoiceType(APIConstants.GSTR2B_ECOMA);
						innerEntity.setItcDetails(
								APIConstants.ReverseChareSupplies);
						innerEntity.setIgstAmt(ecomAItem.getIgst());
						innerEntity.setCgstAmt(ecomAItem.getCgst());
						innerEntity.setSgstAmt(ecomAItem.getSgst());
						innerEntity.setCessAmt(ecomAItem.getCess());
						itcSummryList.add(innerEntity);

					}
				}
			}


			// Saving ITC Summary Data
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Before Saving to GSTR2B ITC Summary table, Id, size {}, {}",
						invocationId, itcSummryList.size());
			}
			itcSummaryRepo.saveAll(itcSummryList);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Saved successfully to GSTR2B ITC Summary table, Id, size {}, {}",
						invocationId, itcSummryList.size());
			}

		}
		// rejSuppSummary
		
		if (rejSuppSummary != null) {

			List<B2BAndB2bASummary> b2bASummary = rejSuppSummary.getB2bASummary();
			List<B2BAndB2bASummary> b2bSummary = rejSuppSummary.getB2bSummary();
			List<CDNRAndCDNRASummary> cdnrASummary = rejSuppSummary
					.getCdnrASummary();
			List<CDNRAndCDNRASummary> cdnrSummary = rejSuppSummary
					.getCdnrSummary();
			List<IMPGSEZSummary> impgSezSummary = rejSuppSummary
					.getImpgSezSummary();
			List<ISDAndISDASummary> isdASummary = rejSuppSummary.getIsdASummary();
			List<ISDAndISDASummary> isdSummary = rejSuppSummary.getIsdSummary();
			
			List<EcomSummary> ecomSummary = rejSuppSummary.getEcomSummary();
			List<EcomSummary> ecomASummary = rejSuppSummary.getEcomASummary();

			if (b2bASummary != null && !b2bASummary.isEmpty()) {
				suppSumB2BAndB2BAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						b2bASummary, APIConstants.GSTR2B_B2BA, invocationId,
						status, isDelete, true);
			}
			if (b2bSummary != null && !b2bSummary.isEmpty()) {
				suppSumB2BAndB2BAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, b2bSummary,
						APIConstants.GSTR2B_B2B, invocationId, status,
						isDelete, true);
			}

			if (cdnrASummary != null && !cdnrASummary.isEmpty()) {
				suppSumCDNRAndCDNRAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						cdnrASummary, APIConstants.GSTR2B_CDNRA, invocationId,
						status, isDelete, true);

			}

			if (cdnrSummary != null && !cdnrSummary.isEmpty()) {
				suppSumCDNRAndCDNRAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						cdnrSummary, APIConstants.GSTR2B_CDNR, invocationId,
						status, isDelete, true);

			}

			if (isdASummary != null && !isdASummary.isEmpty()) {
				suppSumIsdAndIsdAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						isdASummary, APIConstants.GSTR2B_ISDA, invocationId,
						status, isDelete, true);

			}

			if (isdSummary != null && !isdSummary.isEmpty()) {
				suppSumIsdAndIsdAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, isdSummary,
						APIConstants.GSTR2B_ISD, invocationId, status,
						isDelete, true);

			}

			if (impgSezSummary != null && !impgSezSummary.isEmpty()) {
				suppSumImpgSezParse(checkSum, now, userName, gstin, taxPeriod,
						genDate, version, suppEntityList, impgSezSummary,
						APIConstants.GSTR2B_IMPGSEZ, invocationId, status,
						isDelete, true);

			}
			if (ecomSummary != null && !ecomSummary.isEmpty()) {
				suppSumEcomAndEcomAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, ecomSummary,
						APIConstants.GSTR2B_ECOM, invocationId, status,
						isDelete, true);
			}
			if (ecomASummary != null && !ecomASummary.isEmpty()) {
				suppSumEcomAndEcomAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, ecomASummary,
						APIConstants.GSTR2B_ECOMA, invocationId, status,
						isDelete, true);
			}

			// Saving Supplier Summary Data
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Before Saving to GSTR2B Rejected Supplier Summary table, Id, size {}, {}",
						invocationId, suppEntityList.size());
			}
			suppSummaryRepo.saveAll(suppEntityList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Saved successfully to GSTR2B Rejected Supplier Summary table, Id, size {}, {}",
						invocationId, suppEntityList.size());
			}

		}
		
		if (suppSummary != null) {

			List<B2BAndB2bASummary> b2bASummary = suppSummary.getB2bASummary();
			List<B2BAndB2bASummary> b2bSummary = suppSummary.getB2bSummary();
			List<CDNRAndCDNRASummary> cdnrASummary = suppSummary
					.getCdnrASummary();
			List<CDNRAndCDNRASummary> cdnrSummary = suppSummary
					.getCdnrSummary();
			List<IMPGSEZSummary> impgSezSummary = suppSummary
					.getImpgSezSummary();
			List<ISDAndISDASummary> isdASummary = suppSummary.getIsdASummary();
			List<ISDAndISDASummary> isdSummary = suppSummary.getIsdSummary();
			
			List<EcomSummary> ecomSummary = suppSummary.getEcomSummary();
			List<EcomSummary> ecomASummary = suppSummary.getEcomASummary();

			if (b2bASummary != null && !b2bASummary.isEmpty()) {
				suppSumB2BAndB2BAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						b2bASummary, APIConstants.GSTR2B_B2BA, invocationId,
						status, isDelete, false);
			}
			if (b2bSummary != null && !b2bSummary.isEmpty()) {
				suppSumB2BAndB2BAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, b2bSummary,
						APIConstants.GSTR2B_B2B, invocationId, status,
						isDelete, false);
			}

			if (cdnrASummary != null && !cdnrASummary.isEmpty()) {
				suppSumCDNRAndCDNRAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						cdnrASummary, APIConstants.GSTR2B_CDNRA, invocationId,
						status, isDelete, false);

			}

			if (cdnrSummary != null && !cdnrSummary.isEmpty()) {
				suppSumCDNRAndCDNRAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						cdnrSummary, APIConstants.GSTR2B_CDNR, invocationId,
						status, isDelete, false);

			}

			if (isdASummary != null && !isdASummary.isEmpty()) {
				suppSumIsdAndIsdAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList,
						isdASummary, APIConstants.GSTR2B_ISDA, invocationId,
						status, isDelete, false);

			}

			if (isdSummary != null && !isdSummary.isEmpty()) {
				suppSumIsdAndIsdAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, isdSummary,
						APIConstants.GSTR2B_ISD, invocationId, status,
						isDelete, false);

			}

			if (impgSezSummary != null && !impgSezSummary.isEmpty()) {
				suppSumImpgSezParse(checkSum, now, userName, gstin, taxPeriod,
						genDate, version, suppEntityList, impgSezSummary,
						APIConstants.GSTR2B_IMPGSEZ, invocationId, status,
						isDelete, false);

			}
			if (ecomSummary != null && !ecomSummary.isEmpty()) {
				suppSumEcomAndEcomAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, ecomSummary,
						APIConstants.GSTR2B_ECOM, invocationId, status,
						isDelete, false);
			}
			if (ecomASummary != null && !ecomASummary.isEmpty()) {
				suppSumEcomAndEcomAParse(checkSum, now, userName, gstin,
						taxPeriod, genDate, version, suppEntityList, ecomASummary,
						APIConstants.GSTR2B_ECOMA, invocationId, status,
						isDelete, false);
			}

			// Saving Supplier Summary Data
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Before Saving to GSTR2B Supplier Summary table, Id, size {}, {}",
						invocationId, suppEntityList.size());
			}
			suppSummaryRepo.saveAll(suppEntityList);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Saved successfully to GSTR2B Supplier Summary table, Id, size {}, {}",
						invocationId, suppEntityList.size());
			}

		}
		if (docDetails != null) {

			List<B2BDocuments> b2bSummary = docDetails.getB2bSummary();
			List<B2BADocuments> b2bASummary = docDetails.getB2bASummary();
			List<CDNRDocuments> cdnrSummary = docDetails.getCdnrSummary();
			List<CDNRADocuments> cdnrASummary = docDetails.getCdnrASummary();
			List<IMPGDocuments> impgSummary = docDetails.getImpgSummary();
			List<IMPGSEZDocuments> impgSezSummary = docDetails
					.getImpgSezSummary();
			List<ISDDocuments> isdSummary = docDetails.getIsdSummary();
			List<ISDADocuments> isdASummary = docDetails.getIsdASummary();
			
			List<EcomDocuments> ecomSummary = docDetails.getEcomSummary();
			List<EcomADocuments> ecomASummary = docDetails.getEcomASummary();

			if (b2bSummary != null && !b2bSummary.isEmpty()) {

				List<Gstr2GetGstr2BLinkingB2bHeaderEntity> b2bTemp = parseService
						.parseB2bData(reqDto, b2bSummary, genDate, invocationId,
								status, isDelete);

				// Saving B2B Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bTemp.size());
				}
				b2bHeaderRepo.saveAll(b2bTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bTemp.size());
				}
			}

			if (b2bASummary != null && !b2bASummary.isEmpty()) {

				List<Gstr2GetGstr2BLinkingB2baHeaderEntity> b2bATemp = parseService
						.parseB2bAData(reqDto, b2bASummary, genDate,
								invocationId, status, isDelete);

				// Saving B2BA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bATemp.size());
				}

				b2bAHeaderRepo.saveAll(b2bATemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bATemp.size());
				}

			}

			if (cdnrSummary != null && !cdnrSummary.isEmpty()) {

				List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> cdnrTemp = parseService
						.parseCdnrData(reqDto, cdnrSummary, genDate,
								invocationId, status, isDelete);

				// Saving CDNR Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B CDNR table, Id, size {}, {}",
							invocationId, cdnrTemp.size());
				}
				cdnrHeaderRepo.saveAll(cdnrTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B CDNR table, Id, size {}, {}",
							invocationId, cdnrTemp.size());
				}

			}

			if (cdnrASummary != null && !cdnrASummary.isEmpty()) {
				List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> cdnrATemp = parseService
						.parseCdnrAData(reqDto, cdnrASummary, genDate,
								invocationId, status, isDelete);

				// Saving CDNRA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B CDNRA table, Id, size {}, {}",
							invocationId, cdnrATemp.size());
				}

				cdnrAHeaderRepo.saveAll(cdnrATemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B CDNRA table, Id, size {}, {}",
							invocationId, cdnrATemp.size());
				}

			}

			if (isdSummary != null && !isdSummary.isEmpty()) {
				List<Gstr2GetGstr2BIsdHeaderEntity> isdTemp = parseService
						.parseIsdData(reqDto, isdSummary, genDate, invocationId,
								status, isDelete);

				// Saving ISD Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B ISD table, Id, size {}, {}",
							invocationId, isdTemp.size());
				}

				isdHeaderRepo.saveAll(isdTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B ISD table, Id, size {}, {}",
							invocationId, isdTemp.size());
				}

			}

			if (isdASummary != null && !isdASummary.isEmpty()) {
				List<Gstr2GetGstr2BIsdaHeaderEntity> isdATemp = parseService
						.parseIsdAData(reqDto, isdASummary, genDate,
								invocationId, status, isDelete);

				// Saving ISDA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B ISDA table, Id, size {}, {}",
							invocationId, isdATemp.size());
				}

				isdAHeaderRepo.saveAll(isdATemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B ISDA table, Id, size {}, {}",
							invocationId, isdATemp.size());
				}

			}

			if (impgSummary != null && !impgSummary.isEmpty()) {
				List<Gstr2GetGstr2BImpgHeaderEntity> impgTemp = parseService
						.parseImpgData(reqDto, impgSummary, genDate,
								invocationId, status, isDelete);

				// Saving IMPG Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B IMPG table, Id, size {}, {}",
							invocationId, impgTemp.size());
				}

				impgHeaderRepo.saveAll(impgTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B IMPG table, Id, size {}, {}",
							invocationId, impgTemp.size());
				}

			}

			if (impgSezSummary != null && !impgSezSummary.isEmpty()) {
				List<Gstr2GetGstr2BImpgsezHeaderEntity> impgSezEntity = parseService
						.parseImpgSezData(reqDto, impgSezSummary, genDate,
								invocationId, status, isDelete);

				// saving to IMPG SEZ table
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B IMPG SEZ table, Id, size {}, {}",
							invocationId, impgSezEntity.size());
				}

				impgSezHeaderRepo.saveAll(impgSezEntity);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B IMPG SEZ table, Id, size {}, {}",
							invocationId, impgSezEntity.size());
				}

			}
			
			if (ecomSummary != null && !ecomSummary.isEmpty()) {

				List<Gstr2GetGstr2BLinkingEcomHeaderEntity> ecomTemp = parseService
						.parseEcomData(reqDto, ecomSummary, genDateLocal, invocationId,
								status, isDelete);

				// Saving Ecom Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B Ecom table, Id, size {}, {}",
							invocationId, ecomTemp.size());
				}
				ecomHeaderRepo.saveAll(ecomTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B Ecom table, Id, size {}, {}",
							invocationId, ecomTemp.size());
				}
			}

			if (ecomASummary != null && !ecomASummary.isEmpty()) {

				List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> ecomaTemp = parseService
						.parseEcomaData(reqDto, ecomASummary, genDateLocal,
								invocationId, status, isDelete);

				// Saving ECOMA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B Ecoma table, Id, size {}, {}",
							invocationId, ecomaTemp.size());
				}

				ecomaHeaderRepo.saveAll(ecomaTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B Ecoma table, Id, size {}, {}",
							invocationId, ecomaTemp.size());
				}

			}

		}
		
		if (rejectedDocDetails != null) {

			List<B2BDocuments> b2bSummary = rejectedDocDetails.getB2bSummary();
			List<B2BADocuments> b2bASummary = rejectedDocDetails.getB2bASummary();
			List<CDNRDocuments> cdnrSummary = rejectedDocDetails.getCdnrSummary();
			List<CDNRADocuments> cdnrASummary = rejectedDocDetails.getCdnrASummary();
			List<IMPGDocuments> impgSummary = rejectedDocDetails.getImpgSummary();
			List<IMPGSEZDocuments> impgSezSummary = rejectedDocDetails
					.getImpgSezSummary();
			List<ISDDocuments> isdSummary = rejectedDocDetails.getIsdSummary();
			List<ISDADocuments> isdASummary = rejectedDocDetails.getIsdASummary();
			
			List<EcomDocuments> ecomSummary = rejectedDocDetails.getEcomSummary();
			List<EcomADocuments> ecomASummary = rejectedDocDetails.getEcomASummary();

			if (b2bSummary != null && !b2bSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BLinkingB2bHeaderEntity> b2bTemp = parseService
						.parseB2bData(reqDto, b2bSummary, genDate, invocationId,
								status, isDelete);

				// Saving B2B Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bTemp.size());
				}
				b2bHeaderRepo.saveAll(b2bTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bTemp.size());
				}
			}

			if (b2bASummary != null && !b2bASummary.isEmpty()) {
				reqDto.setItcRejected(true);	
				List<Gstr2GetGstr2BLinkingB2baHeaderEntity> b2bATemp = parseService
						.parseB2bAData(reqDto, b2bASummary, genDate,
								invocationId, status, isDelete);

				// Saving B2BA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bATemp.size());
				}

				b2bAHeaderRepo.saveAll(b2bATemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B B2B table, Id, size {}, {}",
							invocationId, b2bATemp.size());
				}

			}

			if (cdnrSummary != null && !cdnrSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> cdnrTemp = parseService
						.parseCdnrData(reqDto, cdnrSummary, genDate,
								invocationId, status, isDelete);

				// Saving CDNR Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B CDNR table, Id, size {}, {}",
							invocationId, cdnrTemp.size());
				}
				cdnrHeaderRepo.saveAll(cdnrTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B CDNR table, Id, size {}, {}",
							invocationId, cdnrTemp.size());
				}

			}

			if (cdnrASummary != null && !cdnrASummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> cdnrATemp = parseService
						.parseCdnrAData(reqDto, cdnrASummary, genDate,
								invocationId, status, isDelete);

				// Saving CDNRA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B CDNRA table, Id, size {}, {}",
							invocationId, cdnrATemp.size());
				}

				cdnrAHeaderRepo.saveAll(cdnrATemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B CDNRA table, Id, size {}, {}",
							invocationId, cdnrATemp.size());
				}

			}

			if (isdSummary != null && !isdSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BIsdHeaderEntity> isdTemp = parseService
						.parseIsdData(reqDto, isdSummary, genDate, invocationId,
								status, isDelete);

				// Saving ISD Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B ISD table, Id, size {}, {}",
							invocationId, isdTemp.size());
				}

				isdHeaderRepo.saveAll(isdTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B ISD table, Id, size {}, {}",
							invocationId, isdTemp.size());
				}

			}

			if (isdASummary != null && !isdASummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BIsdaHeaderEntity> isdATemp = parseService
						.parseIsdAData(reqDto, isdASummary, genDate,
								invocationId, status, isDelete);

				// Saving ISDA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B ISDA table, Id, size {}, {}",
							invocationId, isdATemp.size());
				}

				isdAHeaderRepo.saveAll(isdATemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B ISDA table, Id, size {}, {}",
							invocationId, isdATemp.size());
				}

			}

			if (impgSummary != null && !impgSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BImpgHeaderEntity> impgTemp = parseService
						.parseImpgData(reqDto, impgSummary, genDate,
								invocationId, status, isDelete);

				// Saving IMPG Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B IMPG table, Id, size {}, {}",
							invocationId, impgTemp.size());
				}

				impgHeaderRepo.saveAll(impgTemp);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B IMPG table, Id, size {}, {}",
							invocationId, impgTemp.size());
				}

			}

			if (impgSezSummary != null && !impgSezSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BImpgsezHeaderEntity> impgSezEntity = parseService
						.parseImpgSezData(reqDto, impgSezSummary, genDate,
								invocationId, status, isDelete);

				// saving to IMPG SEZ table
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B IMPG SEZ table, Id, size {}, {}",
							invocationId, impgSezEntity.size());
				}

				impgSezHeaderRepo.saveAll(impgSezEntity);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B IMPG SEZ table, Id, size {}, {}",
							invocationId, impgSezEntity.size());
				}

			}
			
			if (ecomSummary != null && !ecomSummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BLinkingEcomHeaderEntity> ecomTemp = parseService
						.parseEcomData(reqDto, ecomSummary, genDateLocal, invocationId,
								status, isDelete);

				// Saving Ecom Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B Ecom table, Id, size {}, {}",
							invocationId, ecomTemp.size());
				}
				ecomHeaderRepo.saveAll(ecomTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B Ecom table, Id, size {}, {}",
							invocationId, ecomTemp.size());
				}
			}

			if (ecomASummary != null && !ecomASummary.isEmpty()) {
				reqDto.setItcRejected(true);
				List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> ecomaTemp = parseService
						.parseEcomaData(reqDto, ecomASummary, genDateLocal,
								invocationId, status, isDelete);

				// Saving ECOMA Data
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Before Saving to GSTR2B Ecoma table, Id, size {}, {}",
							invocationId, ecomaTemp.size());
				}

				ecomaHeaderRepo.saveAll(ecomaTemp);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Saved successfully to GSTR2B Ecoma table, Id, size {}, {}",
							invocationId, ecomaTemp.size());
				}

			}

		}


	}

	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntityList
	 * @param b2bAndB2bASummary
	 */
	private void suppSumB2BAndB2BAParse(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList,
			List<B2BAndB2bASummary> b2bAndB2bASummary, String cpSumm,
			Long invocationId, String status, boolean isDelete, boolean isItcRejected) {
		for (B2BAndB2bASummary b2bA : b2bAndB2bASummary) {
			Gstr2GetGstr2BSuppSmryEntity suppEntity = new Gstr2GetGstr2BSuppSmryEntity();
			suppEntity.setIsItcRejected(isItcRejected);

			setSuppSumHeaderData(checkSum, now, userName, gstin, taxPeriod,
					genDate, version, suppEntity, invocationId, status,
					isDelete);

			String ctin = b2bA.getSuppGstin();
			String SuppName = b2bA.getSuppName();
			BigDecimal igst = b2bA.getIgst();
			BigDecimal cgst = b2bA.getCgst();
			BigDecimal sgst = b2bA.getSgst();
			BigDecimal cess = b2bA.getCess();
			BigDecimal totalTaxableVal = b2bA.getTotalTaxableVal();
			String gstr1FileDate = b2bA.getGstr1FilingDate();
			String gstr1FilePeriod = b2bA.getGstr1FilingPeriod();
			Integer totalDoc = b2bA.getTotalDoc();

			setSuppSumLineItemData(ctin, SuppName, igst, cgst, sgst, cess,
					totalTaxableVal, gstr1FileDate, gstr1FilePeriod, totalDoc,
					null, null, null, suppEntity, cpSumm);

			suppEntityList.add(suppEntity);

		}
	}

	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntityList
	 * @param cdnrAndCdnrASummary
	 */
	private void suppSumCDNRAndCDNRAParse(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList,
			List<CDNRAndCDNRASummary> cdnrAndCdnrASummary, String cpSumm,
			Long invocationId, String status, boolean isDelete, boolean isItcRejected) {
		for (CDNRAndCDNRASummary cdnrAndA : cdnrAndCdnrASummary) {
			Gstr2GetGstr2BSuppSmryEntity suppEntity = new Gstr2GetGstr2BSuppSmryEntity();
			suppEntity.setIsItcRejected(isItcRejected);

			setSuppSumHeaderData(checkSum, now, userName, gstin, taxPeriod,
					genDate, version, suppEntity, invocationId, status,
					isDelete);

			String ctin = cdnrAndA.getSuppGstin();
			String SuppName = cdnrAndA.getSuppName();
			BigDecimal igst = cdnrAndA.getIgst();
			BigDecimal cgst = cdnrAndA.getCgst();
			BigDecimal sgst = cdnrAndA.getSgst();
			BigDecimal cess = cdnrAndA.getCess();
			BigDecimal totalTaxableVal = cdnrAndA.getTotalTaxableVal();
			String gstr1FileDate = cdnrAndA.getGstr1FilingDate();
			String gstr1FilePeriod = cdnrAndA.getGstr1FilingPeriod();
			Integer totalDoc = cdnrAndA.getTotalDoc();
			String noteType = cdnrAndA.getNoteType();

			setSuppSumLineItemData(ctin, SuppName, igst, cgst, sgst, cess,
					totalTaxableVal, gstr1FileDate, gstr1FilePeriod, totalDoc,
					noteType, null, null, suppEntity, cpSumm);

			suppEntityList.add(suppEntity);

		}
	}

	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntityList
	 * @param isdAndIsdASummary
	 */
	private void suppSumIsdAndIsdAParse(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList,
			List<ISDAndISDASummary> isdAndIsdASummary, String cpSumm,
			Long invocationId, String status, boolean isDelete, boolean isItcRejected) {
		for (ISDAndISDASummary cdnrAndA : isdAndIsdASummary) {
			Gstr2GetGstr2BSuppSmryEntity suppEntity = new Gstr2GetGstr2BSuppSmryEntity();
			suppEntity.setIsItcRejected(isItcRejected);

			setSuppSumHeaderData(checkSum, now, userName, gstin, taxPeriod,
					genDate, version, suppEntity, invocationId, status,
					isDelete);

			String ctin = cdnrAndA.getSuppGstin();
			String SuppName = cdnrAndA.getSuppName();
			BigDecimal igst = cdnrAndA.getIgst();
			BigDecimal cgst = cdnrAndA.getCgst();
			BigDecimal sgst = cdnrAndA.getSgst();
			BigDecimal cess = cdnrAndA.getCess();
			BigDecimal totalTaxableVal = null;
			String gstr1FileDate = cdnrAndA.getGstr1FilingDate();
			String gstr1FilePeriod = cdnrAndA.getGstr1FilingPeriod();
			Integer totalDoc = cdnrAndA.getTotalDoc();
			String noteType = cdnrAndA.getIsdDoctype();

			setSuppSumLineItemData(ctin, SuppName, igst, cgst, sgst, cess,
					totalTaxableVal, gstr1FileDate, gstr1FilePeriod, totalDoc,
					noteType, null, null, suppEntity, cpSumm);

			suppEntityList.add(suppEntity);

		}
	}

	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntityList
	 * @param isdAndIsdASummary
	 */
	private void suppSumImpgSezParse(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList,
			List<IMPGSEZSummary> impgSezSummary, String cpSumm,
			Long invocationId, String status, boolean isDelete, boolean isItcRejected) {
		for (IMPGSEZSummary impgSez : impgSezSummary) {
			Gstr2GetGstr2BSuppSmryEntity suppEntity = new Gstr2GetGstr2BSuppSmryEntity();
			suppEntity.setIsItcRejected(isItcRejected);

			setSuppSumHeaderData(checkSum, now, userName, gstin, taxPeriod,
					genDate, version, suppEntity, invocationId, status,
					isDelete);

			String ctin = impgSez.getSuppGstin();
			String SuppName = impgSez.getSuppName();
			BigDecimal igst = impgSez.getIgst();
			BigDecimal cess = impgSez.getCess();
			BigDecimal totalTaxableVal = impgSez.getTotalTaxableVale();
			String portCode = impgSez.getPortCode();
			Integer totalDoc = impgSez.getTotalDoc();

			setSuppSumLineItemData(ctin, SuppName, igst, null, null, cess,
					totalTaxableVal, null, null, totalDoc, null, null, portCode,
					suppEntity, cpSumm);

			suppEntityList.add(suppEntity);
		}
	}

	/**
	 * SuppSummary Item Set
	 * 
	 * @param suppEntity
	 */
	private void setSuppSumLineItemData(String ctin, String SuppName,
			BigDecimal igst, BigDecimal cgst, BigDecimal sgst, BigDecimal cess,
			BigDecimal totalTaxableVal, String gstr1FileDate,
			String gstr1FilePeriod, Integer totalDoc, String invoiceType,
			String orgInvoiceType, String portCode,
			Gstr2GetGstr2BSuppSmryEntity suppEntity, String cpSumm) {

		suppEntity.setSGstin(ctin);
		suppEntity.setSupTradeName(SuppName);
		suppEntity.setIgstAmt(igst);
		suppEntity.setCgstAmt(cgst);
		suppEntity.setSgstAmt(sgst);
		suppEntity.setCessAmt(cess);
		suppEntity.setTaxableValue(totalTaxableVal);
		if (gstr1FileDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");
			LocalDate genDateLocal = LocalDate.parse(gstr1FileDate, formatter);
			LocalDateTime gstr1Date = genDateLocal.atStartOfDay();
			suppEntity.setSupFilingDate(gstr1Date);
		}
		suppEntity.setSupFilingPeriod(gstr1FilePeriod);
		suppEntity.setTotalDocsCount(totalDoc);
		suppEntity.setCpsumm(cpSumm);

	}

	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntity
	 */
	private void setSuppSumHeaderData(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			Gstr2GetGstr2BSuppSmryEntity suppEntity, Long invocationId,
			String status, boolean isDelete) {
		suppEntity.setRGstin(gstin);
		suppEntity.setTaxPeriod(taxPeriod);
		suppEntity.setVersion(version);
		suppEntity.setCheckSum(checkSum);
		suppEntity.setGenDate(genDate);
		suppEntity.setIsDelete(isDelete);
		suppEntity.setCreatedBy(userName);
		suppEntity.setCreatedOn(now);
		suppEntity.setModifiedBy(userName);
		suppEntity.setModifiedOn(now);
		suppEntity.setInvocationId(invocationId);
		suppEntity.setStatus(status);
	}

	/**
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param itcEntity
	 */
	private void setITCSummaryHeaderData(LocalDateTime now, String userName,
			String gstin, String taxPeriod, LocalDateTime genDate,
			String version, String checkSum,
			Gstr2GetGstr2BItcItemEntity itcEntity, Long invocationId,
			String status, boolean isDelete) {
		itcEntity.setRGstin(gstin);
		itcEntity.setTaxPeriod(taxPeriod);
		itcEntity.setVersion(version);
		itcEntity.setGenDate(genDate);
		itcEntity.setCreatedBy(userName);
		itcEntity.setCreatedOn(now);
		itcEntity.setModifiedOn(now);
		itcEntity.setModifiedBy(userName);
		itcEntity.setIsDelete(isDelete);
		itcEntity.setCheckSum(checkSum);
		itcEntity.setItcSummary(APIConstants.GSTR2B_ITC_Available);
		itcEntity.setInvocationId(invocationId);
		itcEntity.setStatus(status);
	}

	/**
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param itcEntity
	 */
	private void setITCSummaryUnAvailHeaderData(LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version, String checkSum,
			Gstr2GetGstr2BItcItemEntity itcEntity, Long invocationId,
			String status, boolean isDelete) {
		itcEntity.setRGstin(gstin);
		itcEntity.setTaxPeriod(taxPeriod);
		itcEntity.setVersion(version);
		itcEntity.setGenDate(genDate);
		itcEntity.setCreatedBy(userName);
		itcEntity.setCreatedOn(now);
		itcEntity.setModifiedOn(now);
		itcEntity.setModifiedBy(userName);
		itcEntity.setIsDelete(isDelete);
		itcEntity.setCheckSum(checkSum);
		itcEntity.setItcSummary(APIConstants.GSTR2B_ITC_UnAvailable);
		itcEntity.setInvocationId(invocationId);
		itcEntity.setStatus(status);
	}
	
	/**
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param itcEntity
	 */
	private void setITCSummaryRejectedHeaderData(LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version, String checkSum,
			Gstr2GetGstr2BItcItemEntity itcEntity, Long invocationId,
			String status, boolean isDelete) {
		itcEntity.setRGstin(gstin);
		itcEntity.setTaxPeriod(taxPeriod);
		itcEntity.setVersion(version);
		itcEntity.setGenDate(genDate);
		itcEntity.setCreatedBy(userName);
		itcEntity.setCreatedOn(now);
		itcEntity.setModifiedOn(now);
		itcEntity.setModifiedBy(userName);
		itcEntity.setIsDelete(isDelete);
		itcEntity.setCheckSum(checkSum);
		itcEntity.setItcSummary(APIConstants.GSTR2B_ITC_Rejected);
		itcEntity.setInvocationId(invocationId);
		itcEntity.setStatus(status);
	}
	
	/**
	 * @param checkSum
	 * @param now
	 * @param userName
	 * @param gstin
	 * @param taxPeriod
	 * @param genDate
	 * @param version
	 * @param suppEntityList
	 * @param ecomAndecomASummary
	 */
	private void suppSumEcomAndEcomAParse(String checkSum, LocalDateTime now,
			String userName, String gstin, String taxPeriod,
			LocalDateTime genDate, String version,
			List<Gstr2GetGstr2BSuppSmryEntity> suppEntityList,
			List<EcomSummary> ecomSummary, String cpSumm,
			Long invocationId, String status, boolean isDelete, boolean isItcRejected) {
		for (EcomSummary ecom : ecomSummary) {
			Gstr2GetGstr2BSuppSmryEntity suppEntity = new Gstr2GetGstr2BSuppSmryEntity();
			suppEntity.setIsItcRejected(isItcRejected);

			setSuppSumHeaderData(checkSum, now, userName, gstin, taxPeriod,
					genDate, version, suppEntity, invocationId, status,
					isDelete);

			String ctin = ecom.getSuppGstin();
			String SuppName = ecom.getSuppName();
			BigDecimal igst = ecom.getIgst();
			BigDecimal cgst = ecom.getCgst();
			BigDecimal sgst = ecom.getSgst();
			BigDecimal cess = ecom.getCess();
			BigDecimal totalTaxableVal = ecom.getTotalTaxableVal();
			String gstr1FileDate = ecom.getGstr1FilingDate();
			String gstr1FilePeriod = ecom.getGstr1FilingPeriod();
			Integer totalDoc = ecom.getTotalDoc();

			setSuppSumLineItemData(ctin, SuppName, igst, cgst, sgst, cess,
					totalTaxableVal, gstr1FileDate, gstr1FilePeriod, totalDoc,
					null, null, null, suppEntity, cpSumm);

			suppEntityList.add(suppEntity);

		}
	}

}