package com.giftedcat.uploadhelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.giftedcat.uploadhelp.dao.DaoMaster;
import com.giftedcat.uploadhelp.dao.DaoSession;


/**
 * 数据库辅助类
 *
 * @author Administrator
 */
public class DBHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper mInstance;
    private DaoMaster.DevOpenHelper mOpenHelper;
    private DaoMaster mDaoMaster;
    private SQLiteDatabase db;
    private DaoSession mDaoSession;
    private String password = "AskSky_TanPeiQi_1195211669_JMSQJ";
    private static final String DBName = "testDb";

    private DBHelper() {
    }

    public static DBHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DBHelper();
        }
        return mInstance;
    }

    public void init(Context context) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mOpenHelper = new DaoMaster.DevOpenHelper(context, DBName, null);
//        mDaoMaster = new DaoMaster(mOpenHelper.getEncryptedWritableDb(Utils.getMd5(password)));
//        mDaoMaster = new DaoMaster(mOpenHelper.getEncryptedWritableDb(password));

        db = mOpenHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
