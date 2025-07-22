package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterCustomerEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;
import com.ey.advisory.common.ProcessingResult;

/**
 * 
 * @author Mahesh.Golla
 *
 */
/*
 * This service is responsible for convert the object of master customer data
 * from excel into related formats
 */

@Service("MasterDataToCustomerErrorConverter")
public class MasterDataToCustomerErrorConverter {

	public List<MasterErrorEntity> convertCustomer(List<Object[]> errRecords,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults, Long entityId) {

		List<MasterErrorEntity> customers = new ArrayList<MasterErrorEntity>();

		for (Object[] obj : errRecords) {
			MasterErrorEntity cust = new MasterErrorEntity();

			String recipientGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : null;
			String legalName = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : null;
			String tradeName = (obj[2] != null)
					? String.valueOf(obj[2].toString()) : null;
			String recipientType = (obj[3] != null)
					? String.valueOf(obj[3].toString()) : null;
			String recipientCode = (obj[4] != null)
					? String.valueOf(obj[4].toString()) : null;
			String outsideIndia = (obj[5] != null)
					? String.valueOf(obj[5].toString()) : null;
			String email = (obj[6] != null) ? String.valueOf(obj[6].toString())
					: null;
			String mobileNo = (obj[7] != null)
					? String.valueOf(obj[7].toString()) : null;
			String customerKey = getCustomerValues(obj);

			StringJoiner totalRecord = new StringJoiner(",")
					.add(recipientGstinOrPan).add(legalName).add(tradeName)
					.add(recipientType).add(recipientCode).add(outsideIndia)
					.add(email).add(mobileNo);

			cust.setErrorKey(customerKey);
			cust.setErrorRecords(totalRecord.toString());
			if (updateFileStatus != null) {
				cust.setMasterFileId(updateFileStatus.getId());
				cust.setFileName(updateFileStatus.getFileName());
				cust.setFileType(updateFileStatus.getFileType());
				cust.setStatus(updateFileStatus.getFileStatus());
			}
			cust.setCreatedBy("System");
			cust.setCreatedOn(LocalDateTime.now());
			List<String> dec = new ArrayList<>();
			List<String> code = new ArrayList<>();
			List<ProcessingResult> list = processingResults.get(customerKey);
			list.stream().forEach(key -> {
				code.add(key.getCode());
				dec.add(key.getDescription());
			});
			String codes = code.toString();
			String decs = dec.toString();
			cust.setErrorCode(codes.substring(1, codes.length() - 1));
			cust.setErrorDec(decs.substring(1, decs.length() - 1));
			cust.setEntityId(entityId);
			customers.add(cust);
		}
		return customers;
	}

	public String getCustomerValues(Object[] obj) {
		String outSideIndia = (obj[5] != null)
				? String.valueOf(obj[5].toString()) : "";
		String suplierGstinOrPanOrLegalName = null;
		if (outSideIndia == ""
				|| GSTinConstants.N.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPanOrLegalName = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : "";
		}
		if (GSTinConstants.Y.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPanOrLegalName = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : "";
		}
		return suplierGstinOrPanOrLegalName;
	}

	public List<MasterErrorEntity> convertDuplicateCheckCustomer(
			List<MasterCustomerEntity> withDuplicateProcess,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> businessValErrors,
			Long entityId) {
		
		List<MasterErrorEntity> customers = new ArrayList<MasterErrorEntity>();
		for (MasterCustomerEntity obj : withDuplicateProcess) {
			MasterErrorEntity cust = new MasterErrorEntity();
			

			StringJoiner totalRecord = new StringJoiner(",")
					.add(obj.getRecipientGstnOrPan()).add(obj.getLegalName())
					.add(obj.getTradeName()).add(obj.getRecipientType())
					.add(obj.getRecipientCode()).add(obj.getOutSideIndia())
					.add(obj.getEmailId()).add(obj.getMobileNum());
			cust.setErrorKey(obj.getCustKey());
			cust.setErrorRecords(totalRecord.toString());
			
		if (updateFileStatus != null) {
			cust.setMasterFileId(updateFileStatus.getId());
			cust.setFileName(updateFileStatus.getFileName());
			cust.setFileType(updateFileStatus.getFileType());
			cust.setStatus(updateFileStatus.getFileStatus());
		}
		cust.setCreatedBy("System");
		cust.setCreatedOn(LocalDateTime.now());
		String desc = (obj.getRecipientGstnOrPan() != null && 
				!obj.getRecipientGstnOrPan().isEmpty()) ? 
						obj.getRecipientGstnOrPan() : obj.getLegalName();
		cust.setErrorDec("Duplicate Records of" + desc);
		cust.setErrorCode("ERXXXX");
		cust.setEntityId(entityId);
		customers.add(cust);
		
		}
		return customers;
	}
}
