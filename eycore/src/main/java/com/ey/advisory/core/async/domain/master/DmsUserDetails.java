/**
 * 
 */
package com.ey.advisory.core.async.domain.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Data;


/**
 * @author vishal.verma
 *
 */

@Entity
@Table(name = "DMS_USER_DETAILS")
@Data
public class DmsUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GROUP_CODE")
    private String groupCode;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "JWT_TOKEN")
    private String jwtToken;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "TOKEN_GEN_TIME")
    private LocalDateTime tokenGenTime;

    @Column(name = "TOKEN_EXP_TIME")
    private LocalDateTime tokenExpTime;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;
}

