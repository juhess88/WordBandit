package me.jhessstudios.wordbandit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

/**
 * Created by Juhess88 on 8/11/2015.
 */
public class DBTestActivity extends ActionBarActivity {

    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView tv = (TextView)findViewById(R.id.db_data);
        dbAdapter = new DBAdapter(this);
        String data = dbAdapter.getAllData();
        tv.setText(data);
    }
}
