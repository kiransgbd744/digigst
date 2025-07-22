package com.ey.advisory.admin.services.onboarding.gstinfileupload.docs;

import org.javatuples.Pair;
/**
 * 
 * @author Mahesh.Golla
 *
 */

public interface MasterHeaderCheckService {
    /**
    * This method checks if the header cols is as expected for the
    * uplaod format. If the headers is not as expected, then return
    * an error message. 
    **/        
    public Pair<Boolean, String> validate(Object[] headerCols);


}
