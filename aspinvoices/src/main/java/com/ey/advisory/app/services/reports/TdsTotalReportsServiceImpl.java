package com.ey.advisory.app.services.reports;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.ey.advisory.admin.data.entities.client.EntityInfoEntity;
import com.ey.advisory.admin.data.repositories.client.EntityInfoDetailsRepository;
import com.ey.advisory.app.docs.dto.simplified.TDSDetailedRespDto;
import com.ey.advisory.app.docs.dto.simplified.TDSDownlaodRequestDto;
import com.ey.advisory.app.services.jobs.gstr6.GetGstr2xScreensDaoImpl;
import com.ey.advisory.app.services.jobs.gstr6.TdsDetailsScreensDaoImpl;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.search.PageRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

/**
 * @author Sasidhar
 *
 * 
 */

@Service("TdsTotalReportsServiceImpl")
public class TdsTotalReportsServiceImpl {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(TdsTotalReportsServiceImpl.class);

    @Autowired
    CommonUtility commonUtility;

    @Autowired
    @Qualifier("GetGstr2xScreensDaoImpl")
    GetGstr2xScreensDaoImpl getGstr2xScreensDaoImpl;

    @Autowired
    @Qualifier("ProcessedRecordsCommonSecParam")
    private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

    @Autowired
    @Qualifier("TdsDetailsScreensDaoImpl")
    private TdsDetailsScreensDaoImpl idsScreensDaoImpl;

    
    @Autowired
	@Qualifier("entityInfoDetailsRepository")
	EntityInfoDetailsRepository repo;
    
    @PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;



    public Workbook downloadReports(TDSDownlaodRequestDto criteria,
            PageRequest pageReq) {

        Workbook workbook = new Workbook();
        int startRow = 6;
        int startcolumn = 0;
        int tds = 6;
        int tcs = 0;
        int tdsa = 1;
        int tcsa = 0;
        boolean isHeaderRequired = false;
      /* List<Geetgstr2xScreensRespDto> screenTdsResponseFromdist = new ArrayList<>();
        screenTdsResponseFromdist = getGstr2xScreensDaoImpl
                .responsebyTdsReq(criteria);

        List<TDSDetailedRespDto> gettdsresponseFromdist = new ArrayList<>();
        gettdsresponseFromdist = idsScreensDaoImpl.fetchTdsByReq(criteria);
        */
        //List<Object[]> list = null;

		//Long fileId = criteria.getFileId();
		//String type = criteria.getType();
		List<String> gstin = criteria.getGstin();
		String convertedGstin = String.join(",", gstin);
		String taxperiod = criteria.getTaxPeriod();
		Integer convertedTaxPeriod = GenUtil.convertTaxPeriodToInt(taxperiod);


		
		/*StoredProcedureQuery storedProcSummaryReport = entityManager
				.createStoredProcedureQuery(
						"USP_TCS_TDS_CREDIT_REPORT");

		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_SUPPLIER_GSTIN", String.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_SUPPLIER_GSTIN", convertedGstin);
		
		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_REPORT_TYPE", String.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_REPORT_TYPE", type);
		
		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_DERIVED_RET_PERIOD", convertedTaxPeriod);
		
		list = storedProcSummaryReport.getResultList();

		List<Geetgstr2xScreensRespDto> screenTdsResponseFromdist = list
				.stream().map(o -> convertCreditReport(o))
				.collect(Collectors
						.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Credit report data {}"  ,screenTdsResponseFromdist);
		}
        */
        


        workbook = createWorkbookWithExcelTemplate("ReportTemplates",
                "GetGsr2XTotalRecords.xlsx");

       /* if (screenTdsResponseFromdist != null
                && screenTdsResponseFromdist.size() > 0) {
        	
            String[] invoiceHeaders = commonUtility
                    .getProp("getGstr2x.total.report.columns").split(",");
            if(workbook!=null && workbook.getWorksheets()!=null){
            	Worksheet worksheet = workbook.getWorksheets().get(0);
   			 Cells errorDumpCells = worksheet.getCells();
   				
           // Cells errorDumpCells = workbook.getWorksheets().get(0).getCells();
            List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			if (findEntityByEntityId != null) {

				errorDumpCells.get("B2").setValue(
				 findEntityByEntityId.getEntityName());
			
			}
			
			Optional<Geetgstr2xScreensRespDto> fromTaxPeriod = screenTdsResponseFromdist
					.stream().findFirst();
			if (fromTaxPeriod.isPresent()) {
				errorDumpCells.get("B3").setValue(
						fromTaxPeriod.get().getReturnPeriod());
			}

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");

			String date = FOMATTER.format(istDateTimeFromUTC);
			String time = FOMATTER1.format(istDateTimeFromUTC);

			errorDumpCells.get("D3").setValue( date + "" + time);
			//errorDumpCells.get("F3").setValue( time);
			
            errorDumpCells.importCustomObjects(screenTdsResponseFromdist,
                    invoiceHeaders, isHeaderRequired, startRow, startcolumn,
                    screenTdsResponseFromdist.size(), true, "yyyy-mm-dd",
                    false);
            
          //to delete last row of the report.
			int lastRowIndex = worksheet.getCells().getMaxDataRow();
			worksheet.getCells().deleteRow(lastRowIndex + 1);
        }
    }*/
        List<Object[]> detailedList = null;

		//Long fileId = criteria.getFileId();
		//String type = criteria.getType();

		
		StoredProcedureQuery storedProcSummaryReport = entityManager
				.createStoredProcedureQuery(
						"USP_TCS_TDS_CONSOLIDATED_REPORT");

		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_SUPPLIER_GSTIN", String.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_SUPPLIER_GSTIN", convertedGstin);
		
		storedProcSummaryReport.registerStoredProcedureParameter(
				"P_DERIVED_RET_PERIOD", Integer.class, ParameterMode.IN);

		storedProcSummaryReport.setParameter("P_DERIVED_RET_PERIOD", convertedTaxPeriod);
		
		detailedList = storedProcSummaryReport.getResultList();

		List<TDSDetailedRespDto> gettdsresponseFromdist = detailedList
				.stream().map(o -> convertConsolidatedReport(o))
				.collect(Collectors
						.toCollection(ArrayList::new));
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Consolidated report Data {}"  ,gettdsresponseFromdist);
		}
        
        
        
        if (gettdsresponseFromdist != null
                && gettdsresponseFromdist.size() > 0) {
        	String[] invoiceHeaders = commonUtility
                    .getProp("gettds.total.report.columns").split(",");
        	if(workbook!=null && workbook.getWorksheets()!=null){
        		 Worksheet worksheet = workbook.getWorksheets().get(0);
    			 Cells errorDumpCells = worksheet.getCells();
            //Cells errorDumpCells = workbook.getWorksheets().get(1).getCells();
            List<Long> entityId = criteria.getEntityId();
			EntityInfoEntity findEntityByEntityId = new EntityInfoEntity();
			for (Long entity : entityId) {
				findEntityByEntityId = repo.findEntityByEntityId(entity);
			}
			if (findEntityByEntityId != null) {
			
				errorDumpCells.get("B2").setValue(
						 findEntityByEntityId.getEntityName());
			
			}
			
			Optional<TDSDetailedRespDto> fromTaxPeriod = gettdsresponseFromdist
					.stream().findFirst();
			if (fromTaxPeriod.isPresent()) {
				errorDumpCells.get("B3").setValue(
						fromTaxPeriod.get().getTaxPeriod());
			}

			LocalDateTime now = EYDateUtil
					.toUTCDateTimeFromLocal(LocalDateTime.now());

			LocalDateTime istDateTimeFromUTC = EYDateUtil
					.toISTDateTimeFromUTC(now);
			DateTimeFormatter FOMATTER = DateTimeFormatter
					.ofPattern("dd-MM-yyyy");

			DateTimeFormatter FOMATTER1 = DateTimeFormatter
					.ofPattern("HH:mm:ss");

			String date = FOMATTER.format(istDateTimeFromUTC);
			String time = FOMATTER1.format(istDateTimeFromUTC);

			errorDumpCells.get("D3").setValue("'" + date + " " + time);
			//errorDumpCells.get("F3").setValue( time);
			
			
			 errorDumpCells.importCustomObjects(gettdsresponseFromdist,
	                    invoiceHeaders, isHeaderRequired, tds, tcs,
	                    gettdsresponseFromdist.size(), true, "yyyy-mm-dd",
	                    false);
			 
			//to delete last row of the report.
				int lastRowIndex = worksheet.getCells().getMaxDataRow();
				worksheet.getCells().deleteRow(lastRowIndex + 1);
        }
        }
        return workbook;
    }

    private Workbook createWorkbookWithExcelTemplate(String folderName,
            String fileName) {
        Workbook workbook = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL template_Dir = classLoader.getResource(folderName + "/");
            String templatePath = template_Dir.getPath() + fileName;
            LoadOptions options = new LoadOptions(FileFormatType.XLSX);
            CommonUtility.setAsposeLicense();
            workbook = new Workbook(templatePath, options);
            workbook.getSettings()
                    .setMemorySetting(MemorySetting.MEMORY_PREFERENCE);
        } catch (Exception ex) {
            LOGGER.error("Exception in creating workbook : ", ex);
        }
        return workbook;
    }
    private TDSDetailedRespDto convertConsolidatedReport(Object[] arr) {
		TDSDetailedRespDto obj = new TDSDetailedRespDto();
		
		obj.setActionSavedatDigiGST(arr[0] != null ? arr[0].toString() : null);
		obj.setDigiGstRemarks(arr[1] != null ? arr[1].toString() : null);
		obj.setDigiGstComment(arr[2] != null ? arr[2].toString() : null);
		obj.setGstin(
				arr[3] != null ? arr[3].toString() : null);
		obj.setType(
				arr[4] != null ? arr[4].toString() : null);
		obj.setTaxPeriod(
				arr[5] != null ? arr[5].toString() : null);
		obj.setMonth(arr[6] != null ? arr[6].toString() : null);
		obj.setGstinOfDeductor(
				arr[7] != null ? arr[7].toString() : null);
		obj.setDeductorName(
				arr[8] != null ? arr[8].toString() : null);
		obj.setDocNo(arr[9] != null ? arr[9].toString() : null);
		obj.setDocDate(
				arr[10] != null ? arr[10].toString() : null);
		obj.setOrgMonth(arr[11] != null ? arr[11].toString() : null);
		obj.setOrgDocNo(arr[12] != null ? arr[12].toString() : null);
		obj.setOrgDocDate(arr[13] != null ? arr[13].toString() : null);
		obj.setSuppliesCollected(arr[14] != null ? arr[14].toString() : null);
		obj.setSuppliesReturned(
				arr[15] != null ? arr[15].toString() : null);
		obj.setNetSupplies(
				arr[16] != null ? arr[16].toString() : null);
		obj.setIGST(
				arr[17] != null ? arr[17].toString() : null);
		obj.setCGST(
				arr[18] != null ? arr[18].toString() : null);
		obj.setSGST(
				arr[19] != null ? arr[19].toString() : null);
		obj.setInvoiceValue(
				arr[20] != null ? arr[20].toString() : null);
		obj.setOrgTaxableValue(
				arr[21] != null ? arr[21].toString() : null);
		obj.setOrgInvoiceValue(
				arr[22] != null ? arr[22].toString() : null);
		obj.setPos(
				arr[23] != null ? arr[23].toString() : null);
		obj.setChkSum(
				arr[24] != null ? arr[24].toString() : null);
		obj.setActionSavedatGSTN(
				arr[25] != null ? arr[25].toString() : null);
		obj.setGstnRemarks(
				arr[26] != null ? arr[26].toString() : null);
		obj.setGstnComment(
				arr[27] != null ? arr[27].toString() : null);

		

		return obj;

	}
}
