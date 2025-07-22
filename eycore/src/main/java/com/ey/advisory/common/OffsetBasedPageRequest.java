package com.ey.advisory.common;

import org.springframework.data.domain.AbstractPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest extends AbstractPageRequest {

	private static final long serialVersionUID = 1L;
	private int offset;
	private Sort sort;

	public OffsetBasedPageRequest(int offset, int limit) {
		super(offset, limit);
		this.offset = offset;
	}
	
	public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
		super(offset, limit);
		this.sort = sort;
		this.offset = offset;
	}
	
	public long getOffset() { return this.offset; }

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Pageable first() {
		return null;
	}

	@Override
	public Pageable next() {
		return null;
	}

	@Override
	public Pageable previous() {
		return null;
	}

	@Override
	public Pageable withPage(int pageNumber) {
		// TODO Auto-generated method stub
		return null;
	}
}
