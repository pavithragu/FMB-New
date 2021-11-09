package com.android.findmybus;

public class HelpModel {

    private String name, number;

    public HelpModel() {
    }

    public HelpModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }
}
