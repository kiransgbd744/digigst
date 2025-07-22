package com.ey.advisory.common;

import java.util.List;

import com.ey.advisory.app.data.entities.client.OutwardTransDocument;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface GstnOutwardTransDocSearchPredicateBuilder {
	
	public abstract List<Predicate> build(
			GstnDocSearchReqDto criteria,
			Root<OutwardTransDocument> root,
			CriteriaBuilder criteriaBuilder);

}
