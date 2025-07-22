package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;

/**
 * This class is responsible for repository operations of Document Header Entity
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr2InwardDocRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr2InwardDocRepository
		extends JpaRepository<InwardTransDocument, Long>,
		JpaSpecificationExecutor<InwardTransDocument>,
		DocSearchForSaveCustomRepository, ProcedureCallRepository {

	@Query("SELECT entity.docKey from InwardTransDocument entity "
			+ "where entity.isProcessed=true and entity.supplyType NOT IN ('CAN') "
			+ "and entity.isDeleted=false and entity.docKey IN (:docKey)")
	List<String> getActiveDocKeys(@Param("docKey") List<String> docKey);

}
