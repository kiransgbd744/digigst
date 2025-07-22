package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.InwardTransDocument;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface InwardTransDocSearchPredicateBuilder {
	
		public abstract List<Predicate> build(
			DocSearchReqDto criteria,
			Root<InwardTransDocument> root,
			CriteriaBuilder criteriaBuilder);

}
