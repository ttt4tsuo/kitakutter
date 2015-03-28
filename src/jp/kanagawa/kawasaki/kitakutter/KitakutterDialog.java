package jp.kanagawa.kawasaki.kitakutter;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class KitakutterDialog extends DialogFragment {
    int mNum;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static KitakutterDialog newInstance(int num) {
    	KitakutterDialog f = new KitakutterDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        switch ((mNum)%8) {
        	//テーマなし
            case 0: style = DialogFragment.STYLE_NO_TITLE; break;
            case 1: style = DialogFragment.STYLE_NO_FRAME; break;
            case 2: style = DialogFragment.STYLE_NO_INPUT; break;
            //テーマあり
            case 3: style = DialogFragment.STYLE_NORMAL; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NO_TITLE; break;
            case 6: style = DialogFragment.STYLE_NO_FRAME; break;
            case 7: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum)%8) {
        	//http://y-anz-m.blogspot.jp/2011/05/androidholo-theme.html
            case 3: theme = android.R.style.Theme_Holo; break;//暗い、全画面
            case 4: theme = android.R.style.Theme_Holo_Light_Dialog; break;//明るい、ダイアログ
            case 5: theme = android.R.style.Theme_Holo_Light; break;//明るい、全画面
            case 6: theme = android.R.style.Theme_Holo_Light_Panel; break;//明るい、ウィジット付き
            case 7: theme = android.R.style.Theme_Holo_Light; break;//明るい、全画面
        }
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_fragment, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText(getText(R.string.dialog_text));

        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.show);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.d("myapp","click");
                KitakutterDialog.this.dismiss();
            }
        });

        return v;
    }
}