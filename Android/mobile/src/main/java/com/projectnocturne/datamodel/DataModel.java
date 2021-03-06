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
 * </p><p>
 * <b><i>Copyright 2013-2014 Bath Institute of Medical Engineering.</i></b>
 * --------------------------------------------------------------------------
 */
package com.projectnocturne.datamodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.projectnocturne.NocturneApplication;
import com.projectnocturne.contentprovider.NocturneSensorReadingContentProvider;
import com.projectnocturne.contentprovider.NocturneUserConnectContentProvider;
import com.projectnocturne.contentprovider.NocturneUserContentProvider;
import com.projectnocturne.datamodel.DbMetadata.RegistrationStatus;
import com.projectnocturne.db.NocturneDatabaseHelper;
import com.projectnocturne.views.NocturneFragment;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public final class DataModel extends Observable {
    private static final String LOG_TAG = DataModel.class.getSimpleName() + "::";
    private static DataModel instance = null;
    private final List<NocturneFragment> myObservers = new ArrayList<NocturneFragment>();
    private Context ctx;

    private NocturneDatabaseHelper databaseHelper = null;
    private SQLiteDatabase db;

    private DataModel() {
    }

    public static DataModel getInstance(final Context ctx) {
        if (instance == null) {
            instance = new DataModel();
        }
        instance.ctx = ctx;
        return instance;
    }

    public SensorReadingDb addSensorReading(final SensorReadingDb itm) {
        final ContentResolver cr = ctx.getContentResolver();
        final ContentValues values = itm.getContentValues();

        final Uri insertedUri = cr.insert(NocturneSensorReadingContentProvider.CONTENT_URI, values);
        // get the row id - it's the last path segment in the returned uri
        // for the inserted record
        final String lastPathSegment = insertedUri.getLastPathSegment();
        // save the inserted record's row id in global variable
        itm.setUniqueIdentifier(Integer.valueOf(lastPathSegment));

        return itm;
    }

    public UserDb addUser(UserDb itm) {
        final ContentResolver cr = ctx.getContentResolver();
        final ContentValues values = itm.getContentValues();

        final Uri insertedUri = cr.insert(NocturneUserContentProvider.CONTENT_URI, values);
        // get the row id - it's the last path segment in the returned uri
        // for the inserted record
        final String lastPathSegment = insertedUri.getLastPathSegment();
        // save the inserted record's row id in global variable
        itm.setUniqueIdentifier(Integer.valueOf(lastPathSegment));

        return itm;
    }

    public void destroy() {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "destroy()");
        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }

    public RegistrationStatus getRegistrationStatus() {
        final DbMetadata dbMetaDta = getDbMetadata();
        return dbMetaDta.registrationStatus;
    }

    private DbMetadata getDbMetadata() {
        DbMetadata dbMetaDta = null;

        final String selectionSql = null;
        final String[] selectionArgs = null;
        final String groupBy = null;
        final String having = null;
        final String orderBy = null;
        final Cursor results = db.query(DbMetadata.DATABASE_TABLE_NAME, null, selectionSql, selectionArgs, groupBy, having, orderBy);

        if (results.getCount() > 0) {
            results.moveToFirst();
            dbMetaDta = new DbMetadata(results);
        }
        results.close();
        if (dbMetaDta == null) {
            dbMetaDta = new DbMetadata();
            dbMetaDta.lastUpdated = new DateTime().toString(NocturneApplication.simpleDateFmtStrDb);
            db.insert(DbMetadata.DATABASE_TABLE_NAME, null, dbMetaDta.getContentValues());
            NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "getDbMetadata() new metadata object created");
        }
        return dbMetaDta;
    }

    public void setRegistrationStatus(final RegistrationStatus aRequestSent) {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "setRegistrationStatus()");
        final DbMetadata metadata = getDbMetadata();
        metadata.setRegistrationStatus(aRequestSent);
        try {
            final String selection = BaseColumns._ID + "=?";
            final String[] selectionArgs = {String.valueOf(metadata.getUniqueIdentifier())};
            metadata.lastUpdated = new DateTime().toString(NocturneApplication.simpleDateFmtStrDb);
            db.update(DbMetadata.DATABASE_TABLE_NAME, metadata.getContentValues(), selection, selectionArgs);
            setChanged();
            notifyObservers();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyObservers() {
        // NocturneApplication.logMessage(Log.DEBUG, LOG_TAG +
        // "notifyObservers() notifying [" + myObservers.size() +
        // "] observers");
        for (final NocturneFragment a : myObservers) {
            a.update(DataModel.instance, a);
        }
    }

    public UserConnectDb setUserConnection(final UserConnectDb usrCnctDb) {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "setUserConnection()");
        try {
            usrCnctDb.lastUpdated = new DateTime().toString(NocturneApplication.simpleDateFmtStrDb);
            if (usrCnctDb.getUniqueIdentifier() == -1) {
                final ContentResolver cr = ctx.getContentResolver();
                final Uri insertedUri = cr.insert(NocturneUserConnectContentProvider.CONTENT_URI, usrCnctDb.getContentValues());
                final String lastPathSegment = insertedUri.getLastPathSegment();
                usrCnctDb.setUniqueIdentifier(Integer.valueOf(lastPathSegment));
            } else {
                final String selection = BaseColumns._ID + "=?";
                final String[] selectionArgs = {String.valueOf(usrCnctDb.getUniqueIdentifier())};
                db.update(usrCnctDb.DATABASE_TABLE_NAME, usrCnctDb.getContentValues(), selection, selectionArgs);
            }
            setChanged();
            notifyObservers();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return usrCnctDb;
    }

    public UserDb getUser(final int tagId) {
        final String selectionSql = BaseColumns._ID + "=?";
        final String[] selectionArgs = new String[]{"" + tagId};
        final String groupBy = null;
        final String having = null;
        final String orderBy = UserDb.FIELD_NAME_name_first;
        final Cursor results = db.query(UserDb.DATABASE_TABLE_NAME, null, selectionSql, selectionArgs, groupBy, having, orderBy);

        UserDb tg = null;
        if (results.getCount() > 0) {
            results.moveToFirst();
            tg = new UserDb(results);
        }
        results.close();
        return tg;
    }

    public UserDb getUser(final String username) {
        final String selectionSql = UserDb.FIELD_NAME_USERNAME + "=?";
        final String[] projection = null;
        final String[] selectionArgs = new String[]{username};
        final String groupBy = null;
        final String having = null;
        final String orderBy = UserDb.FIELD_NAME_name_first;
        // final Cursor results = db.query(UserDb.DATABASE_TABLE_NAME, null,
        // selectionSql, selectionArgs, groupBy, having,orderBy);

        final ContentResolver cr = ctx.getContentResolver();
        final Cursor results = cr.query(NocturneUserContentProvider.CONTENT_URI, projection, selectionSql, selectionArgs, orderBy);

        UserDb tg = null;
        if (results.getCount() > 0) {
            results.moveToFirst();
            tg = new UserDb(results);
        }
        results.close();
        return tg;
    }

    public List<UserDb> getUsers() {
        final List<UserDb> users = new ArrayList<UserDb>();
        final String[] projection = null;
        final String selectionSql = null;
        final String[] selectionArgs = new String[]{};
        final String groupBy = null;
        final String having = null;
        final String orderBy = UserDb.FIELD_NAME_name_first;
        // final Cursor results = db.query(UserDb.DATABASE_TABLE_NAME, null,
        // selectionSql, selectionArgs, groupBy, having, orderBy);

        final ContentResolver cr = ctx.getContentResolver();
        final Cursor results = cr.query(NocturneUserContentProvider.CONTENT_URI, projection, selectionSql, selectionArgs, orderBy);

        results.moveToFirst();
        UserDb tg = null;
        final int nbrResults = results.getCount();
        while (results.isAfterLast() == false) {
            tg = new UserDb(results);
            users.add(tg);
            results.moveToNext();
        }
        results.close();
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "getUsers() found [" + nbrResults + "] users");
        return users;
    }

    public void initialise(final Context ctx) throws SQLException {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "initialise()");
        if (databaseHelper == null) {
            databaseHelper = new NocturneDatabaseHelper(ctx);
        }
        db = databaseHelper.getWritableDatabase();
        final String logMsg = LOG_TAG + "initialise() db object " + (db == null ? "NOT" : "") + " created";
        NocturneApplication.logMessage(Log.DEBUG, logMsg);
    }

    public void shutdown() {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "shutdown()");
    }

    /**
     * @param itm
     * @return
     */
    public UserDb updateUser(final UserDb itm) {
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "updateUser()");

        final ContentResolver cr = ctx.getContentResolver();
        final ContentValues values = itm.getContentValues();

        final String selection = BaseColumns._ID + "=?";
        final String[] selectionArgs = {String.valueOf(itm.getUniqueIdentifier())};

        final int numberRecordsUpdated = cr.update(NocturneUserContentProvider.CONTENT_URI, values, selection, selectionArgs);
        return itm;
    }

    public List<UserConnectDb> getUsersConnected(UserDb userDbObj) {
        final List<UserConnectDb> userConnections = new ArrayList<UserConnectDb>();
        final String[] projection = null;
        final String selectionSql = UserDb._ID + "=?";
        final String[] selectionArgs = new String[]{String.valueOf(userDbObj.getUniqueIdentifier())};
        final String groupBy = null;
        final String having = null;
        final String orderBy = UserDb.FIELD_NAME_name_first;
        // final Cursor results = db.query(UserDb.DATABASE_TABLE_NAME, null,
        // selectionSql, selectionArgs, groupBy, having, orderBy);

        final ContentResolver cr = ctx.getContentResolver();
        final Cursor results = cr.query(NocturneUserConnectContentProvider.CONTENT_URI, projection, selectionSql, selectionArgs, orderBy);

        results.moveToFirst();
        UserConnectDb tg = null;
        final int nbrResults = results.getCount();
        while (results.isAfterLast() == false) {
            tg = new UserConnectDb(results);
            userConnections.add(tg);
            results.moveToNext();
        }
        results.close();
        NocturneApplication.logMessage(Log.DEBUG, LOG_TAG + "getUsersConnected() found [" + nbrResults + "] connections");
        return userConnections;
    }
}
