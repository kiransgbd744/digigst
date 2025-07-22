package com.ey.advisory.app.data.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr1FileStatusRepository;
import com.ey.advisory.app.data.entities.client.Gstr1NilNonExmptSummaryEntity;
import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.app.data.repositories.client.DocRepository;
import com.ey.advisory.app.data.repositories.client.Gstr1NilNonExtSummaryRepository;
import com.ey.advisory.app.data.repositories.client.NilAndHsnProcedureCallRepository;
import com.ey.advisory.app.docs.dto.FileArrivalMsgDto;
import com.ey.advisory.app.services.docs.ComprehensiveSRFileArrivalHandler;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.Message;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Component("ComprehensiveEinvoicePopProcedureImpl")
@Slf4j
public class ComprehensiveEinvoicePopProcedureImpl {
	@Autowired
	@Qualifier("ComprehensiveSRFileArrivalHandler")
	private ComprehensiveSRFileArrivalHandler comprehensiveSRFileArrivalHandler;

	@Autowired
	@Qualifier("Gstr1FileStatusRepository")
	private Gstr1FileStatusRepository gstr1FileStatusRepository;

	@Autowired
	@Qualifier("NilAndHsnProcedureCallRepositoryImpl")
	private NilAndHsnProcedureCallRepository nilAndHsnProcedureCallRepository;

	@Autowired
	@Qualifier("DocRepository")
	private DocRepository docHeaderRepository;

	@Autowired
	@Qualifier("Gstr1NilNonExtSummaryRepository")
	private Gstr1NilNonExtSummaryRepository gstr1NilNonExtSummaryRepository;

	private static final List<String> HSN_TABLE_SECTION = ImmutableList.of(
			GSTConstants.GSTR1_4A, GSTConstants.GSTR1_4B, GSTConstants.GSTR1_5A,
			GSTConstants.GSTR1_6A, GSTConstants.GSTR1_6B, GSTConstants.GSTR1_6C,
			GSTConstants.GSTR1_7A1, GSTConstants.GSTR1_7B1,
			GSTConstants.GSTR1_8A, GSTConstants.GSTR1_8B, GSTConstants.GSTR1_8C,
			GSTConstants.GSTR1_8D, GSTConstants.GSTR1_9B);
	private static final List<String> NIL_TABLE_SECTION = ImmutableList.of(
			GSTConstants.GSTR1_8A, GSTConstants.GSTR1_8B, GSTConstants.GSTR1_8C,
			GSTConstants.GSTR1_8D);

	public void procHsnAndTransCall(Message message) {
		FileArrivalMsgDto msg = comprehensiveSRFileArrivalHandler
				.extractAndValidateMessage(message);
		String fileName = msg.getFileName();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		Long id = updateFileStatus.getId();
		List<OutwardTransDocument> outWardTransList = docHeaderRepository
				.findByFileId(id);

		Set<String> nilSet = null;
		Set<String> hsnSet = null;

		for (OutwardTransDocument sum : outWardTransList) {
			if (sum.getSgstin() == null || sum.getSgstin().isEmpty()
					|| GSTConstants.URP.equalsIgnoreCase(sum.getSgstin())){
			continue;
			}
				if (!GSTConstants.I.equalsIgnoreCase(sum.getTransactionType())) {
				if (HSN_TABLE_SECTION.contains(sum.getTableType())) {
					hsnSet = new HashSet<>();
					String sgstin = sum.getSgstin();
					Integer derivedRetPeriod = sum.getDerivedTaxperiod();
					String concate = sgstin.concat(derivedRetPeriod.toString());
					LOGGER.debug("HSN_TABLE_SECTION values :", concate);
					hsnSet.add(concate);
				}
				if (NIL_TABLE_SECTION.contains(sum.getTableType())) {
					nilSet = new HashSet<>();
					String sgstin = sum.getSgstin();
					Integer derivedRetPeriod = sum.getDerivedTaxperiod();
					String concate = sgstin.concat(derivedRetPeriod.toString());
					LOGGER.debug("NIL_TABLE_SECTION values :", concate);
					nilSet.add(concate);
				}
			}
		}
		if (hsnSet != null && !hsnSet.isEmpty()) {
			LOGGER.debug("hsnSet Non empty");
			for (String strSet : hsnSet) {
				String sgstin = strSet != null && strSet.length() >= 15
						? strSet.substring(0, 15) : null;
				String devPeriod = strSet != null && strSet.length() == 21
						? strSet.substring(15) : null;
				Integer intDevPeriod = devPeriod != null
						? Integer.parseInt(devPeriod) : 0;
				if (sgstin != null && intDevPeriod > 0) {
					LOGGER.debug(
							"Gstin,Derivaed return period {},{} for hsnSet values :",
							sgstin, intDevPeriod);
					nilAndHsnProcedureCallRepository.getHsnProc(sgstin,
							intDevPeriod);
					LOGGER.debug(
							"HSN proc called with Gstin,Derivaed return period {},{}  :",
							sgstin, intDevPeriod);
				}
			}
		}
		if (nilSet != null && !nilSet.isEmpty()) {
			LOGGER.debug("nilSet Non empty");
			for (String strSet : nilSet) {
				String sgstin = strSet != null && strSet.length() >= 15
						? strSet.substring(0, 15) : null;
				String devPeriod = strSet != null && strSet.length() == 21
						? strSet.substring(15) : null;
				Integer intDevPeriod = devPeriod != null
						? Integer.parseInt(devPeriod) : 0;
				if (sgstin != null && intDevPeriod > 0) {
					LOGGER.debug(
							"Gstin,Derivaed return period {},{} for nilSet values :",
							sgstin, intDevPeriod);
					nilAndHsnProcedureCallRepository.getNilNonProc(sgstin,
							intDevPeriod);
					LOGGER.debug(
							"NilNonExpt proc called with Gstin,Derivaed return period {},{}  :",
							sgstin, intDevPeriod);
				}
			}
		}
	}

	public void procNilNonExmptCall(Message message) {

		FileArrivalMsgDto msg = comprehensiveSRFileArrivalHandler
				.extractAndValidateMessage(message);
		String fileName = msg.getFileName();
		Gstr1FileStatusEntity updateFileStatus = gstr1FileStatusRepository
				.getFileName(fileName);
		Long id = updateFileStatus.getId();
		List<Gstr1NilNonExmptSummaryEntity> tableSumSaved = gstr1NilNonExtSummaryRepository
				.findByFileId(id);

		Set<String> set = new HashSet<String>();
		for (Gstr1NilNonExmptSummaryEntity sum : tableSumSaved) {
			String sgstin = sum.getSgstin();
			Integer derivedRetPeriod = sum.getDerivedRetPeriod();
			String concate = sgstin.concat(derivedRetPeriod.toString());
			set.add(concate);
		}
		for (String strSet : set) {
			String sgstin = strSet.substring(0, 15);
			String devPeriod = strSet.substring(15);
			Integer intDevPeriod = Integer.parseInt(devPeriod);
			nilAndHsnProcedureCallRepository.getNilNonProc(sgstin,
					intDevPeriod);
			nilAndHsnProcedureCallRepository.getHsnProc(sgstin, intDevPeriod);
		}
	}

	public void procErpHsnAndTransCall(List<Long> ids) {
		List<OutwardTransDocument> outWardTransList = docHeaderRepository
				.findById(ids);
		Set<String> nilSet = null;
		Set<String> hsnSet = null;

		for (OutwardTransDocument sum : outWardTransList) {
			if (!GSTConstants.I.equalsIgnoreCase(sum.getTransactionType())) {
				if (HSN_TABLE_SECTION.contains(sum.getTableType())) {
					hsnSet = new HashSet<>();
					String sgstin = sum.getSgstin();
					Integer derivedRetPeriod = sum.getDerivedTaxperiod();
					String concate = sgstin.concat(derivedRetPeriod.toString());
					LOGGER.debug("HSN_TABLE_SECTION values :", concate);
					hsnSet.add(concate);
				}
				if (NIL_TABLE_SECTION.contains(sum.getTableType())) {
					nilSet = new HashSet<>();
					String sgstin = sum.getSgstin();
					Integer derivedRetPeriod = sum.getDerivedTaxperiod();
					String concate = sgstin.concat(derivedRetPeriod.toString());
					LOGGER.debug("NIL_TABLE_SECTION values :", concate);
					nilSet.add(concate);
				}
			}
		}
		if (hsnSet != null && !hsnSet.isEmpty()) {
			LOGGER.debug("hsnSet Non empty");
			for (String strSet : hsnSet) {
				String sgstin = strSet != null && strSet.length() >= 15
						? strSet.substring(0, 15) : null;
				String devPeriod = strSet != null && strSet.length() == 21
						? strSet.substring(15) : null;
				Integer intDevPeriod = devPeriod != null
						? Integer.parseInt(devPeriod) : 0;
				if (sgstin != null && intDevPeriod > 0) {
					LOGGER.debug(
							"Gstin,Derivaed return period {},{} for hsnSet values :",
							sgstin, intDevPeriod);
					nilAndHsnProcedureCallRepository.getHsnProc(sgstin,
							intDevPeriod);
					LOGGER.debug(
							"HSN proc called with Gstin,Derivaed return period {},{}  :",
							sgstin, intDevPeriod);
				}
			}
		}
		if (nilSet != null && !nilSet.isEmpty()) {
			LOGGER.debug("nilSet Non empty");
			for (String strSet : nilSet) {
				String sgstin = strSet != null && strSet.length() >= 15
						? strSet.substring(0, 15) : null;
				String devPeriod = strSet != null && strSet.length() == 21
						? strSet.substring(15) : null;
				Integer intDevPeriod = devPeriod != null
						? Integer.parseInt(devPeriod) : 0;
				if (sgstin != null && intDevPeriod > 0) {
					LOGGER.debug(
							"Gstin,Derivaed return period {},{} for nilSet values :",
							sgstin, intDevPeriod);
					nilAndHsnProcedureCallRepository.getNilNonProc(sgstin,
							intDevPeriod);
					LOGGER.debug(
							"NilNonExpt proc called with Gstin,Derivaed return period {},{}  :",
							sgstin, intDevPeriod);
				}
			}
		}

	}

}
