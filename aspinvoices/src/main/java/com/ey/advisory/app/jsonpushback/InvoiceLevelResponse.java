
package com.ey.advisory.app.jsonpushback;
import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class InvoiceLevelResponse implements Serializable
{

    @SerializedName("RecordType")
    @Expose
    private String recordType;
    @SerializedName("InvoiceNumber")
    @Expose
    private String invoiceNumber;
    @SerializedName("InvoiceStatus")
    @Expose
    private String invoiceStatus;
    @SerializedName("ErrorCd")
    @Expose
    private String errorCd;
    @SerializedName("ErrorMsg")
    @Expose
    private String errorMsg;
    @SerializedName("BatchID")
    @Expose
    private String batchID;
    @SerializedName("AdminUnit")
    @Expose
    private String adminUnit;
    @SerializedName("PrincipalOffice")
    @Expose
    private String principalOffice;
    @SerializedName("BranchOffice")
    @Expose
    private String branchOffice;
    private final static long serialVersionUID = 6182933590973509263L;

}
