package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("Gstr6DistrbtnExcelConvertion")
public class Gstr6DistrbtnExcelConvertion {

	public List<Gstr6DistributionExcelEntity> convertProduct(
			List<Object[]> DistributonList,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr6DistributionExcelEntity> distribution = new ArrayList<>();

		Gstr6DistributionExcelEntity distrbtnExcel = null;
		for (Object[] obj : DistributonList) {
			distrbtnExcel = new Gstr6DistributionExcelEntity();

			String returnPeriod = (obj[0] != null
					&& !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]) : null;

			String derivedRePeroid = null;
			if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
						Integer derivedRetPeriod = GenUtil
								.convertTaxPeriodToInt(returnPeriod);
						derivedRePeroid = String.valueOf(derivedRetPeriod);
					}
				}
			}

			String isdGstin = (obj[1] != null
					&& !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]) : null;

			String recepGstin = (obj[2] != null
					&& !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]) : null;

			String stateCode = (obj[3] != null
					&& !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]) : null;

			String origRecpGstin = (obj[4] != null
					&& !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]) : null;

			String OrigsateCode = (obj[5] != null
					&& !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]) : null;

			String docType = (obj[6] != null
					&& !obj[6].toString().trim().isEmpty())
							? String.valueOf(obj[6]) : null;

			String supplyType = (obj[7] != null
					&& !obj[7].toString().trim().isEmpty())
							? String.valueOf(obj[7]) : null;

							obj[8] = CommonUtility.singleQuoteCheck(obj[8]); 
							obj[8] = CommonUtility.exponentialAndZeroCheck(obj[8]);
							
			String docNumber = (obj[8] != null
					&& !obj[8].toString().trim().isEmpty())
							? String.valueOf(obj[8]) : null;

			String docDate = (obj[9] != null
					&& !obj[9].toString().trim().isEmpty())
							? String.valueOf(obj[9]) : null;

			// LocalDate localDocDate = DateUtil .parseObjToDate(docDate);
			LocalDate localDocDate = null;
			if (docDate != null) {
				localDocDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(docDate));
			}
			
			obj[10] = CommonUtility.singleQuoteCheck(obj[10]); 
			obj[10] = CommonUtility.exponentialAndZeroCheck(obj[10]);
			String OrigDocNumber = (obj[10] != null
					&& !obj[10].toString().trim().isEmpty())
							? String.valueOf(obj[10]) : null;

			String OrigDocDate = (obj[11] != null
					&& !obj[11].toString().trim().isEmpty())
							? String.valueOf(obj[11]) : null;

			// LocalDate localOrigDocNumber = DateUtil
			// .parseObjToDate(OrigDocDate);
			LocalDate localOrigDocNumber = null;
			if (OrigDocDate != null) {
				localOrigDocNumber = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(OrigDocDate));
			}
			String OrigCrNoteNumber = (obj[12] != null
					&& !obj[12].toString().trim().isEmpty())
							? String.valueOf(obj[12]) : null;

			String OrigCrNoteDate = (obj[13] != null
					&& !obj[13].toString().trim().isEmpty())
							? String.valueOf(obj[13]) : null;

			// LocalDate localOrigCrNoteDate = DateUtil
			// .parseObjToDate(OrigCrNoteDate);
			LocalDate localOrigCrNoteDate = null;
			if (OrigCrNoteDate != null) {
				localOrigCrNoteDate = EYDateUtil.toUTCDateTimeFromLocal(
						DateUtil.parseObjToDate(OrigCrNoteDate));
			}
			String eligibleIndicator = getValues(obj[14]);
			String igstAsIgst = getValues(obj[15]);
			String igstAsSgst = getValues(obj[16]);
			String igstAsCgst = getValues(obj[17]);
			String sgstAsSgst = getValues(obj[18]);
			String sgstAsIgst = getValues(obj[19]);
			String cgstAsCgst = getValues(obj[20]);
			String cgstAsIgst = getValues(obj[21]);
			String cessAmount = getValues(obj[22]);

			String processKey = getFileProcessedKey(obj);
			// prod.setEntityId(entityId);
			if (updateFileStatus != null) {
				distrbtnExcel.setFileId(updateFileStatus.getId());
			//	prod.setCreatedBy(updateFileStatus.getUpdatedBy());

				// distrbtnExcel.setFileName(updateFileStatus.getFileName());
				
				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					distrbtnExcel.setDataOriginType("E");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					distrbtnExcel.setDataOriginType("A");
				}
				
			}
			
			
			distrbtnExcel.setRetPeriod(returnPeriod);
			distrbtnExcel.setIsdGstin(isdGstin);
			distrbtnExcel.setCustGstin((recepGstin));
			distrbtnExcel.setStateCode(stateCode);
			distrbtnExcel.setOrgCustGstin(origRecpGstin);
			distrbtnExcel.setOrgStateCode(OrigsateCode);

			distrbtnExcel.setDocType(docType);
			distrbtnExcel.setSupplyType(supplyType);
			distrbtnExcel.setDocNum(docNumber);

			distrbtnExcel.setDocDate(localDocDate);
			distrbtnExcel.setOrgDocNum(OrigDocNumber);
			distrbtnExcel.setOrgDocDate(localOrigDocNumber);

			distrbtnExcel.setOrgCrDate(localOrigCrNoteDate);

			distrbtnExcel.setOrgCrNum(OrigCrNoteNumber);
			distrbtnExcel.setEligibleIndicator(eligibleIndicator);
			distrbtnExcel.setIgstAsIgst(igstAsIgst);
			distrbtnExcel.setIgstAsSgst(igstAsSgst);
			distrbtnExcel.setIgstAsCgst(igstAsCgst);
			distrbtnExcel.setSgstAsIgst(sgstAsIgst);
			distrbtnExcel.setSgstAsSgst(sgstAsSgst);
			distrbtnExcel.setCgstAsIgst(cgstAsIgst);
			distrbtnExcel.setCgstAsCgst(cgstAsCgst);
			distrbtnExcel.setCessAmount(cessAmount);
			distrbtnExcel.setDerived_Ret_period(derivedRePeroid);
			distrbtnExcel.setProcessKey(processKey);
			distrbtnExcel.setCreatedBy("System");
		
			LocalDateTime convertNow = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			
			distrbtnExcel.setCreatedOn(convertNow);

			distribution.add(distrbtnExcel);

		}
		return distribution;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	public String getFileProcessedKey(Object[] obj) {

		String isdGstn = (obj[1] != null && !obj[1].toString().trim().isEmpty())
				? String.valueOf(obj[1]) : null;

		String docType = (obj[6] != null && !obj[6].toString().trim().isEmpty())
				? String.valueOf(obj[6]) : null;
		String docNumber = (obj[8] != null) ? String.valueOf(obj[8]) : null;

		String docYear = (obj[9] != null && !obj[9].toString().trim().isEmpty())
				? String.valueOf(obj[9]) : null;

		LocalDate parseObjToDate = DateUtil.parseObjToDate(docYear);

		LocalDate localOrigDocDate = EYDateUtil
				.toUTCDateTimeFromLocal(parseObjToDate);
		
		String fYear = null ;
		if(localOrigDocDate != null){
			 fYear = localOrigDocDate.toString();
		}
		return new StringJoiner(GSTConstants.WEB_UPLOAD_KEY).add(isdGstn)
				.add(docType).add(docNumber).add(fYear).toString();
	}
}
