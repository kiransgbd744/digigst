package com.ey.advisory.app.services.docs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.admin.data.entities.client.Gstr6DistributionEntity;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("SRFileToGstr6DistbtnDetailsConvertion")
public class SRFileToGstr6DistbtnDetailsConvertion {

	private final static String WEB_UPLOAD_KEY = "|";

	/*
	 * private static final Logger LOGGER = LoggerFactory
	 * .getLogger(SRFileToGstr6DistbtnDetailsConvertion.class);
	 */
	public List<Gstr6DistributionEntity> convertProduct(
			List<Gstr6DistributionExcelEntity> productList,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr6DistributionEntity> product = new ArrayList<>();

		for (Gstr6DistributionExcelEntity obj : productList) {
			Gstr6DistributionEntity prod = new Gstr6DistributionEntity();

			String returnPeriod = (obj.getRetPeriod() != null
					&& !obj.getRetPeriod().toString().trim().isEmpty())
							? String.valueOf(obj.getRetPeriod()) : null;

			int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
			String isdGstin = (obj.getIsdGstin() != null
					&& !obj.getIsdGstin().toString().trim().isEmpty())
							? String.valueOf(obj.getIsdGstin()) : null;

			String recepGstin = (obj.getCustGstin() != null
					&& !obj.getCustGstin().toString().trim().isEmpty())
							? String.valueOf(obj.getCustGstin()) : null;

			String stateCode = (obj.getStateCode() != null
					&& !obj.getStateCode().toString().trim().isEmpty())
							? String.valueOf(obj.getStateCode()) : null;

			String origRecpGstin = (obj.getOrgCustGstin() != null
					&& !obj.getOrgCustGstin().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgCustGstin()) : null;

			String OrigsateCode = (obj.getOrgStateCode() != null
					&& !obj.getOrgStateCode().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgStateCode()) : null;

			String docType = (obj.getDocType() != null
					&& !obj.getDocType().toString().trim().isEmpty())
							? String.valueOf(obj.getDocType()) : null;

			String supplyType = (obj.getSupplyType() != null
					&& !obj.getSupplyType().toString().trim().isEmpty())
							? String.valueOf(obj.getSupplyType()) : null;

			String docNumber = (obj.getDocNum() != null
					&& !obj.getDocNum().toString().trim().isEmpty())
							? String.valueOf(obj.getDocNum()) : null;

			/*String docDate = (obj.getDocDate() != null
					&& !obj.getDocDate().toString().trim().isEmpty())
							? String.valueOf(obj.getDocDate()) : null;*/
							LocalDate localDocDate = obj.getDocDate();
			// LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
		/*	LocalDate localDocDate = null;
			if (docDate != null) {
				localDocDate = EYDateUtil.toUTCDateTimeFromLocal(docDate);
			}*/

			String OrigDocNumber = (obj.getOrgDocNum() != null
					&& !obj.getOrgDocNum().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgDocNum()) : null;

			/*String OrigDocDate = (obj.getOrgDocDate() != null
					&& !obj.getOrgDocDate().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgDocDate()) : null;*/
							LocalDate localOrigDocDate = obj.getOrgDocDate();
			// LocalDate localOrigDocDate =
			// DateUtil.parseObjToDate(OrigDocDate);
		/*	LocalDate localOrigDocDate = null;
			if (OrigDocDate != null) {
				localOrigDocDate = EYDateUtil.toUTCDateTimeFromLocal(OrigDocDate);
			}*/
			String OrigCrNoteNumber = (obj.getOrgCrNum() != null
					&& !obj.getOrgCrNum().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgCrNum()) : null;

		/*	String OrigCrNoteDate = (obj.getOrgCrDate() != null
					&& !obj.getOrgCrDate().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgCrDate()) : null;*/
							LocalDate localOrigCrNoteDate = obj.getOrgCrDate();
			/*
			 * LocalDate localOrigCrNoteDate = DateUtil
			 * .parseObjToDate(OrigCrNoteDate);
			 */

/*			LocalDate localOrigCrNoteDate = null;
			if (OrigCrNoteDate != null) {
				localOrigCrNoteDate = EYDateUtil.toUTCDateTimeFromLocal(OrigCrNoteDate);
			}
*/			String eligibleIndicator = (obj.getEligibleIndicator() != null
					&& !obj.getEligibleIndicator().toString().trim().isEmpty())
							? String.valueOf(obj.getEligibleIndicator()) : null;

			BigDecimal igstAsIgst = BigDecimal.ZERO;
			if (obj.getIgstAsIgst() != null
					&& !obj.getIgstAsIgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getIgstAsIgst()));
				igstAsIgst = new BigDecimal(rateStr);
			}

			BigDecimal igstAsSgst = BigDecimal.ZERO;
			if (obj.getIgstAsSgst() != null
					&& !obj.getIgstAsSgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getIgstAsSgst()));
				igstAsSgst = new BigDecimal(rateStr);
			}

			BigDecimal igstAsCgst = BigDecimal.ZERO;
			if (obj.getIgstAsCgst() != null
					&& !obj.getIgstAsCgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getIgstAsCgst()));
				igstAsCgst = new BigDecimal(rateStr);
			}

			BigDecimal sgstAsSgst = BigDecimal.ZERO;
			if (obj.getSgstAsSgst() != null
					&& !obj.getSgstAsSgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getSgstAsSgst()));
				sgstAsSgst = new BigDecimal(rateStr);
			}

			BigDecimal sgstAsIgst = BigDecimal.ZERO;
			if (obj.getSgstAsIgst() != null
					&& !obj.getSgstAsIgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getSgstAsIgst()));
				sgstAsIgst = new BigDecimal(rateStr);
			}

			BigDecimal cgstAsCgst = BigDecimal.ZERO;
			if (obj.getCgstAsCgst() != null
					&& !obj.getCgstAsCgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getCgstAsCgst()));
				cgstAsCgst = new BigDecimal(rateStr);
			}

			BigDecimal cgstAsIgst = BigDecimal.ZERO;
			if (obj.getCgstAsIgst() != null
					&& !obj.getCgstAsIgst().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getCgstAsIgst()));
				cgstAsIgst = new BigDecimal(rateStr);
			}

			BigDecimal cessAmount = BigDecimal.ZERO;
			if (obj.getCessAmount() != null
					&& !obj.getCessAmount().toString().trim().isEmpty()) {
				String rateStr = (String.valueOf(obj.getCessAmount()));
				cessAmount = new BigDecimal(rateStr);
			}

		//	String processKey = getProductValues(obj.getProcessKey());
			// prod.setEntityId(entityId);
			if (updateFileStatus != null) {
				prod.setFileId(updateFileStatus.getId());

				if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.WEB_UPLOAD)) {
					prod.setDataOriginType("E");
				} else if (updateFileStatus.getSource()
						.equalsIgnoreCase(JobStatusConstants.ERP)) {
					prod.setDataOriginType("A");
				}
				
				// prod.setFileName(updateFileStatus.getFileName());
			}

			prod.setAsEnterTableId(obj.getId());
			prod.setReturnPeriod(returnPeriod);
			prod.setIsdGstin(isdGstin);
			prod.setRecipientGSTIN(recepGstin);
			prod.setStateCode(stateCode);
			prod.setOriginalRecipeintGstin(origRecpGstin);
			prod.setOriginalStatecode(OrigsateCode);

			prod.setDocumentType(docType);
			prod.setSupplyType(supplyType);
			prod.setDocNum(docNumber);

			prod.setDocDate(localDocDate);

			prod.setOrigDocNumber(OrigDocNumber);

			prod.setOrigDocDate(localOrigDocDate);
			prod.setOrigCrNoteDate(localOrigCrNoteDate);
			prod.setDelete(obj.isDelete());
			prod.setInfo(obj.isInfo());
			// prod.setProcessed(obj.isProcessed());
			prod.setError(obj.isError());
			prod.setOrigCrNoteNumber(OrigCrNoteNumber);
			prod.setEligibleIndicator(eligibleIndicator);
			prod.setIgstAsIgst(igstAsIgst);
			prod.setIgstAsSgst(igstAsSgst);
			prod.setIgstAsCgst(igstAsCgst);
			prod.setSgstAsIgst(sgstAsIgst);
			prod.setSgstAsSgst(sgstAsSgst);
			prod.setCgstAsIgst(cgstAsIgst);
			prod.setCgstAsCgst(cgstAsCgst);
			prod.setCessAmount(cessAmount);
			prod.setDerivedRetPeriod(derivedRetPeriod);
			prod.setProcessKey(obj.getProcessKey());
			prod.setCreatedBy("System");
			prod.setCreatedOn(LocalDateTime.now());

			product.add(prod);
			/*
			 * if (LOGGER.isDebugEnabled()) {
			 * LOGGER.debug("ProductFileCoversion List  -> ", prod, product); }
			 */
		}
		return product;
	}

	private String getValues(Object obj) {
		String value = (obj != null) ? String.valueOf(obj).trim() : null;

		if (value != null && value.length() > 100) {
			return value.substring(0, 100).trim();
		}
		return value;
	}

	/*public String getProductValues(Gstr6DistributionExcelEntity obj) {
		String isdGstn = (obj.getIsdGstin() != null
				&& !obj.getIsdGstin().toString().trim().isEmpty())
						? String.valueOf(obj.getIsdGstin()) : null;

		String docType = (obj.getDocType() != null
				&& !obj.getDocType().toString().trim().isEmpty())
						? String.valueOf(obj.getDocType()) : null;
		String docNumber = (obj.getDocNum() != null)
				? String.valueOf(obj.getDocNum()) : null;

		String fYear = (obj.getDocDate() != null
				&& !obj.getDocDate().toString().trim().isEmpty())
						? String.valueOf(obj.getDocDate()) : null;

		
				String docYear = (obj.getDocDate() != null
				&& !obj.getDocDate().toString().trim().isEmpty())
						? String.valueOf(obj.getDocDate()) : null;

						LocalDate localOrigDocDate = obj.getDocDate();
	//	LocalDate parseObjToDate = DateUtil.parseObjToDate(docYear);
		// LocalDate localOrigCrNoteDate = null;
	//	LocalDate localOrigDocDate = EYDateUtil.toUTCDateTimeFromLocal(docYear);

		String fYear = localOrigDocDate.toString();

		return new StringJoiner(WEB_UPLOAD_KEY).add(isdGstn).add(docType)
				.add(docNumber).add(fYear).toString();
	}
*/
}
