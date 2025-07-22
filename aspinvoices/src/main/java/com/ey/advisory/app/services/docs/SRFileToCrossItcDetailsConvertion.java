package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.CrossItcAsEnteredEntity;
import com.ey.advisory.app.data.entities.client.CrossItcProcessEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.NumberFomatUtil;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("SRFileToCrossItcDetailsConvertion")
public class SRFileToCrossItcDetailsConvertion {
	private final static String WEB_UPLOAD_KEY = "|";

	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	public List<CrossItcAsEnteredEntity> convertSRFileToCrossItcExcel(
			List<Object[]> crossItcObjects,
			Gstr1FileStatusEntity fileStatusId) {

		List<CrossItcAsEnteredEntity> listOfCrossItc = new ArrayList<>();

		for (Object[] obj : crossItcObjects) {
			CrossItcAsEnteredEntity crossItcs = new CrossItcAsEnteredEntity();
			String isdGstin = getValues(obj[0]);
			String returnPeriod = getValues(obj[1]);
			String igstUsedAsIgst = getValues(obj[2]);
			String sgstUsedAsIgst = getValues(obj[3]);
			String cgstUsedAsIgst = getValues(obj[4]);
			String sgstUsedAsSgst = getValues(obj[5]);
			String igstUsedAsSgst = getValues(obj[6]);
			String cgstUsedAsCgst = getValues(obj[7]);
			String igstUsedAsCgst = getValues(obj[8]);
			String cessUsedAsCess = getValues(obj[9]);
			String crossItcKey = getInvKey(obj);
			if (crossItcKey != null && crossItcKey.length() > 2200) {
				crossItcKey = crossItcKey.substring(0, 2200);
			}
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int months = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}
			crossItcs.setIsdGstin(isdGstin);
			crossItcs.setTaxPeriod(returnPeriod);
			if (fileStatusId != null) {
				crossItcs.setFileId(fileStatusId.getId());
			}
			crossItcs.setIgstUsedAsIgst(igstUsedAsIgst);
			crossItcs.setSgstUsedAsIgst(sgstUsedAsIgst);
			crossItcs.setCgstUsedAsIgst(cgstUsedAsIgst);
			crossItcs.setSgstUsedAsSgst(sgstUsedAsSgst);
			crossItcs.setIgstUsedAsSgst(igstUsedAsSgst);
			crossItcs.setCgstUsedAsCgst(cgstUsedAsCgst);
			crossItcs.setIgstUsedAsCgst(igstUsedAsCgst);
			crossItcs.setCessUsedAsCess(cessUsedAsCess);
			crossItcs.setCrossItcDocKey(crossItcKey);
			crossItcs.setDerivedRetPeriod(derivedRePeroid);
			if (fileStatusId != null) {
				crossItcs.setCreatedBy(fileStatusId.getUpdatedBy());
			}
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			crossItcs.setCreatedOn(convertNow);
			listOfCrossItc.add(crossItcs);

		}
		return listOfCrossItc;
	}

	public String getInvKey(Object[] obj) {
		String isdGstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(isdGstin).add(retPeriod)
				.toString();
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public List<CrossItcProcessEntity> convertSRFileToCrossItc(
			List<CrossItcAsEnteredEntity> busProcessRecords,
			Gstr1FileStatusEntity updateFileStatus) {
		List<CrossItcProcessEntity> listCrossItc = new ArrayList<>();

		CrossItcProcessEntity crossItc = null;
		for (CrossItcAsEnteredEntity arr : busProcessRecords) {
			crossItc = new CrossItcProcessEntity();
			crossItc.setIsdGstin(arr.getIsdGstin());
			crossItc.setTaxPeriod(arr.getTaxPeriod());

			String igstUsedAsIgst = arr.getIgstUsedAsIgst();
			if (igstUsedAsIgst != null && !igstUsedAsIgst.isEmpty()) {
				BigDecimal igstUAIgst = NumberFomatUtil
						.getBigDecimal(igstUsedAsIgst);
				crossItc.setIgstUsedAsIgst(
						igstUAIgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}
			String sgstUsedAsIgst = arr.getSgstUsedAsIgst();
			if (sgstUsedAsIgst != null && !sgstUsedAsIgst.isEmpty()) {
				BigDecimal sgstUAIgst = NumberFomatUtil
						.getBigDecimal(sgstUsedAsIgst);
				crossItc.setSgstUsedAsIgst(
						sgstUAIgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String cgstUsedAsIgst = arr.getCgstUsedAsIgst();
			if (cgstUsedAsIgst != null && !cgstUsedAsIgst.isEmpty()) {
				BigDecimal cgstUAIgst = NumberFomatUtil
						.getBigDecimal(cgstUsedAsIgst);
				crossItc.setCgstUsedAsIgst(
						cgstUAIgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String sgstUsedAsSgst = arr.getSgstUsedAsSgst();
			if (sgstUsedAsSgst != null && !sgstUsedAsSgst.isEmpty()) {
				BigDecimal sgstUASgst = NumberFomatUtil
						.getBigDecimal(sgstUsedAsSgst);
				crossItc.setSgstUsedAsSgst(
						sgstUASgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String igstUsedAsSgst = arr.getIgstUsedAsSgst();
			if (igstUsedAsSgst != null && !igstUsedAsSgst.isEmpty()) {
				BigDecimal igstUASgst = NumberFomatUtil
						.getBigDecimal(igstUsedAsSgst);
				crossItc.setIgstUsedAsSgst(
						igstUASgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String cgstUsedAsCgst = arr.getCgstUsedAsCgst();
			if (cgstUsedAsCgst != null && !cgstUsedAsCgst.isEmpty()) {
				BigDecimal cgstUACgst = NumberFomatUtil
						.getBigDecimal(cgstUsedAsCgst);
				crossItc.setCgstUsedAsCgst(
						cgstUACgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String igstUsedAsCgst = arr.getIgstUsedAsCgst();
			if (igstUsedAsCgst != null && !igstUsedAsCgst.isEmpty()) {
				BigDecimal igstUACgst = NumberFomatUtil
						.getBigDecimal(igstUsedAsCgst);
				crossItc.setIgstUsedAsCgst(
						igstUACgst.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			String cessUsedAsCess = arr.getCessUsedAsCess();
			if (cessUsedAsCess != null && !cessUsedAsCess.isEmpty()) {
				BigDecimal cessUACess = NumberFomatUtil
						.getBigDecimal(cessUsedAsCess);
				crossItc.setCessUsedAsCess(
						cessUACess.setScale(2, BigDecimal.ROUND_HALF_EVEN));
			}

			if (updateFileStatus != null) {
				crossItc.setFileId(updateFileStatus.getId());
			}
			crossItc.setAsEnterId(arr.getId());
			crossItc.setInfo(arr.isInfo());
			crossItc.setCrossItcDocKey(arr.getCrossItcDocKey());
			crossItc.setCreatedBy(arr.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			crossItc.setCreatedOn(convertNow);
			listCrossItc.add(crossItc);
		}
		return listCrossItc;
	}
}
