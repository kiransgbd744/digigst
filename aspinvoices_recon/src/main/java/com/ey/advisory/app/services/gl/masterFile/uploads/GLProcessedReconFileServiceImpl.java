/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.services.gl.masterFile.uploads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import org.apache.chemistry.opencmis.client.api.Document;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ey.advisory.admin.data.entities.client.User;
import com.ey.advisory.app.data.entities.client.asprecon.GlReconReportConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GLReconReportConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlReconSrprStgRepository;
import com.ey.advisory.app.glrecon.GlReconSrprStgEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.SecurityContext;
import com.ey.advisory.common.TraverserFactory;
import com.ey.advisory.common.eyfileutils.tabular.DummyTabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataLayout;
import com.ey.advisory.common.eyfileutils.tabular.TabularDataSourceTraverser;
import com.ey.advisory.common.eyfileutils.tabular.impl.FileUploadDocRowHandler;
import com.google.common.base.Strings;
import com.google.common.collect.Streams;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("GLProcessedReconFileServiceImpl")
public class GLProcessedReconFileServiceImpl {
	
	@Autowired
	@Qualifier("GLReconReportConfigRepository")
	private GLReconReportConfigRepository glReconReportConfig;

	@Autowired
	@Qualifier("DefaultTraverserFactoryImpl")
	private TraverserFactory traverserFactory;

	@Autowired
	@Qualifier("GlReconSrprStgRepository")
	private GlReconSrprStgRepository glReconSrprStgRepository;
	
	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	

	private static final List<String> EXPECTED_HEADERNAMES_LIST = Arrays.asList(
			"G/L_GL", "Reference_GL", "Accounting_Document_Number_GL",
			"Document_Type_GL", "Document_Date_GL", "Amount_GL", "Text_GL",
			"Clearing_Document_Number_GL", "Plant_GL", "Posting_Date_GL",
			"Company_Code_GL", "Customer_Code_GL", "Vendor_Code_GL",
			"Entry_Date_GL", "BP_GL", "Offset_Account_GL",
			"Transaction_Type_GL", "Period_GL", "Year/month_GL", "Nature_GL",
			"GSTIN", "GL_Type", "Tax_Code_Description_MS", "Tax_Type_MS",
			"Eligibility_MS", "Tax_Rate_MS", "Document_Type",
			"Document_Type_MS", "Period_x", "Key", "Advance GL", "Revenue GL",
			"Tax GL", "CGST_GL", "GL Code Missing in Master", "IGST_GL",
			"Revenue", "SGST_GL", "Category_x",
			"Accounting_Document_Number_Reg", "GSTR 9C", "SourceIdentifier",
			"SourceFileName", "GLAccountCode", "Division", "SubDivision",
			"ProfitCentre1", "ProfitCentre2", "PlantCode", "ReturnPeriod",
			"SupplierGSTIN", "DocumentType", "SupplyType", "DocumentNumber",
			"DocumentDate", "OriginalDocumentNumber", "OriginalDocumentDate",
			"CRDRPreGST", "LineNumber", "CustomerGSTIN", "UINorComposition",
			"OriginalCustomerGSTIN", "CustomerName", "CustomerCode",
			"BillToState", "ShipToState", "POS", "PortCode",
			"ShippingBillNumber", "ShippingBillDate", "FOB", "ExportDuty",
			"HSNorSAC", "ProductCode", "ProductDescription",
			"CategoryOfProduct", "UnitOfMeasurement", "IntegratedTaxRate",
			"CentralTaxRate", "StateUTTaxRate", "CessRateAdvalorem",
			"CessAmountAdvalorem", "CessRateSpecific", "InvoiceValue",
			"ReverseChargeFlag", "TCSFlag", "eComGSTIN", "ITCFlag",
			"ReasonForCreditDebitNote", "AccountingVoucherNumber",
			"AccountingVoucherDate", "Userdefinedfield1", "Userdefinedfield2",
			"Userdefinedfield3", "Tax_Rate_Reg", "Rate Check", "POS Check",
			"Period_y", "Filing Period", "GSTIN Valid", "Credit Note",
			"Export days condition", "HSN", "GSTR 9", "Category_y",
			"Sub_Category", "CentralTaxAmount", "CessAmountSpecific",
			"IntegratedTaxAmount", "Quantity", "StateUTTaxAmount",
			"Tax_Amount_Reg", "TaxableValue", "SR_vs_GL",
			"Difference in Taxable Amount", "Difference in IGST Amount",
			"Difference in CGST Amount", "Difference in SGST Amount");

	private static final int NO_OF_COLUMNS = EXPECTED_HEADERNAMES_LIST.size();

	
	public String ReadProcessedFileAndPersist(Long configId,
			Optional<GlReconReportConfigEntity> reconConfigEntity)
			throws Exception, IOException {
		Document document;
		 String status=null;
		User user = SecurityContext.getUser();
		String userName = user.getUserPrincipalName()!=null?user.getUserPrincipalName():"SYSTEM";
		
		if (!reconConfigEntity.isPresent()) {
			throw new AppException(
					"Recon config not found for ID: " + configId);
		}

		String docId = reconConfigEntity.get().getDocId();
		if (Strings.isNullOrEmpty(docId)) {
			throw new AppException(
					"Document ID is empty for configId: " + configId);
		}

		document = DocumentUtility.downloadDocumentByDocId(docId);
		LOGGER.debug("Document name: {}", document.getName());


		try (InputStream zipInputStream = document.getContentStream().getStream();
			     ZipInputStream zis = new ZipInputStream(zipInputStream)) {

			    ZipEntry entry;
			    while ((entry = zis.getNextEntry()) != null) {
			        if (!entry.isDirectory() && entry.getName().endsWith(".csv")) {
			            LOGGER.info("Processing file from ZIP: {}", entry.getName());

			            String fileName = entry.getName();
			            String fileType = fileName.split("_")[0];

			            TabularDataSourceTraverser traverser = traverserFactory.getTraverser(fileName);
			            if (traverser == null) {
			                throw new AppException("No Traverser found for fileType: " + fileType);
			            }

			            // Optional: wrap zis for safety if traverser consumes stream
			            ByteArrayOutputStream baos = new ByteArrayOutputStream();
			            byte[] buffer = new byte[1024];
			            int len;
			            while ((len = zis.read(buffer)) > 0) {
			                baos.write(buffer, 0, len);
			            }

			            ByteArrayInputStream entryInputStream = new ByteArrayInputStream(baos.toByteArray());

			            TabularDataLayout layout = new DummyTabularDataLayout(0, 0, 0, NO_OF_COLUMNS);
			            FileUploadDocRowHandler<?> rowHandler = new FileUploadDocRowHandler<>();

			            traverser.traverse(entryInputStream, layout, rowHandler, null);

			            List<Object[]> fileList = rowHandler.getFileUploadList();
			            if (CollectionUtils.isEmpty(fileList)) {
			                throw new AppException("Empty file detected in ZIP: " + fileName);
			            }

			            List<GlReconSrprStgEntity> rowsAsDtoList = fileList.stream()
			                    .map(row -> convertRowsToDto(row, userName, configId))
			                    .collect(Collectors.toList());
			            
			            if (LOGGER.isDebugEnabled()) {
			                String msg = String.format("Saving %d GL Recon SRPR staging records: %s",
			                                           rowsAsDtoList.size(), rowsAsDtoList);
			                LOGGER.debug(msg);
			            }


			            glReconSrprStgRepository.saveAll(rowsAsDtoList);
			            
			            if (LOGGER.isDebugEnabled()) {
			                String msg = String.format("Saving %d GL Recon SRPR staging records: %s",
			                                           rowsAsDtoList.size(), rowsAsDtoList);
			                LOGGER.debug(msg);
			            }
			        }
			    }

			    // Now the ZIP is closed, call your stored procedure safely
			     status = callGlReconSrPrDataProc(configId);
			    LOGGER.info("Procedure call status: {}", status);
			}
		return status;
	}
	private static File createTempDir() throws IOException {
		String tempFolderPrefix = "GL_RECON";
		return Files.createTempDirectory(tempFolderPrefix).toFile();
	}

	private void validateHeaders(Long fileId,
			FileUploadDocRowHandler<?> rowHandler) {
		try {

			if (rowHandler.getHeaderRow() == null) {

				String msg = "The headers are empty.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				// fileStatusRepository.updateErrorFieNameById(fileId, msg);
				// markFileAsFailed(fileId, msg);
				LOGGER.error(msg);
				throw new AppException(msg);

			}

			@SuppressWarnings({"unchecked", "rawtypes"})
			List<String> actualHeaderNames = new ArrayList(
					Arrays.asList(rowHandler.getHeaderRow()));

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"User upload file headers names %s "
								+ "and header count %d",
						actualHeaderNames.toString(), actualHeaderNames.size());
				LOGGER.debug(msg);
			}

			if (actualHeaderNames.size() != NO_OF_COLUMNS) {
				String msg = String.format(
						"The number of columns in the file should be %d. "
								+ "Aborting the file processing.",
						NO_OF_COLUMNS);
				// markFileAsFailed(fileId, msg);

				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				// fileStatusRepository.updateErrorFieNameById(fileId, msg);
				LOGGER.equals(msg);
				throw new AppException(msg);
			}

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Expected User response headers names "
								+ "%s and header count %d",
						EXPECTED_HEADERNAMES_LIST.toString(),
						EXPECTED_HEADERNAMES_LIST.size());
				LOGGER.debug(msg);
			}

			boolean isMatch = headersMatch(EXPECTED_HEADERNAMES_LIST,
					actualHeaderNames);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Comparing two files header name " + "and count: %s",
						isMatch);
				LOGGER.debug(msg);
			}

			if (!isMatch) {
				String msg = "The header names/order are not as expected.";
				if (msg.length() > 200)
					msg = msg.substring(0, 200);
				// fileStatusRepository.updateErrorFieNameById(fileId, msg);
				// markFileAsFailed(fileId, msg);
				LOGGER.error(msg);
				throw new AppException(msg);
			}

		} catch (Exception ex) {
			String msg = (ex instanceof AppException)
					? ex.getMessage()
					: "Error occured while processing the file";
			// markFileAsFailed(fileId, msg);
			if (msg.length() > 200)
				msg = msg.substring(0, 200);

			// fileStatusRepository.updateErrorFieNameById(fileId, msg);
			throw (ex instanceof AppException)
					? ((AppException) ex)
					: new AppException(msg, ex);
		}

	}

	private GlReconSrprStgEntity convertRowsToDto(Object[] arr, String userName, Long configId) {

		GlReconSrprStgEntity obj = new GlReconSrprStgEntity();
		
		obj.setGlGl((arr[0] != null) ? arr[0].toString() : null);
		obj.setReferenceGl((arr[1] != null) ? arr[1].toString() : null);
		obj.setAccountingDocumentNumberGl(
				(arr[2] != null) ? arr[2].toString() : null);
		obj.setDocumentTypeGl((arr[3] != null) ? arr[3].toString() : null);
		obj.setDocumentDateGl((arr[4] != null) ? arr[4].toString() : null);
		obj.setAmountGl((arr[5] != null) ? arr[5].toString() : null);
		obj.setTextGl((arr[6] != null) ? arr[6].toString() : null);
		obj.setClearingDocumentNumberGl(
				(arr[7] != null) ? arr[7].toString() : null);
		obj.setPlantGl((arr[8] != null) ? arr[8].toString() : null);
		obj.setPostingDateGl((arr[9] != null) ? arr[9].toString() : null);
		obj.setCompanyCodeGl((arr[10] != null) ? arr[10].toString() : null);
		obj.setCustomerCodeGl((arr[11] != null) ? arr[11].toString() : null);
		obj.setVendorCodeGl((arr[12] != null) ? arr[12].toString() : null);
		obj.setEntryDateGl((arr[13] != null) ? arr[13].toString() : null);
		obj.setBpGl((arr[14] != null) ? arr[14].toString() : null);
		obj.setOffsetAccountGl((arr[15] != null) ? arr[15].toString() : null);
		obj.setTransactionTypeGl((arr[16] != null) ? arr[16].toString() : null);
		obj.setPeriodGl((arr[17] != null) ? arr[17].toString() : null);
		obj.setYearMonthGl((arr[18] != null) ? arr[18].toString() : null);
		obj.setNatureGl((arr[19] != null) ? arr[19].toString() : null);
		obj.setGstin((arr[20] != null) ? arr[20].toString() : null);
		obj.setGlType((arr[21] != null) ? arr[21].toString() : null);
		obj.setTaxCodeDescriptionMs(
				(arr[22] != null) ? arr[22].toString() : null);
		obj.setTaxTypeMs((arr[23] != null) ? arr[23].toString() : null);
		obj.setEligibilityMs((arr[24] != null) ? arr[24].toString() : null);
		obj.setTaxRateMs((arr[25] != null) ? arr[25].toString() : null);
		obj.setDocumentType((arr[26] != null) ? arr[26].toString() : null);
		obj.setDocumentTypeMs((arr[27] != null) ? arr[27].toString() : null);
		obj.setPeriodX((arr[28] != null) ? arr[28].toString() : null);
		obj.setGlKey((arr[29] != null) ? arr[29].toString() : null);
		obj.setAdvanceGl((arr[30] != null) ? arr[30].toString() : null);
		obj.setRevenueGl((arr[31] != null) ? arr[31].toString() : null);
		obj.setTaxGl((arr[32] != null) ? arr[32].toString() : null);
		obj.setCgstGl((arr[33] != null) ? arr[33].toString() : null);
		obj.setGlCodeMissingInMaster(
				(arr[34] != null) ? arr[34].toString() : null);
		obj.setIgstGl((arr[35] != null) ? arr[35].toString() : null);
		obj.setRevenue((arr[36] != null) ? arr[36].toString() : null);
		obj.setSgstGl((arr[37] != null) ? arr[37].toString() : null);
		obj.setCategoryX((arr[38] != null) ? arr[38].toString() : null);
		obj.setAccountingDocumentNumberReg(
				(arr[39] != null) ? arr[39].toString() : null);
		obj.setGstr9c((arr[40] != null) ? arr[40].toString() : null);
		obj.setSourceIdentifier((arr[41] != null) ? arr[41].toString() : null);
		obj.setSourceFileName((arr[42] != null) ? arr[42].toString() : null);
		obj.setGlAccountCode((arr[43] != null) ? arr[43].toString() : null);
		obj.setDivision((arr[44] != null) ? arr[44].toString() : null);
		obj.setSubdivision((arr[45] != null) ? arr[45].toString() : null);
		obj.setProfitCentre1((arr[46] != null) ? arr[46].toString() : null);
		obj.setProfitCentre2((arr[47] != null) ? arr[47].toString() : null);
		obj.setPlantCode((arr[48] != null) ? arr[48].toString() : null);
		obj.setReturnPeriod((arr[49] != null) ? arr[49].toString() : null);
		obj.setSupplierGstin((arr[50] != null) ? arr[50].toString() : null);
		obj.setDocumentType((arr[51] != null) ? arr[51].toString() : null);
		obj.setSupplyType((arr[52] != null) ? arr[52].toString() : null);
		obj.setDocumentNumber((arr[53] != null) ? arr[53].toString() : null);
		obj.setDocumentDate((arr[54] != null) ? arr[54].toString() : null);
		obj.setOriginalDocumentNumber(
				(arr[55] != null) ? arr[55].toString() : null);
		obj.setOriginalDocumentDate(
				(arr[56] != null) ? arr[56].toString() : null);
		obj.setCrdrpreGst((arr[57] != null) ? arr[57].toString() : null);
		obj.setLineNumber((arr[58] != null) ? arr[58].toString() : null);
		obj.setCustomerGstin((arr[59] != null) ? arr[59].toString() : null);
		obj.setUinOrComposition((arr[60] != null) ? arr[60].toString() : null);
		obj.setOriginalCustomerGstin(
				(arr[61] != null) ? arr[61].toString() : null);
		obj.setCustomerName((arr[62] != null) ? arr[62].toString() : null);
		obj.setCustomerCode((arr[63] != null) ? arr[63].toString() : null);
		obj.setBillToState((arr[64] != null) ? arr[64].toString() : null);
		obj.setShipToState((arr[65] != null) ? arr[65].toString() : null);
		obj.setPos((arr[66] != null) ? arr[66].toString() : null);
		obj.setPortCode((arr[67] != null) ? arr[67].toString() : null);
		obj.setShippingBillNumber(
				(arr[68] != null) ? arr[68].toString() : null);
		obj.setShippingBillDate((arr[69] != null) ? arr[69].toString() : null);
		obj.setFob((arr[70] != null) ? arr[70].toString() : null);
		obj.setExportDuty((arr[71] != null) ? arr[71].toString() : null);
		obj.setHsnOrSac((arr[72] != null) ? arr[72].toString() : null);
		obj.setProductCode((arr[73] != null) ? arr[73].toString() : null);
		obj.setProductDescription(
				(arr[74] != null) ? arr[74].toString() : null);
		obj.setCategoryOfProduct((arr[75] != null) ? arr[75].toString() : null);
		obj.setUnitOfMeasurement((arr[76] != null) ? arr[76].toString() : null);
		obj.setIntegratedTaxRate((arr[77] != null) ? arr[77].toString() : null);
		obj.setCentralTaxRate((arr[78] != null) ? arr[78].toString() : null);
		obj.setStateUtTaxRate((arr[79] != null) ? arr[79].toString() : null);
		obj.setCessRateAdvalorem((arr[80] != null) ? arr[80].toString() : null);
		obj.setCessAmountAdvalorem(
				(arr[81] != null) ? arr[81].toString() : null);
		obj.setCessRateSpecific((arr[82] != null) ? arr[82].toString() : null);
		obj.setInvoiceValue((arr[83] != null) ? arr[83].toString() : null);
		obj.setReverseChargeFlag((arr[84] != null) ? arr[84].toString() : null);
		obj.setTcsFlag((arr[85] != null) ? arr[85].toString() : null);
		obj.setEcomGstin((arr[86] != null) ? arr[86].toString() : null);
		obj.setItcFlag((arr[87] != null) ? arr[87].toString() : null);
		obj.setReasonForCreditDebitNote(
				(arr[88] != null) ? arr[88].toString() : null);
		obj.setAccountingVoucherNumber(
				(arr[89] != null) ? arr[89].toString() : null);
		obj.setAccountingVoucherDate(
				(arr[90] != null) ? arr[90].toString() : null);
		obj.setUserDefinedField1((arr[91] != null) ? arr[91].toString() : null);
		obj.setUserDefinedField2((arr[92] != null) ? arr[92].toString() : null);
		obj.setUserDefinedField3((arr[93] != null) ? arr[93].toString() : null);
		obj.setTaxRateReg((arr[94] != null) ? arr[94].toString() : null);
		obj.setRateCheck((arr[95] != null) ? arr[95].toString() : null);
		obj.setPosCheck((arr[96] != null) ? arr[96].toString() : null);
		obj.setPeriodY((arr[97] != null) ? arr[97].toString() : null);
		obj.setFilingPeriod((arr[98] != null) ? arr[98].toString() : null);
		obj.setGstinValid((arr[99] != null) ? arr[99].toString() : null);
		obj.setCreditNote((arr[100] != null) ? arr[100].toString() : null);
		obj.setExportDaysCondition(
				(arr[101] != null) ? arr[101].toString() : null);
		obj.setHsn((arr[102] != null) ? arr[102].toString() : null);
		obj.setGstr9((arr[103] != null) ? arr[103].toString() : null);
		obj.setCategoryY((arr[104] != null) ? arr[104].toString() : null);
		obj.setSubCategory((arr[105] != null) ? arr[105].toString() : null);
		obj.setCentralTaxAmount(
				(arr[106] != null) ? arr[106].toString() : null);
		obj.setCessAmountSpecific(
				(arr[107] != null) ? arr[107].toString() : null);
		obj.setIntegratedTaxAmount(
				(arr[108] != null) ? arr[108].toString() : null);
		obj.setQuantity((arr[109] != null) ? arr[109].toString() : null);
		obj.setStateUtTaxAmount(
				(arr[110] != null) ? arr[110].toString() : null);
		obj.setTaxAmountReg((arr[111] != null) ? arr[111].toString() : null);
		obj.setTaxableValue((arr[112] != null) ? arr[112].toString() : null);
		obj.setSrVsGl((arr[113] != null) ? arr[113].toString() : null);
		obj.setDifferenceInTaxableAmount(
				(arr[114] != null) ? arr[114].toString() : null);
		obj.setDifferenceInIgstAmount(
				(arr[115] != null) ? arr[115].toString() : null);
		obj.setDifferenceInCgstAmount(
				(arr[116] != null) ? arr[116].toString() : null);
		obj.setDifferenceInSgstAmount(
				(arr[117] != null) ? arr[117].toString() : null);
		obj.setReconConfigId(configId);
		obj.setReconKey(generateReconKey(arr));
		obj.setIsDelete(false);
		obj.setCreatedOn(LocalDateTime.now());
		obj.setCreatedBy(userName);
		return obj;

	}
	private boolean headersMatch(List<String> expected, List<String> actual) {
		return !Streams
				.zip(actual.stream(), expected.stream(),
						(a, e) -> createPair(a, e))
				.anyMatch(p -> !p.getValue0().equals(p.getValue1()));
	}

	private Pair<String, String> createPair(String val1, String val2) {
		String val1Str = (val1 == null) ? "" : val1.trim().toUpperCase();
		String val2Str = (val2 == null) ? "" : val2.trim().toUpperCase();
		return new Pair<>(val1Str, val2Str);
	}
	
	private String generateReconKey(Object[] arr) {
	    String gstin = (arr[20] != null) ? arr[20].toString() : "";
	    String voucherNumber = (arr[89] != null) ? arr[89].toString() : "";
	    String voucherDate = (arr[90] != null) ? arr[90].toString() : "";

	    return gstin + "|" + voucherNumber + "|" + voucherDate;
	}

	private String callGlReconSrPrDataProc(Long configId) {
	    String procName = "callGlReconSrPrDataProc";

	    try {
	        StoredProcedureQuery storedProc = entityManager
	            .createNamedStoredProcedureQuery(procName);

	        storedProc.setParameter("P_RECON_CONFIG_ID", configId);
	        LOGGER.info("Stored procedure {} before execution", procName);
	        // Execute the procedure without expecting a result
	        storedProc.execute();

	        LOGGER.info("Stored procedure {} executed successfully", procName);
	        return "Success";
	    } catch (Exception e) {
	        LOGGER.error("Error executing stored procedure: {}", e.getMessage(), e);
	        throw new AppException("Stored procedure execution failed", e);
	    }
	}

}
