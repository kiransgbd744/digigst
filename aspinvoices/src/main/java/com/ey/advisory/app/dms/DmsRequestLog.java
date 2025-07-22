/**
 * 
 */
package com.ey.advisory.app.dms;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author vishal.verma
 *
 */

@Data
@Entity
@Table(name = "DMS_REQUEST_LOG")
public class DmsRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "JOB_CATEG")
    private String jobCategory;

    @Lob
    @Column(name = "EXEC_FILE_RESP")
    private String execFileResponse;

    @Lob
    @Column(name = "CALLBACK_RESP")
    private String callbackResponse;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "UPDATED_ON")
    private LocalDateTime updatedOn;

}

