package com.flaredown.flaredownApp.API.Sync;

import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flaredown.flaredownApp.FlaredownApplication;
import com.flaredown.flaredownApp.Helpers.APIv2_old.EndPointUrl;
import com.flaredown.flaredownApp.Helpers.Volley.QueueProvider;
import com.flaredown.flaredownApp.Helpers.Volley.VolleyRequestWrapper;
import com.flaredown.flaredownApp.Helpers.Volley.WebAttributes;

/**
 *
 */

public abstract class Endpoint<M extends ServerModel, U extends ServerUpdate<M>> {
    public Endpoint() {
        construct();
    }

    /**
     * Method to be called in all constructor methods.
     */
    private void construct() {
    }

    public void sendRequest(final U update, final UpdateManager<M> updateManager) {
        WebAttributes attributes = update.getWebAttributes();

        VolleyRequestWrapper volleyRequest = new VolleyRequestWrapper(
                update.getRequestMethod().getVolleyInt(),
                EndPointUrl.getAPIUrl(update.getEndpoint()),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        updateManager.triggerModelUpdateListener(DataSource.SOURCE, update.generateServerModel(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        updateManager.triggerErrorListener(error, false);
                    }
                }
        );


        if(update.getRequestMethod() == RequestMethod.POST && attributes != null && attributes.size() > 0)
            volleyRequest.setPostParams(attributes);

        QueueProvider.getQueue(FlaredownApplication.getInstance()).add(volleyRequest);
    }
}
