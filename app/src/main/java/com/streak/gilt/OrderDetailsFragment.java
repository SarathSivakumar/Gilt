package com.streak.gilt;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsFragment extends Fragment {

    TextView model,size,weight,factory,option,seal,audio_starttime, audio_endtime;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String modeltext,sizetext,weighttext,factorytext,optiontext,sealtext,audioPath;
    Boolean isAudio;
    SeekBar audioSeekbar;
    ImageView audio_play;
    MediaPlayer mp;
    TableRow audioRow;
    int totalTime;


    public OrderDetailsFragment(String model,String size,String weight,String factory, String option, String seal, Boolean isAudio, String audioPath) {
        modeltext=model;
        sizetext=size;
        weighttext=weight;
        factorytext=factory;
        optiontext=option;
        sealtext=seal;

        this.isAudio=isAudio;
        this.audioPath=audioPath;
    }

    public static OrderDetailsFragment newInstance(String param1, String param2,String param3, String param4,String param5, String param6, Boolean param7,String param8) {
        OrderDetailsFragment fragment = new OrderDetailsFragment(param1,param2,param3,param4,param5,param6,param7,param8);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_order_details, container, false);
        model=view.findViewById(R.id.orderdetails_model);
        size=view.findViewById(R.id.orderdetails_size);
        weight=view.findViewById(R.id.orderdetails_weight);
        option=view.findViewById(R.id.orderdetails_option1);
        seal=view.findViewById(R.id.orderdetails_seal);
        factory=view.findViewById(R.id.orderdetails_factoryName);
        audioRow=view.findViewById(R.id.audioRow);

        audio_play=view.findViewById(R.id.audio_play);
        audioSeekbar=view.findViewById(R.id.audio_seekbar);
        audio_starttime=view.findViewById(R.id.audio_starttime);
        audio_endtime=(TextView) view.findViewById(R.id.audio_endtime);

        model.setText(modeltext);
        size.setText(sizetext);
        weight.setText(weighttext);
        option.setText(optiontext);
        seal.setText(sealtext);
        factory.setText(factorytext);

        if(isAudio) {

                mp = MediaPlayer.create(getContext(), Uri.parse(audioPath));
                mp.setLooping(false);
                mp.seekTo(0);
                mp.setVolume(0, 100);
                totalTime = mp.getDuration();
                audioSeekbar.setMax(totalTime);
                //audio_endtime.setText(totalTime);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (mp != null) {
                            try {
                                Message msg = new Message();
                                msg.what = mp.getCurrentPosition();
                                handler.sendMessage(msg);
                                Thread.sleep(100);

                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }).start();
                audio_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mp.isPlaying()) {
                            mp.start();
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    //Toast.makeText(getContext(),"Media stopped",Toast.LENGTH_SHORT).show();
                                    //mp.pause();
                                    //audioSeekbar.setProgress(0);
                                    audio_play.setImageResource(R.drawable.ic_play);
                                }
                            });
                            audio_play.setImageResource(R.drawable.ic_pause);
                        } else {
                            mp.pause();
                            audio_play.setImageResource(R.drawable.ic_play);
                        }
                        audioSeekbar.setOnSeekBarChangeListener(
                                new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        if (fromUser) {
                                            mp.seekTo(progress);
                                            seekBar.setProgress(progress);
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }

                                }
                        );

                    }
                });



        }
        else {
            audioRow.setVisibility(View.GONE);
        }

        return  view;
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            int currentPosition=msg.what;
            String time=createTimeLabel(currentPosition);
            audioSeekbar.setProgress(currentPosition);
            audio_starttime.setText(time);
            audio_endtime.setText(createTimeLabel(totalTime));
        }
    };
    public String createTimeLabel(int time){
        String result;
        int min=time/1000/60;
        int sec=time/1000%60;
        result=min+":";
        if(sec<10) result+=0;
        result+=sec;
        return  result;
    }
    @Override
    public void onStop() {
        super.onStop();
        super.onPause();
        if (isAudio) {
            mp.pause();
            audio_play.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(isAudio) {
            mp.pause();
            audio_play.setImageResource(R.drawable.ic_play);
        }
    }



}