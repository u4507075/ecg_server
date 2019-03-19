//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.09 at 03:53:07 PM EST 
//


package parser;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www3.medical.philips.com}ordernumber"/>
 *         &lt;element ref="{http://www3.medical.philips.com}uniqueorderid"/>
 *         &lt;element ref="{http://www3.medical.philips.com}orderbillingcode"/>
 *         &lt;element ref="{http://www3.medical.philips.com}orderremarks"/>
 *         &lt;element ref="{http://www3.medical.philips.com}reasonfororder"/>
 *         &lt;element ref="{http://www3.medical.philips.com}drgcategories"/>
 *         &lt;element ref="{http://www3.medical.philips.com}orderstatus"/>
 *         &lt;element ref="{http://www3.medical.philips.com}inbox"/>
 *       &lt;/sequence>
 *       &lt;attribute name="priority" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ordernumber",
    "uniqueorderid",
    "orderbillingcode",
    "orderremarks",
    "reasonfororder",
    "drgcategories",
    "orderstatus",
    "inbox"
})
@XmlRootElement(name = "orderinfo")
public class Orderinfo {

    @XmlElement(required = true)
    protected String ordernumber;
    @XmlElement(required = true)
    protected String uniqueorderid;
    @XmlElement(required = true)
    protected String orderbillingcode;
    @XmlElement(required = true)
    protected String orderremarks;
    @XmlElement(required = true)
    protected String reasonfororder;
    @XmlElement(required = true)
    protected Drgcategories drgcategories;
    @XmlAttribute(name = "priority", required = true)
    protected String priority;
    @XmlElement(required = true)
    protected String orderstatus;
    @XmlElement(required = true)
    protected String inbox;

    /**
     * Gets the value of the encounterid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEncounterid() {
        return ordernumber;
    }

    /**
     * Sets the value of the encounterid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEncounterid(String value) {
        this.ordernumber = value;
    }

    /**
     * Gets the value of the operatorid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatorid() {
        return uniqueorderid;
    }

    /**
     * Sets the value of the operatorid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatorid(String value) {
        this.uniqueorderid = value;
    }

    /**
     * Gets the value of the ordernumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrdernumber() {
        return ordernumber;
    }

    /**
     * Sets the value of the ordernumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrdernumber(String value) {
        this.ordernumber = value;
    }

    /**
     * Gets the value of the orderremarks property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderremarks() {
        return orderremarks;
    }

    /**
     * Sets the value of the orderremarks property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderremarks(String value) {
        this.orderremarks = value;
    }

    /**
     * Gets the value of the orderingclinicianname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderingclinicianname() {
        return reasonfororder;
    }

    /**
     * Sets the value of the orderingclinicianname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderingclinicianname(String value) {
        this.reasonfororder = value;
    }

    /**
     * Gets the value of the reasonfororder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonfororder() {
        return reasonfororder;
    }

    /**
     * Sets the value of the reasonfororder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonfororder(String value) {
        this.reasonfororder = value;
    }

    /**
     * Gets the value of the drgcategories property.
     * 
     * @return
     *     possible object is
     *     {@link Drgcategories }
     *     
     */
    public Drgcategories getDrgcategories() {
        return drgcategories;
    }

    /**
     * Sets the value of the drgcategories property.
     * 
     * @param value
     *     allowed object is
     *     {@link Drgcategories }
     *     
     */
    public void setDrgcategories(Drgcategories value) {
        this.drgcategories = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPriority(String value) {
        this.priority = value;
    }
}