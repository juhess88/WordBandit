package me.tb.player;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Juhess88 on 8/11/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wbdb";
    private static final String TABLE_NAME = "WBTABLE";
    private static final int DATABASE_VERSION = 3;
    private static final String UID = "_id";
    private static final String NAME = "Name";
    private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255));";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME ;
    private Context context;

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Toast.makeText(context, "DB Constructor called", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            Toast.makeText(context, "DB onCreate called", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(context, "error creating", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(DROP_TABLE);
            Toast.makeText(context, "DB onUpgrade called", Toast.LENGTH_SHORT).show();
            onCreate(sqLiteDatabase);
        } catch (SQLException e) {
            Toast.makeText(context, "error upgrading", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
