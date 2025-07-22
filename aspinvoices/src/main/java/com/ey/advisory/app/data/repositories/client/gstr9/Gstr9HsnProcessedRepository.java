package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnProcessEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("Gstr9HsnProcessedRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9HsnProcessedRepository
		extends CrudRepository<Gstr9HsnProcessEntity, Long> {

	@Modifying
	@Query("update Gstr9HsnProcessEntity set isDelete = true where gst9HsnDocKey in (:tdsInvKeys)")
	void updateSameInvKey(@Param("tdsInvKeys") List<String> tdsInvKeys);

	@Query("Select g from Gstr9HsnProcessEntity g where g.gstin = :gstin"
			+ " and g.fy = :fyYear  and g.tableNumber = :tableNumber and g.isDelete = false")
	List<Gstr9HsnProcessEntity> listAllGstr9ProcessedData(
			@Param("gstin") String gstin, @Param("fyYear") String fyYear,
			@Param("tableNumber") String tableNumber);

	@Query("Select g from Gstr9HsnProcessEntity g where g.gstin = :gstin"
			+ " and g.fy = :fyYear  and  g.isDelete = false and g.tableNumber in ('17','18')")
	List<Gstr9HsnProcessEntity> listAllGstr9ProcessedDetails(
			@Param("gstin") String gstin, @Param("fyYear") String fyYear);

	@Modifying
	@Query("UPDATE Gstr9HsnProcessEntity g SET g.isDelete=true,"
			+ "g.modifiedOn = CURRENT_TIMESTAMP, g.modifiedBy =:userName, "
			+ "g.updatedSource ='U' WHERE g.gstin=:gstin AND g.fy=:fy "
			+ "AND g.tableNumber=:tableNumber AND g.isDelete=false")
	void performSoftDelete(@Param("gstin") String gstin, @Param("fy") String fy,
			@Param("tableNumber") String tableNumber,
			@Param("userName") String userName);

	@Query("select count(e) from Gstr9HsnProcessEntity e where"
			+ " gstin = :gstin and retPeriod= :retPeriod and isDelete = false")
	public Long findActiveHsnCounts(@Param("gstin") String gstin,
			@Param("retPeriod") String retPeriod);

	@Modifying
	@Query("UPDATE Gstr9HsnProcessEntity g SET g.isDelete=true,"
			+ "g.modifiedOn = CURRENT_TIMESTAMP, g.modifiedBy =:userName, "
			+ "g.updatedSource ='U' WHERE g.gstin=:gstin AND g.retPeriod=:retPeriod "
			+ "AND g.tableNumber=:tableNumber AND g.isDelete=false")
	int softDeleteActiveEntries(@Param("gstin") String gstin,
			@Param("retPeriod") String fy,
			@Param("tableNumber") List<String> tableNumber,
			@Param("userName") String userName);
	
	@Modifying
	@Query("UPDATE Gstr9HsnProcessEntity g SET g.isDelete=true,"
			+ "g.modifiedOn = CURRENT_TIMESTAMP, g.modifiedBy =:userName, "
			+ "g.updatedSource ='U' WHERE g.gstin=:gstin AND g.retPeriod=:retPeriod "
			+ "AND g.tableNumber IN (:tableNumber) AND g.isDelete=false")
	int softDeleteBasedOnGstinAndFy(@Param("gstin") String gstin,
			@Param("retPeriod") String fy,
			@Param("tableNumber") List<String> tableNumber,
			@Param("userName") String userName);

}
