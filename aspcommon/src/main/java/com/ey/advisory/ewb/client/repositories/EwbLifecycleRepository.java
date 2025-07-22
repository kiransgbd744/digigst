/**
 * 
 */
package com.ey.advisory.ewb.client.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.ewb.client.domain.EwbLifecycleEntity;

/**
 * @author Khalid1.Khan
 *
 */
@Repository("EwbLifecycleRepository")
@Transactional(value = "clientTransactionManager", propagation = 
			Propagation.REQUIRED)
@Qualifier("EwbLifecycleRepository")
public interface EwbLifecycleRepository extends JpaRepository<EwbLifecycleEntity, Long>,
JpaSpecificationExecutor<EwbLifecycleEntity> {
	
	/*@Modifying
	@Query("UPDATE EwbLifecycleEntity ewbLc SET "
			+ "ewbLc.isDelete=:status,ewbLc.modifiedOn = :cancelDate"
			+ " WHERE ewbLc.ewbNum=:ewbNo")
	void updatePartBInfo(@Param("UpdatePartBEwbRequestDto")
	UpdatePartBEwbRequestDto req,
			@Param("UpdatePartBEwbResponseDto") 
	UpdatePartBEwbResponseDto resp);*/
	
	
	@Modifying
	@Query("UPDATE EwbLifecycleEntity ewbLc SET "
			+ "ewbLc.isActive = false, ewbLc.modifiedOn = :modifiedDate"
			+ " WHERE ewbLc.ewbNum=:ewbNum AND ewbLc.function = :function")
	void deleteRowByEwbAndFunction(@Param("function") String function,
	@Param("ewbNum") String ewbNum,
	@Param("modifiedDate") LocalDateTime modifiedDate );
	
	
	@Modifying
	@Query("select e from EwbLifecycleEntity e "
			+ " WHERE e.id in (SELECT max(id) FROM EwbLifecycleEntity"  
			+ " where ewbNum in (:ewbNumList) and function = 'VEHEWB' "
			+ "and functionStatus = false group by ewbNum)")
	List<EwbLifecycleEntity> findLatestPartBDetailsByewbNum(
			@Param("ewbNumList") List<String> ewbNumList);
	
	@Modifying
	@Query("select e from EwbLifecycleEntity e "
			+ " WHERE e.id in (SELECT max(id) FROM EwbLifecycleEntity"  
			+ " where ewbNum in (:ewbNumList) and function = 'UPDATETRANSPORTER' "
			+ "and functionStatus = false group by ewbNum)")
	List<EwbLifecycleEntity> findLatestTransporterDetailsByewbNum(
			@Param("ewbNumList") List<String> ewbNumList);

	List<EwbLifecycleEntity> findByEwbNum(String ewbNo);
	
	@Query("select ewb.vehicleUpdateDate from EwbLifecycleEntity ewb where "
			+ "ewb.ewbNum = :ewbNum AND function = 'VEHEWB' ORDER BY ID DESC")
	public List<Object> findEwbByEwbNumAndEwbFun(@Param("ewbNum") Long ewbNum);
	
	@Query("select e from EwbLifecycleEntity e "
			+ " where ewbNum in (:ewbNumList) and function = 'VEHEWB' " )
	List<EwbLifecycleEntity> findPartBDetailsByewbNum(
			@Param("ewbNumList") List<String> ewbNumList);

	List<EwbLifecycleEntity> findByEwbNumAndFunctionInAndIsActiveTrueAndFunctionStatusFalseOrderByIdDesc(
			String ewbNum, List<String> function);

}
