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
import com.ey.advisory.admin.data.entities.client.MasterProductEntity;
import com.ey.advisory.common.DateUtil;

/**
 * 
 * @author Anand3.M
 *
 */

@Service("MasterDataToProductConverter")
public class MasterDataToProductConverter {
	private final static String WEB_UPLOAD_KEY = "|";


	private static final Logger LOGGER = LoggerFactory
			.getLogger(MasterDataToProductConverter.class);

	public List<MasterProductEntity> convertProduct(List<Object[]> productList,
			Gstr1FileStatusEntity updateFileStatus,Long entityId) {
		List<MasterProductEntity> product = new ArrayList<>();

		for (Object[] obj : productList) {
			MasterProductEntity prod = new MasterProductEntity();

			// TO-DO Structural Validations

			String sgstin = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]) : null;

			String productCode = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]) : null;

			String productDescription = (obj[2] != null
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

			String itcFlag = (obj[14] != null
					&& !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]) : null;

			String productKey = getProductValues(obj);
			prod.setEntityId(entityId);
			if (updateFileStatus != null) {
				prod.setFileId(updateFileStatus.getId());
				prod.setFileName(updateFileStatus.getFileName());
			}

			prod.setGstinPan(sgstin);
			if(productCode!=null && productCode.length()>100){
				productCode=productCode.substring(0, 100);
			}
			prod.setProductCode(productCode);
			if(productDescription!=null && productDescription.length()>100){
				productDescription=productDescription.substring(0, 100);
			}
			prod.setProductDesc(productDescription);
			if(category!=null && category.length()>100){
				category=category.substring(0, 100);
			}
			prod.setProductCategory(category);
			prod.setHsnOrSac(hsnOrSac);
			if(uom!=null && uom.length()>100){
				uom=uom.substring(0, 100);
			}
			prod.setUom(uom);
			prod.setReverseChargeFlag(reverseCharge);
			prod.setTdsFlag(tds);
			prod.setDiffPercent(differential);
			prod.setNilOrNonOrExmt(nilNonExmpt);
			if(notificationNumber!=null && notificationNumber.length()>100){
				notificationNumber=notificationNumber.substring(0, 100);
			}
			prod.setIfYCircularNotificationNum(notificationNumber);
			prod.setNotificationDate(localnotifiDate);
			prod.setEfCircularDate(localcirculDate);
			prod.setRate(rate);
			prod.setItcFlag(itcFlag);
			prod.setProdKey(productKey);

			product.add(prod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ProductFileCoversion List  -> ", prod, product);
			}
		}
		return product;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public String getProductValues(Object[] obj) {
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
