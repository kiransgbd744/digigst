package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
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
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.B2csaInvoice;
import com.ey.advisory.gstr9.jsontocsv.model.gstr1.B2csaLineItemDetail;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("B2csaJsonToCsvConverter")
public class B2csaJsonToCsvConverter implements JsonToCsvConverter {

	private static String isFiled = "N";
	private static String stateName = "";
	
	@Autowired
	GetAnx1BatchRepository getAnx1BatchRepo;
	
	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String HEADER = "OriginalReturnPeriod,SupplyType,StateName,POS,"
			+ "OriginalPOS,RATEOFTAX,TaxableValue,IGSTAmount,CGSTAmount,"
			+ "SGST/UTGSTAmount,CessAmount,DifferentialPercentageRate,Type,EcomGSTIN,IsFiled\r\n";
			
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
			if (rootName.equals(JobStatusConstants.B2CSA_TYPE)) {
				reader.beginArray();
				while (reader.hasNext()) {
					B2csaInvoice invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, B2csaInvoice.class);
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

	private static void writeToCSV(B2csaInvoice invoice, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");
		/*GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getInvoicestatus()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getCheckSum()));*/
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOrgMonthOfInvoice()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getSupplyType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(stateName)); // StateName
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getPos()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getOrgPos()));
		
		
		/*GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getType()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getEcomTin()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(invoice.getDiffPercent()));*/
		
		String joinedString = joiner.toString();
		List<B2csaLineItemDetail> b2csaLineItemList = invoice.getItems();
		for (B2csaLineItemDetail lineItem : b2csaLineItemList) {
			StringBuilder buf = new StringBuilder(joinedString);
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(lineItem.getRate()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getTaxableValue())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getIgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getCgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getSgstAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(GenUtil.checkExpo(lineItem.getCessAmount())));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getDiffPercent()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getType()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(invoice.getEcomTin()));
			GenUtil.appendStringToBuffer(buf, ",");
			GenUtil.appendStringToBuffer(buf,
					GenUtil.toCsvString(isFiled));
			GenUtil.appendStringToBuffer(buf, "\r\n");
			bw.write(buf.toString());
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
					CommonContext.getTaxPeriod(), "B2CSA", "GSTR1A");
		} else {
			obj = getAnx1BatchRepo.findIsFiled(CommonContext.getGstin(),
					CommonContext.getTaxPeriod(), "B2CSA", "GSTR1");
		}
		if(obj != null ){
			Integer i = Integer.parseInt(obj.toString());
			if(i == 1)
				isFiled = APIConstants.Y;
		}
	}

}
