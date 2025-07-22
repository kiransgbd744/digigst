package com.ey.advisory.gstr9.jsontocsv.converters.gstr7;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7GetSummaryTaxPaidDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7TaxPayIgstSummaryDto;
import com.ey.advisory.app.docs.dto.anx1.Gstr7TaxPaySummaryDto;
import com.ey.advisory.app.util.GsonUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr7SummaryJsonToCsvConverter")
public class Gstr7SummaryJsonToCsvConverter implements JsonToCsvConverter {

	private static final String REPORT_HDR = "TDSDeductorGSTIN,ReturnPeriod,Head,"
			+ "Description,Liability Id - Payable,Transaction code - Payable,"
			+ "Transaction date - Payable,TaxPayable,InterestPayable,PenaltyPayable,"
			+ "FeePayable,OthersPayable,TotalPayable,Liability Id - Paid,"
			+ "Transaction code - Paid,Transaction date - Paid,TaxPaid,"
			+ "InterestPaid,PenaltyPaid,FeePaid,OthersPaid,TotalPaid\r\n";

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR7 Summary file.
		Gson gson = GsonUtil.newSAPGsonInstance();
		BufferedWriter csvWriter = csvWriters[0];
		LOGGER.debug("File Response {}", reader);
		Gstr7GetSummaryDto jsonSummaryData = gson.fromJson(reader,
				Gstr7GetSummaryDto.class);

		String gstin = jsonSummaryData.getGstin();
		String returnPeriod = jsonSummaryData.getFp();

		List<Gstr7TaxPaySummaryDto> taxpayList = jsonSummaryData.getTax_pay();
		Gstr7GetSummaryTaxPaidDto tax_paid = jsonSummaryData.getTax_paid();
		if (jsonSummaryData != null) {
			writeTxPyDataToCSV(gstin, returnPeriod, taxpayList, tax_paid,
					csvWriter);
		}

	}

	private static void writeTxPyDataToCSV(String gstin, String returnPeriod,
			List<Gstr7TaxPaySummaryDto> gstr7SummDto,
			Gstr7GetSummaryTaxPaidDto gstr7TxPaidDto, BufferedWriter bw)
			throws IOException {

		LOGGER.debug("Entering writeDataToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString("'" + returnPeriod));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("IGST"));

		String debit_id = "";
		if (gstr7TxPaidDto != null) {
			List<Gstr7TaxPaySummaryDto> txPyDto = gstr7TxPaidDto
					.getPd_by_cash();
			if (txPyDto != null && !txPyDto.isEmpty()) {

				debit_id = txPyDto.get(0).getDebit_id();
			}
		}
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(debit_id));

		if (gstr7SummDto != null && !gstr7SummDto.isEmpty()) {
			for (Gstr7TaxPaySummaryDto reqDto : gstr7SummDto) {
				Gstr7TaxPayIgstSummaryDto igstTxPy = reqDto.getIgst();
				if (igstTxPy != null) {
					txPayabletoCsv(joiner, reqDto, igstTxPy);
				} else {
					defaultTxPayabletoCsv(joiner);
				}
			}
		} else {
			defaultTxPayabletoCsv(joiner);
		}

		if (gstr7TxPaidDto != null) {
			List<Gstr7TaxPaySummaryDto> txPyDto = gstr7TxPaidDto
					.getPd_by_cash();
			if (txPyDto != null && !txPyDto.isEmpty()) {
				for (Gstr7TaxPaySummaryDto reqDto : txPyDto) {
					Gstr7TaxPayIgstSummaryDto igstTxPy = reqDto.getIgst();
					if (igstTxPy != null) {
						txPaidtoCsv(joiner, reqDto, igstTxPy);
					} else {
						defaultTxPaidtoCsv(joiner);
					}
				}
			} else {
				defaultTxPaidtoCsv(joiner);
			}
		} else {
			defaultTxPaidtoCsv(joiner);
		}
		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString("'" + returnPeriod));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("CGST"));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(debit_id));

		if (gstr7SummDto != null && !gstr7SummDto.isEmpty()) {
			for (Gstr7TaxPaySummaryDto reqDto : gstr7SummDto) {
				Gstr7TaxPayIgstSummaryDto cgstTxPy = reqDto.getCgst();
				if (cgstTxPy != null) {
					txPayabletoCsv(joiner, reqDto, cgstTxPy);
				} else {
					defaultTxPayabletoCsv(joiner);
				}
			}
		} else {
			defaultTxPayabletoCsv(joiner);
		}

		if (gstr7TxPaidDto != null) {
			List<Gstr7TaxPaySummaryDto> txPyDto = gstr7TxPaidDto
					.getPd_by_cash();
			if (txPyDto != null && !txPyDto.isEmpty()) {
				for (Gstr7TaxPaySummaryDto reqDto : txPyDto) {
					Gstr7TaxPayIgstSummaryDto cgstTxPy = reqDto.getCgst();
					if (cgstTxPy != null) {
						txPaidtoCsv(joiner, reqDto, cgstTxPy);
					} else {
						defaultTxPaidtoCsv(joiner);
					}
				}
			} else {
				defaultTxPaidtoCsv(joiner);
			}
		} else {
			defaultTxPaidtoCsv(joiner);
		}
		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString("'" + returnPeriod));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("SGST"));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(debit_id));

		if (gstr7SummDto != null && !gstr7SummDto.isEmpty()) {
			for (Gstr7TaxPaySummaryDto reqDto : gstr7SummDto) {
				Gstr7TaxPayIgstSummaryDto sgstTxPy = reqDto.getSgst();
				if (sgstTxPy != null) {
					txPayabletoCsv(joiner, reqDto, sgstTxPy);
				} else {
					defaultTxPayabletoCsv(joiner);
				}
			}
		} else {
			defaultTxPayabletoCsv(joiner);
		}

		if (gstr7TxPaidDto != null) {
			List<Gstr7TaxPaySummaryDto> txPyDto = gstr7TxPaidDto
					.getPd_by_cash();
			if (txPyDto != null && !txPyDto.isEmpty()) {
				for (Gstr7TaxPaySummaryDto reqDto : txPyDto) {
					Gstr7TaxPayIgstSummaryDto sgstTxPy = reqDto.getSgst();
					if (sgstTxPy != null) {
						txPaidtoCsv(joiner, reqDto, sgstTxPy);
					} else {
						defaultTxPaidtoCsv(joiner);
					}
				}
			} else {
				defaultTxPaidtoCsv(joiner);
			}
		} else {
			defaultTxPaidtoCsv(joiner);
		}
		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(gstin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString("'" + returnPeriod));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("CESS"));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(debit_id));

		if (gstr7SummDto != null && !gstr7SummDto.isEmpty()) {
			for (Gstr7TaxPaySummaryDto reqDto : gstr7SummDto) {
				Gstr7TaxPayIgstSummaryDto cessTxPy = reqDto.getCess();
				if (cessTxPy != null) {
					txPayabletoCsv(joiner, reqDto, cessTxPy);
				} else {
					defaultTxPayabletoCsv(joiner);
				}
			}
		} else {
			defaultTxPayabletoCsv(joiner);
		}

		if (gstr7TxPaidDto != null) {
			List<Gstr7TaxPaySummaryDto> txPyDto = gstr7TxPaidDto
					.getPd_by_cash();
			if (txPyDto != null && !txPyDto.isEmpty()) {
				for (Gstr7TaxPaySummaryDto reqDto : txPyDto) {
					Gstr7TaxPayIgstSummaryDto cessTxPy = reqDto.getCess();
					if (cessTxPy != null) {
						txPaidtoCsv(joiner, reqDto, cessTxPy);
					} else {
						defaultTxPaidtoCsv(joiner);
					}
				}
			} else {
				defaultTxPaidtoCsv(joiner);
			}
		} else {
			defaultTxPaidtoCsv(joiner);
		}
		finalJoiner.add(joiner.toString());

		
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		// BufferedWriter bw = new BufferedWriter();
		LOGGER.debug("Exiting writeDataToCSV");
	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { REPORT_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 1;
	}

	private static void txPayabletoCsv(StringJoiner joiner,
			Gstr7TaxPaySummaryDto reqDto, Gstr7TaxPayIgstSummaryDto igstTxPy) {
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(reqDto.getLiabId())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(reqDto.getTransCode())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(reqDto.getTransDate()));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getTx())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getIntr())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getPen())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getFee())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getOth())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getTot())));
	}

	private static void defaultTxPayabletoCsv(StringJoiner joiner) {
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

		defaultToZeroJoiner(joiner, 6);
	}

	private static void defaultTxPaidtoCsv(StringJoiner joiner) {
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));

		defaultToZeroJoiner(joiner, 6);
	}

	private static void txPaidtoCsv(StringJoiner joiner,
			Gstr7TaxPaySummaryDto reqDto, Gstr7TaxPayIgstSummaryDto igstTxPy) {

		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(reqDto.getLiabId())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(String.valueOf(reqDto.getTransCode())));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(reqDto.getTransDate()));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getTx())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getIntr())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getPen())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getFee())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getOth())));
		GenUtil.appendStringToJoiner(joiner, GenUtil
				.toCsvString(GenUtil.checkExponenForAmt(igstTxPy.getTot())));
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