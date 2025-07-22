package com.ey.advisory.app.gstr2b.summary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr2BDetailsServiceImpl")
public class Gstr2BDetailsServiceImpl implements Gstr2BDetailsService {

	@Autowired
	@Qualifier("Gstr2BDetailsDaoImpl")
	private Gstr2BDetailsDao dao;

	@Override
	public Gstr2BDetailsRespDto getDetailsList(String gstin, String toTaxPeriod,
			String fromTaxPeriod) {

		Gstr2BDetailsRespDto dto = new Gstr2BDetailsRespDto();

		try {

			List<Gstr2BDetailsDto> dbResp = dao.getDetailsResp(
					Arrays.asList(gstin), toTaxPeriod, fromTaxPeriod);

			List<Gstr2BDetailsDto> itcSuppliesFromRegPerson = new ArrayList<>();
			List<Gstr2BDetailsDto> inwardSuppliesFromIsd = new ArrayList<>();
			List<Gstr2BDetailsDto> inwardSuppliesRevChrg = new ArrayList<>();
			List<Gstr2BDetailsDto> importOfGoods = new ArrayList<>();
			List<Gstr2BDetailsDto> others = new ArrayList<>();

			for (Gstr2BDetailsDto obj : dbResp) {

				if ((obj.getTableName().equalsIgnoreCase("B2B - Invoices"))
						|| (obj.getTableName()
								.equalsIgnoreCase("B2B - Debit notes"))
						|| (obj.getTableName()
								.equalsIgnoreCase("B2B - Invoices (Amendment)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Debit notes (Amendment)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"ECOM"))
						|| (obj.getTableName().equalsIgnoreCase(
								"ECOM (Amendment)"))) {

					itcSuppliesFromRegPerson.add(setValues(obj));
					dto.setItcSuppliesFromRegPerson(itcSuppliesFromRegPerson);

				} else if ((obj.getTableName()
						.equalsIgnoreCase("ISD - Invoices"))
						|| (obj.getTableName().equalsIgnoreCase(
								"ISD - Invoices (Amendment)"))) {

					inwardSuppliesFromIsd.add(setValues(obj));
					dto.setInwardSuppliesFromIsd(inwardSuppliesFromIsd);

				} else if ((obj.getTableName()
						.equalsIgnoreCase("B2B - Invoices (RCM)"))
						|| (obj.getTableName()
								.equalsIgnoreCase("B2B - Debit notes (RCM)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Invoices (Amendment)(RCM)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Debit notes (Amendment) (RCM)"))) {

					inwardSuppliesRevChrg.add(setValues(obj));
					dto.setInwardSuppliesRevChrg(inwardSuppliesRevChrg);

				} else if ((obj.getTableName().equalsIgnoreCase(
						"IMPG - Import of goods from overseas"))
						|| (obj.getTableName()
								.equalsIgnoreCase("IMPG (Amendment)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"IMPGSEZ - Import of goods from SEZ"))
						|| (obj.getTableName()
								.equalsIgnoreCase("IMPGSEZ (Amendment)"))) {

					importOfGoods.add(setValues(obj));
					dto.setImportOfGoods(importOfGoods);

				} else if ((obj.getTableName()
						.equalsIgnoreCase("B2B - Credit notes"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Credit notes (Amendment)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Credit notes (Reverse charge)"))
						|| (obj.getTableName().equalsIgnoreCase(
								"B2B - Credit notes (Reverse charge)(Amendment)"))
						|| (obj.getTableName()
								.equalsIgnoreCase("ISD - Credit notes"))
						|| (obj.getTableName().equalsIgnoreCase(
								"ISD - Credit notes (Amendment)"))) {

					others.add(setCRValues(obj));
					dto.setOthers(others);

				} else if (obj.getTableName()
						.equalsIgnoreCase("~~~~TOTAL~~~~")) {

					Gstr2BDetailsDto totalValues = setValues(obj);
					totalValues.setTableName("Total");

					dto.setTotal(totalValues);
				}
			}

		} catch (Exception ex) {

			ex.printStackTrace();
			String msg = String.format(
					"Error occued while converting to "
							+ "Gstr2BDetailsRespDto :: gstin %s, toTaxPeriod %s, "
							+ "fromTaxPeriod %s ",
					gstin, toTaxPeriod, fromTaxPeriod);
			LOGGER.error(msg, ex);

		}

		return dto;
	}

	private Gstr2BDetailsDto setValues(Gstr2BDetailsDto obj) {
		Gstr2BDetailsDto dbObj = new Gstr2BDetailsDto();

		dbObj.setAvailItcCess(obj.getAvailItcCess());
		dbObj.setAvailItcCgst(obj.getAvailItcCgst());
		dbObj.setAvailItcIgst(obj.getAvailItcIgst());
		dbObj.setAvailItcSgst(obj.getAvailItcSgst());

		dbObj.setNonAvailItcCess(obj.getNonAvailItcCess());
		dbObj.setNonAvailItcCgst(obj.getNonAvailItcCgst());
		dbObj.setNonAvailItcIgst(obj.getNonAvailItcIgst());
		dbObj.setNonAvailItcSgst(obj.getNonAvailItcSgst());
		
		dbObj.setRejectedItcCess(obj.getRejectedItcCess());
		dbObj.setRejectedItcCgst(obj.getRejectedItcCgst());
		dbObj.setRejectedItcIgst(obj.getRejectedItcIgst());
		dbObj.setRejectedItcSgst(obj.getRejectedItcSgst());

		dbObj.setTableName(obj.getTableName());

		dbObj.setTotalTaxCess(obj.getTotalTaxCess());
		dbObj.setTotalTaxCgst(obj.getTotalTaxCgst());
		dbObj.setTotalTaxIgst(obj.getTotalTaxIgst());
		dbObj.setTotalTaxSgst(obj.getTotalTaxSgst());

		return dbObj;
	}

	private Gstr2BDetailsDto setCRValues(Gstr2BDetailsDto obj) {
		Gstr2BDetailsDto dbObj = new Gstr2BDetailsDto();

		dbObj.setAvailItcCess(obj.getAvailItcCess());
		dbObj.setAvailItcCgst(obj.getAvailItcCgst());
		dbObj.setAvailItcIgst(obj.getAvailItcIgst());
		dbObj.setAvailItcSgst(obj.getAvailItcSgst());

		dbObj.setNonAvailItcCess(obj.getNonAvailItcCess());
		dbObj.setNonAvailItcCgst(obj.getNonAvailItcCgst());
		dbObj.setNonAvailItcIgst(obj.getNonAvailItcIgst());
		dbObj.setNonAvailItcSgst(obj.getNonAvailItcSgst());
		
		dbObj.setRejectedItcCess(obj.getRejectedItcCess());
		dbObj.setRejectedItcCgst(obj.getRejectedItcCgst());
		dbObj.setRejectedItcIgst(obj.getRejectedItcIgst());
		dbObj.setRejectedItcSgst(obj.getRejectedItcSgst());

		dbObj.setTableName(obj.getTableName());

		dbObj.setTotalTaxCess(obj.getTotalTaxCess());
		dbObj.setTotalTaxCgst(obj.getTotalTaxCgst());
		dbObj.setTotalTaxIgst(obj.getTotalTaxIgst());
		dbObj.setTotalTaxSgst(obj.getTotalTaxSgst());

		return dbObj;
	}

/*	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}*/
}
