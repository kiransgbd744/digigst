package com.ey.advisory.gstr9.jsontocsv.converters.gstr9;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.services.gstr9.GSTR9InwardConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9PyTransInCyConstants;
import com.ey.advisory.app.data.services.gstr9.Gstr9TaxPaidConstants;
import com.ey.advisory.app.docs.dto.gstr9.GetDetailsForGstr9ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table10ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table14ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table15ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table16ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ItemsReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table17ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table18ItemsReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table18ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table4ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table5ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6IogReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppNonRchrgReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppRchrgRegReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table6SuppRchrgUnRegReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table7OtherReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table7ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table8ReqDto;
import com.ey.advisory.app.docs.dto.gstr9.Gstr9Table9ReqDto;
import com.ey.advisory.app.services.common.Gstr9CommonUtility;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GSTR9Constants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr9JsonToCsvConverter")
public class Gstr9JsonToCsvConverter implements JsonToCsvConverter {

	private static final String OUTINWARDDTLS_HDR = "GSTIN,Table Number,Nature of Supply,"
			+ "Taxable Value,IGST,CGST,SGST,CESS,Interest,Latefee,Penalty,Other\r\n";

	private static final String TAX_PAID_HDR = "GSTIN,Table Number,Description,"
			+ "Tax Payable,Paid through cash,Paid through ITC IGST,Paid through"
			+ " ITC	CGST,Paid through ITC SGST,Paid through ITC	Cess\r\n";

	private static final String HSN_DETAILS_HDR = "GSTIN,FY,TableNumber,"
			+ "HSN,Description,RateofTax,UQC,TotalQuantity,TaxableValue,"
			+ "ConcessionalRateFlag,IGST,CGST,SGST,Cess\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR3b Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		BufferedWriter taxPaidWriter = csvWriters[1];
		BufferedWriter hsnWriter = csvWriters[2];

		GetDetailsForGstr9ReqDto gstr9GetReqDto = gson.fromJson(reader,
				GetDetailsForGstr9ReqDto.class);
		String ctin = gstr9GetReqDto.getGstin();
		Gstr9Table4ReqDto gstr9Tbl4reqDto = gstr9GetReqDto.getTable4ReqDto();
		Gstr9Table5ReqDto gstr9Tbl5reqDto = gstr9GetReqDto.getTable5ReqDto();
		Gstr9Table6ReqDto gstr9Tbl6reqDto = gstr9GetReqDto.getTable6ReqDto();
		Gstr9Table7ReqDto gstr9Tbl7reqDto = gstr9GetReqDto.getTable7ReqDto();
		Gstr9Table8ReqDto gstr9Tbl8reqDto = gstr9GetReqDto.getTable8ReqDto();
		Gstr9Table10ReqDto gstr9Tbl10reqDto = gstr9GetReqDto.getTable10ReqDto();
		Gstr9Table15ReqDto gstr9Tbl15reqDto = gstr9GetReqDto.getTable15ReqDto();
		Gstr9Table16ReqDto gstr9Tbl16reqDto = gstr9GetReqDto.getTable16ReqDto();

		String taxPeriod = gstr9GetReqDto.getFp();
		String fy = GenUtil.getFinancialYearByTaxperiod(taxPeriod);

		// Tax Paid Details
		Gstr9Table9ReqDto gstr9Tbl9reqDto = gstr9GetReqDto.getTable9ReqDto();
		Gstr9Table14ReqDto gstr9Tbl14reqDto = gstr9GetReqDto.getTable14ReqDto();

		// Hsn Details
		Gstr9Table17ReqDto gstr9Tbl17reqDto = gstr9GetReqDto.getTable17ReqDto();
		Gstr9Table18ReqDto gstr9Tbl18reqDto = gstr9GetReqDto.getTable18ReqDto();

		if (gstr9Tbl4reqDto != null) {
			writeTbl4DtlsToCSV(gstr9Tbl4reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl5reqDto != null) {
			writeTbl5DtlsToCSV(gstr9Tbl5reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl6reqDto != null) {
			writeTbl6DtlsToCSV(gstr9Tbl6reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl7reqDto != null) {
			writeTbl7DtlsToCSV(gstr9Tbl7reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl8reqDto != null) {
			writeTbl8DtlsToCSV(gstr9Tbl8reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl10reqDto != null) {
			writeTbl10DtlsToCSV(gstr9Tbl10reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl15reqDto != null) {
			writeTbl15DtlsToCSV(gstr9Tbl15reqDto, ctin, csvWriter);
		}
		if (gstr9Tbl16reqDto != null) {
			writeTbl16DtlsToCSV(gstr9Tbl16reqDto, ctin, csvWriter);
		}

		// Tax Paid Details
		if (gstr9Tbl9reqDto != null) {
			writeTbl9DtlsToCSV(gstr9Tbl9reqDto, ctin, taxPaidWriter);
		}
		if (gstr9Tbl14reqDto != null) {
			writeTbl14DtlsToCSV(gstr9Tbl14reqDto, ctin, taxPaidWriter);
		}

		// Hsn Details

		if (gstr9Tbl17reqDto != null) {
			writeTbl17DtlsToCSV(gstr9Tbl17reqDto, ctin, hsnWriter, fy);
		}
		if (gstr9Tbl18reqDto != null) {
			writeTbl18DtlsToCSV(gstr9Tbl18reqDto, ctin, hsnWriter, fy);
		}

	}

	private static void writeTbl4DtlsToCSV(Gstr9Table4ReqDto tbl4ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl4DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4A));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4A));

		if (tbl4ReqDto.getTable4B2CReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2CReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2CReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2CReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2CReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2CReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4B));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4B));

		if (tbl4ReqDto.getTable4B2BReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2BReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2BReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2BReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2BReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4B2BReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4C));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4C));

		if (tbl4ReqDto.getTable4ExpReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4ExpReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4ExpReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4ExpReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4D));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4D));

		if (tbl4ReqDto.getTable4SezReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SezReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SezReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SezReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4E));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4E));

		if (tbl4ReqDto.getTable4DeemedReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4DeemedReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4DeemedReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4DeemedReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4DeemedReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4DeemedReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4F
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4F));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4F));

		if (tbl4ReqDto.getTable4AtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AtReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AtReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AtReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AtReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AtReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4G));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4G));

		if (tbl4ReqDto.getTable4RchrgReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4RchrgReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4RchrgReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4RchrgReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4RchrgReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4RchrgReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4H
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4H));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4H));

		if (tbl4ReqDto.getTable4SubTotalAGReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl4ReqDto
							.getTable4SubTotalAGReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalAGReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalAGReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalAGReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl4ReqDto
							.getTable4SubTotalAGReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4I
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4I));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4I));

		if (tbl4ReqDto.getTable4CrntReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4CrntReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4CrntReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4CrntReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4CrntReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4CrntReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4J
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4J));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4J));

		if (tbl4ReqDto.getTable4drntReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4drntReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4drntReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4drntReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4drntReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4drntReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4K
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4K));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4K));

		if (tbl4ReqDto.getTable4AmdPosReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdPosReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdPosReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdPosReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdPosReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdPosReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4L
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4L));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4L));

		if (tbl4ReqDto.getTable4AmdNegReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdNegReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdNegReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdNegReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdNegReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4AmdNegReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4M
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4M));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4M));

		if (tbl4ReqDto.getTable4SubTotalILReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl4ReqDto
							.getTable4SubTotalILReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalILReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalILReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubTotalILReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl4ReqDto
							.getTable4SubTotalILReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 4N
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_4N));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_4N));

		if (tbl4ReqDto.getTable4SubAdvReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubAdvReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubAdvReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubAdvReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubAdvReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl4ReqDto.getTable4SubAdvReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl4DtlsToCSV");
	}

	private static void writeTbl5DtlsToCSV(Gstr9Table5ReqDto tbl5ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl5DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5A));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5A));

		if (tbl5ReqDto.getTable5ZeroRtdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5ZeroRtdReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5B));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5B));

		if (tbl5ReqDto.getTable5SezReqDto() != null) {

			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5SezReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5C));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5C));

		if (tbl5ReqDto.getTable5RchrgReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5RchrgReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5D));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5D));

		if (tbl5ReqDto.getTable5ExmtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5ExmtReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5E));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5E));
		if (tbl5ReqDto.getTable5NilReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5NilReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5F
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5F));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5F));

		if (tbl5ReqDto.getTable5NonGstReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5NonGstReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5G));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5G));

		if (tbl5ReqDto.getTable5SubTotalAfReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl5ReqDto
							.getTable5SubTotalAfReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5H
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5H));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5H));

		if (tbl5ReqDto.getTable5CrNtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5CrNtReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5I
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5I));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5I));

		if (tbl5ReqDto.getTable5DbNtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5DbNtReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5J
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5J));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5J));

		if (tbl5ReqDto.getTable5AmdPosReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5AmdPosReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5K
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5K));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5K));

		if (tbl5ReqDto.getTable5AmdNegReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5AmdNegReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5L
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5L));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5L));

		if (tbl5ReqDto.getTable5SubTotalHkReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl5ReqDto
							.getTable5SubTotalHkReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5M
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5M));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5M));

		if (tbl5ReqDto.getTable5ToverTaxNpReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl5ReqDto
							.getTable5ToverTaxNpReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 5N
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_5N));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_5N));

		if (tbl5ReqDto.getTable5TotalToverReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl5ReqDto
							.getTable5TotalToverReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5TotalToverReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5TotalToverReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl5ReqDto.getTable5TotalToverReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl5ReqDto
							.getTable5TotalToverReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl5DtlsToCSV");
	}

	private static void writeTbl6DtlsToCSV(Gstr9Table6ReqDto tbl6ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl6DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6A));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6A));

		if (tbl6ReqDto.getTable6Itc3bReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Itc3bReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Itc3bReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Itc3bReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Itc3bReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());

		// 6B
		if (tbl6ReqDto.getTable6SuppNonRchrgReqDto() != null
				&& !tbl6ReqDto.getTable6SuppNonRchrgReqDto().isEmpty()) {

			for (Gstr9Table6SuppNonRchrgReqDto suppNon : tbl6ReqDto
					.getTable6SuppNonRchrgReqDto()) {
				String itcType = suppNon.getItctyp();
				if ("ip".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6B1);
				}
				if ("cg".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6B2);
				}
				if ("is".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6B3);
				}
			}

			List<Gstr9Table6SuppNonRchrgReqDto> sortedUsers = tbl6ReqDto
					.getTable6SuppNonRchrgReqDto().stream()
					.sorted(Comparator.comparing(
							Gstr9Table6SuppNonRchrgReqDto::getItctyp))
					.collect(Collectors.toList());

			for (Gstr9Table6SuppNonRchrgReqDto suppNon : sortedUsers) {
				String itcType = suppNon.getItctyp();
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if (GSTR9InwardConstants.Table_6B1.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6B1));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6B1));

				} else if (GSTR9InwardConstants.Table_6B2
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6B2));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6B2));

				} else if (GSTR9InwardConstants.Table_6B3
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6B3));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6B3));
				}

				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCsamt())));
				defaultToZeroJoiner(joiner, 4);
				finalJoiner.add(joiner.toString());
			}
		}

		// 6C

		if (tbl6ReqDto.getTable6SuppRchrgUnRegReqDto() != null
				&& !tbl6ReqDto.getTable6SuppRchrgUnRegReqDto().isEmpty()) {

			for (Gstr9Table6SuppRchrgUnRegReqDto suppNon : tbl6ReqDto
					.getTable6SuppRchrgUnRegReqDto()) {
				String itcType = suppNon.getItctyp();
				if ("ip".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6C1);
				}
				if ("cg".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6C2);
				}
				if ("is".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6C3);
				}
			}
			List<Gstr9Table6SuppRchrgUnRegReqDto> sortedDtos = tbl6ReqDto
					.getTable6SuppRchrgUnRegReqDto().stream()
					.sorted(Comparator.comparing(
							Gstr9Table6SuppRchrgUnRegReqDto::getItctyp))
					.collect(Collectors.toList());

			for (Gstr9Table6SuppRchrgUnRegReqDto suppNon : sortedDtos) {
				String itcType = suppNon.getItctyp();
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if (GSTR9InwardConstants.Table_6C1.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6C1));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6C1));

				} else if (GSTR9InwardConstants.Table_6C2
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6C2));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6C2));

				} else if (GSTR9InwardConstants.Table_6C3
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6C3));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6C3));
				}

				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCsamt())));
				defaultToZeroJoiner(joiner, 4);
				finalJoiner.add(joiner.toString());

			}
		}

		// 6D
		if (tbl6ReqDto.getTable6SuppRchrgRegReqDto() != null
				&& !tbl6ReqDto.getTable6SuppRchrgRegReqDto().isEmpty()) {

			for (Gstr9Table6SuppRchrgRegReqDto suppNon : tbl6ReqDto
					.getTable6SuppRchrgRegReqDto()) {
				String itcType = suppNon.getItctyp();
				if ("ip".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6D1);
				}
				if ("cg".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6D2);
				}
				if ("is".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6D3);
				}
			}
			List<Gstr9Table6SuppRchrgRegReqDto> sortedDtos = tbl6ReqDto
					.getTable6SuppRchrgRegReqDto().stream()
					.sorted(Comparator.comparing(
							Gstr9Table6SuppRchrgRegReqDto::getItctyp))
					.collect(Collectors.toList());

			for (Gstr9Table6SuppRchrgRegReqDto suppNon : sortedDtos) {
				String itcType = suppNon.getItctyp();
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if (GSTR9InwardConstants.Table_6D1.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6D1));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6D1));

				} else if (GSTR9InwardConstants.Table_6D2
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6D2));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6D2));

				} else if (GSTR9InwardConstants.Table_6D3
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString(GSTR9InwardConstants.Table_6D3));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6D3));
				}

				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCsamt())));
				defaultToZeroJoiner(joiner, 4);
				finalJoiner.add(joiner.toString());

			}
		}

		// 6E
		if (tbl6ReqDto.getTable6IogReqDto() != null
				&& !tbl6ReqDto.getTable6IogReqDto().isEmpty()) {

			for (Gstr9Table6IogReqDto suppNon : tbl6ReqDto
					.getTable6IogReqDto()) {
				String itcType = suppNon.getItctyp();
				if ("ip".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6E1);
				}
				if ("cg".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6E2);
				}
				if ("is".equalsIgnoreCase(itcType)) {
					suppNon.setItctyp(GSTR9InwardConstants.Table_6E3);
				}
			}
			List<Gstr9Table6IogReqDto> sortedDtos = tbl6ReqDto
					.getTable6IogReqDto().stream()
					.sorted(Comparator
							.comparing(Gstr9Table6IogReqDto::getItctyp))
					.collect(Collectors.toList());

			for (Gstr9Table6IogReqDto suppNon : sortedDtos) {
				String itcType = suppNon.getItctyp();
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if (GSTR9InwardConstants.Table_6E1.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString("'" + GSTR9InwardConstants.Table_6E1));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6E1));
				} else if (GSTR9InwardConstants.Table_6E2
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString("'" + GSTR9InwardConstants.Table_6E2));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6E2));
				} else if (GSTR9InwardConstants.Table_6E3
						.equalsIgnoreCase(itcType)) {
					GenUtil.appendStringToJoiner(joiner, GenUtil
							.toCsvString("'" + GSTR9InwardConstants.Table_6E3));
					GenUtil.appendStringToJoiner(joiner,
							Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
									GSTR9InwardConstants.Table_6E3));
				}
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getIamt())));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCsamt())));
				defaultToZeroJoiner(joiner, 4);
				finalJoiner.add(joiner.toString());

			}
		}

		// 6F
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6F));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6F));

		if (tbl6ReqDto.getTable6IosReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IosReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IosReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6G
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6G));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6G));

		if (tbl6ReqDto.getTable6IsdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IsdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IsdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IsdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6IosReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6H
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6H));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6H));

		if (tbl6ReqDto.getTable6ItcClmdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6ItcClmdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6ItcClmdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6ItcClmdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6ItcClmdReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6I
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6I));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6I));

		if (tbl6ReqDto.getTable6SubTotalBhReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalBhReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalBhReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalBhReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl6ReqDto
							.getTable6SubTotalBhReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6J
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6J));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6J));

		if (tbl6ReqDto.getTable6DifferenceReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6DifferenceReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6DifferenceReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6DifferenceReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl6ReqDto
							.getTable6DifferenceReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6K
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6K));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6K));

		if (tbl6ReqDto.getTable6Trans1ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Trans1ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Trans1ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6L
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6L));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6L));

		if (tbl6ReqDto.getTable6Trans2ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Trans2ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6Trans2ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6M
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6M));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6M));

		if (tbl6ReqDto.getTable6OtherReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6OtherReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6OtherReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6OtherReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6OtherReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6N
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6N));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6N));

		if (tbl6ReqDto.getTable6SubTotalKmReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalKmReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalKmReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTable6SubTotalKmReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl6ReqDto
							.getTable6SubTotalKmReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 6O
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_6O));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_6O));

		if (tbl6ReqDto.getTotalItcAvailedReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTotalItcAvailedReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTotalItcAvailedReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTotalItcAvailedReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl6ReqDto.getTotalItcAvailedReqDto().getCsamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			defaultToZeroJoiner(joiner, 3);

		}

		finalJoiner.add(joiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl6DtlsToCSV");
	}

	private static void writeTbl7DtlsToCSV(Gstr9Table7ReqDto tbl7ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl7DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7A));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7A));

		if (tbl7ReqDto.getGstr9Table7Rule37ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule37ReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule37ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule37ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule37ReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7B));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7B));

		if (tbl7ReqDto.getGstr9Table7Rule39ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule39ReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule39ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule39ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule39ReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7C));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7C));

		if (tbl7ReqDto.getGstr9Table7Rule42ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule42ReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule42ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule42ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule42ReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7D));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7D));

		if (tbl7ReqDto.getGstr9Table7Rule43ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule43ReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule43ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule43ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Rule43ReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7E));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7E));

		if (tbl7ReqDto.getGstr9Table7Sec17ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl7ReqDto.getGstr9Table7Sec17ReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl7ReqDto.getGstr9Table7Sec17ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl7ReqDto.getGstr9Table7Sec17ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7Sec17ReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7F
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7F));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7F));

		if (tbl7ReqDto.getGstr9Table7RevsTrans1ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7RevsTrans1ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7RevsTrans1ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7G));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7G));

		if (tbl7ReqDto.getGstr9Table7RevsTrans2ReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7RevsTrans2ReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7RevsTrans2ReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());

		if (tbl7ReqDto.getGstr9Table7OtherReqDto() != null
				&& !tbl7ReqDto.getGstr9Table7OtherReqDto().isEmpty()) {

			for (Gstr9Table7OtherReqDto suppNon : tbl7ReqDto
					.getGstr9Table7OtherReqDto()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(GSTR9InwardConstants.Table_7H));
				GenUtil.appendStringToJoiner(joiner,
						Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
								GSTR9InwardConstants.Table_7H));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(suppNon.getCsamt())));
				defaultToZeroJoiner(joiner, 4);

				finalJoiner.add(joiner.toString());

			}
		}

		// 7I
		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7I));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7I));

		if (tbl7ReqDto.getGstr9Table7TotItcRevdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7TotItcRevdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7TotItcRevdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7TotItcRevdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7TotItcRevdReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 7J
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_7J));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_7J));

		if (tbl7ReqDto.getGstr9Table7NetItcAvalReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7NetItcAvalReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7NetItcAvalReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7NetItcAvalReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl7ReqDto
							.getGstr9Table7NetItcAvalReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl7DtlsToCSV");
	}

	private static void writeTbl8DtlsToCSV(Gstr9Table8ReqDto tbl8ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl8DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8A));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8A));

		if (tbl8ReqDto.getGstr9Table8Itc2AReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl8ReqDto.getGstr9Table8Itc2AReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl8ReqDto.getGstr9Table8Itc2AReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl8ReqDto.getGstr9Table8Itc2AReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8Itc2AReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8B));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8B));

		if (tbl8ReqDto.getGstr9Table8ItcTotReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcTotReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcTotReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcTotReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcTotReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8C));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8C));

		if (tbl8ReqDto.getGstr9Table8ItcInwdSuppReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcInwdSuppReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcInwdSuppReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcInwdSuppReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcInwdSuppReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8D));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8D));

		if (tbl8ReqDto.getGstr9Table8DifferenceABCReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceABCReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceABCReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceABCReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceABCReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8E));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8E));

		if (tbl8ReqDto.getGstr9Table8ItcNtAvaildReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtAvaildReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtAvaildReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtAvaildReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtAvaildReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8f
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8F));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8F));

		if (tbl8ReqDto.getGstr9Table8ItcNtElegReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtElegReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtElegReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtElegReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8ItcNtElegReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8G));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8G));

		if (tbl8ReqDto.getGstr9Table8IogTaxPaidReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogTaxPaidReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogTaxPaidReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogTaxPaidReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogTaxPaidReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8H
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8H));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8H));

		if (tbl8ReqDto.getGstr9Table8IogItcAvaildReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcAvaildReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcAvaildReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcAvaildReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcAvaildReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8I
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8I));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8I));

		if (tbl8ReqDto.getGstr9Table8DifferenceGhReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceGhReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceGhReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceGhReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8DifferenceGhReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8J
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8J));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8J));

		if (tbl8ReqDto.getGstr9Table8IogItcNtAvaildReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcNtAvaildReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcNtAvaildReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcNtAvaildReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8IogItcNtAvaildReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 8K
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9InwardConstants.Table_8K));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						GSTR9InwardConstants.Table_8K));

		if (tbl8ReqDto.getGstr9Table8TotItcLapsedReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8TotItcLapsedReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8TotItcLapsedReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8TotItcLapsedReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl8ReqDto
							.getGstr9Table8TotItcLapsedReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());

		// joiner = new StringJoiner(",");
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Entering writeTbl9DtlsToCSV");
	}

	private static void writeTbl10DtlsToCSV(Gstr9Table10ReqDto tbl10ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl10DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9PyTransInCyConstants.Table_10));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9PyTransInCyConstants.Table_10));

		if (tbl10ReqDto.getDbnAmdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getDbnAmdReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getDbnAmdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getDbnAmdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getDbnAmdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getDbnAmdReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9PyTransInCyConstants.Table_11));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9PyTransInCyConstants.Table_11));

		if (tbl10ReqDto.getCdnAmdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getCdnAmdReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getCdnAmdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getCdnAmdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getCdnAmdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl10ReqDto.getCdnAmdReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9PyTransInCyConstants.Table_12));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9PyTransInCyConstants.Table_12));

		if (tbl10ReqDto.getGstr9Table10ItcRvslReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt((tbl10ReqDto
							.getGstr9Table10ItcRvslReqDto().getTxval()))));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcRvslReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcRvslReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcRvslReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcRvslReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9PyTransInCyConstants.Table_13));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9PyTransInCyConstants.Table_13));

		if (tbl10ReqDto.getGstr9Table10ItcAvaildReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcAvaildReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcAvaildReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcAvaildReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcAvaildReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl10ReqDto
							.getGstr9Table10ItcAvaildReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl10DtlsToCSV");
	}

	private static void writeTbl15DtlsToCSV(Gstr9Table15ReqDto tbl15ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl15DtlsToCSV");

		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15A));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15A));

		if (tbl15ReqDto.getGstr9Table15RfdClmdReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdClmdReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdClmdReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdClmdReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdClmdReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15B));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15B));

		if (tbl15ReqDto.getGstr9Table15RfdSancReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdSancReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdSancReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdSancReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdSancReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15C));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15C));

		if (tbl15ReqDto.getGstr9Table15RfdRejtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdRejtReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdRejtReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdRejtReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdRejtReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15D));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15D));

		if (tbl15ReqDto.getGstr9Table15RfdPendReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdPendReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdPendReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdPendReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15RfdPendReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15D));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15D));

		if (tbl15ReqDto.getGstr9Table15TaxDmndtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getCsamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getIntr())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getFee())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxDmndtReqDto().getPen())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15F
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15F));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15F));

		if (tbl15ReqDto.getGstr9Table15TaxPaidReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getCsamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getIntr())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getFee())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TaxPaidReqDto().getPen())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 15G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_15G));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_15G));

		if (tbl15ReqDto.getGstr9Table15TotalDmndPendReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getCsamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getIntr())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getFee())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl15ReqDto
							.getGstr9Table15TotalDmndPendReqDto().getPen())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}

		finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl15DtlsToCSV");
	}

	private static void writeTbl16DtlsToCSV(Gstr9Table16ReqDto tbl16ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl16DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_16A));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_16A));

		if (tbl16ReqDto.getGstr9Table16CompSuppReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16CompSuppReqDto().getTxval())));
			defaultToZeroJoiner(joiner, 8);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 16B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_16B));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_16B));

		if (tbl16ReqDto.getGstr9Table16DeemedSuppReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16DeemedSuppReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16DeemedSuppReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16DeemedSuppReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16DeemedSuppReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16DeemedSuppReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 16C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_16C));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_16C));

		if (tbl16ReqDto.getGstr9Table16NotReturnedReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16NotReturnedReqDto().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16NotReturnedReqDto().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16NotReturnedReqDto().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16NotReturnedReqDto().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl16ReqDto
							.getGstr9Table16NotReturnedReqDto().getCsamt())));
			defaultToZeroJoiner(joiner, 4);
		}

		finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Entering writeTbl16DtlsToCSV");
	}

	private static void writeTbl9DtlsToCSV(Gstr9Table9ReqDto tbl9ReqDto,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.debug("Entering writeTbl19DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9A));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9A));

		if (tbl9ReqDto.getGstr9Table9IamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IamtReqDto().getTxpaidCash())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IamtReqDto().getTaxPaidItcIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IamtReqDto().getTaxPaidItcSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IamtReqDto().getTaxPaidItcCamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9B));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9B));

		if (tbl9ReqDto.getGstr9Table9CamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CamtReqDto().getTxpaidCash())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CamtReqDto().getTaxPaidItcIamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CamtReqDto().getTaxPaidItcCamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9C));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9C));

		if (tbl9ReqDto.getGstr9Table9SamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9SamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9SamtReqDto().getTxpaidCash())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9SamtReqDto().getTaxPaidItcIamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9SamtReqDto().getTaxPaidItcSamt())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());

		}
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9D));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9D));

		if (tbl9ReqDto.getGstr9Table9CsamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CsamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9CsamtReqDto().getTxpaidCash())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							tbl9ReqDto.getGstr9Table9CsamtReqDto()
									.getTaxPaidItcCsamt())));

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9E));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9E));

		if (tbl9ReqDto.getGstr9Table9IntrReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IntrReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9IntrReqDto().getTxpaidCash())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9F
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9F));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9F));

		if (tbl9ReqDto.getGstr9Table9TeeReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9TeeReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9TeeReqDto().getTxpaidCash())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9G
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9G));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9G));

		if (tbl9ReqDto.getGstr9Table9PenReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9PenReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9PenReqDto().getTxpaidCash())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 9H
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr9TaxPaidConstants.Table_9H));
		GenUtil.appendStringToJoiner(joiner,
				Gstr9CommonUtility.getNatureOfSuppliesForSubSection(
						Gstr9TaxPaidConstants.Table_9H));

		if (tbl9ReqDto.getGstr9Table9OtherReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9OtherReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table9OtherReqDto().getTxpaidCash())));
			defaultToZeroJoiner(joiner, 4);

		}

		// finalJoiner.add(joiner.toString());
		// joiner = new StringJoiner(",");

		finalJoiner.add(joiner.toString());
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl19DtlsToCSV");
	}

	private static void writeTbl14DtlsToCSV(Gstr9Table14ReqDto tbl9ReqDto,
			String ctin, BufferedWriter bw) throws IOException {

		LOGGER.debug("Entering writeTbl14DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_14A));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_14A));

		if (tbl9ReqDto.getGstr9Table14IamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14IamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14IamtReqDto().getTxpaid())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 14B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_14B));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_14B));

		if (tbl9ReqDto.getGstr9Table14CamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14CamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14CamtReqDto().getTxpaid())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 14C
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_14C));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_14C));

		if (tbl9ReqDto.getGstr9Table14SamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14SamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14SamtReqDto().getTxpaid())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 14D
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_14D));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_14D));

		if (tbl9ReqDto.getGstr9Table14CSamtReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14CSamtReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14CSamtReqDto().getTxpaid())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 14E
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(GSTR9Constants.Table_14E));
		GenUtil.appendStringToJoiner(joiner, Gstr9CommonUtility
				.getNatureOfSuppliesForSubSection(GSTR9Constants.Table_14E));

		if (tbl9ReqDto.getGstr9Table14IntrReqDto() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14IntrReqDto().getTxpyble())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(tbl9ReqDto
							.getGstr9Table14IntrReqDto().getTxpaid())));
			defaultToZeroJoiner(joiner, 4);

		}

		finalJoiner.add(joiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl14DtlsToCSV");
	}

	private static void writeTbl17DtlsToCSV(Gstr9Table17ReqDto tbl17ReqDto,
			String ctin, BufferedWriter bw, String fy) throws IOException {
		LOGGER.debug("Entering writeTbl17DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		// 17
		if (tbl17ReqDto.getGstr9Table17ItemsReqDtos() != null
				&& !tbl17ReqDto.getGstr9Table17ItemsReqDtos().isEmpty()) {

			for (Gstr9Table17ItemsReqDto itemDtos : tbl17ReqDto
					.getGstr9Table17ItemsReqDtos()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("17"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getHsnSc()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getDesc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getRt())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getQty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getTxval())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getIsconcesstional()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getCsamt())));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl17DtlsToCSV");
	}

	private static void writeTbl18DtlsToCSV(Gstr9Table18ReqDto tbl18ReqDto,
			String ctin, BufferedWriter bw, String fy) throws IOException {
		LOGGER.debug("Entering writeTbl18DtlsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		// 17
		if (tbl18ReqDto.getGstr9Table18ItemsReqDtos() != null
				&& !tbl18ReqDto.getGstr9Table18ItemsReqDtos().isEmpty()) {
			for (Gstr9Table18ItemsReqDto itemDtos : tbl18ReqDto
					.getGstr9Table18ItemsReqDtos()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(fy));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("18"));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getHsnSc()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getDesc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getRt())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getUqc()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getQty())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getTxval())));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(itemDtos.getIsconcesstional()));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(itemDtos.getCsamt())));
				finalJoiner.add(joiner.toString());

			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug("Exiting writeTbl18DtlsToCSV");
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { OUTINWARDDTLS_HDR, TAX_PAID_HDR,
				HSN_DETAILS_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 3;
	}

	private static StringJoiner defaultToZeroJoiner(StringJoiner joiner,
			int noOfColumns) {

		for (int i = 0; i < noOfColumns; i++) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		return joiner;
	}

}