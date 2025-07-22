package com.ey.advisory.gstr9.jsontocsv.converters.gstr2A;

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
import com.ey.advisory.gstr9.jsontocsv.model.gstr2A.B2bInvoiceGstr2A;
import com.ey.advisory.gstr9.jsontocsv.model.gstr2A.B2bLineItemGstr2A;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("B2bJsonToCsvConverterGstr2A")
public class B2bJsonToCsvConverterGstr2A implements JsonToCsvConverter {

	private static final String HEADER = "CustomerGstin,CounterPartyFlag,"
			+ "CheckSum,InvoiceNumber,InvoiceDate,InvoiceValue,Pos,"
			+ "ReverseCharge,LineNumber,Taxable Value,IGST Amount,"
			+ "SGST Amount,CGST Amount,Cess Amount,Rate,InvoiceType,"
			+ "Difference Percentage\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriter) throws IOException {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked "
					+ "for " + this.getClass().getSimpleName());
		}		
		
		int colCount = HEADER.split(",").length;
		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.GSTR2A_B2B_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					String ctin = "";
					String cfs = "";
					reader.beginObject();
					List<String[]> list = new ArrayList<>();
					while (reader.hasNext()) {
						String name = reader.nextName();
						if (name.equals("ctin")) {
							ctin = reader.nextString();
						} else if (name.equals("cfs")) {
							cfs = reader.nextString();
						} else if (name.equals("inv")) {
							reader.beginArray();
							while (reader.hasNext()) {
								B2bInvoiceGstr2A invoice = JsonUtil
										.newGsonInstance(false)
										.fromJson(reader,
												B2bInvoiceGstr2A.class);
								// System.out.println(invoice);
								LOGGER.info("Invoice" + invoice);
								// Read the invoice into an array and store
								// it
								// in the list.
								List<String[]> arr = getLineItemList(
										invoice, colCount);
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
					populateCtinAndCfs(ctin, cfs, list);
					writeToCSV(list, colCount, csvWriter[0]);

					reader.endObject();
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

	/**
	 * Populate the ctin and cfs values in the array.
	 * 
	 * @param ctin
	 * @param cfs
	 * @param list
	 */
	private void populateCtinAndCfs(String ctin, String cfs,
			List<String[]> list) {

		for (String[] arr : list) {
			arr[0] = GenUtil.toCsvString(ctin);
			arr[1] = GenUtil.toCsvString(cfs);
		}
	}

	private static List<String[]> getLineItemList(B2bInvoiceGstr2A invoice,
			int colCount) throws IOException {

		List<String[]> list = new ArrayList<>();
		List<B2bLineItemGstr2A> b2bLineItemList = invoice.getLineItems();
		for (B2bLineItemGstr2A lineItem : b2bLineItemList) {
			int i = 0;
			// Invoice level data.
			String[] arr = new String[colCount];
			arr[i++] = null;
			arr[i++] = null;
			arr[i++] = GenUtil.toCsvString(invoice.getCheckSum());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceNumber());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceDate());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceValue());
			arr[i++] = GenUtil.toCsvString(invoice.getPos());
			arr[i++] = GenUtil.toCsvString(invoice.getReverseCharge());

			// Line item level data.
			arr[i++] = GenUtil.convLineNumToString(lineItem.getLineNumber());
			arr[i++] = GenUtil
					.toCsvString(lineItem.getItemDetail().getTaxableValue());
			arr[i++] = GenUtil
					.toCsvString(lineItem.getItemDetail().getIgstAmount());
			arr[i++] = GenUtil
					.toCsvString(lineItem.getItemDetail().getCgstAmount());
			arr[i++] = GenUtil
					.toCsvString(lineItem.getItemDetail().getSgstAmount());
			arr[i++] = GenUtil
					.toCsvString(lineItem.getItemDetail().getCessAmount());
			arr[i++] = GenUtil.toCsvString(lineItem.getItemDetail().getRate());
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceType());
			arr[i] = GenUtil.toCsvString(invoice.getDiffPercent());

			list.add(arr);
		}

		return list;
	}

	private static void writeToCSV(List<String[]> list, 
					int colCount, BufferedWriter bw)
			throws IOException {
		for (String[] arr : list) {
			StringJoiner joiner = new StringJoiner(",");
			for (int i = 0; i < colCount; i++) {
				GenUtil.appendStringToJoiner(joiner, arr[i]);
			}
			bw.write(joiner.toString());
			bw.write("\r\n");
		}
	}
	
	/**
	 * Return the Csv Header String for the client.
	 */
	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}	
}
