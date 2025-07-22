package com.ey.advisory.gstr9.jsontocsv.converters.gstr7;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr7.Gstr7TdsDto;
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
@Component("Gstr7JsonToCsvConverter")
public class Gstr7JsonToCsvConverter implements JsonToCsvConverter {

	private static final String REPORT_HDR = "TDSDeductorGSTIN,ReturnPeriod,Checksum,"
			+ "OriginalTDSDeducteeGSTIN,OriginalReturnPeriod,OriginalGrossAmount,"
			+ "TDSDeducteeGSTIN,GrossAmount,TDSIGST,TDSCGST,TDSSGST,SourceofAmendment,Actiontaken\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR7 Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		LOGGER.debug("File Response {}", reader);
		SaveGstr7 gstr7Dto = gson.fromJson(reader, SaveGstr7.class);
		LOGGER.debug("Gstr7 Dto {}", gstr7Dto);

		if (gstr7Dto != null) {
			writeDataToCSV(gstr7Dto, csvWriter);
		}

	}

	private static void writeDataToCSV(SaveGstr7 gstr7Dto, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		if (gstr7Dto.getTdsInvoice() != null
				&& !gstr7Dto.getTdsInvoice().isEmpty()) {

			for (Gstr7TdsDto tdsDto : gstr7Dto.getTdsInvoice()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsDto.getChksum()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				defaultToZeroJoiner(joiner, 1);
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsDto.getGstin_ded()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsDto.getAmt_ded())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsDto.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsDto.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsDto.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				finalJoiner.add(joiner.toString());
			}
		}

		if (gstr7Dto.getTdsaInvoice() != null
				&& !gstr7Dto.getTdsaInvoice().isEmpty()) {

			for (Gstr7TdsaDto tdsaDto : gstr7Dto.getTdsaInvoice()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(CommonContext.getGstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString("'" + CommonContext.getTaxPeriod()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getChksum()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getOgstin_ded()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getOmonth()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsaDto.getOamt_ded())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getGstin_ded()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsaDto.getAmt_ded())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsaDto.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsaDto.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(tdsaDto.getSamt())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getSource()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(tdsaDto.getActTaken()));
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