package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterItemEntity;
import com.ey.advisory.common.DateUtil;

/**
 * 
 * @author Anand3.M
 *
 */
@Service("MasterDataToItemConverter")
public class MasterDataToItemConverter {
	private final static String WEB_UPLOAD_KEY = "|";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterDataToItemConverter.class);

	public List<MasterItemEntity> convertItem(List<Object[]> itemList,
			Gstr1FileStatusEntity updateFileStatus, Long entityId) {
		List<MasterItemEntity> item = new ArrayList<>();

		for (Object[] obj : itemList) {
			MasterItemEntity ite = new MasterItemEntity();

			// TO-DO Structural Validations

			String sgstin = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]) : null;

			String itemCode = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]) : null;

			String itemDescription = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]) : null;

			String category = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]) : null;

			Integer hsnOrSac = 0;
			if (obj[4] != null && !obj[4].toString().trim().isEmpty()) {
				String hsnOrSacstr = (String.valueOf(obj[4]));
				hsnOrSac = Integer.valueOf(hsnOrSacstr);
			}

			String uom = (obj[5] != null && !obj[5].toString().trim().isEmpty())
					? String.valueOf(obj[5]) : null;

			String reverseCharge = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]) : null;

			String tds = (obj[7] != null && !obj[7].toString().trim().isEmpty())
					? String.valueOf(obj[7]) : null;

			String differential = (obj[8] != null
					&& !obj[8].toString().trim().isEmpty())
							? String.valueOf(obj[8]) : null;

			String nilNonExmpt = (obj[9] != null
					&& !obj[9].toString().trim().isEmpty())
							? String.valueOf(obj[9]) : null;

			String notificationNumber = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]) : null;

			String notificationDate = (obj[11] != null
					&& !obj[11].toString().trim().isEmpty())
							? String.valueOf(obj[11]) : null;
			LocalDate localnotifiDate = DateUtil
					.parseObjToDate(notificationDate);

			String circularDate = (obj[12] != null
					&& !obj[12].toString().trim().isEmpty())
							? String.valueOf(obj[12]) : null;
			LocalDate localcirculDate = DateUtil.parseObjToDate(circularDate);

			BigDecimal rate = BigDecimal.ZERO;
			if (obj[13] != null && !obj[13].toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj[13]));
				rate = new BigDecimal(rateStr);
			}

			String eligiIndicator = (obj[14] != null
					&& !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]) : null;

			BigDecimal per0fElgi = BigDecimal.ZERO;
			if (obj[15] != null && !obj[15].toString().trim().isEmpty()) {
				String per0fElg = (String.valueOf(obj[15]));
				per0fElgi = new BigDecimal(per0fElg);
			}

			String commSupplyIndicator = (obj[16] != null
					&& !obj[16].toString().trim().isEmpty())
							? String.valueOf(obj[16]) : null;

			String itcReversalIdent = (obj[17] != null
					&& !obj[17].toString().trim().isEmpty())
							? String.valueOf(obj[17]) : null;

			String itcEntilement = (obj[18] != null
					&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]) : null;

			String itemKey = getItemValues(obj);
			ite.setEntityId(entityId);
			if (updateFileStatus != null) {
				ite.setFileId(updateFileStatus.getId());
				ite.setFileName(updateFileStatus.getFileName());
			}

			ite.setGstinPan(sgstin);
			if (itemCode != null && itemCode.length() > 100) {
				itemCode = itemCode.substring(0, 100);
			}
			ite.setItmCode(itemCode);
			if (itemDescription != null && itemDescription.length() > 100) {
				itemDescription = itemDescription.substring(0, 100);
			}
			ite.setItemDesc(itemDescription);
			if (category != null && category.length() > 100) {
				category = category.substring(0, 100);
			}
			ite.setItmCategory(category);
			ite.setHsnOrSac(hsnOrSac);
			if (uom != null && uom.length() > 100) {
				uom = uom.substring(0, 100);
			}
			ite.setUom(uom);
			ite.setReverseChargeFlag(reverseCharge);
			ite.setTdsFlag(tds);
			ite.setDiffPercent(differential);
			ite.setNilOrNonOrExmt(nilNonExmpt);
			if (notificationNumber != null
					&& notificationNumber.length() > 100) {
				notificationNumber = notificationNumber.substring(0, 100);
			}
			ite.setIfYCirularNotificationNum(notificationNumber);
			ite.setIfYCirularNotificationDate(localnotifiDate);
			ite.setEfCircularDate(localcirculDate);
			ite.setRate(rate);
			ite.setElgblIndicator(eligiIndicator);
			ite.setPerOfElgbl(per0fElgi);
			ite.setCommonSuppIndicator(commSupplyIndicator);
			ite.setItcReversalIdentifier(itcReversalIdent);
			ite.setItcsEntitlement(itcEntilement);
			ite.setItemKey(itemKey);

			item.add(ite);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ItemFileCoversion List  -> ", ite, item);
			}
		}
		return item;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public String getItemValues(Object[] obj) {

		String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
				? String.valueOf(obj[0]) : null;

		String hsnOrSac = (obj[4] != null
				&& !obj[4].toString().trim().isEmpty()) ? String.valueOf(obj[4])
						: null;
		String circularDate = (obj[12] != null) ? String.valueOf(obj[12])
				: null;

		String rate = (obj[13] != null && !obj[13].toString().trim().isEmpty())
				? String.valueOf(obj[13]) : null;
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(hsnOrSac)
				.add(circularDate).add(rate).toString();
	}
}
