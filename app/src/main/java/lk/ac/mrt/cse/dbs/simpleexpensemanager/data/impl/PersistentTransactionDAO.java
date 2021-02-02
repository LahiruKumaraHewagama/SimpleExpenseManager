package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBController;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {

    private DBController dbController;

    public PersistentTransactionDAO(DBController dbController){
        this.dbController = dbController;
    }


    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = dbController.getWritableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        ContentValues transaction = new ContentValues();
        transaction.put(DBController.TransactionTable.COLUMN_ACC_NO,accountNo);
        transaction.put(DBController.TransactionTable.COLUMN_DATE, sdf.format(date));
        transaction.put(DBController.TransactionTable.COLUMN_TYPE,expenseType.toString());
        transaction.put(DBController.TransactionTable.COLUMN_AMOUNT,amount);


        long newRowID = db.insert(DBController.TransactionTable.TABLE_NAME, null, transaction);

    }

    @Override
    public void removeTransactions(String accountNo) {

        SQLiteDatabase db = dbController.getWritableDatabase();
        String[] whereArgs = {accountNo};
        String whereClause = DBController.AccountTable.COLUMN_ACC_NO + " =?";

        int x = db.delete(DBController.TransactionTable.TABLE_NAME,whereClause,whereArgs);




    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        SQLiteDatabase db = dbController.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        String[] columns = {DBController.TransactionTable.COLUMN_ACC_NO, DBController.TransactionTable.COLUMN_DATE,
                DBController.TransactionTable.COLUMN_TYPE, DBController.TransactionTable.COLUMN_AMOUNT};

        Cursor cursor = db.query(DBController.TransactionTable.TABLE_NAME,columns,null,null,null,null,null);

        List<Transaction> transactions = new ArrayList<Transaction>();

        while(cursor.moveToNext()){

            Date date = new Date();
            try { date = sdf.parse(cursor.getString(cursor.getColumnIndex(DBController.TransactionTable.COLUMN_DATE))); }
            catch (ParseException e) {}

            String acc_no = cursor.getString(cursor.getColumnIndex(DBController.TransactionTable.COLUMN_ACC_NO));

            ExpenseType type = ExpenseType.valueOf(cursor.getString(cursor.getColumnIndex(DBController.TransactionTable.COLUMN_TYPE)).toUpperCase()) ;
            Double amount = cursor.getDouble(cursor.getColumnIndex(DBController.TransactionTable.COLUMN_AMOUNT));

            Transaction newTrans = new Transaction(date,acc_no,type,amount);

            transactions.add(newTrans);
        }

        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        List<Transaction> transactions = getAllTransactionLogs();
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
