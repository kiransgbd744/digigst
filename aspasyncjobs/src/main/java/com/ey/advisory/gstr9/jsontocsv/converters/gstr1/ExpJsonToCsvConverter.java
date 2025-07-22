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
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpInvoice;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpLineItemDetail;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.ExpType;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("ExpJsonToCsvConverter")
public class ExpJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "Export Type,Invoice Status,"
			+ "CheckSum,Invoice Number,Invoice Date,Invoice Value,"
			+ "Shipping Bill Port Code,Shipping Bill No,Shipping Bill Date,"
			+ "Difference Percent,TaxableValue,Rate,IGST Amount,Cess Amount,"
			+ "Source Type of IRN,IRN Generation Date,IRN Number\r\n";

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
			if (rootName.equals(JobStatusConstants.EXP_TYPE)) {
				Type listType = new TypeToken<ArrayList<ExpInvoice>>() {
				}.getType();
				List<ExpInvoice> invoices = JsonUtil.newGsonInstance(false)
						.fromJson(reader, listType);
				for (ExpInvoice invoice : invoices) {
					for (ExpType type : invoice.getExpInvoice()) {
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

	private static void writeToCSV(ExpType invoice, String type,
			BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(type));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceStatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));
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
					GenUtil.toCsvString(lineItem.getRate()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getIgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(lineItem.getCessAmount())));
			
			//irn attributes in 2.1 version
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getSrctyp()));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getIrngendate()));
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getIrn()));
			
			GenUtil.appendStringToBuffer(buf, "\r\n");
			
			bw.write(buf.toString());
		}

	}

	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}
	
}
