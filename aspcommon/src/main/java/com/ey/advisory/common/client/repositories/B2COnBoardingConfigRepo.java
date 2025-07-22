/**
 * 
 */
package com.ey.advisory.common.client.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.common.client.domain.B2COnBoardingConfigEntity;

/**
 * @author Siva.Reddy
 *
 */
@Repository("B2COnBoardingConfigRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface B2COnBoardingConfigRepo
		extends JpaRepository<B2COnBoardingConfigEntity, Long>,
		JpaSpecificationExecutor<B2COnBoardingConfigEntity> {

	@Modifying
	@Query("UPDATE B2COnBoardingConfigEntity g SET g.isActive = false,g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy =:updatedBy "
			+ "WHERE g.entityId = :entityId and g.isActive = true")
	public int updateActiveExistingRecords(@Param("entityId") Long entityId,
			@Param("updatedBy") String updatedBy);

	public B2COnBoardingConfigEntity findByEntityIdAndIsActiveTrue(
			Long entityId);

	List<B2COnBoardingConfigEntity> findByEntityIdAndOptionSelectedAndIsActiveTrue(
			Long entityId, String optionSelected);

	@Query("SELECT entity FROM B2COnBoardingConfigEntity entity "
			+ "WHERE entity.pan =:pan and entity.isActive = true")
	B2COnBoardingConfigEntity getActivePan(@Param("pan") String pan);

	List<B2COnBoardingConfigEntity> findByPanAndIsActiveTrue(String pan);

	B2COnBoardingConfigEntity findByPanAndPlantCodeAndProfitCentreAndIsActiveTrue(
			String pan, String plantCode, String profitCenter);

	B2COnBoardingConfigEntity findByPanAndProfitCentreAndPlantCodeIsNullAndIsActiveTrue(
			String pan, String profitCenter);

	B2COnBoardingConfigEntity findByPanAndPlantCodeAndProfitCentreIsNullAndIsActiveTrue(
			String pan, String plantCode);

	B2COnBoardingConfigEntity findByPanAndPlantCodeIsNullAndProfitCentreIsNullAndIsActiveTrue(
			String pan);

	B2COnBoardingConfigEntity findByPanAndPlantCodeAndGstinAndIsActiveTrue(
			String pan, String plantCode, String gstin);

	B2COnBoardingConfigEntity findByPanAndPlantCodeAndGstinIsNullAndIsActiveTrue(
			String pan, String plantCode);

	B2COnBoardingConfigEntity findByPanAndGstinAndPlantCodeIsNullAndIsActiveTrue(
			String pan, String gstin);

	B2COnBoardingConfigEntity findByPanAndPlantCodeIsNullAndGstinIsNullAndIsActiveTrue(
			String pan);

	B2COnBoardingConfigEntity findByPanAndProfitCentreAndGstinAndIsActiveTrue(
			String pan, String plantCode, String gstin);

	B2COnBoardingConfigEntity findByPanAndProfitCentreAndGstinIsNullAndIsActiveTrue(
			String pan, String plantCode);

	B2COnBoardingConfigEntity findByPanAndGstinAndProfitCentreIsNullAndIsActiveTrue(
			String pan, String gstin);

	B2COnBoardingConfigEntity findByPanAndProfitCentreIsNullAndGstinIsNullAndIsActiveTrue(
			String pan);

}