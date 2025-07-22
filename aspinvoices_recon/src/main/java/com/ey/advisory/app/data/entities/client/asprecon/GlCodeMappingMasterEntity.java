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
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "TBL_GL_CODE_MAPPING_MASTER")
@Data
public class GlCodeMappingMasterEntity {@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BASE_HEADERS")
    private String baseHeaders;

    @Column(name = "INPUT_FILE_HEADERS")
    private String inputFileHeaders;

    @Column(name = "ENITY_ID")
    private Long entityId;

    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate;

    @Column(name = "UPDATED_DATE")
    private LocalDateTime updatedDate;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "FILE_TYPE")
    private String fileType;
    
}