package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.HsnDetail;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.HsnHeader;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("HsnSumJsonToCsvConverter")
public class HsnSumJsonToCsvConverter implements JsonToCsvConverter {

	private static String isFiled = null;

	@Autowired
	GetAnx1BatchRepository getAnx1BatchRepo;

	private static final String HEADER = "RecordType,HSN,Description,UserDescription,UQC,TotalQuantity,TaxableValue,"
			+ "IGSTAmount,CGSTAmount,SGST/UTGSTAmount,CessAmount,TotalValue,IsFiled\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader, BufferedWriter... csvWriter)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}

		if (isFiled == null)
			checkIsFiled();

		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.HSN_SUM_TYPE)) {
				HsnHeader invoices = JsonUtil.newGsonInstance(false)
						.fromJson(reader, HsnHeader.class);
				writeToCSV(invoices, csvWriter[0]);
			}
		}
		reader.endObject();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}

	}

	private static void writeToCSV(HsnHeader invoice, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(invoice.getInvoiceStatus()));
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(invoice.getCheckSum()));
		 */

		// Process different HSN detail types using the helper method
		writeHsnDetailsToCSV(invoice.getHsnDetails(), "", bw);
		writeHsnDetailsToCSV(invoice.getHsnB2bDetails(), "B2B", bw);
		writeHsnDetailsToCSV(invoice.getHsnB2cDetails(), "B2C", bw);

	}

	// Helper method to process and write CSV for each HSN detail
	private static void writeHsnDetailsToCSV(List<HsnDetail> hsnDetails,
			String type, BufferedWriter bw) throws IOException {
		if (hsnDetails != null) {
			for (HsnDetail lineItem : hsnDetails) {
				StringJoiner joiner = new StringJoiner(",");
				StringBuilder buf = new StringBuilder(joiner.toString());
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(type));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf,
						GenUtil.toCsvString(lineItem.getHsnCode()));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf,
						GenUtil.toCsvString(lineItem.getDescription()));
				GenUtil.appendStringToBuffer(buf, ",");
				if (type.equalsIgnoreCase("")) {
					GenUtil.appendStringToBuffer(buf, "");
				} else {
					GenUtil.appendStringToBuffer(buf,
							GenUtil.toCsvString(lineItem.getUserDescription()));
				}
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf,
						GenUtil.toCsvString(lineItem.getUnitOfMeasurement()));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf,
						GenUtil.toCsvString(lineItem.getQuantity()));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getTaxableValue())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getIgstAmount())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getCgstAmount())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getSgstAmount())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getCessAmount())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(
						GenUtil.checkExpo(lineItem.getTotalValue())));
				GenUtil.appendStringToBuffer(buf, ",");
				GenUtil.appendStringToBuffer(buf, GenUtil.toCsvString(isFiled));
				GenUtil.appendStringToBuffer(buf, "\r\n");
				bw.write(buf.toString());
			}
		}
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { HEADER };
	}

	private void checkIsFiled() {
		Object obj = null;
		if (CommonContext.getReturnType() != null && APIConstants.GSTR1A
				.equalsIgnoreCase(CommonContext.getReturnType()))

		{
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "HSN", "GSTR1A");
		} else {
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "HSN", "GSTR1");
		}

		if (obj != null) {
			Integer i = Integer.parseInt(obj.toString());
			if (i == 1)
				isFiled = APIConstants.Y;
		} else {
			isFiled = APIConstants.N;
		}
	}

}
