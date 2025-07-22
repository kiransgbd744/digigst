package com.ey.advisory.app.docs.dto.gstr1;

import java.math.BigDecimal;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Data
public class Gstr1NilExmpNonGstSummaryStatusRespDto {
    private String type;
    private int count = 0;
    private BigDecimal nilAmount = BigDecimal.ZERO;
    private BigDecimal extAmount = BigDecimal.ZERO;
    private BigDecimal nonAmount = BigDecimal.ZERO;
    private String order;
}
