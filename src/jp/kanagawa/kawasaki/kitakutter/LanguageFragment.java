package jp.kanagawa.kawasaki.kitakutter;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LanguageFragment extends Fragment {
	private static final String ARG_SECTION_NUMBER = "section_number";
	private View rootView;
	
	public static LanguageFragment newInstance(int sectionNumber) {
		LanguageFragment fragment = new LanguageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public LanguageFragment() {	
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.language_fragment, container, false);
		Button lang_btn = (Button)rootView.findViewById(R.id.language_btn);
        lang_btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		
        		new AlertDialog.Builder(getActivity())
        		.setTitle(getString(R.string.btn_language_question))
        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		        		SharedPreferences.Editor editor = sp.edit();
		        		editor.putString("user_lang", "japanese");
		        		editor.commit();
		        		displaylanguage();
					}
				})
        		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
		        		SharedPreferences.Editor editor = sp.edit();
		        		editor.putString("user_lang", "");
		        		editor.commit();
		        		displaylanguage();
					}
				})
        		.create()
        		.show();
        	}
        });
        
        displaylanguage();
        
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
	}
	
	void displaylanguage(){
		EditText edit_text = (EditText)rootView.findViewById(R.id.edit_text);
        SharedPreferences sp = getActivity().getSharedPreferences("saveddata",Context.MODE_PRIVATE);
        
        if("japanese".equals(sp.getString("user_lang", "")) ){
        	edit_text.setText("日本語");
        }else{
        	edit_text.setText("English");
        }
        edit_text.setKeyListener(null);
	}
}
