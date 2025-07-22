package com.ey.advisory.app.services.gstr2fileupload;

import org.javatuples.Pair;


	public interface Gstr2HeaderCheckService {
		
		 /**
		    * This method checks if the header cols is as expected for the
		    * uplaod format. If the headers is not as expected, then return
		    * an error message. 
		    **/        
		    public Pair<Boolean, String> validate(Object[] headerCols);
	}



