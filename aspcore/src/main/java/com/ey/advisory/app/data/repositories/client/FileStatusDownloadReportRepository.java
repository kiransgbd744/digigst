package com.ey.advisory.app.data.repositories.client;

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

import com.ey.advisory.app.data.entities.client.FileStatusDownloadReportEntity;

@Repository("FileStatusDownloadReportRepository")
@Transactional(propagation = Propagation.REQUIRED)
public interface FileStatusDownloadReportRepository
		extends JpaRepository<FileStatusDownloadReportEntity, Long>,
		JpaSpecificationExecutor<FileStatusDownloadReportEntity> {

	@Query("Select A from FileStatusDownloadReportEntity A where"
			+ " A.createdBy = :userName")
	List<FileStatusDownloadReportEntity> findByCreatedBy(
			@Param("userName") String userName);

	@Modifying
	@Query("UPDATE FileStatusDownloadReportEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, completedOn =:completedOn"
			+ " where id  = :id")
	void updateStatus(@Param("id") Long id,
			@Param("reportStatus") String status,
			@Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn);
	
	@Modifying
	@Query("UPDATE FileStatusDownloadReportEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, completedOn =:completedOn, docId = :docId"
			+ " where id  = :id")
	void updateStatus(@Param("id") Long id,
			@Param("reportStatus") String status,
			@Param("filePath") String filePath,
			@Param("completedOn") LocalDateTime completedOn, @Param("docId") String docId);
	
	@Modifying
	@Query("UPDATE FileStatusDownloadReportEntity SET reportStatus = :reportStatus,"
			+ " completedOn =:completedOn"
			+ " where id  = :id")
	void updateStatusAndCompltdOn(@Param("id") Long id,
			@Param("reportStatus") String status,
			@Param("completedOn") LocalDateTime completedOn);
	
	@Modifying
	@Query("UPDATE FileStatusDownloadReportEntity SET reportStatus = :reportStatus,"
			+ " filePath = :filePath, upldFileName = :upldFileName, completedOn =:completedOn"
			+ " where id  = :id")
	void updateStatusWithFilename(@Param("id") Long id,
			@Param("reportStatus") String status,
			@Param("filePath") String filePath,@Param("upldFileName") String upldFileName,
			@Param("completedOn") LocalDateTime completedOn);

	
	List<FileStatusDownloadReportEntity> findAllByCreatedByAndCreatedDateBetween(String userName,
			LocalDateTime fromDate1, LocalDateTime toDate1, Pageable pageReq);

	int countByCreatedByAndCreatedDateBetween(String userName,
			LocalDateTime fromDate1, LocalDateTime toDate1);

	List<FileStatusDownloadReportEntity> findAllByCreatedByAndDataTypeInAndReportCategInAndCreatedDateBetween(
			String userName, List<String> dataType,List<String> reportCateg , LocalDateTime fromDate1,
			LocalDateTime toDate1, Pageable pageReq);

	int countByCreatedByAndDataTypeInAndReportCategInAndCreatedDateBetween(String userName,
			List<String> dataType, List<String> reportCateg, LocalDateTime fromDate1,
			LocalDateTime toDate1);
	
	List<FileStatusDownloadReportEntity> findAllByCreatedByAndDataTypeInAndCreatedDateBetween(
			String userName, List<String> dataType,LocalDateTime fromDate1,
			LocalDateTime toDate1, Pageable pageReq);

	int countByCreatedByAndDataTypeInAndCreatedDateBetween(String userName,
			List<String> dataType, LocalDateTime fromDate1,
			LocalDateTime toDate1);
	
	Optional<FileStatusDownloadReportEntity> findById(Long id);
	
	List<FileStatusDownloadReportEntity> findAllByDataTypeInAndReportCategInAndCreatedDateBetween(
			List<String> dataType,List<String> reportCateg , LocalDateTime fromDate1,
			LocalDateTime toDate1, Pageable pageReq);
	
	int countByDataTypeInAndReportCategInAndCreatedDateBetween(
			List<String> dataType, List<String> reportCateg, LocalDateTime fromDate1,
			LocalDateTime toDate1);
	
	List<FileStatusDownloadReportEntity> findAllByDataTypeInAndCreatedDateBetween(
			List<String> dataType,LocalDateTime fromDate1,
			LocalDateTime toDate1, Pageable pageReq);
	
	int countByDataTypeInAndCreatedDateBetween(List<String> dataType, LocalDateTime fromDate1,
			LocalDateTime toDate1);

	List<FileStatusDownloadReportEntity> findAllByCreatedDateBetween(
			LocalDateTime fromDate1, LocalDateTime toDate1, Pageable pageReq);

	int countByCreatedDateBetween(LocalDateTime fromDate1, LocalDateTime toDate1);
}
