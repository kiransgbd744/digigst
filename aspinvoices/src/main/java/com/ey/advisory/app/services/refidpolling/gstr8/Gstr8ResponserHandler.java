package com.ey.advisory.app.services.refidpolling.gstr8;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.gstr8.Gstr8DocErrorEntity;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8ErrorReport;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8SaveDto;
import com.ey.advisory.app.docs.dto.gstr8.TcsDto;
import com.ey.advisory.app.docs.dto.gstr8.TcsaDto;
import com.ey.advisory.app.docs.dto.gstr8.UrdDto;
import com.ey.advisory.app.docs.dto.gstr8.UrdaDto;
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
 * @author Siva.Reddy
 *
 */
@Component("Gstr8ResponserHandler")
public class Gstr8ResponserHandler implements Gstr8ResponseHandler {

	public static final String DASH = "-";
	@Autowired
	@Qualifier("GstnKeyGenerator")
	private GstnKeyGenerator docKeyGenerator;

	@Override
	public Triplet<List<String>, Map<String, Gstr8DocErrorEntity>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();
		Gstr8SaveDto gstr8 = gson.fromJson(root, Gstr8SaveDto.class);
		Map<String, Gstr8DocErrorEntity> docErrorMap = new HashMap<>();
		Gstr8ErrorReport gsterrors = gstr8.getGstr8ErrorReport();
		if (APIConstants.TCS.equalsIgnoreCase(batch.getSection())) {
			for (TcsDto invoice : gsterrors.getTcs()) {
				String ecomGstin = gstr8.getGstin() == null ? batch.getSgstin()
						: gstr8.getGstin();
				String returnPeroid = gstr8.getFp() == null
						? batch.getReturnPeriod() : gstr8.getFp();
				String sTin = invoice.getStin();
				String invkey = docKeyGenerator.generateGstr8TcsUrdKey(
						ecomGstin, returnPeroid, sTin,
						APIConstants.TCS.toUpperCase());
				invKeyList.add(invkey);
				Gstr8DocErrorEntity errObj = new Gstr8DocErrorEntity();
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
		} else if (APIConstants.TCSA.equalsIgnoreCase(batch.getSection())) {
			for (TcsaDto invoice : gsterrors.getTcsa()) {
				String ecomGstin = gstr8.getGstin() == null ? batch.getSgstin()
						: gstr8.getGstin();
				String returnPeroid = gstr8.getFp() == null
						? batch.getReturnPeriod() : gstr8.getFp();
				String sTin = invoice.getStin();
				String orgReturnPeroid = invoice.getOfp();
				String invkey = docKeyGenerator.generateGstr8TcsaUrdaKey(
						ecomGstin, returnPeroid, orgReturnPeroid, sTin,
						APIConstants.TCSA.toUpperCase());
				invKeyList.add(invkey);
				Gstr8DocErrorEntity errObj = new Gstr8DocErrorEntity();
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

		else if (APIConstants.URD.equalsIgnoreCase(batch.getSection())) {
			for (UrdDto invoice : gsterrors.getUrd()) {
				String ecomGstin = gstr8.getGstin() == null ? batch.getSgstin()
						: gstr8.getGstin();
				String returnPeroid = gstr8.getFp() == null
						? batch.getReturnPeriod() : gstr8.getFp();
				String eId = invoice.getEid();
				String invkey = docKeyGenerator.generateGstr8TcsUrdKey(
						ecomGstin, returnPeroid, eId,
						APIConstants.URD.toUpperCase());
				invKeyList.add(invkey);
				Gstr8DocErrorEntity errObj = new Gstr8DocErrorEntity();
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

		else if (APIConstants.URDA.equalsIgnoreCase(batch.getSection())) {
			for (UrdaDto invoice : gsterrors.getUrda()) {
				String ecomGstin = gstr8.getGstin() == null ? batch.getSgstin()
						: gstr8.getGstin();
				String returnPeroid = gstr8.getFp() == null
						? batch.getReturnPeriod() : gstr8.getFp();
				String eId = invoice.getEid();
				String orgReturnPeroid = invoice.getOfp();
				String invkey = docKeyGenerator.generateGstr8TcsaUrdaKey(
						ecomGstin, returnPeroid, orgReturnPeroid, eId,
						APIConstants.URDA.toUpperCase());
				invKeyList.add(invkey);
				Gstr8DocErrorEntity errObj = new Gstr8DocErrorEntity();
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
		return new Triplet<List<String>, Map<String, Gstr8DocErrorEntity>, Integer>(
				invKeyList, docErrorMap, n);
	}

}
