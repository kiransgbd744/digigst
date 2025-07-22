/**
 * 
 */
package com.ey.advisory.app.service.ims;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author vishal.verma
 *
 */
@Table(name = "TBL_GETIMS_GSTN_COUNTS")
@Entity
@Data
public class ImsGstnCountEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "SECTION")
    private String section;

    @Column(name = "GOODS_TYPE")
    private String goodsType;

    @Column(name = "GSTN_TOTAL")
    private Integer gstnTotal;

    @Column(name = "GSTN_NO_ACTION")
    private Integer gstnNoAction;

    @Column(name = "GSTN_ACCEPTED")
    private Integer gstnAccepted;

    @Column(name = "GSTN_REJECTED")
    private Integer gstnRejected;

    @Column(name = "GSTN_PENDING")
    private Integer gstnPending;

    @Column(name = "IS_DELETE")
    private Boolean isDelete;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "BATCH_ID")
    private Long batchId;

}
