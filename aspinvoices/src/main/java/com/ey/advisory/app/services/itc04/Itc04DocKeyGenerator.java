/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import java.time.LocalDate;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04DocKeyGenerator")
public class Itc04DocKeyGenerator
		implements DocKeyGenerator<Itc04HeaderEntity, String> {

	private static final String DOC_KEY_JOINER = "|";

	@Override
	public String generateKey(Itc04HeaderEntity doc) {

		StringJoiner genKey = new StringJoiner(DOC_KEY_JOINER);
		String tableNumber = doc.getTableNumber();
		String jwGstin = null;

		if (tableNumber != null
				&& GSTConstants.TABLE_NUMBER_4.equalsIgnoreCase(tableNumber)) {

			String sgstin = (doc.getSupplierGstin() != null)
					? doc.getSupplierGstin().trim() : null;
			String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
					? doc.getDeliveryChallanaNumber().trim() : null;
			String delChFinYear = (doc.getFyDcDate() != null
					&& !doc.getFyDcDate().isEmpty()) ? doc.getFyDcDate().trim()
							: null;
			return genKey.add(tableNumber).add(sgstin).add(delChallanNum)
					.add(delChFinYear).toString();
		} else if (tableNumber != null
				&& GSTConstants.TABLE_NUMBER_5C.equalsIgnoreCase(tableNumber)) {

			String sgstin = (doc.getSupplierGstin() != null)
					? doc.getSupplierGstin().trim() : null;
			String invNum = (doc.getInvNumber() != null)
					? doc.getInvNumber().trim() : null;
			String invFinYear = (doc.getFyInvDate() != null
					&& !doc.getFyInvDate().isEmpty())
							? doc.getFyInvDate().trim() : null;
			String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
					? doc.getDeliveryChallanaNumber().trim() : null;
			String delChFinYear = (doc.getFyDcDate() != null
					&& !doc.getFyDcDate().isEmpty()) ? doc.getFyDcDate().trim()
							: null;
			if (doc.getJobWorkerGstin() != null
					&& !doc.getJobWorkerGstin().isEmpty()) {
				jwGstin = (doc.getJobWorkerGstin() != null)
						? doc.getJobWorkerGstin().trim() : null;
			} else {
				jwGstin = (doc.getJobWorkerStateCode() != null)
						? doc.getJobWorkerStateCode().trim() : null;
			}
			return genKey.add(tableNumber).add(sgstin).add(invNum)
					.add(invFinYear).add(delChallanNum).add(delChFinYear)
					.add(jwGstin).toString();
		} else if ((tableNumber != null
				&& GSTConstants.TABLE_NUMBER_5A.equalsIgnoreCase(tableNumber))
				|| (tableNumber != null && GSTConstants.TABLE_NUMBER_5B
						.equalsIgnoreCase(tableNumber))) {

			if ((doc.getJwDeliveryChallanaNumber() == null
					|| doc.getJwDeliveryChallanaNumber().isEmpty())
					&& (doc.getFyjwDcDate() == null
							|| doc.getFyjwDcDate().isEmpty())) {

				String retPeriod = (doc.getRetPeriodDocKey() != null)
						? doc.getRetPeriodDocKey().trim() : null;
				String sgstin = (doc.getSupplierGstin() != null)
						? doc.getSupplierGstin().trim() : null;
				String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
						? doc.getDeliveryChallanaNumber().trim() : null;
				String delChFinYear = (doc.getFyDcDate() != null
						&& !doc.getFyDcDate().isEmpty())
								? doc.getFyDcDate().trim() : null;
				return genKey.add(tableNumber).add(retPeriod).add(sgstin)
						.add(delChallanNum).add(delChFinYear).toString();

			} else if ((doc.getDeliveryChallanaNumber() == null
					|| doc.getDeliveryChallanaNumber().isEmpty())
					&& (doc.getFyDcDate() == null
							|| doc.getFyDcDate().isEmpty())) {
				String retPeriod = (doc.getRetPeriodDocKey() != null)
						? doc.getRetPeriodDocKey().trim() : null;
				String sgstin = (doc.getSupplierGstin() != null)
						? doc.getSupplierGstin().trim() : null;
				String jwChallanNum = (doc
						.getJwDeliveryChallanaNumber() != null)
								? doc.getJwDeliveryChallanaNumber().trim()
								: null;
				String jwFinYear = (doc.getFyjwDcDate() != null
						&& !doc.getFyjwDcDate().isEmpty())
								? doc.getFyjwDcDate().trim() : null;
				if (doc.getJobWorkerGstin() != null
						&& !doc.getJobWorkerGstin().isEmpty()) {
					jwGstin = (doc.getJobWorkerGstin() != null)
							? doc.getJobWorkerGstin().trim() : null;
				} else {
					jwGstin = (doc.getJobWorkerStateCode() != null)
							? doc.getJobWorkerStateCode().trim() : null;
				}
				return genKey.add(tableNumber).add(retPeriod).add(sgstin)
						.add(jwChallanNum).add(jwFinYear).add(jwGstin)
						.toString();

			} else {
				String retPeriod = (doc.getRetPeriodDocKey() != null)
						? doc.getRetPeriodDocKey().trim() : null;
				String sgstin = (doc.getSupplierGstin() != null)
						? doc.getSupplierGstin().trim() : null;
				String delChallanNum = (doc.getDeliveryChallanaNumber() != null)
						? doc.getDeliveryChallanaNumber().trim() : null;
				String delChFinYear = (doc.getFyDcDate() != null
						&& !doc.getFyDcDate().isEmpty())
								? doc.getFyDcDate().trim() : null;
				String jwChallanNum = (doc
						.getJwDeliveryChallanaNumber() != null)
								? doc.getJwDeliveryChallanaNumber().trim()
								: null;
				String jwFinYear = (doc.getFyjwDcDate() != null
						&& !doc.getFyjwDcDate().isEmpty())
								? doc.getFyjwDcDate().trim() : null;
				if (doc.getJobWorkerGstin() != null
						&& !doc.getJobWorkerGstin().isEmpty()) {
					jwGstin = (doc.getJobWorkerGstin() != null)
							? doc.getJobWorkerGstin().trim() : null;
				} else {
					jwGstin = (doc.getJobWorkerStateCode() != null)
							? doc.getJobWorkerStateCode().trim() : null;
				}
				return genKey.add(tableNumber).add(retPeriod).add(sgstin)
						.add(delChallanNum).add(delChFinYear).add(jwChallanNum)
						.add(jwFinYear).add(jwGstin).toString();
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

			if (doc.getJobWorkerGstin() != null
					&& !doc.getJobWorkerGstin().isEmpty()) {
				jwGstin = (doc.getJobWorkerGstin() != null)
						? doc.getJobWorkerGstin().trim() : null;
			} else {
				jwGstin = (doc.getJobWorkerStateCode() != null)
						? doc.getJobWorkerStateCode().trim() : null;
			}
			return genKey.add(tableNumber).add(retPeriod).add(supplierGstin)
					.add(deliveryChallanNo).add(finYear)
					.add(jwDeliveryChallanNo).add(jwDFinYear).add(invNumber)
					.add(invFinYear).add(jwGstin).toString();

		}
	}

	@Override
	public String generateOrgKey(Itc04HeaderEntity doc) {
		// TODO Auto-generated method stub
		return null;
	}
}
