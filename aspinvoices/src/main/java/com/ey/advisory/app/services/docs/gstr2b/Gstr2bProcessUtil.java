package com.ey.advisory.app.services.docs.gstr2b;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aspose.cells.DateTime;
import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bB2bEcomaHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bEcomaItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnrInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingCdnraInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgsezInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingImpgsezInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2bStagingIsdaInvoicesItemEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.SecurityContext;
import com.google.common.collect.Lists;
import com.ibm.icu.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Ravindra
 *
 */
@Slf4j
@Component
public class Gstr2bProcessUtil {

	private static final String DOC_KEY_JOINER = "|";

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	String userName = SecurityContext.getUser() != null ? (SecurityContext.getUser().getUserPrincipalName() != null
			? SecurityContext.getUser().getUserPrincipalName() : "SYSTEM") : "SYSTEM";

	Map<String, String> mapping = getMapping();
	
	private static final DateTimeFormatter FOMATTER = DateTimeFormatter
			.ofPattern("dd-MM-yyyy");

	public List<GetGstr2bStagingB2bInvoicesHeaderEntity> convertB2bWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {
		List<GetGstr2bStagingB2bInvoicesHeaderEntity> b2bDataList = Lists.newArrayList();
		try {
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

					if (invoiceNumber != null) {
						GetGstr2bStagingB2bInvoicesHeaderEntity entity = new GetGstr2bStagingB2bInvoicesHeaderEntity();
						String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
						if (sgstin != null && sgstin.length() > 22) {
							sgstin = sgstin.substring(0, 22);
						}
						entity.setSGstin(sgstin);

						String stName = (obj[1] != null && !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						entity.setRGstin(rgstin);

						String inv = invoiceNumber;
						if (inv != null && inv.length() > 16) {
							inv = inv.substring(0, 16);
						}
						entity.setInvoiceNumber(inv);

						String invType = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;

						if (invType != null) {
							if (invType.equalsIgnoreCase("Regular")) {
								entity.setInvoiceType("R");
							} else {
								entity.setInvoiceType(invType);
							}

						}

						String invDateStr = null;
						try {
							invDateStr = StringUtils.isEmpty((String) obj[4]) ? null : (String) obj[4];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[4];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						// LocalDate invoiceDate = null;
						if (invDateStr != null) {

							LocalDate dt = DateUtil.parseObjToDate(invDateStr);

							LocalDateTime localDateTime1 = dt.atStartOfDay();

							entity.setInvoiceDate(localDateTime1);
						}

						entity.setTaxPeriod(taxPeriod);

						entity.setSupInvoiceValue(getAppropriateValueFromObject(obj[5]));

						String pos = removeSpecialCharacters(obj[6]);

						entity.setPos(getStateCodeForStateName(pos));

						String rvc = (obj[7] != null && !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

						rvc = mapping.get(rvc) != null ? mapping.get(rvc) : null;

						if (rvc != null && rvc.length() > 1) {
							rvc = rvc.substring(0, 1);
						}
						entity.setRev(rvc);

						String supFilingPeriod = (obj[14] != null && !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod);

						String supFilingDate = null;
						try {
							supFilingDate = StringUtils.isEmpty((String) obj[15]) ? null : (String) obj[15];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[15];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (supFilingDate != null) {

							filingDate = DateUtil.parseObjToDate(supFilingDate);
							
							entity.setLnkingDocKey(deriveLinkingKey(
									filingDate,
									entity.getRGstin(), entity.getSGstin(), "INV", invoiceNumber));
							
							LocalDateTime localDateTime1 = filingDate.atStartOfDay();

							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[16] != null && !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;

						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[17] != null && !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);

						String diffPercent = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;
						
						if(diffPercent !=null){
							if (diffPercent.equalsIgnoreCase("100%") || diffPercent.equalsIgnoreCase("0.01") || diffPercent.equalsIgnoreCase("1")) {
								entity.setDiffPercent(BigDecimal.ONE);
							} else if (diffPercent.equalsIgnoreCase("65%") || diffPercent.equalsIgnoreCase("0.65")) {
								BigDecimal y = new BigDecimal("0.65");
								y = y.setScale(2, BigDecimal.ROUND_HALF_UP);
								entity.setDiffPercent(y);
							} else {
								entity.setDiffPercent(BigDecimal.ZERO);
							}
						} else {
							entity.setDiffPercent(BigDecimal.ZERO);
						}
						

						String irnSrcType = (obj[19] != null && !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;
						entity.setIrnSrcType(irnSrcType);

						String irnNo = (obj[20] != null && !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;
						entity.setIrnNo(irnNo);

						String irnGenDate = null;
						try {
							irnGenDate = StringUtils.isEmpty((String) obj[21]) ? null : (String) obj[21];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[21];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnGenDate != null) {
							irnDate = LocalDate.parse(irnGenDate,
									DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnGenDate(irnDate);	
						}
						
						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, sgstin, invType, inv, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bStagingB2bInvoicesItemEntity itemEntity = new GetGstr2bStagingB2bInvoicesItemEntity();

						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[9]));
						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[10]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[11]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[12]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[13]));
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[8]));

						entity.setLineItems(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);		
						b2bDataList.add(entity);
					}
				}
			}
		} catch (Exception e) {
			String msg = String.format("error occured while converting into B2B entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return b2bDataList;
	}

	public List<GetGstr2bStagingB2baInvoicesHeaderEntity> convertB2baWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bStagingB2baInvoicesHeaderEntity> b2baInvoicesHeaderEntities = Lists.newArrayList();

		try {
			for (int i = 0; i < objList.length; i++) {
				Object obj[] = objList[i];
				boolean isDataPresent = checkObjectAsData(obj, columnCount);
				if (!isDataPresent) {
					String invoiceNumber = null;
					try {
						invoiceNumber = (String) obj[4];
					} catch (ClassCastException e) {
						try {
							Double invDouble = (Double) obj[4];
							invoiceNumber = String.valueOf(invDouble.longValue());
						} catch (ClassCastException ex) {
							Integer invDouble = (Integer) obj[4];
							invoiceNumber = String.valueOf(invDouble.intValue());
						}
					}

					if (invoiceNumber != null) {
						GetGstr2bStagingB2baInvoicesHeaderEntity entity = new GetGstr2bStagingB2baInvoicesHeaderEntity();

						String orgInvoiceNumber = null;
						try {
							orgInvoiceNumber = (String) obj[0];
						} catch (ClassCastException e) {
							try {
								Double invDouble = (Double) obj[0];
								orgInvoiceNumber = String.valueOf(invDouble.longValue());
							} catch (ClassCastException ex) {
								Integer invDouble = (Integer) obj[0];
								orgInvoiceNumber = String.valueOf(invDouble.intValue());
							}
						}

						String origInvNum = orgInvoiceNumber;
						if (origInvNum != null && origInvNum.length() > 20) {
							origInvNum = origInvNum.substring(0, 20);
						}
						entity.setOrgInvoiceNumber(origInvNum);

						String origInvDateStr = null;
						try {
							origInvDateStr = StringUtils.isEmpty((String) obj[1]) ? null : (String) obj[1];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[1];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							origInvDateStr = dateFormat.format(date1);
						}
						LocalDate orgInvoiceDate = null;
						if (origInvDateStr != null) {

							orgInvoiceDate = DateUtil.parseObjToDate(origInvDateStr);

							LocalDateTime localDateTime1 = orgInvoiceDate.atStartOfDay();

							entity.setOrgInvoiceDate(localDateTime1);
						}

						String stin = (obj[2] != null && !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
						if (stin != null && stin.length() > 22) {
							stin = stin.substring(0, 22);
						}
						entity.setSGstin(stin);

						entity.setRGstin(rgstin);

						String stName = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						String invType = (obj[5] != null && !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
						if (invType != null && invType.length() > 20) {
							invType = invType.substring(0, 20);
						}
						if (invType != null) {
							if (invType.equalsIgnoreCase("Regular")) {
								entity.setInvoiceType("R");
							} else {
								entity.setInvoiceType(invType);
							}
						}

						String inv = invoiceNumber;
						if (inv != null && inv.length() > 16) {
							inv = inv.substring(0, 16);
						}
						entity.setInvoiceNumber(inv);

						String invDateStr = null;
						try {
							invDateStr = StringUtils.isEmpty((String) obj[6]) ? null : (String) obj[6];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[6];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate invoiceDate = null;
						if (invDateStr != null) {
							invoiceDate = DateUtil.parseObjToDate(invDateStr);
							LocalDateTime localDateTime1 = invoiceDate.atStartOfDay();
							entity.setInvoiceDate(localDateTime1);
						}

						entity.setTaxPeriod(taxPeriod);

						entity.setSupInvoiceValue(getAppropriateValueFromObject(obj[7]));
						String pos = removeSpecialCharacters(obj[8]);
						entity.setPos(getStateCodeForStateName(pos));

						String rch = (obj[9] != null && !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;

						rch = mapping.get(rch) != null ? mapping.get(rch) : null;

						if (rch != null && rch.length() > 1) {
							rch = rch.substring(0, 1);
						}
						entity.setRev(rch);

						String supFilingPeriod = (obj[16] != null && !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod != null ? supFilingPeriod.replace("'", "") : null);

						String supFilingDate = null;
						try {
							supFilingDate = StringUtils.isEmpty((String) obj[17]) ? null : (String) obj[17];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[17];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							supFilingDate = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (supFilingDate != null) {
							filingDate = DateUtil.parseObjToDate(supFilingDate);
							
							entity.setLnkingDocKey(deriveLinkingKey(filingDate,
									entity.getRGstin(), entity.getSGstin(), "RNV", invoiceNumber));
				
							
							LocalDateTime localDateTime1 = filingDate.atStartOfDay();
							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;
						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[19] != null && !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);

						String diffPercent = (obj[20] != null && !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;
						if(diffPercent !=null){
							if (diffPercent.equalsIgnoreCase("100%") || diffPercent.equalsIgnoreCase("0.01") || diffPercent.equalsIgnoreCase("1")) {
								entity.setDiffPercent(BigDecimal.ONE);
							} else if (diffPercent.equalsIgnoreCase("65%") || diffPercent.equalsIgnoreCase("0.65")) {
								BigDecimal y = new BigDecimal("0.65");
								y = y.setScale(2, BigDecimal.ROUND_HALF_UP);
								entity.setDiffPercent(y);
							} else {
								entity.setDiffPercent(BigDecimal.ZERO);
							}
						} else {
							entity.setDiffPercent(BigDecimal.ZERO);
						}

						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, stin, invType, inv, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bStagingB2baInvoicesItemEntity itemEntity = new GetGstr2bStagingB2baInvoicesItemEntity();

						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[11]));

						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[12]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[13]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[14]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[15]));
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[10]));
							
						entity.setLineItemss(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						b2baInvoicesHeaderEntities.add(entity);
					}
				}
			}

		} catch (Exception e) {
			String msg = String.format("error occured while converting into B2BA entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}

		return b2baInvoicesHeaderEntities;
	}

	public List<GetGstr2bStagingCdnrInvoicesHeaderEntity> convertCdnrWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bStagingCdnrInvoicesHeaderEntity> cdnrInvoicesHeaderEntities = Lists.newArrayList();
		try {
			for (int i = 0; i < objList.length; i++) {
				Object obj[] = objList[i];
				boolean isDataPresent = checkObjectAsData(obj, columnCount);
				if (!isDataPresent) {

					String noteNumber = null;
					try {
						noteNumber = (String) obj[2];
					} catch (ClassCastException e) {
						try {
							Double noteDouble = (Double) obj[2];
							noteNumber = String.valueOf(noteDouble.longValue());
						} catch (ClassCastException ex) {
							Integer noteDouble = (Integer) obj[2];
							noteNumber = String.valueOf(noteDouble.intValue());
						}
					}

					if (noteNumber != null) {

						GetGstr2bStagingCdnrInvoicesHeaderEntity entity = new GetGstr2bStagingCdnrInvoicesHeaderEntity();

						entity.setNoteNumber(noteNumber);

						String sgst = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
						if (sgst != null && sgst.length() > 15) {
							sgst = sgst.substring(0, 15);
						}
						entity.setSGstin(sgst);
						entity.setRGstin(rgstin);

						String stName = (obj[1] != null && !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						String noteType = (String) obj[3];
						noteType = mapping.get(noteType) != null ? mapping.get(noteType) : null;
						entity.setNoteType(noteType);

						String supType = (obj[4] != null && !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;

						if (supType != null) {
							if (supType.equalsIgnoreCase("Regular")) {
								entity.setInvoiceType("R");
							} else {
								supType = supType.substring(0, 1);
								entity.setInvoiceType(supType);
							}
						}

						String origInvDateStr = null;
						try {
							origInvDateStr = StringUtils.isEmpty((String) obj[5]) ? null : (String) obj[5];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[5];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							origInvDateStr = dateFormat.format(date1);
						}
						LocalDate orgInvoiceDate = null;
						if (origInvDateStr != null) {
							orgInvoiceDate = DateUtil.parseObjToDate(origInvDateStr);
							LocalDateTime localDateTime1 = orgInvoiceDate.atStartOfDay();
							entity.setNoteDate(localDateTime1);
						}

						entity.setSupInvoiceValue(getAppropriateValueFromObject(obj[6]));

						String pos = removeSpecialCharacters(obj[7]);
						entity.setPos(getStateCodeForStateName(pos));

						String rvc = (obj[8] != null && !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;

						rvc = mapping.get(rvc) != null ? mapping.get(rvc) : null;

						if (rvc != null && rvc.length() > 2) {
							rvc = rvc.substring(0, 1);
						}
						entity.setRev(rvc);

						entity.setTaxPeriod(taxPeriod);

						String supFilingPeriod = (obj[15] != null && !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod != null ? supFilingPeriod.replace("'", "") : null);

						String invDateStr = null;
						try {
							invDateStr = StringUtils.isEmpty((String) obj[16]) ? null : (String) obj[16];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[16];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (invDateStr != null) {
							filingDate = DateUtil.parseObjToDate(invDateStr);
							
							if (noteType != null && ("C".equalsIgnoreCase(noteType)
									|| "CR".equalsIgnoreCase(noteType)))
							// adding linking key
							{
								entity.setLnkingDocKey(deriveLinkingKey(
										filingDate,
										entity.getRGstin(), entity.getSGstin(), "CR", noteNumber));

							} else if (noteType != null && ("D".equalsIgnoreCase(noteType)
									|| "DR".equalsIgnoreCase(noteType)))
							// adding linking key
							{
								entity.setLnkingDocKey(deriveLinkingKey(
										filingDate,
										entity.getRGstin(), entity.getSGstin(), "DR", noteNumber));
		}
							
							
							LocalDateTime localDateTime1 = filingDate.atStartOfDay();
							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[17] != null && !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;

						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);

						String diffPercent = (obj[19] != null && !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;

						if (diffPercent.equalsIgnoreCase("100%") || diffPercent.equalsIgnoreCase("0.01") || diffPercent.equalsIgnoreCase("1")) {
							entity.setDiffPercent(BigDecimal.ONE);
						} else if (diffPercent.equalsIgnoreCase("65%") || diffPercent.equalsIgnoreCase("0.65")) {
							BigDecimal y = new BigDecimal("0.65");
							y = y.setScale(2, BigDecimal.ROUND_HALF_UP);
							entity.setDiffPercent(y);
						} else {
							entity.setDiffPercent(BigDecimal.ZERO);
						}

						String irnSrcType = (obj[20] != null && !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;
						entity.setIrnSrcType(irnSrcType);

						String irnNo = (obj[21] != null && !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[21]).trim() : null;
						entity.setIrnNo(irnNo);

						String irnGenDate = null;
						try {
							irnGenDate = StringUtils.isEmpty((String) obj[22]) ? null : (String) obj[22];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[22];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnGenDate != null) {
							irnDate = LocalDate.parse(irnGenDate,
									DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnGenDate(irnDate);	
						}

						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, sgst, noteType, noteNumber, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bStagingCdnrInvoicesItemEntity itemEntity = new GetGstr2bStagingCdnrInvoicesItemEntity();

						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[10]));

						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[11]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[12]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[13]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[14]));
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[9]));

						entity.setLineItemss(Arrays.asList(itemEntity));
						
						itemEntity.setHeader(entity);

						cdnrInvoicesHeaderEntities.add(entity);
					}
				}
			}
		} catch (Exception e) {
			String msg = String.format("error occured while converting into CDNR entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return cdnrInvoicesHeaderEntities;
	}

	public List<GetGstr2bStagingCdnraInvoicesHeaderEntity> convertCdnraWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bStagingCdnraInvoicesHeaderEntity> cdnraInvoicesHeaderEntities = Lists.newArrayList();
		try {
			for (int i = 0; i < objList.length; i++) {
				Object obj[] = objList[i];
				boolean isDataPresent = checkObjectAsData(obj, columnCount);
				if (!isDataPresent) {

					String noteNumber = null;
					try {
						noteNumber = (String) obj[5];
					} catch (ClassCastException e) {
						try {
							Double noteDouble = (Double) obj[5];
							noteNumber = String.valueOf(noteDouble.longValue());
						} catch (ClassCastException ex) {
							Integer noteDouble = (Integer) obj[5];
							noteNumber = String.valueOf(noteDouble.intValue());
						}
					}

					if (noteNumber != null) {

						GetGstr2bStagingCdnraInvoicesHeaderEntity entity = new GetGstr2bStagingCdnraInvoicesHeaderEntity();

						entity.setNoteNumber(noteNumber);

						String sgst = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
						if (sgst != null && sgst.length() > 15) {
							sgst = sgst.substring(0, 15);
						}
						entity.setSGstin(sgst);
						entity.setRGstin(rgstin);

						String stName = (obj[4] != null && !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						String noteType = (String) obj[6];
						noteType = mapping.get(noteType) != null ? mapping.get(noteType) : null;
						entity.setNoteType(noteType);

						String supType = (obj[7] != null && !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

						if (supType != null) {
							if (supType.equalsIgnoreCase("Regular")) {
								entity.setInvoiceType("R");
							} else {
								supType = supType.substring(0, 1);
								entity.setInvoiceType(supType);
							}
						}

						String origInvDateStr = null;
						try {
							origInvDateStr = StringUtils.isEmpty((String) obj[8]) ? null : (String) obj[8];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[8];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							origInvDateStr = dateFormat.format(date1);
						}
						LocalDate orgInvoiceDate = null;
						if (origInvDateStr != null) {
							orgInvoiceDate = DateUtil.parseObjToDate(origInvDateStr);
							LocalDateTime localDateTime1 = orgInvoiceDate.atStartOfDay();
							entity.setNoteDate(localDateTime1);
						}

						String origInvDate = null;
						try {
							origInvDate = StringUtils.isEmpty((String) obj[2]) ? null : (String) obj[2];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[2];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							origInvDate = dateFormat.format(date1);
						}
						LocalDate orgInvoiDate = null;
						if (origInvDate != null) {
							orgInvoiDate = DateUtil.parseObjToDate(origInvDate);
							LocalDateTime localDateTimeOrg = orgInvoiDate.atStartOfDay();
							entity.setOrgNoteDate(localDateTimeOrg);
						}

						String origNoteNumber = StringUtils.isEmpty((String) obj[1]) ? null : (String) obj[1];
						entity.setOrgNoteNumber(origNoteNumber);
						String origNoteType = (String) obj[0];
						origNoteType = mapping.get(origNoteType) != null ? mapping.get(origNoteType) : null;
						entity.setOrgNoteType(origNoteType);

						String pos = removeSpecialCharacters(obj[10]);
						entity.setPos(getStateCodeForStateName(pos));

						String rvc = (obj[11] != null && !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;

						rvc = mapping.get(rvc) != null ? mapping.get(rvc) : null;

						if (rvc != null && rvc.length() > 2) {
							rvc = rvc.substring(0, 1);
						}
						entity.setRev(rvc);

						entity.setTaxPeriod(taxPeriod);

						String supFilingPeriod = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod != null ? supFilingPeriod.replace("'", "") : null);

						String invDateStr = null;
						try {
							invDateStr = StringUtils.isEmpty((String) obj[19]) ? null : (String) obj[19];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[19];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (invDateStr != null) {
							filingDate = DateUtil.parseObjToDate(invDateStr);
							
							if (noteType != null && ("C".equalsIgnoreCase(noteType)
									|| "RCR".equalsIgnoreCase(noteType)))
							// adding linking key
							{
								entity.setLnkingDocKey(deriveLinkingKey(filingDate,
										entity.getRGstin(), entity.getSGstin(), "RCR", noteNumber));

							} else if (noteType != null && ("D".equalsIgnoreCase(noteType)
									|| "RDR".equalsIgnoreCase(noteType)))
							// adding linking key
							{
								entity.setLnkingDocKey(deriveLinkingKey(filingDate,
										entity.getRGstin(), entity.getSGstin(), "RDR", noteNumber));
							}
							
							
							LocalDateTime localDateTime1 = filingDate.atStartOfDay();
							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[20] != null && !obj[20].toString().trim().isEmpty())
								? String.valueOf(obj[20]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;

						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[21] != null && !obj[21].toString().trim().isEmpty())
								? String.valueOf(obj[21]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);
						String diffPercent = (obj[22] != null && !obj[22].toString().trim().isEmpty())
								? String.valueOf(obj[22]).trim() : null;

						if (diffPercent.equalsIgnoreCase("100%") || 
								diffPercent.equalsIgnoreCase("0.01") || diffPercent.equalsIgnoreCase("1")
								|| diffPercent.equalsIgnoreCase("0.1") ) {
							entity.setDiffPercent(BigDecimal.ONE);
						} else if (diffPercent.equalsIgnoreCase("65%") || diffPercent.equalsIgnoreCase("0.65")) {
							BigDecimal y = new BigDecimal("0.65");
							y = y.setScale(2, BigDecimal.ROUND_HALF_UP);
							entity.setDiffPercent(y);
						} else {
							entity.setDiffPercent(BigDecimal.ZERO);
						}

						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, sgst, noteType, noteNumber, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bStagingCdnraInvoicesItemEntity itemEntity = new GetGstr2bStagingCdnraInvoicesItemEntity();

						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[13]));

						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[14]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[15]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[16]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[17]));
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[12]));
						entity.setLineItemss(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						cdnraInvoicesHeaderEntities.add(entity);
					}
				}
			}
		} catch (Exception e) {
			String msg = String.format("error occured while converting into CDNRA entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return cdnraInvoicesHeaderEntities;
	}

	public List<GetGstr2bStagingImpgInvoicesHeaderEntity> convertImpgWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bStagingImpgInvoicesHeaderEntity> impgInvoicesHeaderEntities = Lists.newArrayList();
		try {
			for (int i = 0; i < objList.length; i++) {
				Object obj[] = objList[i];
				boolean isDataPresent = checkObjectAsData(obj, columnCount);
				if (!isDataPresent) {

					String DocNumber = null;
					try {
						DocNumber = (String) obj[2];
					} catch (ClassCastException e) {
						try {
							Double impgDoc = (Double) obj[2];
							DocNumber = String.valueOf(impgDoc.longValue());
						} catch (ClassCastException ex) {
							Integer impgDoc = (Integer) obj[2];
							DocNumber = String.valueOf(impgDoc.intValue());
						}
					}

					if (DocNumber != null) {

						GetGstr2bStagingImpgInvoicesHeaderEntity entity = new GetGstr2bStagingImpgInvoicesHeaderEntity();

						entity.setRGstin(rgstin);
						entity.setTaxPeriod(taxPeriod);
						entity.setIsDelete(false);
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());
						entity.setChecksum("");

						GetGstr2bStagingImpgInvoicesItemEntity itemEntity = new GetGstr2bStagingImpgInvoicesItemEntity();

						String refDate = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
						if (refDate != null) {
							LocalDate dt = DateUtil.parseObjToDate(refDate);
							LocalDateTime localDateTime1 = dt.atStartOfDay();
							itemEntity.setRefDateIcegate(localDateTime1);
						}

						String portCode = (obj[1] != null && !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
						if (portCode != null && portCode.length() > 10) {
							portCode = portCode.substring(0, 10);
						}
						itemEntity.setPortCode(portCode);

						Long Num = Long.valueOf(DocNumber);
						itemEntity.setBoeNumber(Num);

						String boeDate = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
						boeDate = boeDate.substring(0, 10);
						if (boeDate != null) {
								LocalDate dt = DateUtil.parseObjToDate(boeDate);
								LocalDateTime localDateTime1 = dt.atStartOfDay();
								itemEntity.setBoeDate(localDateTime1);
						}

						itemEntity.setTaxValue(getAppropriateValueFromObject(obj[4]));
						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[5]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[6]));

						String amended = (obj[7] != null && !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

						if (amended.equalsIgnoreCase("yes")) {
							itemEntity.setIsAmd(true);
						} else if (amended.equalsIgnoreCase("No")) {
							itemEntity.setIsAmd(false);
						}

						entity.setLineItemss(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						impgInvoicesHeaderEntities.add(entity);
					}
				}
			}

		} catch (Exception e) {
			String msg = String.format("error occured while converting into IMPG entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return impgInvoicesHeaderEntities;
	}

	public List<GetGstr2bStagingImpgsezInvoicesHeaderEntity> convertImpgSezWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bStagingImpgsezInvoicesHeaderEntity> impgsezInvoicesHeaderEntities = Lists.newArrayList();
		try {
			for (int i = 0; i < objList.length; i++) {
				Object obj[] = objList[i];
				boolean isDataPresent = checkObjectAsData(obj, columnCount);
				if (!isDataPresent) {

					String DocNumber = null;
					try {
						DocNumber = (String) obj[4];
					} catch (ClassCastException e) {
						try {
							Double impgDoc = (Double) obj[4];
							DocNumber = String.valueOf(impgDoc.longValue());
						} catch (ClassCastException ex) {
							Integer impgDoc = (Integer) obj[4];
							DocNumber = String.valueOf(impgDoc.intValue());
						}
					}

					if (DocNumber != null) {

						GetGstr2bStagingImpgsezInvoicesHeaderEntity entity = new GetGstr2bStagingImpgsezInvoicesHeaderEntity();

						entity.setRGstin(rgstin);
						entity.setSGstin((obj[0] != null) ? obj[0].toString() : null);
						entity.setSupTradeName((obj[1] != null) ? obj[1].toString() : null);
						entity.setTaxPeriod(taxPeriod);
						entity.setIsDelete(false);
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());
						entity.setChecksum("");

						GetGstr2bStagingImpgsezInvoicesItemEntity itemEntity = new GetGstr2bStagingImpgsezInvoicesItemEntity();

						String refDate = (obj[2] != null && !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
						if (refDate != null) {
							LocalDate dt = DateUtil.parseObjToDate(refDate);
							LocalDateTime localDateTime1 = dt.atStartOfDay();
							itemEntity.setRefDateIcegate(localDateTime1);
						}

						String portCode = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
						if (portCode != null && portCode.length() > 10) {
							portCode = portCode.substring(0, 10);
						}
						itemEntity.setPortCode(portCode);

						Long Num = Long.valueOf(DocNumber);
						itemEntity.setBoeNumber(Num);

						String boeDate = (obj[5] != null && !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
						boeDate = boeDate.substring(0, 10);
						if (boeDate != null) {
								LocalDate dt = DateUtil.parseObjToDate(boeDate);
								LocalDateTime localDateTime1 = dt.atStartOfDay();
								itemEntity.setBoeDate(localDateTime1);
						}

						itemEntity.setTaxValue(getAppropriateValueFromObject(obj[6]));
						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[7]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[8]));

						String amended = (obj[9] != null && !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;

						if (amended.equalsIgnoreCase("yes")) {
							itemEntity.setIsAmd(true);
						} else if (amended.equalsIgnoreCase("No")) {
							itemEntity.setIsAmd(false);
						}

						entity.setLineItemss(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						impgsezInvoicesHeaderEntities.add(entity);
					}
				}
			}

		} catch (Exception e) {
			String msg = String.format("error occured while converting into IMPGSEZ entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return impgsezInvoicesHeaderEntities;
	}
	public List<GetGstr2bStagingIsdInvoicesHeaderEntity> convertIsdWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

	List<GetGstr2bStagingIsdInvoicesHeaderEntity> isdDataList = Lists.newArrayList();
	try {
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String docNumber = null;
				try {
					docNumber = (String) obj[3];
				} catch (ClassCastException e) {
					try {
						Double docDouble = (Double) obj[3];
						docNumber = String.valueOf(docDouble.longValue());
					} catch (ClassCastException ex) {
						Integer docDouble = (Integer) obj[3];
						docNumber = String.valueOf(docDouble.intValue());
					}
				}

				if (docNumber != null) {
					GetGstr2bStagingIsdInvoicesHeaderEntity entity = new GetGstr2bStagingIsdInvoicesHeaderEntity();
					
					String orgInvoiceNumber = null;
					try {
						orgInvoiceNumber = (String) obj[5];
					} catch (ClassCastException e) {
						try {
							Double invDouble = (Double) obj[5];
							orgInvoiceNumber = String.valueOf(invDouble.longValue());
						} catch (ClassCastException ex) {
							Integer invDouble = (Integer) obj[5];
							orgInvoiceNumber = String.valueOf(invDouble.intValue());
						}
					}

					String origInvNum = orgInvoiceNumber;
					if (origInvNum != null && origInvNum.length() > 20) {
						origInvNum = origInvNum.substring(0, 20);
					}
					entity.setOrgInvoiceNumber(origInvNum);

					String origInvDateStr = null;
					try {
						origInvDateStr = StringUtils.isEmpty((String) obj[6]) ? null : (String) obj[6];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[6];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						origInvDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origInvDateStr != null) {

						orgInvoiceDate = DateUtil.parseObjToDate(origInvDateStr);

						LocalDateTime localDateTime1 = orgInvoiceDate.atStartOfDay();

						entity.setOrgInvoiceDate(localDateTime1);
					}
					String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
							? String.valueOf(obj[0]).trim() : null;
					if (sgstin != null && sgstin.length() > 22) {
						sgstin = sgstin.substring(0, 22);
					}
					entity.setSGstin(sgstin);

					String stName = (obj[1] != null && !obj[1].toString().trim().isEmpty())
							? String.valueOf(obj[1]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					entity.setRGstin(rgstin);

					String doc = docNumber;
					if (doc != null && doc.length() > 16) {
						doc = doc.substring(0, 16);
					}
					entity.setDocNumber(doc);

					String isdDocType = (obj[2] != null && !obj[2].toString().trim().isEmpty())
							? String.valueOf(obj[2]).trim() : null;

					if (isdDocType != null) {
						if (isdDocType.equalsIgnoreCase("Invoice")) {
							entity.setIsdDocType("ISDI");
						}
							else if(isdDocType.equalsIgnoreCase("Credit Note")){
							entity.setIsdDocType("ISDC");
							}else {
							entity.setIsdDocType(isdDocType);
						}

					}

					String docDateStr = null;
					try {
						docDateStr = StringUtils.isEmpty((String) obj[4]) ? null : (String) obj[4];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[4];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						docDateStr = dateFormat.format(date1);
					}
					// LocalDate invoiceDate = null;
					if (docDateStr != null) {

						LocalDate dt = DateUtil.parseObjToDate(docDateStr);

						LocalDateTime localDateTime1 = dt.atStartOfDay();

						entity.setDocDate(localDateTime1);
					}

					entity.setTaxPeriod(taxPeriod);

					String supFilingPeriod = (obj[11] != null && !obj[11].toString().trim().isEmpty())
							? String.valueOf(obj[11]).trim() : null;

					entity.setSupFilingPeriod(supFilingPeriod);

					String supFilingDate = null;
					try {
						supFilingDate = StringUtils.isEmpty((String) obj[12]) ? null : (String) obj[12];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[12];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						supFilingDate = dateFormat.format(date1);
					}
					LocalDate filingDate = null;
					if (supFilingDate != null) {

						filingDate = DateUtil.parseObjToDate(supFilingDate);

						LocalDateTime localDateTime1 = filingDate.atStartOfDay();

						entity.setSupFilingDate(localDateTime1);
					}
					entity.setIsDelete(false);
					entity.setChecksum("");
					entity.setVersion("1.0");
					entity.setCreatedBy(userName);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setModifiedBy(userName);
					entity.setModifiedOn(LocalDateTime.now());

		
					String docKey = generateKeyIsd(sgstin, isdDocType, docNumber, docDateStr);

					entity.setDocKey(docKey);

					GetGstr2bStagingIsdInvoicesItemEntity itemEntity = new GetGstr2bStagingIsdInvoicesItemEntity();

					
					itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[7]));
					itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[8]));
					itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[9]));
					itemEntity.setCessAmt(getAppropriateValueFromObject(obj[10]));
					
					String itcEligible = (obj[13] != null && !obj[13].toString().trim().isEmpty())
							? String.valueOf(obj[13]).trim() : null;

							itcEligible = mapping.get(itcEligible) != null ? mapping.get(itcEligible) : null;

					itemEntity.setItcEligible(itcEligible);
					

					entity.setLineItemss(itemEntity);
					itemEntity.setHeader(entity);

					isdDataList.add(entity);
				}
			}
		}
	} catch (Exception e) {
		String msg = String.format("error occured while converting into isd entity", e);
		LOGGER.error(msg, e);
		new AppException(msg, e);

	}
	return isdDataList;
	}
	
	public List<GetGstr2bStagingIsdaInvoicesHeaderEntity> convertIsdaWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

	List<GetGstr2bStagingIsdaInvoicesHeaderEntity> isdaDataList = Lists.newArrayList();
	try {
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String docNumber = null;
				try {
					docNumber = (String) obj[6];
				} catch (ClassCastException e) {
					try {
						Double docDouble = (Double) obj[6];
						docNumber = String.valueOf(docDouble.longValue());
					} catch (ClassCastException ex) {
						Integer docDouble = (Integer) obj[6];
						docNumber = String.valueOf(docDouble.intValue());
					}
				}

				if (docNumber != null) {
					GetGstr2bStagingIsdaInvoicesHeaderEntity entity = new GetGstr2bStagingIsdaInvoicesHeaderEntity();
					
					String orgInvoiceNumber = null;
					try {
						orgInvoiceNumber = (String) obj[8];
					} catch (ClassCastException e) {
						try {
							Double invDouble = (Double) obj[8];
							orgInvoiceNumber = String.valueOf(invDouble.longValue());
						} catch (ClassCastException ex) {
							Integer invDouble = (Integer) obj[8];
							orgInvoiceNumber = String.valueOf(invDouble.intValue());
						}
					}

					String origInvNum = orgInvoiceNumber;
					if (origInvNum != null && origInvNum.length() > 20) {
						origInvNum = origInvNum.substring(0, 20);
					}
					entity.setOrgInvoiceNumber(origInvNum);

					String origInvDateStr = null;
					try {
						origInvDateStr = StringUtils.isEmpty((String) obj[9]) ? null : (String) obj[9];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[9];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						origInvDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origInvDateStr != null) {

						orgInvoiceDate = DateUtil.parseObjToDate(origInvDateStr);

						LocalDateTime localDateTime1 = orgInvoiceDate.atStartOfDay();

						entity.setOrgInvoiceDate(localDateTime1);
					}
					
					String orgDocNumber = null;
						try {
							orgDocNumber = (String) obj[1];
						} catch (ClassCastException e) {
							try {
								Double docDouble = (Double) obj[1];
								orgDocNumber = String.valueOf(docDouble.longValue());
							} catch (ClassCastException ex) {
								Integer docDouble = (Integer) obj[1];
								orgDocNumber = String.valueOf(docDouble.intValue());
							}
						}

						String origDocNum = orgDocNumber;
						if (origDocNum != null && origDocNum.length() > 20) {
							origDocNum = origDocNum.substring(0, 20);
						}
						entity.setOrgdocNumber(origDocNum);

						String origDocDateStr = null;
						try {
							origDocDateStr = StringUtils.isEmpty((String) obj[2]) ? null : (String) obj[2];
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[2];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							origDocDateStr = dateFormat.format(date1);
						}
						LocalDate orgDocDate = null;
						if (origDocDateStr != null) {

							orgDocDate = DateUtil.parseObjToDate(origDocDateStr);

							LocalDateTime localDateTime1 = orgDocDate.atStartOfDay();

							entity.setOrgDocDate(localDateTime1);
						}
						String orgDocType = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;

						if (orgDocType != null) {
							if (orgDocType.equalsIgnoreCase("Invoice")) {
								entity.setOrgDocType("ISDI");
							}
								else if(orgDocType.equalsIgnoreCase("Credit Note")){
								entity.setOrgDocType("ISDC");
								}else {
								entity.setOrgDocType(orgDocType);
							}

						}
					String sgstin = (obj[3] != null && !obj[3].toString().trim().isEmpty())
							? String.valueOf(obj[3]).trim() : null;
					if (sgstin != null && sgstin.length() > 22) {
						sgstin = sgstin.substring(0, 22);
					}
					entity.setSuppSstin(sgstin);

					String stName = (obj[4] != null && !obj[4].toString().trim().isEmpty())
							? String.valueOf(obj[4]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					entity.setRGstin(rgstin);

					String doc = docNumber;
					if (doc != null && doc.length() > 16) {
						doc = doc.substring(0, 16);
					}
					entity.setDocNumber(doc);

					String isdDocType = (obj[5] != null && !obj[5].toString().trim().isEmpty())
							? String.valueOf(obj[5]).trim() : null;

					if (isdDocType != null) {
						if (isdDocType.equalsIgnoreCase("Invoice")) {
							entity.setIsdDocType("ISDI");
						}
							else if(isdDocType.equalsIgnoreCase("Credit Note")){
							entity.setIsdDocType("ISDC");
							}else {
							entity.setIsdDocType(isdDocType);
						}

					}

					String docDateStr = null;
					try {
						docDateStr = StringUtils.isEmpty((String) obj[7]) ? null : (String) obj[7];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[7];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						docDateStr = dateFormat.format(date1);
					}
					// LocalDate docDate = null;
					if (docDateStr != null) {

						LocalDate dt = DateUtil.parseObjToDate(docDateStr);

						LocalDateTime localDateTime1 = dt.atStartOfDay();

						entity.setDocDate(localDateTime1);
					}

					entity.setTaxPeriod(taxPeriod);

					String supFilingPeriod = (obj[14] != null && !obj[14].toString().trim().isEmpty())
							? String.valueOf(obj[14]).trim() : null;

					entity.setSupFilingPeriod(supFilingPeriod);

					String supFilingDate = null;
					try {
						supFilingDate = StringUtils.isEmpty((String) obj[15]) ? null : (String) obj[15];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[15];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
						supFilingDate = dateFormat.format(date1);
					}
					LocalDate filingDate = null;
					if (supFilingDate != null) {

						filingDate = DateUtil.parseObjToDate(supFilingDate);

						LocalDateTime localDateTime1 = filingDate.atStartOfDay();

						entity.setSupFilingDate(localDateTime1);
					}
					
					entity.setIsDelete(false);
					entity.setChecksum("");
					entity.setVersion("1.0");
					entity.setCreatedBy(userName);
					entity.setCreatedOn(LocalDateTime.now());
					entity.setModifiedBy(userName);
					entity.setModifiedOn(LocalDateTime.now());

					
					String docKey = generateKeyIsd(sgstin, isdDocType, docNumber, docDateStr);

					entity.setDocKey(docKey);

					GetGstr2bStagingIsdaInvoicesItemEntity itemEntity = new GetGstr2bStagingIsdaInvoicesItemEntity();

					
					itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[10]));
					itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[11]));
					itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[12]));
					itemEntity.setCessAmt(getAppropriateValueFromObject(obj[13]));
					
					String itcEligible = (obj[16] != null && !obj[16].toString().trim().isEmpty())
							? String.valueOf(obj[16]).trim() : null;

							itcEligible = mapping.get(itcEligible) != null ? mapping.get(itcEligible) : null;

					itemEntity.setItcEligible(itcEligible);
					

					entity.setLineItemss(itemEntity);
					itemEntity.setHeader(entity);

					isdaDataList.add(entity);
				}
			}
		}
	} catch (Exception e) {
		String msg = String.format("error occured while converting into isd entity", e);
		LOGGER.error(msg, e);
		new AppException(msg, e);

	}
	return isdaDataList;
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
			finalString.append(StringUtils.capitalize(arr[0].toLowerCase())).append(" ").append("and");
			for (int i = 2; i < arr.length; i++) {
				finalString.append(" ").append(StringUtils.capitalize(arr[i].toLowerCase()));
			}
		} else if (arr.length > 1) {
			finalString.append(StringUtils.capitalize(arr[0].toLowerCase())).append(" ")
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

	public boolean checkInvoiceNumberAgainstToFinYear(LocalDate dataInDate, LocalDate entityInvDate) {
		if (entityInvDate != null) {
			String finYear = GenUtil.getFinYear(dataInDate);
			String dbFinYear = GenUtil.getFinYear(entityInvDate);
			if (finYear.equals(dbFinYear)) {
				return true;
			}
		}
		return false;
	}

	public static final Map<String, String> getMapping() {

		Map<String, String> map = new HashMap<>();

		map.put("Yes", "Y");
		map.put("No", "N");
		map.put("POS and supplier state are same but recipient state is different", "P");
		map.put("Return filed post annual cut-off", "C");
		map.put("ISD Invoice", "ISDI");
		map.put("ISD Credit Note", "ISDC");
		map.put("ITC temporarily available", "T");
		map.put("Regular", "R");
		map.put("Deemed Export", "DE");
		map.put("SEZ supplies with payment of tax", "SEZWP");
		map.put("SEZ supplies without payment of tax", "SEZWOP");
		map.put("Debit Note", "D");
		map.put("Credit Note", "C");
		map.put("Intra-State supplies attracting IGST", "CBW");
		map.put("Debit note", "D");
		map.put("Credit note", "C");

		return map;

	}// POS and supplier state are same but recipient state is different

	private String generateKey(String taxPeriod, String sgstin, String invType, String invNum, String rgstin) {

		// String finYear =
		// GenUtil.getFinYear(DateUtil.parseObjToDate(invDate));
		String finYear = GenUtil.getFinancialYearByTaxperiod(taxPeriod);
		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(sgstin).add(invType).add(invNum).add(rgstin)
				.toString();
	}
	private String generateKeyIsd(String sgstin, String isdDocType, String docNumber, String docDate) {

		//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		//String formattedDate = docDate.format(formatter);
		return new StringJoiner(DOC_KEY_JOINER).add(sgstin).add(isdDocType).add(docNumber).add(docDate)
				.toString();
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
	
	//Ecom logic
	public List<GetGstr2bB2bEcomHeaderEntity> convertEcomWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate)
	{

		List<GetGstr2bB2bEcomHeaderEntity> ecomDataList = Lists.newArrayList();
		try {
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

					if (invoiceNumber != null) {
						GetGstr2bB2bEcomHeaderEntity entity = new GetGstr2bB2bEcomHeaderEntity();
						String sgstin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
						if (sgstin != null && sgstin.length() > 22) {
							sgstin = sgstin.substring(0, 22);
						}
						entity.setSGstin(sgstin);

						String stName = (obj[1] != null && !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						entity.setRGstin(rgstin);

						String inv = invoiceNumber;
						if (inv != null && inv.length() > 16) {
							inv = inv.substring(0, 16);
						}
						entity.setInvoiceNumber(inv);

						String invType = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;

						if (invType != null) {
								entity.setInvoiceType(invType);
						}

						String invDateStr = null;
						try {
							invDateStr = (obj[4] != null && !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[4];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						// LocalDate invoiceDate = null;
						if (invDateStr != null) {

							LocalDate dt = DateUtil.parseObjToDate(invDateStr);
							
							entity.setLnkingDocKey(deriveLinkingKey(dt,
									entity.getRGstin(), entity.getSGstin(), "INV", invoiceNumber));
							
							LocalDateTime localDateTime1 = dt.atStartOfDay();

							entity.setInvoiceDate(localDateTime1);
						}

						entity.setTaxPeriod(taxPeriod);

						entity.setSupInvoiceValue(getAppropriateValueFromObject(obj[5]));

						String pos = removeSpecialCharacters(obj[6]);

						entity.setPos(getStateCodeForStateName(pos));

						String rvc = (obj[7] != null && !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

						rvc = mapping.get(rvc) != null ? mapping.get(rvc) : null;

						if (rvc != null && rvc.length() > 1) {
							rvc = rvc.substring(0, 1);
						}
						entity.setRev(rvc);

						String supFilingPeriod = (obj[14] != null && !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod);

						String supFilingDate = null;
						try {
							supFilingDate = (obj[15] != null && !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[15];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (supFilingDate != null) {

							filingDate = DateUtil.parseObjToDate(supFilingDate);

							LocalDateTime localDateTime1 = filingDate.atStartOfDay();

							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[16] != null && !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;

						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[17] != null && !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);

						String diffPercent = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;

						String irnSrcType = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;
						entity.setIrnSrcType(irnSrcType);

						String irnNo = (obj[19] != null && !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;
						entity.setIrnNo(irnNo);

						String irnGenDate = null;
						try {
							irnGenDate = (obj[20] != null && !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[20];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnGenDate != null) {
							 irnDate = LocalDateTime.parse(irnGenDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
						            .toLocalDate();
							 String formattedIrnDate = irnDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
							  irnDate = LocalDate.parse(formattedIrnDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));



							entity.setIrnGenDate(irnDate);	
						}
						
						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, sgstin, invType, inv, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bEcomItemEntity itemEntity = new GetGstr2bEcomItemEntity();

						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[9]));
						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[10]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[11]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[12]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[13]));
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[8]));
						
		
						
						entity.setLineItems(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						ecomDataList.add(entity);
					}
				}
			}
		} catch (Exception e) {
			String msg = String.format("error occured while converting into Ecom entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}
		return ecomDataList;
	
	}
	
	//ECOMA code
	public List<GetGstr2bB2bEcomaHeaderEntity> convertEcomaWorkSheetDataToList(Object[][] objList,
			int columnCount, String rgstin, String taxPeriod, Long fileId, String genDate) {

		List<GetGstr2bB2bEcomaHeaderEntity> ecomaHeaderEntities = Lists.newArrayList();

		try {
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

					if (invoiceNumber != null) {
						GetGstr2bB2bEcomaHeaderEntity entity = new GetGstr2bB2bEcomaHeaderEntity();
						String stin = (obj[0] != null && !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
						if (stin != null && stin.length() > 22) {
							stin = stin.substring(0, 22);
						}
						entity.setSGstin(stin);

						entity.setRGstin(rgstin);

						String stName = (obj[1] != null && !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
						if (stName != null && stName.length() > 100) {
							stName = stName.substring(0, 100);
						}
						entity.setSupTradeName(stName);

						String invType = (obj[3] != null && !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
						if (invType != null && invType.length() > 20) {
							invType = invType.substring(0, 20);
						}
						if (invType != null) {
							if (invType.equalsIgnoreCase("Regular")) {
								entity.setInvoiceType("R");
							} else {
								entity.setInvoiceType(invType);
							}
						}

						String inv = invoiceNumber;
						if (inv != null && inv.length() > 16) {
							inv = inv.substring(0, 16);
						}
						entity.setInvoiceNumber(inv);

						String invDateStr = null;
						try {
							invDateStr = (obj[4] != null && !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[4];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate invoiceDate = null;
						if (invDateStr != null) {
							invoiceDate = DateUtil.parseObjToDate(invDateStr);
							
							
							// adding linking key
							entity.setLnkingDocKey(deriveLinkingKey(
									invoiceDate,
									entity.getRGstin(), entity.getSGstin(), "RNV", invoiceNumber));
					
							
							LocalDateTime localDateTime1 = invoiceDate.atStartOfDay();
							entity.setInvoiceDate(localDateTime1);
						}

						entity.setTaxPeriod(taxPeriod);

						entity.setSupInvoiceValue(getAppropriateValueFromObject(obj[5]));
						String pos = removeSpecialCharacters(obj[6]);
						entity.setPos(getStateCodeForStateName(pos));

						String rch = (obj[7] != null && !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;

						rch = mapping.get(rch) != null ? mapping.get(rch) : null;

						if (rch != null && rch.length() > 1) {
							rch = rch.substring(0, 1);
						}
						entity.setRev(rch);

						String supFilingPeriod = (obj[14] != null && !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;

						entity.setSupFilingPeriod(supFilingPeriod != null ? supFilingPeriod.replace("'", "") : null);

						String supFilingDate = null;
						try {
							supFilingDate = (obj[15] != null && !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[15];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							supFilingDate = dateFormat.format(date1);
						}
						LocalDate filingDate = null;
						if (supFilingDate != null) {
							filingDate = DateUtil.parseObjToDate(supFilingDate);
							LocalDateTime localDateTime1 = filingDate.atStartOfDay();
							entity.setSupFilingDate(localDateTime1);
						}

						String itcAvailable = (obj[16] != null && !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;

						itcAvailable = mapping.get(itcAvailable) != null ? mapping.get(itcAvailable) : null;
						entity.setItcAvailable(itcAvailable);

						String rsn = (obj[17] != null && !obj[17].toString().trim().isEmpty())
								? String.valueOf(obj[17]).trim() : null;

						rsn = mapping.get(rsn) != null ? mapping.get(rsn) : null;
						entity.setRsn(rsn);
						

						String irnSrcType = (obj[18] != null && !obj[18].toString().trim().isEmpty())
								? String.valueOf(obj[18]).trim() : null;
						entity.setIrnSrcType(irnSrcType);

						String irnNo = (obj[19] != null && !obj[19].toString().trim().isEmpty())
								? String.valueOf(obj[19]).trim() : null;
						entity.setIrnNo(irnNo);

						String irnGenDate = null;
						try {
							irnGenDate = (obj[20] != null && !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
						} catch (ClassCastException e) {
							DateTime dateTime = (DateTime) obj[20];
							Date date1 = dateTime.toDate();
							SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							invDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnGenDate != null) {
							/*irnDate = LocalDate.parse(irnGenDate,
									DateUtil.SUPPORTED_DATE_FORMAT2);*/
							
							 irnDate = LocalDateTime.parse(irnGenDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
							            .toLocalDate();
								 String formattedIrnDate = irnDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
								  irnDate = LocalDate.parse(formattedIrnDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

							entity.setIrnGenDate(irnDate);	
						}
						
						entity.setIsDelete(false);
						entity.setChecksum("");
						entity.setVersion("1.0");
						entity.setCreatedBy(userName);
						entity.setCreatedOn(LocalDateTime.now());
						entity.setModifiedBy(userName);
						entity.setModifiedOn(LocalDateTime.now());

						String docKey = generateKey(taxPeriod, stin, invType, inv, rgstin);

						entity.setDocKey(docKey);

						GetGstr2bEcomaItemEntity itemEntity = new GetGstr2bEcomaItemEntity();
						itemEntity.setTaxRate(getAppropriateValueFromObject(obj[8]));
						itemEntity.setTaxableValue(getAppropriateValueFromObject(obj[9]));
						itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[10]));
						itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[11]));
						itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[12]));
						itemEntity.setCessAmt(getAppropriateValueFromObject(obj[13]));
					
						entity.setLineItemss(Arrays.asList(itemEntity));
						itemEntity.setHeader(entity);

						ecomaHeaderEntities.add(entity);
					}
				}
			}

		} catch (Exception e) {
			String msg = String.format("error occured while readimg the ECOMA values from file and converting to entity", e);
			LOGGER.error(msg, e);
			new AppException(msg, e);

		}

		return ecomaHeaderEntities;
	}
	
	private String deriveLinkingKey(LocalDate date, String cgstin,
			String sgstin, String docType, String documentNumber) {
		String finYear = GenUtil.getFinYear(date);

		return new StringJoiner(DOC_KEY_JOINER).add(finYear).add(cgstin)
				.add(sgstin).add(docType).add(documentNumber).toString();
	}
}
