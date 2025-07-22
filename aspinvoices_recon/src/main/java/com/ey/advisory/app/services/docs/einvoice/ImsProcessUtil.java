package com.ey.advisory.app.services.docs.einvoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.inward.einvoice.ImsDataDto;
import com.ey.advisory.common.GenUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra V S
 *
 */
@Component
@Slf4j
public class ImsProcessUtil {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final Pattern GSTIN_PATTERN = Pattern.compile("\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}\\d[Z]{1}[A-Z0-9]{1}");
	private static final Pattern DOC_NUMBER_PATTERN = Pattern.compile("[A-Za-z0-9\\-\\/]{1,16}");
	private static final Pattern POS_PATTERN = Pattern.compile("\\d{2}");
	private static final Pattern FILING_PERIOD_PATTERN = Pattern.compile("\\d{6}");
	private static final Pattern CHECKSUM_PATTERN = Pattern.compile("[a-fA-F0-9]{64}");

	public List<ImsDataDto> convertImsWorkSheetDataToList(
			Object[][] objList, int columnCount, String rgstin) {
		List<ImsDataDto> imsDataList = Lists
				.newArrayList();
		
		Set<String> seenRgstinChecksumPairs = new HashSet<>();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				ImsDataDto dto = new ImsDataDto();
				
				dto.setAction(isNullOrEmpty(obj[0]) ? null : validateAction(obj[0]));

	            dto.setTableType(isNullOrEmpty(obj[1]) ? null : validateTableType(obj[1]));
	            dto.setRecipientGstin(isNullOrEmpty(obj[2]) ? null : validateGstin(obj[2]));
	            dto.setSupplierGstin(isNullOrEmpty(obj[3]) ? null : validateGstin(obj[3]));
	            dto.setDocumentType(isNullOrEmpty(obj[4]) ? null : validateDocumentType(obj[4]));
	            dto.setDocumentNumber(isNullOrEmpty(obj[5]) ? null : validateDocumentNumber(removeQuotes(obj[5].toString())));
	           
	            dto.setDocumentDate(isNullOrEmpty(obj[6]) ? null : validateDate(removeQuotes(obj[6].toString())));
	            if (LOGGER.isDebugEnabled()) {
	            	if(!isNullOrEmpty(obj[6])){
	            		LOGGER.debug("Data from Excel documentDate: {}",
	            				obj[6].toString());
	            	}
				    
				}
	            dto.setTaxableValue(isNullOrEmpty(obj[7]) ? BigDecimal.ZERO : validateBigDecimal(obj[7]));
	            dto.setIgst(isNullOrEmpty(obj[8]) ? BigDecimal.ZERO : validateBigDecimal(obj[8]));
	            dto.setCgst(isNullOrEmpty(obj[9]) ? BigDecimal.ZERO : validateBigDecimal(obj[9]));
	            dto.setSgst(isNullOrEmpty(obj[10]) ? BigDecimal.ZERO : validateBigDecimal(obj[10]));
	            dto.setCess(isNullOrEmpty(obj[11]) ? BigDecimal.ZERO : validateBigDecimal(obj[11]));
	            dto.setInvoiceValue(isNullOrEmpty(obj[13]) ? BigDecimal.ZERO : validateBigDecimal(obj[13]));

	            dto.setPos(isNullOrEmpty(obj[14]) ? null : validatePos(removeQuotes(obj[14].toString())));
	            dto.setFormType(isNullOrEmpty(obj[15]) ? null : validateFormType(obj[15]));
	            dto.setGstr1FilingStatus(isNullOrEmpty(obj[16]) ? null : validateFilingStatus(obj[16]));
	            dto.setGstr1FilingPeriod(isNullOrEmpty(obj[17]) ? null : validateFilingPeriod(removeQuotes(obj[17].toString())));
	            dto.setOriginalDocumentNumber(isNullOrEmpty(obj[18]) ? null : validateDocumentNumber(removeQuotes(obj[18].toString())));
	            dto.setOriginalDocumentDate(isNullOrEmpty(obj[19]) ? null : validateDate(removeQuotes(obj[19].toString())));

	            dto.setPendingActionBlocked(isNullOrEmpty(obj[20]) ? null : validatePendingActionBlocked(obj[20]));
	            dto.setChecksum(isNullOrEmpty(obj[21]) ? null : validateChecksum(obj[21]));
	          
	            String rgstinChecksumPair = obj[2].toString() + "-" + obj[21].toString();
	            if (seenRgstinChecksumPairs.contains(rgstinChecksumPair)) {
	                throw new IllegalArgumentException("Duplicate RGSTIN + Checksum pair detected: " + rgstinChecksumPair);
	            } else {
	                seenRgstinChecksumPairs.add(rgstinChecksumPair);
	            }
	            imsDataList.add(dto);
			}
		}

		return imsDataList;
	}
	
	private String validateAction(Object obj) {
        String action = getStringValue(obj);
        if ("No Action".equals(action) || "Accepted".equals(action) || "Pending".equals(action) || "Rejected".equals(action)) {
            return action.substring(0, 1); // Map to N, A, P, R
        }
        throw new IllegalArgumentException("Invalid Action: " + action);
    }

    private String validateTableType(Object obj) {
        String type = getStringValue(obj);
        if ("B2B".equals(type) || "B2BA".equals(type) || "CN".equals(type) || "CNA".equals(type) ||
            "DN".equals(type) || "DNA".equals(type) || "ECOM".equals(type) || "ECOMA".equals(type)) {
            return type;
        }
        throw new IllegalArgumentException("Invalid Table Type: " + type);
    }

    private String validateGstin(Object obj) {
        String gstin = getStringValue(obj);
        if (gstin != null && GSTIN_PATTERN.matcher(gstin).matches()) {
            return gstin;
        }
        throw new IllegalArgumentException("Invalid GSTIN: " + gstin);
    }

    private String validateDocumentType(Object obj) {
        String docType = getStringValue(obj);
        if ("INV".equals(docType) || "CR".equals(docType) || "DR".equals(docType) || "RNV".equals(docType) ||
            "RCR".equals(docType) || "RDR".equals(docType)) {
            return docType;
        }
        throw new IllegalArgumentException("Invalid Document Type: " + docType);
    }

    private String validateDocumentNumber(Object obj) {
        String docNum = getStringValue(obj);
        if (docNum != null && DOC_NUMBER_PATTERN.matcher(docNum).matches()) {
            return docNum;
        }
        throw new IllegalArgumentException("Invalid Document Number: " + docNum);
    }

    private LocalDate validateDate(Object obj) {
        String dateStr = getStringValue(obj);
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid Document Date: " + dateStr);
        }
    }

    private BigDecimal validateBigDecimal(Object obj) {
        try {
            return new BigDecimal(obj.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid BigDecimal value: " + obj);
        }
    }

    private String validatePos(Object obj) {
        String pos = getStringValue(obj);
        if (pos != null && POS_PATTERN.matcher(pos).matches()) {
            return pos;
        }
        throw new IllegalArgumentException("Invalid POS: " + pos);
    }

    private String validateFormType(Object obj) {
        String formType = getStringValue(obj);
        if ("R1".equals(formType) || "R1A".equals(formType) || "IFF".equals(formType)) {
            return formType;
        }
        throw new IllegalArgumentException("Invalid Form Type: " + formType);
    }

    private String validateFilingStatus(Object obj) {
        String status = getStringValue(obj);
        if ("Filed".equals(status) || "Not Filed".equals(status)) {
            return status;
        }
        throw new IllegalArgumentException("Invalid Filing Status: " + status);
    }

    private String validateFilingPeriod(Object obj) {
        String period = getStringValue(obj);
        if (period != null && FILING_PERIOD_PATTERN.matcher(period).matches()) {
            return period;
        }
        throw new IllegalArgumentException("Invalid Filing Period: " + period);
    }

    private String validatePendingActionBlocked(Object obj) {
    	String value = getStringValue(obj);
        if ("Y".equalsIgnoreCase(value) || "N".equalsIgnoreCase(value)) {
            return value;
        }
        throw new IllegalArgumentException("Invalid Pending Action Blocked value: " + value);
    }

    private String validateChecksum(Object obj) {
        String checksum = getStringValue(obj);
        if (checksum != null && CHECKSUM_PATTERN.matcher(checksum).matches()) {
            return checksum;
        }
        throw new IllegalArgumentException("Invalid Checksum: " + checksum);
    }

    // Helper Methods
    private boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().trim().isEmpty();
    }

    private String getStringValue(Object obj) {
        return obj != null ? obj.toString() : null;
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

	
}
