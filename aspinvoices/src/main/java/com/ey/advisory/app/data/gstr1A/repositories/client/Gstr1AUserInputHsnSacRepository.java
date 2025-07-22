package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AUserInputHsnSacEntity;

/**
 * 
 * @author Shashikant.Shukla
 *
 */
@Repository("Gstr1AUserInputHsnSacRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1AUserInputHsnSacRepository
		extends CrudRepository<Gstr1AUserInputHsnSacEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr1AUserInputHsnSacEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.docKey IN (:docKey) ")
	public void UpdateId(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1AUserInputHsnSacEntity b SET b.isDelete= TRUE "
			+ "WHERE b.isDelete= FALSE AND  b.docKey IN (:docKey) ")
	public int deleteKey(@Param("docKey") List<String> docKey);

	@Query("SELECT HSN FROM Gstr1AUserInputHsnSacEntity HSN WHERE HSN.isDelete = FALSE "
			+ "AND HSN.docKey IN (:docKey)")
	Gstr1AUserInputHsnSacEntity getHsnData(@Param("docKey") String docKey);

	@Modifying
	@Query("UPDATE Gstr1AUserInputHsnSacEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP"
			+ " WHERE doc.returnPeriod = :retPeriod AND doc.sgstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :userMaxId")
	void updateUserInputBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("userMaxId") Long userMaxId);

	@Modifying
	@Query("UPDATE Gstr1AUserInputHsnSacEntity doc SET doc.isGstnError=true, "
			+ "doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);

	@Modifying
	@Query("UPDATE Gstr1AUserInputHsnSacEntity doc SET doc.isSaved=true, "
			+ "doc.isGstnError =false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

	@Query("SELECT hsn.desc FROM Gstr1AUserInputHsnSacEntity hsn WHERE hsn.isDelete = false AND hsn.docKey IN (:docKey)")
	String getHsnDescData(@Param("docKey") String docKey);

	@Query("SELECT COUNT(*) FROM Gstr1AUserInputHsnSacEntity  WHERE  "
			+ " isDelete = false and sgstin=:sgstin and returnPeriod=:returnPeriod")
	public int isHsnDataAvail(@Param("sgstin") String sgstin,
			@Param("returnPeriod") String returnPeriod);

	@Modifying
	@Query("Update Gstr1AUserInputHsnSacEntity SET isDelete = true ,softDeleteReason = 'Duplicate Records' WHERE "
			+ " docKey in (:docKeyList) AND isDelete = false")
	int updateIsDeleteFlag(@Param("docKeyList") List<String> docKeyList);
}
