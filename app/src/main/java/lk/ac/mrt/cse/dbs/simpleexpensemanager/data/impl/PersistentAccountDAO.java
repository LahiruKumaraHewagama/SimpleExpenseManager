package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBController;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO{

    private DBController dbController;

    public PersistentAccountDAO(DBController dbController) {
        this.dbController = dbController;
    }

    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase db = dbController.getReadableDatabase();
        String[] columns = {DBController.AccountTable.COLUMN_ACC_NO};

        Cursor cursor = db.query(DBController.AccountTable.TABLE_NAME,columns,null,null,null,null,null);

        List<String> account_nos = new ArrayList<String>();

        while (cursor.moveToNext()){
            account_nos.add(cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_ACC_NO)));
        }

        cursor.close();
        return account_nos;

    }

    @Override
    public List<Account> getAccountsList() {

        SQLiteDatabase db = dbController.getReadableDatabase();
        String[] columns = {DBController.AccountTable.COLUMN_ACC_NO, DBController.AccountTable.COLUMN_BANK,
                DBController.AccountTable.COLUMN_HOLDER, DBController.AccountTable.COLUMN_BALANCE};

        Cursor cursor = db.query(DBController.AccountTable.TABLE_NAME,columns,null,null,null,null,null);

        List<Account> accounts = new ArrayList<Account>();

        while(cursor.moveToNext()){
            String acc_no = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_ACC_NO));
            String bank = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_BANK));
            String holder = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_HOLDER));
            Double balance = cursor.getDouble(cursor.getColumnIndex(DBController.AccountTable.COLUMN_BALANCE));

            Account newAcc = new Account(acc_no,bank,holder,balance);

            accounts.add(newAcc);
        }

        cursor.close();
        return accounts;

    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = dbController.getWritableDatabase();

        ContentValues account_info = new ContentValues();
        account_info.put(DBController.AccountTable.COLUMN_ACC_NO,account.getAccountNo());
        account_info.put(DBController.AccountTable.COLUMN_BANK,account.getBankName());
        account_info.put(DBController.AccountTable.COLUMN_HOLDER,account.getAccountHolderName());
        account_info.put(DBController.AccountTable.COLUMN_BALANCE,account.getBalance());

        long newRowID = db.insert(DBController.AccountTable.TABLE_NAME,null,account_info);

    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase db = dbController.getWritableDatabase();

        String[] columns = {DBController.AccountTable.COLUMN_BALANCE};
        String[] whereArgs = {accountNo};    //used for prepared statement

        //want to get current balance of that account
        Cursor cursor = db.query(DBController.AccountTable.TABLE_NAME, columns,
                DBController.AccountTable.COLUMN_ACC_NO + "= ?", whereArgs,
                null,null,null);

        if(cursor.getCount()>0) {

            cursor.moveToNext();
            Double balance = cursor.getDouble(cursor.getColumnIndex(DBController.AccountTable.COLUMN_BALANCE));
            cursor.close();

            //update that record with the new balance
            Double newBalance = (expenseType == ExpenseType.EXPENSE) ? (balance - amount) : (balance + amount);


            ContentValues updateBal = new ContentValues();
            updateBal.put(DBController.AccountTable.COLUMN_BALANCE,newBalance);

            int x = db.update(DBController.AccountTable.TABLE_NAME,updateBal,
                    DBController.AccountTable.COLUMN_ACC_NO + "= ?", whereArgs);

        }
        else {
            cursor.close();
            throw new InvalidAccountException("Account does not exist");
        }
    }


    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = dbController.getWritableDatabase();
        String[] whereArgs = {accountNo};
        String whereClause = DBController.AccountTable.COLUMN_ACC_NO + " =?";

        int x = db.delete(DBController.AccountTable.TABLE_NAME,whereClause,whereArgs);

        if (x==0){ throw new InvalidAccountException("Account not found"); }


    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = dbController.getReadableDatabase();
        String[] columns = {DBController.AccountTable.COLUMN_ACC_NO, DBController.AccountTable.COLUMN_BANK,
                DBController.AccountTable.COLUMN_HOLDER, DBController.AccountTable.COLUMN_BALANCE};
        String[] whereArgs = {accountNo};    //used for prepared statement

        Cursor cursor = db.query(DBController.AccountTable.TABLE_NAME,columns,
                DBController.AccountTable.COLUMN_ACC_NO + "= ?", whereArgs,null,null,null);

        List<Account> accounts = new ArrayList<Account>();

        if(cursor.getCount()>0) {
            cursor.moveToNext();

            String acc_no = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_ACC_NO));
            String bank = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_BANK));
            String holder = cursor.getString(cursor.getColumnIndex(DBController.AccountTable.COLUMN_HOLDER));
            Double balance = cursor.getDouble(cursor.getColumnIndex(DBController.AccountTable.COLUMN_BALANCE));

            Account newAcc = new Account(acc_no, bank, holder, balance);

            cursor.close();
            return newAcc;

        } else {
            throw new InvalidAccountException("Account does not exist");
        }
    }

}
