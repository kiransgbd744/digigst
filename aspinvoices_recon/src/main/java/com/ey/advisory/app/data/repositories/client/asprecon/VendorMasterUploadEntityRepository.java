package com.ey.advisory.app.data.repositories.client.asprecon;

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

import com.ey.advisory.app.itcmatching.vendorupload.VendorMasterUploadEntity;

@Repository("VendorMasterUploadEntityRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorMasterUploadEntityRepository
		extends JpaRepository<VendorMasterUploadEntity, Long>,
		JpaSpecificationExecutor<VendorMasterUploadEntity> {

	VendorMasterUploadEntity findByInvoiceKeyAndIsDeleteFalse(
			String invoiceKey);

	List<VendorMasterUploadEntity> findByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
			List<String> reciepientPan, List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrueOrderByVendorGstinAsc(
			String reciepientPan, List<String> vendorGstIn);

	@Query("SELECT count(*) from VendorMasterUploadEntity where recipientPAN = :reciepientPan and vendorGstin in :vendorGstIn and isDelete=false and isVendorCom=true")
	int findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
			@Param("reciepientPan") String reciepientPan,
			@Param("vendorGstIn") List<String> vendorGstIn);

	@Query("SELECT count(*) from VendorMasterUploadEntity where recipientPAN in :reciepientPan and vendorGstin in :vendorGstIn and isDelete=false")
	int findCountByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
			@Param("reciepientPan") List<String> reciepientPan,
			@Param("vendorGstIn") List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANInAndIsDeleteFalse(
			List<String> reciepientPan, Pageable pageReq);
	
//default search count
	@Query("SELECT count(*) from VendorMasterUploadEntity where recipientPAN in :reciepientPan and isDelete=false")
	int findCountByRecipientPANInAndIsDeleteFalse(
			@Param("reciepientPan") List<String> reciepientPan);

	List<VendorMasterUploadEntity> findByVendorGstinIn(List<String> vendorGstIn,
			Pageable pageRequest);

	List<VendorMasterUploadEntity> findByVendorGstinInAndIsDeleteFalse(
			List<String> vendorGstIn);
	
	List<VendorMasterUploadEntity> findByPayloadIdAndIsDeleteFalse(
			String payloadId);
	
	@Query("SELECT v from VendorMasterUploadEntity v where v.recipientPAN in :recipientPan and v.isDelete = false")
	List<VendorMasterUploadEntity> getByRecipientPANInAndIsDeleteIsFalse(
	        @Param("recipientPan") List<String> recipientPan);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
			String reciepientPan, List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorNameInAndVendorGstinInAndIsDeleteFalseAndIsVendorComTrue(
			String reciepientPan, List<String> vendorNameList,
			List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANIn(
			List<String> vendorGstIn, Sort sort);

	List<VendorMasterUploadEntity> findByVendorGstinIn(
			List<String> reciepientGstIn, Sort sort);

	List<VendorMasterUploadEntity> findAll(Sort sort);

	//for search
	List<VendorMasterUploadEntity> findByIsDeleteFalseAndRecipientPANIn(
			List<String> recipientPANs);

	List<VendorMasterUploadEntity> findByIsDeleteFalseAndRecipientPANInAndVendorPANIn(
			List<String> recipientPANs, List<String> vendorPans);

	List<VendorMasterUploadEntity> findByIsDeleteFalseAndRecipientPANInAndVendorGstinIn(
			List<String> recipientPANs, List<String> vendorGstins);

	@Query("SELECT DISTINCT vendorGstin from VendorMasterUploadEntity where isFetched=false and isDelete=false")
	List<String> getDistinctVendorGSTINByIsFETCHED();

	@Query("select v.vendorGstin, v.vendorName"
			+ " from VendorMasterUploadEntity "
			+ " v where v.vendorGstin in :vendorGstin and isDelete = false and isVendorCom=true")
	public List<Object[]> getVendorGstinAndVendorName(
			@Param("vendorGstin") List<String> vendorGstin);

	@Query("SELECT DISTINCT vendorGstin from VendorMasterUploadEntity where isDelete=false order by vendorGstin asc")
	List<String> findVendorGstin();

	@Modifying
	@Query("UPDATE VendorMasterUploadEntity entity SET entity.isDelete=true, entity.modifiedOn = :modifiedOn where entity.isDelete=false and entity.invoiceKey in :invoiceKeys")
	public int softDeleteDuplicateInv(
			@Param("invoiceKeys") List<String> invoiceKeys,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT entity.vendorGstin  from VendorMasterUploadEntity entity where entity.isFetched=true and  entity.vendorGstin IN (:vendorGstins)")
	List<String> getFetchedTrueVendorGstins(
			@Param("vendorGstins") List<String> vendorGstins);

	// List<VendorMasterUploadEntity> findByVendorGstinInAndIsDeleteFalse(
	// List<String> vendorGSTList);

	@Query("SELECT count(*) from VendorMasterUploadEntity where vendorGstin in :vendorGstinList and isDelete=false")
	int findCountByVendorGstinInAndIsDeleteFalse(
			@Param("vendorGstinList") List<String> vendorGstinList);

	@Modifying
	@Query("UPDATE VendorMasterUploadEntity entity SET entity.isFetched=true "
			+ "where entity.isDelete=false and entity.isFetched=false and entity.vendorGstin IN (:vendorGstin)")
	public int updateisFetchedAfterPersist(
			@Param("vendorGstin") List<String> vendorGstin);

	@Query("SELECT DISTINCT v.vendorPAN FROM VendorMasterUploadEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false and v.isVendorCom = true")
	public List<String> getDistinctActiveVendorPans(
			@Param("recipientPANs") List<String> recipientPANs);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where vendorPAN in :vendorPans "
			+ "and isDelete=false and recipientPAN = :reciepientPan and isVendorCom = true")
	public List<String> findAllVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") String reciepientPan);

	List<VendorMasterUploadEntity> findByRecipientPANInAndIsDeleteFalse(
			List<String> reciepientPan);

	// List<VendorMasterUploadEntity>
	// findByRecipientPANInAndVendorGstinInAndIsDeleteFalse(
	// List<String> reciepientPan, List<String> vendorGstIn);

	@Query("SELECT DISTINCT v.vendorGstin FROM VendorMasterUploadEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false")
	public List<String> getDistinctActiveVendorGstin(
			@Param("recipientPANs") List<String> recipientPANs);

	List<VendorMasterUploadEntity> findByRecipientPANInAndIsDeleteFalseAndIsExcludeVendorTrue(
			List<String> reciepientPan);

	List<VendorMasterUploadEntity> findByVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
			List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANInAndVendorGstinInAndIsDeleteFalseAndIsExcludeVendorTrue(
			List<String> reciepientPan, List<String> vendorGstIn);

	@Query("SELECT DISTINCT v.vendorGstin FROM VendorMasterUploadEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false and v.isExcludeVendor = true")
	public List<String> getDistinctActiveExcludedVendorGstin(
			@Param("recipientPANs") List<String> recipientPANs);

	@Modifying
	@Query("UPDATE VendorMasterUploadEntity entity SET entity.isExcludeVendor=false, entity.modifiedOn = :modifiedOn "
			+ "where entity.isDelete=false and entity.recipientPAN in :reciepientPan and entity.vendorGstin in :vendorGstIn")
	public int softDeleteExcludedVendors(
			@Param("reciepientPan") List<String> reciepientPan,
			@Param("vendorGstIn") List<String> vendorGstIn,
			@Param("modifiedOn") LocalDateTime modifiedOn);

	@Query("SELECT DISTINCT v.vendorPAN FROM VendorMasterUploadEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false and v.isNonComplaintCom = true")
	public List<String> getDistinctActiveNonComplaintComVendorPans(
			@Param("recipientPANs") List<String> recipientPANs);

	@Query("SELECT DISTINCT v.vendorPAN FROM VendorMasterUploadEntity v "
			+ " WHERE v.recipientPAN in :recipientPANs and v.isDelete = false")
	public List<String> getDistinctActiveNonComplaintVendorPans(
			@Param("recipientPANs") List<String> recipientPANs);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where vendorPAN in :vendorPans "
			+ "and isDelete=false and recipientPAN = :reciepientPan")
	public List<String> findAllNonCompVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") String reciepientPan);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where vendorPAN in :vendorPans "
			+ "and isDelete=false and recipientPAN = :reciepientPan and isNonComplaintCom = true")
	public List<String> findAllNonCompComVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") String reciepientPan);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
			String reciepientPan, List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorNameInAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
			String reciepientPan, List<String> vendorNameList,
			List<String> vendorGstIn);

	List<VendorMasterUploadEntity> findByIsDeleteFalseAndRecipientPANInAndVendorGstinInAndIsNonComplaintComTrue(
			List<String> recipientPANs, List<String> vendorGstins);

	List<VendorMasterUploadEntity> findByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrueOrderByVendorGstinAsc(
			String reciepientPan, List<String> vendorGstIn);

	@Query("SELECT count(*) from VendorMasterUploadEntity where recipientPAN = :reciepientPan and vendorGstin in :vendorGstIn and isDelete=false and isNonComplaintCom=true")
	int findCountByRecipientPANAndVendorGstinInAndIsDeleteFalseAndIsNonComplaintComTrue(
			@Param("reciepientPan") String reciepientPan,
			@Param("vendorGstIn") List<String> vendorGstIn);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where "
			+ " vendorPAN in :vendorPans and isDelete=false and "
			+ " recipientPAN in :reciepientPan")
	public List<String> getAllActiveVendorGstinByVendorPans(
			@Param("vendorPans") List<String> vendorPans,
			@Param("reciepientPan") List<String> reciepientPan);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where "
			+ " vendorGstin in :vendorGstins and isDelete=false and "
			+ " recipientPAN in :reciepientPan")
	public List<String> getAllActiveVendorGstinByRecipientPanAndVendorGstins(
			@Param("vendorGstins") List<String> vendorGstins,
			@Param("reciepientPan") List<String> reciepientPan);

	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where "
			+ " isDelete =false and recipientPAN in :reciepientPan")
	public List<String> getAllActiveVendorGstinByRecipientPan(
			@Param("reciepientPan") List<String> reciepientPan);
	
	@Query("SELECT DISTINCT vendorGstin FROM VendorMasterUploadEntity where "
			+ " isDelete =false and recipientPAN in :reciepientPan and isVendorCom = true")
	public List<String> getAllVendorGstinByRecipientPan(
			@Param("reciepientPan") List<String> reciepientPan);
	//vendor new screen
	@Query("select v.vendorName"
			+ " from VendorMasterUploadEntity "
			+ " v where v.vendorGstin in :vendorGstin and isDelete = false and isVendorCom=true")
	public String getVendorName(
			@Param("vendorGstin") String vendorGstin);

}
