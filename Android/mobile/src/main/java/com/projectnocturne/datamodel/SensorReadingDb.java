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
package com.projectnocturne.datamodel;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;

public final class SensorReadingDb extends AbstractDataObj {

    public static final String DATABASE_TABLE_NAME = "Sensor_Reading";
    private static final String FIELD_NAME_SENSOR_ID = "SENSOR_ID";
    private static final String FIELD_NAME_SENSOR_READING_TIME = "SENSOR_READING_TIME";
    private static final String FIELD_NAME_SENSOR_VALUE = "SENSOR_VALUE";

    public SensorReading sensorReading = new SensorReading();

    public SensorReadingDb() {
    }

    public SensorReadingDb(final Cursor results) {
        setUniqueIdentifier(results.getString(results.getColumnIndex(BaseColumns._ID)));
        setLastUpdated(results.getString(results.getColumnIndex(AbstractDataObj.FIELD_NAME_LAST_UPDATED)));
        sensorReading.sensor_id = results.getString(results.getColumnIndex(FIELD_NAME_SENSOR_ID));
        sensorReading.sensor_value = results.getString(results.getColumnIndex(FIELD_NAME_SENSOR_VALUE));
        sensorReading.sensor_reading_time = results.getString(results.getColumnIndex(FIELD_NAME_SENSOR_READING_TIME));
    }

    public SensorReadingDb(final HashMap<String, String> aRow) {
        super(aRow);
        sensorReading.sensor_id = aRow.get(FIELD_NAME_SENSOR_ID);
        sensorReading.sensor_value = aRow.get(FIELD_NAME_SENSOR_VALUE);
        sensorReading.sensor_reading_time = aRow.get(FIELD_NAME_SENSOR_READING_TIME);
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues map = super.getContentValues();
        map.put(FIELD_NAME_SENSOR_ID, sensorReading.sensor_id);
        map.put(FIELD_NAME_SENSOR_VALUE, sensorReading.sensor_value);
        map.put(FIELD_NAME_SENSOR_READING_TIME, sensorReading.sensor_reading_time);
        return map;
    }

    @Override
    public SparseArray<ArrayList<String>> getFields() {
        final SparseArray<ArrayList<String>> fields = super.getFields();
        int x = fields.size();
        fields.put(x++, getArrayList(FIELD_NAME_SENSOR_ID, "LONG NOT NULL"));
        fields.put(x++, getArrayList(FIELD_NAME_SENSOR_VALUE, "VARCHAR(255) NOT NULL"));
        fields.put(x++, getArrayList(FIELD_NAME_SENSOR_READING_TIME, "VARCHAR(255) NOT NULL"));
        return fields;
    }

    @Override
    public String getTableName() {
        return DATABASE_TABLE_NAME;
    }

    @Override
    public String toString() {
        return null;
    }
}
