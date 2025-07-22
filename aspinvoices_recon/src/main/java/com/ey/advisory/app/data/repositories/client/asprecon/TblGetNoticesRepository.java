package com.ey.advisory.app.data.repositories.client.asprecon;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.asprecon.TblGetNoticesEntity;
import com.ey.advisory.app.get.notices.handlers.GstNoticeValidatorPredicateBuilder;
import com.ey.advisory.app.get.notices.handlers.GstnNoticeReqDto;
import com.ey.advisory.common.StaticContextHolder;

import jakarta.persistence.criteria.Predicate;

@Repository("TblGetNoticesRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface TblGetNoticesRepository
		extends CrudRepository<TblGetNoticesEntity, Long>,
		JpaSpecificationExecutor<TblGetNoticesEntity>{
	

	List<TblGetNoticesEntity> findByGstinAndIsDeleteFalse(String gstin);
	
	public default List<TblGetNoticesEntity> findConsolidatedDataByParams(
			GstnNoticeReqDto qrParams) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			GstNoticeValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(GstNoticeValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		});
}
	public default Page<TblGetNoticesEntity> findConsolidatedDataByParams(
			GstnNoticeReqDto qrParams, Pageable pageRequest) {

		// findAll method will allows us to build where clause based on the
		// input params
		return findAll((root, criteriaQuery, criteriaBuilder) -> {

			GstNoticeValidatorPredicateBuilder builder = StaticContextHolder
					.getBean(GstNoticeValidatorPredicateBuilder.class);

			List<Predicate> predicates = builder
					.buildAsycnExecJobSearchPredicates(qrParams, root,
							criteriaBuilder);

			criteriaQuery
					.orderBy(criteriaBuilder.desc(root.get("id")));

			return criteriaBuilder
					.and(predicates.toArray(new Predicate[predicates.size()]));

		}, pageRequest);
}

}
