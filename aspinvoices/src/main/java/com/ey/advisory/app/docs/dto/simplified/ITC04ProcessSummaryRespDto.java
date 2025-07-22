package com.ey.advisory.app.docs.dto.simplified;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class ITC04ProcessSummaryRespDto {

    private String gstin;
    private String authToken;
    private String state;
    private String regType;
    private String saveStatus;
    private LocalDateTime saveDateTime;
	private String timeStamp;
    private Integer gsCount;
    private BigDecimal gsQuantity;
    private BigDecimal gsTaxableValue;
    private Integer grCount;
    private BigInteger grQuantityRece;
    private BigInteger grQuantityLoss;
   /* private int totalCount;
    private int savedCount;
    private int errorCount;
    private int notSentCount;
    private int notSavedCount;*/
	private String taxPeriod;
    	
}
