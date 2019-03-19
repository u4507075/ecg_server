/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package interpret;

import ecg.ECGvalue;
import ecg.Type;
import ecg.Variable;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import patientinfo.Readpatientinfo;

/**
 *
 * @author bon
 */
public class GetECG {
    public void run()
    {
        try {
            getFilenamelist("/home/bon/Documents/Project/ECG project/ECG name list/ecg.xls");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private ArrayList getFilenamelist(String filename) throws FileNotFoundException, IOException
    {
        ArrayList array = new ArrayList();
        FileInputStream file = new FileInputStream(filename);
        HSSFWorkbook wb = new HSSFWorkbook(file);
        HSSFSheet sheet = wb.getSheet("ecg");
            
        for(int i=0;i<sheet.getPhysicalNumberOfRows();i++)
        {
            if(sheet.getRow(i).getCell(0)==null)
                    {
                        break;
                    }
                    else
                    {
                        if(sheet.getRow(i).getCell(0)!=null)
                        {

                            //System.out.println(ecgname);

                            if(sheet.getRow(i).getCell(1)==null && i<14000)
                            {
                                //if(!sheet.getRow(i).getCell(1).getStringCellValue().equals("DONE") )
                                {
                                try {  
                                    System.out.println(i);
                                    getECGfile(i,filename,wb);

                                } catch (ParserConfigurationException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SAXException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (JAXBException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (TransformerConfigurationException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (TransformerException ex) {
                                    Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            }
                        }
                    }
        }
        
        return array;
    }
    private void getECGfile(int id, String filename,HSSFWorkbook wb) throws ParserConfigurationException, MalformedURLException, IOException, SAXException, JAXBException, TransformerConfigurationException, TransformerException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        //url = http://172.17.8.246/ekg/get_ekg_file.php?id=40
        //url = http://10.0.2.71/ekg/get_ekg_file.php?id=40
        URL url = new URL("http://"+Variable.getHost()+Variable.getECGfile()+id);
        URLConnection con = url.openConnection();
        con.setConnectTimeout(1000);

        Document doc = dBuilder.parse(con.getInputStream());
        
        doc.getDocumentElement().normalize();
        String sss = DoctoString(doc);
        NodeList list = doc.getElementsByTagName("data");
                    Node node = list.item(0);
                    Element e = (Element) node;
                    String value = e.getAttribute("value");
                    if(value.equals("0"))
                    {
                        String content = e.getTextContent();
                        if(!content.equals(""))
                        {
                        String decodedString = new String(Base64.decodeBase64(content));
                        String string = decodedString.replaceAll("�", "");
                        //String xml = decodedString.substring(2,decodedString.length());
                        //Get type here
                        
                        //drawGridfromstring(decodedString);
                        
                        ecg.Type t = new ecg.Type();

                        String type = t.getTypefromstring(string);
                        
                        ECGvalue v = new ECGvalue();
                        switch (type) {
                            case "philips":
                                v.readPhilipsfromstring(string);
                                break;
                            case "ge":
                                v.readGEfromstring(string);
                                break;
                            case "e4l":
                                v.readE4Lfromstring(string);
                                break;
                        }
                        if(ECGvalue.getStatement()!=null)
                        {
                            HSSFSheet ecg = wb.getSheet("ecg");
                            HSSFSheet statement = wb.getSheet("statement");
                            HSSFSheet parameter = wb.getSheet("parameter");
                            int lastrow = statement.getLastRowNum()+1;
                            for(int i=0;i<ECGvalue.getStatement().size();i++)
                            {
                                //System.out.println(ECGvalue.getStatement().get(i).getLeftstatement());
                                
                                
                                statement.createRow(lastrow+i);
                                HSSFCell cell = statement.getRow(lastrow+i).createCell(0);
                                cell.setCellValue(id);
                                HSSFCell cell2 = statement.getRow(lastrow+i).createCell(1);
                                cell2.setCellValue(ECGvalue.getStatement().get(i).getLeftstatement());
                            }
                            
                            int last = parameter.getLastRowNum()+1;
                            parameter.createRow(last);
                            try {
                                Readpatientinfo.read(ECGvalue.getHN());
                                Object[] values = {id,
                                                                getPatientAge(Readpatientinfo.getBirth()),
                                                                Readpatientinfo.getSex(),
                                                                ECGvalue.getRate(),
                                                                ECGvalue.getPR(),
                                                                ECGvalue.getQRSD(),
                                                                ECGvalue.getQT(),
                                                                ECGvalue.getQTCB(),
                                                                ECGvalue.getQTCF(),
                                                                ECGvalue.getPaxis(),
                                                                ECGvalue.getQRSaxis(),
                                                                ECGvalue.getTaxis(),
                                                                ECGvalue.getSV1(),
                                                                ECGvalue.getRV5(),
                                                                ECGvalue.getRR()};
                                for(int i=0;i<values.length;i++)
                                {
                                    HSSFCell cell = parameter.getRow(last).createCell(i);
                                    if(values[i] == null)
                                    {
                                        values[i] = "";
                                    }
                                    cell.setCellValue(values[i].toString());
                                }
                            } catch (ParseException ex) {
                                Logger.getLogger(GetECG.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            
                            
                            HSSFCell cell =ecg.getRow(id).getCell(1);
                            if(cell == null)
                            {
                                cell = ecg.getRow(id).createCell(1);
                                cell.setCellValue("DONE");
                            }
                            else
                            {
                                cell.setCellValue("DONE");
                            }
                            
                             FileOutputStream outFile = new FileOutputStream(new File(filename));
                            {
                                wb.write(outFile);
                            }
                        }
                        }
                        else
                        {

                        }
                    }
    }
    public String DoctoString(Document doc) throws TransformerConfigurationException, TransformerException {

        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
}
    
    public final String getPatientAge(String dob) throws ParseException
        {
        if(dob==null||dob.equals("")||dob.startsWith("no"))
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
        //return ""+age+" ปี "+month+" เดือน";
        return ""+age;
        }
        }
}

