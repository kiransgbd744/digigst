package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpLineItemDetail;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpaInvoice;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpaType;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ExpaJsonToCsvConverter")
public class ExpaJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "Export Type,Invoice Status,"
			+ "CheckSum,Original Invoice Number,"
			+ "Original Invoice Date,Invoice Number,Invoice Date,"
			+ "Invoice Value,Shipping Bill Port Code,"
			+ "Shipping Bill No,Shipping Bill Date,Difference Percent,"
			+ "TaxableValue,Rate,IGST Amount,Cess Amount\r\n";

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
			if (rootName.equals(JobStatusConstants.EXPA_TYPE)) {
				Type listType = new TypeToken<ArrayList<ExpaInvoice>>() {
				}.getType();
				List<ExpaInvoice> invoices = JsonUtil.newGsonInstance(false)
						.fromJson(reader, listType);
				for (ExpaInvoice invoice : invoices) {
					for (ExpaType type : invoice.getExpInvoice()) {
						writeToCSV(type, invoice.getExpType(), csvWriter[0]);
					}
				}
			}
		}
		reader.endObject();
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}			
	}

	private static void writeToCSV(ExpaType invoice, String type,
			BufferedWriter bw) throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(type));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceStatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));
		// GenUtil.appendStringToJoiner(joiner,
		// GenUtil.toCsvString(invoice.getCounterPartyGstin()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOriginalInvoiceNumber()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOriginalInvoiceDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpoForInvoice(invoice.getInvoiceNumber())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceValue()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getPortCode()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getBillNumber()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getBillDate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getDiffPercent()));
		String joinedString = joiner.toString();
		List<ExpLineItemDetail> b2bLineItemList = invoice.getLineItems();
		for (ExpLineItemDetail lineItem : b2bLineItemList) {
			StringBuilder buf = new StringBuilder(joinedString);
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getTaxableValue())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getRate())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getIgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getCessAmount())));
			GenUtil.appendStringToBuffer(buf, "\r\n");
			bw.write(buf.toString());
		}

	}

	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}	
	
}
