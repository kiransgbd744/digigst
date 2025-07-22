package com.ey.advisory.gstr9.jsontocsv.converters.gstr8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.UrdLineItem;
import com.ey.advisory.app.docs.dto.UrdUrdaInvoiceData;
import com.ey.advisory.app.docs.dto.UrdaLineItem;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr8UrdUrdaJsonToCsvConverter")
public class Gstr8UrdUrdaJsonToCsvConverter implements JsonToCsvConverter {

	private static final String REPORT_HDR = "eCOMGSTIN,ReturnPeriod,OriginalReturnPeriod,"
			+ "OriginalNetSupplies,RecordType,DocumentType,SupplyType,SupplierGSTINorEnrolmentID,"
			+ "OriginalSupplierGSTINorEnrolmentID,SuppliesToRegistered,ReturnsFromRegistered,"
			+ "SuppliesToUnRegistered,ReturnsFromUnRegistered,NetSupplies,IntegratedTaxAmount,"
			+ "CentralTaxAmount,StateUTTaxAmount,Chksum,Source\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR8 Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		LOGGER.debug("File Response {}", reader);
		UrdUrdaInvoiceData gstr8Dto = gson.fromJson(reader,
				UrdUrdaInvoiceData.class);
		LOGGER.debug("Gstr8 Dto {}", gstr8Dto);

		if (gstr8Dto != null) {
			writeDataToCSV(gstr8Dto, csvWriter);
		}

	}

	private static void writeDataToCSV(UrdUrdaInvoiceData gstr8Dto,
			BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		if (gstr8Dto.getUrdLineItems() != null
				&& !gstr8Dto.getUrdLineItems().isEmpty()) {

			for (UrdLineItem urdDto : gstr8Dto.getUrdLineItems()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("URD"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("INV"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TAX"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(urdDto.getEnrolmentId()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								urdDto.getGrossSuppliesMadeRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								urdDto.getGrossSuppliesReturnedRegistered())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(urdDto.getNetAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(urdDto.getCheckSum()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());
			}
		}

		if (gstr8Dto.getUrdaLineItems() != null
				&& !gstr8Dto.getUrdaLineItems().isEmpty()) {

			for (UrdaLineItem urdaDto : gstr8Dto.getUrdaLineItems()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(urdaDto.getOriginalFinancialPeriod()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("URDA"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("RNV"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString("TAX"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(urdaDto.getEnrolmentId()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(urdaDto.getOriginalEnrolmentId()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								urdaDto.getGrossSuppliesMadeRegistered())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GenUtil.checkExponenForAmt(
								urdaDto.getGrossSuppliesReturnedRegistered())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(urdaDto.getNetAmount())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(urdaDto.getCheckSum()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
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

}