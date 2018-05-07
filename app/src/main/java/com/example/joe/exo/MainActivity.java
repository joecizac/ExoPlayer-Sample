package com.example.joe.exo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private com.example.joe.exo.Util util;
    private int resumeWindow;
    private long resumePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.player_view);
        util = new com.example.joe.exo.Util();
    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        clearResumePosition();
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void initializePlayer() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        DataSource.Factory dataSourceFactory = util.buildDataSourceFactory(this, bandwidthMeter);

        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
//        trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);

        int extensionRendererMode = 0;
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
                null, extensionRendererMode);

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
        playerView.setPlayer(player);
        player.setPlayWhenReady(true);

//        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse("http://vod.leasewebcdn.com/bbb.flv?ri=1024&rs=150&start=0"),
        MediaSource mediaSource = new DashMediaSource.Factory(
                new DefaultDashChunkSource.Factory(dataSourceFactory), dataSourceFactory)
                .createMediaSource(
//                        Uri.parse("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?" +
//                                "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,source,id,as&" +
//                                "ip=0.0.0.0&ipbits=0&expire=19000000000&" +
//                                "signature=51AF5F39AB0CEC3E5497CD9C900EBFEAECCCB5C7.8506521BFC350652163895D4C26DEE124209AA9E&key=ik0"));
                        Uri.parse("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"));

        player.prepare(mediaSource);

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumePosition);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            updateResumePosition();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = Math.max(0, player.getContentPosition());
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }

}
