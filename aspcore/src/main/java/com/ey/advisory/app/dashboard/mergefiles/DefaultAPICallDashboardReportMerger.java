package com.ey.advisory.app.dashboard.mergefiles;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.AppException;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("DefaultAPICallDashboardReportMerger")
public class DefaultAPICallDashboardReportMerger
		implements APICallDashboardReportMerger {

	private static final List<String> GSTR1_SECTIONS = ImmutableList.of("B2B",
			"B2CL", "B2CS", "B2BA", "NIL_RATED", "CDNR", "CDNRA", "TXP", "AT",
			"EXP", "CDNUR", "B2CSA", "B2CLA", "EXPA", "ATA", "TXPA", "CDNURA",
			"HSN_SUMMARY", "DOC_ISSUE", "SUPECOM14","SUPECOM14A","ECOM15","ECOM15A");

	private static final List<String> GSTR3B_SECTIONS = ImmutableList
			.of("taxPayable", "summary");

	private static final List<String> ITC04_SECTIONS = ImmutableList.of("GET");

	private static final List<String> GSTR7_SECTIONS = ImmutableList.of(
			APIConstants.GSTR7_TDS_DETAILS, APIConstants.GSTR7_SUMMARY_DETAILS,APIConstants.TRANSACTIONAL_DETAILS);

	private static final List<String> GSTR2X_SECTIONS = ImmutableList
			.of("TCSANDTDS");

	private static final List<String> GSTR6_SECTIONS = ImmutableList.of("B2B",
			"B2BA", "CDN", "CDNA", "ISD", "ISDA");
	private static final List<String> GSTR8_SECTIONS = ImmutableList.of("TCS",
			"URD");

	@Autowired
	@Qualifier("FileMergeInfoCalculatorServiceImpl")
	FileMergeInfoCalculator mergeInfoCalculator;

	@Autowired
	@Qualifier("DefaultFileMerger")
	FileMerger fileMerger;

	@Override
	public String mergeReport(
			List<Triplet<String, String, String>> combinations, Long reportId) {
		List<FileMergeInput> fileMergeInputList = new ArrayList<>();
		String returnType = combinations.get(0).getValue2();
		List<FileMergeInfo> mergeInfoList = null;

		switch (returnType) {
		case APIConstants.GSTR1_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), GSTR1_SECTIONS)));
			mergeInfoList = mergeInfoCalculator.calculator(reportId,
					fileMergeInputList);
			break;
			
		case APIConstants.GSTR1A_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), GSTR1_SECTIONS)));
			mergeInfoList = mergeInfoCalculator.calculator(reportId,
					fileMergeInputList);
			break;

		case APIConstants.GSTR3B:
			combinations.forEach(obj -> fileMergeInputList
					.add(new FileMergeInput(obj.getValue0(), obj.getValue1(),
							GSTR3B_SECTIONS)));
			mergeInfoList = mergeInfoCalculator.calculatorGSTR3B(reportId,
					fileMergeInputList);
			break;

		case APIConstants.GSTR7_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), GSTR7_SECTIONS)));
			mergeInfoList = mergeInfoCalculator
					.calculatorGSTR7(fileMergeInputList);
			break;

		case APIConstants.ITC04_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), ITC04_SECTIONS)));
			mergeInfoList = mergeInfoCalculator
					.calculatorITC04(fileMergeInputList);
			break;

		case APIConstants.GSTR2X_RETURN_TYPE:
			combinations.forEach(obj -> fileMergeInputList
					.add(new FileMergeInput(obj.getValue0(), obj.getValue1(),
							GSTR2X_SECTIONS)));
			mergeInfoList = mergeInfoCalculator
					.calculatorGSTR2X(fileMergeInputList);
			break;

		case APIConstants.GSTR6_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), GSTR6_SECTIONS)));
			mergeInfoList = mergeInfoCalculator
					.calculatorGSTR6(fileMergeInputList);
			break;
		case APIConstants.GSTR9:
			List<String> dummyList = new ArrayList<>();
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), dummyList)));
			mergeInfoList = mergeInfoCalculator
					.calculatorGSTR9(fileMergeInputList);
			break;
		case APIConstants.GSTR8_RETURN_TYPE:
			combinations
					.forEach(obj -> fileMergeInputList.add(new FileMergeInput(
							obj.getValue0(), obj.getValue1(), GSTR8_SECTIONS)));
			mergeInfoList = mergeInfoCalculator
					.calculatorGSTR8(fileMergeInputList);
			break;
			
		default:
			String errMsg = String.format(
					"Currently Merger not supported"
							+ " returntype %s to generate ACD Report",
					returnType);
			LOGGER.error(errMsg);
			throw new AppException(errMsg);
		}
		return fileMerger.merge(reportId, mergeInfoList, returnType);
	}

}
