package com.ey.advisory.app.services.gstr8;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetB2BSummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNASummaryEntity;
import com.ey.advisory.admin.data.entities.client.Gstr8AGetCDNSummaryEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
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
public class Gstr8aProcessUtil {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;

	public List<Gstr8AGetB2BSummaryEntity> convertB2bWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId, String fy) {
		List<Gstr8AGetB2BSummaryEntity> b2bDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String invoiceNumber = null;
				try {
					invoiceNumber = (String) obj[3];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[3];
						invoiceNumber = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[3];
						invoiceNumber = String.valueOf(invDouble.intValue());
					}
				}
				Gstr8AGetB2BSummaryEntity entity = new Gstr8AGetB2BSummaryEntity();
				String sgstin = (obj[1] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 22) {
					sgstin = sgstin.substring(0, 22);
				}
				entity.setSgstin(sgstin);
				entity.setCgstin(cgstin);
				entity.setRetPeriod(taxPeriod);
				String stName = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (stName != null && stName.length() > 100) {
					stName = stName.substring(0, 100);
				}

				String inv = invoiceNumber;
				if (inv != null && inv.length() > 16) {
					inv = inv.substring(0, 16);
				}
				entity.setInvNum(inv);

				String invType = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (invType != null && invType.length() > 20) {
					invType = invType.substring(0, 20);
				}
				entity.setInvType(invType);

				String invDateStr = null;
				try {
					invDateStr =  obj[5] == null ? null
							: (String) obj[5];
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[5];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd-MM-yyyy");
					invDateStr = dateFormat.format(date1);
				}
				LocalDate invoiceDate = null;
				/*entity.setInvDate(invDateStr);
				if (invDateStr != null) {
					invoiceDate = LocalDate.parse(invDateStr,
							DateUtil.SUPPORTED_DATE_FORMAT4);
					entity.setInvDate(invoiceDate.toString());
				}*/
				
				
				// Ensure obj[5] is not null and is of a valid type for conversion
				if (obj[5] != null) {
				    if (obj[5] instanceof String) {
				        // Parse string to LocalDate
				        invoiceDate = LocalDate.parse((String) obj[5], DateUtil.SUPPORTED_DATE_FORMAT4);
				    } else if (obj[5] instanceof java.util.Date) {
				        // Convert java.util.Date to LocalDate
				        invoiceDate = ((java.util.Date) obj[5]).toInstant()
				                .atZone(ZoneId.systemDefault())
				                .toLocalDate();
				    } else if (obj[5] instanceof LocalDate) {
				        // Directly cast to LocalDate
				        invoiceDate = (LocalDate) obj[5];
				    } else {
				        LOGGER.warn("Unsupported date type for obj[5]: " + obj[5].getClass().getName());
				    }
				}

				// Set the invoice date, or leave it null if parsing failed
				entity.setInvDate(invoiceDate);

				//entity.setDocType(getDocType("B2B",invType));
				
//				entity.setOriginalNoteType(getOriginNoteType(obj.getSection(),
//						doc.getOrigiNoteType()));
				//entity.setOriginalDocNum(null);
				//entity.setOriginalDocDate(null);
				//entity.setOriginalInvNum("");
				//entity.setOriginalInvDate(null);
				
				
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());

				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser().getUserPrincipalName() != null)
								? SecurityContext.getUser().getUserPrincipalName()
								: "SYSTEM";
				entity.setCreatedBy(userName);
								
					entity.setFinYear(fy);
				
					entity.setInvValue(getAppropriateValueFromObject(obj[6]));

					String pos = removeSpecialCharacters(obj[7]);

					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[8] != null
							&& !obj[8].toString().trim().isEmpty())
									? String.valueOf(obj[8]).trim() : null;
					if (rvc != null && rvc.length() > 1) {
						rvc = rvc.substring(0, 1);
					}
					entity.setReverseCharge(rvc);
					Integer taxRate = (obj[9] != null && !obj[9].toString().trim().isEmpty())
			                  ? Integer.parseInt(obj[9].toString().trim()) : null;
					entity.setRt(taxRate.toString());
					entity.setTaxPayable(getAppropriateValueFromObject(obj[10]));
					entity.setIgst(getAppropriateValueFromObject(obj[11]));
					entity.setCgst(getAppropriateValueFromObject(obj[12]));
					entity.setSgst(getAppropriateValueFromObject(obj[13]));
					entity.setCess(getAppropriateValueFromObject(obj[14]));
					LocalDate filingDat = null;
					try {
					    filingDat = obj[15] == null ? null : (LocalDate) obj[15];
					} catch (ClassCastException e) {
					    LOGGER.error("Error casting obj[15] to LocalDate", e);
					}

					// Set filingDate directly if it's not null
					if (filingDat != null) {
					    entity.setFilingDate(filingDat); // Directly pass the LocalDate
					}

					String itc = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					if(itc.equalsIgnoreCase("Yes")){
						entity.setEligibleItc("Y");
						} else {
							entity.setEligibleItc("N");
						}
					String reason = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					entity.setReason(reason);
					
	
					b2bDataList.add(entity);
			}
		}

		return b2bDataList;
	}
	
	public static String getDocType(String section, String doc) {

		String docType = "";

		switch (section.toUpperCase()) {

		case "B2B":
			docType = "INV";
			break;

		case "B2BA":
			docType = "RNV";
			break;

		case "CDN":
			if (doc.equalsIgnoreCase("C"))
				docType = "CR";
			else
				docType = "DR";
			break;

		case "CDNA":
			if (doc.equalsIgnoreCase("C"))
				docType = "RCR";
			else
				docType = "RDR";
			break;

		default:
			break;

		}

		return docType;
	}

	public List<Gstr8AGetB2BASummaryEntity> convertB2baWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId, String fy) {
		List<Gstr8AGetB2BASummaryEntity> b2baDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String invoiceNumber = null;
				try {
					invoiceNumber = (String) obj[1];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[1];
						invoiceNumber = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[1];
						invoiceNumber = String.valueOf(invDouble.intValue());
					}
				}
				Gstr8AGetB2BASummaryEntity entity = new Gstr8AGetB2BASummaryEntity();
				String sgstin = (obj[3] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				if (sgstin != null && sgstin.length() > 22) {
					sgstin = sgstin.substring(0, 22);
				}
				entity.setSgstin(sgstin);
				entity.setCgstin(cgstin);
				entity.setRetPeriod(taxPeriod);
				String stName = (obj[4] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (stName != null && stName.length() > 100) {
					stName = stName.substring(0, 100);
				}

				String inv = null;
				try {
					inv = (String) obj[6];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[6];
						inv = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[6];
						inv = String.valueOf(invDouble.intValue());
					}
				}
				if (inv != null && inv.length() > 16) {
					inv = inv.substring(0, 16);
				}
				entity.setInvNum(inv);

				String invType = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				if (invType != null && invType.length() > 20) {
					invType = invType.substring(0, 20);
				}
				entity.setInvType(invType);

				LocalDate invoiceDate = null;
				try {
				    invoiceDate = obj[7] == null ? null : (LocalDate) obj[7];
				} catch (ClassCastException e) {
				    try {
				        DateTime dateTime = (DateTime) obj[7];
				        Date date1 = dateTime.toDate();
				        invoiceDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				    } catch (Exception ex) {
				        LOGGER.error("Error parsing date for obj[7]: ", ex);
				    }
				}

				// Set the date if it's not null
				if (invoiceDate != null) {
				    entity.setInvDate(invoiceDate);
				}

				entity.setOriginalInvNum(invoiceNumber);
				LocalDate invDate = null;
			/*	try {
					invDate =  obj[2] == null ? null
							: (String) obj[2];
				} catch (ClassCastException e) {
					DateTime dateTime = (DateTime) obj[2];
					Date date1 = dateTime.toDate();
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"dd/MM/yyyy");
					invDate = dateFormat.format(date1);
				}*/
				LocalDate invoiceDateOri = null;
				if (invoiceDate != null) {
				    entity.setOriginalInvDate(invoiceDate);
				}
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());
				
				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser().getUserPrincipalName() != null)
								? SecurityContext.getUser().getUserPrincipalName()
								: "SYSTEM";
				entity.setCreatedBy(userName);
								
					entity.setFinYear(fy);
				
					entity.setInvValue(getAppropriateValueFromObject(obj[8]));

					String pos = removeSpecialCharacters(obj[9]);

					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[10] != null
							&& !obj[10].toString().trim().isEmpty())
									? String.valueOf(obj[10]).trim() : null;
					if (rvc != null && rvc.length() > 1) {
						rvc = rvc.substring(0, 1);
					}
					entity.setReverseCharge(rvc);
					Integer taxRate = (obj[11] != null && !obj[11].toString().trim().isEmpty())
			                  ? Integer.parseInt(obj[11].toString().trim()) : null;
					entity.setTx(taxRate.toString());
					entity.setTaxPayable(getAppropriateValueFromObject(obj[12]));
					entity.setIgst(getAppropriateValueFromObject(obj[13]));
					entity.setCgst(getAppropriateValueFromObject(obj[14]));
					entity.setSgst(getAppropriateValueFromObject(obj[15]));
					entity.setCess(getAppropriateValueFromObject(obj[16]));
					LocalDate filingDate = null;
					try {
					    filingDate = obj[17] == null ? null : (LocalDate) obj[17];
					} catch (ClassCastException e) {
					    try {
					        DateTime dateTime = (DateTime) obj[17];
					        Date date1 = dateTime.toDate();
					        filingDate = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					    } catch (Exception ex) {
					        LOGGER.error("Error parsing filing date for obj[17]: ", ex);
					    }
					}

					// Set the filing date if it's not null
					if (filingDate != null) {
					    entity.setFilingDate(filingDate);
					}

					String itc = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if(itc.equalsIgnoreCase("Yes")){
						entity.setEligibleItc("Y");
						} else {
							entity.setEligibleItc("N");
						}
					String reason = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					entity.setReason(reason);
					
	
					b2baDataList.add(entity);
			}
		}

		return b2baDataList;
	}

	public List<Gstr8AGetCDNSummaryEntity> convertCdnrWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId, String fy) {
		List<Gstr8AGetCDNSummaryEntity> cdnrDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String invoiceNumber = null;
				try {
					invoiceNumber = (String) obj[5];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[5];
						invoiceNumber = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[5];
						invoiceNumber = String.valueOf(invDouble.intValue());
					}
				}
				Gstr8AGetCDNSummaryEntity entity = new Gstr8AGetCDNSummaryEntity();
				String sgstin = (obj[1] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 22) {
					sgstin = sgstin.substring(0, 22);
				}
				entity.setSgstin(sgstin);
				entity.setCgstin(cgstin);
				entity.setRetPeriod(taxPeriod);
				//entity.setTableType("CDN");
				String stName = (obj[2] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (stName != null && stName.length() > 100) {
					stName = stName.substring(0, 100);
				}

				String inv = invoiceNumber;
				if (inv != null && inv.length() > 16) {
					inv = inv.substring(0, 16);
				}
				entity.setInvNum(inv);
				String invType = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				if (invType != null && invType.length() > 20) {
					invType = invType.substring(0, 20);
				}
				entity.setNoteType(invType);

				LocalDate invDate = null;

				try {
				    // Check if obj[6] is null, otherwise cast to LocalDate
				    invDate = obj[6] == null ? null : (LocalDate) obj[6];
				} catch (ClassCastException e) {
				    try {
				        // If obj[6] is a DateTime, convert it to LocalDate
				        DateTime dateTime = (DateTime) obj[6];
				        invDate = dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				    } catch (Exception ex) {
				        LOGGER.error("Error parsing invDate for obj[6]: ", ex);
				    }
				}

				// If invDate is not null, set it directly to the entity
				if (invDate != null) {
				    entity.setNoteDate(invDate); // Directly assign LocalDate to NoteDate
				    entity.setInvDate(invDate);  // Directly assign LocalDate to InvDate
				}



				
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());
				
				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser().getUserPrincipalName() != null)
								? SecurityContext.getUser().getUserPrincipalName()
								: "SYSTEM";
				entity.setCreatedBy(userName);
								
					entity.setFinYear(fy);
				
					entity.setInvValue(getAppropriateValueFromObject(obj[7]));

					String pos = removeSpecialCharacters(obj[8]);

					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[9] != null
							&& !obj[9].toString().trim().isEmpty())
									? String.valueOf(obj[9]).trim() : null;
					if (rvc != null && rvc.length() > 1) {
						rvc = rvc.substring(0, 1);
					}
					entity.setReverseCharge(rvc);
					Integer taxRate = (obj[10] != null && !obj[10].toString().trim().isEmpty())
			                  ? Integer.parseInt(obj[10].toString().trim()) : null;
					entity.setRt(taxRate.toString());
					entity.setTaxPayable(getAppropriateValueFromObject(obj[11]));
					entity.setIgst(getAppropriateValueFromObject(obj[12]));
					entity.setCgst(getAppropriateValueFromObject(obj[13]));
					entity.setSgst(getAppropriateValueFromObject(obj[14]));
					entity.setCess(getAppropriateValueFromObject(obj[15]));
					LocalDate filingDate = null;

					try {
					    // Check if obj[16] is null, otherwise cast to LocalDate
					    filingDate = obj[16] == null ? null : (LocalDate) obj[16];
					} catch (ClassCastException e) {
					    try {
					        // If obj[16] is a DateTime, convert it to LocalDate
					        DateTime dateTime = (DateTime) obj[16];
					        filingDate = dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					    } catch (Exception ex) {
					        LOGGER.error("Error parsing filingDate for obj[16]: ", ex);
					    }
					}

					// If filingDate is not null, set it directly to the entity
					if (filingDate != null) {
					    entity.setFilingDate(filingDate); // Directly assign LocalDate to FilingDate
					}

					String itc = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if(itc.equalsIgnoreCase("Yes")){
						entity.setEligibleItc("Y");
						} else {
							entity.setEligibleItc("N");
						}
					String reason = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					entity.setReason(reason);
					
	
					cdnrDataList.add(entity);
			}
		}

		return cdnrDataList;
	}

	public List<Gstr8AGetCDNASummaryEntity> convertCdnraWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId, String fy) {
		List<Gstr8AGetCDNASummaryEntity> cdnraDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String invoiceNumber = null;
				try {
					invoiceNumber = (String) obj[2];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[2];
						invoiceNumber = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[2];
						invoiceNumber = String.valueOf(invDouble.intValue());
					}
				}
				
				Gstr8AGetCDNASummaryEntity entity = new Gstr8AGetCDNASummaryEntity();
				String sgstin = (obj[4] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (sgstin != null && sgstin.length() > 22) {
					sgstin = sgstin.substring(0, 22);
				}
				entity.setSgstin(sgstin);
				entity.setCgstin(cgstin);
				entity.setRetPeriod(taxPeriod);
				entity.setFinYear(fy);
				String stName = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				if (stName != null && stName.length() > 100) {
					stName = stName.substring(0, 100);
				}

				String inv = null;
				try {
					inv = (String) obj[9];
				} catch (ClassCastException e) {
					try {
						Double invDouble = (Double) obj[9];
						invoiceNumber = String.valueOf(invDouble.longValue());
					} catch (ClassCastException ex) {
						Integer invDouble = (Integer) obj[9];
						invoiceNumber = String.valueOf(invDouble.intValue());
					}
				}
				if (inv != null && inv.length() > 16) {
					inv = inv.substring(0, 16);
				}
				entity.setInvNum(inv);

				String invType = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (invType != null && invType.length() > 20) {
					invType = invType.substring(0, 20);
				}
				
				String type = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				if (type != null && type.length() > 20) {
					type = type.substring(0, 20);
				}
				entity.setNoteType(type);

				LocalDate invDate = null;

				try {
				    // Check if obj[3] is null, otherwise cast to LocalDate
				    invDate = obj[3] == null ? null : (LocalDate) obj[3];
				} catch (ClassCastException e) {
				    try {
				        // If obj[3] is a DateTime, convert it to LocalDate
				        DateTime dateTime = (DateTime) obj[3];
				        invDate = dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				    } catch (Exception ex) {
				        LOGGER.error("Error parsing invDate for obj[3]: ", ex);
				    }
				}
				
				entity.setOrigiNoteType(invType);
				entity.setOrigiNoteNum(invoiceNumber);
				
				
				
				entity.setIsDelete(false);
				entity.setCreatedOn(LocalDateTime.now());
				
				String userName = (SecurityContext.getUser() != null
						&& SecurityContext.getUser().getUserPrincipalName() != null)
								? SecurityContext.getUser().getUserPrincipalName()
								: "SYSTEM";
				entity.setCreatedBy(userName);
								
					entity.setFinYear(fy);
				
					entity.setInvValue(getAppropriateValueFromObject(obj[10]));

					String pos = removeSpecialCharacters(obj[11]);

					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[12] != null
							&& !obj[8].toString().trim().isEmpty())
									? String.valueOf(obj[12]).trim() : null;
					if (rvc != null && rvc.length() > 1) {
						rvc = rvc.substring(0, 1);
					}
					entity.setReverseCharge(rvc);
					Integer taxRate = (obj[13] != null && !obj[13].toString().trim().isEmpty())
			                  ? Integer.parseInt(obj[13].toString().trim()) : null;
					entity.setRt(taxRate.toString());
					entity.setTaxPayable(getAppropriateValueFromObject(obj[14]));
					entity.setIgst(getAppropriateValueFromObject(obj[15]));
					entity.setCgst(getAppropriateValueFromObject(obj[16]));
					entity.setSgst(getAppropriateValueFromObject(obj[17]));
					entity.setCess(getAppropriateValueFromObject(obj[18]));
					LocalDate filingDate = null;

					try {
					    // Check if obj[19] is null, otherwise cast to LocalDate
					    filingDate = obj[19] == null ? null : (LocalDate) obj[19];
					} catch (ClassCastException e) {
					    try {
					        // If obj[19] is a DateTime, convert it to LocalDate
					        DateTime dateTime = (DateTime) obj[19];
					        filingDate = dateTime.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					    } catch (Exception ex) {
					        LOGGER.error("Error parsing filingDate for obj[19]: ", ex);
					    }
					}

					// Assign the LocalDate directly to the entity if it is not null
					if (filingDate != null) {
					    entity.setFilingDate(filingDate); // Directly assign LocalDate
					}

					String itc = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					if(itc.equalsIgnoreCase("Yes")){
						entity.setEligibleItc("Y");
						} else {
							entity.setEligibleItc("N");
						}
					String reason = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]).trim() : null;
					entity.setReason(reason);
					
	
					cdnraDataList.add(entity);
			}
		}

		return cdnraDataList;
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
