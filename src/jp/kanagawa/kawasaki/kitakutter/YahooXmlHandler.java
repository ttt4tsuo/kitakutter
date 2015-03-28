package jp.kanagawa.kawasaki.kitakutter;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class YahooXmlHandler extends DefaultHandler {
	boolean sflag = false;
	boolean eflag = false;
	ArrayList<String> ret = new ArrayList<String>();
	
	YahooXmlHandler() {
		sflag = false;
		eflag = false;
    }
	public ArrayList<String> getResult(){
		return ret;
	}
	
	@Override
	public void startDocument() throws SAXException {
	}
	@Override
	public void endDocument() throws SAXException {
	}
	@Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		if(localName.equals("surface")){
			sflag = true;
		}
	}
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		sflag = false;
	}
	@Override
    public void characters(char ch[], int start, int length) {
		if (sflag && !eflag) {
	        String theString = new String(ch, start, length);
	        if(theString.equals("http")){
	        	eflag = true;
	        	ret.add("end");
	        }else{
	        	ret.add((theString));
	        	if(theString.equals("�B")){
	        		ret.add("end");
	        	}
	        }
    	}
	}
	
}