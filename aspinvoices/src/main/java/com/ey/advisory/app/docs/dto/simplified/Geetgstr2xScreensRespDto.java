package com.ey.advisory.app.docs.dto.simplified;

import lombok.Data;

/**
 * 
 * @author Sasidhar
 *
 */
@Data
public class Geetgstr2xScreensRespDto {

    private String ReturnPeriod;
    private String TaxPayerGSTIN;
    private String Type;
    private String accNoofRecord;
    private String accTotalAmoun;
    private String accIgst;
    private String accSgst;
    private String accCgst;
    private String rejNoofRecord;
    private String rejTotalAmoun;
    private String rejIgst;
    private String rejSgst;
    private String rejCgst;
}
