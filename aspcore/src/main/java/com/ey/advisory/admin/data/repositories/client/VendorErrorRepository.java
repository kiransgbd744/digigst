package com.ey.advisory.admin.data.repositories.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.admin.data.entities.client.MasterErrorEntity;


@Repository("VendorErrorRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface VendorErrorRepository
		extends JpaRepository<MasterErrorEntity, Long>,
		JpaSpecificationExecutor<MasterErrorEntity> {

//	public List<MasterErrorEntity> findByDocHeaderId(Long id);

	/**
	 * A single query method to extract errors belonging to multiple documents.
	 * This can avoid multiple query executions if we have a bunch of documents
	 * at hand, for which we need the associated errors.
	 * 
	 * @param ids
	 *            List of document ids for which we need the errors.
	 * 
	 * @return The list of OutwardTransDocError objects for the specified ids.
	 *         Ordering of the output is not guaranteed. The caller of this
	 *         method should take care of separating out the errors belonging to
	 *         the respective input document ids.
	 */
//	@Query("select o FROM MasterErrorEntity o WHERE o.docHeaderId "
//			+ "IN (:ids)")
//	public List<MasterErrorEntity> findByDocHeaderIds(
//			@Param("ids") List<Long> ids);

}
