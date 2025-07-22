package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.repositories.master.NatureDocTypeRepo;
import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredInvEntity;
import com.ey.advisory.app.data.entities.client.Gstr1InvoiceFileUploadEntity;
import com.ey.advisory.app.services.common.WebUploadKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Service("SRFileToInvoiceDetailsConvertion")
public class SRFileToInvoiceDetailsConvertion {
	@Autowired
	@Qualifier("WebUploadKeyGenerator")
	private WebUploadKeyGenerator webUploadKeyGenerator;

	@Autowired
	@Qualifier("NatureDocTypeRepo")
	private NatureDocTypeRepo natureDocTypeRepo;

	private final static String WEB_UPLOAD_KEY = "|";

	public List<Gstr1AsEnteredInvEntity> convertSRFileToExcelInvoiceDoc(
			List<Object[]> listOfInvoices, Gstr1FileStatusEntity fileStatus) {
		List<Gstr1AsEnteredInvEntity> list = new ArrayList<>();

		for (Object[] obj : listOfInvoices) {
			Gstr1AsEnteredInvEntity invoice = new Gstr1AsEnteredInvEntity();
			String sgstin = getValues(obj[0]);
			String returnPeriod = getValues(obj[1]);
			String serialNum = getValues(obj[2]);
			String natureOfDoc = getDocValues(obj[3]);
			String docFrom = getValues(obj[4]);
			LocalDate localFromDate = DateUtil.parseObjToDate(docFrom);
			String docTo = getValues(obj[5]);
			LocalDate localToDate = DateUtil.parseObjToDate(docTo);
			String totalNum = getValues(obj[6]);
			String cancelled = getValues(obj[7]);
			String neetNum = getValues(obj[8]);
			String invKey = generateInvKey(obj);
			if (invKey != null && invKey.length() > 2200) {
				invKey = invKey.substring(0, 2200);
			}
			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int months = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((months <= 12 && months >= 01)
							&& (year <= 9999 && year >= 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}
			invoice.setNatureOfDocument(natureOfDoc);
			invoice.setSgstin(sgstin);
			invoice.setDerivedRetPeriod(derivedRePeroid);
			invoice.setReturnPeriod(returnPeriod);
			if (localFromDate != null) {
				invoice.setFrom(String.valueOf(localFromDate));
			} else {
				invoice.setFrom(docFrom);
			}
			if (localToDate != null) {
				invoice.setTo(String.valueOf(localToDate));
			} else {
				invoice.setTo(docTo);
			}
			invoice.setSerialNo(serialNum);
			invoice.setTotalNumber(totalNum);
			invoice.setCancelled(cancelled);
			invoice.setNetNumber(neetNum);
			invoice.setInvoiceKey(invKey);
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			invoice.setCreatedOn(convertNow);
			if (fileStatus != null) {
				invoice.setFileId(fileStatus.getId());
				invoice.setCreatedBy(fileStatus.getUpdatedBy());
				if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					invoice.setDataOriginType("E");
				} else if (fileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					invoice.setDataOriginType("A");
				}
			}

			list.add(invoice);
		}
		return list;
	}

	private String getDocValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;
		if (value != null && value.length() > 110) {
			return value.substring(0, 110).trim();
		}
		return value;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public List<Gstr1InvoiceFileUploadEntity> convertSRFileToInvoiceDoc(
			List<Gstr1AsEnteredInvEntity> busProcessRecords,
			Gstr1FileStatusEntity fileStatus) {
		List<Gstr1InvoiceFileUploadEntity> invoices = new ArrayList<>();
		Gstr1InvoiceFileUploadEntity invoice = null;

		for (Gstr1AsEnteredInvEntity obj : busProcessRecords) {
			invoice = new Gstr1InvoiceFileUploadEntity();
			invoice.setSgstin(obj.getSgstin());
			if (fileStatus != null) {
				invoice.setFileId(fileStatus.getId());
			}
			invoice.setReturnPeriod(obj.getReturnPeriod());
			Integer deriRetPeriod = 0;
			if (obj.getReturnPeriod() != null
					&& !obj.getReturnPeriod().isEmpty()) {
				deriRetPeriod = GenUtil
						.convertTaxPeriodToInt(obj.getReturnPeriod());
			}

			invoice.setSerialNo(Integer.parseInt(obj.getSerialNo()));
			invoice.setNatureOfDocument(obj.getNatureOfDocument());
			invoice.setFrom(obj.getFrom());
			invoice.setTo(obj.getTo());
			Integer total = Integer.parseInt(obj.getTotalNumber());
			invoice.setTotalNumber(total);
			Integer can = Integer.parseInt(obj.getCancelled());
			invoice.setCancelled(can);
			Integer netNum = Integer.parseInt(obj.getNetNumber());
			invoice.setNetNumber(netNum);
			invoice.setInvoiceKey(obj.getInvoiceKey());
			invoice.setDerivedRetPeriod(deriRetPeriod);
			invoice.setInfo(obj.isInfo());
			invoice.setDataOriginType(obj.getDataOriginType());
			invoice.setAsEnterId(obj.getId());

			if (total == 0 && netNum == 0 && can == 0) {
				invoice.setDelete(true);
			} else {
				invoice.setDelete(obj.isDelete());
			}
			invoice.setCreatedBy(obj.getCreatedBy());
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			invoice.setCreatedOn(convertNow);
			invoices.add(invoice);
		}
		return invoices;
	}

	public String generateInvKey(Object[] obj) {
		String sgstin = (obj[0] != null) ? String.valueOf(obj[0]).trim() : "";
		String retPeriod = (obj[1] != null) ? String.valueOf(obj[1]).trim()
				: "";
		String serialNum = (obj[2] != null) ? String.valueOf(obj[2]).trim()
				: "";
		String from = (obj[4] != null) ? String.valueOf(obj[4]).trim() : "";
		String to = (obj[5] != null) ? String.valueOf(obj[5]).trim() : "";
		return new StringJoiner(WEB_UPLOAD_KEY).add(sgstin).add(retPeriod)
				.add(serialNum).add(from).add(to).toString();
	}

}
