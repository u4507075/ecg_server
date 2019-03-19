/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.batik.transcoder.TranscoderException;
import org.xml.sax.SAXException;

/**
 *
 * @author bon
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        

            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                try {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());

                    break;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
            }
            
        
            test(args);
            //SVGConverter(args);
            //ECGRecorder(args);
            //ECGViewer(args);
            
        
        
        
    }
    private static void SVGConverter(String[] args)
    {
        /*
            //SVG converter
            args = new String[3];
            args[0] = "/home/bon/Downloads/test/107037028_20130430110056.xml";
            args[1] = "/home/bon/Downloads/output.svg";
            args[2] = "/home/bon/Downloads/output.png";
            */
            
        try {
            SVGconverter s = new SVGconverter();
            s.main(args);
            
            } catch (ParserConfigurationException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TranscoderException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void ECGViewer(String[] args)
    {
        if(args.length>1)
        {
            //java -jar JRunner.jar http://localhost/public_html/ECG/ECG.jar 3250043 http://localhost/public_html/ECG/ECG.properties
            //Get variables from property file
            Variable var = new Variable();
            var.initbyURL(args[1]);
            //var.init();
            //args[0] = senderid
            //ECGRecorder.main(args);
            //args[0] = HN
            ECGView.main(args);
        }
        else if(args.length>0)
        {
            //Get variables from property file
            Variable var = new Variable();
            var.init();
            //args[0] = senderid
            //ECGRecorder.main(args);
            //args[0] = HN
            ECGView.main(args);
        }
        else
        {
            System.out.println("This program needs a sender ID to initialize.");
        }
    }
    private static void ECGRecorder(String[] args)
    {
         if(args.length>1)
        {
            //java -jar JRunner.jar http://localhost/public_html/ECG/ECG.jar 3250043 http://localhost/public_html/ECG/ECG.properties
            //Get variables from property file
            Variable var = new Variable();
            var.initbyURL(args[1]);
            //var.init();
            //args[0] = senderid
            ECGRecorder.main(args);
            //args[0] = HN
            //ECGView.main(args);
        }
        else if(args.length>0)
        {
            //Get variables from property file
            Variable var = new Variable();
            var.init();
            //args[0] = senderid
            ECGRecorder.main(args);
            //args[0] = HN
            //ECGView.main(args);
        }
        else
        {
            System.out.println("This program needs a sender ID to initialize.");
        }
        
    }
    private static void test(String[] args)
    {
            
            args = new String[1];
            args[0] = "2803589";
            //args[0] = "3279470";
            Variable var = new Variable();
            var.init();
            //ECGRecorder.main(args);
            ECGView.main(args);
            
    }
}
