package com.ey.advisory.app.docs.dto;

import java.util.List;

import com.ey.advisory.app.docs.dto.anx1.SaveAnx1;
import com.ey.advisory.app.docs.dto.anx2.SaveAnx2;
import com.ey.advisory.app.docs.dto.gstr2x.Gstr2XDto;
import com.ey.advisory.app.docs.dto.gstr6.SaveGstr6;
import com.ey.advisory.app.docs.dto.gstr7.SaveGstr7;
import com.ey.advisory.app.docs.dto.gstr8.Gstr8SaveDto;
import com.ey.advisory.app.docs.dto.itc04.Itc04Dto;
import com.ey.advisory.app.docs.dto.ret.SaveRet;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
public class SaveBatchProcessDto {

	private List<SaveGstr1> gstr1;
	
	private List<SaveAnx1> anx1;
	
	private List<SaveAnx2> anx2;
	
	private List<SaveRet> ret;
	
	private List<List<Long>> idsList;
	
	private List<SaveGstr6> gstr6;
	
	private List<SaveGstr7> gstr7;
	
	private List<Itc04Dto> itc04;	
	
	private List<Gstr2XDto> gstr2x;	
	
	private List<Gstr8SaveDto> gstr8;
	
	private String origin;

}
