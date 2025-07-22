package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9GetSummaryEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr9GetSummaryRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9GetSummaryRepository
		extends CrudRepository<Gstr9GetSummaryEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr9GetSummaryEntity g SET g.isActive = false,g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy =:updatedBy "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.retPeriod=:retPeriod")
	public int updateActiveExistingRecords(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("updatedBy") String updatedBy);

	List<Gstr9GetSummaryEntity> findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section);

	@Query("select e.txPyble, e.subSection, e.txpaidCash, e.taxPaidItcIamt, "
			+ " e.taxPaidItcCamt, e.taxPaidItcSamt, e.taxPaidItcCSamt from "
			+ " Gstr9GetSummaryEntity e where e.gstin = :gstin and e.retPeriod = :retPeriod"
			+ " and e.section = :section and e.isActive = true")
	public List<Object[]> getGstr9TaxPaidGetSummData(
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("section") String section);

	@Query("select e.txVal, e.section, e.iamt, e.camt, "
			+ "e.samt, e.csamt from Gstr9GetSummaryEntity e "
			+ "where e.gstin = :gstin and e.retPeriod = :retPeriod "
			+ "and section in :section and e.isActive = true")
	List<Object[]> getPyTransInCySummData(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("section") List<String> section);

	@Query("Select g from Gstr9GetSummaryEntity g where g.gstin = :gstin"
			+ " and g.fy = :fyYear  and  g.isActive = true and g.section in ('17','18')")
	public List<Gstr9GetSummaryEntity> listAllGstr9SummaryData(
			@Param("gstin") String gstin, @Param("fyYear") String fyYear);
	
	List<Gstr9GetSummaryEntity> findByGstinAndFyAndIsActiveTrue(String gstin,
			String fy);
	
	@Query("Select g from Gstr9GetSummaryEntity g where g.gstin = :gstin"
			+ " and g.fy = :fyYear  and  g.isActive = false and g.section in ('17','18')")
	List<Gstr9GetSummaryEntity> listAllGstr9ProcessedDetailsGet(
			@Param("gstin") String gstin, @Param("fyYear") String fyYear);

}
