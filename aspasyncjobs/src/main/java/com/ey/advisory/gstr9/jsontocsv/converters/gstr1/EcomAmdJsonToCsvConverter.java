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
import com.ey.advisory.app.docs.dto.B2CSALineItem;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.Ecom;
import com.ey.advisory.app.docs.dto.EcomAmd;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EcomAmdJsonToCsvConverter")
public class EcomAmdJsonToCsvConverter implements JsonToCsvConverter {

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
			if (rootName.equals(APIConstants.ECOMAMD)) {
				reader.beginArray();
				while (reader.hasNext()) {
					EcomAmd invoice = JsonUtil.newGsonInstance(false)
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

	private void writeToCSV(EcomAmd ecom, BufferedWriter bw)
			throws IOException {
		StringJoiner joiner = new StringJoiner(",");

		List<B2BEcomInvoices> b2bDetails = ecom.getB2ba();
		List<B2BEcomInvoices> urp2BDetails = ecom.getUrp2ba();
		List<B2CSInvoices> b2csInvoice = ecom.getB2ca();
		List<B2CSInvoices> urpB2CDetails = ecom.getUrp2ca();

		StringJoiner finalJoiner = new StringJoiner("\n");

		setTransData(joiner, b2bDetails,
				GSTConstants.GSTR1_15AIA.concat("- B2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15AIB.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setTransData(joiner, urp2BDetails,
				GSTConstants.GSTR1_15AIIA.concat("- URP2B"));

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		setSummaryData(joiner, urpB2CDetails,
				GSTConstants.GSTR1_15AIIB.concat("- B2C"));

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
				if (b2bEcom != null && b2bEcom.getB2bInvoiceData() != null) { 
					for (B2BInvoiceData b2bInvoices : b2bEcom
							.getB2bInvoiceData()) {
						if (b2bInvoices != null
								&& b2bInvoices.getLineItems() != null) { 
							for (B2bLineItem lineItem : b2bInvoices
									.getLineItems()) {
								if (lineItem != null
										&& lineItem.getItemDetail() != null) { 
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
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(null)); 
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(null)); 
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(null)); 
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getOrigInvNumber()));
									GenUtil.appendStringToJoiner(joiner,
											GenUtil.toCsvString(b2bInvoices
													.getOrigInvDate()));
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
				if (b2csInv != null && b2csInv.getLineItems() != null) { 
					for (B2CSALineItem b2csLineItem : b2csInv.getLineItems()) {
						if (b2csLineItem != null) { 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(tableType));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(b2csInv.getStin()));
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(b2csInv.getSupplyType()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(b2csInv.getCheckSum()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(b2csInv.getOrgMonthInv()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(b2csInv.getOstin()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(b2csInv.getPointOfSupply()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(null)); 
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getRate()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getTaxableValue()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getIgstAmount()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getCgstAmount()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getSgstAmount()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.checkExponenForAmt(
											b2csLineItem.getCessAmount()));
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(b2csInv.getInvoiceStatus()));
						}
					}
				}
			}
		}
	}
/*public static void main(String[] args) {
		

		StringJoiner joiner = new StringJoiner(",");
		
		String apiResp = "{\n" +
				"  \"ecoma\": {\n" +
				"    \"b2ba\": [\n" +
				"      {\n" +
				"        \"inv\": [\n" +
				"          {\n" +
				"            \"val\": 445677,\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"num\": 100,\n" +
				"                \"itm_det\": {\n" +
				"                  \"rt\": 1,\n" +
				"                  \"txval\": 8456,\n" +
				"                  \"iamt\": 84.56\n" +
				"                }\n" +
				"              }\n" +
				"            ],\n" +
				"            \"oinum\": \"DE-1\",\n" +
				"            \"flag\": \"N\",\n" +
				"            \"inum\": \"de-amd-1\",\n" +
				"            \"inv_typ\": \"DE\",\n" +
				"            \"pos\": \"32\",\n" +
				"            \"idt\": \"08-03-2023\",\n" +
				"            \"oidt\": \"12-03-2023\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"rtin\": \"32KLJIP1179C1ZF\",\n" +
				"        \"stin\": \"33CBCHE8993D1Z5\"\n" +
				"      },\n" +
				"      {\n" +
				"        \"inv\": [\n" +
				"          {\n" +
				"            \"val\": 7654654,\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"num\": 300,\n" +
				"                \"itm_det\": {\n" +
				"                  \"rt\": 3,\n" +
				"                  \"txval\": 4567,\n" +
				"                  \"iamt\": 137.01\n" +
				"                }\n" +
				"              }\n" +
				"            ],\n" +
				"            \"oinum\": \"test\",\n" +
				"            \"flag\": \"N\",\n" +
				"            \"inum\": \"Test-Amend\",\n" +
				"            \"inv_typ\": \"SEWP\",\n" +
				"            \"pos\": \"07\",\n" +
				"            \"idt\": \"02-03-2023\",\n" +
				"            \"oidt\": \"21-03-2023\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"          },\n" +
				"          {\n" +
				"            \"val\": 13567,\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"num\": 100,\n" +
				"                \"itm_det\": {\n" +
				"                  \"rt\": 1,\n" +
				"                  \"txval\": 14300,\n" +
				"                  \"iamt\": 0,\n" +
				"                  \"camt\": 137.01,\n" +
				"                  \"samt\": 137.01,\n" +
				"                  \"csamt\": 137.01\n" +
				"                }\n" +
				"              }\n" +
				"            ],\n" +
				"            \"oinum\": \"test-2\",\n" +
				"            \"flag\": \"N\",\n" +
				"            \"inum\": \"sezwp-amd\",\n" +
				"            \"inv_typ\": \"SEWOP\",\n" +
				"            \"pos\": \"37\",\n" +
				"            \"idt\": \"19-03-2023\",\n" +
				"            \"oidt\": \"21-03-2023\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"rtin\": \"32KLJIP1179C1ZF\",\n" +
				"        \"stin\": \"33CBCHE8993D1Z5\"\n" +
				"      },\n" +
				"      {\n" +
				"        \"inv\": [\n" +
				"          {\n" +
				"            \"val\": 345678,\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"num\": 100,\n" +
				"                \"itm_det\": {\n" +
				"                  \"rt\": 1,\n" +
				"                  \"txval\": 65467,\n" +
				"                  \"iamt\": 654.67\n" +
				"                }\n" +
				"              }\n" +
				"            ],\n" +
				"            \"oinum\": \"test-1\",\n" +
				"            \"flag\": \"N\",\n" +
				"            \"inum\": \"test-amd-1\",\n" +
				"            \"inv_typ\": \"R\",\n" +
				"            \"pos\": \"37\",\n" +
				"            \"idt\": \"14-03-2023\",\n" +
				"            \"oidt\": \"15-03-2023\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"rtin\": \"32KLJIP1179C1ZF\",\n" +
				"        \"stin\": \"33CBCHE8993D1Z5\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"b2ca\": [\n" +
				"      {\n" +
				"        \"pos\": \"07\",\n" +
				"        \"posItms\": [\n" +
				"          {\n" +
				"            \"flag\": \"N\",\n" +
				"            \"omon\": \"062022\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"stin\": \"33CBCHE8993D1Z5\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\",\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"csamt\": 0,\n" +
				"                \"samt\": 0,\n" +
				"                \"rt\": 1.5,\n" +
				"                \"txval\": 34353,\n" +
				"                \"camt\": 0,\n" +
				"                \"iamt\": 515.29\n" +
				"              },\n" +
				"              {\n" +
				"                \"csamt\": 0,\n" +
				"                \"samt\": 0,\n" +
				"                \"rt\": 2,\n" +
				"                \"txval\": 34353,\n" +
				"                \"camt\": 0,\n" +
				"                \"iamt\": 515.29\n" +
				"              }\n" +
				"            ]\n" +
				"          }\n" +
				"        ]\n" +
				"      }\n" +
				"    ],\n" +
				"    \"urp2ba\": [\n" +
				"      {\n" +
				"        \"inv\": [\n" +
				"          {\n" +
				"            \"val\": 98765434,\n" +
				"            \"itms\": [\n" +
				"              {\n" +
				"                \"num\": 150,\n" +
				"                \"itm_det\": {\n" +
				"                  \"rt\": 1.5,\n" +
				"                  \"txval\": 7865,\n" +
				"                  \"iamt\": 117.98\n" +
				"                }\n" +
				"              }\n" +
				"            ],\n" +
				"            \"oinum\": \"b2cl/test/1\",\n" +
				"            \"flag\": \"N\",\n" +
				"            \"idt\": \"10-06-2022\",\n" +
				"            \"inum\": \"b2cl/test/amd/1\",\n" +
				"            \"oidt\": \"17-06-2022\",\n" +
				"            \"pos\": \"24\",\n" +
				"            \"sply_ty\": \"INTER\",\n" +
				"            \"inv_typ\": \"R\",\n" +
				"            \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"          }\n" +
				"        ],\n" +
				"        \"rtin\": \"32KLJIP1179C1ZF\"\n" +
				"      }\n" +
				"    ],\n" +
				"    \"urp2ca\": [\n" +
				"      {\n" +
				"        \"itms\": [\n" +
				"          {\n" +
				"            \"csamt\": 0,\n" +
				"            \"samt\": 0,\n" +
				"            \"rt\": 1.5,\n" +
				"            \"txval\": 34353,\n" +
				"            \"camt\": 0,\n" +
				"            \"iamt\": 515.29\n" +
				"          }\n" +
				"        ],\n" +
				"        \"flag\": \"N\",\n" +
				"        \"pos\": \"07\",\n" +
				"        \"omon\": \"062022\",\n" +
				"        \"sply_ty\": \"INTER\",\n" +
				"        \"chksum\": \"6927009f6608b520bb94b0b6890512a91dcc74e5a8723c91280241287058bf66\"\n" +
				"      }\n" +
				"    ]\n" +
				"  }\n" +
				"}";

	        
	        JsonObject respObject = JsonParser.parseString(apiResp).getAsJsonObject();


	        // Create Gson instance and parse the JSON into an Ecom object
	        Gson gson = new Gson();
	        EcomAmd invoice = gson.fromJson(respObject.getAsJsonObject("ecoma"), EcomAmd.class);
		
		


			List<B2BEcomInvoices> b2bDetails = invoice.getB2ba();
			List<B2BEcomInvoices> urp2BDetails = invoice.getUrp2ba();
			List<B2CSInvoices> b2csInvoice = invoice.getB2ca();
			List<B2CSInvoices> urpB2CDetails = invoice.getUrp2ca();

			StringJoiner finalJoiner = new StringJoiner("\n");

			setTransData(joiner, b2bDetails,
					GSTConstants.GSTR1_15AIA.concat("- B2B"));

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");

			setTransData(joiner, urp2BDetails,
					GSTConstants.GSTR1_15AIB.concat("- URP2B"));

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");

			setTransData(joiner, urp2BDetails,
					GSTConstants.GSTR1_15AIIA.concat("- URP2B"));

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");

			setSummaryData(joiner, urpB2CDetails,
					GSTConstants.GSTR1_15AIIB.concat("- B2C"));

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");

			setSummaryData(joiner, b2csInvoice,
					GSTConstants.GSTR1_15IV.concat("- URP2C"));

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");
		
		System.out.println(finalJoiner.toString());

	
	}*/

	/*private void setTransData(StringJoiner joiner,
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
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(null));
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(b2bInvoices.getOrigInvNumber()));
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(b2bInvoices.getOrigInvDate()));
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

	/*private void setSummaryData(StringJoiner joiner,
			List<B2CSInvoices> b2csInvoice, String tableType) {
		for (B2CSInvoices b2csInv : b2csInvoice) {

			for (B2CSALineItem b2csLineItem : b2csInv.getLineItems()) {

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
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(b2csInv.getOrgMonthInv()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(b2csInv.getOstin()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(b2csInv.getPointOfSupply()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(null));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getRate()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getTaxableValue()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getIgstAmount()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getCgstAmount()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getSgstAmount()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.checkExponenForAmt(b2csLineItem.getCessAmount()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(b2csInv.getInvoiceStatus()));
			}

		}

	}*/
}
