package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("gstr1UserInputNilExtnOnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1UserInputNilExtnOnRepository
        extends CrudRepository<Gstr1UserInputNilExtnOnEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE Gstr1UserInputNilExtnOnEntity SET isDelete = true WHERE "
            + " isDelete = false and returnPeriod = :taxPeriod and"
            + " supplierGstin = :gstin and docKey = :docKey ")
    public void updateAllToDelete(@Param("taxPeriod") String taxPeriod,
            @Param("gstin") String gstin, @Param("docKey") String docKey);
    
    @Modifying
	@Query("UPDATE Gstr1UserInputNilExtnOnEntity doc SET doc.gstnBatchId=:gstnBatchId,"
			+ "doc.isSent=true,doc.sentToGSTNDate = CURRENT_TIMESTAMP,doc.modifiedOn = "
			+ "CURRENT_TIMESTAMP WHERE doc.returnPeriod = :retPeriod AND doc.supplierGstin = :sgstin "
			+ "AND doc.isDelete = false AND doc.id <= :userMaxId")
	void updateUserInputBatchId(@Param("gstnBatchId") Long gstnBatchId,
			@Param("retPeriod") String retPeriod,
			@Param("sgstin") String sgstin, @Param("userMaxId") Long userMaxId);
    
    @Modifying
	@Query("UPDATE Gstr1UserInputNilExtnOnEntity doc SET doc.isGstnError=true, "
			+ "doc.isSaved=false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIserrorfalg(@Param("gstnBatchId") Long gstnBatchId);
	
	@Modifying
	@Query("UPDATE Gstr1UserInputNilExtnOnEntity doc SET doc.isSaved=true, "
			+ "doc.isGstnError =false WHERE doc.gstnBatchId=:gstnBatchId")
	void updateIsSavedToGstn(@Param("gstnBatchId") Long gstnBatchId);

}
