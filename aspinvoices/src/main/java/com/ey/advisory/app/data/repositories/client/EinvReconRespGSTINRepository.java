/**
 * 
 */
package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.EinvReconRespGSTINEntity;

/**
 * @author Siva.Reddy
 *
 */

@Repository("EinvReconRespGSTINRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface EinvReconRespGSTINRepository
		extends JpaRepository<EinvReconRespGSTINEntity, Long>,
		JpaSpecificationExecutor<EinvReconRespGSTINEntity> {

	@Query("SELECT COUNT(*) FROM EinvReconRespGSTINEntity g "
			+ "WHERE g.gstin IN (:gstins) AND g.returnPeriod =:returnPeriod "
			+ "and g.isActive = true and g.status IN ('In-Progress','Initiated')")
	public int gstinIsAvailable(@Param("gstins") List<String> gstins,
			@Param("returnPeriod") String returnPeriod);

	@Modifying
	@Query("UPDATE EinvReconRespGSTINEntity g SET g.isActive = false "
			+ "WHERE g.gstin IN (:gstins) AND g.returnPeriod =:returnPeriod"
			+ " and g.isActive = true and g.status IN ('Completed','Failed')")
	public int updateActiveExistingRecords(@Param("gstins") List<String> gstins,
			@Param("returnPeriod") String returnPeriod);

	@Query("SELECT entity FROM EinvReconRespGSTINEntity entity "
			+ "WHERE entity.reconRespConfigId = :reconRespConfigId")
	List<EinvReconRespGSTINEntity> findByConfigId(
			@Param("reconRespConfigId") Long reconRespConfigId);

	@Modifying
	@Query("UPDATE EinvReconRespGSTINEntity g SET g.errorDesc = :errorDesc,g.status = :status "
			+ "WHERE g.reconRespConfigId = :reconRespConfigId")
	public int updateStatusandErrorDesc(
			@Param("reconRespConfigId") Long reconRespConfigId,
			@Param("errorDesc") String errorDesc,
			@Param("status") String status);
	
	@Query("SELECT g.gstin, g.reconRespConfigId, g.status FROM EinvReconRespGSTINEntity g "
			+ "WHERE g.gstin IN (:gstnsList) AND g.returnPeriod =:returnPeriod "
			+ "and g.isActive = true")
	public List<Object[]> getReconStatus(@Param("gstnsList") List<String> gstnsList,
			@Param("returnPeriod") String taxPeriod);


}
