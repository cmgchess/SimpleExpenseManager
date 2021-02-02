package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private DBHelper dbHelper;

    public PersistentTransactionDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date,accountNo,expenseType,amount);
        if (accountNo!=null){
            dbHelper.logTransaction(transaction);
        }
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dbHelper.getAllTransactionLogs(-1);  //-1 defined to get all
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactionList = dbHelper.getAllTransactionLogs(limit);
        Collections.reverse(transactionList);  //to get latest transaction to bottom in UI
        return transactionList;
    }
}
