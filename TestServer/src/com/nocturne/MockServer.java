/**
 --------------------------------------------------------------------------

 SOFTWARE FILE HEADER
 -----------------------------

 Classification : UNCLASSIFIED

 Project Name   : ASTRAEA 2 - Mobile I.P. Node

 --------------------------------------------------------------------------

 Copyright Notice
 ----------------

 The copyright in this document is the property of Cassidian
 Systems Limited.

 Without the written consent of Cassidian Systems Limited
 given by Contract or otherwise the document must not be copied, reprinted or
 reproduced in any material form, either wholly or in part, and the contents
 of the document or any method or technique available there from, must not be
 disclosed to any other person whomsoever.

 Copyright 2014 Cassidian Systems Limited.
 --------------------------------------------------------------------------

 */
package com.nocturne;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.simpleframework.http.*;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author aspela
 */
public class MockServer implements Container {

    private static final Logger logger = LogManager.getLogger(MockServer.class.getName());

    private static final String DB_NAME = "./mockserver.db";
    private SqlJetDb db = null;

    public static void main(final String[] list) throws Exception {  
        logger.info("MockServer::main() starting");

        final MockServer mockSvr = new MockServer();
        final Server server = new ContainerServer(mockSvr);
        final Connection socketConnection = new SocketConnection(server);
        final SocketAddress address = new InetSocketAddress(9090);

        mockSvr.dbInitialise();

        socketConnection.connect(address);
        logger.info("started listening on [" + address.toString() + "]");
    }

    private String getJsonString(final String key, final String value) {
        return "\"" + key + "\":\"" + value + "\"";
    }

    @Override
    public void handle(final Request request, final Response response) {
        logger.info("MockServer::handle() starting");
        PrintStream body = null;
        try {
            body = response.getPrintStream();
            final long time = System.currentTimeMillis();

            //response.setValue("Content-Type", "text/plain");
            response.setValue("Content-Type", "application/json");
            //response.setValue("Content-Encoding", "application/json");
            response.setValue("Server", "MockServer/1.0 (Simple 4.0)");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            final ContentType type = request.getContentType();
            if (type != null) {
                logger.info("request context-type was [" + type.toString() + "]");
                final String primary = type.getPrimary();
                final String secondary = type.getSecondary();
                final String charset = type.getCharset();
            }

            final long length = request.getContentLength();
            final String contentBody = request.getContent();
            final boolean persistent = request.isKeepAlive();
            logger.info("MockServer() handle() contentBody [" + contentBody + "]");

            final Path path = request.getPath();
            final String directory = path.getDirectory();
            final String name = path.getName();
            final String[] segments = path.getSegments();

            logger.info("MockServer() handle() path [" + path + "]");
            logger.info("MockServer() handle() directory [" + directory + "]");
            logger.info("MockServer() handle() name [" + name + "]");

            for (String seg : segments) {
                logger.info("MockServer() handle() Segment [" + seg + "]");
            }

            final Query query = request.getQuery();
            final String value = query.get("key");

            if (directory.equalsIgnoreCase("/users/")) {
                if (name.equalsIgnoreCase("register")) {
                    handleRequestUserRegister(request, body);
                } else if (name.equalsIgnoreCase("connect")) {
                    handleRequestUserConnect(request, body);
                }
            }
            //body.println("Hello World");

            logger.info("MockServer() handle() sending response [" + response.toString() + "]");
            response.commit();
            body.close();
        } catch (final Exception e) {
            e.printStackTrace();
            if (body != null) {
                // body.println("{\"RESTResponseMsg\": {\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"exception occured\"}}");
                body.println("{\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"exception occured\"}");
                body.close();
            }
        }
    }

    /**
     * @param request
     * @param body
     */
    private void handleRequestUserRegister(final Request request, final PrintStream body) {
        logger.info("MockServer::handleRequestUserRegister() starting");

        String jsonStr = null;
        try {
            jsonStr = request.getContent();

            JSONObject userObj = (JSONObject) JSONValue.parse(jsonStr); //get "user" object

            String username = userObj.get("username").toString();
            String name_last = userObj.get("name_last").toString();
            String name_first = userObj.get("name_first").toString();
            String email = "";
            if (userObj.containsKey("email1")) {
                email = userObj.get("email1").toString();
            }
            String status = "";
            if (userObj.containsKey("status")) {
                status = userObj.get("status").toString();
            }
            String addr_line1 = "";
            if (userObj.containsKey("addr_line1")) {
                addr_line1 = userObj.get("addr_line1").toString();
            }
            String addr_line2 = "";
            if (userObj.containsKey("addr_line2")) {
                addr_line2 = userObj.get("addr_line2").toString();
            }
            String addr_line3 = "";
            if (userObj.containsKey("addr_line3")) {
                addr_line3 = userObj.get("addr_line3").toString();
            }
            String postcode = "";
            if (userObj.containsKey("postcode")) {
                postcode = userObj.get("postcode").toString();
            }
            String phone_home = "";
            if (userObj.containsKey("phone_home")) {
                phone_home = userObj.get("phone_home").toString();
            }
            String phone_mobile = userObj.get("phone_mbl").toString();

            db.beginTransaction(SqlJetTransactionMode.WRITE);
            ISqlJetTable table = db.getTable("nocturne_users");
            table.insert(username, name_first, name_last, email, phone_mobile, phone_home, addr_line1, addr_line2, addr_line3, postcode, "REGISTERED");
            db.commit();

            //body.println("{" + getJsonString("key", "value") + "}");
            //body.println("{\"RESTResponseMsg\": {\"request\":\"/users/register\",\"status\":\"success\",\"message\": \"User registered\"}}");
            String respStr = ("{\"request\":\"/users/register\",\"status\":\"success\",\"message\": \"User registered\"}");
            logger.info("handleRequestUserRegister() sending response : " + respStr);
            body.println(respStr);
        } catch (IOException e) {
            logger.error("handleRequestUserRegister() Exception : ", e);
            body.println("{\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"getting JSON from http request failed\"}");
        } catch (SqlJetException e) {
            logger.error("handleRequestUserRegister() Exception : ", e);
            body.println("{\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"Adding user to database failed\"}");
        }
    }

    /**
     * @param request
     * @param body
     */
    private void handleRequestUserConnect(final Request request, final PrintStream body) {
        logger.info("MockServer::handleRequestUserConnect() starting");

        Query reqQry = request.getQuery();
        String user_email = null;
        if (reqQry != null) {
            user_email = reqQry.get("user_email");
        }
        if (user_email != null && user_email.length() > 0) {
            //No body data, so it's a GET???
            handleRequestGetConnectedUsers(request, body);
        } else {

            String jsonStr = null;
            try {
                jsonStr = request.getContent();
                logger.info("handleRequestUserConnect() received request : " + jsonStr);

                //FIXME : parse request message
                JSONObject userObj = (JSONObject) JSONValue.parse(jsonStr);

                String user1_email = "";
                if (userObj.containsKey("user1_email")) {
                    user1_email = userObj.get("user1_email").toString();
                }
                String user2_email = "";
                if (userObj.containsKey("user2_email")) {
                    user2_email = userObj.get("user2_email").toString();
                }
                String user1_role = "";
                if (userObj.containsKey("user1_role")) {
                    user1_role = userObj.get("user1_role").toString();
                }
                String user2_role = "";
                if (userObj.containsKey("user2_role")) {
                    user2_role = userObj.get("user2_role").toString();
                }
                String status = "";
                if (userObj.containsKey("status")) {
                    status = userObj.get("status").toString();
                }

                db.beginTransaction(SqlJetTransactionMode.WRITE);
                ISqlJetTable table = db.getTable("nocturne_user_connect");
                table.insert(user1_email, user1_role, user2_email, user2_role, status);
                db.commit();

                String respStr = "{\"request\":\"/users/connect\",\"status\":\"success\",\"message\": \"User connection registered\"";
                respStr += ","+jsonStr.substring(1, jsonStr.length() - 1);
                respStr += "}";
                logger.info("handleRequestUserConnect() sending response : " + respStr);
                body.println(respStr);
            } catch (IOException e) {
                logger.error("handleRequestUserConnect() Exception : ", e);
                body.println("{\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"getting JSON from http request failed\"}");
            } catch (SqlJetException e) {
                logger.error("handleRequestUserConnect() Exception : ", e);
                body.println("{\"request\":\"/users/register\",\"status\":\"failed\",\"message\": \"Adding user connection to database failed\"}");
            }
        }
    }

    private void handleRequestGetConnectedUsers(final Request request, final PrintStream body) {
        logger.info("MockServer::handleRequestGetConnectedUsers() starting");
        try {
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            ISqlJetTable table = db.getTable("nocturne_user_connect");
            ISqlJetCursor cursor = table.open();
            long rowCount = cursor.getRowCount();

            String respStr = "{\"request\":\"/users/connect\",\"status\":\"success\",\"message\": {\"user_connections\":";
            if (rowCount == 0) {
                respStr += "{}}}";
            } else {
                do {
                    respStr += "{";
                    respStr += "\"user1_email\":\"" + cursor.getString("user1_email") + "\"";
                    respStr += "\"user1_role\":\"" + cursor.getString("user1_role") + "\"";
                    respStr += "\"user2_email\":\"" + cursor.getString("user2_email") + "\"";
                    respStr += "\"user2_role\":\"" + cursor.getString("user2_role") + "\"";
                    respStr += "\"status\":\"" + cursor.getString("status") + "\"";
                    respStr += "}";
                } while (cursor.next());
                respStr += "}}";
                respStr.replace("}{", "},{");
            }
            cursor.close();
            logger.info("handleRequestGetConnectedUsers() sending response : " + respStr);
            body.println(respStr);
        } catch (Exception e) {
            logger.error(e.getClass().getName() + ": handleRequestGetConnectedUsers() ", e);
        }
    }

    public void dbInitialise() {
        logger.info("MockServer::dbInitialise() starting");
        try {
            dbOpenConnection();
            if (!isDbSetup()) {
                dbCreate();
                dbCreateTables();
                dbCreateDummyData();
            }
        } catch (Exception e) {
            logger.error(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public boolean isDbSetup() {
        logger.info("MockServer::isDbSetup() starting");
        boolean issetup = false;
        try {
            db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
            ISqlJetTable table = db.getTable("nocturne_user_sensors");
            if (table != null) {
                issetup = true;
            }
        } catch (Exception e) {
            //logger.error(e.getClass().getName() + ": isDbSetup() ", e);
            logger.info("isDbSetup() DB not setup yet");
        }
        return issetup;
    }

    private void dbCreate() {
        logger.info("MockServer::dbCreate() starting");
        try {
            File dbFile = new File(DB_NAME);
            boolean deleted = dbFile.delete();
            dbCloseConnection();
            db = SqlJetDb.open(dbFile, true);
            db.getOptions().setAutovacuum(true);
            db.beginTransaction(SqlJetTransactionMode.WRITE);
            try {
                db.getOptions().setUserVersion(1);
            } finally {
                db.commit();
            }
        } catch (SqlJetException e) {
            logger.error(e.getClass().getName() + ": dbOpenConnection() ", e);
            System.exit(0);
        }
    }

    private void dbOpenConnection() {
        logger.info("MockServer::dbOpenConnection() starting");
        if (db == null) {
            try {
                File dbFile = new File(DB_NAME);
                db = SqlJetDb.open(dbFile, true);
            } catch (SqlJetException e) {
                logger.error(e.getClass().getName() + ": dbOpenConnection() ", e);
                System.exit(0);
            }
        }
    }

    private void dbCloseConnection() {
        logger.info("MockServer::dbCloseConnection() starting");
        try {
            if (db != null) {
                db.close();
            }
        } catch (SqlJetException e) {
            logger.error(e.getClass().getName() + ": dbCloseConnection()", e);
        }
    }

    private void dbCreateTables() {
        logger.info("MockServer::dbCreateTables() starting");
        try {
            db.beginTransaction(SqlJetTransactionMode.WRITE);

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_users (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  username VARCHAR(45) NOT NULL,\n" +
                    "  name_first VARCHAR(45) NOT NULL,\n" +
                    "  name_last VARCHAR(45) NOT NULL,\n" +
                    "  email1 VARCHAR(45) NOT NULL,\n" +
                    "  phone_mbl VARCHAR(45) NOT NULL,\n" +
                    "  phone_home VARCHAR(45) ,\n" +
                    "  addr_line1 VARCHAR(45) ,\n" +
                    "  addr_line2 VARCHAR(45) ,\n" +
                    "  addr_line3 VARCHAR(45) ,\n" +
                    "  postcode VARCHAR(45),\n" +
                    "  registration_status VARCHAR(45) NOT NULL)");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_conditions (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  condition_name VARCHAR(45) NOT NULL,\n" +
                    "  condition_desc VARCHAR(45) NULL);");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_user_condition (\n" +
                    "  user_id INTEGER NOT NULL,\n" +
                    "  condition_id INTEGER NOT NULL,\n" +
                    "  PRIMARY KEY (user_id, condition_id));");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_user_connect (\n" +
                    "  user1_email VARCHAR(45) NOT NULL,\n" +
                    "  user1_role VARCHAR(45) NOT NULL,\n" +
                    "  user2_email VARCHAR(45) NOT NULL,\n" +
                    "  user2_role VARCHAR(45) NOT NULL,\n" +
                    "  status VARCHAR(45) NOT NULL,\n" +
                    "  PRIMARY KEY (user1_email, user2_email));");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_alerts (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  user_Id INTEGER NOT NULL,\n" +
                    "  alert_name VARCHAR(45) NOT NULL,\n" +
                    "  alert_desc VARCHAR(45) NULL,\n" +
                    "  response VARCHAR(255) NULL,\n" +
                    "  response_sent TINYINT(1) NULL);");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_sensor (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  sensor_name VARCHAR(45) NULL,\n" +
                    "  sensor_desc VARCHAR(45) NULL);");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_sensor_timeperiods (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  start_time VARCHAR(45) NULL,\n" +
                    "  stop_time VARCHAR(45) NULL,\n" +
                    "  sensor_value_exprected VARCHAR(45) NULL,\n" +
                    "  sensor_warm_time VARCHAR(45) NULL,\n" +
                    "  sensor_alert_time VARCHAR(45) NULL,\n" +
                    "  sensor_id INTEGER NOT NULL);");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_sensor_reading (\n" +
                    "  _id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  sensor_id INTEGER NOT NULL,\n" +
                    "  sensor_value VARCHAR(45) NOT NULL,\n" +
                    "  sensor_reading_time VARCHAR(45) NOT NULL);");

            db.createTable("CREATE TABLE IF NOT EXISTS nocturne_user_sensors (\n" +
                    "  user_id INTEGER NOT NULL,\n" +
                    "  sensor_timeperiods_id INTEGER NOT NULL,\n" +
                    "  sensor_reading__id INTEGER NOT NULL,\n" +
                    "  sensor_reading_sensor_id INTEGER NOT NULL,\n" +
                    "  PRIMARY KEY (user_id, sensor_timeperiods_id));");

            db.commit();
        } catch (SqlJetException e) {
            logger.error(e.getClass().getName() + ": dbCreateTables() ", e);
        }
    }

    private void dbCreateDummyData() {
        logger.info("MockServer::dbCreateDummyData() starting");
        try {
            db.beginTransaction(SqlJetTransactionMode.WRITE);
            ISqlJetTable tblNocturneUsers = db.getTable("nocturne_users");
            tblNocturneUsers.insert("aspellclark@yahoo.co.uk", "AndyY", "Aspell-Clark", "aspellclark@yahoo.co.uk", "07986", "0117", "22 smithcourt", "", "", "bs34", "REGISTERED");
            tblNocturneUsers.insert("droidinactu@gmail.com", "AndyD", "Aspell-Clark", "droidinactu@gmail.com", "07986", "0117", "22 smithcourt", "", "", "bs34", "REGISTERED");

            ISqlJetTable tblNocturneCondition = db.getTable("nocturne_conditions");
            tblNocturneCondition.insert("Cancer", "");
            tblNocturneCondition.insert("Coronary Heart Disease", "");
            tblNocturneCondition.insert("Diabetes", "");
            tblNocturneCondition.insert("Dementia", "");
            tblNocturneCondition.insert("Depression", "");
            tblNocturneCondition.insert("Osteoporosis", "");
            tblNocturneCondition.insert("High Blood Pressure", "");
            tblNocturneCondition.insert("Parkinsons", "");
            db.commit();
        } catch (Exception e) {
            logger.error(e.getClass().getName() + ": dbCreateDummyData() ", e);
            System.exit(0);
        }
        logger.info("Records created successfully");
    }

}

