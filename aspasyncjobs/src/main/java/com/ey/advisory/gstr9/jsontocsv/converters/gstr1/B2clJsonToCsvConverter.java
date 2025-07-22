package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.B2clInvoice;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.B2clLineItem;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("B2clJsonToCsvConverter")
public class B2clJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "POS,Invoice Status,CheckSum,"
			+ "Invoice Number,Invoice Date,Invoice Value,Ecom Tin,Invoice Type,"
			+ "Difference Percent,Line Number,Rate,"
			+ "TaxableValue,IGST Amount,Cess Amount\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader, BufferedWriter... csvWriter)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}

		int colCount = HEADER.split(",").length;

		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.B2CL_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					String pos = "";
					reader.beginObject();

					List<String[]> list = new ArrayList<>();
					while (reader.hasNext()) {
						String name = reader.nextName();
						if (name.equals("pos")) {
							pos = GenUtil.checkExpoAndEmpty(reader.nextString());
						} else if (name.equals("inv")) {
							reader.beginArray();
							while (reader.hasNext()) {
								B2clInvoice invoice = JsonUtil
										.newGsonInstance(false)
										.fromJson(reader, B2clInvoice.class);
								// Read the invoice into an array and store
								// it
								// in the list.
								List<String[]> arr = getLineItemList(invoice,
										colCount);
								list.addAll(arr);

							}
							reader.endArray();
						} else {
							reader.skipValue(); // avoid some unhandle
												// events
						}
					}

					// At this point, populate the elements in the array
					// with the ctin and cfs. Once this is done, we can
					// flush the array to the csvwriter and the mergedFile
					// writer.
					populateCtinAndCfs(pos, list);
					writeToCSV(list, colCount, csvWriter[0]);
					reader.endObject();
				}
				reader.endArray();
			} else {
				reader.skipValue(); // avoid some unhandle events
			}
		}
		reader.endObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}

	}

	/**
	 * Populate the ctin and cfs values in the array.
	 * 
	 * @param ctin
	 * @param cfs
	 * @param list
	 */
	private void populateCtinAndCfs(String pos, List<String[]> list) {

		for (String[] arr : list) {
			arr[0] = GenUtil.toCsvString(pos);
		}
	}

	private static List<String[]> getLineItemList(B2clInvoice invoice,
			int colCount) throws IOException {

		List<String[]> list = new ArrayList<>();
		List<B2clLineItem> b2clLineItemList = invoice.getLineItems();
		for (B2clLineItem lineItem : b2clLineItemList) {
			int i = 0;
			// Invoice level data.
			String[] arr = new String[colCount];
			arr[i++] = null;
			arr[i++] = GenUtil.toCsvString(invoice.getInvoicestatus());
			arr[i++] = GenUtil.toCsvString(invoice.getCheckSum());
			arr[i++] = GenUtil.toCsvString(GenUtil.checkExpoForInvoice(invoice.getInvoiceNumber()));
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceDate());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceValue());
			arr[i++] = GenUtil.toCsvString(invoice.getEcomTin());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceType());
			arr[i++] = GenUtil.toCsvString(invoice.getDiffPercent());
			// Line item level data.
			arr[i++] = GenUtil.toCsvString(lineItem.getLineNumber());
			arr[i++] = GenUtil.toCsvString(lineItem.getItemDetail().getRate());
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getTaxableValue()));
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getIgstAmount()));
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getCessAmount()));

			list.add(arr);
		}

		return list;
	}

	private static void writeToCSV(List<String[]> list, int colCount,
			BufferedWriter bw) throws IOException {
		for (String[] arr : list) {
			StringJoiner joiner = new StringJoiner(",");
			for (int i = 0; i < colCount; i++) {
				GenUtil.appendStringToJoiner(joiner, arr[i]);
			}
			bw.write(joiner.toString());
			bw.write("\r\n");
		}
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { HEADER };
	}

}
