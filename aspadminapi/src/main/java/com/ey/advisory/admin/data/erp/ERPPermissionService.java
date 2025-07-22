/**
 * 
 */
package com.ey.advisory.admin.data.erp;

import java.util.List;

import com.ey.advisory.core.dto.ErpPermissionDeleteDto;

/**
 * @author Laxmi.Salukuti
 *
 */
public interface ERPPermissionService {

	public void deleteErpPermissionDetails(List<ErpPermissionDeleteDto> erpDelPerdto);

}
