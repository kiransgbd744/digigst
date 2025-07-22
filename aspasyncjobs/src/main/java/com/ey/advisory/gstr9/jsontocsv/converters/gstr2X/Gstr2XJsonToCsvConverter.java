package com.ey.advisory.gstr9.jsontocsv.converters.gstr2X;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Component("Gstr2XJsonToCsvConverter")
@Slf4j
public class Gstr2XJsonToCsvConverter implements JsonToCsvConverter {

	private static final String CLASS_NAME = Gstr2XJsonToCsvConverter.class
			.getName();

	private static final String SUMMARY_REPORT_HDR = "GSTIN,Type,Tax period,"
			+ "Month,GSTIN of deductor/Collector,Deductor/Collector Name,Document Number,Document Date,Original Month,Original Document No,"
			+ "Original Document Date,SuppliesCollected,SuppliesReturned,Taxable Value/Net Supplies,IGST,CGST,SGST,"
			+ "Invoice Value,Original Taxable Value,Original Invoice Value,POS,Chksum,GSTN Action,GSTN Remarks,GSTN Comment\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {
		BufferedWriter detailedWriter = csvWriters[0];
		Gson gson = GsonUtil.newSAPGsonInstance();
		Gstr2XgetCallJsonDto gstr2XGetReqDto = gson.fromJson(reader,
				Gstr2XgetCallJsonDto.class);
		writeDetailedReportToCSV(gstr2XGetReqDto, detailedWriter);
		detailedWriter.flush();
	}

	private static void writeDetailedReportToCSV(Gstr2XgetCallJsonDto invoice,
			BufferedWriter bw) throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeDetailedReportToCSV");

		List<Gstr2XgetCallJsonTdsDto> tdsList = invoice.getTdsList();
		List<Gstr2XgetCallJsonTdsaDto> tdsaList = invoice.getTdsaList();
		List<Gstr2XgetCallJsonTcsDto> tcsList = invoice.getTcsList();
		List<Gstr2XgetCallJsonTcsaDto> tcsaList = invoice.getTcsaList();
		String gstin = invoice.getSummaryDto().getGstin();
		String returnPeriod = invoice.getSummaryDto().getRetperiod();
		StringJoiner finalJoiner = new StringJoiner("\n");
		for (Gstr2XgetCallJsonTdsDto tds : tdsList) {
			StringJoiner joiner = new StringJoiner("\t");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin)); // GSTIN
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("TDS")); // Type
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(returnPeriod)); // Tax period
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tds.getMonth())); // Month
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tds.getCtin())); // GSTIN of deductor
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Deductor
																			// Name
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tds.getInum())); // Document Number
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tds.getIdt())); // Document Date
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Month
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// No
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// Date
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Supplies
																			// Collected
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Supplies
																			// Returned
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tds.getAmtded()))); // Taxable
																				// Value/Net
																				// Supplies
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tds.getIamt()))); // IGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tds.getCamt()))); // CGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tds.getSamt()))); // SGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tds.getIval()))); // Invoice
																				// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Taxable
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // POS
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tds.getChksum())); // Chksum
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Action
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Remarks
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Comment

			finalJoiner.add(joiner.toString());
		}

		for (Gstr2XgetCallJsonTdsaDto tdsa : tdsaList) {
			StringJoiner joiner = new StringJoiner("\t");

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin)); // GSTIN
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("TDSA")); // Type
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(returnPeriod)); // Tax period
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getMonth())); // Month
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getCtin())); // GSTIN of deductor
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Deductor
																			// Name
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getInum())); // Document Number
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getIdt())); // Document Date
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getOmonth())); // Original Month
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// No
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// Date
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // SuppliesCollected
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // SuppliesReturned
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tdsa.getAmtded()))); // Taxable
																					// Value
																					// /
																					// Net
																					// Supplies
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tdsa.getIamt()))); // IGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tdsa.getCamt()))); // CGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tdsa.getSamt()))); // SGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tdsa.getIval()))); // Invoice
																				// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Taxable
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // POS
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tdsa.getChksum())); // Chksum
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Action
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Remarks
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // GSTN
																			// Comment

			finalJoiner.add(joiner.toString());
		}

		for (Gstr2XgetCallJsonTcsDto tcs : tcsList) {
			StringJoiner joiner = new StringJoiner("\t");

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin)); // GSTIN
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("TCS")); // Type
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(returnPeriod)); // Tax period
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getMonth())); // Month
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getCtin())); // GSTIN of collector
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Collector
																			// Name
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Document
																			// Number
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Document
																			// Date
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Month
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// No
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// Date

			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getSupR())
							+ GenUtil.checkExponenForAmt(tcs.getSupU()))); // SuppliesCollected
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getRetsupR())
							+ GenUtil.checkExponenForAmt(tcs.getRetsupU()))); // SuppliesReturned

			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getAmt()))); // Taxable
																				// Value
																				// /
																				// Net
																				// Supplies
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getIamt()))); // IGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getCamt()))); // CGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcs.getSamt()))); // SGST

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Taxable
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getPos())); // POS
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getChksum())); // Chksum

			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getFlag())); // GSTN Action
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getRemarks())); // GSTN Remarks
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcs.getComment())); // GSTN Comment

			finalJoiner.add(joiner.toString());
		}

		for (Gstr2XgetCallJsonTcsaDto tcsa : tcsaList) {
			StringJoiner joiner = new StringJoiner("\t");

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin)); // GSTIN
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("TCSA")); // Type
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(returnPeriod)); // Tax period
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getMonth())); // Month
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getCtin())); // GSTIN of collector
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Collector
																			// Name
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Document
																			// Number
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Document
																			// Date
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getOmonth())); // Original Month
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// No
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Document
																			// Date

			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getSupR())
							+ GenUtil.checkExponenForAmt(tcsa.getSupU()))); // SuppliesCollected
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getRetsupR())
							+ GenUtil.checkExponenForAmt(tcsa.getRetsupU()))); // SuppliesReturned
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getAmt()))); // Taxable
																				// Value
																				// /
																				// Net
																				// Supplies
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getIamt()))); // IGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getCamt()))); // CGST
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GenUtil.checkExponenForAmt(tcsa.getSamt()))); // SGST

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Taxable
																			// Value
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("")); // Original
																			// Invoice
																			// Value
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getPos())); // POS
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getChksum())); // Chksum

			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getFlag())); // GSTN Action
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getRemarks())); // GSTN Remarks
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tcsa.getComment())); // GSTN Comment

			finalJoiner.add(joiner.toString());
		}

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeDetailedReportToCSV");
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { SUMMARY_REPORT_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 1;
	}

}