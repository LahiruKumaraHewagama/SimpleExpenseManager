package lk.ac.mrt.cse.dbs.simpleexpensemanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.security.PublicKey;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBController extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "180186X.db";

    public static final String SQL_CREATE_ACCOUNT_TABLE = "CREATE TABLE " + AccountTable.TABLE_NAME + " (" +
            AccountTable.COLUMN_ACC_NO + " TEXT PRIMARY KEY," +
            AccountTable.COLUMN_BANK + " TEXT," +
            AccountTable.COLUMN_HOLDER + " TEXT," +
            AccountTable.COLUMN_BALANCE + " REAL)";

    public static final String SQL_CREATE_TRANSACTION_TABLE = "CREATE TABLE " + TransactionTable.TABLE_NAME + " (" +
            TransactionTable.COLUMN_DATE + " TEXT," +
            TransactionTable.COLUMN_ACC_NO + " TEXT," +
            TransactionTable.COLUMN_TYPE + " TEXT," +
            TransactionTable.COLUMN_AMOUNT + " REAL)";

    public static final String SQL_DROP_ACCOUNT_TABLE = "DROP TABLE IF EXISTS `account`";

    public DBController(Context context)  {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Inner class to hold details of Account table
    public static class AccountTable implements BaseColumns{
        public static final String TABLE_NAME = "`account`";
        public static final String COLUMN_ACC_NO = "acc_no";
        public static final String COLUMN_BANK = "bank";
        public static final String COLUMN_HOLDER = "holder";
        public static final String COLUMN_BALANCE = "balance";

        public static final String PRELIMINARY_SQL_1 = "INSERT INTO " + TABLE_NAME + " VALUES ('12345A','Yoda Bank','Anakin Skywalker',10000.0);";
        public static final String PRELIMINARY_SQL_2 = "INSERT INTO " + TABLE_NAME + " VALUES ('78945Z','Clone BC','Obi-Wan Kenobi',80000.0);";

    }

    public static class TransactionTable implements BaseColumns{
        public static final String TABLE_NAME = "`transaction`";
        public static final String COLUMN_ACC_NO = "acc_no";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_AMOUNT = "amount";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNT_TABLE);
        db.execSQL(SQL_CREATE_TRANSACTION_TABLE);
        db.execSQL(AccountTable.PRELIMINARY_SQL_1);
        db.execSQL(AccountTable.PRELIMINARY_SQL_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_ACCOUNT_TABLE);
        onCreate(db);
    }
}
