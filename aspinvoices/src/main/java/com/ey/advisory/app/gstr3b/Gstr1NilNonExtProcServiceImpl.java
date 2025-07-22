package com.ey.advisory.app.gstr3b;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr1.Gstr1NilExmpNonGstVerticalStatusRespDto;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Sasidhar reddy
 *
 */
@Service("Gstr1NilNonExtProcServiceImpl")
public class Gstr1NilNonExtProcServiceImpl {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Gstr1vs3bProcProcessServiceImpl.class);

    @Autowired
    @Qualifier(value = "Gstr1NilNonExtProcDaoImpl")
    private Gstr1NilNonExtProcDaoImpl gstr1NilNonExtProcDaoImpl;

    public String fetchgstr1NilNonExtProc(
            final Gstr1NilExmpNonGstVerticalStatusRespDto summaryRespDto) {
        String msg = null;
        String gstinList = summaryRespDto.getGstin();
        if (gstinList != null && !gstinList.isEmpty()) {
            int derivedRetPer = GenUtil
                    .convertTaxPeriodToInt(summaryRespDto.getTaxPeriod());

            int count = gstr1NilNonExtProcDaoImpl
                    .proceCallForComputeReversal(gstinList, derivedRetPer);
            if (count > 0) {
                msg = "Succefully updated ";
            } else {
                msg = "Invalid Gstin";
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Gstr1vs3bProcProcessServiceImpl proceCall end ");
            }
        }
        return msg;
    }

}
