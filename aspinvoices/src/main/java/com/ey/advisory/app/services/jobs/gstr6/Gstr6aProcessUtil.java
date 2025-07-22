package com.ey.advisory.app.services.jobs.gstr6;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aspose.cells.DateTime;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2bItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingB2baItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnItemEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaHeaderEntity;
import com.ey.advisory.app.data.entities.gstr6a.GetGstr6aStagingCdnaItemEntity;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * 
 * @author Ravindra V S
 *
 */
@Component
public class Gstr6aProcessUtil {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;
	
	public List<GetGstr6aStagingB2bHeaderEntity> convertB2bWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {
		List<GetGstr6aStagingB2bHeaderEntity> b2bDataList = Lists
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

				if (invoiceNumber != null && !invoiceNumber.contains("Total")) {
					GetGstr6aStagingB2bHeaderEntity entity = new GetGstr6aStagingB2bHeaderEntity();

					entity.setBatchId(batchId);
					String sgstin = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (sgstin != null && sgstin.length() > 22) {
						sgstin = sgstin.substring(0, 22);
					}
					entity.setGstin(sgstin);

					String stName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					entity.setCtin(cgstin);

					String inv = invoiceNumber;
					if (inv != null && inv.length() > 16) {
						inv = inv.substring(0, 16);
					}
					entity.setInvNum(inv);
					entity.setDocNum(inv);
					String invType = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (invType != null && invType.length() > 20) {
						invType = invType.substring(0, 20);
					}
					entity.setInvType(invType);

					String invDateStr = null;
					try {
						invDateStr = StringUtils.isEmpty((String) obj[4]) ? null
								: (String) obj[4];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[4];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						invDateStr = dateFormat.format(date1);
					}
					LocalDate invoiceDate = null;
					if (invDateStr != null) {
						invoiceDate = LocalDate.parse(invDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setInvDate(invoiceDate);
						entity.setDocDate(invoiceDate);
					}
					// adding inv_key
					String docType = invType == "R" ? "INV"
							: (invType == "C" ? "CR"
									: (invType == "D" ? "DR"
											: (invType == "B" ? "BOS"
													: invType)));
					String fy = GenUtil.getFinYear(invoiceDate);
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(inv);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(sgstin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);
			
					entity.setInvKey(generateKey);
			

					entity.setTaxPeriod(taxPeriod);
					entity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setInvValue(getAppropriateValueFromObject(obj[5]));

					String pos = removeSpecialCharacters(obj[6]);

					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[7] != null
							&& !obj[7].toString().trim().isEmpty())
									? String.valueOf(obj[7]).trim() : null;
					if (rvc != null && rvc.length() > 1) {
						rvc = rvc.substring(0, 1);
					}
					entity.setReverseCharge(rvc);

					entity.setTaxableValue(getAppropriateValueFromObject(obj[9]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[10]));
					entity.setCgstAmt(getAppropriateValueFromObject(obj[11]));
					entity.setSgstAmt(getAppropriateValueFromObject(obj[12]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[13]));
					String cfs = (obj[14] != null
							&& !obj[14].toString().trim().isEmpty())
									? String.valueOf(obj[14]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfs(cfs);


					String filPeriod = (obj[15] != null
							&& !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setCfp(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}
					
					String sourceTypeIrn = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					entity.setIrnSourceType(sourceTypeIrn);
					
					String irnNum = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
							? String.valueOf(obj[17]).trim() : null;					
					entity.setIrnNumber(irnNum);	
					
					String irnDateStr = null;
					try {
						irnDateStr = StringUtils.isEmpty((String) obj[18]) ? null
										: (String) obj[18];
						} catch (ClassCastException e) {
								DateTime dateTime = (DateTime) obj[18];
								Date date1 = dateTime.toDate();
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"dd-MM-yyyy");
								irnDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnDateStr != null) {
							irnDate = LocalDate.parse(irnDateStr,
							DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnDate(irnDate);	
						}
						
					entity.setDelete(false);
					entity.setChksum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr6aStagingB2bItemEntity itemEntity = new GetGstr6aStagingB2bItemEntity();

					itemEntity
							.setTaxRate(getAppropriateValueFromObject(obj[8]));

					itemEntity.setTaxableValue(
							getAppropriateValueFromObject(obj[9]));
					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[10]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[11]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[12]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[13]));

					itemEntity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);

						entity.setChksum(chkSum);
					}
					b2bDataList.add(entity);
				}
			}
		}

		return b2bDataList;
	}

	public List<GetGstr6aStagingB2baHeaderEntity> convertB2baWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr6aStagingB2baHeaderEntity> b2baInvoicesHeaderEntities = Lists
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

				if (invoiceNumber != null && !invoiceNumber.contains("Total")) {
					GetGstr6aStagingB2baHeaderEntity entity = new GetGstr6aStagingB2baHeaderEntity();

					entity.setBatchId(batchId);
					String orgInvoiceNumber = null;
					try {
						orgInvoiceNumber = (String) obj[0];
					} catch (ClassCastException e) {
						try {
							Double invDouble = (Double) obj[0];
							orgInvoiceNumber = String
									.valueOf(invDouble.longValue());
						} catch (ClassCastException ex) {
							Integer invDouble = (Integer) obj[0];
							orgInvoiceNumber = String
									.valueOf(invDouble.intValue());
						}
					}

					String origInvNum = orgInvoiceNumber;
					if (origInvNum != null && origInvNum.length() > 20) {
						origInvNum = origInvNum.substring(0, 20);
					}
					entity.setOrgDocNum(origInvNum);
					entity.setOrgInvNum(origInvNum);

					String origInvDateStr = null;
					try {
						origInvDateStr = StringUtils.isEmpty((String) obj[1])
								? null : (String) obj[1];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[1];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						origInvDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origInvDateStr != null) {
						orgInvoiceDate = LocalDate.parse(origInvDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setOrgDocDate(orgInvoiceDate);
						entity.setOrgInvDate(orgInvoiceDate);
					}

					String stin = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]).trim() : null;
					if (stin != null && stin.length() > 22) {
						stin = stin.substring(0, 22);
					}
					entity.setGstin(stin);

					entity.setCtin(cgstin);

					String stName = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					String invType = (obj[4] != null
							&& !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
					if (invType != null && invType.length() > 20) {
						invType = invType.substring(0, 20);
					}
					entity.setInvType(invType);

					String inv = invoiceNumber;
					if (inv != null && inv.length() > 16) {
						inv = inv.substring(0, 16);
					}
					entity.setInvNum(inv);
					entity.setDocNum(inv);

					String invDateStr = null;
					try {
						invDateStr = StringUtils.isEmpty((String) obj[6]) ? null
								: (String) obj[6];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[6];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						invDateStr = dateFormat.format(date1);
					}
					LocalDate invoiceDate = null;
					if (invDateStr != null) {
						invoiceDate = LocalDate.parse(invDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setInvDate(invoiceDate);
						entity.setDocDate(invoiceDate);
					}

					String docType = invType == "R" ? "INV"
							: (invType == "C" ? "CR"
									: (invType == "D" ? "DR"
											: (invType == "B" ? "BOS"
													: invType)));
					String fy = GenUtil.getFinYear(invoiceDate);
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(inv);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);
		
					entity.setInvKey(generateKey);
					entity.setTaxPeriod(taxPeriod);
					entity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setInvValue(getAppropriateValueFromObject(obj[7]));
					String pos = removeSpecialCharacters(obj[8]);
					entity.setPos(getStateCodeForStateName(pos));

					String rch = (obj[9] != null
							&& !obj[9].toString().trim().isEmpty())
									? String.valueOf(obj[9]).trim() : null;
					if (rch != null && rch.length() > 1) {
						rch = rch.substring(0, 1);
					}
					entity.setRevCharge(rch);

					entity.setTaxableValue(getAppropriateValueFromObject(obj[11]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[12]));
					entity.setCgstAmt(getAppropriateValueFromObject(obj[13]));
					entity.setSgstAmt(getAppropriateValueFromObject(obj[14]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[15]));
					String cfs = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfs(cfs);

					String filPeriod = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setCfp(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					entity.setDelete(false);
					entity.setChksum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr6aStagingB2baItemEntity itemEntity = new GetGstr6aStagingB2baItemEntity();

					itemEntity
							.setTaxRate(getAppropriateValueFromObject(obj[10]));

					itemEntity.setTaxableValue(
							getAppropriateValueFromObject(obj[11]));

					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[12]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[13]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[14]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[15]));

					itemEntity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setChksum(chkSum);
					}
					b2baInvoicesHeaderEntities.add(entity);
				}
			}
		}

		return b2baInvoicesHeaderEntities;
	}

	public List<GetGstr6aStagingCdnHeaderEntity> convertCdnrWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr6aStagingCdnHeaderEntity> cdnrInvoicesHeaderEntities = Lists
				.newArrayList();
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				String noteNumber = null;
				try {
					noteNumber = (String) obj[3];
				} catch (ClassCastException e) {
					try {
						Double noteDouble = (Double) obj[3];
						noteNumber = String.valueOf(noteDouble.longValue());
					} catch (ClassCastException ex) {
						Integer noteDouble = (Integer) obj[3];
						noteNumber = String.valueOf(noteDouble.intValue());
					}
				}

				if (noteNumber != null && !noteNumber.contains("Total")) {

					GetGstr6aStagingCdnHeaderEntity entity = new GetGstr6aStagingCdnHeaderEntity();

					entity.setBatchId(batchId);
					String sgst = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (sgst != null && sgst.length() > 15) {
						sgst = sgst.substring(0, 15);
					}
					entity.setGstin(sgst);
					entity.setCtin(cgstin);

					String stName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					String noteType = (String) obj[2];
					if (noteType != null) {
						if (noteType.equalsIgnoreCase("Debit note")) {
							entity.setNoteType("D");
						} else {
							entity.setNoteType("C");
						}
					}

					String cdNum = noteNumber;
					if (cdNum != null && cdNum.length() > 16) {
						cdNum = cdNum.substring(0, 16);
					}
					entity.setNoteNum(cdNum);
					entity.setDocNum(cdNum);

					String supType = (obj[4] != null
							&& !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
					if (supType != null && supType.length() > 10) {
						supType = supType.substring(0, 20);
					}
					entity.setSupType(supType);

					String origInvDateStr = null;
					try {
						origInvDateStr = StringUtils.isEmpty((String) obj[5])
								? null : (String) obj[5];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[5];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						origInvDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origInvDateStr != null) {
						orgInvoiceDate = LocalDate.parse(origInvDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setNoteDate(orgInvoiceDate);
						entity.setDocDate(orgInvoiceDate);
					}
					// adding inv_key
					String docType = noteType == "R" ? "INV"
							: (noteType == "C" || noteType == "Credit note"
									? "CR"
									: (noteType == "D"
											|| noteType == "Debit note" ? "DR"
													: (noteType == "B" ? "BOS"
															: noteType)));
					String fy = GenUtil.getFinYear(orgInvoiceDate);

					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(cdNum);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(sgst);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);

					entity.setInvKey(generateKey);

					entity.setInvValue(getAppropriateValueFromObject(obj[6]));

					String pos = removeSpecialCharacters(obj[7]);
					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[8] != null
							&& !obj[8].toString().trim().isEmpty())
									? String.valueOf(obj[8]).trim() : null;
					if (rvc != null && rvc.length() > 2) {
						rvc = rvc.substring(0, 1);
					}
					entity.setdFlag(rvc);

					entity.setTaxPeriod(taxPeriod);
					entity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setTaxableValue(getAppropriateValueFromObject(obj[10]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[11]));
					entity.setCgstAmt(getAppropriateValueFromObject(obj[12]));
					entity.setSgstAmt(getAppropriateValueFromObject(obj[13]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[14]));
					String cfs = (obj[15] != null
							&& !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfs(cfs);

					String filPeriod = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setCfp(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}
					
					String sourceTypeIrn = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					entity.setIrnSourceType(sourceTypeIrn);
					
					String irnNum = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
							? String.valueOf(obj[18]).trim() : null;					
					entity.setIrnNumber(irnNum);	
					
					String irnDateStr = null;
					try {
						irnDateStr = StringUtils.isEmpty((String) obj[19]) ? null
										: (String) obj[19];
						} catch (ClassCastException e) {
								DateTime dateTime = (DateTime) obj[19];
								Date date1 = dateTime.toDate();
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"dd-MM-yyyy");
								irnDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnDateStr != null) {
							irnDate = LocalDate.parse(irnDateStr,
							DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnDate(irnDate);	
						}

					entity.setDelete(false);
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr6aStagingCdnItemEntity itemEntity = new GetGstr6aStagingCdnItemEntity();

					itemEntity
							.setTaxRate(getAppropriateValueFromObject(obj[9]));

					itemEntity
							.setTaxableValue(getAppropriateValueFromObject(obj[10]));

					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[11]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[12]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[13]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[14]));

					itemEntity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setChksum(chkSum);
					}
					cdnrInvoicesHeaderEntities.add(entity);
				}
			}
		}

		return cdnrInvoicesHeaderEntities;
	}

	public List<GetGstr6aStagingCdnaHeaderEntity> convertCdnraWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr6aStagingCdnaHeaderEntity> cdnraInvoicesHeaderEntities = Lists
				.newArrayList();
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {
				String noteNumber = null;
				try {
					noteNumber = (String) obj[6];
				} catch (ClassCastException e) {
					try {
						Double noteDouble = (Double) obj[6];
						noteNumber = String.valueOf(noteDouble.longValue());
					} catch (ClassCastException ex) {
						Integer noteDouble = (Integer) obj[6];
						noteNumber = String.valueOf(noteDouble.intValue());
					}
				}
				if (noteNumber != null && !noteNumber.contains("Total")) {
					GetGstr6aStagingCdnaHeaderEntity entity = new GetGstr6aStagingCdnaHeaderEntity();

					entity.setBatchId(batchId);
					String odType = (String) obj[0];
					if (odType != null) {
						if (odType.equalsIgnoreCase("Credit note")) {
							entity.setNoteType("C");
						} else {
							entity.setNoteType("D");
						}
					}

					String orgDocNumber = null;
					try {
						orgDocNumber = (String) obj[1];
					} catch (ClassCastException e) {
						try {
							Double noteDouble = (Double) obj[1];
							orgDocNumber = String
									.valueOf(noteDouble.longValue());
						} catch (ClassCastException ex) {
							Integer noteDouble = (Integer) obj[1];
							orgDocNumber = String
									.valueOf(noteDouble.intValue());
						}
					}
					String odNum = orgDocNumber;
					if (odNum != null && odNum.length() > 16) {
						odNum = odNum.substring(0, 16);
					}
					entity.setOrgNoteNum(odNum);

					String origInvDateStr = null;
					try {
						origInvDateStr = StringUtils.isEmpty((String) obj[2])
								? null : (String) obj[2];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[2];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						origInvDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origInvDateStr != null) {
						orgInvoiceDate = LocalDate.parse(origInvDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setOrgNoteDate(orgInvoiceDate);
					}
					String stin = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (stin != null && stin.length() > 15) {
						stin = stin.substring(0, 15);
					}
					entity.setGstin(stin);
					entity.setCtin(cgstin);

					// adding inv_key
					String docType = odType == "R" ? "INV"
							: (odType == "C" || odType == "Credit note" ? "CR"
									: (odType == "D" || odType == "Debit note"
											? "DR"
											: (odType == "B" ? "BOS"
													: odType)));
					String fy = GenUtil.getFinYear(orgInvoiceDate);
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(noteNumber);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);
		
					entity.setInvKey(generateKey);

					String stName = (obj[4] != null
							&& !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					String noteType = (String) obj[5];
					if (noteType != null) {
						if (noteType.equalsIgnoreCase("Credit note")) {
							entity.setNoteType("C");
						} else {
							entity.setNoteType("D");
						}
					}

					String supType = (obj[7] != null
							&& !obj[7].toString().trim().isEmpty())
									? String.valueOf(obj[7]).trim() : null;
					if (supType != null && supType.length() > 10) {
						supType = supType.substring(0, 20);
					}
					entity.setSupType(supType);

					String InvDateStr = null;
					try {
						InvDateStr = StringUtils.isEmpty((String) obj[8]) ? null
								: (String) obj[8];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[8];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						InvDateStr = dateFormat.format(date1);
					}
					LocalDate InvoiceDate = null;
					if (InvDateStr != null) {
						InvoiceDate = LocalDate.parse(InvDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setNoteDate(InvoiceDate);
						entity.setDocDate(InvoiceDate);
					}

					String notNum = noteNumber;
					if (notNum != null && notNum.length() > 16) {
						notNum = notNum.substring(0, 16);
					}
					entity.setNoteNum(notNum);
					entity.setDocNum(notNum);

					entity.setInvValue(getAppropriateValueFromObject(obj[9]));
					String pos = removeSpecialCharacters(obj[10]);
					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[11] != null
							&& !obj[11].toString().trim().isEmpty())
									? String.valueOf(obj[11]).trim() : null;
					if (rvc != null && rvc.length() > 2) {
						rvc = rvc.substring(0, 1);
					}
					entity.setdFlag(rvc);

					entity.setTaxPeriod(taxPeriod);
					entity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setTaxableValue(getAppropriateValueFromObject(obj[13]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[14]));
					entity.setCgstAmt(getAppropriateValueFromObject(obj[15]));
					entity.setSgstAmt(getAppropriateValueFromObject(obj[16]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[17]));
					String cfs = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfs(cfs);

					String filPeriod = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setCfp(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					entity.setDelete(false);
					entity.setChksum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr6aStagingCdnaItemEntity itemEntity = new GetGstr6aStagingCdnaItemEntity();

					itemEntity
							.setTaxRate(getAppropriateValueFromObject(obj[12]));

					itemEntity
							.setTaxableValue(getAppropriateValueFromObject(obj[13]));

					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[14]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[15]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[16]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[17]));

					itemEntity.setDerTaxPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setChksum(chkSum);
					}
					cdnraInvoicesHeaderEntities.add(entity);
				}
			}
		}
		return cdnraInvoicesHeaderEntities;
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
	
}
