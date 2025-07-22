package com.ey.advisory.gstr9.jsontocsv.converters.gstr3B;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.StringJoiner;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BMonthlyTrendTaxAmountRepository;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BEcoDtlsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BElgItcDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BIntLateFeeDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BInterStateSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BInwSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BOutInwSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSecDetailsDTO;
import com.ey.advisory.app.gstr3b.Gstr3bMonthlyTrendTaxAmountsEntity;
import com.ey.advisory.app.gstr3b.dto.TaxPaymentDetailsInvoice;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.Gstr3BConstants;
import com.ey.advisory.core.async.JobStatusConstants;
import com.ey.advisory.gstr9.jsontocsv.converters.JsonToCsvConverter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("Gstr3BJsonToCsvConverter")
public class Gstr3BJsonToCsvConverter implements JsonToCsvConverter {

	@Autowired
	Gstr3BMonthlyTrendTaxAmountRepository gstr3bMonthlyTaxRepo;

	private static final String CLASS_NAME = Gstr3BJsonToCsvConverter.class
			.getName();

	private static final String GSTR_3B_HDR = "GSTIN,Table Type,Table Heading,"
			+ "Table Description,POS,Taxable Value,IGST Amount, SGST Amount, "
			+ "CGST Amount,Cess Amount\r\n";

	private static final String TAX_PAYMENT_HDR = "GSTIN,Description,Tax Payable,"
			+ "Paid through ITC - IGST,Paid through ITC - CGST,Paid through ITC - SGST,"
			+ "Paid through ITC - Cess,Paid In Cash, Interest, Late Fee\r\n";

	private static boolean table3_2Flag = true;

	@Override
	public void convertJsonTOCsv(JsonReader reader,
			BufferedWriter... csvWriters) throws IOException {

		// The first writer will create the GSTR3b Summary file.
		BufferedWriter csvWriter = csvWriters[0];
		BufferedWriter taxPaymentWriter = csvWriters[1];
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		
		Gstr3BSavetoGstnDTO dto = gson.fromJson(reader,
				Gstr3BSavetoGstnDTO.class);

		if (dto != null) {

			String ctin = dto.getGstin();
			String taxPeriod = dto.getRetPeriod();

			if (dto.getSupDetails() != null) {
				writeSupDetailsToCSV(dto.getSupDetails(), ctin, csvWriter);
			}
			if (dto.getEcoDtls() != null) {
				writeEcoDetailsToCSV(dto.getEcoDtls(), ctin, csvWriter);
			} else {
				writeEcoDetailsToCSV(new Gstr3BEcoDtlsDTO(), ctin, csvWriter);
			}
			if (dto.getInterSup() != null) {
				writeInterSupDetailsToCSV(dto.getInterSup(), ctin, csvWriter);
			}
			if (dto.getItcElg() != null) {
				writeItcEligibleDetailsToCSV(dto.getItcElg(), ctin, csvWriter);
			}
			if (dto.getInwardSup() != null) {
				writeInwardSupDetailsToCSV(dto.getInwardSup(), ctin, csvWriter);
			}
			if (dto.getIntrLtfee() != null) {
				writeInterestLateFeeDetailsToCSV(dto.getIntrLtfee(), ctin,
						csvWriter);
			}
			Optional<Gstr3bMonthlyTrendTaxAmountsEntity> gstr3BEntityList = gstr3bMonthlyTaxRepo
					.findBySuppGstinAndTaxPeriodAndIsActiveTrue(ctin, taxPeriod);
			Gstr3bMonthlyTrendTaxAmountsEntity gstr3BEntity = new Gstr3bMonthlyTrendTaxAmountsEntity();

			if (gstr3BEntityList.isPresent())
				gstr3BEntity = gstr3BEntityList.get();

			if (dto.getTxPmt() != null || dto.getSupDetails() != null
					|| dto.getEcoDtls() != null) {
				writeTaxPaymentDetailsToCSV(dto.getTxPmt(), dto.getSupDetails(),
						dto.getEcoDtls(), ctin, taxPaymentWriter, gstr3BEntity);
				gstr3bMonthlyTaxRepo.save(gstr3BEntity);
			}
		}
	}
		private static void writeTaxPaymentDetailsToCSV(
			TaxPaymentDetailsInvoice taxPaymentDetailsInvoice,
			Gstr3BOutInwSuppDTO supDetails, Gstr3BEcoDtlsDTO ecoDetails,
			String ctin, BufferedWriter bw,
			Gstr3bMonthlyTrendTaxAmountsEntity gstr3BEntity)
			throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeTaxPaymentDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner IGSTJoiner = new StringJoiner(",");
		StringJoiner CGSTJoiner = new StringJoiner(",");
		StringJoiner SGSTJoiner = new StringJoiner(",");
		StringJoiner CessJoiner = new StringJoiner(",");

		GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(ctin));

		GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString("IGST"));
		GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString("CGST"));
		GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString("SGST"));
		GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString("CESS"));

		// US - 67274 - (sup_details)osup_det + (sup_details)osup_zero +
		// (sup_details)isup_rev + eco_sup (eco_dtls)

		BigDecimal igstAmountTaxPayable = BigDecimal.ZERO;
		BigDecimal cgstAmountTaxPayable = BigDecimal.ZERO;
		BigDecimal sgstAmountTaxPayable = BigDecimal.ZERO;
		BigDecimal cessAmountTaxPayable = BigDecimal.ZERO;
		BigDecimal totalTaxPayable = BigDecimal.ZERO;

		if (taxPaymentDetailsInvoice.getTaxPayable() != null) {

			if (supDetails.getOsupDet() != null) {
				igstAmountTaxPayable = igstAmountTaxPayable
						.add(supDetails.getOsupDet().getIamt() != null
								? supDetails.getOsupDet().getIamt()
								: BigDecimal.ZERO);
				cgstAmountTaxPayable = cgstAmountTaxPayable
						.add(supDetails.getOsupDet().getCamt() != null
								? supDetails.getOsupDet().getCamt()
								: BigDecimal.ZERO);
				sgstAmountTaxPayable = sgstAmountTaxPayable
						.add(supDetails.getOsupDet().getSamt() != null
								? supDetails.getOsupDet().getSamt()
								: BigDecimal.ZERO);
				cessAmountTaxPayable = cessAmountTaxPayable
						.add(supDetails.getOsupDet().getCsamt() != null
								? supDetails.getOsupDet().getCsamt()
								: BigDecimal.ZERO);

			}
			if (supDetails.getOsupZero() != null) {
				igstAmountTaxPayable = igstAmountTaxPayable
						.add(supDetails.getOsupZero().getIamt() != null
								? supDetails.getOsupZero().getIamt()
								: BigDecimal.ZERO);
				cgstAmountTaxPayable = cgstAmountTaxPayable
						.add(supDetails.getOsupZero().getCamt() != null
								? supDetails.getOsupZero().getCamt()
								: BigDecimal.ZERO);
				sgstAmountTaxPayable = sgstAmountTaxPayable
						.add(supDetails.getOsupZero().getSamt() != null
								? supDetails.getOsupZero().getSamt()
								: BigDecimal.ZERO);
				cessAmountTaxPayable = cessAmountTaxPayable
						.add(supDetails.getOsupZero().getCsamt() != null
								? supDetails.getOsupZero().getCsamt()
								: BigDecimal.ZERO);
			}

			if (supDetails.getIsupRev() != null) {
				igstAmountTaxPayable = igstAmountTaxPayable
						.add(supDetails.getIsupRev().getIamt() != null
								? supDetails.getIsupRev().getIamt()
								: BigDecimal.ZERO);
				cgstAmountTaxPayable = cgstAmountTaxPayable
						.add(supDetails.getIsupRev().getCamt() != null
								? supDetails.getIsupRev().getCamt()
								: BigDecimal.ZERO);
				sgstAmountTaxPayable = sgstAmountTaxPayable
						.add(supDetails.getIsupRev().getSamt() != null
								? supDetails.getIsupRev().getSamt()
								: BigDecimal.ZERO);
				cessAmountTaxPayable = cessAmountTaxPayable
						.add(supDetails.getIsupRev().getCsamt() != null
								? supDetails.getIsupRev().getCsamt()
								: BigDecimal.ZERO);
			}
		}
		if (ecoDetails != null) {
			if (ecoDetails.getEcoSup() != null) {
				igstAmountTaxPayable = igstAmountTaxPayable
						.add(ecoDetails.getEcoSup().getIamt() != null
								? ecoDetails.getEcoSup().getIamt()
								: BigDecimal.ZERO);
				cgstAmountTaxPayable = cgstAmountTaxPayable
						.add(ecoDetails.getEcoSup().getCamt() != null
								? ecoDetails.getEcoSup().getCamt()
								: BigDecimal.ZERO);
				sgstAmountTaxPayable = sgstAmountTaxPayable
						.add(ecoDetails.getEcoSup().getSamt() != null
								? ecoDetails.getEcoSup().getSamt()
								: BigDecimal.ZERO);
				cessAmountTaxPayable = cessAmountTaxPayable
						.add(ecoDetails.getEcoSup().getCsamt() != null
								? ecoDetails.getEcoSup().getCsamt()
								: BigDecimal.ZERO);
			}
		}

		GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(GenUtil
				.checkExpoAndEmpty(igstAmountTaxPayable.toPlainString())));
		GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(GenUtil
				.checkExpoAndEmpty(cgstAmountTaxPayable.toPlainString())));
		GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(GenUtil
				.checkExpoAndEmpty(sgstAmountTaxPayable.toPlainString())));
		GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(GenUtil
				.checkExpoAndEmpty(cessAmountTaxPayable.toPlainString())));

		// US 49322 - Dashboard
		// taxPayable values persistance in DB

		gstr3BEntity.setTaxPayIgst(igstAmountTaxPayable);
		gstr3BEntity.setTaxPaySgst(sgstAmountTaxPayable);
		gstr3BEntity.setTaxPayCgst(cgstAmountTaxPayable);
		gstr3BEntity.setTaxPayCess(cessAmountTaxPayable);
		 totalTaxPayable = totalTaxPayable.add(igstAmountTaxPayable
				.add(sgstAmountTaxPayable.add(cgstAmountTaxPayable.add(cessAmountTaxPayable))));
		gstr3BEntity.setTaxPayTotal(totalTaxPayable);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					String.format(" Tax Payable values for - igst '%s' sgst '%s' Cgst '%s' total Tax '%s' ",
					igstAmountTaxPayable, sgstAmountTaxPayable,
					cgstAmountTaxPayable, totalTaxPayable));
		}
		if (taxPaymentDetailsInvoice.getPaidItc() != null) {

			BigDecimal igstAmtItc = BigDecimal.ZERO;
			BigDecimal cgstAmtItc = BigDecimal.ZERO;
			BigDecimal sgstAmtItc = BigDecimal.ZERO;
			BigDecimal cessAmtItc = BigDecimal.ZERO;
			BigDecimal totalAmtItc = BigDecimal.ZERO;

			GenUtil.appendStringToJoiner(IGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getIGSTPaidUsingIGST())));
			GenUtil.appendStringToJoiner(IGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getIGSTPaidUsingCGST())));
			GenUtil.appendStringToJoiner(IGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getIGSTPaidUsingSGST())));
			GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(""));

			GenUtil.appendStringToJoiner(CGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getCGSTPaidUsingIGST())));
			GenUtil.appendStringToJoiner(CGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getCGSTPaidUsingCGST())));
			GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(""));

			GenUtil.appendStringToJoiner(SGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getSGSTPaidUsingIGST())));
			GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(SGSTJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getSGSTPaidUsingSGST())));
			GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(""));

			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(CessJoiner,
					GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(taxPaymentDetailsInvoice
									.getPaidItc().getCessPaidUsingCess())));
			
			
			
			// for paid through itc - igst, sgst, cgst, total
			gstr3BEntity.setIgstVsIgstOthers(taxPaymentDetailsInvoice.getPaidItc()
					.getIGSTPaidUsingIGST());
			
			gstr3BEntity.setIgstVsCgstOthers(taxPaymentDetailsInvoice.getPaidItc()
							.getIGSTPaidUsingCGST());
			gstr3BEntity.setIgstVsSgstOthers(taxPaymentDetailsInvoice
					.getPaidItc().getIGSTPaidUsingSGST());
			gstr3BEntity.setCgstVsIgstOthers(taxPaymentDetailsInvoice.getPaidItc()
					.getCGSTPaidUsingCGST());
			gstr3BEntity.setCgstVsCgstOthers(taxPaymentDetailsInvoice
					.getPaidItc().getCGSTPaidUsingIGST());
			gstr3BEntity.setSgstVsIgstOthers(taxPaymentDetailsInvoice.getPaidItc()
					.getSGSTPaidUsingIGST());
			gstr3BEntity.setSgstVsSgstOthers(taxPaymentDetailsInvoice
					.getPaidItc().getSGSTPaidUsingSGST());
			gstr3BEntity.setCessVsCessOthers(taxPaymentDetailsInvoice.getPaidItc()
					.getCessPaidUsingCess());
			
			igstAmtItc = igstAmtItc.add(taxPaymentDetailsInvoice.getPaidItc()
					.getIGSTPaidUsingIGST()
					.add(taxPaymentDetailsInvoice.getPaidItc()
							.getIGSTPaidUsingCGST().add(taxPaymentDetailsInvoice
									.getPaidItc().getIGSTPaidUsingSGST())));
			sgstAmtItc = sgstAmtItc.add(taxPaymentDetailsInvoice.getPaidItc()
					.getSGSTPaidUsingIGST().add(taxPaymentDetailsInvoice
							.getPaidItc().getSGSTPaidUsingSGST()));

			cgstAmtItc = cgstAmtItc.add(taxPaymentDetailsInvoice.getPaidItc()
					.getCGSTPaidUsingCGST().add(taxPaymentDetailsInvoice
							.getPaidItc().getCGSTPaidUsingIGST()));
			cessAmtItc = cessAmtItc.add(taxPaymentDetailsInvoice.getPaidItc()
					.getCessPaidUsingCess());
					
			gstr3BEntity.setItcIgst(igstAmtItc);
			gstr3BEntity.setItcSgst(sgstAmtItc);
			gstr3BEntity.setItcCgst(cgstAmtItc);
			gstr3BEntity.setItcCess(cessAmtItc);
			gstr3BEntity.setItcTotal(totalAmtItc
					.add(igstAmtItc.add(sgstAmtItc.add(cgstAmtItc.add(cessAmtItc)))));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format(
						" ITC values for - igst '%s' sgst '%s' Cgst '%s' total Tax '%s' ",
						igstAmtItc, sgstAmtItc, cgstAmtItc, totalAmtItc));
			}
		}

		if (taxPaymentDetailsInvoice.getPaidCash() != null) {

			BigDecimal igstAmount = BigDecimal.ZERO;
			BigDecimal cgstAmount = BigDecimal.ZERO;
			BigDecimal sgstAmount = BigDecimal.ZERO;
			BigDecimal cessAmount = BigDecimal.ZERO;

			BigDecimal igstIntPaid = BigDecimal.ZERO;
			BigDecimal cgstIntPaid = BigDecimal.ZERO;
			BigDecimal sgstIntPaid = BigDecimal.ZERO;
			BigDecimal cessIntPaid = BigDecimal.ZERO;

			BigDecimal igstLateFee = BigDecimal.ZERO;
			BigDecimal cgstLateFee = BigDecimal.ZERO;
			BigDecimal sgstLateFee = BigDecimal.ZERO;
			BigDecimal cessLateFee = BigDecimal.ZERO;

			for (com.ey.advisory.app.gstr3b.dto.PaidCashDetails inv : taxPaymentDetailsInvoice
					.getPaidCash()) {

				igstAmount = igstAmount.add(inv.getIgstPaid());
				cgstAmount = cgstAmount.add(inv.getCgstPaid());
				sgstAmount = sgstAmount.add(inv.getSgstPaid());
				cessAmount = cessAmount.add(inv.getCessPaid());

				igstIntPaid = igstIntPaid.add(inv.getIgstIntPaid());
				cgstIntPaid = cgstIntPaid.add(inv.getCgstIntPaid());
				sgstIntPaid = sgstIntPaid.add(inv.getSgstIntPaid());
				cessIntPaid = cessIntPaid.add(inv.getCessIntPaid());

				igstLateFee = igstLateFee.add(inv.getIgstLateFeePaid());
				cgstLateFee = cgstLateFee.add(inv.getCgstLateFeePaid());
				sgstLateFee = sgstLateFee.add(inv.getSgstLateFeePaid());
				cessLateFee = cessLateFee.add(inv.getCessLateFeePaid());
				
				if("30002".equalsIgnoreCase(inv.getTransType()))
				{
					gstr3BEntity.setTaxCashOthrsIgst(inv.getIgstPaid());
					gstr3BEntity.setTaxCashOthrsCgst(inv.getCgstPaid());
					gstr3BEntity.setTaxCashOthrsSgst(inv.getSgstPaid());
					gstr3BEntity.setTaxCashOthrsCess(inv.getCessPaid());
					
					gstr3BEntity.setIntrstCashOthrsIgst(inv.getIgstIntPaid());
					gstr3BEntity.setIntrstCashOthrsCgst(inv.getCgstIntPaid());
					gstr3BEntity.setIntrstCashOthrsSgst(inv.getSgstIntPaid());
					gstr3BEntity.setIntrstCashOthrsCess(inv.getCessIntPaid());
					
					gstr3BEntity.setLateFeeOthrsCgst(inv.getCgstLateFeePaid());
					gstr3BEntity.setLateFeeOthrsSgst(inv.getSgstLateFeePaid());
					
				}else if("30003".equalsIgnoreCase(inv.getTransType()))
				{
					gstr3BEntity.setTaxCashRevIgst(inv.getIgstPaid());
					gstr3BEntity.setTaxCashRevCgst(inv.getCgstPaid());
					gstr3BEntity.setTaxCashRevSgst(inv.getSgstPaid());
					gstr3BEntity.setTaxCashRevCess(inv.getCessPaid());
				}

			}

			GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(igstAmount.toPlainString())));
			GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cgstAmount.toPlainString())));
			GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(sgstAmount.toPlainString())));
			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cessAmount.toPlainString())));

			// paid in cash - IGST, CGST, SGST, Total
			gstr3BEntity.setCashIgst(igstAmount);
			gstr3BEntity.setCashSgst(sgstAmount);
			gstr3BEntity.setCashCgst(cgstAmount);
			gstr3BEntity.setCashCess(cessAmount);
			gstr3BEntity
					.setCashTotal(igstAmount.add(sgstAmount.add(cgstAmount.add(cessAmount))));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						String.format(" Cash values for - igst '%s' sgst '%s' Cgst '%s' ",
						igstAmount, sgstAmount, cgstAmount));
			}

			GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(igstIntPaid.toPlainString())));
			GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cgstIntPaid.toPlainString())));
			GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(sgstIntPaid.toPlainString())));
			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cessIntPaid.toPlainString())));
			
			
			GenUtil.appendStringToJoiner(IGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(igstLateFee.toPlainString())));
			GenUtil.appendStringToJoiner(CGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cgstLateFee.toPlainString())));
			GenUtil.appendStringToJoiner(SGSTJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(sgstLateFee.toPlainString())));
			GenUtil.appendStringToJoiner(CessJoiner, GenUtil.toCsvString(
					GenUtil.checkExpoAndEmpty(cessLateFee.toPlainString())));
		}

		finalJoiner.add(IGSTJoiner.toString());
		finalJoiner.add(CGSTJoiner.toString());
		finalJoiner.add(SGSTJoiner.toString());
		finalJoiner.add(CessJoiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeTaxPaymentDetailsToCSV");
	}

	private static void writeSupDetailsToCSV(Gstr3BOutInwSuppDTO invoice,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD + "writeSupDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_A));
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_A);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table.getValue1()));
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.OSUP_DET_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getOsupDet() != null) {

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupDet().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupDet().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupDet().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupDet().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupDet().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 */
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 3.1B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_B));
		Pair<String, String> table3_1B = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_B);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1B.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1B.getValue1()));
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.OSUP_ZERO));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getOsupZero() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupZero().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupZero().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.checkExponenForAmt(invoice.getOsupZero().getSamt()));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.checkExponenForAmt(invoice.getOsupZero().getCamt()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupZero().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 */
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 3.1c
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_C));
		Pair<String, String> table3_1C = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_C);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1C.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1C.getValue1()));
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.OSUP_NIL_EXMPT_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getOsupNilExmp() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNilExmp().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNilExmp().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.checkExponenForAmt(invoice.getOsupNilExmp().getSamt()));
			GenUtil.appendStringToJoiner(joiner, GenUtil
					.checkExponenForAmt(invoice.getOsupNilExmp().getCamt()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNilExmp().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		 */
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 3.1d
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_D));
		Pair<String, String> table3_1D = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_D);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1D.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1D.getValue1()));
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.ISUP_REV_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getIsupRev() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIsupRev().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIsupRev().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIsupRev().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIsupRev().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIsupRev().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * for (int j = 1; j <= 3; j++) { GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString("")); }
		 */
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 3.1e
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_E));
		Pair<String, String> table3_1E = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_E);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1E.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1E.getValue1()));
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.OSUP_NONGST_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getOsupNongst() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNongst().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNongst().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNongst().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNongst().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getOsupNongst().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * for (int j = 1; j <= 3; j++) { GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString("")); }
		 */
		finalJoiner.add(joiner.toString());
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD + " writeSupDetailsToCSV");
	}

	private static void writeEcoDetailsToCSV(Gstr3BEcoDtlsDTO invoice,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD + "writeEcoDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_1_A));
		Pair<String, String> table = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_1_A);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table.getValue1()));
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getEcoSup() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getEcoSup().getTxval())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getEcoSup().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getEcoSup().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getEcoSup().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getEcoSup().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}

		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");

		// 3.1.1B
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_1_1_B));
		Pair<String, String> table3_1B = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_1_1_B);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1B.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_1B.getValue1()));

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		if (invoice.getEcoRegSup() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getEcoRegSup().getTxval())));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}

		finalJoiner.add(joiner.toString());
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD + " writeSupDetailsToCSV");
	}

	private static void writeInterSupDetailsToCSV(
			Gstr3BInterStateSuppDTO invoice, String ctin, BufferedWriter bw)
			throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeInterSupDetailsToCSV");

		table3_2Flag = false;

		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = null;
		if (invoice.getUnregDetails() != null
				&& !invoice.getUnregDetails().isEmpty()) {
			for (Gstr3BSecDetailsDTO inv : invoice.getUnregDetails()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(Gstr3BConstants.Table3_2_A));
				Pair<String, String> table3_2A = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_A);
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2A.getValue0()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2A.getValue1()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(GenUtil.checkExpoAndEmpty(inv.getPos())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getTxval())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getIamt())));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				finalJoiner.add(joiner.toString());
			}
		} else {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(Gstr3BConstants.Table3_2_A));
			Pair<String, String> table3_2A = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_A);
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2A.getValue0()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2A.getValue1()));

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

		}
		if (invoice.getCompDetails() != null
				&& !invoice.getCompDetails().isEmpty()) {
			for (Gstr3BSecDetailsDTO inv : invoice.getCompDetails()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(Gstr3BConstants.Table3_2_B));
				Pair<String, String> table3_2B = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_B);
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2B.getValue0()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2B.getValue1()));
				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(GenUtil.checkExpoAndEmpty(inv.getPos())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getTxval())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getIamt())));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				finalJoiner.add(joiner.toString());
			}
		} else {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(Gstr3BConstants.Table3_2_B));
			Pair<String, String> table3_2B = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_B);
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2B.getValue0()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2B.getValue1()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

		}

		if (invoice.getUinDetails() != null
				&& !invoice.getUinDetails().isEmpty()) {
			for (Gstr3BSecDetailsDTO inv : invoice.getUinDetails()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(Gstr3BConstants.Table3_2_C));
				Pair<String, String> table3_2C = CommonUtility
						.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_C);
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2C.getValue0()));
				GenUtil.appendStringToJoiner(joiner,
						GenUtil.toCsvString(table3_2C.getValue1()));

				GenUtil.appendStringToJoiner(joiner, GenUtil
						.toCsvString(GenUtil.checkExpoAndEmpty(inv.getPos())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getTxval())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getIamt())));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());

				finalJoiner.add(joiner.toString());
			}
		}

		else {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(Gstr3BConstants.Table3_2_C));
			Pair<String, String> table3_2C = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_C);
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2C.getValue0()));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(table3_2C.getValue1()));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}

			finalJoiner.add(joiner.toString());

		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeInterSupDetailsToCSV");
	}

	private static void writeItcEligibleDetailsToCSV(Gstr3BElgItcDTO invoice,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeItcEligibleDetailsToCSV");

		if (table3_2Flag) {
			DefaultInterSupDetailsToCSV(ctin, bw);
		}
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = null;
		StringJoiner joiner1 = null;
		StringJoiner joiner2 = null;
		StringJoiner joiner3 = null;
		StringJoiner joiner4 = null;
		StringJoiner joiner5 = null;
		if (invoice.getItc_avl() != null) {
			for (Gstr3BSecDetailsDTO inv : invoice.getItc_avl()) {
				if ("IMPG".equalsIgnoreCase(inv.getTy())) {
					joiner1 = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner1,
							GenUtil.toCsvString(ctin));

					GenUtil.appendStringToJoiner(joiner1,
							Gstr3BConstants.Table4A1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A1);
					GenUtil.appendStringToJoiner(joiner1, table.getValue0());
					GenUtil.appendStringToJoiner(joiner1, table.getValue1());

					GenUtil.appendStringToJoiner(joiner1,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner1,
							BigDecimal.ZERO.toPlainString());
					GenUtil.appendStringToJoiner(joiner1, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIamt())));
					GenUtil.appendStringToJoiner(joiner1, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getSamt())));
					GenUtil.appendStringToJoiner(joiner1, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCamt())));
					GenUtil.appendStringToJoiner(joiner1, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCsamt())));

				} else if ("IMPS".equalsIgnoreCase(inv.getTy())) {
					joiner2 = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner2,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner2,
							Gstr3BConstants.Table4A2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A2);
					GenUtil.appendStringToJoiner(joiner2, table.getValue0());
					GenUtil.appendStringToJoiner(joiner2, table.getValue1());

					GenUtil.appendStringToJoiner(joiner2,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner2,
							BigDecimal.ZERO.toPlainString());
					GenUtil.appendStringToJoiner(joiner2, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIamt())));
					GenUtil.appendStringToJoiner(joiner2, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getSamt())));
					GenUtil.appendStringToJoiner(joiner2, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCamt())));
					GenUtil.appendStringToJoiner(joiner2, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCsamt())));

				} else if ("ISRC".equalsIgnoreCase(inv.getTy())) {
					joiner3 = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner3,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner3,
							Gstr3BConstants.Table4A3);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A3);
					GenUtil.appendStringToJoiner(joiner3, table.getValue0());
					GenUtil.appendStringToJoiner(joiner3, table.getValue1());

					GenUtil.appendStringToJoiner(joiner3,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner3,
							BigDecimal.ZERO.toPlainString());
					GenUtil.appendStringToJoiner(joiner3, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIamt())));
					GenUtil.appendStringToJoiner(joiner3, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getSamt())));
					GenUtil.appendStringToJoiner(joiner3, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCamt())));
					GenUtil.appendStringToJoiner(joiner3, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCsamt())));

				} else if ("ISD".equalsIgnoreCase(inv.getTy())) {
					joiner4 = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner4,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner4,
							Gstr3BConstants.Table4A4);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A4);
					GenUtil.appendStringToJoiner(joiner4, table.getValue0());
					GenUtil.appendStringToJoiner(joiner4, table.getValue1());

					GenUtil.appendStringToJoiner(joiner4,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner4,
							BigDecimal.ZERO.toPlainString());
					GenUtil.appendStringToJoiner(joiner4, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIamt())));
					GenUtil.appendStringToJoiner(joiner4, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getSamt())));
					GenUtil.appendStringToJoiner(joiner4, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCamt())));
					GenUtil.appendStringToJoiner(joiner4, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCsamt())));

				} else if ("OTH".equalsIgnoreCase(inv.getTy())) {
					joiner5 = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner5,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner5,
							Gstr3BConstants.Table4A5);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A5);
					GenUtil.appendStringToJoiner(joiner5, table.getValue0());
					GenUtil.appendStringToJoiner(joiner5, table.getValue1());

					GenUtil.appendStringToJoiner(joiner5,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner5,
							BigDecimal.ZERO.toPlainString());
					GenUtil.appendStringToJoiner(joiner5, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIamt())));
					GenUtil.appendStringToJoiner(joiner5, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getSamt())));
					GenUtil.appendStringToJoiner(joiner5, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCamt())));
					GenUtil.appendStringToJoiner(joiner5, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getCsamt())));

				}

			}
			finalJoiner.add(joiner1 != null ? joiner1.toString() : "");
			finalJoiner.add(joiner2 != null ? joiner2.toString() : "");
			finalJoiner.add(joiner3 != null ? joiner3.toString() : "");
			finalJoiner.add(joiner4 != null ? joiner4.toString() : "");
			finalJoiner.add(joiner5 != null ? joiner5.toString() : "");
		} else {

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4A1);
			Pair<String, String> table1 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A1);
			GenUtil.appendStringToJoiner(joiner, table1.getValue0());
			GenUtil.appendStringToJoiner(joiner, table1.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}

			finalJoiner.add(joiner.toString());
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4A2);
			Pair<String, String> table2 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A2);
			GenUtil.appendStringToJoiner(joiner, table2.getValue0());
			GenUtil.appendStringToJoiner(joiner, table2.getValue1());

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4A3);
			Pair<String, String> table3 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A3);
			GenUtil.appendStringToJoiner(joiner, table3.getValue0());
			GenUtil.appendStringToJoiner(joiner, table3.getValue1());

			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4A4);
			Pair<String, String> table4 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A4);
			GenUtil.appendStringToJoiner(joiner, table4.getValue0());
			GenUtil.appendStringToJoiner(joiner, table4.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4A5);
			Pair<String, String> table5 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4A5);
			GenUtil.appendStringToJoiner(joiner, table5.getValue0());
			GenUtil.appendStringToJoiner(joiner, table5.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

		}
		if (invoice.getItcRev() != null) {
			for (Gstr3BSecDetailsDTO inv : invoice.getItcRev()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if ("RUL".equalsIgnoreCase(inv.getTy())) {
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table4B1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B1);
					GenUtil.appendStringToJoiner(joiner, table.getValue0());
					GenUtil.appendStringToJoiner(joiner, table.getValue1());

				} else if ("OTH".equalsIgnoreCase(inv.getTy())) {
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table4B2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B2);
					GenUtil.appendStringToJoiner(joiner, table.getValue0());
					GenUtil.appendStringToJoiner(joiner, table.getValue1());
				}
				/*
				 * GenUtil.appendStringToJoiner(joiner,
				 * GenUtil.toCsvString(JobStatusConstants.ITC_REV_Type));
				 */
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getCsamt())));
				/*
				 * GenUtil.appendStringToJoiner(joiner,
				 * GenUtil.toCsvString(inv.getTy())); for (int j = 1; j <= 2;
				 * j++) { GenUtil.appendStringToJoiner(joiner,
				 * GenUtil.toCsvString("")); }
				 */
				finalJoiner.add(joiner.toString());
			}
		} else {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4B1);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B1);
			GenUtil.appendStringToJoiner(joiner, table.getValue0());
			GenUtil.appendStringToJoiner(joiner, table.getValue1());
			// GenUtil.toCsvString(JobStatusConstants.ITC_REV_Type);
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			/*
			 * GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString("RUL"));
			 * for (int j = 1; j <= 2; j++) {
			 * GenUtil.appendStringToJoiner(joiner,
			 * BigDecimal.ZERO.toPlainString()); }
			 */
			finalJoiner.add(joiner.toString());

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4B2);
			Pair<String, String> table1 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4B2);
			GenUtil.appendStringToJoiner(joiner, table1.getValue0());
			GenUtil.appendStringToJoiner(joiner, table1.getValue1());
			// GenUtil.toCsvString(JobStatusConstants.ITC_REV_Type);
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());
		}
		if (invoice.getItcNet() != null) {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4C);
			Pair<String, String> table1 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4C);
			GenUtil.appendStringToJoiner(joiner, table1.getValue0());
			GenUtil.appendStringToJoiner(joiner, table1.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getItcNet().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getItcNet().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
					GenUtil.checkExponenForAmt(invoice.getItcNet().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getItcNet().getCsamt())));

			finalJoiner.add(joiner.toString());
		} else {
			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4C);
			Pair<String, String> table1 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4C);
			GenUtil.appendStringToJoiner(joiner, table1.getValue0());
			GenUtil.appendStringToJoiner(joiner, table1.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}
			finalJoiner.add(joiner.toString());

		}
		if (invoice.getItcInelg() != null) {
			for (Gstr3BSecDetailsDTO inv : invoice.getItcInelg()) {
				joiner = new StringJoiner(",");
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
				if ("RUL".equalsIgnoreCase(inv.getTy())) {
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table4D1);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D1);
					GenUtil.appendStringToJoiner(joiner, table.getValue0());
					GenUtil.appendStringToJoiner(joiner, table.getValue1());

				} else if ("OTH".equalsIgnoreCase(inv.getTy())) {
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table4D2);
					Pair<String, String> table = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D2);
					GenUtil.appendStringToJoiner(joiner, table.getValue0());
					GenUtil.appendStringToJoiner(joiner, table.getValue1());
				}

				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getIamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getSamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getCamt())));
				GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
						GenUtil.checkExponenForAmt(inv.getCsamt())));

				finalJoiner.add(joiner.toString());
			}
		} else {

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4D1);
			Pair<String, String> table = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D1);
			GenUtil.appendStringToJoiner(joiner, table.getValue0());
			GenUtil.appendStringToJoiner(joiner, table.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}

			finalJoiner.add(joiner.toString());

			joiner = new StringJoiner(",");
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
			GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table4D2);
			Pair<String, String> table1 = CommonUtility
					.getGstr3bHeadingandDesc(Gstr3BConstants.Table4D2);
			GenUtil.appendStringToJoiner(joiner, table1.getValue0());
			GenUtil.appendStringToJoiner(joiner, table1.getValue1());
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
			for (int j = 1; j <= 5; j++) {
				GenUtil.appendStringToJoiner(joiner,
						BigDecimal.ZERO.toPlainString());
			}

			finalJoiner.add(joiner.toString());

		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");

		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeItcEligibleDetailsToCSV");
	}

	private static void writeInwardSupDetailsToCSV(Gstr3BInwSuppDTO invoice,
			String ctin, BufferedWriter bw) throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeInwardSupDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = null;
		if (invoice.getIsupDetails() != null) {
			for (Gstr3BSecDetailsDTO inv : invoice.getIsupDetails()) {
				joiner = new StringJoiner(",");
				if ("GST".equalsIgnoreCase(inv.getTy())) {
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table5A1);
					Pair<String, String> table1 = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table5A1);
					GenUtil.appendStringToJoiner(joiner, table1.getValue0());
					GenUtil.appendStringToJoiner(joiner, table1.getValue1());
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getInter())));
					for (int j = 1; j <= 4; j++) {
						GenUtil.appendStringToJoiner(joiner,
								BigDecimal.ZERO.toPlainString());
					}
					finalJoiner.add(joiner.toString());

					joiner = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table5A2);
					Pair<String, String> table2 = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table5A2);
					GenUtil.appendStringToJoiner(joiner, table2.getValue0());
					GenUtil.appendStringToJoiner(joiner, table2.getValue1());
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIntra())));
					for (int j = 1; j <= 4; j++) {
						GenUtil.appendStringToJoiner(joiner,
								BigDecimal.ZERO.toPlainString());
					}

					finalJoiner.add(joiner.toString());
				}
				if ("NONGST".equalsIgnoreCase(inv.getTy())) {
					joiner = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table5B1);
					Pair<String, String> table1 = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table5B1);
					GenUtil.appendStringToJoiner(joiner, table1.getValue0());
					GenUtil.appendStringToJoiner(joiner, table1.getValue1());

					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getInter())));
					for (int j = 1; j <= 4; j++) {
						GenUtil.appendStringToJoiner(joiner,
								BigDecimal.ZERO.toPlainString());
					}

					finalJoiner.add(joiner.toString());

					joiner = new StringJoiner(",");
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(ctin));
					GenUtil.appendStringToJoiner(joiner,
							Gstr3BConstants.Table5B2);
					Pair<String, String> table2 = CommonUtility
							.getGstr3bHeadingandDesc(Gstr3BConstants.Table5B2);
					GenUtil.appendStringToJoiner(joiner, table2.getValue0());
					GenUtil.appendStringToJoiner(joiner, table2.getValue1());
					GenUtil.appendStringToJoiner(joiner,
							GenUtil.toCsvString(""));
					GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(
							GenUtil.checkExponenForAmt(inv.getIntra())));
					for (int j = 1; j <= 4; j++) {
						GenUtil.appendStringToJoiner(joiner,
								BigDecimal.ZERO.toPlainString());
					}
					finalJoiner.add(joiner.toString());
				}
			}
		}
		bw.write(finalJoiner.toString());
		bw.write("\r\n");

		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeInwardSupDetailsToCSV");
	}

	private static void writeInterestLateFeeDetailsToCSV(
			Gstr3BIntLateFeeDTO invoice, String ctin, BufferedWriter bw)
			throws IOException {
		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "writeInterestLateFeeDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table5_1A);
		Pair<String, String> table1 = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table5_1A);
		GenUtil.appendStringToJoiner(joiner, table1.getValue0());
		GenUtil.appendStringToJoiner(joiner, table1.getValue1());
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.INTR_DET_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, BigDecimal.ZERO.toPlainString());
		if (invoice.getIntrDetails() != null) {
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIntrDetails().getIamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIntrDetails().getSamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIntrDetails().getCamt())));
			GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(GenUtil
					.checkExponenForAmt(invoice.getIntrDetails().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * for (int j = 1; j <= 3; j++) { GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString("")); }
		 */
		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner, Gstr3BConstants.Table5_1B);
		Pair<String, String> table2 = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table5_1B);
		GenUtil.appendStringToJoiner(joiner, table2.getValue0());
		GenUtil.appendStringToJoiner(joiner, table2.getValue1());
		/*
		 * GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString(JobStatusConstants.LATE_FEE_DET_Type));
		 */
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		GenUtil.appendStringToJoiner(joiner, BigDecimal.ZERO.toPlainString());
		if (invoice.getLateFeeDetails() != null) {
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							invoice.getLateFeeDetails().getIamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							invoice.getLateFeeDetails().getSamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							invoice.getLateFeeDetails().getCamt())));
			GenUtil.appendStringToJoiner(joiner,
					GenUtil.toCsvString(GenUtil.checkExponenForAmt(
							invoice.getLateFeeDetails().getCsamt())));
		} else {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}
		/*
		 * for (int j = 1; j <= 3; j++) { GenUtil.appendStringToJoiner(joiner,
		 * GenUtil.toCsvString("")); }
		 */
		finalJoiner.add(joiner.toString());
		joiner = new StringJoiner(",");
		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.info(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " writeInterestLateFeeDetailsToCSV");
	}

	private static void DefaultInterSupDetailsToCSV(String ctin,
			BufferedWriter bw) throws IOException {

		LOGGER.info(JobStatusConstants.LOGGER_ENTERING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ "DefaultInterSupDetailsToCSV");
		StringJoiner finalJoiner = new StringJoiner("\n");
		StringJoiner joiner = null;

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_2_A));
		Pair<String, String> table3_2A = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_A);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2A.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2A.getValue1()));

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		for (int j = 1; j <= 5; j++) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}

		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_2_B));
		Pair<String, String> table3_2B = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_B);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2B.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2B.getValue1()));

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		for (int j = 1; j <= 5; j++) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}

		finalJoiner.add(joiner.toString());

		joiner = new StringJoiner(",");
		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(ctin));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(Gstr3BConstants.Table3_2_C));
		Pair<String, String> table3_2C = CommonUtility
				.getGstr3bHeadingandDesc(Gstr3BConstants.Table3_2_C);
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2C.getValue0()));
		GenUtil.appendStringToJoiner(joiner,
				GenUtil.toCsvString(table3_2C.getValue1()));

		GenUtil.appendStringToJoiner(joiner, GenUtil.toCsvString(""));
		for (int j = 1; j <= 5; j++) {
			GenUtil.appendStringToJoiner(joiner,
					BigDecimal.ZERO.toPlainString());
		}

		finalJoiner.add(joiner.toString());

		bw.write(finalJoiner.toString());
		bw.write("\r\n");
		LOGGER.debug(JobStatusConstants.LOGGER_EXITING + CLASS_NAME
				+ JobStatusConstants.LOGGER_METHOD
				+ " DefaultInterSupDetailsToCSV");

	}

	@Override
	public String[] getCsvHeaderStrings() {
		return new String[] { GSTR_3B_HDR, TAX_PAYMENT_HDR };
	}

	@Override
	public int getNoOfConvOutputs() {
		return 2;
	}

}