/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import filter.FIRfilter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.*;

/**
 *
 * @author bon
 */
public class ECGvalue {
    static String HN = "";
    static String RECORDDATETIME = "";
    static String RECORDDATE = "";
    static String RECORDTIME = "";
    static String SEVERITY = "";
    static String Rate = "";
    static String RR = "";
    static String PR = "";
    static String QRSD = "";
    static String QT = "";
    static String QTCB = "";
    static String QTCF = "";
    static String Paxis = "";
    static String QRSaxis = "";
    static String Taxis = "";
    static String Qonset = "";
    static String Qoffset = "";
    static String SV1 = "";
    static String RV5 = "";
    
    
    static java.util.List<Statement> STATEMENT;
    static String SIGNATURE = "";
    static String PAPERSPEED = "";
    static String SPEEDUNIT = "";
    static String AMPLIFICATION = "";
    static String AMPLIFICATIONUNIT = "";
    static int AGEYEAR = 0;
    static int AGEMONTH = 0;
    static int AGEDAY = 0;
    
    static int[][] DATA;
    static int[][] DATAFIRFILTER;
    static int[][] COEF;
    
    public void readPhilipsfromfile(String filepath) throws IOException, JAXBException
    {
        SierraEcgFiles sierra = new SierraEcgFiles();
        sierra.preprocess(new File(filepath));
        Restingecgdata ecg = sierra.getRestingecgdata();
        
        getECGPhilipsvalue(sierra, ecg);
        
    }
    public void readPhilipsfromstring(String string) throws IOException, JAXBException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
        DocumentBuilder  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(stream); 
        doc.getDocumentElement().normalize();
        String xml = getContent(doc);
        SierraEcgFiles sierra = new SierraEcgFiles();
        sierra.preprocess(xml);
        Restingecgdata ecg = sierra.getRestingecgdata();
        
        getECGPhilipsvalue(sierra, ecg);
    }
    private void getECGPhilipsvalue(SierraEcgFiles sierra, Restingecgdata ecg)
    {
        HN = ecg.getPatient().getGeneralpatientdata().getPatientid();
        RECORDDATE = ""+ecg.getReportinfo().getDate();
        RECORDTIME = ""+ecg.getReportinfo().getTime();
        RECORDDATETIME = RECORDDATE +" "+ RECORDTIME;
        SEVERITY = ecg.getInterpretations().getInterpretation().get(0).getSeverity().getValue();
        
        Globalmeasurements me = ecg.getInterpretations().getInterpretation().get(0).getGlobalmeasurements();
        Rate = me.getHeartrate().getValue();
        RR = me.getRRint().getValue();
        PR = me.getPRint().getValue();
        QRSD = me.getQRSduration().getValue();
        QT = me.getQTint().getValue();
        QTCB = me.getQTCB().getValue();
        QTCF = me.getQTCF().getValue();
        Paxis = me.getPfrontaxis().getValue();
        QRSaxis = me.getQrsfrontaxis().getValue();
        Taxis = me.getTfrontaxis().getValue();
        
        STATEMENT = ecg.getInterpretations().getInterpretation().get(0).getStatement();
        SIGNATURE = ecg.getInterpretations().getInterpretation().get(0).getMdsignatureline();
        
        PAPERSPEED = ""+ecg.getReportinfo().getReportgain().getTimegain().getValue();
        SPEEDUNIT = ecg.getReportinfo().getReportgain().getTimegain().getUnit();
        AMPLIFICATION  = ""+ecg.getReportinfo().getReportgain().getAmplitudegain().getOverallgain();
        AMPLIFICATIONUNIT = ecg.getReportinfo().getReportgain().getAmplitudegain().getUnit();
        
        AGEYEAR = ecg.getReportinfo().getDate().getYear();
        AGEMONTH = ecg.getReportinfo().getDate().getMonth();
        AGEDAY = ecg.getReportinfo().getDate().getDay();
        
        DecodedLead[] leads = sierra.getLeads();
        DATA = new int[leads.length][];
        
        for(int i=0;i<DATA.length;i++)
        {
            DATA[i] = leads[i].getData();
        }
    }
    public void readGEfromfile(String filepath) throws IOException, JAXBException, ParserConfigurationException, SAXException
    {
        File f = new File(filepath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document doc = builder.parse(f);
        doc.getDocumentElement().normalize();
        getGEvalue(doc.getElementsByTagName("sapphire"));
    }
    public void readGEfromstring(String string) throws IOException, JAXBException, ParserConfigurationException, SAXException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
        DocumentBuilder  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList ECG = doc.getElementsByTagName("ecg");
        NodeList content = getSubnode(ECG, "content");
        //NodeList node = ((Element)content.item(0)).getChildNodes();
        Element e = (Element)content.item(0);
        String xmlcontent = e.getTextContent();
        xmlcontent = new String(org.apache.commons.codec.binary.Base64.decodeBase64(xmlcontent));
        
        ByteArrayInputStream contentstream = new ByteArrayInputStream(xmlcontent.getBytes());
        DocumentBuilder  contentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doccontent = contentbuilder.parse(contentstream);
        doccontent.getDocumentElement().normalize();
        getGEvalue(doccontent.getChildNodes());
    }
    private void getGEvalue(NodeList nl)
    {
        
        
            NodeList nList = getSubnode(nl,"dcarRecord");
            NodeList patientinfo = getSubnode(nList, "patientInfo");
            NodeList id = getSubnode(patientinfo, "id");
            HN = getAttributevalue(id, "V");
            
            NodeList visit = getSubnode(patientinfo, "visit");
            NodeList order = getSubnode(visit, "order");
            NodeList testinfo = getSubnode(order, "testInfo");
            NodeList acquisitiondatetime = getSubnode(testinfo, "acquisitionDateTime");
            String datetime = getAttributevalue(acquisitiondatetime, "V");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
            Calendar c = Calendar.getInstance();
        try {
            Date date = df.parse(datetime);
            c.setTime(date);
            RECORDDATETIME = dtf.format(date);
            RECORDDATE = dateformat.format(date);
            RECORDTIME  = timeformat.format(date);
            AGEYEAR = c.get(Calendar.YEAR);
            AGEMONTH = c.get(Calendar.MONTH);
            AGEDAY = c.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        NodeList ecgresting = getSubnode(order, "ecgResting");
        NodeList params = getSubnode(ecgresting, "params");
        NodeList ecg = getSubnode(params, "ecg");
        NodeList num = getSubnode(ecg, "num");
        NodeList ventricularrate = getSubnode(num, "ventricularRate");
        Rate = getAttributevalue(ventricularrate, "V");
        NodeList rrinterval = getSubnode(num, "RR_Interval");
        RR = getAttributevalue(rrinterval, "V");
        NodeList var = getSubnode(ecg, "var");
        NodeList mediantemplate = getSubnode(var, "medianTemplate");
        NodeList measurements = getSubnode(mediantemplate, "measurements");
        NodeList global = getSubnode(measurements, "global");
        NodeList qrsduration = getSubnode(global, "QRS_Duration");
        QRSD = getAttributevalue(qrsduration, "V");
        NodeList qtinterval = getSubnode(global, "QT_Interval");
        QT = getAttributevalue(qtinterval, "V");
        NodeList printerval = getSubnode(global, "PR_Interval");
        PR = getAttributevalue(printerval, "V");
        QTCB = calQTCB(QT, RR);
        QTCF = calQTCF(QT, RR);
        NodeList paxis = getSubnode(global, "P_Axis");
        Paxis = getAttributevalue(paxis, "V");
        NodeList raxis = getSubnode(global, "R_Axis");
        QRSaxis = getAttributevalue(raxis, "V");
        NodeList taxis = getSubnode(global, "T_Axis");
        Taxis = getAttributevalue(taxis, "V");
        NodeList qonset = getSubnode(global, "Q_Onset");
        Qonset = getAttributevalue(qonset, "V");
        NodeList qoffset = getSubnode(global, "Q_Offset");
        Qoffset = getAttributevalue(qoffset, "V");
        
        NodeList interpretation = getSubnode(ecg, "interpretation");
        NodeList statement = getSubnode(interpretation, "statement");
        STATEMENT = null;
        SEVERITY = "";
        if(statement!=null)
        {
            STATEMENT  = new ArrayList<>();
            for(int i=0;i<statement.getLength();i++)
            {
                Node nNode = statement.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    String value = eElement.getAttribute("V");
                    if(!value.equals(""))
                    {
                        Statement s = new Statement();
                        s.setLeftstatement(value);
                        s.setRightstatement("");
                        STATEMENT.add(s);
                    }
                }
            }
            if(STATEMENT.size()>0)
            {
            SEVERITY = STATEMENT.get(STATEMENT.size()-1).getLeftstatement();
            }
            else
            {
                SEVERITY = "";
            }
        }
        SIGNATURE = "";
        
        NodeList cfg = getSubnode(ecg, "cfg");
        NodeList reportconfiguration = getSubnode(cfg, "reportConfiguration");
        NodeList writerspeed = getSubnode(reportconfiguration, "writerSpeed");
        PAPERSPEED = getAttributevalue(writerspeed, "V");
        SPEEDUNIT = getAttributevalue(writerspeed, "U");
        
        NodeList frontalleadgain = getSubnode(reportconfiguration, "frontalLeadGain");
        AMPLIFICATION = getAttributevalue(frontalleadgain, "V");
        AMPLIFICATIONUNIT = getAttributevalue(frontalleadgain, "U");
        
        NodeList wav = getSubnode(ecg, "wav");
        NodeList ecgwaveform = getSubnode(wav, "ecgWaveform");
        DATA = getECGvalue(ecgwaveform, "V");

        
        /*
        //get median template for filtering using Finite Impulse Response Residual (FIR) Filtering Algorithm 
        DATAFIRFILTER = new int[DATA.length][];
        COEF = new int[DATA.length][];
        NodeList ecgwaveformmxg = getSubnode(mediantemplate, "ecgWaveformMXG");
        NodeList ecgwaveformfilter = getSubnode(ecgwaveformmxg, "ecgWaveform");
        
        for(int i=0;i<ecgwaveformfilter.getLength();i++)
        {
            Node nNode = ecgwaveformfilter.item(i);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    String lead = eElement.getAttribute("lead");
                    String data = eElement.getAttribute("V");
                    String[] coefsstring = data.split(" ");
                    
                    int from = Integer.parseInt(Qonset)/2;
                    int to = Integer.parseInt(Qoffset)/2;
                    
                    System.out.println("from "+from+"to "+to);
                    COEF[i] = new int[to-from];
                    for(int j=0;j<to-from;j++)
                    {
                        COEF[i][j] = Integer.parseInt(coefsstring[j+from]);
                    }
                    */
                    /*
                    double[] coefs = new double[coefsstring.length];
                    COEF[i] = new int[coefsstring.length];
                    for(int j=0;j<coefsstring.length;j++)
                    {
                        coefs[j] = Double.parseDouble(coefsstring[j]);
                        COEF[i][j] = (int) coefs[j];

                    }
                    double h[] = {0.8, 0.8, 0.8, 0.8, 0.8};
                    DATAFIRFILTER[i] = new int[DATA[i].length];
                    
                    /*
                    int set = DATA[i].length/COEF[i].length;
                    for(int k=0;k<set;k++)
                    {
                        int from = COEF[i].length*k;
                        int to = COEF[i].length*(k+1);
                        if(to>DATA[i].length)
                        {
                            to = DATA[i].length;
                        }
                        System.out.println("from "+from+"to "+to);
                        int max = -1000000;
                        int position = 0;
                        for(int l=from;l<to;l++)
                        {
                            if(DATA[i][l]>max)
                            {
                                max = DATA[i][l];
                                position = l;
                            }
                        }
                        //System.out.println("Lead "+i+"Loop "+k+"position "+position);
                    }*/
                    /*
                    for(int k=0;k<DATA[i].length;k++)
                    {
                        FIRfilter f = new FIRfilter(h);
                        double result = f.getOutputSample(DATA[i][k]);
                        if(k<h.length)
                        {
                            DATAFIRFILTER[i][k] = (int) h[k];
                        }
                        else
                        {
                        DATAFIRFILTER[i][k] = (int) result;
                        }
                        
                        
                        
                        /*
                        FIRfilter f = new FIRfilter(coefs);
                        double result = f.getOutputSample(DATA[i][k]);
                        if(k<coefs.length)
                        {
                            DATAFIRFILTER[i][k] = (int) coefs[k];
                        }
                        else
                        {
                        DATAFIRFILTER[i][k] = (int) result;
                        }*/
                    //}
                //}
        //}
        //String dd = "";
    }
    public void readE4Lfromfile(String filepath) throws IOException, JAXBException, ParserConfigurationException, SAXException
    {
        File f = new File(filepath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document doc = builder.parse(f);
        doc.getDocumentElement().normalize();
        getE4Lvalue(doc.getElementsByTagName("ClinicalDocument"));
    }
    public void readE4Lfromstring(String string) throws IOException, JAXBException, ParserConfigurationException, SAXException
    {
        ByteArrayInputStream stream = new ByteArrayInputStream(string.getBytes());
        DocumentBuilder  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(stream);
        doc.getDocumentElement().normalize();
        NodeList ECG = doc.getElementsByTagName("ecg");
        NodeList content = getSubnode(ECG, "content");
        //NodeList node = ((Element)content.item(0)).getChildNodes();
        Element e = (Element)content.item(0);
        String xmlcontent = e.getTextContent();
        xmlcontent = new String(org.apache.commons.codec.binary.Base64.decodeBase64(xmlcontent));
        
        ByteArrayInputStream contentstream = new ByteArrayInputStream(xmlcontent.getBytes());
        DocumentBuilder  contentbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doccontent = contentbuilder.parse(contentstream);
        doccontent.getDocumentElement().normalize();
        getE4Lvalue(doccontent.getChildNodes());
    }
    private void getE4Lvalue(NodeList nl)
    {
        
            //NodeList ECG = doc.getElementsByTagName("ecg");
            //NodeList content = getSubnode(ECG, "content");
            //int i = ((Element)content.item(0)).getChildNodes().getLength();
            //NodeList node = ((Element)content.item(0)).getChildNodes();
        
            //NodeList nList = getSubnode(node,"dcarRecord");
            //NodeList patientinfo = getSubnode(nList, "patientInfo");
            //NodeList id = getSubnode(patientinfo, "id");
            NodeList patient = getSubnode(nl, "Patient");
            HN = getAttributevalue(patient, "HN");

            String datetime = getAttributevalue(nl, "MeasurementTime");
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ss");
            Calendar c = Calendar.getInstance();
        try {
            Date date = df.parse(datetime);
            c.setTime(date);
            RECORDDATE = dateformat.format(date);
            RECORDTIME  = timeformat.format(date);
            RECORDDATETIME = dtf.format(date);
            AGEYEAR = c.get(Calendar.YEAR);
            AGEMONTH = c.get(Calendar.MONTH);
            AGEDAY = c.get(Calendar.DAY_OF_MONTH);
        } catch (ParseException ex) {
            Logger.getLogger(Type.class.getName()).log(Level.SEVERE, null, ex);
        }

        NodeList globalmeasurement = getSubnode(nl, "GlobalMeasurement");
        NodeList params = getSubnode(globalmeasurement, "item");
        int p = params.getLength();
        Rate = getAttributevalue(params, 0,  "value");
        RR = getAttributevalue(params, 10, "value");
        SV1 = getAttributevalue(params, 8, "value");
        RV5 = getAttributevalue(params, 9, "value");
        PR = getAttributevalue(params, 1, "value");
        QRSD = getAttributevalue(params, 2, "value");
        QT = getAttributevalue(params, 3, "value");
        QTCB = calQTCB(QT, RR);
        QTCF = calQTCF(QT, RR);
        Paxis = getAttributevalue(params, 5, "value");
        QRSaxis = getAttributevalue(params, 6, "value");
        Taxis = getAttributevalue(params, 7, "value");
        
        
        
        NodeList automatictypification = getSubnode(nl, "AutomaticTypification");
        NodeList statement = getSubnode(automatictypification, "item");
        STATEMENT = null;
        SEVERITY = "";
        if(statement!=null)
        {
            STATEMENT  = new ArrayList<>();
            for(int i=0;i<statement.getLength();i++)
            {
                Node nNode = statement.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    String value = eElement.getAttribute("text");
                    if(!value.equals(""))
                    {
                        Statement s = new Statement();
                        s.setLeftstatement(value);
                        s.setRightstatement("");
                        STATEMENT.add(s);
                    }
                }
            }
            if(STATEMENT.size()>0)
            {
            SEVERITY = STATEMENT.get(0).getLeftstatement();
            }
            else
            {
                SEVERITY = "";
            }
        }
        
        

        SIGNATURE = "";

        PAPERSPEED = "25";
        SPEEDUNIT = "mm/s";

        AMPLIFICATION = "10";
        AMPLIFICATIONUNIT = "mm/mV";
        
        NodeList measurement = getSubnode(nl,"ECGWave");
        NodeList wav = getSubnode(measurement, "ECGWave");

        // Samplinginterval = 2 msec ==> f = 1000/2 = 500/sec
        NodeList samplingresolution = getSubnode(wav, "SamplingResolution");
        float resolution = Float.parseFloat(getAttributevalue(samplingresolution, "value"));
        NodeList waveformdata = getSubnode(wav, "WaveFormData");
        DATA = getECGvalue(waveformdata, "value");
        for(int i=0;i<DATA.length;i++)
        {
            for(int j=0;j<DATA[i].length;j++)
            {
                //BigDecimal n1 = new BigDecimal(DATA[i][j]);
                //BigDecimal n2 = new BigDecimal(5);
                //BigDecimal n = n1.divide(n2);
                float number = Float.parseFloat(""+DATA[i][j]);
                DATA[i][j] = Integer.parseInt(""+(Math.round(number/5)));
                //DATA[i][j] = n.intValueExact();
            }
        }
String d = "";
    }
    private String getContent(Document doc) throws TransformerConfigurationException, TransformerException, ParserConfigurationException, SAXException, IOException
    {
        NodeList ecg = doc.getElementsByTagName("ecg");
        NodeList content = getSubnode(ecg, "content");
        Element e = (Element)content.item(0);
        String xmlcontent = e.getTextContent();
        xmlcontent = new String(org.apache.commons.codec.binary.Base64.decodeBase64(xmlcontent));
        
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlcontent.getBytes());
        DocumentBuilder  builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doccontent = builder.parse(stream);
        doccontent.getDocumentElement().normalize();
        
        
        //Node node = ((Element)content.item(0)).getChildNodes().item(1);
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doccontent), new StreamResult(sw));

        return sw.toString();
    }
    private NodeList getSubnode(NodeList node, String childname)
    {
        
            if(node==null)
            {
                return null;
            }
            else
            {
                Node nNode = node.item(0);
                if (node.getLength()>0 && nNode.getNodeType() == Node.ELEMENT_NODE) 
                {
                    Element eElement = (Element) nNode;
                    return eElement.getElementsByTagName(childname);
                }
                else
                {
                    return null;
                }
            }
    }
    private String getAttributevalue(NodeList node, String value)
    {
        
        if(node==null)
        {
            return "";
        }
        else
        {
            Node nodelist = node.item(0);
            if (node.getLength()>0 && nodelist.getNodeType() == Node.ELEMENT_NODE) 
            {
                Element element = (Element) nodelist;     
                return element.getAttribute(value);
            }
            else
            {
                return "";
            }
        }
    }
    private String getAttributevalue(NodeList node, int i, String value)
    {
        
        if(node==null)
        {
            return "";
        }
        else
        {
            Node nodelist = node.item(i);
            if (node.getLength()>0 && nodelist.getNodeType() == Node.ELEMENT_NODE) 
            {
                Element element = (Element) nodelist;     
                return element.getAttribute(value);
            }
            else
            {
                return "";
            }
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
                    BigDecimal n = new BigDecimal(array[j]);
                    lead[j] = n.intValue();
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
    private String calQTCB(String qt, String rr)
    {
        String qtcb;
        try
        {
            BigDecimal qtint = BigDecimal.valueOf(Long.parseLong(qt));
            BigDecimal rrint =  BigDecimal.valueOf(Math.sqrt(Double.parseDouble(rr)/1000));
            qtcb = ""+(qtint.divide(rrint,0,BigDecimal.ROUND_HALF_UP)).intValue();
            return qtcb;
        }
        catch(Exception e)
        {
            return "";
        }
    }
    private String calQTCF(String qt, String rr)
    {
        String qtcf;
        try
        {
            BigDecimal qtint = BigDecimal.valueOf(Long.parseLong(qt)*10);
            BigDecimal rrint =  BigDecimal.valueOf(Math.cbrt(Double.parseDouble(rr)));
            qtcf = ""+(qtint.divide(rrint,0,BigDecimal.ROUND_HALF_UP)).intValue();
            return qtcf;
        }
        catch(Exception e)
        {
            return "";
        }
    }
    public static String getHN()
    {
        return HN;
    }
    public static String getRecorddatetime()
    {
        return RECORDDATETIME;
    }
    public static String getRecorddate()
    {
        return RECORDDATE;
    }
    public static String getRecordtime()
    {
        return RECORDTIME;
    }
    public static String getSeverity()
    {
        return SEVERITY;
    }
    public static String getRate()
    {
        return Rate;
    }
    public static String getRR()
    {
        return RR;
    }
    public static String getSV1()
    {
        return SV1;
    }
    public static String getRV5()
    {
        return RV5;
    }
    public static String getPR()
    {
        return PR;
    }
    public static String getQRSD()
    {
        return QRSD;
    }
    public static String getQT()
    {
        return QT;
    }
    public static String getQTCB()
    {
        return QTCB;
    }
    public static String getQTCF()
    {
        return QTCF;
    }
    public static String getPaxis()
    {
        return Paxis;
    }
    public static String getQRSaxis()
    {
        return QRSaxis;
    }
    public static String getTaxis()
    {
        return Taxis;
    }
    public static List<Statement> getStatement()
    {
        return STATEMENT;
    }
    public static String getSignature()
    {
        return SIGNATURE;
    }
    public static String getPaperspeed()
    {
        return PAPERSPEED;
    }
    public static String getSpeedunit()
    {
        return SPEEDUNIT;
    }
    public static String getAmplification()
    {
        return AMPLIFICATION;
    }
    public static String getAmplificationunit()
    {
        return AMPLIFICATIONUNIT;
    }
    public static int getAgeyear()
    {
        return AGEYEAR;
    }
    public static int getAgemonth()
    {
        return AGEMONTH;
    }
    public static int getAgeday()
    {
        return AGEDAY;
    }
    public static int[][] getData()
    {
        return DATA;
    }
    private static String getTagValue(String sTag, Element eElement) {
        if(eElement.getElementsByTagName(sTag).item(0)!=null)
        {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue!=null)
        {
            return nValue.getNodeValue();
        }
        else
        {
            return "no data";
        }
        }
        else
        {
            return "no data";
        }
  }
}
