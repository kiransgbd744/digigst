package com.ey.advisory.app.inward.einvoice;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemB2BRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemDexpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemExpWpRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwopRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GetIrnLineItemSezwpRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.ey.advisory.einv.dto.AddlDocument;
import com.ey.advisory.einv.dto.Attribute;
import com.ey.advisory.einv.dto.Contract;
import com.ey.advisory.einv.dto.EinvoiceRequestDto;
import com.ey.advisory.einv.dto.ItemDto;
import com.ey.advisory.einv.dto.PrecDocument;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Slf4j
@Service("InwardGetIrnDetailsDataParserServiceImpl")
@Transactional(value = "clientTransactionManager")
public class InwardGetIrnDetailsDataParserServiceImpl
		implements InwardGetIrnDetailsDataParserService {
	
	@Autowired
	private GetIrnLineItemB2BRepository getIrnb2bLineRepo;
	
	@Autowired
	private GetIrnLineItemSezwopRepository getIrnSezwopLineRepo;

	@Autowired
	private GetIrnLineItemSezwpRepository getIrnSezwpLineRepo;

	@Autowired
	private GetIrnLineItemDexpRepository getIrnDexpLineRepo;

	@Autowired
	private GetIrnLineItemExpWpRepository getIrnExpWpLineRepo;

	@Autowired
	private GetIrnLineItemExpWopRepository getIrnExpWopLineRepo;


	private final DateTimeFormatter formatter = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final DateTimeFormatter formatter1 = DateTimeFormatter
			.ofPattern("dd/MM/yyyy");
	
	private final DateTimeFormatter formatter2 = DateTimeFormatter
			.ofPattern("yyyy-MM-dd");

	@Override
	public <K> K convertToHeaderList(EinvoiceRequestDto respDto,
			Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto irnDtlDto,
			Class<K> headerEntity) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException, InstantiationException {

		K entity = headerEntity.newInstance();

		entity.getClass().getMethod("setBatchId", Long.class).invoke(entity,
				dto.getBatchId());
		entity.getClass().getMethod("setIrn", String.class).invoke(entity,
				respDto.getIrn());
		entity.getClass().getMethod("setIrnDate", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(respDto.getAckDt())
						? LocalDateTime.parse(respDto.getAckDt(), formatter)
						: null);
		entity.getClass().getMethod("setIrnDateTime", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(respDto.getAckDt())
						? LocalDateTime.parse(respDto.getAckDt(), formatter)
						: null);
		entity.getClass().getMethod("setIrnStatus", String.class).invoke(entity,
				irnDtlDto.getIrnSts() != null ? irnDtlDto.getIrnSts() : null);
		

		String irnGenPer = String.valueOf(LocalDateTime
				.parse(respDto.getAckDt(), formatter)
				.getMonthValue() < 10
						? "0" + String.valueOf(LocalDateTime
								.parse(respDto.getAckDt(), formatter)
								.getMonthValue())
						: LocalDateTime.parse(respDto.getAckDt(), formatter)
								.getMonthValue())
				+ String.valueOf(LocalDateTime
						.parse(respDto.getAckDt(), formatter).getYear());
		
		entity.getClass().getMethod("setIrnGenerationPeriod", String.class).invoke(entity, irnGenPer);
		

		entity.getClass().getMethod("setIrnCancelDateTime", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getCnlDt())
						? LocalDateTime.parse(irnDtlDto.getCnlDt(), formatter)
						: null);
		entity.getClass().getMethod("setIrnCancelDate", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getCnlDt())
						? LocalDateTime.parse(irnDtlDto.getCnlDt(), formatter)
						: null);

		entity.getClass().getMethod("setAckNum", Long.class).invoke(entity,
				!Strings.isNullOrEmpty(respDto.getAckNum())
						? Long.valueOf(respDto.getAckNum()) : null);

		if (respDto.getTranDtls() != null) {
			entity.getClass().getMethod("setTaxScheme", String.class)
					.invoke(entity, respDto.getTranDtls().getTaxSch() != null
							? respDto.getTranDtls().getTaxSch() : null);
			
			String suppTyp = respDto.getTranDtls().getSupTyp();
			if("DEXP".equalsIgnoreCase(suppTyp)){
				suppTyp="DXP";
			}
			
			entity.getClass().getMethod("setSupplyType", String.class)
			        .invoke(entity, suppTyp != null
			                ? suppTyp : null);

			entity.getClass().getMethod("setEcomGSTIN", String.class)
					.invoke(entity, respDto.getTranDtls().getEcmGstin() != null
							? respDto.getTranDtls().getEcmGstin() : null);

			entity.getClass().getMethod("setSection7OfIGSTFlag", String.class)
					.invoke(entity,
							respDto.getTranDtls().getIgstOnIntra() != null
									? respDto.getTranDtls().getIgstOnIntra()
									: null);

			entity.getClass().getMethod("setReverseChargeFlag", String.class)
					.invoke(entity, respDto.getTranDtls().getRegRev() != null
							? respDto.getTranDtls().getRegRev() : "N");

		}

		entity.getClass().getMethod("setCancellationReason", String.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getCnlRsn())
						? irnDtlDto.getCnlRsn() : null);
		entity.getClass().getMethod("setCancellationRemarks", String.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getCnlRem())
						? irnDtlDto.getCnlRem() : null);

		if (respDto.getDocDtls() != null) {
			
			String docTyp = respDto.getDocDtls().getTyp();
			if("CRN".equalsIgnoreCase(docTyp)){
				docTyp="CR";
			}else if("DBN".equalsIgnoreCase(docTyp)){
				docTyp="DR";
			}
			entity.getClass().getMethod("setDocType", String.class)
			        .invoke(entity, docTyp != null
			                ? docTyp : null);
			
			entity.getClass().getMethod("setDocNum", String.class)
					.invoke(entity, respDto.getDocDtls().getNo() != null
							? respDto.getDocDtls().getNo() : null);
			entity.getClass().getMethod("setDocDate", LocalDateTime.class)
					.invoke(entity,
							respDto.getDocDtls().getDt() != null ? respDto
									.getDocDtls().getDt().atStartOfDay()
									: null);
		}

		if (respDto.getSellerDtls() != null) {
			entity.getClass().getMethod("setSupplierGSTIN", String.class)
					.invoke(entity, respDto.getSellerDtls().getGstin() != null
							? respDto.getSellerDtls().getGstin() : null);
			entity.getClass().getMethod("setSupplierTradeName", String.class)
					.invoke(entity, respDto.getSellerDtls().getTrdNm() != null
							? respDto.getSellerDtls().getTrdNm() : null);
			entity.getClass().getMethod("setSupplierLegalName", String.class)
					.invoke(entity, respDto.getSellerDtls().getLglNm() != null
							? respDto.getSellerDtls().getLglNm() : null);

			entity.getClass().getMethod("setSupplierAddress1", String.class)
					.invoke(entity, respDto.getSellerDtls().getAddr1() != null
							? respDto.getSellerDtls().getAddr1() : null);
			entity.getClass().getMethod("setSupplierAddress2", String.class)
					.invoke(entity, respDto.getSellerDtls().getAddr2() != null
							? respDto.getSellerDtls().getAddr2() : null);
			entity.getClass().getMethod("setSupplierLocation", String.class)
					.invoke(entity, respDto.getSellerDtls().getLoc() != null
							? respDto.getSellerDtls().getLoc() : null);
			entity.getClass().getMethod("setSupplierPincode", Integer.class)
					.invoke(entity, respDto.getSellerDtls().getPin() != null
							? respDto.getSellerDtls().getPin() : null);
			entity.getClass().getMethod("setSupplierStateCode", Integer.class)
					.invoke(entity,
							!Strings.isNullOrEmpty(
									respDto.getSellerDtls().getState())
											? Integer.valueOf(respDto
													.getSellerDtls().getState())
											: null);
			entity.getClass().getMethod("setSupplierPhone", String.class)
					.invoke(entity, respDto.getSellerDtls().getPh() != null
							? respDto.getSellerDtls().getPh() : null);
			entity.getClass().getMethod("setSupplierEmail", String.class)
					.invoke(entity, respDto.getSellerDtls().getEm() != null
							? respDto.getSellerDtls().getEm() : null);
		}

		if (respDto.getBuyerDtls() != null) {
			entity.getClass().getMethod("setCustomerGSTIN", String.class)
					.invoke(entity, respDto.getBuyerDtls().getGstin() != null
							? respDto.getBuyerDtls().getGstin() : null);
			entity.getClass().getMethod("setCustomerTradeName", String.class)
					.invoke(entity, respDto.getBuyerDtls().getTrdNm() != null
							? respDto.getBuyerDtls().getTrdNm() : null);
			entity.getClass().getMethod("setCustomerLegalName", String.class)
					.invoke(entity, respDto.getBuyerDtls().getLglNm() != null
							? respDto.getBuyerDtls().getLglNm() : null);

			entity.getClass().getMethod("setCustomerAddress1", String.class)
					.invoke(entity, respDto.getBuyerDtls().getAddr1() != null
							? respDto.getBuyerDtls().getAddr1() : null);
			entity.getClass().getMethod("setCustomerAddress2", String.class)
					.invoke(entity, respDto.getBuyerDtls().getAddr2() != null
							? respDto.getBuyerDtls().getAddr2() : null);
			entity.getClass().getMethod("setCustomerLocation", String.class)
					.invoke(entity, respDto.getBuyerDtls().getLoc() != null
							? respDto.getBuyerDtls().getLoc() : null);
			entity.getClass().getMethod("setCustomerPincode", Integer.class)
					.invoke(entity, respDto.getBuyerDtls().getPin() != null
							? respDto.getBuyerDtls().getPin() : null);
			entity.getClass().getMethod("setCustomerStateCode", Integer.class)
					.invoke(entity,
							!Strings.isNullOrEmpty(
									respDto.getBuyerDtls().getState())
											? Integer.valueOf(respDto
													.getBuyerDtls().getState())
											: null);
			entity.getClass().getMethod("setCustomerPhone", String.class)
					.invoke(entity, respDto.getBuyerDtls().getPh() != null
							? respDto.getBuyerDtls().getPh() : null);
			entity.getClass().getMethod("setCustomerEmail", String.class)
					.invoke(entity, respDto.getBuyerDtls().getEm() != null
							? respDto.getBuyerDtls().getEm() : null);
			entity.getClass().getMethod("setBillingPOS", String.class)
					.invoke(entity, respDto.getBuyerDtls().getPos() != null
							? respDto.getBuyerDtls().getPos() : null);
		}

		if (respDto.getDispDtls() != null) {
			entity.getClass().getMethod("setDispatcherTradeName", String.class)
					.invoke(entity, respDto.getDispDtls().getNm() != null
							? respDto.getDispDtls().getNm() : null);
			entity.getClass().getMethod("setDispatcherAddress1", String.class)
					.invoke(entity, respDto.getDispDtls().getAddr1() != null
							? respDto.getDispDtls().getAddr1() : null);
			entity.getClass().getMethod("setDispatcherAddress2", String.class)
					.invoke(entity, respDto.getDispDtls().getAddr2() != null
							? respDto.getDispDtls().getAddr2() : null);
			entity.getClass().getMethod("setDispatcherLocation", String.class)
					.invoke(entity, respDto.getDispDtls().getLoc() != null
							? respDto.getDispDtls().getLoc() : null);
			entity.getClass().getMethod("setDispatcherPincode", Integer.class)
					.invoke(entity, respDto.getDispDtls().getPin() != null
							? respDto.getDispDtls().getPin() : null);
			entity.getClass().getMethod("setDispatcherStateCode", Integer.class)
					.invoke(entity,
							!Strings.isNullOrEmpty(
									respDto.getDispDtls().getStcd())
											? Integer.parseInt(respDto
													.getDispDtls().getStcd())
											: null);
		}

		if (respDto.getShipDtls() != null) {
			entity.getClass().getMethod("setShipToGSTIN", String.class)
					.invoke(entity, respDto.getShipDtls().getGstin() != null
							? respDto.getShipDtls().getGstin() : null);
			entity.getClass().getMethod("setShipToTradeName", String.class)
					.invoke(entity, respDto.getShipDtls().getTrdNm() != null
							? respDto.getShipDtls().getTrdNm() : null);
			entity.getClass().getMethod("setShipToLegalName", String.class)
					.invoke(entity, respDto.getShipDtls().getLglNm() != null
							? respDto.getShipDtls().getLglNm() : null);
			entity.getClass().getMethod("setShipToAddress1", String.class)
					.invoke(entity, respDto.getShipDtls().getAddr1() != null
							? respDto.getShipDtls().getAddr1() : null);
			entity.getClass().getMethod("setShipToAddress2", String.class)
					.invoke(entity, respDto.getShipDtls().getAddr2() != null
							? respDto.getShipDtls().getAddr2() : null);
			entity.getClass().getMethod("setShipToLocation", String.class)
					.invoke(entity, respDto.getShipDtls().getLoc() != null
							? respDto.getShipDtls().getLoc() : null);
			entity.getClass().getMethod("setShipToPincode", Integer.class)
					.invoke(entity, respDto.getShipDtls().getPin() != null
							? respDto.getShipDtls().getPin() : null);
			entity.getClass().getMethod("setShipToStateCode", Integer.class)
					.invoke(entity,
							!Strings.isNullOrEmpty(
									respDto.getShipDtls().getStcd())
											? Integer.parseInt(respDto
													.getShipDtls().getStcd())
											: null);

		}

		if (respDto.getValDtls() != null) {
			entity.getClass().getMethod("setInvOtherCharges", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getOthChrg() != null
									? respDto.getValDtls().getOthChrg()
									: BigDecimal.ZERO);

			entity.getClass().getMethod("setInvAssessableAmt", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getAssVal() != null
									? respDto.getValDtls().getAssVal()
									: BigDecimal.ZERO);
			entity.getClass().getMethod("setInvIgstAmt", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getIgstVal() != null
									? respDto.getValDtls().getIgstVal()
									: BigDecimal.ZERO);
			entity.getClass().getMethod("setInvCgstAmt", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getCgstVal() != null
									? respDto.getValDtls().getCgstVal()
									: BigDecimal.ZERO);
			entity.getClass().getMethod("setInvSgstAmt", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getSgstVal() != null
									? respDto.getValDtls().getSgstVal()
									: BigDecimal.ZERO);

			entity.getClass().getMethod("setInvValue", BigDecimal.class).invoke(
					entity,
					respDto.getValDtls().getTotInvVal() != null
							? respDto.getValDtls().getTotInvVal()
							: BigDecimal.ZERO);
			entity.getClass().getMethod("setRoundOff", BigDecimal.class).invoke(
					entity,
					respDto.getValDtls().getRndOffAmt() != null
							? respDto.getValDtls().getRndOffAmt()
							: BigDecimal.ZERO);
			entity.getClass()
					.getMethod("setUserDefinedField28", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getDiscount() != null
									? respDto.getValDtls().getDiscount()
									: BigDecimal.ZERO);
			entity.getClass().getMethod("setInvoiceValueFC", BigDecimal.class)
					.invoke(entity,
							respDto.getValDtls().getTotInvValFc() != null
									? respDto.getValDtls().getTotInvValFc()
									: BigDecimal.ZERO);

			entity.getClass().getMethod("setDiscount", BigDecimal.class).invoke(
					entity,
					respDto.getValDtls().getDiscount() != null
							? respDto.getValDtls().getDiscount()
							: BigDecimal.ZERO);
		}

		if (respDto.getPayDtls() != null) {
			entity.getClass().getMethod("setPayeeName", String.class)
					.invoke(entity, respDto.getPayDtls().getNm() != null
							? respDto.getPayDtls().getNm() : null);
			entity.getClass().getMethod("setPayeeName", String.class)
					.invoke(entity, respDto.getPayDtls().getNm() != null
							? respDto.getPayDtls().getNm() : null);
			entity.getClass().getMethod("setAccountDetail", String.class)
					.invoke(entity, respDto.getPayDtls().getAccDet() != null
							? respDto.getPayDtls().getAccDet() : null);
			entity.getClass().getMethod("setModeOfPayment", String.class)
					.invoke(entity, respDto.getPayDtls().getMode() != null
							? respDto.getPayDtls().getMode() : null);
			entity.getClass().getMethod("setBranchOrIFSCCode", String.class)
					.invoke(entity, respDto.getPayDtls().getFinInsBr() != null
							? respDto.getPayDtls().getFinInsBr() : null);
			entity.getClass().getMethod("setPaymentTerms", String.class)
					.invoke(entity, respDto.getPayDtls().getPayTerm() != null
							? respDto.getPayDtls().getPayTerm() : null);
			entity.getClass().getMethod("setPaymentInstruction", String.class)
					.invoke(entity, respDto.getPayDtls().getPayInstr() != null
							? respDto.getPayDtls().getPayInstr() : null);
			entity.getClass().getMethod("setCreditTransfer", String.class)
					.invoke(entity, respDto.getPayDtls().getCrTrn() != null
							? respDto.getPayDtls().getCrTrn() : null);
			entity.getClass().getMethod("setDirectDebit", String.class)
					.invoke(entity, respDto.getPayDtls().getDirDr() != null
							? respDto.getPayDtls().getDirDr() : null);
			entity.getClass().getMethod("setCreditDays", Integer.class)
					.invoke(entity, respDto.getPayDtls().getCrDay() != null
							? respDto.getPayDtls().getCrDay() : null);
			entity.getClass().getMethod("setPaidAmount", BigDecimal.class)
					.invoke(entity,
							respDto.getPayDtls().getPaidAmt() != null
									? respDto.getPayDtls().getPaidAmt()
									: BigDecimal.ZERO);
			entity.getClass().getMethod("setBalanceAmount", BigDecimal.class)
					.invoke(entity,
							respDto.getPayDtls().getPaymtDue() != null
									? respDto.getPayDtls().getPaymtDue()
									: BigDecimal.ZERO);
		}

		if (respDto.getExpDtls() != null) {
			entity.getClass().getMethod("setShippingBillNumber", String.class)
					.invoke(entity, respDto.getExpDtls().getShipBNo() != null
							? respDto.getExpDtls().getShipBNo() : null);

			entity.getClass()
					.getMethod("setShippingBillDate", LocalDateTime.class)
					.invoke(entity,
							respDto.getExpDtls().getShipBDt() != null ? respDto
									.getExpDtls().getShipBDt().atStartOfDay()
									: null);

			entity.getClass().getMethod("setPortCode", String.class)
					.invoke(entity, respDto.getExpDtls().getPort() != null
							? respDto.getExpDtls().getPort() : null);

			entity.getClass().getMethod("setClaimRefundFlag", String.class)
					.invoke(entity, respDto.getExpDtls().getRefClm() != null
							? respDto.getExpDtls().getRefClm() : null);

			entity.getClass().getMethod("setCurrencyCode", String.class)
					.invoke(entity, respDto.getExpDtls().getForCur() != null
							? respDto.getExpDtls().getForCur() : null);

			entity.getClass().getMethod("setCountryCode", String.class)
					.invoke(entity, respDto.getExpDtls().getCntCode() != null
							? respDto.getExpDtls().getCntCode() : null);
			
			entity.getClass().getMethod("setExportDuty", BigDecimal.class)
			.invoke(entity,
					respDto.getExpDtls().getExpDuty() != null
							? respDto.getExpDtls().getExpDuty()
							: BigDecimal.ZERO);


		}

		if (respDto.getEwbDetails() != null) {
			entity.getClass().getMethod("setTransporterID", String.class)
					.invoke(entity, respDto.getEwbDetails().getTransId() != null
							? respDto.getEwbDetails().getTransId() : null);
			entity.getClass().getMethod("setTransporterName", String.class)
					.invoke(entity,
							respDto.getEwbDetails().getTransName() != null
									? respDto.getEwbDetails().getTransName()
									: null);
			entity.getClass().getMethod("setTransportMode", String.class)
					.invoke(entity,
							respDto.getEwbDetails().getTransMode() != null
									? respDto.getEwbDetails().getTransMode()
									: null);
			entity.getClass().getMethod("setTransportDocNo", String.class)
					.invoke(entity,
							respDto.getEwbDetails().getTransDocNo() != null
									? respDto.getEwbDetails().getTransDocNo()
									: null);

			entity.getClass().getMethod("setTransportDocDate", String.class)
					.invoke(entity,
							respDto.getEwbDetails().getTransDocDt() != null
									? respDto.getEwbDetails().getTransDocDt()
											.toString()
									: null);

			entity.getClass().getMethod("setDistance", Integer.class).invoke(
					entity, respDto.getEwbDetails().getDistance() != null
							? respDto.getEwbDetails().getDistance() : null);
			entity.getClass().getMethod("setVehicleNo", String.class)
					.invoke(entity, respDto.getEwbDetails().getVehNo() != null
							? respDto.getEwbDetails().getVehNo() : null);
			entity.getClass().getMethod("setVehicleType", String.class)
					.invoke(entity, respDto.getEwbDetails().getVehType() != null
							? respDto.getEwbDetails().getVehType() : null);
		}
		entity.getClass().getMethod("setEWayBillNumber", Long.class).invoke(
				entity,
				irnDtlDto.getEwbNo() != null ? irnDtlDto.getEwbNo() : null);
		
		entity.getClass().getMethod("setEWayBillDate", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getEwbDt())
						? LocalDateTime.parse(irnDtlDto.getEwbDt(), formatter)
						: null);
		entity.getClass().getMethod("setValidUpto", LocalDateTime.class)
				.invoke(entity, !Strings.isNullOrEmpty(irnDtlDto.getEwbVald())
						? LocalDateTime.parse(irnDtlDto.getEwbVald(), formatter)
						: null);

		if (respDto.getRefDtls() != null) {

			entity.getClass().getMethod("setInvoiceRemarks", String.class)
					.invoke(entity,
							!Strings.isNullOrEmpty(
									respDto.getRefDtls().getInvRm())
											? respDto.getRefDtls().getInvRm()
											: null);

			if (respDto.getRefDtls().getDocPerdDtls() != null) {
				entity.getClass()
						.getMethod("setInvoicePeriodStartDate",
								LocalDateTime.class)
						.invoke(entity,
								respDto.getRefDtls().getDocPerdDtls()
										.getInvStDt() != null
												? respDto
														.getRefDtls()
														.getDocPerdDtls()
														.getInvStDt()
														.atStartOfDay()
												: null);

				entity.getClass()
						.getMethod("setInvoicePeriodEndDate",
								LocalDateTime.class)
						.invoke(entity,
								respDto.getRefDtls().getDocPerdDtls()
										.getInvEndDt() != null
												? respDto
														.getRefDtls()
														.getDocPerdDtls()
														.getInvEndDt()
														.atStartOfDay()
												: null);

			}
		}
		entity.getClass().getMethod("setDelete", boolean.class).invoke(entity,
				false);
		entity.getClass().getMethod("setCreatedBy", String.class).invoke(entity,
				"SYSTEM");
		entity.getClass().getMethod("setCreatedOn", LocalDateTime.class)
				.invoke(entity, LocalDateTime.now());

		return entity;
	}

	@Override
	public <T> List<T> convertToLineandNestedList(EinvoiceRequestDto respDto,
			Gstr1GetInvoicesReqDto dto, GetIrnDtlsRespDto irnDtlDto,
			Long hdrEntityId, Class<T> itemEntity,
			List<GetIrnPreceedingDocDetailEntity> nestedList,
			List<GetIrnContractDetailEntity> contrctList,
			List<GetIrnAdditionalSuppDocsEntity> additonalSuppDocsList,
			List<GetIrnItemAttributeListEntity> itmAttList)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException,
			InstantiationException {

		List<T> itemList = new ArrayList<>();

		// To Set the ITEM LIST

		for (ItemDto item : respDto.getItemList()) {
			T itmEntity = itemEntity.newInstance();
			itmEntity.getClass().getMethod("setHeaderId", Long.class)
					.invoke(itmEntity, hdrEntityId);
			itmEntity.getClass().getMethod("setItemSerialNumber", String.class)
					.invoke(itmEntity,
							item.getSlNo() != null ? item.getSlNo() : null);
			itmEntity.getClass()
					.getMethod("setProductSerialNumber", String.class)
					.invoke(itmEntity, item.getPrdSlNo() != null
							? item.getPrdSlNo() : null);
			itmEntity.getClass()
					.getMethod("setProductDescription", String.class)
					.invoke(itmEntity, item.getPrdDesc() != null
							? item.getPrdDesc() : null);
			itmEntity.getClass().getMethod("setIsService", String.class).invoke(
					itmEntity,
					item.getIsServc() != null ? item.getIsServc() : null);

			itmEntity.getClass()
					.getMethod("setOrderLineReference", String.class)
					.invoke(itmEntity, item.getOrdLineRef() != null
							? item.getOrdLineRef() : null);
			itmEntity.getClass().getMethod("setOriginCountry", String.class)
					.invoke(itmEntity, item.getOrgCntry() != null
							? item.getOrgCntry() : null);
			if (item.getBchDtls() != null) {
				itmEntity.getClass().getMethod("setBatchName", String.class)
						.invoke(itmEntity, item.getBchDtls().getNm() != null
								? item.getBchDtls().getNm() : null);

				itmEntity.getClass()
						.getMethod("setBatchExpiryDate", LocalDateTime.class)
						.invoke(itmEntity,
								item.getBchDtls().getExpDt() != null ? item
										.getBchDtls().getExpDt().atStartOfDay()
										: null);

				itmEntity.getClass()
						.getMethod("setWarrantyDate", LocalDateTime.class)
						.invoke(itmEntity,
								item.getBchDtls().getWrDt() != null ? item
										.getBchDtls().getWrDt().atStartOfDay()
										: null);
			}
			itmEntity.getClass().getMethod("setHsn", String.class).invoke(
					itmEntity,
					item.getHsnCd() != null ? item.getHsnCd() : null);
			itmEntity.getClass().getMethod("setBarcode", String.class).invoke(
					itmEntity,
					item.getBarcde() != null ? item.getBarcde() : null);

			itmEntity.getClass().getMethod("setQuantity", BigDecimal.class)
					.invoke(itmEntity, item.getQty() != null ? item.getQty()
							: BigDecimal.ZERO);

			itmEntity.getClass().getMethod("setFreeQuantity", BigDecimal.class)
					.invoke(itmEntity, item.getFreeQty() != null
							? item.getFreeQty() : BigDecimal.ZERO);

			itmEntity.getClass().getMethod("setUnitPrice", BigDecimal.class)
					.invoke(itmEntity, item.getUnitPrice() != null
							? item.getUnitPrice() : null);

			itmEntity.getClass().getMethod("setPreTaxAmount", BigDecimal.class)
					.invoke(itmEntity, item.getPreTaxVal() != null
							? item.getPreTaxVal() : null);

			itmEntity.getClass().getMethod("setItemAmount", BigDecimal.class)
					.invoke(itmEntity,
							item.getTotAmt() != null ? item.getTotAmt() : null);
			itmEntity.getClass().getMethod("setItemDiscount", BigDecimal.class)
					.invoke(itmEntity, item.getDiscount() != null
							? item.getDiscount() : null);
			itmEntity.getClass()
					.getMethod("setItemAssessableAmt", BigDecimal.class)
					.invoke(itmEntity,
							item.getAssAmt() != null ? item.getAssAmt() : null);
			
			if (item.getIgstAmt() != null
					&& item.getIgstAmt().compareTo(BigDecimal.ZERO) == 1) {
				itmEntity.getClass().getMethod("setIgstRate", BigDecimal.class)
						.invoke(itmEntity, item.getGstRt() != null
								? item.getGstRt() : null);

			}

			if (item.getCgstAmt() != null
					&& item.getCgstAmt().compareTo(BigDecimal.ZERO) == 1
					&& item.getCgstAmt() != null
					&& item.getCgstAmt().compareTo(BigDecimal.ZERO) == 1)

			{
				BigDecimal rate = BigDecimal.ZERO;

				if (item.getGstRt() != null
						&& item.getGstRt().compareTo(BigDecimal.ZERO) == 1) {
					rate = item.getGstRt().divide(BigDecimal.valueOf(2.00), 2);
				}
				itmEntity.getClass().getMethod("setCgstRate", BigDecimal.class)
						.invoke(itmEntity, rate);

				itmEntity.getClass().getMethod("setSgstRate", BigDecimal.class)
						.invoke(itmEntity, rate);

			}
			
			itmEntity.getClass().getMethod("setIgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getIgstAmt() != null
							? item.getIgstAmt() : null);
			itmEntity.getClass().getMethod("setCgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getCgstAmt() != null
							? item.getCgstAmt() : null);
			itmEntity.getClass().getMethod("setSgstAmount", BigDecimal.class)
					.invoke(itmEntity, item.getSgstAmt() != null
							? item.getSgstAmt() : null);
			itmEntity.getClass()
					.getMethod("setCessAdvaloremRate", BigDecimal.class)
					.invoke(itmEntity,
							item.getCesRt() != null ? item.getCesRt() : null);
			itmEntity.getClass()
					.getMethod("setCessAdvaloremAmount", BigDecimal.class)
					.invoke(itmEntity,
							item.getCesAmt() != null ? item.getCesAmt() : null);
			itmEntity.getClass()
					.getMethod("setCessSpecificAmount", BigDecimal.class)
					.invoke(itmEntity, item.getCesNonAdvlAmt() != null
							? item.getCesNonAdvlAmt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessAdvaloremRate", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesRt() != null
							? item.getStateCesRt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessAdvaloremAmount", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesAmt() != null
							? item.getStateCesAmt() : null);
			itmEntity.getClass()
					.getMethod("setStateCessSpecificAmount", BigDecimal.class)
					.invoke(itmEntity, item.getStateCesNonAdvlAmt() != null
							? item.getStateCesNonAdvlAmt() : null);
			itmEntity.getClass()
					.getMethod("setItemOtherCharges", BigDecimal.class)
					.invoke(itmEntity, item.getOthChrg() != null
							? item.getOthChrg() : null);
			itmEntity.getClass()
					.getMethod("setTotalItemAmount", BigDecimal.class)
					.invoke(itmEntity, item.getTotItemVal() != null
							? item.getTotItemVal() : null);
			itmEntity.getClass().getMethod("setUnit", String.class).invoke(
					itmEntity, item.getUnit() != null ? item.getUnit() : null);

			itmEntity.getClass().getMethod("setCreatedOn", LocalDateTime.class)
					.invoke(itmEntity, LocalDateTime.now());
			itmEntity.getClass().getMethod("setCreatedBy", String.class)
					.invoke(itmEntity, "SYSTEM");
			itmEntity.getClass().getMethod("setUqc", String.class).invoke(
					itmEntity,
					item.getUnit() != null ? item.getUnit() : null);
			
			String supplyType = respDto.getTranDtls().getSupTyp();
			Long itmId = null;
			switch(supplyType)
			{
			case "B2B":
				GetIrnB2bItemEntity e1 = getIrnb2bLineRepo.save((GetIrnB2bItemEntity)itmEntity);
				itmId = e1.getId();
				break;
			
			case "DEXP":
				GetIrnDexpItemEntity e2 = getIrnDexpLineRepo
						.save((GetIrnDexpItemEntity) itmEntity);
				itmId = e2.getId();
				break;

			case "EXPWOP":
				GetIrnExpWopItemEntity e3 = getIrnExpWopLineRepo
						.save((GetIrnExpWopItemEntity) itmEntity);
				itmId = e3.getId();
				break;

			case "EXPWP":
				GetIrnExpWpItemEntity e4 = getIrnExpWpLineRepo
						.save((GetIrnExpWpItemEntity) itmEntity);
				itmId = e4.getId();
				break;

			case "SEZWP":
				GetIrnSezWpItemEntity e5 = getIrnSezwpLineRepo
						.save((GetIrnSezWpItemEntity) itmEntity);
				itmId = e5.getId();
				break;

			case "SEZWOP":
				GetIrnSezWopItemEntity e6 = getIrnSezwopLineRepo
						.save((GetIrnSezWopItemEntity) itmEntity);
				itmId = e6.getId();
				break;
			}

			if (item.getAttribDtls() != null) {
				itmAttList.addAll(convertItmAttItem(hdrEntityId, itmId, respDto,
						item.getAttribDtls(), dto));
			}
			
			/*
			Long itmId = Long.valueOf(itmEntity.getClass().getMethod("getId")
					.invoke(itmEntity).toString());
*/
			// taling the id
			// making attribute values persistance
			//

			itemList.add(itmEntity);

		}
		// headerList.add(entity);

		// Preceeding List

		if (respDto.getRefDtls() != null) {
			if (respDto.getRefDtls().getPrecDocDtls() != null) {
				for (PrecDocument nestedItem : respDto.getRefDtls()
						.getPrecDocDtls()) {

					GetIrnPreceedingDocDetailEntity preDocEntity = new GetIrnPreceedingDocDetailEntity();
					preDocEntity.setHeaderId(hdrEntityId);
					preDocEntity.setPreInvNum(nestedItem.getInvNo() != null
							? nestedItem.getInvNo() : null);
					preDocEntity
							.setInvReference(nestedItem.getOthRefNo() != null
									? nestedItem.getOthRefNo() : null);
					preDocEntity.setPreInvDate(nestedItem.getInvDt() != null
							? nestedItem.getInvDt().atStartOfDay() : null);
					preDocEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());
					preDocEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						preDocEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						preDocEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						
						String docTypee = respDto.getDocDtls().getTyp();
						if("CRN".equalsIgnoreCase(docTypee)){
							docTypee="CR";
						}else if("DBN".equalsIgnoreCase(docTypee)){
							docTypee="DR";
						}
						preDocEntity.setDocType(
								docTypee != null
						                ? docTypee : null);
						
					}
					preDocEntity.setReturnPeriod(dto.getReturnPeriod());
					String suppTypee = dto.getType();
					if("DEXP".equalsIgnoreCase(suppTypee)){
						suppTypee="DXP";
					}
					preDocEntity.setSupplyType(suppTypee);	
					preDocEntity.setCreatedBy("SYSTEM");
					preDocEntity.setCreatedOn(LocalDateTime.now());
					preDocEntity.setDelete(false);

					nestedList.add(preDocEntity);

				}
			}

			if (respDto.getRefDtls().getContrDtls() != null) {

				for (Contract contrctItem : respDto.getRefDtls()
						.getContrDtls()) {

					GetIrnContractDetailEntity contrctEntity = new GetIrnContractDetailEntity();
					contrctEntity.setHeaderId(hdrEntityId);
					contrctEntity.setReceiptAdviceReference(
							contrctItem.getRecAdvRefr() != null
									? contrctItem.getRecAdvRefr() : null);
					contrctEntity.setReceiptAdviceDate(
							contrctItem.getRecAdvDt() != null
									? contrctItem.getRecAdvDt().atStartOfDay()
									: null);
					contrctEntity.setTenderReference(
							contrctItem.getTendRef() != null
									? contrctItem.getTendRef() : null);

					contrctEntity.setContractReference(
							contrctItem.getContrRef() != null
									? contrctItem.getContrRef() : null);

					contrctEntity.setExternalReference(
							contrctItem.getExtRef() != null
									? contrctItem.getExtRef() : null);

					contrctEntity.setProjectReference(
							contrctItem.getProjRef() != null
									? contrctItem.getProjRef() : null);

					contrctEntity
							.setCustPoRefNum(contrctItem.getPORefr() != null
									? contrctItem.getPORefr() : null);

					contrctEntity
							.setCustPoRefDate(contrctItem.getPORefDt() != null
									? contrctItem.getPORefDt().atStartOfDay()
									: null);

					contrctEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());

					contrctEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						contrctEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						contrctEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						
						String docTy = respDto.getDocDtls().getTyp();
						if("CRN".equalsIgnoreCase(docTy)){
							docTy="CR";
						}else if("DBN".equalsIgnoreCase(docTy)){
							docTy="DR";
						}
						contrctEntity.setDocType(
								docTy != null
						                ? docTy : null);
						
					}
					contrctEntity.setReturnPeriod(dto.getReturnPeriod());
					String supTyp = dto.getType();
					if("DEXP".equalsIgnoreCase(supTyp)){
						supTyp="DXP";
					}
					contrctEntity.setDerivedRetPeriod(Integer
							.valueOf(dto.getReturnPeriod().substring(2, 6)
									+ dto.getReturnPeriod().substring(0, 2)));

					contrctEntity.setSupplyType(supTyp);
					contrctEntity.setCreatedBy("SYSTEM");
					contrctEntity.setCreatedOn(LocalDateTime.now());
					contrctList.add(contrctEntity);

				}

			}
			if (respDto.getAddlDocDtls() != null) {
				for (AddlDocument adlDocEntity : respDto.getAddlDocDtls()) {

					GetIrnAdditionalSuppDocsEntity addlDocEntity = new GetIrnAdditionalSuppDocsEntity();
					addlDocEntity.setHeaderId(hdrEntityId);
					addlDocEntity.setSuppDocUrl(adlDocEntity.getUrl() != null
							? adlDocEntity.getUrl() : null);
					addlDocEntity
							.setSuppDocBase64(adlDocEntity.getDocs() != null
									? adlDocEntity.getDocs() : null);
					addlDocEntity.setAddInfo(adlDocEntity.getInfo() != null
							? adlDocEntity.getInfo() : null);

					addlDocEntity.setSupplierGstin(
							respDto.getBuyerDtls().getGstin());

					addlDocEntity.setBatchId(dto.getBatchId());

					if (respDto.getDocDtls() != null) {
						addlDocEntity
								.setDocNum(respDto.getDocDtls().getNo() != null
										? respDto.getDocDtls().getNo() : null);
						addlDocEntity.setDocDate(
								respDto.getDocDtls().getDt() != null ? respDto
										.getDocDtls().getDt().atStartOfDay()
										: null);
						
						String typ = respDto.getDocDtls().getTyp();
						if("CRN".equalsIgnoreCase(typ)){
							typ="CR";
						}else if("DBN".equalsIgnoreCase(typ)){
							typ="DR";
						}
						addlDocEntity.setDocType(
								typ != null
						                ? typ : null);
						
					}
					addlDocEntity.setReturnPeriod(dto.getReturnPeriod());

					addlDocEntity.setDerivedRetPeriod(Integer
							.valueOf(dto.getReturnPeriod().substring(2, 6)
									+ dto.getReturnPeriod().substring(0, 2)));

					String supTy = dto.getType();
					if("DEXP".equalsIgnoreCase(supTy)){
						supTy="DXP";
					}
					addlDocEntity.setSupplyType(supTy);
					addlDocEntity.setCreatedBy("SYSTEM");
					addlDocEntity.setCreatedOn(LocalDateTime.now());
					additonalSuppDocsList.add(addlDocEntity);

				}
			}
		}
		
		 if (LOGGER.isDebugEnabled()) {
	 		    LOGGER.debug("itemList: {}", itemList);
	 		}
		return itemList;
	}

	private List<GetIrnItemAttributeListEntity> convertItmAttItem(
			Long hdrEntityId, Long itmId, EinvoiceRequestDto respDto,
			List<Attribute> attItm, Gstr1GetInvoicesReqDto dto) {

		List<GetIrnItemAttributeListEntity> itemAttList = new ArrayList<>();

		for (Attribute attItmEntity : attItm) {
			GetIrnItemAttributeListEntity itmAttDto = new GetIrnItemAttributeListEntity();
			itmAttDto.setHeaderId(hdrEntityId);
			itmAttDto.setItmId(itmId);

			itmAttDto.setSupplierGstin(respDto.getBuyerDtls().getGstin());

			itmAttDto.setBatchId(dto.getBatchId());

			itmAttDto.setAttrName(
					attItmEntity.getNm() != null ? attItmEntity.getNm() : null);

			itmAttDto.setAttrValue(attItmEntity.getVal() != null
					? attItmEntity.getVal() : null);

			if (respDto.getDocDtls() != null) {
				itmAttDto.setDocNum(respDto.getDocDtls().getNo() != null
						? respDto.getDocDtls().getNo() : null);
				itmAttDto.setDocDate(respDto.getDocDtls().getDt() != null
						? respDto.getDocDtls().getDt().atStartOfDay() : null);
				
				String typee = respDto.getDocDtls().getTyp();
				if("CRN".equalsIgnoreCase(typee)){
					typee="CR";
				}else if("DBN".equalsIgnoreCase(typee)){
					typee="DR";
				}
				itmAttDto.setDocType(
						typee != null
				                ? typee : null);
				
			}
			itmAttDto.setReturnPeriod(dto.getReturnPeriod());
			String supTypeee = dto.getType();
			if("DEXP".equalsIgnoreCase(supTypeee)){
				supTypeee="DXP";
			}
			itmAttDto.setDerivedRetPeriod(
					Integer.valueOf(dto.getReturnPeriod().substring(2, 6)
							+ dto.getReturnPeriod().substring(0, 2)));

			itmAttDto.setSupplyType(supTypeee);
			itmAttDto.setCreatedBy("SYSTEM");
			itmAttDto.setCreatedOn(LocalDateTime.now());
			itemAttList.add(itmAttDto);
		}

		return itemAttList;
	}

}
