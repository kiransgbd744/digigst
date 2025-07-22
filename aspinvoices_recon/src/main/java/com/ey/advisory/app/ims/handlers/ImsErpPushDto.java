package com.ey.advisory.app.ims.handlers;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.springframework.stereotype.Service;

import com.ey.advisory.common.JaxbXmlFormatter;

import java.util.List;

import lombok.Data;

@Data
@XmlRootElement(name = "IM_IMS_REV")
@XmlAccessorType(XmlAccessType.FIELD)
@Service("ImsErpPushDto")
public class ImsErpPushDto implements JaxbXmlFormatter {

    @XmlElement(name = "item")
    private List<ImsItemDto> items;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ImsItemDto {

        @XmlElement(name = "ACTION_GSTN")
        private String actionGstn;

        @XmlElement(name = "TABLE_TYPE")
        private String tableType;

        @XmlElement(name = "RECIPIENT_GSTIN")
        private String recipientGstin;
        
        @XmlElement(name = "ENTITY_NAME")
        private String entityName;
        
        @XmlElement(name = "FLAG")
        private String flag;

        @XmlElement(name = "SUPPLIER_GSTIN")
        private String supplierGstin;

        @XmlElement(name = "SUPPLIERLNAME")
        private String supplierLName;

        @XmlElement(name = "SUPPLIERTNAME")
        private String supplierTName;

        @XmlElement(name = "DOCUMENTTYPE")
        private String documentType;

        @XmlElement(name = "DOCUMENTNUMBER")
        private String documentNumber;

        @XmlElement(name = "DOCUMENT_DATE")
        private String documentDate;

        @XmlElement(name = "TAXABLE_VALUE")
        private String taxableValue;

        @XmlElement(name = "IGST")
        private String igst;

        @XmlElement(name = "CGST")
        private String cgst;

        @XmlElement(name = "SGST")
        private String sgst;

        @XmlElement(name = "CESS")
        private String cess;

        @XmlElement(name = "INVOICE_VALUE")
        private String invoiceValue;

        @XmlElement(name = "POS")
        private String pos;

        @XmlElement(name = "FORM_TYPE")
        private String formType;

        @XmlElement(name = "GSTR1_FILSTATUS")
        private String gstr1FilStatus;

        @XmlElement(name = "GSTR1_FILPERIOD")
        private String gstr1FilPeriod;

        @XmlElement(name = "ORIGINALDOCNUM")
        private String originalDocNum;

        @XmlElement(name = "ORIGINALDOCDATE")
        private String originalDocDate;

        @XmlElement(name = "PENDINGACTBLOCKED")
        private String pendingActBlocked;

        @XmlElement(name = "CHECKSUM")
        private String checksum;

        @XmlElement(name = "GET_CALL_DT_TIME")
        private String getCallDateTime;

        @XmlElement(name = "ACTIVEINIMS_GSTN")
        private String activeInIMS_GSTN;
    }
}
