package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.client.domain.EwbMultiVehicleEntity;

/**
 * @author Rajesh N K
 *
 */
@Repository("EwbMultiVehicleRepository")
@Transactional(value = "clientTransactionManager", propagation = Propagation.REQUIRED)
public interface EwbMultiVehicleRepository
		extends JpaRepository<EwbMultiVehicleEntity, Long>,
		JpaSpecificationExecutor<EwbMultiVehicleEntity> {

	EwbMultiVehicleEntity findByEwbNoAndGroupNo(Long ewbNo, Long groupNo);

	@Query("SELECT ewb.groupNo FROM EwbMultiVehicleEntity ewb WHERE ewb.ewbNo = :ewbno ")
	public List<Long> findByEwbNumber(@Param("ewbno") Long ewbno);

	@Query("SELECT ewb.id FROM EwbMultiVehicleEntity ewb WHERE ewb.ewbNo = :eWbno ")
	public EwbMultiVehicleEntity getIdForEwb(@Param("eWbno") Long eWbno);

	@Query("SELECT ewb.id FROM EwbMultiVehicleEntity ewb WHERE ewb.ewbNo = :eWbno ")
	public List<String> findEwbId(@Param("eWbno") Long eWbno);

	List<EwbMultiVehicleEntity> findByEwbNo(Long ewbNo);

	@Query("SELECT ewb FROM EwbMultiVehicleEntity ewb WHERE ewb.ewbNo = :ewbno and ewb.groupNo IS NOT NULL")
	public List<EwbMultiVehicleEntity> getActiveEwbNo(@Param("ewbno") Long ewbno);
	
	EwbMultiVehicleEntity findByEwbNoAndGroupNoAndErrorCodeIsNullAndErrorMessageIsNull(
			Long ewbNo, Long groupNumber);

	List<EwbMultiVehicleEntity> findByEwbNoOrderByIdDesc(Long ewbNo);
		

}
