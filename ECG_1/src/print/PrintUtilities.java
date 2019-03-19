package print;

import ecg.DrawECG;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.print.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RepaintManager;
import javax.xml.bind.JAXBException;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
  private DrawECG z;
  int A4WIDTH = 297*4;
  int A4HEIGHT = 210*4;

  public void printComponent(DrawECG z) {
        this.z = z;
  }
  
  public PrintUtilities() {
    
  }
  /*
  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
          /*
        PageFormat f = printJob.defaultPage();
       f.setOrientation(PageFormat.LANDSCAPE);
       Paper paper = new Paper();
       paper.setSize(A4WIDTH,A4HEIGHT);
       f.setPaper(paper);*/
  
  /*
        printJob.print();
      } catch(PrinterException pe) {
        System.out.println("Error printing: " + pe);
      }
  }
*/
    @Override
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D ourGraphics = (Graphics2D)g;
      /*
      ourGraphics.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      double scale = (new BigDecimal(pageFormat.getImageableHeight()).divide(new BigDecimal(A4HEIGHT),10,BigDecimal.ROUND_HALF_UP)).doubleValue();
      AffineTransform tx = ourGraphics.getTransform();
            tx.translate(0,0);
            tx.scale(scale, scale);
            ourGraphics.setTransform(tx);*/
            ourGraphics.setColor(Color.white);
            ourGraphics.fillRect(0,0,A4WIDTH,A4HEIGHT);
            ourGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
            ourGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      //g2d.scale(scale,scale);
        try {
            z.drawECG(ourGraphics);
        } catch (    JAXBException | IOException ex) {
            Logger.getLogger(PrintUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
      //disableDoubleBuffering(componentToBePrinted);
      //componentToBePrinted.paint(g2d);
      //enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
