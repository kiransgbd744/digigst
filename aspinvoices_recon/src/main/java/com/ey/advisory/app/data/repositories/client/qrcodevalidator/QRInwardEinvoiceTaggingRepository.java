package com.ey.advisory.app.data.repositories.client.qrcodevalidator;

import com.ey.advisory.app.data.entities.qrcodevalidator.QRInwardEinvoiceTaggingEntity;
import com.ey.advisory.app.data.entities.qrcodevalidator.QRResponseLogEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("QRInwardEinvoiceTaggingRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface QRInwardEinvoiceTaggingRepository
		extends JpaRepository<QRInwardEinvoiceTaggingEntity, Long> {
	
	
    @Modifying
    @Query("UPDATE QRInwardEinvoiceTaggingEntity e SET e.isDelete = true WHERE e.irn in (:irn)")
    public void updateIsDeleteStatus(@Param("irn") List<String> irn);

    //jpa query to get the count of records where is delete = false and isTagged = false
    @Query("SELECT COUNT(e) FROM QRInwardEinvoiceTaggingEntity e WHERE e.isDelete = false AND e.isTagged = false")
    Long getActiveCount();

//jpa query to get the records where is delete = false and isTagged = false
@Query("SELECT e FROM QRInwardEinvoiceTaggingEntity e WHERE e.isDelete = false AND e.isTagged = false")
List<QRInwardEinvoiceTaggingEntity> getActiveRecords();


}