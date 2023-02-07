package com.asyncexcel.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class URLUtil {

    public static InputStream getStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static InputStream getStream(String url){
        try {
            URL fileUrl = new URL(url);
            return URLUtil.getStream(fileUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
