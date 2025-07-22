package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "GSTR2A_VS_3B_STATUS")
@Data
public class Gstr2avs3bStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "BATCH_ID")
    private Long batchId;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "FROM_DERIVED_RET_PERIOD")
    private Integer deriverdRetPeriodFrom;

    @Column(name = "TO_DERIVED_RET_PERIOD")
    private Integer deriverdRetPeriodTo;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IS_DELETE")
    private boolean isDelete;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "CREATED_BY")
    private String createdBy;

}
