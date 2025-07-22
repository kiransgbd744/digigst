package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.gstr1A.entities.client.OutwardTransDocErrorGstr1A;

@Repository("Gstr1ADocErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Gstr1ADocErrorRepository
		extends JpaRepository<OutwardTransDocErrorGstr1A, Long>,
		JpaSpecificationExecutor<OutwardTransDocErrorGstr1A> {
	
	public List<OutwardTransDocErrorGstr1A> findByDocHeaderId(Long id);

	/**
	 * A single query method to extract errors belonging to multiple documents.
	 * This can avoid multiple query executions if we have a bunch of 
	 * documents at hand, for which we need the associated errors.
	 * 
	 * @param ids List of document ids for which we need the errors.
	 * 
	 * @return The list of OutwardTransDocErrorGstr1A objects for the specified ids. 
	 * Ordering of the output is not guaranteed. The caller of this method 
	 * should take care of separating out the errors belonging to the 
	 * respective input document ids.
	 */
	@Query("select o FROM OutwardTransDocErrorGstr1A o WHERE o.docHeaderId "
			+ "IN (:ids) AND o.type = :valType")	
	public List<OutwardTransDocErrorGstr1A> findByDocHeaderIdsAndValType(
			@Param("ids") List<Long> ids,
			@Param("valType") String valType);
	
	
	@Query("select o FROM OutwardTransDocErrorGstr1A o WHERE o.docHeaderId "
			+ "IN (:ids) AND o.errorType IN ('ERR','INFO') AND "
			+ "(o.type = 'BV' OR o.type IS NULL)")
	public List<OutwardTransDocErrorGstr1A> findByDocHeaderIdsForBV(
			@Param("ids") List<Long> ids);

	
	
}
