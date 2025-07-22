package com.ey.advisory.app.gstr1.einv;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostReconSummaryTabDto {

	private List<PostReconSummaryDto> postrecondata;
}
