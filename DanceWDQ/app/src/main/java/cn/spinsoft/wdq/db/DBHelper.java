package cn.spinsoft.wdq.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhoujun on 15/11/10.
 */
class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = DBHelper.class.getSimpleName();

    private static final String DB_NAME = "spinSoft.wdq";
    private static final int DB_VERSION = 1;

    protected static final String TABLE_SEARCH = "searchRecode";
    protected static final String SEARCH_KEYWORDS = "keyWords";
    protected static final String SEARCH_TYPE = "type";
    protected static final String SYS_TIME = "systemTime";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String searchSql = "create table if not exists " + TABLE_SEARCH + " ( " + SEARCH_KEYWORDS
                + " text not null," + SEARCH_TYPE + " text," + SYS_TIME + " integer);";
        db.execSQL(searchSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
