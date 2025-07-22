package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.gstr3b.Gstr3BGstinAspComputeEntity;

@Repository("Gstr3BGstinAspComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public interface Gstr3BGstinAspComputeRepository
		extends CrudRepository<Gstr3BGstinAspComputeEntity, Long> {

	@Modifying
	@Transactional
	@Query("UPDATE Gstr3BGstinAspComputeEntity set isActive=false "
			+ "WHERE taxPeriod=:taxPeriod AND gstin=:gstin AND "
			+ "subSectionName=:subSectionName AND isActive=true ")
	public void updateIdGstr3BAspCompute(@Param("taxPeriod") String taxPeriod,
			@Param("gstin") String gstin,
			@Param("subSectionName") String subSectionName);

	@Modifying
	@Transactional
	@Query("UPDATE Gstr3BGstinAspComputeEntity set isActive=false "
			+ "WHERE taxPeriod=:taxPeriod AND gstin=:gstin AND "
			+ "sectionName IN (:sectionName) AND isActive=true ")
	public void updateIdGstr3BAspComputeBySectionName(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionName") List<String> sectionName);
	
	@Query("from Gstr3BGstinAspComputeEntity WHERE "
			+ "taxPeriod = :taxPeriod AND gstin =:gstin"
			+ "  AND sectionName IN (:sectionNameList) and isActive = true ")
	List<Gstr3BGstinAspComputeEntity> findBySectionNameList(
			@Param("taxPeriod") String taxPeriod, @Param("gstin") String gstin,
			@Param("sectionNameList") List<String> sectionNameList);
}
