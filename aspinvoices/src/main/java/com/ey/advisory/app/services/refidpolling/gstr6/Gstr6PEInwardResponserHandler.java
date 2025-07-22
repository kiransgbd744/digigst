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
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.gstr2.InwardTransDocRepository;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2baInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6CdnaNtData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6ErrorReport;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.services.docs.gstr2.DefaultInwardTransDocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * 
 * @author SriBhavya
 *
 */
@Component("Gstr6PEInwardResponserHandler")
public class Gstr6PEInwardResponserHandler implements Gstr6ResponseHandler {

	public static final String DASH = "-";

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DefaultInwardTransDocKeyGenerator docKeyGenerator;

	@Autowired
	@Qualifier("InwardTransDocRepository")
	private InwardTransDocRepository inwardTransDocRepository;

	@Override
	public Triplet<List<String>, Map<String, InwardTransDocError>, Integer> response(JsonElement root,
			Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();
		SaveGstr6 gstr6 = gson.fromJson(root, SaveGstr6.class);
		Map<String, InwardTransDocError> docErrorMap = new HashMap<>();
		Gstr6ErrorReport gsterrors = gstr6.getGstr6ErrorReport();
		if (APIConstants.B2B.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6B2bDto invoice : gsterrors.getB2bInvoice()) {
				for (Gstr6B2bInvoiceData docData : invoice.getInv()) {
					/*String invKey = inwardTransDocRepository.findDocKeyByBatchID(batch.getId(), batch.getSection(),
							docData.getInum(), batch.getSgstin(), batch.getReturnPeriod());
					if (invKey == null || invKey.isEmpty()) {*/
						InwardTransDocument transData = new InwardTransDocument();
						transData.setDocNo(docData.getInum());
						transData.setSgstin(invoice.getCgstin());
						transData.setCgstin(batch.getSgstin());
						transData.setDocType(GSTConstants.INV);
						String idt = docData.getIdt();
						String[] arr = idt.split(DASH);
						LocalDate docDate = deserialize(arr[2] + DASH + arr[1] + DASH + arr[0]);
						transData.setFinYear(GenUtil.getFinYear(docDate));
						String invKey = docKeyGenerator.generateKey(transData);
					//}
					invKeyList.add(invKey);
					InwardTransDocError errObj = new InwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(LocalDateTime.now());
					errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
					docErrorMap.put(invKey, errObj);
				}
				n = invoice.getInv().size();
			}
		} else if (APIConstants.B2BA.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6B2baDto invoice : gsterrors.getB2baInvoice()) {
				for (Gstr6B2baInvoiceData docData : invoice.getInv()) {
					/*String invKey = inwardTransDocRepository.findDocKeyByBatchID(batch.getId(), batch.getSection(),
							docData.getInum(), batch.getSgstin(), batch.getReturnPeriod());
					if (invKey == null || invKey.isEmpty()) {*/
						InwardTransDocument transData = new InwardTransDocument();
						transData.setDocNo(docData.getInum());
						transData.setSgstin(invoice.getCgstin());
						transData.setCgstin(batch.getSgstin());
						transData.setDocType(GSTConstants.RNV);
						String idt = docData.getIdt();
						String[] arr = idt.split(DASH);
						LocalDate docDate = deserialize(arr[2] + DASH + arr[1] + DASH + arr[0]);
						transData.setFinYear(GenUtil.getFinYear(docDate));
						String invKey = docKeyGenerator.generateKey(transData);
					//}
					invKeyList.add(invKey);
					InwardTransDocError errObj = new InwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(LocalDateTime.now());
					errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
					docErrorMap.put(invKey, errObj);
				}
				n = invoice.getInv().size();
			}
		} else if (APIConstants.CDN.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6CdnDto invoice : gsterrors.getCdnInvoice()) {
				for (Gstr6CdnNtData docData : invoice.getNt()) {
					/*String invKey = inwardTransDocRepository.findDocKeyByBatchID(batch.getId(), batch.getSection(),
							docData.getNtnum(), batch.getSgstin(), batch.getReturnPeriod());
					if (invKey == null || invKey.isEmpty()) {*/
						InwardTransDocument transData = new InwardTransDocument();
						transData.setDocNo(docData.getNtnum());
						transData.setSgstin(invoice.getCgstin());
						transData.setCgstin(batch.getSgstin());
						String dType = docData.getNtty();
						if (dType != null && dType.equalsIgnoreCase("C")) {
							transData.setDocType("CR");
						} else if (dType != null && dType.equalsIgnoreCase("D")) {
							transData.setDocType("DR");
						} else {
							transData.setDocType(docData.getNtty());
						}
						String idt = docData.getNtdt();
						String[] arr = idt.split(DASH);
						LocalDate docDate = deserialize(arr[2] + DASH + arr[1] + DASH + arr[0]);
						transData.setFinYear(GenUtil.getFinYear(docDate));
						String invKey = docKeyGenerator.generateKey(transData);
					//}
					invKeyList.add(invKey);
					InwardTransDocError errObj = new InwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(LocalDateTime.now());
					errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
					docErrorMap.put(invKey, errObj);
				}
			}
			n = invKeyList.size();
		} else if (APIConstants.CDNA.equalsIgnoreCase(batch.getSection())) {
			for (Gstr6CdnaDto invoice : gsterrors.getCdnaInvoice()) {
				for (Gstr6CdnaNtData docData : invoice.getNt()) {
					/*String invKey = inwardTransDocRepository.findDocKeyByBatchID(batch.getId(), batch.getSection(),
							docData.getNtnum(), batch.getSgstin(), batch.getReturnPeriod());
					if (invKey == null || invKey.isEmpty()) {*/
						InwardTransDocument transData = new InwardTransDocument();
						transData.setDocNo(docData.getNtnum());
						transData.setSgstin(invoice.getCgstin());
						transData.setCgstin(batch.getSgstin());
						String dType = docData.getNtty();
						if (dType != null && dType.equalsIgnoreCase("C")) {
							transData.setDocType("RCR");
						} else if (dType != null && dType.equalsIgnoreCase("D")) {
							transData.setDocType("RDR");
						} else {
							transData.setDocType(docData.getNtty());
						}
						String idt = docData.getNtdt();
						String[] arr = idt.split(DASH);
						LocalDate docDate = deserialize(arr[2] + DASH + arr[1] + DASH + arr[0]);
						transData.setFinYear(GenUtil.getFinYear(docDate));
						String invKey = docKeyGenerator.generateKey(transData);
					//}
					invKeyList.add(invKey);
					InwardTransDocError errObj = new InwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(LocalDateTime.now());
					errObj.setErrorType(GSTConstants.BUSINESS_VALIDATIONS);
					docErrorMap.put(invKey, errObj);
				}
			}
			n = invKeyList.size(); 
		}
		return new Triplet<List<String>, Map<String, InwardTransDocError>, Integer>(invKeyList, docErrorMap, n);
	}

	public LocalDate deserialize(String date) throws JsonParseException {
		return LocalDate.parse(date);
	}

	@Override
	public Triplet<List<String>, Map<String, Gstr6VerticalWebError>, Integer> isdResponse(JsonElement root,
			Gstr1SaveBatchEntity batch, Integer n) {
		// TODO Auto-generated method stub
		return null;
	}

}
