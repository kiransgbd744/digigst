/**
 * 
 */
package com.ey.advisory.app.processors.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.repositories.client.DocRepositoryGstr1A;
import com.ey.advisory.app.data.gstr1A.repositories.client.Gstr1AInvoiceRepository;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1InvoiceRepository;
import com.ey.advisory.app.docs.dto.SaveToGstnReqDto;
import com.ey.advisory.app.services.jobs.anx1.ClientResetUtil;
import com.ey.advisory.app.services.savetogstn.jobs.gstr1.SaveToGstnEventStatus;
import com.ey.advisory.app.util.GstnUserRequestUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.dto.Gstr1SaveToGstnReqDto;
import com.ey.advisory.repositories.client.Gstr1BatchRepository;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Slf4j
@Service("Gstr1SaveToGstnResetHandler")
public class Gstr1SaveToGstnResetHandler {

	@Autowired
	private AsyncJobsService asyncJobsService;

	@Autowired
	private GstnUserRequestUtil gstnUserRequestUtil;

	@Autowired
	@Qualifier("saveToGstnEventStatusImpl")
	private SaveToGstnEventStatus saveToGstnEventStatus;

	@Autowired
	private DocRepository docRepo;
	
	@Autowired
	private DocRepositoryGstr1A docRepoGstr1A;

	@Autowired
	@Qualifier("batchSaveStatusRepository")
	private Gstr1BatchRepository batchRepo;

	@Autowired
	private ClientResetUtil clientResetUtil;

	@Autowired
	@Qualifier("Gstr1InvoiceRepository")
	private Gstr1InvoiceRepository gstr1DocIssueProcRepo;
	
	@Autowired
	@Qualifier("Gstr1AInvoiceRepository")
	private Gstr1AInvoiceRepository gstr1ADocIssueProcRepo;

	private static final List<String> SUMMARY_SECTIONS = ImmutableList.of(
			APIConstants.AT, APIConstants.ATA, APIConstants.TXP,
			APIConstants.TXPA, APIConstants.B2CS, APIConstants.B2CSA,
			APIConstants.NIL, APIConstants.HSNSUM, APIConstants.DOCISS,
			GSTConstants.SUP_ECOM);

	/*
	 * private static final List<String> ALL_SECTIONS = ImmutableList.of(
	 * APIConstants.B2B, APIConstants.B2BA, APIConstants.B2CL,
	 * APIConstants.B2CLA, APIConstants.EXP, APIConstants.EXPA,
	 * APIConstants.CDNR, APIConstants.CDNRA, APIConstants.CDNUR,
	 * APIConstants.CDNURA, APIConstants.AT, APIConstants.ATA, APIConstants.TXP,
	 * APIConstants.TXPA, APIConstants.B2CS, APIConstants.B2CSA,
	 * APIConstants.NIL, APIConstants.HSNSUM, APIConstants.DOCISS);
	 */

	private static final List<String> EINV_SECTIONS = ImmutableList.of(
			APIConstants.B2B, APIConstants.EXP, APIConstants.CDNR,
			APIConstants.CDNUR

	);

	@Transactional(value = "clientTransactionManager")
	public void createSectionWiseJobs(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<String> sections = dto.getTableSections();

		// resetAspTableData(pair, dto);

		/*
		 * if (dto.getIsResetSave()) { //sections.forEach(section -> { for
		 * (String section : sections) {
		 * 
		 * if(SUMMARY_SECTIONS.contains(section.toLowerCase())) { //Summary
		 * sections are not required update/reset List<Object[]> lastObj =
		 * batchRepo.selectMaxSumryRecord( pair.getValue0(), pair.getValue1(),
		 * APIConstants.GSTR1.toUpperCase(), section.toUpperCase()); if
		 * (CollectionUtils.isNotEmpty(lastObj)) { Object[] obj =
		 * lastObj.get(0); if (obj != null && obj[0] != null) { Long gstnBatchId
		 * = obj[0] != null ? Long.parseLong(obj[0].toString()) : null;
		 * 
		 * batchRepo.resetMaxIdsOfOldSectionToZero( pair.getValue0(),
		 * pair.getValue1(), section.toUpperCase(),
		 * APIConstants.GSTR1.toUpperCase(), gstnBatchId); } } continue; }
		 * ArrayList<String> bifurcations = new ArrayList<String>(); if
		 * (APIConstants.EXPA.equalsIgnoreCase(section)) {
		 * bifurcations.add(GSTConstants.GSTR1_EXPA.toUpperCase()); } else if
		 * (APIConstants.EXP.equalsIgnoreCase(section)) {
		 * bifurcations.add(GSTConstants.GSTR1_EXP.toUpperCase()); } else if
		 * (APIConstants.CDNUR.equalsIgnoreCase(section)) {
		 * bifurcations.add(GSTConstants.CDNUR.toUpperCase());
		 * bifurcations.add(GSTConstants.CDNUR_B2CL.toUpperCase());
		 * bifurcations.add(GSTConstants.CDNUR_EXPORTS.toUpperCase()); } else {
		 * bifurcations.add(section.toUpperCase()); }
		 * docRepo.resetSaveGstr1AuditColumns(pair.getValue0(),
		 * pair.getValue1(), bifurcations); }//); }
		 */
		// 21-01-2021 Commenting this part because sections selection mandate
		// for SaveUnsavedData also
		/*
		 * else { sections.addAll(ALL_SECTIONS); }
		 */
		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
				pair.getValue0(), pair.getValue1(), APIConstants.SAVE,
				APIConstants.GSTR1.toUpperCase(), groupCode, userName,
				dto.getIsNilUserInput(), dto.getIsHsnUserInput(), false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		sections.forEach(section -> {
			if (section.equals(APIConstants.ECOMSUPSUM)
					|| section.equals(APIConstants.ECOMSUP)) {
				SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(
						pair.getValue0(), pair.getValue1(), userRequestId,
						section, null);
				String sectionJson = GsonUtil.gsonWithDisableHtmlEscpaing()
						.toJson(sectionDto);
				createAsyncJob(groupCode, JobConstants.GSTR1_SAVETOGSTN,
						userName, sectionJson);

			} else {
				SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(
						pair.getValue0(), pair.getValue1(), userRequestId,
						section.toLowerCase(), null);
				String sectionJson = gson.toJson(sectionDto);
				createAsyncJob(groupCode, JobConstants.GSTR1_SAVETOGSTN,
						userName, sectionJson);

			}
		});
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10,
				groupCode);

	}

	@Transactional(value = "clientTransactionManager")
	public void createSectionWiseJobsForGstr1A(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<String> sections = dto.getTableSections();

		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
				pair.getValue0(), pair.getValue1(), APIConstants.SAVE,
				APIConstants.GSTR1A.toUpperCase(), groupCode, userName,
				dto.getIsNilUserInput(), dto.getIsHsnUserInput(), false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		sections.forEach(section -> {
			if (section.equals(APIConstants.ECOMSUPSUM)
					|| section.equals(APIConstants.ECOMSUP)) {
				SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(
						pair.getValue0(), pair.getValue1(), userRequestId,
						section, null);
				String sectionJson = GsonUtil.gsonWithDisableHtmlEscpaing()
						.toJson(sectionDto);
				createAsyncJob(groupCode, JobConstants.GSTR1A_SAVETOGSTN,
						userName, sectionJson);

			} else {
				SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(
						pair.getValue0(), pair.getValue1(), userRequestId,
						section.toLowerCase(), null);
				String sectionJson = gson.toJson(sectionDto);
				createAsyncJob(groupCode, JobConstants.GSTR1A_SAVETOGSTN,
						userName, sectionJson);

			}
		});
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10,
				groupCode);

	}
	
	@Transactional(value = "clientTransactionManager")
	public void createSectionWiseJobs(String gstin, String retPeriod,
			String section, List<Long> docIds, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();

		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(gstin,
				retPeriod, APIConstants.SAVE, APIConstants.GSTR1.toUpperCase(),
				groupCode, userName, false, false, false);
		SaveToGstnReqDto sectionDto = null;
		if (section.equals(APIConstants.ECOMSUP)) {
			sectionDto = new SaveToGstnReqDto(gstin, retPeriod, userRequestId,
					section, docIds);
			String sectionJson = GsonUtil.gsonWithDisableHtmlEscpaing()
					.toJson(sectionDto);
			createAsyncJob(groupCode, JobConstants.GSTR1_SAVETOGSTN, userName,
					sectionJson);
		} else {
			sectionDto = new SaveToGstnReqDto(gstin, retPeriod, userRequestId,
					section.toLowerCase(), docIds);
			String sectionJson = GsonUtil.newSAPGsonInstance()
					.toJson(sectionDto);
			createAsyncJob(groupCode, JobConstants.GSTR1_SAVETOGSTN, userName,
					sectionJson);
		}
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(retPeriod, gstin, 10, groupCode, section.toUpperCase());

	}

	/**
	 * 
	 */
	private void createAsyncJob(String groupCode, String jobCateg,
			String userName, String eachJson) {

		asyncJobsService.createJob(groupCode, jobCateg, eachJson, userName,
				JobConstants.PRIORITY, JobConstants.PARENT_JOB_ID,
				JobConstants.SCHEDULE_AFTER_IN_MINS);

	}

	public void resetAspTableData(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType) {

		List<String> sections = dto.getTableSections();
		if (sections != null && !Collections.isEmpty(sections)) {
			// createResetEntries(pair, dto, resetType);
			clientResetUtil.createResetEntries(pair, dto, resetType,
					APIConstants.GSTR1.toUpperCase());
		}
		// if (dto.getIsResetSave()) {
		for (String section : sections) {
			if (APIConstants.DOCISS.equalsIgnoreCase(section)) {
				gstr1DocIssueProcRepo.resetSaveGstr1AuditColumns(
						pair.getValue0(), pair.getValue1());
				continue;
			} else if (SUMMARY_SECTIONS.contains(section.toLowerCase())) {
				// Summary sections are not required update/reset
				List<Object[]> lastObj = batchRepo.selectMaxSumryRecord(
						pair.getValue0(), pair.getValue1(),
						APIConstants.GSTR1.toUpperCase(),
						section.toUpperCase());
				if (CollectionUtils.isNotEmpty(lastObj)) {
					Object[] obj = lastObj.get(0);
					if (obj != null && obj[0] != null) {
						Long gstnBatchId = obj[0] != null
								? Long.parseLong(obj[0].toString()) : null;

						batchRepo.resetMaxIdsOfOldSectionToZero(
								pair.getValue0(), pair.getValue1(),
								section.toUpperCase(),
								APIConstants.GSTR1.toUpperCase(), gstnBatchId);
					}
				}
				continue;
			}
			ArrayList<String> bifurcations = new ArrayList<String>();
			if (APIConstants.EXPA.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.GSTR1_EXPA.toUpperCase());
			} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.GSTR1_EXP.toUpperCase());
			} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.CDNUR.toUpperCase());
				bifurcations.add(GSTConstants.CDNUR_B2CL.toUpperCase());
				bifurcations.add(GSTConstants.CDNUR_EXPORTS.toUpperCase());
			} else if (GSTConstants.SUP_ECOM.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.SUP_ECOM);
			} else {
				bifurcations.add(section.toUpperCase());
			}

			if (sections.contains(APIConstants.ECOMSUP)) {
				LOGGER.debug("Section {}", sections);
				docRepo.resetSaveGstr1EcomTrans(pair.getValue0(),
						pair.getValue1(), GSTConstants.ECOM_SUP);
			} else {
				docRepo.resetSaveGstr1AuditColumns(pair.getValue0(),
						pair.getValue1(), bifurcations);
			}
			if (sections.contains(GSTConstants.SUP_ECOM)) {
				docRepo.resetSaveGstr1SupEcom(pair.getValue0(),
						pair.getValue1(), GSTConstants.SUP_ECOM);
			}
			if (sections.contains(APIConstants.ECOMSUPSUM)) {
				docRepo.resetSaveGstr1EcomSupSum(pair.getValue0(),
						pair.getValue1(), GSTConstants.ECOM_SUP);
			}
		}

	}

	public void resetNonRespondedAspTableData(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType) {

		List<String> sections = dto.getTableSections();
		if (sections != null && !Collections.isEmpty(sections)) {
			// createResetEntries(pair, dto, resetType);
			clientResetUtil.createResetEntries(pair, dto, resetType,
					APIConstants.GSTR1.toUpperCase());
		}
		for (String section : sections) {
			if (EINV_SECTIONS.contains(section.toLowerCase())) {
				ArrayList<String> bifurcations = new ArrayList<String>();
				if (APIConstants.EXPA.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.GSTR1_EXPA.toUpperCase());
				} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.GSTR1_EXP.toUpperCase());
				} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.CDNUR.toUpperCase());
					bifurcations.add(GSTConstants.CDNUR_B2CL.toUpperCase());
					bifurcations.add(GSTConstants.CDNUR_EXPORTS.toUpperCase());
				} else {
					bifurcations.add(section.toUpperCase());
				}

				docRepo.resetSaveGstr1AuditColumnsForNonRespondedData(
						pair.getValue0(), pair.getValue1(), bifurcations);
			}
		}
	}

	@Transactional(value = "clientTransactionManager")
	public void createDeleteGstnJob(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		List<String> sections = dto.getTableSections();

		sections.addAll(EINV_SECTIONS);
		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
				pair.getValue0(), pair.getValue1(),
				APIConstants.DELETE_FILE_UPLOAD,
				APIConstants.GSTR1.toUpperCase(), groupCode, userName,
				dto.getIsNilUserInput(), dto.getIsHsnUserInput(), false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		sections.forEach(section -> {
			SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(pair.getValue0(),
					pair.getValue1(), userRequestId, section.toLowerCase(),
					null);
			String sectionJson = gson.toJson(sectionDto);
			createAsyncJob(groupCode, JobConstants.GSTR1_DELETE_GSTN_DATA,
					userName, sectionJson);
		});
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10,
				groupCode);

	}

	@Transactional(value = "clientTransactionManager")
	public void createResetGstnJob(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		// List<String> sections = dto.getTableSections();

		// sections.addAll(EINV_SECTIONS);
		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
				pair.getValue0(), pair.getValue1(),
				APIConstants.DELETE_FULL_DATA, APIConstants.GSTR1.toUpperCase(),
				groupCode, userName, dto.getIsNilUserInput(),
				dto.getIsHsnUserInput(), false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		// sections.forEach(section -> {
		SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(pair.getValue0(),
				pair.getValue1(), userRequestId, null, null);
		String sectionJson = gson.toJson(sectionDto);
		// });
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10,
				groupCode);
		createAsyncJob(groupCode, JobConstants.GSTR1_RESET_GSTN_DATA, userName,
				sectionJson);

	}
	
	@Transactional(value = "clientTransactionManager")
	public void createResetGstr1AGstnJob(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String groupCode) {

		String userName = SecurityContext.getUser().getUserPrincipalName();
		// List<String> sections = dto.getTableSections();

		// sections.addAll(EINV_SECTIONS);
		Long userRequestId = gstnUserRequestUtil.createGstnUserRequest(
				pair.getValue0(), pair.getValue1(),
				APIConstants.DELETE_FULL_DATA, APIConstants.GSTR1A.toUpperCase(),
				groupCode, userName, dto.getIsNilUserInput(),
				dto.getIsHsnUserInput(), false);
		Gson gson = GsonUtil.newSAPGsonInstance();
		// sections.forEach(section -> {
		SaveToGstnReqDto sectionDto = new SaveToGstnReqDto(pair.getValue0(),
				pair.getValue1(), userRequestId, null, null);
		String sectionJson = gson.toJson(sectionDto);
		// });
		// status code 10 says status as USER REQUEST INITIATED
		saveToGstnEventStatus.EventEntry(pair.getValue1(), pair.getValue0(), 10,
				groupCode);
		createAsyncJob(groupCode, JobConstants.GSTR1A_RESET_GSTN_DATA, userName,
				sectionJson);

	}
	
	public void resetGstr1AAspTableData(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType) {

		List<String> sections = dto.getTableSections();
		if (sections != null && !Collections.isEmpty(sections)) {
			// createResetEntries(pair, dto, resetType);
			clientResetUtil.createResetEntries(pair, dto, resetType,
					APIConstants.GSTR1A.toUpperCase());
		}
		// if (dto.getIsResetSave()) {
		for (String section : sections) {
			if (APIConstants.DOCISS.equalsIgnoreCase(section)) {
				gstr1ADocIssueProcRepo.resetSaveGstr1AuditColumns(
						pair.getValue0(), pair.getValue1());
				continue;
			} else if (SUMMARY_SECTIONS.contains(section.toLowerCase())) {
				// Summary sections are not required update/reset
				List<Object[]> lastObj = batchRepo.selectMaxSumryRecord(
						pair.getValue0(), pair.getValue1(),
						APIConstants.GSTR1A.toUpperCase(),
						section.toUpperCase());
				if (CollectionUtils.isNotEmpty(lastObj)) {
					Object[] obj = lastObj.get(0);
					if (obj != null && obj[0] != null) {
						Long gstnBatchId = obj[0] != null
								? Long.parseLong(obj[0].toString()) : null;

						batchRepo.resetMaxIdsOfOldSectionToZero(
								pair.getValue0(), pair.getValue1(),
								section.toUpperCase(),
								APIConstants.GSTR1A.toUpperCase(), gstnBatchId);
					}
				}
				continue;
			}
			ArrayList<String> bifurcations = new ArrayList<String>();
			if (APIConstants.EXPA.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.GSTR1_EXPA.toUpperCase());
			} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.GSTR1_EXP.toUpperCase());
			} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.CDNUR.toUpperCase());
				bifurcations.add(GSTConstants.CDNUR_B2CL.toUpperCase());
				bifurcations.add(GSTConstants.CDNUR_EXPORTS.toUpperCase());
			} else if (GSTConstants.SUP_ECOM.equalsIgnoreCase(section)) {
				bifurcations.add(GSTConstants.SUP_ECOM);
			} else {
				bifurcations.add(section.toUpperCase());
			}
			
			if (sections.contains(APIConstants.ECOMSUP)) {
				LOGGER.debug("Section {}", sections);
				docRepoGstr1A.resetSaveGstr1EcomTrans(pair.getValue0(),
						pair.getValue1(), GSTConstants.ECOM_SUP);
			} else {
				docRepoGstr1A.resetSaveGstr1AuditColumns(pair.getValue0(),
						pair.getValue1(), bifurcations);
			}
			if (sections.contains(GSTConstants.SUP_ECOM)) {
				docRepoGstr1A.resetSaveGstr1SupEcom(pair.getValue0(),
						pair.getValue1(), GSTConstants.SUP_ECOM);
			}
			if (sections.contains(APIConstants.ECOMSUPSUM)) {
				docRepoGstr1A.resetSaveGstr1EcomSupSum(pair.getValue0(),
						pair.getValue1(), GSTConstants.ECOM_SUP);
			}
		}

	}
	
	public void resetGstr1ANonRespondedAspTableData(Pair<String, String> pair,
			Gstr1SaveToGstnReqDto dto, String resetType) {

		List<String> sections = dto.getTableSections();
		if (sections != null && !Collections.isEmpty(sections)) {
			// createResetEntries(pair, dto, resetType);
			clientResetUtil.createResetEntries(pair, dto, resetType,
					APIConstants.GSTR1A.toUpperCase());
		}
		for (String section : sections) {
			if (EINV_SECTIONS.contains(section.toLowerCase())) {
				ArrayList<String> bifurcations = new ArrayList<String>();
				if (APIConstants.EXPA.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.GSTR1_EXPA.toUpperCase());
				} else if (APIConstants.EXP.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.GSTR1_EXP.toUpperCase());
				} else if (APIConstants.CDNUR.equalsIgnoreCase(section)) {
					bifurcations.add(GSTConstants.CDNUR.toUpperCase());
					bifurcations.add(GSTConstants.CDNUR_B2CL.toUpperCase());
					bifurcations.add(GSTConstants.CDNUR_EXPORTS.toUpperCase());
				} else {
					bifurcations.add(section.toUpperCase());
				}
				
				docRepoGstr1A.resetSaveGstr1AuditColumnsForNonRespondedData(
						pair.getValue0(), pair.getValue1(), bifurcations);
			}
		}
	}
}
