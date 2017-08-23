package com.thunderbird.chennai.fapapp.Model;

import org.json.JSONObject;

/**
 * Created by piyush87530 on 23-05-2017.
 */

public class Equipment {
    String ep_id;
    String ep_name;
    String sp_id;
    String ue_id;

    public String getUe_id() {
        return ue_id;
    }

    public void setUe_id(String ue_id) {
        this.ue_id = ue_id;
    }

    public String getEp_id() {
        return ep_id;
    }

    public void setEp_id(String ep_id) {
        this.ep_id = ep_id;
    }

    public String getEp_name() {
        return ep_name;
    }

    public void setEp_name(String ep_name) {
        this.ep_name = ep_name;
    }

    public String getSp_id() {
        return sp_id;
    }

    public void setSp_id(String sp_id) {
        this.sp_id = sp_id;
    }
}
