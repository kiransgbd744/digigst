package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.docs.dto.SupEcomAmd;
import com.ey.advisory.app.docs.dto.SupEcomInvoices;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("SupEcomAmdJsonToCsvConverter")
public class SupEcomAmdJsonToCsvConverter implements JsonToCsvConverter {

	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String HEADER = "TaxPeriod,Type,ecom GSTIN,Original ecom GSTIN,"
			+ "Original Tax Period,CheckSum,TaxableValue,"
			+ "IGST Amount,CGST Amount,SGST Amount,Cess Amount,Flag\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader, BufferedWriter... csvWriter)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}

		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(APIConstants.SUPECOAMD)) {
				reader.beginArray();
				while (reader.hasNext()) {
					SupEcomAmd invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, SupEcomAmd.class);
					writeToCSV(invoice, csvWriter[0]);
				}
				reader.endArray();
			}
		}
		reader.endObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}
	}

	private static void writeToCSV(SupEcomAmd supEcomAmd, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");

		List<SupEcomInvoices> clttxa = supEcomAmd.getClttxa();
		List<SupEcomInvoices> paytxa = supEcomAmd.getPaytxa();

		StringJoiner finalJoiner = new StringJoiner("\n");

		for (SupEcomInvoices supEcomInvoices : clttxa) {
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GSTConstants.GSTR1_14AI.concat(" -Sec 52")));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(supEcomInvoices.getEtin()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getSuppval()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getIgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getCgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getSgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getCess()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(supEcomInvoices.getFlag()));
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		for (SupEcomInvoices supEcomInvoices : paytxa) {
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(GSTConstants.GSTR1_14AI.concat(" -Sec 9(5)")));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(supEcomInvoices.getEtin()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getSuppval()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getIgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getCgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getSgst()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(supEcomInvoices.getCess()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(supEcomInvoices.getFlag()));
			// GenUtil.appendStringToJoiner(joiner, "\r\n");
		}

		finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");

		bw.write(finalJoiner.toString());
		bw.write("\r\n");

	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { HEADER };
	}

}
