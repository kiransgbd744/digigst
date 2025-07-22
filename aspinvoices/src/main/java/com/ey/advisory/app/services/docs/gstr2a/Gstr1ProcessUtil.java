package com.ey.advisory.app.services.docs.gstr2a;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesB2bItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnrItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesCdnurItemEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpHeaderEntity;
import com.ey.advisory.app.data.entities.client.GetGstr1EInvoicesExpItemEntity;
import com.ey.advisory.app.services.common.Anx1DocKeyGenerator;
import com.ey.advisory.common.DateUtil;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.core.api.APIConstants;
import com.google.common.collect.Lists;

/**
 * 
 * @author Anand3.M
 *
 */
@Component
public class Gstr1ProcessUtil {

	@Autowired
	private Anx1DocKeyGenerator docKeyGenerator;

	public List<GetGstr1EInvoicesB2bHeaderEntity> convertB2bWorkSheetDataToList(
			Object[][] objList, int columnCount, Long batchId) {
		List<GetGstr1EInvoicesB2bHeaderEntity> b2bDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				GetGstr1EInvoicesB2bHeaderEntity entity = new GetGstr1EInvoicesB2bHeaderEntity();

				String retPeriod = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				if (retPeriod != null && retPeriod.length() > 10) {
					retPeriod = retPeriod.substring(0, 10);
				}
				entity.setReturnPeriod(retPeriod);

				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				entity.setBatchId(batchId);

				String sgstin = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 15) {
					sgstin = sgstin.substring(0, 15);
				}
				entity.setGstin(sgstin);

				String invDate = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (invDate != null) {
					entity.setInvDate(String.format(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				entity.setInvValue(getAppropriateValueFromObject(obj[3]));

				String rev = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (rev != null && rev.length() > 1) {
					rev = rev.substring(0, 1);
				}
				entity.setRchrg(rev);

				String etin = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				if (etin != null && etin.length() > 15) {
					etin = etin.substring(0, 15);
				}
				entity.setEtin(etin);

				String pos = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				if (pos != null && pos.length() > 2) {
					pos = pos.substring(0, 2);
				}
				entity.setPos(pos);

				String chk = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				if (chk != null && chk.length() > 64) {
					chk = chk.substring(0, 64);
				}
				entity.setInvChksum(chk);

				String invNum = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				if (invNum != null && invNum.length() > 16) {
					invNum = invNum.substring(0, 16);
				}
				entity.setInvNum(invNum);

				String invType = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				if (invType != null && invType.length() > 5) {
					invType = invType.substring(0, 5);
				}
				entity.setInvType(invType);

				String trade = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				if (trade != null && trade.length() > 20) {
					trade = trade.substring(0, 20);
				}
				entity.setTradeName(trade);

				String einvStatus = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				if (einvStatus != null && einvStatus.length() > 10) {
					einvStatus = einvStatus.substring(0, 10);
				}
				entity.seteInvStatus(einvStatus);

				String auto = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;
				if (auto != null && auto.length() > 30) {
					auto = auto.substring(0, 30);
				}
				entity.setAutoDraftStatus(auto);

				String autoDate = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;
				if (autoDate != null) {
					entity.setAutoDraftDate(String.format(autoDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String irn = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;
				if (irn != null && irn.length() > 64) {
					irn = irn.substring(0, 64);
				}
				entity.setIrnNum(irn);

				String irnDate = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;
				if (irnDate != null) {
					entity.setIrnGenDate(String.format(irnDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String ctin = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;
				if (ctin != null && ctin.length() > 15) {
					ctin = ctin.substring(0, 15);
				}
				entity.setCtin(ctin);

				entity.setTaxValue(getAppropriateValueFromObject(obj[17]));
				entity.setIgstAmt(getAppropriateValueFromObject(obj[18]));
				entity.setCgstAmt(getAppropriateValueFromObject(obj[19]));
				entity.setSgstAmt(getAppropriateValueFromObject(obj[20]));
				entity.setCessAmt(getAppropriateValueFromObject(obj[21]));

				if (invDate != null && sgstin != null && invNum != null) {
					String finYear = GenUtil.getFinYear(LocalDate.parse(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));

					entity.setDocKey(docKeyGenerator.generateKey(sgstin, invNum,
							finYear, GSTConstants.INV.toUpperCase()));
				}

				GetGstr1EInvoicesB2bItemEntity itemEntity = new GetGstr1EInvoicesB2bItemEntity();
				itemEntity.setTaxValue(getAppropriateValueFromObject(obj[17]));
				itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[18]));
				itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[19]));
				itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[20]));
				itemEntity.setCessAmt(getAppropriateValueFromObject(obj[21]));
				itemEntity.setTaxRate(getAppropriateValueFromObject(obj[22]));

				itemEntity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				entity.getLineItems().add(itemEntity);
				itemEntity.setDocument(entity);

				b2bDataList.add(entity);
			}
		}

		return b2bDataList;

	}

	public List<GetGstr1EInvoicesCdnrHeaderEntity> convertCdnrWorkSheetDataToList(
			Object[][] objList, int columnCount, Long batchId) {
		List<GetGstr1EInvoicesCdnrHeaderEntity> cdnrDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				GetGstr1EInvoicesCdnrHeaderEntity entity = new GetGstr1EInvoicesCdnrHeaderEntity();

				String retPeriod = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				if (retPeriod != null && retPeriod.length() > 6) {
					retPeriod = retPeriod.substring(0, 6);
				}
				entity.setReturnPeriod(retPeriod);

				entity.setBatchId(batchId);

				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				String sgstin = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 15) {
					sgstin = sgstin.substring(0, 15);
				}
				entity.setGstin(sgstin);

				String invDate = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (invDate != null) {
					entity.setNoteDate(String.format(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String noteType = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				if (noteType != null) {
					if (noteType.equalsIgnoreCase(APIConstants.C)) {
						entity.setNoteType("C");
					} else {
						entity.setNoteType("D");
					}
				}

				String rev = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (rev != null && rev.length() > 2) {
					rev = rev.substring(0, 2);
				}
				entity.setRevCharge(rev);

				entity.setInvValue(getAppropriateValueFromObject(obj[5]));

				String pos = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				if (pos != null && pos.length() > 2) {
					pos = pos.substring(0, 2);
				}
				entity.setPos(pos);

				String chk = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				if (chk != null && chk.length() > 64) {
					chk = chk.substring(0, 64);
				}
				entity.setChksum(chk);

				String noteNum = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				if (noteNum != null && noteNum.length() > 16) {
					noteNum = noteNum.substring(0, 16);
				}
				entity.setNoteNum(noteNum);

				String invType = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				if (invType != null && invType.length() > 10) {
					invType = invType.substring(0, 10);
				}
				entity.setInvType(invType);

				String trade = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				if (trade != null && trade.length() > 100) {
					trade = trade.substring(0, 100);
				}
				entity.setTradeName(trade);

				String einvStatus = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				if (einvStatus != null && einvStatus.length() > 10) {
					einvStatus = einvStatus.substring(0, 10);
				}
				entity.seteInvStatus(einvStatus);

				String auto = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;
				if (auto != null && auto.length() > 30) {
					auto = auto.substring(0, 30);
				}
				entity.setAutoDraftStatus(auto);

				String autoDate = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;
				if (autoDate != null) {
					entity.setAutoDraftDate(String.format(autoDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String irn = (obj[14] != null
						&& !obj[14].toString().trim().isEmpty())
								? String.valueOf(obj[14]).trim() : null;
				if (irn != null && irn.length() > 64) {
					irn = irn.substring(0, 64);
				}
				entity.setIrnNum(irn);

				String irnDate = (obj[15] != null
						&& !obj[15].toString().trim().isEmpty())
								? String.valueOf(obj[15]).trim() : null;
				if (irnDate != null) {
					entity.setIrnGenDate(String.format(irnDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String ctin = (obj[16] != null
						&& !obj[16].toString().trim().isEmpty())
								? String.valueOf(obj[16]).trim() : null;
				if (ctin != null && ctin.length() > 15) {
					ctin = ctin.substring(0, 15);
				}
				entity.setCtin(ctin);

				entity.setTaxValue(getAppropriateValueFromObject(obj[17]));
				entity.setIgstAmt(getAppropriateValueFromObject(obj[18]));
				entity.setCgstAmt(getAppropriateValueFromObject(obj[19]));
				entity.setSgstAmt(getAppropriateValueFromObject(obj[20]));
				entity.setCessAmt(getAppropriateValueFromObject(obj[21]));

				if (invDate != null && sgstin != null && noteNum != null
						&& noteType != null) {
					String finYear = GenUtil.getFinYear(LocalDate.parse(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
					String noteTyp =  "C".equalsIgnoreCase(noteType) ? "CR" : "DR";

					entity.setDocKey(docKeyGenerator.generateKey(sgstin,
							noteNum, finYear, noteTyp));
				}

				GetGstr1EInvoicesCdnrItemEntity itemEntity = new GetGstr1EInvoicesCdnrItemEntity();
				itemEntity.setTaxValue(getAppropriateValueFromObject(obj[17]));
				itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[18]));
				itemEntity.setCgstAmt(getAppropriateValueFromObject(obj[19]));
				itemEntity.setSgstAmt(getAppropriateValueFromObject(obj[20]));
				itemEntity.setCessAmt(getAppropriateValueFromObject(obj[21]));
				itemEntity.setTaxRate(getAppropriateValueFromObject(obj[22]));

				itemEntity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				entity.getLineItems().add(itemEntity);
				itemEntity.setDocument(entity);

				cdnrDataList.add(entity);
			}
		}

		return cdnrDataList;

	}

	public List<GetGstr1EInvoicesCdnurHeaderEntity> convertCdnurWorkSheetDataToList(
			Object[][] objList, int columnCount, Long batchId) {
		List<GetGstr1EInvoicesCdnurHeaderEntity> cdnrDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				GetGstr1EInvoicesCdnurHeaderEntity entity = new GetGstr1EInvoicesCdnurHeaderEntity();

				String retPeriod = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				if (retPeriod != null && retPeriod.length() > 6) {
					retPeriod = retPeriod.substring(0, 6);
				}
				entity.setReturnPeriod(retPeriod);
				entity.setBatchId(batchId);

				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				String sgstin = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 15) {
					sgstin = sgstin.substring(0, 15);
				}
				entity.setGstin(sgstin);

				String invDate = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (invDate != null) {
					entity.setNoteDate(String.format(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String noteType = (obj[3] != null
						&& !obj[3].toString().trim().isEmpty())
								? String.valueOf(obj[3]).trim() : null;
				if (noteType != null) {
					if (noteType.equalsIgnoreCase(APIConstants.C)) {
						entity.setNoteType("C");
					} else {
						entity.setNoteType("D");
					}
				}

				entity.setInvValue(getAppropriateValueFromObject(obj[4]));

				String pos = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				if (pos != null && pos.length() > 2) {
					pos = pos.substring(0, 2);
				}
				entity.setPos(pos);

				String chk = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				if (chk != null && chk.length() > 64) {
					chk = chk.substring(0, 64);
				}
				entity.setInvChkSum(chk);

				String noteNum = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				if (noteNum != null && noteNum.length() > 16) {
					noteNum = noteNum.substring(0, 16);
				}
				entity.setNoteNum(noteNum);

				String invType = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				if (invType != null && invType.length() > 6) {
					invType = invType.substring(0, 6);
				}
				entity.setType(invType);

				String einvStatus = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				if (einvStatus != null && einvStatus.length() > 10) {
					einvStatus = einvStatus.substring(0, 10);
				}
				entity.seteInvStatus(einvStatus);

				String auto = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				if (auto != null && auto.length() > 30) {
					auto = auto.substring(0, 30);
				}
				entity.setAutoDraftStatus(auto);

				String autoDate = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				if (autoDate != null) {
					entity.setAutoDraftDate(String.format(autoDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String irn = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;
				if (irn != null && irn.length() > 64) {
					irn = irn.substring(0, 64);
				}
				entity.setIrnNum(irn);

				String irnDate = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;
				if (irnDate != null) {
					entity.setIrnGenDate(String.format(irnDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				entity.setTaxValue(getAppropriateValueFromObject(obj[14]));
				entity.setIgstAmt(getAppropriateValueFromObject(obj[15]));
				entity.setCessAmt(getAppropriateValueFromObject(obj[16]));

				if (invDate != null && sgstin != null && noteNum != null
						&& noteType != null) {
					String finYear = GenUtil.getFinYear(LocalDate.parse(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
					
					String noteTyp =  "C".equalsIgnoreCase(noteType) ? "CR" : "DR";
							

					entity.setDocKey(docKeyGenerator.generateKey(sgstin,
							noteNum, finYear, noteTyp));
				}

				GetGstr1EInvoicesCdnurItemEntity itemEntity = new GetGstr1EInvoicesCdnurItemEntity();
				itemEntity.setTaxValue(getAppropriateValueFromObject(obj[14]));
				itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[15]));
				itemEntity.setCessAmt(getAppropriateValueFromObject(obj[16]));
				itemEntity.setTaxRate(getAppropriateValueFromObject(obj[17]));

				itemEntity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				entity.getLineItems().add(itemEntity);
				itemEntity.setDocument(entity);

				cdnrDataList.add(entity);
			}
		}

		return cdnrDataList;

	}

	public List<GetGstr1EInvoicesExpHeaderEntity> convertExpWorkSheetDataToList(
			Object[][] objList, int columnCount, Long batchId) {
		List<GetGstr1EInvoicesExpHeaderEntity> expDataList = Lists
				.newArrayList();

		for (int i = 0; i < objList.length; i++) {
			Object obj[] = objList[i];
			boolean isDataPresent = checkObjectAsData(obj, columnCount);
			if (!isDataPresent) {

				GetGstr1EInvoicesExpHeaderEntity entity = new GetGstr1EInvoicesExpHeaderEntity();

				String retPeriod = (obj[0] != null
						&& !obj[0].toString().trim().isEmpty())
								? String.valueOf(obj[0]).trim() : null;
				if (retPeriod != null && retPeriod.length() > 6) {
					retPeriod = retPeriod.substring(0, 6);
				}
				entity.setReturnPeriod(retPeriod);
				entity.setBatchId(batchId);

				entity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				String sgstin = (obj[1] != null
						&& !obj[1].toString().trim().isEmpty())
								? String.valueOf(obj[1]).trim() : null;
				if (sgstin != null && sgstin.length() > 15) {
					sgstin = sgstin.substring(0, 15);
				}
				entity.setGstin(sgstin);

				String invDate = (obj[2] != null
						&& !obj[2].toString().trim().isEmpty())
								? String.valueOf(obj[2]).trim() : null;
				if (invDate != null) {
					entity.setInvDate(String.format(invDate.substring(0, 10),
							DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				entity.setInvValue(getAppropriateValueFromObject(obj[3]));

				String expType = (obj[4] != null
						&& !obj[4].toString().trim().isEmpty())
								? String.valueOf(obj[4]).trim() : null;
				if (expType != null && expType.length() > 5) {
					expType = expType.substring(0, 5);
				}
				entity.setExportType(expType);

				String portCode = (obj[5] != null
						&& !obj[5].toString().trim().isEmpty())
								? String.valueOf(obj[5]).trim() : null;
				if (portCode != null && portCode.length() > 6) {
					portCode = portCode.substring(0, 6);
				}
				entity.setShipBillPortCode(portCode);

				String shipNum = (obj[6] != null
						&& !obj[6].toString().trim().isEmpty())
								? String.valueOf(obj[6]).trim() : null;
				if (shipNum != null && shipNum.length() > 7) {
					shipNum = shipNum.substring(0, 7);
				}
				entity.setShipBillNum(shipNum);

				String shipNumDate = (obj[7] != null
						&& !obj[7].toString().trim().isEmpty())
								? String.valueOf(obj[7]).trim() : null;
				if (shipNumDate != null) {
					entity.setShipBillDate(
							String.format(shipNumDate.substring(0, 10),
									DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String invNum = (obj[8] != null
						&& !obj[8].toString().trim().isEmpty())
								? String.valueOf(obj[8]).trim() : null;
				if (invNum != null && invNum.length() > 16) {
					invNum = invNum.substring(0, 16);
				}
				entity.setInvNum(invNum);

				String einvStatus = (obj[9] != null
						&& !obj[9].toString().trim().isEmpty())
								? String.valueOf(obj[9]).trim() : null;
				if (einvStatus != null && einvStatus.length() > 10) {
					einvStatus = einvStatus.substring(0, 10);
				}
				entity.seteInvStatus(einvStatus);

				String auto = (obj[10] != null
						&& !obj[10].toString().trim().isEmpty())
								? String.valueOf(obj[10]).trim() : null;
				if (auto != null && auto.length() > 20) {
					auto = auto.substring(0, 20);
				}
				entity.setAutoDraftStatus(auto);

				String autoDate = (obj[11] != null
						&& !obj[11].toString().trim().isEmpty())
								? String.valueOf(obj[11]).trim() : null;
				if (autoDate != null) {
					entity.setAutoDraftDate(
							String.format(autoDate.substring(0, 10),
									DateUtil.SUPPORTED_DATE_FORMAT2));
				}

				String irn = (obj[12] != null
						&& !obj[12].toString().trim().isEmpty())
								? String.valueOf(obj[12]).trim() : null;
				if (irn != null && irn.length() > 64) {
					irn = irn.substring(0, 64);
				}
				entity.setIrnNum(irn);

				String irnDate = (obj[13] != null
						&& !obj[13].toString().trim().isEmpty())
								? String.valueOf(obj[13]).trim() : null;
				if (irnDate != null) {
					entity.setIrnGenDate(
							String.format(irnDate.substring(0, 10),
									DateUtil.SUPPORTED_DATE_FORMAT2));
				}
				entity.setTaxValue(getAppropriateValueFromObject(obj[14]));
				entity.setIgstAmt(getAppropriateValueFromObject(obj[15]));
				entity.setCessAmt(getAppropriateValueFromObject(obj[16]));

				if (invDate != null && sgstin != null && invNum != null) {
					String finYear = GenUtil.getFinYear(
							LocalDate.parse(invDate.substring(0, 10),
									DateUtil.SUPPORTED_DATE_FORMAT2));

					entity.setDocKey(docKeyGenerator.generateKey(sgstin, invNum,
							finYear, GSTConstants.INV.toUpperCase()));
				}

				GetGstr1EInvoicesExpItemEntity itemEntity = new GetGstr1EInvoicesExpItemEntity();
				itemEntity.setTaxVal(getAppropriateValueFromObject(obj[14]));
				itemEntity.setIgstAmt(getAppropriateValueFromObject(obj[15]));
				itemEntity.setCessAmt(getAppropriateValueFromObject(obj[16]));
				itemEntity.setTaxRate(getAppropriateValueFromObject(obj[17]));

				itemEntity.setDerivedTaxperiod(
						GenUtil.convertTaxPeriodToInt(retPeriod));

				entity.getLineItems().add(itemEntity);
				itemEntity.setDocument(entity);

				expDataList.add(entity);
			}
		}

		return expDataList;

	}

	public static void main(String[] args) {
		String invDate = "2020-02-01T00:00:00";
		System.out.println(
				String.format(invDate, DateUtil.SUPPORTED_DATE_FORMAT2));
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

}
