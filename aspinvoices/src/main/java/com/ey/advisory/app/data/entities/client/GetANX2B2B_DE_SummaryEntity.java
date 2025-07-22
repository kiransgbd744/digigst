package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Entity
@Table(name = "GETANX2_B2B_DE_SUMMARY")
@Data
public class GetANX2B2B_DE_SummaryEntity {
	
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="ID")
	private long id;
    
    @Column(name="BATCH_ID")
	private String batchId;
    
    @Column(name="GSTIN")
	private String gstIn;
    
    @Column(name="TAX_PERIOD")
	private String taxperiod;
    
    @Column(name="CHKSUM")
	private String chksum;
    
    @Column(name="SUMMARY_TYPE")
	private String summtype;
    
    @Column(name="RETURN_SECTION")
	private String retsec;
    
    @Column(name="TOTAL_DOC")
	private int ttdoc;
    
    @Column(name="TOTAL_VALUE")
	private BigDecimal ttval;
    
    @Column(name="TOTAL_IGST")
	private BigDecimal ttigst;
    
    @Column(name="TOTAL_CGST")
	private BigDecimal ttcgst;
    
    @Column(name="TOTAL_SGST")
	private BigDecimal ttsgst;
    
    @Column(name="TOTAL_CESS")
	private BigDecimal ttcess;
    
    @Column(name="TOTAL_TAX")
	private BigDecimal tttax;
    
    @Column(name="TOTAL_TAX_VAL")
	private BigDecimal tttaxval;
    
    @Column(name="CREATED_BY")
    private String createdBy;
    
    @Column(name="CREATED_ON")
    private LocalDateTime createdOn;
    
    @Column(name="IS_DELETE")
    private boolean isdelete;
   
    @OneToMany(mappedBy = "headerId",cascade = CascadeType.ALL)
  // @JoinColumn(name = "SEC_SUMM_ID");
	 private Set<GetAnx2ActionSummaryEntity> secSumId;
    
    

}
