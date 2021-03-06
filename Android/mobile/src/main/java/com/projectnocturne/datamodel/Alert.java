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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//if ignoreUnknown is false, Jackson would throw an exception if we don't parse all fields
@JsonIgnoreProperties(ignoreUnknown = true)
public final class Alert {

    @JsonProperty("alert_desc")
    public String alert_desc;

    @JsonProperty("alert_name")
    public String alert_name;

    @JsonProperty("response")
    public String response;

    @JsonProperty("response_sent")
    public boolean response_sent = false;

    @JsonProperty("user_Id")
    public long user_Id;

    public Alert() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the alert_desc
     */
    public String getAlert_desc() {
        return alert_desc;
    }

    /**
     * @param alert_desc the alert_desc to set
     */
    public void setAlert_desc(final String alert_desc) {
        this.alert_desc = alert_desc;
    }

    /**
     * @return the alert_name
     */
    public String getAlert_name() {
        return alert_name;
    }

    /**
     * @param alert_name the alert_name to set
     */
    public void setAlert_name(final String alert_name) {
        this.alert_name = alert_name;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(final String response) {
        this.response = response;
    }

    /**
     * @return the user_Id
     */
    public long getUser_Id() {
        return user_Id;
    }

    /**
     * @param user_Id the user_Id to set
     */
    public void setUser_Id(final long user_Id) {
        this.user_Id = user_Id;
    }

    /**
     * @return the response_sent
     */
    public boolean isResponse_sent() {
        return response_sent;
    }

    /**
     * @param response_sent the response_sent to set
     */
    public void setResponse_sent(final boolean response_sent) {
        this.response_sent = response_sent;
    }

    @Override
    public String toString() {
        return null;
    }
}
