package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.data.repositories.client.GetAnx1BatchRepository;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.B2csInvoice;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("B2csJsonToCsvConverter")
public class B2csJsonToCsvConverter implements JsonToCsvConverter {
	
	private static String isFiled = "N";
	private static String stateName = null;
	
	@Autowired
	GetAnx1BatchRepository getAnx1BatchRepo;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	
	private static final String HEADER = "SupplyType,StateName,POS,RATEOFTAX,TaxableValue,"
			+ "IGSTAmount,CGSTAmount,SGST/UTGSTAmount,Cess Amount,"
			+ "DifferentialPercentageRate,Type,EcomGSTIN,IsFiled\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader, BufferedWriter... csvWriter)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}
		
		if(stateName == null)
			checkIsFiled();
		
		stateName = defaultStateCache.getStateName(CommonContext.getGstin().substring(0, 2));

		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(JobStatusConstants.B2CS_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					B2csInvoice invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, B2csInvoice.class);
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

	private static void writeToCSV(B2csInvoice invoice, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		/*GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoicestatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));*/
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getSupplyType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(stateName));//statename
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getPos()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getRate()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpo(invoice.getTaxableValue())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpo(invoice.getIgstAmount())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpo(invoice.getCgstAmount())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpo(invoice.getSgstAmount())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GenUtil.checkExpo(invoice.getCessAmount())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getDiffPercent()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getEcomTin()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(isFiled));
		
		// GenUtil.appendStringToJoiner(joiner, "\r\n");
		bw.write(joiner.toString());
		bw.write("\r\n");
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
					CommonContext.getTaxPeriod(), "B2CS", "GSTR1A");
		} else {
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "B2CS", "GSTR1");
		}
		if(obj != null ){
			Integer i = Integer.parseInt(obj.toString());
			if(i == 1)
				isFiled = APIConstants.Y;
		}
	}

}
