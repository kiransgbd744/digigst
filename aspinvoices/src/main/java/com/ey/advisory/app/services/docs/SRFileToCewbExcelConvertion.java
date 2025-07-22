package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.CewbEntity;
import com.ey.advisory.app.data.entities.client.CewbExcelEntity;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToCewbExcelConvertion")
public class SRFileToCewbExcelConvertion {

	public List<CewbExcelEntity> convertSRFileToCewbExcel(
			List<Object[]> refundList, Gstr1FileStatusEntity updateFileStatus) {

		List<CewbExcelEntity> listRefunds = new ArrayList<>();
		CewbExcelEntity cewbExcelEntity = null;
		for (Object[] arr : refundList) {
			cewbExcelEntity = new CewbExcelEntity();

			String sNo = getValues(arr[0]);
			Object ewbN = CommonUtility.exponentialAndZeroCheck(arr[1]);// EWBNo
			String ewbNo = getValues(ewbN);
			String fromPlace = getValues(arr[2]);
			String fromState = getValues(arr[3]);
			String vehicleNo = getValues(arr[4]);
			String transMode = getValues(arr[5]);
			String transDocNo = getValues(arr[6]);
			String transDocDate = getValues(arr[7]);
			LocalDate transDate = DateUtil.parseObjToDate(transDocDate);
			String gstin = getValues(arr[8]);
			String cewbInvKey = getCewbInvKey(arr);
			cewbExcelEntity.setSNo(sNo);
			cewbExcelEntity.setEwbNo(ewbNo);
			cewbExcelEntity.setGstin(gstin);
			cewbExcelEntity.setFromPlace(fromPlace);
			cewbExcelEntity.setFromState(fromState);
			cewbExcelEntity.setVehicleNo(vehicleNo);
			cewbExcelEntity.setTransMode(transMode);
			cewbExcelEntity.setTransDocNo(transDocNo);
			if (transDate != null && !transDate.toString().isEmpty()) {
				cewbExcelEntity.setTransDocDate(String.valueOf(transDate));
			} else {
				cewbExcelEntity.setTransDocDate(transDocDate);
			}

			cewbExcelEntity.setCewbInvKey(cewbInvKey);

			if (updateFileStatus != null) {
				cewbExcelEntity.setFileId(updateFileStatus.getId());
				cewbExcelEntity.setCreatedBy(updateFileStatus.getUpdatedBy());
			} else {
				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				cewbExcelEntity.setCreatedOn(convertNow);
			}
			listRefunds.add(cewbExcelEntity);
		}
		return listRefunds;
	}

	public String getCewbInvKey(Object[] obj) {
		String sNo = (obj[0] != null) ? (String.valueOf(obj[0])).trim() : "";
		return new StringJoiner("|").add(sNo).toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public List<CewbEntity> convertSRFileToCewb(
			List<CewbExcelEntity> busProcessRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<CewbEntity> listCewb = new ArrayList<>();

		CewbEntity cewb = null;
		for (CewbExcelEntity arr : busProcessRecords) {
			cewb = new CewbEntity();

			String sNo = arr.getSNo();
			if (sNo != null && !sNo.isEmpty()) {
				Long sNoB = Long.valueOf(sNo);
				cewb.setSNo(sNoB);
			}
			cewb.setEwbNo(arr.getEwbNo());
			cewb.setFromPlace(arr.getFromPlace());
			cewb.setFromState(arr.getFromState());
			cewb.setVehicleNo(arr.getVehicleNo());

			// String transMode = EyEwbCommonUtil.getTransMode(arr.getTransMode());
			
			if (arr.getTransMode() != null
					&& !arr.getTransMode().trim().isEmpty()) {
				cewb.setTransMode(arr.getTransMode().toUpperCase());
			}
			cewb.setTransMode(arr.getTransMode());
			LocalDate transDate = DateUtil
					.parseObjToDate(arr.getTransDocDate());
			cewb.setTransDocDate(transDate);
			cewb.setTransDocNo(arr.getTransDocNo());
			cewb.setGstin(arr.getGstin());
			if (updateFileStatus != null) {
				cewb.setFileId(updateFileStatus.getId());
			}
			cewb.setAsEnterTableId(arr.getId());
			cewb.setInfo(arr.isInfo());
			cewb.setCewbInvKey(arr.getCewbInvKey());
			cewb.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			cewb.setCreatedOn(convertNow);
			listCewb.add(cewb);
		}
		return listCewb;
	}
}
