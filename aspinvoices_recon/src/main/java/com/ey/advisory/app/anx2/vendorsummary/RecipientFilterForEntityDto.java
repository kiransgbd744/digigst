package com.ey.advisory.app.anx2.vendorsummary;

import java.io.Serializable;
import java.util.List;

import com.ey.advisory.gstr2.userdetails.GstinDto;
import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecipientFilterForEntityDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Expose
	private final List<GstinDto> sgstins;
	
	@Expose
	private final List<PanDto> cPans;
	
	@Expose
	private final List<GstinDto> cgstins;
	
	

}
