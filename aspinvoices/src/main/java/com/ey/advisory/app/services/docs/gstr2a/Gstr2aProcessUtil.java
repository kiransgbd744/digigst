package com.ey.advisory.app.services.docs.gstr2a;

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
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2bInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingB2baInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingCdnaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingEcomaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingImpgSezItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr2aStagingIsdaInvoicesItemEntity;
import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.app.data.repositories.client.StatecodeRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.app.services.jobs.gstr2a.GetGstr2aAmdhistUtil;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.google.common.collect.Lists;
import com.ibm.icu.text.SimpleDateFormat;

/**
 * 
 * @author Anand3.M
 *
 */
@Component
public class Gstr2aProcessUtil {

	@Autowired
	@Qualifier("StatecodeRepository")
	private StatecodeRepository statecodeRepository;

	@Autowired
	@Qualifier("DefaultInwardTransDocKeyGenerator")
	private DocKeyGenerator<InwardTransDocument, String> docKeyGenerator;

	@Autowired
	private GetGstr2aAmdhistUtil amdUtil;

	public List<GetGstr2aStagingB2bInvoicesHeaderEntity> convertB2bWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {
		List<GetGstr2aStagingB2bInvoicesHeaderEntity> b2bDataList = Lists
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
					GetGstr2aStagingB2bInvoicesHeaderEntity entity = new GetGstr2aStagingB2bInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setB2bBatchIdGstr2a(batchId);
					String sgstin = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (sgstin != null && sgstin.length() > 22) {
						sgstin = sgstin.substring(0, 22);
					}
					entity.setSgstin(sgstin);

					String stName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					entity.setCgstin(cgstin);

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
					// String generateKey =
					// docKeyGenerator.generateKey(sgstin,invoiceNumber, fy,
					// docType);
					entity.setInvKey(generateKey);
					/*
					 * String invDate = (obj[4] != null &&
					 * !obj[4].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[4]).trim() : null; LocalDate
					 * localInvDate = DateUtil.parseObjToDate(invDate);
					 * entity.setInvDate(localInvDate);
					 */

					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
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
					entity.setRchrg(rvc);

					entity.setTaxable(getAppropriateValueFromObject(obj[9]));
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
					entity.setCfsGstr1(cfs);

					String filDateStr = null;
					try {
						filDateStr = StringUtils.isEmpty((String) obj[15])
								? null : (String) obj[15];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[15];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MMM-yy");
						filDateStr = dateFormat.format(date1);
					}
					LocalDate filDate = null;
					if (filDateStr != null) {
						filDate = LocalDate.parse(filDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT7);
						entity.setFileDate(filDate);
					}

					/*
					 * String filDate = (obj[15] != null &&
					 * !obj[15].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[15]).trim() : null; //LocalDate
					 * localFilDate = DateUtil.parseObjToDate(filDate); if
					 * (filDate != null) {
					 * entity.setFileDate(String.format(filDate,
					 * DateUtil.SUPPORTED_DATE_FORMAT7)); }
					 * //entity.setFileDate(localFilDate);
					 */
					String filPeriod = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);

					String amd = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (amd != null && amd.length() > 1) {
						amd = amd.substring(0, 1);
					}
					entity.setOrgInvAmdType(amd);

					String amdPeriod = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					if (amdPeriod != null && amdPeriod.length() > 6) {
						amdPeriod = amdPeriod.substring(0, 6);
					}
					if (amdPeriod != null) {
						entity.setOrgInvAmdPeriod(String.format(amdPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}
					/*
					 * if(amdPeriod != null) { amdPeriod = "01-" + amdPeriod;
					 * LocalDate localDate = LocalDate.parse(amdPeriod,
					 * DateTimeFormatter.ofPattern("dd-MMM-yy")); String
					 * finalTaxPeriod = (localDate.getMonthValue() < 10 ? "0" +
					 * localDate.getMonthValue() : localDate.getMonthValue()) +
					 * "" + localDate.getYear();
					 * entity.setOrgInvAmdPeriod(finalTaxPeriod); } else {
					 * entity.setOrgInvAmdPeriod(null); }
					 */

					String canDate = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);
					
					String sourceTypeIrn = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]).trim() : null;
					entity.setIrnSrcType(sourceTypeIrn);
					
					String irnNum = (obj[22] != null
							&& !obj[22].toString().trim().isEmpty())
							? String.valueOf(obj[22]).trim() : null;					
					entity.setIrnNum(irnNum);	
					
					String irnDateStr = null;
					try {
						irnDateStr = StringUtils.isEmpty((String) obj[23]) ? null
										: (String) obj[23];
						} catch (ClassCastException e) {
								DateTime dateTime = (DateTime) obj[23];
								Date date1 = dateTime.toDate();
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"dd-MM-yyyy");
								irnDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnDateStr != null) {
							irnDate = LocalDate.parse(irnDateStr,
							DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnGenDate(irnDate);	
						}
						
					entity.setDelete(false);
					// entity.setFileUploadData(true);
					entity.setDiffPercentage(BigDecimal.ZERO);
					entity.setAction("N");
					entity.setChkSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingB2bInvoicesItemEntity itemEntity = new GetGstr2aStagingB2bInvoicesItemEntity();

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

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);

						entity.setChkSum(chkSum);
					}
					b2bDataList.add(entity);
				}
			}
		}

		return b2bDataList;
	}

	public List<GetGstr2aStagingB2baInvoicesHeaderEntity> convertB2baWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingB2baInvoicesHeaderEntity> b2baInvoicesHeaderEntities = Lists
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
					GetGstr2aStagingB2baInvoicesHeaderEntity entity = new GetGstr2aStagingB2baInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setB2bBatchIdGstr2a(batchId);
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
					entity.setOrigInvNum(origInvNum);

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
						entity.setOrigInvDate(orgInvoiceDate);
					}

					/*
					 * String orgInvDate = (obj[1] != null &&
					 * !obj[1].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[1]).trim() : null; LocalDate
					 * localOrgInvDate = DateUtil .parseObjToDate(orgInvDate);
					 * entity.setOrigInvDate(localOrgInvDate);
					 */

					String stin = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]).trim() : null;
					if (stin != null && stin.length() > 22) {
						stin = stin.substring(0, 22);
					}
					entity.setSgstin(stin);

					entity.setCgstin(cgstin);

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
					}

					/*
					 * String InvDate = (obj[6] != null &&
					 * !obj[6].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[6]).trim() : null; LocalDate
					 * localInvDate = DateUtil.parseObjToDate(InvDate);
					 * entity.setInvDate(localInvDate);
					 */
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
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);
					// String generateKey =
					// docKeyGenerator.generateKey(stin,invoiceNumber, fy,
					// docType);
					entity.setInvKey(generateKey);
					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
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
					entity.setRchrg(rch);

					entity.setTaxable(getAppropriateValueFromObject(obj[11]));
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
					entity.setCfsGstr1(cfs);

					/*
					 * String filDateStr = null; try { filDateStr =
					 * StringUtils.isEmpty((String) obj[17]) ? null : (String)
					 * obj[17]; } catch (ClassCastException e) { DateTime
					 * dateTime = (DateTime) obj[17]; Date date1 =
					 * dateTime.toDate(); SimpleDateFormat dateFormat = new
					 * SimpleDateFormat( "dd-MMM-yy"); filDateStr =
					 * dateFormat.format(date1); } LocalDate filDate = null; if
					 * (filDateStr != null) { filDate =
					 * LocalDate.parse(filDateStr,
					 * DateUtil.SUPPORTED_DATE_FORMAT7);
					 * entity.setFileDate(filDate); }
					 */

					String filDate = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					LocalDate localFilDate = DateUtil.parseObjToDate(filDate);
					entity.setFileDate(localFilDate);

					String filPeriod = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);

					String canDate = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);

					String amd = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]).trim() : null;
					if (amd != null && amd.length() > 1) {
						amd = amd.substring(0, 1);
					}
					entity.setOrgInvAmdType(amd);

					String amdPeriod = (obj[22] != null
							&& !obj[22].toString().trim().isEmpty())
									? String.valueOf(obj[22]).trim() : null;
					if (amdPeriod != null && amdPeriod.length() > 6) {
						amdPeriod = amdPeriod.substring(0, 6);
					}
					if (amdPeriod != null) {
						entity.setOrgInvAmdPeriod(String.format(amdPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					entity.setDelete(false);
					entity.setDiffPercentage(BigDecimal.ZERO);
					entity.setAction("N");
					entity.setChkSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingB2baInvoicesItemEntity itemEntity = new GetGstr2aStagingB2baInvoicesItemEntity();

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

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setChkSum(chkSum);
					}
					b2baInvoicesHeaderEntities.add(entity);
				}
			}
		}

		return b2baInvoicesHeaderEntities;
	}

	public List<GetGstr2aStagingCdnInvoicesHeaderEntity> convertCdnrWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingCdnInvoicesHeaderEntity> cdnrInvoicesHeaderEntities = Lists
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

					GetGstr2aStagingCdnInvoicesHeaderEntity entity = new GetGstr2aStagingCdnInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setCdnBatchIdGstr2a(batchId);
					String sgst = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (sgst != null && sgst.length() > 15) {
						sgst = sgst.substring(0, 15);
					}
					entity.setGstin(sgst);
					entity.setCountergstin(cgstin);

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
							entity.setCredDebRefVoucher("D");
						} else {
							entity.setCredDebRefVoucher("C");
						}
					}

					String cdNum = noteNumber;
					if (cdNum != null && cdNum.length() > 16) {
						cdNum = cdNum.substring(0, 16);
					}
					entity.setCredDebRefVoucherNum(cdNum);

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
						entity.setCredDebRefVoucherDate(orgInvoiceDate);
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
					// String generateKey =
					// docKeyGenerator.generateKey(sgst,cdNum, fy, docType);
					entity.setInvKey(generateKey);
					/*
					 * String InvDate = (obj[5] != null &&
					 * !obj[5].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[5]).trim() : null; LocalDate
					 * localInvDate = DateUtil.parseObjToDate(InvDate);
					 * entity.setCredDebRefVoucherDate(localInvDate);
					 */

					entity.setNotevalue(getAppropriateValueFromObject(obj[6]));

					String pos = removeSpecialCharacters(obj[7]);
					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[8] != null
							&& !obj[8].toString().trim().isEmpty())
									? String.valueOf(obj[8]).trim() : null;
					if (rvc != null && rvc.length() > 2) {
						rvc = rvc.substring(0, 1);
					}
					entity.setRcrg(rvc);

					entity.setTaxPeriod(taxPeriod);
					entity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setTaxVal(getAppropriateValueFromObject(obj[10]));
					entity.setIgstamt(getAppropriateValueFromObject(obj[11]));
					entity.setCgstamt(getAppropriateValueFromObject(obj[12]));
					entity.setSgstamt(getAppropriateValueFromObject(obj[13]));
					entity.setCessamt(getAppropriateValueFromObject(obj[14]));
					String cfs = (obj[15] != null
							&& !obj[15].toString().trim().isEmpty())
									? String.valueOf(obj[15]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfsGstr1(cfs);

					/*
					 * String filDateStr = null; try { filDateStr =
					 * StringUtils.isEmpty((String) obj[16]) ? null : (String)
					 * obj[16]; } catch (ClassCastException e) { DateTime
					 * dateTime = (DateTime) obj[16]; Date date1 =
					 * dateTime.toDate(); SimpleDateFormat dateFormat = new
					 * SimpleDateFormat( "dd-MMM-yy"); filDateStr =
					 * dateFormat.format(date1); } LocalDate filDate = null; if
					 * (filDateStr != null) { filDate =
					 * LocalDate.parse(filDateStr,
					 * DateUtil.SUPPORTED_DATE_FORMAT7);
					 * entity.setFileDate(filDate); }
					 */

					String filDate = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					LocalDate localFilDate = DateUtil.parseObjToDate(filDate);
					entity.setFileDate(localFilDate);

					String filPeriod = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);

					String amd = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					if (amd != null && amd.length() > 1) {
						amd = amd.substring(0, 1);
					}
					entity.setOrgInvAmdType(amd);

					String amdPeriod = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					if (amdPeriod != null && amdPeriod.length() > 6) {
						amdPeriod = amdPeriod.substring(0, 6);
					}
					if (amdPeriod != null) {
						entity.setOrgInvAmdPeriod(String.format(amdPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String canDate = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);
					
					String sourceTypeIrn = (obj[22] != null
							&& !obj[22].toString().trim().isEmpty())
									? String.valueOf(obj[22]).trim() : null;
					entity.setIrnSrcType(sourceTypeIrn);
					
					String irnNum = (obj[23] != null
							&& !obj[23].toString().trim().isEmpty())
							? String.valueOf(obj[23]).trim() : null;					
					entity.setIrnNum(irnNum);	
					
					String irnDateStr = null;
					try {
						irnDateStr = StringUtils.isEmpty((String) obj[24]) ? null
										: (String) obj[24];
						} catch (ClassCastException e) {
								DateTime dateTime = (DateTime) obj[24];
								Date date1 = dateTime.toDate();
								SimpleDateFormat dateFormat = new SimpleDateFormat(
										"dd-MM-yyyy");
								irnDateStr = dateFormat.format(date1);
						}
						LocalDate irnDate = null;
						if (irnDateStr != null) {
							irnDate = LocalDate.parse(irnDateStr,
							DateUtil.SUPPORTED_DATE_FORMAT2);
							entity.setIrnGenDate(irnDate);	
						}

					entity.setDelete(false);
					entity.setDiffvalue(BigDecimal.ZERO);
					entity.setCheckSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingCdnInvoicesItemEntity itemEntity = new GetGstr2aStagingCdnInvoicesItemEntity();

					itemEntity
							.setTaxrate(getAppropriateValueFromObject(obj[9]));

					itemEntity
							.setTaxval(getAppropriateValueFromObject(obj[10]));

					itemEntity
							.setIgstamt(getAppropriateValueFromObject(obj[11]));
					itemEntity
							.setCgstamt(getAppropriateValueFromObject(obj[12]));
					itemEntity
							.setSgstamt(getAppropriateValueFromObject(obj[13]));
					itemEntity
							.setCessamt(getAppropriateValueFromObject(obj[14]));

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setCheckSum(chkSum);
					}
					cdnrInvoicesHeaderEntities.add(entity);
				}
			}
		}

		return cdnrInvoicesHeaderEntities;
	}

	public List<GetGstr2aStagingCdnaInvoicesHeaderEntity> convertCdnraWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingCdnaInvoicesHeaderEntity> cdnraInvoicesHeaderEntities = Lists
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
					GetGstr2aStagingCdnaInvoicesHeaderEntity entity = new GetGstr2aStagingCdnaInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setCdnBatchIdGstr2a(batchId);
					String odType = (String) obj[0];
					if (odType != null) {
						if (odType.equalsIgnoreCase("Credit note")) {
							entity.setOriCredDebType("C");
						} else {
							entity.setOriCredDebType("D");
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
					entity.setOriCredDebNum(odNum);

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
						entity.setOriCredDebDate(orgInvoiceDate);
					}
					String stin = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (stin != null && stin.length() > 15) {
						stin = stin.substring(0, 15);
					}
					entity.setGstin(stin);
					entity.setCountergstin(cgstin);

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
					inwardDoc.setDocNo(odNum);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);
					// String generateKey =
					// docKeyGenerator.generateKey(stin,odNum, fy, docType);
					entity.setInvKey(generateKey);

					/*
					 * String orgInvDate = (obj[2] != null &&
					 * !obj[2].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[2]).trim() : null; LocalDate
					 * localOrgInvDate = DateUtil .parseObjToDate(orgInvDate);
					 * entity.setOriCredDebDate(localOrgInvDate);
					 */

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
							entity.setCredDebRefVoucher("C");
						} else {
							entity.setCredDebRefVoucher("D");
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
						entity.setCredDebRefVoucherDate(InvoiceDate);
					}

					/*
					 * String invDate = (obj[8] != null &&
					 * !obj[8].toString().trim().isEmpty()) ?
					 * String.valueOf(obj[8]).trim() : null; LocalDate
					 * localInvDate = DateUtil.parseObjToDate(invDate);
					 * entity.setCredDebRefVoucherDate(localInvDate);
					 */

					String notNum = noteNumber;
					if (notNum != null && notNum.length() > 16) {
						notNum = notNum.substring(0, 16);
					}
					entity.setCredDebRefVoucherNum(notNum);

					entity.setNotevalue(getAppropriateValueFromObject(obj[9]));
					String pos = removeSpecialCharacters(obj[10]);
					entity.setPos(getStateCodeForStateName(pos));

					String rvc = (obj[11] != null
							&& !obj[11].toString().trim().isEmpty())
									? String.valueOf(obj[11]).trim() : null;
					if (rvc != null && rvc.length() > 2) {
						rvc = rvc.substring(0, 1);
					}
					entity.setRcrg(rvc);

					entity.setTaxPeriod(taxPeriod);
					entity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setTaxVal(getAppropriateValueFromObject(obj[13]));
					entity.setIgstamt(getAppropriateValueFromObject(obj[14]));
					entity.setCgstamt(getAppropriateValueFromObject(obj[15]));
					entity.setSgstamt(getAppropriateValueFromObject(obj[16]));
					entity.setCessamt(getAppropriateValueFromObject(obj[17]));
					String cfs = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (cfs != null && cfs.length() > 1) {
						cfs = cfs.substring(0, 1);
					}
					entity.setCfsGstr1(cfs);

					String filDate = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					LocalDate localFilDate = DateUtil.parseObjToDate(filDate);
					entity.setFileDate(localFilDate);

					String filPeriod = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[21] != null
							&& !obj[21].toString().trim().isEmpty())
									? String.valueOf(obj[21]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);

					String amd = (obj[22] != null
							&& !obj[22].toString().trim().isEmpty())
									? String.valueOf(obj[22]).trim() : null;
					if (amd != null && amd.length() > 1) {
						amd = amd.substring(0, 1);
					}
					entity.setOrgInvAmdType(amd);

					String amdPeriod = (obj[23] != null
							&& !obj[23].toString().trim().isEmpty())
									? String.valueOf(obj[23]).trim() : null;
					if (amdPeriod != null && amdPeriod.length() > 6) {
						amdPeriod = amdPeriod.substring(0, 6);
					}
					if (amdPeriod != null) {
						entity.setOrgInvAmdPeriod(String.format(amdPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String canDate = (obj[24] != null
							&& !obj[24].toString().trim().isEmpty())
									? String.valueOf(obj[24]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);

					entity.setDelete(false);
					entity.setDiffvalue(BigDecimal.ZERO);
					entity.setCheckSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingCdnaInvoicesItemEntity itemEntity = new GetGstr2aStagingCdnaInvoicesItemEntity();

					itemEntity
							.setTaxrate(getAppropriateValueFromObject(obj[12]));

					itemEntity
							.setTaxval(getAppropriateValueFromObject(obj[13]));

					itemEntity
							.setIgstamt(getAppropriateValueFromObject(obj[14]));
					itemEntity
							.setCgstamt(getAppropriateValueFromObject(obj[15]));
					itemEntity
							.setSgstamt(getAppropriateValueFromObject(obj[16]));
					itemEntity
							.setCessamt(getAppropriateValueFromObject(obj[17]));

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setCheckSum(chkSum);
					}
					cdnraInvoicesHeaderEntities.add(entity);
				}
			}
		}
		return cdnraInvoicesHeaderEntities;
	}

	public List<GetGstr2aStagingIsdInvoicesHeaderEntity> convertIsdWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingIsdInvoicesHeaderEntity> isdInvoicesHeaderEntities = Lists
				.newArrayList();
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				String isdDocNumber = null;
				String isdCredNoteNum = null;
				try {
					isdDocNumber = (String) obj[4];
				} catch (ClassCastException e) {
					try {
						Double isdDoc = (Double) obj[4];
						isdDocNumber = String.valueOf(isdDoc.longValue());
					} catch (ClassCastException ex) {
						Integer isdDoc = (Integer) obj[4];
						isdDocNumber = String.valueOf(isdDoc.intValue());
					}
				}
				

				//if (isdDocNumber != null && !isdDocNumber.contains("Total")) {

					GetGstr2aStagingIsdInvoicesHeaderEntity entity = new GetGstr2aStagingIsdInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setIsdBatchIdGstr2a(batchId);
					entity.setItcElg(removeSpecialCharacters(obj[0]));

					String stin = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (stin != null && stin.length() > 15) {
						stin = stin.substring(0, 15);
					}
					entity.setGstin(stin);
					entity.setCgstin(cgstin);

					String stName = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					String iDoc = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (iDoc != null && iDoc.length() > 10) {
						iDoc = iDoc.substring(0, 10);

					}
					entity.setIsdDocumentType(iDoc);

					String DocNum = isdDocNumber;
					if (DocNum != null && DocNum.length() > 16) {
						DocNum = DocNum.substring(0, 16);
					}
					entity.setDocumentNumber(DocNum);
					
					//isd credit note number
					try {
						if(iDoc.equalsIgnoreCase("ISDCN"))
						{
						isdCredNoteNum = (String) obj[6];
						}
												
					} catch (ClassCastException e) {
						try {
							Double isdCrDoc = (Double) obj[6];
							isdCredNoteNum = String.valueOf(isdCrDoc.longValue());
						} catch (ClassCastException ex) {
							Integer isdCrDoc = (Integer) obj[6];
							isdCredNoteNum = String.valueOf(isdCrDoc.intValue());
						}
					}

					//credit note num
					if(iDoc.equalsIgnoreCase("ISDCN"))
					{
					String creditNotNum = isdCredNoteNum;
					if (creditNotNum != null && creditNotNum.length() > 16) {
						creditNotNum = creditNotNum.substring(0, 16);
					}
					entity.setDocumentNumber(creditNotNum);
					}
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
						entity.setDocumentDate(orgInvoiceDate);
					}
					//ISD Credit note date
					String isdCrDateStr = null;
					try {
						if(iDoc.equalsIgnoreCase("ISDCN"))
						{
						isdCrDateStr = StringUtils.isEmpty((String) obj[7])
								? null : (String) obj[7];
						}
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[7];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						isdCrDateStr = dateFormat.format(date1);
					}
					if(iDoc.equalsIgnoreCase("ISDCN"))
					{
					LocalDate isdCrDate = null;
					if (isdCrDateStr != null) {
						isdCrDate = LocalDate.parse(isdCrDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setDocumentDate(isdCrDate);
					}
					}
					//////
					
					// adding inv_key
					String docType = iDoc == "R" ? "INV"
							: (iDoc == "C" || iDoc == "Credit note" ? "CR"
									: (iDoc == "D" || iDoc == "Debit note"
											? "DR"
											: (iDoc == "B" ? "BOS" : iDoc)));
					String fy = GenUtil.getFinYear(orgInvoiceDate);
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(DocNum);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);

					entity.setInvKey(generateKey);

					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setIgstamt(getAppropriateValueFromObject(obj[10]));
					entity.setCgstamt(getAppropriateValueFromObject(obj[11]));
					entity.setSgstamt(getAppropriateValueFromObject(obj[12]));
					entity.setCesamt(getAppropriateValueFromObject(obj[13]));
					String cfs6b = (obj[14] != null
							&& !obj[14].toString().trim().isEmpty())
									? String.valueOf(obj[14]).trim() : null;
					if (cfs6b != null && cfs6b.length() > 1) {
						cfs6b = cfs6b.substring(0, 1);
					}
					entity.setCounFillStatus(cfs6b);

					/** need to add amendment entity.set(amd); **/

					entity.setDelete(false);
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingIsdInvoicesItemEntity itemEntity = new GetGstr2aStagingIsdInvoicesItemEntity();
					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[10]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[11]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[12]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[13]));
					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs6b != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs6b);
						entity.setCheckSum(chkSum);
					}
					isdInvoicesHeaderEntities.add(entity);
				//}
			}
		}
		return isdInvoicesHeaderEntities;
	}

	@SuppressWarnings("deprecation")
	public List<GetGstr2aStagingIsdaInvoicesHeaderEntity> convertIsdaWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingIsdaInvoicesHeaderEntity> isdaInvoicesHeaderEntities = Lists
				.newArrayList();
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				String DocNumber = null;
				try {
					DocNumber = (String) obj[7];
				} catch (ClassCastException e) {
					try {
						Double isdDoc = (Double) obj[7];
						DocNumber = String.valueOf(isdDoc.longValue());
					} catch (ClassCastException ex) {
						Integer isdDoc = (Integer) obj[7];
						DocNumber = String.valueOf(isdDoc.intValue());
					}
				}

				//if (DocNumber != null && !DocNumber.contains("Total")) {

					GetGstr2aStagingIsdaInvoicesHeaderEntity entity = new GetGstr2aStagingIsdaInvoicesHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setIsdaBatchIdGstr2a(batchId);
					String oIsdDoc = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (oIsdDoc != null && oIsdDoc.length() > 10) {
						oIsdDoc = oIsdDoc.substring(0, 10);
					}
					entity.setOrgIsdDocumentType(oIsdDoc);

					String isdDocNumber = null;
					try {
						isdDocNumber = (String) obj[1];
					} catch (ClassCastException e) {
						try {
							Double noteDouble = (Double) obj[1];
							isdDocNumber = String
									.valueOf(noteDouble.longValue());
						} catch (ClassCastException ex) {
							Integer noteDouble = (Integer) obj[1];
							isdDocNumber = String
									.valueOf(noteDouble.intValue());
						}
					}
					String idNum = isdDocNumber;
					if (idNum != null && idNum.length() > 16) {
						idNum = idNum.substring(0, 16);
					}
					entity.setOriginalDocumentNumber(idNum);

					String origDocDateStr = null;
					try {
						origDocDateStr = StringUtils.isEmpty((String) obj[2])
								? null : (String) obj[2];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[2];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						origDocDateStr = dateFormat.format(date1);
					}
					LocalDate orgInvoiceDate = null;
					if (origDocDateStr != null) {
						orgInvoiceDate = LocalDate.parse(origDocDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setOriginalDocumentDate(orgInvoiceDate);
					}

					String stin = (obj[4] != null
							&& !obj[4].toString().trim().isEmpty())
									? String.valueOf(obj[4]).trim() : null;
					if (stin != null && stin.length() > 15) {
						stin = stin.substring(0, 15);
					}
					entity.setGstin(stin);
					entity.setCgstin(cgstin);
					// adding inv_key
					String docType = oIsdDoc == "R" ? "INV"
							: (oIsdDoc == "C" || oIsdDoc == "Credit note" ? "CR"
									: (oIsdDoc == "D" || oIsdDoc == "Debit note"
											? "DR"
											: (oIsdDoc == "B" ? "BOS"
													: oIsdDoc)));
					String fy = GenUtil.getFinYear(orgInvoiceDate);
					InwardTransDocument inwardDoc = new InwardTransDocument();
					inwardDoc.setFinYear(fy);
					inwardDoc.setDocNo(idNum);
					inwardDoc.setDocType(docType);
					inwardDoc.setSgstin(stin);// output
					inwardDoc.setCgstin(cgstin);// input

					String generateKey = docKeyGenerator.generateKey(inwardDoc);

					entity.setInvKey(generateKey);

					entity.setItcElg(removeSpecialCharacters(obj[3]));

					String stName = (obj[5] != null
							&& !obj[5].toString().trim().isEmpty())
									? String.valueOf(obj[5]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					String iDoc = (obj[6] != null
							&& !obj[6].toString().trim().isEmpty())
									? String.valueOf(obj[6]).trim() : null;
					if (iDoc != null && iDoc.length() > 10) {
						iDoc = iDoc.substring(0, 10);

					}
					entity.setIsdDocumentType(iDoc);

					String dNum = DocNumber;
					if (dNum != null && dNum.length() > 16) {
						dNum = dNum.substring(0, 16);
					}
					entity.setDocumentNumber(dNum);
//CR number-----------------------
					String creditNoteNum = null;
					try {
						if(iDoc.equalsIgnoreCase("ISDCN"))
						{
						creditNoteNum = (String) obj[9];
						}
					} catch (ClassCastException e) {
						try {
							Double iscrDoc = (Double) obj[9];
							creditNoteNum = String.valueOf(iscrDoc.longValue());
						} catch (ClassCastException ex) {
							Integer isdDoc = (Integer) obj[9];
							creditNoteNum = String.valueOf(isdDoc.intValue());
						}
					}
					if(iDoc.equalsIgnoreCase("ISDCN"))
					{
					String cnNum = creditNoteNum;
					if (cnNum != null && cnNum.length() > 16) {
						cnNum = cnNum.substring(0, 16);
					}
					entity.setDocumentNumber(cnNum);
					}
					//--------------------------------------------------

					String docDateStr = null;
					try {
						docDateStr = StringUtils.isEmpty((String) obj[8]) ? null
								: (String) obj[8];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[8];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						docDateStr = dateFormat.format(date1);
					}
					LocalDate InvoiceDate = null;
					if (docDateStr != null) {
						InvoiceDate = LocalDate.parse(docDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setDocumentDate(InvoiceDate);
					}
					
					//CR date---------
					String CrDateStr = null;
					try {
						if(iDoc.equalsIgnoreCase("ISDCN"))
						{
						CrDateStr = StringUtils.isEmpty((String) obj[10]) ? null
								: (String) obj[10];
						}
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[10];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MM-yyyy");
						CrDateStr = dateFormat.format(date1);
					}
					if(iDoc.equalsIgnoreCase("ISDCN"))
					{
					LocalDate CrNoteDate = null;
					if (CrDateStr != null) {
						CrNoteDate = LocalDate.parse(CrDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT2);
						entity.setDocumentDate(CrNoteDate);
					}
					}
					//---------

					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setIgstamt(getAppropriateValueFromObject(obj[13]));
					entity.setCgstamt(getAppropriateValueFromObject(obj[14]));
					entity.setSgstamt(getAppropriateValueFromObject(obj[15]));
					entity.setCesamt(getAppropriateValueFromObject(obj[16]));

					String cfs6b = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if (cfs6b != null && cfs6b.length() > 1) {
						cfs6b = cfs6b.substring(0, 1);
					}
					entity.setCounFillStatus(cfs6b);
					/** need to add amendment entity.set(amd); **/

					entity.setDelete(false);
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingIsdaInvoicesItemEntity itemEntity = new GetGstr2aStagingIsdaInvoicesItemEntity();
					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[13]));
					itemEntity
							.setCgstAmt(getAppropriateValueFromObject(obj[14]));
					itemEntity
							.setSgstAmt(getAppropriateValueFromObject(obj[15]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[16]));
					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs6b != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs6b);
						entity.setCheckSum(chkSum);
					}
					isdaInvoicesHeaderEntities.add(entity);
				//}
			}
		}
		return isdaInvoicesHeaderEntities;
	}

	public List<GetGstr2aStagingImpgHeaderEntity> convertImpgWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) throws Exception {

		List<GetGstr2aStagingImpgHeaderEntity> impgInvoicesHeaderEntities = Lists
				.newArrayList();
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

				if (DocNumber != null && !DocNumber.contains("Total")) {

					GetGstr2aStagingImpgHeaderEntity entity = new GetGstr2aStagingImpgHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setBatchId(batchId);
					String refDate = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (refDate != null) {
						entity.setBoeRefDate(String.format(refDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}

					String portCode = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (portCode != null && portCode.length() > 10) {
						portCode = portCode.substring(0, 10);
					}
					entity.setPortCode(portCode);

					Long Num = Long.valueOf(DocNumber);
					entity.setBoeNum(Num);

					String boeDate = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
									boeDate = boeDate.substring(0, 10);
					if (boeDate != null) {
						boolean isDate = false;
						String datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}";
						isDate = boeDate.matches(datePattern);
						if(isDate){
							entity.setBoeCreatedDate(String.format(boeDate,
									DateUtil.SUPPORTED_DATE_FORMAT2).substring(0, 10));
						}else {
							throw new Exception("Date Format is not correct for boeDate in Impg");
						}
					}

					entity.setGstin(cgstin);
					entity.setRetPeriod(taxPeriod);
					entity.setDerRetPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					String amdHistKey = amdUtil.generateImpgKey(cgstin,
							portCode, DateUtil.parseObjToDate(boeDate), Num);

					entity.setInvKey(amdHistKey);

					entity.setTaxValue(getAppropriateValueFromObject(obj[4]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[5]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[6]));

					String isAmend = (obj[7] != null
							&& !obj[7].toString().trim().isEmpty())
									? String.valueOf(obj[7]).trim() : null;
					if (isAmend != null && isAmend.length() > 1) {
						isAmend = isAmend.substring(0, 1);
					}
					entity.setIsAmdBoe(isAmend);

					entity.setDelete(false);
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingImpgItemEntity itemEntity = new GetGstr2aStagingImpgItemEntity();
					itemEntity.setTaxableValue(
							getAppropriateValueFromObject(obj[4]));
					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[5]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[6]));
					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					impgInvoicesHeaderEntities.add(entity);
				}
			}
		}
		return impgInvoicesHeaderEntities;
	}

	public List<GetGstr2aStagingImpgSezHeaderEntity> convertImpgSezWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) throws Exception {

		List<GetGstr2aStagingImpgSezHeaderEntity> impgSezInvoicesHeaderEntities = Lists
				.newArrayList();
		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				String DocNumber = null;
				try {
					DocNumber = (String) obj[4];
				} catch (ClassCastException e) {
					try {
						Double impgSezDoc = (Double) obj[4];
						DocNumber = String.valueOf(impgSezDoc.longValue());
					} catch (ClassCastException ex) {
						Integer impgSezDoc = (Integer) obj[4];
						DocNumber = String.valueOf(impgSezDoc.intValue());
					}
				}

				if (DocNumber != null && !DocNumber.contains("Total")) {

					GetGstr2aStagingImpgSezHeaderEntity entity = new GetGstr2aStagingImpgSezHeaderEntity();

					//entity.setDeltaInStatus("N");
					entity.setBatchId(batchId);
					String gstin = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (gstin != null && gstin.length() > 15) {
						gstin = gstin.substring(0, 15);
					}
					entity.setSgstin(gstin);

					String tradeName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (tradeName != null && tradeName.length() > 100) {
						tradeName = tradeName.substring(0, 100);
					}
					entity.setTradeName(tradeName);

					String refDate = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]).trim() : null;
					if (refDate != null) {
						entity.setBoeRefDate(String.format(refDate,
								DateUtil.SUPPORTED_DATE_FORMAT2));
					}

					String portCode = (obj[3] != null
							&& !obj[3].toString().trim().isEmpty())
									? String.valueOf(obj[3]).trim() : null;
					if (portCode != null && portCode.length() > 10) {
						portCode = portCode.substring(0, 10);
					}
					entity.setPortCode(portCode);

					Long Num = Long.valueOf(DocNumber);
					entity.setBoeNum(Num);

					String boeDate = (obj[5] != null
							&& !obj[5].toString().trim().isEmpty())
									? String.valueOf(obj[5]).trim() : null;
									boeDate = boeDate.substring(0, 10);
					if (boeDate != null) {
						boolean isDate = false;
						String datePattern = "\\d{1,2}-\\d{1,2}-\\d{4}";
						isDate = boeDate.matches(datePattern);
						if(isDate){
							entity.setBoeCreatedDate(String.format(boeDate,
									DateUtil.SUPPORTED_DATE_FORMAT2).substring(0, 10));
						}else {
							throw new Exception("Date Format is not correct for boeDate in ImpgSez");
						}
					}

					entity.setGstin(cgstin);
					entity.setRetPeriod(taxPeriod);
					entity.setDerRetPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					String amdHistKey = amdUtil.generateImpgSezKey(cgstin,
							gstin, portCode, DateUtil.parseObjToDate(boeDate),
							Num);
					entity.setInvKey(amdHistKey);

					entity.setTaxValue(getAppropriateValueFromObject(obj[6]));
					entity.setIgstAmt(getAppropriateValueFromObject(obj[7]));
					entity.setCessAmt(getAppropriateValueFromObject(obj[8]));

					String isAmend = (obj[9] != null
							&& !obj[9].toString().trim().isEmpty())
									? String.valueOf(obj[9]).trim() : null;
					if (isAmend != null && isAmend.length() > 1) {
						isAmend = isAmend.substring(0, 1);
					}
					entity.setIsAmdBoe(isAmend);
					entity.setDelete(false);
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingImpgSezItemEntity itemEntity = new GetGstr2aStagingImpgSezItemEntity();
					itemEntity.setTaxableValue(
							getAppropriateValueFromObject(obj[6]));
					itemEntity
							.setIgstAmt(getAppropriateValueFromObject(obj[7]));
					itemEntity
							.setCessAmt(getAppropriateValueFromObject(obj[8]));

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					impgSezInvoicesHeaderEntities.add(entity);
				}
			}
		}
		return impgSezInvoicesHeaderEntities;
	}
	
	public List<GetGstr2aStagingEcomInvoicesHeaderEntity> convertEcomWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {
		List<GetGstr2aStagingEcomInvoicesHeaderEntity> ecomDataList = Lists
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
					GetGstr2aStagingEcomInvoicesHeaderEntity entity = new GetGstr2aStagingEcomInvoicesHeaderEntity();

				
					entity.setEcomBatchIdGstr2a(batchId);
					String sgstin = (obj[0] != null
							&& !obj[0].toString().trim().isEmpty())
									? String.valueOf(obj[0]).trim() : null;
					if (sgstin != null && sgstin.length() > 22) {
						sgstin = sgstin.substring(0, 22);
					}
					entity.setSgstin(sgstin);

					String stName = (obj[1] != null
							&& !obj[1].toString().trim().isEmpty())
									? String.valueOf(obj[1]).trim() : null;
					if (stName != null && stName.length() > 100) {
						stName = stName.substring(0, 100);
					}
					entity.setSupTradeName(stName);

					entity.setCgstin(cgstin);

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
				

					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
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
					entity.setRchrg(rvc);

					entity.setTaxable(getAppropriateValueFromObject(obj[9]));
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
					entity.setCfsGstr1(cfs);

					String filDateStr = null;
					try {
						filDateStr = StringUtils.isEmpty((String) obj[15])
								? null : (String) obj[15];
					} catch (ClassCastException e) {
						DateTime dateTime = (DateTime) obj[15];
						Date date1 = dateTime.toDate();
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd-MMM-yy");
						filDateStr = dateFormat.format(date1);
					}
					LocalDate filDate = null;
					if (filDateStr != null) {
						filDate = LocalDate.parse(filDateStr,
								DateUtil.SUPPORTED_DATE_FORMAT7);
						entity.setFileDate(filDate);
					}

					String filPeriod = (obj[16] != null
							&& !obj[16].toString().trim().isEmpty())
									? String.valueOf(obj[16]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);


					String canDate = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);
						
					entity.setDelete(false);
					entity.setDiffPercentage(BigDecimal.ZERO);
					entity.setAction("N");
					entity.setChkSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingEcomInvoicesItemEntity itemEntity = new GetGstr2aStagingEcomInvoicesItemEntity();

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

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);

						entity.setChkSum(chkSum);
					}
					ecomDataList.add(entity);
				}
			}
		}

		return ecomDataList;
	}

	public List<GetGstr2aStagingEcomaInvoicesHeaderEntity> convertEcomaWorkSheetDataToList(
			Object[][] objList, int columnCount, String cgstin,
			String taxPeriod, Long batchId) {

		List<GetGstr2aStagingEcomaInvoicesHeaderEntity> ecomaInvoicesHeaderEntities = Lists
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
					GetGstr2aStagingEcomaInvoicesHeaderEntity entity = new GetGstr2aStagingEcomaInvoicesHeaderEntity();

					
					entity.setEcomBatchIdGstr2a(batchId);
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
					entity.setOrigInvNum(origInvNum);

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
						entity.setOrigInvDate(orgInvoiceDate);
					}

					String stin = (obj[2] != null
							&& !obj[2].toString().trim().isEmpty())
									? String.valueOf(obj[2]).trim() : null;
					if (stin != null && stin.length() > 22) {
						stin = stin.substring(0, 22);
					}
					entity.setSgstin(stin);

					entity.setCgstin(cgstin);

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
					entity.setReturnPeriod(taxPeriod);
					entity.setDerReturnPeriod(
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
					entity.setRchrg(rch);

					entity.setTaxable(getAppropriateValueFromObject(obj[11]));
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
					entity.setCfsGstr1(cfs);

					String filDate = (obj[17] != null
							&& !obj[17].toString().trim().isEmpty())
									? String.valueOf(obj[17]).trim() : null;
					LocalDate localFilDate = DateUtil.parseObjToDate(filDate);
					entity.setFileDate(localFilDate);

					String filPeriod = (obj[18] != null
							&& !obj[18].toString().trim().isEmpty())
									? String.valueOf(obj[18]).trim() : null;
					if (filPeriod != null && filPeriod.length() > 6) {
						filPeriod = filPeriod.substring(0, 6);
					}
					if (filPeriod != null) {
						entity.setFilePeriod(String.format(filPeriod,
								DateUtil.SUPPORTED_DATE_FORMAT10));
					}

					String cfs3b = (obj[19] != null
							&& !obj[19].toString().trim().isEmpty())
									? String.valueOf(obj[19]).trim() : null;
					if (cfs3b != null && cfs3b.length() > 1) {
						cfs3b = cfs3b.substring(0, 1);
					}
					entity.setCfsGstr3B(cfs3b);

					String canDate = (obj[20] != null
							&& !obj[20].toString().trim().isEmpty())
									? String.valueOf(obj[20]).trim() : null;
					LocalDate localCanDate = DateUtil.parseObjToDate(canDate);
					entity.setCancelDate(localCanDate);


					entity.setDelete(false);
					entity.setDiffPercentage(BigDecimal.ZERO);
					entity.setAction("N");
					entity.setChkSum("");
					LocalDateTime convertNow = EYDateUtil
							.toUTCDateTimeFromLocal(LocalDateTime.now());
					entity.setCreatedOn(convertNow);

					GetGstr2aStagingEcomaInvoicesItemEntity itemEntity = new GetGstr2aStagingEcomaInvoicesItemEntity();

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

					itemEntity.setDerReturnPeriod(
							GenUtil.convertTaxPeriodToInt(taxPeriod));

					entity.setLineItems(Arrays.asList(itemEntity));
					itemEntity.setHeader(entity);

					if (cgstin != null && docType != null && cfs != null) {
						String chkSum = "DummyChkSum_File_".concat(cgstin)
								.concat(docType).concat(cfs);
						entity.setChkSum(chkSum);
					}
					ecomaInvoicesHeaderEntities.add(entity);
				}
			}
		}

		return ecomaInvoicesHeaderEntities;
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
