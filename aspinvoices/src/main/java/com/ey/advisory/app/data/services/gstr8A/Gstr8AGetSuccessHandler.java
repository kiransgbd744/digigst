package com.ey.advisory.app.data.services.gstr8A;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BSummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNSummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetSummaryEntity;
import com.ey.advisory.app.data.repositories.client.GstinGetStatusRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8AB2BSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNASummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ACDNSummaryDetailsRepository;
import com.ey.advisory.app.data.repositories.client.Gstr8ASummaryDetailsRepository;
import com.ey.advisory.app.util.Anx1GetBatchUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.APIIdentifiers;
import com.ey.advisory.gstnapi.APIInvokerUtil;
import com.ey.advisory.gstnapi.SuccessHandler;
import com.ey.advisory.gstnapi.SuccessResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Siva.Reddy
 *
 */

@Slf4j
@Service("Gstr8AGetSuccessHandler")
public class Gstr8AGetSuccessHandler implements SuccessHandler {

	@Autowired
	Gstr8ASummaryDetailsRepository gstr8ASummaryDetailsRepo;

	@Autowired
	GstinGetStatusRepository gstinGetStatusRepo;

	@Autowired
	@Qualifier("anx1GetBatchUtil")
	private Anx1GetBatchUtil batchUtil;

	@Autowired
	Gstr8AB2BSummaryDetailsRepository gstr8AB2BSummaryDetailsRepository;

	@Autowired
	Gstr8AB2BASummaryDetailsRepository gstr8AB2BASummaryDetailsRepository;

	@Autowired
	Gstr8ACDNSummaryDetailsRepository gstr8ACDNSummaryDetailsRepository;

	@Autowired
	Gstr8ACDNASummaryDetailsRepository gstr8ACDNASummaryDetailsRepository;

	@Override
	public void handleSuccess(SuccessResult result, String apiParams) {

		Long batchId = null;
		String gstin = null;
		String fy = null;
		String taxPeriod = null;
		try {
			List<Long> resultIds = result.getSuccessIds();
			String ctxParams = result.getCtxParams();
			JsonObject ctxParamsObj = JsonParser.parseString(ctxParams)
					.getAsJsonObject();
			gstin = ctxParamsObj.get("gstin").getAsString();
			fy = ctxParamsObj.get("fy").getAsString();
			String[] fyArr = fy.split("-");
			taxPeriod = "0320" + fyArr[1];
			batchId = ctxParamsObj.get("batchId").getAsLong();
			LocalDateTime updatedOn = LocalDateTime.now();
			String updatedBy = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("GET Call is in Gstr8AGetSuccessHandler"
						+ " with {} reqIds", resultIds.size());
			}
			gstr8AB2BSummaryDetailsRepository.softDelete(gstin, fy, updatedOn,
					updatedBy);
			gstr8AB2BASummaryDetailsRepository.softDelete(gstin, fy, updatedOn,
					updatedBy);
			gstr8ACDNSummaryDetailsRepository.softDelete(gstin, fy, updatedOn,
					updatedBy);
			gstr8ACDNASummaryDetailsRepository.softDelete(gstin, fy, updatedOn,
					updatedBy);
			for (Long id : resultIds) {
				String apiResp = APIInvokerUtil.getResultById(id);

				saveGstr8ADetails(apiResp, gstin, fy);
			}
			boolean isTokenResp = false;
			if (resultIds != null && resultIds.size() > 1) {
				isTokenResp = true;
			}
			batchUtil.updateById(batchId, APIConstants.SUCCESS, null, null,
					isTokenResp);
			gstinGetStatusRepo.updateGetGstnStatus(true, APIConstants.SUCCESS,
					LocalDateTime.now(), null, gstin, taxPeriod,
					APIConstants.GSTR8A, "GET8A");
		} catch (Exception e) {
			LOGGER.error("Exception while handling success "
					+ "Gstr8AGetSuccessHandler", e);
			gstinGetStatusRepo.updateGetGstnStatus(false, APIConstants.FAILED,
					LocalDateTime.now(),
					e.getMessage().length() > 500
							? e.getMessage().substring(0, 499) : e.getMessage(),
					gstin, taxPeriod, APIConstants.GSTR8A, "GET8A");
			batchUtil.updateById(batchId, APIConstants.FAILED, null,
					e.getMessage(), false);
			throw new APIException(e.getMessage());
		}
	}

	private void saveGstr8ADetails(String getGstnData, String gstin,
			String fy) {

		/*Gson gson = new Gson();
		GetGstr8ADetails dto = gson.fromJson(getGstnData,
				GetGstr8ADetails.class);*/
		
		try {
			Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
			
			/* Gson gson = new GsonBuilder()
		             .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
		             .create();*/
			 GetGstr8ADetails  dto = gson.fromJson(getGstnData, GetGstr8ADetails.class);
			int docId = dto.getDocid();
			List<Gstr8AGetSummaryEntity> saveList = new ArrayList<>();
			if (dto.getB2bDetails() != null && !dto.getB2bDetails().isEmpty()) {
				List<Gstr8AInvoiceDetails> b2bDetails = dto.getB2bDetails();
				for (Gstr8AInvoiceDetails obj : b2bDetails) {
					obj.setSection("B2B");
					get8aB2BSummarydetails(dto.getGstin(), obj, saveList, fy,
							docId);
				}
			}
			if (dto.getB2baDetails() != null && !dto.getB2baDetails().isEmpty()) {
				List<Gstr8AInvoiceDetails> b2baDetails = dto.getB2baDetails();
				for (Gstr8AInvoiceDetails obj : b2baDetails) {
					obj.setSection("B2BA");
					get8aB2BASummarydetails(dto.getGstin(), obj, saveList, fy,
							docId);
				}
			}
			if (dto.getCdnDetails() != null && !dto.getCdnDetails().isEmpty()) {
				List<Gstr8AInvoiceDetails> cdnDetails = dto.getCdnDetails();
				for (Gstr8AInvoiceDetails obj : cdnDetails) {
					obj.setSection("CDN");
					get8aCDNSummarydetails(dto.getGstin(), obj, saveList, fy,
							docId);
				}
			}
			if (dto.getCdnaDetails() != null && !dto.getCdnaDetails().isEmpty()) {
				List<Gstr8AInvoiceDetails> cdnaDetails = dto.getCdnaDetails();
				for (Gstr8AInvoiceDetails obj : cdnaDetails) {
					obj.setSection("CDNA");
					get8aCDNASummarydetails(dto.getGstin(), obj, saveList, fy,
							docId);
				}
			}
		} catch (Exception ex) {
			
			String msg = String.format(
					"Exception while invoking getGstr8A get call Details '%s',"
							+ " GSTIN - '%s' and fy - '%s'",
					APIIdentifiers.GSTR8A_GETDETAILS, gstin, fy);
			LOGGER.error(msg, ex);
			throw ex;
	}
		
	}

	public LocalDate getOriginInvoiceDate(String section, LocalDate invoiceDate) {

		LocalDate oriInvoiceDate = null;

		if ("CDNA".equalsIgnoreCase(section)) {

			oriInvoiceDate = invoiceDate;
		}

		if (oriInvoiceDate == null)
			return null;


		return oriInvoiceDate;
	}

	public String getOriginInvoiceNum(String section, String invoiceNum) {

		String oriInvoiceNum = "";

		if ("CDNA".equalsIgnoreCase(section)) {

			oriInvoiceNum = invoiceNum;
		}

		return oriInvoiceNum;
	}

	public LocalDate getOriginDocDate(String section, GSTR8ADocDetails doc) {

		LocalDate oriDocDate = null;

		if ("CDN".equalsIgnoreCase(section)) {
			oriDocDate = doc.getInvoiceDate();
		} else if ("B2BA".equalsIgnoreCase(section)) {
			oriDocDate = doc.getOrigiInvoiceDate();
		} else if ("CDNA".equalsIgnoreCase(section)) {
			oriDocDate = doc.getOrigiNoteDate();
		}

		if (oriDocDate == null)
			return null;


		return oriDocDate;
	}

	public String getOriginNoteType(String section, String oriNoteType) {

		String originNoteType = "";

		if ("CDNA".equalsIgnoreCase(section)) {

			if ("C".equalsIgnoreCase(oriNoteType)) {
				originNoteType = "CR";
			} else if ("D".equalsIgnoreCase(oriNoteType)) {
				originNoteType = "DR";
			}
		}

		return originNoteType;
	}

	public String getDocNum(String section, String invoiceNum,
			String noteNumber) {

		String docNum = "";

		if ("B2B".equalsIgnoreCase(section) || "B2BA".equalsIgnoreCase(section))
			docNum = invoiceNum;
		else if ("CDN".equalsIgnoreCase(section)
				|| "CDNA".equalsIgnoreCase(section))
			docNum = noteNumber;

		return docNum;
	}

	public static String getDocType(String section, GSTR8ADocDetails doc) {

		String docType = "";

		switch (section.toUpperCase()) {

		case "B2B":
			docType = "INV";
			break;

		case "B2BA":
			docType = "RNV";
			break;

		case "CDN":
			if (doc.getNoteType().equalsIgnoreCase("C"))
				docType = "CR";
			else
				docType = "DR";
			break;

		case "CDNA":
			if (doc.getNoteType().equalsIgnoreCase("C"))
				docType = "RCR";
			else
				docType = "RDR";
			break;

		default:
			break;

		}

		return docType;
	}

	public void get8aB2BSummarydetails(String gstin, Gstr8AInvoiceDetails obj,
			List<Gstr8AGetSummaryEntity> list, String fy, int docId) {

		List<Gstr8AGetB2BSummaryEntity> b2bList = new ArrayList<>();

		List<GSTR8ADocDetails> docList = obj.getDocDetails();

		for (GSTR8ADocDetails doc : docList) {

			Gstr8AGetB2BSummaryEntity entity = new Gstr8AGetB2BSummaryEntity();

			entity.setCgstin(gstin);
			entity.setSgstin(obj.getStin() != null ? obj.getStin() : null);
			entity.setRetPeriod(obj.getReturnPeriod() != null ? obj.getReturnPeriod() : null);
			entity.setFinYear(fy != null ? fy : null);
			entity.setInvType(doc.getInvoiceType() != null ? doc.getInvoiceType() : null);
			entity.setInvDate(doc.getInvoiceDate() != null ? doc.getInvoiceDate() : null);
			entity.setInvNum(doc.getInvoiceNum() != null ? doc.getInvoiceNum() : null);
			entity.setPos(doc.getPos() != null ? doc.getPos() : null);
			entity.setReverseCharge(doc.getReverseCharge() != null ? doc.getReverseCharge() : null);
			entity.setInvValue(doc.getVal() != null ? doc.getVal() : null);
			entity.setTaxPayable(doc.getTaxableValue() != null ? doc.getTaxableValue() : null);
			entity.setIgst(doc.getIamt() != null ? doc.getIamt() : null);
			entity.setCgst(doc.getCamt() != null ? doc.getCamt() : null);
			entity.setSgst(doc.getSamt() != null ? doc.getSamt() : null);
			entity.setCess(doc.getCsamt() != null ? doc.getCsamt() : null);
			entity.setEligibleItc(doc.getIsEligible() != null ? doc.getIsEligible() : null);
			entity.setReason(doc.getReason() != null ? doc.getReason() : null);
			entity.setIsDelete(false);
			entity.setCreatedOn(LocalDateTime.now());//date and time we need to show

			String userName = (SecurityContext.getUser() != null 
			        && SecurityContext.getUser().getUserPrincipalName() != null) 
			        ? SecurityContext.getUser().getUserPrincipalName() 
			        : "SYSTEM";
			entity.setCreatedBy(userName);

			LocalDate invDateStr = obj.getFilingDate() != null ? obj.getFilingDate() : null;
			entity.setFilingDate(invDateStr);

			entity.setRt(doc.getRt() != null ? doc.getRt() : null);

			b2bList.add(entity);
		}
		gstr8AB2BSummaryDetailsRepository.saveAll(b2bList);
	}

	public void get8aB2BASummarydetails(String gstin, Gstr8AInvoiceDetails obj,
			List<Gstr8AGetSummaryEntity> list, String fy, int docId) {

		List<Gstr8AGetB2BASummaryEntity> b2baList = new ArrayList<>();

		List<GSTR8ADocDetails> docList = obj.getDocDetails();

		for (GSTR8ADocDetails doc : docList) {

			Gstr8AGetB2BASummaryEntity entity = new Gstr8AGetB2BASummaryEntity();

			entity.setCgstin(gstin != null ? gstin : null);
			entity.setSgstin(obj.getStin() != null ? obj.getStin() : null);
			entity.setRetPeriod(obj.getReturnPeriod() != null ? obj.getReturnPeriod() : null);
			entity.setFinYear(fy != null ? fy : null);
			entity.setInvType(doc.getInvoiceType() != null ? doc.getInvoiceType() : null);
			entity.setInvNum(doc.getInvoiceNum() != null ? doc.getInvoiceNum() : null);
			entity.setInvDate(doc.getInvoiceDate() != null ? doc.getInvoiceDate() : null);
			entity.setOriginalInvNum(doc.getOrigiInvoiceNo() != null ? doc.getOrigiInvoiceNo() : null);
			entity.setOriginalInvDate(doc.getOrigiInvoiceDate() != null ? doc.getOrigiInvoiceDate() : null);
			entity.setPos(doc.getPos() != null ? doc.getPos() : null);
			entity.setReverseCharge(doc.getReverseCharge() != null ? doc.getReverseCharge() : null);
			entity.setInvValue(doc.getVal() != null ? doc.getVal() : null);
			entity.setTaxPayable(doc.getTaxableValue() != null ? doc.getTaxableValue() : null);
			entity.setIgst(doc.getIamt() != null ? doc.getIamt() : null);
			entity.setCgst(doc.getCamt() != null ? doc.getCamt() : null);
			entity.setSgst(doc.getSamt() != null ? doc.getSamt() : null);
			entity.setCess(doc.getCsamt() != null ? doc.getCsamt() : null);
			entity.setEligibleItc(doc.getIsEligible() != null ? doc.getIsEligible() : null);
			entity.setReason(doc.getReason() != null ? doc.getReason() : null);
			entity.setIsDelete(false);
			entity.setCreatedOn(LocalDateTime.now());

			String userName = (SecurityContext.getUser() != null 
			        && SecurityContext.getUser().getUserPrincipalName() != null) 
			        ? SecurityContext.getUser().getUserPrincipalName() 
			        : "SYSTEM";
			entity.setCreatedBy(userName);

			LocalDate invDateStr = obj.getFilingDate() != null ? obj.getFilingDate() : null;
			entity.setFilingDate(invDateStr);

			entity.setTx(doc.getRt() != null ? doc.getRt() : null);

			b2baList.add(entity);
		}
		gstr8AB2BASummaryDetailsRepository.saveAll(b2baList);
	}

	public void get8aCDNSummarydetails(String gstin, Gstr8AInvoiceDetails obj,
			List<Gstr8AGetSummaryEntity> list, String fy, int docId) {

		List<Gstr8AGetCDNSummaryEntity> cdnList = new ArrayList<>();

		List<GSTR8ADocDetails> docList = obj.getDocDetails();

		for (GSTR8ADocDetails doc : docList) {

			Gstr8AGetCDNSummaryEntity entity = new Gstr8AGetCDNSummaryEntity();

			entity.setCgstin(gstin != null ? gstin : null);
			entity.setSgstin(obj.getStin() != null ? obj.getStin() : null);
			entity.setRetPeriod(obj.getReturnPeriod() != null ? obj.getReturnPeriod() : null);
			entity.setFinYear(fy != null ? fy : null);

			entity.setInvNum(doc.getInvoiceNum() != null ? doc.getInvoiceNum() : null);
			entity.setInvDate(doc.getInvoiceDate() != null ? doc.getInvoiceDate() : null);
			entity.setPos(doc.getPos() != null ? doc.getPos() : null);
			entity.setReverseCharge(doc.getReverseCharge() != null ? doc.getReverseCharge() : null);
			entity.setInvValue(doc.getVal() != null ? doc.getVal() : null);
			entity.setTaxPayable(doc.getTaxableValue() != null ? doc.getTaxableValue() : null);
			entity.setIgst(doc.getIamt() != null ? doc.getIamt() : null);
			entity.setCgst(doc.getCamt() != null ? doc.getCamt() : null);
			entity.setSgst(doc.getSamt() != null ? doc.getSamt() : null);
			entity.setCess(doc.getCsamt() != null ? doc.getCsamt() : null);
			entity.setEligibleItc(doc.getIsEligible() != null ? doc.getIsEligible() : null);
			entity.setReason(doc.getReason() != null ? doc.getReason() : null);

			entity.setIsDelete(false);
			entity.setCreatedOn(LocalDateTime.now());

			String userName = (SecurityContext.getUser() != null 
			        && SecurityContext.getUser().getUserPrincipalName() != null) 
			        ? SecurityContext.getUser().getUserPrincipalName() 
			        : "SYSTEM";
			entity.setCreatedBy(userName);

			entity.setFilingDate(obj.getFilingDate() != null ? obj.getFilingDate() : null);

			entity.setNoteType(doc.getNoteType() != null ? doc.getNoteType() : null);
			entity.setNoteNumber(doc.getNoteNumber() != null ? doc.getNoteNumber() : null);
			entity.setNoteDate(doc.getNoteDate() != null ? doc.getNoteDate() : null);
			entity.setRt(doc.getRt() != null ? doc.getRt() : null);

			cdnList.add(entity);
		}
		gstr8ACDNSummaryDetailsRepository.saveAll(cdnList);
	}

	public void get8aCDNASummarydetails(String gstin, Gstr8AInvoiceDetails obj,
			List<Gstr8AGetSummaryEntity> list, String fy, int docId) {

		List<Gstr8AGetCDNASummaryEntity> cdnaList = new ArrayList<>();

		List<GSTR8ADocDetails> docList = obj.getDocDetails();

		for (GSTR8ADocDetails doc : docList) {

			Gstr8AGetCDNASummaryEntity entity = new Gstr8AGetCDNASummaryEntity();
			entity.setCgstin(gstin != null ? gstin : null);
			entity.setSgstin(obj.getStin() != null ? obj.getStin() : null);
			entity.setRetPeriod(obj.getReturnPeriod() != null ? obj.getReturnPeriod() : null);
			entity.setFinYear(fy != null ? fy : null);
			entity.setOrigiNoteType(doc.getOrigiNoteType() != null ? doc.getOrigiNoteType() : null);
			entity.setOrigiNoteDate(doc.getOrigiNoteDate() != null ? doc.getOrigiNoteDate() : null);
			entity.setOrigiNoteNum(doc.getOrigiNoteNum() != null ? doc.getOrigiNoteNum() : null);
			entity.setInvNum(doc.getInvoiceNum() != null ? doc.getInvoiceNum() : null);
			entity.setInvDate(doc.getInvoiceDate() != null ? doc.getInvoiceDate() : null);
			entity.setPos(doc.getPos() != null ? doc.getPos() : null);
			entity.setReverseCharge(doc.getReverseCharge() != null ? doc.getReverseCharge() : null);
			entity.setInvValue(doc.getVal() != null ? doc.getVal() : null);
			entity.setTaxPayable(doc.getTaxableValue() != null ? doc.getTaxableValue() : null);
			entity.setIgst(doc.getIamt() != null ? doc.getIamt() : null);
			entity.setCgst(doc.getCamt() != null ? doc.getCamt() : null);
			entity.setSgst(doc.getSamt() != null ? doc.getSamt() : null);
			entity.setCess(doc.getCsamt() != null ? doc.getCsamt() : null);
			entity.setEligibleItc(doc.getIsEligible() != null ? doc.getIsEligible() : null);
			entity.setReason(doc.getReason() != null ? doc.getReason() : null);
			entity.setIsDelete(false);
			entity.setCreatedOn(LocalDateTime.now());

			String userName = (SecurityContext.getUser() != null 
			        && SecurityContext.getUser().getUserPrincipalName() != null) 
			        ? SecurityContext.getUser().getUserPrincipalName() 
			        : "SYSTEM";
			entity.setCreatedBy(userName);

			entity.setFilingDate(obj.getFilingDate() != null ? obj.getFilingDate() : null);

			entity.setNoteType(doc.getNoteType() != null ? doc.getNoteType() : null);
			entity.setNoteNumber(doc.getNoteNumber() != null ? doc.getNoteNumber() : null);
			entity.setNoteDate(doc.getNoteDate() != null ? doc.getNoteDate() : null);
			entity.setRt(doc.getRt() != null ? doc.getRt() : null);

			cdnaList.add(entity);
		}
		gstr8ACDNASummaryDetailsRepository.saveAll(cdnaList);
	}

}
