package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9AutoCalculateEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr9AutoCalculateRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9AutoCalculateRepository
		extends CrudRepository<Gstr9AutoCalculateEntity, Long> {

	List<Gstr9AutoCalculateEntity> findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section);

	List<Gstr9AutoCalculateEntity> findByGstinAndRetPeriodAndSubSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section);

	@Query("select e.txPyble, e.subSection, e.txPaidCash, e.taxPaidItcIamt, e.taxPaidItcCamt, "
			+ " e.taxPaidItcSamt, e.taxPaidItcCSamt from Gstr9AutoCalculateEntity "
			+ " e where e.gstin = :gstin and e.retPeriod = :retPeriod and "
			+ " e.section = :section and e.isActive = true")
	public List<Object[]> getGstr9TaxPaidAutoCalData(
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("section") String section);

	@Query("select e.txVal, e.section, e.iamt, e.camt, "
			+ " e.samt, e.csamt from Gstr9AutoCalculateEntity "
			+ " e where e.gstin = :gstin and e.retPeriod = :retPeriod and e.section in :section and e.isActive = true")
	public List<Object[]> getGstr9PyTransInCyAutoCalData(
			@Param("gstin") String gstin, @Param("retPeriod") String retPeriod,
			@Param("section") List<String> section);

	@Modifying
	@Query("UPDATE Gstr9AutoCalculateEntity g SET g.isActive = false,g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy =:updatedBy "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.retPeriod=:retPeriod")
	public int updateActiveExistingRecords(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("updatedBy") String updatedBy);

	@Query("select e from Gstr9AutoCalculateEntity "
			+ "e where e.gstin = :gstin and e.fy = :fy and e.isActive = true")
	public List<Gstr9AutoCalculateEntity> getActiveRecordsAutoCalData(
			@Param("gstin") String gstin, @Param("fy") String fy);

	List<Gstr9AutoCalculateEntity> findByGstinInAndFyAndIsActiveTrue(
			List<String> gstnsLists, String fy);

	List<Gstr9AutoCalculateEntity> findByGstinAndRetPeriodAndSectionInAndSubSectionNotInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section,
			List<String> subSection);
}
