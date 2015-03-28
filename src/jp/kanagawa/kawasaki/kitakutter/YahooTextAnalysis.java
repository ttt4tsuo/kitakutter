package jp.kanagawa.kawasaki.kitakutter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.kanagawa.kawasaki.kitakutter.TweetDB.DataColumns;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class YahooTextAnalysis{
	public YahooTextAnalysis(){
		;
	}
	public void jaDataPush(String inputString,Context context){
    	try{
    		String targetString = URLEncoder.encode(inputString, "UTF-8");
    		KeyStore ks = new KeyStore(); 
    		StringBuilder urlstr = new StringBuilder("http://jlp.yahooapis.jp/MAService/V1/parse?appid=");
    		urlstr.append(ks.getYahooAppid());
    		urlstr.append("&results=ma&ma_response=surface,pos&sentence=");
    		urlstr.append(targetString);
    		URL url = new URL(urlstr.toString());
    		HttpURLConnection request = (HttpURLConnection)url.openConnection();
	        request.setRequestMethod("GET");
	        request.connect();
			
			InputSource is = new InputSource(request.getInputStream());
			SAXParserFactory factory = SAXParserFactory.newInstance();
	        SAXParser parser = factory.newSAXParser();
	        XMLReader xmlreader = parser.getXMLReader();
	        YahooXmlHandler hyahooxml = new YahooXmlHandler();
	        xmlreader.setContentHandler(hyahooxml);
	        xmlreader.parse(is);
	        
	        ArrayList<String> alist = hyahooxml.getResult();
	        TweetDB mTweetDB = new TweetDB(context);

	        for(int i=0;i<=alist.size()-2;i++){
	        	ContentValues cv = new ContentValues();
	        	if(alist.size()<3){
	        		break;
	        	}
	        	if(i == alist.size()-2){
	        		cv.put(DataColumns.FIRST,alist.get(i));
		        	cv.put(DataColumns.SECOND,alist.get(i+1));
		        	cv.put(DataColumns.THIRD,"end");
		        	mTweetDB.insert(cv);
		        	cv.clear();
		        	break;
	        	}
	        	if(alist.get(i+1).equals("end") || alist.get(i).equals("end")){
	        		;
	        	}else{
	        		cv.put(DataColumns.FIRST,alist.get(i));
	        		cv.put(DataColumns.SECOND,alist.get(i+1));
	        		cv.put(DataColumns.THIRD,alist.get(i+2));
	        		mTweetDB.insert(cv);
	        		cv.clear();
	        	}
	        }
	        mTweetDB.closeDB();
	        
    	}catch (Exception e){
    		e.printStackTrace();
			Toast.makeText(context,"YahooTextAnalysis: Error." ,Toast.LENGTH_LONG).show();
			return;
    	}

	}
	
	public void enDataPush(String inputString,Context context){
		final String[] inSplitString = inputString.split(" ");
		TweetDB mTweetDB = new TweetDB(context);
        for(int i=0;i<=inSplitString.length-2;i++){
        	Log.d("Critical",String.valueOf(inSplitString.length));
        	ContentValues cv = new ContentValues();
        	if(inSplitString.length<3){
        		break;
        	}
        	if(i == inSplitString.length-2){
        		cv.put(DataColumns.FIRST,inSplitString[i]);
	        	cv.put(DataColumns.SECOND,inSplitString[i+1]);
	        	cv.put(DataColumns.THIRD,"end");
	        	mTweetDB.insert(cv);
	        	cv.clear();
	        	break;
        	}
        	if(inSplitString[i+1].equals("end") || inSplitString[i].equals("end")){
        		;
        	}else{
        		cv.put(DataColumns.FIRST,inSplitString[i]);
        		cv.put(DataColumns.SECOND,inSplitString[i+1]);
        		cv.put(DataColumns.THIRD,inSplitString[i+2]);
        		mTweetDB.insert(cv);
        		cv.clear();
        	}
        }
        mTweetDB.closeDB();	
	}
	
}