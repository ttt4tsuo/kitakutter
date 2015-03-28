package jp.kanagawa.kawasaki.kitakutter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class KitakutterUtils {
	private Context cntxt;
	
	public KitakutterUtils(Context cntxt){
		this.cntxt = cntxt;
	}
	
	public void storeTokenAndTokenscret(HashMap<String,String> tokenAndTokenscret){
		SharedPreferences sp = cntxt.getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("access_token", tokenAndTokenscret.get("token"));
		editor.putString("token_secret", tokenAndTokenscret.get("tokenScret"));
		editor.commit();
	}
	
	public void storeGeoData(ArrayList<String> ret){
		SharedPreferences sp = cntxt.getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("geo_name", ret.get(0));
		editor.putString("geo_lat", ret.get(1));
		editor.putString("geo_lng", ret.get(2));
		editor.putString("geo_search_name", ret.get(3));
		editor.commit();
	}
	
	public ArrayList<String> getGeoData(){
		SharedPreferences sp = cntxt.getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(sp.getString("geo_name", ""));
		ret.add(sp.getString("geo_lat", ""));
		ret.add(sp.getString("geo_lng", ""));
		ret.add(sp.getString("geo_search_name", ""));
		return ret;
	}
	
	public boolean isNetworkAvailable(){
		ConnectivityManager connectManager = (ConnectivityManager) this.cntxt.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfos = connectManager.getAllNetworkInfo();
		for(NetworkInfo netInfo:netInfos){
			if(netInfo.isConnected()){
				return true;
			}
		}
		return false;
	}

}
