/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package patientinfo;

import ecg.Variable;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bon
 */
public class Readpatientinfo {

    //static String senderid;
    static String ID;
    static String name;
    static String surname;
    static String sex;
    static String birth;
    static String status;
    static String nation;
    static String address;
    static ArrayList ward;
    static ArrayList txn;
    static String department;
    
    public static String read(String HN) throws ParserConfigurationException, SAXException, IOException, ParseException
    {
        init();
        /*
<data value="0">
* <hn value="2739313">
* <delete_from_mis>n</delete_from_mis>
* <pid>3500900468939</pid>
* <fnm>จันทร์</fnm>
* <lnm>บุญปัญญา</lnm>
* <sex>ช</sex>
* <dob>1944-07-08</dob>
* <sta>คู่</sta>
* <nat> ไทย</nat>
* <type>ประกันสุขภาพถ้วนหน้า(สูงอายุ >60 ปี)</type>
* <typem>ประกันสุขภาพถ้วนหน้า(สูงอายุ >60 ปี)</typem>
* <addr>60</addr>
* <moo>5</moo>
* <tam>หนองบัว</tam>
* <amp>ไชยปราการ</amp>
* <cwt>เชียงใหม่</cwt>
* <zip>50320</zip>
* <father>มา</father>
* <mother>หลาน</mother>
* <opd value="0" desc="มีประวัติการลงทะเบียน">
* <room code="68-B05" name="Med คลินิกตรวจโรคเลือด ชั้น 5 ตึก 7 ชั้น" date="2012-03-13" time="00:16" txn="105531453"/>
* <room code="06-S07" name="OPD เบอร์ 6 $" date="2012-03-13" time="00:16" txn="105531463"/>
* <room code="23-S11" name="OPD เบอร์ 23 $" date="2012-03-12" time="18:57" txn="105528832"/>
* </opd>
* <ipd value="0" desc="PT admitted">
* <room code="CHEM27" name="เคมีบำบัด (Ward)" adm_date="2012-03-13" time="10:36" txn="672355"/>
* </ipd>
* </hn>
* </data>
        */
        ward = new ArrayList();
        txn = new ArrayList();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        URL url = new URL("http://"+Variable.getHost()+Variable.getHNdirectory()+HN);//new URL("http://sddn.med.cmu.ac.th/report&program/program/vs_api/vs_search_pt.php?hn="+HN);
        URLConnection con = url.openConnection();
        con.setConnectTimeout(1000);
        Document doc = dBuilder.parse(con.getInputStream());
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("hn");
        Node nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                Element eElement = (Element) nNode;
                /*
                                if(getTagValue("pid", eElement).equals("no data"))
                                {
                                    return "No patient for this HN";
                                }
                                else
                                {*/
                                ID = getTagValue("pid", eElement);
                                name = getTagValue("fnm", eElement);
                                surname = getTagValue("lnm", eElement);
                                sex = getTagValue("sex", eElement);
                    if(sex.equals("ช")) 
                    {
                        sex = "ชาย";
                    }
                    else if(sex.equals("ญ"))
                    {
                        sex = "หญิง";
                    }
                    else
                    {
                        sex = "-";
                    }
                                birth = getTagValue("dob", eElement);
                                status = getTagValue("sta", eElement);
                                nation = getTagValue("nat", eElement);
                                if(!(getTagValue("addr", eElement).equals("no data")))
                                {
                                    address = getTagValue("addr", eElement);
                                }
                                if(!(getTagValue("moo", eElement).equals("no data")))
                                {
                                    address = address +" หมู่ "+getTagValue("moo", eElement);
                                }
                                if(!(getTagValue("tam", eElement).equals("no data")))
                                {
                                    address = address +" "+getTagValue("tam", eElement);
                                }
                                if(!(getTagValue("amp", eElement).equals("no data")))
                                {
                                    address = address +" "+getTagValue("amp", eElement);
                                }
                                if(!(getTagValue("cwt", eElement).equals("no data")))
                                {
                                    address = address +" "+getTagValue("cwt", eElement);
                                }
                                if(!(getTagValue("zip", eElement).equals("no data")))
                                {
                                    address = address +" "+getTagValue("zip", eElement);
                                }
                                nList = doc.getElementsByTagName("ipd");
                                Element e = (Element)nList.item(0);
                             
                                String admit;
                                
                                try
                                {
                                    admit = e.getAttribute("value");
                                }
                                catch(Exception ex)
                                {
                                    admit = "";
                                }
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.UK);
                                if(admit.equals("0"))
                                {
                                    department="ipd";
                                    Node node = nList.item(0).getFirstChild();
                                    if (node.getNodeType() == Node.ELEMENT_NODE) 
                                    {
                                        Element element = (Element) node;
                                        if(!"".equals(element.getAttribute("adm_date")))
                                            {
                                                ward.add(element.getAttribute("name"));
                                                txn.add(element.getAttribute("txn"));
                                            }
                                    }
                                }
                                else
                                {
                                    department="opd";
                                    nList = doc.getElementsByTagName("opd");
                                    e = (Element)nList.item(0);
                                    String visit = "";
                                    if(e!=null)
                                    {
                                        visit = e.getAttribute("value");
                                    }
                                    
                                    if(visit.equals("0"))
                                    {
                                        
                                        NodeList node = nList.item(0).getChildNodes();
                                        for(int i=0;i<node.getLength();i++)
                                        {
                                            e = (Element)node.item(i);
                                            Calendar record = Calendar.getInstance();
                                            record.setTime(format.parse(e.getAttribute("date")));
                                            Calendar today = Calendar.getInstance();
                                            today.setTime(new Date());
                                            if(record.get(Calendar.YEAR)==today.get(Calendar.YEAR) && record.get(Calendar.MONTH)==today.get(Calendar.MONTH) && record.get(Calendar.DATE)==today.get(Calendar.DATE))//if patient visits today
                                            {
                                                ward.add(e.getAttribute("name"));
                                                txn.add(e.getAttribute("txn"));
                                            }
                                        }
                                    }
                                    else
                                    {
                                        return "No registration record history";
                                    }
                                }
                                return "Success";
                                //}
            }
            else
            {
                return "No patient for this HN";
            }
 
  }
private static void init()
{
    //senderid = "";
    ID = "";
    name = "";
    surname = "";
    sex = "";
    birth = "";
    status = "";
    nation = "";
    address = "";
    ward = new ArrayList();
    department = "";
    txn = new ArrayList();
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
    /*
    public static String getSenderID()
    {
        return senderid;
    }*/
    public static String getID()
    {
        return ID;
    }
    public static String getName()
    {
        return name;
    }
    public static String getSurname()
    {
        return surname;
    }
    public static String getSex()
    {
        return sex;
    }
    public static String getBirth()
    {
        return birth;
    }
    public static String getStatus()
    {
        return status;
    }
    public static String getNation()
    {
        return nation;
    }
    public static String getAddress()
    {
        return address;
    }
    public static ArrayList getWard()
    {
        return ward;
    }
    public static String getDepartment()
    {
        return department;
    }
    public static ArrayList getTXN()
    {
        return txn;
    }
}
