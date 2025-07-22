package com.ey.advisory.gstr9.jsontocsv.converters.gstr2A;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr2A.TdsInvoiceGstr2A;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("TdsJsonToCsvConverterGstr2A")
public class TdsJsonToCsvConverterGstr2A implements JsonToCsvConverter {

	private static final String HEADER = "GstinDeducted,AmountDeducted,"
			+ "IGST Amount,CGST Amount,SGST Amount\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriter) throws IOException {

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked "
					+ "for " + this.getClass().getSimpleName());
		}		
				
		reader.beginObject();
		while (reader.hasNext()) {
			// reader.beginObject();
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.GSTR2A_TDS_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					TdsInvoiceGstr2A invoice = JsonUtil
							.newGsonInstance(false)
							.fromJson(reader, TdsInvoiceGstr2A.class);
					LOGGER.info("Invoice" + invoice);
					writeToCSV(invoice, csvWriter[0]);
				}
				reader.endArray();
			} else {
				reader.skipValue(); // avoid some unhandle events
			}
		}
		reader.endObject();
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}	
	}

	private static void writeToCSV(TdsInvoiceGstr2A invoice, 
						BufferedWriter bw) throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getGstinDeducted()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getAmtDeducted()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getIgstAmount()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCgstAmount()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getSgstAmount()));
		String joinedString = joiner.toString();
		StringBuilder buf = new StringBuilder(joinedString);
		GenUtil.appendStringToBuffer(buf, "\r\n");
		bw.write(buf.toString());
	}

	/**
	 * Return the Csv Header String for the client.
	 */
	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}			
}
