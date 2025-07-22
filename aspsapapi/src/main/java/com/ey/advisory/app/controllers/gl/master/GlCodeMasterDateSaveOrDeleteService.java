/**
 * @author kiran s
 
 
 */
package com.ey.advisory.app.controllers.gl.master;

import java.util.List;

public interface GlCodeMasterDateSaveOrDeleteService {
	
	  public void saveGlCodeMasterList(List<GlCodeMasterDto> dtoList, Long entityId);
	  void saveDocTypeMasterList(List<GlMasterDocTypeDto> dtoList, Long entityId);
	  void saveSupplyTypeMasterList(List<GlMasterSupplyTypeDto> dtoList, Long entityId);
	  void saveBusinessPlaceMasterList(List<GlBusinessPlaceMasterDto> dtoList, Long entityId);
	  void saveTaxCodeMasterList(List<GlTaxCodeMasterDto>taxCodeList, Long entityId);
	  void saveGlMappingMasterList(List<GlCodeMappingMasterDto> dtoList, Long entityId);
	  //delete
	  public void softDeleteGlCodeMaster(List<Long> ids);
	  public void softDeleteDocTypeMaster(List<Long> ids);
	  public void softDeleteSupplyTypeMaster(List<Long> ids);
	  public void softDeleteBusinessPlaceMaster(List<Long> ids);
	  public void softDeleteTaxCodeMaster(List<Long> ids);
	
}
