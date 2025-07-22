package com.ey.advisory.app.data.entities.qrcodevalidator;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class QRUploadFileStatusDTO {

    @Expose
    @SerializedName("fileId")
    private Long id;

    @Expose
    @SerializedName("uploadedBy")
    private String uploadedBy;

    @Expose
    @SerializedName("dateOfUpload")
    private LocalDate dateOfUpload;

    @Expose
    @SerializedName("fileName")
    private String fileName;

    @Expose
    @SerializedName("fileStatus")
    private String fileStatus;

    @Expose
    @SerializedName("filePath")
    private String filePath;

    @Expose
    @SerializedName("totUplDoc")
    private int totUplDoc;

    @Expose
    @SerializedName("procQr")
    private int procQr;

    @Expose
    @SerializedName("sigMisMat")
    private int sigMisMat;

    @Expose
    @SerializedName("fullMatch")
    private int fullMatch;

    @Expose
    @SerializedName("errMatch")
    private int errMatch;

    @Expose
    @SerializedName("countOfMisMat")
    private int countOfMisMat;

    @Expose
    @SerializedName("createdBy")
    private String createdBy;

    @Expose
    @SerializedName("createdOn")
    private String createdOn;  // Changed to String for formatted LocalDateTime

    @Expose
    @SerializedName("updatedOn")
    private LocalDateTime updatedOn;

    @Expose
    @SerializedName("startTime")
    private LocalDateTime startTime;

    @Expose
    @SerializedName("endTime")
    private LocalDateTime endTime;

    @Expose
    @SerializedName("updatedBy")
    private String updatedBy;

    @Expose
    @SerializedName("source")
    private String source;

    @Expose
    @SerializedName("isReverseInt")
    private Boolean isReverseInt;

    @Expose
    @SerializedName("docId")
    private String docId;

    @Expose
    @SerializedName("optionOpted")
    private String optionOpted;

    @Expose
    @SerializedName("entityId")
    private Long entityId;

    @Expose
    @SerializedName("feature")
    private String feature;

    @Expose
    @SerializedName("flagAnswer")
    private String flagAnswer;

}

