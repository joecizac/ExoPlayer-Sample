package com.example.joe.exo;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

public class Util {

    private String userAgent;

    private String getUserAgent(Context context) {
        if (userAgent == null)
            userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(context, "ExoPlayerDemo");
        return userAgent;
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(Context context, TransferListener<? super DataSource> listener) {
        return new DefaultHttpDataSourceFactory(getUserAgent(context), listener);
    }

    DataSource.Factory buildDataSourceFactory(Context context, TransferListener<? super DataSource> listener) {
        return new DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(context, listener));
    }

}
