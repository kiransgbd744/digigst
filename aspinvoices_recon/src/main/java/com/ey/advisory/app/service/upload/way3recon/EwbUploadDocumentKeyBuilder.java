package com.ey.advisory.app.service.upload.way3recon;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.eyfileutils.tabular.DataBlockKeyBuilder;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * 
 * This class is responsible for Building a unique document key which is a
 * combination of DocType, DocNo, fy, Supplier GSTIN,
 * 
 * @author vishal.verma
 *
 */

@Slf4j
public class EwbUploadDocumentKeyBuilder implements DataBlockKeyBuilder<String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String buildDataBlockKey(Object[] arr, TabularDataLayout config) {
		
		try {

		String supplyType = arr[2] != null ? arr[2].toString().trim() : null;
		String docType = createDocType(supplyType);

		String docNum = arr[3] != null ? arr[3].toString().trim() : "";

		String fromGstinInfo = arr[7] != null ? arr[7].toString().trim() : null;

		String docDate = arr[4] != null ? arr[4].toString().trim() : null;

		String suppGstin = "";
		if (fromGstinInfo != null && fromGstinInfo.length() > 14) {
			suppGstin = fromGstinInfo.substring(0, 15);
		}

		String finYear = "";

		finYear = GenUtil.getFinYear(DateUtil.parseObjToDate(docDate));

		// Inward Invoice Key Format - FY |RGSTIN|SGSTIN |DocType | DocNo
		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(suppGstin)
				.add(docType).add(docNum).toString();
		
		} catch(Exception ex){
			LOGGER.error("Error Occured Inside EwbUploadDocumentKeyBuilder");
			throw new AppException(ex);
		}
		
	}

	@Override
	public String buildComprehenceDataBlockKey(Object[] arr, TabularDataLayout config) {
		
		return null;
	}

	@Override
	public String buildItc04DataBlockKey(Object[] arr, TabularDataLayout layout) {
		
		return null;
	}

	private String createDocType(String supplyType) {

		Map<String, String> docTypeMap = getdocTypeMap();

		if (supplyType != null && docTypeMap.containsKey(supplyType)) {
			return supplyType != null ? docTypeMap.get(supplyType) : null;
		} else {
			return supplyType;
		}

	}

	private Map<String, String> getdocTypeMap() {

		Map<String, String> map = new HashMap<>();

		map.put("Outward Supply", "INV");
		map.put("Outward Export", "INV");
		map.put("Outward Job Work", "DLC");
		map.put("Outward SKD/CKD", "INV");
		map.put("Outward Recipient not known", "DLC");
		map.put("Outward For own use", "DLC");
		map.put("Outward Exhibition or Fairs", "DLC");
		map.put("Outward Line Sales", "DLC");
		map.put("Outward Others", "DLC");

		return map;
	}
}