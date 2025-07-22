package com.ey.advisory.app.services.jobs.gstr1;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpItemEntity;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2BInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
import com.ey.advisory.app.docs.dto.CDNRInvoices;
import com.ey.advisory.app.docs.dto.CDNURInvoices;
import com.ey.advisory.app.docs.dto.CdnrLineItem;
import com.ey.advisory.app.docs.dto.CdnrLineItemDetail;
import com.ey.advisory.app.docs.dto.CdnurLineItem;
import com.ey.advisory.app.docs.dto.CdnurLineItemDetail;
import com.ey.advisory.app.docs.dto.CreditDebitNote;
import com.ey.advisory.app.docs.dto.EXPInvoiceData;
import com.ey.advisory.app.docs.dto.EXPInvoices;
import com.ey.advisory.app.docs.dto.EXPLineItem;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Anand3.M
 *
 */
@Component("Gstr1EInvoiceDataParserImpl")
@Slf4j
public class Gstr1EInvoiceDataParserImpl implements Gstr1EInvoiceDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1EInvoicesB2bHeaderEntity> parseEInvoiceB2bData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1EInvoicesB2bHeaderEntity> headerList = new ArrayList<>();

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.B2B).getAsJsonArray();

		Type listType = new TypeToken<List<B2BInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<B2BInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (B2BInvoices eachInv : baseEntity) {

			for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1EInvoicesB2bItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {

					for (B2bLineItem b2cItems : eachInvData.getLineItems()) {

						B2bLineItemDetail b2cItem = b2cItems.getItemDetail();
						// New Item Entity
						GetGstr1EInvoicesB2bItemEntity item = new GetGstr1EInvoicesB2bItemEntity();
						/**
						 * B2BItemDetails data
						 */

						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null
								&& dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(
									GenUtil.convertTaxPeriodToInt(
											dto.getReturnPeriod()));
						}
						item.setTaxRate(b2cItem.getRate());
						item.setTaxValue(b2cItem.getTaxableValue());
						item.setIgstAmt(b2cItem.getIgstAmount());
						item.setCgstAmt(b2cItem.getCgstAmount());
						item.setSgstAmt(b2cItem.getSgstAmount());
						item.setCessAmt(b2cItem.getCessAmount());
						// item.setInvValue(eachInvData.getInvoiceValue());
						item.setSerialNum(b2cItems.getLineNumber());

						lineItems.add(item);

						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getTaxValue() != null) {
							taxValue = taxValue.add(item.getTaxValue());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}
						if (item.getCgstAmt() != null) {

							cgstAmt = cgstAmt.add(item.getCgstAmt());
						}
						if (item.getSgstAmt() != null) {

							sgstAmt = sgstAmt.add(item.getSgstAmt());
						}

						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1EInvoicesB2bHeaderEntity header = new GetGstr1EInvoicesB2bHeaderEntity();
					/**
					 * Input data
					 */
					// header.setAction(APIConstants.N);
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					// header.setTableSection(dto.getSection());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * B2BInvoices data
					 */
					header.setCtin(eachInv.getCgstin());
					header.setTradeName(eachInv.getTradeName());
					header.setInvChksum(eachInvData.getCheckSum());
					header.setInvNum(eachInvData.getInvoiceNumber());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setPos(eachInvData.getPos());
					header.setRchrg(eachInvData.getReverseCharge());
					header.setEtin(eachInvData.getEcomTin());
					header.setInvType(eachInvData.getInvoiceType());
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setIrnNum(eachInvData.getIrn());
					header.setIrnSrcType(eachInvData.getSrctyp());
					String irnDate = eachInvData.getIrngendate();
					if (irnDate != null) {
						header.setIrnGenDate(String.format(irnDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}

					header.seteInvStatus(eachInvData.getEinvstatus());
					header.setAutoDraftStatus(eachInvData.getAutodft());
					String autoDate = eachInvData.getAutodftdt();
					if (autoDate != null) {
						header.setAutoDraftDate(String.format(autoDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setErrorCode(eachInvData.getErrorCd());
					header.setErrorDesc(eachInvData.getErrorMsg());

					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					// taxes at header level by summing the item values
					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCgstAmt(cgstAmt);
					header.setSgstAmt(sgstAmt);
					header.setCessAmt(cessAmt);

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNumber();
					String sgtin = dto.getGstin();

					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));

					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
							finYear, GSTConstants.INV.toUpperCase()));

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});

					headerList.add(header);
				}
			}
		}

		return headerList;
	}

	@Override
	public List<GetGstr1EInvoicesCdnrHeaderEntity> parseEInvoiceCdnrData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1EInvoicesCdnrHeaderEntity> headerList = new ArrayList<>();

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.CDNR).getAsJsonArray();

		Type listType = new TypeToken<List<CDNRInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNRInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNRInvoices eachInv : baseEntity) {

			for (CreditDebitNote eachInvData : eachInv
					.getCreditDebitNoteDetails()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cgstAmt = BigDecimal.ZERO;
				BigDecimal sgstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1EInvoicesCdnrItemEntity> lineItems = new ArrayList<>();

				for (CdnrLineItem b2cItems : eachInvData.getCdnrLineItem()) {

					CdnrLineItemDetail b2cItem = b2cItems.getItemDetail();
					// New Item Entity
					GetGstr1EInvoicesCdnrItemEntity item = new GetGstr1EInvoicesCdnrItemEntity();
					/**
					 * CDNRItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					// item.setInvValue(eachInvData.getTotalNoteVal());
					item.setIgstAmt(b2cItem.getIgstAmount());
					item.setCgstAmt(b2cItem.getCgstAmount());
					item.setSgstAmt(b2cItem.getSgstAmount());
					item.setCessAmt(b2cItem.getCessAmount());
					item.setSerialNum(b2cItems.getLineNumber());

					lineItems.add(item);

					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getTaxValue() != null) {
						taxValue = taxValue.add(item.getTaxValue());
					}
					if (item.getIgstAmt() != null) {

						igstAmt = igstAmt.add(item.getIgstAmt());
					}
					if (item.getCgstAmt() != null) {

						cgstAmt = cgstAmt.add(item.getCgstAmt());
					}
					if (item.getSgstAmt() != null) {

						sgstAmt = sgstAmt.add(item.getSgstAmt());
					}

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1EInvoicesCdnrHeaderEntity header = new GetGstr1EInvoicesCdnrHeaderEntity();
				/**
				 * Input data
				 */

				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnrInvoices data
				 */

				header.setChksum(eachInvData.getCheckSum());
                header.setTradeName(eachInv.getTradeName());
                header.setNoteNum(eachInvData.getCredDebRefVoucherNum());
				header.setIrnNum(eachInvData.getIrn());
				header.setIrnSrcType(eachInvData.getSrctyp());
				String irnDate = eachInvData.getIrngendate();
				if (irnDate != null) {
					header.setIrnGenDate(String.format(irnDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.seteInvStatus(eachInvData.getEinvstatus());
				header.setAutoDraftStatus(eachInvData.getAutodft());
				String autoDate = eachInvData.getAutodftdt();
				if (autoDate != null) {
					header.setAutoDraftDate(String.format(autoDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setErrorCode(eachInvData.getErrorCd());
				header.setErrorDesc(eachInvData.getErrorMsg());
				header.setCtin(eachInv.getCpGstin());
				String noteDate = eachInvData.getCredDebRefVoucherDate();
				if (noteDate != null) {
					header.setNoteDate(String.format(noteDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.setInvValue(eachInvData.getTotalNoteVal());
				header.setPos(eachInvData.getPos());
				header.setRevCharge(eachInvData.getReverseCharge());
				header.setInvType(eachInvData.getInvoiceType());
				header.setNoteType(eachInvData.getCredDebRefVoucher());

				
				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCgstAmt(cgstAmt);
				header.setSgstAmt(sgstAmt);
				header.setCessAmt(cessAmt);

				String docDate = eachInvData.getCredDebRefVoucherDate();
				String docNum = eachInvData.getCredDebRefVoucherNum();
				String sgtin = dto.getGstin();
				String docType = eachInvData.getCredDebRefVoucher();
				if (docType != null) {
					docType = docType.equalsIgnoreCase(APIConstants.C)
							? GSTConstants.CR : GSTConstants.DR;
				}
				String finYear = null;
				if (docDate != null) {
					finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
						finYear, docType));

				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}

		return headerList;
	}

	@Override
	public List<GetGstr1EInvoicesCdnurHeaderEntity> parseEInvoiceCdnurData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1EInvoicesCdnurHeaderEntity> headerList = new ArrayList<>();

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.CDNUR).getAsJsonArray();

		Type listType = new TypeToken<List<CDNURInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<CDNURInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (CDNURInvoices eachInv : baseEntity) {

			BigDecimal taxRate = BigDecimal.ZERO;
			BigDecimal taxValue = BigDecimal.ZERO;
			BigDecimal igstAmt = BigDecimal.ZERO;
			BigDecimal cessAmt = BigDecimal.ZERO;
			List<GetGstr1EInvoicesCdnurItemEntity> lineItems = new ArrayList<>();
			if (eachInv.getCdnrLineItem() != null) {

				for (CdnurLineItem b2cItems : eachInv.getCdnrLineItem()) {

					CdnurLineItemDetail b2cItem = b2cItems.getItemDetail();
					// New Item Entity
					GetGstr1EInvoicesCdnurItemEntity item = new GetGstr1EInvoicesCdnurItemEntity();
					/**
					 * CDNURItemDetails data
					 */

					item.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						item.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					item.setTaxRate(b2cItem.getRate());
					item.setTaxValue(b2cItem.getTaxableValue());
					item.setIgstAmt(b2cItem.getIgstAmount());
					// item.setInvValue(eachInv.getTotalNoteVal());
					item.setCessAmt(b2cItem.getCessAmount());
					item.setSerialNum(b2cItems.getLineNumber());

					lineItems.add(item);

					// Header Amounts count
					if (item.getTaxRate() != null) {
						taxRate = taxRate.add(item.getTaxRate());
					}
					if (item.getTaxValue() != null) {
						taxValue = taxValue.add(item.getTaxValue());
					}
					if (item.getIgstAmt() != null) {

						igstAmt = igstAmt.add(item.getIgstAmt());
					}

					if (item.getCessAmt() != null) {
						cessAmt = cessAmt.add(item.getCessAmt());
					}

				}
				// New Header Entity
				GetGstr1EInvoicesCdnurHeaderEntity header = new GetGstr1EInvoicesCdnurHeaderEntity();
				/**
				 * Input data
				 */
				header.setGstin(dto.getGstin());
				header.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					header.setDerivedTaxperiod(GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				header.setBatchId(dto.getBatchId());

				/**
				 * CdnurInvoices data
				 */

				header.setInvChkSum(eachInv.getCheckSum());

				header.setType(eachInv.getType());
				header.setNoteType(eachInv.getCredDebRefVoucher());
				String noteDate = eachInv.getCredDebRefVoucherDate();
				if (noteDate != null) {
					header.setNoteDate(String.format(noteDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setNoteNum(eachInv.getCredDebRefVoucherNum());

				header.setInvValue(eachInv.getTotalNoteVal());
				header.setIrnNum(eachInv.getIrn());
				header.setIrnSrcType(eachInv.getSrctyp());
				String irnDate = eachInv.getIrngendate();
				if (irnDate != null) {
					header.setIrnGenDate(String.format(irnDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				header.seteInvStatus(eachInv.getEinvstatus());
				header.setAutoDraftStatus(eachInv.getAutodft());
				String autoDate = eachInv.getAutodftdt();
				if (autoDate != null) {
					header.setAutoDraftDate(String.format(autoDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setErrorCode(eachInv.getErrorCd());
				header.setErrorDesc(eachInv.getErrorMsg());
				header.setPos(eachInv.getPos());

				// taxes at header level by summing the item values

				header.setTaxValue(taxValue);
				header.setIgstAmt(igstAmt);
				header.setCessAmt(cessAmt);

				String docDate = eachInv.getCredDebRefVoucherDate();
				String docNum = eachInv.getCredDebRefVoucherNum();
				String sgtin = dto.getGstin();
				String docType = eachInv.getCredDebRefVoucher();
				if (docType != null) {
					docType = docType.equalsIgnoreCase(APIConstants.C)
							? GSTConstants.CR : GSTConstants.DR;
				}
				String finYear = null;
				if (docDate != null) {
					finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
						finYear, docType));

				LocalDateTime convertNow = EYDateUtil
						.toUTCDateTimeFromLocal(LocalDateTime.now());
				header.setCreatedOn(convertNow);
				header.setCreatedBy("SYSTEM");

				header.setLineItems(lineItems);
				lineItems.forEach(item -> {
					item.setDocument(header);
				});

				headerList.add(header);
			}
		}

		return headerList;
	}

	@Override
	public List<GetGstr1EInvoicesExpHeaderEntity> parseEInvoiceExpData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1EInvoicesExpHeaderEntity> headerList = new ArrayList<>();

		respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
				.get(APIConstants.EXP).getAsJsonArray();

		Type listType = new TypeToken<List<EXPInvoices>>() {
		}.getType();

		// List of Header and Item Data
		List<EXPInvoices> baseEntity = gson.fromJson(respObject, listType);

		for (EXPInvoices eachInv : baseEntity) {

			for (EXPInvoiceData eachInvData : eachInv.getExpInvoiceData()) {

				BigDecimal taxRate = BigDecimal.ZERO;
				BigDecimal taxValue = BigDecimal.ZERO;
				BigDecimal igstAmt = BigDecimal.ZERO;
				BigDecimal cessAmt = BigDecimal.ZERO;
				List<GetGstr1EInvoicesExpItemEntity> lineItems = new ArrayList<>();

				if (eachInvData.getLineItems() != null) {
					for (EXPLineItem eachInvDa : eachInvData.getLineItems()) {

						// New Item Entity
						GetGstr1EInvoicesExpItemEntity item = new GetGstr1EInvoicesExpItemEntity();
						/**
						 * EXPItemDetails data
						 */
						item.setReturnPeriod(dto.getReturnPeriod());
						if (dto.getReturnPeriod() != null
								&& dto.getReturnPeriod().length() > 0) {
							item.setDerivedTaxperiod(
									GenUtil.convertTaxPeriodToInt(
											dto.getReturnPeriod()));
						}
						item.setTaxRate(eachInvDa.getRate());
						item.setTaxVal(eachInvDa.getTaxableValue());
						item.setIgstAmt(eachInvDa.getIgstAmount());
						item.setCessAmt(eachInvDa.getCessAmount());

						lineItems.add(item);
						// Header Amounts count
						if (item.getTaxRate() != null) {
							taxRate = taxRate.add(item.getTaxRate());
						}
						if (item.getTaxVal() != null) {
							taxValue = taxValue.add(item.getTaxVal());
						}
						if (item.getIgstAmt() != null) {

							igstAmt = igstAmt.add(item.getIgstAmt());
						}

						if (item.getCessAmt() != null) {
							cessAmt = cessAmt.add(item.getCessAmt());
						}

					}
					// New Header Entity
					GetGstr1EInvoicesExpHeaderEntity header = new GetGstr1EInvoicesExpHeaderEntity();
					/**
					 * Input data
					 */
					header.setGstin(dto.getGstin());
					header.setReturnPeriod(dto.getReturnPeriod());
					if (dto.getReturnPeriod() != null
							&& dto.getReturnPeriod().length() > 0) {
						header.setDerivedTaxperiod(GenUtil
								.convertTaxPeriodToInt(dto.getReturnPeriod()));
					}
					header.setBatchId(dto.getBatchId());

					/**
					 * ExpInvoices data
					 */
					header.setInvChksum(eachInvData.getCheckSum());

					header.setExportType(eachInv.getExportType());
					header.setInvNum(eachInvData.getInvoiceNum());
					String invDate = eachInvData.getInvoiceDate();
					if (invDate != null) {
						header.setInvDate(String.format(invDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setInvValue(eachInvData.getInvoiceValue());
					header.setShipBillPortCode(
							eachInvData.getShipBillPortCode());
					header.setShipBillNum(eachInvData.getShipBillNum());
					String shipBillDate = eachInvData.getShipBillDate();
					if (shipBillDate != null) {
						header.setShipBillDate(String.format(shipBillDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setIrnNum(eachInvData.getIrn());
					String irnDate = eachInvData.getIrngendate();
					if (irnDate != null) {
						header.setIrnGenDate(String.format(irnDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setIrnSrcType(eachInvData.getSrctyp());
					header.seteInvStatus(eachInvData.getEinvstatus());
					header.setAutoDraftStatus(eachInvData.getAutodft());
					String autoDate = eachInvData.getAutodftdt();
					if (autoDate != null) {
						header.setAutoDraftDate(String.format(autoDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}
					header.setErrorCode(eachInvData.getErrorCd());
					header.setErrorDesc(eachInvData.getErrorMsg());

					// taxes at header level by summing the item values

					header.setTaxValue(taxValue);
					header.setIgstAmt(igstAmt);
					header.setCessAmt(cessAmt);

					String docDate = eachInvData.getInvoiceDate();
					String docNum = eachInvData.getInvoiceNum();
					String sgtin = dto.getGstin();

					String finYear = GenUtil.getFinYear(LocalDate.parse(docDate,
							DateUtil.SUPPORTED_DATE_FORMAT2));
					header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum,
							finYear, GSTConstants.INV.toUpperCase()));

					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					header.setCreatedOn(convertNow);
					header.setCreatedBy("SYSTEM");

					header.setLineItems(lineItems);
					lineItems.forEach(item -> {
						item.setDocument(header);
					});

					headerList.add(header);
				}
			}
		}

		return headerList;
	}

}
