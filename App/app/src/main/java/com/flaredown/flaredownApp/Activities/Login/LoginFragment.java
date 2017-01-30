package com.flaredown.flaredownApp.Activities.Login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaredown.flaredownApp.Dagger2.HasComponent;
import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.ErrorMessageDeterminer;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityComponent;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.FragmentWrapper;
import com.flaredown.flaredownApp.R;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login Fragment, Enables the user to login to their Flaredown account.
 *
 * See:
 *      - {@link LoginActivity}
 */

public class LoginFragment
        extends FragmentWrapper<LinearLayout, LoginModel, LoginView, LoginPresenter, LoginViewState>
        implements LoginView {
    private ActivityComponent loginComponent;

    @BindView(R.id.ll_email_login_form)
    LinearLayout ll_emailLoginFrom;

    @BindView(R.id.act_email)
    AutoCompleteTextView act_email;

    @BindView(R.id.et_password)
    EditText et_password;

    @BindView(R.id.errorView)
    TextView tv_error;

    @Inject
    public FlaredownApplication application;
    @Inject
    public ActivityWrapper activity;

    /**
     * Injects the dependencies (Dagger2)
     */
    protected void injectDependencies() {
        // Not too worred about not checking the cast, as if the cast didn't work the activity would
        // crash elsewhere with a NPE.
        ((HasComponent<ActivityComponent>) getActivity()).getComponent().injectFragment(this);
    }

    /**
     * Inflates the R.layout.login_fragment layout.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        injectDependencies();
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override
    public void onNewViewStateInstance() {
        super.onNewViewStateInstance();
        getPresenter().doSplashScreen();
    }

    @Override
    public void onViewStateInstanceRestored(boolean instanceStateRetained) {
        super.onViewStateInstanceRestored(instanceStateRetained);
    }



    /**
     * Create the view state object of this class
     */
    @Override
    public LceViewState<LoginModel, LoginView> createViewState() {
        return new LoginViewState();
    }

    @Override
    public void setData(LoginModel data) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {

    }

    @Override
    public LoginModel getData() {
        return getViewState().getLoadedData();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return ErrorMessageDeterminer.getErrorMessage(e).getMessage();
    }

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        getViewState().setStateShowLoading();
        ll_emailLoginFrom.setVisibility(View.GONE);
    }

    public void hideLoading() {
        getViewState().setStateHideLoading();
        getViewState().apply(this, true, false);
    }

    @Override
    public void showContent() {
        getViewState().setStateShowContent(getData());
        ll_emailLoginFrom.setVisibility(View.VISIBLE);
        act_email.requestFocus();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        getViewState().setStateShowError(e, pullToRefresh);
        if(e == null) {
            // Hide the error.
            tv_error.setVisibility(View.GONE);
            return;
        }
        tv_error.setText(getErrorMessage(e, pullToRefresh));
        tv_error.setVisibility(View.VISIBLE);
    }

    @Override
    public void clearError() {
        showError(null, true);
    }

    @Override
    public void setFieldErrorMessages(String email, String password) {
        getViewState().setStateErrorFieldMessages(email, password);
        act_email.setError(email);
        et_password.setError(password);
    }

    @Override
    public void clearFields() {
        setFieldErrorMessages(null, null);
        clearError();
        et_password.setText("");
    }

    @Override
    public void showSplashScreen() {
        getViewState().setStateSplashScreen();
        ll_emailLoginFrom.setVisibility(View.GONE);
    }

    @OnClick(R.id.bt_sign_in)
    public void loginClick(View view) {
        getPresenter().doLogin(act_email.getText().toString(), et_password.getText().toString());
    }
}
