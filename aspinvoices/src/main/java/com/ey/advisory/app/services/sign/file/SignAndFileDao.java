/**
 * 
 */
package com.ey.advisory.app.services.sign.file;

/**
 * @author Hemasundar.J
 *
 */
public interface SignAndFileDao {

	void updateGstr1Tables(String taxperiod, String gstin, String groupCode);
}
