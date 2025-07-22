package com.ey.advisory.admin.data.repositories.client;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr1FileStatusEntity;

@Component("FileStatusRepository")
public interface FileStatusRepository
		extends JpaRepository<Gstr1FileStatusEntity, Long>,
		JpaSpecificationExecutor<Gstr1FileStatusEntity> {

	@Query("FROM Gstr1FileStatusEntity file WHERE file.fileName = :oriFileName ")
	public Gstr1FileStatusEntity getFileName(
			@Param("oriFileName") String oriFileName);

	@Query("FROM Gstr1FileStatusEntity file WHERE file.id = :id ")
	public List<Gstr1FileStatusEntity> findByIds(@Param("id") Long id);

	@Query("SELECT file.fileType, file.dataType FROM Gstr1FileStatusEntity file "
			+ "WHERE file.id = :id ")
	public List<Object[]> getTypes(@Param("id") Long id);

	@Query("SELECT file.dataType FROM Gstr1FileStatusEntity file "
			+ "WHERE file.id = :id ")
	public String getDatatype(@Param("id") Long id);

	/*
	 * @Query("Select b FROM Gstr1FileStatusEntity b WHERE b.dataType = :dataType"
	 * + " AND b.fileType = :fileType AND  b.receivedDate BETWEEN " +
	 * ":uploadFromDate AND :uploadToDate order by 1 desc")
	 */
	public List<Gstr1FileStatusEntity> findByDataTypeAndFileTypeAndUpdatedByAndReceivedDateBetween(
			@Param("dataType") String dataType,
			@Param("fileType") String filetype,
			@Param("updatedBy") String updatedBy,
			@Param("uploadFromDate") LocalDate uploadFromDate,
			@Param("uploadToDate") LocalDate uploadToDate, Sort sort);

	@Modifying
	@Query("UPDATE Gstr1FileStatusEntity SET  fileStatus = :fileStatus,"
			+ "modifiedBy = :modifiedBy,modifiedOn = :modifiedOn WHERE id = :id ")
	public void updateFileStatus(@Param("id") Long id,
			@Param("fileStatus") String fileStatus,
			@Param("modifiedBy") String modifiedBy,
			@Param("modifiedOn") LocalDateTime modifiedOn);

}
