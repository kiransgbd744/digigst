package com.ey.advisory.app.docs.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data

public class GlReconDumpAPIListDto {

	private List<GlDumpAPIPushListDto> glData = new ArrayList<>();

}