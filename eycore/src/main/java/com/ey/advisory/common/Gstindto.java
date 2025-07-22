package com.ey.advisory.common;

import java.io.Serializable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Gstindto implements Serializable {

	private static final long serialVersionUID = 1L;
	private String gstin;

	public Gstindto(String gstin) {
		super();
		this.gstin = gstin;
	}
}
