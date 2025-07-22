package com.ey.advisory.app.gstr2b;

/**
 * @author vishal.verma
 *
 */
public interface Gstr2RespParser {

	public void pasrseResp(boolean isSingleProcess, Gstr2BGETDataDto reqDto,
			Long id);

}
