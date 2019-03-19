/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import ecg.ECGView;
import ecg.Variable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author bon
 */
public class read {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Variable var = new Variable();
            var.init();
            String filepath = "/home/bon/Documents/Project/ECG project/ecgwithsignalproblem.xml";
            ECGView v = new ECGView();
        try {
            v.drawGridfromfile(filepath);
        } catch (JAXBException ex) {
            Logger.getLogger(read.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(read.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(read.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(read.class.getName()).log(Level.SEVERE, null, ex);
        }
            v.setVisible(true);
    }
}
