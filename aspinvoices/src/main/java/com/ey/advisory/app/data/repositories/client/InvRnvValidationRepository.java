package com.ey.advisory.app.data.repositories.client;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;



@Repository("invRnvValidationRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InvRnvValidationRepository extends 
		JpaRepository<OutwardTransDocument, Long> ,
			JpaSpecificationExecutor<OutwardTransDocument> {
	
	@Query("SELECT entity FROM OutwardTransDocument entity WHERE "
			+ "entity.sgstin = :gstin AND entity.docNo = :docNo")
	public List<OutwardTransDocument> findByOriginal(
			@Param("gstin") String gstin,
			@Param("docNo") String docNo
			);

	
/*	@Query("SELECT entity FROM OutwardTransDocument entity WHERE "
			+ "entity.sgstin = :sgstin AND entity.docNo = :docNum"
			+ "entity.docDate = :docDate AND entity.cgstin = :cgstin")
	public List<OutwardTransDocument> findByCgstin(
			@Param("sgstin")String sgstin,
			@Param("docNum")String docNum,
			@Param("docDate")LocalDate docDate,
			@Param("cgstin")String cgstin);
	
*/	//@Modifying
	/*@Query("SELECT count(doc) OutwardTransDocument doc WHERE doc.isProcessed=true "
			+ "doc.sgstin = :sgstin AND doc.docNo = :docNum AND "
			+ "doc.docDate = :docDate AND doc.cgstin = :cgstin AND doc.isSaved=FALSE")
	int findByCgstin(
			@Param("sgstin")String sgstin,
			@Param("docNum")String docNum,
			@Param("docDate")LocalDate docDate,
			@Param("cgstin")String cgstin);
*/

/*public List<OutwardTransDocument> findByDocGstin(
		@Param("sgstin")String sgstin,
		@Param("docNum")String docNum,
		@Param("docDate")LocalDate docDate,
		@Param("cgstin")String cgstin);
	*/

}
