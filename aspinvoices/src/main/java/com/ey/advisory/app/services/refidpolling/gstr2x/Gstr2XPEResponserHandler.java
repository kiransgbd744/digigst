package com.ey.advisory.app.services.refidpolling.gstr2x;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr2XProcessedTcsTdsEntity;
import com.ey.advisory.admin.data.entities.client.Gstr2XVerticalErrorEntity;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XErrorReport;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XTcsDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XTcsaDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XTdsDto;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XTdsaDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr2XPEResponserHandler")
public class Gstr2XPEResponserHandler {
	
	@Autowired
	private Gstr2XDocKeyGenerator gstr2XDocKeyGenerator;

	public Triplet<List<String>, Map<String, Gstr2XVerticalErrorEntity>, Integer> response(JsonElement root,
			Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<>();
		Gstr2XDto gstr2x = gson.fromJson(root, Gstr2XDto.class);
		Map<String, Gstr2XVerticalErrorEntity> docErrorMap = new HashMap<>();
		Gstr2XErrorReport gsterrors = gstr2x.getGstr2XErrorReport();
		if (APIConstants.TCS.equalsIgnoreCase(batch.getSection())) {
			for (Gstr2XTcsDto invoice : gsterrors.getTcs()) {
				Gstr2XProcessedTcsTdsEntity transData = new Gstr2XProcessedTcsTdsEntity();
				transData.setRecordType(batch.getSection());
				transData.setGstin(batch.getSgstin());
				transData.setRetPeriod(batch.getReturnPeriod());
				transData.setCtin(invoice.getCtin());
				transData.setDeductorUplMonth(invoice.getMonth());
				String invKey = gstr2XDocKeyGenerator.generateKey(transData);				
				invKeyList.add(invKey);
				Gstr2XVerticalErrorEntity errObj = new Gstr2XVerticalErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size(); 
		} else if(APIConstants.TCSA.equalsIgnoreCase(batch.getSection())){
			for (Gstr2XTcsaDto invoice : gsterrors.getTcsa()) {
				Gstr2XProcessedTcsTdsEntity transData = new Gstr2XProcessedTcsTdsEntity();
				transData.setRecordType(batch.getSection());
				transData.setGstin(batch.getSgstin());
				transData.setRetPeriod(batch.getReturnPeriod());
				transData.setCtin(invoice.getCtin());
				transData.setOrgDeductorUplMonth(invoice.getOmonth());
				String invKey = gstr2XDocKeyGenerator.generateKey(transData);				
				invKeyList.add(invKey);
				Gstr2XVerticalErrorEntity errObj = new Gstr2XVerticalErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size(); 
		} else if(APIConstants.TDS.equalsIgnoreCase(batch.getSection())){
			for (Gstr2XTdsDto invoice : gsterrors.getTds()) {
				Gstr2XProcessedTcsTdsEntity transData = new Gstr2XProcessedTcsTdsEntity();
				transData.setRecordType(batch.getSection());
				transData.setGstin(batch.getSgstin());
				transData.setRetPeriod(batch.getReturnPeriod());
				transData.setCtin(invoice.getCtin());
				transData.setDeductorUplMonth(invoice.getMonth());
				String invKey = gstr2XDocKeyGenerator.generateKey(transData);				
				invKeyList.add(invKey);
				Gstr2XVerticalErrorEntity errObj = new Gstr2XVerticalErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size(); 
		} else if(APIConstants.TDSA.equalsIgnoreCase(batch.getSection())){
			for (Gstr2XTdsaDto invoice : gsterrors.getTdsa()) {
				Gstr2XProcessedTcsTdsEntity transData = new Gstr2XProcessedTcsTdsEntity();
				transData.setRecordType(batch.getSection());
				transData.setGstin(batch.getSgstin());
				transData.setRetPeriod(batch.getReturnPeriod());
				transData.setCtin(invoice.getCtin());
				transData.setOrgDeductorUplMonth(invoice.getOmonth());
				String invKey = gstr2XDocKeyGenerator.generateKey(transData);				
				invKeyList.add(invKey);
				Gstr2XVerticalErrorEntity errObj = new Gstr2XVerticalErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size(); 
		}
		return new Triplet<>(invKeyList, docErrorMap, n);
	}
}
