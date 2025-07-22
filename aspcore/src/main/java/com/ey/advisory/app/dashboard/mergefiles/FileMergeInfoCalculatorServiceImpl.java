package com.ey.advisory.app.dashboard.mergefiles;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.ImmutableList;

@Component("FileMergeInfoCalculatorServiceImpl")
public class FileMergeInfoCalculatorServiceImpl
		implements FileMergeInfoCalculator {

	ImmutableList<String> transactionalSectionList = ImmutableList.of("B2B",
			"B2BA", "EXP", "EXPA", "B2CL", "B2CLA", "CDNR", "CDNRA", "CDNUR",
			"CDNURA");

	@Override
	public List<FileMergeInfo> calculator(Long requestId,
			List<FileMergeInput> requestData) {

		List<FileInfo> transactionalList = new ArrayList<>();
		List<FileInfo> nilRatedList = new ArrayList<>();
		List<FileInfo> hsnSummaryList = new ArrayList<>();
		List<FileInfo> atList = new ArrayList<>();
		List<FileInfo> ataList = new ArrayList<>();
		List<FileInfo> txpList = new ArrayList<>();
		List<FileInfo> txpaList = new ArrayList<>();
		List<FileInfo> b2csList = new ArrayList<>();
		List<FileInfo> b2csaList = new ArrayList<>();
		List<FileInfo> docIssueList = new ArrayList<>();
		List<FileInfo> supEcomList = new ArrayList<>();
		List<FileInfo> supEcomAmdList = new ArrayList<>();
		List<FileInfo> ecomList = new ArrayList<>();
		List<FileInfo> ecomAmdList = new ArrayList<>();

		for (FileMergeInput info : requestData) {

			List<String> sectionList = info.getSection();

			for (String section : sectionList) {

				FileInfo fileInfo = new FileInfo(info.getGstin(),
						info.getTaxPeriod(), section);

				if (transactionalSectionList.contains(section)) {
					transactionalList.add(fileInfo);
				} else if ("HSN_SUMMARY".equalsIgnoreCase(section)) {
					hsnSummaryList.add(fileInfo);
				} else if ("NIL_RATED".equalsIgnoreCase(section)) {
					nilRatedList.add(fileInfo);
				} else if ("AT".equalsIgnoreCase(section)) {
					atList.add(fileInfo);
				} else if ("ATA".equalsIgnoreCase(section)) {
					ataList.add(fileInfo);
				} else if ("TXP".equalsIgnoreCase(section)) {
					txpList.add(fileInfo);
				} else if ("TXPA".equalsIgnoreCase(section)) {
					txpaList.add(fileInfo);
				} else if ("B2CS".equalsIgnoreCase(section)) {
					b2csList.add(fileInfo);
				} else if ("B2CSA".equalsIgnoreCase(section)) {
					b2csaList.add(fileInfo);
				} else if ("DOC_ISSUE".equalsIgnoreCase(section)) {
					docIssueList.add(fileInfo);
				} else if ("SUPECOM14".equalsIgnoreCase(section)) {
					supEcomList.add(fileInfo);
				} else if ("SUPECOM14A".equalsIgnoreCase(section)) {
					supEcomAmdList.add(fileInfo);
				} else if ("ECOM15".equalsIgnoreCase(section)) {
					ecomList.add(fileInfo);
				} else if ("ECOM15A".equalsIgnoreCase(section)) {
					ecomAmdList.add(fileInfo);
				}
			}

		}

		List<FileMergeInfo> finalList = new ArrayList<>();

		if (!transactionalList.isEmpty()) {
			FileMergeInfo transactionalInfo = new FileMergeInfo(
					transactionalList, "Transaction_Data");
			finalList.add(transactionalInfo);
		}

		if (!hsnSummaryList.isEmpty()) {
			FileMergeInfo hsnInfo = new FileMergeInfo(hsnSummaryList,
					"HSN_SUMMARY");
			finalList.add(hsnInfo);
		}

		if (!nilRatedList.isEmpty()) {
			FileMergeInfo nilInfo = new FileMergeInfo(nilRatedList,
					"NIL_RATED");
			finalList.add(nilInfo);
		}

		if (!atList.isEmpty()) {
			FileMergeInfo atInfo = new FileMergeInfo(atList, "AT_Data");
			finalList.add(atInfo);
		}

		if (!ataList.isEmpty()) {
			FileMergeInfo ataInfo = new FileMergeInfo(ataList, "ATA_Data");
			finalList.add(ataInfo);
		}

		if (!txpList.isEmpty()) {
			FileMergeInfo txpInfo = new FileMergeInfo(txpList, "TXP_Data");
			finalList.add(txpInfo);
		}

		if (!txpaList.isEmpty()) {
			FileMergeInfo txpaInfo = new FileMergeInfo(txpaList, "TXPA_Data");
			finalList.add(txpaInfo);
		}

		if (!b2csList.isEmpty()) {
			FileMergeInfo b2csInfo = new FileMergeInfo(b2csList, "B2CS_Data");
			finalList.add(b2csInfo);
		}

		if (!b2csaList.isEmpty()) {
			FileMergeInfo b2csaInfo = new FileMergeInfo(b2csaList,
					"B2CSA_Data");
			finalList.add(b2csaInfo);
		}

		if (!docIssueList.isEmpty()) {
			FileMergeInfo docIssueInfo = new FileMergeInfo(docIssueList,
					"DOC_ISSUE");
			finalList.add(docIssueInfo);
		}

		if (!supEcomList.isEmpty()) {
			FileMergeInfo supEcomInfo = new FileMergeInfo(supEcomList,
					"SUPECOM14");
			finalList.add(supEcomInfo);
		}

		if (!supEcomAmdList.isEmpty()) {
			FileMergeInfo supEcomAmdInfo = new FileMergeInfo(supEcomAmdList,
					"SUPECOM14A");
			finalList.add(supEcomAmdInfo);
		}

		if (!ecomList.isEmpty()) {
			FileMergeInfo ecomInfo = new FileMergeInfo(ecomList, "ECOM15");
			finalList.add(ecomInfo);
		}

		if (!ecomAmdList.isEmpty()) {
			FileMergeInfo ecomAmdinfo = new FileMergeInfo(ecomAmdList,
					"ECOM15A");
			finalList.add(ecomAmdinfo);
		}

		return finalList;
	}

	@Override
	public List<FileMergeInfo> calculatorGSTR8(
			List<FileMergeInput> requestData) {

		List<FileInfo> tcsList = new ArrayList<>();
		List<FileInfo> urdList = new ArrayList<>();

		for (FileMergeInput info : requestData) {

			List<String> sectionList = info.getSection();

			for (String section : sectionList) {

				FileInfo fileInfo = new FileInfo(info.getGstin(),
						info.getTaxPeriod(), section);

				if ("TCS".equalsIgnoreCase(section)) {
					tcsList.add(fileInfo);
				} else if ("URD".equalsIgnoreCase(section)) {
					urdList.add(fileInfo);
				}
			}
		}

		List<FileMergeInfo> finalList = new ArrayList<>();

		if (!tcsList.isEmpty()) {
			FileMergeInfo tcsInfo = new FileMergeInfo(
					tcsList, "TCS");
			finalList.add(tcsInfo);
		}

		if (!urdList.isEmpty()) {
			FileMergeInfo urdInfo = new FileMergeInfo(urdList,
					"URD");
			finalList.add(urdInfo);
		}

		return finalList;
	}
	
	@Override
	public List<FileMergeInfo> calculatorGSTR3B(Long id,
			List<FileMergeInput> requestData) {

		List<FileMergeInfo> finalList = new ArrayList<>();
		List<FileInfo> summaryList = new ArrayList<>();
		List<FileInfo> taxPayableList = new ArrayList<>();

		for (FileMergeInput info : requestData) {

			FileInfo summaryInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "summary");
			summaryList.add(summaryInfo);

			FileInfo taxPayableInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "taxPayable");
			taxPayableList.add(taxPayableInfo);

		}

		if (summaryList != null && !summaryList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(summaryList,
					"GSTR3B_Summary");
			finalList.add(summaryInfo);
		}

		if (taxPayableList != null && !taxPayableList.isEmpty()) {
			FileMergeInfo taxPayableInfo = new FileMergeInfo(taxPayableList,
					"GSTR3B_TaxPayable");
			finalList.add(taxPayableInfo);
		}

		return finalList;

	}

	@Override
	public List<FileMergeInfo> calculatorGSTR9(
			List<FileMergeInput> requestData) {

		List<FileMergeInfo> finalList = new ArrayList<>();
		List<FileInfo> outInwardList = new ArrayList<>();
		List<FileInfo> taxPaidList = new ArrayList<>();
		List<FileInfo> hsnDetailsList = new ArrayList<>();

		for (FileMergeInput info : requestData) {

			FileInfo outInwardInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "outInWard");
			outInwardList.add(outInwardInfo);

			FileInfo taxPaidInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "taxPaid");
			taxPaidList.add(taxPaidInfo);

			FileInfo hsnDetailsInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "hsnDetails");
			hsnDetailsList.add(hsnDetailsInfo);

		}

		if (outInwardList != null && !outInwardList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(outInwardList,
					"GSTR9_OutInWard");
			finalList.add(summaryInfo);
		}

		if (taxPaidList != null && !taxPaidList.isEmpty()) {
			FileMergeInfo taxPayableInfo = new FileMergeInfo(taxPaidList,
					"GSTR9_TaxPaid");
			finalList.add(taxPayableInfo);
		}

		if (hsnDetailsList != null && !hsnDetailsList.isEmpty()) {
			FileMergeInfo taxPayableInfo = new FileMergeInfo(hsnDetailsList,
					"GSTR9_HsnDetails");
			finalList.add(taxPayableInfo);
		}

		return finalList;

	}

	@Override
	public List<FileMergeInfo> calculatorGSTR7(
			List<FileMergeInput> fileMergeInputList) {

		List<FileMergeInfo> finalList = new ArrayList<>();

		List<FileInfo> tdsList = new ArrayList<>();
		List<FileInfo> tdsSummaryList = new ArrayList<>();
		List<FileInfo> transactionalList = new ArrayList<>();

		for (FileMergeInput info : fileMergeInputList) {

			FileInfo tdsInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), APIConstants.GSTR7_TDS_DETAILS);
			tdsList.add(tdsInfo);

			FileInfo tdsSummaryInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), APIConstants.GSTR7_SUMMARY_DETAILS);
			tdsSummaryList.add(tdsSummaryInfo);
			
			FileInfo trasactionalInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), APIConstants.TRANSACTIONAL_DETAILS);
			transactionalList.add(trasactionalInfo);

		}

		if (tdsList != null && !tdsList.isEmpty()) {
			FileMergeInfo tdsData = new FileMergeInfo(tdsList, "GSTR7_DETAILS");
			finalList.add(tdsData);
		}
		if (tdsSummaryList != null && !tdsSummaryList.isEmpty()) {
			FileMergeInfo tdsSummaryData = new FileMergeInfo(tdsSummaryList,
					"GSTR7_SUMMARY_DETAILS");
			finalList.add(tdsSummaryData);
		}
		if (transactionalList != null && !transactionalList.isEmpty()) {
			FileMergeInfo transactionalData = new FileMergeInfo(
					transactionalList,
					"GSTR7_" + APIConstants.TRANSACTIONAL_DETAILS);
			finalList.add(transactionalData);
		}

		return finalList;

	}

	@Override
	public List<FileMergeInfo> calculatorGSTR2X(
			List<FileMergeInput> fileMergeInputList) {

		List<FileMergeInfo> finalList = new ArrayList<>();

		List<FileInfo> tdsList = new ArrayList<>();

		for (FileMergeInput info : fileMergeInputList) {

			FileInfo tdsInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "TCSANDTDS");
			tdsList.add(tdsInfo);

		}

		if (!tdsList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(tdsList,
					"GSTR2X_DETAILS");
			finalList.add(summaryInfo);
		}

		return finalList;

	}

	@Override
	public List<FileMergeInfo> calculatorITC04(
			List<FileMergeInput> fileMergeInputList) {

		List<FileMergeInfo> finalList = new ArrayList<>();

		List<FileInfo> getList = new ArrayList<>();

		for (FileMergeInput info : fileMergeInputList) {

			FileInfo tdsInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "GET");
			getList.add(tdsInfo);

		}

		if (getList != null && !getList.isEmpty()) {
			FileMergeInfo itc04Info = new FileMergeInfo(getList,
					"ITC04_DETAILS");
			finalList.add(itc04Info);
		}

		return finalList;
	}

	@Override
	public List<FileMergeInfo> calculatorGSTR6(
			List<FileMergeInput> fileMergeInputList) {

		List<FileMergeInfo> finalList = new ArrayList<>();
		List<FileInfo> b2bList = new ArrayList<>();
		List<FileInfo> b2baList = new ArrayList<>();
		List<FileInfo> cdnList = new ArrayList<>();
		List<FileInfo> cdnaList = new ArrayList<>();
		List<FileInfo> isdList = new ArrayList<>();
		List<FileInfo> isdaList = new ArrayList<>();

		for (FileMergeInput info : fileMergeInputList) {

			FileInfo b2bInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "B2B");
			b2bList.add(b2bInfo);

			FileInfo b2baInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "B2BA");
			b2baList.add(b2baInfo);

			FileInfo cdnInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "CDN");
			cdnList.add(cdnInfo);

			FileInfo cdnaInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "CDNA");
			cdnaList.add(cdnaInfo);

			FileInfo isdInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "ISD");
			isdList.add(isdInfo);

			FileInfo isdaInfo = new FileInfo(info.getGstin(),
					info.getTaxPeriod(), "ISDA");
			isdaList.add(isdaInfo);

		}

		if (!b2bList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(b2bList, "GSTR6_B2B");
			finalList.add(summaryInfo);
		}

		if (!b2baList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(b2baList,
					"GSTR6_B2BA");
			finalList.add(summaryInfo);
		}

		if (!cdnList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(cdnList, "GSTR6_CDN");
			finalList.add(summaryInfo);
		}

		if (!cdnaList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(cdnaList,
					"GSTR6_CDNA");
			finalList.add(summaryInfo);
		}

		if (!isdList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(isdList, "GSTR6_ISD");
			finalList.add(summaryInfo);
		}

		if (!isdaList.isEmpty()) {
			FileMergeInfo summaryInfo = new FileMergeInfo(isdaList,
					"GSTR6_ISDA");
			finalList.add(summaryInfo);
		}

		return finalList;
	}
}
