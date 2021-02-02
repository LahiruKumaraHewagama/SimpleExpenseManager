package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.DBController;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentExpenseManager extends ExpenseManager {
    private Context context;
    private DBController dbController;

    public PersistentExpenseManager(Context context) {

        this.context = context;
        this.dbController = new DBController(context);
        setup();
    }

    @Override
    public void updateAccountBalance(String accountNo, int day, int month, int year, ExpenseType expenseType, String amount)
            throws InvalidAccountException {

        SQLiteDatabase db = dbController.getWritableDatabase();

        db.beginTransaction();     //to keep DB in consistent state, grouping transactions.
        try {
            super.updateAccountBalance(accountNo, day, month, year, expenseType, amount);  //there are many transactions inside this.
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = dbController.getWritableDatabase();

        db.beginTransaction();     //to keep DB in consistent state, grouping transactions.
        try {
            super.removeAccount(accountNo); //there are many transactions inside this.
            db.setTransactionSuccessful();
        } catch (Exception e){
            throw e;
        } finally {
            db.endTransaction();
        }

    }

    @Override
    public void setup() {

        AccountDAO persistentADO = new PersistentAccountDAO(dbController);
        TransactionDAO persistentTDO = new PersistentTransactionDAO(dbController);

        setAccountsDAO(persistentADO);
        setTransactionsDAO(persistentTDO);

        //Dummy data is added in DBController Class, when setting up database for the first time.

        /**
        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
        getAccountsDAO().addAccount(dummyAcct1);
        getAccountsDAO().addAccount(dummyAcct2);

        /*** End ***/
    }
}
