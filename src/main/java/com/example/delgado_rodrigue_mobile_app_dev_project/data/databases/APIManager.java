package com.example.delgado_rodrigue_mobile_app_dev_project.data.databases;

import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.LoginDataSource;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.LoginRepository;
import com.example.delgado_rodrigue_mobile_app_dev_project.ui.login.LoginActivity;
import com.example.delgado_rodrigue_mobile_app_dev_project.ui.login.LoginViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class APIManager {
    private static final String USERS_API_URL = "https://60102f166c21e10017050128.mockapi.io/labbbank/config";
    private static final String ACCOUNTS_API_URL = "https://60102f166c21e10017050128.mockapi.io/labbbank/accounts";

    public static void UsersAPI_UpdateLocalDBForLogin(String userID, UsersAccountsDBUtil dbUtil, LoginRepository loginRepo, LoginViewModel loginViewModel) throws IOException {
        AndroidNetworking.get(USERS_API_URL + "/" + userID).build().getAsJSONObject(
            new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    User user = null;
                    try {
                        user = new User(
                                Integer.parseInt(response.getString("id")),
                                response.getString("name"),
                                response.getString("lastname")
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        dbUtil.deleteUser(user);
                        dbUtil.addUser(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LoginDataSource.loginAfterDBUpdate(userID, dbUtil, loginRepo, loginViewModel);
                }
                @Override
                public void onError(ANError error) {
                    // handle errors
                }
            }
        );
    }

    // TODO: Rework this for its specific use case
    public static void AccountsAPI_UpdateLocalDB(String accountID, UsersAccountsDBUtil dbUtil, LoginViewModel loginViewModel, TextView accountsListTextView) throws IOException {
        AndroidNetworking.get(ACCOUNTS_API_URL + "/" + accountID).build().getAsJSONObject(
            new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    Account account = null;
                    try {
                        account = new Account(
                                Integer.parseInt(response.getString("id")),
                                response.getString("accountName"),
                                response.getDouble("amount"),
                                response.getString("iban"),
                                response.getString("currency")
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        dbUtil.deleteAccount(account);
                        dbUtil.addAccount(account);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    LoginActivity.refreshAccountPageDisplay(dbUtil, loginViewModel, accountsListTextView);
                }
                @Override
                public void onError(ANError error) {
                    // handle errors
                }


            }
        );
    }

    //    public static void HttpsConnection(String url) throws IOException {
    //        AndroidNetworking.get(url).build().getAsJSONObject(
    //            new JSONObjectRequestListener() {
    //                @Override
    //                public void onResponse(JSONObject response) {
    //                    jsonResponse[0] = response;
    //                    // do anything with response
    //                    int a = 0;
    //                    return jsonResponse[0];
    //                }
    //                @Override
    //                public void onError(ANError error) {
    //                    // handle errors
    //                    int a = 0;
    //                }
    //            }
    //        );
    //    }
}
