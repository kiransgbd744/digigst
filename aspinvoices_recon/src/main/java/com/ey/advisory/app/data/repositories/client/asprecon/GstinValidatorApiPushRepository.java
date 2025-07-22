/**
 * 
 */
package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.GetGstinMasterDetailApiPushEntity;

/**
 * @author vishal.verma
 *
 */
@Repository("GstinValidatorApiPushRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinValidatorApiPushRepository
		extends CrudRepository<GetGstinMasterDetailApiPushEntity, Long> {
	
	@Query("SELECT e.gstin FROM GetGstinMasterDetailApiPushEntity e "
			+ "WHERE e.payloadId =:payloadId ")
	public List<String> getPayloadGstins(@Param("payloadId") String payloadId);
	
	@Query(" SELECT e FROM GetGstinMasterDetailApiPushEntity e "
			+ "WHERE e.payloadId =:payloadId AND vendorValidationApi = false ")
	public List<GetGstinMasterDetailApiPushEntity> getPayloadGstinDetails(@Param("payloadId") String payloadId);

	
	@Query(" SELECT e FROM GetGstinMasterDetailApiPushEntity e "
			+ "WHERE e.payloadId =:payloadId  AND vendorValidationApi = true ")
	public List<GetGstinMasterDetailApiPushEntity> getPayloadGstinDetailsForvendorAPI(@Param("payloadId") String payloadId);
	
	@Query("SELECT e.gstin FROM GetGstinMasterDetailApiPushEntity e "
			+ "WHERE e.taxpayerType In (:taxpayerType) And gstin In "
			+ "(:gstin) And e.payloadId =:payloadId ")
	public List<String> getGstinsByTaxPayerTypeInAndGstinAndPayloadId(
			@Param("taxpayerType") List<String> taxpayerType,
			@Param("gstin") List<String> gstin,
			@Param("payloadId") String payloadId);

}