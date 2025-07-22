package com.ey.advisory.app.docs.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr2Link2APRDto {

	private List<String> recipientGstins;
	private List<String> vendorGstins;
}
