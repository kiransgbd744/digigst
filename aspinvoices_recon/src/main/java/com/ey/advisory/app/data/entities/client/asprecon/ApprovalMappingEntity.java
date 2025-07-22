package com.ey.advisory.app.data.entities.client.asprecon;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "MAKER_CHECKER_MAPPING")
@Setter
@Getter
@ToString
@Component
public class ApprovalMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ENTITY_ID")
    private Long entityId;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "IS_DELETE")
    private boolean isDelete;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;
    
    @Column(name ="RETURN_TYPE")
    private String retType;
    
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

}
