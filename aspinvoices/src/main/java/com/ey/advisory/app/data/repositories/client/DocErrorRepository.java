package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.OutwardTransDocError;

@Repository("DocErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface DocErrorRepository
		extends JpaRepository<OutwardTransDocError, Long>,
		JpaSpecificationExecutor<OutwardTransDocError> {
	
	public List<OutwardTransDocError> findByDocHeaderId(Long id);

	/**
	 * A single query method to extract errors belonging to multiple documents.
	 * This can avoid multiple query executions if we have a bunch of 
	 * documents at hand, for which we need the associated errors.
	 * 
	 * @param ids List of document ids for which we need the errors.
	 * 
	 * @return The list of OutwardTransDocError objects for the specified ids. 
	 * Ordering of the output is not guaranteed. The caller of this method 
	 * should take care of separating out the errors belonging to the 
	 * respective input document ids.
	 */
	@Query("select o FROM OutwardTransDocError o WHERE o.docHeaderId "
			+ "IN (:ids) AND o.type = :valType")	
	public List<OutwardTransDocError> findByDocHeaderIdsAndValType(
			@Param("ids") List<Long> ids,
			@Param("valType") String valType);
	
	
	@Query("select o FROM OutwardTransDocError o WHERE o.docHeaderId "
			+ "IN (:ids) AND o.errorType IN ('ERR','INFO') AND "
			+ "(o.type = 'BV' OR o.type IS NULL)")
	public List<OutwardTransDocError> findByDocHeaderIdsForBV(
			@Param("ids") List<Long> ids);

	
	
}
