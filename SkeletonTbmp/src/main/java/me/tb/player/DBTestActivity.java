package me.tb.player;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Juhess88 on 8/11/2015.
 */
public class DBTestActivity extends ActionBarActivity {

    DBAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        dbAdapter = new DBAdapter(this);
//        SQLiteDatabase sqLiteDatabase = dbAdapter.getWritableDatabase();
    }
}
