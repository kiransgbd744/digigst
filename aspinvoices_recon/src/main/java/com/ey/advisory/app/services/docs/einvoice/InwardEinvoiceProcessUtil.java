package com.ey.advisory.app.services.docs.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aspose.cells.DateTime;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.inward.einvoice.InwardEinvoiceDataDto;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.icu.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */
@Component
@Slf4j
public class InwardEinvoiceProcessUtil {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	public List<InwardEinvoiceDataDto> convertInwardEinvoiceWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod) {
		List<InwardEinvoiceDataDto> inwardEinvoiceDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				InwardEinvoiceDataDto dto = new InwardEinvoiceDataDto();
		        dto.setIrnGenerationPeriod(obj[0] != null ? (String) obj[0] : null);
		        if (obj[1] != null) {
		            String status = (String) obj[1];
		            if ("Active".equals(status)) {
		                dto.setIrnStatus("ACT");
		            } else if ("Cancelled".equals(status)) {
		                dto.setIrnStatus("CNL");
		            } else {
		                dto.setIrnStatus(status);
		            }
		        } else {
		            dto.setIrnStatus(null);
		        }

		        dto.setIrnNumber(obj[2] != null ? (String) obj[2] : null);
		        String irnDate = null;
				try {
					irnDate = StringUtils.isEmpty((String) obj[3]) ? null
							: removeQuotes(obj[3].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[3];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					irnDate = dateFormat.format(date1);
				}
				LocalDateTime invoiceDate8 = null;
				if (irnDate != null) {
				 invoiceDate8 = DateUtil.stringToTime(
							irnDate,
							DateUtil.DATE_FORMAT1);
					dto.setIrnDate(invoiceDate8);
				}
		        if (obj[4] != null) {
		            if (obj[4] instanceof String) {
		                dto.setAcknowledgmentNumber(Long.parseLong((String) obj[4]));
		            } else if (obj[4] instanceof Long) {
		                dto.setAcknowledgmentNumber((Long) obj[4]);
		            } else if (obj[4] instanceof Integer) {
		                dto.setAcknowledgmentNumber(((Integer) obj[4]).longValue());
		            } else if (obj[4] instanceof Double) {
		                dto.setAcknowledgmentNumber(((Double) obj[4]).longValue());
		            } else {
		                throw new ClassCastException("Unsupported data type for acknowledgment number: " + obj[4].getClass().getName());
		            }
		        } else {
		            dto.setAcknowledgmentNumber(null);
		        }
		        
		       String irnCancellationDate = null;
				try {
					irnCancellationDate = obj[5] == null ? null
							: removeQuotes(obj[5].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[5];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					irnCancellationDate = dateFormat.format(date1);
				}
				LocalDateTime invoiceDate21 = null;
				if (irnCancellationDate != null) {
					invoiceDate21 = DateUtil.stringToTime(
							irnCancellationDate,
							DateUtil.DATE_FORMAT1);
					dto.setIrnCancellationDate(invoiceDate21);
				}
		        dto.setCancellationReason(obj[6] != null ? (String) obj[6] : null);
		        dto.setCancellationRemarks(obj[7] != null ? (String) obj[7] : null);
		        if (obj[8] != null) {
		            if (obj[8] instanceof String) {
		                dto.setEWayBillNumber(Long.parseLong((String) obj[8]));
		            } else if (obj[8] instanceof Long) {
		                dto.setEWayBillNumber((Long) obj[8]);
		            } else if (obj[8] instanceof Integer) {
		                dto.setEWayBillNumber(((Integer) obj[8]).longValue());
		            } else if (obj[8] instanceof Double) {
		                dto.setEWayBillNumber(((Double) obj[8]).longValue());
		            } else {
		                throw new ClassCastException("Unsupported data type for EWayBillNumber: " + obj[8].getClass().getName());
		            }
		        } else {
		            dto.setEWayBillNumber(null);
		        }
		        String eWayBillDate = null;
				try {
					eWayBillDate = obj[9] == null ? null
							: removeQuotes(obj[9].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[9];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					eWayBillDate = dateFormat.format(date1);
				}
				LocalDateTime invoiceDate100 = null;
				if (eWayBillDate != null) {
					invoiceDate100 = DateUtil.stringToTime(
							eWayBillDate,
								DateUtil.DATE_FORMAT1);
					dto.setEWayBillDate(invoiceDate100);
				}
		        
		        String validUpto = null;
				try {
					validUpto = obj[10] == null ? null
							: removeQuotes(obj[10].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[10];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					validUpto = dateFormat.format(date1);
				}
				LocalDateTime invoiceDate22 = null;
				if (validUpto != null) {
					invoiceDate22 = DateUtil.stringToTime(
							validUpto,
							DateUtil.DATE_FORMAT1);
					dto.setValidUpto(invoiceDate22);
				}
		        dto.setTaxScheme(obj[11] != null ? (String) obj[11] : null);
		        dto.setSupplyType(obj[12] != null ? (String) obj[12] : null);
		        dto.setDocumentType(obj[13] != null ? (String) obj[13] : null);
		        dto.setDocumentNumber(obj[14] != null ? (String) obj[14] : null);
		        
		        String documentDate = null;
				try {
					documentDate = removeQuotes(obj[15].toString());
					if (LOGGER.isDebugEnabled()) {
					    LOGGER.debug("Data from Excel documentDate: {}",
					            documentDate);
					}

				    if (documentDate != null) {
				        LocalDate invoiceDate;
				        try {
				            // Try parsing with the expected format "dd/MM/yyyy"
				            invoiceDate = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT4);
				        } catch (DateTimeParseException e1) {
				            try {
				                // Try parsing with the format "yyyy/MM/dd"
				                invoiceDate = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT3);
				            } catch (DateTimeParseException e2) {
				                try {
				                    // Try parsing with the format "yyyy-MM-dd"
				                    invoiceDate = LocalDate.parse(documentDate,DateUtil.SUPPORTED_DATE_FORMAT1);
				                } catch (DateTimeParseException e3) {
				                    // Try parsing with the format "dd-MM-yyyy"
				                    invoiceDate = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT2);
				                }
				            }
				        }
				        dto.setDocumentDate(invoiceDate);
				    }
				} catch (ClassCastException | DateTimeParseException e) {
					DateTime dateTime = (DateTime) obj[15];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					documentDate = dateFormat.format(date1);
				}

		        dto.setReverseChargeFlag(obj[16] != null ? (String) obj[16] : null);
		        dto.setSupplierGstin(obj[17] != null ? (String) obj[17] : null);
		        dto.setSupplierTradeName(obj[18] != null ? (String) obj[18] : null);
		        dto.setSupplierLegalName(obj[19] != null ? (String) obj[19] : null);
		        dto.setSupplierAddress1(obj[20] != null ? (String) obj[20] : null);
		        dto.setSupplierAddress2(obj[21] != null ? (String) obj[21] : null);
		        dto.setSupplierLocation(obj[22] != null ? (String) obj[22] : null);
		        if (obj[23] != null) {
		            if (obj[23] instanceof String) {
		                dto.setSupplierPincode((String) obj[23]);
		            } else if (obj[23] instanceof Integer) {
		                dto.setSupplierPincode(obj[23].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[23]: " + obj[23].getClass().getName());
		            }
		        } else {
		            dto.setSupplierPincode(null);
		        }

		        if (obj[24] != null) {
		            if (obj[24] instanceof String) {
		                dto.setSupplierStateCode((String) obj[24]);
		            } else if (obj[24] instanceof Integer) {
		                dto.setSupplierStateCode(obj[24].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[24]: " + obj[24].getClass().getName());
		            }
		        } else {
		            dto.setSupplierStateCode(null);
		        }
		        if (obj[25] != null) {
		            if (obj[25] instanceof String) {
		                dto.setSupplierPhone(removeQuotes(obj[25].toString()));
		            } else if (obj[25] instanceof Integer) {
		                dto.setSupplierPhone(String.valueOf(removeQuotes(obj[25].toString())));
		            } else if (obj[25] instanceof Long) {
		                dto.setSupplierPhone(String.valueOf(removeQuotes(obj[25].toString())));
		            }  else if (obj[25] instanceof Double) {
		                dto.setSupplierPhone(String.valueOf(removeQuotes(obj[25].toString())));
		            } else {
		                throw new IllegalArgumentException("Unexpected type for supplier phone");
		            }
		        } else {
		            dto.setSupplierPhone(null);
		        }
		        dto.setSupplierEmail(obj[26] != null ? (String) obj[26] : null);
		        dto.setCustomerGstin(obj[27] != null ? (String) obj[27] : null);
		        dto.setCustomerTradeName(obj[28] != null ? (String) obj[28] : null);
		        dto.setCustomerLegalName(obj[29] != null ? (String) obj[29] : null);
		        dto.setCustomerAddress1(obj[30] != null ? (String) obj[30] : null);
		        dto.setCustomerAddress2(obj[31] != null ? (String) obj[31] : null);
		        dto.setCustomerLocation(obj[32] != null ? (String) obj[32] : null);
		        if (obj[33] != null) {
		            if (obj[33] instanceof String) {
		                dto.setCustomerPincode((String) obj[33]);
		            } else if (obj[33] instanceof Integer) {
		                dto.setCustomerPincode(obj[33].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[33]: " + obj[33].getClass().getName());
		            }
		        } else {
		            dto.setCustomerPincode(null);
		        }

		        if (obj[34] != null) {
		            if (obj[34] instanceof String) {
		                dto.setCustomerStateCode((String) obj[34]);
		            } else if (obj[34] instanceof Integer) {
		                dto.setCustomerStateCode(obj[34].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[34]: " + obj[34].getClass().getName());
		            }
		        } else {
		            dto.setCustomerStateCode(null);
		        }
		        if (obj[35] != null) {
		            if (obj[35] instanceof String) {
		                dto.setBillingPos((String) obj[35]);
		            } else if (obj[35] instanceof Integer) {
		                dto.setBillingPos(obj[35].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[35]: " + obj[35].getClass().getName());
		            }
		        } else {
		            dto.setBillingPos(null);
		        }

		        if (obj[36] != null) {
		            if (obj[36] instanceof String) {
		                dto.setCustomerPhone(removeQuotes(obj[36].toString()));
		            } else if (obj[36] instanceof Integer) {
		                dto.setCustomerPhone(String.valueOf(removeQuotes(obj[36].toString())));
		            } else if (obj[36] instanceof Long) {
		                dto.setCustomerPhone(String.valueOf(removeQuotes(obj[36].toString())));
		            }  else if (obj[36] instanceof Double) {
		                dto.setCustomerPhone(String.valueOf(removeQuotes(obj[36].toString())));
		            } else {
		                throw new IllegalArgumentException("Unexpected type for customer phone");
		            }
		        } else {
		            dto.setCustomerPhone(null);
		        }
		        dto.setCustomerEmail(obj[37] != null ? (String) obj[37] : null);
		        dto.setDispatcherTradeName(obj[38] != null ? (String) obj[38] : null);
		        dto.setDispatcherAddress1(obj[39] != null ? (String) obj[39] : null);
		        dto.setDispatcherAddress2(obj[40] != null ? (String) obj[40] : null);
		        dto.setDispatcherLocation(obj[41] != null ? (String) obj[41] : null);
		        if (obj[42] != null) {
		            if (obj[42] instanceof String) {
		                dto.setDispatcherPincode(Integer.parseInt((String) obj[42]));
		            } else if (obj[42] instanceof Integer) {
		                dto.setDispatcherPincode((Integer) obj[42]);
		            } else {
		                throw new ClassCastException("Unexpected type for obj[42]: " + obj[42].getClass().getName());
		            }
		        } else {
		            dto.setDispatcherPincode(null);
		        }

		        if (obj[43] != null) {
		            if (obj[43] instanceof String) {
		                dto.setDispatcherStateCode((String) obj[43]);
		            } else if (obj[43] instanceof Integer) {
		                dto.setDispatcherStateCode(obj[43].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[43]: " + obj[43].getClass().getName());
		            }
		        } else {
		            dto.setDispatcherStateCode(null);
		        }
		        dto.setShipToGstin(obj[44] != null ? (String) obj[44] : null);
		        dto.setShipToTradeName(obj[45] != null ? (String) obj[45] : null);
		        dto.setShipToLegalName(obj[46] != null ? (String) obj[46] : null);
		        dto.setShipToAddress1(obj[47] != null ? (String) obj[47] : null);
		        dto.setShipToAddress2(obj[48] != null ? (String) obj[48] : null);
		        dto.setShipToLocation(obj[49] != null ? (String) obj[49] : null);
		        if (obj[50] != null) {
		            if (obj[50] instanceof String) {
		                dto.setShipToPincode(Integer.parseInt((String) obj[50]));
		            } else if (obj[50] instanceof Integer) {
		                dto.setShipToPincode((Integer) obj[50]);
		            } else {
		                throw new ClassCastException("Unexpected type for obj[50]: " + obj[50].getClass().getName());
		            }
		        } else {
		            dto.setShipToPincode(null);
		        }

		        if (obj[51] != null) {
		            if (obj[51] instanceof String) {
		                dto.setShipToStateCode((String) obj[51]);
		            } else if (obj[51] instanceof Integer) {
		                dto.setShipToStateCode(obj[51].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[51]: " + obj[51].getClass().getName());
		            }
		        } else {
		            dto.setShipToStateCode(null);
		        }

		        if (obj[52] != null) {
		            if (obj[52] instanceof String) {
		                dto.setItemSerialNumber((String) obj[52]);
		            } else if (obj[52] instanceof Integer) {
		                dto.setItemSerialNumber(obj[52].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[52]: " + obj[52].getClass().getName());
		            }
		        } else {
		            dto.setItemSerialNumber(null);
		        }

		        if (obj[53] != null) {
		            if (obj[53] instanceof String) {
		                dto.setProductSerialNumber((String) obj[53]);
		            } else if (obj[53] instanceof Integer) {
		                dto.setProductSerialNumber(obj[53].toString());
		            } else if (obj[53] instanceof Long) {
		                dto.setProductSerialNumber(obj[53].toString());
		            } else if (obj[53] instanceof Double) {
		                dto.setProductSerialNumber(obj[53].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[53]: " + obj[53].getClass().getName());
		            }
		        } else {
		            dto.setProductSerialNumber(null);
		        }
		        dto.setProductDescription(obj[54] != null ? (String) obj[54] : null);
		        dto.setIsService(obj[55] != null ? (String) obj[55] : null);
		        if (obj[56] != null) {
		            if (obj[56] instanceof String) {
		                dto.setHsn((String) obj[56]);
		            } else if (obj[56] instanceof Integer) {
		                dto.setHsn(obj[56].toString());
		            } else if (obj[56] instanceof Long) {
		                dto.setHsn(obj[56].toString());
		            } else if (obj[56] instanceof Double) {
		                dto.setHsn(obj[56].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[56]: " + obj[56].getClass().getName());
		            }
		        } else {
		            dto.setHsn(null);
		        }
		        dto.setBarcode(obj[57] != null ? (String) obj[57] : null);
		        dto.setBatchName(obj[58] != null ? (String) obj[58] : null);
		        //dto.setBatchExpiryDate(obj[59] != null ? removeQuotes(obj[59].toString()) : null);
		        
		        String batchExpiryDate = null;
				try {
					batchExpiryDate = StringUtils.isEmpty((String) obj[59]) ? null
							: removeQuotes(obj[59].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[59];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					batchExpiryDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate101 = null;
				if (batchExpiryDate != null) {
					invoiceDate101 = LocalDate.parse(batchExpiryDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setBatchExpiryDate(invoiceDate101);
				}
				
		        //dto.setWarrantyDate(obj[60] != null ? removeQuotes(obj[60].toString()) : null);
		        
		        String warrantyDate = null;
				try {
					warrantyDate = StringUtils.isEmpty((String) obj[60]) ? null
							: removeQuotes(obj[60].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[60];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					warrantyDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate102 = null;
				if (warrantyDate != null) {
					invoiceDate102 = LocalDate.parse(warrantyDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setWarrantyDate(invoiceDate102);
				}
				
		        dto.setOrderLineReference(obj[61] != null ? (String) obj[61] : null);
		        dto.setAttributeName(obj[62] != null ? (String) obj[62] : null);
		        dto.setAttributeValue(obj[63] != null ? String.valueOf(obj[63]) : null);
		        dto.setOriginCountry(obj[64] != null ? (String) obj[64] : null);
		        dto.setUqc(obj[65] != null ? (String) obj[65] : null);
		        dto.setQuantity(obj[66] != null ? getAppropriateValueFromObject(obj[66]) : null);
		        dto.setFreeQuantity(obj[67] != null ? getAppropriateValueFromObject(obj[67]) : null);
		        dto.setUnitPrice(obj[68] != null ? getAppropriateValueFromObject(obj[68]) : null);
		        dto.setItemAmount(obj[69] != null ? getAppropriateValueFromObject(obj[69]) : null);
		        dto.setItemDiscount(obj[70] != null ? getAppropriateValueFromObject(obj[70]) : null);
		        dto.setPreTaxAmount(obj[71] != null ? getAppropriateValueFromObject(obj[71]) : null);
		        dto.setItemAssessableAmount(obj[72] != null ? getAppropriateValueFromObject(obj[72]) : null);
		        dto.setIgstRate(obj[73] != null ? getAppropriateValueFromObject(obj[73]) : null);
		        dto.setIgstAmount(obj[74] != null ? getAppropriateValueFromObject(obj[74]) : null);
		        dto.setCgstRate(obj[75] != null ? getAppropriateValueFromObject(obj[75]) : null);
		        dto.setCgstAmount(obj[76] != null ? getAppropriateValueFromObject(obj[76]) : null);
		        dto.setSgstRate(obj[77] != null ? getAppropriateValueFromObject(obj[77]) : null);
		        dto.setSgstAmount(obj[78] != null ? getAppropriateValueFromObject(obj[78]) : null);
		        dto.setCessAdValoremRate(obj[79] != null ? getAppropriateValueFromObject(obj[79]) : null);
		        dto.setCessAdValoremAmount(obj[80] != null ? getAppropriateValueFromObject(obj[80]) : null);
		        dto.setCessSpecificAmount(obj[81] != null ? getAppropriateValueFromObject(obj[81]) : null);
		        dto.setStateCessAdValoremRate(obj[82] != null ? getAppropriateValueFromObject(obj[82]) : null);
		        dto.setStateCessAdValoremAmount(obj[83] != null ? getAppropriateValueFromObject(obj[83]) : null);
		        dto.setStateCessSpecificAmount(obj[84] != null ? getAppropriateValueFromObject(obj[84]) : null);
		        dto.setItemOtherCharges(obj[85] != null ? getAppropriateValueFromObject(obj[85]) : null);
		        dto.setTotalItemAmount(obj[86] != null ? getAppropriateValueFromObject(obj[86]) : null);
		        dto.setInvoiceOtherCharges(obj[87] != null ? getAppropriateValueFromObject(obj[87]) : null);
		        dto.setInvoiceAssessableAmount(obj[88] != null ? getAppropriateValueFromObject(obj[88]) : null);
		        dto.setInvoiceIgstAmount(obj[89] != null ? getAppropriateValueFromObject(obj[89]) : null);
		        dto.setInvoiceCgstAmount(obj[90] != null ? getAppropriateValueFromObject(obj[90]) : null);
		        dto.setInvoiceSgstAmount(obj[91] != null ? getAppropriateValueFromObject(obj[91]) : null);
		        dto.setInvoiceCessAdValoremAmount(obj[92] != null ? getAppropriateValueFromObject(obj[92]) : null);
		        dto.setInvoiceCessSpecificAmount(obj[93] != null ? getAppropriateValueFromObject(obj[93]) : null);
		        dto.setInvoiceStateCessAdValoremAmount(obj[94] != null ? getAppropriateValueFromObject(obj[94]) : null);
		        dto.setInvoiceStateCessSpecificAmount(obj[95] != null ? getAppropriateValueFromObject(obj[95]) : null);
		        dto.setInvoiceDiscount(obj[96] != null ? getAppropriateValueFromObject(obj[96]) : null);
		        dto.setInvoiceValue(obj[97] != null ? getAppropriateValueFromObject(obj[97]) : null);
		        dto.setRoundOff(obj[98] != null ? getAppropriateValueFromObject(obj[98]) : null);
		        if (obj[99] != null) {
		            if (obj[99] instanceof String) {
		                dto.setCurrencyCode((String) obj[99]);
		            } else if (obj[99] instanceof Integer) {
		                dto.setCurrencyCode(obj[99].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[99]: " + obj[99].getClass().getName());
		            }
		        } else {
		            dto.setCurrencyCode(null);
		        }

		        if (obj[100] != null) {
		            if (obj[100] instanceof String) {
		                dto.setCountryCode((String) obj[100]);
		            } else if (obj[100] instanceof Integer) {
		                dto.setCountryCode(obj[100].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[100]: " + obj[100].getClass().getName());
		            }
		        } else {
		            dto.setCountryCode(null);
		        }
		        dto.setInvoiceValueFc(obj[101] != null ? getAppropriateValueFromObject(obj[101]) : null);
		        if (obj[102] != null) {
		            if (obj[102] instanceof String) {
		                dto.setPortCode((String) obj[102]);
		            } else if (obj[102] instanceof Integer) {
		                dto.setPortCode(obj[102].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[102]: " + obj[102].getClass().getName());
		            }
		        } else {
		            dto.setPortCode(null);
		        }

		        if (obj[103] != null) {
		            if (obj[103] instanceof String) {
		                dto.setShippingBillNumber((String) obj[103]);
		            } else if (obj[103] instanceof Integer) {
		                dto.setShippingBillNumber(obj[103].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[103]: " + obj[103].getClass().getName());
		            }
		        } else {
		            dto.setShippingBillNumber(null);
		        }
		        //dto.setShippingBillDate(obj[104] != null ? (LocalDate) obj[104] : null);
		        
		        String shippingBillDate = null;
				try {
					shippingBillDate = removeQuotes(obj[104].toString());
					if (LOGGER.isDebugEnabled()) {
					    LOGGER.debug("Data from Excel shippingBillDate: {}",
					    		shippingBillDate);
					}

				    if (shippingBillDate != null) {
				        LocalDate invoiceDate6;
				        try {
				            // Try parsing with the expected format "dd/MM/yyyy"
				        	invoiceDate6 = LocalDate.parse(shippingBillDate, DateUtil.SUPPORTED_DATE_FORMAT4);
				        } catch (DateTimeParseException e1) {
				            try {
				                // Try parsing with the format "yyyy/MM/dd"
				            	invoiceDate6 = LocalDate.parse(shippingBillDate, DateUtil.SUPPORTED_DATE_FORMAT3);
				            } catch (DateTimeParseException e2) {
				                try {
				                    // Try parsing with the format "yyyy-MM-dd"
				                	invoiceDate6 = LocalDate.parse(shippingBillDate,DateUtil.SUPPORTED_DATE_FORMAT1);
				                } catch (DateTimeParseException e3) {
				                    // Try parsing with the format "dd-MM-yyyy"
				                	invoiceDate6 = LocalDate.parse(shippingBillDate, DateUtil.SUPPORTED_DATE_FORMAT2);
				                }
				            }
				        }
				        dto.setShippingBillDate(invoiceDate6);
				    }
				} catch (ClassCastException | DateTimeParseException e) {
					DateTime dateTime = (DateTime) obj[15];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					shippingBillDate = dateFormat.format(date1);
				}
		        dto.setInvoiceRemarks(obj[105] != null ? (String) obj[105] : null);
		        
		        String invoicePeriodStartDate = null;
				try {
					invoicePeriodStartDate = StringUtils.isEmpty((String) obj[106]) ? null
							: removeQuotes(obj[106].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[106];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					invoicePeriodStartDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate1 = null;
				if (invoicePeriodStartDate != null) {
					invoiceDate1 = LocalDate.parse(invoicePeriodStartDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setInvoicePeriodStartDate(invoiceDate1);
				}
		        
		        String invoicePeriodEndDate = null;
				try {
					invoicePeriodEndDate = StringUtils.isEmpty((String) obj[107]) ? null
							: removeQuotes(obj[107].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[107];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					invoicePeriodEndDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate2 = null;
				if (invoicePeriodEndDate != null) {
					invoiceDate2 = LocalDate.parse(invoicePeriodEndDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setInvoicePeriodEndDate(invoiceDate2);
				}
		        dto.setPrecedingInvoiceNumber(obj[108] != null ? (String) obj[108] : null);
		        
		        String precedingInvoiceDate = null;
				try {
					precedingInvoiceDate = StringUtils.isEmpty((String) obj[109]) ? null
							: removeQuotes(obj[109].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[109];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					precedingInvoiceDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate3 = null;
				if (precedingInvoiceDate != null) {
					invoiceDate3 = LocalDate.parse(precedingInvoiceDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setPrecedingInvoiceDate(invoiceDate3);
				}
		        dto.setOtherReference(obj[110] != null ? String.valueOf(obj[110]) : null);
		        dto.setReceiptAdviceReference(obj[111] != null ? String.valueOf(obj[111]) : null);
		        
		        String receiptAdviceDate = null;
				try {
					receiptAdviceDate = StringUtils.isEmpty((String) obj[112]) ? null
							: removeQuotes(obj[112].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[112];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					receiptAdviceDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate4 = null;
				if (receiptAdviceDate != null) {
					invoiceDate4 = LocalDate.parse(receiptAdviceDate,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					dto.setReceiptAdviceDate(invoiceDate4);
				}
		        dto.setTenderReference(obj[113] != null ? String.valueOf(obj[113]) : null);
		        dto.setContractReference(obj[114] != null ? String.valueOf(obj[114]) : null);
		        dto.setExternalReference(obj[115] != null ? String.valueOf(obj[115]) : null);
		        dto.setProjectReference(obj[116] != null ? String.valueOf(obj[116]) : null);
		        dto.setCustomerPoReferenceNumber(obj[117] != null ? String.valueOf(obj[117]) : null);
		        
		        String customerPoReferenceDate = null;
				try {
					customerPoReferenceDate = StringUtils.isEmpty((String) obj[118]) ? null
							: removeQuotes(obj[138].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[138];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					customerPoReferenceDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate5 = null;
				if (customerPoReferenceDate != null) {
					 try {
						 invoiceDate5 = LocalDate.parse(customerPoReferenceDate,
									DateUtil.SUPPORTED_DATE_FORMAT4);
						 } catch (DateTimeParseException e1) {
					            try {
					                // Try parsing with the format "yyyy/MM/dd"
					            	invoiceDate5 = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT3);
					            } catch (DateTimeParseException e2) {
					                try {
					                    // Try parsing with the format "yyyy-MM-dd"
					                	invoiceDate5 = LocalDate.parse(documentDate,DateUtil.SUPPORTED_DATE_FORMAT1);
					                } catch (DateTimeParseException e3) {
					                    // Try parsing with the format "dd-MM-yyyy"
					                	invoiceDate5 = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT2);
					                }
					            }
					        }
					dto.setCustomerPoReferenceDate(invoiceDate5);
				}
		        dto.setPayeeName(obj[119] != null ? (String) obj[119] : null);
		        dto.setModeOfPayment(obj[120] != null ? (String) obj[120] : null);
		        dto.setBranchOrIfscCode(obj[121] != null ? (String) obj[121] : null);
		        dto.setPaymentTerms(obj[122] != null ? String.valueOf(obj[122]) : null);
		        dto.setPaymentInstruction(obj[123] != null ? (String) obj[123] : null);
		        dto.setCreditTransfer(obj[124] != null ? String.valueOf(obj[124]) : null);
		        dto.setDirectDebit(obj[125] != null ? String.valueOf(obj[125]) : null);
		        dto.setCreditDays(obj[126] != null ? Integer.parseInt(String.valueOf(obj[126])) : null);
		        dto.setPaidAmount(obj[127] != null ? getAppropriateValueFromObject(obj[127]) : null);
		        dto.setBalanceAmount(obj[128] != null ? getAppropriateValueFromObject(obj[128]) : null);
		        dto.setAccountDetail(obj[129] != null ? String.valueOf(obj[129]) : null);
		        dto.setEcomGstin(obj[130] != null ? (String) obj[130] : null);
		        dto.setSupportingDocUrl(obj[131] != null ? (String) obj[131] : null);
		        dto.setSupportingDocument(obj[132] != null ? (String) obj[132] : null);
		        dto.setAdditionalInformation(obj[133] != null ? (String) obj[133] : null);
		        dto.setTransporterId(obj[134] != null ? (String) obj[134] : null);
		        dto.setTransporterName(obj[135] != null ? (String) obj[135] : null);
		        if (obj[136] != null) {
		            if (obj[136] instanceof String) {
		                dto.setTransportMode((String) obj[136]);
		            } else if (obj[136] instanceof Integer) {
		                dto.setTransportMode(obj[136].toString());
		            } else {
		                throw new ClassCastException("Unexpected type for obj[136]: " + obj[136].getClass().getName());
		            }
		        } else {
		            dto.setTransportMode(null);
		        }
		        dto.setTransportDocNo(obj[137] != null ? (String) obj[137] : null);
		        
		        String transportDocDate = null;
				try {
					transportDocDate = StringUtils.isEmpty((String) obj[138]) ? null
							: removeQuotes(obj[138].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[138];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					transportDocDate = dateFormat.format(date1);
				}
				LocalDate invoiceDate = null;
				if (transportDocDate != null) {
					 try {
						invoiceDate = LocalDate.parse(transportDocDate,
								DateUtil.SUPPORTED_DATE_FORMAT4);
					 } catch (DateTimeParseException e1) {
				            try {
				                // Try parsing with the format "yyyy/MM/dd"
				                invoiceDate = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT3);
				            } catch (DateTimeParseException e2) {
				                try {
				                    // Try parsing with the format "yyyy-MM-dd"
				                    invoiceDate = LocalDate.parse(documentDate,DateUtil.SUPPORTED_DATE_FORMAT1);
				                } catch (DateTimeParseException e3) {
				                    // Try parsing with the format "dd-MM-yyyy"
				                    invoiceDate = LocalDate.parse(documentDate, DateUtil.SUPPORTED_DATE_FORMAT2);
				                }
				            }
				        }
					dto.setTransportDocDate(invoiceDate);
				}
		        
				if (obj[139] != null) {
				    if (obj[139] instanceof String) {
				        dto.setDistance(Integer.parseInt((String) obj[139]));
				    } else if (obj[139] instanceof Integer) {
				        dto.setDistance((Integer) obj[139]);
				    } else {
				        throw new ClassCastException("Unexpected type for obj[139]: " + obj[139].getClass().getName());
				    }
				} else {
				    dto.setDistance(null);
				}
		        dto.setVehicleNo(obj[140] != null ? (String) obj[140] : null);
		        dto.setVehicleType(obj[141] != null ? (String) obj[141] : null);
		        dto.setSection7OfIgstFlag(obj[142] != null ? (String) obj[142] : null);
		        dto.setClaimRefundFlag(obj[143] != null ? (String) obj[143] : null);
		        dto.setExportDuty(obj[144] != null ? getAppropriateValueFromObject(obj[144]) : null);
		        
		        String irnGetCallDateTime = null;
				try {
					irnGetCallDateTime = StringUtils.isEmpty((String) obj[145]) ? null
							: removeQuotes(obj[145].toString());
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[145];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					irnGetCallDateTime = dateFormat.format(date1);
				}
				LocalDate invoiceDate7 = null;
				if (irnGetCallDateTime != null) {
					invoiceDate7 = LocalDate.parse(irnGetCallDateTime,
							DateUtil.SUPPORTED_DATE_FORMAT2);
					dto.setIrnGetCallDateTime(invoiceDate7);
				}
		        dto.setSourceIrp(obj[146] != null ? (String) obj[146] : null);
		        inwardEinvoiceDataList.add(dto);
			}
		}

		return inwardEinvoiceDataList;
	}

	private String removeSpecialCharacters(Object object) {
		String str = String.valueOf(object);
		if (str.contains("-") || str.contains("_")) {
			return "";
		}
		return str;
	}

	private BigDecimal getAppropriateValueFromObject(Object obj) {
		BigDecimal returnValue = BigDecimal.ZERO;
		if (obj != null) {
			String str = String.valueOf(obj);
			if (str.contains(".")) {
				Double value = Double.parseDouble(str);
				returnValue = BigDecimal.valueOf(value);
			} else if (str.contains("-") || str.contains("_")) {
				return returnValue;
			} else {
				Integer value = Integer.parseInt(str);
				returnValue = new BigDecimal(value);
			}
		}
		return returnValue;
	}

	private String getStateCodeForStateName(String pos) {
		String stCode = null;
		if (pos != null && !pos.trim().isEmpty()) {
			pos = checkStateNameToMatchDb(pos);
			stCode = statecodeRepository.getStateCodes((pos));
		}
		return stCode;
	}

	private String checkStateNameToMatchDb(String pos) {
		StringBuffer finalString = new StringBuffer();
		String arr[] = pos.split(" ");
		if (arr.length > 2 && arr[1].equalsIgnoreCase("and")) {
			finalString.append(StringUtils.capitalize(arr[0].toLowerCase()))
					.append(" ").append("and");
			for (int i = 2; i < arr.length; i++) {
				finalString.append(" ")
						.append(StringUtils.capitalize(arr[i].toLowerCase()));
			}
		} else if (arr.length > 1) {
			finalString.append(StringUtils.capitalize(arr[0].toLowerCase()))
					.append(" ")
					.append(StringUtils.capitalize(arr[1].toLowerCase()));
		} else {
			finalString.append(StringUtils.capitalize(arr[0].toLowerCase()));
		}
		return finalString.toString();
	}

	public boolean checkObjectAsData(Object obj[], int columnCount) {
		List<Boolean> booleans = Lists.newArrayList();
		for (int i = 0; i < columnCount; i++) {
			Object object = obj[i];
			if (object == null) {
				booleans.add(true);
			}
		}
		if (booleans.size() == columnCount) {
			return true;
		}
		return false;
	}

	public boolean checkInvoiceNumberAgainstToFinYear(LocalDate dataInDate,
			LocalDate entityInvDate) {
		if (entityInvDate != null) {
			String finYear = GenUtil.getFinYear(dataInDate);
			String dbFinYear = GenUtil.getFinYear(entityInvDate);
			if (finYear.equals(dbFinYear)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValidFormat(String format, String value, Locale locale) {
	    LocalDateTime ldt = null;
	    DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

	    try {
	        ldt = LocalDateTime.parse(value, fomatter);
	        String result = ldt.format(fomatter);
	        return result.equals(value);
	    } catch (DateTimeParseException e) {
	        try {
	            LocalDate ld = LocalDate.parse(value, fomatter);
	            String result = ld.format(fomatter);
	            return result.equals(value);
	        } catch (DateTimeParseException exp) {
	            try {
	                LocalTime lt = LocalTime.parse(value, fomatter);
	                String result = lt.format(fomatter);
	                return result.equals(value);
	            } catch (DateTimeParseException e2) {
	                e2.getMessage();
	            }
	        }
	    }

	    return false;
	}
	
	private String removeQuotes(String data) {
		if (Strings.isNullOrEmpty(data)) {
			return null;
		}
		if (data.contains("'")) {
			return data.replace("'", "");
		}
		return data;

	}
	
	private void MAIN() {
	
	}
	
}
