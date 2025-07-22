package com.ey.advisory.app.data.repositories.client.gstr7;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr7AsEnteredTdsEntity;

/**
 * 
 * @author Anand3.M
 *
 */

@Repository("Gstr7AsEnteredTdsRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr7AsEnteredTdsRepository extends CrudRepository<Gstr7AsEnteredTdsEntity, Long> {

	@Modifying
	@Query("update Gstr7AsEnteredTdsEntity set isDelete = true where tdsKey in (:tdsKeys)")
	void inactiveExistingData(@Param("tdsKeys") List<String> tdsKeys);

	@Query("SELECT entity FROM Gstr7AsEnteredTdsEntity entity WHERE entity.tdsKey "
			+ "IN (:tdsKeys) and entity.isDelete = false ")
	List<Gstr7AsEnteredTdsEntity> fetchEnteredEntiryByTdsKeys(@Param("tdsKeys") List<String> tdsKeys);

	@Modifying
	@Query("update Gstr7AsEnteredTdsEntity set isInformation = true, isError= true where id in (:asEnterIds)")
	void updateAsEnteredbyErrorAndInfo(@Param("asEnterIds") List<Long> asEnterIds);
	
	@Modifying
	@Query("update Gstr7AsEnteredTdsEntity set isInformation = true, isError= false where id in (:asEnterIds)")
	void updateAsEnteredbyProcessAndInfo(@Param("asEnterIds") List<Long> asEnterIds);
	
	@Query("SELECT doc.tdsKey FROM Gstr7AsEnteredTdsEntity doc "
			+ "WHERE doc.tdsKey IN (:docKeys) AND doc.isError = false AND doc.isDelete = false ")
	public List<String> findCancelDocsCountsByDocKeys(@Param("docKeys") List<String> docKeys);

}
