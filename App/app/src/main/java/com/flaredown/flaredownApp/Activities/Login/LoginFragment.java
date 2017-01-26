package com.flaredown.flaredownApp.Activities.Login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.flaredown.flaredownApp.Dagger2.HasComponent;
import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityComponent;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.FragmentWrapper;
import com.flaredown.flaredownApp.R;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;

import javax.inject.Inject;

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

    }

    @Override
    public void onNewViewStateInstance() {
        super.onNewViewStateInstance();
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
        return null;
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return "";
    }

    @Override
    public LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    public void hideLoading() {

    }


}
