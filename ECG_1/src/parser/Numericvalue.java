//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.09 at 03:53:07 PM EST 
//


package parser;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="changed" type="{http://www3.medical.philips.com}TYPEflag" default="False" />
 *       &lt;attribute name="ndigits" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="nprecision" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "numericvalue")
public class Numericvalue {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "changed")
    protected TYPEflag changed;
    @XmlAttribute(name = "ndigits", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger ndigits;
    @XmlAttribute(name = "nprecision", required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger nprecision;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the changed property.
     * 
     * @return
     *     possible object is
     *     {@link TYPEflag }
     *     
     */
    public TYPEflag getChanged() {
        if (changed == null) {
            return TYPEflag.FALSE;
        } else {
            return changed;
        }
    }

    /**
     * Sets the value of the changed property.
     * 
     * @param value
     *     allowed object is
     *     {@link TYPEflag }
     *     
     */
    public void setChanged(TYPEflag value) {
        this.changed = value;
    }

    /**
     * Gets the value of the ndigits property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNdigits() {
        return ndigits;
    }

    /**
     * Sets the value of the ndigits property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNdigits(BigInteger value) {
        this.ndigits = value;
    }

    /**
     * Gets the value of the nprecision property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNprecision() {
        return nprecision;
    }

    /**
     * Sets the value of the nprecision property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNprecision(BigInteger value) {
        this.nprecision = value;
    }

}
