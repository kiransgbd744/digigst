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
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnurInvoice;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnurLineItem;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CdnurJsonToCsvConverter")
public class CdnurJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "Invoice Status,"
			+ "CheckSum,Invoice Type,"
			+ "Note Type,Note Number,Note Date,Pre Gst,Invoice Number,"
			+ "Invoice Date,Invoice Value,Difference Percent,Line Number,Rate,"
			+ "TaxableValue,IGST Amount,Cess Amount,Source Type of IRN,"
			+ "IRN Generation Date,IRN Number,Delink Flag,POS,ReverseCharge\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriter) throws IOException {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked "
					+ "for " + this.getClass().getSimpleName());
		}
		
		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.CDNUR_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					CdnurInvoice invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, CdnurInvoice.class);
					writeToCSV(invoice, csvWriter[0]);
				}
				reader.endArray();
			}
		}
		reader.endObject();
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}			
	}

	private static void writeToCSV(CdnurInvoice invoice, 
						BufferedWriter bw) throws IOException {
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
		String joinedString = joiner.toString();
		List<CdnurLineItem> cdnurLineItemList = invoice.getLineItems();
		for (CdnurLineItem lineItem : cdnurLineItemList) {
			StringBuilder buf = new StringBuilder(joinedString);
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getLineNumber()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getItemDetail().getRate()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil
					.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getTaxableValue())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil
					.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getIgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf, GenUtil
					.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getCessAmount())));
			
			//irn attributes in 2.1 version
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getSrctyp()));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getIrngendate()));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getIrn()));
			
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getDlinkFlag()));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(invoice.getPos())));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getReverseCharge()));
			
			GenUtil.appendStringToBuffer(buf, "\r\n");
			
			bw.write(buf.toString());
		}

	}

	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}	
	
}
