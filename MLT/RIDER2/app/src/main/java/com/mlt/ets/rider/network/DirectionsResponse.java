package com.mlt.ets.rider.network;

import org.json.JSONArray;

public class DirectionsResponse {
    private JSONArray routes;

    public JSONArray getRoutes() {
        return routes;
    }

    public void setRoutes(JSONArray routes) {
        this.routes = routes;
    }
}
