package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.RecipientMasterUploadEntity;

/**
 * @author Hema G M
 *
 */

@Repository("RecipientMasterUploadRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface RecipientMasterUploadRepository
		extends JpaRepository<RecipientMasterUploadEntity, Long>,
		JpaSpecificationExecutor<RecipientMasterUploadEntity> {

	RecipientMasterUploadEntity findByInvoiceKeyAndIsDeleteFalse(
			String invoiceKey);

	List<RecipientMasterUploadEntity> findAll(Sort sort);

	List<RecipientMasterUploadEntity> findByIsDeleteFalseAndRecipientPANIn(
			List<String> recipientPANs);

	@Modifying
	@Query("UPDATE RecipientMasterUploadEntity entity SET entity.isDelete=true, entity.modifiedOn = :modifiedOn where entity.isDelete=false and entity.invoiceKey in :invoiceKeys")
	public int softDeleteDuplicateInv(
			@Param("invoiceKeys") List<String> invoiceKeys,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	List<RecipientMasterUploadEntity> findByRecipientPANInAndIsDeleteFalse(
			List<String> recipientPanList, Pageable pageReq);

	@Query("SELECT count(*) from RecipientMasterUploadEntity where recipientPAN in :reciepientPan and isDelete=false")
	int findCountByRecipientPANInAndIsDeleteFalse(
			@Param("reciepientPan") List<String> reciepientPan);

	List<RecipientMasterUploadEntity> findByRecipientGstinInAndIsDeleteFalse(
					List<String> recipientGstinsList, Pageable pageReq);
	
	@Query("SELECT count(*) from RecipientMasterUploadEntity where recipientGstin in :recipientGstinsList and isDelete=false")
	int findCountByRecipientGstinInAndIsDeleteFalse(
			@Param("recipientGstinsList") List<String> recipientGstinsList);

	List<RecipientMasterUploadEntity> findByRecipientPANInAndRecipientGstinInAndIsDeleteFalse(
			List<String> recipientPanList, List<String> recipientGstinsList,
			Pageable pageReq);

	@Query("SELECT count(*) from RecipientMasterUploadEntity where recipientPAN in :reciepientPan and recipientGstin in :recipientGstinsList and isDelete=false")
	int findCountByRecipientPANInAndRecipientGstinInAndIsDeleteFalse(
			@Param("reciepientPan") List<String> recipientPanList,
			@Param("recipientGstinsList") List<String> recipientGstinsList);

	@Query("select DISTINCT r.recipientGstin from RecipientMasterUploadEntity r"
			+ " where r.isDelete = false and r.isGetGstr2BEmail = true"
			+ " and recipientGstin in :recipientGstins")
	List<String> findAllActive2BGstins(
			@Param("recipientGstins") List<String> recipientGstins);

	RecipientMasterUploadEntity findByRecipientGstinAndIsDeleteFalseAndIsGetGstr2BEmailTrue(
			String gstin);

	List<RecipientMasterUploadEntity> findByRecipientPANAndIsDeleteFalse(
			String pan);

	List<RecipientMasterUploadEntity> findByRecipientPANAndRecipientGstinInAndIsDeleteFalseAndIsRetCompStatusEmailTrueOrderByRecipientGstinAsc(
			String reciepientPan, List<String> reciepientGstInList, Pageable pageReq);

	@Query("SELECT count(*) from RecipientMasterUploadEntity where recipientPAN = :reciepientPan and recipientGstin in :reciepientGstIn and isDelete=false and isRetCompStatusEmail=true")
	int findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsRetCompStatusEmailTrue(
			@Param("reciepientPan") String reciepientPan,
			@Param("reciepientGstIn") List<String> reciepientGstIn);
	
	List<RecipientMasterUploadEntity> findByRecipientGstinInAndIsDeleteFalseAndIsGetGstr2BEmailTrue(
			List<String> recipientGstins);

	List<RecipientMasterUploadEntity> findByRecipientPrimEmailIdInAndRecipientGstinInAndIsDeleteFalseAndIsGetGstr2BEmailTrue(
			List<String> recipientPrimaryEmails, List<String> recipientGstins);
	
	List<RecipientMasterUploadEntity> findByRecipientGstinInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
			List<String> recipientGstinsList);
	
	List<RecipientMasterUploadEntity> findByRecipientPrimEmailIdInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
			List<String> recipientPrimaryEmails);
	
	List<RecipientMasterUploadEntity> findByRecipientPrimEmailIdAndRecipientGstinInAndIsDeleteFalseAndIsGetGstr2AEmailTrueOrderByRecipientPrimEmailId(
			String recipientPrimaryEmail, List<String> recipientGstins);

	RecipientMasterUploadEntity findByRecipientGstinAndIsDeleteFalseAndIsDRC01BEmailTrue(
			String gstin);
	
	RecipientMasterUploadEntity findByRecipientGstinAndIsDeleteFalseAndIsDRC01CEmailTrue(
			String gstin);
}
