package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.simplified.client.Gstr6StatusEntity;

/**
 * 
 * @author SriBhavya
 *
 */
@Repository("Gstr6StatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr6StatusRepository extends CrudRepository<Gstr6StatusEntity, Long>{

	@Modifying
	@Query("UPDATE Gstr6StatusEntity b SET b.gstinStatus =:gstinStatus,"
			+ " b.gstinTimeStamp =:gstnTimeStamp WHERE "
			+ " b.isdGstin IN :isdGstins AND "
			+ " b.currentRetPeriod =:taxPeriod AND b.isDelete=false ")
	public int UpdateStatusAndTimeStampForComputeGstn(@Param("gstinStatus") String gstinStatus,
			@Param("gstnTimeStamp") LocalDateTime gstnTimeStamp,
			@Param("isdGstins") List<String> isdGstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Modifying
	@Query("UPDATE Gstr6StatusEntity b SET b.gstinStatus =:gstinStatus "
			+ " WHERE b.isdGstin =:isdGstin AND b.gstinStatus IS NULL AND "
			+ " b.currentRetPeriod =:taxPeriod AND b.isDelete=false ")
	void UpdateStatusForComputeGstn(@Param("gstinStatus") String gstinStatus,
			@Param("isdGstin") String isdGstin,
			@Param("taxPeriod") String taxPeriod);
	
	@Query("select e from Gstr6StatusEntity e where e.currentRetPeriod =:taxPeriod "
			+ "AND e.isdGstin IN :isdGstins "
			+ "AND e.isDelete=false")
	public List<Gstr6StatusEntity> getGstinStatus(@Param("isdGstins") List<String> isdGstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Modifying
	@Query("UPDATE Gstr6StatusEntity b SET b.isDelete = true,"
			+ " b.gstinTimeStamp =:gstnTimeStamp WHERE "
			+ " b.isdGstin IN :isdGstins AND "
			+ " b.gstinStatus IS NOT NULL AND "
			+ " b.currentRetPeriod =:taxPeriod AND "
			+ " b.digiGstStatus IS NOT NULL AND "
			+ " b.isDelete=false ")
	public int softDeleteGstinDigiGstStatusNotNull(@Param("gstnTimeStamp") 
	        LocalDateTime gstnTimeStamp,
			@Param("isdGstins") List<String> isdGstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Modifying
	@Query("UPDATE Gstr6StatusEntity b SET b.isDelete = true,"
			+ " b.gstinTimeStamp =:gstnTimeStamp WHERE "
			+ " b.isdGstin IN :isdGstins AND "
			+ " b.currentRetPeriod =:taxPeriod AND "
			+ " b.isDelete=false ")
	public int softDelete(@Param("gstnTimeStamp") LocalDateTime gstnTimeStamp,
			@Param("isdGstins") List<String> isdGstins,
			@Param("taxPeriod") String taxPeriod);
	
	@Modifying
	@Query("UPDATE Gstr6StatusEntity b SET b.gstinStatus =:gstinStatus,"
			+ " b.gstinTimeStamp =:gstnTimeStamp WHERE "
			+ " b.id = :id AND b.isDelete=false ")
	public int updateStatusAndTimeStampForComputeGstnById(@Param("id") Long id, 
			@Param("gstnTimeStamp") LocalDateTime gstnTimeStamp,
			@Param("gstinStatus") String gstinStatus);
	// "b.gstinTimeStamp = CURRENT_TIMESTAMP "
	
	@Query("SELECT MAX(b.batchId) FROM Gstr6StatusEntity b")
	public  Long findMaxBatchId();

}
