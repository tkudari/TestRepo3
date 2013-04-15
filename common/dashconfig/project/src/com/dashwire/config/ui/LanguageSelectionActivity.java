package com.dashwire.config.ui;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.dashwire.config.R;
import com.dashwire.config.util.CommonUtils;

public class LanguageSelectionActivity extends ListActivity {

    protected static final String TAG = LanguageSelectionActivity.class.getCanonicalName();
    static final String[] LANGUAGES = new String[] {
        "English",
        "Español"
    };
    protected Context context = this;
    
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        
        setContentView( R.layout.language_selection );
        super.onCreate( savedInstanceState );

        setListAdapter( new ArrayAdapter<String>( this, R.layout.language_selection_row, LANGUAGES ) );

        ListView lv = getListView();
        lv.setTextFilterEnabled( true );

        lv.setOnItemClickListener( new OnItemClickListener() {
            public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
                String selectedLanguage = ( ( TextView ) view ).getText().toString();             
                if ("English".equalsIgnoreCase( selectedLanguage ))
                {
                    CommonUtils.setLanguage("en", getApplicationContext());
                }else if ("Español".equalsIgnoreCase( selectedLanguage ))
                {
                    CommonUtils.setLanguage("es", getApplicationContext());
                }
                CommonUtils.setSelectLanguageCompletedFlag( true, context );
                finish();
            }
        } );
    }
}