/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import filter.Convolution;
import filter.Medianfilter;
import filter.baselinewander;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import logo.logo;
import org.xml.sax.SAXException;
import parser.Statement;
import patientinfo.Readpatientinfo;
/**
 *
 * @author bon
 */
public class DrawECG {

String type;
//SierraEcgFiles sierra;
//Restingecgdata  ecg;
private static TransformingCanvas canvas;
public static Image im;
private static JPanel panel;
private static JPanel glasspane;
private static int translateX;
private static int translateY;
private static double scale = 0;
String HN = "";
String NAME = "";
Container c;
//File file;
static int A4WIDTH = 297*4;
static int A4HEIGHT = 210*4;

int ECGWIDTH = A4WIDTH;//1000;
int ECGHEIGHT = 640;
int INFOGAP = A4HEIGHT-ECGHEIGHT;
static int smoothing = 1;
int[] LeadpositionsY= {(ECGHEIGHT/8)+(ECGHEIGHT*0/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*0/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*0/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*1/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*1/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*1/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*2/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*2/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*2/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*3/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*3/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*3/5)+INFOGAP,
        (ECGHEIGHT/8)+(ECGHEIGHT*4/5)+INFOGAP,};   
int[] LeadpositionsX= {  0,
                                    (A4WIDTH/3),
                                    (A4WIDTH*2/3),
                                    0,
                                    (A4WIDTH/3),
                                    (A4WIDTH*2/3),
                                    0,
                                    (A4WIDTH/3),
                                    (A4WIDTH*2/3),
                                    0,
                                    (A4WIDTH/3),
                                    (A4WIDTH*2/3),
                                    0};
public DrawECG(String type)
{
    this.type = type;
}
public void drawECGfromfile(String filepath, int smoothing,final JPanel panel, JPanel glasspane) throws JAXBException, IOException, ParserConfigurationException, SAXException
{
        //SierraEcgFiles sierra = new SierraEcgFiles();
        //sierra.preprocess(new File(filepath));
        //Restingecgdata ecg = sierra.getRestingecgdata();
    
        ECGvalue v = new ECGvalue();
        switch (type) {
            case Type.PHILIPS:
                v.readPhilipsfromfile(filepath);
                break;
            case Type.GE:
                v.readGEfromfile(filepath);
                break;
            case Type.E4L:
                v.readE4Lfromfile(filepath);
                break;
        }
    DrawECG.glasspane = glasspane;
    DrawECG.smoothing = smoothing;
    DrawECG.panel = panel;
    canvas = new TransformingCanvas();
    TranslateHandler translater = new TranslateHandler();
    canvas.addMouseListener(translater);
    canvas.addMouseMotionListener(translater);
    canvas.drawECGImage();
    c = new Container();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.repaint();
    c.setSize(20000,20000);
    c.setVisible(true); 
}

public void drawECGfromstring(String string, int smoothing,final JPanel panel, JPanel glasspane) throws JAXBException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
{
    //SierraEcgFiles sierra = new SierraEcgFiles();
    //sierra.preprocess(string);
    //Restingecgdata ecg = sierra.getRestingecgdata();

    ECGvalue v = new ECGvalue();
        switch (type) {
            case Type.PHILIPS:
                v.readPhilipsfromstring(string);
                break;
            case Type.GE:
                v.readGEfromstring(string);
                break;
            case Type.E4L:
                v.readE4Lfromstring(string);
                break;
        }
    DrawECG.glasspane = glasspane;
    DrawECG.smoothing = smoothing;
    DrawECG.panel = panel;
    canvas = new TransformingCanvas();
    TranslateHandler translater = new TranslateHandler();
    canvas.addMouseListener(translater);
    canvas.addMouseMotionListener(translater);
    canvas.drawECGImage();
    c = new Container();
    c.setLayout(new BorderLayout());
    c.add(canvas, BorderLayout.CENTER);
    c.repaint();
    c.setSize(20000,20000);
    c.setVisible(true); 
}
public void setHN(String HN)
{
    this.HN = HN;
}
public String getHN()
{
    return HN;
}
public void setName(String NAME)
{
    this.NAME = NAME;
}
public String getName()
{
    return NAME;
}
/*
public Restingecgdata getECG()
{
    return sierra.getRestingecgdata();
}*/
public void drawECG(Graphics2D g) throws JAXBException, IOException
{
    TransformingCanvas can = new TransformingCanvas();
    can.drawECGfile(g);
}
private class TransformingCanvas extends JComponent {

        TransformingCanvas() 
        {
            setOpaque(true);
            setDoubleBuffered(true);
        }
        public void drawECGImage() throws JAXBException, IOException
        {
            int topgap = 10;
            if(scale==0)
            {
                scale = (new BigDecimal(panel.getHeight()-100).divide(new BigDecimal(A4HEIGHT),10,BigDecimal.ROUND_HALF_UP)).doubleValue();
            }

            int center = (panel.getWidth()-(int)(A4WIDTH*scale))/2;

            if(center<0)
            {
                center = 0; 
            }

            int width;
            int height;
            
            if(scale<1)
            {
                width = Math.max(1, (int)(A4WIDTH));
                height = Math.max(1, (int)(A4HEIGHT))+topgap;
            }
            else
            {
                width = Math.max(1, (int)(A4WIDTH*scale))+center;
                height = Math.max(1, (int)(A4HEIGHT*scale))+topgap;
            }
            width = Math.min(10000,width);
            height = Math.min(7500,height);

            im = panel.createImage(width,height);

            Graphics2D ourGraphics = (Graphics2D)im.getGraphics();
            AffineTransform tx = ourGraphics.getTransform();
            tx.translate(center, topgap);
            tx.scale(scale, scale);
            ourGraphics.setTransform(tx);
            ourGraphics.setColor(Color.white);
            ourGraphics.fillRect(0,0,width,height);
            ourGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
            ourGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawGrid(ourGraphics);
            addTracing(ourGraphics);
            setDoubleBuffered(true);
            ourGraphics.dispose();
            
        }

        public void drawECGfile(Graphics2D g) throws JAXBException, IOException
        {
            g.setColor(Color.white);
            g.fillRect(0,0,A4WIDTH,A4HEIGHT);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            //parseECG();
            drawGrid(g);
            addTracing(g);
            g.dispose();
        }
 
        @Override 
        public void paint(Graphics g) {
            g.clearRect(0, 0, getWidth(), getHeight());
            g.drawImage(im, (int)translateX, (int)translateY,null);
            g.dispose();
        }

        private void drawGrid(Graphics2D g)
        {

            g.setPaint(Color.black);

            //draw border
            g.drawLine(0, 0, 0, A4HEIGHT);
            g.drawLine(0, 0, A4WIDTH,0);
            g.drawLine(0, A4HEIGHT, A4WIDTH, A4HEIGHT);
            g.drawLine(A4WIDTH, 0, A4WIDTH, A4HEIGHT);

            //draw info
            drawInfo(g);

            //draw lead label
            g.drawString("I", 10, INFOGAP+10);
            g.drawString("II", 10+(A4WIDTH/3), INFOGAP+10);
            g.drawString("III", 10+(A4WIDTH*2/3), INFOGAP+10);
            g.drawString("aVR", 10, INFOGAP+(ECGHEIGHT/5));
            g.drawString("aVL", 10+(A4WIDTH/3), INFOGAP+(ECGHEIGHT/5));
            g.drawString("aVF", 10+(A4WIDTH*2/3), INFOGAP+(ECGHEIGHT/5));

            g.drawString("V1", 10, INFOGAP+(ECGHEIGHT*2/5));
            g.drawString("V2", 10+(A4WIDTH/3), INFOGAP+(ECGHEIGHT*2/5));
            g.drawString("V3", 10+(A4WIDTH*2/3), INFOGAP+(ECGHEIGHT*2/5));
            g.drawString("V4", 10, INFOGAP+(ECGHEIGHT*3/5));
            g.drawString("V5", 10+(A4WIDTH/3), INFOGAP+(ECGHEIGHT*3/5));
            g.drawString("V6", 10+(A4WIDTH*2/3), INFOGAP+(ECGHEIGHT*3/5));

            g.drawString("II", 10, INFOGAP+(ECGHEIGHT*4/5));
            
            double cm = 40;
            double mm = 4.0;
            for(int i=0;i<ECGWIDTH;i++)
            {
                if(i*cm<ECGWIDTH)
                {
                Shape biglineV = new Line2D.Double(i*cm, INFOGAP, i*cm, A4HEIGHT);
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.2f));
                g.draw(biglineV);
                }
                if(i*cm/2<ECGWIDTH)
                {
                Shape midlineV = new Line2D.Double(i*cm/2, INFOGAP, i*cm/2, A4HEIGHT);
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.2f));
                g.draw(midlineV);
                }
                if(i*cm/10<ECGWIDTH)
                {
                Shape smalllineV = new Line2D.Double(i*mm, INFOGAP, i*mm, A4HEIGHT);
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.1f));
                g.draw(smalllineV);
                }
            }
            for(int i=0;i<ECGHEIGHT;i++)
            {
                if(i*cm<ECGHEIGHT)
                {
                Shape biglineH = new Line2D.Double(0,INFOGAP+(i*cm),A4WIDTH, INFOGAP+(i*cm));
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.2f));
                g.draw(biglineH);
                }
                if(i*cm/2<ECGHEIGHT)
                {
                Shape midlineH = new Line2D.Double(0,INFOGAP+(i*cm/2),A4WIDTH, INFOGAP+(i*cm/2));
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.2f));
                g.draw(midlineH);
                }
                if(i*cm/10<ECGHEIGHT)
                {
                Shape smalllineH = new Line2D.Double(0,INFOGAP+(i*mm),A4WIDTH, INFOGAP+(i*mm));
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(0.1f));
                g.draw(smalllineH);
                }
            }
        }
        private void drawInfo(Graphics2D g)
        {
            HN = ECGvalue.getHN();//ecg.getPatient().getGeneralpatientdata().getPatientid();
            if(!"".equals(HN))
            {
            try {
                Readpatientinfo.read(HN);
            } catch (ParserConfigurationException | SAXException | IOException | ParseException ex) {
                Logger.getLogger(DrawECG.class.getName()).log(Level.SEVERE, null, ex);
            }
            


            
            int toppadding = 30;
            int sidepadding = 20;
            int linespace = 15;
            this.setFont(new Font(Variable.getFont(), Font.PLAIN, Variable.getHeader2size()));
            //String hn = "HN 2739313";
            //HN = hn;
            Font font = new Font(this.getFont().getFamily(), Font.BOLD, Variable.getHeader1size());
            FontMetrics fontMetrics = getFontMetrics(font);
            int hnwidth = fontMetrics.stringWidth("HN "+HN);


            g.setPaint(Color.black);
            g.setFont(font);
            if(!HN.equals(""))
            {
                //HN = "XXXXXXX";
                g.drawString("HN "+HN,A4WIDTH-hnwidth-sidepadding-40,toppadding);
            }
            String age = "";
            try {
                age = getPatientAge(Readpatientinfo.getBirth());
            } catch (ParseException ex) {
                Logger.getLogger(DrawECG.class.getName()).log(Level.SEVERE, null, ex);
            }
            NAME = Readpatientinfo.getName()+" "+Readpatientinfo.getSurname();
            String patientinfo =    "";//"จันทร์ดี ศรีสุขใจ  อายุ 76 ปี   เพศ หญิง";
            if(!Readpatientinfo.getName().equals(""))
            {
                patientinfo = patientinfo + Readpatientinfo.getName()+" ";
            }
            if(!Readpatientinfo.getSurname().equals(""))
            {
                patientinfo = patientinfo + Readpatientinfo.getSurname()+" ";
            }
            //patientinfo = "EXAMPLE ECG ";
            if(!Readpatientinfo.getSex().equals(""))
            {
                patientinfo = patientinfo +"เพศ "+ Readpatientinfo.getSex()+" ";
            }
            if(!age.equals(""))
            {
                patientinfo = patientinfo +"อายุ "+ age;
            }

            
            
            g.drawString(patientinfo, sidepadding, toppadding);

            String record = "วันที่บันทึก: "+ECGvalue.getRecorddate()+" "+ECGvalue.getRecordtime();//"วันที่บันทึก: "+ecg.getReportinfo().getDate()+" "+ecg.getReportinfo().getTime();//"วันที่บันทึก: 28 ตุลาคม 2554  08:38:50";
            g.drawString(record, sidepadding+(A4WIDTH/2), toppadding);

            g.setPaint(Color.red);
            g.setStroke(new BasicStroke(3));
            g.drawLine(0, 40, A4WIDTH, 40);

            g.setStroke(new BasicStroke(1));
            g.drawLine(120, 40, 120, INFOGAP);

            this.setFont(new Font(Variable.getFont(), Font.BOLD, Variable.getHeader2size()));
            g.setFont(this.getFont());
            g.setPaint(Color.black);
            g.drawString("Rate", sidepadding, 55);
            g.drawString("RR", sidepadding, 55+(linespace*1));
            g.drawString("PR", sidepadding, 55+(linespace*2));
            g.drawString("QRS dur", sidepadding, 55+(linespace*3));
            g.drawString("QT", sidepadding, 55+(linespace*4));
            g.drawString("QTCB", sidepadding, 55+(linespace*5));
            g.drawString("QTCF", sidepadding, 55+(linespace*6));
            g.drawString("P axis", sidepadding, 55+(linespace*7));
            g.drawString("QRS axis", sidepadding, 55+(linespace*8));
            g.drawString("T axis", sidepadding, 55+(linespace*9));

            g.drawString("Interpretation", 140, 55);
            g.drawString("Finding", sidepadding+(A4WIDTH/2), 55);
            g.drawString("Diagnosis:", 140, 55+(linespace*9));
            g.drawString("Approved by: ", sidepadding+(A4WIDTH/2), 55+(linespace*9));
            fontMetrics = getFontMetrics(this.getFont());
            int datetitlewidth = fontMetrics.stringWidth("Date:");
            int approvewidth = fontMetrics.stringWidth("Approved by: ");
            this.setFont(new Font(Variable.getFont(), Font.PLAIN, Variable.getHeader2size()));
            fontMetrics = getFontMetrics(this.getFont());
            int datewidth = fontMetrics.stringWidth(" 2012-11-24 12:20:31");
            //g.drawString(ecg.getInterpretations().getInterpretation().get(0).getSeverity().getValue(), (A4WIDTH)-datetitlewidth-datewidth-sidepadding, 55+(linespace*0));
            String severity = ECGvalue.getSeverity();
            if(!severity.equals(""))
            {
                g.drawString(severity, (A4WIDTH)-datetitlewidth-datewidth-sidepadding, 55+(linespace*0));
            }
            
            g.drawString("Date:", (A4WIDTH)-datetitlewidth-datewidth-sidepadding, 55+(linespace*9));

            g.setFont(this.getFont());
            /*
            Globalmeasurements me = ecg.getInterpretations().getInterpretation().get(0).getGlobalmeasurements();
            String Rate = me.getHeartrate().getValue();
            String RR = me.getRRint().getValue();
            String PR = me.getPRint().getValue();
            String QRSD = me.getQRSduration().getValue();
            String QT = me.getQTint().getValue();
            String QTCB = me.getQTCB().getValue();
            String QTCF = me.getQTCF().getValue();
            String Paxis = me.getPfrontaxis().getValue();
            String QRSaxis = me.getQrsfrontaxis().getValue();
            String Taxis = me.getTfrontaxis().getValue();
            g.drawString(Rate, 80, 55);
            g.drawString(RR, 80, 55+(linespace*1));
            g.drawString(PR, 80, 55+(linespace*2));
            g.drawString(QRSD, 80, 55+(linespace*3));
            g.drawString(QT, 80, 55+(linespace*4));
            g.drawString(QTCB, 80, 55+(linespace*5));
            g.drawString(QTCF, 80, 55+(linespace*6));
            g.drawString(Paxis, 80, 55+(linespace*7));
            g.drawString(QRSaxis, 80, 55+(linespace*8));
            g.drawString(Taxis, 80, 55+(linespace*9));
            */
            g.drawString(ECGvalue.getRate(), 80, 55);
            g.drawString(ECGvalue.getRR(), 80, 55+(linespace*1));
            g.drawString(ECGvalue.getPR(), 80, 55+(linespace*2));
            g.drawString(ECGvalue.getQRSD(), 80, 55+(linespace*3));
            g.drawString(ECGvalue.getQT(), 80, 55+(linespace*4));
            g.drawString(ECGvalue.getQTCB(), 80, 55+(linespace*5));
            g.drawString(ECGvalue.getQTCF(), 80, 55+(linespace*6));
            g.drawString(ECGvalue.getPaxis(), 80, 55+(linespace*7));
            g.drawString(ECGvalue.getQRSaxis(), 80, 55+(linespace*8));
            g.drawString(ECGvalue.getTaxis(), 80, 55+(linespace*9));
            //java.util.List<Statement> statement = ecg.getInterpretations().getInterpretation().get(0).getStatement();
             java.util.List<Statement> statement = ECGvalue.getStatement();
             if(statement!=null)
             {
                for(int i=0;i<statement.size();i++)
                {
                    if(i==8)
                    {
                        break;
                    }
                    g.drawString(statement.get(i).getLeftstatement(), 140, 55+(linespace*(i+1)));
                    g.drawString(statement.get(i).getRightstatement(), sidepadding+(A4WIDTH/2), 55+(linespace*(i+1)));
                }
             }
            g.drawString("", 200, 55+(linespace*9));//Diagnosis
            //g.drawString(ecg.getInterpretations().getInterpretation().get(0).getMdsignatureline(), sidepadding+(A4WIDTH/2)+approvewidth, 55+(linespace*9));//Signature
            g.drawString(ECGvalue.getSignature(), sidepadding+(A4WIDTH/2)+approvewidth, 55+(linespace*9));//Signature
            g.drawString("", (A4WIDTH)-datewidth-sidepadding, 55+(linespace*9));//" 2012-11-24 12:20:31"
            /*
            g.drawString("Paper speed:  "+ecg.getReportinfo().getReportgain().getTimegain().getValue()+" "
                                +ecg.getReportinfo().getReportgain().getTimegain().getUnit()
                                +"          Amplification:  "
                                +ecg.getReportinfo().getReportgain().getAmplitudegain().getOverallgain()+" "
                                +ecg.getReportinfo().getReportgain().getAmplitudegain().getUnit(), sidepadding, A4HEIGHT-sidepadding);
            */
            g.drawString("Paper speed:  "+ECGvalue.getPaperspeed()+" "
                                +ECGvalue.getSpeedunit()
                                +"          Amplification:  "
                                +ECGvalue.getAmplification()+" "
                                +ECGvalue.getAmplificationunit(), sidepadding, A4HEIGHT-sidepadding);
            fontMetrics = getFontMetrics(this.getFont());
            int namewidth = fontMetrics.stringWidth("โรงพยาบาลมหาราชนครเชียงใหม่ คณะแพทยศาสตร์ มหาวิทยาลัยเชียงใหม่");
            g.drawString("โรงพยาบาลมหาราชนครเชียงใหม่ คณะแพทยศาสตร์ มหาวิทยาลัยเชียงใหม่", A4WIDTH-namewidth-sidepadding-25,A4HEIGHT-sidepadding);
            
            /*try {
                BufferedImage img = ImageIO.read(new File("logo/medlogo.jpg"));
                g.drawImage(img, A4WIDTH-sidepadding-30, A4HEIGHT-sidepadding-30,30,30, null);
            } catch (IOException ex) {
                Logger.getLogger(DrawECG.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            BufferedImage img = new logo().getLogo();
            g.drawImage(img, A4WIDTH-sidepadding-30, A4HEIGHT-sidepadding-30,30,30, null);
            }
            else
            {
                panel.getGraphics().clearRect(0, 0, panel.getWidth(), panel.getHeight());
            }
        }
        public final String getPatientAge(String dob) throws ParseException
        {
        if(dob==null||dob.equals(""))
        {
            return "";
        }
        else
        {
        //dob = "2011-01-04";

        //TAKE SUBSTRINGS OF THE DOB SO SPLIT OUT YEAR, MONTH AND DAY
        //INTO SEPERATE VARIABLES
        int yearDOB = Integer.parseInt(dob.substring(0, 4));
        int monthDOB = Integer.parseInt(dob.substring(5, 7));
        int dayDOB = Integer.parseInt(dob.substring(8, 10));

        //CALCULATE THE CURRENT YEAR, MONTH AND DAY
        //INTO SEPERATE VARIABLES

        /*
        int thisYear = ecg.getReportinfo().getDate().getYear();
        int thisMonth = ecg.getReportinfo().getDate().getMonth();
        int thisDay = ecg.getReportinfo().getDate().getDay();
        * 
        */
        int thisYear = ECGvalue.getAgeyear();
        int thisMonth = ECGvalue.getAgemonth();
        int thisDay = ECGvalue.getAgeday();
        //CREATE AN AGE VARIABLE TO HOLD THE CALCULATED AGE
        //TO START WILL – SET THE AGE EQUEL TO THE CURRENT YEAR MINUS THE YEAR
        //OF THE DOB
        int age = thisYear - yearDOB;
        int month;

        //IF THE CURRENT MONTH IS LESS THAN THE DOB MONTH
        //THEN REDUCE THE DOB BY 1 AS THEY HAVE NOT HAD THEIR
        //BIRTHDAY YET THIS YEAR
        if(thisMonth < monthDOB){
            age = age-1;
            month = 12 - (monthDOB - thisMonth);
        }
        else
        {
            month = thisMonth - monthDOB;
        }

        //IF THE MONTH IN THE DOB IS EQUEL TO THE CURRENT MONTH
        //THEN CHECK THE DAY TO FIND OUT IF THEY HAVE HAD THEIR
        //BIRTHDAY YET. IF THE CURRENT DAY IS LESS THAN THE DAY OF THE DOB
        //THEN REDUCE THE DOB BY 1 AS THEY HAVE NOT HAD THEIR
        //BIRTHDAY YET THIS YEAR
        if(thisMonth == monthDOB && thisDay < dayDOB){
            age = age-1;
        }

        //THE AGE VARIBALE WILL NOW CONTAIN THE CORRECT AGE
        //DERIVED FROMTHE GIVEN DOB
        //System.out.println(age+" "+month); 
        return ""+age+" ปี "+month+" เดือน";
        }
        }
        private void addTracing(Graphics2D g) throws JAXBException, IOException
        {
            drawLead(g);
            //g.drawString("Copyright 2014. Piyapong Khumrin", A4WIDTH-220,220);
        }
        public void drawLead(Graphics2D g) throws IOException, JAXBException
        {
            //DecodedLead[] leads = sierra.getLeads();//new SierraEcgFiles().extractLeads(new File(filepath));
            int[][] alldata = ECGvalue.getData();
            float s = 0.2f;

            //for(int j=0;j<leads.length;j++)
            for(int j=0;j<alldata.length;j++)
            {
            //DecodedLead qrs = getQRS(leads[j]);
            //int[] data = getQRS(leads[j]);
                
            int[] data  = getQRS(alldata[j],true);
                
            //Butterworth filter
            //Preprocessing pre = new Preprocessing();
            //data = pre.process(data);

            /*
            baselinewander base = new baselinewander();
            int[] newdata = base.getdiff(data);
            int[] shift = getSed(data);//getFraction(data);
            */
            int[] newdata = null;
            //only GE
            if(type.equals(Type.GE))
            {
                //newdata = ECGvalue.DATAFIRFILTER[j];
                //Convolution c = new Convolution(ECGvalue.COEF[j]);
                //newdata = c.getValue(data);
            }
            //Medianfilter m = new Medianfilter();
            //int[] newdata =m.getMedian(alldata[j]);
            //Convolution c = new Convolution(ECGvalue.COEF[j]);
            //int[] newdata = c.getValue(alldata[j]);
            
            //Baseline wander
            if(type.equals(Type.PHILIPS))
            {
                data = setBaselinewander(data);
            }
            
            data = setSmoothing(data);
            
            if(j==1)
            {
                
                
                for(int i=50;i<data.length-1;i++)
                {
                    if(i==6000)
                    {
                        break;
                    }

                    
                    Shape q = new Line2D.Double(LeadpositionsX[12]+(i*s), LeadpositionsY[12]-(data[i]*s), LeadpositionsX[12]+((i+1)*s), LeadpositionsY[12]-(data[i+1]*s));
                    g.setPaint(Color.black);
                    g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                    g.draw(q);
                    /*
                    q = new Line2D.Double(LeadpositionsX[12]+(i*s), LeadpositionsY[12]-(diff[i]*s), LeadpositionsX[12]+((i+1)*s), LeadpositionsY[12]-(diff[i+1]*s));
                    g.setPaint(Color.green);
                    g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                    g.draw(q);
                    */
                }
            }
            //else
            {
                
            for(int i=0;i<1950;i++)
            //for(int i=50;i<data.length-1;i++)
            {
                //Shape q = new Line2D.Double(LeadpositionsX[j]+(i*s), LeadpositionsY[j]-(qrs.get(i)*s), LeadpositionsX[j]+((i+1)*s), LeadpositionsY[j]-(qrs.get(i+1)*s));
                Shape q = new Line2D.Double(LeadpositionsX[j]+(i*s), LeadpositionsY[j]-(data[i]*s), LeadpositionsX[j]+((i+1)*s), LeadpositionsY[j]-(data[i+1]*s));
                g.setPaint(Color.black);
                g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g.draw(q);
                if(newdata!=null)
                {
                q = new Line2D.Double(LeadpositionsX[j]+((i+(ECGvalue.COEF[j].length/2))*s), LeadpositionsY[j]-(newdata[i]*s), LeadpositionsX[j]+((i+(ECGvalue.COEF[j].length/2)+1)*s), LeadpositionsY[j]-(newdata[i+1]*s));
                g.setPaint(Color.green);
                g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g.draw(q);
                
                if(i+1<ECGvalue.COEF[j].length)
                {
                q = new Line2D.Double(LeadpositionsX[j]+(i*s), LeadpositionsY[j]-(ECGvalue.COEF[j][i]*s), LeadpositionsX[j]+((i+1)*s), LeadpositionsY[j]-(ECGvalue.COEF[j][i+1]*s));
                g.setPaint(Color.blue);
                g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g.draw(q);
                }
                }
                /*
                q = new Line2D.Double(LeadpositionsX[j]+(i*s), LeadpositionsY[j]-(rest[i]*s), LeadpositionsX[j]+((i+1)*s), LeadpositionsY[j]-(rest[i+1]*s));
                g.setPaint(Color.red);
                g.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g.draw(q);*/
            }
            }
            }
            
        }
        
        private int[] getQRS(int[] data, boolean lead2)
        {

            ECGFilter qrs = new ECGFilter();
            int[] newdata;
            if(lead2)
            {
                newdata = new int[data.length];
            }
            else
            {
                newdata = new int[2000];
            }
            System.arraycopy(data, 0, newdata, 0, newdata.length);
            //newdata = (qrs.max(newdata,smoothing));
            return newdata;
        }
        private int[] setBaselinewander(int[] data)
        {
            baselinewander base = new baselinewander();
            //int[] newdata = base.getdiff(data);
            int[] shift = base.getShift(data);
            ArrayList shiftpoint = base.getShiftpoint(data);
            int num = 0;
            for(int i=0;i<data.length;i++)
            {
                num = num+shift[i];
                data[i] = data[i]-num;
            }
            //while(true)
            for(int b=0;b<100;b++)
            {
                if(shiftpoint.size()<3)
                {
                    break;
                }
                else
                {
                    boolean complete = true;
                    for(int i=0;i<shiftpoint.size()-1;i++)
                    {
                        int start = Integer.parseInt(shiftpoint.get(i).toString());
                        int end = Integer.parseInt(shiftpoint.get(i+1).toString());

                        int[] range =new int[end-start];
                        
                        System.arraycopy(data, start, range, 0, range.length);
                        //System.out.println(range.length);
                        float median = 0; 
                        if(range.length>1)
                        {   
                            median = base.median(range);
                        }
                        else if (range.length==1)
                        {
                            median = range[0];
                        }
                        //System.out.println("point "+i+" median "+median);
                        if(median>200)
                        {
                            //System.out.println("Above threadhold Lead point "+i+" median "+median);
                            complete = false;
                            for(int k=start;k<end;k++)
                            {
                                data[k] = data[k]-256;
                            }
                        }
                        else if(median<-200)
                        {
                            //System.out.println("Below threadhold Lead "+j+" point "+i+" median "+median);
                            complete = false;
                            for(int k=start;k<end;k++)
                            {
                                data[k] = data[k]+256;
                            }
                        }
                    }
                    if(complete)
                    {
                        break;
                    }
                }
            }
            
            if(shiftpoint.size()>2)
            {
            for(int i=0;i<shiftpoint.size()-1;i++)
            {
                int start = Integer.parseInt(shiftpoint.get(i).toString());
                int end = Integer.parseInt(shiftpoint.get(i+1).toString());

                int[] range =new int[end-start];
                if(range.length>1)
                {
                    System.arraycopy(data, start, range, 0, range.length);
                    float median = 0; 
                        if(range.length>1)
                        {   
                            median = base.median(range);
                        }
                        else if (range.length==1)
                        {
                            median = range[0];
                        }

                    if(median<0)
                    {
                        //System.out.println("Lift up Lead "+j+" point "+i+" median "+median);
                        for(int k=start;k<end;k++)
                        {
                            data[k] = data[k]+256;
                        }
                    }
                    /*
                    else if(median>125)
                    {
                        //System.out.println("Lift up Lead "+j+" point "+i+" median "+median);
                        for(int s=0;s<10;s++)
                        {
                            if(median<256)
                            {
                                break;
                            }
                            for(int k=start;k<end;k++)
                            {
                                data[k] = data[k]-256;
                            }
                            
                            System.arraycopy(data, start, range, 0, range.length);
                            median = base.median(range);
                        }
                    }*/
                }
            }
            }
            return data;
        }
        private int[] getSed(int[] data)
        {
            int[] newdata = new int[data.length];
            int u = 0;
            for(int i=0;i<data.length;i++)
            {
                newdata[i] = data[i]%(256*1);
                /*
                if(i>0 &&newdata[i-1]>220&&newdata[i-1]<256&&newdata[i-1]>data[i])
                {
                    u++;
                    //newdata[i] = newdata[i]+256;
                }
                newdata[i] = newdata[i]+(256*u);
                * 
                */
                /*
            if(data[i]<0)
                {
                    int v = data[i]%256;
                    if(v<-128)
                    {
                        newdata[i] = data[i]-(v+256);
                    }
                    else
                    {
                        newdata[i] = data[i]-v;
                    }
                }
                else
                {
                    int v = data[i]%256;
                    if(v<128)
                    {
                        newdata[i] = data[i]-v;
                    }
                    else
                    {
                        newdata[i] = data[i]+(256-v);
                    }
                }*/
            }
            return newdata;
        }
        private int[] getFraction(int[] data)
        {
            int[] newdata = new int[data.length];
            for(int i=0;i<data.length;i++)
            {
                
                if(data[i]<0)
                {
                    newdata[i] = new BigInteger(Integer.toHexString(data[i]| 0xFFFFFF00), 16).intValue();
                    //System.out.println("old "+data[i]+" = "+" hex "+Integer.toHexString(data[i])+" new "+newdata[i]+" hex "+Integer.toHexString(data[i]| 0xFFFFFF00));
                }
                else
                {
                    String s = Integer.toHexString(data[i]);
                    StringBuilder builder = new StringBuilder(s); 
                    if(s.length()>2)
                    {
                        builder.replace(0, builder.length()-2, "0");
                    }
                    String newtext = builder.toString();
                    
                    newdata[i] = new BigInteger(newtext, 16).intValue();
                    
                    //System.out.println("old "+data[i]+" = "+" hex "+Integer.toHexString(data[i])+" new "+newdata[i]+" hex "+newtext);
                }

                BigDecimal a = new BigDecimal(data[i]);
                BigDecimal b = new BigDecimal(256);
                BigDecimal a1;
                if(i<data.length-1)
                {
                    a1 = new BigDecimal(data[i+1]-data[i]);
                }
                else
                {
                    a1 = new BigDecimal(data[i]-data[i]);
                }
                int mul = a.divide(b,BigDecimal.ROUND_HALF_DOWN).intValue();
                int mul1 = a1.divide(b,BigDecimal.ROUND_HALF_DOWN).intValue();
                newdata[i] = (mul1)*256;
                //System.out.println(newdata[i]);
                /*
                int mul = data[i]/256;
                
                if(data[i]<0)
                {
                    newdata[i] = (mul*256)+data[i];
                }
                else
                {
                    newdata[i] = (-1*mul*256)+data[i];
                }*/
                
                /*
                else 
                {
                    newdata[i] = data[i];
                }
                */
            
                /*
                if(data[i]<0)
                {
                    int v = data[i]%256;
                    if(v<-128)
                    {
                        newdata[i] = data[i]-(v+256);
                    }
                    else
                    {
                        newdata[i] = data[i]-v;
                    }
                }
                else
                {
                    int v = data[i]%256;
                    if(v<128)
                    {
                        newdata[i] = data[i]-v;
                    }
                    else
                    {
                        newdata[i] = data[i]+(256-v);
                    }
                }
                newdata[i] = data[i] - newdata[i];*/
            }
            /*
            int[] dd = new int[data.length];
            
            int s = 0;
            for(int i=0;i<data.length-1;i++)
            {
                int diff = newdata[i]-newdata[i+1];
                //System.out.println(diff);
                if(diff>200)
                {
                    if(s==0)
                    {
                        s=1;
                    }
                    else if(s==-1)
                    {
                        s=0;
                    }
                }
                else if(diff<-200)
                {
                    if(s==0)
                    {
                        s=-1;
                    }
                    else if(s==1)
                    {
                        s=0;
                    }
                    
                }
                else
                {

                }
                dd[i] = newdata[i+1]+(256*s);
            }
            return dd;*/
            return newdata;
            /*
            baselinewander base = new baselinewander();
            //int[] newdata = base.getdiff(data);

            ArrayList shiftpoint = base.getShiftpoint(data);
            
            if(shiftpoint.size()>2)
            {
            for(int i=0;i<shiftpoint.size()-1;i++)
            {
                int start = Integer.parseInt(shiftpoint.get(i).toString());
                int end = Integer.parseInt(shiftpoint.get(i+1).toString());

                int[] range =new int[end-start];
                if(range.length>1)
                {
                    System.arraycopy(dd, start, range, 0, range.length);
                    float median = 0; 
                        if(range.length>1)
                        {   
                            median = base.median(range);
                        }
                        else if (range.length==1)
                        {
                            median = range[0];
                        }

                    if(median<0)
                    {
                        //System.out.println("Lift up Lead "+j+" point "+i+" median "+median);
                        for(int k=start;k<end;k++)
                        {
                            dd[k] = dd[k]+256;
                        }
                    }
                }
            }
            
        }
            */
            //return dd;
    }
        private int[] setSmoothing(int[] data)
        {
            ECGFilter qrs = new ECGFilter();
            data = (qrs.max(data,smoothing));
            return data;
        }
/*
        private int[] getQRS(DecodedLead sig)
        {

            ECGFilter qrs = new ECGFilter();
            int[] data = sig.getData().clone();
            data = (qrs.max(data,smoothing));
            return data;
        }*/
    }
 
private static class TranslateHandler implements MouseListener,MouseMotionListener 
{
        private int lastOffsetX;
        private int lastOffsetY;
        
        private Point start = new Point();
        private Point end = new Point();
        
        private int startp;
        private int endp;

        
        @Override
        public void mousePressed(MouseEvent e) 
        {

            if(SwingUtilities.isRightMouseButton(e))
            {
                startp = e.getXOnScreen();
                start.x = e.getX();
                start.y = e.getY();
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                glasspane.setLocation(start.x, start.y);
                glasspane.setSize(0,0);
                glasspane.setVisible(true);
            }
            else if(SwingUtilities.isLeftMouseButton(e))
            {
                // capture starting point
                lastOffsetX = e.getX();
                lastOffsetY = e.getY();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            if(SwingUtilities.isLeftMouseButton(e))
            {
            // new x and y are defined by current mouse location subtracted
            // by previously processed mouse location
            int newX = e.getX() - lastOffsetX;
            int newY = e.getY() - lastOffsetY;

            // increment last offset to last processed by drag event.
            lastOffsetX += newX;
            lastOffsetY += newY;

            // update the canvas locations
            translateX += newX;
            translateY += newY;

            // draw new image.
            panel.getGraphics().clearRect(0, 0, panel.getWidth(), panel.getHeight());
            panel.getGraphics().drawImage(im, (int)translateX, (int)translateY, null);
            }
            else if(SwingUtilities.isRightMouseButton(e))
            {            
                endp = e.getXOnScreen();
                end.x = e.getX()-start.x;
                end.y = e.getY()-start.y;

                glasspane.setLocation(start.x, start.y);
                glasspane.setSize(end.x,end.y);
            }
        }
 
        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {}
        @Override
        public void mouseMoved(MouseEvent e) {}
        @Override
        public void mouseReleased(MouseEvent e) {

        
        if(SwingUtilities.isRightMouseButton(e))
            {
                panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                glasspane.setVisible(false);
                
                int ss = endp-startp;
                
                if(ss>0)
                {
                    panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    int center = (panel.getWidth()-(int)(A4WIDTH*scale))/2;
                    int vcenter;
                    if(center<0)
                    {
                        center = 0;
                        vcenter = 0;
                    }
                    else
                    {
                        center = center+(int)(translateX*scale);
                        vcenter = (int)(translateY*scale);
                    }
                    scale = A4WIDTH*scale/(endp-startp); 
                    //prevent an overflow zooming
                    if(scale>5)
                    {
                        scale = 5;
                    }

                    try {
                    //canvas=null;
                    canvas.drawECGImage();
                   
                    } catch (JAXBException | IOException ex) {
                        panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        Logger.getLogger(DrawECG.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    translateX = (int)(translateX+((-start.x+center)*scale));
                    translateY = (int)(translateY+((-start.y+vcenter)*scale));
                     panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
                else
                {
                    scale = (new BigDecimal(panel.getHeight()-100).divide(new BigDecimal(A4HEIGHT),10,BigDecimal.ROUND_HALF_UP)).doubleValue();
                    translateX = 0;
                    translateY = 0;
                    panel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    try {
                    //canvas=null;
                    canvas.drawECGImage();
                    } catch (JAXBException | IOException ex) {
                        panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                        Logger.getLogger(DrawECG.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
                }
                    canvas.getGraphics().dispose();
            }
        }
    }
}
