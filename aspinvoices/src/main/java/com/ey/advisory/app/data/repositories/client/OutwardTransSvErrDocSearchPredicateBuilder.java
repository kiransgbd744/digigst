package com.ey.advisory.app.data.repositories.client;

import java.util.List;

import com.ey.advisory.app.data.entities.client.Anx1OutWardErrHeader;
import com.ey.advisory.core.dto.DocSearchReqDto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * 
 * @author Mohana.Dasari
 *
 */
public interface OutwardTransSvErrDocSearchPredicateBuilder {
	
	public abstract List<Predicate> build(
			DocSearchReqDto criteria,
			Root<Anx1OutWardErrHeader> root,
			CriteriaBuilder criteriaBuilder);

}
