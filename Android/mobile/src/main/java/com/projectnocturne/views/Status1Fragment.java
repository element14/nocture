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
package com.projectnocturne.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.percolate.caffeine.ViewUtils;
import com.projectnocturne.NocturneApplication;
import com.projectnocturne.R;
import com.projectnocturne.services.SensorTagService;

public class Status1Fragment extends NocturneFragment {
    public static final String LOG_TAG = Status1Fragment.class.getSimpleName() + "::";

    private boolean readyFragment;

    private TextView txtStatusScr1StatusItem1;
    private TextView txtStatusScr1StatusItem1Value;
    private TextView txtStatusScr1StatusItem2;
    private TextView txtStatusScr1StatusItem2Value;
    private TextView txtStatusScr1StatusItem3;
    private TextView txtStatusScr1StatusItem3Value;
    private TextView txtStatusScr1StatusItem4;
    private TextView txtStatusScr1StatusItem4Value;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        update();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_status_1, container, false);

        txtStatusScr1StatusItem1 = ViewUtils.findViewById(v, R.id.statusScr1StatusItem1);
        txtStatusScr1StatusItem1Value = ViewUtils.findViewById(v, R.id.statusScr1StatusItem1_value);
        txtStatusScr1StatusItem2 = ViewUtils.findViewById(v, R.id.statusScr1StatusItem2);
        txtStatusScr1StatusItem2Value = ViewUtils.findViewById(v, R.id.statusScr1StatusItem2_value);
        txtStatusScr1StatusItem3 = ViewUtils.findViewById(v, R.id.statusScr1StatusItem3);
        txtStatusScr1StatusItem3Value = ViewUtils.findViewById(v, R.id.statusScr1StatusItem3_value);
        txtStatusScr1StatusItem4 = ViewUtils.findViewById(v, R.id.statusScr1StatusItem4);
        txtStatusScr1StatusItem4Value = ViewUtils.findViewById(v, R.id.statusScr1StatusItem4_value);

        // Instantiates a new SensorTagStatusReceiver
        SensorTagStatusReceiver mSensorTagStateReceiver = new SensorTagStatusReceiver();

        // The filter's action is BROADCAST_ACTION
        IntentFilter mStatusIntentFilter = new IntentFilter(SensorTagService.ACTION_DATA_AVAILABLE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSensorTagStateReceiver, mStatusIntentFilter);

        mStatusIntentFilter = new IntentFilter(SensorTagService.ACTION_GATT_CONNECTED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSensorTagStateReceiver, mStatusIntentFilter);

        mStatusIntentFilter = new IntentFilter(SensorTagService.ACTION_GATT_DISCONNECTED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSensorTagStateReceiver, mStatusIntentFilter);

        readyFragment = true;

        setHasOptionsMenu(true);
        update();
        return v;
    }

    public void update() {
        if (!readyFragment) {
            Log.i(NocturneApplication.LOG_TAG, Status1Fragment.LOG_TAG + "update() not ready");
            return;
        }
        Log.i(NocturneApplication.LOG_TAG, Status1Fragment.LOG_TAG + "update() ready");

    }

    // Broadcast receiver for receiving status updates from the IntentService
    private class SensorTagStatusReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private SensorTagStatusReceiver() {
        }

        // Called when the BroadcastReceiver gets an Intent it's registered to receive
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(SensorTagService.ACTION_GATT_CONNECTED)) {
                Log.i(NocturneApplication.LOG_TAG, Status1Fragment.LOG_TAG + "SensorTagStatusReceiver::onReceive() ACTION_GATT_CONNECTED");
            } else if (intent.getAction().equalsIgnoreCase(SensorTagService.ACTION_GATT_DISCONNECTED)) {
                Log.i(NocturneApplication.LOG_TAG, Status1Fragment.LOG_TAG + "SensorTagStatusReceiver::onReceive() ACTION_GATT_DISCONNECTED");
            } else if (intent.getAction().equalsIgnoreCase(SensorTagService.ACTION_DATA_AVAILABLE)) {
                Log.i(NocturneApplication.LOG_TAG, Status1Fragment.LOG_TAG + "SensorTagStatusReceiver::onReceive() ACTION_DATA_AVAILABLE");
            }


        }
    }
}
