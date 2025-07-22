package com.ey.advisory.app.services.annexure1fileupload;

import org.javatuples.Pair;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Annexure11HeaderCheckService {
	public Pair<Boolean, String> validate(Object[] headerCols);

}
