package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr6UserInputEntity;

/**
 * 
 * @author Mahesh.Golla
 *
 */
@Repository("Gstr6UserInputRepo")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6UserInputRepo
		extends JpaRepository<Gstr6UserInputEntity, Long>,
		JpaSpecificationExecutor<Gstr6UserInputEntity> {

	@Modifying
	@Query("UPDATE Gstr6UserInputEntity doc SET doc.isDelete=true,"
			+ "doc.modifiedOn =:updatedDate,doc.modifiedBy=:updatedBy "
			+ "WHERE doc.id IN (:ids) ")
	public void recordsDeletionByIds(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Query("Select doc.gstin from Gstr6UserInputEntity doc where doc.gstin IN (:gstins)"
			+ " and doc.isDelete=false")
	public List<String> findByGstinDistribution(
			@Param("gstins") List<String> gstins);
	
	@Query("Select doc.id from Gstr6UserInputEntity doc where doc.gstin=:gstin"
			+ " and doc.currentRetPer=:currentRetPer and doc.isDelete=false")
	public Integer findByGstinAndTaxPeriod(
			@Param("gstin") String gstin,
			@Param("currentRetPer") String currentRetPer);
	
	@Query("Select doc.gstin from Gstr6UserInputEntity doc where doc.isdGstin=:gstin"
			+ " and doc.currentRetPer=:fromPeriod and doc.isDelete=false")
	public List<String> findByGstinsAndTaxPeriod(
			@Param("gstin") String gstin,
			@Param("fromPeriod") String fromPeriod);
	
	@Query("Select doc.gstin from Gstr6UserInputEntity doc where doc.id=:id"
			+ " and doc.isDelete=false")
	public String findByGstinsAndById(
			@Param("id") Long id);
	
	@Query("Select doc from Gstr6UserInputEntity doc where doc.isdGstin IN (:gstins)"
			+ " and doc.isDelete=false")
	public List<Gstr6UserInputEntity> findByIsdGstins(
			@Param("gstins") List<String> gstins);
	
	@Query("Select distinct doc.gstin from Gstr6UserInputEntity doc where doc.isdGstin IN (:gstins)"
			+ " and doc.isDelete=false")
	public List<String> findGstinsByIsdGstins(
			@Param("gstins") List<String> gstins);
}
