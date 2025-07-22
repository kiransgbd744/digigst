package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Gstr1NilExmpNonGstSaveReqDto {
    private String desc;
    private String id;
    private String taxPeriod;
    private String docKey;
    private int derivedRetPeriod;
    private String gstin;
    private BigDecimal aspNilRated = BigDecimal.ZERO;
    private BigDecimal aspExempted = BigDecimal.ZERO;
    private BigDecimal aspNonGst = BigDecimal.ZERO;
    private BigDecimal usrNilRated = BigDecimal.ZERO;
    private BigDecimal usrExempted = BigDecimal.ZERO;
    private BigDecimal usrNonGst = BigDecimal.ZERO;

}
