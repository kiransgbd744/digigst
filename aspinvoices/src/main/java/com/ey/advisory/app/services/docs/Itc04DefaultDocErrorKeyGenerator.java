package com.ey.advisory.app.services.docs;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderErrorsEntity;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * This class is responsible for creating a document key for an Outward Supply
 * Document. This is required, as the identifier that we use as docId is
 * internal to our system. This is valid within our system, but not outside our
 * system. So, when we send documents to GSTN, we lose this ID, as GSTN doesn't
 * store our internal id. (It would be nice if GSTN introduced this
 * functionality). So, when we fetch the documents back from GSTN, we need some
 * other mechanism to match it with the documents existing in our DB (as ID is
 * not in the data that we obtained from GSTN). So, we need to have another
 * natural key for each document to do the matching. Following is the
 * combination that we use:
 * 
 * Document Key = SGS
 * 
 * @author Mahesh.Golla
 *
 */
@Component("Itc04DefaultDocErrorKeyGenerator")
public class Itc04DefaultDocErrorKeyGenerator
		implements DocKeyGenerator<Itc04HeaderErrorsEntity, String> {

	/**
	 * sgstin, docType, docNo, docDate
	 */

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Itc04HeaderErrorsEntity doc) {

		StringJoiner genKey = new StringJoiner(DOC_KEY_JOINER);
		String tableNumber = doc.getTableNumber();
		String jwStateCode = doc.getJobWorkerStateCode();
		String jwStateGstin = doc.getJobWorkerGstin();
		String fy = doc.getFy() != null ? doc.getFy() : "";

		String gstinOrStateCode = GstinOrStateCode(jwStateCode, jwStateGstin);

		if (tableNumber != null
				&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(tableNumber)) {

			String sgstin = (doc.getSupplierGstin() != null)
					? doc.getSupplierGstin().trim() : "";
			String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
					? doc.getDeliveryChallanaNumber().trim() : "";
			Object deliveryChallanDate = (doc.getDeliveryChallanaDate() != null
					&& !doc.getDeliveryChallanaDate().toString().isEmpty())
							? doc.getDeliveryChallanaDate() : "";

			String delChFinYear = "";
			LocalDate localDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			delChFinYear = GenUtil.getFinYear(localDocDate);
			genKey.add(tableNumber).add(sgstin).add(delChallanNum)
					.add(delChFinYear);
			return genKey.toString();
		}
		if (tableNumber != null
				&& GSTConstants.TABLE_NUMBER_5C.equalsIgnoreCase(tableNumber)) {

			String sgstin = (doc.getSupplierGstin() != null)
					? doc.getSupplierGstin().trim() : "";
			String invNum = (doc.getInvNumber() != null)
					? doc.getInvNumber().trim() : "";
			Object invDate = (doc.getInvDate() != null
					&& !doc.getInvDate().toString().isEmpty())
							? doc.getInvDate() : "";
			String invFinYear = "";
			LocalDate localDocDate = DateUtil.parseObjToDate(invDate);

			invFinYear = GenUtil.getFinYear(localDocDate);
			String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
					? doc.getDeliveryChallanaNumber().trim() : "";

			Object deliveryChallanDate = (doc.getDeliveryChallanaDate() != null
					&& !doc.getDeliveryChallanaDate().toString().isEmpty())
							? doc.getDeliveryChallanaDate() : "";

			String delChFinYear = "";
			LocalDate localDeDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			delChFinYear = GenUtil.getFinYear(localDeDocDate);
			genKey.add(tableNumber).add(sgstin).add(invNum).add(invFinYear)
					.add(delChallanNum).add(delChFinYear).add(gstinOrStateCode);
			return genKey.toString();
		}
		if ((tableNumber != null
				&& GSTConstants.TABLE_NUMBER_5A.equalsIgnoreCase(tableNumber))
				|| (tableNumber != null && GSTConstants.TABLE_NUMBER_5B
						.equalsIgnoreCase(tableNumber))) {

			Object deliveryChallanDate = (doc.getDeliveryChallanaDate() != null
					&& !doc.getDeliveryChallanaDate().toString().isEmpty())
							? doc.getDeliveryChallanaDate() : null;

			Object jwDeliveryChallanDate = (doc
					.getJwDeliveryChallanaDate() != null
					&& !doc.getJwDeliveryChallanaDate().toString().isEmpty())
							? doc.getJwDeliveryChallanaDate() : null;

			if (deliveryChallanDate != null && jwDeliveryChallanDate != null) {
				String supplierGstin = (doc.getSupplierGstin() != null
						&& !doc.getSupplierGstin().toString().isEmpty())
								? String.valueOf((doc.getSupplierGstin()))
										.trim().toUpperCase()
								: "";
				String deliveryChallanNo = (doc
						.getDeliveryChallanaNumber() != null
						&& !doc.getDeliveryChallanaNumber().toString()
								.isEmpty())
										? String.valueOf(
												(doc.getDeliveryChallanaNumber()))
												.trim()
										: "";
				String retPeriod = (doc.getRetPeriod() != null
						&& !doc.getRetPeriod().toString().isEmpty())
								? String.valueOf((doc.getRetPeriod())).trim()
								: "";
				String jwDeliveryChallanNo = (doc
						.getJwDeliveryChallanaNumber() != null
						&& !doc.getJwDeliveryChallanaNumber().toString()
								.isEmpty())
										? String.valueOf(
												(doc.getJwDeliveryChallanaNumber()))
												.trim()
										: "";

				String dFinYear = "";
				LocalDate localDocDate = DateUtil
						.parseObjToDate(deliveryChallanDate);
				dFinYear = GenUtil.getFinYear(localDocDate);

				String jwDFinYear = "";
				LocalDate jwDlocalDocDate = DateUtil
						.parseObjToDate(jwDeliveryChallanDate);
				jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);

				return genKey.add(tableNumber).add(retPeriod.concat(fy))
						.add(supplierGstin).add(deliveryChallanNo).add(dFinYear)
						.add(jwDeliveryChallanNo).add(jwDFinYear)
						.add(gstinOrStateCode).toString();

			}
			if (jwDeliveryChallanDate != null) {
				String supplierGstin = (doc.getSupplierGstin() != null
						&& !doc.getSupplierGstin().toString().isEmpty())
								? String.valueOf((doc.getSupplierGstin()))
										.trim().toUpperCase()
								: "";
				String retPeriod = (doc.getRetPeriod() != null
						&& !doc.getRetPeriod().toString().isEmpty())
								? String.valueOf((doc.getRetPeriod())).trim()
								: "";
				String jwDeliveryChallanNo = (doc
						.getJwDeliveryChallanaNumber() != null
						&& !doc.getJwDeliveryChallanaNumber().toString()
								.isEmpty())
										? String.valueOf(
												(doc.getJwDeliveryChallanaNumber()))
												.trim()
										: "";

				String jwDFinYear = "";
				LocalDate jwDlocalDocDate = DateUtil
						.parseObjToDate(jwDeliveryChallanDate);
				jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);

				return genKey.add(tableNumber).add(retPeriod.concat(fy))
						.add(supplierGstin).add(jwDeliveryChallanNo)
						.add(jwDFinYear).add(gstinOrStateCode).toString();

			}
			if (deliveryChallanDate != null) {
				String supplierGstin = (doc.getSupplierGstin() != null
						&& !doc.getSupplierGstin().toString().isEmpty())
								? String.valueOf((doc.getSupplierGstin()))
										.trim().toUpperCase()
								: "";
				String retPeriod = (doc.getRetPeriod() != null
						&& !doc.getRetPeriod().toString().isEmpty())
								? String.valueOf((doc.getRetPeriod())).trim()
								: "";

				String deliveryChallanNo = (doc
						.getDeliveryChallanaNumber() != null
						&& !doc.getDeliveryChallanaNumber().toString()
								.isEmpty())
										? String.valueOf(
												(doc.getDeliveryChallanaNumber()))
												.trim()
										: "";

				String dFinYear = "";
				LocalDate localDocDate = DateUtil
						.parseObjToDate(deliveryChallanDate);
				dFinYear = GenUtil.getFinYear(localDocDate);

				return genKey.add(tableNumber).add(retPeriod.concat(fy))
						.add(supplierGstin).add(deliveryChallanNo).add(dFinYear)
						.add(gstinOrStateCode).toString();

			}

		} else {

			String supplierGstin = (doc.getSupplierGstin() != null
					&& !doc.getSupplierGstin().isEmpty())
							? String.valueOf(doc.getSupplierGstin()).trim()
							: "";

			String deliveryChallanNo = (doc.getDeliveryChallanaNumber() != null
					&& !doc.getDeliveryChallanaNumber().toString().isEmpty())
							? String.valueOf((doc.getDeliveryChallanaNumber()))
									.trim()
							: "";
			Object deliveryChallanDate = (doc.getDeliveryChallanaDate() != null
					&& !doc.getDeliveryChallanaDate().toString().isEmpty())
							? doc.getDeliveryChallanaDate() : "";

			String finYear = "";
			LocalDate localDocDate = DateUtil
					.parseObjToDate(deliveryChallanDate);

			finYear = GenUtil.getFinYear(localDocDate);

			String invNumber = (doc.getInvNumber() != null
					&& !doc.getInvNumber().toString().isEmpty())
							? String.valueOf((doc.getInvNumber())).trim() : "";
			Object invDate = (doc.getInvDate() != null
					&& !doc.getInvDate().toString().isEmpty())
							? doc.getInvDate() : "";

			String invFinYear = "";
			LocalDate localInvDate = DateUtil.parseObjToDate(invDate);
			invFinYear = GenUtil.getFinYear(localInvDate);

			String retPeriod = (doc.getRetPeriod() != null
					&& !doc.getRetPeriod().toString().isEmpty())
							? String.valueOf((doc.getRetPeriod())).trim() : "";
			String jwDeliveryChallanNo = (doc
					.getJwDeliveryChallanaNumber() != null
					&& !doc.getJwDeliveryChallanaNumber().toString().isEmpty())
							? String.valueOf(
									(doc.getJwDeliveryChallanaNumber())).trim()
							: "";

			Object jwDeliveryChallanDate = (doc
					.getJwDeliveryChallanaDate() != null
					&& !doc.getJwDeliveryChallanaDate().toString().isEmpty())
							? doc.getJwDeliveryChallanaDate() : null;

			String jwDFinYear = "";
			LocalDate jwDlocalDocDate = DateUtil
					.parseObjToDate(jwDeliveryChallanDate);
			jwDFinYear = GenUtil.getFinYear(jwDlocalDocDate);

			return genKey.add(tableNumber).add(retPeriod.concat(fy))
					.add(supplierGstin).add(deliveryChallanNo).add(finYear)
					.add(jwDeliveryChallanNo).add(jwDFinYear).add(invNumber)
					.add(invFinYear).add(gstinOrStateCode).toString();

		}

		return tableNumber;
	}

	private String GstinOrStateCode(String jwStateCode, String jwStateGstin) {
		if ((jwStateCode != null && !jwStateCode.isEmpty())
				&& (jwStateGstin != null && !jwStateGstin.isEmpty()))
			return jwStateGstin;
		if (jwStateGstin != null && !jwStateGstin.isEmpty())
			return jwStateGstin;
		if (jwStateCode != null && !jwStateCode.isEmpty())
			return jwStateCode;

		return null;

	}

	@Override
	public String generateOrgKey(Itc04HeaderErrorsEntity doc) {
		// TODO Auto-generated method stub
		return null;
	}

}
