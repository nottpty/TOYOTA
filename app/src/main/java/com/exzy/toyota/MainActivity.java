package com.exzy.toyota;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private ImageView image1, image2, image3;
//    private ProgressBar seekBar_video1, seekBar_video2, seekBar_video3;
    private ProgressBar progressBar_video1,progressBar_video2,progressBar_video3;
    private ObjectAnimator progressAnimator1;
    private boolean video1_isPlay, video2_isPlay, video3_isPlay;
    private long startTime;
    private boolean firstIn;
    private int tempPuase;
    private String currentVideo;
    private TextView topic,sup_topic,text_filter_video1,text_filter_video2,text_filter_video3;
    private TextView text_playing_video1,text_playing_video2,text_playing_video3;
    private ImageView border1,border2,border3;
    private ImageView play_icon1,play_icon2,play_icon3;
    private ImageView filter_video1,filter_video2,filter_video3;
    private Bitmap bImage1,bImage2,bImage3,oImage1,oImage2,oImage3,fadePauseImage1,fadePauseImage2,fadePauseImage3;
    private ImageView image1_filter,image2_filter,image3_filter;
    private final int TIME_VIDEO1 = 10;
    private final int TIME_VIDEO2 = 10;
    private final int TIME_VIDEO3 = 10;

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
                image2.setImageBitmap(bImage2);
                image3.setImageBitmap(bImage3);
                image2.setAlpha(0.5f);
                image3.setAlpha(0.5f);
                play_icon2.setAlpha(0.5f);
                play_icon3.setAlpha(0.5f);
                filter_video2.setAlpha(0.5f);
                filter_video3.setAlpha(0.5f);
                text_filter_video2.setAlpha(0.5f);
                text_filter_video3.setAlpha(0.5f);
                if (!currentVideo.equals("1")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "1";
                if (video1_isPlay) {
                    video1_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video1.getProgress();
                    play_icon1.setVisibility(View.VISIBLE);
//                    image1_filter.setVisibility(View.VISIBLE);

                    QuickAnim.fadeIn(image1_filter, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }
                    });

//                    text_filter_video1.setVisibility(View.VISIBLE);
//                    filter_video1.setVisibility(View.VISIBLE);
//                    progressBar_video1.setVisibility(View.VISIBLE);
//                    text_playing_video1.setVisibility(View.VISIBLE);
//                    border1.setVisibility(View.VISIBLE);
                    text_playing_video1.setText("PAUSE");
//                    progressAnimator1.pause();
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
                    Log.e("Width", image1.getWidth()+"");
                    Log.e("Height", image1.getHeight()+"");
                    QuickAnim.scale(image1, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeIn(border1, null);
                        }
                    });
                    QuickAnim.scale(border1, 1.0f, null);
//                    QuickAnim.scale(progressBar_video1, 1.4f, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//
//                        }
//                    });
                    QuickAnim.scale(text_playing_video1, 1.0f, null);
                    QuickAnim.scale(play_icon1, 1.0f, null);

                    image2_filter.setVisibility(View.INVISIBLE);
                    image3_filter.setVisibility(View.INVISIBLE);
                    text_playing_video1.setText("PLAYING . . .");
                    image1.setAlpha(1.0f);
                    image1.setImageBitmap(oImage1);
                    play_icon1.setAlpha(1.0f);
                    border2.setVisibility(View.INVISIBLE);
                    border3.setVisibility(View.INVISIBLE);
                    progressBar_video2.setVisibility(View.INVISIBLE);
                    text_playing_video2.setVisibility(View.INVISIBLE);
                    progressBar_video3.setVisibility(View.INVISIBLE);
                    text_playing_video3.setVisibility(View.INVISIBLE);
                    play_icon2.setVisibility(View.VISIBLE);
                    play_icon3.setVisibility(View.VISIBLE);
                    filter_video2.setVisibility(View.VISIBLE);
                    text_filter_video2.setVisibility(View.VISIBLE);
                    filter_video3.setVisibility(View.VISIBLE);
                    text_filter_video3.setVisibility(View.VISIBLE);

                    client.send("1", IP_SERVER, TCP_PORT);
                    tempPuase = 0;
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = true;
                    video2_isPlay = false;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video1.setMax(TIME_VIDEO1);
                    play_icon1.setVisibility(View.INVISIBLE);
                    QuickAnim.scale(text_filter_video1, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            QuickAnim.fadeOut(text_filter_video1, 300, true, null);
                        }
                    });
//                    text_filter_video1.setVisibility(View.INVISIBLE);
                    QuickAnim.scaleAndFade(filter_video1, 100, 1.17f, 1.0f, 1.0f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.slideInXAxisTo(filter_video1, 2000, 600, false, null);
                            QuickAnim.fadeOut(filter_video1,false,null);
                        }
                    });
//                    QuickAnim.scale(filter_video1, 1.1f, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            QuickAnim.slideInXAxisTo(filter_video1, 2000, 500, false, null);
//                        }
//                    });
//                    filter_video1.setVisibility(View.INVISIBLE);
//                    progressBar_video1.setVisibility(View.VISIBLE);
                    QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0.7f, new OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
//                            text_playing_video1.setVisibility(View.VISIBLE);
                            QuickAnim.fadeIn(text_playing_video1, null);
                        }
                    });
//                    progressBar_video1.setVisibility(View.VISIBLE);
//                    text_playing_video1.setVisibility(View.VISIBLE);
                    border1.setVisibility(View.VISIBLE);
                    seek1_update();
                    firstIn = false;
                } else if (!video1_isPlay && !firstIn) {
                    QuickAnim.scale(border1, 1.0f, null);
                    QuickAnim.scale(text_playing_video1, 1.0f, null);
                    QuickAnim.scale(play_icon1, 1.0f, null);
                    QuickAnim.fadeOut(text_filter_video1, 300, false, null);
                    if(progressBar_video1.getProgress() == 0) {
                        QuickAnim.scaleAndFade(filter_video1, 100, 1.17f, 1.0f, 1.0f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
//                            QuickAnim.slideInXAxisTo(filter_video1, 2000, 600, false, null);
                                QuickAnim.fadeOut(filter_video1,false,null);
                            }
                        });
                        QuickAnim.scale(image1, 1.0f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                QuickAnim.fadeIn(border1, null);
                            }
                        });
                        QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0.7f, new OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd(Animation animation) {
//                            text_playing_video1.setVisibility(View.VISIBLE);
                                QuickAnim.fadeIn(text_playing_video1, null);
                            }
                        });
                    }
//                    QuickAnim.slideInXAxisTo(filter_video1, 2000, 500, false, null);
//                    progressBar_video1.setVisibility(View.VISIBLE);
//                    QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0.7f, new OnAnimationEndListener() {
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
////                            text_playing_video1.setVisibility(View.VISIBLE);
//                            QuickAnim.fadeIn(text_playing_video1, null);
//                        }
//                    });

                    video1_isPlay = true;
                    text_playing_video1.setText("PLAYING . . .");
                    startTime = System.currentTimeMillis();
                    play_icon1.setVisibility(View.INVISIBLE);
//                    text_filter_video1.setVisibility(View.INVISIBLE);
//                    filter_video1.setVisibility(View.INVISIBLE);
//                    progressBar_video1.setVisibility(View.VISIBLE);
//                    text_playing_video1.setVisibility(View.VISIBLE);
//                    border1.setVisibility(View.VISIBLE);
//                    image1_filter.setVisibility(View.INVISIBLE);
                    QuickAnim.fadeOut(image1_filter, false, null);
                    seek1_update();
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1.setImageBitmap(bImage1);
                image3.setImageBitmap(bImage3);
                image1.setAlpha(0.5f);
                image3.setAlpha(0.5f);
                play_icon1.setAlpha(0.5f);
                play_icon3.setAlpha(0.5f);
                filter_video1.setAlpha(0.5f);
                filter_video3.setAlpha(0.5f);
                text_filter_video1.setAlpha(0.5f);
                text_filter_video3.setAlpha(0.5f);
                if (!currentVideo.equals("2")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "2";
                Log.e("isPlay", video2_isPlay + "");
                Log.e("firstIn", firstIn + "");
                if (video2_isPlay) {
                    video2_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video2.getProgress();
                    play_icon2.setVisibility(View.VISIBLE);
                    image2_filter.setVisibility(View.VISIBLE);
                    text_playing_video2.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
                    image1_filter.setVisibility(View.INVISIBLE);
                    image3_filter.setVisibility(View.INVISIBLE);
                    text_playing_video2.setText("PLAYING . . .");
                    image2.setAlpha(1.0f);
                    image2.setImageBitmap(oImage2);
                    play_icon2.setAlpha(1.0f);
                    border1.setVisibility(View.INVISIBLE);
                    border3.setVisibility(View.INVISIBLE);
                    progressBar_video1.setVisibility(View.INVISIBLE);
                    text_playing_video1.setVisibility(View.INVISIBLE);
                    progressBar_video3.setVisibility(View.INVISIBLE);
                    text_playing_video3.setVisibility(View.INVISIBLE);
                    play_icon1.setVisibility(View.VISIBLE);
                    play_icon3.setVisibility(View.VISIBLE);
                    filter_video1.setVisibility(View.VISIBLE);
                    text_filter_video1.setVisibility(View.VISIBLE);
                    filter_video3.setVisibility(View.VISIBLE);
                    text_filter_video3.setVisibility(View.VISIBLE);

                    client.send("2", IP_SERVER, TCP_PORT);
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = false;
                    video2_isPlay = true;
                    video3_isPlay = false;
                    startTime = System.currentTimeMillis();
                    progressBar_video2.setMax(TIME_VIDEO2);
                    play_icon2.setVisibility(View.INVISIBLE);
                    text_filter_video2.setVisibility(View.INVISIBLE);
                    filter_video2.setVisibility(View.INVISIBLE);
                    progressBar_video2.setVisibility(View.VISIBLE);
                    text_playing_video2.setVisibility(View.VISIBLE);
                    border2.setVisibility(View.VISIBLE);
                    seek2_update();
                    firstIn = false;
                } else if (!video2_isPlay && !firstIn) {
                    video2_isPlay = true;
                    startTime = System.currentTimeMillis();
                    text_playing_video2.setText("PLAYING . . .");
                    play_icon2.setVisibility(View.INVISIBLE);
                    text_filter_video2.setVisibility(View.INVISIBLE);
                    filter_video2.setVisibility(View.INVISIBLE);
                    progressBar_video2.setVisibility(View.VISIBLE);
                    text_playing_video2.setVisibility(View.VISIBLE);
                    border2.setVisibility(View.VISIBLE);
                    image2_filter.setVisibility(View.INVISIBLE);
                    seek2_update();
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image1.setImageBitmap(bImage1);
                image2.setImageBitmap(bImage2);
                image1.setAlpha(0.5f);
                image2.setAlpha(0.5f);
                play_icon1.setAlpha(0.5f);
                play_icon2.setAlpha(0.5f);
                filter_video1.setAlpha(0.5f);
                filter_video2.setAlpha(0.5f);
                text_filter_video1.setAlpha(0.5f);
                text_filter_video2.setAlpha(0.5f);
                if (!currentVideo.equals("3")) {
                    firstIn = true;
                    tempPuase = 0;
                }
                currentVideo = "3";
                if (video3_isPlay) {
                    video3_isPlay = false;
                    firstIn = false;
                    tempPuase = progressBar_video3.getProgress();
                    play_icon3.setVisibility(View.VISIBLE);
                    image3_filter.setVisibility(View.VISIBLE);
                    text_playing_video3.setText("PAUSE");
                    Log.e("temp", tempPuase + "");
                } else if (firstIn) {
                    image1_filter.setVisibility(View.INVISIBLE);
                    image2_filter.setVisibility(View.INVISIBLE);
                    text_playing_video3.setText("PLAYING . . .");
                    image3.setAlpha(1.0f);
                    image3.setImageBitmap(oImage3);
                    play_icon3.setAlpha(1.0f);
                    border1.setVisibility(View.INVISIBLE);
                    border2.setVisibility(View.INVISIBLE);
                    progressBar_video1.setVisibility(View.INVISIBLE);
                    text_playing_video1.setVisibility(View.INVISIBLE);
                    progressBar_video2.setVisibility(View.INVISIBLE);
                    text_playing_video2.setVisibility(View.INVISIBLE);
                    play_icon1.setVisibility(View.VISIBLE);
                    play_icon2.setVisibility(View.VISIBLE);
                    filter_video1.setVisibility(View.VISIBLE);
                    text_filter_video1.setVisibility(View.VISIBLE);
                    filter_video2.setVisibility(View.VISIBLE);
                    text_filter_video2.setVisibility(View.VISIBLE);

                    client.send("3", IP_SERVER, TCP_PORT);
                    progressBar_video1.setProgress(0);
                    progressBar_video2.setProgress(0);
                    progressBar_video3.setProgress(0);
                    video1_isPlay = false;
                    video2_isPlay = false;
                    video3_isPlay = true;
                    startTime = System.currentTimeMillis();
                    progressBar_video3.setMax(TIME_VIDEO3);
                    play_icon3.setVisibility(View.INVISIBLE);
                    text_filter_video3.setVisibility(View.INVISIBLE);
                    filter_video3.setVisibility(View.INVISIBLE);
                    progressBar_video3.setVisibility(View.VISIBLE);
                    text_playing_video3.setVisibility(View.VISIBLE);
                    border3.setVisibility(View.VISIBLE);
                    seek3_update();
                    firstIn = false;
                } else if (!video3_isPlay && !firstIn) {
                    video3_isPlay = true;
                    startTime = System.currentTimeMillis();
                    text_playing_video3.setText("PLAYING . . .");
                    play_icon3.setVisibility(View.INVISIBLE);
                    text_filter_video3.setVisibility(View.INVISIBLE);
                    filter_video3.setVisibility(View.INVISIBLE);
                    progressBar_video3.setVisibility(View.VISIBLE);
                    text_playing_video3.setVisibility(View.VISIBLE);
                    border3.setVisibility(View.VISIBLE);
                    image3_filter.setVisibility(View.INVISIBLE);
                    seek3_update();
                }
            }
        });
    }

    public void seek1_update() {
        if (video1_isPlay) {
            progressBar_video1.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("Current time Video 1", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");
//            progressAnimator1 = ObjectAnimator.ofInt(progressBar_video1, "progress", tempPuase, progressBar_video1.getMax());
//            int elapseTime = progressBar_video1.getMax()-tempPuase;
//            progressAnimator1.setDuration(1000*elapseTime);
//            progressAnimator1.setInterpolator(new LinearInterpolator());
//            progressAnimator1.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    video1_isPlay = false;
//                    progressBar_video1.setProgress(0);
//                    tempPuase = 0;
//                    play_icon1.setVisibility(View.VISIBLE);
//                    text_filter_video1.setVisibility(View.VISIBLE);
//                    filter_video1.setVisibility(View.VISIBLE);
//                    progressBar_video1.setVisibility(View.INVISIBLE);
//                    text_playing_video1.setVisibility(View.INVISIBLE);
//                    border1.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//            progressAnimator1.start();


            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video1.getMax()) {
                video1_isPlay = false;
                progressBar_video1.setProgress(0);
                tempPuase = 0;
                image2.setImageBitmap(oImage2);
                image3.setImageBitmap(oImage3);
//                play_icon1.setVisibility(View.VISIBLE);
                QuickAnim.fadeIn(play_icon1,null);
//                text_filter_video1.setVisibility(View.VISIBLE);
                QuickAnim.fadeIn(text_filter_video1, null);
                QuickAnim.scaleAndFade(filter_video1, 300, 0.91f, 0.91f, 1.0f, null);
//                filter_video1.setVisibility(View.VISIBLE);
//                progressBar_video1.setVisibility(View.INVISIBLE);
                QuickAnim.slideYAxisTo(progressBar_video1, 600, 0, 0, new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
//                            text_playing_video1.setVisibility(View.VISIBLE);
//                        QuickAnim.fadeIn(text_playing_video1, null);
                    }
                });
//                QuickAnim.fadeOut(progressBar_video1,false,null);
                QuickAnim.fadeOut(text_playing_video1,false,null);
//                text_playing_video1.setVisibility(View.INVISIBLE);
//                border1.setVisibility(View.INVISIBLE);
                image2.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video2.setAlpha(1.0f);
                text_filter_video3.setAlpha(1.0f);

//                filter_video1.setAlpha(1.0f);
//                text_filter_video1.setAlpha(1.0f);
//                text_filter_video1.setScaleX(1.0f);
//                text_filter_video1.setScaleY(1.0f);

                QuickAnim.scale(image1, 0.91f, null);
                QuickAnim.scale(border1, 0.91f, new OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        QuickAnim.fadeOut(border1, false,null);
                    }
                });
                text_playing_video1.setScaleX(1.0f);
                text_playing_video1.setScaleY(1.0f);
                play_icon1.setScaleX(1.0f);
                play_icon1.setScaleY(1.0f);
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    seek1_update();
                }
            };
            new Handler().postDelayed(r, 1000);
        }
//        progressAnimator.pause();
    }

    public void seek2_update() {
        if (video2_isPlay) {
            Log.e("temp_uodate", tempPuase + "");
            progressBar_video2.setProgress((int) ((System.currentTimeMillis() - startTime) / 1000) + tempPuase);
            Log.e("Current time Video 2", ((System.currentTimeMillis() - startTime) / 1000) + tempPuase + "");

            if (((System.currentTimeMillis() - startTime) / 1000) + tempPuase > progressBar_video2.getMax()) {
                video2_isPlay = false;
                progressBar_video2.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image3.setImageBitmap(oImage3);
                play_icon2.setVisibility(View.VISIBLE);
                text_filter_video2.setVisibility(View.VISIBLE);
                filter_video2.setVisibility(View.VISIBLE);
                progressBar_video2.setVisibility(View.INVISIBLE);
                text_playing_video2.setVisibility(View.INVISIBLE);
                border2.setVisibility(View.INVISIBLE);
                image1.setAlpha(1.0f);
                image3.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon3.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video3.setAlpha(1.0f);
                text_filter_video1.setAlpha(1.0f);
                text_filter_video3.setAlpha(1.0f);

                filter_video2.setAlpha(1.0f);
                text_filter_video2.setAlpha(1.0f);
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
                video3_isPlay = false;
                progressBar_video3.setProgress(0);
                tempPuase = 0;
                image1.setImageBitmap(oImage1);
                image2.setImageBitmap(oImage2);
                play_icon3.setVisibility(View.VISIBLE);
                text_filter_video3.setVisibility(View.VISIBLE);
                filter_video3.setVisibility(View.VISIBLE);
                progressBar_video3.setVisibility(View.INVISIBLE);
                text_playing_video3.setVisibility(View.INVISIBLE);
                border3.setVisibility(View.INVISIBLE);
                image1.setAlpha(1.0f);
                image2.setAlpha(1.0f);
                play_icon1.setAlpha(1.0f);
                play_icon2.setAlpha(1.0f);
                filter_video1.setAlpha(1.0f);
                filter_video2.setAlpha(1.0f);
                text_filter_video1.setAlpha(1.0f);
                text_filter_video2.setAlpha(1.0f);

                filter_video3.setAlpha(1.0f);
                text_filter_video3.setAlpha(1.0f);
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
