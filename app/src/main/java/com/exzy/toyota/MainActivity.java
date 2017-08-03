package com.exzy.toyota;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
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
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
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
    private boolean video1_isPlay, video2_isPlay, video3_isPlay;
    private long startTime;
    private boolean firstIn;
    private int tempPuase;
    private String currentVideo;
    private TextView topic, sup_topic, text_filter_video1_1, text_filter_video1_2, text_filter_video2_1, text_filter_video2_2, text_filter_video3_1,text_filter_video3_2;
    private TextView text_playing_video1, text_playing_video2, text_playing_video3;
    private ImageView border1, border2, border3;
    private ImageView play_icon1, play_icon2, play_icon3;
    private ImageView circle_icon1, circle_icon2, circle_icon3;
    private ImageView filter_video1, filter_video2, filter_video3;
    private Bitmap bImage1, bImage2, bImage3, oImage1, oImage2, oImage3, pauseImg, playImg;
    private ImageView image1_filter, image2_filter, image3_filter;
    private final int TIME_VIDEO1 = 233;
    private final int TIME_VIDEO2 = 99;
    private final int TIME_VIDEO3 = 131;
    private FrameLayout container1, container2, container3;
    private VideoView video_bg;
    private ImageView light_img;
    private boolean emptyStatus;
    private ImageView progress1, progress2, progress3;
    private final int MAX_DISTANCE_PROGRESS = 1140;
    //    private int presentDistance1,presentDistance2,presentDistance3;
    private int timeUsed1, timeUsed2, timeUsed3;
//    private int timer1, timer2, timer3;
    private ImageView black_progress1, black_progress2, black_progress3;
//    private int countPause;

    private final int TCP_PORT = 13000;
    private final String IP_SERVER = "192.168.0.100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        FullScreencall();
        setContentView(R.layout.activity_main);
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
        wakeLock.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "no sleep");
        wakeLock.release();
    }

    public void initComponents() {
//        countPause = 0;

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

        progress1 = (ImageView) findViewById(R.id.blue_line_progress1);
        Log.e("distance progress", progress1.getScrollX() + "");
        Log.e("distance progress", progress1.getX() + "");
        Log.e("distance progress", progress1.getWidth() + "");
        Log.e("distance progress", progress1.getPaddingRight() + "");
        progress2 = (ImageView) findViewById(R.id.blue_line_progress2);
        progress3 = (ImageView) findViewById(R.id.blue_line_progress3);
        QuickAnim.collapseViewWidth(progress1, 1, false, false, null);
        QuickAnim.collapseViewWidth(progress2, 1, false, false, null);
        QuickAnim.collapseViewWidth(progress3, 1, false, false, null);

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

        black_progress1 = (ImageView) findViewById(R.id.black_line_progress1);
        black_progress2 = (ImageView) findViewById(R.id.black_line_progress2);
        black_progress3 = (ImageView) findViewById(R.id.black_line_progress3);

        currentVideo = "";

        circle_icon1 = (ImageView) findViewById(R.id.circle_icon1);
//        animationButton1();
        circle_icon2 = (ImageView) findViewById(R.id.circle_icon2);
//        animationButton2();
        circle_icon3 = (ImageView) findViewById(R.id.circle_icon3);
//        animationButton3();

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
        text_filter_video1_1 = (TextView) findViewById(R.id.text_filter_video1_1);
        text_filter_video1_2 = (TextView) findViewById(R.id.text_filter_video1_2);
        text_filter_video2_1 = (TextView) findViewById(R.id.text_filter_video2_1);
        text_filter_video2_2 = (TextView) findViewById(R.id.text_filter_video2_2);
        text_filter_video3_1 = (TextView) findViewById(R.id.text_filter_video3_1);
        text_filter_video3_2 = (TextView) findViewById(R.id.text_filter_video3_2);

        bImage1 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_1_black);
        bImage2 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_2_black);
        bImage3 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic_3_black);

        oImage1 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic1);
        oImage2 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic2);
        oImage3 = BitmapFactory.decodeResource(getResources(), R.mipmap.pic3);

        pauseImg = BitmapFactory.decodeResource(getResources(), R.mipmap.pause_icon);
        playImg = BitmapFactory.decodeResource(getResources(), R.mipmap.play_icon);

        image1_filter = (ImageView) findViewById(R.id.image1_filter);
        image2_filter = (ImageView) findViewById(R.id.image2_filter);
        image3_filter = (ImageView) findViewById(R.id.image3_filter);

        Typeface heaventMediumOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_medium_original.TTF");
        Typeface heaventThinOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_thin_original.TTF");
        Typeface heaventLightOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_light_original.TTF");
        Typeface heaventBoldOriginal = Typeface.createFromAsset(getAssets(), "fonts/heavent_bold_original.TTF");

        topic.setTypeface(heaventBoldOriginal);
        sup_topic.setTypeface(heaventLightOriginal);
        text_filter_video1_1.setTypeface(heaventBoldOriginal);
        text_filter_video2_1.setTypeface(heaventBoldOriginal);
        text_filter_video3_1.setTypeface(heaventBoldOriginal);
        text_filter_video1_2.setTypeface(heaventMediumOriginal);
        text_filter_video2_2.setTypeface(heaventMediumOriginal);
        text_filter_video3_2.setTypeface(heaventMediumOriginal);
        text_playing_video1.setTypeface(heaventMediumOriginal);
        text_playing_video2.setTypeface(heaventMediumOriginal);
        text_playing_video3.setTypeface(heaventMediumOriginal);

        firstIn = true;

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
//                pause_icon2.setAlpha(0.0f);
//                pause_icon3.setAlpha(0.0f);

                filter_video2.setAlpha(0.5f);
                filter_video2.setVisibility(View.VISIBLE);
                filter_video3.setAlpha(0.5f);
                filter_video3.setVisibility(View.VISIBLE);
                text_filter_video2_1.setAlpha(0.5f);
                text_filter_video2_1.setVisibility(View.VISIBLE);
                text_filter_video3_1.setAlpha(0.5f);
                text_filter_video3_1.setVisibility(View.VISIBLE);
                text_filter_video2_2.setAlpha(0.5f);
                text_filter_video2_2.setVisibility(View.VISIBLE);
                text_filter_video3_2.setAlpha(0.5f);
                text_filter_video3_2.setVisibility(View.VISIBLE);
                if (currentVideo.equals("2")) {
                    play_icon2.setImageBitmap(playImg);
                }
                if (currentVideo.equals("3")) {
                    play_icon3.setImageBitmap(playImg);
                }
                play_icon2.setAlpha(0.5f);
                play_icon2.setVisibility(View.VISIBLE);
                play_icon3.setAlpha(0.5f);
                play_icon3.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("1")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "1";
                circle_icon2.setAlpha(0.0f);
                circle_icon3.setAlpha(0.0f);
                if (firstIn) {
                    animationButton1();
                }
                if (video1_isPlay) {
                    firstIn = false;
                    tempPuase = progressBar_video1.getProgress();
                    // test
//                    presentDistance1 = MAX_DISTANCE_PROGRESS-progress1.getWidth();
                    video1_isPlay = false;
//                    timeUsed1 = TIME_VIDEO1 - timer1 + 1;
                    pauseProgress1();

                    play_icon1.setImageBitmap(playImg);
                    play_icon1.setAlpha(1.0f);
//                    QuickAnim.changeImageWithFade(play_icon1, playImg, 300 ,null);

                    QuickAnim.fadeIn(image1_filter, null);

                    text_playing_video1.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
//                    timer1 = 0;
//                    timer2 = 0;
//                    timer3 = 0;
//                    countPause = 0;
                    Log.e("time used1",timeUsed1+"");

                    //about other components
                    QuickAnim.fadeOut(border2, false, null);
                    QuickAnim.fadeOut(border3, false, null);
                    QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);

                    Log.e("Width", image1.getWidth() + "");
                    Log.e("Height", image1.getHeight() + "");
                    progressBar_video1.setVisibility(View.VISIBLE);
                    black_progress1.setVisibility(View.VISIBLE);
                    progress1.setVisibility(View.VISIBLE);
                    progress1.setAlpha(1.0f);
                    QuickAnim.fadeIn(border1, null);
                    play_icon1.setImageBitmap(pauseImg);
                    play_icon1.setAlpha(0.75f);

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
//                    progress1.setPadding(0, 0, -842, 0);
                    progressBar_video2.setProgress(0);
//                    progress2.setPadding(0, 0, -842, 0);
                    progressBar_video3.setProgress(0);
//                    progress3.setPadding(0, 0, -842, 0);
                    QuickAnim.fadeOut(progress2, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress2, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(progress3, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress3, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(black_progress3, false, null);
                    QuickAnim.fadeOut(black_progress2, false, null);

                    video1_isPlay = true;
                    video2_isPlay = false;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video1.setMax(TIME_VIDEO1);

                    QuickAnim.fadeOut(text_filter_video1_1, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video1_2, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video1, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(black_progress1, 600, 0, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video1, null);
                        }
                    });
                    // test
                    timeUsed1 = TIME_VIDEO1 - tempPuase;
                    startTimer1();

                    seek1_update();
                    firstIn = false;
                } else if (!video1_isPlay && !firstIn) {
                    play_icon1.setImageBitmap(pauseImg);
                    play_icon1.setAlpha(0.75f);
                    QuickAnim.fadeOut(text_filter_video1_1, 300, false, null);
                    QuickAnim.fadeOut(text_filter_video1_2, 300, false, null);
                    if (progress1.getWidth() == 0) {
                        QuickAnim.collapseViewWidth(filter_video1, 1000, false, false, null);
                        QuickAnim.fadeIn(border1, null);
                        QuickAnim.slideYAxisTo(black_progress1, 600, 0, 1.0f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video1, null);
                            }
                        });
                    }

                    video1_isPlay = true;

                    // test
                    timeUsed1 = TIME_VIDEO1 - tempPuase;
//                    timeUsed1 = TIME_VIDEO1 - (int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase;
                    Log.e("time video1", TIME_VIDEO1 + "");
//                    Log.e("timer1", timer1 + "");

                    text_playing_video1.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    startTimer1();
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

                filter_video1.setAlpha(0.5f);
                filter_video1.setVisibility(View.VISIBLE);
                filter_video3.setAlpha(0.5f);
                filter_video3.setVisibility(View.VISIBLE);
                text_filter_video1_1.setAlpha(0.5f);
                text_filter_video1_1.setVisibility(View.VISIBLE);
                text_filter_video3_1.setAlpha(0.5f);
                text_filter_video3_1.setVisibility(View.VISIBLE);
                text_filter_video1_2.setAlpha(0.5f);
                text_filter_video1_2.setVisibility(View.VISIBLE);
                text_filter_video3_2.setAlpha(0.5f);
                text_filter_video3_2.setVisibility(View.VISIBLE);
                if (currentVideo.equals("1")) {
                    play_icon1.setImageBitmap(playImg);
                }
                if (currentVideo.equals("3")) {
                    play_icon3.setImageBitmap(playImg);
                }
                play_icon1.setAlpha(0.5f);
                play_icon1.setVisibility(View.VISIBLE);
                play_icon3.setAlpha(0.5f);
                play_icon3.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("2")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "2";
                circle_icon1.setVisibility(View.INVISIBLE);
                circle_icon3.setVisibility(View.INVISIBLE);
                if (firstIn) {
                    animationButton2();
                }
                if (video2_isPlay) {
                    video2_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video2.getProgress();

                    // test
//                    timeUsed2 = TIME_VIDEO2 - timer2 +1;
                    pauseProgress2();

                    play_icon2.setImageBitmap(playImg);
                    play_icon2.setAlpha(1.0f);
//                    QuickAnim.changeImageWithFade(play_icon2, playImg, 300 ,null);

                    QuickAnim.fadeIn(image2_filter, null);

                    text_playing_video2.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
//                    timer1 = 0;
//                    timer2 = 0;
//                    timer3 = 0;
                    Log.e("time used2",timeUsed2+"");

                    //about other components
                    QuickAnim.fadeOut(border1, false, null);
                    QuickAnim.fadeOut(border3, false, null);
                    QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);

                    Log.e("Width", image2.getWidth() + "");
                    Log.e("Height", image2.getHeight() + "");
                    progressBar_video2.setVisibility(View.VISIBLE);
                    black_progress2.setVisibility(View.VISIBLE);
                    progress2.setVisibility(View.VISIBLE);
                    progress2.setAlpha(1.0f);
                    QuickAnim.fadeIn(border2, null);
//                    QuickAnim.fadeOut(play_icon2, false, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeIn(pause_icon2, null);
//                        }
//                    });
                    play_icon2.setImageBitmap(pauseImg);
                    play_icon2.setAlpha(0.75f);
//                    QuickAnim.changeImageWithFade(play_icon2, pauseImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon2, 300, 0.5f, null);
//                        }
//                    });

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
//                    progress1.setPadding(0, 0, -842, 0);
                    progressBar_video2.setProgress(0);
//                    progress2.setPadding(0, 0, -842, 0);
                    progressBar_video3.setProgress(0);
//                    progress3.setPadding(0, 0, -842, 0);
                    QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(progress3, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress3, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(black_progress1, false, null);
                    QuickAnim.fadeOut(black_progress3, false, null);
                    video1_isPlay = false;
                    video2_isPlay = true;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video2.setMax(TIME_VIDEO2);


                    QuickAnim.fadeOut(text_filter_video2_1, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video2_2, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video2, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(black_progress2, 600, 0, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video2, null);

                        }
                    });
                    // test
                    timeUsed2 = TIME_VIDEO2 - tempPuase;
                    startTimer2();

                    seek2_update();
                    firstIn = false;
                } else if (!video2_isPlay && !firstIn) {
//                    QuickAnim.fadeIn(pause_icon2, null);

//                    QuickAnim.fadeOut(play_icon2, 100, false, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeIn(pause_icon2, null);
//                        }
//                    });
                    play_icon2.setImageBitmap(pauseImg);
                    play_icon2.setAlpha(0.75f);
//                    QuickAnim.changeImageWithFade(play_icon2, pauseImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon2, 300, 0.5f, null);
//                        }
//                    });
                    QuickAnim.fadeOut(text_filter_video2_1, 300, false, null);
                    QuickAnim.fadeOut(text_filter_video2_2, 300, false, null);
//                    if (progressBar_video2.getProgress() == 0) {
//                        QuickAnim.collapseViewWidth(filter_video2, 1000, false, false, null);
//                        QuickAnim.fadeIn(border2, null);
//                        QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0.7f, new OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                QuickAnim.fadeIn(text_playing_video2, null);
//                            }
//                        });
//                    }
                    if (progress2.getWidth() == 0) {
                        QuickAnim.collapseViewWidth(filter_video2, 1000, false, false, null);
                        QuickAnim.fadeIn(border2, null);
                        QuickAnim.slideYAxisTo(black_progress2, 600, 0, 1.0f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video2, null);
                            }
                        });
                    }

                    video2_isPlay = true;

                    // test
                    timeUsed2 = TIME_VIDEO2 - tempPuase;

                    text_playing_video2.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    startTimer2();
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
//                pause_icon1.setAlpha(0.0f);
//                pause_icon2.setAlpha(0.0f);

                filter_video1.setAlpha(0.5f);
                filter_video1.setVisibility(View.VISIBLE);
                filter_video2.setAlpha(0.5f);
                filter_video2.setVisibility(View.VISIBLE);
                text_filter_video1_1.setAlpha(0.5f);
                text_filter_video1_1.setVisibility(View.VISIBLE);
                text_filter_video2_1.setAlpha(0.5f);
                text_filter_video2_1.setVisibility(View.VISIBLE);
                text_filter_video1_2.setAlpha(0.5f);
                text_filter_video1_2.setVisibility(View.VISIBLE);
                text_filter_video2_2.setAlpha(0.5f);
                text_filter_video2_2.setVisibility(View.VISIBLE);
                if (currentVideo.equals("1")) {
                    play_icon1.setImageBitmap(playImg);
//                    QuickAnim.changeImageWithFade(play_icon1, playImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon1, 300, 0.5f, null);
//                        }
//                    });
                }
                if (currentVideo.equals("2")) {
                    play_icon2.setImageBitmap(playImg);
//                    QuickAnim.changeImageWithFade(play_icon2, playImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon2, 300, 0.5f, null);
//                        }
//                    });
                }
                play_icon1.setAlpha(0.5f);
                play_icon1.setVisibility(View.VISIBLE);
                play_icon2.setAlpha(0.5f);
                play_icon2.setVisibility(View.VISIBLE);
                if (!currentVideo.equals("3") ) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "3";
                circle_icon1.setVisibility(View.INVISIBLE);
                circle_icon2.setVisibility(View.INVISIBLE);
                if (firstIn) {
                    animationButton3();
                }
//                QuickAnim.changeImageWithFade(play_icon1, playImg, 300 ,null);
//                QuickAnim.changeImageWithFade(play_icon2, playImg, 300 ,null);
                if (video3_isPlay) {
                    video3_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video3.getProgress();
//                    QuickAnim.fadeOut(pause_icon3, false, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeIn(play_icon3, null);
//                        }
//                    });

                    // test
//                    timeUsed3 = TIME_VIDEO3 - timer3 + 1;
                    pauseProgress3();

                    play_icon3.setImageBitmap(playImg);
                    play_icon3.setAlpha(1.0f);
//                    QuickAnim.changeImageWithFade(play_icon3, playImg, 300 ,null);

                    QuickAnim.fadeIn(image3_filter, null);

                    text_playing_video3.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
//                    timer1 = 0;
//                    timer2 = 0;
//                    timer3 = 0;
                    Log.e("time used3",timeUsed3+"");

                    //about other components
                    QuickAnim.fadeOut(border1, false, null);
                    QuickAnim.fadeOut(border2, false, null);
                    QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                    QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);

                    Log.e("Width", image3.getWidth() + "");
                    Log.e("Height", image3.getHeight() + "");
                    progressBar_video3.setVisibility(View.VISIBLE);
                    black_progress3.setVisibility(View.VISIBLE);
                    progress3.setVisibility(View.VISIBLE);
                    progress3.setAlpha(1.0f);
                    QuickAnim.fadeIn(border3, null);
//                    QuickAnim.fadeOut(play_icon3, false, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeIn(pause_icon3, null);
//                        }
//                    });
                    play_icon3.setImageBitmap(pauseImg);
                    play_icon3.setAlpha(0.75f);
//                    QuickAnim.changeImageWithFade(play_icon3, pauseImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon3, 300, 0.5f, null);
//                        }
//                    });

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
//                    progress1.setPadding(0, 0, -842, 0);
                    progressBar_video2.setProgress(0);
//                    progress2.setPadding(0, 0, -842, 0);
                    progressBar_video3.setProgress(0);
//                    progress3.setPadding(0, 0, -842, 0);
                    QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(progress2, false, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.collapseViewWidth(progress2, 500, false, false, null);
                        }
                    });
                    QuickAnim.fadeOut(black_progress1, false, null);
                    QuickAnim.fadeOut(black_progress2, false, null);
                    video1_isPlay = false;
                    video2_isPlay = false;
                    video3_isPlay = true;
                    startTime = System.currentTimeMillis();
                    progressBar_video3.setMax(TIME_VIDEO3);
//                    seek3_update();
                    QuickAnim.fadeOut(text_filter_video3_1, 100, false, null);
                    QuickAnim.fadeOut(text_filter_video3_2, 100, false, null);
                    QuickAnim.collapseViewWidth(filter_video3, 1000, false, false, null);

                    QuickAnim.slideYAxisTo(black_progress3, 600, 0, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(text_playing_video3, null);
                        }
                    });
                    // test
                    timeUsed3 = TIME_VIDEO3 - tempPuase;
                    startTimer3();

                    seek3_update();
                    firstIn = false;
                } else if (!video3_isPlay && !firstIn) {
//                    QuickAnim.fadeIn(pause_icon3, null);

//                    QuickAnim.fadeOut(play_icon3, 100, false, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeIn(pause_icon3, null);
//                        }
//                    });
                    play_icon3.setImageBitmap(pauseImg);
                    play_icon3.setAlpha(0.75f);
//                    QuickAnim.changeImageWithFade(play_icon3, pauseImg, 300, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.fadeTo(play_icon3, 300, 0.5f, null);
//                        }
//                    });
                    QuickAnim.fadeOut(text_filter_video3_1, 300, false, null);
                    QuickAnim.fadeOut(text_filter_video3_2, 300, false, null);
//                    if (progressBar_video3.getProgress() == 0) {
//                        QuickAnim.collapseViewWidth(filter_video3, 1000, false, false, null);
//                        QuickAnim.fadeIn(border3, null);
//                        QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0.7f, new OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                QuickAnim.fadeIn(text_playing_video3, null);
//                            }
//                        });
//                    }
                    if (progress3.getWidth() == 0) {
                        QuickAnim.collapseViewWidth(filter_video3, 1000, false, false, null);
                        QuickAnim.fadeIn(border3, null);
                        QuickAnim.slideYAxisTo(black_progress3, 600, 0, 1.0f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(text_playing_video3, null);
                            }
                        });
                    }

                    video3_isPlay = true;

                    // test
                    timeUsed3 = TIME_VIDEO3 - tempPuase;

                    text_playing_video3.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    startTimer3();
                    seek3_update();
                    QuickAnim.fadeOut(image3_filter, false, null);
                }
            }
        });
    }

    public void seek1_update() {
//        if (video1_isPlay) {
            QuickAnim.expandViewWidthTo2(progress1, MAX_DISTANCE_PROGRESS, timeUsed1 * 1000, false, new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
//                    if (progressBar_video1.getProgress() == TIME_VIDEO1) {
//                        currentVideo = "";
//                        QuickAnim.changeImageWithFade(play_icon1, playImg, 300, null);
//                        emptyStatus = true;
//                        QuickAnim.fadeOut(light_img, false, null);
//                        QuickAnim.scale(container1, 1.0f, null);
//                        video1_isPlay = false;
//                        progressBar_video1.setProgress(0);
//                        tempPuase = 0;
//                        image2.setImageBitmap(oImage2);
//                        image3.setImageBitmap(oImage3);
////                QuickAnim.fadeIn(play_icon1, null);
//                        QuickAnim.fadeIn(text_filter_video1, null);
//                        filter_video1.setAlpha(1.0f);
//                        QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
//                        QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, null);
//                        QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
//                            }
//                        });
//                        QuickAnim.slideYAxisTo(black_progress1, 600, 0, 0, null);
//                        QuickAnim.fadeOut(text_playing_video1, false, null);
//                        image2.setAlpha(1.0f);
//                        image3.setAlpha(1.0f);
//                        play_icon2.setAlpha(1.0f);
//                        play_icon3.setAlpha(1.0f);
//                        filter_video2.setAlpha(1.0f);
//                        filter_video3.setAlpha(1.0f);
//                        text_filter_video2.setAlpha(1.0f);
//                        text_filter_video3.setAlpha(1.0f);
//                        QuickAnim.fadeOut(border1, false, null);
//                    }
                }
            });
//            progressBar_video1.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
//            Log.e("Current time Video 1", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

//            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video1.getMax()) {
//            if (timeUsed1 == 0) {
//                currentVideo = "";
//                QuickAnim.changeImageWithFade(play_icon1, playImg, 300, null);
//                emptyStatus = true;
//                QuickAnim.fadeOut(light_img, false, null);
//                QuickAnim.scale(container1, 1.0f, null);
//                video1_isPlay = false;
//                progressBar_video1.setProgress(0);
//                tempPuase = 0;
//                image2.setImageBitmap(oImage2);
//                image3.setImageBitmap(oImage3);
////                QuickAnim.fadeIn(play_icon1, null);
//                QuickAnim.fadeIn(text_filter_video1, null);
//                filter_video1.setAlpha(1.0f);
//                QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
//                QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, null);
//                QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
//                    }
//                });
//                QuickAnim.slideYAxisTo(black_progress1, 600, 0, 0, null);
//                QuickAnim.fadeOut(text_playing_video1, false, null);
//                image2.setAlpha(1.0f);
//                image3.setAlpha(1.0f);
//                play_icon2.setAlpha(1.0f);
//                play_icon3.setAlpha(1.0f);
//                filter_video2.setAlpha(1.0f);
//                filter_video3.setAlpha(1.0f);
//                text_filter_video2.setAlpha(1.0f);
//                text_filter_video3.setAlpha(1.0f);
//                QuickAnim.fadeOut(border1, false, null);
//            }

//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    seek1_update();
//                }
//            };
//            new Handler().postDelayed(r, 10000);
//        }
    }

    public void pauseProgress1() {
        QuickAnim.expandViewWidthTo2(progress1, progress1.getWidth(), 1, false, null);
    }

    public void startTimer1() {
        if (video1_isPlay) {
//            timer1 = timer1 + 1;
//            Log.e("timer1",timer1+"");
            progressBar_video1.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("progress1",progressBar_video1.getProgress()+"");
//            timer1 = timer1 + (int) ((System.currentTimeMillis() - startTime) / 1000);

            if (progressBar_video1.getProgress() == TIME_VIDEO1) {
                currentVideo = "";
                QuickAnim.changeImageWithFade(play_icon1, playImg, 300, null);
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container1, 1.0f, null);
                video1_isPlay = false;
                progressBar_video1.setProgress(0);
                tempPuase = 0;
                image2.setImageBitmap(oImage2);
                image3.setImageBitmap(oImage3);
//                QuickAnim.fadeIn(play_icon1, null);
                QuickAnim.fadeIn(text_filter_video1_1, null);
                QuickAnim.fadeIn(text_filter_video1_2, null);
                filter_video1.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, null);
                QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
                    }
                });
                QuickAnim.slideYAxisTo(black_progress1, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video1, false, null);
                image2.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video2_1.setAlpha(1.0f);
                text_filter_video3_1.setAlpha(1.0f);
                text_filter_video2_2.setAlpha(1.0f);
                text_filter_video3_2.setAlpha(1.0f);
                QuickAnim.fadeOut(border1, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startTimer1();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

//    public void checkPause1() {
//        if(countPause < 10 && currentVideo.equals("1")) {
//            countPause++;
//            Log.e("time pause",countPause+"");
//
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    checkPause1();
//                }
//            };
//            new Handler().postDelayed(r, 1000);
//        } else {
//            currentVideo = "";
//            QuickAnim.changeImageWithFade(play_icon1, playImg, 300, null);
//            emptyStatus = true;
//            QuickAnim.fadeOut(light_img, false, null);
//            QuickAnim.scale(container1, 1.0f, null);
//            video1_isPlay = false;
//            progressBar_video1.setProgress(0);
//            tempPuase = 0;
//            image2.setImageBitmap(oImage2);
//            image3.setImageBitmap(oImage3);
////                QuickAnim.fadeIn(play_icon1, null);
//            QuickAnim.fadeIn(text_filter_video1, null);
//            filter_video1.setAlpha(1.0f);
//            QuickAnim.expandViewWidthTo(filter_video1, 450, 1000, false, null);
//            QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, null);
//            QuickAnim.fadeOut(progress1, false, new OnAnimationEndListener() {
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    QuickAnim.collapseViewWidth(progress1, 500, false, false, null);
//                }
//            });
//            QuickAnim.slideYAxisTo(black_progress1, 600, 0, 0, null);
//            QuickAnim.fadeOut(text_playing_video1, false, null);
//            image2.setAlpha(1.0f);
//            image3.setAlpha(1.0f);
//            play_icon2.setAlpha(1.0f);
//            play_icon3.setAlpha(1.0f);
//            filter_video2.setAlpha(1.0f);
//            filter_video3.setAlpha(1.0f);
//            text_filter_video2.setAlpha(1.0f);
//            text_filter_video3.setAlpha(1.0f);
//            QuickAnim.fadeOut(border1, false, null);
//            QuickAnim.fadeOut(image1_filter, false, null);
//            countPause = 0;
//        }
//    }

    public void seek2_update() {
//        if (video2_isPlay) {
            QuickAnim.expandViewWidthTo2(progress2, MAX_DISTANCE_PROGRESS, timeUsed2 * 1000, false, new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.e("distance2",progress2.getWidth()+"");
//                    if (timeUsed2 == TIME_VIDEO2) {
//                        currentVideo = "";
//                        QuickAnim.changeImageWithFade(play_icon2, playImg, 300, null);
//                        emptyStatus = true;
//                        QuickAnim.fadeOut(light_img, false, null);
//                        QuickAnim.scale(container2, 1.0f, null);
//                        video2_isPlay = false;
//                        progressBar_video2.setProgress(0);
//                        tempPuase = 0;
//                        image1.setImageBitmap(oImage1);
//                        image3.setImageBitmap(oImage3);
////                QuickAnim.fadeIn(play_icon2, null);
//                        QuickAnim.fadeIn(text_filter_video2, null);
//                        filter_video2.setAlpha(1.0f);
//                        QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
//                        QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0, null);
//                        QuickAnim.fadeOut(progress2, false, new OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                QuickAnim.collapseViewWidth(progress2, 500, false, false, null);
//                            }
//                        });
//                        QuickAnim.slideYAxisTo(black_progress2, 600, 0, 0, null);
//                        QuickAnim.fadeOut(text_playing_video2, false, null);
//                        image1.setAlpha(1.0f);
//                        image3.setAlpha(1.0f);
//                        play_icon1.setAlpha(1.0f);
//                        play_icon3.setAlpha(1.0f);
//                        filter_video1.setAlpha(1.0f);
//                        filter_video3.setAlpha(1.0f);
//                        text_filter_video1.setAlpha(1.0f);
//                        text_filter_video3.setAlpha(1.0f);
//                        QuickAnim.fadeOut(border2, false, null);
//                    }
                }
            });
//            progressBar_video2.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
//            Log.e("Current time Video 2", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

//            if (timeUsed2 == 0) {
//                currentVideo = "";
//                QuickAnim.changeImageWithFade(play_icon2, playImg, 300, null);
//                emptyStatus = true;
//                QuickAnim.fadeOut(light_img, false, null);
//                QuickAnim.scale(container2, 1.0f, null);
//                video2_isPlay = false;
//                progressBar_video2.setProgress(0);
//                tempPuase = 0;
//                image1.setImageBitmap(oImage1);
//                image3.setImageBitmap(oImage3);
////                QuickAnim.fadeIn(play_icon2, null);
//                QuickAnim.fadeIn(text_filter_video2, null);
//                filter_video2.setAlpha(1.0f);
//                QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
//                QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0, null);
//                QuickAnim.fadeOut(progress2, false, new OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        QuickAnim.collapseViewWidth(progress2, 500, false, false, null);
//                    }
//                });
//                QuickAnim.slideYAxisTo(black_progress2, 600, 0, 0, null);
//                QuickAnim.fadeOut(text_playing_video2, false, null);
//                image1.setAlpha(1.0f);
//                image3.setAlpha(1.0f);
//                play_icon1.setAlpha(1.0f);
//                play_icon3.setAlpha(1.0f);
//                filter_video1.setAlpha(1.0f);
//                filter_video3.setAlpha(1.0f);
//                text_filter_video1.setAlpha(1.0f);
//                text_filter_video3.setAlpha(1.0f);
//                QuickAnim.fadeOut(border2, false, null);
//            }

//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    seek2_update();
//                }
//            };
//            new Handler().postDelayed(r, 1000);
//        }
    }

    public void pauseProgress2() {
        QuickAnim.expandViewWidthTo2(progress2, progress2.getWidth(), 1, false, null);
    }

    public void startTimer2() {
        if (video2_isPlay) {
//            timer2 = timer2 + 1;
//            Log.e("timer2",timer2+"");
            progressBar_video2.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("progress2",progressBar_video2.getProgress()+"");

            if (progressBar_video2.getProgress() == TIME_VIDEO2) {
                currentVideo = "";
                QuickAnim.changeImageWithFade(play_icon2, playImg, 300, null);
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container2, 1.0f, null);
                video2_isPlay = false;
                progressBar_video2.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image3.setImageBitmap(oImage3);
//                QuickAnim.fadeIn(play_icon2, null);
                QuickAnim.fadeIn(text_filter_video2_1, null);
                QuickAnim.fadeIn(text_filter_video2_2, null);
                filter_video2.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video2, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video2, 600, 0, 0, null);
                QuickAnim.fadeOut(progress2, false, new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        QuickAnim.collapseViewWidth(progress2, 500, false, false, null);
                    }
                });
                QuickAnim.slideYAxisTo(black_progress2, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video2, false, null);
                image1.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video1_1.setAlpha(1.0f);
                text_filter_video3_1.setAlpha(1.0f);
                text_filter_video1_2.setAlpha(1.0f);
                text_filter_video3_2.setAlpha(1.0f);
                QuickAnim.fadeOut(border2, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startTimer2();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

    public void seek3_update() {
//        if (video3_isPlay) {
            QuickAnim.expandViewWidthTo2(progress3, MAX_DISTANCE_PROGRESS, timeUsed3 * 1000, false, new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
//                    if (timeUsed3 == TIME_VIDEO3) {
//                        currentVideo = "";
//                        QuickAnim.changeImageWithFade(play_icon3, playImg, 300, null);
//                        emptyStatus = true;
//                        QuickAnim.fadeOut(light_img, false, null);
//                        QuickAnim.scale(container3, 1.0f, null);
//                        video3_isPlay = false;
//                        progressBar_video3.setProgress(0);
//                        tempPuase = 0;
//                        image1.setImageBitmap(oImage1);
//                        image2.setImageBitmap(oImage2);
////                QuickAnim.fadeIn(play_icon3, null);
//                        QuickAnim.fadeIn(text_filter_video3, null);
//                        filter_video3.setAlpha(1.0f);
//                        QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);
//                        QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0, null);
//                        QuickAnim.fadeOut(progress3, false, new OnAnimationEndListener() {
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//                                QuickAnim.collapseViewWidth(progress3, 500, false, false, null);
//                            }
//                        });
//                        QuickAnim.slideYAxisTo(black_progress3, 600, 0, 0, null);
//                        QuickAnim.fadeOut(text_playing_video3, false, null);
//                        image1.setAlpha(1.0f);
//                        image2.setAlpha(1.0f);
//                        play_icon1.setAlpha(1.0f);
//                        play_icon2.setAlpha(1.0f);
//                        filter_video1.setAlpha(1.0f);
//                        filter_video2.setAlpha(1.0f);
//                        text_filter_video1.setAlpha(1.0f);
//                        text_filter_video2.setAlpha(1.0f);
//                        QuickAnim.fadeOut(border3, false, null);
//                    }
                }
            });
//            progressBar_video3.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
//            Log.e("Current time Video 3", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

//            if (timeUsed3 == 0) {
//                currentVideo = "";
//                QuickAnim.changeImageWithFade(play_icon3, playImg, 300, null);
//                emptyStatus = true;
//                QuickAnim.fadeOut(light_img, false, null);
//                QuickAnim.scale(container3, 1.0f, null);
//                video3_isPlay = false;
//                progressBar_video3.setProgress(0);
//                tempPuase = 0;
//                image1.setImageBitmap(oImage1);
//                image2.setImageBitmap(oImage2);
////                QuickAnim.fadeIn(play_icon3, null);
//                QuickAnim.fadeIn(text_filter_video3, null);
//                filter_video3.setAlpha(1.0f);
//                QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);
//                QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0, null);
//                QuickAnim.fadeOut(progress3, false, new OnAnimationEndListener() {
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        QuickAnim.collapseViewWidth(progress3, 500, false, false, null);
//                    }
//                });
//                QuickAnim.slideYAxisTo(black_progress3, 600, 0, 0, null);
//                QuickAnim.fadeOut(text_playing_video3, false, null);
//                image1.setAlpha(1.0f);
//                image2.setAlpha(1.0f);
//                play_icon1.setAlpha(1.0f);
//                play_icon2.setAlpha(1.0f);
//                filter_video1.setAlpha(1.0f);
//                filter_video2.setAlpha(1.0f);
//                text_filter_video1.setAlpha(1.0f);
//                text_filter_video2.setAlpha(1.0f);
//                QuickAnim.fadeOut(border3, false, null);
//            }

//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    seek3_update();
//                }
//            };
//            new Handler().postDelayed(r, 1000);
//        }
    }

    public void pauseProgress3() {
        QuickAnim.expandViewWidthTo2(progress3, progress3.getWidth(), 1, false, null);
    }

    public void startTimer3() {
        if (video3_isPlay) {
            progressBar_video3.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("progress3",progressBar_video3.getProgress()+"");

            if (progressBar_video3.getProgress() == TIME_VIDEO3) {
                currentVideo = "";
                QuickAnim.changeImageWithFade(play_icon3, playImg, 300, null);
                emptyStatus = true;
                QuickAnim.fadeOut(light_img, false, null);
                QuickAnim.scale(container3, 1.0f, null);
                video3_isPlay = false;
                progressBar_video3.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image2.setImageBitmap(oImage2);
//                QuickAnim.fadeIn(play_icon3, null);
                QuickAnim.fadeIn(text_filter_video3_1, null);
                QuickAnim.fadeIn(text_filter_video3_2, null);
                filter_video3.setAlpha(1.0f);
                QuickAnim.expandViewWidthTo(filter_video3, 450, 1000, false, null);
                QuickAnim.slideYAxisTo(progressBar_video3, 600, 0, 0, null);
                QuickAnim.fadeOut(progress3, false, new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        QuickAnim.collapseViewWidth(progress3, 500, false, false, null);
                    }
                });
                QuickAnim.slideYAxisTo(black_progress3, 600, 0, 0, null);
                QuickAnim.fadeOut(text_playing_video3, false, null);
                image1.setAlpha(1.0f);
                image2.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                text_filter_video1_1.setAlpha(1.0f);
                text_filter_video2_1.setAlpha(1.0f);
                text_filter_video1_2.setAlpha(1.0f);
                text_filter_video2_2.setAlpha(1.0f);
                QuickAnim.fadeOut(border3, false, null);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startTimer3();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
    }

    public void animationButton1() {
        if (currentVideo.equals("1")) {
            circle_icon1.setScaleX(1.0f);
            circle_icon1.setScaleY(1.0f);
            circle_icon1.setAlpha(1.0f);
            circle_icon1.setVisibility(View.VISIBLE);
            QuickAnim.scaleAndFade(circle_icon1, 1500, 1.4f, 1.4f, 0.0f, null);

            Runnable r = new Runnable() {
                @Override
                public void run() {
//                    circle_icon1.setScaleX(1.0f);
//                    circle_icon1.setScaleY(1.0f);
//                    circle_icon1.setAlpha(1.0f);
//                    circle_icon1.setVisibility(View.VISIBLE);
                    animationButton1();
                }
            };
            new Handler().postDelayed(r, 1500);
        }
    }

    public void animationButton2() {
        if (currentVideo.equals("2")) {
            circle_icon2.setScaleX(1.0f);
            circle_icon2.setScaleY(1.0f);
            circle_icon2.setAlpha(1.0f);
            circle_icon2.setVisibility(View.VISIBLE);
            QuickAnim.scaleAndFade(circle_icon2, 1500, 1.4f, 1.4f, 0.0f, null);

            Runnable r = new Runnable() {
                @Override
                public void run() {
//                    circle_icon2.setScaleX(1.0f);
//                    circle_icon2.setScaleY(1.0f);
//                    circle_icon2.setAlpha(1.0f);
//                    circle_icon2.setVisibility(View.VISIBLE);
                    animationButton2();
                }
            };
            new Handler().postDelayed(r, 1500);
        }
    }

    public void animationButton3() {
        if (currentVideo.equals("3")) {
            circle_icon3.setScaleX(1.0f);
            circle_icon3.setScaleY(1.0f);
            circle_icon3.setAlpha(1.0f);
            circle_icon3.setVisibility(View.VISIBLE);
            QuickAnim.scaleAndFade(circle_icon3, 1500, 1.4f, 1.4f, 0.0f, null);

            Runnable r = new Runnable() {
                @Override
                public void run() {
//                    circle_icon3.setScaleX(1.0f);
//                    circle_icon3.setScaleY(1.0f);
//                    circle_icon3.setAlpha(1.0f);
//                    circle_icon3.setVisibility(View.VISIBLE);
                    animationButton3();
                }
            };
            new Handler().postDelayed(r, 1500);
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
