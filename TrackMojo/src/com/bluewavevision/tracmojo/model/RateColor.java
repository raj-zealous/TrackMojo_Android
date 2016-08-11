package com.bluewavevision.tracmojo.model;

import java.io.Serializable;

/**
 * Created by Admin on 3/30/2015.
 */
public class RateColor implements Serializable{
    private String rate;
    private String rgb;
    private String hex;

    public RateColor() {
    }

    public RateColor(String rate, String rgb, String hex) {
        this.rate = rate;
        this.rgb = rgb;
        this.hex = hex;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRgb() {
        return rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    public String getHex() {
        return hex;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }
}
