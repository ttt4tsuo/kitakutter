package jp.kanagawa.kawasaki.kitakutter;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

public class MainActivity extends ActionBarActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		//言語の初期化処理
        String localeString = getResources().getConfiguration().locale.toString();
        SharedPreferences sp = getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		if("none".equals(sp.getString("user_lang", "none"))
        		&& (Locale.JAPAN.toString().equals(localeString) || Locale.JAPANESE.equals(localeString)) ){
        	SharedPreferences.Editor editor = sp.edit();
    		editor.putString("user_lang", "japanese");
    		editor.commit();
        }
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		if(position == 0){
			fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						GetTimelineFragment.newInstance(position + 1)).commit();
		}else if(position == 1){
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					LanguageFragment.newInstance(position + 1)).commit();
		}else if(position == 2){
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					GeoFragment.newInstance(position + 1)).commit();
		}else if(position == 3){
			fragmentManager
			.beginTransaction()
			.replace(R.id.container,
					TweetFragment.newInstance(position + 1)).commit();
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			KitakutterDialog newFragment = KitakutterDialog.newInstance(7);
		    newFragment.show(getSupportFragmentManager(), "dialog");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent.getData()==null || intent.getData().toString().length()<"mezam://".length()){
			return;
		}
		if("mezam://".equals(intent.getData().toString().substring(0,"mezam://".length()))){
			FragmentManager fragmentManager = getSupportFragmentManager();
			Fragment f = (Fragment)fragmentManager.findFragmentById(R.id.container);
			if("GetTimelineFragment".equals(f.toString().substring(0, "GetTimelineFragment".length()))){
				((GetTimelineFragment)f).onNewIntent(intent);
			}else if("TweetFragment".equals(f.toString().substring(0, "TweetFragment".length()))){
				((TweetFragment)f).onNewIntent(intent);
			}
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*public static class PlaceholderFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);			
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}*/

}
