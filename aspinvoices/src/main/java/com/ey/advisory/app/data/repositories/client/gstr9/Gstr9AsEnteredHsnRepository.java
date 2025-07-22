package com.ey.advisory.app.data.repositories.client.gstr9;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.gstr9.Gstr9HsnAsEnteredEntity;

/**
 * 
 * @author Anand3.M
 *
 */
@Repository("Gstr9AsEnteredHsnRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr9AsEnteredHsnRepository
		extends CrudRepository<Gstr9HsnAsEnteredEntity, Long> {

	@Modifying
	@Query("update Gstr9HsnAsEnteredEntity set isInfo = true, isError= true where id in (:asEnterIds)")
	void updateAsEnteredbyErrorAndInfo(
			@Param("asEnterIds") List<Long> asEnterIds);

	@Modifying
	@Query("update Gstr9HsnAsEnteredEntity set isInfo = true, isError= false where id in (:asEnterIds)")
	void updateAsEnteredbyProcessAndInfo(
			@Param("asEnterIds") List<Long> asEnterIds);

	@Modifying
	@Query("update Gstr9HsnAsEnteredEntity set isDelete = true where gst9DocKey in (:hsnKeys)")
	void inactiveExistingData(@Param("hsnKeys") List<String> hsnKeys);

	@Query("SELECT entity FROM Gstr9HsnAsEnteredEntity entity WHERE entity.gst9DocKey "
			+ "IN (:hsnKeys) and entity.isDelete = false ")
	List<Gstr9HsnAsEnteredEntity> fetchEnteredEntiryByTdsKeys(
			@Param("hsnKeys") List<String> hsnKeys);

}
