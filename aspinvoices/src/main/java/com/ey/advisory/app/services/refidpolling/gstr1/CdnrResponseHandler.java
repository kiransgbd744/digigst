package com.ey.advisory.app.services.refidpolling.gstr1;

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

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;
import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.docs.dto.Gstr1ErrorReport;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.app.services.common.DocumentTypeMapperFactory;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

@Component("CdnrResponseHandler")
public class CdnrResponseHandler implements Gstr1ResponseHandler {
	@Autowired
	@Qualifier("CdnrDefaultDocumentTypeFactoryImpl")
	private DocumentTypeMapperFactory cdnr;

	@Autowired
	@Qualifier("CdnraDefaultDocumentTypeFactoryImpl")
	private DocumentTypeMapperFactory cdnra;

	@Autowired
	@Qualifier("Anx1DocKeyGenerator")
	private Anx1DocKeyGenerator docKeyGenerator;

	public Triplet<List<String>, Map<String, OutwardTransDocError>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();

		SaveGstr1 gstr1 = gson.fromJson(root, SaveGstr1.class);
		Map<String, OutwardTransDocError> docErrorMap = new HashMap<>();
		Gstr1ErrorReport gsterrors = gstr1.getGstr1ErrorReport();
		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());

		if (APIConstants.CDNR.equalsIgnoreCase(batch.getSection())
				&& gsterrors != null) {
			for (CDNRInvoices invoice : gsterrors.getCdnrInvoice()) {
				for (CreditDebitNote docData : invoice
						.getCreditDebitNoteDetails()) {

					String docNo = docData.getCredDebRefVoucherNum();

					String idt = docData.getCredDebRefVoucherDate();
					String[] arr = idt.split("-");

					LocalDate docDate = deserialize(
							arr[2] + "-" + arr[1] + "-" + arr[0]);
					String finYear = GenUtil.getFinYear(docDate);
					String digiGstDocType = null;
					// Since the Doc Type that GSTN returns is different from
					// the
					// doc type that we maintain in the DB, we need to convert
					// the
					// GSTN doc type to the DigiGST doc type.
					digiGstDocType = cdnr
							.mapDocType(docData.getCredDebRefVoucher());
					// Now that we have the SGSTIN, DocNo, DocDate and the
					// DigiGST
					// Doc Type, we can create the doc key.
					String invKey = docKeyGenerator.generateKey(
							batch.getSgstin(), docNo, finYear, digiGstDocType);
					/*
					 * String invKey = docKeyGenerator.generateKey(
					 * batch.getSgstin(), docNo, docDate, digiGstDocType);
					 */
					invKeyList.add(invKey);

					// At this point of creation, we will not have the
					// document header id with us. We only have the document
					// key.
					// At a later point, we will use these keys to fetch the ids
					// of these documents. In order to know which error belongs
					// to which document, we need to map this error object
					// to the corresponding document key.
					OutwardTransDocError errObj = new OutwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(now);
					errObj.setType(GSTConstants.BUSINESS_VALIDATIONS);

					// Map the document key with the associated error, so that
					// after fetching the actual document ids for these
					// documents,
					// we can update these error objects with the ids.
					docErrorMap.put(invKey, errObj);

				}
				n = invoice.getCreditDebitNoteDetails().size();
			}
		} else if (APIConstants.CDNRA.equalsIgnoreCase(batch.getSection())
				&& gsterrors != null) {
			for (CDNRInvoices invoice : gsterrors.getCdnraInvoice()) {
				for (CreditDebitNote docData : invoice
						.getCreditDebitNoteDetails()) {
					String docNo = docData.getCredDebRefVoucherNum();

					String idt = docData.getCredDebRefVoucherDate();
					String[] arr = idt.split("-");

					LocalDate docDate = deserialize(
							arr[2] + "-" + arr[1] + "-" + arr[0]);
					String finYear = GenUtil.getFinYear(docDate);
					String digiGstDocType = null;
					// Since the Doc Type that GSTN returns is different from
					// the
					// doc type that we maintain in the DB, we need to convert
					// the
					// GSTN doc type to the DigiGST doc type.
					digiGstDocType = cdnra
							.mapDocType(docData.getCredDebRefVoucher());
					// Now that we have the SGSTIN, DocNo, DocDate and the
					// DigiGST
					// Doc Type, we can create the doc key.
					String invKey = docKeyGenerator.generateKey(
							batch.getSgstin(), docNo, finYear, digiGstDocType);
					/*
					 * String invKey = docKeyGenerator.generateKey(
					 * batch.getSgstin(), docNo, docDate, digiGstDocType);
					 */
					invKeyList.add(invKey);

					// At this point of creation, we will not have the
					// document header id with us. We only have the document
					// key.
					// At a later point, we will use these keys to fetch the ids
					// of these documents. In order to know which error belongs
					// to which document, we need to map this error object
					// to the corresponding document key.
					OutwardTransDocError errObj = new OutwardTransDocError();
					errObj.setErrorCode(invoice.getError_cd());
					errObj.setErrorType(GSTConstants.ERR);
					errObj.setSource(GSTConstants.GSTN_VALIDATION);
					errObj.setErrorDesc(invoice.getError_msg());
					errObj.setCreatedBy(GSTConstants.SYSTEM);
					errObj.setCreatedDate(now);
					errObj.setType(GSTConstants.BUSINESS_VALIDATIONS);

					// Map the document key with the associated error, so that
					// after fetching the actual document ids for these
					// documents,
					// we can update these error objects with the ids.
					docErrorMap.put(invKey, errObj);

				}
				n = invoice.getCreditDebitNoteDetails().size();
			}
		}
		return new Triplet<List<String>, Map<String, OutwardTransDocError>, Integer>(
				invKeyList, docErrorMap, n);

	}

	public LocalDate deserialize(String date) throws JsonParseException {
		return LocalDate.parse(date);
	}

	@Override
	public Integer SaveResponse(JsonElement root, Gstr1SaveBatchEntity batch,
			Integer n) {
		// TODO Auto-generated method stub
		return null;
	}

}
