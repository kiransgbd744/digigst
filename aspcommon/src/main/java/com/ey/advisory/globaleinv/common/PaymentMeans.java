package com.ey.advisory.globaleinv.common;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentMeans{
    @XmlElement(name = "PaymentMeansCode") 
    public int paymentMeansCode;
    @XmlElement(name = "PaymentDueDate") 
    public String paymentDueDate;
    @XmlElement(name = "PaymentChannelCode") 
    public String paymentChannelCode;
    @XmlElement(name = "PaymentID") 
    public String paymentID;
    @XmlElement(name = "PayeeFinancialAccount") 
    public PayeeFinancialAccount payeeFinancialAccount;
}
