package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;
import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.core.async.JobStatusConstants;

/**
 * 
 * @author Balakrishna.S
 *
 */

@Service("Gstr6DistrbtnDataConverter")
public class Gstr6DistrbtnDataConverter {
	private final static String WEB_UPLOAD_KEY = "|";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr6DistrbtnDataConverter.class);

	public List<Gstr6DistributionExcelEntity> convertProduct(
			List<Gstr6DistributionExcelEntity> productList,
			Gstr1FileStatusEntity updateFileStatus) {
		List<Gstr6DistributionExcelEntity> product = new ArrayList<>();

		for (Gstr6DistributionExcelEntity obj : productList) {
			Gstr6DistributionExcelEntity prod = new Gstr6DistributionExcelEntity();

			String returnPeriod = (obj.getRetPeriod() != null
					&& !obj.getRetPeriod().toString().trim().isEmpty())
							? String.valueOf(obj.getRetPeriod()) : null;

		//	int derivedRetPeriod = GenUtil.convertTaxPeriodToInt(returnPeriod);
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

		/*	String docDate = (obj.getDocDate() != null
					&& !obj.getDocDate().toString().trim().isEmpty())
							? String.valueOf(obj.getDocDate()) : null;*/

							LocalDate localDocDate = obj.getDocDate();
		//	LocalDate localDocDate = DateUtil.parseObjToDate(docDate);
			//	LocalDate localDocDate = null;
		/*	if(docDate != null){
				 localDocDate = EYDateUtil.
						.toUTCDateTimeFromLocal(docDate);
								}*/

			String OrigDocNumber = (obj.getOrgDocNum() != null
					&& !obj.getOrgDocNum().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgDocNum()) : null;

		/*	String OrigDocDate = (obj.getOrgDocDate() != null
					&& !obj.getOrgDocDate().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgDocDate()) : null;*/

							LocalDate localOrigDocDate = obj.getOrgDocDate();
		//	LocalDate localOrigDocDate = DateUtil.parseObjToDate(OrigDocDate);
			
			
		/*	LocalDate localOrigDocDate = null;
			if(OrigDocDate != null){
				localOrigDocDate = EYDateUtil
						.toUTCDateTimeFromLocal(OrigDocDate);
								}
			*/

			String OrigCrNoteNumber = (obj.getOrgCrNum() != null
					&& !obj.getOrgCrNum().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgCrNum()) : null;

		/*	String OrigCrNoteDate = (obj.getOrgCrDate() != null
					&& !obj.getOrgCrDate().toString().trim().isEmpty())
							? String.valueOf(obj.getOrgCrDate()) : null;*/

							LocalDate localOrigCrNoteDate = obj.getOrgCrDate();
		/*	LocalDate localOrigCrNoteDate = DateUtil
					.parseObjToDate(OrigCrNoteDate);*/
			
			/*LocalDate localOrigCrNoteDate = null;
			if(OrigCrNoteDate != null){
				localOrigDocDate = EYDateUtil
						.toUTCDateTimeFromLocal(OrigCrNoteDate);
								}*/
			
			
			
			String eligibleIndicator = (obj.getEligibleIndicator() != null
					&& !obj.getEligibleIndicator().toString().trim().isEmpty())
							? String.valueOf(obj.getEligibleIndicator()) : null;

		//	BigDecimal igstAsIgst = BigDecimal.ZERO;
							String igstasIgst = null;
			if (obj.getIgstAsIgst() != null
					&& !obj.getIgstAsIgst().toString().trim().isEmpty()) {
				 igstasIgst = (String.valueOf(obj.getIgstAsIgst()));
			//	igstAsIgst = new BigDecimal(rateStr);
			}

		//	BigDecimal igstAsSgst = BigDecimal.ZERO;
			String igstAsSgst = null ;
			if (obj.getIgstAsSgst() != null
					&& !obj.getIgstAsSgst().toString().trim().isEmpty()) {
				 igstAsSgst = (String.valueOf(obj.getIgstAsSgst()));
			//	igstAsSgst = new BigDecimal(rateStr);
			}

		//	BigDecimal igstAsCgst = BigDecimal.ZERO;
			String igstAsCgst = null ;
			if (obj.getIgstAsCgst() != null
					&& !obj.getIgstAsCgst().toString().trim().isEmpty()) {
				 igstAsCgst = (String.valueOf(obj.getIgstAsCgst()));
			//	igstAsCgst = new BigDecimal(rateStr);
			}

		//	BigDecimal sgstAsSgst = BigDecimal.ZERO;
			String sgstAsSgst = null ;
			if (obj.getSgstAsSgst() != null
					&& !obj.getSgstAsSgst().toString().trim().isEmpty()) {
				 sgstAsSgst = (String.valueOf(obj.getSgstAsSgst()));
			//	sgstAsSgst = new BigDecimal(rateStr);
			}

		//	BigDecimal sgstAsIgst = BigDecimal.ZERO;
			String sgstAsIgst = null;
			if (obj.getSgstAsIgst() != null
					&& !obj.getSgstAsIgst().toString().trim().isEmpty()) {
				 sgstAsIgst = (String.valueOf(obj.getSgstAsIgst()));
			//	sgstAsIgst = new BigDecimal(rateStr);
			}

		//	BigDecimal cgstAsCgst = BigDecimal.ZERO;
			String cgstAsCgst = null ;
			if (obj.getCgstAsCgst() != null
					&& !obj.getCgstAsCgst().toString().trim().isEmpty()) {
				 cgstAsCgst = (String.valueOf(obj.getCgstAsCgst()));
			//	cgstAsCgst = new BigDecimal(rateStr);
			}

		//	BigDecimal cgstAsIgst = BigDecimal.ZERO;
			String cgstAsIgst = null;
			if (obj.getCgstAsIgst() != null
					&& !obj.getCgstAsIgst().toString().trim().isEmpty()) {
				 cgstAsIgst = (String.valueOf(obj.getCgstAsIgst()));
			//	cgstAsIgst = new BigDecimal(rateStr);
			}

		//	BigDecimal cessAmount = BigDecimal.ZERO;
			String cessAmount = null;
			if (obj.getCessAmount() != null
					&& !obj.getCessAmount().toString().trim().isEmpty()) {
				 cessAmount = (String.valueOf(obj.getCessAmount()));
			//	cessAmount = new BigDecimal(rateStr);
			}

	//		String processKey = getProductValues(obj);
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

				
			//	prod.setFileName(updateFileStatus.getFileName());
			}

			prod.setId(obj.getId());
			prod.setRetPeriod(returnPeriod);
			prod.setIsdGstin(isdGstin);
			prod.setCustGstin(recepGstin);
			prod.setStateCode(stateCode);
			prod.setOrgCustGstin(origRecpGstin);
			prod.setOrgStateCode(OrigsateCode);

			prod.setDocType(docType);
			prod.setSupplyType(supplyType);
			prod.setDocNum(docNumber);

			prod.setDocDate(localDocDate);

			prod.setOrgDocNum(OrigDocNumber);

			prod.setOrgDocDate(localOrigDocDate);
			prod.setOrgCrDate(localOrigCrNoteDate);

			prod.setOrgCrNum(OrigCrNoteNumber);
			prod.setEligibleIndicator(eligibleIndicator);
			prod.setIgstAsIgst(igstasIgst);
			prod.setIgstAsSgst(igstAsSgst);
			prod.setIgstAsCgst(igstAsCgst);
			prod.setSgstAsIgst(sgstAsIgst);
			prod.setSgstAsSgst(sgstAsSgst);
			prod.setCgstAsIgst(cgstAsIgst);
			prod.setCgstAsCgst(cgstAsCgst);
			prod.setCessAmount(cessAmount);
			
		//	String derivedRePeroid = null;
		/*	if (returnPeriod != null && returnPeriod.matches("[0-9]+")) {
				if (returnPeriod.length() == 6) {
					int month = Integer.valueOf(returnPeriod.substring(0, 2));
					int year = Integer.valueOf(returnPeriod.substring(2));
					if ((month < 12 && month > 01)
							&& (year < 9999 && year > 0000)) {
					Integer derivedReturnPeriod = GenUtil
							.convertTaxPeriodToInt(returnPeriod);
					derivedRePeroid = String.valueOf(derivedRetPeriod);
				}
				}
			}*/
			
			prod.setDerived_Ret_period(obj.getDerived_Ret_period());
			prod.setProcessKey(obj.getProcessKey());
			prod.setCreatedBy("System");
			prod.setCreatedOn(obj.getCreatedOn());

			product.add(prod);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("ProductFileCoversion List  -> ", prod, product);
			}
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

	public String getProductValues(Gstr6DistributionExcelEntity obj) {
		String isdGstn = (obj.getIsdGstin() != null
				&& !obj.getIsdGstin().toString().trim().isEmpty())
						? String.valueOf(obj.getIsdGstin()) : null;

		String docType = (obj.getDocType() != null
				&& !obj.getDocType().toString().trim().isEmpty())
						? String.valueOf(obj.getDocType()) : null;
		String docNumber = (obj.getDocNum() != null)
				? String.valueOf(obj.getDocNum()) : null;

	/*	String docYear = (obj.getDocDate() != null
				&& !obj.getDocDate().toString().trim().isEmpty())
						? String.valueOf(obj.getDocDate()) : null;*/
						LocalDate localOrigDocDate = obj.getDocDate();
					//	LocalDate parseObjToDate = DateUtil.parseObjToDate(docYear);
					//	LocalDate localOrigCrNoteDate = null;
					/*	LocalDate localOrigDocDate = EYDateUtil
									.toUTCDateTimeFromLocal(docYear);*/
					
						String fYear = localOrigDocDate.toString();		
		
		return new StringJoiner(WEB_UPLOAD_KEY).add(isdGstn).add(docType)
				.add(docNumber).add(fYear).toString();
	}

}
