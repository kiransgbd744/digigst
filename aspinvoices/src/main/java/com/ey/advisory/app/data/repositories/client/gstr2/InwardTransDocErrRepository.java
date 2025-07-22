package com.ey.advisory.app.data.repositories.client.gstr2;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InwardTransDocError;

@Repository("InwardTransDocErrRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface InwardTransDocErrRepository
		extends JpaRepository<InwardTransDocError, Long>,
		JpaSpecificationExecutor<InwardTransDocError> {
	
	public List<InwardTransDocError> findByDocHeaderId(Long id);

	
	@Query("select i FROM InwardTransDocError i WHERE i.docHeaderId "
			+ "IN (:ids) AND i.valType = :errValType")	
	public List<InwardTransDocError> findSvErrByDocHeaderIds(
			@Param("ids") List<Long> ids,
			@Param("errValType") String errValType);
	
	@Query("select o FROM InwardTransDocError o WHERE o.docHeaderId "
			+ "IN (:ids) AND o.errorType IN ('ERR','INFO') AND "
			+ "(o.valType = 'BV' OR o.valType IS NULL)")
	public List<InwardTransDocError> findByDocHeaderIdsForBV(
			@Param("ids") List<Long> ids);
}
