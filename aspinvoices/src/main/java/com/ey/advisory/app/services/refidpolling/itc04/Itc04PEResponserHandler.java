package com.ey.advisory.app.services.refidpolling.itc04;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.docs.dto.itc04.Itc04Dto;
import com.ey.advisory.app.docs.dto.itc04.Itc04ErrorReport;
import com.ey.advisory.app.docs.dto.itc04.Itc04ErrorReportData;
import com.ey.advisory.app.docs.dto.itc04.Itc04ItemsDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04M2jwDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5ADto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5BDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Table5CDto;
import com.ey.advisory.app.services.itc04.Itc04DocKeyGenerator;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonLocalDateConverter;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.domain.client.Gstr1SaveBatchEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */
@Slf4j
@Component("Itc04PEResponserHandler")
public class Itc04PEResponserHandler {

	@Autowired
	private Itc04DocKeyGenerator itc04DocKeyGenerator;

	public Triplet<List<String>, Map<String, Pair<String, String>>, Integer> response(
			JsonElement root, Gstr1SaveBatchEntity batch, Integer n) {
		Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new GsonLocalDateConverter()).create();
		List<String> invKeyList = new ArrayList<String>();
		Itc04Dto itc04 = gson.fromJson(root, Itc04Dto.class);
		Map<String, Pair<String, String>> docErrorMap = new HashMap<>();
		Itc04ErrorReport errorReport = itc04.getItc04ErrorReport();
		Itc04ErrorReportData gsterrors = errorReport.getITC04();
		if (APIConstants.M2JW.equalsIgnoreCase(batch.getSection())) {
			for (Itc04M2jwDto invoice : gsterrors.getM2jw()) {
				Itc04HeaderEntity data = new Itc04HeaderEntity();
				data.setTableNumber(GSTConstants.TABLE_NUMBER_4);
				data.setSupplierGstin(batch.getSgstin());
				data.setDeliveryChallanaNumber(invoice.getChNum());
				if (invoice.getChDate() != null) {
					DateTimeFormatter formatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");
					LocalDate deliveryChallanDate = LocalDate
							.parse(invoice.getChDate(), formatter);
					String dcfinYear = GenUtil.getFinYear(deliveryChallanDate);
					data.setFyDcDate(dcfinYear);
				}
				String invKey = itc04DocKeyGenerator.generateKey(data);
				invKeyList.add(invKey);
				docErrorMap.put(invKey, new Pair(invoice.getError_cd(),
						invoice.getError_msg()));

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("invKeyList ITC04 of M2JW {}", invKeyList);
				}
			}
			n = invKeyList.size();
		} else if (APIConstants.TABLE5A.equalsIgnoreCase(batch.getSection())) {
			for (Itc04Table5ADto invoiceData : gsterrors.getTable5a()) {
				for (Itc04ItemsDto invoice : invoiceData.getItc04ItemsDto()) {
					Itc04HeaderEntity data = new Itc04HeaderEntity();
					data.setTableNumber(GSTConstants.TABLE_NUMBER_5A);
					String retPeroid = getReturnPeroidDataInQuaterlyFormat(
							batch.getReturnPeriod());
					data.setRetPeriodDocKey(retPeroid);
					data.setSupplierGstin(batch.getSgstin() != null
							? batch.getSgstin() : null);
					data.setDeliveryChallanaNumber(invoice.getOchnum() != null
							? invoice.getOchnum() : null);
					if (invoice.getOchdt() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate deliveryChallanDate = LocalDate
								.parse(invoice.getOchdt(), formatter);
						data.setDeliveryChallanaDate(deliveryChallanDate);
						String dcfinYear = GenUtil
								.getFinYear(deliveryChallanDate);
						data.setFyDcDate(dcfinYear);
					}
					data.setJwDeliveryChallanaNumber(
							invoice.getJw2Chnum() != null
									? invoice.getJw2Chnum() : null);
					if (invoice.getJw2Chdate() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate jwDeliveryChallanDate = LocalDate
								.parse(invoice.getJw2Chdate(), formatter);
						data.setJwDeliveryChallanaDate(jwDeliveryChallanDate);
						String dcfinYear = GenUtil
								.getFinYear(jwDeliveryChallanDate);
						data.setFyjwDcDate(dcfinYear);
					}
					data.setJobWorkerGstin(invoiceData.getCtin() != null
							? invoiceData.getCtin() : null);
					data.setJobWorkerStateCode(invoiceData.getJwStCd() != null
							? invoiceData.getJwStCd() : null);
					String invKey = itc04DocKeyGenerator.generateKey(data);
					invKeyList.add(invKey);
					docErrorMap.put(invKey, new Pair(invoiceData.getError_cd(),
							invoiceData.getError_msg()));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("invKeyList ITC04 TABLE5A{}", invKeyList);
					}
				}

			}
			n = invKeyList.size();
		} else if (APIConstants.TABLE5B.equalsIgnoreCase(batch.getSection())) {
			for (Itc04Table5BDto invoiceData : gsterrors.getTable5b()) {
				for (Itc04ItemsDto invoice : invoiceData.getItc04ItemsDto()) {
					Itc04HeaderEntity data = new Itc04HeaderEntity();
					data.setTableNumber(GSTConstants.TABLE_NUMBER_5B);
					String retPeroid = getReturnPeroidDataInQuaterlyFormat(
							batch.getReturnPeriod());
					data.setRetPeriodDocKey(retPeroid);
					data.setSupplierGstin(batch.getSgstin() != null
							? batch.getSgstin() : null);
					data.setDeliveryChallanaNumber(invoice.getOchnum() != null
							? invoice.getOchnum() : null);
					if (invoice.getOchdt() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate deliveryChallanDate = LocalDate
								.parse(invoice.getOchdt(), formatter);
						data.setDeliveryChallanaDate(deliveryChallanDate);
						String dcfinYear = GenUtil
								.getFinYear(deliveryChallanDate);
						data.setFyDcDate(dcfinYear);
					}
					data.setJwDeliveryChallanaNumber(
							invoice.getJw2Chnum() != null
									? invoice.getJw2Chnum() : null);
					if (invoice.getJw2Chdate() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate jwDeliveryChallanDate = LocalDate
								.parse(invoice.getJw2Chdate(), formatter);
						data.setJwDeliveryChallanaDate(jwDeliveryChallanDate);
						String dcfinYear = GenUtil
								.getFinYear(jwDeliveryChallanDate);
						data.setFyjwDcDate(dcfinYear);
					}
					data.setJobWorkerGstin(invoiceData.getCtin() != null
							? invoiceData.getCtin() : null);
					data.setJobWorkerStateCode(invoiceData.getJwStCd() != null
							? invoiceData.getJwStCd() : null);
					String invKey = itc04DocKeyGenerator.generateKey(data);

					invKeyList.add(invKey);
					docErrorMap.put(invKey, new Pair(invoiceData.getError_cd(),
							invoiceData.getError_msg()));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("invKeyList ITC04 TABLE5B{}", invKeyList);
					}

				}
			}
			n = invKeyList.size();
		} else if (APIConstants.TABLE5C.equalsIgnoreCase(batch.getSection())) {
			for (Itc04Table5CDto invoiceData : gsterrors.getTable5c()) {
				for (Itc04ItemsDto invoice : invoiceData.getItc04ItemsDto()) {
					Itc04HeaderEntity data = new Itc04HeaderEntity();
					data.setTableNumber(GSTConstants.TABLE_NUMBER_5C);
					data.setSupplierGstin(batch.getSgstin() != null
							? batch.getSgstin() : null);
					data.setDeliveryChallanaNumber(invoice.getOchnum() != null
							? invoice.getOchnum() : null);
					if (invoice.getOchdt() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate deliveryChallanDate = LocalDate
								.parse(invoice.getOchdt(), formatter);
						data.setDeliveryChallanaDate(deliveryChallanDate);
						String dcfinYear = GenUtil
								.getFinYear(deliveryChallanDate);
						data.setFyDcDate(dcfinYear);
					}
					data.setInvNumber(invoice.getInum() != null
							? invoice.getInum() : null);
					if (invoice.getIdt() != null) {
						DateTimeFormatter formatter = DateTimeFormatter
								.ofPattern("dd-MM-yyyy");
						LocalDate invDate = LocalDate.parse(invoice.getIdt(),
								formatter);
						data.setInvDate(invDate);
						String dcfinYear = GenUtil.getFinYear(invDate);
						data.setFyInvDate(dcfinYear);
					}
					data.setJobWorkerGstin(invoiceData.getCtin() != null
							? invoiceData.getCtin() : null);
					data.setJobWorkerStateCode(invoiceData.getJwStCd() != null
							? invoiceData.getJwStCd() : null);
					String invKey = itc04DocKeyGenerator.generateKey(data);
					invKeyList.add(invKey);
					docErrorMap.put(invKey, new Pair(invoiceData.getError_cd(),
							invoiceData.getError_msg()));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("invKeyList ITC04 TABLE5C{}", invKeyList);
					}

				}
			}
			n = invKeyList.size();
		}
		return new Triplet<List<String>, Map<String, Pair<String, String>>, Integer>(
				invKeyList, docErrorMap, n);
	}

	private String getReturnPeroidDataInQuaterlyFormat(String returnPeriod) {
		String retPeroid = null;
		if (returnPeriod != null && !returnPeriod.isEmpty()) {
			String quater = returnPeriod.substring(0, 2);
			String year = returnPeriod.substring(2, 6);
			String qMonths = null;
			if (quater.equalsIgnoreCase("13")) {
				qMonths = "Apr-Jun";
			} else if (quater.equalsIgnoreCase("14")) {
				qMonths = "Jul-Sep";
			} else if (quater.equalsIgnoreCase("15")) {
				qMonths = "Oct-Dec";
			} else if (quater.equalsIgnoreCase("16")) {
				qMonths = "Jan-Mar";
			} else if (quater.equalsIgnoreCase("17")) {
				qMonths = "Apr-Sep";
			} else if (quater.equalsIgnoreCase("18")) {
				qMonths = "Oct-Mar";
			}
			String yEnd = returnPeriod.substring(4, 6);
			int inum = Integer.parseInt(yEnd);
			int addyear = inum + 1;
			String yString = Integer.toString(addyear);
			String FinalPeroid = year.concat("-".concat(yString));
			if (qMonths != null && !qMonths.isEmpty()) {
				retPeroid = qMonths.concat(FinalPeroid);
			}
		}
		return retPeroid;
	}

}
