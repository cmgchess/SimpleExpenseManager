package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private DBHelper dbHelper;

    public PersistentAccountDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNumbers = new ArrayList<>();
        List<Account> accountsList = dbHelper.getAccountsList();
        if (accountsList.isEmpty()){
            return accountNumbers;
        }
        else {
            for (Account account: accountsList){
                accountNumbers.add(account.getAccountNo());
            }
            return accountNumbers;
        }
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHelper.getAccountsList();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        List<String> accountNumbers = getAccountNumbersList();
        if (!accountNumbers.contains(accountNo)){
            throw new InvalidAccountException("Invalid Account Number");
        }
        return dbHelper.getAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbHelper.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        List<String> accountNumbers = getAccountNumbersList();
        if (!accountNumbers.contains(accountNo)){
            throw new InvalidAccountException("Invalid Account Number");
        }
        dbHelper.removeAccount(accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        List<String> accountNumbers = getAccountNumbersList();
        if (!accountNumbers.contains(accountNo)){
            throw new InvalidAccountException("Invalid Account Number");
        }
        Account account = dbHelper.getAccount(accountNo);
        double balance = account.getBalance();
        if (expenseType==ExpenseType.INCOME){
            account.setBalance(balance+amount);
        }
        else {
            account.setBalance(balance-amount);
        }
        if (account.getBalance()<0){
            throw new InvalidAccountException("Balance Insufficient");
        }
        else {
            dbHelper.updateAccount(account);
        }
    }
}
