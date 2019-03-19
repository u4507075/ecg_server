/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package print;

import ecg.DrawECG;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.*;
import javax.xml.bind.JAXBException;

/**
 *
 * @author bon
 */
public class TestPrint implements Printable{

    public final float DPI = 72;
    public DrawECG z;
    public void start(DrawECG z) {
        this.z = z;
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                /*
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }
*/
                float width = cmToPixel(21f, DPI);
                float height = cmToPixel(29.7f, DPI);

                Paper paper = new Paper();
                float margin = cmToPixel(1, DPI);
                paper.setImageableArea(margin, margin, width - (margin * 2), height - (margin * 2));
                PageFormat pf = new PageFormat();
                pf.setOrientation(PageFormat.LANDSCAPE);
                pf.setPaper(paper);

                BufferedImage img = new BufferedImage(Math.round(width), Math.round(height), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fill(new Rectangle2D.Float(0, 0, width, height));
                try {
                    g2d.setClip(new Rectangle2D.Double(pf.getImageableX(), pf.getImageableY(), pf.getImageableWidth(), pf.getImageableHeight()));
                    print(g2d, pf, 0);
                } catch (PrinterException ex) {
                }
                g2d.dispose();
            }
        });

    }
    public void directprint() throws FileNotFoundException, PrintException, IOException
    {
    PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
    pras.add(new Copies(1));
    pras.add(null);
    PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.GIF, pras);
    if (pss.length == 0)
      throw new RuntimeException("No printer services available.");
    PrintService ps = pss[0];
    System.out.println("Printing to " + ps);
    DocPrintJob job = ps.createPrintJob();
        try (FileInputStream fin = new FileInputStream("YOurImageFileName.PNG")) {
            Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.GIF, null);
            job.print(doc, pras);
        }
    }

    public float cmToPixel(float cm, float dpi) {

        return (dpi / 2.54f) * cm;

    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int page) throws PrinterException {

        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g = (Graphics2D) graphics;

        g.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        if(z!=null)
        {
            try {
                z.drawECG(g);
            } catch (JAXBException | IOException ex) {
                Logger.getLogger(TestPrint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return PAGE_EXISTS;
    }
}
