//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.09 at 03:53:07 PM EST 
//


package parser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;attribute name="href" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="compressflag" use="required" type="{http://www3.medical.philips.com}TYPEflag" />
 *       &lt;attribute name="compressmethod" use="required" type="{http://www3.medical.philips.com}TYPEcompress" />
 *       &lt;attribute name="durationperchannel" use="required" type="{http://www3.medical.philips.com}TYPEduration" />
 *       &lt;attribute name="nbitspersample" use="required" type="{http://www3.medical.philips.com}TYPEnbitspersample" />
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
@XmlRootElement(name = "unparsedwaveforms")
public class Unparsedwaveforms {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "href", required = true)
    protected String href;
    @XmlAttribute(name = "compressflag", required = true)
    protected TYPEflag compressflag;
    @XmlAttribute(name = "compressmethod", required = true)
    protected TYPEcompress compressmethod;
    @XmlAttribute(name = "durationperchannel", required = true)
    protected String durationperchannel;
    @XmlAttribute(name = "nbitspersample", required = true)
    protected int nbitspersample;

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
     * Gets the value of the href property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * Sets the value of the href property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Gets the value of the compressflag property.
     * 
     * @return
     *     possible object is
     *     {@link TYPEflag }
     *     
     */
    public TYPEflag getCompressflag() {
        return compressflag;
    }

    /**
     * Sets the value of the compressflag property.
     * 
     * @param value
     *     allowed object is
     *     {@link TYPEflag }
     *     
     */
    public void setCompressflag(TYPEflag value) {
        this.compressflag = value;
    }

    /**
     * Gets the value of the compressmethod property.
     * 
     * @return
     *     possible object is
     *     {@link TYPEcompress }
     *     
     */
    public TYPEcompress getCompressmethod() {
        return compressmethod;
    }

    /**
     * Sets the value of the compressmethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link TYPEcompress }
     *     
     */
    public void setCompressmethod(TYPEcompress value) {
        this.compressmethod = value;
    }

    /**
     * Gets the value of the durationperchannel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDurationperchannel() {
        return durationperchannel;
    }

    /**
     * Sets the value of the durationperchannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDurationperchannel(String value) {
        this.durationperchannel = value;
    }

    /**
     * Gets the value of the nbitspersample property.
     * 
     */
    public int getNbitspersample() {
        return nbitspersample;
    }

    /**
     * Sets the value of the nbitspersample property.
     * 
     */
    public void setNbitspersample(int value) {
        this.nbitspersample = value;
    }

}
