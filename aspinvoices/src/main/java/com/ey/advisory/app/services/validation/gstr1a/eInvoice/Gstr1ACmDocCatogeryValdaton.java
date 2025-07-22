package com.ey.advisory.app.services.validation.gstr1a.eInvoice;

import static com.ey.advisory.common.GSTConstants.APP_VALIDATION;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.app.services.validation.DocRulesValidator;
import com.ey.advisory.app.services.validation.sales.BigDecimalNagativeValueUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.TransDocProcessingResultLoc;

/**
 * @author Shashikant.Shukla
 *
 */
@Component("Gstr1ACmDocCatogeryValdaton")
public class Gstr1ACmDocCatogeryValdaton
		implements DocRulesValidator<Gstr1AOutwardTransDocument> {

	private static final String D = "D";
	private static final String S = "S";
	private static final String R = "R";
	private static final String C = "C";

	@Override
	public List<ProcessingResult> validate(Gstr1AOutwardTransDocument document,
			ProcessingContext context) {
		List<ProcessingResult> errors = new ArrayList<>();
		String docCategory = document.getDocCategory();
		String dispatcherAddr1 = document.getDispatcherBuildingNumber();
		String dispatcherAddr2 = document.getDispatcherBuildingName();
		String dispatcherLocation = document.getDispatcherLocation();
		Integer dispatcherPincode = document.getDispatcherPincode();
		String dispatcherStateCode = document.getDispatcherStateCode();
		String shipToAddr1 = document.getShipToBuildingNumber();
		String shipToAddr2 = document.getShipToBuildingName();
		String shipToLocation = document.getShipToLocation();
		Integer shipToPincode = document.getShipToPincode();
		String shipToState = document.getShipToState();
		String SuppAddress1 = document.getSupplierBuildingNumber();
		String SuppAddress2 = document.getSupplierBuildingName();
		String supplierLocation = document.getSupplierLocation();
		Integer supplierPincode = document.getSupplierPincode();
		String CustAddress1 = document.getCustOrSuppAddress1();
		String CustAddress2 = document.getCustOrSuppAddress2();
		String custLocation = document.getCustOrSuppAddress4();
		Integer customerPincode = document.getCustomerPincode();

		/*
		 * if (!BigDecimalNagativeValueUtil.valid(docCategory, CustAddress1, C))
		 * { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.CUST_ADDER1);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15113",
		 * "CustomerAddress1 cannot be left blank", location));
		 * 
		 * 
		 * } if (!BigDecimalNagativeValueUtil.valid(docCategory, CustAddress2,
		 * C)) { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.CUST_ADDER2);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15114",
		 * "CustomerAddress2 cannot be left blank", location));
		 * 
		 * 
		 * }
		 */

		if (!BigDecimalNagativeValueUtil.valid(docCategory, custLocation, C)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.CUSTOMER_LOCATION);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10024",
					"customerLocation cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, customerPincode,
				C)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.CUSTOMER_PINCODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10025",
					"customerPincode cannot be left blank", location));

		}

		/*
		 * if (!BigDecimalNagativeValueUtil.valid(docCategory, SuppAddress1, R))
		 * { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.SupplierAddress1);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15111",
		 * "supplierAddress1 cannot be left blank", location));
		 * 
		 * 
		 * } if (!BigDecimalNagativeValueUtil.valid(docCategory, SuppAddress2,
		 * R)) { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.SupplierAddress2);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15137",
		 * "supplierAddress2 cannot be left blank", location));
		 * 
		 * 
		 * }
		 */
		if (!BigDecimalNagativeValueUtil.valid(docCategory, supplierLocation,
				R)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SUPPLIER_LOCATION);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10012",
					"supplier Location cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, supplierPincode,
				R)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SUPPLIER_PINCODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10014",
					"supplier Pincode cannot be left blank", location));

		}

		/*
		 * if (!BigDecimalNagativeValueUtil.valid(docCategory, dispatcherAddr1,
		 * D)) { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.DIS_PATCHER_ADDR1);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15129",
		 * "DispatcherAddress1 cannot be left blank", location));
		 * 
		 * 
		 * } if (!BigDecimalNagativeValueUtil.valid(docCategory,
		 * dispatcherAddr2, D)) { List<String> errorLocations = new
		 * ArrayList<>(); errorLocations.add(GSTConstants.DIS_PATCHER_ADDR2);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15130",
		 * "DispatcherAddress2 cannot be left blank", location));
		 * 
		 * 
		 * }
		 */
		if (!BigDecimalNagativeValueUtil.valid(docCategory, dispatcherLocation,
				D)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.DISPATCHER_LOCATION);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10031",
					"DispatcherLocation cannot be left blank", location));

		}

		if (!BigDecimalNagativeValueUtil.valid(docCategory, dispatcherPincode,
				D)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.DPINCODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10033",
					"DispatcherPincode cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, dispatcherStateCode,
				D)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.DPSTATECODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER15147",
					"DispatcherStateCode cannot be left blank", location));

		}

		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToPincode, S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHIP_TO_PIN);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, " ER10041",
					"ShiptoPincode cannot be left blank", location));

		}
		/*
		 * if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToAddr1, S))
		 * { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.SHIP_TO_ADDRESS1);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15131",
		 * "shipToAddrss1 cannot be left blank", location));
		 * 
		 * } if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToAddr2,
		 * S)) { List<String> errorLocations = new ArrayList<>();
		 * errorLocations.add(GSTConstants.SHIP_TO_ADDRESS2);
		 * TransDocProcessingResultLoc location = new
		 * TransDocProcessingResultLoc( null, errorLocations.toArray());
		 * errors.add(new ProcessingResult(APP_VALIDATION, "ER15132",
		 * "shipToAddrss2  cannot be left blank", location));
		 * 
		 * }
		 */

		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToLocation,
				S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHIP_TO_LOCATION);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10039",
					"shipToLocation Pincode cannot be left blank", location));

		}
		if (!BigDecimalNagativeValueUtil.valid(docCategory, shipToState, S)) {
			List<String> errorLocations = new ArrayList<>();
			errorLocations.add(GSTConstants.SHIP_TO_STATE_CODE);
			TransDocProcessingResultLoc location = new TransDocProcessingResultLoc(
					null, errorLocations.toArray());
			errors.add(new ProcessingResult(APP_VALIDATION, "ER10042",
					"Ship to State cannot be left blank", location));

		}
		return errors;
	}

}
