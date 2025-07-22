package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetCallComputeEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr9GetCallComputeRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9GetCallComputeRepository
		extends CrudRepository<Gstr9GetCallComputeEntity, Long> {

	List<Gstr9GetCallComputeEntity> findByGstinAndFinancialYearAndSectionInAndIsActiveTrue(
			String gstin, Integer fy, List<String> section);

	@Query("SELECT doc FROM Gstr9GetCallComputeEntity doc "
			+ "WHERE doc.gstin =:gstin and doc.financialYear = :fy and "
			+ "doc.section IN (:sections) AND doc.isActive = true and "
			+ "doc.subSection NOT IN (:subsections)")
	List<Gstr9GetCallComputeEntity> findRecordsforSelectedSections(
			@Param("gstin") String gstin, @Param("fy") Integer fy,
			@Param("sections") List<String> sections,
			@Param("subsections") List<String> subsections);

	List<Gstr9GetCallComputeEntity> findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section);

	List<Gstr9GetCallComputeEntity> findByGstinAndRetPeriodAndIsActiveTrue(
			String gstin, String retPriod);

	List<Gstr9GetCallComputeEntity> findByGstinAndFinancialYearAndSectionAndIsActiveTrue(
			String gstin, Integer fy, String section);

	@Query("select e.txVal, e.section, e.igst, e.cgst, "
			+ "e.sgst, e.cess from Gstr9GetCallComputeEntity e "
			+ "where e.gstin = :gstin and e.financialYear = :financialYear "
			+ "and section in :section and e.isActive = true")
	List<Object[]> getPyTransInCyCompuData(@Param("gstin") String gstin,
			@Param("financialYear") Integer financialYear,
			@Param("section") List<String> section);
}
