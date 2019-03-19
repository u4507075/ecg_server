/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ecg;

import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author bon
 */
public class SVGconverter {
    int A4WIDTH = 297*4;
    int A4HEIGHT = 210*4;
    int scale = 2;
    public void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerConfigurationException, TransformerException, JAXBException, TranscoderException
    {
        Variable var = new Variable();
        var.init();
        SVGconverter con = new SVGconverter();
        //File f = new File("/home/bon/Downloads/test/107037028_20130430110056.xml");
        File f = new File(args[0]);
        
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = dbFactory.newDocumentBuilder();
        Document doc = builder.parse(f);
        String xml = con.DoctoString(doc);
        xml = xml.replaceAll("�", "");
        //String output = "/home/bon/Downloads/output.svg";
        String output = args[1];
        
        System.out.println("Start converting XML to SVG. Please wait...");
        con.saveFile(xml, output);
        System.out.println("Successfully converted XML to SVG");
        //String png = "/home/bon/Downloads/output.png";
        String png = args[2];
        System.out.println("Start converting SVG to PNG. Please wait...");
        con.SVGtoPNG(output, png);
        System.out.println("Successfully converted SVG to PNG");
        System.out.println("Bye Bye");
        System.exit(0);
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
    public void SVGtoPNG(String input, String output) throws MalformedURLException, FileNotFoundException, TranscoderException, IOException
    {
        //Step -1: We read the input SVG document into Transcoder Input
        //We use Java NIO for this purpose
        String svg_URI_input = Paths.get(input).toUri().toURL().toString();
        TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);        
        try (OutputStream png_ostream = new FileOutputStream(output)) {
            TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);              
         
            // Step-3: Create PNGTranscoder and define hints if required
            PNGTranscoder my_converter = new PNGTranscoder();      
            
            my_converter.addTranscodingHint( PNGTranscoder.KEY_WIDTH, new Float(A4WIDTH*scale) );
            my_converter.addTranscodingHint( PNGTranscoder.KEY_HEIGHT, new Float(A4HEIGHT*scale) );
            // Step-4: Convert and Write output
            my_converter.transcode(input_svg_image, output_png_image);
            // Step 5- close / flush Output Stream
            png_ostream.flush();
        }
    }
    private void saveFile(String xml, String output) throws IOException, JAXBException, TranscoderException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {

            //try {
               
            // Get a DOMImplementation.
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
            // Create an instance of org.w3c.dom.Document.
            String svgNS = "";
            Document document = domImpl.createDocument(svgNS, "svg", null);
            
            File svgFile = new File(output);

             // Ask the test to render into the SVG Graphics2D implementation.
                ecg.Type t = new ecg.Type();
                String type = t.getTypefromstring(xml);
                
                ECGvalue v = new ECGvalue();
                switch (type) {
                    case Type.PHILIPS:
                        v.readPhilipsfromstring(xml);
                        break;
                    case Type.GE:
                        v.readGEfromstring(xml);
                        break;
                    case Type.E4L:
                        v.readE4Lfromstring(xml);
                        break;
                }
                
                SVGGraphics2D g = new SVGGraphics2D(document);
                AffineTransform tx = g.getTransform();
            tx.translate(0,0);
            tx.scale(scale, scale);
            g.setTransform(tx);
                DrawECG z = new DrawECG(type);
                //z.drawECGfromstring(xml, 1, new JPanel(), new JPanel());
                z.drawECG(g);
                
                

                try (OutputStream outputStream = new FileOutputStream(svgFile); Writer out = new OutputStreamWriter(outputStream, "UTF-8")) {
                    g.stream(out, true);///true = use css
                    outputStream.flush();
                }
        /*}
        catch (ParserConfigurationException ex) {
            Logger.getLogger(SVGconverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SVGconverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SVGconverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SVGconverter.class.getName()).log(Level.SEVERE, null, ex);
        }*/    }
}
