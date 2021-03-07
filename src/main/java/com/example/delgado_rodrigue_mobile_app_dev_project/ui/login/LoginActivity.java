package com.example.delgado_rodrigue_mobile_app_dev_project.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.example.delgado_rodrigue_mobile_app_dev_project.R;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.APIManager;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.Account;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.UsersAccountsDBUtil;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private UsersAccountsDBUtil dbUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AndroidNetworking.initialize(getApplicationContext());

        createLoginPage();
    }

    private void createLoginPage() {
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        dbUtil = new UsersAccountsDBUtil(this);

        final EditText userIDEditText = findViewById(R.id.EditText_LoginPage_UserID);
        final Button loginButton = findViewById(R.id.Button_LoginPage_Login);
        final Button quitButton = findViewById(R.id.Button_LoginPage_Quit);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    userIDEditText.setError(getString(loginFormState.getUsernameError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) return;
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    createAccountsPage(loginResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(userIDEditText.getText().toString());
            }
        };
        userIDEditText.addTextChangedListener(afterTextChangedListener);
        userIDEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.loginInit(userIDEditText.getText().toString(), dbUtil);
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.loginInit(userIDEditText.getText().toString(), dbUtil);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void createAccountsPage(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " " + model.getDisplayName() + "!";

        setContentView(R.layout.activity_accounts);
        final TextView welcomeMessageTextView = findViewById(R.id.TextView_AccountsPage_WelcomeMessage);
        final TextView accountsListTextView = findViewById(R.id.TextView_AccountsPage_AccountsList);
        final Button refreshButton = findViewById(R.id.Button_AccountsPage_Refresh);
        final Button logoutButton = findViewById(R.id.Button_AccountsPage_Logout);

        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        welcomeMessageTextView.setText(welcome);
        try {
            APIManager.AccountsAPI_UpdateLocalDB(String.valueOf(loginViewModel.getUserID()), dbUtil, loginViewModel, accountsListTextView);
        } catch (Exception e){
            e.printStackTrace();
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountsListTextView.setText("Refreshing...");
                try {
                    APIManager.AccountsAPI_UpdateLocalDB(String.valueOf(loginViewModel.getUserID()), dbUtil, loginViewModel, accountsListTextView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loginViewModel.logout();
                    createLoginPage();
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void refreshAccountPageDisplay(UsersAccountsDBUtil dbUtil, LoginViewModel loginViewModel, TextView accountsListTextView) {
        List<Account> thisUsersAccounts = null;
        try {
            thisUsersAccounts = dbUtil.getAccountsByUserID(String.valueOf(loginViewModel.getUserID()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String displayableAccountsList = " --- Your accounts --- \n";
        for (Account account : thisUsersAccounts) {
            displayableAccountsList += account.getDisplayableString();
        }
        accountsListTextView.setText(displayableAccountsList);
    }
}