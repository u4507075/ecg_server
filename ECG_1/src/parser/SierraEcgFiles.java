package parser;

import java.io.*;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;


public class SierraEcgFiles {
	static DecodedLead[] leads;
                Restingecgdata restingecgdata;
	public SierraEcgFiles() {
	}
	
	private void preprocess(JAXBContext context, File input) throws JAXBException, IOException {
                    Unmarshaller reader = context.createUnmarshaller();
                    restingecgdata = (Restingecgdata)reader.unmarshal(input);
                    leads = extractLeads(restingecgdata);
		
                    StringBuilder buffer = new StringBuilder();
                    for (DecodedLead lead : leads) 
                    {
                        for (int count = 0; count < lead.size(); ++count) 
                        {
        		buffer.append(lead.get(count));
        		if (count % 25 > 0 || count == 0) {
        			buffer.append(" ");
        		} 
        		else {
        			buffer.append("\n");
        		}
                        }
                    }
        
                    Parsedwaveforms parsedwaveforms = restingecgdata.getWaveforms().getParsedwaveforms();
                    parsedwaveforms.setDataencoding(TYPEdataencoding.PLAIN);
                    //parsedwaveforms.setCompressflag(TYPEflag.FALSE);
                    parsedwaveforms.setValue(buffer.toString());
	}
        
                private void preprocess(JAXBContext context, String input) throws JAXBException, IOException {
                    ByteArrayInputStream xml = new ByteArrayInputStream (input.getBytes()); 
                    Unmarshaller reader = context.createUnmarshaller();
                    //restingecgdata = (Restingecgdata)reader.unmarshal(input);
                    StringBuilder xmlStr = new StringBuilder(input);
                    restingecgdata = (Restingecgdata)reader.unmarshal(xml);//( new StreamSource( new StringReader( xmlStr.toString() ) ) );
                    leads = extractLeads(restingecgdata);
		
                    StringBuilder buffer = new StringBuilder();
                    for (DecodedLead lead : leads) 
                    {
                        for (int count = 0; count < lead.size(); ++count) 
                        {
        		buffer.append(lead.get(count));
        		if (count % 25 > 0 || count == 0) {
        			buffer.append(" ");
        		} 
        		else {
        			buffer.append("\n");
        		}
                        }
                    }
        
                    Parsedwaveforms parsedwaveforms = restingecgdata.getWaveforms().getParsedwaveforms();
                    parsedwaveforms.setDataencoding(TYPEdataencoding.PLAIN);
                    //parsedwaveforms.setCompressflag(TYPEflag.FALSE);
                    parsedwaveforms.setValue(buffer.toString());
	}
	
	public void preprocess(File input) throws IOException, JAXBException {
            
                                ClassLoader cl = parser.ObjectFactory.class.getClassLoader();
		JAXBContext context = JAXBContext.newInstance("parser",cl);
		preprocess(context, input);
	}
                public void preprocess(String input) throws IOException, JAXBException {
                    
                                ClassLoader cl = parser.ObjectFactory.class.getClassLoader();
		JAXBContext context = JAXBContext.newInstance("parser",cl);
		preprocess(context, input);
	}
                public Restingecgdata getRestingecgdata()
                {
                    return restingecgdata;
                }
                public DecodedLead[] getLeads()
                {
                    return leads;
                }
	
	public void preprocess(File input, File output) throws IOException, JAXBException {
		JAXBContext context = JAXBContext.newInstance("parser");
		
		preprocess(context, input);
        
        Marshaller writer = context.createMarshaller();
        writer.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        writer.marshal(restingecgdata, output);
	}
	
	private DecodedLead[] extractLeads(Restingecgdata input) throws IOException {
		Parsedwaveforms parsedwaveforms = input.getWaveforms().getParsedwaveforms();
		
		InputStream in = new ByteArrayInputStream(parsedwaveforms.getValue().getBytes());
		if (parsedwaveforms.getDataencoding() == TYPEdataencoding.BASE_64) {
			in = new Base64.InputStream(in);
		}
		
		//ArrayList<int[]> leadData = new ArrayList<>();
                                ArrayList leadData = new ArrayList();
		if (parsedwaveforms.getCompression().equals(TYPEcompress.XLI.value())) {
			XliDecompressor xli = new XliDecompressor(in);
			int[] payload;
			while (null != (payload = xli.readLeadPayload())) {
				leadData.add(payload);
			}
		}
		
		TYPEreportlabel reporttype = input.getReportinfo().getReportlabel();
		leads = DecodedLead.createFromLeadSet(reporttype.value(), leadData);
		
		return leads;
	}
	
	public DecodedLead[] extractLeads(File input) throws IOException, JAXBException {
		JAXBContext context = JAXBContext.newInstance("parser");
		Unmarshaller reader = context.createUnmarshaller();
		restingecgdata = (Restingecgdata)reader.unmarshal(input);
		return extractLeads(restingecgdata);
	}
}
