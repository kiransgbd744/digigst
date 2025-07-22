package com.ey.advisory.app.data.repositories.client;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr3bItcStatusEntity;

@Repository("Gstr3bItcStatusRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr3bItcStatusRepository
		extends CrudRepository<Gstr3bItcStatusEntity, Long> {

	@Modifying
	@Query("UPDATE Gstr3bItcStatusEntity SET status=:status "
			+ "WHERE id =:id ")
	void gstr3bItcStatusUpdate(@Param("status") String status,
			@Param("id") Long id);

	@Query("SELECT e FROM Gstr3bItcStatusEntity e WHERE e.gstin=:gstin AND "
			+ "e.deriverdRetPeriod=:deriverdRetPeriod AND e.isDelete=false ")
	Gstr3bItcStatusEntity get3btcStatus(@Param("gstin") String gstin,
			@Param("deriverdRetPeriod") Integer deriverdRetPeriod);

	@Modifying
	@Query("UPDATE Gstr3bItcStatusEntity SET isDelete=true "
			+ " WHERE gstin=:gstin AND "
			+ "deriverdRetPeriod=:deriverdRetPeriod AND isDelete=false ")
	void gstr3bItcInActiveUpdate(@Param("gstin") String gstin,
			@Param("deriverdRetPeriod") Integer deriverdRetPeriod);

	@Query("select e from Gstr3bItcStatusEntity e where " + " e.id =:id ")
	Gstr3bItcStatusEntity getDetailsById(@Param("id") Long id);

}
