package jp.kanagawa.kawasaki.kitakutter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GeoFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private GoogleGeo mGooglegeo;
	private boolean mBtn_clicked = false;

	public static GeoFragment newInstance(int sectionNumber) {
		GeoFragment fragment = new GeoFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public GeoFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.geo_fragment, container, false);
		Button geo_btn = (Button) rootView.findViewById(R.id.geo_btn);
		geo_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText et = (EditText) getActivity().findViewById(R.id.edittext);
				if (mBtn_clicked == true) {
					return;
				}
				mGooglegeo = new GoogleGeo();
				if (new KitakutterUtils(getActivity()).isNetworkAvailable()) {
					mGooglegeo.execute(et.getText().toString());
				}
				//displayData(ret, null);
			}
		});

		ArrayList<String> ret = new KitakutterUtils(getActivity().getApplicationContext()).getGeoData();
		displayData(ret, rootView);

		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}

	@Override
	public void onPause() {
		if (mGooglegeo != null) {
			mGooglegeo.cancel(true);
		}
		mBtn_clicked = false;
		super.onPause();
	}

	/*
	 * Google GEO コード取得用クラス
	 */
	private class GoogleGeo extends AsyncTask<String, Void, Void> {
		private ArrayList<String> ret;
		private String inputString;

		private GoogleGeo() {
			mBtn_clicked = true;
			this.ret = new ArrayList<String>();
		}

		@Override
		protected Void doInBackground(String... inputString) {
			try {
				this.inputString = inputString[0];
				String encodedString = URLEncoder.encode(this.inputString,"UTF-8");
				Log.d("myapp", "req:" + this.inputString);
				URL url = new URL("http://maps.google.com/maps/api/geocode/json?address=" + encodedString + "&sensor=false");
				// URL url = new
				// URL("http://maps.google.com/maps/api/geocode/json?address=tokyo&sensor=false");
				HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.setRequestMethod("GET");
				request.connect();
				BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
				String line = "";
				String linetmp = "";
				while (true) {
					if ((linetmp = reader.readLine()) == null) {
						break;
					}
					line = line + linetmp;
				}
				line = line.replaceAll(" ", "");
				reader.close();
				request.disconnect();

				JSONObject entries = new JSONObject(line);
				JSONArray post = (JSONArray) entries.get("results");
				//検索結果の一番上を取得
				JSONObject post2 = post.getJSONObject(0);
				this.ret.add(post2.getString("formatted_address"));
				JSONObject post3 = post2.getJSONObject("geometry");
				JSONObject post4 = post3.getJSONObject("location");
				this.ret.add(post4.getString("lat"));
				this.ret.add(post4.getString("lng"));

				return null;
			} catch (Exception e) {
				e.printStackTrace();
				this.ret.clear();
				return null;
			}
		}

		@Override
		protected void onPostExecute(Void v) {
			if (!this.ret.isEmpty()) {// ひとつのデータが欠けるという状況は発生しない
				ret.add(inputString);
				displayData(ret, null);
				new KitakutterUtils(getActivity().getApplicationContext()).storeGeoData(this.ret);
			}
		}
	}

	void displayData(ArrayList<String> ret, View rootView) {
		mBtn_clicked = false;

		//rootViewがnullの場合は、画面がまだ生成されていない場合
		if (rootView != null) {
			TextView tv = (TextView) rootView.findViewById(R.id.TextView00);
			tv.setText(ret.get(0));
			tv = (TextView) rootView.findViewById(R.id.TextView01);
			tv.setText(ret.get(1));
			tv = (TextView) rootView.findViewById(R.id.TextView02);
			tv.setText(ret.get(2));
			tv = (TextView) rootView.findViewById(R.id.edittext);
			if (ret.get(3).length() != 0) {
				tv.setText(ret.get(3));
			}
		} else {
			TextView tv = (TextView) getActivity().findViewById(R.id.TextView00);
			tv.setText(ret.get(0));
			tv = (TextView) getActivity().findViewById(R.id.TextView01);
			tv.setText(ret.get(1));
			tv = (TextView) getActivity().findViewById(R.id.TextView02);
			tv.setText(ret.get(2));
			tv = (TextView) getActivity().findViewById(R.id.edittext);
			if (ret.get(3).length() != 0) {
				tv.setText(ret.get(3));
			}
		}
	}
}
