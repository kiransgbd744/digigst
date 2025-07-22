package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnurLineItem;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnuraInvoice;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CdnuraJsonToCsvConverter")
public class CdnuraJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "Invoice Status,"
			+ "CheckSum,Invoice Type,"
			+ "Note Type,Original Note Number,Original Note Date,Note Number,"
			+ "Note Date,Pre Gst,Invoice Number,Invoice Date,Invoice Value,"
			+ "Difference Percent,Delink Flag,POS,ReverseCharge,Line Number,Rate,"
			+ "TaxableValue,IGST Amount,Cess Amount\r\n";

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
			if (rootName.equals(JobStatusConstants.CDNURA_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					CdnuraInvoice invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, CdnuraInvoice.class);
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

	private static void writeToCSV(CdnuraInvoice invoice, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceStatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getNoteType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOrgNoteNumber()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOrgNoteDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getNoteNumber()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getNoteDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getPreGst()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpoForInvoice(invoice.getInvoiceNumber())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceValue()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getDiffPercent()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getDlinkFlag()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(invoice.getPos())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getReverseCharge()));
		String joinedString = joiner.toString();
		List<CdnurLineItem> cdnuraLineItemList = invoice.getLineItems();
		for (CdnurLineItem lineItem : cdnuraLineItemList) {
			StringBuilder buf = new StringBuilder(joinedString);
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getLineNumber()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getItemDetail().getRate()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getTaxableValue())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getIgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getCessAmount())));
			GenUtil.appendStringToBuffer(buf, "\r\n");
			bw.write(buf.toString());
		}

	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { HEADER };
	}

}
