package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.GlReconFileStatusEntity;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Repository("GlReconFileStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GlReconFileStatusRepository
		extends JpaRepository<GlReconFileStatusEntity, Long>,
		JpaSpecificationExecutor<GlReconFileStatusEntity> {

	@Modifying
	@Query("UPDATE GlReconFileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus, file.docId = :docId where file.id = :id ")
	public void updateFileStatus(@Param("id") Long id,
			@Param("fileStatus") String fileStatus, @Param("docId") String docId);

	@Modifying
	@Query("UPDATE GlReconFileStatusEntity file SET file.isDelete = true"
			+ " where file.fileType = :fileType ")
	public void updateFileTypeisDelete(@Param("fileType") String fileType);

	@Modifying
	@Query("UPDATE GlReconFileStatusEntity file SET file.isActive = false"
			+ " where file.fileType = :fileType ")
	public void updateFileTypeisActive(@Param("fileType") String fileType);
	
	@Query("FROM GlReconFileStatusEntity file WHERE file.docId = :docId ")
	Optional<GlReconFileStatusEntity> findByDocId(@Param("docId") String docId);
	
	public List<GlReconFileStatusEntity> findAllByFileName(String fileName);
	 
	public Optional<GlReconFileStatusEntity> findById(Long id);
	
	public List<GlReconFileStatusEntity> findAllByFileTypeInAndFileStatusInAndUpdatedOnBetween(
			List<String> fileType,List<String> source,LocalDateTime fromDate1, LocalDateTime toDate1,
			Pageable pageReq);
	 
	 
	public int countByFileTypeInAndFileStatusInAndUpdatedOnBetween(List<String> fileType,
			List<String> source,LocalDateTime fromDate1, LocalDateTime toDate1);
	
	public List<GlReconFileStatusEntity> findAllByFileTypeInAndFileStatusIn(
				List<String> fileType,List<String> status,
				Pageable pageReq);
		 
	public int countByFileTypeInAndFileStatusIn(List<String> fileType,
				List<String> source);
		 
	public List<GlReconFileStatusEntity> findByIsActiveTrue();

//multi user access
	
	List<GlReconFileStatusEntity> findAllByFileTypeInAndFileStatusInAndUpdatedOnBetweenAndCraetedBy( 
		    List<String> fileType, List<String> source, LocalDateTime fromDate1, LocalDateTime toDate1, 
		    String craetedBy, Pageable pageReq);

		int countByFileTypeInAndFileStatusInAndUpdatedOnBetweenAndCraetedBy(
		    List<String> fileType, 
		    List<String> source, 
		    LocalDateTime fromDate1, 
		    LocalDateTime toDate1,
		    String craetedBy);

	
	
        @Query("SELECT file FROM GlReconFileStatusEntity file " +
               "WHERE file.isActive = true " +
               "AND (file.fileType IN ('GL_Code_Mapping_Master_GL', 'Tax_code', 'GL_dump_mapping_file', " +
               "'Supply_Type', 'Business_Unit_code', 'Document_type') " +
               "OR (file.fileType = 'GL Dump' AND file.craetedBy = :createdBy))")
        List<GlReconFileStatusEntity> findActiveFileTypesAndGLDumpByCreatedBy(@Param("createdBy") String createdBy);
    

//condition opted for multi user access    
    @Modifying
    @Query("UPDATE GlReconFileStatusEntity file SET file.isActive = false "
            + "WHERE file.fileType = :fileType AND file.craetedBy = :createdBy")
    void updateFileTypeIsActive(@Param("fileType") String fileType, @Param("createdBy") String createdBy);
//not opted for multi user acess
    @Query("SELECT file FROM GlReconFileStatusEntity file " +
    	       "WHERE file.isActive = true " +
    	       "AND (file.fileType IN ('GL_Code_Mapping_Master_GL', 'Tax_code', 'GL_dump_mapping_file', " +
    	       "'Supply_Type', 'Business_Unit_code', 'Document_type') " +
    	       "OR file.id = (SELECT MAX(f.id) FROM GlReconFileStatusEntity f WHERE f.fileType = 'GL Dump' AND f.isActive = true))")
    	List<GlReconFileStatusEntity> findActiveFileTypesAndLatestGLDump();
    
    //sr vs gl search without any filter
    List<GlReconFileStatusEntity> findAllByFileTypeInAndFileStatusInAndCraetedBy(
            List<String> fileType, 
            List<String> status, 
            String craetedBy, 
            Pageable pageReq);
  //count--sr vs gl search without any filter
    int countByFileTypeInAndFileStatusInAndCraetedBy(
            List<String> fileType, 
            List<String> status, 
            String craetedBy);
    
	@Query("FROM GlReconFileStatusEntity file WHERE file.fileName = :oriFileName ")
	public GlReconFileStatusEntity getFileName(
			@Param("oriFileName") String oriFileName);

	@Modifying
	@Query("UPDATE GlReconFileStatusEntity file SET file.fileStatus = "
			+ ":fileStatus where file.id = :id ")
	public void updateStatus(@Param("id") Long id,
			@Param("fileStatus") String fileStatus);
	
	//gl new screen
		@Modifying
		@Query("UPDATE GlReconFileStatusEntity file SET file.fileStatus = :fileStatus, file.docId = :docId, file.errorDesc = :errorDesc WHERE file.id = :id")
		void updateFileStatusWithError(@Param("id") Long id,
		                               @Param("fileStatus") String fileStatus,
		                               @Param("docId") String docId,
		                               @Param("errorDesc") String errorDesc);
		
	@Query(" SELECT file from GlReconFileStatusEntity file where file.payloadId =:payloadId ")
	public Optional<GlReconFileStatusEntity> getFileBasedonPayloadId(@Param("payloadId") String payloadId);
	
	@Modifying
	@Query("UPDATE GlReconFileStatusEntity file SET "
			+ " file.transformationStatus =:transformationStatus, "
			+ " file.modifiedOn = CURRENT_TIMESTAMP where file.id = :id ")
	public void updateTransformationStatus(@Param("id") Long id,
			@Param("transformationStatus") String transformationStatus);
	
	@Modifying
	@Query("UPDATE GlReconFileStatusEntity f SET f.docId =:docId, "
			+ " f.modifiedOn = CURRENT_TIMESTAMP,f.fileStatus =:fileStatus where f.id = :id ")
	public void updateDocIdAndStatus(@Param("id") Long id, @Param("docId") String docId,
			@Param("fileStatus") String fileStatus);
	
	
	Optional<GlReconFileStatusEntity> findByPayloadId(String payloadId);

}
