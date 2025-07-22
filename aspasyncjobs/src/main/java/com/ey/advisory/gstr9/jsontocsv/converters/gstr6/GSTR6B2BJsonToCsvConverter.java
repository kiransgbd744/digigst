package com.ey.advisory.gstr9.jsontocsv.converters.gstr6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bDto;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6B2bInvoiceData;
import com.ey.advisory.app.docs.dto.gstr6.Gstr6Items;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.multitenancy.CommonContext;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GSTR6B2BJsonToCsvConverter")
public class GSTR6B2BJsonToCsvConverter implements JsonToCsvConverter {

	private static final String GSTR6_HDR = "Category,Counter Party Return Status,"
			+ "DelinkFlag,Return Period,Recipent GSTIN,Document Type,Document Number,"
			+ "Document Date,Original Document Number,Original Document Date,"
			+ "OriginalInvoiceNo,OriginalInvoiceDate,CRDRPreGST,LineNumber,"
			+ "Supplier GSTIN,POS,Taxable Value ,Tax Rate,Integrated Tax Amount,"
			+ "Central Tax Amount,StateUT TaxAmount,Cess Amount,Invoice Value,"
			+ "Reason For Credit Debit Note,Reverse Charge Flag,Differential Percentage\r\n";

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { GSTR6_HDR };
	}

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {
		Gson gson = GsonUtil.newSAPGsonInstance();

		BufferedWriter csvWriter = csvWriters[0];
		SaveGstr6 gstr6Dto = gson.fromJson(reader, SaveGstr6.class);

		writeDataToCSV(gstr6Dto, csvWriter);

	}

	private static void writeDataToCSV(SaveGstr6 gstr6Dto, BufferedWriter bw)
			throws IOException {
		LOGGER.debug("Entering Gstr6B2b writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = null;
		List<Gstr6B2bInvoiceData> invList = null;
		String cGstin = null;
		String cfs = null;

		if (gstr6Dto.getB2bInvoice() != null
				&& !gstr6Dto.getB2bInvoice().isEmpty()) {

			for (Gstr6B2bDto gstr6B2BGetReqDto : gstr6Dto.getB2bInvoice()) {

				invList = gstr6B2BGetReqDto.getInv();
				cGstin = gstr6B2BGetReqDto.getCgstin();
				cfs = gstr6B2BGetReqDto.getCfs();
				if (invList != null && !invList.isEmpty()) {
					for (Gstr6B2bInvoiceData data : invList) {

						List<Gstr6Items> itms = data.getItms();

						for (Gstr6Items itm : itms) {
							joiner = new StringJoiner(",");
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString("B2B"));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(cfs));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// dlinkFlag
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString("'"
											+ CommonContext.getTaxPeriod()));// retPeriod
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(CommonContext.getGstin()));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString("INV"));// docType
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(data.getInum()));//
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(data.getIdt()));

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// OriginalDocNo
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// OriginalDocDt
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// OriginalInvoiceNo
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// OriginalInvoiceDate
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// CRDRPreGST
							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(String.valueOf(itm.getNum())));
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(cGstin));// Suppliergstin

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(data.getPos()));// pos
							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(GenUtil
											.checkExponenForAmt(itm.getItmdet()
													.getTaxableValue())));

							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(GenUtil.checkExponenForAmt(
											itm.getItmdet().getRate())));

							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(GenUtil.checkExponenForAmt(
											itm.getItmdet().getIgstAmount())));

							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(GenUtil.checkExponenForAmt(
											itm.getItmdet().getCgstAmount())));

							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(GenUtil.checkExponenForAmt(
											itm.getItmdet().getSgstAmount())));

							GenUtil.appendStringToJoiner(joiner, GenUtil
									.toCsvString(GenUtil.checkExponenForAmt(
											itm.getItmdet().getCessAmount())));

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(
											GenUtil.checkExponenForAmt(
													data.getVal())));

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// RsnCrDr

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// RvChrgFlag

							GenUtil.appendStringToJoiner(joiner,
									GenUtil.toCsvString(""));// DifrPerce
							finalJoiner.add(joiner.toString());
						}

					}

				}
			}

		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting Gstr6B2b writeDataToCSV");

	}

}
