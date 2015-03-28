package jp.kanagawa.kawasaki.kitakutter;

import twitter4j.TwitterException;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.AsyncTask;
import android.widget.Toast;

/*
 * 位置に入った時に、PendingIntentを受け取るレシーバ
 */
public class IntentReceiver extends BroadcastReceiver{
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent){
		this.context=context;
		Toast.makeText(context,"onReceiveIntent",Toast.LENGTH_LONG).show();
		if(new KitakutterUtils(context).isNetworkAvailable()){
			new TweetUpdateTask(intent).execute();
		}else{
			Toast.makeText(context,"Not connect Network.",Toast.LENGTH_LONG).show();
		}
	}
	
	/*
	 * 通信用タスク
	 */
	private class TweetUpdateTask extends AsyncTask<Void, Void, String>{
		private TweetClient tweetClient;
		private Intent intent;
		
		public TweetUpdateTask(Intent intent){
			this.intent=intent;
			tweetClient = new TweetClient();
		}
		
		@Override
		protected String doInBackground(Void... params) {
			tweetClient.setToken(intent.getStringExtra("access_token"),intent.getStringExtra("token_secret"));
			try {
				tweetClient.tweet(intent.getStringExtra("gentext"));
			} catch (TwitterException e) {
				return e.getMessage();
			}
			return null;
		}
		protected void onPostExecute(String result) {
			if(result!=null){
				Toast.makeText(context,result,Toast.LENGTH_LONG).show();
			}
		}
		
	}
}