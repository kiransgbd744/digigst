/**
 * 
 */
package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.AnxErpScenarioControlEntity;

/**
 * @author Hemasundar.J
 *
 */
@Repository("AnxErpScenarioControlRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface AnxErpScenarioControlRepository
		extends CrudRepository<AnxErpScenarioControlEntity, Long> {

	/*@Query("SELECT MAX(doc.id) FROM AnxErpScenarioControlEntity doc WHERE "
			+ "doc.gstin = :gstin AND doc.scenarioId = :scenarioId AND "
			+ "doc.destination = :destination AND doc.isDelete = false")
	public Long findMaxIdByGstnAndScenarioIdAndDestinationAndIsDeleteFalse(
			@Param("gstin") String gstin, @Param("scenarioId") Long scenarioId,
			@Param("destination") String destination);*/
	
	@Query("SELECT doc1.maxIdOut FROM AnxErpScenarioControlEntity doc1 WHERE "
			+ "doc1.id = (SELECT MAX(doc.id) FROM AnxErpScenarioControlEntity "
			+ "doc WHERE doc.gstin = :gstin AND doc.scenarioId = :scenarioId "
			+ "AND doc.isDelete = false)")
	public Long findLastScenarioMaxIdForOutward(
			@Param("gstin") String gstin, @Param("scenarioId") Long scenarioId);
	
	@Query("SELECT doc1.maxIdIn FROM AnxErpScenarioControlEntity doc1 WHERE "
			+ "doc1.id = (SELECT MAX(doc.id) FROM AnxErpScenarioControlEntity "
			+ "doc WHERE doc.gstin = :gstin AND doc.scenarioId = :scenarioId "
			+ "AND doc.isDelete = false)")
	public Long findLastScenarioMaxIdForInward(
			@Param("gstin") String gstin, @Param("scenarioId") Long scenarioId);
	
	@Query("SELECT doc.batchIdOut FROM AnxErpScenarioControlEntity doc WHERE "
			+ "doc.gstin = :gstin AND doc.scenarioId = :scenarioId AND "
			+ "doc.isDelete = false AND doc.batchIdOut =:batchIdOut")
	public Long findByBatchIdForOutward(@Param("gstin") String gstin,
			@Param("scenarioId") Long scenarioId,
			@Param("batchIdOut") Long batchIdOut);
	
	@Query("SELECT doc.batchIdIn FROM AnxErpScenarioControlEntity doc WHERE "
			+ "doc.gstin = :gstin AND doc.scenarioId = :scenarioId AND "
			+ "doc.isDelete = false AND doc.batchIdIn =:batchIdIn")
	public Long findByBatchIdForInward(@Param("gstin") String gstin,
			@Param("scenarioId") Long scenarioId,
			@Param("batchIdIn") Long batchIdIn);

}
