package com.ey.advisory.admin.services.onboarding.gstinfileupload;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;
import com.ey.advisory.admin.data.entities.client.MasterVendorEntity;
import com.ey.advisory.admin.services.gstin.jobs.GSTinConstants;
import com.ey.advisory.common.ProcessingResult;

@Service("VendorErrorConverter")
public class VendorErrorConverter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(VendorErrorConverter.class);

	public List<MasterErrorEntity> convertVendor(List<Object[]> vendorList,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> processingResults,
			Long entityId) {
		List<MasterErrorEntity> vendorsList = new ArrayList<MasterErrorEntity>();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("processingResults List  -> ", processingResults);
		}
		for (Object[] obj : vendorList) {
			MasterErrorEntity data = new MasterErrorEntity();
			String suplierGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : null;
			String legalName = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : null;
			String supplierType = (obj[2] != null)
					? String.valueOf(obj[2].toString()) : null;
			String supplierCode = (obj[3] != null)
					? String.valueOf(obj[3].toString()) : null;
			String outSideIndia = (obj[4] != null)
					? String.valueOf(obj[4].toString()) : null;
			String email = (obj[5] != null) ? String.valueOf(obj[5].toString())
					: null;
			String mobileNo = String.valueOf(obj[6].toString());
			String vendorKey = getVendorValues(obj);

			StringJoiner totalRecord = new StringJoiner(",").add(supplierCode)
					.add(suplierGstinOrPan).add(legalName).add(supplierType)
					.add(outSideIndia).add(email).add(mobileNo);
			data.setErrorKey(vendorKey);
			data.setErrorRecords(totalRecord.toString());
			if (updateFileStatus != null) {
				data.setMasterFileId(updateFileStatus.getId());
				data.setFileName(updateFileStatus.getFileName());
				data.setFileType(updateFileStatus.getFileType());
				data.setStatus(updateFileStatus.getFileStatus());
			}
			data.setCreatedBy("System");
			data.setCreatedOn(LocalDateTime.now());
			List<String> dec = new ArrayList<>();
			List<String> code = new ArrayList<>();
			List<ProcessingResult> list = processingResults.get(vendorKey);
			list.stream().forEach(key -> {
				code.add(key.getCode());
				dec.add(key.getDescription());
			});
			String codes = code.toString();
			String decs = dec.toString();
			data.setErrorCode(codes.substring(1, codes.length() - 1));
			data.setErrorDec(decs.substring(1, decs.length() - 1));
			data.setEntityId(entityId);
			vendorsList.add(data);
		}
		return vendorsList;
	}

	public String getVendorValues(Object[] obj) {
		String outSideIndia = (obj[4] != null)
				? String.valueOf(obj[4].toString()) : "";
		String suplierGstinOrPan = null;
		if (outSideIndia == ""
				|| GSTinConstants.N.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPan = (obj[0] != null)
					? String.valueOf(obj[0].toString()) : "";
		}
		if (GSTinConstants.Y.equalsIgnoreCase(outSideIndia)) {
			return suplierGstinOrPan = (obj[1] != null)
					? String.valueOf(obj[1].toString()) : "";
		}
		return suplierGstinOrPan;
	}

	public List<MasterErrorEntity> convertDuplicateCheckCustomer(
			List<MasterVendorEntity> withDuplicateProcess,
			Gstr1FileStatusEntity updateFileStatus,
			Map<String, List<ProcessingResult>> businessValErrors,
			Long entityId) {

		List<MasterErrorEntity> customers = new ArrayList<MasterErrorEntity>();
		for (MasterVendorEntity obj : withDuplicateProcess) {
			MasterErrorEntity cust = new MasterErrorEntity();

			StringJoiner totalRecord = new StringJoiner(",")
					.add(obj.getSupplierGstinPan()).add(obj.getLegalName())
					.add(obj.getLegalName()).add(obj.getSupplierType())
					.add(obj.getSupplierCode()).add(obj.getOutSideIndia())
					.add(obj.getEmailId()).add(obj.getMobileNum());
			cust.setErrorKey(obj.getVendorKey());
			cust.setErrorRecords(totalRecord.toString());

			if (updateFileStatus != null) {
				cust.setMasterFileId(updateFileStatus.getId());
				cust.setFileName(updateFileStatus.getFileName());
				cust.setFileType(updateFileStatus.getFileType());
				cust.setStatus(updateFileStatus.getFileStatus());
			}
			cust.setCreatedBy("System");
			cust.setCreatedOn(LocalDateTime.now());
			String desc = (obj.getSupplierGstinPan() != null
					&& !obj.getSupplierGstinPan().isEmpty())
							? obj.getSupplierGstinPan() : obj.getLegalName();
			cust.setErrorDec("Duplicate Records of" + desc);
			cust.setErrorCode("ERXXXX");
			cust.setEntityId(entityId);
			customers.add(cust);

		}
		return customers;
	}
}
