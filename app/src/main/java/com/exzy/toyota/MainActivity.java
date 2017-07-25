package com.exzy.toyota;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private ImageView image1, image2, image3;
    private ProgressBar progressBar_video1, progressBar_video2, progressBar_video3;
    private ObjectAnimator progressAnimator1;
    private boolean video1_isPlay, video2_isPlay, video3_isPlay;
    private long startTime;
    private boolean firstIn;
    private int tempPuase;
    private String currentVideo;
    private TextView topic, sup_topic, text_filter_video1, text_filter_video2, text_filter_video3;
    private TextView text_playing_video1, text_playing_video2, text_playing_video3;
    private ImageView border1, border2, border3;
    private ImageView play_icon1, play_icon2, play_icon3;
    private ImageView filter_video1, filter_video2, filter_video3;
    private Bitmap bImage1, bImage2, bImage3, oImage1, oImage2, oImage3;
    private ImageView image1_filter, image2_filter, image3_filter;
    private final int TIME_VIDEO1 = 161;
    private final int TIME_VIDEO2 = 77;
    private final int TIME_VIDEO3 = 23;
    private FrameLayout container1, container2, container3;
    private VideoView video_bg;
    private ImageView light_img;
    private boolean emptyStatus;
//    private int countDot;

    private final int TCP_PORT = 13000;
    private final String IP_SERVER = "192.168.1.185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FullScreencall();
        setContentView(R.layout.activity_main);
        initComponents();
    }

    public void initComponents() {
//        countDot = 0;
        emptyStatus = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        video_bg = (VideoView) findViewById(R.id.video_bg);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg);
        video_bg.setVideoURI(uri);
        video_bg.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        video_bg.start();

        light_img = (ImageView) findViewById(R.id.light_img);

        container1 = (FrameLayout) findViewById(R.id.container1);
        container2 = (FrameLayout) findViewById(R.id.container2);
        container3 = (FrameLayout) findViewById(R.id.container3);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);

        border1 = (ImageView) findViewById(R.id.border_video1);
        border2 = (ImageView) findViewById(R.id.border_video2);
        border3 = (ImageView) findViewById(R.id.border_video3);

        play_icon1 = (ImageView) findViewById(R.id.play_video1);
        play_icon2 = (ImageView) findViewById(R.id.play_video2);
        play_icon3 = (ImageView) findViewById(R.id.play_video3);

        filter_video1 = (ImageView) findViewById(R.id.filter_video1);
        filter_video2 = (ImageView) findViewById(R.id.filter_video2);
        filter_video3 = (ImageView) findViewById(R.id.filter_video3);
        QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
        QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
        QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);

        progressBar_video1 = (ProgressBar) findViewById(R.id.progressBar_video1);
        progressBar_video2 = (ProgressBar) findViewById(R.id.progressBar_video2);
        progressBar_video3 = (ProgressBar) findViewById(R.id.progressBar_video3);

        text_playing_video1 = (TextView) findViewById(R.id.text_playing_video1);
        text_playing_video2 = (TextView) findViewById(R.id.text_playing_video2);
        text_playing_video3 = (TextView) findViewById(R.id.text_playing_video3);

        topic = (TextView) findViewById(R.id.topic);
        sup_topic = (TextView) findViewById(R.id.sup_topic);
        text_filter_video1 = (TextView) findViewById(R.id.text_filter_video1);
        text_filter_video2 = (TextView) findViewById(R.id.text_filter_video2);
        text_filter_video3 = (TextView) findViewById(R.id.text_filter_video3);

        bImage1 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_1_black);
        bImage2 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_2_black);
        bImage3 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_3_black);

        oImage1 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic1);
        oImage2 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic2);
        oImage3 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic3);

        image1_filter = (ImageView) findViewById(R.id.image1_filter);
        image2_filter = (ImageView) findViewById(R.id.image2_filter);
        image3_filter = (ImageView) findViewById(R.id.image3_filter);

        Typeface heaventMediumOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_medium_original.TTF");
        Typeface heaventThinOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_thin_original.TTF");
        Typeface heaventLightOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_light_original.TTF");
        Typeface heaventBoldOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_bold_original.TTF");

        topic.setTypeface(heaventBoldOriginal);
        sup_topic.setTypeface(heaventLightOriginal);
        text_filter_video1.setTypeface(heaventBoldOriginal);
        text_filter_video2.setTypeface(heaventBoldOriginal);
        text_filter_video3.setTypeface(heaventBoldOriginal);
        text_playing_video1.setTypeface(heaventMediumOriginal);
        text_playing_video2.setTypeface(heaventMediumOriginal);
        text_playing_video3.setTypeface(heaventMediumOriginal);

        firstIn = true;

        currentVideo = "";

        final SimpleTCPClient client = new SimpleTCPClient();

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send("1", IP_SERVER, TCP_PORT);
                if (emptyStatus) {
                    emptyStatus = false;
                    QuickAnim.fadeIn(light_img, null);
                }
                QuickAnim.scale(container1, 1.1f, null);
                QuickAnim.scale(container2, 1.0f, null);
                QuickAnim.scale(container3, 1.0f, null);
                image2.setImageBitmap(bImage2);
                image3.setImageBitmap(bImage3);
                image2.setAlpha(0.5f);
                image3.setAlpha(0.5f);
                play_icon2.setAlpha(0.5f);
                play_icon2.setVisibility(View.VISIBLE);
                play_icon3.setAlpha(0.5f);
                play_icon3.setVisibility(View.VISIBLE);
                filter_video2.setAlpha(0.5f);
                filter_video2.setVisibility(View.VISIBLE);
                filter_video3.setAlpha(0.5f);
                filter_video3.setVisibility(View.VISIBLE);
                text_filter_video2.setAlpha(0.5f);
                text_filter_video2.setVisibility(View.VISIBLE);
                text_filter_video3.setAlpha(0.5f);
                text_filter_video3.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("1")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "1";
                if (video1_isPlay) {
                    video1_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video1.getProgress();
                    QuickAnim.fadeIn(play_icon1, null);

                    QuickAnim.fadeIn(image1_filter, null);

                    text_playing_video1.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {

                    //about other components
                    QuickAnim.fadeOut(border2, false, null);
                    QuickAnim.fadeOut(border3, false, null);
                    QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);

                    Log.e("Width", image1.getWidth() + "");
                    Log.e("Height", image1.getHeight() + "");
                    progressBar_video1.setVisibility(View.VISIBLE);
                    QuickAnim.fadeIn(border1, null);
                    QuickAnim.fadeOut(play_icon1, false, null);

                    QuickAnim.fadeOut(image2_filter, false, null);
                    QuickAnim.fadeOut(image3_filter, false, null);
                    text_playing_video1.setText("PLAYING . . .");
                    image1.setAlpha(1.0f);
                    image1.setImageBitmap(oImage1);
                    QuickAnim.fadeOut(progressBar_video2, false, null);
                    QuickAnim.fadeOut(text_playing_video2, false, null);
                    QuickAnim.fadeOut(progressBar_video3, false, null);
                    QuickAnim.fadeOut(text_playing_video3, false, null);
                    tempPuase = 0;
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = true;
                    video2_isPlay = false;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video1.setMax(TIME_VIDEO1);
                    seek1_update();

                    QuickAnim.fadeOut(text_filter_video1, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video1, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0.7f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video1, null);
                        }
                    });
                    firstIn = false;
                } else if (!video1_isPlay && !firstIn) {
                    QuickAnim.fadeOut(play_icon1, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video1, 300, false, null);
                    if (progressBar_video1.getProgress() == 0) {
                        QuickAnim.collapseViewWidth(filter_video1, 1000, false, false, null);
                        QuickAnim.fadeIn(border1, null);
                        QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0.7f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video1, null);
                            }
                        });
                    }

                    video1_isPlay = true;

                    text_playing_video1.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    seek1_update();
                    QuickAnim.fadeOut(image1_filter, false, null);
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send("2", IP_SERVER, TCP_PORT);
                if (emptyStatus) {
                    emptyStatus = false;
                    QuickAnim.fadeIn(light_img, null);
                }
                QuickAnim.scale(container2, 1.1f, null);
                QuickAnim.scale(container1, 1.0f, null);
                QuickAnim.scale(container3, 1.0f, null);
                image1.setImageBitmap(bImage1);
                image3.setImageBitmap(bImage3);
                image1.setAlpha(0.5f);
                image3.setAlpha(0.5f);
                play_icon1.setAlpha(0.5f);
                play_icon1.setVisibility(View.VISIBLE);
                play_icon3.setAlpha(0.5f);
                play_icon3.setVisibility(View.VISIBLE);
                filter_video1.setAlpha(0.5f);
                filter_video1.setVisibility(View.VISIBLE);
                filter_video3.setAlpha(0.5f);
                filter_video3.setVisibility(View.VISIBLE);
                text_filter_video1.setAlpha(0.5f);
                text_filter_video1.setVisibility(View.VISIBLE);
                text_filter_video3.setAlpha(0.5f);
                text_filter_video3.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("2")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "2";
                if (video2_isPlay) {
                    video2_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video2.getProgress();
                    QuickAnim.fadeIn(play_icon2, null);

                    QuickAnim.fadeIn(image2_filter, null);

                    text_playing_video2.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {

                    //about other components
                    QuickAnim.fadeOut(border1, false, null);
                    QuickAnim.fadeOut(border3, false, null);
                    QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);

                    Log.e("Width", image2.getWidth() + "");
                    Log.e("Height", image2.getHeight() + "");
                    progressBar_video2.setVisibility(View.VISIBLE);
                    QuickAnim.fadeIn(border2, null);
                    QuickAnim.fadeOut(play_icon2, false, null);

                    QuickAnim.fadeOut(image1_filter, false, null);
                    QuickAnim.fadeOut(image3_filter, false, null);
                    text_playing_video2.setText("PLAYING . . .");
                    image2.setAlpha(1.0f);
                    image2.setImageBitmap(oImage2);
                    QuickAnim.fadeOut(progressBar_video1, false, null);
                    QuickAnim.fadeOut(text_playing_video1, false, null);
                    QuickAnim.fadeOut(progressBar_video3, false, null);
                    QuickAnim.fadeOut(text_playing_video3, false, null);

//                    client.send("2", IP_SERVER, TCP_PORT);
                    tempPuase = 0;
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = false;
                    video2_isPlay = true;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video2.setMax(TIME_VIDEO2);
                    seek2_update();
                    QuickAnim.fadeOut(text_filter_video2, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video2, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0.7f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video2, null);
                        }
                    });
                    firstIn = false;
                } else if (!video2_isPlay && !firstIn) {
                    QuickAnim.fadeOut(play_icon2, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video2, 300, false, null);
                    if (progressBar_video2.getProgress() == 0) {
                        QuickAnim.collapseViewWidth(filter_video2, 1000, false, false, null);
                        QuickAnim.fadeIn(border2, null);
                        QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0.7f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video2, null);
                            }
                        });
                    }

                    video2_isPlay = true;
                    text_playing_video2.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    seek2_update();
                    QuickAnim.fadeOut(image2_filter, false, null);
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.send("3", IP_SERVER, TCP_PORT);
                if (emptyStatus) {
                    emptyStatus = false;
                    QuickAnim.fadeIn(light_img, null);
                }
                QuickAnim.scale(container3, 1.1f, null);
                QuickAnim.scale(container1, 1.0f, null);
                QuickAnim.scale(container2, 1.0f, null);
                image1.setImageBitmap(bImage1);
                image2.setImageBitmap(bImage2);
                image1.setAlpha(0.5f);
                image2.setAlpha(0.5f);
                play_icon1.setAlpha(0.5f);
                play_icon1.setVisibility(View.VISIBLE);
                play_icon2.setAlpha(0.5f);
                play_icon2.setVisibility(View.VISIBLE);
                filter_video1.setAlpha(0.5f);
                filter_video1.setVisibility(View.VISIBLE);
                filter_video2.setAlpha(0.5f);
                filter_video2.setVisibility(View.VISIBLE);
                text_filter_video1.setAlpha(0.5f);
                text_filter_video1.setVisibility(View.VISIBLE);
                text_filter_video2.setAlpha(0.5f);
                text_filter_video2.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("3")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "3";
                if (video3_isPlay) {
                    video3_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video3.getProgress();
                    QuickAnim.fadeIn(play_icon3, null);

                    QuickAnim.fadeIn(image3_filter, null);

                    text_playing_video3.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {

                    //about other components
                    QuickAnim.fadeOut(border1, false, null);
                    QuickAnim.fadeOut(border2, false, null);
                    QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);

                    Log.e("Width", image3.getWidth() + "");
                    Log.e("Height", image3.getHeight() + "");
                    progressBar_video3.setVisibility(View.VISIBLE);
                    QuickAnim.fadeIn(border3, null);
                    QuickAnim.fadeOut(play_icon3, false, null);

                    QuickAnim.fadeOut(image1_filter, false, null);
                    QuickAnim.fadeOut(image2_filter, false, null);
                    text_playing_video3.setText("PLAYING . . .");
                    image3.setAlpha(1.0f);
                    image3.setImageBitmap(oImage3);
                    QuickAnim.fadeOut(progressBar_video1, false, null);
                    QuickAnim.fadeOut(text_playing_video1, false, null);
                    QuickAnim.fadeOut(progressBar_video2, false, null);
                    QuickAnim.fadeOut(text_playing_video2, false, null);

//                    client.send("3", IP_SERVER, TCP_PORT);
                    tempPuase = 0;
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = false;
                    video2_isPlay = false;
                    video3_isPlay = true;
                    startTime = System.currentTimeMillis();
                    progressBar_video3.setMax(TIME_VIDEO3);
                    seek3_update();
                    QuickAnim.fadeOut(text_filter_video3, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video3, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0.7f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video3, null);
                        }
                    });
                    firstIn = false;
                } else if (!video3_isPlay && !firstIn) {
                    QuickAnim.fadeOut(play_icon3, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video3, 300, false, null);
                    if (progressBar_video3.getProgress() == 0) {
                        QuickAnim.collapseViewWidth(filter_video3, 1000, false, false, null);
                        QuickAnim.fadeIn(border3, null);
                        QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0.7f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video3, null);
                            }
                        });
                    }

                    video3_isPlay = true;
                    startTime = System.currentTimeMillis();
                    seek3_update();
                    text_playing_video3.setText("PLAYING . . .");
                    QuickAnim.fadeOut(image3_filter, false, null);
                }
            }
        });
    }

    public void seek1_update() {
        if (video1_isPlay) {
            progressBar_video1.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("Current time Video 1", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video1.getMax()) {
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container1, 1.0f, null);
                video1_isPlay = false;
                progressBar_video1.setProgress(0);
                tempPuase = 0;
                image2.setImageBitmap(oImage2);
                image3.setImageBitmap(oImage3);
                QuickAnim.fadeIn(play_icon1, null);
                QuickAnim.fadeIn(text_filter_video1, null);
                filter_video1.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video1, false, null);
                image2.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video2.setAlpha(1.0f);
                text_filter_video3.setAlpha(1.0f);
                QuickAnim.fadeOut(border1, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    seek1_update();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

    public void seek2_update() {
        if (video2_isPlay) {
            progressBar_video2.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("Current time Video 2", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video2.getMax()) {
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container2, 1.0f, null);
                video2_isPlay = false;
                progressBar_video2.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image3.setImageBitmap(oImage3);
                QuickAnim.fadeIn(play_icon2, null);
                QuickAnim.fadeIn(text_filter_video2, null);
                filter_video2.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video2, false, null);
                image1.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video1.setAlpha(1.0f);
                text_filter_video3.setAlpha(1.0f);
                QuickAnim.fadeOut(border2, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    seek2_update();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

    public void seek3_update() {
        if (video3_isPlay) {
            progressBar_video3.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("Current time Video 3", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video3.getMax()) {
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container3, 1.0f, null);
                video3_isPlay = false;
                progressBar_video3.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image2.setImageBitmap(oImage2);
                QuickAnim.fadeIn(play_icon3, null);
                QuickAnim.fadeIn(text_filter_video3, null);
                filter_video3.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video3, false, null);
                image1.setAlpha(1.0f);
                image2.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                text_filter_video1.setAlpha(1.0f);
                text_filter_video2.setAlpha(1.0f);
                QuickAnim.fadeOut(border3, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    seek3_update();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }
    /**
     * Still need fix bug
     */
//    public void updateText() {
//        if (video1_isPlay) {
//            String text = "";
//            if (countDot > 3) {
//                text = "PLAYING";
//                countDot = 0;
//            } else {
//                text = text_playing_video1.getText().toString() + " .";
//            }
//            text_playing_video1.setText(text);
//            countDot++;
//        }
//
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                updateText();
//            }
//        };
//        new Handler().postDelayed(r, 600);
//    }

    /**
     * Set full screen.
     */
    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
