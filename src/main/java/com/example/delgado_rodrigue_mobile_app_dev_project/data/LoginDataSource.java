package com.example.delgado_rodrigue_mobile_app_dev_project.data;

import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.APIManager;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.User;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.UsersAccountsDBUtil;
import com.example.delgado_rodrigue_mobile_app_dev_project.ui.login.LoginViewModel;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public void login(String userID, UsersAccountsDBUtil dbUtil, LoginRepository loginRepo, LoginViewModel loginViewModel) {
        try {
            if (dbUtil.doesUserExist(userID)) {
                User user = dbUtil.findUserByID(userID);
                loginRepo.loginResultHandler(
                    new Result.Success<>(user),
                    loginViewModel
                );
                //return new Result.Success<>(user);
            } else {
                APIManager.UsersAPI_UpdateLocalDBForLogin(userID, dbUtil, loginRepo, loginViewModel);
            }
        } catch (Exception e) {
            loginRepo.loginResultHandler(
                new Result.Error(new IOException("Error logging in", e)),
                loginViewModel
            );
        }
    }

    public static void loginAfterDBUpdate(String userID, UsersAccountsDBUtil dbUtil, LoginRepository loginRepo, LoginViewModel loginViewModel) {
        try {
            if (dbUtil.doesUserExist(userID)) {
                User user = dbUtil.findUserByID(userID);
                loginRepo.loginResultHandler(
                    new Result.Success<>(user),
                    loginViewModel
                );
            } else {
                loginRepo.loginResultHandler(
                    new Result.Error(new IOException("Error logging in")),
                    loginViewModel
                );
            }
        } catch (Exception e) {
            loginRepo.loginResultHandler(
                new Result.Error(new IOException("Error logging in", e)),
                loginViewModel
            );
        }
    }
}