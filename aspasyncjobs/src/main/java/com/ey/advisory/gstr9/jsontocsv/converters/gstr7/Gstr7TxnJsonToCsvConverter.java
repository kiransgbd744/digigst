package com.ey.advisory.gstr9.jsontocsv.converters.gstr7;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryTaxPaidDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7TaxPayIgstSummaryDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7TaxPaySummaryDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsTdsaInvDto;
import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsaDto;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7TxnJsonToCsvConverter")
public class Gstr7TxnJsonToCsvConverter implements JsonToCsvConverter {

	private static final String REPORT_HDR = "ReturnPeriod,Deductor GSTIN,Section,"
			+ "Document Number,Document Date,Original Document Number,"
			+ "Original Document Date,Deductee GSTIN,Deductee Name,Original DeducteeGSTIN,"
			+ "Original Deductee Name,Original Return Period,Original Taxable Value,"
			+ "Taxable Value,IGST,CGST,SGST,Invoice Value,"
			+ "Original Invoice Value,Checksum,Source,Action taken\r\n";

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { REPORT_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 1;
	}

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR7 Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		LOGGER.debug("File Response {}", reader);
		SaveGstr7 gstr7Dto = gson.fromJson(reader, SaveGstr7.class);

		if (gstr7Dto != null) {
			writeGstr7TxnDataToCSV(gstr7Dto, csvWriter);
		}
	}

	private static void writeGstr7TxnDataToCSV(SaveGstr7 gstr7Dto,
			BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeGstr7TxnDataToCSV");
		List<Gstr7TdsTdsaInvDto> invList = null;
		StringJoiner finalJoiner = new StringJoiner("\n");
		String reqTime = gstr7Dto.getReqTime();
		String deducteeGSTIN = "";
		String deducteeName = "";
		String orgDeducteeGSTIN = "";
		String orgDeducteeName = "";
		String orgReturnPeriod = "";
		String source = "";
		String actionTaken = "";
		
		if (gstr7Dto != null && gstr7Dto.getTdsInvoice() != null
				&& !gstr7Dto.getTdsInvoice().isEmpty()) {
			
			for (Gstr7TdsDto gstr7TdsDto : gstr7Dto.getTdsInvoice()) {

				deducteeGSTIN = gstr7TdsDto.getGstin_ded();
				deducteeName = gstr7TdsDto.getDeducteeName();
				invList = gstr7TdsDto.getGstr7TdsTdsaInvDto();
				if (invList != null && !invList.isEmpty()) {
					for (Gstr7TdsTdsaInvDto invData : invList) {
						buildCsvLine(finalJoiner, invData, deducteeGSTIN,
								deducteeName, orgDeducteeGSTIN, orgDeducteeName,
								orgReturnPeriod, "TDS",source ,actionTaken);
					}

				}
			}

		}

		if (gstr7Dto != null && gstr7Dto.getTdsaInvoice() != null
				&& !gstr7Dto.getTdsaInvoice().isEmpty()) {

			for (Gstr7TdsaDto gstr7TdsaDto : gstr7Dto.getTdsaInvoice()) {

				deducteeGSTIN = gstr7TdsaDto.getGstin_ded();
				deducteeName = gstr7TdsaDto.getDeducteeName();
				orgDeducteeGSTIN = gstr7TdsaDto.getOgstin_ded();
				orgDeducteeName = gstr7TdsaDto.getODeducteeName();
				orgReturnPeriod = gstr7TdsaDto.getOmonth();
				invList = gstr7TdsaDto.getGstr7TdsTdsaInvDto();
				if (invList != null && !invList.isEmpty()) {
					for (Gstr7TdsTdsaInvDto invData : invList) {
						buildCsvLine(finalJoiner, invData, deducteeGSTIN,
								deducteeName, orgDeducteeGSTIN, orgDeducteeName,
								orgReturnPeriod, "TDSA",invData.getSource() ,actionTaken);
					}

				}
			}

		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting Gstr6B2b writeDataToCSV");

	}

	private static void buildCsvLine(StringJoiner finalJoiner,
			Gstr7TdsTdsaInvDto invData, String deducteeGSTIN,
			String deducteeName, String orgDeducteeGSTIN,
			String orgDeducteeName, String orgReturnPeriod, String section,
			String source, String actionTaken) {

		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString("'" + CommonContext.getTaxPeriod()));// retPeriod
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(CommonContext.getGstin())); // DeductorGSTIN

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(section));// Section

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invData.getInum())); // Document Number

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invData.getIdt())); // Document Date

		
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(invData.getOinum()));// OriginalDocNo
		
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(invData.getOidt()));// OriginalDocDt

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(deducteeGSTIN));// Deductee GSTIN

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(deducteeName));// DeducteeName
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(orgDeducteeGSTIN));// Original DeducteeGSTIN

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(orgDeducteeName));// Original Deductee Name

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(orgReturnPeriod)); // Original Return Period

		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getOamt_ded()))); // Original
																			// Taxable
																			// Value

		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getAmt_ded()))); // Taxable
																					// Value
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getIamt()))); // IGST
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getCamt()))); // CGST
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getSamt()))); // SGST

		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(invData.getIval()))); // Invoice
																				// Value

		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(BigDecimal.ZERO))); // Original
																			// Invoice
																			// Value
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(invData.getChksum())));// Checksum
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(source)));// Source
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(actionTaken)));// Action taken
		finalJoiner.add(joiner.toString());

	}

}