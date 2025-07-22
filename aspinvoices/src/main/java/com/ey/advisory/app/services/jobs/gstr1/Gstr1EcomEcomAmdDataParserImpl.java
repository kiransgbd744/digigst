package com.ey.advisory.app.services.jobs.gstr1;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupAmdHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupAmdItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EcomSupItemEntity;
import com.ey.advisory.app.docs.dto.B2BEcomInvoices;
import com.ey.advisory.app.docs.dto.B2BInvoiceData;
import com.ey.advisory.app.docs.dto.B2CSALineItem;
import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.B2bLineItem;
import com.ey.advisory.app.docs.dto.B2bLineItemDetail;
import com.ey.advisory.app.docs.dto.Ecom;
import com.ey.advisory.app.docs.dto.EcomAmd;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.dto.Gstr1GetInvoicesReqDto;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Component("Gstr1EcomEcomAmdDataParserImpl")
@Slf4j
public class Gstr1EcomEcomAmdDataParserImpl
		implements Gstr1EcomEcomAmdDataParser {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	@Override
	public List<GetGstr1EcomSupHeaderEntity> parseEcomData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		//JsonArray respObject = null;
		JsonObject respObject = null;
		List<GetGstr1EcomSupHeaderEntity> headerList = new ArrayList<>();

		try {
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				LOGGER.error("API Resp is Null, hence returning Null.");
				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("API Response is {} ", apiResp);
			}

			/*respObject = JsonParser.parseString(apiResp).getAsJsonObject()
					.get(APIConstants.ECOM).getAsJsonArray();*/
			 respObject = JsonParser.parseString(apiResp).getAsJsonObject();

			if (respObject == null) {
				return null;
			}

			//Ecom baseEntity = gson.fromJson(respObject, Ecom.class);
				
		     Ecom baseEntity = gson.fromJson(respObject.getAsJsonObject(APIConstants.ECOM), Ecom.class);


			List<B2BEcomInvoices> b2bEcomInv = baseEntity.getB2b();
			List<B2BEcomInvoices> urp2bEcomInv = baseEntity.getUrp2b();
			List<B2CSInvoices> b2cEcomInv = baseEntity.getB2c();
			List<B2CSInvoices> urpb2cEcomInv = baseEntity.getUrp2c();
			
			// List of Header and Item Data
			
			if (b2bEcomInv != null) {
			    for (B2BEcomInvoices eachInv : b2bEcomInv) {
			        if (eachInv != null && eachInv.getB2bInvoiceData() != null) {
			            for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
			                if (eachInvData != null) {
			                    GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
			                    setEcomTrans(header, eachInv, eachInvData, dto, GSTConstants.GSTR1_15I);
			                    headerList.add(header);
			                }
			            }
			        }
			    }
			}

			if (urp2bEcomInv != null) {
			    for (B2BEcomInvoices eachInv : urp2bEcomInv) {
			        if (eachInv != null && eachInv.getB2bInvoiceData() != null) {
			            for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
			                if (eachInvData != null) {
			                    GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
			                    setEcomTrans(header, eachInv, eachInvData, dto, GSTConstants.GSTR1_15III);
			                    headerList.add(header);
			                }
			            }
			        }
			    }
			}

			if (b2cEcomInv != null) {
			    for (B2CSInvoices eachInv : b2cEcomInv) {
			        if (eachInv != null) {
			            GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
			            setEcomSumm(dto, eachInv, header, GSTConstants.GSTR1_15II);
			            headerList.add(header);
			        }
			    }
			}

			if (urpb2cEcomInv != null) {
			    for (B2CSInvoices eachInv : urpb2cEcomInv) {
			        if (eachInv != null) {
			            GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
			            setEcomSumm(dto, eachInv, header, GSTConstants.GSTR1_15IV);
			            headerList.add(header);
			        }
			    }
			}

			
			/*for (B2BEcomInvoices eachInv : b2bEcomInv) {
				for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
					GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
					setEcomTrans(header, eachInv, eachInvData, dto,
							GSTConstants.GSTR1_15I);
					headerList.add(header);
				}
			}
			for (B2BEcomInvoices eachInv : urp2bEcomInv) {
				for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
					GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
					setEcomTrans(header, eachInv, eachInvData, dto,
							GSTConstants.GSTR1_15III);
					headerList.add(header);
				}
			}
			for (B2CSInvoices eachInv : b2cEcomInv) {
				GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
				setEcomSumm(dto, eachInv, header, GSTConstants.GSTR1_15II);
				headerList.add(header);
			}
			for (B2CSInvoices eachInv : urpb2cEcomInv) {
				GetGstr1EcomSupHeaderEntity header = new GetGstr1EcomSupHeaderEntity();
				setEcomSumm(dto, eachInv, header, GSTConstants.GSTR1_15IV);
				headerList.add(header);
			}*/

			return headerList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception in Ecom DataParsing for GSTIN %s and TaxPeriod %s",
					dto.getGstin(), dto.getReturnPeriod());
			LOGGER.error(errMsg, e);
			throw new APIException(errMsg);
		}
	}

	@Override
	public List<GetGstr1EcomSupAmdHeaderEntity> parseEcomAmdData(
			Gstr1GetInvoicesReqDto dto, String apiResp) {

		Gson gson = GsonUtil.newSAPGsonInstance();
		JsonArray respObject = null;
		List<GetGstr1EcomSupAmdHeaderEntity> headerList = new ArrayList<>();

		try {
			JsonObject asJsonObject = JsonParser.parseString(apiResp)
					.getAsJsonObject();
			if (asJsonObject == null) {
				LOGGER.error("API Resp is Null, hence returning Null.");
				return null;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("API Response is {} ", apiResp);
			}

			respObject = JsonParser.parseString(apiResp).getAsJsonObject()
					.get(APIConstants.ECOMAMD).getAsJsonArray();
			if (respObject == null) {
				return null;
			}

			EcomAmd baseEntity = gson.fromJson(respObject, EcomAmd.class);

			List<B2BEcomInvoices> b2bEcomInv = baseEntity.getB2ba();
			List<B2BEcomInvoices> urp2bEcomInv = baseEntity.getUrp2ba();
			List<B2CSInvoices> b2cEcomInv = baseEntity.getB2ca();
			List<B2CSInvoices> urpb2cEcomInv = baseEntity.getUrp2ca();
			// List of Header and Item Data
			
			if (b2bEcomInv != null) {
			    for (B2BEcomInvoices eachInv : b2bEcomInv) {
			        if (eachInv != null && eachInv.getB2bInvoiceData() != null) {
			            for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
			                if (eachInvData != null) {
			                    GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
			                    setEcomAmdTrans(header, eachInv, eachInvData, dto, GSTConstants.GSTR1_15AIA);
			                    headerList.add(header);
			                }
			            }
			        }
			    }
			}

			if (urp2bEcomInv != null) {
			    for (B2BEcomInvoices eachInv : urp2bEcomInv) {
			        if (eachInv != null && eachInv.getB2bInvoiceData() != null) {
			            for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
			                if (eachInvData != null) {
			                    GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
			                    setEcomAmdTrans(header, eachInv, eachInvData, dto, GSTConstants.GSTR1_15AIB);
			                    headerList.add(header);
			                }
			            }
			        }
			    }
			}

			if (b2cEcomInv != null) {
			    for (B2CSInvoices eachInv : b2cEcomInv) {
			        if (eachInv != null) {
			            GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
			            setEcomAmdSumm(dto, eachInv, header, GSTConstants.GSTR1_15AIIA);
			            headerList.add(header);
			        }
			    }
			}

			if (urpb2cEcomInv != null) {
			    for (B2CSInvoices eachInv : urpb2cEcomInv) {
			        if (eachInv != null) {
			            GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
			            setEcomAmdSumm(dto, eachInv, header, GSTConstants.GSTR1_15AIIB);
			            headerList.add(header);
			        }
			    }
			}


			/*for (B2BEcomInvoices eachInv : b2bEcomInv) {
				for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
					GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
					setEcomAmdTrans(header, eachInv, eachInvData, dto,
							GSTConstants.GSTR1_15AIA);
					headerList.add(header);
				}
			}
			for (B2BEcomInvoices eachInv : urp2bEcomInv) {
				for (B2BInvoiceData eachInvData : eachInv.getB2bInvoiceData()) {
					GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
					setEcomAmdTrans(header, eachInv, eachInvData, dto,
							GSTConstants.GSTR1_15AIB);
					headerList.add(header);
				}
			}

			for (B2CSInvoices eachInv : b2cEcomInv) {
				GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
				setEcomAmdSumm(dto, eachInv, header, GSTConstants.GSTR1_15AIIA);
				headerList.add(header);
			}
			for (B2CSInvoices eachInv : urpb2cEcomInv) {
				GetGstr1EcomSupAmdHeaderEntity header = new GetGstr1EcomSupAmdHeaderEntity();
				setEcomAmdSumm(dto, eachInv, header, GSTConstants.GSTR1_15AIIB);
				headerList.add(header);
			}*/

			return headerList;
		} catch (Exception e) {
			String errMsg = String.format(
					"Exception in Ecom DataParsing for GSTIN %s and TaxPeriod %s",
					dto.getGstin(), dto.getReturnPeriod());
			LOGGER.error(errMsg, e);
			throw new APIException(errMsg);
		}
	}

	private void setEcomTrans(GetGstr1EcomSupHeaderEntity header,
			B2BEcomInvoices eachInv, B2BInvoiceData eachInvData,
			Gstr1GetInvoicesReqDto dto, String type) {

		BigDecimal taxRate = BigDecimal.ZERO;
		BigDecimal taxValue = BigDecimal.ZERO;
		BigDecimal igstAmt = BigDecimal.ZERO;
		BigDecimal cgstAmt = BigDecimal.ZERO;
		BigDecimal sgstAmt = BigDecimal.ZERO;
		BigDecimal cessAmt = BigDecimal.ZERO;
		List<GetGstr1EcomSupItemEntity> lineItems = new ArrayList<>();

		if (eachInvData.getLineItems() != null) {
			for (B2bLineItem b2cItems : eachInvData.getLineItems()) {
				B2bLineItemDetail b2cItem = b2cItems.getItemDetail();
				// New Item Entity
				GetGstr1EcomSupItemEntity item = new GetGstr1EcomSupItemEntity();
				item.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					item.setDerivedTaxperiod(GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				item.setTaxRate(b2cItem.getRate());
				item.setTaxValue(b2cItem.getTaxableValue());
				item.setIgstAmt(b2cItem.getIgstAmount());
				item.setCgstAmt(b2cItem.getCgstAmount());
				item.setSgstAmt(b2cItem.getSgstAmount());
				item.setCessAmt(b2cItem.getCessAmount());
				item.setInvValue(eachInvData.getInvoiceValue());
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
			/**
			 * Input data
			 */
			header.setGstin(dto.getGstin());
			header.setReturnPeriod(dto.getReturnPeriod());
			header.setChksum(eachInvData.getCheckSum());
			if (dto.getReturnPeriod() != null
					&& dto.getReturnPeriod().length() > 0) {
				header.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			}
			header.setBatchId(dto.getBatchId());
			if (type.equalsIgnoreCase(GSTConstants.GSTR1_15I)) {
				header.setTableSection(GSTConstants.GSTR1_15I.concat("- B2B"));
			} else {
				header.setTableSection(
						GSTConstants.GSTR1_15III.concat("- URP2B"));
			}
			/**
			 * B2BInvoices data
			 */
			header.setChksum(eachInvData.getCheckSum());
			header.setFlag(eachInvData.getInvoiceStatus());
			header.setInvNum(eachInvData.getInvoiceNumber());
			String invDate = eachInvData.getInvoiceDate();
			if (invDate != null) {
				header.setInvDate(EYDateUtil.fmtDateOnly(invDate,
						DateUtil.SUPPORTED_DATE_FORMAT2,
						DateUtil.SUPPORTED_DATE_FORMAT1));
			}
			header.setPos(eachInvData.getPos());
			header.setInvValue(eachInvData.getInvoiceValue());
			header.setInvType(eachInvData.getInvoiceType());
			header.setSupplyType(eachInvData.getSupplyType());
			header.setRTin(eachInv.getRtin());
			header.setSTin(eachInv.getStin());
			header.setCreatedOn(LocalDateTime.now());
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
			String docType = eachInvData.getInvoiceType();
			String finYear = GenUtil.getFinYear(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
			header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear,
					docType));
			header.setLineItems(lineItems);
			lineItems.forEach(item -> {
				item.setDocument(header);
			});

		}
	}

	private void setEcomAmdTrans(GetGstr1EcomSupAmdHeaderEntity header,
			B2BEcomInvoices eachInv, B2BInvoiceData eachInvData,
			Gstr1GetInvoicesReqDto dto, String type) {
		BigDecimal taxRate = BigDecimal.ZERO;
		BigDecimal taxValue = BigDecimal.ZERO;
		BigDecimal igstAmt = BigDecimal.ZERO;
		BigDecimal cgstAmt = BigDecimal.ZERO;
		BigDecimal sgstAmt = BigDecimal.ZERO;
		BigDecimal cessAmt = BigDecimal.ZERO;
		List<GetGstr1EcomSupAmdItemEntity> lineItems = new ArrayList<>();

		if (eachInvData.getLineItems() != null) {

			for (B2bLineItem b2bAItems : eachInvData.getLineItems()) {

				B2bLineItemDetail b2bAItem = b2bAItems.getItemDetail();
				// New Item Entity
				GetGstr1EcomSupAmdItemEntity item = new GetGstr1EcomSupAmdItemEntity();

				item.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					item.setDerivedTaxperiod(GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				item.setTaxRate(b2bAItem.getRate());
				item.setTaxValue(b2bAItem.getTaxableValue());
				item.setIgstAmt(b2bAItem.getIgstAmount());
				item.setCgstAmt(b2bAItem.getCgstAmount());
				item.setSgstAmt(b2bAItem.getSgstAmount());
				item.setCessAmt(b2bAItem.getCessAmount());
				item.setInvValue(eachInvData.getInvoiceValue());
				item.setSerialNum(b2bAItems.getLineNumber());

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

			if (type.equalsIgnoreCase(GSTConstants.GSTR1_15AIA)) {
				header.setTableSection(
						GSTConstants.GSTR1_15AIA.concat("- B2B"));
			} else {
				header.setTableSection(
						GSTConstants.GSTR1_15AIB.concat("- URP2B"));
			}
			header.setAction(APIConstants.N);
			header.setGstin(dto.getGstin());
			header.setReturnPeriod(dto.getReturnPeriod());
			if (dto.getReturnPeriod() != null
					&& dto.getReturnPeriod().length() > 0) {
				header.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
			}
			header.setBatchId(dto.getBatchId());

			header.setFlag(eachInvData.getInvoiceStatus());
			header.setInvNum(eachInvData.getInvoiceNumber());
			String invDate = eachInvData.getInvoiceDate();
			header.setInvDate(EYDateUtil.fmtDateOnly(invDate,
					DateUtil.SUPPORTED_DATE_FORMAT2,
					DateUtil.SUPPORTED_DATE_FORMAT1));

			header.setOrgInvNum(eachInvData.getOrigInvNumber());
			header.setOrgInvDate(
					EYDateUtil.fmtDateOnly(eachInvData.getOrigInvDate(),
							DateUtil.SUPPORTED_DATE_FORMAT2,
							DateUtil.SUPPORTED_DATE_FORMAT1));

			header.setPos(eachInvData.getPos());
			header.setOrgPeriod(eachInvData.getOpd());
			header.setInvValue(eachInvData.getInvoiceValue());
			header.setInvType(eachInvData.getInvoiceType());
			header.setCtin(eachInv.getRtin());
			header.setGstin(eachInv.getStin());
			header.setCreatedOn(LocalDateTime.now());
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
			String docType = eachInvData.getInvoiceType();
			String finYear = GenUtil.getFinYear(
					LocalDate.parse(docDate, DateUtil.SUPPORTED_DATE_FORMAT2));
			header.setDocKey(docKeyGenerator.generateKey(sgtin, docNum, finYear,
					docType));

			header.setLineItems(lineItems);
			lineItems.forEach(item -> {
				item.setDocument(header);
			});
		}
	}

	private void setEcomSumm(Gstr1GetInvoicesReqDto dto,
			B2CSInvoices b2csInvoices, GetGstr1EcomSupHeaderEntity header,
			String type) {
		header.setGstin(dto.getGstin());
		header.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			header.setDerivedTaxperiod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		header.setBatchId(dto.getBatchId());
		header.setSTin(b2csInvoices.getStin());
		header.setPos(b2csInvoices.getPointOfSupply());
		header.setChksum(b2csInvoices.getCheckSum());
		header.setFlag(b2csInvoices.getInvoiceStatus());
		header.setTaxValue(b2csInvoices.getTaxableValue());
		header.setSupplyType(b2csInvoices.getSupplyType());
		header.setIgstAmt(b2csInvoices.getIgstAmount());
		header.setCgstAmt(b2csInvoices.getCgstAmount());
		header.setSgstAmt(b2csInvoices.getSgstAmount());
		header.setCessAmt(b2csInvoices.getCessAmount());
		header.setTaxRate(b2csInvoices.getRate());
		if (type.equalsIgnoreCase(GSTConstants.GSTR1_15II)) {
			header.setTableSection(GSTConstants.GSTR1_15II.concat("- B2C"));
		} else {
			header.setTableSection(GSTConstants.GSTR1_15IV.concat("- URP2C"));
		}
		header.setCreatedOn(LocalDateTime.now());
		header.setCreatedBy("SYSTEM");
	}

	private void setEcomAmdSumm(Gstr1GetInvoicesReqDto dto,
			B2CSInvoices eachInv, GetGstr1EcomSupAmdHeaderEntity header,
			String type) {

		BigDecimal taxVal = BigDecimal.ZERO;
		BigDecimal igstAmt = BigDecimal.ZERO;
		BigDecimal cgstAmt = BigDecimal.ZERO;
		BigDecimal sgstAmt = BigDecimal.ZERO;
		BigDecimal cessAmt = BigDecimal.ZERO;

		List<GetGstr1EcomSupAmdItemEntity> lineItems = new ArrayList<>();
		if (eachInv.getLineItems() != null) {
			for (B2CSALineItem eachInvData : eachInv.getLineItems()) {

				GetGstr1EcomSupAmdItemEntity item = new GetGstr1EcomSupAmdItemEntity();

				item.setReturnPeriod(dto.getReturnPeriod());
				if (dto.getReturnPeriod() != null
						&& dto.getReturnPeriod().length() > 0) {
					item.setDerivedTaxperiod(GenUtil
							.convertTaxPeriodToInt(dto.getReturnPeriod()));
				}
				item.setTaxRate(eachInvData.getRate());
				item.setTaxValue(eachInvData.getTaxableValue());
				item.setIgstAmt(eachInvData.getIgstAmount());
				item.setCgstAmt(eachInvData.getCgstAmount());
				item.setSgstAmt(eachInvData.getSgstAmount());
				item.setCessAmt(eachInvData.getCessAmount());

				lineItems.add(item);
				// Header Amounts count
				if (item.getTaxValue() != null) {
					taxVal = taxVal.add(item.getTaxValue());
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
		}

		header.setGstin(dto.getGstin());
		header.setReturnPeriod(dto.getReturnPeriod());
		if (dto.getReturnPeriod() != null
				&& dto.getReturnPeriod().length() > 0) {
			header.setDerivedTaxperiod(
					GenUtil.convertTaxPeriodToInt(dto.getReturnPeriod()));
		}
		header.setBatchId(dto.getBatchId());

		/**
		 * B2CSAInvoices data
		 */

		header.setPos(eachInv.getPointOfSupply());
		header.setSupplyType(eachInv.getSupplyType());
		header.setOrgPeriod(eachInv.getOrgMonthInv());
		header.setPos(eachInv.getPointOfSupply());
		header.setOrgSuppGstin(eachInv.getOstin());
		header.setOrgCustGstin(eachInv.getOrtin());
		header.setChksum(eachInv.getCheckSum());
		// taxes at header level by summing the item values

		header.setTaxValue(taxVal);
		header.setIgstAmt(igstAmt);
		header.setCgstAmt(cgstAmt);
		header.setSgstAmt(sgstAmt);
		header.setCessAmt(cessAmt);
		header.setCreatedOn(LocalDateTime.now());
		header.setCreatedBy("SYSTEM");
		if (type.equalsIgnoreCase(GSTConstants.GSTR1_15AIIA)) {
			header.setTableSection(GSTConstants.GSTR1_15AIIA.concat("- B2C"));
		} else {
			header.setTableSection(GSTConstants.GSTR1_15AIIB.concat("- URP2C"));
		}
		// String sgtin = dto.getGstin();
		// String retPeriod = dto.getReturnPeriod();
		// String month = eachInv.getOrgMonthInv();
		// String newPos = eachInv.getPointOfSupply();
		//
		// doubt

		// header.setDocKey(gstr1GetKeyGenerator.generateB2csaKey(sgtin,
		// retPeriod,
		// type, month, newPos, eGstin));

		header.setLineItems(lineItems);
		lineItems.forEach(item -> {
			item.setDocument(header);
		});

	}
}
