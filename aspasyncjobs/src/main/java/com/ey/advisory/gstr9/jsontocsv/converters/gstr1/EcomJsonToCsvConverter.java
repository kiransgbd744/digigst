package com.ey.advisory.gstr9.jsontocsv.converters.gstr1;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.DefaultStateCache;
import com.ey.advisory.app.docs.dto.B2BEcomInvoices;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.Ecom;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EcomJsonToCsvConverter")
public class EcomJsonToCsvConverter implements JsonToCsvConverter {

	@Autowired
	DefaultStateCache defaultStateCache;

	private static final String HEADER = "TaxPeriod,Type,Customer GSTIN,SupplierGSTIN,"
			+ "SupplyType,CheckSum,InvoiceNumber,InvoiceDate,InvoiceValue,Pos,"
			+ "InvoiceType,LineNumber,Rate,Taxable Value,IGST Amount,CGST Amount,"
			+ "SGST Amount,Cess Amount,Flag\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader, BufferedWriter... csvWriter)
			throws IOException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("ConvertJsonToCsv invoked " + "for "
					+ this.getClass().getSimpleName());
		}

		reader.beginObject();
		while (reader.hasNext()) {
			String rootName = reader.nextName();
			if (rootName.equals(APIConstants.ECOM)) {
				reader.beginArray();
				while (reader.hasNext()) {
					Ecom invoice = JsonUtil.newGsonInstance(false)
							.fromJson(reader, Ecom.class);
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

	private void writeToCSV(Ecom ecom, BufferedWriter bw) throws IOException {
		StringJoiner joiner = new StringJoiner(",");

		List<B2BEcomInvoices> b2bDetails = ecom.getB2b();
		List<B2BEcomInvoices> urp2BDetails = ecom.getUrp2b();
		List<B2CSInvoices> b2csInvoice = ecom.getB2c();
		List<B2CSInvoices> urpB2CDetails = ecom.getUrp2c();

		StringJoiner finalJoiner = new StringJoiner("\n");

		setTransData(joiner, b2bDetails,
				GSTConstants.GSTR1_15I.concat("- B2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15III.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15II.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setSummaryData(joiner, urpB2CDetails,
				GSTConstants.GSTR1_15II.concat("- B2C"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setSummaryData(joiner, b2csInvoice,
				GSTConstants.GSTR1_15IV.concat("- URP2C"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		bw.write(finalJoiner.toString());
		bw.write("\r\n");

	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { HEADER };
	}

	
	
	private void setTransData(StringJoiner joiner,
			List<B2BEcomInvoices> b2bEcomDetails, String tableType) {
		if (b2bEcomDetails != null) { 
			for (B2BEcomInvoices b2bEcom : b2bEcomDetails) {
				if (b2bEcom != null) { 
					for (B2BInvoiceData b2bInvoices : b2bEcom
							.getB2bInvoiceData()) {
						if (b2bInvoices != null) { 
													
							for (B2bLineItem lineItem : b2bInvoices
									.getLineItems()) {
								if (lineItem != null) { 
													
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(tableType));
									GenUtil.appendStringToJoiner(joiner, GenUtil
											.toCsvString(b2bEcom.getRtin()));
									GenUtil.appendStringToJoiner(joiner, GenUtil
											.toCsvString(b2bEcom.getStin()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getSupplyType()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(
													b2bInvoices.getCheckSum()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getInvoiceNumber()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getInvoiceDate()));
									GenUtil.appendStringToJoiner(joiner, GenUtil
											.checkExponenForAmt(b2bInvoices
													.getInvoiceValue()));
									GenUtil.appendStringToJoiner(joiner, GenUtil
											.toCsvString(b2bInvoices.getPos()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getInvoiceType()));
									GenUtil.appendStringToJoiner(joiner, String
											.valueOf(lineItem.getLineNumber()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(
													lineItem.getItemDetail()
															.getRate()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(lineItem
													.getItemDetail().getTaxableValue()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(
													lineItem.getItemDetail()
															.getIgstAmount()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(
													lineItem.getItemDetail()
															.getCgstAmount()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(
													lineItem.getItemDetail()
															.getSgstAmount()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.checkExponenForAmt(
													lineItem.getItemDetail()
															.getCessAmount()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getInvoiceStatus()));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void setSummaryData(StringJoiner joiner,
			List<B2CSInvoices> b2csInvoice, String tableType) {
		if (b2csInvoice != null) {
			for (B2CSInvoices b2csInv : b2csInvoice) {
				if (b2csInv != null) { 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(tableType));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null)); 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2csInv.getStin()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2csInv.getSupplyType()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2csInv.getCheckSum()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null)); 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null)); 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2csInv.getPointOfSupply()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null)); 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null)); 
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.checkExponenForAmt(b2csInv.getRate()));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.checkExponenForAmt(b2csInv.getTaxableValue()));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.checkExponenForAmt(b2csInv.getIgstAmount()));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.checkExponenForAmt(b2csInv.getCgstAmount()));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.checkExponenForAmt(b2csInv.getSgstAmount()));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.checkExponenForAmt(b2csInv.getCessAmount()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2csInv.getInvoiceStatus()));
				}
			}
		}
	}

	/*private static void setSummaryData(StringJoiner joiner,
			List<B2CSInvoices> b2csInvoice, String tableType) {
		for (B2CSInvoices b2csInv : b2csInvoice) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tableType));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2csInv.getStin()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2csInv.getSupplyType()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2csInv.getCheckSum()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2csInv.getPointOfSupply()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getRate()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getTaxableValue()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getIgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getCgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getSgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(b2csInv.getCessAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2csInv.getInvoiceStatus()));
		}

	}*/
	
	/*private static void setTransData(StringJoiner joiner,
	List<B2BEcomInvoices> b2bEcomDetails, String tableType) {
for (B2BEcomInvoices b2bEcom : b2bEcomDetails) {
	for (B2BInvoiceData b2bInvoices : b2bEcom.getB2bInvoiceData()) {
		for (B2bLineItem lineItem : b2bInvoices.getLineItems()) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(tableType));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bEcom.getRtin()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bEcom.getStin()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bInvoices.getSupplyType()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bInvoices.getCheckSum()));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(b2bInvoices.getInvoiceNumber()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bInvoices.getInvoiceDate()));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.checkExponenForAmt(b2bInvoices.getInvoiceValue()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bInvoices.getPos()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(b2bInvoices.getInvoiceType()));
			GenUtil.appendStringToJoiner(joiner,
					String.valueOf(lineItem.getLineNumber()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(
							lineItem.getItemDetail().getRate()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(lineItem.getItemDetail()
							.getTaxableValue()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(
							lineItem.getItemDetail().getIgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(
							lineItem.getItemDetail().getCgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(
							lineItem.getItemDetail().getSgstAmount()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.checkExponenForAmt(
							lineItem.getItemDetail().getCessAmount()));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.toCsvString(b2bInvoices.getInvoiceStatus()));
		}
	}
}

}*/
	/*public static void main(String[] args) {
		

		StringJoiner joiner = new StringJoiner(",");
		
		String apiResp = "{\n  \"ecom\": {\n    \"b2b\": [\n      {\n        \"inv\": [\n          {\n            \"val\": 34567,\n            \"itms\": [\n              {\n                \"num\": 100,\n                \"itm_det\": {\n                  \"rt\": 1,\n                  \"txval\": 456,\n                  \"iamt\": 4.56\n                }\n              }\n            ],\n            \"inv_typ\": \"DE\",\n            \"flag\": \"N\",\n            \"pos\": \"32\",\n            \"idt\": \"12-03-2023\",\n            \"inum\": \"DE-1\",\n            \"sply_ty\": \"INTER\",\n            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n          },\n          {\n            \"val\": 235423,\n            \"itms\": [\n              {\n                \"num\": 25,\n                \"itm_det\": {\n                  \"rt\": 0.25,\n                  \"txval\": 1234,\n                  \"iamt\": 3.09\n                }\n              }\n            ],\n            \"inv_typ\": \"DE\",\n            \"flag\": \"N\",\n            \"pos\": \"32\",\n            \"idt\": \"08-03-2023\",\n            \"inum\": \"Normal-1\",\n            \"sply_ty\": \"INTER\",\n            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n          }\n        ],\n        \"rtin\": \"32KLJIP1179C1ZF\",\n        \"stin\": \"33CBCHE8993D1Z5\"\n      },\n      {\n        \"inv\": [\n          {\n            \"val\": 567,\n            \"itms\": [\n              {\n                \"num\": 100,\n                \"itm_det\": {\n                  \"rt\": 1,\n                  \"txval\": 765\n                }\n              }\n            ],\n            \"inv_typ\": \"SEWOP\",\n            \"flag\": \"N\",\n            \"pos\": \"37\",\n            \"idt\": \"21-03-2023\",\n            \"inum\": \"B2B-2\",\n            \"sply_ty\": \"INTER\",\n            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n          },\n          {\n            \"val\": 7654654,\n            \"itms\": [\n              {\n                \"num\": 100,\n                \"itm_det\": {\n                  \"rt\": 1,\n                  \"txval\": 78654,\n                  \"iamt\": 786.54\n                }\n              }\n            ],\n            \"inv_typ\": \"SEWP\",\n            \"flag\": \"N\",\n            \"pos\": \"27\",\n            \"idt\": \"21-03-2023\",\n            \"inum\": \"B2B\",\n            \"sply_ty\": \"INTER\",\n            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n          }\n        ],\n        \"rtin\": \"37AABMH4573CKZN\",\n        \"stin\": \"33CBCHE8993D1Z5\"\n      }\n    ],\n    \"b2c\": [\n      {\n        \"stin\": \"33CBCHE8993D1Z5\",\n        \"csamt\": 546,\n        \"samt\": 0,\n        \"rt\": 1,\n        \"flag\": \"N\",\n        \"pos\": \"04\",\n        \"txval\": 8778654,\n        \"camt\": 0,\n        \"iamt\": 87786.54,\n        \"sply_ty\": \"INTER\",\n        \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n      }\n    ],\n    \"urp2b\": [\n      {\n        \"inv\": [\n          {\n            \"val\": 235423,\n            \"itms\": [\n              {\n                \"num\": 25,\n                \"itm_det\": {\n                  \"rt\": 0.25,\n                  \"txval\": 1234,\n                  \"iamt\": 3.09\n                }\n              }\n            ],\n            \"flag\": \"N\",\n            \"pos\": \"32\",\n            \"idt\": \"08-03-2023\",\n            \"inum\": \"URP2B-1\",\n            \"sply_ty\": \"INTER\",\n            \"inv_typ\": \"R\",\n            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n          }\n        ],\n        \"rtin\": \"32KLJIP1179C1ZF\"\n      }\n    ],\n    \"urp2c\": [\n      {\n        \"csamt\": 546,\n        \"samt\": 0,\n        \"rt\": 1,\n        \"flag\": \"N\",\n        \"pos\": \"04\",\n        \"txval\": 8778654,\n        \"camt\": 0,\n        \"iamt\": 87786.54,\n        \"sply_ty\": \"INTER\",\n        \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n      }\n    ]\n  }\n}";

	        
	        JsonObject respObject = JsonParser.parseString(apiResp).getAsJsonObject();


	        // Create Gson instance and parse the JSON into an Ecom object
	        Gson gson = new Gson();
	        Ecom invoice = gson.fromJson(respObject.getAsJsonObject(APIConstants.ECOM), Ecom.class);
		
		Ecom invoice = JsonUtil.newGsonInstance(false)
				.fromJson(reader, Ecom.class);

		List<B2BEcomInvoices> b2bDetails = invoice.getB2b();
		List<B2BEcomInvoices> urp2BDetails = invoice.getUrp2b();
		List<B2CSInvoices> b2csInvoice = invoice.getB2c();
		List<B2CSInvoices> urpB2CDetails = invoice.getUrp2c();

		StringJoiner finalJoiner = new StringJoiner("\n");

		setTransData(joiner, b2bDetails,
				GSTConstants.GSTR1_15I.concat("- B2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15III.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15II.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setSummaryData(joiner, urpB2CDetails,
				GSTConstants.GSTR1_15II.concat("- B2C"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setSummaryData(joiner, b2csInvoice,
				GSTConstants.GSTR1_15IV.concat("- URP2C"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");
		
		System.out.println(finalJoiner.toString());

	
	}*/
}
