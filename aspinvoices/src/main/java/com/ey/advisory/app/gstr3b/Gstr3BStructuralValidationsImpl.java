package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.common.NumberFomatUtil;
import com.ey.advisory.common.ProcessingResult;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BStructuralValidationsImpl")
public class Gstr3BStructuralValidationsImpl
		implements Gstr3BStructuralValidations {

	String errMsg = null;

	List<String> errors = new ArrayList<>();

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstnRepo;

	@Autowired
	@Qualifier("Gstr3bBlockKeyBuilder")
	private Gstr3bBlockKeyBuilder keyBulder;
	
	@Override
	public Map<String, List<ProcessingResult>> Validation(
			List<Object[]> rowsData, TabularDataLayout layout) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = " Inside Gstr3BStructuralValidationsImpl.Validation()"
					+ "method ";
			LOGGER.debug(msg);
		}

		List<ProcessingResult> validationResult = null;
		Map<String, List<ProcessingResult>> errMap = new HashMap<>();

		for (Object[] rowData : rowsData) {
			 validationResult = new ArrayList<>();
			isValidGstn(rowData, validationResult);
			isValidTaxPeriod(rowData, validationResult);
			isValidTable(rowData, validationResult);
			isValidTaxableval(rowData, validationResult);
			isValidIgst(rowData, validationResult);
			isValidSgst(rowData, validationResult);
			isValidCgst(rowData, validationResult);
			isValidCess(rowData, validationResult);
			String key = keyBulder.buildDataBlockKey(rowData, layout);
			if(!validationResult.isEmpty())
			errMap.put(key, validationResult);
		}
		
		if (LOGGER.isDebugEnabled()) {
			String msg = " Before return "
					+ "Gstr3BStructuralValidationsImpl.Validation() method,  "
					+ "validationResult : " + validationResult ;
			LOGGER.debug(msg);
		}
		return errMap;
	}

	private void isValidTaxableval(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidTaxableval() method,  "
					+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		BigDecimal taxableVal = NumberFomatUtil.getBigDecimal(rowData[3]);
		errMsg = "Invalid Taxable Value";
		if(taxableVal.compareTo(BigDecimal.ZERO) < 0){
			ProcessingResult result = new ProcessingResult("Upload", "ER6009",
					errMsg);
			validationResult.add(result);
			return;
		}
		if(!NumberFomatUtil.isValidDec(taxableVal)){
			ProcessingResult result = new ProcessingResult("Upload", "ER6008",
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isValidCess(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidCess() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		
		BigDecimal cess = NumberFomatUtil.getBigDecimal(rowData[7]);
		errMsg = "Invalid Cess";
		if(cess.compareTo(BigDecimal.ZERO) < 0){
			ProcessingResult result = new ProcessingResult("Upload", "ER6017",
					errMsg);
			validationResult.add(result);
			return;
		}
		if(!NumberFomatUtil.isValidDec(cess)){
			ProcessingResult result = new ProcessingResult("Upload", "ER6016",
					errMsg);
			validationResult.add(result);
			return;
		}
	}

	private void isValidCgst(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidCgst() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		
		BigDecimal cgst = NumberFomatUtil.getBigDecimal(rowData[5]);
		errMsg = "Invalid Cgst";
		if(cgst.compareTo(BigDecimal.ZERO) < 0){
			ProcessingResult result = new ProcessingResult("Upload", "ER6013",
					errMsg);
			validationResult.add(result);
			return;
		}
		if(!NumberFomatUtil.isValidDec(cgst)){
			ProcessingResult result = new ProcessingResult("Upload", "ER6012",
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isValidSgst(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidSgst() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		BigDecimal sgst = NumberFomatUtil.getBigDecimal(rowData[6]);
		errMsg = "Invalid Sgst";
		if(sgst.compareTo(BigDecimal.ZERO) < 0){
			ProcessingResult result = new ProcessingResult("Upload", "ER6015",
					errMsg);
			validationResult.add(result);
			return;
		}
		if(!NumberFomatUtil.isValidDec(sgst)){
			ProcessingResult result = new ProcessingResult("Upload", "ER6014",
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isValidIgst(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidIgst() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		BigDecimal igst = NumberFomatUtil.getBigDecimal(rowData[4]);
		errMsg = "Invalid Igst";
		if(igst.compareTo(BigDecimal.ZERO) < 0){
			ProcessingResult result = new ProcessingResult("Upload", "ER6011",
					errMsg);
			validationResult.add(result);
			return;
		}
		if(!NumberFomatUtil.isValidDec(igst)){
			ProcessingResult result = new ProcessingResult("Upload", "ER6010",
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isValidTable(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidTable() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		String table = (String)rowData[2];
		List<String> tableList = Arrays.asList("3.1(A)", "3.1(B)", "3.1(C)",
				"3.1(D)", "3.1(E)", "4(A)(1)", "4(A)(2)", "4(A)(3)", "4(A)(4)",
				"4(A)(5)", "4(B)(1)", "4(B)(2)", "4(D)(1)", "4(D)(2)",
				"5(A)(1)", "5(A)(2)", "5(A)(3)", "5(A)(4)", "5.1(A)",
				"5.1(B))");
		if (table == null || table == ""
				|| !tableList.contains(table.toUpperCase())) {
			errMsg = "Invalid Table";
			ProcessingResult result = new ProcessingResult("Upload", "ER6007",
					errMsg);
			validationResult.add(result);
			return;
		}

	}

	private void isValidTaxPeriod(Object[] rowData,
			List<ProcessingResult> validationResult) {
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidTaxPeriod() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}

		String taxPeriod = String.valueOf(rowData[0]);
		
		String gstin = (String) rowData[1];

		if (taxPeriod == null || taxPeriod == "") {
			errMsg = "Return Period cannot be left blank.";
			ProcessingResult result = new ProcessingResult("Upload", "ER6001",
					errMsg);
			validationResult.add(result);
			return;
		}
		
		if (isInValidDate(taxPeriod)) {
			errMsg = " Invalid Return Period.";
			ProcessingResult result = new ProcessingResult("Upload", "ER6002",
					errMsg);
			validationResult.add(result);
			return;
		}
		
		
		if(isValidRegistraion(taxPeriod, gstin)){
			errMsg = "Return Period cannot be before Date of Registration";
			ProcessingResult result = new ProcessingResult("Upload", "ER6003",
					errMsg);
			validationResult.add(result);
			return;
		}
		}
		
		
	private boolean isValidRegistraion(String taxPeriod, String gstin) {
		
		LocalDate regDate = gstnRepo.findRegistraionDate(gstin);
		String month = taxPeriod.substring(0, 2);
		String year= taxPeriod.substring(2, taxPeriod.length());
		String day = "01";
		
		LocalDate cd = LocalDate.parse(year+"-"+month+"-"+day);
		if(regDate != null && regDate.isAfter(cd)){
			return true;
		}
		return false;
	}


	private boolean isInValidDate(String taxPeriod) {
		if (!Pattern.matches("[0-9]{6}",  taxPeriod) 
				|| Integer.valueOf(taxPeriod.substring(0, 2)) < 1
				|| Integer.valueOf(taxPeriod.substring(0, 2)) > 12)
			
			return true;

		return false;

	}

	private void isValidGstn(Object[] rowData,
			List<ProcessingResult> validationResult) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Inside Gstr3BStructuralValidationsImpl"
					+ ".isValidGstn() method,  "+ "rowData : " + rowData ;
			LOGGER.debug(msg);
		}
		String regEx = "^([0][1-9]|[1-2][0-9]|[3][0-7])([a-zA-Z]{5}[0-9]{4}"
				+ "[a-zA-Z]{1}[1-9a-zA-Z]{1}[zZ]{1}[0-9a-zA-Z]{1})+$";

		String gstin = (String) rowData[1];

		if (gstin == null || gstin == "") {
			errMsg = "Supplier GSTIN cannot be left balnk";
			ProcessingResult result = new ProcessingResult("Upload", "ER6005",
					errMsg);
			validationResult.add(result);
			return;
		}

		if (gstin.matches(regEx) || gstin.length() != 15) {
			errMsg = "Invalid Supplier GSTIN.";
			ProcessingResult result = new ProcessingResult("Upload", "ER6004",
					errMsg);
			validationResult.add(result);
			return;
		}

		if (gstnRepo.findByGstinAndIsDeleteFalse(gstin) == null) {
			errMsg = "Supplier GSTIN is not as per On-Boarding data";
			ProcessingResult result = new ProcessingResult("Upload", "ER6006",
					errMsg);
			validationResult.add(result);
		}

	}

}
