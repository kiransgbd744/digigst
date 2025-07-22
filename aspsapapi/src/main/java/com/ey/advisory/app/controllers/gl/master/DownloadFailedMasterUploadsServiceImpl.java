/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aspose.cells.Cells;
import com.aspose.cells.FileFormatType;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.MemorySetting;
import com.aspose.cells.Workbook;
import com.ey.advisory.app.data.entities.client.asprecon.GlBusinessPlaceMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlCodeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlDocTypeMasterEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlMasterSupplyTypeEntity;
import com.ey.advisory.app.data.entities.client.asprecon.GlTaxCodeMasterEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.GlBusinessPlaceMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlCodeMasterRepo;
import com.ey.advisory.app.data.repositories.client.asprecon.GlDocTypeMasterRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlMasterSupplyTypeRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.GlTaxCodeMasterRepo;
import com.ey.advisory.common.CommonUtility;

import lombok.extern.slf4j.Slf4j;

@Service("DownloadFailedMasterUploadsServiceImpl")
@Slf4j
public class DownloadFailedMasterUploadsServiceImpl
		implements
			DownloadFailedMasterUploadsService {

	@Autowired
	@Qualifier("GlBusinessPlaceMasterRepository")
	private GlBusinessPlaceMasterRepository glBusinessPlaceMasterRepository;

	@Autowired
	@Qualifier("GlDocTypeMasterRepository")
	private GlDocTypeMasterRepository glDocTypeMasterRepository;

	@Autowired
	@Qualifier("GlMasterSupplyTypeRepository")
	private GlMasterSupplyTypeRepository glMasterSupplyTypeRepository;

	@Autowired
	@Qualifier("GlTaxCodeMasterRepo")
	private GlTaxCodeMasterRepo glTaxCodeMasterRepo;
	
	@Autowired
	@Qualifier("GlCodeMasterRepo")
	GlCodeMasterRepo glCodeMasterRepo;
	

	@Autowired
	CommonUtility commonUtility;

	@Override
	public Workbook generateFailedMasterUploadWorkbook(String fileId, String fileType) {
	    List<Object> dtoList = new ArrayList<>();
	    String templateFile;
        String[] headers;
	    // Fetch failed records based on fileType
	    try {
	        switch (fileType) {
	            case "Business_Unit_code":
	                List<GlBusinessPlaceMasterEntity> businessPlaceList = glBusinessPlaceMasterRepository
	                        .findByFileIdAndFileTypeAndIsActiveFalse(fileId, fileType);
	                for (GlBusinessPlaceMasterEntity entity : businessPlaceList) {
	                    GlBusinessPlaceMasterDto dto = new GlBusinessPlaceMasterDto();
	                    dto.setErrorCode(entity.getErrorCode());                        
	                    dto.setErrorDescription(entity.getErrorDesc());                 
	                    dto.setBusinessPlace(entity.getBusinessPlace());              
	                    dto.setBusinessArea(entity.getBusinessArea());                 
	                    dto.setPlantCode(entity.getPlantCode());                      
	                    dto.setProfitCentre(entity.getProfitCentre());                 
	                    dto.setCostCentre(entity.getCostCentre());                    
	                    dto.setGstin(entity.getGstin());                              
	                    dtoList.add(dto);
	                }
	                break;

	            case "Document_type":
	                List<GlDocTypeMasterEntity> docTypeList = glDocTypeMasterRepository
	                        .findByFileIdAndFileTypeAndIsActiveFalse(fileId, fileType);
	                for (GlDocTypeMasterEntity entity : docTypeList) {
	                    GlDocTypeMasterDto dto = new GlDocTypeMasterDto();
	                    dto.setErrorCode(entity.getErrorCode());              
	                    dto.setErrorDescription(entity.getErrorDesc());       
	                    dto.setDocType(entity.getDocType());                 
	                    dto.setDocTypeMs(entity.getDocTypeMs());             

	                    dtoList.add(dto);
	                }
	                break;


	            case "Supply_Type":
	                List<GlMasterSupplyTypeEntity> supplyTypeList = glMasterSupplyTypeRepository
	                        .findByFileIdAndFileTypeAndIsActiveFalse(fileId, fileType);
	                for (GlMasterSupplyTypeEntity entity : supplyTypeList) {
	                    GlMasterSupplyTypeDto dto = new GlMasterSupplyTypeDto();
	                    dto.setErrorCode(entity.getErrorCode());              // DigiGST Error Code
	                    dto.setErrorDescription(entity.getErrorDesc());       // DigiGST Error Description
	                    dto.setSupplyTypeReg(entity.getSupplyTypeReg());      // Supply_Type_Reg
	                    dto.setSupplyTypeMs(entity.getSupplyTypeMs());        // Supply_Type_MS

	                    dtoList.add(dto);
	                }
	                break;

	            case "Tax_code":
	                List<GlTaxCodeMasterEntity> taxCodeList = glTaxCodeMasterRepo
	                        .findByFileIdAndFileTypeAndIsActiveFalse(fileId, fileType);
	                
	                for (GlTaxCodeMasterEntity entity : taxCodeList) {
	                    GlTaxCodeMasterDto dto = new GlTaxCodeMasterDto();
	                    dto.setErrorCode(entity.getErrorCode());                     
	                    dto.setErrorDescription(entity.getErrorDesc());              
	                    dto.setTransactionTypeGl(entity.getTransactionTypeGl());      
	                    dto.setTaxCodeDescriptionMs(entity.getTaxCodeDescriptionMs()); 
	                    dto.setTaxTypeMs(entity.getTaxTypeMs());                      
	                    dto.setEligibilityMs(entity.getEligibilityMs());             
	                    dto.setTaxRateMs(entity.getTaxRateMs());                     

	                    dtoList.add(dto);
	                }
	                break;
	                
	            case "GL_Code_Mapping_Master_GL":
	                List<GlCodeMasterEntity> glCodeMasterList = glCodeMasterRepo
	                        .findByFileIdAndFileTypeAndIsActiveFalse(fileId, fileType);

	                for (GlCodeMasterEntity entity : glCodeMasterList) {
	                    GlCodeMasterDto dto = new GlCodeMasterDto();
	                    dto.setErrorCode(entity.getErrorCode());
	                    dto.setErrorDescription(entity.getErrorDesc());
	                    dto.setCgstTaxGlCode(entity.getCgstTaxGlCode());
	                    dto.setSgstTaxGlCode(entity.getSgstTaxGlCode());
	                    dto.setIgstTaxGlCode(entity.getIgstTaxGlCode());
	                    dto.setUgstTaxGlCode(entity.getUgstTaxGlCode());
	                    dto.setCompensationCessGlCode(entity.getCompensationCessGlCode());
	                    dto.setKeralaCessGlCode(entity.getKeralaCessGlCode());
	                    dto.setRevenueGls(entity.getRevenueGls());
	                    dto.setExpenceGls(entity.getExpenceGls());
	                    dto.setExchangeRate(entity.getExchangeRate());
	                   // dto.setDiffGl(entity.getDiffGl());
	                    dto.setExportGl(entity.getExportGl());
	                    dto.setForexGlsPor(entity.getForexGlsPor());
	                    dto.setTaxableAdvanceLiabilityGls(entity.getTaxableAdvanceLiabilityGls());
	                    dto.setNonTaxableAdvanceLiabilityGls(entity.getNonTaxableAdvanceLiabilityGls());
	                    dto.setCcAndStGls(entity.getCcAndStGls());
	                    dto.setUnbilledRevenueGls(entity.getUnbilledRevenueGls());
	                    dto.setBankAccGls(entity.getBankAccGls());
	                    dto.setInputTaxGls(entity.getInputTaxGls());
	                    dto.setFixedAssetGls(entity.getFixedAssetGls());
	                    dtoList.add(dto);
	                }
	                break;


	            default:
	                LOGGER.warn("Unsupported fileType provided: {}", fileType);
	                return null;
	        }

	        // Determine template and headers
	       

	        switch (fileType) {
	            case "Business_Unit_code":
	                templateFile = "Business_Unit_Report.xlsx";
	                headers = commonUtility.getProp("gl.business.unit.report.headers").split(",");
	                break;
	            case "Document_type":
	                templateFile = "Document_Type_Report.xlsx";
	                headers = commonUtility.getProp("gl.document.type.report.headers").split(",");
	                break;
	            case "Supply_Type":
	                templateFile = "Supply_Type_Report.xlsx";
	                headers = commonUtility.getProp("gl.supply.type.report.headers").split(",");
	                break;
	            
	            case "Tax_code":
	            	
	                templateFile = "Tax_Code_Master_Report.xlsx";
	                headers = commonUtility.getProp("gl.tax.code.master.report.headers").split(",");
	                break;
	                
	            case "GL_Code_Mapping_Master_GL":
	            	templateFile = "GL_Code_Master_Report.xlsx";
	                headers = commonUtility.getProp("gl.code.master.report.headers").split(",");
	                break;
	                
	            default:
	            	templateFile = "GL_Code_Master_Report.xlsx";
	                headers = commonUtility.getProp("gl.code.master.report.headers").split(",");
	             break;
	        }

	        // Create and populate workbook
	        Workbook workbook = createWorkbookWithExcelTemplate("ReportTemplates", templateFile);

	        if (!dtoList.isEmpty()) {
	            Cells cells = workbook.getWorksheets().get(0).getCells();
	            cells.importCustomObjects(dtoList, headers, false, 1, 0, dtoList.size(), true, "yyyy-mm-dd", false);
	        }

	        return workbook;

	    } catch (Exception ex) {
	        LOGGER.error("Error generating workbook for fileType {}: {}", fileType, ex.getMessage(), ex);
	        return null;
	    }
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

}
