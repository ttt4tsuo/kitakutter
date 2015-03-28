package jp.kanagawa.kawasaki.kitakutter;

import twitter4j.TwitterException;
import jp.kanagawa.kawasaki.kitakutter.TweetDB.DataColumns;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TweetFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";

	public static TweetFragment newInstance(int sectionNumber) {
		TweetFragment fragment = new TweetFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public TweetFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tweet_fragment, container, false);
		Button tweet_btn = (Button)rootView.findViewById(R.id.tweet_btn);
        tweet_btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
        		Log.d("myapp","access_token:"+sp.getString("access_token", ""));
        	    Log.d("myapp","token_secret:"+sp.getString("token_secret", ""));
        	    Log.d("myapp","gentext:"+sp.getString("gentext", ""));
        	    if(sp.getString("access_token","").length()==0 || sp.getString("token_secret","").length()==0 || sp.getString("gentext", "").length()==0){
        	    	Toast.makeText(getActivity().getApplicationContext(),"Params is Null" ,Toast.LENGTH_LONG).show();     	    	
        	    	final TweetClient mTweetClient = new TweetClient();
        	    	new Thread(new Runnable() {
        				@Override
        				public void run() {
        					try {
								mTweetClient.makeAuthUrl();
							} catch (TwitterException e) {
								//Twitter service or network is unavailable
								return;
							}
        					Uri url = mTweetClient.getAuthUri();
        					startActivity(new Intent(Intent.ACTION_VIEW, url));
        				}}).start();
        	    	return;
        	    }
        	    pendTweetIntent();
        	}
        });
        
        Button generatephrase_btn = (Button)rootView.findViewById(R.id.generatephrase);
        generatephrase_btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        			String[] seeds = {null};
        			String tubuyaki;
        			SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
        			if(sp.getString("user_lang", "").equals("japanese")){
        				seeds[0] = "は";
        				tubuyaki = "今";
        			}else{
        				seeds[0] = "a";
            			tubuyaki = "This is ";
        			}
        			int i=0;
        			while(tubuyaki.indexOf("end")<0 && tubuyaki.indexOf("???")<0 && i<140){
        				tubuyaki = tubuyaki + MarkovTweetDB(seeds);
        				i=i+1;
        			}
        			tubuyaki = tubuyaki.replace("end", "");
        			tubuyaki = tubuyaki.replace("???", "");
        			TextView tv = (TextView)getActivity().findViewById(R.id.TextView03);
        			tv.setText(tubuyaki);
        			SharedPreferences.Editor editor = sp.edit();
        			editor.putString("gentext", tubuyaki);
        			editor.commit();
        	}
        });
        
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
	
	protected void onNewIntent(Intent intent) {
		pendTweetIntent();
	}
	
	void pendTweetIntent(){
		SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		Intent intentForReceiver = new Intent(getActivity(),IntentReceiver.class);
	    intentForReceiver.putExtra("access_token",sp.getString("access_token", ""));
	    intentForReceiver.putExtra("token_secret",sp.getString("token_secret", ""));
	    intentForReceiver.putExtra("gentext",sp.getString("gentext", ""));
		try{
			PendingIntent sender = PendingIntent.getBroadcast(getActivity(), 0,
					intentForReceiver,
					PendingIntent.FLAG_ONE_SHOT);
			LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

			if(sp.getString("geo_lat", "").equals("") || sp.getString("geo_lng", "").equals("")){
				return;
			}
			Double lat = Double.parseDouble(sp.getString("geo_lat", ""));
			Double lng = Double.parseDouble(sp.getString("geo_lng", ""));
			Log.d("myapp",Double.toString(lat)+" / "+Double.toString(lng));
			Toast.makeText(getActivity().getApplicationContext(),
					Double.toString(lat)+" / "+Double.toString(lng),Toast.LENGTH_LONG).show();
			lm.addProximityAlert(lat, lng, 1000, -1, sender);
		}catch (Exception e){
			e.printStackTrace();
			return;
		}
	}
	
	public String MarkovTweetDB(String[] seedsString){
    	String ret = null;
    	int countRow = 0;
        TweetDB mTweetDB = new TweetDB(getActivity().getApplicationContext());
        Cursor c = mTweetDB.query(null, "first = ?" ,new String[]{seedsString[0]}, "_id desc");
        if (c.moveToFirst()) {
        	do {
                countRow = countRow + 1;
            } while (c.moveToNext());
        }
        if(countRow == 0){
        	c.close();
            mTweetDB.closeDB();
        	return "???";
        }
        if(c.moveToFirst()){
        	for(int i=1;i<(Math.random()*10000)%countRow;i++){
        		c.moveToNext();
        	}
        }
        String queryString1 = c.getString(c.getColumnIndex(DataColumns.FIRST));
        String queryString2 = c.getString(c.getColumnIndex(DataColumns.SECOND));
        c.close();
        mTweetDB.closeDB();
        mTweetDB = new TweetDB(getActivity().getApplicationContext());
        c = mTweetDB.query(null, "first = ? and second = ?" ,new String[]{queryString1,queryString2}, "_id desc");
        
        if (c.moveToFirst()) {
        	countRow = 0;
        	do {
                countRow = countRow + 1;
            } while (c.moveToNext());
        }
        if(countRow == 0){
        	c.close();
            mTweetDB.closeDB();
        	return "???";
        }
        if(c.moveToFirst()){
        	for(int i=1;i<(Math.random()*10000)%countRow;i++){
        		c.moveToNext();
        	}
        }
        ret = c.getString(c.getColumnIndex(DataColumns.FIRST));
        SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		if(sp.getString("user_lang", "").equals("japanese")==false){
			ret = ret + " ";
		}
        ret = ret + c.getString(c.getColumnIndex(DataColumns.SECOND));
        if(sp.getString("user_lang", "").equals("japanese")==false){
        	ret = ret + " ";
        }
        seedsString[0]= c.getString(c.getColumnIndex(DataColumns.THIRD));
        c.close();
        mTweetDB.closeDB();
        
    	return ret;
    }
}
