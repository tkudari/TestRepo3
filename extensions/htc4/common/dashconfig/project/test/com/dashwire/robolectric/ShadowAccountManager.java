package com.dashwire.robolectric;

import android.accounts.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.util.Implementation;
import com.xtremelabs.robolectric.util.Implements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Implements(AccountManager.class)
public class ShadowAccountManager {

    @Implementation
    public static AccountManager get(Context context) {
        return Robolectric.newInstanceOf(AccountManager.class);
    }

    private List<Account> accounts = new ArrayList<Account>();

    @Implementation
    public Account[] getAccounts() {
        return (Account[]) accounts.toArray(new Account[accounts.size()]);
    }

    
    @Implementation
    public Account[] getAccountsByType (String type) {
        List<Account> accountsByType = new ArrayList<Account>();
        for (Account account : accounts) {
            if (account.type.equals(type)) {
                accountsByType.add(account);
            }
        }
        return (Account[]) accountsByType.toArray(new Account[accountsByType.size()]);
    }
    
    @Implementation
    public boolean addAccountExplicitly(Account account, String password, Bundle userdata) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
            return true;
        }
        return false;
    }
    
    private static class AccountManagerFutureImpl<T> implements AccountManagerFuture<T> {

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            throw new RuntimeException("Mock method not implemented.");
        }

        @Override
        public T getResult() throws OperationCanceledException, IOException, AuthenticatorException {
            throw new RuntimeException("Mock method not implemented.");
        }

        private T result;

        private AuthenticatorException authenticatorException;
        public void setResult(AuthenticatorException authenticatorException) {
            this.authenticatorException = authenticatorException;
        }
        
        @Override
        public T getResult(long timeout, TimeUnit unit) throws OperationCanceledException, IOException, AuthenticatorException {
            if (authenticatorException != null) {
                throw authenticatorException;
            }
            if (ioException != null) {
                throw ioException;
            }
            return result;
        }

        @Override
        public boolean isCancelled() {
            throw new RuntimeException("Mock method not implemented.");
        }

        @Override
        public boolean isDone() {
            throw new RuntimeException("Mock method not implemented.");
        }

        private IOException ioException;
        public void setResult(NetworkErrorException networkErrorException) {
            this.ioException = new IOException(networkErrorException);
        }

        public void setResult(T result) {
            this.result = result;
        }
    }
    
    @Implementation
    public AccountManagerFuture<Boolean> removeAccount(Account account, AccountManagerCallback<Boolean> callback, Handler handler) {
        accounts.remove(account);
        return new AccountManagerFutureImpl<Boolean>();
    }
    
    private HashMap<String, AbstractAccountAuthenticator> authenticators = new HashMap<String, AbstractAccountAuthenticator>();

    public void addAuthenticator(String accountType, AbstractAccountAuthenticator authenticator) {
        authenticators.put(accountType, authenticator);
    }
    
    @Implementation
    public AccountManagerFuture<Bundle> addAccount(String accountType, String authTokenType, String[] requiredFeatures, Bundle addAccountOptions, Activity activity, AccountManagerCallback<Bundle> callback, Handler handler) {
        
        AccountManagerFutureImpl<Bundle> result = new AccountManagerFutureImpl<Bundle>();
        AbstractAccountAuthenticator authenticator = authenticators.get(accountType);
        
        if (authenticator == null) {
            result.setResult(new AuthenticatorException());
            return result;
        }
        
        AccountAuthenticatorResponse response = new AccountAuthenticatorResponse(null);
        try {
            Bundle bundle = authenticator.addAccount(response, accountType, authTokenType, requiredFeatures, addAccountOptions);
            result.setResult(bundle);
        } catch (NetworkErrorException e) {
            result.setResult(e);
        }
        return result;
        
    }
    
}

