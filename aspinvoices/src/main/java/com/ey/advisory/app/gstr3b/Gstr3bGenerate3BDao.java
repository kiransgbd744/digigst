package com.ey.advisory.app.gstr3b;

/**
 * @author vishal.verma
 *
 */
public interface Gstr3bGenerate3BDao {

	public String getGstnDtoList(String gstin, String taxPeriod,Long id, Long entityId);

}
