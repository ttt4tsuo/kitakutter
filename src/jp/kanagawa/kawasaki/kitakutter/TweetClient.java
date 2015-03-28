package jp.kanagawa.kawasaki.kitakutter;

import java.util.HashMap;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.util.Log;


public class TweetClient{
	private Twitter twitter4j;
	private RequestToken mTwitter4j_RequestToken;
	private Uri mAuthurl;
	private String mToken;
	private String mTokenSecret;
	
	/*
	 * twitter4jの流れ
	 * ①メイクオース
	 * setOAuthConsumer, getOAuthRequestToken, getAuthorizationURL
	 * ②メイクトークン
	 * getOAuthAccessToken
	 * ③セット、AccessToken取得後は、RequestTokenは不要
	 * setOAuthAccessToken
	 */
	
    public TweetClient(){
    	twitter4j = new TwitterFactory().getInstance();
    	KeyStore ks = new KeyStore();
    	//AccessTokenをsetしてリクエストを投げるときに必要
    	twitter4j.setOAuthConsumer(ks.getTwitterApiKey(), ks.getTwitterApiSecret());
    }
    
    public void makeAuthUrl() throws TwitterException{
    	try{
    		this.mTwitter4j_RequestToken = twitter4j.getOAuthRequestToken("mezam://main");
    		this.mAuthurl = Uri.parse(mTwitter4j_RequestToken.getAuthorizationURL());
    	}catch (TwitterException e){
    		Log.d("myapp", "Twitter service or network is unavailable:"+e.getMessage());
    		throw e;
    	}
    }

    public void makeTokenAndTokenscret(Uri uri) throws TwitterException{
    	if(uri==null){
    		Log.d("myapp","Args of getToken() is null");
    		throw new NullPointerException();
    	}
    	String verifier = uri.getQueryParameter("oauth_verifier");
    	try{
    		AccessToken accessToken = twitter4j.getOAuthAccessToken(this.mTwitter4j_RequestToken, verifier);
    		this.mToken = accessToken.getToken();
    		this.mTokenSecret = accessToken.getTokenSecret();
    	}catch (TwitterException e){
       		Log.d("myapp", "Twitter service or network is unavailable, when the user has not authorized:"+e.getMessage());
       		throw e;
       	}
    }
    
    public void setToken(String token, String tokenSecret){
    	if(token.equals("") || tokenSecret.equals("")){
    		Log.d("myapp","Args of setToken() is null");
    		throw new NullPointerException();
    	}
    	AccessToken accessToken = new AccessToken(token, tokenSecret);
    	twitter4j.setOAuthAccessToken(accessToken);
    }

    /*以下は、ツイート、タイムライン、OAuth以外*/
    public void tweet(String tweetphrase) throws TwitterException{    		
    		if(tweetphrase.length()>140){
    			tweetphrase = tweetphrase.substring(0,139);
    			Log.d("myapp","Phrase is cut");
    		}
    		try{
    			//ツイート
    			twitter4j.updateStatus(tweetphrase);
    		}catch (TwitterException e){
        		Log.d("myapp", "updateStatus error");
        		throw e;
        	}
    }
    
    public List<Status> getTimeline() throws TwitterException{
		try{
			return twitter4j.getHomeTimeline();
		}catch(TwitterException e){
			Log.d("myapp", "Twitter service or network is unavailable");
			throw e;
		}
    }

    public Uri getAuthUri(){
    	return this.mAuthurl;
    }
    public HashMap<String,String> getTokenAndTokenscret(){
    	HashMap<String,String> ret =new HashMap<String,String>();
    	ret.put("token",this.mToken);
    	ret.put("tokenScret",this.mTokenSecret);
		return ret;
    }
}