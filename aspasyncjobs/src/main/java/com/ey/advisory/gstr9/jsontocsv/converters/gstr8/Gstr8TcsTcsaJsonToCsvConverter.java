package com.ey.advisory.gstr9.jsontocsv.converters.gstr8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.TcsLineItem;
import com.ey.advisory.app.docs.dto.TcsTcsaInvoiceData;
import com.ey.advisory.app.docs.dto.TcsaLineItem;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr8TcsTcsaJsonToCsvConverter")
public class Gstr8TcsTcsaJsonToCsvConverter implements JsonToCsvConverter {

	private static final String REPORT_HDR = "eCOMGSTIN,ReturnPeriod,OriginalReturnPeriod,"
			+ "OriginalNetSupplies,RecordType,DocumentType,SupplyType,SupplierGSTINorEnrolmentID,"
			+ "OriginalSupplierGSTINorEnrolmentID,POS,OriginalPOS,SuppliesToRegistered,ReturnsFromRegistered,"
			+ "SuppliesToUnRegistered,ReturnsFromUnRegistered,NetSupplies,IntegratedTaxAmount,"
			+ "CentralTaxAmount,StateUTTaxAmount,Chksum,Source\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR8 Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		LOGGER.debug("File Response {}", reader);
		TcsTcsaInvoiceData gstr8Dto = gson.fromJson(reader,
				TcsTcsaInvoiceData.class);
		LOGGER.debug("Gstr8 Dto {}", gstr8Dto);

		if (gstr8Dto != null) {
			writeDataToCSV(gstr8Dto, csvWriter);
		}

	}

	private static void writeDataToCSV(TcsTcsaInvoiceData gstr8Dto,
			BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		if (gstr8Dto.getTcsLineItems() != null
				&& !gstr8Dto.getTcsLineItems().isEmpty()) {

			for (TcsLineItem tcsDto : gstr8Dto.getTcsLineItems()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TCS"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("INV"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TAX"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsDto.getSupplierGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsDto.getPos()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsDto.getGrossSuppliesMadeRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsDto.getGrossSuppliesReturnedRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsDto.getGrossSuppliesMadeUnRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(tcsDto
								.getGrossSuppliesReturnedUnRegistered())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tcsDto.getNetAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
						.checkExponenForAmt(tcsDto.getIntegratedTaxAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tcsDto.getCentralAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
						.checkExponenForAmt(tcsDto.getStateTaxAmount())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsDto.getCheckSum()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				finalJoiner.add(joiner.toString());
			}
		}

		if (gstr8Dto.getTcsaLineItems() != null
				&& !gstr8Dto.getTcsaLineItems().isEmpty()) {

			for (TcsaLineItem tcsaDto : gstr8Dto.getTcsaLineItems()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));

				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(tcsaDto.getOriginalSupplierGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						"'" + tcsaDto.getOriginalFinancialPeriod()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TCSA"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("RNV"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TAX"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsaDto.getSupplierGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(tcsaDto.getOriginalSupplierGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(tcsaDto.getPos()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(tcsaDto.getOpos()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsaDto.getGrossSuppliesMadeRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsaDto.getGrossSuppliesReturnedRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								tcsaDto.getGrossSuppliesMadeUnRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(tcsaDto
								.getGrossSuppliesReturnedUnRegistered())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tcsaDto.getNetAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
						.checkExponenForAmt(tcsaDto.getIntegratedTaxAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
						.checkExponenForAmt(tcsaDto.getCentralAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
						.checkExponenForAmt(tcsaDto.getStateTaxAmount())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsaDto.getCheckSum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tcsaDto.getSource()));

				finalJoiner.add(joiner.toString());
			}
		}

		bw.write(finalJoiner.toString());
		bw.write("\r\n");

		// BufferedWriter bw = new BufferedWriter();
		LOGGER.debug("Exiting writeDataToCSV");
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { REPORT_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 1;
	}

	private static StringJoiner defaultToZeroJoiner(StringJoiner joiner,
			int noOfColumns) {

		for (int i = 0; i < noOfColumns; i++) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		return joiner;
	}

	public static void main(String[] args) throws IOException {
		BufferedWriter[] csvWriter = new BufferedWriter[1];
		String jsonString = "{\n" +
			    "  \"gstin\": \"08GGMPS7032E6C7\",\n" +
			    "  \"fp\": \"102017\",\n" +
			    "  \"from_time\": \"21-02-2017 11:30\",\n" +
			    "  \"tcs\": [\n" +
			    "    {\n" +
			    "      \"chksum\": \"ASDFGJKLPTBBJKBJKBBJKBB\",\n" +
			    "      \"stin\": \"05AAFPQ6179B1ZQ\",\n" +
			    "      \"stin_name\": \"ABC Ltd\",\n" +
			    "      \"supR\": 0,\n" +
			    "      \"retsupR\": 0,\n" +
			    "      \"supU\": 0,\n" +
			    "      \"retsupU\": 0,\n" +
			    "      \"amt\": 0,\n" +
			    "      \"iamt\": 0,\n" +
			    "      \"camt\": 0,\n" +
			    "      \"samt\": 0\n" +
			    "    }\n" +
			    "  ],\n" +
			    "  \"tcsa\": [\n" +
			    "    {\n" +
			    "      \"chksum\": \"ASDFGJKLPTBBJKBJKBBJKBB\",\n" +
			    "      \"ofp\": \"082017\",\n" +
			    "      \"stin\": \"05AAFPQ6179B1ZQ\",\n" +
			    "      \"ostin\": \"05AAFPQ6179B1ZQ\",\n" +
			    "      \"stin_name\": \"ABC Ltd\",\n" +
			    "      \"ostin_name\": \"ABC Ltd\",\n" +
			    "      \"supR\": 0,\n" +
			    "      \"retsupR\": 0,\n" +
			    "      \"supU\": 0,\n" +
			    "      \"retsupU\": 0,\n" +
			    "      \"amt\": 5000,\n" +
			    "      \"camt\": 50,\n" +
			    "      \"samt\": 50,\n" +
			    "      \"iamt\": 0,\n" +
			    "      \"source\": \"S\",\n" +
			    "      \"actn\": \"Y\"\n" +
			    "    }\n" +
			    "  ]\n" +
			    "}";
		 // Create a StringReader
        StringReader stringReader = new StringReader(jsonString);
        
        // Create a JsonReader from StringReader
        JsonReader jsonReader = new JsonReader(stringReader);
        Gstr8TcsTcsaJsonToCsvConverter converter = new Gstr8TcsTcsaJsonToCsvConverter();
        converter.convertJsonTOCsv(jsonReader,csvWriter[0]);
	}
}