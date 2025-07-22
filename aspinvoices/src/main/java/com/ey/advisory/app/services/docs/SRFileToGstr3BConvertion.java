package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.master.Gstr3BItcEntityMaster;
import com.ey.advisory.app.caches.ehcache.EhcacheGstr3bItc;
import com.ey.advisory.app.data.entities.client.Gstr3bEntity;
import com.ey.advisory.app.data.entities.client.Gstr3bExcelEntity;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Component("SRFileToGstr3BConvertion")
public class SRFileToGstr3BConvertion {

	@Autowired
	@Qualifier("DefaultGstr3BItcCache")
	private EhcacheGstr3bItc ehcacheGstr3bItc;

	public List<Gstr3bExcelEntity> convertSRFileToGstr3BExcel(
			List<Object[]> refundList, Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr3bExcelEntity> listGstr3b = new ArrayList<>();
		Gstr3bExcelEntity gstr3bEntity = null;
		for (Object[] arr : refundList) {
			gstr3bEntity = new Gstr3bExcelEntity();

			String taxPayerGstin = getValues(arr[0]);
			String returnPeriod = getValues(arr[1]);
			String serialNumber = getValues(arr[2]);
			String description = getValues(arr[3]);
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month <= 12 && month >= 01)
							&& (year  <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String igstAmnt = getValues(arr[4]);
			String cgstAmnt = getValues(arr[5]);
			String sgstAmnt = getValues(arr[6]);
			String cessAmnt = getValues(arr[7]);
			String gstr3bKey = getGstr3bInvKey(arr);
			String userName = updateFileStatus != null
					? updateFileStatus.getUpdatedBy() : null;
			gstr3bEntity.setGstin(taxPayerGstin);
			gstr3bEntity.setRetPeriod(returnPeriod);
			gstr3bEntity.setSerialNo(serialNumber);
			gstr3bEntity.setDescription(description);
			gstr3bEntity.setDerivedRetPeriod(derivedRePeroid);
			gstr3bEntity.setIgstAmnt(igstAmnt);
			gstr3bEntity.setSgstAmnt(sgstAmnt);
			gstr3bEntity.setCgstAmnt(cgstAmnt);
			gstr3bEntity.setCessAmnt(cessAmnt);
			gstr3bEntity.setFileId(updateFileStatus != null ? 
					String.valueOf(updateFileStatus.getId()) : null);
			gstr3bEntity.setInvKey(gstr3bKey);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr3bEntity.setCreatedBy(userName);
			gstr3bEntity.setCreatedOn(convertNow);
			if (updateFileStatus != null) {
				gstr3bEntity.setCreatedBy(updateFileStatus.getUpdatedBy());
				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					gstr3bEntity.setDataOriginType("E");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					gstr3bEntity.setDataOriginType("A");
				}

			}
			listGstr3b.add(gstr3bEntity);
		}
		return listGstr3b;
	}

	public String getGstr3bInvKey(Object[] arr) {
		String taxPayerGstin = getValues(arr[0]);
		String returnPeriod = getValues(arr[1]);
		String serialNumber = getValues(arr[2]);
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(taxPayerGstin)
				.add(returnPeriod).add(serialNumber).toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;
		return value;
	}

	public List<Gstr3bEntity> convertSRFileToGstr3b(
			List<Gstr3bExcelEntity> businessProcessedRecords,
			Gstr1FileStatusEntity updateFileStatus) {

		List<Gstr3bEntity> listGstr3b = new ArrayList<>();
		Gstr3bEntity gstr3bEntity = null;
		for (Gstr3bExcelEntity arr : businessProcessedRecords) {
			gstr3bEntity = new Gstr3bEntity();

			String taxPayerGstin = arr.getGstin();
			String returnPeriod = arr.getRetPeriod();
			Integer serialNumber = arr != null
					? Integer.parseInt(arr.getSerialNo()) : null;
			String description = arr.getDescription();
			Integer derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month <= 12 && month >= 01)
							&& (year  <= 9999 && year >= 0000)) {
						derivedRePeroid = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
					}
				}
			}
			BigDecimal igstAmnt = BigDecimal.ZERO;
			String interestIgst = arr.getIgstAmnt();
			if (interestIgst != null && !interestIgst.isEmpty()) {
				igstAmnt = NumberFomatUtil.getBigDecimal(interestIgst);
				gstr3bEntity.setIgstAmnt(igstAmnt);
			}
			BigDecimal cgstAmnt = BigDecimal.ZERO;
			String interestCgst = arr.getCgstAmnt();
			if (interestCgst != null && !interestCgst.isEmpty()) {
				cgstAmnt = NumberFomatUtil.getBigDecimal(interestCgst);
				gstr3bEntity.setCgstAmnt(cgstAmnt);
			}
			BigDecimal sgstAmnt = BigDecimal.ZERO;
			String interestSgst = arr.getSgstAmnt();
			if (interestSgst != null && !interestSgst.isEmpty()) {
				sgstAmnt = NumberFomatUtil.getBigDecimal(interestSgst);
				gstr3bEntity.setSgstAmnt(sgstAmnt);
			}
			BigDecimal cessAmnt = BigDecimal.ZERO;
			String interestCess = arr.getCessAmnt();
			if (interestCess != null && !interestCess.isEmpty()) {
				cessAmnt = NumberFomatUtil.getBigDecimal(interestCess);
				gstr3bEntity.setCessAmnt(cessAmnt);
			}

			if (serialNumber != null && serialNumber > 0) {
				ehcacheGstr3bItc = StaticContextHolder.getBean(
						"DefaultGstr3BItcCache", EhcacheGstr3bItc.class);
				Gstr3BItcEntityMaster entity = ehcacheGstr3bItc
						.findGstr3Bitc(serialNumber);
				if (entity != null) {
					gstr3bEntity
							.setTableDescription(entity.getTableDescription());
					gstr3bEntity.setTableSection(entity.getTableSection());
				}
			}
			String gstr3bKey = arr.getInvKey();
			String userName = updateFileStatus != null ?
					updateFileStatus.getUpdatedBy() : null;
			gstr3bEntity.setGstin(taxPayerGstin);
			gstr3bEntity.setRetPeriod(returnPeriod);
			gstr3bEntity.setSerialNo(serialNumber);
			gstr3bEntity.setDescription(description);
			gstr3bEntity.setDerivedRetPeriod(derivedRePeroid);
			gstr3bEntity.setIgstAmnt(igstAmnt);
			gstr3bEntity.setCgstAmnt(cgstAmnt);
			gstr3bEntity.setSgstAmnt(sgstAmnt);
			gstr3bEntity.setCessAmnt(cessAmnt);
			gstr3bEntity.setInvKey(gstr3bKey);
			gstr3bEntity.setDataOriginType(arr.getDataOriginType()); 
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			gstr3bEntity.setCreatedBy(userName);
			gstr3bEntity.setCreatedOn(convertNow);
			gstr3bEntity.setAsEnteredId(arr.getId());
			gstr3bEntity.setFileId(updateFileStatus != null ? 
					Long.valueOf(updateFileStatus.getId()) : null);
			listGstr3b.add(gstr3bEntity);
		}
		return listGstr3b;
	}

}
