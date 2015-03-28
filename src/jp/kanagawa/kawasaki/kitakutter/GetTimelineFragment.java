package jp.kanagawa.kawasaki.kitakutter;

import java.util.HashMap;
import java.util.List;

import twitter4j.TwitterException;
import jp.kanagawa.kawasaki.kitakutter.TimelineDB.TimelineColumns;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class GetTimelineFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private TweetClient mTweetClient;
	private TimelineWakachiTask mTimelineWTask;
	private boolean mBtn_clicked = false;

	public static GetTimelineFragment newInstance(int sectionNumber) {
		GetTimelineFragment fragment = new GetTimelineFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public GetTimelineFragment() {
		mTweetClient = new TweetClient();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gettimeline_fragment, container, false);
		Button gettimeline_btn = (Button)rootView.findViewById(R.id.gettimeline);
        gettimeline_btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(mBtn_clicked==true){
					return;
				}
        		if(new KitakutterUtils(getActivity()).isNetworkAvailable()){
        			try{
        				new Thread(new Runnable() {
        					@Override
        					public void run() {
        						SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
        						if(sp.getString("access_token", "").equals("") && sp.getString("token_secret", "").equals("")){
        							try {
										mTweetClient.makeAuthUrl();
										Uri url = mTweetClient.getAuthUri();//Uriで受けて関数呼び出ししないとブラウザが反応しない。
										startActivity(new Intent(Intent.ACTION_VIEW, url));
        							} catch (TwitterException e) {
        								//Twitter service or network is unavailable
										return;
									}	
        						}else{
        							mTimelineWTask = new TimelineWakachiTask();
        							mTimelineWTask.execute();
        						}
        					}
        				}).start();
        			}catch(Exception e){
        				e.printStackTrace();
        				return;
        			}
        		}
        	}
        });
        
        TimelineDB mTimelineDB = new TimelineDB(getActivity().getApplicationContext());
        ArrayAdapter<String> timelineadpter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);;
        Cursor c = mTimelineDB.query(null, null, null);
        if (c.moveToFirst()) {
        	do {
        		timelineadpter.add(c.getString(c.getColumnIndex(TimelineColumns.TIMELINE)));
            } while (c.moveToNext());
        }
        mTimelineDB.closeDB();
        ListView listView1 = (ListView)rootView.findViewById(R.id.timeline_listview);
        listView1.setAdapter(timelineadpter);
        
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
	
	@Override
	public void onPause(){
		if(mTimelineWTask!=null){
			mTimelineWTask.cancel(true);
		}
		mBtn_clicked = false;
		super.onPause();
	}
	
	protected void onNewIntent(Intent intent) {
		final Uri uriVerifier = intent.getData();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					mTweetClient.makeTokenAndTokenscret(uriVerifier);
				} catch (TwitterException e) {
					//Twitter service or network is unavailable, when the user has not authorized
					//TODO:削除処理
					HashMap<String,String> blankToken =new HashMap<String,String>();
					blankToken.put("token","");
					blankToken.put("tokenScret","");
					new KitakutterUtils(getActivity().getApplicationContext()).storeTokenAndTokenscret(blankToken);
					return;
				}
				new KitakutterUtils(getActivity().getApplicationContext()).storeTokenAndTokenscret(mTweetClient.getTokenAndTokenscret());
				new TimelineWakachiTask().execute();
			}
		}).start();
	}
	
	private class TimelineWakachiTask extends AsyncTask<Void, Void, String>{
		
		ArrayAdapter<String> timelineadpter;
		
		public TimelineWakachiTask(){
			mBtn_clicked = true;
			this.timelineadpter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		}
		
		protected String doInBackground(Void... params) {
			SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
			mTweetClient.setToken(sp.getString("access_token", ""),sp.getString("token_secret", ""));
			List<twitter4j.Status> statuses;
			try {
				statuses = mTweetClient.getTimeline();
			} catch (TwitterException e) {
				return "Can't get timeline";
			}
			
			TimelineDB mTimelineDB = new TimelineDB(getActivity().getApplicationContext());
			mTimelineDB.dropForUpdateDB();
			for (twitter4j.Status status : statuses) { 
				//Log.d("myapp",status.toString());
				timelineadpter.add(status.getText());
				storeTimelineDB(mTimelineDB,status.getText());
				if(sp.getString("user_lang", "").equals("japanese")){
					if(status.getLang().equals("ja")){
						YahooTextAnalysis yahootextanalysis = new YahooTextAnalysis();
						yahootextanalysis.jaDataPush(status.getText(),getActivity().getApplicationContext());
					}
				}else{
					if(status.getLang().equals("en")){
						YahooTextAnalysis yahootextanalysis = new YahooTextAnalysis();
						yahootextanalysis.enDataPush(status.getText(),getActivity().getApplicationContext());
					}
				}
			}
			mTimelineDB.closeDB();
			return null;
		}
		
		protected void onPostExecute(String result) {
			mBtn_clicked=false;
			if(result!=null){
				Toast.makeText(getActivity(),"Can't get timeline",Toast.LENGTH_LONG).show();
				return;
			}
			ListView listView1 = (ListView)getActivity().findViewById(R.id.timeline_listview);
			if(listView1==null){
				return;
			}else{
				listView1.setAdapter(timelineadpter);
			}
		}
		
		void storeTimelineDB(TimelineDB mTimelineDB,String status){
	        ContentValues cv = new ContentValues();
	        cv.put(TimelineColumns.TIMELINE,status);
	        mTimelineDB.insert(cv);
	        cv.clear();
		}
	}

}