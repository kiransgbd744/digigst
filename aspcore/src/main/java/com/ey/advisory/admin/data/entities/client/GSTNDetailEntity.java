package com.ey.advisory.admin.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This Entity class is responsible for on boarding module - GSTN Detail screen
 * 
 * @author Balakrishna.S
 *
 */
@Entity
@Table(name = "GSTIN_INFO")
@Setter
@Getter
@ToString
@Component
public class GSTNDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "ENTITY_ID")
    private Long entityId;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name = "GSTIN")
    private String gstin;

    @Column(name = "REG_TYPE")
    private String registrationType;

    @Column(name = "GSTN_USRNAME")
    private String gstnUsername;

    @Column(name = "REG_EMAIL")
    private String registeredEmail;

    @Column(name = "REG_MOBILE_NUM")
    private String registeredMobileNo;

    @Column(name = "PRI_AUTH_EMAIL")
    private String primaryAuthEmail;

    @Column(name = "SEC_AUTH_EMAIL")
    private String secondaryAuthEmail;

    @Column(name = "PRI_CONTACT_EMAIL")
    private String primaryContactEmail;

    @Column(name = "SEC_CONTACT_EMAIL")
    private String secondaryContactEmail;

    @Column(name = "BANK_ACC_NUM")
    private String bankAccNum;

    @Column(name = "TURNOVER")
    private BigDecimal turnover;

    @Column(name = "IS_DELETE")
    private Boolean isDelete;

    @Column(name = "REG_DATE")
    private LocalDate regDate;

    @Column(name = "STATE_CODE")
    private String stateCode;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @ManyToOne
    @JoinColumn(name = "ERP_ID")
    private ErpInfoEntity header;

    @Column(name = "REGISTERED_NAME ")
    private String registeredName;

    @Column(name = "ADDRESS_1")
    private String address1;

    @Column(name = "ADDRESS_2")
    private String address2;

    @Column(name = "ADDRESS_3")
    private String address3;
    
    @Column(name = "LEGAL_NAME")
    private String legalName;

    @Column(name = "TRADE_NAME")
    private String tradeName;

}
