package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.EinvGstinClientEntity;
import com.ey.advisory.app.data.repositories.client.EinvClientGstinRepository;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("Gstr2BParseServiceDefault")
public class Gstr2BParseServiceDefault {

	private static final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	private static final String DOC_KEY_JOINER = "|";

	@Autowired
	private EinvClientGstinRepository einvClientGstinRepo;

	// B2b data Parsing
	public List<Gstr2GetGstr2BLinkingB2bHeaderEntity> parseB2bData(
			Gstr2BGETDataDto dto, List<B2BDocuments> b2bSummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BLinkingB2bHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (B2BDocuments eachInv : b2bSummary) {

				List<Gstr2GetGstr2BLinkingB2bHeaderEntity> list = setB2bInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a B2B response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private LocalDate convertDate(String date) {
		LocalDate localDate = null;
		try {
			localDate = LocalDate.parse(date, formatter);
		} catch (Exception ee) {
			LOGGER.warn("Error while converting date, {}", date);
		}
		return localDate;
	}

	private List<Gstr2GetGstr2BLinkingB2bHeaderEntity> setB2bInvoiceData(
			B2BDocuments eachInv, Gstr2BGETDataDto dto, LocalDateTime genDate,
			Long invocationId, String status, boolean isDelete) {

		List<Gstr2GetGstr2BLinkingB2bItemEntity> itemEntityList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingB2bHeaderEntity> invoiceEntityList = new ArrayList<>();
		boolean isFlag = true;
		EinvGstinClientEntity entity = null;
		try {

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime modifiedOn = now;

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String checksum = dto.getChecksum();
			String rGstin = dto.getDetailedData().getRGstin();
			String version = dto.getDetailedData().getVersion();
			String taxPeriod = dto.getDetailedData().getTaxpeiod();

			String gstr1FilingDate = eachInv.getGstr1FilingDate();
			String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
			String suppGstin = eachInv.getSuppGstin();
			String suppName = eachInv.getSuppName();
			List<B2BInvoiceList> b2bInvoiceData = eachInv.getB2bInvoiceData();

			for (B2BInvoiceList b2bInvoiceList : b2bInvoiceData) {
				Gstr2GetGstr2BLinkingB2bHeaderEntity invoice = new Gstr2GetGstr2BLinkingB2bHeaderEntity();
				BigDecimal diffPrcnt = b2bInvoiceList.getDiffPrcnt();
				String invoiceDate = b2bInvoiceList.getInvoiceDate();
				String invoiceNumber = b2bInvoiceList.getInvoiceNumber();
				String invoiceType = b2bInvoiceList.getInvoiceType();
				BigDecimal invoiceValue = b2bInvoiceList.getInvoiceValue();
				String itcAvail = b2bInvoiceList.getItcAvail();
				String pos = b2bInvoiceList.getPos();
				String reasonForItcUnAvail = b2bInvoiceList
						.getReasonForItcUnAvail();
				String suppRevChrg = b2bInvoiceList.getSuppRevChrg();
				List<Item> itemList = b2bInvoiceList.getB2bInvItems();
				String irn = b2bInvoiceList.getIrn();
				String irnGenDate = b2bInvoiceList.getIrngendate();
				String irnSrcType = b2bInvoiceList.getSourceType();
				String imsStatus = b2bInvoiceList.getImsStatus();
				String remarks = b2bInvoiceList.getRemarks();
				
				invoice.setRGstin(rGstin);
				invoice.setTaxPeriod(taxPeriod);
				invoice.setVersion(version);
				invoice.setGenDate(genDate);
				invoice.setModifiedOn(modifiedOn);
				invoice.setModifiedBy(userName);
				invoice.setIsDelete(isDelete);
				invoice.setChecksum(checksum);
				invoice.setCreatedBy(userName);
				invoice.setCreatedOn(now);
				invoice.setInvocationId(invocationId);
				invoice.setStatus(status);
				invoice.setIrnNo(irn);
				invoice.setIrnGenDate(
						irnGenDate != null ? convertDate(irnGenDate) : null);
				invoice.setIrnSrcType(irnSrcType);
				invoice.setDiffPercent(diffPrcnt);
				if (invoiceDate != null) {
					LocalDate invDateLocal = LocalDate.parse(invoiceDate,
							formatter);
					LocalDateTime invDate = invDateLocal.atStartOfDay();
					invoice.setInvoiceDate(invDate);
					invoice.setDocKey(generateDocKey(invDateLocal, rGstin,
							suppGstin, invoiceType, invoiceNumber));

					// adding linking key
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(invoiceDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "INV", invoiceNumber));
				}

				invoice.setRev(suppRevChrg);
				invoice.setRsn(reasonForItcUnAvail);
				invoice.setSGstin(suppGstin);
				invoice.setPos(pos);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setInvoiceType(invoiceType);
				invoice.setItcAvailable(itcAvail);
				invoice.setImsStatus(imsStatus);
				invoice.setRemarks(remarks);
				if (dto.isItcRejected()) {
					invoice.setItcRejected(true);
				}

				if (gstr1FilingDate != null) {
					LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
							formatter);
					LocalDateTime gstr1Date = gstr1DateLocal.atStartOfDay();
					invoice.setSupFilingDate(gstr1Date);
				}

				invoice.setSupFilingPeriod(gstr1FilingPeriod);
				invoice.setSupInvoiceValue(invoiceValue);
				invoice.setSupTradeName(suppName);

				if (itemList != null && !itemList.isEmpty()) {
					for (Item item : itemList) {
						Gstr2GetGstr2BLinkingB2bItemEntity itemEntity = new Gstr2GetGstr2BLinkingB2bItemEntity();
						BigDecimal cess = item.getCess();
						BigDecimal cgst = item.getCgst();
						BigDecimal igst = item.getIgst();
						Integer itemNum = item.getItemNum();
						BigDecimal rate = item.getRate();
						BigDecimal sgst = item.getSgst();
						BigDecimal totalTaxableVal = item.getTotalTaxableVal();

						itemEntity.setCessAmt(cess);
						itemEntity.setCgstAmt(cgst);
						itemEntity.setHeader(invoice);
						itemEntity.setIgstAmt(igst);
						itemEntity.setItemNumber(itemNum);
						itemEntity.setSgstAmt(sgst);
						itemEntity.setTaxableValue(totalTaxableVal);
						itemEntity.setTaxRate(rate);
						itemEntityList.add(itemEntity);
					}
				} else {
					Gstr2GetGstr2BLinkingB2bItemEntity itemEntity = new Gstr2GetGstr2BLinkingB2bItemEntity();
					itemEntity.setCgstAmt(b2bInvoiceList.getCgst());
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(b2bInvoiceList.getIgst());
					itemEntity.setItemNumber(Integer.parseInt("1"));
					itemEntity.setSgstAmt(b2bInvoiceList.getSgst());
					itemEntity.setTaxableValue(
							b2bInvoiceList.getTotalTaxableVal());
					itemEntity.setCessAmt(b2bInvoiceList.getCess());
					itemEntity.setTaxRate(null);
					itemEntityList.add(itemEntity);
				}
				invoice.setLineItems(itemEntityList);
				invoiceEntityList.add(invoice);

			}
		} catch (Exception e) {
			LOGGER.error("Error occured parsing GSTR2B get B2B invoice", e);
		}
		return invoiceEntityList;
	}

	// B2BA parsing
	public List<Gstr2GetGstr2BLinkingB2baHeaderEntity> parseB2bAData(
			Gstr2BGETDataDto dto, List<B2BADocuments> b2bASummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BLinkingB2baHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (B2BADocuments eachInv : b2bASummary) {

				List<Gstr2GetGstr2BLinkingB2baHeaderEntity> list = setB2bAInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a B2BA response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private List<Gstr2GetGstr2BLinkingB2baHeaderEntity> setB2bAInvoiceData(
			B2BADocuments eachInv, Gstr2BGETDataDto dto, LocalDateTime genDate,
			Long invocationId, String status, boolean isDelete) {

		List<Gstr2GetGstr2BLinkingB2baItemEntity> itemEntityList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingB2baHeaderEntity> invoiceEntityList = new ArrayList<>();
		boolean isFlag = true;
		EinvGstinClientEntity entity = null;
		try {

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime modifiedOn = now;

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String checksum = dto.getChecksum();
			String rGstin = dto.getDetailedData().getRGstin();
			String version = dto.getDetailedData().getVersion();
			String taxPeriod = dto.getDetailedData().getTaxpeiod();

			String gstr1FilingDate = eachInv.getGstr1FilingDate();
			String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
			String suppGstin = eachInv.getSuppGstin();
			String suppName = eachInv.getSuppName();

			List<B2BAInvoiceList> b2bInvoiceData = eachInv.getB2bAInvoiceData();

			for (B2BAInvoiceList b2bInvoiceList : b2bInvoiceData) {
				Gstr2GetGstr2BLinkingB2baHeaderEntity invoice = new Gstr2GetGstr2BLinkingB2baHeaderEntity();

				BigDecimal diffPrcnt = b2bInvoiceList.getDiffPrcnt();
				String invoiceDate = b2bInvoiceList.getInvoiceDate();
				String invoiceNumber = b2bInvoiceList.getInvoiceNumber();
				String invoiceType = b2bInvoiceList.getInvoiceType();
				BigDecimal invoiceValue = b2bInvoiceList.getInvoiceValue();
				String itcAvail = b2bInvoiceList.getItcAvail();
				String pos = b2bInvoiceList.getPos();
				String reasonForItcUnAvail = b2bInvoiceList
						.getReasonForItcUnAvail();
				String suppRevChrg = b2bInvoiceList.getSuppRevChrg();
				String orgInvDate = b2bInvoiceList.getOrgInvDate();
				String orgInvNum = b2bInvoiceList.getOrgInvNum();
				String itcRedReq = b2bInvoiceList.getItcRedReq();
				BigDecimal declIgst = b2bInvoiceList.getDeclIgst();
				BigDecimal declSgst = b2bInvoiceList.getDeclSgst();
				BigDecimal declCgst = b2bInvoiceList.getDeclCgst();
				BigDecimal declCess = b2bInvoiceList.getDeclCess();
				String imsStatus = b2bInvoiceList.getImsStatus();
				String remarks = b2bInvoiceList.getRemarks();

				List<Item> itemList = b2bInvoiceList.getB2bAInvItems();

				invoice.setRGstin(rGstin);
				invoice.setTaxPeriod(taxPeriod);
				invoice.setVersion(version);
				invoice.setGenDate(genDate);
				invoice.setModifiedOn(modifiedOn);
				invoice.setModifiedBy(userName);
				invoice.setIsDelete(isDelete);
				invoice.setChecksum(checksum);
				invoice.setCreatedBy(userName);
				invoice.setCreatedOn(now);
				invoice.setInvocationId(invocationId);
				invoice.setStatus(status);
				invoice.setDiffPercent(diffPrcnt);
				invoice.setItcRedReq(itcRedReq);
				invoice.setDeclaredIgst(declIgst);
				invoice.setDeclaredSgst(declSgst);
				invoice.setDeclaredCgst(declCgst);
				invoice.setDeclaredCess(declCess);
				invoice.setImsStatus(imsStatus);
				invoice.setRemarks(remarks);
				
				if (invoiceDate != null) {
					LocalDate invDateLocal = LocalDate.parse(invoiceDate,
							formatter);
					LocalDateTime invDate = invDateLocal.atStartOfDay();
					invoice.setInvoiceDate(invDate);
					invoice.setDocKey(generateDocKey(invDateLocal, rGstin,
							suppGstin, invoiceType, invoiceNumber));

					// adding linking key
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(invoiceDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "RNV", invoiceNumber));
				}

				invoice.setRev(suppRevChrg);
				invoice.setRsn(reasonForItcUnAvail);
				invoice.setSGstin(suppGstin);
				invoice.setPos(pos);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setInvoiceType(invoiceType);
				invoice.setItcAvailable(itcAvail);

				if (orgInvDate != null) {
					DateTimeFormatter invFormatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");
					LocalDate orgInvDateLocal = LocalDate.parse(orgInvDate,
							invFormatter);
					LocalDateTime orgDate = orgInvDateLocal.atStartOfDay();
					invoice.setOrgInvoiceDate(orgDate);
				}

				invoice.setOrgInvoiceNumber(orgInvNum);

				if (gstr1FilingDate != null) {
					DateTimeFormatter gstr1Formatter = DateTimeFormatter
							.ofPattern("dd-MM-yyyy");
					LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
							gstr1Formatter);
					LocalDateTime gstr1Date = gstr1DateLocal.atStartOfDay();
					invoice.setSupFilingDate(gstr1Date);
				}

				invoice.setSupFilingPeriod(gstr1FilingPeriod);
				invoice.setSupInvoiceValue(invoiceValue);
				invoice.setSupTradeName(suppName);
				if (dto.isItcRejected()) {
					invoice.setItcRejected(true);
				}

				if (itemList != null && !itemList.isEmpty()) {
					for (Item item : itemList) {
						Gstr2GetGstr2BLinkingB2baItemEntity itemEntity = new Gstr2GetGstr2BLinkingB2baItemEntity();
						BigDecimal cess = item.getCess();
						BigDecimal cgst = item.getCgst();
						BigDecimal igst = item.getIgst();
						Integer itemNum = item.getItemNum();
						BigDecimal rate = item.getRate();
						BigDecimal sgst = item.getSgst();
						BigDecimal totalTaxableVal = item.getTotalTaxableVal();

						itemEntity.setCessAmt(cess);
						itemEntity.setCgstAmt(cgst);
						itemEntity.setHeader(invoice);
						itemEntity.setIgstAmt(igst);
						itemEntity.setItemNumber(itemNum);
						itemEntity.setSgstAmt(sgst);
						itemEntity.setTaxableValue(totalTaxableVal);
						itemEntity.setTaxRate(rate);
						invoice.setChecksum(checksum);
						itemEntityList.add(itemEntity);
					}
				} else {
					Gstr2GetGstr2BLinkingB2baItemEntity itemEntity = new Gstr2GetGstr2BLinkingB2baItemEntity();

					itemEntity.setCgstAmt(b2bInvoiceList.getCgst());
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(b2bInvoiceList.getIgst());
					itemEntity.setItemNumber(Integer.parseInt("1"));
					itemEntity.setSgstAmt(b2bInvoiceList.getSgst());
					itemEntity.setTaxableValue(
							b2bInvoiceList.getTotalTaxableVal());
					itemEntity.setTaxRate(null);
					itemEntity.setCessAmt(b2bInvoiceList.getCess());
					itemEntityList.add(itemEntity);
				}
				invoice.setLineItems(itemEntityList);
				invoiceEntityList.add(invoice);
			}
		} catch (Exception e) {
			LOGGER.error("Error occured parsing GSTR2B get B2BA invoice", e);
		}
		return invoiceEntityList;
	}

	public List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> parseCdnrData(
			Gstr2BGETDataDto dto, List<CDNRDocuments> cdnrSummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (CDNRDocuments eachInv : cdnrSummary) {
				List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> list = setCdnrInvoiceData(
						eachInv, dto, invocationId, status, isDelete);
				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a CDNR response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;

	}

	private List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> setCdnrInvoiceData(
			CDNRDocuments eachInv, Gstr2BGETDataDto dto, Long invocationId,
			String status, boolean isDelete) {
		List<Gstr2GetGstr2BLinkingCdnrHeaderEntity> invoiceList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingCdnrItemEntity> itemEntityList = new ArrayList<>();
		boolean isFlag = true;
		EinvGstinClientEntity entity = null;

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String genDateString = dto.getDetailedData().getGenDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate genDateLocal = genDateString != null
				? LocalDate.parse(genDateString, formatter) : null;
		LocalDateTime genDate = genDateLocal != null
				? genDateLocal.atStartOfDay() : null;
		String checksum = dto.getChecksum();
		String rGstin = dto.getDetailedData().getRGstin();
		String version = dto.getDetailedData().getVersion();
		String taxPeriod = dto.getDetailedData().getTaxpeiod();

		String gstr1FilingDate = eachInv.getGstr1FilingDate();
		String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
		String suppGstin = eachInv.getSuppGstin();
		String suppName = eachInv.getSuppName();

		List<CDNRNoteData> cdnrNoteData = eachInv.getCdnrNoteData();

		for (CDNRNoteData cdnrNoteItem : cdnrNoteData) {

			Gstr2GetGstr2BLinkingCdnrHeaderEntity invoice = new Gstr2GetGstr2BLinkingCdnrHeaderEntity();

			BigDecimal diffPrcnt = cdnrNoteItem.getDiffPrcnt();
			String itcAvail = cdnrNoteItem.getItcAvail();
			String noteDate = cdnrNoteItem.getNoteDate();
			String noteNumber = cdnrNoteItem.getNoteNumber();
			String noteSupType = cdnrNoteItem.getNoteSupType();
			String noteType = cdnrNoteItem.getNoteType();
			BigDecimal noteValue = cdnrNoteItem.getNoteValue();
			String pos = cdnrNoteItem.getPos();
			String reasonForItcUnAvail = cdnrNoteItem.getReasonForItcUnAvail();
			String suppRevChrg = cdnrNoteItem.getSuppRevChrg();
			String irn = cdnrNoteItem.getIrn();
			String irnGenDate = cdnrNoteItem.getIrngendate();
			String irnSrcType = cdnrNoteItem.getSourceType();
			String itcRedReq = cdnrNoteItem.getItcRedReq();
			BigDecimal declIgst = cdnrNoteItem.getDeclIgst();
			BigDecimal declSgst = cdnrNoteItem.getDeclSgst();
			BigDecimal declCgst = cdnrNoteItem.getDeclCgst();
			BigDecimal declCess = cdnrNoteItem.getDeclCess();
			String imsStatus = cdnrNoteItem.getImsStatus();
			String remarks = cdnrNoteItem.getRemarks();
			
			List<Item> cdnrItems = cdnrNoteItem.getCdnrItems();

			if (cdnrItems != null && !cdnrItems.isEmpty()) {
				for (Item item : cdnrItems) {
					Gstr2GetGstr2BLinkingCdnrItemEntity itemEntity = new Gstr2GetGstr2BLinkingCdnrItemEntity();

					BigDecimal cess = item.getCess();
					BigDecimal cgst = item.getCgst();
					BigDecimal igst = item.getIgst();
					Integer itemNum = item.getItemNum();
					BigDecimal rate = item.getRate();
					BigDecimal sgst = item.getSgst();
					BigDecimal totalTaxableVal = item.getTotalTaxableVal();

					itemEntity.setCessAmt(cess);
					itemEntity.setCgstAmt(cgst);
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(igst);
					itemEntity.setItemNumber(itemNum);
					itemEntity.setSgstAmt(sgst);
					itemEntity.setTaxableValue(totalTaxableVal);
					itemEntity.setTaxRate(rate);
					invoice.setChecksum(checksum);
					itemEntityList.add(itemEntity);
				}
			} else {
				Gstr2GetGstr2BLinkingCdnrItemEntity itemEntity = new Gstr2GetGstr2BLinkingCdnrItemEntity();
				itemEntity.setCgstAmt(cdnrNoteItem.getCgst());
				itemEntity.setHeader(invoice);
				itemEntity.setIgstAmt(cdnrNoteItem.getIgst());
				itemEntity.setItemNumber(Integer.parseInt("1"));
				itemEntity.setSgstAmt(cdnrNoteItem.getSgst());
				itemEntity.setTaxableValue(cdnrNoteItem.getTotalTaxableVal());
				itemEntity.setCessAmt(cdnrNoteItem.getCess());
				
				itemEntity.setTaxRate(null);
				itemEntityList.add(itemEntity);
			}

			invoice.setRGstin(rGstin);
			invoice.setTaxPeriod(taxPeriod);
			invoice.setVersion(version);
			invoice.setGenDate(genDate);
			invoice.setModifiedOn(modifiedOn);
			invoice.setModifiedBy(userName);
			invoice.setIsDelete(isDelete);
			invoice.setChecksum(checksum);
			invoice.setCreatedBy(userName);
			invoice.setCreatedOn(now);
			invoice.setInvocationId(invocationId);
			invoice.setStatus(status);
			invoice.setIrnNo(irn);
			invoice.setIrnGenDate(convertDate(irnGenDate));
			invoice.setIrnSrcType(irnSrcType);
			invoice.setDiffPercent(diffPrcnt);
			invoice.setInvoiceType(noteSupType);
			invoice.setItcAvailable(itcAvail);
			invoice.setItcRedReq(itcRedReq);
			invoice.setDeclaredIgst(declIgst);
			invoice.setDeclaredSgst(declSgst);
			invoice.setDeclaredCgst(declCgst);
			invoice.setDeclaredCess(declCess);
			invoice.setImsStatus(imsStatus);
			invoice.setRemarks(remarks);
			if (dto.isItcRejected()) {
				invoice.setItcRejected(true);
			}
			invoice.setLineItems(itemEntityList);

			if (noteDate != null) {
				LocalDate noteDateLocal = LocalDate.parse(noteDate, formatter);
				LocalDateTime invDate = noteDateLocal.atStartOfDay();
				invoice.setNoteDate(invDate);
				invoice.setDocKey(generateDocKey(noteDateLocal, rGstin,
						suppGstin, noteType, noteNumber));

				if (noteType != null && ("C".equalsIgnoreCase(noteType)
						|| "CR".equalsIgnoreCase(noteType)))
				// adding linking key
				{
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(noteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "CR", noteNumber));

				} else if (noteType != null && ("D".equalsIgnoreCase(noteType)
						|| "DR".equalsIgnoreCase(noteType)))
				// adding linking key
				{
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(noteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "DR", noteNumber));
				}
			}

			invoice.setNoteNumber(noteNumber);
			invoice.setNoteType(noteType);
			invoice.setInvoiceType(noteSupType);
			invoice.setPos(pos);
			invoice.setRev(suppRevChrg);
			invoice.setRsn(reasonForItcUnAvail);
			invoice.setSGstin(suppGstin);

			if (gstr1FilingDate != null) {
				DateTimeFormatter gstr1Formatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
						gstr1Formatter);
				LocalDateTime gstr1Date = gstr1DateLocal.atStartOfDay();
				invoice.setSupFilingDate(gstr1Date);
			}

			invoice.setSupFilingPeriod(gstr1FilingPeriod);
			invoice.setSupInvoiceValue(noteValue);
			invoice.setSupTradeName(suppName);

			invoiceList.add(invoice);

		}
		return invoiceList;
	}

	public List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> parseCdnrAData(
			Gstr2BGETDataDto dto, List<CDNRADocuments> cdnrASummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {
		List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (CDNRADocuments eachInv : cdnrASummary) {

				List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> list = setCdnrAInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a CDNRA response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> setCdnrAInvoiceData(
			CDNRADocuments eachInv, Gstr2BGETDataDto dto, LocalDateTime genDate,
			Long invocationId, String status, boolean isDelete) {
		List<Gstr2GetGstr2BLinkingCdnraHeaderEntity> invoiceList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingCdnraItemEntity> itemEntityList = new ArrayList<>();

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		String checksum = dto.getChecksum();
		String rGstin = dto.getDetailedData().getRGstin();
		String version = dto.getDetailedData().getVersion();
		String taxPeriod = dto.getDetailedData().getTaxpeiod();

		String gstr1FilingDate = eachInv.getGstr1FilingDate();
		String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
		String suppGstin = eachInv.getSuppGstin();
		String suppName = eachInv.getSuppName();

		List<CDNRANoteData> cdnrANoteData = eachInv.getCdnrANoteData();

		for (CDNRANoteData cdnrNoteItem : cdnrANoteData) {

			Gstr2GetGstr2BLinkingCdnraHeaderEntity invoice = new Gstr2GetGstr2BLinkingCdnraHeaderEntity();

			BigDecimal diffPrcnt = cdnrNoteItem.getDiffPrcnt();
			String itcAvail = cdnrNoteItem.getItcAvail();
			String noteDate = cdnrNoteItem.getNoteDate();
			String noteNumber = cdnrNoteItem.getNoteNumber();
			String noteSupType = cdnrNoteItem.getNoteSupType();
			String noteType = cdnrNoteItem.getNoteType();
			BigDecimal noteValue = cdnrNoteItem.getNoteValue();
			String pos = cdnrNoteItem.getPos();
			String reasonForItcUnAvail = cdnrNoteItem.getReasonForItcUnAvail();
			String suppRevChrg = cdnrNoteItem.getSuppRevChrg();
			String orgNoteDate = cdnrNoteItem.getOrgNoteDate();
			String orgNoteNumber = cdnrNoteItem.getOrgNoteNumber();
			String orgNoteType = cdnrNoteItem.getOrgNoteType();
			String itcRedReq = cdnrNoteItem.getItcRedReq();
			BigDecimal declIgst = cdnrNoteItem.getDeclIgst();
			BigDecimal declSgst = cdnrNoteItem.getDeclSgst();
			BigDecimal declCgst = cdnrNoteItem.getDeclCgst();
			BigDecimal declCess = cdnrNoteItem.getDeclCess();
			String imsStatus = cdnrNoteItem.getImsStatus();
			String remarks = cdnrNoteItem.getRemarks();
			
			List<Item> cdnrItems = cdnrNoteItem.getCdnrAItems();
			if (cdnrItems != null && !cdnrItems.isEmpty()) {
				for (Item item : cdnrItems) {
					Gstr2GetGstr2BLinkingCdnraItemEntity itemEntity = new Gstr2GetGstr2BLinkingCdnraItemEntity();

					BigDecimal cess = item.getCess();
					BigDecimal cgst = item.getCgst();
					BigDecimal igst = item.getIgst();
					Integer itemNum = item.getItemNum();
					BigDecimal rate = item.getRate();
					BigDecimal sgst = item.getSgst();
					BigDecimal totalTaxableVal = item.getTotalTaxableVal();

					itemEntity.setCessAmt(cess);
					itemEntity.setCgstAmt(cgst);
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(igst);
					itemEntity.setItemNumber(itemNum);
					itemEntity.setSgstAmt(sgst);
					itemEntity.setTaxableValue(totalTaxableVal);
					itemEntity.setTaxRate(rate);
					invoice.setChecksum(checksum);
					itemEntityList.add(itemEntity);

				}
			} else {
				Gstr2GetGstr2BLinkingCdnraItemEntity itemEntity = new Gstr2GetGstr2BLinkingCdnraItemEntity();

				itemEntity.setCgstAmt(cdnrNoteItem.getCgst());
				itemEntity.setHeader(invoice);
				itemEntity.setIgstAmt(cdnrNoteItem.getIgst());
				itemEntity.setItemNumber(Integer.parseInt("1"));
				itemEntity.setSgstAmt(cdnrNoteItem.getSgst());
				itemEntity.setTaxableValue(cdnrNoteItem.getTotalTaxableVal());
				itemEntity.setCessAmt(cdnrNoteItem.getCess());
				itemEntity.setTaxRate(null);
				itemEntityList.add(itemEntity);
			}

			invoice.setRGstin(rGstin);
			invoice.setTaxPeriod(taxPeriod);
			invoice.setVersion(version);
			invoice.setGenDate(genDate);
			invoice.setModifiedOn(modifiedOn);
			invoice.setModifiedBy(userName);
			invoice.setIsDelete(isDelete);
			invoice.setChecksum(checksum);
			invoice.setCreatedBy(userName);
			invoice.setCreatedOn(now);
			invoice.setInvocationId(invocationId);
			invoice.setStatus(status);

			invoice.setDiffPercent(diffPrcnt);
			invoice.setInvoiceType(noteSupType);
			invoice.setItcAvailable(itcAvail);
			invoice.setInvoiceType(noteSupType);
			invoice.setItcAvailable(itcAvail);
			invoice.setItcRedReq(itcRedReq);
			invoice.setDeclaredIgst(declIgst);
			invoice.setDeclaredSgst(declSgst);
			invoice.setDeclaredCgst(declCgst);
			invoice.setDeclaredCess(declCess);
			invoice.setImsStatus(imsStatus);
			invoice.setRemarks(remarks);
			
			if (dto.isItcRejected()) {
				invoice.setItcRejected(true);
			}
			invoice.setLineItems(itemEntityList);

			if (noteDate != null) {
				LocalDate noteDateLocal = LocalDate.parse(noteDate, formatter);
				LocalDateTime invDate = noteDateLocal.atStartOfDay();
				invoice.setNoteDate(invDate);
				invoice.setDocKey(generateDocKey(noteDateLocal, rGstin,
						suppGstin, noteType, noteNumber));

				if (noteType != null && ("C".equalsIgnoreCase(noteType)
						|| "RCR".equalsIgnoreCase(noteType)))
				// adding linking key
				{
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(noteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "RCR", noteNumber));

				} else if (noteType != null && ("D".equalsIgnoreCase(noteType)
						|| "RDR".equalsIgnoreCase(noteType)))
				// adding linking key
				{
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(noteDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "RDR", noteNumber));
				}
			}

			invoice.setNoteNumber(noteNumber);
			invoice.setNoteType(noteType);
			invoice.setInvoiceType(noteSupType);
			invoice.setPos(pos);
			invoice.setRev(suppRevChrg);
			invoice.setRsn(reasonForItcUnAvail);
			invoice.setSGstin(suppGstin);

			if (orgNoteDate != null) {
				DateTimeFormatter noteFormatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate noteDateLocal = LocalDate.parse(orgNoteDate,
						noteFormatter);
				LocalDateTime orgDate = noteDateLocal.atStartOfDay();
				invoice.setOrgNoteDate(orgDate);
			}

			invoice.setOrgNoteNumber(orgNoteNumber);
			invoice.setOrgNoteType(orgNoteType);

			if (gstr1FilingDate != null) {
				DateTimeFormatter gstr1Formatter = DateTimeFormatter
						.ofPattern("dd-MM-yyyy");
				LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
						gstr1Formatter);
				LocalDateTime gstr1Date = gstr1DateLocal.atStartOfDay();
				invoice.setSupFilingDate(gstr1Date);
			}

			invoice.setSupFilingPeriod(gstr1FilingPeriod);
			invoice.setNoteValue(noteValue);
			invoice.setSupTradeName(suppName);

			invoiceList.add(invoice);

		}

		return invoiceList;
	}

	public List<Gstr2GetGstr2BIsdHeaderEntity> parseIsdData(
			Gstr2BGETDataDto dto, List<ISDDocuments> isdSummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {
		List<Gstr2GetGstr2BIsdHeaderEntity> invoiceList = new ArrayList<>();
		try {

			for (ISDDocuments eachInv : isdSummary) {

				List<Gstr2GetGstr2BIsdHeaderEntity> list = setIsdInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a ISD response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;

	}

	private List<Gstr2GetGstr2BIsdHeaderEntity> setIsdInvoiceData(
			ISDDocuments eachInv, Gstr2BGETDataDto dto, LocalDateTime genDate,
			Long invocationId, String status, boolean isDelete) {
		List<Gstr2GetGstr2BIsdHeaderEntity> invoiceList = new ArrayList<>();

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";
		String checksum = dto.getChecksum();
		String rGstin = dto.getDetailedData().getRGstin();
		String version = dto.getDetailedData().getVersion();
		String taxPeriod = dto.getDetailedData().getTaxpeiod();

		String gstr1FilingDate = eachInv.getGstr1FilingDate();
		String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
		String suppGstin = eachInv.getSuppGstin();
		String suppName = eachInv.getSuppName();

		List<ISDDocDetails> isdDataList = eachInv.getIsdDocDetails();

		for (ISDDocDetails isdData : isdDataList) {

			Gstr2GetGstr2BIsdHeaderEntity invoice = new Gstr2GetGstr2BIsdHeaderEntity();

			Gstr2GetGstr2BIsdItemEntity Item = new Gstr2GetGstr2BIsdItemEntity();

			BigDecimal cess = isdData.getCess();
			BigDecimal cgst = isdData.getCgst();
			BigDecimal igst = isdData.getIgst();
			BigDecimal sgst = isdData.getSgst();
			String itcElg = isdData.getItcElg();

			String isdDocDate = isdData.getIsdDocDate();
			String isdDocNumber = isdData.getIsdDocNumber();
			String isdDocType = isdData.getIsdDocType();
			String orgInvDate = isdData.getOrgInvDate();
			String orgInvNum = isdData.getOrgInvNum();

			invoice.setRGstin(rGstin);
			invoice.setTaxPeriod(taxPeriod);
			invoice.setVersion(version);
			invoice.setGenDate(genDate);
			invoice.setModifiedOn(modifiedOn);
			invoice.setModifiedBy(userName);
			invoice.setIsDelete(isDelete);
			invoice.setChecksum(checksum);
			invoice.setCreatedBy(userName);
			invoice.setCreatedOn(now);
			invoice.setInvocationId(invocationId);
			invoice.setStatus(status);

			invoice.setIsdDocType(isdDocType);

			invoice.setDocNumber(isdDocNumber);

			invoice.setOrgInvoiceNumber(orgInvNum);
			invoice.setSGstin(suppGstin);

			invoice.setSupFilingPeriod(gstr1FilingPeriod);
			invoice.setSupTradeName(suppName);

			if (isdDocDate != null) {
				LocalDate docDateLocal = LocalDate.parse(isdDocDate, formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setDocDate(docDate);
				invoice.setDocKey(generateDocKey(docDateLocal, rGstin,
						suppGstin, isdDocType, isdDocNumber));
			}
			if (orgInvDate != null) {
				LocalDate docDateLocal = LocalDate.parse(orgInvDate, formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setOrgInvoiceDate(docDate);
			}

			if (gstr1FilingDate != null) {
				LocalDate docDateLocal = LocalDate.parse(gstr1FilingDate,
						formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setSupFilingDate(docDate);
			}

			Item.setCessAmt(cess);
			Item.setCgstAmt(cgst);
			Item.setHeader(invoice);
			Item.setIgstAmt(igst);
			Item.setItcEligile(itcElg);
			Item.setSgstAmt(sgst);
			Item.setChecksum(checksum);
			if (dto.isItcRejected()) {
				invoice.setItcRejected(true);
			}
			invoice.setLineItems(Item);

			invoiceList.add(invoice);

		}

		return invoiceList;
	}

	public List<Gstr2GetGstr2BIsdaHeaderEntity> parseIsdAData(
			Gstr2BGETDataDto dto, List<ISDADocuments> isdASummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {
		List<Gstr2GetGstr2BIsdaHeaderEntity> invoiceList = new ArrayList<>();
		try {

			for (ISDADocuments eachInv : isdASummary) {

				List<Gstr2GetGstr2BIsdaHeaderEntity> list = setIsdAInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a ISDA response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;

	}

	private List<Gstr2GetGstr2BIsdaHeaderEntity> setIsdAInvoiceData(
			ISDADocuments eachInv, Gstr2BGETDataDto dto, LocalDateTime genDate,
			Long invocationId, String status, boolean isDelete) {
		List<Gstr2GetGstr2BIsdaHeaderEntity> invoiceList = new ArrayList<>();

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		String checksum = dto.getChecksum();
		String rGstin = dto.getDetailedData().getRGstin();
		String version = dto.getDetailedData().getVersion();
		String taxPeriod = dto.getDetailedData().getTaxpeiod();

		String gstr1FilingDate = eachInv.getGstr1FilingDate();
		String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
		String suppGstin = eachInv.getSuppGstin();
		String suppName = eachInv.getSuppName();

		List<ISDADocDetails> isdADataList = eachInv.getIsdADocDetails();

		for (ISDADocDetails isdData : isdADataList) {

			Gstr2GetGstr2BIsdaHeaderEntity invoice = new Gstr2GetGstr2BIsdaHeaderEntity();

			Gstr2GetGstr2BIsdaItemEntity Item = new Gstr2GetGstr2BIsdaItemEntity();

			BigDecimal cess = isdData.getCess();
			BigDecimal cgst = isdData.getCgst();
			BigDecimal igst = isdData.getIgst();
			BigDecimal sgst = isdData.getSgst();
			String itcElg = isdData.getItcElg();

			String isdDocDate = isdData.getIsdDocDate();
			String isdDocNumber = isdData.getIsdDocNumber();
			String isdDocType = isdData.getIsdDocType();
			String orgInvDate = isdData.getOrgInvDate();
			String orgInvNum = isdData.getOrgInvNumber();
			String orgIsdDocType = isdData.getOrgIsdDocType();

			String orgIsdDocDate = isdData.getOrgIsdDocDate();
			String orgIsdDocNum = isdData.getOrgIsdDocNumber();

			invoice.setRGstin(rGstin);
			invoice.setTaxPeriod(taxPeriod);
			invoice.setVersion(version);
			invoice.setGenDate(genDate);
			invoice.setModifiedOn(modifiedOn);
			invoice.setModifiedBy(userName);
			invoice.setIsDelete(isDelete);
			invoice.setChecksum(checksum);
			invoice.setCreatedBy(userName);
			invoice.setCreatedOn(now);
			invoice.setInvocationId(invocationId);
			invoice.setStatus(status);

			invoice.setIsdDocType(isdDocType);
			invoice.setDocNumber(isdDocNumber);

			invoice.setOrgDocType(orgIsdDocType);
			if (orgIsdDocDate != null) {
				LocalDate docDateLocal = LocalDate.parse(orgIsdDocDate,
						formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setOrgDocDate(docDate);
			}

			invoice.setOrgdocNumber(orgIsdDocNum);
			invoice.setOrgInvoiceNumber(orgInvNum);
			invoice.setSuppSstin(suppGstin);
			invoice.setSupFilingPeriod(gstr1FilingPeriod);
			invoice.setSupTradeName(suppName);

			if (isdDocDate != null) {
				LocalDate docDateLocal = LocalDate.parse(isdDocDate, formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setDocDate(docDate);
				invoice.setDocKey(generateDocKey(docDateLocal, rGstin,
						suppGstin, isdDocType, isdDocNumber));
			}
			if (orgInvDate != null) {
				LocalDate docDateLocal = LocalDate.parse(orgInvDate, formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setOrgInvoiceDate(docDate);
			}

			if (gstr1FilingDate != null) {
				LocalDate docDateLocal = LocalDate.parse(gstr1FilingDate,
						formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				invoice.setSupFilingDate(docDate);
			}

			Item.setCessAmt(cess);
			Item.setCgstAmt(cgst);
			Item.setHeader(invoice);
			Item.setIgstAmt(igst);
			Item.setItcEligile(itcElg);
			Item.setSgstAmt(sgst);
			Item.setChecksum(checksum);

			if (dto.isItcRejected()) {
				invoice.setItcRejected(true);
			}
			invoice.setLineItems(Item);

			invoiceList.add(invoice);

		}

		return invoiceList;
	}

	public List<Gstr2GetGstr2BImpgHeaderEntity> parseImpgData(
			Gstr2BGETDataDto dto, List<IMPGDocuments> impgSummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {
		List<Gstr2GetGstr2BImpgHeaderEntity> invoiceList = new ArrayList<>();
		try {
			Gstr2GetGstr2BImpgHeaderEntity invoice = new Gstr2GetGstr2BImpgHeaderEntity();
			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime modifiedOn = now;

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String checksum = dto.getChecksum();
			String rGstin = dto.getDetailedData().getRGstin();
			String version = dto.getDetailedData().getVersion();
			String taxPeriod = dto.getDetailedData().getTaxpeiod();

			invoice.setRGstin(rGstin);
			invoice.setTaxPeriod(taxPeriod);
			invoice.setVersion(version);
			invoice.setGenDate(genDate);
			invoice.setModifiedOn(modifiedOn);
			invoice.setModifiedBy(userName);
			invoice.setIsDelete(isDelete);
			invoice.setChecksum(checksum);
			invoice.setCreatedBy(userName);
			invoice.setCreatedOn(now);
			invoice.setInvocationId(invocationId);
			invoice.setStatus(status);

			List<Gstr2GetGstr2BImpgItemEntity> itemList = new ArrayList<>();
			for (IMPGDocuments eachInv : impgSummary) {

				Gstr2GetGstr2BImpgItemEntity list = setImpgInvoiceData(eachInv,
						dto, genDate);
				list.setHeader(invoice);

				itemList.add(list);
			}
			invoiceList.add(invoice);

			if (dto.isItcRejected()) {
				invoice.setItcRejected(true);
			}
			invoice.setLineItems(itemList);

		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a Impg response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;

	}

	private Gstr2GetGstr2BImpgItemEntity setImpgInvoiceData(
			IMPGDocuments eachInv, Gstr2BGETDataDto dto,
			LocalDateTime genDate) {

		Gstr2GetGstr2BImpgItemEntity itemEntity = new Gstr2GetGstr2BImpgItemEntity();

		if (eachInv.getBillOfEntityDate() != null) {
			LocalDate docDateLocal = LocalDate
					.parse(eachInv.getBillOfEntityDate(), formatter);
			LocalDateTime docDate = docDateLocal.atStartOfDay();
			itemEntity.setBoeDate(docDate);
		}

		if (eachInv.getICEGATEReferenceDate() != null) {
			LocalDate docDateLocal = LocalDate
					.parse(eachInv.getICEGATEReferenceDate(), formatter);
			LocalDateTime docDate = docDateLocal.atStartOfDay();
			itemEntity.setRefDateIcegate(docDate);
		}

		if (eachInv.getRecvDateInGst() != null) {
			LocalDate docDateLocal = LocalDate.parse(eachInv.getRecvDateInGst(),
					formatter);
			LocalDateTime docDate = docDateLocal.atStartOfDay();
			itemEntity.setRecDateGstin(docDate);
		}

		String isAmended = eachInv.getIsAmended();
		if (isAmended != null && isAmended.equalsIgnoreCase("y")) {
			itemEntity.setIsAmd(true);
		} else {
			itemEntity.setIsAmd(false);
		}

		itemEntity
				.setBoeNumber(Long.parseLong(eachInv.getBillOfEntityNumber()));
		itemEntity.setCessAmt(eachInv.getCess());
		itemEntity.setChecksum(dto.getChecksum());
		itemEntity.setIgstAmt(eachInv.getIgst());
		itemEntity.setPortCode(eachInv.getPortCode());
		itemEntity.setTaxValue(eachInv.getTotalTaxableVal());

		return itemEntity;

	}

	public List<Gstr2GetGstr2BImpgsezHeaderEntity> parseImpgSezData(
			Gstr2BGETDataDto dto, List<IMPGSEZDocuments> impgSezSummary,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {
		List<Gstr2GetGstr2BImpgsezHeaderEntity> invoiceList = new ArrayList<>();
		try {

			for (IMPGSEZDocuments eachInv : impgSezSummary) {

				List<Gstr2GetGstr2BImpgsezHeaderEntity> list = setImpgSezInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2a ImpgSez response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;

	}

	private List<Gstr2GetGstr2BImpgsezHeaderEntity> setImpgSezInvoiceData(
			IMPGSEZDocuments eachInv, Gstr2BGETDataDto dto,
			LocalDateTime genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BImpgsezHeaderEntity> invoiceList = new ArrayList<>();
		List<Gstr2GetGstr2BImpgsezItemEntity> itemList = new ArrayList<>();

		Gstr2GetGstr2BImpgsezHeaderEntity invoice = new Gstr2GetGstr2BImpgsezHeaderEntity();

		LocalDateTime now = EYDateUtil
				.toUTCDateTimeFromLocal(LocalDateTime.now());
		LocalDateTime modifiedOn = now;

		String userName = SecurityContext.getUser() != null
				? (SecurityContext.getUser().getUserPrincipalName() != null
						? SecurityContext.getUser().getUserPrincipalName()
						: "SYSTEM")
				: "SYSTEM";

		String checksum = dto.getChecksum();
		String rGstin = dto.getDetailedData().getRGstin();
		String version = dto.getDetailedData().getVersion();
		String taxPeriod = dto.getDetailedData().getTaxpeiod();

		String suppGstin = eachInv.getSuppGstin();
		String suppName = eachInv.getSuppName();

		invoice.setRGstin(rGstin);
		invoice.setTaxPeriod(taxPeriod);
		invoice.setVersion(version);
		invoice.setGenDate(genDate);
		invoice.setModifiedOn(modifiedOn);
		invoice.setModifiedBy(userName);
		invoice.setIsDelete(isDelete);
		invoice.setChecksum(checksum);
		invoice.setCreatedBy(userName);
		invoice.setCreatedOn(now);
		invoice.setSGstin(suppGstin);
		invoice.setSupTradeName(suppName);
		invoice.setInvocationId(invocationId);
		invoice.setStatus(status);

		List<BIllOfEntryDetails> boeDeatilsList = eachInv
				.getBillOfEntryDetails();
		for (BIllOfEntryDetails bIllOfEntryDetails : boeDeatilsList) {
			Gstr2GetGstr2BImpgsezItemEntity itemEntity = new Gstr2GetGstr2BImpgsezItemEntity();

			if (bIllOfEntryDetails.getBillOfEntityDate() != null) {
				LocalDate docDateLocal = LocalDate.parse(
						bIllOfEntryDetails.getBillOfEntityDate(), formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				itemEntity.setBoeDate(docDate);
			}

			if (bIllOfEntryDetails.getICEGATEReferenceDate() != null) {
				LocalDate docDateLocal = LocalDate.parse(
						bIllOfEntryDetails.getICEGATEReferenceDate(),
						formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				itemEntity.setRefDateIcegate(docDate);
			}

			if (bIllOfEntryDetails.getRecvDateInGst() != null) {
				LocalDate docDateLocal = LocalDate.parse(
						bIllOfEntryDetails.getRecvDateInGst(), formatter);
				LocalDateTime docDate = docDateLocal.atStartOfDay();
				itemEntity.setRecDateGstin(docDate);
			}

			String isAmended = bIllOfEntryDetails.getIsAmended();
			if (isAmended != null && isAmended.equalsIgnoreCase("y")) {
				itemEntity.setIsAmd(true);
			} else {
				itemEntity.setIsAmd(false);
			}

			itemEntity.setBoeNumber(
					Long.parseLong(bIllOfEntryDetails.getBillOfEntityNumber()));
			itemEntity.setCessAmt(bIllOfEntryDetails.getCess());
			itemEntity.setChecksum(dto.getChecksum());
			itemEntity.setIgstAmt(bIllOfEntryDetails.getIgst());
			itemEntity.setPortCode(bIllOfEntryDetails.getPortCode());
			itemEntity.setTaxValue(bIllOfEntryDetails.getTotalTaxableVal());
			itemEntity.setHeader(invoice);
			itemList.add(itemEntity);

		}

		if (dto.isItcRejected()) {
			invoice.setItcRejected(true);
		}
		invoice.setLineItems(itemList);
		invoiceList.add(invoice);

		return invoiceList;
	}

	private String generateDocKey(LocalDate docDate, String rgstin,
			String sgstin, String docType, String docNum) {
		String fy = GenUtil.getFinYear(docDate);
		return new StringJoiner("|").add(fy).add(rgstin).add(sgstin)
				.add(docType).add(docNum).toString();
	}

	private String generateDocKey(LocalDate docDate, String sgstin,
			String docType, String docNum) {
		String fy = GenUtil.getFinYear(docDate);
		return new StringJoiner("|").add(fy).add(sgstin).add(docType)
				.add(docNum).toString();
	}

	// Ecom data Parsing
	public List<Gstr2GetGstr2BLinkingEcomHeaderEntity> parseEcomData(
			Gstr2BGETDataDto dto, List<EcomDocuments> ecomSummary,
			LocalDate genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BLinkingEcomHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (EcomDocuments eachInv : ecomSummary) {

				List<Gstr2GetGstr2BLinkingEcomHeaderEntity> list = setEcomInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2b Ecom response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private List<Gstr2GetGstr2BLinkingEcomHeaderEntity> setEcomInvoiceData(
			EcomDocuments eachInv, Gstr2BGETDataDto dto, LocalDate genDate,
			Long invocationId, String status, boolean isDelete) {

		List<Gstr2GetGstr2BLinkingEcomItemEntity> itemEntityList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingEcomHeaderEntity> invoiceEntityList = new ArrayList<>();
		try {

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime modifiedOn = now;

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String checksum = dto.getChecksum();
			String version = dto.getDetailedData().getVersion();
			String taxPeriod = dto.getDetailedData().getTaxpeiod();

			String gstr1FilingDate = eachInv.getGstr1FilingDate();
			String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
			String suppGstin = eachInv.getSuppGstin();
			String rGstin = dto.getDetailedData().getRGstin();
			String suppName = eachInv.getSuppName();
			List<EcomInvoiceList> ecomInvoiceData = eachInv
					.getEcomInvoiceData();

			for (EcomInvoiceList ecomInvoiceList : ecomInvoiceData) {
				Gstr2GetGstr2BLinkingEcomHeaderEntity invoice = new Gstr2GetGstr2BLinkingEcomHeaderEntity();

				String invoiceDate = ecomInvoiceList.getInvoiceDate();
				String invoiceNumber = ecomInvoiceList.getInvoiceNumber();
				String invoiceType = ecomInvoiceList.getInvoiceType();
				BigDecimal invoiceValue = ecomInvoiceList.getInvoiceValue();
				String itcAvail = ecomInvoiceList.getItcAvail();
				String pos = ecomInvoiceList.getPos();
				String reasonForItcUnAvail = ecomInvoiceList
						.getReasonForItcUnAvail();
				String suppRevChrg = ecomInvoiceList.getSuppRevChrg();
				List<Item> itemList = ecomInvoiceList.getEcomInvItems();
				String irn = ecomInvoiceList.getIrn();
				String irnGenDate = ecomInvoiceList.getIrngendate();
				String irnSrcType = ecomInvoiceList.getSourceType();
				String imsStatus = ecomInvoiceList.getImsStatus();
				String remarks = ecomInvoiceList.getRemarks();
				
				invoice.setTaxPeriod(taxPeriod);
				invoice.setVersion(version);
				invoice.setGenDate(genDate);
				invoice.setModifiedOn(modifiedOn);
				invoice.setModifiedBy(userName);
				invoice.setIsDelete(isDelete);
				invoice.setChecksum(checksum);
				invoice.setCreatedBy(userName);
				invoice.setCreatedOn(now);
				invoice.setInvocationId(invocationId);
				invoice.setStatus(status);
				invoice.setIrnNo(irn);
				invoice.setIrnGenDate(
						irnGenDate != null ? convertDate(irnGenDate) : null);
				invoice.setIrnSrcType(irnSrcType);
				if (invoiceDate != null) {
					LocalDate invDateLocal = LocalDate.parse(invoiceDate,
							formatter);
					invoice.setInvoiceDate(invDateLocal);
					invoice.setDocKey(generateDocKey(invDateLocal, rGstin,
							suppGstin, invoiceType, invoiceNumber));

					// adding linking key
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(invoiceDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "INV", invoiceNumber));

				}
				invoice.setRev(suppRevChrg);
				invoice.setRsn(reasonForItcUnAvail);
				invoice.setSGstin(suppGstin);
				invoice.setRGstin(rGstin);
				invoice.setPos(pos);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setInvoiceType(invoiceType);
				invoice.setItcAvailable(itcAvail);
				invoice.setImsStatus(imsStatus);
				invoice.setRemarks(remarks);
				
				if (gstr1FilingDate != null) {
					LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
							formatter);
					invoice.setSupFilingDate(gstr1DateLocal);
				}

				invoice.setSupFilingPeriod(gstr1FilingPeriod);
				invoice.setSupInvoiceValue(invoiceValue);
				invoice.setSupTradeName(suppName);

				if (itemList != null && !itemList.isEmpty()) {
					for (Item item : itemList) {
						Gstr2GetGstr2BLinkingEcomItemEntity itemEntity = new Gstr2GetGstr2BLinkingEcomItemEntity();
						BigDecimal cess = item.getCess();
						BigDecimal cgst = item.getCgst();
						BigDecimal igst = item.getIgst();
						Integer itemNum = item.getItemNum();
						BigDecimal rate = item.getRate();
						BigDecimal sgst = item.getSgst();
						BigDecimal totalTaxableVal = item.getTotalTaxableVal();

						itemEntity.setCessAmt(cess);
						itemEntity.setCgstAmt(cgst);
						itemEntity.setHeader(invoice);
						itemEntity.setIgstAmt(igst);
						itemEntity.setItemNumber(itemNum);
						itemEntity.setSgstAmt(sgst);
						itemEntity.setTaxableValue(totalTaxableVal);
						itemEntity.setTaxRate(rate);
						itemEntityList.add(itemEntity);
					}
				} else {
					Gstr2GetGstr2BLinkingEcomItemEntity itemEntity = new Gstr2GetGstr2BLinkingEcomItemEntity();

					itemEntity.setCgstAmt(ecomInvoiceList.getCgst());
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(ecomInvoiceList.getIgst());
					itemEntity.setItemNumber(Integer.parseInt("1"));
					itemEntity.setSgstAmt(ecomInvoiceList.getSgst());
					itemEntity.setTaxableValue(
							ecomInvoiceList.getTotalTaxableVal());
					itemEntity.setCessAmt(ecomInvoiceList.getCess());
					itemEntity.setTaxRate(null);
					itemEntityList.add(itemEntity);
				}

				if (dto.isItcRejected()) {
					invoice.setItcRejected(true);
				}
				invoice.setLineItems(itemEntityList);
				invoiceEntityList.add(invoice);
			}
		} catch (Exception e) {
			LOGGER.error("Error occured parsing GSTR2B get Ecom invoice", e);
		}
		return invoiceEntityList;
	}

	// Ecoma data Parsing
	public List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> parseEcomaData(
			Gstr2BGETDataDto dto, List<EcomADocuments> ecomaSummary,
			LocalDate genDate, Long invocationId, String status,
			boolean isDelete) {

		List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> invoiceList = new ArrayList<>();

		try {

			for (EcomADocuments eachInv : ecomaSummary) {

				List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> list = setEcomaInvoiceData(
						eachInv, dto, genDate, invocationId, status, isDelete);

				invoiceList.addAll(list);
			}
		} catch (Exception ex) {
			String msg = "failed to parse Gstr2b Ecom response";
			LOGGER.error(msg, ex);
		}
		return invoiceList;
	}

	private List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> setEcomaInvoiceData(
			EcomADocuments eachInv, Gstr2BGETDataDto dto, LocalDate genDate,
			Long invocationId, String status, boolean isDelete) {

		List<Gstr2GetGstr2BLinkingEcomaItemEntity> itemEntityList = new ArrayList<>();
		List<Gstr2GetGstr2BLinkingEcomaHeaderEntity> invoiceEntityList = new ArrayList<>();
		try {

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			LocalDateTime modifiedOn = now;

			String userName = SecurityContext.getUser() != null
					? (SecurityContext.getUser().getUserPrincipalName() != null
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM")
					: "SYSTEM";

			String checksum = dto.getChecksum();
			String version = dto.getDetailedData().getVersion();
			String taxPeriod = dto.getDetailedData().getTaxpeiod();
			String rGstin = dto.getDetailedData().getRGstin();
			String gstr1FilingDate = eachInv.getGstr1FilingDate();
			String gstr1FilingPeriod = eachInv.getGstr1FilingPeriod();
			String suppGstin = eachInv.getSuppGstin();
			String suppName = eachInv.getSuppName();
			List<EcomaInvoiceList> ecomaInvoiceData = eachInv
					.getEcomaInvoiceData();

			for (EcomaInvoiceList ecomaInvoiceList : ecomaInvoiceData) {
				Gstr2GetGstr2BLinkingEcomaHeaderEntity invoice = new Gstr2GetGstr2BLinkingEcomaHeaderEntity();

				String invoiceDate = ecomaInvoiceList.getInvoiceDate();
				String invoiceNumber = ecomaInvoiceList.getInvoiceNumber();
				String invoiceType = ecomaInvoiceList.getInvoiceType();
				BigDecimal invoiceValue = ecomaInvoiceList.getInvoiceValue();
				String itcAvail = ecomaInvoiceList.getItcAvail();
				String pos = ecomaInvoiceList.getPos();
				String reasonForItcUnAvail = ecomaInvoiceList
						.getReasonForItcUnAvail();
				String suppRevChrg = ecomaInvoiceList.getSuppRevChrg();
				List<Item> itemList = ecomaInvoiceList.getEcomaInvItems();
				String irn = ecomaInvoiceList.getIrn();
				String irnGenDate = ecomaInvoiceList.getIrngendate();
				String irnSrcType = ecomaInvoiceList.getSourceType();
				String itcRedReq = ecomaInvoiceList.getItcRedReq();
				BigDecimal declIgst = ecomaInvoiceList.getDeclIgst();
				BigDecimal declSgst = ecomaInvoiceList.getDeclSgst();
				BigDecimal declCgst = ecomaInvoiceList.getDeclCgst();
				BigDecimal declCess = ecomaInvoiceList.getDeclCess();
				String imsStatus = ecomaInvoiceList.getImsStatus();
				String remarks = ecomaInvoiceList.getRemarks();
				
				invoice.setTaxPeriod(taxPeriod);
				invoice.setVersion(version);
				invoice.setGenDate(genDate);
				invoice.setModifiedOn(modifiedOn);
				invoice.setModifiedBy(userName);
				invoice.setIsDelete(isDelete);
				invoice.setChecksum(checksum);
				invoice.setCreatedBy(userName);
				invoice.setCreatedOn(now);
				invoice.setInvocationId(invocationId);
				invoice.setStatus(status);
				invoice.setIrnNo(irn);
				invoice.setIrnGenDate(
						irnGenDate != null ? convertDate(irnGenDate) : null);
				invoice.setIrnSrcType(irnSrcType);
				if (invoiceDate != null) {
					LocalDate invDateLocal = LocalDate.parse(invoiceDate,
							formatter);
					invoice.setInvoiceDate(invDateLocal);
					invoice.setDocKey(generateDocKey(invDateLocal, rGstin,
							suppGstin, invoiceType, invoiceNumber));

					// adding linking key
					invoice.setLnkingDocKey(deriveLinkingKey(
							LocalDate.parse(invoiceDate,
									DateUtil.SUPPORTED_DATE_FORMAT2),
							rGstin, suppGstin, "RNV", invoiceNumber));

				}
				invoice.setRev(suppRevChrg);
				invoice.setRsn(reasonForItcUnAvail);
				invoice.setSGstin(suppGstin);
				invoice.setRGstin(rGstin);
				invoice.setPos(pos);
				invoice.setInvoiceNumber(invoiceNumber);
				invoice.setInvoiceType(invoiceType);
				invoice.setItcAvailable(itcAvail);
				invoice.setItcRedReq(itcRedReq);
				invoice.setDeclaredIgst(declIgst);
				invoice.setDeclaredSgst(declSgst);
				invoice.setDeclaredCgst(declCgst);
				invoice.setDeclaredCess(declCess);
				invoice.setImsStatus(imsStatus);
				invoice.setRemarks(remarks);
				
				if (gstr1FilingDate != null) {
					LocalDate gstr1DateLocal = LocalDate.parse(gstr1FilingDate,
							formatter);
					invoice.setSupFilingDate(gstr1DateLocal);
				}

				invoice.setSupFilingPeriod(gstr1FilingPeriod);
				invoice.setSupInvoiceValue(invoiceValue);
				invoice.setSupTradeName(suppName);

				if (itemList != null && !itemList.isEmpty()) {
					for (Item item : itemList) {
						Gstr2GetGstr2BLinkingEcomaItemEntity itemEntity = new Gstr2GetGstr2BLinkingEcomaItemEntity();
						BigDecimal cess = item.getCess();
						BigDecimal cgst = item.getCgst();
						BigDecimal igst = item.getIgst();
						Integer itemNum = item.getItemNum();
						BigDecimal rate = item.getRate();
						BigDecimal sgst = item.getSgst();
						BigDecimal totalTaxableVal = item.getTotalTaxableVal();

						itemEntity.setCessAmt(cess);
						itemEntity.setCgstAmt(cgst);
						itemEntity.setHeader(invoice);
						itemEntity.setIgstAmt(igst);
						itemEntity.setItemNumber(itemNum);
						itemEntity.setSgstAmt(sgst);
						itemEntity.setTaxableValue(totalTaxableVal);
						itemEntity.setTaxRate(rate);
						itemEntityList.add(itemEntity);
					}
				} else {
					Gstr2GetGstr2BLinkingEcomaItemEntity itemEntity = new Gstr2GetGstr2BLinkingEcomaItemEntity();

					itemEntity.setCgstAmt(ecomaInvoiceList.getCgst());
					itemEntity.setHeader(invoice);
					itemEntity.setIgstAmt(ecomaInvoiceList.getIgst());
					itemEntity.setItemNumber(Integer.parseInt("1"));
					itemEntity.setSgstAmt(ecomaInvoiceList.getSgst());
					itemEntity.setTaxableValue(
							ecomaInvoiceList.getTotalTaxableVal());
					itemEntity.setTaxRate(null);
					itemEntity.setCessAmt(ecomaInvoiceList.getCess());
					
					itemEntityList.add(itemEntity);
				}

				if (dto.isItcRejected()) {
					invoice.setItcRejected(true);
				}
				invoice.setLineItems(itemEntityList);
				invoiceEntityList.add(invoice);
			}
		} catch (Exception e) {
			LOGGER.error("Error occured parsing GSTR2B get Ecoma invoice", e);
		}
		return invoiceEntityList;
	}

	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
}
