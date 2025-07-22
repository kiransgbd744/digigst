package com.ey.advisory.app.data.services.common;

import java.sql.Clob;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.GstnGetStatusEntity;
import com.ey.advisory.app.data.entities.client.GstnUserRequestEntity;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.GstnUserRequestRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ARRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1ATARepository;
import com.ey.advisory.app.data.repositories.client.Gstr1B2CSRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1UserInputHsnSacRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component
@Slf4j
public class GstnCommonServiceUtil {

	@Autowired
	GstnUserRequestRepository gstnUserRequestRepo;

	@Autowired
	@Qualifier("GstinGetStatusRepository")
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docRepository;

	@Autowired
	Gstr1NilNonExtSummaryRepository nilNonRepo;

	@Autowired
	Gstr1B2CSRepository b2csRepo;

	@Autowired
	Gstr1UserInputHsnSacRepository hsnRepo;

	@Autowired
	Gstr1ARRepository arRepo;

	@Autowired
	Gstr1ATARepository ataRepo;

	@Autowired
	private Gstr1InvoiceRepository gstr1InvoiceRepository;

	public void saveOrUpdateGstnStatus(String gstin, String taxPeriod,
			String invoiceType, String returnType) {

		GstnGetStatusEntity gstnGetApiStatus = null;

		gstnGetApiStatus = gstinGetStatusRepo
				.findByGstinAndTaxPeriodAndReturnTypeAndSection(gstin,
						taxPeriod, returnType, invoiceType);

		Integer derivedTaxPeriod = Integer.valueOf(
				taxPeriod.substring(2).concat(taxPeriod.substring(0, 2)));

		if (gstnGetApiStatus == null) {
			gstnGetApiStatus = new GstnGetStatusEntity();
			gstnGetApiStatus.setGstin(gstin);
			gstnGetApiStatus.setReturnType(returnType);
			gstnGetApiStatus.setTaxPeriod(taxPeriod);
			gstnGetApiStatus.setSection(invoiceType);
			gstnGetApiStatus.setCsvGenerationFlag(false);
			gstnGetApiStatus.setDerivedTaxPeriod(derivedTaxPeriod);
			gstnGetApiStatus.setCreatedOn(LocalDateTime.now());
			gstnGetApiStatus.setStatus("INITIATED");
			gstinGetStatusRepo.save(gstnGetApiStatus);
		} else {
			gstinGetStatusRepo.updateGetGstnStatus(false, "INITIATED",
					LocalDateTime.now(), null, gstin, taxPeriod, returnType,
					invoiceType);
		}

	}

	public void saveOrUpdateGstnUserRequest(String gstin, String taxPeriod,
			String getGstnData, String returnType) {

		GstnUserRequestEntity gstnUserRequestEntity = gstnUserRequestRepo
				.findByGstinAndTaxPeriodAndReturnType(gstin, taxPeriod,
						returnType);

		Clob reqClob;
		try {
			reqClob = new javax.sql.rowset.serial.SerialClob(
					getGstnData.toCharArray());

			if (gstnUserRequestEntity == null) {

				gstnUserRequestEntity = new GstnUserRequestEntity();

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser()
								.getUserPrincipalName() != null)
										? SecurityContext.getUser()
												.getUserPrincipalName()
										: "SYSTEM";

				gstnUserRequestEntity.setGstin(gstin);
				gstnUserRequestEntity.setTaxPeriod(taxPeriod);
				gstnUserRequestEntity.setReturnType(returnType);
				gstnUserRequestEntity.setRequestType("GET");
				gstnUserRequestEntity.setRequestStatus(1);
				gstnUserRequestEntity.setCreatedBy(userName);
				gstnUserRequestEntity.setCreatedOn(LocalDateTime.now());
				gstnUserRequestEntity.setDelete(false);
				gstnUserRequestEntity.setGetResponsePayload(reqClob);
				gstnUserRequestEntity.setDerivedRetPeriod(
						Integer.valueOf(taxPeriod.substring(2)
								.concat(taxPeriod.substring(0, 2))));

				gstnUserRequestRepo.save(gstnUserRequestEntity);

			} else {
				gstnUserRequestRepo.updateGstnResponse(reqClob, 1, gstin,
						taxPeriod, returnType, LocalDateTime.now());

			}
		} catch (Exception e) {
			String msg = "Exception occured while persisting gstnUserRequest data";
			LOGGER.error(msg, e);

			throw new AppException(msg, e);
		}
	}

	public boolean isNilRetApp(String gstin, String taxPeriod) {

		int docHeaderCount = docRepository.isInvDataAvaCount(gstin, taxPeriod);
		if (docHeaderCount > 0) {
			String msg = String.format(
					"Data Available is is in Doc Header table for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}
		int isNilDataAvai = nilNonRepo.isNilDataAvail(gstin, taxPeriod);
		if (isNilDataAvai > 0) {
			String msg = String.format(
					"Data Available is in NIL NON EXPT for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}
		int isB2CsDataAvai = b2csRepo.isB2CsDataAvail(gstin, taxPeriod);
		if (isB2CsDataAvai > 0) {
			String msg = String.format(
					"Data Available is in B2CS for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}

		int isHsnDataAvail = hsnRepo.isHsnDataAvail(gstin, taxPeriod);
		if (isHsnDataAvail > 0) {
			String msg = String.format(
					"Data Available is in HSN Userinput table for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}
		int isARDataAvail = arRepo.isARDtlsDataAvail(gstin, taxPeriod);
		if (isARDataAvail > 0) {
			String msg = String.format(
					"Data Available is in AR table for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}
		int isAdvAdjDataAvail = ataRepo.isAdAjuDataAvail(gstin, taxPeriod);

		if (isAdvAdjDataAvail > 0) {
			String msg = String.format(
					"Data Available is in Adv Adj table for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}

		int isInvSeriDataAvail = gstr1InvoiceRepository
				.isInvSeriDataAvail(gstin, taxPeriod);
		if (isInvSeriDataAvail > 0) {
			String msg = String.format(
					"Data Available is in Invoice Series table for GSTIN %s and TaxPeriod %s",
					gstin, taxPeriod);
			LOGGER.error(msg);
			return false;
		}
		return true;

	}

}
