package Service;

import DAO.AccountDAO;
import Model.Account;
import java.util.List;

public class AccountService {
    
    private AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public boolean accountExists(int accountId) {
        return accountDAO.existsById(accountId);
    }

    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account);
    }

    public Account getAccountByUsername(String username) {
        return accountDAO.getAccountByUsername(username);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }
}
