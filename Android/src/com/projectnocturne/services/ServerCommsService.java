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
package com.projectnocturne.services;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.projectnocturne.NocturneApplication;
import com.projectnocturne.SettingsFragment;
import com.projectnocturne.datamodel.Alert;
import com.projectnocturne.datamodel.Sensor;
import com.projectnocturne.datamodel.User;
import com.projectnocturne.server.HttpRequestTask;
import com.projectnocturne.server.HttpRequestTask.RequestMethod;
import com.projectnocturne.server.RestUriFactory;
import com.projectnocturne.server.RestUriFactory.RestUriType;

public final class ServerCommsService {
	private static final String LOG_TAG = ServerCommsService.class.getSimpleName() + "::";

	public void checkUserStatus(final Context ctx, final User obj) {
		Log.i(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "checkUserStatus()");

		final List<NameValuePair> uriData = RestUriFactory.getUri(RestUriType.CHECK_USER_STATUS, obj);

		if (uriData.size() == 0) {
			Log.e(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "checkUserStatus() for " + obj.username);
			return;
		}

		final HttpRequestTask restReq = new HttpRequestTask();
		final String serverAddr = this.getServerAddress(ctx);
		restReq.execute(RequestMethod.POST.toString(), serverAddr + "check_user_status", uriData);
	}

	private String getServerAddress(final Context ctx) {
		// getting preferences from a specified file
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
		final String serverAddr = "http://"
				+ settings
						.getString(SettingsFragment.PREF_SERVER_ADDRESS, SettingsFragment.PREF_SERVER_ADDRESS_DEFAULT)
				+ ":"
				+ settings.getString(SettingsFragment.PREF_SERVER_PORT, SettingsFragment.PREF_SERVER_PORT_DEFAULT)
				+ "/";
		return serverAddr;
	}

	public void sendAlert(final Context ctx, final Alert obj) {
		Log.i(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendAlert() " + obj.alert_name);

		final List<NameValuePair> uriData = RestUriFactory.getUri(RestUriType.SEND_ALERT, obj);

		if (uriData.size() == 0) {
			Log.e(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendAlert() for " + obj.alert_name);
			return;
		}

		final HttpRequestTask restReq = new HttpRequestTask();
		final String serverAddr = this.getServerAddress(ctx);
		restReq.execute(RequestMethod.POST.toString(), serverAddr + "alert_from_patient", uriData);
	}

	public void sendSensorReading(final Context ctx, final Sensor obj) {
		Log.i(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendSensorReading() " + obj.sensor_name);

		final List<NameValuePair> uriData = RestUriFactory.getUri(RestUriType.SEND_SENSOR_READING, obj);

		if (uriData.size() == 0) {
			Log.e(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendSensorReading() for "
					+ obj.sensor_name);
			return;
		}

		final HttpRequestTask restReq = new HttpRequestTask();
		final String serverAddr = this.getServerAddress(ctx);
		restReq.execute(RequestMethod.POST.toString(), serverAddr + "send_sendor_reading", uriData);
	}

	public void sendSubscriptionMessage(final Context ctx, final User obj) {
		Log.i(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendSubscriptionMessage() for "
				+ obj.name_first);

		final List<NameValuePair> uriData = RestUriFactory.getUri(RestUriType.SUBSCRIBETO_SERVICE, obj);

		if (uriData.size() == 0) {
			Log.e(NocturneApplication.LOG_TAG, ServerCommsService.LOG_TAG + "sendSubscriptionMessage() for "
					+ obj.name_first);
			return;
		}

		final HttpRequestTask restReq = new HttpRequestTask();
		final String serverAddr = this.getServerAddress(ctx);
		restReq.execute(RequestMethod.POST.toString(), serverAddr + "users/register", uriData);
	}

}
