package com.example.delgado_rodrigue_mobile_app_dev_project.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.delgado_rodrigue_mobile_app_dev_project.data.LoginRepository;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.Result;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.User;
import com.example.delgado_rodrigue_mobile_app_dev_project.data.databases.UsersAccountsDBUtil;
import com.example.delgado_rodrigue_mobile_app_dev_project.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void loginInit(String userID, UsersAccountsDBUtil dbUtil) {
        // can be launched in a separate asynchronous job
        loginRepository.loginInit(userID, dbUtil, this);
    }

    public void loginResultHandler(Result<User> result) {  // TODO: Handle display of login result
        if (result instanceof Result.Success) {
            User loggedInUser = ((Result.Success<User>) result).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(loggedInUser.getDisplayName())));
        } else {
            loginResult.setValue(new LoginResult(R.string.login_failed));
        }
    }

    public void loginDataChanged(String userID) {
        if (!isUserIDValid(userID)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    public boolean isUserIDValid(String userID) {
        try {
            Integer.parseInt(userID);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getUserID() { return loginRepository.getUserID(); }
    public void logout() { loginRepository.logout();}
}