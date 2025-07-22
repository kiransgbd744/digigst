package com.ey.advisory.app.services.refidpolling.gstr7;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr7DocErrorEntity;
import com.ey.advisory.app.data.repositories.client.gstr7.Gstr7ProcessedRepository;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7ErrorReport;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsaDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.services.common.GstnKeyGenerator;
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
@Component("Gstr7ResponserHandler")
public class Gstr7ResponserHandler implements Gstr7ResponseHandler{
	
	public static final String DASH = "-";
	@Autowired
	@Qualifier("GstnKeyGenerator")
	private GstnKeyGenerator docKeyGenerator;
	
	@Autowired
	@Qualifier("Gstr7ProcessedRepository")
	private Gstr7ProcessedRepository gstr7ProcessedRepository;

	@Override
	public Triplet<List<String>, Map<String, Gstr7DocErrorEntity>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();
		SaveGstr7 gstr7 = gson.fromJson(root, SaveGstr7.class);
		Map<String, Gstr7DocErrorEntity> docErrorMap = new HashMap<>();		
		Gstr7ErrorReport gsterrors = gstr7.getGstr7ErrorReport();
		if (APIConstants.TDS.equalsIgnoreCase(batch.getSection())) {
			for (Gstr7TdsDto invoice : gsterrors.getTdsInvoice()) {
				String deductorGstin = gstr7.getGstin() == null
						? batch.getSgstin() : gstr7.getGstin();
				String returnPeroid = gstr7.getTaxperiod() == null
						? batch.getReturnPeriod() : gstr7.getTaxperiod();
				String deducteeGstin = invoice.getGstin_ded();
				String invkey = docKeyGenerator.generateGstr7TdsKey(
						deductorGstin, returnPeroid, deducteeGstin);
				/*
				 * String tabNum = "Table-3"; List<String> invKey =
				 * gstr7ProcessedRepository.findDocKeyByBatchID(batch.getId(),
				 * invoice.getGstin_ded(),tabNum);
				 */
				invKeyList.add(invkey);
				Gstr7DocErrorEntity errObj = new Gstr7DocErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDescription(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDate.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invkey, errObj);
			}
			n = invKeyList.size();
		}else if(APIConstants.TDSA.equalsIgnoreCase(batch.getSection())){
			for (Gstr7TdsaDto invoice : gsterrors.getTdsaInvoice()) {
				/*
				 * String tabNum = "Table-4"; String invKey =
				 * gstr7ProcessedRepository.findDocKeyByBatchID(batch.getId(),
				 * invoice.getGstin_ded(),tabNum);
				 */
				String deductorGstin = gstr7.getGstin() == null
						? batch.getSgstin() : gstr7.getGstin();
				String returnPeroid = gstr7.getTaxperiod() == null
						? batch.getReturnPeriod() : gstr7.getTaxperiod();
				String deducteeGstin = invoice.getGstin_ded();
				String orgReturnPeroid = invoice.getOmonth();
				String invkey = docKeyGenerator.generateGstr7TdsaKey(
						deductorGstin, returnPeroid, orgReturnPeroid,
						deducteeGstin);
				invKeyList.add(invkey);
				Gstr7DocErrorEntity errObj = new Gstr7DocErrorEntity();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDescription(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedOn(LocalDate.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invkey, errObj);
			}
			n = invKeyList.size();
		}
		return new Triplet<List<String>, Map<String, Gstr7DocErrorEntity>, Integer>(
				invKeyList, docErrorMap, n);
	}

}
