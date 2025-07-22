package com.ey.advisory.app.services.gstr1fileupload;

import org.javatuples.Pair;
/**
 * 
 * @author Mahesh.Golla
 *
 */
public interface Gstr1HeaderCheckService {
	
	 /**
	    * This method checks if the header cols is as expected for the
	    * uplaod format. If the headers is not as expected, then return
	    * an error message. 
	    **/        
	    public Pair<Boolean, String> validate(Object[] headerCols);
}
