/**
 * @author kiran s
 
 
 */
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
@Table(name = "TBL_GL_DOC_TYPE_MASTER")
@Slf4j
@Data
public class GlDocTypeMasterEntity {

    @Expose
    @Id
    @SequenceGenerator(name = "glDocTypeMasterSeq", sequenceName = "TBL_GL_DOC_TYPE_MASTER_SEQ", allocationSize = 1)
    @GeneratedValue(generator = "glDocTypeMasterSeq", strategy = GenerationType.SEQUENCE)
    @Column(name = "ID")
    private Long id;

    @Expose
    @Column(name = "DOC_TYPE")
    private String docType;

    @Expose
    @Column(name = "DOC_TYPE_MS")
    private String docTypeMs;

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
    
    @Column(name = "FILE_TYPE")
    private String fileType; 
    
    @Column(name = "ERROR_CODE")
    private String errorCode;
    
    @Column(name = "ERROR_DESC")
    private String errorDesc;
    
    @Column(name = "FILE_ID")
    private String fileId;
}