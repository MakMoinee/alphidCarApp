package com.thesis.alphidcar.repository;

import android.content.Context;

import com.github.MakMoinee.library.services.MSqliteDBHelper;

public class LocalSQL extends MSqliteDBHelper {
    private static final String dbName = "alphids";

    public LocalSQL(Context context) {
        super(context, dbName);
    }
}
