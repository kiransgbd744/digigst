package com.ey.advisory.app.data.gstr1A.repositories.client;

import java.util.List;

import com.ey.advisory.app.data.gstr1A.entities.client.Gstr1AOutwardTransDocument;
import com.ey.advisory.core.dto.GstnDocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface Gstr1AGstnOutwardTransDocSearchPredicateBuilder {
	
	public abstract List<Predicate> build(
			GstnDocSearchReqDto criteria,
			Root<Gstr1AOutwardTransDocument> root,
			CriteriaBuilder criteriaBuilder);

}
