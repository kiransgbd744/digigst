package com.ey.advisory.app.services.refidpolling.gstr6;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Gstr6VerticalWebError;
import com.ey.advisory.app.data.entities.client.InwardTransDocError;
import com.ey.advisory.app.data.repositories.client.Gstr6GstnDistributionSaveRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ErrorReport;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6IsdDetailsDto;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.services.common.GstnKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr6PEDistributionResponserHandler")
public class Gstr6PEDistributionResponserHandler implements Gstr6ResponseHandler {

	public static final String DASH = "-";
	
	@Autowired
	@Qualifier("Gstr6GstnDistributionSaveRepository")
	private Gstr6GstnDistributionSaveRepository distributionRepository;

	@Autowired
	@Qualifier("GstnKeyGenerator")
	private GstnKeyGenerator docKeyGenerator;

	@Override
	public Triplet<List<String>, Map<String, Gstr6VerticalWebError>, Integer> isdResponse(JsonElement root,
			Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();
		SaveGstr6 gstr6 = gson.fromJson(root, SaveGstr6.class);
		Map<String, Gstr6VerticalWebError> docErrorMap = new HashMap<>();
		Gstr6ErrorReport gsterrors = gstr6.getGstr6ErrorReport();
		if (APIConstants.ISD.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6IsdDetailsDto invoice : gsterrors.getIsdInvoice()) {
				/*String invKey = distributionRepository.findDocKeyByBatchID(batch.getId(), invoice.getDocnum(),
						batch.getSgstin(), batch.getReturnPeriod());
				if (invKey == null || invKey.isEmpty()) {*/
				String isdGstin = batch.getSgstin();
				String docNo = null;				
				String docType = invoice.getIsd_docty();
				String fYear = null ;	
				if (docType != null && (docType.equalsIgnoreCase("ISD") || docType.equalsIgnoreCase("ISDUR"))) {
					docType = "INV";
					docNo = invoice.getDocnum();
					String idt = invoice.getDocdt();
					LocalDate parseObjToDate = DateUtil.parseObjToDate(idt);
					//LocalDate localOrigDocDate = EYDateUtil.toUTCDateTimeFromLocal(parseObjToDate);
					if (parseObjToDate != null) {
						fYear = parseObjToDate.toString();
					}
				} else if (docType != null
						&& (docType.equalsIgnoreCase("ISDCN") || docType.equalsIgnoreCase("ISDCNUR"))) {
					docType = "CR";
					docNo = invoice.getCrdnum();
					String idt = invoice.getCrddt();
					LocalDate parseObjToDate = DateUtil.parseObjToDate(idt);
					//LocalDate localOrigDocDate = EYDateUtil.toUTCDateTimeFromLocal(parseObjToDate);
					if (parseObjToDate != null) {
						fYear = parseObjToDate.toString();
					}
				} else {
					docType = invoice.getIsd_docty();
				}
				String invKey = docKeyGenerator.generateGstr6IsdKey(isdGstin, docNo, fYear, docType);
				//}
				invKeyList.add(invKey);
				Gstr6VerticalWebError errObj = new Gstr6VerticalWebError();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedDate(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size();
		} else if (APIConstants.ISDA.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6IsdDetailsDto invoice : gsterrors.getIsdaInvoice()) {
				/*String invKey = distributionRepository.findDocKeyByBatchID(batch.getId(), invoice.getDocnum(),
						batch.getSgstin(), batch.getReturnPeriod());
				if (invKey == null || invKey.isEmpty()) {*/
					String isdGstin = batch.getSgstin();
					String docNo = invoice.getDocnum();
					String idt = invoice.getDocdt();
					String docType = invoice.getIsd_docty();
					if (docType != null && (docType.equalsIgnoreCase("ISDA") || docType.equalsIgnoreCase("ISDURA"))) {
						docType = "RNV";
					} else if (docType != null
							&& (docType.equalsIgnoreCase("ISDCNA") || docType.equalsIgnoreCase("ISDCNURA"))) {
						docType = "RCR";
					} else {
						docType = invoice.getIsd_docty();
					}
					String invKey = docKeyGenerator.generateGstr6IsdKey(isdGstin, docNo, idt, docType);
				//}
				invKeyList.add(invKey);
				Gstr6VerticalWebError errObj = new Gstr6VerticalWebError();
				errObj.setErrorCode(invoice.getError_cd());
				errObj.setErrorType(GSTConstants.ERR);
				errObj.setErrorSource(GSTConstants.GSTN_VALIDATION);
				errObj.setErrorDesc(invoice.getError_msg());
				errObj.setCreatedBy(GSTConstants.SYSTEM);
				errObj.setCreatedDate(LocalDateTime.now());
				errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
				docErrorMap.put(invKey, errObj);
			}
			n = invKeyList.size();
		}
		return new Triplet<List<String>, Map<String, Gstr6VerticalWebError>, Integer>(invKeyList, docErrorMap, n);
	}

	public LocalDate deserialize(String date) throws JsonParseException {
		return LocalDate.parse(date);
	}

	@Override
	public Triplet<List<String>, Map<String, InwardTransDocError>, Integer> response(JsonElement root,
			Gstr1SaveBatchEntity batch, Integer n) {
		// TODO Auto-generated method stub
		return null;
	}

}
