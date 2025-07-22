/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.services.gstr3b.qtr.filing.apipush.Gstr3bQtrFilingDetailApiPushEntity;

/**
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr3bQtrFilingApiPushRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bQtrFilingApiPushRepository
		extends CrudRepository<Gstr3bQtrFilingDetailApiPushEntity, Long> {

	@Query("SELECT e.gstin FROM Gstr3bQtrFilingDetailApiPushEntity e "
			+ "WHERE e.payloadId =:payloadId ")
	public List<String> getPayloadGstins(@Param("payloadId") String payloadId);

	@Query(" SELECT e FROM Gstr3bQtrFilingDetailApiPushEntity e "
			+ "WHERE e.payloadId =:payloadId  ")
	public List<Gstr3bQtrFilingDetailApiPushEntity> getPayloadGstinDetails(
			@Param("payloadId") String payloadId);

}