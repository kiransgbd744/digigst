package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

/**
 * 
 * @author Sasidhar
 *
 */
@Data
public class TDSProcessScreenSummaryRespDto {

    private String returnPeriod;
    private String taxPayerGSTIN;
    private String type;
    private String accNoofRecords;
    private String accTotalAmount;
    private String accIGST;
    private String accCGST;
    private String accSGST;
    private String rejNoofRecords;
    private String rejTotalAmount;
    private String rejIGST;
    private String rejCGST;
    private String rejSGST;

}
