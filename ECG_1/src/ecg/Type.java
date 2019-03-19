/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bon
 */
public class Type {
    static final String PHILIPS = "philips";
    static final String GE = "ge";
    static final String E4L = "e4l";

    public String getPhilipstype()
    {
        return PHILIPS;
    }
    public String getGEtype()
    {
        return GE;
    }
    public String getE4Ltype()
    {
        return E4L;
    }
    
    public boolean checkFilecomplete(String path)
    {
        try {
            
            File f = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(f);
            doc.getDocumentElement().normalize();
            return true;
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            return false;
        }
    }
    public String getTypefromfile(String path)
    {
        try {
            
            File f = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(f);
            doc.getDocumentElement().normalize();
            String type = doc.getDocumentElement().getNodeName();
            System.out.println(type);
            return checkType(type);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    public String getTypefromstring(String xml)
    {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilder  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();
            //String type = doc.getDocumentElement().getNodeName();
            NodeList ecg = doc.getElementsByTagName("ecg");
            NodeList content = getSubnode(ecg, "content");
            //Node node = ((Element)content.item(0)).getChildNodes().item(1);
            Element e = (Element)content.item(0);
        String xmlcontent = e.getTextContent();
        xmlcontent = new String(Base64.decodeBase64(xmlcontent));
        
        ByteArrayInputStream contentstream = new ByteArrayInputStream(xmlcontent.getBytes());
        DocumentBuilder  contentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doccontent = contentbuilder.parse(contentstream);
        doccontent.getDocumentElement().normalize();
        String name = doccontent.getChildNodes().item(0).getNodeName();
            System.out.println(name);
            return checkType(name);
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }

    }
    private String checkType(String type)
    {
        switch (type) {
            case "restingecgdata":
                return Type.PHILIPS;
            case "sapphire":
                return Type.GE;
            case "ClinicalDocument":
                return Type.E4L;
            default:
                return "";
        }
    }
    public static void main(String[] args) throws IOException
    {
        Type t = new Type();
        //String path = "/home/bon/Documents/Project/ECG project/ECG XML/0b19c500-68f3-11e1-4823-000524cd0029.xml";
        String path = "/home/bon/Documents/Project/ECG project/E4L ECG XML/20130425150456.xml";
        
        String type = t.getTypefromfile(path);
        System.out.println(type);
        /*
        String xml = IOUtils.toString(new FileReader(path));
        xml = xml.replaceAll("�", "");
        t.getTypefromstring(xml);*/
        try {
            t.getE4L(path);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void getE4L(String path) throws ParserConfigurationException, SAXException, IOException
    {
            File f = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document doc = builder.parse(f);
            doc.getDocumentElement().normalize();

            NodeList patient = doc.getElementsByTagName("Patient");

            String HN = getAttributevalue(patient, "HN");
            System.out.println(HN);
            
            String datetime =doc.getDocumentElement().getAttributes().getNamedItem("MeasurementTime").getTextContent();
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
            Calendar c = Calendar.getInstance();
        try {
            Date date = df.parse(datetime);
            c.setTime(date);
            System.out.println(dateformat.format(date));
            System.out.println(timeformat.format(date));
            System.out.println(c.get(Calendar.YEAR));
            System.out.println(c.get(Calendar.MONTH));
            System.out.println(c.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        NodeList interpret = doc.getElementsByTagName("AutomaticTypification");
        NodeList interpretitem = getSubnode(interpret, "item");
        for(int i=0;i<interpretitem.getLength();i++)
        {
            if(i==0)
            {
                String severity = ((Element)interpretitem.item(i)).getAttribute("text");
                System.out.println(severity);
            }
            else
            {
                String interprettext = ((Element)interpretitem.item(i)).getAttribute("text");
                System.out.println(interprettext);
            }
        }
        
        NodeList measurement = doc.getElementsByTagName("GlobalMeasurement");
        NodeList measurementitem = getSubnode(measurement, "item");
        int l = measurementitem.getLength();
        String HR = ((Element)measurementitem.item(0)).getAttribute("value");
        String RR = ((Element)measurementitem.item(10)).getAttribute("value");
        String PR = ((Element)measurementitem.item(1)).getAttribute("value");
        String QRSD = ((Element)measurementitem.item(2)).getAttribute("value");
        String QT = ((Element)measurementitem.item(3)).getAttribute("value");
        String QTCB = "";
        String QTCF = "";
        String Paxis = ((Element)measurementitem.item(5)).getAttribute("value");
        String QRSaxis = ((Element)measurementitem.item(6)).getAttribute("value");
        String Taxis = ((Element)measurementitem.item(7)).getAttribute("value");

        
        NodeList wave = doc.getElementsByTagName("ECGWave");
        NodeList wavelist = getSubnode(wave, "ECGWave");
        NodeList ecgwaveform = getSubnode(wavelist, "WaveFormData");
        int[][] DATA = getECGvalue(ecgwaveform, "value");
        String dd = "";
        /*
        NodeList cfg = getSubnode(ecg, "cfg");
        NodeList reportconfiguration = getSubnode(cfg, "reportConfiguration");
        NodeList writerspeed = getSubnode(reportconfiguration, "writerSpeed");
        String paperspeed = getAttributevalue(writerspeed, "V");
        System.out.println(paperspeed);
        String speedunit = getAttributevalue(writerspeed, "U");
        System.out.println(speedunit);
        
        NodeList frontalleadgain = getSubnode(reportconfiguration, "frontalLeadGain");
        String amplification = getAttributevalue(frontalleadgain, "V");
        System.out.println(amplification);
        String amplificationunit = getAttributevalue(frontalleadgain, "U");
        System.out.println(amplificationunit);
        
        NodeList wav = getSubnode(ecg, "wav");
        NodeList ecgwaveform = getSubnode(wav, "ecgWaveform");
        int[][] data = getECGvalue(ecgwaveform, "V");
        //System.out.println(ecgwaveform.getLength());*/
    }
    /*
    private NodeList getFirstsubnode(Document doc, String parentname, String childname)
    {
        NodeList nList = doc.getElementsByTagName(parentname);
            Node nNode = nList.item(0);
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                Element eElement = (Element) nNode;
                return eElement.getElementsByTagName(childname);
            }
            else
            {
                return null;
            }
    }*/
    private NodeList getSubnode(NodeList node, String childname)
    {
        Node nNode = node.item(0);
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                Element eElement = (Element) nNode;
                return eElement.getElementsByTagName(childname);
            }
            else
            {
                return null;
            }
    }
    private String getAttributevalue(NodeList node, String value)
    {
        Node nodelist = node.item(0);
        if (nodelist.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) nodelist;     
            return element.getAttribute(value);
        }
        else
        {
            return null;
        }
    }
    private int[][] getECGvalue(NodeList node, String value)
    {
        int[][] data = new int[node.getLength()][];
        for(int i=0;i<node.getLength();i++)
        {
        Node nodelist = node.item(i);
        if (nodelist.getNodeType() == Node.ELEMENT_NODE) 
        {
            Element element = (Element) nodelist;     
            String d = element.getAttribute(value);
            String[] array = d.split(" ");
            int[] lead = new int[array.length];
            for(int j=0;j<lead.length;j++)
            {
                try
                {
                    lead[j] = Integer.parseInt(array[j]);
                }
                catch(Exception e)
                {
                    lead[j] = 0;
                }
            }
            data[i] = lead;
        }
        }
        return data;
    }
}
