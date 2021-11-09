package com.android.findmybus;

public class BusInfoModel {
    private String time, stage;

    public BusInfoModel() {
    }

    public BusInfoModel(String time, String stage) {
        this.time = time;
        this.stage = stage;
    }

    public String getTime() {
        return time;
    }

    public String getStage() {
        return stage;
    }
}
