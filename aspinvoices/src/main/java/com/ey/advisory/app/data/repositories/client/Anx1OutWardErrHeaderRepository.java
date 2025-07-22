package com.ey.advisory.app.data.repositories.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.common.StaticContextHolder;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.Predicate;

@Repository("anx1OutWardErrHeaderRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface Anx1OutWardErrHeaderRepository
extends JpaRepository<Anx1OutWardErrHeader, Long>,
JpaSpecificationExecutor<Anx1OutWardErrHeader>, 
DocSearchForSaveCustomRepository, ProcedureCallRepository{

    @Query("SELECT COUNT(doc) FROM Anx1OutWardErrHeader doc "
	    + "WHERE doc.acceptanceId=:acceptanceId AND "
	    + "doc.isError='true'")
    public Integer structaralValidationCount(
	    @Param("acceptanceId") final Long fileId);
    
    public default Page<Anx1OutWardErrHeader> findDocsBySearchCriteria(
			DocSearchReqDto searchParams, Pageable pageRequest) {

		// findAll method will allow us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			OutwardTransSvErrDocSearchPredicateBuilder builder = 
					StaticContextHolder
					.getBean(
							"DefaultOutwardTransSvErrDocSearchPredicateBuilder",
							OutwardTransSvErrDocSearchPredicateBuilder.class);

			List<Predicate> predicates = builder.build(searchParams, root,
					criteriaBuilder);

			// Include the Sort Criteria, so that pagination will return
			// consistent results.
			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("createdDate")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
	}
    
    @Transactional(value = "clientTransactionManager")
	@Modifying
	@Query("UPDATE Anx1OutWardErrHeader doc SET doc.isDeleted='true',"
			+ "doc.updatedDate=:updatedDate WHERE doc.id IN (:ids)")
	void updateDocDeletion(@Param("ids") List<Long> ids,
			@Param("updatedDate") LocalDateTime updatedDate);
}
