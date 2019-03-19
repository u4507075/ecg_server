/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package print;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.keys.BooleanKey;
import org.apache.batik.transcoder.keys.LengthKey;
import org.apache.batik.transcoder.keys.StringKey;
/**
 *
 * @author bon
 */
public class PrintTranscoder extends SVGAbstractTranscoder implements Printable {

    public static final String KEY_AOI_STR = "aoi";
    public static final String KEY_HEIGHT_STR = "height";
    public static final String KEY_LANGUAGE_STR = "language";
    public static final String KEY_MARGIN_BOTTOM_STR = "marginBottom";
    public static final String KEY_MARGIN_LEFT_STR = "marginLeft";
    public static final String KEY_MARGIN_RIGHT_STR = "marginRight";
    public static final String KEY_MARGIN_TOP_STR = "marginTop";
    public static final String KEY_PAGE_HEIGHT_STR = "pageHeight";
    public static final String KEY_PAGE_ORIENTATION_STR         = "pageOrientation";
    public static final String KEY_PAGE_WIDTH_STR = "pageWidth";
    public static final String KEY_PIXEL_TO_MM_STR = "pixelToMm";
    public static final String KEY_SCALE_TO_PAGE_STR         = "scaleToPage";
    public static final String KEY_SHOW_PAGE_DIALOG_STR = "showPageDialog";
    public static final String KEY_SHOW_PRINTER_DIALOG_STR = "showPrinterDialog";
    public static final String KEY_USER_STYLESHEET_URI_STR = "userStylesheet";
    public static final String KEY_WIDTH_STR = "width";
    public static final String KEY_XML_PARSER_CLASSNAME_STR = "xmlParserClassName";
    public static final String VALUE_MEDIA_PRINT = "print";
    public static final String VALUE_PAGE_ORIENTATION_LANDSCAPE = "landscape";
    public static final String VALUE_PAGE_ORIENTATION_PORTRAIT  = "portrait";
    public static final String VALUE_PAGE_ORIENTATION_REVERSE_LANDSCAPE = "reverseLandscape";

    /**
     * Set of inputs this transcoder has been requested to
     * transcode so far
     */
    //private Vector inputs = new Vector();
    private ArrayList inputs = new ArrayList();



    /**
     * Currently printing set of pages. This vector is
     * created as a clone of inputs when the first page is printed.
     */
    //private Vector printedInputs = null;
    private ArrayList printedInputs = null;

    /**
     * Index of the page corresponding to root
     */
    private int curIndex = -1;

    float A4WIDTH = 297*4;
    float A4HEIGHT = 210*4;
    //float MARGIN = 0.5f;
    /**
     * Constructs a new transcoder that prints images.
     */
    public PrintTranscoder() {
        super();

        hints.put(KEY_MEDIA,
                  VALUE_MEDIA_PRINT);
        hints.put(KEY_PAGE_ORIENTATION,VALUE_PAGE_ORIENTATION_LANDSCAPE);
        hints.put(KEY_SCALE_TO_PAGE,true);
        hints.put(KEY_HEIGHT,A4HEIGHT);
        hints.put(KEY_WIDTH,A4WIDTH);

        //hints.put(KEY_SHOW_PRINTER_DIALOG,true);
        //hints.put(KEY_SHOW_PAGE_DIALOG,true);
        /*
        hints.put(KEY_MARGIN_BOTTOM,MARGIN);
        hints.put(KEY_MARGIN_LEFT,MARGIN);
        hints.put(KEY_MARGIN_RIGHT,MARGIN);
        hints.put(KEY_MARGIN_TOP,MARGIN);*/
    }

    @Override
    public void transcode(TranscoderInput in,
                          TranscoderOutput out){
        if(in != null){
            //inputs.addElement(in);
            inputs.add(in);
        }
    }

    /**
     * Convenience method
     */
    public void print() throws PrinterException{
        //
        // Now, request the transcoder to actually perform the
        // printing job.
        //
        PrinterJob printerJob =
            PrinterJob.getPrinterJob();

        PageFormat pageFormat =
            printerJob.defaultPage();

        //
        // Set the page parameters from the hints
        //
        Paper paper = pageFormat.getPaper();

        Float pageWidth = (Float)hints.get(KEY_PAGE_WIDTH);
        Float pageHeight = (Float)hints.get(KEY_PAGE_HEIGHT);
        if(pageWidth != null){
            paper.setSize(pageWidth.floatValue(),
                          paper.getHeight());
        }
        if(pageHeight != null){
            paper.setSize(paper.getWidth(),
                          pageHeight.floatValue());
        }

        float x=0, y=0;
        float w=(float)paper.getWidth(), h=(float)paper.getHeight();

        Float leftMargin = (Float)hints.get(KEY_MARGIN_LEFT);
        Float topMargin = (Float)hints.get(KEY_MARGIN_TOP);
        Float rightMargin = (Float)hints.get(KEY_MARGIN_RIGHT);
        Float bottomMargin = (Float)hints.get(KEY_MARGIN_BOTTOM);

        if(leftMargin != null){
            x = leftMargin.floatValue();
            w -= leftMargin.floatValue();
        }
        if(topMargin != null){
            y = topMargin.floatValue();
            h -= topMargin.floatValue();
        }
        if(rightMargin != null){
            w -= rightMargin.floatValue();
        }
        if(bottomMargin != null){
            h -= bottomMargin.floatValue();
        }

        paper.setImageableArea(x, y, w, h);

        String pageOrientation = (String)hints.get(KEY_PAGE_ORIENTATION);
        if(VALUE_PAGE_ORIENTATION_PORTRAIT.equalsIgnoreCase(pageOrientation)){
            pageFormat.setOrientation(PageFormat.PORTRAIT);
        }
        else if(VALUE_PAGE_ORIENTATION_LANDSCAPE.equalsIgnoreCase(pageOrientation)){
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
        }
        else if(VALUE_PAGE_ORIENTATION_REVERSE_LANDSCAPE.equalsIgnoreCase(pageOrientation)){
            pageFormat.setOrientation(PageFormat.REVERSE_LANDSCAPE);
        }

        pageFormat.setPaper(paper);
        pageFormat = printerJob.validatePage(pageFormat);

        //
        // If required, pop up a dialog to adjust the page format
        //
        Boolean showPageFormat = (Boolean)hints.get(KEY_SHOW_PAGE_DIALOG);
        if(showPageFormat != null && showPageFormat.booleanValue()){
            PageFormat tmpPageFormat = printerJob.pageDialog(pageFormat);
            if(tmpPageFormat == pageFormat){
                // Dialog was cancelled, meaning that the print process should
                // be stopped.
                return;
            }

            pageFormat = tmpPageFormat;
        }

        //
        // If required, pop up a dialog to select the printer
        //
        Boolean showPrinterDialog = (Boolean)hints.get(KEY_SHOW_PRINTER_DIALOG);
        if(showPrinterDialog != null && showPrinterDialog.booleanValue()){
            if(!printerJob.printDialog()){
                // Dialog was cancelled, meaning that the print process
                // should be stopped.
                return;
            }
        }

        // Print now
        printerJob.setPrintable(this, pageFormat);
        printerJob.print();

    }

    /**
     * Printable implementation
     */
    @Override
    public int print(Graphics _g, PageFormat pageFormat, int pageIndex){
        //
        // On the first page, take a snapshot of the vector of
        // TranscodeInputs.
        //
        if(pageIndex == 0){
            //printedInputs = (Vector)inputs.clone();
            printedInputs = (ArrayList)inputs.clone();
        }

        //
        // If we have already printed each page, return
        //
        if(pageIndex >= printedInputs.size()){
            curIndex = -1;
            System.out.println("Printed");
            return NO_SUCH_PAGE;
        }

        //
        // Load a new document now if we are printing a new page
        //
        if(curIndex != pageIndex){
            // The following call will invoke this class' transcode
            // method which takes a document as an input. That method
            // builds the GVT root tree.{
            try{
                //super.transcode((TranscoderInput)printedInputs.elementAt(pageIndex),
                super.transcode((TranscoderInput)printedInputs.get(pageIndex),
                                null);
                curIndex = pageIndex;
            }catch(TranscoderException e){
                drawError(_g, e);
                return PAGE_EXISTS;
            }
        }

        // Cast to Graphics2D to access Java 2D features
        Graphics2D g = (Graphics2D)_g;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING,
                           RenderingHintsKeyExt.VALUE_TRANSCODING_PRINTING);

        //
        // Compute transform so that the SVG document fits on one page
        //
        AffineTransform t = g.getTransform();
        Shape clip = g.getClip();

        Rectangle2D bounds = curAOI;

        double scaleX = pageFormat.getImageableWidth() / bounds.getWidth();
        double scaleY = pageFormat.getImageableHeight() / bounds.getHeight();
        double scale = scaleX < scaleY ? scaleX : scaleY;

        // Check hint to know if scaling is really needed
        Boolean scaleToPage = (Boolean)hints.get(KEY_SCALE_TO_PAGE);
        if(scaleToPage != null && !scaleToPage.booleanValue()) {
            // Printing Graphics is always set up for 72dpi, so scale
            // according to what user agent thinks it should be.
            double pixSzMM = userAgent.getPixelUnitToMillimeter();
            double pixSzInch = (25.4/pixSzMM);
            scale = 72/pixSzInch;
        }

        double xMargin = (pageFormat.getImageableWidth() - 
                          bounds.getWidth()*scale)/2;
        double yMargin = (pageFormat.getImageableHeight() - 
                          bounds.getHeight()*scale)/2;
        g.translate(pageFormat.getImageableX() + xMargin,pageFormat.getImageableY() + yMargin);
        //g.translate(5, 5);
        g.scale(scale, scale);
        //g.scale(0.5,0.5);
        //g.drawRect((int)xMargin, (int)yMargin, (int)bounds.getWidth(), (int)bounds.getHeight());
        //
        // Append transform to selected area
        //
        g.transform(curTxf);

        g.clip(curAOI);

        //
        // Delegate rendering to painter
        //
        try{
            root.paint(g);
        }catch(Exception e){
            g.setTransform(t);
            g.setClip(clip);
            drawError(_g, e);
        }

        //
        // Restore transform and clip
        //
        g.setTransform(t);
        g.setClip(clip);

        // g.setPaint(Color.black);
        // g.drawString(uris[pageIndex], 30, 30);


        //
        // Return status indicated that we did paint a page
        //
        return PAGE_EXISTS;
    }

    /**
     * Prints an error on the output page
     */
    private void drawError(Graphics g, Exception e){
        // Do nothing now.
    }

    // --------------------------------------------------------------------
    // Keys definition
    // --------------------------------------------------------------------

    /**
     * The showPageDialog key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_SHOW_PAGE_DIALOG</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">false</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specifies whether or not the transcoder
     *                  should pop up a dialog box for selecting
     *                  the page format.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_SHOW_PAGE_DIALOG
        = new BooleanKey();

    /**
     * The showPrinterDialog key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_SHOW_PAGE_DIALOG</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">false</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specifies whether or not the transcoder
     *                  should pop up a dialog box for selecting
     *                  the printer. If the dialog box is not
     *                  shown, the transcoder will use the default
     *                  printer.</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_SHOW_PRINTER_DIALOG
        = new BooleanKey();


    /**
     * The pageWidth key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_PAGE_WIDTH</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The width of the print page</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_PAGE_WIDTH
        = new LengthKey();

    /**
     * The pageHeight key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_PAGE_HEIGHT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The height of the print page</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_PAGE_HEIGHT
        = new LengthKey();

    /**
     * The marginTop key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_MARGIN_TOP</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The print page top margin</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_MARGIN_TOP
        = new LengthKey();

    /**
     * The marginRight key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_MARGIN_RIGHT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The print page right margin</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_MARGIN_RIGHT
        = new LengthKey();

    /**
     * The marginBottom key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_MARGIN_BOTTOM</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The print page bottom margin</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_MARGIN_BOTTOM
        = new LengthKey();

    /**
     * The marginLeft key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_MARGIN_LEFT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Length</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">None</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The print page left margin</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_MARGIN_LEFT
        = new LengthKey();

    /**
     * The pageOrientation key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_PAGE_ORIENTATION</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">String</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">VALUE_PAGE_ORIENTATION_PORTRAIT</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">The print page's orientation</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_PAGE_ORIENTATION
        = new StringKey();


    /**
     * The scaleToPage key.
     * <TABLE BORDER="0" CELLSPACING="0" CELLPADDING="1">
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Key: </TH>
     * <TD VALIGN="TOP">KEY_SCALE_TO_PAGE</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Value: </TH>
     * <TD VALIGN="TOP">Boolean</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Default: </TH>
     * <TD VALIGN="TOP">true</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Required: </TH>
     * <TD VALIGN="TOP">No</TD></TR>
     * <TR>
     * <TH VALIGN="TOP" ALIGN="RIGHT"><P ALIGN="RIGHT">Description: </TH>
     * <TD VALIGN="TOP">Specifies whether or not the SVG images are scaled to
     *                  fit into the printed page</TD></TR>
     * </TABLE> */
    public static final TranscodingHints.Key KEY_SCALE_TO_PAGE
        = new BooleanKey();

    public static final String USAGE = "java org.apache.batik.transcoder.print.PrintTranscoder <svgFileToPrint>";

    public void print(File file) throws Exception{

        //
        // Builds a PrintTranscoder
        //
        PrintTranscoder transcoder = new PrintTranscoder();

        //
        // Set the hints, from the command line arguments
        //

        // Language
        setTranscoderFloatHint(transcoder,
                               KEY_LANGUAGE_STR,
                               KEY_LANGUAGE);

        // User stylesheet
        setTranscoderFloatHint(transcoder,
                               KEY_USER_STYLESHEET_URI_STR,
                               KEY_USER_STYLESHEET_URI);

        // XML parser
        setTranscoderStringHint(transcoder,
                                 KEY_XML_PARSER_CLASSNAME_STR,
                                 KEY_XML_PARSER_CLASSNAME);

        // Scale to page
        setTranscoderBooleanHint(transcoder,
                                 KEY_SCALE_TO_PAGE_STR,
                                 KEY_SCALE_TO_PAGE);

        // AOI
        setTranscoderRectangleHint(transcoder,
                                   KEY_AOI_STR,
                                   KEY_AOI);


        // Image size
        setTranscoderFloatHint(transcoder,
                               KEY_WIDTH_STR,
                               KEY_WIDTH);
        setTranscoderFloatHint(transcoder,
                               KEY_HEIGHT_STR,
                               KEY_HEIGHT);

        // Pixel to millimeter
        setTranscoderFloatHint(transcoder,
                               KEY_PIXEL_TO_MM_STR,
                               KEY_PIXEL_UNIT_TO_MILLIMETER);

        // Page orientation
        setTranscoderStringHint(transcoder,
                                KEY_PAGE_ORIENTATION_STR,
                                KEY_PAGE_ORIENTATION);

        // Page size
        setTranscoderFloatHint(transcoder,
                               KEY_PAGE_WIDTH_STR,
                               KEY_PAGE_WIDTH);
        setTranscoderFloatHint(transcoder,
                               KEY_PAGE_HEIGHT_STR,
                               KEY_PAGE_HEIGHT);

        // Margins
        setTranscoderFloatHint(transcoder,
                               KEY_MARGIN_TOP_STR,
                               KEY_MARGIN_TOP);
        setTranscoderFloatHint(transcoder,
                               KEY_MARGIN_RIGHT_STR,
                               KEY_MARGIN_RIGHT);
        setTranscoderFloatHint(transcoder,
                               KEY_MARGIN_BOTTOM_STR,
                               KEY_MARGIN_BOTTOM);
        setTranscoderFloatHint(transcoder,
                               KEY_MARGIN_LEFT_STR,
                               KEY_MARGIN_LEFT);

        // Dialog options
        setTranscoderBooleanHint(transcoder,
                                 KEY_SHOW_PAGE_DIALOG_STR,
                                 KEY_SHOW_PAGE_DIALOG);

        setTranscoderBooleanHint(transcoder,
                                 KEY_SHOW_PRINTER_DIALOG_STR,
                                 KEY_SHOW_PRINTER_DIALOG);

        //
        // First, request the transcoder to transcode
        // each of the input files
        //
            transcoder.transcode(new TranscoderInput(new FileInputStream(file)),null);

        //
        // Now, print...
        //
        transcoder.print();
    }

    public static void setTranscoderFloatHint(Transcoder transcoder,
                                              String property,
                                              TranscodingHints.Key key){
        String str = System.getProperty(property);
        if(str != null){
            try{
                Float value = new Float(Float.parseFloat(str));
                transcoder.addTranscodingHint(key, value);
            }catch(NumberFormatException e){
                handleValueError(property, str);
            }
        }
    }

    public static void setTranscoderRectangleHint(Transcoder transcoder,
                                                  String property,
                                                  TranscodingHints.Key key){
        String str = System.getProperty(property);
        if(str != null){
            StringTokenizer st = new StringTokenizer(str, " ,");
            if(st.countTokens() != 4){
                handleValueError(property, str);
            }

            try{
                String x = st.nextToken();
                String y = st.nextToken();
                String width = st.nextToken();
                String height = st.nextToken();
                Rectangle2D r = new Rectangle2D.Float(Float.parseFloat(x),
                                                      Float.parseFloat(y),
                                                      Float.parseFloat(width),
                                                      Float.parseFloat(height));
                transcoder.addTranscodingHint(key, r);
            }catch(NumberFormatException e){
                handleValueError(property, str);
            }
        }
    }

    public static void setTranscoderBooleanHint(Transcoder transcoder,
                                                String property,
                                                TranscodingHints.Key key){
        String str = System.getProperty(property);
        if(str != null){
            Boolean value = "true".equalsIgnoreCase(str);
            transcoder.addTranscodingHint(key, value);
        }
    }

    public static void setTranscoderStringHint(Transcoder transcoder,
                                              String property,
                                              TranscodingHints.Key key){
        String str = System.getProperty(property);
        if(str != null){
            transcoder.addTranscodingHint(key, str);
        }
    }

    public static void handleValueError(String property,
                                        String value){
        System.err.println("Invalid " + property + " value : " + value);
        System.exit(1);
    }

}