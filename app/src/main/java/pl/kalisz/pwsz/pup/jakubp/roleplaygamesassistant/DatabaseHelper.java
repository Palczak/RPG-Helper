package pl.kalisz.pwsz.pup.jakubp.roleplaygamesassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "RPGAssistant";
    private static final int DATABASE_VERSION = 1;

    private final String CREATE_TABLE_HEALTH_QUERY = "CREATE TABLE Health (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, max INTEGER, current INTEGER);";
    private final String CREATE_TABLE_AMMUNITION_QUERY = "CREATE TABLE Ammunition(_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,amount INTEGER);";
    private final String CREATE_TABLE_WALLET_QUERY = "CREATE TABLE Wallet(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, money INTEGER);";
    private final String CREATE_TABLE_CURRENCY_QUERY = "CREATE TABLE Currency(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, value INTEGER, idWallet INTEGER REFERENCES Wallet(id));";
    private static DatabaseHelper instance = null;

    static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HEALTH_QUERY);
        db.execSQL(CREATE_TABLE_AMMUNITION_QUERY);
        db.execSQL(CREATE_TABLE_WALLET_QUERY);
        db.execSQL(CREATE_TABLE_CURRENCY_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
