package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Dibyakanta.Sahoo
 *
 */
@Entity
@Table(name = "GETGSTR6_B2B_CDN_SUMMARY")
@Data
public class Getgstr6SummaryEntity {
	
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
    
    @Column(name="TABLE_SECTION")
	private String tableSection;
    
    @Column(name="RECORD_COUNT")
	private Integer recordCount;
    
    @Column(name="TOTAL_VALUE")
	private BigDecimal totalValue;
    
    @Column(name="TOTAL_TAXABLE_VALUE")
	private BigDecimal totalTaxaBleValue;
    
    @Column(name="TOTAL_IGST")
	private BigDecimal ttigst;
    
    @Column(name="TOTAL_CGST")
	private BigDecimal ttcgst;
    
    @Column(name="TOTAL_SGST")
	private BigDecimal ttsgst;
    
    @Column(name="TOTAL_CESS")
	private BigDecimal ttcess;
  
    @Column(name="CREATED_BY")
    private String createdBy;
    
    @Column(name="CREATED_ON")
    private LocalDateTime createdOn;
    
    @Column(name="MODIFIED_BY")
    private String modifiedBy;
    
    @Column(name="MODIFIED_ON")
    private LocalDateTime modifiedOn;
    
    @Column(name="DERIVED_RET_PERIOD")
	private int deriviedReturnPeriod;
    
    @Column(name="IS_DELETE")
    private boolean isdelete;
     
   /* @OneToMany(mappedBy = "headerId",cascade = CascadeType.ALL)
  	 private Set<Getgstr6CounterPartyEntity> secSumId;*/

}
