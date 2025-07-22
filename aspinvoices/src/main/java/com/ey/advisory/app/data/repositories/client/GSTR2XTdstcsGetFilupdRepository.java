package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.data.entities.client.Gstr2xTdsTcsFileUplEntity;

import jakarta.transaction.Transactional;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Repository("GSTR2XTdstcsGetFilupdRepository")
@Transactional
public interface GSTR2XTdstcsGetFilupdRepository
		extends CrudRepository<Gstr2xTdsTcsFileUplEntity, Long> {

	
	/*@Modifying
	@Query("UPDATE Gstr2xTdsTcsFileUplEntity b SET b.userAction = (:userAction) "
			+ "WHERE b.gstin IN (:gstin) AND b.derivedRetPeriod = (:derivedRetPeriod) AND "
			+ "b.ctin IN (:ctin) AND b.recordType = (:recordType) ")
	public void UpdateId(@Param("userAction") String actionDigist,
			@Param("gstin") List<String> gstin,@Param("derivedRetPeriod") int derivedRetPeriod,
			@Param("ctin") List<String> ctin,@Param("recordType") String recordType);
	
	
	@Modifying
	@Query("UPDATE Gstr2xTdsTcsFileUplEntity b SET b.userAction = (:userAction) "
			+ "WHERE b.gstin IN (:gstin) AND b.derivedRetPeriod = (:derivedRetPeriod) AND "
			+ "b.recordType = (:recordType) ")
	public void UpdateAction(@Param("userAction") String actionDigist,
			@Param("gstin") List<String> gstin,@Param("derivedRetPeriod") int derivedRetPeriod,
			@Param("recordType") String recordType);
	*/
	
}
