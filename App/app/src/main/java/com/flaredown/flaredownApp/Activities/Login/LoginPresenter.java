package com.flaredown.flaredownApp.Activities.Login;

import com.flaredown.flaredownApp.Helpers.Wrappers.Mosby.PresenterWrapper;

/**
 * Login Presenter for the {@link LoginFragment}
 */

public class LoginPresenter extends PresenterWrapper<LoginView, LoginModel> {

    public void doSplashScreen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(isViewAttached())
                            getView().showContent();
                    }
                });
            }
        }).start();
    }

    public void doLogin(String username, String password) {
        if(isViewAttached()) {
            getView().showLoading(true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isViewAttached()) {
                                getView().hideLoading();
                            }
                        }
                    });
                }
            }).start();
        }
    }
}
