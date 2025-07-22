/**
 * 
 */
package com.ey.advisory.app.services.itc04;

import static com.ey.advisory.common.GenUtil.trimAndConvToUpperCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.caches.UomCache;
import com.ey.advisory.app.data.entities.client.Itc04HeaderEntity;
import com.ey.advisory.app.data.entities.client.Itc04ItemEntity;
import com.ey.advisory.app.data.repositories.client.Itc04DocRepository;
import com.ey.advisory.app.services.docs.DocKeyGenerator;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GSTConstants;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxmi.Salukuti
 *
 */
@Component("Itc04DocSave")
@Slf4j
public class Itc04DocSave {

	@Autowired
	@Qualifier("Itc04DocRepository")
	private Itc04DocRepository itc04DocRepository;

	@Autowired
	@Qualifier("Itc04DuplicateDocCheckServiceImpl")
	private Itc04DuplicateDocCheckService duplicateDocCheckService;

	@Autowired
	@Qualifier("Itc04FieldsTruncation")
	private Itc04FieldsTruncation itc04FieldsTruncation;

	@Autowired
	@Qualifier("DefaultUomCache")
	private UomCache uomCache;

	@Autowired
	private Itc04TaxPeriodUtil taxPeriodUtil;

	public void convertCalcAndSetValues(List<Itc04HeaderEntity> documents) {

		documents.forEach(document -> {
			setqRetAndDocKeyRetPeriod(document);
			setDataOriginTypeCode(document);
			trimForTableNumber(document);
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					setHeaderValuesToItem(document, item);
					setUqcLossesUqc(document, item);
					calculateAndSetTaxableValue(document, item);
					calculateAndSetItemTaxRate(document, item);
					calculateAndSetTotalTaxAmt(document, item);
				});
			}
		});
	}

	private void trimForTableNumber(Itc04HeaderEntity document) {

		if (!Strings.isNullOrEmpty(document.getTableNumber())) {
			document.setTableNumber(document.getTableNumber().trim());
		}
	}

	public List<Itc04HeaderEntity> saveDocs(List<Itc04HeaderEntity> docs,
			DocKeyGenerator<Itc04HeaderEntity, String> docKeyGen) {

		// Get all the non-null ids from the list. This will be used to
		// mark the existing documents in the DB as deleted.
		List<Long> docIds = docs.stream().filter(doc -> doc.getId() != null)
				.map(doc -> doc.getId()).collect(Collectors.toList());
		Itc04HeaderEntity firstOutwardDoc = docs.get(0);
		// Execute the repository query to udpate the isDelete to true for the
		// above ids. Also mark the updated date to the current date.
		// call the is delete to true repository method.

		if (!docIds.isEmpty()) {
			// LocalDateTime updatedDate = LocalDateTime.now();
			LocalDateTime updatedDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			itc04DocRepository.updateDocDeletion(docIds, updatedDate,
					firstOutwardDoc.getCreatedBy());
			docs.forEach(doc -> {

				doc.setGstnError(false);
				doc.setSavedToGstn(false);
				doc.setSentToGstn(false);
			});
		}

		duplicateDocCheckService.softDeleteDupDocsDetails(docs);

		docs.forEach(document -> {
			String docKey = docKeyGen.generateKey(document);
			LocalDateTime createdDate = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			// document.setCreatedDate(LocalDateTime.now());
			document.setCreatedDate(createdDate);
			document.setReceivedDate(createdDate.toLocalDate());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Doc Key " + docKey);
			}
			document.setDocKey(docKey);

			itc04FieldsTruncation.truncateHeaderFields(document);
			if (null != document.getLineItems()) {
				document.getLineItems().forEach(item -> {
					itc04FieldsTruncation.truncateItemFields(item);
					item.setDocument(document);
				});
			}
			// Set Document Saving Time for Performance Testing
			// LocalDateTime beforeSavingTime = LocalDateTime.now();
			LocalDateTime beforeSavingTime = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());
			document.setBeforeSavingOn(beforeSavingTime);
		});

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("DefaultDocSaveService saveDocuments End");
		}
		// save all the documents.

		List<Itc04HeaderEntity> itc04DocList = itc04DocRepository.saveAll(docs);

		return itc04DocList;
	}

	private void setqRetAndDocKeyRetPeriod(Itc04HeaderEntity document) {

		String dockeyRetPeriod = null;
		String SubFin = null;
		String finalQRet = null;
		String firstSubString = null;
		String secondSubString = null;
		String finalFinYear = null;
		// convert retperiod
		String finYear = document.getFinYear();
		if (finYear.length() == 4) {
			int plusFinYear = Integer.parseInt(finYear);
			int secondFyYear = plusFinYear + 1;
			finYear = finYear + "-" + secondFyYear;
			document.setFinYear(finYear);
		}
		// QRetPeriod setting Starting
		if (finYear != null && !finYear.isEmpty()) {
			SubFin = finYear.substring(0, 4);
		}

		document.setRetPeriod(taxPeriodUtil.getValidReturnPeriod(
				document.getRetPeriod(), SubFin));
		String retPeriod = document.getRetPeriod();
		String retNum = taxPeriodUtil
				.getQReturnPeriod(trimAndConvToUpperCase(retPeriod), SubFin);

		if (SubFin != null && retNum != null) {
			finalQRet = retNum.concat(SubFin);
		}
		document.setQRetPeriod(finalQRet);
		// QRetPeriod setting ending

		// retPeriodDocKey setting Starting
		if (finYear != null && !finYear.isEmpty()) {
			firstSubString = finYear.substring(0, 5);
		}
		if (finYear != null && !finYear.isEmpty()) {
			secondSubString = finYear.substring(7, 9);
		}
		if (firstSubString != null && secondSubString != null) {
			finalFinYear = firstSubString.concat(secondSubString);
		}
		if ((retPeriod != null && !retPeriod.isEmpty())
				&& finalFinYear != null) {
			dockeyRetPeriod = retPeriod.concat(finalFinYear);
		}
		document.setRetPeriodDocKey(dockeyRetPeriod);
		// retPeriodDocKey setting ending

	}

	private void calculateAndSetTaxableValue(Itc04HeaderEntity document,
			Itc04ItemEntity item) {
		BigDecimal taxableVal = document.getTaxableValue();
		if (taxableVal == null) {
			taxableVal = BigDecimal.ZERO;
		}
		// Set Taxable Value to Document Header
		if (item.getTaxableValue() != null) {
			taxableVal = taxableVal.add(item.getTaxableValue());
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Taxable Value  " + taxableVal + " for Doc Key "
					+ document.getDocKey());
		}
		document.setTaxableValue(taxableVal);
	}

	private void calculateAndSetItemTaxRate(Itc04HeaderEntity document,
			Itc04ItemEntity item) {
		// Calculate Tax Rate
		BigDecimal igstRate = item.getIgstRate();
		BigDecimal cgstRate = item.getCgstRate();
		BigDecimal sgstRate = item.getSgstRate();
		if (igstRate == null) {
			igstRate = BigDecimal.ZERO;
		}
		if (cgstRate == null) {
			cgstRate = BigDecimal.ZERO;
		}
		if (sgstRate == null) {
			sgstRate = BigDecimal.ZERO;
		}
		BigDecimal totalTaxRate = igstRate.add(cgstRate).add(sgstRate);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Tax Rate " + totalTaxRate + " for Doc Key "
					+ document.getDocKey() + " for Item " + item);
		}
		item.setTaxRate(totalTaxRate);
	}

	private void calculateAndSetTotalTaxAmt(Itc04HeaderEntity document,
			Itc04ItemEntity item) {
		BigDecimal docIgstAmt = document.getIgstAmount();
		BigDecimal docCgstAmt = document.getCgstAmount();
		BigDecimal docSgstAmt = document.getSgstAmount();
		BigDecimal docCessAmtSpec = document.getCessSpecificAmount();
		BigDecimal docCessAmtAdv = document.getCessAdvaloremAmount();
		if (docIgstAmt == null) {
			docIgstAmt = BigDecimal.ZERO;
		}
		if (docCgstAmt == null) {
			docCgstAmt = BigDecimal.ZERO;
		}
		if (docSgstAmt == null) {
			docSgstAmt = BigDecimal.ZERO;
		}
		if (docCessAmtSpec == null) {
			docCessAmtSpec = BigDecimal.ZERO;
		}
		if (docCessAmtAdv == null) {
			docCessAmtAdv = BigDecimal.ZERO;
		}
		// Set Igst Amount to Document Header
		if (item.getIgstAmount() != null) {
			docIgstAmt = docIgstAmt.add(item.getIgstAmount());
			document.setIgstAmount(docIgstAmt);
		}
		// Set Cgst Amount to Document Header
		if (item.getCgstAmount() != null) {
			docCgstAmt = docCgstAmt.add(item.getCgstAmount());
			document.setCgstAmount(docCgstAmt);
		}
		// Set Sgst Amount to Document Header
		if (item.getSgstAmount() != null) {
			docSgstAmt = docSgstAmt.add(item.getSgstAmount());
			document.setSgstAmount(docSgstAmt);
		}
		// Set Cess Amt Specific to Document Header
		if (item.getCessSpecificAmount() != null) {
			docCessAmtSpec = docCessAmtSpec.add(item.getCessSpecificAmount());
			document.setCessSpecificAmount(docCessAmtSpec);
		}
		// Set Cess Amt Advalorem to Document Header
		if (item.getCessAdvaloremAmount() != null) {
			docCessAmtAdv = docCessAmtAdv.add(item.getCessAdvaloremAmount());
			document.setCessAdvaloremAmount(docCessAmtAdv);
		}
	}

	private void setUqcLossesUqc(Itc04HeaderEntity document,
			Itc04ItemEntity item) {

		item.setUqc(trimAndConvToUpperCase(item.getUqc()));

		if (item.getUqc() == null || item.getUqc().isEmpty()) {
			item.setUqc(GSTConstants.OTH);
		}
		if (item.getLossesUqc() == null || item.getLossesUqc().isEmpty()) {
			item.setLossesUqc(GSTConstants.OTH);
		}

		String uqc = item.getUqc();
		String lossesUqc = item.getLossesUqc();
		int n = uomCache.finduom(uqc);
		int n1 = uomCache.finduom(lossesUqc);
		if (n <= 0) {
			item.setUqc(GSTConstants.OTH);
		}
		if (n1 <= 0) {
			item.setLossesUqc(GSTConstants.OTH);
		}
	}

	private void setHeaderValuesToItem(Itc04HeaderEntity document,
			Itc04ItemEntity item) {

		item.setRetPeriod(document.getRetPeriod());
		item.setQRetPeriod(document.getQRetPeriod());
		item.setRetPeriodDocKey(document.getRetPeriodDocKey());
	}

	private void setDataOriginTypeCode(Itc04HeaderEntity document) {
		/*
		 * Fix for external API call, only when dataorigintypecode is coming as
		 * NULL from external API, setting to A
		 */
		if (Strings.isNullOrEmpty(document.getDataOriginTypeCode())) {
			document.setDataOriginTypeCode(
					GSTConstants.DataOriginTypeCodes.ERP_API
							.getDataOriginTypeCode());
		}
	}
}
