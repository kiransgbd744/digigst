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
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnrLineItem;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.CdnraInvoice;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("CdnraJsonToCsvConverter")
public class CdnraJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "Customer Gstin,"
			+ "GSTR2 Filing Status,Invoice Status,"
			+ "Counter Party Flag,Tax Period,"
			+ "Uploaded By,CheckSum,Original Note Number,"
			+ "Original Note Date,Note Type,Note Number,Note Date,"
			+ "Invoice Number,Invoice Date,Invoice Value,PreGST,Difference Percent,"
			+ "Delink Flag,POS,ReverseCharge,"
			+ "Line Number,Rate,TaxableValue,IGST Amount,"
			+ "CGST Amount,SGST Amount,Cess Amount\r\n";

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
			if (rootName.equals(JobStatusConstants.CDNRA_TYPE)) {
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
						} else if (name.equals("nt")) {
							reader.beginArray();
							while (reader.hasNext()) {
								CdnraInvoice invoice = JsonUtil
										.newGsonInstance(false)
										.fromJson(reader, CdnraInvoice.class);
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
	private void populateCtinAndCfs(String ctin, String cfs,
			List<String[]> list) {

		for (String[] arr : list) {
			arr[0] = GenUtil.toCsvString(ctin);
			arr[1] = GenUtil.toCsvString(cfs);
		}
	}

	private static List<String[]> getLineItemList(CdnraInvoice invoice,
			int colCount) throws IOException {

		List<String[]> list = new ArrayList<>();
		List<CdnrLineItem> cdnrLineItemList = invoice.getLineItems();
		for (CdnrLineItem lineItem : cdnrLineItemList) {
			int i = 0;
			// Invoice level data.
			String[] arr = new String[colCount];
			arr[i++] = null;
			arr[i++] = null;
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceStatus());
			arr[i++] = GenUtil.toCsvString(invoice.getCounterPartyFlag());
			arr[i++] = GenUtil.toCsvString(invoice.getTaxPeriod());
			arr[i++] = GenUtil.toCsvString(invoice.getUploadedBy());
			arr[i++] = GenUtil.toCsvString(invoice.getCheckSum());
			arr[i++] = GenUtil.toCsvString(invoice.getOriginalNoteNumber());
			arr[i++] = GenUtil.toCsvString(invoice.getOriginalNoteDate());
			arr[i++] = GenUtil.toCsvString(invoice.getNoteType());
			arr[i++] = GenUtil.toCsvString(invoice.getNoteNumber());
			arr[i++] = GenUtil.toCsvString(invoice.getNoteDate());

			arr[i++] = GenUtil.toCsvString(GenUtil.checkExpoForInvoice(invoice.getInvoiceNumber()));
			arr[i++] = GenUtil.toCsvString(invoice.getInvoiceDate());
			arr[i++] = GenUtil
					.toCsvString(GenUtil.checkExpoAndEmpty(invoice.getInvoiceValue()));
			arr[i++] = GenUtil.toCsvString(invoice.getPreGst());
			arr[i++] = GenUtil.toCsvString(invoice.getDiffPercent());
			
			arr[i++] = GenUtil.toCsvString((invoice.getDlinkFlag()));
			arr[i++] = GenUtil.toCsvString(GenUtil.checkExpoAndEmpty(invoice.getPos()));
			arr[i++] = GenUtil.toCsvString(invoice.getReverseCharge());
			// Line item level data.
			arr[i++] = GenUtil.toCsvString(lineItem.getLineNumber());
			arr[i++] = GenUtil.toCsvString(lineItem.getItemDetail().getRate());
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getTaxableValue()));
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getIgstAmount()));
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getCgstAmount()));
			arr[i++] = GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(lineItem.getItemDetail().getSgstAmount()));
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
