/**
 * <p>
 * <u><b>Copyright Notice</b></u>
 * </p><p>
 * The copyright in this document is the property of 
 * Bath Institute of Medical Engineering.
 * </p><p>
 * Without the written consent of Bath Institute of Medical Engineering
 * given by Contract or otherwise the document must not be copied, reprinted or
 * reproduced in any material form, either wholly or in part, and the contents
 * of the document or any method or technique available there from, must not be
 * disclosed to any other person whomsoever.
 *  </p><p>
 *  <b><i>Copyright 2013-2014 Bath Institute of Medical Engineering.</i></b>
 * --------------------------------------------------------------------------
 *
 */
package com.projectnocturne.server;

import android.os.AsyncTask;
import android.util.Log;

import com.projectnocturne.NocturneApplication;
import com.projectnocturne.server.SpringRestTask.RequestMethod;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * this AsyncTask takes in two parameters :<br/>
 * <ol>
 * <li>the first is the type of request ("GET" or "POST")</li>
 * <li>the second parameter is the uri to call</li>
 * </ol>
 *
 * @author andy
 */
public final class HttpRequestTask extends AsyncTask<Object, String, String> {

    private static final String LOG_TAG = HttpRequestTask.class.getSimpleName() + ":";

    private HttpResponse doGet(final String uri, final List<NameValuePair> pairs) {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doGet(" + uri + ")");
        HttpResponse response = null;
        try {
            final HttpClient httpclient = new DefaultHttpClient();
            String uriStr = uri + "?";
            for (int x = 0; x < pairs.size(); x++) {
                final NameValuePair nvp = pairs.get(x);
                uriStr += URLEncoder.encode(nvp.getName(), "UTF-8") + "=" + URLEncoder.encode(nvp.getValue(), "UTF-8");
                if (x < pairs.size()) {
                    uriStr += "&";
                }
            }
            response = httpclient.execute(new HttpGet(uriStr));
            return response;
        } catch (final UnsupportedEncodingException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() UnsupportedEncodingException");
        } catch (final ClientProtocolException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() ClientProtocolException");
        } catch (final IOException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() IOException");
        }
        return response;
    }

    @Override
    protected String doInBackground(final Object... uri) {
        final RequestMethod reqMthd = RequestMethod.valueOf(uri[0].toString());
        for (int x = 0; x < uri.length; x++) {
            NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doInBackground() uri[" + x + "] [" + uri[x] + "]");
        }
        try {
            new URL(uri[1].toString());
        } catch (final MalformedURLException e1) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doInBackground() MalformedURLException", e1);
        }

        HttpResponse response;
        String responseString = null;
        try {
            NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doInBackground() reqMthd  ["
                    + (reqMthd == RequestMethod.GET ? "Get" : "Post") + "] uri[0] [" + uri[0] + "] uri[1] [" + uri[1]
                    + "] uri[2] [" + uri[2] + "]");
            switch (reqMthd) {
                case GET: {
                    response = doGet(uri[1].toString(), (List<NameValuePair>) uri[2]);
                    break;
                }
                default: {
                    response = doPost(uri[1].toString(), (List<NameValuePair>) uri[2]);
                    break;
                }
            }
            final StatusLine statusLine = response.getStatusLine();
            NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doInBackground() statusLine [" + statusLine.toString()
                    + "]");

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            } else {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doInBackground() status Not OK [" + out.toString()
                        + "]");
                // Closes the connection.
                out.close();
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (final IllegalStateException ise) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doInBackground() IllegalStateException", ise);
        } catch (final ClientProtocolException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doInBackground() ClientProtocolException", e);
        } catch (final IOException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doInBackground() IOException", e);
        }
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doInBackground() got status OK [" + responseString + "]");
        return responseString;
    }

    private HttpResponse doPost(final String uri, final List<NameValuePair> pairs) {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "doPost(" + uri + ")");
        HttpResponse response = null;
        try {
            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost(uri);
            httppost.setEntity(new UrlEncodedFormEntity(pairs, HTTP.UTF_8));
            response = httpclient.execute(httppost);
            NocturneApplication.logMessage(Log.DEBUG,
                    LOG_TAG + "doPost() httppost : " + EntityUtils.toString(httppost.getEntity()));
            return response;
        } catch (final UnsupportedEncodingException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() UnsupportedEncodingException");
        } catch (final ClientProtocolException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() ClientProtocolException");
        } catch (final IOException e) {
            NocturneApplication.logMessage(Log.ERROR, LOG_TAG + "doPost() IOException");
        }
        return response;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        NocturneApplication.logMessage(Log.INFO, LOG_TAG + "onPostExecute() [" + result + "]");
        // Do something with response..
    }
}
