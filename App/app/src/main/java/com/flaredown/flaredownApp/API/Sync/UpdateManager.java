package com.flaredown.flaredownApp.API.Sync;

/**
 * Created by thunter on 23/03/2017.
 */

public class UpdateManager<M extends ServerModel> {
    private OnErrorListener errorListener;
    private OnModelUpdateListener<M> modelUpdateListener;


    public UpdateManager() {

    }

    public UpdateManager<M> setErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public UpdateManager<M> setModelUpdateListener(OnModelUpdateListener<M> modelUpdateListener) {
        this.modelUpdateListener = modelUpdateListener;
        return this;
    }

    protected void triggerModelUpdateListener(DataSource dataSource, M model) {
        this.modelUpdateListener.onUpdate(dataSource, model);
    }

    /**
     * If a error listener has been set trigger it.
     *
     * @param throwable The throwable error what occurred.
     * @param cachedCalled If true {@link OnModelUpdateListener#onUpdate(DataSource, ServerModel)}
     *                     has been called with a cached version.
     */
    protected void triggerErrorListener(Throwable throwable, boolean cachedCalled){
        this.errorListener.onError(throwable, cachedCalled);
    }
}
