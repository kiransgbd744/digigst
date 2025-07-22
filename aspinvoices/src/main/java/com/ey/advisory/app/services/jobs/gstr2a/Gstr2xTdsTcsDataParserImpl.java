package com.ey.advisory.app.services.jobs.gstr2a;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaInvoicesEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTcsAndTcsaSummaryEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2xTdsAndTdsaInvoicesEntity;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCDSSummaryDetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTCSAndTCSADetailsAtGstnRepository;
import com.ey.advisory.app.data.repositories.client.gstr2.Gstr2xGetTDSAndTDSADetailsAtGstnRepository;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xAcceptAndrejectResDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xGetInvoicesResDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xTcdsSummaryResDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xTcsAndTcsaResDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xTdsAndTdsaResDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr2xTdsTcsSummaryResDto;
import com.ey.advisory.app.services.docs.SRFileToTcsTdsConvertion;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateFormatForStructuralValidatons;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("Gstr2xTdsTcsDataParserImpl")
@Slf4j
public class Gstr2xTdsTcsDataParserImpl implements Gstr2xTdsAndTcsDataParser {

	@Autowired
	@Qualifier("Gstr2xGetTCDSSummaryDetailsAtGstnRepository")
	private Gstr2xGetTCDSSummaryDetailsAtGstnRepository gstr2xGetTCDSSummaryDetailsAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2xGetTDSAndTDSADetailsAtGstnRepository")
	private Gstr2xGetTDSAndTDSADetailsAtGstnRepository gstr2xGetTDSAndTDSADetailsAtGstnRepository;

	@Autowired
	@Qualifier("Gstr2xGetTCSAndTCSADetailsAtGstnRepository")
	private Gstr2xGetTCSAndTCSADetailsAtGstnRepository gstr2xGetTCSAndTCSADetailsAtGstnRepository;

	@Autowired
	@Qualifier("SRFileToTcsTdsConvertion")
	private SRFileToTcsTdsConvertion sRFileToTcsTdsConvertion;

	@Override
	public void parseTdsData(final Gstr1GetInvoicesReqDto dto,
			final String apiResp, final String type, Long batchId) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonObject jsonObject = null;
		jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject();

		Gstr2xGetInvoicesResDto baseEntity = gson.fromJson(jsonObject,
				Gstr2xGetInvoicesResDto.class);
		List<Gstr2xTcsAndTcsaResDto> tcs = baseEntity.getTcs();
		List<Gstr2xTdsAndTdsaResDto> tds = baseEntity.getTds();
		List<Gstr2xTcsAndTcsaResDto> tcsa = baseEntity.getTcsa();
		List<Gstr2xTdsAndTdsaResDto> tdsa = baseEntity.getTdsa();
		Gstr2xTcdsSummaryResDto summary = baseEntity.getSummary();

		List<GetGstr2xTcsAndTcsaInvoicesEntity> tcsList = new ArrayList<>();
		List<GetGstr2xTcsAndTcsaInvoicesEntity> tcsaList = new ArrayList<>();
		List<GetGstr2xTdsAndTdsaInvoicesEntity> tdsList = new ArrayList<>();
		List<GetGstr2xTdsAndTdsaInvoicesEntity> tdsaList = new ArrayList<>();
		List<GetGstr2xTcsAndTcsaSummaryEntity> summaryListFinal = new ArrayList<>();
		try {
			if (tcs != null && !tcs.isEmpty()) {
				for (Gstr2xTcsAndTcsaResDto tcsData : tcs) {
					dto.setRecType(APIConstants.TCS.toUpperCase());
					GetGstr2xTcsAndTcsaInvoicesEntity tcsEnt = convertDateFromTcsDtoToEntity(
							tcsData, dto);
					tcsList.add(tcsEnt);
				}
			}
			if (tcsa != null && !tcsa.isEmpty()) {
				for (Gstr2xTcsAndTcsaResDto tcsaData : tcsa) {
					dto.setRecType(APIConstants.TCSA.toUpperCase());
					GetGstr2xTcsAndTcsaInvoicesEntity tcsaEnt = convertDateFromTcsDtoToEntity(
							tcsaData, dto);
					tcsaList.add(tcsaEnt);
				}
			}
			if (tds != null && !tds.isEmpty()) {
				for (Gstr2xTdsAndTdsaResDto tdsData : tds) {
					dto.setRecType(APIConstants.TDS.toUpperCase());
					GetGstr2xTdsAndTdsaInvoicesEntity tcsEnt = convertDateFromTdsDtoToEntity(
							tdsData, dto);
					tdsList.add(tcsEnt);
				}
			}
			if (tdsa != null && !tdsa.isEmpty()) {
				for (Gstr2xTdsAndTdsaResDto tdsData : tdsa) {
					dto.setRecType(APIConstants.TDSA.toUpperCase());
					GetGstr2xTdsAndTdsaInvoicesEntity tdsaEnt = convertDateFromTdsDtoToEntity(
							tdsData, dto);
					tdsaList.add(tdsaEnt);
				}
			}
			if (summary != null) {
				List<GetGstr2xTcsAndTcsaSummaryEntity> summ = convertTcsAndTdsDataToSummary(
						summary, dto);
				summaryListFinal.addAll(summ);
			}
			if (tcsList != null && !tcsList.isEmpty()) {
				gstr2xGetTCSAndTCSADetailsAtGstnRepository.saveAll(tcsList);
			}
			if (tcsaList != null && !tcsaList.isEmpty()) {
				gstr2xGetTCSAndTCSADetailsAtGstnRepository.saveAll(tcsaList);
			}
			if (tdsList != null && !tdsList.isEmpty()) {
				gstr2xGetTDSAndTDSADetailsAtGstnRepository.saveAll(tdsList);
			}
			if (tdsaList != null && !tdsaList.isEmpty()) {
				gstr2xGetTDSAndTDSADetailsAtGstnRepository.saveAll(tdsaList);
			}
			if (summaryListFinal != null && !summaryListFinal.isEmpty()) {
				gstr2xGetTCDSSummaryDetailsAtGstnRepository
						.saveAll(summaryListFinal);
			}

		} catch (Exception e) {
			LOGGER.error("Error Occureed in Gstr2xTdsTcsDataParserImpl", e);
			throw new AppException(e);
		}
	}

	private List<GetGstr2xTcsAndTcsaSummaryEntity> convertTcsAndTdsDataToSummary(
			Gstr2xTcdsSummaryResDto summary, Gstr1GetInvoicesReqDto dto) {
		List<GetGstr2xTcsAndTcsaSummaryEntity> summaryList = new ArrayList<>();
		Gstr2xTdsTcsSummaryResDto tcs = summary.getTcs();
		Gstr2xTdsTcsSummaryResDto tcsa = summary.getTcsa();
		Gstr2xTdsTcsSummaryResDto tds = summary.getTds();
		Gstr2xTdsTcsSummaryResDto tdsa = summary.getTdsa();

		if (tcs != null) {
			dto.setRecType(APIConstants.TCS.toUpperCase());
			List<GetGstr2xTcsAndTcsaSummaryEntity> tcsEnt = convertTcsAndTdsDataToSummaryAcc(
					tcs, summary, dto);
			summaryList.addAll(tcsEnt);
		}
		if (tcsa != null) {
			dto.setRecType(APIConstants.TCSA.toUpperCase());
			List<GetGstr2xTcsAndTcsaSummaryEntity> tcsEnt = convertTcsAndTdsDataToSummaryAcc(
					tcsa, summary, dto);
			summaryList.addAll(tcsEnt);
		}
		if (tds != null) {
			dto.setRecType(APIConstants.TDS.toUpperCase());
			List<GetGstr2xTcsAndTcsaSummaryEntity> tcsEnt = convertTcsAndTdsDataToSummaryAcc(
					tds, summary, dto);
			summaryList.addAll(tcsEnt);
		}
		if (tdsa != null) {
			dto.setRecType(APIConstants.TDSA.toUpperCase());
			List<GetGstr2xTcsAndTcsaSummaryEntity> tcsEnt = convertTcsAndTdsDataToSummaryAcc(
					tdsa, summary, dto);
			summaryList.addAll(tcsEnt);
		}

		return summaryList;
	}

	private List<GetGstr2xTcsAndTcsaSummaryEntity> convertTcsAndTdsDataToSummaryAcc(
			Gstr2xTdsTcsSummaryResDto tcs, Gstr2xTcdsSummaryResDto summary,
			Gstr1GetInvoicesReqDto dto) {
		List<GetGstr2xTcsAndTcsaSummaryEntity> list = new ArrayList<>();

		Gstr2xAcceptAndrejectResDto accepted = tcs.getAccepted();
		Gstr2xAcceptAndrejectResDto rejected = tcs.getRejected();

		if (accepted != null) {
			GetGstr2xTcsAndTcsaSummaryEntity ent = new GetGstr2xTcsAndTcsaSummaryEntity();
			ent.setChkSum(summary.getChksum());
			ent.setSgstin(summary.getGstin());
			ent.setRecordType(dto.getRecType());
			ent.setRecordTypeParameter(GSTConstants.A);
			ent.setRetPeriod(summary.getRetPeriod());
			Integer deriRetPeriod = 0;
			if (dto.getReturnPeriod() != null && !dto.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getReturnPeriod());
				ent.setDerReturnPeriod(deriRetPeriod);
			}
			ent.setTcsAndTcsaBatchIdGstr2x(dto.getBatchId());
			ent.setTotalRecords(accepted.getTotCount());
			ent.setTotalIgst(accepted.getToIamt());
			ent.setTotalCgst(accepted.getTotCamt());
			ent.setTotaSgst(accepted.getTotSamt());
			ent.setTotalValue(accepted.getTotAmt());
			User user = SecurityContext.getUser();
			String userName = user != null ? user.getUserPrincipalName()
					: APIConstants.SYSTEM.toUpperCase();
			ent.setCreatedBy(userName);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ent.setCreatedOn(convertNow);
			list.add(ent);

		}
		if (rejected != null) {
			GetGstr2xTcsAndTcsaSummaryEntity ent = new GetGstr2xTcsAndTcsaSummaryEntity();
			ent.setChkSum(summary.getChksum());
			ent.setSgstin(summary.getGstin());
			ent.setRecordType(dto.getRecType());
			ent.setRecordTypeParameter(GSTConstants.R);
			ent.setRetPeriod(summary.getRetPeriod());
			Integer deriRetPeriod = 0;
			if (dto.getReturnPeriod() != null && !dto.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(dto.getReturnPeriod());
				ent.setDerReturnPeriod(deriRetPeriod);
			}
			ent.setTcsAndTcsaBatchIdGstr2x(dto.getBatchId());
			ent.setTotalRecords(rejected.getTotCount());
			ent.setTotalIgst(rejected.getToIamt());
			ent.setTotalCgst(rejected.getTotCamt());
			ent.setTotaSgst(rejected.getTotSamt());
			ent.setTotalValue(rejected.getTotAmt());
			User user = SecurityContext.getUser();
			String userName = user != null ? user.getUserPrincipalName()
					: APIConstants.SYSTEM.toUpperCase();
			ent.setCreatedBy(userName);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			ent.setCreatedOn(convertNow);
			list.add(ent);
		}
		return list;
	}

	private GetGstr2xTdsAndTdsaInvoicesEntity convertDateFromTdsDtoToEntity(
			Gstr2xTdsAndTdsaResDto tdsData, Gstr1GetInvoicesReqDto dto) {

		GetGstr2xTdsAndTdsaInvoicesEntity tdsEntity = new GetGstr2xTdsAndTdsaInvoicesEntity();
		tdsEntity.setSgstin(dto.getGstin());
		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(dto.getFromTime());
		tdsEntity.setFromTime(date);
		tdsEntity.setRetPeriod(dto.getReturnPeriod());
		tdsEntity.setRecordType(dto.getRecType());
		tdsEntity.setTcsAndTcsaBatchIdGstr2x(dto.getBatchId());
		Integer deriRetPeriod = 0;
		if (dto.getReturnPeriod() != null && !dto.getReturnPeriod().isEmpty()) {
			deriRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getReturnPeriod());
			tdsEntity.setDerReturnPeriod(deriRetPeriod);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName()
				: APIConstants.SYSTEM.toUpperCase();
		tdsEntity.setCreatedBy(userName);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		tdsEntity.setCreatedOn(convertNow);
		tdsEntity.setCgstin(tdsData.getCtin());
		tdsEntity.setChkSum(tdsData.getChksum());
		tdsEntity.setSaveAction(tdsData.getFlag());
		tdsEntity.setDeductorUploadedMonth(tdsData.getMonth());
		tdsEntity.setOrgDeductorUploadedMonth(tdsData.getOmonth());
		tdsEntity.setIgstAmt(tdsData.getIamt());
		tdsEntity.setCgstAmt(tdsData.getCamt());
		tdsEntity.setSgstAmt(tdsData.getSamt());
		tdsEntity.setTaxableType(tdsData.getAmtDed());
		String genearateKey = sRFileToTcsTdsConvertion.genearateKey(
				tdsEntity.getRecordType(), tdsEntity.getSgstin(),
				tdsEntity.getRetPeriod(), tdsEntity.getCgstin(),
				tdsEntity.getDeductorUploadedMonth(),
				tdsEntity.getOrgDeductorUploadedMonth());
		String processGenerateKey = sRFileToTcsTdsConvertion.processGenerateKey(
				tdsEntity.getRecordType(), tdsEntity.getSgstin(),
				tdsEntity.getRetPeriod());
		tdsEntity.setPsKey(processGenerateKey);
		tdsEntity.setDocKey(genearateKey);

		return tdsEntity;
	}

	private GetGstr2xTcsAndTcsaInvoicesEntity convertDateFromTcsDtoToEntity(
			Gstr2xTcsAndTcsaResDto tcsData, Gstr1GetInvoicesReqDto dto) {
		GetGstr2xTcsAndTcsaInvoicesEntity tcsEntity = new GetGstr2xTcsAndTcsaInvoicesEntity();
		tcsEntity.setSgstin(dto.getGstin());
		LocalDate date = DateFormatForStructuralValidatons
				.parseObjToDate(dto.getFromTime());
		tcsEntity.setFromTime(date);
		tcsEntity.setRetPeriod(dto.getReturnPeriod());
		tcsEntity.setRecordType(dto.getRecType());
		tcsEntity.setTcsAndTcsaBatchIdGstr2x(dto.getBatchId());
		Integer deriRetPeriod = 0;
		if (dto.getReturnPeriod() != null && !dto.getReturnPeriod().isEmpty()) {
			deriRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getReturnPeriod());
			tcsEntity.setDerReturnPeriod(deriRetPeriod);
		}
		User user = SecurityContext.getUser();
		String userName = user != null ? user.getUserPrincipalName()
				: APIConstants.SYSTEM.toUpperCase();
		tcsEntity.setCreatedBy(userName);
		LocalDateTime convertNow = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		tcsEntity.setCreatedOn(convertNow);
		tcsEntity.setCgstin(tcsData.getCtin());
		tcsEntity.setChkSum(tcsData.getChksum());
		tcsEntity.setSaveAction(tcsData.getFlag());
		tcsEntity.setDeductorUploadedMonth(tcsData.getMonth());
		tcsEntity.setOrgDeductorUploadedMonth(tcsData.getOmonth());
		tcsEntity.setIgstAmt(tcsData.getIamt());
		tcsEntity.setCgstAmt(tcsData.getCamt());
		tcsEntity.setSgstAmt(tcsData.getSamt());
		tcsEntity.setTaxableType(tcsData.getAmt());
		tcsEntity.setRegSupplier(tcsData.getSupR());
		tcsEntity.setRegRetSupplier(tcsData.getRetsupU());
		tcsEntity.setUnRegSupplier(tcsData.getSupU());
		tcsEntity.setUnRegRetSupplier(tcsData.getRetsupU());
		tcsEntity.setPos(tcsData.getPos());
		tcsEntity.setDigiGstRemarks(tcsData.getRemarks());
		tcsEntity.setDigiGstComment(tcsData.getComment());
		
		String genearateKey = sRFileToTcsTdsConvertion.genearateKey(
				tcsEntity.getRecordType(), tcsEntity.getSgstin(),
				tcsEntity.getRetPeriod(), tcsEntity.getCgstin(),
				tcsEntity.getDeductorUploadedMonth(),
				tcsEntity.getOrgDeductorUploadedMonth());
		String processGenerateKey = sRFileToTcsTdsConvertion.processGenerateKey(
				tcsEntity.getRecordType(), tcsEntity.getSgstin(),
				tcsEntity.getRetPeriod());
		tcsEntity.setDocKey(genearateKey);
		tcsEntity.setPsKey(processGenerateKey);

		return tcsEntity;
	}

}
