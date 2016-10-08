package cn.spinsoft.wdq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by hushujun on 15/11/10.
 */
public class DBOperationUtil extends Observable {
    private static final String TAG = DBOperationUtil.class.getSimpleName();
    private static DBOperationUtil operationUtil;
    private DBHelper dbHelper;

    private DBOperationUtil(Context context) {
        dbHelper = new DBHelper(context);
    }

    public static DBOperationUtil getInstance(Context context) {
        if (operationUtil == null) {
            operationUtil = new DBOperationUtil(context);
        }
        return operationUtil;
    }

    public boolean insertSearchRecode(String keyWords, String type) {
        if (TextUtils.isEmpty(keyWords) || TextUtils.isEmpty(type)) {
            return false;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.SEARCH_KEYWORDS, keyWords);
        values.put(DBHelper.SEARCH_TYPE, type);
        values.put(DBHelper.SYS_TIME, System.currentTimeMillis() / 1000);
        long idx = db.update(DBHelper.TABLE_SEARCH, values, DBHelper.SEARCH_KEYWORDS + "=?", new String[]{keyWords});
        if (idx <= 0) {
            idx = db.insert(DBHelper.TABLE_SEARCH, null, values);
        }
        db.close();
        if (idx > 0) {
            setChanged();
            notifyObservers(type);
        }
        return idx > 0;
    }

    public List<String> querySearchRecodes(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_SEARCH, null, DBHelper.SEARCH_TYPE + "=?",
                new String[]{type}, null, null, DBHelper.SYS_TIME + " desc ", "0,20");
        List<String> keyWords = null;
        if (cursor != null && cursor.getCount() > 0) {
            keyWords = new ArrayList<>();
            while (cursor.moveToNext()) {
                keyWords.add(cursor.getString(cursor.getColumnIndex(DBHelper.SEARCH_KEYWORDS)));
            }
            cursor.close();
        }
        db.close();
        return keyWords;
    }
}
