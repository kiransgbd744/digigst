package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.Gstr6DistributionExcelEntity;

import jakarta.transaction.Transactional;
/**
 * 
 * @author Balakrishna.S
 *
 */


@Service("Gstr6DistributionExcelRepository")
public interface Gstr6DistributionExcelRepository
		extends JpaRepository<Gstr6DistributionExcelEntity, Long>,
		JpaSpecificationExecutor<Gstr6DistributionExcelEntity> {

	
	@Query("SELECT gstr6 FROM Gstr6DistributionExcelEntity gstr6 WHERE gstr6.processKey "
			+ "IN (:structuralValKeys) AND gstr6.id IN (:id) ")
	List<Gstr6DistributionExcelEntity> getAllExcelData(
			@Param("structuralValKeys") List<String> processKey,
			@Param("id") List<Long> id);

	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionExcelEntity B2C SET B2C.isError = TRUE "
			+ "WHERE B2C.processKey IN (:b2cKey) AND B2C.id IN (:ids) ")
	public void b2cUpdateStructuralError(@Param("b2cKey") List<String> b2cKey,
			@Param("ids") List<Long> ids);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionExcelEntity B2C SET B2C.isInfo = TRUE ,"
			+ "B2C.isDelete = TRUE WHERE B2C.processKey IN (:b2cKey) "
			+ "AND B2C.id IN (:id) ")
	public void b2cUpdateErrorInfo(@Param("b2cKey") List<String> b2cKey,
			@Param("id") List<Long> ids);

	
	@Query("SELECT B2C FROM Gstr6DistributionExcelEntity B2C WHERE B2C.processKey "
			+ "IN (:structuralValKeys) AND B2C.id IN (:id)  AND "
			+ "B2C.isError = FALSE ")
	List<Gstr6DistributionExcelEntity> getAllProcessedWithInfoData(
			@Param("structuralValKeys") List<String> structuralValKeys,
			@Param("id") List<Long> id);
	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionExcelEntity B2C SET B2C.isInfo = TRUE "
			+ "WHERE B2C.processKey IN (:b2cKey) AND B2C.id IN (:id) ")
	public void b2cUpdateInfo(@Param("b2cKey") List<String> b2cKey,
			@Param("id") List<Long> id);
	
	
	@Query("SELECT doc.processKey FROM Gstr6DistributionExcelEntity doc "
			+ "WHERE doc.processKey IN (:docKeys) AND doc.isError = false AND doc.isDelete = false ")
	public List<String> findCancelDocsCountsByDocKeys(@Param("docKeys") List<String> docKeys);
	
	@Transactional
	@Modifying
	@Query("UPDATE Gstr6DistributionExcelEntity b SET b.isDelete = TRUE "
			+ "WHERE b.processKey IN (:processKey) ")
	public void updateSameInvKey(@Param("processKey") List<String> processKey);
	
	
}
