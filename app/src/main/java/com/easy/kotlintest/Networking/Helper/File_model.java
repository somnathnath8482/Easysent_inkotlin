package com.easy.kotlintest.Networking.Helper;

import android.widget.ProgressBar;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class File_model {
    String url;
    String authHeader;
    HashMap<String, Object> map;
    List<File> f;
    String file_key;
    ProgressBar progressBar;

    public File_model(String url, String authHeader, HashMap<String, Object> map, List<File> f, String file_key, ProgressBar progressBar) {
        this.url = url;
        this.authHeader = authHeader;
        this.map = map;
        this.f = f;
        this.file_key = file_key;
        this.progressBar = progressBar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Object> map) {
        this.map = map;
    }

    public List<File> getF() {
        return f;
    }

    public void setF(List<File> f) {
        this.f = f;
    }

    public String getFile_key() {
        return file_key;
    }

    public void setFile_key(String file_key) {
        this.file_key = file_key;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }
}
