package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.client.domain.EwbMultiVehicleDetailsEntity;

/**
 * @author Rajesh N K
 *
 */
@Repository("EwbMultiVehicleDetailsRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface EwbMultiVehicleDetailsRepository
		extends JpaRepository<EwbMultiVehicleDetailsEntity, Long>,
		JpaSpecificationExecutor<EwbMultiVehicleDetailsEntity> {

	@Query("SELECT vehicleNum,transDocNo from EwbMultiVehicleDetailsEntity"
			+ " where multiVehicleId = :multiVehicleId AND groupNo = :groupNo"
			+ " AND isDelete =false")
	List<Object[]> findActiveVehicleNumberForMultiVehicle(
			@Param("multiVehicleId") Long multiVehicleId,
			@Param("groupNo") Long groupNo);

	public EwbMultiVehicleDetailsEntity findByMultiVehicleIdAndVehicleNumAndTransDocNoAndIsDeleteFalseAndErrorCodeIsNullAndErrorMessageIsNull(
			Long multiVehicleId, String vehicleNo, String transDocNo);

	public EwbMultiVehicleDetailsEntity findByMultiVehicleIdAndVehicleNumAndTransDocNoAndIsDeleteFalse(
			Long multiVehicleId, String vehicleNo, String transDocNo);

	EwbMultiVehicleDetailsEntity findByMultiVehicleIdAndVehicleNumAndTransDocNoAndIsDeleteTrue(
			Long multiVehicleId, String vehicleNo, String transDocNo);

	@Query("SELECT vehicleNum,transDocNo,transDocDate,vehicleQty "
			+ "from EwbMultiVehicleDetailsEntity where multiVehicleId = :multiVehicleId "
			+ "AND groupNo = :groupNo AND isDelete =false")
	List<Object[]> findActiveVehicleNumber(
			@Param("multiVehicleId") Long multiVehicleId,
			@Param("groupNo") Long groupNo);

	List<EwbMultiVehicleDetailsEntity> findByMultiVehicleIdIn(List<String> id);

	public List<EwbMultiVehicleDetailsEntity> findByMultiVehicleIdAndIsDeleteFalseAndIsErrorFalseOrderByCreatedDateDesc(
			Long multiVehicleId);

	List<EwbMultiVehicleDetailsEntity> findByMultiVehicleId(Long id);

}
