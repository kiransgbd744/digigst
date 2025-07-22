package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.InwardTable3I3HExcelEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("InwardExcel3I3HRepository")
public interface InwardExcel3I3HRepository
		extends JpaRepository<InwardTable3I3HExcelEntity, Long>,
		JpaSpecificationExecutor<InwardTable3I3HExcelEntity> {

	@Query("SELECT table3h3i FROM InwardTable3I3HExcelEntity table3h3i WHERE "
			+ "table3h3i.table3h3iInvKey IN (:structuralValKeys) AND "
			+ "table3h3i.id IN (:ids) ")
	List<InwardTable3I3HExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3I3HExcelEntity inward3h3i SET "
			+ "inward3h3i.isInfo = TRUE , inward3h3i.isDelete = TRUE WHERE "
			+ "inward3h3i.table3h3iInvKey IN (:table3h3iKey) AND "
			+ "inward3h3i.id IN (:id) ")
	public void table3h3iUpdateErrorInfo(
			@Param("table3h3iKey") List<String> table3h3iKey,
			@Param("id") List<Long> ids);

	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3I3HExcelEntity inward3h3i SET "
			+ "inward3h3i.isError = TRUE WHERE inward3h3i.table3h3iInvKey "
			+ "IN (:table3h3iKey) AND inward3h3i.id IN (:ids) ")
	public void table3h3iUpdateErrors(
			@Param("table3h3iKey") List<String> table3h3iKey,
			@Param("ids") List<Long> ids);
	
	
	
	@Transactional
	@Modifying
	@Query("UPDATE InwardTable3I3HExcelEntity inward3h3i SET "
			+ "inward3h3i.isInfo = TRUE WHERE inward3h3i.table3h3iInvKey IN "
			+ "(:table3h3iKey) AND inward3h3i.id IN (:id) ")
	public void table3h3iUpdateInfo(@Param("table3h3iKey") List<String> table3h3iKey,
			@Param("id") List<Long> id);
	
	@Query("SELECT table3h3i FROM InwardTable3I3HExcelEntity table3h3i WHERE "
			+ "table3h3i.table3h3iInvKey IN (:structuralValKeys) AND "
			+ "table3h3i.id IN (:ids) AND table3h3i.isError = FALSE  ")
	List<InwardTable3I3HExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("ids") List<Long> ids);
}
