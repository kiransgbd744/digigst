package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9DigiComputeEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr9DigiComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9DigiComputeRepository
		extends CrudRepository<Gstr9DigiComputeEntity, Long> {

	List<Gstr9DigiComputeEntity> findByGstinAndSectionInAndRetPeriodAndIsActiveTrue(
			String gstin, List<String> section,String retPeriod);

	List<Gstr9DigiComputeEntity> findByGstinInAndRetPeriodAndIsActiveTrue(
			List<String> gstnsLists, String returnPeriod);
	
	@Query("select e.taxableValue, e.section, e.igstAmount, e.cgstAmount, "
			+ "e.sgstAmount, e.cessAmount from Gstr9DigiComputeEntity e "
			+ "where e.gstin = :gstin and e.retPeriod = :retPeriod "
			+ "and section in :section and e.isActive = true")
	List<Object[]> getPyTransInCyASPData(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod, @Param("section") List<String> section);
	
	@Query("select e.taxableValue, e.igstAmount, e.cgstAmount, "
			+ "e.sgstAmount, e.cessAmount, e.section, e.subSection from Gstr9DigiComputeEntity e "
			+ "where e.gstin = :gstin and e.financialYear = :financialYear "
			+ "and section in :section and e.isActive = true")
	List<Object[]> retrieveTaxAndGstDetails(@Param("gstin") String gstin,
			@Param("financialYear") Integer financialYear, @Param("section") List<String> section);


}
