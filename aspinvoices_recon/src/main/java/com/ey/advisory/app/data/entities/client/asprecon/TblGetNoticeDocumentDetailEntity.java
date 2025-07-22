/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */

@Data   
@Entity
@Table(name = "TBL_NOTICE_DOCUMENT_DETAILS")
public class TblGetNoticeDocumentDetailEntity {

    @Id
	@SerializedName("id")
    @SequenceGenerator(name = "sequence", sequenceName = "TBL_NOTICE_DOCUMENT_DETAILS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "DOC_NAME", length = 75)
    private String docName;

    @Column(length = 100)
    private String hash;

    @Column(name = "DOC_ID", length = 11)
    private String docId;

    @Column(name = "DOC_TYPE", length = 4)
    private String docType;

    @Lob
    @Column(name = "DOC_DATA")
    private String docData; // Alternatively, use `Clob` if you want precise CLOB mapping

    @Column(name = "CONTENT_TYPE", length = 25)
    private String contentType;

    @Column(name = "ERROR_CODE", length = 25)
    private String errorCode;

    @Column(name = "ERROR_DESC", length = 500)
    private String errorDesc;

    @Column(name = "IS_MAINDOC")
    private Boolean isMainDoc;

    @EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "ND_ID", insertable = false, updatable = false)
    private TblGetNoticesEntity noticeDetail;

}