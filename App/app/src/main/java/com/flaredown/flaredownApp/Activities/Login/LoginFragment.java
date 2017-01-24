package com.flaredown.flaredownApp.Activities.Login;

import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.FragmentWRapper;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

/**
 * Login Fragment, Enables the user to login to their Flaredown account.
 *
 * See:
 *      - {@link LoginActivity}
 */

public class LoginFragment extends FragmentWRapper<LinearLayout, LoginModel, LoginView, LoginPresenter, LoginViewState> {

    /**
     * Create the view state object of this class
     */
    @Override
    public LceViewState<LoginModel, LoginView> createViewState() {
        return null;
    }

    @Override
    public void setData(LoginModel data) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {

    }

    @Override
    public LoginModel getData() {
        return null;
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return "";
    }

    @Override
    public LoginPresenter createPresenter() {
        return null;
    }
}
