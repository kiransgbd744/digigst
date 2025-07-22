package com.ey.advisory.app.data.repositories.client.asprecon;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ey.advisory.app.service.upload.way3recon.GetEwbCntrlEntity;

@Repository("GetEwbCntrlRepo")
public interface GetEwbCntrlRepo
		extends CrudRepository<GetEwbCntrlEntity, Long>,
		JpaSpecificationExecutor<GetEwbCntrlEntity> {

	@Query("SELECT doc from GetEwbCntrlEntity doc"
			+ " where doc.gstin = :gstin and doc.getCallDate = :getCallDate")
	public Optional<GetEwbCntrlEntity> isRecAvail(@Param("gstin") String gstin,
			@Param("getCallDate") LocalDate getCallDate);

	Optional<GetEwbCntrlEntity> findTop1ByGstinOrderByUpdatedOnDesc(String gstin);

	public List<GetEwbCntrlEntity> findByGstinInAndGetStatus(List<String> gstin,
			String getStatus);

	@Modifying
	@Query("UPDATE GetEwbCntrlEntity doc SET doc.updatedOn = :updatedOn,"
			+ "doc.getStatus = :getStatus,doc.errMsg = :errMsg "
			+ "WHERE doc.gstin = :gstin and doc.getCallDate = :getCallDate")
	public void updateGetCallTime(@Param("gstin") String gstin,
			@Param("getCallDate") LocalDate getCallDate,
			@Param("updatedOn") LocalDateTime updatedOn,
			@Param("errMsg") String errMsg,
			@Param("getStatus") String getStatus);

	@Query("SELECT doc from GetEwbCntrlEntity doc"
			+ " WHERE doc.gstin IN :gstinList and doc.getCallDate BETWEEN :fromDate AND :toDate ORDER by doc.getCallDate desc")
	public List<GetEwbCntrlEntity> getEwableData(
			@Param("gstinList") List<String> gstinList,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate);

	@Query("SELECT doc from GetEwbCntrlEntity doc"
			+ " WHERE doc.gstin IN :gstinList and doc.getCallDate BETWEEN :fromDate AND :toDate"
			+ " and doc.getStatus = :getStatus")
	public List<GetEwbCntrlEntity> getEwableFailedData(
			@Param("gstinList") List<String> gstinList,
			@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate,
			@Param("getStatus") String getStatus);
//	
//	@Query("SELECT doc from GetEwbCntrlEntity doc"
//			+ " WHERE doc.gstin :gstin and doc.getCallDate BETWEEN :fromDate AND :toDate")
//	public List<GetEwbCntrlEntity> getEwableData(
//			@Param("gstin") String gstins,
//			@Param("fromDate") LocalDate fromDate,
//			@Param("toDate") LocalDate toDate);
}
