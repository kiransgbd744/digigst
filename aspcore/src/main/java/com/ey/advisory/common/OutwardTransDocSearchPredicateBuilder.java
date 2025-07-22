package com.ey.advisory.common;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface OutwardTransDocSearchPredicateBuilder {
	public abstract List<Predicate> build(
			DocSearchReqDto criteria,
			Root<OutwardTransDocument> root,
			CriteriaBuilder criteriaBuilder);
}
