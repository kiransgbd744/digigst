package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "LEDGER_SAVE_TO_GSTN_RCM")
@Data
@NoArgsConstructor
public class LedgerSaveToGstnRcmEntity {

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

  
    @Column(name = "ENTITYID")
    private Long entityId;

    
    @Column(name = "IGST")
    private BigDecimal igst;

   
    @Column(name = "CGST")
    private BigDecimal cgst;

    
    @Column(name = "SGST")
    private BigDecimal sgst;

    
    @Column(name = "CESS")
    private BigDecimal cess;

   
    @Column(name = "GSTIN")
    private String gstin;

    
    @Column(name = "IS_AMENDED")
    private String isAmended;

    
    @Column(name = "STATUS")
    private String status;

    
    @Column(name = "INITIATED_BY")
    private String initiatedBy;

    
    @Column(name = "INITIATED_ON")
    private LocalDateTime initiatedOn;

   
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;
    
    @Column(name = "LEDGER_TYPE")
    private String ledgerType;
    
        
    @Column(name = "COMPLETED_ON")
    private LocalDateTime completedOn;
    
    @Column(name = "ACK_NUM")
    private String ackNum;
    
    @Column(name = "ERR_MSG")
    private String errmsg;
    
    
    
    

    
}


