package com.flaredown.flaredownApp.Activities.Login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.flaredown.flaredownApp.Dagger2.HasComponent;
import com.flaredown.flaredownApp.Helpers.Wrappers.Android.ActivityWrapper;
import com.flaredown.flaredownApp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Login Activity Class, enables the user to log into their Flaredown account.
 *
 * See
 *      - {@link LoginFragment}
 */
public class LoginActivity extends ActivityWrapper {

    private static final String LOGIN_FRAGMENT_TAG = "Login Fragment Tag";

    @BindView(R.id.fl_fragment_container)
    FrameLayout fl_fragmentContainer;

    LoginFragment f_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO in the super manage font injection.

        // Set the content view to one with a fragment wrapper.
        setContentView(R.layout.activity_fragment_wrapper);

        // Bind Butterknife fields.
        ButterKnife.bind(this);
        if(getLoginFragment() == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_fragment_container, f_login = new LoginFragment(), LOGIN_FRAGMENT_TAG)
                    .commit();
    }

    /**
     * Get the login in fragment, if one is active.
     * @return The login fragment, null if none attached.
     */
    public LoginFragment getLoginFragment() {
        if(f_login != null)
            return f_login;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FRAGMENT_TAG);
        if(fragment instanceof LoginFragment) {
            return f_login = (LoginFragment) fragment;
        }
        return null;
    }
}