package com.ey.advisory.app.gstr2jsonupload;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aCdnaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aIsdInvoicesHeaderEntity;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.core.api.APIConstants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Service("Gstr2JsonParserImpl")
public class Gstr2JsonParserImpl implements Gstr2JsonParser {

	DateFormat formatter = new SimpleDateFormat("MMyyyy");
	DateFormat dateFormat = new SimpleDateFormat("yyyyMM");
	
	/*
	 * Method for B2B
	 *
	 */

	@Override
	public List<GetGstr2aB2bInvoicesHeaderEntity> parseB2bData(Header dto,
			String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr2aB2bInvoicesHeaderEntity> invoiceList = new ArrayList<>();

		JsonArray respObject = null;
		try {
			respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
					.get(APIConstants.B2B).getAsJsonArray();

			Type listType = new TypeToken<List<B2b>>() {
			}.getType();
			List<B2b> baseEntity = gson.fromJson(respObject, listType);
			for (B2b eachInv : baseEntity) {
				for (B2bInvoice eachInvData : eachInv.getInv()) {
					List<GetGstr2aB2bInvoicesItemEntity> itemList = new ArrayList<>();
					GetGstr2aB2bInvoicesHeaderEntity invoice = setInvoiceData(
							eachInv, eachInvData, dto);
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (B2bItem lineItem : eachInvData.getItms()) {
						GetGstr2aB2bInvoicesItemEntity item = setItemData(
								lineItem, dto);
						itemList.add(item);

						B2bItemDetails detail = lineItem.getItmDet();
						BigDecimal detailsIgst = detail.getIamt() != null
								? detail.getIamt() : BigDecimal.ZERO;
						BigDecimal detailsCgst = detail.getCamt() != null
								? detail.getCamt() : BigDecimal.ZERO;
						BigDecimal detailsSgst = detail.getSamt() != null
								? detail.getSamt() : BigDecimal.ZERO;
						BigDecimal detailsCess = detail.getCsamt() != null
								? detail.getCsamt() : BigDecimal.ZERO;
						BigDecimal detailsTaxable = detail.getTxval() != null
								? detail.getTxval() : BigDecimal.ZERO;

						igst = igst.add(detailsIgst);
						cgst = cgst.add(detailsCgst);
						sgst = sgst.add(detailsSgst);
						cess = cess.add(detailsCess);
						taxable = taxable.add(detailsTaxable);
					}
					/**
					 * setting summary rate info at invoice level.
					 */
					invoice.setIgstAmt(igst);
					invoice.setCgstAmt(cgst);
					invoice.setSgstAmt(sgst);
					invoice.setCessAmt(cess);
					invoice.setTaxable(taxable);
					invoice.setLineItems(itemList);
					invoice.setAction(APIConstants.N);
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a B2B response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetGstr2aB2bInvoicesHeaderEntity setInvoiceData(B2b eachInv,
			B2bInvoice eachInvData, Header dto) {
		
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		GetGstr2aB2bInvoicesHeaderEntity invoice = 
				new GetGstr2aB2bInvoicesHeaderEntity();
		try {

			String cfs = eachInv.getCfs();
			String sgstin = eachInv.getCtin();
			String cName = eachInv.getCname();
			String checkSum = eachInvData.getChksum();
			String invoiceNumber = eachInvData.getInum();
			String invDate = eachInvData.getIdt();
			BigDecimal invoiceValue = eachInvData.getVal();
			String pos = eachInvData.getPos();
			String reverseCharge = eachInvData.getRchrg();
			String invoiceType = eachInvData.getInvTyp();

			/**
			 * setting B2BInvoices
			 */
			invoice.setCfsGstr1(cfs);
			invoice.setSgstin(sgstin);
			invoice.setSupTradeName(cName);
			/**
			 * setting B2BInvoiceData
			 */
			invoice.setChkSum(checkSum);
			invoice.setInvNum(invoiceNumber);

			if (invDate != null && invDate.trim().length() > 0) {
				invoice.setInvDate(LocalDate.parse(invDate,
						DateUtil.SUPPORTED_DATE_FORMAT2));
			} else {
				invoice.setInvDate(null);
			}

			invoice.setInvValue(invoiceValue);
			invoice.setPos(pos);
			invoice.setRchrg(reverseCharge);
			invoice.setInvType(invoiceType);
			Date date = (Date) formatter.parse(dto.getFp());
			String reqDate = dateFormat.format(date);
			invoice.setDerReturnPeriod(Integer.parseInt(reqDate));

			invoice.setCgstin(dto.getGstin());
			invoice.setReturnPeriod(dto.getFp());
			invoice.setDelete(false);

			invoice.setModifiedBy(userName);
			invoice.setCreatedBy(userName);
			 
			LocalDateTime Localdate = LocalDateTime.now();
			invoice.setModifiedOn(Localdate);
			invoice.setCreatedOn(Localdate);

		} catch (ParseException e) {
			e.printStackTrace();
		}
		return invoice;
	}

	private GetGstr2aB2bInvoicesItemEntity setItemData(B2bItem lineItem,
			Header dto) {
		GetGstr2aB2bInvoicesItemEntity item = new GetGstr2aB2bInvoicesItemEntity();
		try {
			item.setItmNum(lineItem.getNum());
			B2bItemDetails detail = lineItem.getItmDet();
			item.setTaxRate(detail.getRt() != null ? detail.getRt()
					: BigDecimal.ZERO);

			Date date = (Date) formatter.parse(dto.getFp());
			String reqDate = dateFormat.format(date);
			item.setDerReturnPeriod(Integer.parseInt(reqDate));

			item.setTaxableValue(detail.getTxval() != null ? detail.getTxval()
					: BigDecimal.ZERO);
			item.setIgstAmt(detail.getIamt() != null ? detail.getIamt()
					: BigDecimal.ZERO);
			item.setCgstAmt(detail.getCamt() != null ? detail.getCamt()
					: BigDecimal.ZERO);
			item.setSgstAmt(detail.getSamt() != null ? detail.getSamt()
					: BigDecimal.ZERO);
			item.setCessAmt(detail.getCsamt() != null ? detail.getCsamt()
					: BigDecimal.ZERO);
			
		} catch (Exception ex) {
			String msg = "failed to set data in  Gstr2a B2B Line Item response";
			LOGGER.error(msg, ex);
		}
		return item;
	}
	
	/*
	 * Method for B2BA
	 *
	 */

	@Override
	public List<GetGstr2aB2baInvoicesHeaderEntity> parseB2baData(Header dto,
			String apiResp) {
		
		Gson gson = GsonUtil.newSAPGsonInstance();
		List<GetGstr2aB2baInvoicesHeaderEntity> invoiceList = new ArrayList<>();

		JsonArray respObject = null;
		try {
			
				respObject = (new JsonParser()).parse(apiResp).getAsJsonObject()
						.get(APIConstants.B2BA).getAsJsonArray();
			
			Type listType = new TypeToken<List<B2bA>>() {
			}.getType();
			List<B2bA> baseEntity = gson.fromJson(respObject, listType);
			for (B2bA eachInv : baseEntity) {
				for (B2bAInvoice eachInvData : eachInv.getInv()) {
					List<GetGstr2aB2baInvoicesItemEntity> itemList = 
							new ArrayList<>();
					GetGstr2aB2baInvoicesHeaderEntity invoice = setInvoiceB2baData(
							eachInv, eachInvData, dto);
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO;
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxable = BigDecimal.ZERO;
					for (B2bAItem lineItem : eachInvData.getItms()) {
						GetGstr2aB2baInvoicesItemEntity item = setItemB2baData(
								lineItem, dto);
						itemList.add(item);
						B2bAItemDetails detail = lineItem.getItmDet();
						BigDecimal detailsIgst = detail.getIamt() != null
								? detail.getIamt() : BigDecimal.ZERO;
						BigDecimal detailsCgst = detail.getCamt() != null
								? detail.getCamt() : BigDecimal.ZERO;
						BigDecimal detailsSgst = detail.getSamt() != null
								? detail.getSamt() : BigDecimal.ZERO;
						BigDecimal detailsCess = detail.getCsamt() != null
								? detail.getCsamt() : BigDecimal.ZERO;
						BigDecimal detailsTaxable = detail
								.getTxval()!= null	? detail.getTxval()
										: BigDecimal.ZERO;
						
						igst = igst.add(detailsIgst);
						cgst = cgst.add(detailsCgst);
						sgst = sgst.add(detailsSgst);
						cess = cess.add(detailsCess);
						taxable = taxable.add(detailsTaxable);
					}
					/**
					 * setting summary rate info at invoice level.
					 */
					invoice.setIgstAmt(igst);
					invoice.setCgstAmt(cgst);
					invoice.setSgstAmt(sgst);
					invoice.setCessAmt(cess);
					invoice.setTaxable(taxable);
					invoice.setLineItems(itemList);
					invoice.setAction(APIConstants.N);
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a B2BA response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetGstr2aB2baInvoicesItemEntity setItemB2baData(
			B2bAItem lineItem , Header dto) {
		GetGstr2aB2baInvoicesItemEntity item = new 
				GetGstr2aB2baInvoicesItemEntity();
		try {
		item.setItmNum(lineItem.getNum());
		B2bAItemDetails detail = lineItem.getItmDet();
		item.setTaxRate(detail.getRt() != null ? detail.getRt()
				: BigDecimal.ZERO);
		item.setTaxableValue(detail.getTxval() != null ? detail.getTxval()
				: BigDecimal.ZERO);
		item.setIgstAmt(detail.getIamt() != null ? detail.getIamt()
				: BigDecimal.ZERO);
		item.setCgstAmt(detail.getCamt() != null ? detail.getCamt()
				: BigDecimal.ZERO);
		item.setSgstAmt(detail.getSamt() != null ? detail.getSamt()
				: BigDecimal.ZERO);
		item.setCessAmt(detail.getCsamt() != null ? detail.getCsamt()
				: BigDecimal.ZERO);
		
		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		item.setDerReturnPeriod(Integer.parseInt(reqDate));
		} catch (Exception ex) {
			String msg = "failed to set data in  Gstr2a B2BA LineItem ";
			LOGGER.error(msg, ex);
		}
		return item;
	}

	private GetGstr2aB2baInvoicesHeaderEntity setInvoiceB2baData(
			B2bA eachInv, B2bAInvoice eachInvData,
			Header dto) {
		
		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		
		GetGstr2aB2baInvoicesHeaderEntity invoice = new 
				GetGstr2aB2baInvoicesHeaderEntity();
		try {
		String cfs = eachInv.getCfs();
		String sgstin = eachInv.getCtin();
		String checkSum = eachInvData.getChksum();
		String invoiceNumber = eachInvData.getInum();
		String invDate = eachInvData.getIdt();
		String origInvNumber = eachInvData.getOinum();
		String origInvDate = eachInvData.getOidt();
		BigDecimal invoiceValue = eachInvData.getVal();
		String pos = eachInvData.getPos();
		String reverseCharge = eachInvData.getRchrg();
		String invoiceType = eachInvData.getInvTyp();
		
		/**
		 * setting B2bAInvoice
		 */
		invoice.setCfsGstr1(cfs);
		invoice.setSgstin(sgstin); 
		
		/**
		 * setting B2bInvoiceData
		 */
		invoice.setChkSum(checkSum);
		invoice.setInvNum(invoiceNumber);
		if (invDate != null && invDate.trim().length() > 0) {
			invoice.setInvDate(LocalDate.parse(invDate,
					DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			invoice.setInvDate(null);
		}
		invoice.setOrigInvNum(origInvNumber);

		if (origInvDate != null && origInvDate.trim().length() > 0) {
			invoice.setOrigInvDate(LocalDate.parse(origInvDate,
					DateUtil.SUPPORTED_DATE_FORMAT2));
		} else {
			invoice.setOrigInvDate(null);
		}

		invoice.setInvValue(invoiceValue);

		if (pos != null && pos.trim().length() > 0) {
			invoice.setPos(pos);
		} else {
			invoice.setPos(null);
		}
		invoice.setRchrg(reverseCharge);
		invoice.setInvType(invoiceType);
	
		invoice.setCgstin(dto.getGstin());
		invoice.setReturnPeriod(dto.getFp());
		invoice.setDelete(false);
		
		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		invoice.setDerReturnPeriod(Integer.parseInt(reqDate));
		
		invoice.setModifiedBy(userName);
		invoice.setCreatedBy(userName);
		 
		LocalDateTime Localdate = LocalDateTime.now();
		invoice.setModifiedOn(Localdate);
		invoice.setCreatedOn(Localdate);
		
		} catch (Exception ex) {
			String msg = "failed to set data in Gstr2a B2BA header";
			LOGGER.error(msg, ex);
		}
		return invoice;

	}

	/*
	 * Method for CDN
	 *
	 */
	
	@Override
	public List<GetGstr2aCdnInvoicesHeaderEntity> parseCdnData(Header dto,
			String apiResp) {
		List<GetGstr2aCdnInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		Type listType = null;
		try {
				respObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.CDN).getAsJsonArray();

			listType = new TypeToken<List<Cdn>>() {
			}.getType();

			List<Cdn> baseEntity = gson.fromJson(respObject, listType);
			for (Cdn eachInv : baseEntity) {
				for (CdnNt creddebnote : eachInv.getNt()) {
			List<GetGstr2aCdnInvoicesItemEntity> itemList = new ArrayList<>();
				GetGstr2aCdnInvoicesHeaderEntity invoice = setInvoiceCdnData(
							eachInv, creddebnote, dto);
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO; 
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxVal = BigDecimal.ZERO;
					for (CdnItem lineItem : creddebnote.getItms()) {
						GetGstr2aCdnInvoicesItemEntity item = setItemCdnData(
								lineItem, dto);
						itemList.add(item);
						CdnItemDetails detail = lineItem.getItmDet();
						BigDecimal detailsIgst = detail.getIamt() != null
								? detail.getIamt() : BigDecimal.ZERO;
						BigDecimal detailsCgst = detail.getCamt() != null
								? detail.getCamt() : BigDecimal.ZERO;
						BigDecimal detailsSgst = detail.getSamt() != null
								? detail.getSamt() : BigDecimal.ZERO;
						BigDecimal detailsCess = detail.getCsamt() != null
								? detail.getIamt() : BigDecimal.ZERO;
						BigDecimal detailsTaxVal = (detail.getTxval() != null 
								? detail.getTxval() : BigDecimal.ZERO);
						
						igst = igst.add(detailsIgst);
						cgst = cgst.add(detailsCgst);
						sgst = sgst.add(detailsSgst);
						cess = cess.add(detailsCess);
						taxVal = taxVal.add(detailsTaxVal);
					}
					/**
					 * setting summary rate info at invoice level.
					 */
					invoice.setIgstamt(igst);
					invoice.setCgstamt(cgst);
					invoice.setSgstamt(sgst);
					invoice.setCessamt(cess);
					invoice.setTaxVal(taxVal);
					invoice.setLineItems(itemList);
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a CDN response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}
	
	private GetGstr2aCdnInvoicesHeaderEntity setInvoiceCdnData(Cdn eachInv, 
			        CdnNt creddebnote, Header dto) {
		GetGstr2aCdnInvoicesHeaderEntity invoice = 
				new GetGstr2aCdnInvoicesHeaderEntity();
		try {
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
		String cfs = eachInv.getCfs();
		String cgstin = eachInv.getCtin();
		String checkSum = creddebnote.getChksum();
		String credDebRefVoucher = creddebnote.getNtty();
		String credDebRefVoucherNum = creddebnote.getNtNum();
		String credDebRefVoucherDate = creddebnote.getNtDt();
		String invNum = creddebnote.getInum();
		String invDate = creddebnote.getIdt();
		String preGst = creddebnote.getPGst();
		BigDecimal noteVal = creddebnote.getVal();
		
		/**
		 * setting CDNInvoices
		 */
		invoice.setGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getFp());
		invoice.setCfsGstr1(cfs);
		invoice.setCountergstin(cgstin);
		/**
		 * setting CredDebNotes
		 */
		
		invoice.setCheckSum(checkSum);
		invoice.setCredDebRefVoucher(credDebRefVoucher);
		invoice.setCredDebRefVoucherNum(credDebRefVoucherNum);
		if (credDebRefVoucherDate != null
				&& credDebRefVoucherDate.trim().length() > 0) {
			invoice.setCredDebRefVoucherDate(
					LocalDate.parse(credDebRefVoucherDate,
							DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setCredDebRefVoucherDate(null);
		}
		
		if (invDate != null
				&& invDate.trim().length() > 0) {
			invoice.setInvDate(LocalDate.parse(invDate,
							DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setInvDate(null);
		}


		invoice.setNotevalue(noteVal);
		invoice.setInvNum(invNum);
		invoice.setPreGst(preGst);
		invoice.setDelete(false);

		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		invoice.setDerReturnPeriod(Integer.parseInt(reqDate));
		
		invoice.setModifiedBy(userName);
		invoice.setCreatedBy(userName);
		 
		LocalDateTime Localdate = LocalDateTime.now();
		invoice.setModifiedOn(Localdate);
		invoice.setCreatedOn(Localdate);
		
		} catch (Exception ex) {
			String msg = "failed to set data in Gstr2a CDN header";
			LOGGER.error(msg, ex);
		}
		return invoice;

	}

	private GetGstr2aCdnInvoicesItemEntity setItemCdnData(
			CdnItem lineItem, Header dto) {
		GetGstr2aCdnInvoicesItemEntity item = 
				new GetGstr2aCdnInvoicesItemEntity();
		
		try{
		item.setItemnum(lineItem.getNum());
		CdnItemDetails detail = lineItem.getItmDet();
		item.setTaxval(detail.getTxval() != null ? detail.getTxval()
				: BigDecimal.ZERO);
		item.setTaxrate(detail.getRt() != null ? detail.getRt()
				: BigDecimal.ZERO);
		item.setIgstamt(detail.getIamt() != null ? detail.getIamt() 
				: BigDecimal.ZERO);
		item.setCgstamt(detail.getCamt() != null ? detail.getCamt() 
				: BigDecimal.ZERO );
		item.setSgstamt(detail.getSamt() != null ? detail.getSamt() 
				: BigDecimal.ZERO);
		item.setCessamt(detail.getCsamt() != null ? detail.getCsamt() 
				: BigDecimal.ZERO);
		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		item.setDerReturnPeriod(Integer.parseInt(reqDate));
		
		} catch (Exception ex) {
			String msg = "failed to set data in  Gstr2a B2BA LineItem ";
			LOGGER.error(msg, ex);
		}
		return item;
	}
	
/*--------------------------------------------------------------------------*/	
	
	
	/*
	 * Method for CDNA
	 *
	 */

	@Override
	public List<GetGstr2aCdnaInvoicesHeaderEntity> parseCdnAData(Header dto,
			String apiResp) {
		List<GetGstr2aCdnaInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		Type listType = null;
		try {
				respObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
						.get(APIConstants.CDNA).getAsJsonArray();

			listType = new TypeToken<List<CdnA>>() {
			}.getType();

			List<CdnA> baseEntity = gson.fromJson(respObject, listType);
			for (CdnA eachInv : baseEntity) {
				for (CdnANt creddebnote : eachInv.getNt()) {
					List<GetGstr2aCdnaInvoicesItemEntity> itemList = 
							new ArrayList<>();
					GetGstr2aCdnaInvoicesHeaderEntity invoice = setInvoiceCdnaData(
							eachInv, creddebnote, dto);
					invoiceList.add(invoice);
					BigDecimal igst = BigDecimal.ZERO; 
					BigDecimal cgst = BigDecimal.ZERO;
					BigDecimal sgst = BigDecimal.ZERO;
					BigDecimal cess = BigDecimal.ZERO;
					BigDecimal taxVal = BigDecimal.ZERO;

					for (CdnAItem lineItem : creddebnote.getItms()) {
						GetGstr2aCdnaInvoicesItemEntity item = setItemCdnaData(
								lineItem, dto);
						itemList.add(item);
						CdnAItemDetails detail = lineItem.getItmDet();
						
						BigDecimal detailsIgst = detail.getIamt() != null
								? detail.getIamt() : BigDecimal.ZERO;
						BigDecimal detailsCgst = detail.getCamt() != null
								? detail.getCamt() : BigDecimal.ZERO;
						BigDecimal detailsSgst = detail.getSamt() != null
								? detail.getSamt() : BigDecimal.ZERO;
						BigDecimal detailsCess = detail.getCsamt() != null
								? detail.getCsamt() : BigDecimal.ZERO;
						BigDecimal detailsTaxVal = detail.getTxval() != null
										? detail.getTxval() : BigDecimal.ZERO;
						
						igst = igst.add(detailsIgst);
						cgst = cgst.add(detailsCgst);
						sgst = sgst.add(detailsSgst);
						cess = cess.add(detailsCess);
						taxVal = taxVal.add(detailsTaxVal);
					}
					/*
					 * setting summary rate info at invoice level.
					 */
					invoice.setIgstamt(igst);
					invoice.setCgstamt(cgst);
					invoice.setSgstamt(sgst);
					invoice.setCessamt(cess);
					invoice.setTaxVal(taxVal);
					invoice.setLineItems(itemList);
					itemList.forEach(item -> {
						item.setHeader(invoice);
					});

				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a CDNA response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}
	
	private GetGstr2aCdnaInvoicesItemEntity setItemCdnaData(CdnAItem 
			lineItem, Header dto) {
		GetGstr2aCdnaInvoicesItemEntity item = 
				new GetGstr2aCdnaInvoicesItemEntity();
		try {
		item.setItemnum(lineItem.getNum());
		CdnAItemDetails detail = lineItem.getItmDet();
		item.setTaxval(detail.getTxval() != null ? detail.getTxval()
				: BigDecimal.ZERO);
		item.setTaxrate(
				detail.getRt() != null ? detail.getRt() : BigDecimal.ZERO);
		item.setIgstamt(
				detail.getIamt() != null ? detail.getIamt() : BigDecimal.ZERO);
		item.setCgstamt(
				detail.getCamt() != null ? detail.getCamt() : BigDecimal.ZERO);
		item.setSgstamt(
				detail.getSamt() != null ? detail.getSamt() : BigDecimal.ZERO);
		item.setCessamt(detail.getCsamt() != null ? detail.getCsamt()
				: BigDecimal.ZERO);
		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		item.setDerReturnPeriod(Integer.parseInt(reqDate));
		
		} catch (Exception ex) {
			String msg = "failed to set data in  Gstr2a B2BA LineItem ";
			LOGGER.error(msg, ex);
		}
		return item;

	}

	private GetGstr2aCdnaInvoicesHeaderEntity setInvoiceCdnaData(
			CdnA eachInv, CdnANt creddebnote, Header dto) {
		GetGstr2aCdnaInvoicesHeaderEntity invoice = 
				new GetGstr2aCdnaInvoicesHeaderEntity();
		
		try {
			
			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";
			
		String cfs = eachInv.getCfs();
		String Sgstin = eachInv.getCtin();
		String checkSum = creddebnote.getChksum();
		String credDebRefVoucherNum = creddebnote.getNtNum();
		String oriCredDebNum = creddebnote.getOntNum();
		String credDebRefVoucherDate = creddebnote.getNtDt();
		String oriCredDebDate = creddebnote.getOntDt();
		String invNum = creddebnote.getInum();
		String preGst = creddebnote.getPGst();
		BigDecimal noteVal = creddebnote.getVal();
		String noteType = creddebnote.getNtty();
		String invDate = creddebnote.getIdt();
		
		
		invoice.setGstin(dto.getGstin());
		invoice.setTaxPeriod(dto.getFp());
		
		/**
		 * setting CDNInvoices
		 */
		invoice.setCfsGstr1(cfs);
		invoice.setCountergstin(Sgstin);
		/**
		 * setting CredDebNotes
		 */
		invoice.setCheckSum(checkSum);
		invoice.setCredDebRefVoucherNum(credDebRefVoucherNum);
		invoice.setOriCredDebNum(oriCredDebNum);
		if (credDebRefVoucherDate != null
				&& credDebRefVoucherDate.trim().length() > 0) {
			invoice.setCredDebRefVoucherDate(
					LocalDate.parse(credDebRefVoucherDate,
							DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setCredDebRefVoucherDate(null);
		}

		if (oriCredDebDate != null && oriCredDebDate.trim().length() > 0) {
			invoice.setOriCredDebDate(LocalDate.parse(oriCredDebDate,
					DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setOriCredDebDate(null);
		}
		
		if (invDate != null && invDate.trim().length() > 0) {
			invoice.setInvDate(LocalDate.parse(invDate,
					DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		} else {
			invoice.setInvDate(null);
		}
		invoice.setCredDebRefVoucher(noteType);
		invoice.setPreGst(preGst);
		invoice.setInvNum(invNum);
		invoice.setNotevalue(noteVal);
		invoice.setDelete(false);
		Date date = (Date) formatter.parse(dto.getFp());
		String reqDate = dateFormat.format(date);
		invoice.setDerReturnPeriod(Integer.parseInt(reqDate));
		
		invoice.setModifiedBy(userName);
		invoice.setCreatedBy(userName);
		 
		LocalDateTime Localdate = LocalDateTime.now();
		invoice.setModifiedOn(Localdate);
		invoice.setCreatedOn(Localdate);
		
		} catch (Exception ex) {
			String msg = "failed to set data in Gstr2a CDNA header";
			LOGGER.error(msg, ex);
		}
		return invoice;
	}

	/*
	 * Method for ISD
	 *
	 */

	@Override
	public List<GetGstr2aIsdInvoicesHeaderEntity> parseIsdData(Header dto,
			String apiResp) {

		List<GetGstr2aIsdInvoicesHeaderEntity> invoiceList = new ArrayList<>();
		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray jsonObject = null;
		Type listType = null;
		try {
			jsonObject = (new JsonParser().parse(apiResp)).getAsJsonObject()
					.get(APIConstants.ISD).getAsJsonArray();

			listType = new TypeToken<List<Isd>>() {
			}.getType();

			List<Isd> baseEntities = gson.fromJson(jsonObject, listType);
			for (Isd eachInv : baseEntities) {
				for (IsdDocList eachInvData : eachInv.getDoclist()) {
					GetGstr2aIsdInvoicesHeaderEntity invoice = setInvoiceIsdData(
							eachInv, eachInvData, dto);
					invoiceList.add(invoice);
				}
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a ISD response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private GetGstr2aIsdInvoicesHeaderEntity setInvoiceIsdData(Isd eachInv,
			IsdDocList eachInvData, Header dto) {

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		GetGstr2aIsdInvoicesHeaderEntity invoice = new GetGstr2aIsdInvoicesHeaderEntity();
		try {
			String cfs = eachInv.getCfs();
			String sgstin = eachInv.getCtin();
			String checkSum = eachInvData.getChksum();
			String invDate = eachInvData.getDocdt();
			String invNum = eachInvData.getDocnum();
			String invoiceType = eachInvData.getIsdDocty();
			String itcElg = eachInvData.getItcElg();
			String cgstin = dto.getGstin();
	
			invoice.setCounFillStatus(cfs);
			invoice.setCgstin(sgstin);
			invoice.setGstin(cgstin);
			invoice.setCheckSum(checkSum);
			invoice.setDocumentNumber(invNum);
			if (invDate != null && invDate.trim().length() > 0) {
				invoice.setDocumentDate(LocalDate.parse(invDate,
						DateUtil.SUPPORTED_DATE_FORMAT2));
			} else {
				invoice.setDocumentDate(null);
			}
			invoice.setItcElg(itcElg);
			invoice.setIsdDocumentType(invoiceType);

			invoice.setCgstin(dto.getGstin());
			invoice.setReturnPeriod(dto.getFp());
			invoice.setDelete(false);

			Date date = (Date) formatter.parse(dto.getFp());
			String reqDate = dateFormat.format(date);
			invoice.setDerReturnPeriod(Integer.parseInt(reqDate));

			invoice.setModifiedBy(userName);
			invoice.setCreatedBy(userName);

			LocalDateTime Localdate = LocalDateTime.now();
			invoice.setModifiedOn(Localdate);
			invoice.setCreatedOn(Localdate);
			
			BigDecimal detailsIgst = eachInvData.getIamt() != null
					? eachInvData.getIamt() : BigDecimal.ZERO;
			BigDecimal detailsCgst = eachInvData.getCamt() != null
					? eachInvData.getCamt() : BigDecimal.ZERO;
			BigDecimal detailsSgst = eachInvData.getSamt() != null
					? eachInvData.getSamt() : BigDecimal.ZERO;
			BigDecimal detailsCess = eachInvData.getCess() != null
					? eachInvData.getCess() : BigDecimal.ZERO;
	
			invoice.setIgstamt(detailsIgst);
			invoice.setCgstamt(detailsCgst);
			invoice.setSgstamt(detailsSgst);
			invoice.setCesamt(detailsCess);

		} catch (Exception ex) {
			String msg = "failed to set data in Gstr2a ISD header";
			LOGGER.error(msg, ex);
		}
		return invoice;

	}
}
