package Service;

import DAO.AccountDAO;
import Model.Account;
import java.util.Optional;

/**
 * Business logic for account management.
 * Validates and handles registration and login functionality.
 */
public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Registers a new account if valid and unique
    public Account register(Account account) {
        // Validate: username not blank, password at least 4 characters
        if (account.getUsername().isBlank() || account.getPassword().length() < 4) {
            return null;
        }

        // Ensure username is unique
        if (accountDAO.getAccountByUsername(account.getUsername()).isPresent()) {
            return null;
        }

        // Insert into database
        return accountDAO.insertAccount(account);
    }

    // Authenticates an account by matching credentials
    public Account login(Account account) {
        Optional<Account> existing = accountDAO.getAccountByUsernameAndPassword(
            account.getUsername(), account.getPassword()
        );
        return existing.orElse(null);
    }
}