package com.ey.advisory.app.data.repositories.client.gstr9;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9UserInputEntity;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Repository("Gstr9UserInputRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9UserInputRepository
		extends JpaRepository<Gstr9UserInputEntity, Long> {

	List<Gstr9UserInputEntity> findByGstinAndRetPeriodAndSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> section);

	List<Gstr9UserInputEntity> findByGstinAndFyAndSectionInAndIsActiveTrue(
			String gstin, String fy, List<String> section);

	List<Gstr9UserInputEntity> findByGstinAndRetPeriodAndIsActiveTrue(
			String gstin, String retPriod);

	List<Gstr9UserInputEntity> findByGstinAndFyAndIsActiveTrue(String gstin,
			String fy);

	List<Gstr9UserInputEntity> findByGstinAndRetPeriodAndSubSectionInAndIsActiveTrue(
			String gstin, String retPriod, List<String> subSection);

	List<Gstr9UserInputEntity> findByGstinAndFyAndSubSectionInAndIsActiveTrue(
			String gstin, String fy, List<String> subSection);

	@Query("select count(e) from Gstr9UserInputEntity e where"
			+ " gstin = :gstin and retPeriod= :retPeriod and isActive = true")
	public Long findActiveCounts(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);

	@Query("SELECT doc.id FROM Gstr9UserInputEntity doc "
			+ "WHERE doc.docKey IN (:docKeys) AND doc.isActive = true ")
	List<Long> findActiveDocsByDocKeys(@Param("docKeys") List<String> docKeys);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity doc SET doc.isActive=false,"
			+ "doc.updatedOn =:updatedDate,doc.updatedBy =:updatedBy,doc.updatedSource ='E'  "
			+ "WHERE doc.id IN (:ids) ")
	public void updateDuplicateDocDeletionByDocKeys(
			@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate,
			@Param("updatedBy") String updatedBy);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity e SET e.isActive=false,"
			+ "e.updatedOn = CURRENT_TIMESTAMP, e.updatedBy =:updatedBy,e.updatedSource =:updatedSource"
			+ " WHERE e.gstin = :gstin AND e.retPeriod = :retPeriod AND  e.section IN (:section) AND e.isActive = true")
	public int softDeleteActiveEntries(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("section") List<String> subSection,
			@Param("updatedSource") String updatedSource,
			@Param("updatedBy") String updatedBy);

	Gstr9UserInputEntity findByGstinAndFyAndSectionAndIsActiveTrue(
			String gstin, String fy, String section);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity g SET g.isActive = false, g.updatedOn = CURRENT_TIMESTAMP,"
			+ " g.updatedBy = :updatedBy, g.updatedSource = :updatedSource "
			+ " WHERE g.gstin = :gstin and g.isActive = true and "
			+ " g.fy = :fy and g.section = :section")
	public int softDeleteBasedOnSection(@Param("gstin") String gstin,
			@Param("fy") String fy, @Param("updatedBy") String updatedBy,
			@Param("updatedSource") String updatedSource,
			@Param("section") String section);

	@Query("select e.txVal, e.section, e.igst, e.cgst, "
			+ "e.sgst, e.cess from Gstr9UserInputEntity e "
			+ "where e.gstin = :gstin and e.fy = :fy "
			+ "and section in :section and e.isActive = true")
	List<Object[]> getPyTransInCyUserInputData(@Param("gstin") String gstin,
			@Param("fy") String fy, @Param("section") List<String> section);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity g SET g.isActive = false, g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy = :updatedBy, g.updatedSource = :updatedSource "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.fy = :fy and g.section in :section")
	public int softDeleteBasedOnSectionList(@Param("gstin") String gstin,
			@Param("fy") String fy, @Param("updatedBy") String updatedBy,
			@Param("updatedSource") String updatedSource,
			@Param("section") List<String> section);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity g SET g.isActive = false, g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy = :updatedBy, g.updatedSource = :updatedSource "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.fy = :fy and g.subSection = :subSection")
	public int softDeleteBasedOnsubSection(@Param("gstin") String gstin,
			@Param("fy") String fy, @Param("updatedBy") String updatedBy,
			@Param("updatedSource") String updatedSource,
			@Param("subSection") String subSection);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity g SET g.isActive = false, g.updatedOn = CURRENT_TIMESTAMP,"
			+ "g.updatedBy = :updatedBy, g.updatedSource = :updatedSource "
			+ "WHERE g.gstin = :gstin and g.isActive = true and g.retPeriod = :retPeriod")
	public int softDeleteBasedOnGstinandRetPeriod(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod,
			@Param("updatedBy") String updatedBy,
			@Param("updatedSource") String updatedSource);

	@Modifying
	@Query("UPDATE Gstr9UserInputEntity e SET e.isActive=false,"
			+ "e.updatedOn = CURRENT_TIMESTAMP, e.updatedBy =:updatedBy,e.updatedSource =:updatedSource"
			+ " WHERE e.gstin = :gstin AND e.fy = :fy AND  e.section IN (:section) AND e.isActive = true")
	public int softDeleteBasedOnGstinandFy(@Param("gstin") String gstin,
			@Param("fy") String retPeriod,
			@Param("section") List<String> subSection,
			@Param("updatedSource") String updatedSource,
			@Param("updatedBy") String updatedBy);
}
