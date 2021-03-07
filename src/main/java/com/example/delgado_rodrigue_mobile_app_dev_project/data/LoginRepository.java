package com.example.delgado_rodrigue_mobile_app_dev_project.data;

import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.User;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.UsersAccountsDBUtil;
import com.example.delgado_rodrigue_mobile_app_dev_project.ui.login.LoginViewModel;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private User user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
    }

    private void setLoggedInUser(User user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public void loginInit(String userID, UsersAccountsDBUtil dbUtil, LoginViewModel loginViewModel) {
        // handle login
        dataSource.login(userID, dbUtil, this, loginViewModel);
    }

    public void loginResultHandler(Result<User> result, LoginViewModel loginViewModel) {
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<User>) result).getData());
        }
        loginViewModel.loginResultHandler(result);
    }

    public int getUserID() { return user.getUserID(); }
}