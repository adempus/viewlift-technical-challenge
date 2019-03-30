package com.example.viewlift_tech_challenge;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

/**
 * Activity for streaming videos selected from MainActivity.
 * Utilizes ExoPlayer for playing videos from URL.
 **/
public class VideoPlaybackActivity extends AppCompatActivity {
    private PlayerView playbackView;
    private ExoPlayer videoPlayer;
    private Uri streamUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_video_playback);
        String videoUrl = getIntent().getExtras().getString("video_url");
        streamUri = Uri.parse(videoUrl);
        playbackView = findViewById(R.id.playback_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        videoPlayer = ExoPlayerFactory.newSimpleInstance(
                this, new DefaultTrackSelector());
        playbackView.setPlayer(videoPlayer);
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "video playback")
        );
        ExtractorMediaSource mediaSource =
                new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(streamUri);
        videoPlayer.prepare(mediaSource);
        videoPlayer.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        playbackView.setPlayer(null);
        videoPlayer.release();
        videoPlayer = null;
    }
}
