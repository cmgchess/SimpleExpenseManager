package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class DBHelper extends SQLiteOpenHelper{

    public static final String ACCOUNT_TABLE = "account";
    public static final String TRANSACTION_TABLE = "transactions";

    private DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public DBHelper(Context context) {
        super(context, "180187C", null, 2);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String accountDDL = "CREATE TABLE " +ACCOUNT_TABLE+ " (accountNo TEXT(50) PRIMARY KEY,bankName TEXT(100) NOT NULL,accountHolderName TEXT(100) NOT NULL,balance REAL NOT NULL) ";
        String transactionDDL = "CREATE TABLE " +TRANSACTION_TABLE+ " (accountNo TEXT(50) NOT NULL,date DATE NOT NULL,expenseType TEXT(10) NOT NULL,amount REAL NOT NULL,FOREIGN KEY (accountNo) REFERENCES "+ACCOUNT_TABLE+"(account_no))";
        db.execSQL(accountDDL);
        db.execSQL(transactionDDL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ACCOUNT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE);
        onCreate(db);
    }

    public boolean addAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());

        long insert = db.insert(ACCOUNT_TABLE,null,cv);
        return insert != -1;
    }

    public boolean updateAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("accountNo",account.getAccountNo());
        cv.put("bankName",account.getBankName());
        cv.put("accountHolderName",account.getAccountHolderName());
        cv.put("balance",account.getBalance());

        long update = db.update(ACCOUNT_TABLE,cv,"accountNo = ?",new String[]{account.getAccountNo()});
        return update != -1;
    }

    public boolean removeAccount(String accountNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ACCOUNT_TABLE,"accountNo = "+accountNo,null) > 0;
    }

    public Account getAccount(String accountNo){
        SQLiteDatabase db = this.getReadableDatabase();
        Account account = null;
        String queryString = "SELECT * FROM "+ACCOUNT_TABLE+" WHERE accountNo = ?";
        Cursor cursor = db.rawQuery(queryString,new String[]{accountNo});
        if (cursor.getCount()==0){
            cursor.close();
            db.close();
            return account;
        }
        else {
            while (cursor.moveToNext()){
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);
                account = new Account(accountNo,bankName,accountHolderName,balance);
            }
            cursor.close();
            db.close();
            return account;
        }
    }

    public List<Account> getAccountsList() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Account> accountsList=new ArrayList<>();
        String queryString = "SELECT * FROM "+ACCOUNT_TABLE;
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.getCount()==0){
            cursor.close();
            db.close();
            return accountsList;
        }
        else {
            while (cursor.moveToNext()){
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);
                accountsList.add(new Account(accountNo,bankName,accountHolderName,balance));
            }
            cursor.close();
            db.close();
            return accountsList;
        }
    }

    public boolean logTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("accountNo",transaction.getAccountNo());
        cv.put("date",DATE_FORMAT.format(transaction.getDate()));
        cv.put("expenseType",transaction.getExpenseType().toString());
        cv.put("amount",transaction.getAmount());

        long insert = db.insert(TRANSACTION_TABLE,null,cv);
        return insert != -1;
    }

    public List<Transaction> getAllTransactionLogs(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Transaction> transactionsList=new ArrayList<>();
        String queryString = "";
        if (limit==-1){     //defined for non paginated query
            queryString += "SELECT * FROM "+TRANSACTION_TABLE;
        }
        else {
            queryString += "SELECT * FROM "+TRANSACTION_TABLE+" LIMIT "+limit;
        }
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor.getCount()==0){
            cursor.close();
            db.close();
            return transactionsList;
        }
        else {
            while (cursor.moveToNext()){
                String accountNo = cursor.getString(0);
                Date date = new Date();
                try {
                    date = DATE_FORMAT.parse(cursor.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ExpenseType expenseType = ExpenseType.valueOf(cursor.getString(2));
                double amount = cursor.getDouble(3);
                transactionsList.add(new Transaction(date,accountNo,expenseType,amount));
            }
            cursor.close();
            db.close();
            return transactionsList;
        }
    }

}
