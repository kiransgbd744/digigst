package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "TBL_GL_BUSINESS_PLACE_MASTER")
@Slf4j
@Data
public class GlBusinessPlaceMasterEntity {    @Expose
    @Id
    @SequenceGenerator(name = "glBpMasterSeq", sequenceName = "TBL_GL_BUSINESS_PLACE_MASTER_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "glBpMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Expose
    @Column(name = "BUSINESS_PLACE")
    private String businessPlace;

    @Expose
    @Column(name = "BUSINESS_AREA")
    private String businessArea;

    @Expose
    @Column(name = "PLANT_CODE")
    private String plantCode;

    @Expose
    @Column(name = "PROFIT_CENTRE")
    private String profitCentre;

    @Expose
    @Column(name = "COST_CENTRE")
    private String costCentre;

    @Expose
    @Column(name = "GSTIN")
    private String gstin;

    @Expose
    @Column(name = "ENITY_ID")
    private Long entityId;

    @Expose
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Expose
    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;
    @Expose
    
    @Column(name = "FILE_TYPE")
    private String fileType;    
    
    @Column(name = "ERROR_CODE")
    private String errorCode;
    
    @Column(name = "ERROR_DESC")
    private String errorDesc;
    
    @Column(name = "FILE_ID")
    private String fileId;
}
