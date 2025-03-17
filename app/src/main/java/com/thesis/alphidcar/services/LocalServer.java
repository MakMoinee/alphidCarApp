package com.thesis.alphidcar.services;

import android.util.Log;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.services.LocalAndroidServer;

import java.io.IOException;
import java.util.Map;

public class LocalServer extends LocalAndroidServer {

    private static final String TAG = "LocalServer";
    DefaultBaseListener listener;
    public String myIp;
    public LocalServer(int port, DefaultBaseListener l) throws IOException {
        super(port, l);
        listener = l;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String clientIp = session.getHeaders().get("remote-addr"); // Get client IP
        myIp = clientIp;
        Log.d(TAG, "Request received from IP: " + clientIp);

        if (session.getMethod() == Method.POST) {

            listener.onSuccess("detected");
            try {
                session.parseBody(null);
            } catch (IOException | ResponseException e) {

//                Log.d(TAG, "error: " + e.getLocalizedMessage());
            }
            Map<String, String> params = session.getParms();
            String message = params.get("message");

            if ("detect".equals(message)) {
                // Log detection and show notification
                Log.d(TAG, "Detection Alert received from: " + clientIp);
            }
        }

        return newFixedLengthResponse("Received");
    }
}
