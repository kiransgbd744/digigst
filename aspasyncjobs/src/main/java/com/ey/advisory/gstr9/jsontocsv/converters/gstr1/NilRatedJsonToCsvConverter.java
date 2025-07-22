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
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.NilRatedDetail;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.NilRatedHeader;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("NilRatedJsonToCsvConverter")
public class NilRatedJsonToCsvConverter implements JsonToCsvConverter {

	private static final String HEADER = "SupplyType,NilAmt,ExemptedAmt,"
			+ "NonGstSupAmt,IsFiled\r\n";	
	 
	private static String isFiled = null;
	
	@Autowired
	GetAnx1BatchRepository getAnx1BatchRepo;
	 
	@Override
	public void convertJsonTOCsv(JsonReader reader,
				BufferedWriter... csvWriter) throws IOException {
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked "
					+ "for " + this.getClass().getSimpleName());
		}
		
		if(isFiled == null)
			checkIsFiled();
		
		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.NIL_RATED_TYPE)) {
				NilRatedHeader invoices = JsonUtil.newGsonInstance(false)
						.fromJson(reader, NilRatedHeader.class);
				writeToCSV(invoices, csvWriter[0]);
			}
		}
		reader.endObject();
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv completed for "
					+ this.getClass().getSimpleName());
		}		
	}
	
	private static void writeToCSV(NilRatedHeader invoice, 
				BufferedWriter bw) throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		/*GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoiceStatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));*/
		List<NilRatedDetail> hsnDetails = invoice.getNilRatedDetails();
		for (NilRatedDetail lineItem : hsnDetails) {
			StringBuilder buf = new StringBuilder();
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getSupplyType()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getNilRatedAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getTotalExemptedAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getTotalNonGstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(isFiled));
			GenUtil.appendStringToBuffer(buf, "\r\n");
			bw.write(buf.toString());
		}
	}


	/**
	 * Return the Csv Header String for the client.
	 */
	@Override
	public String[] getCsvHeaderStrings() {		
		return new String[] {HEADER};
	}
	
	private void checkIsFiled() {
		Object obj = null;
		if (CommonContext.getReturnType() != null && APIConstants.GSTR1A
				.equalsIgnoreCase(CommonContext.getReturnType()))

		{
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "NIL", "GSTR1A");
		} else {
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "NIL", "GSTR1");
		}
		
		if(obj != null ){
			Integer i = Integer.parseInt(obj.toString());
			if(i == 1)
				isFiled = APIConstants.Y;
		} else {
			isFiled = APIConstants.N;
		}
	}
	
}
