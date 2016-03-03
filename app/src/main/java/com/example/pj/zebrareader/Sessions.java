package com.example.pj.zebrareader;

/**
 * Created by PJ on 21/01/2016.
 */
public class Sessions {

    static String ipAddQr;
    static String ipAddId;
    static String filename;

    public static String getIpAddId() {
        return Sessions.ipAddId;
    }

    public static void setIpAddId(String ipAddId) {
        Sessions.ipAddId = ipAddId;
    }

    public static String getIpAddQr() {
        return Sessions.ipAddQr;
    }

    public static void setIpAddQr(String ipAddQr) {
        Sessions.ipAddQr = ipAddQr;
    }

    public static void setFilename(String filename)
    {
        Sessions.filename = filename;
    }

    public static String getFilename()
    {
        return Sessions.filename;
    }



}

