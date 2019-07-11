package obelab.com.sdkexample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jetbrains.annotations.NotNull;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import obelab.com.nirsitsdk.NirsitData;
import obelab.com.nirsitsdk.NirsitProvider;
import obelab.com.sdkexample.model.Song;
import obelab.com.sdkexample.service.MusicBinder;
import obelab.com.sdkexample.service.MusicService;
import obelab.com.sdkexample.utils.DensityUtil;
import obelab.com.sdkexample.utils.GraphUtils;
import obelab.com.sdkexample.utils.PlaybackInfoListener;
import uk.me.berndporr.iirj.Butterworth;


public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("tensorflow_inference");
    }

    private Butterworth[] butterworth;
    private String MODEL_PATH = "file:///android_asset/Network.pb";
    private String INPUT_NAME = "dense_3_input";
    private String OUTPUT_NAME = "output_1";
    private MusicService mMusicService;

    private TensorFlowInferenceInterface tf;


    //    TextView data780TextView;
//    TextView data850TextView;
    Button startButton;
    Button stopButton;
    Button resetButton;
    NirsitProvider nirsitProvider;
    private ImageView mEmotionImageView;

    Switch lpfSwitch;
    Switch mbllSwitch;
    Switch heartbeatSwitch;

    Button clearLog;
    Button showMore;
    Button setIp;
    boolean isShowMore = false;
    Button monitorButton;
    // Default device ip
    String ip = "192.168.0.1";

    LinearLayout setting_container;
    final int port = 50007;
    final int TIME_OUT = 3000;

    double[] D780;
    double[] D850;
    int time = 0;

    private double[][] temp_D780_data = new double[24][204];
    private double[][] temp_D850_data = new double[24][204];
    private double[][] HbO2 = new double[24][204];
    private float[] HbO2_avg = new float[204];

    private int[] mEmotionsResource;
    private LineChart mLineChart;
    private GraphUtils mGraphUtils;
    private static final int[] mColors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
    };
    private List<AssetFileDescriptor> mSongs;
    private MusicBinder mMusicBinder;
    private int[] mPlayResults = {0, 0, 0};
    private PlaybackInfoListener mPlaybackInfoListener = new PlaybackInfoListener() {
        @Override
        public void onLoadMedia(@NotNull Song song) {

        }

        @Override
        public void onDurationChanged(int duration) {

        }

        @Override
        public void onPositionChanged(int position) {

        }

        @Override
        public void onStateChange(int state) {

        }

        @Override
        public void onComplete() {
            play();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicBinder = (MusicBinder) service;
            mMusicBinder.addPlaybackInfoListener(mPlaybackInfoListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private List<AssetFileDescriptor> getSongs() throws IOException {
        List<AssetFileDescriptor> fds = new ArrayList<>();
        fds.add(getAssets().openFd("neutral.mp3"));
        fds.add(getAssets().openFd("happy.mp3"));
        fds.add(getAssets().openFd("negative.mp3"));
        return fds;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        butterworth = new Butterworth[204];
        for (int i = 0; i < 204; i++) {
            butterworth[i] = new Butterworth();
            butterworth[i].bandPass(4, 8.1, 0.11, 0.9);
            butterworth[i].filter(1);
            butterworth[i].filter(2);
            butterworth[i].filter(3);
        }

        initActivity();
    }

    /**
     * 初始化Activity
     */
    private void initActivity() {
//        data780TextView = findViewById(R.id.data780TextView);
//        data850TextView = findViewById(R.id.data850TextView);
        mLineChart = findViewById(R.id.lc);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        lpfSwitch = findViewById(R.id.lpfSwitch);
        mbllSwitch = findViewById(R.id.mbllSwitch);
        heartbeatSwitch = findViewById(R.id.heartbeatSwitch);
        resetButton = findViewById(R.id.resetButton);
        monitorButton = findViewById(R.id.monitor);
        mEmotionImageView = findViewById(R.id.iv_emotion);
        showMore = findViewById(R.id.show_more);
        setIp = findViewById(R.id.set_nirsit_ip);
        clearLog = findViewById(R.id.clear_log);
        setting_container = findViewById(R.id.setting_container);
        DensityUtil.setTransparent((Toolbar) findViewById(R.id.tl),this);
        mEmotionsResource = new int[]{R.drawable.neutral, R.drawable.smile, R.drawable.cry};
        mGraphUtils = new GraphUtils(mLineChart, new String[]{ "data780", "data850" }, mColors);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            init();
                            startRecord();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        monitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            changeEmotion();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            }

        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMusicBinder.release();
                if (nirsitProvider == null) {
                    return;
                }
                nirsitProvider.stopMonitoring();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nirsitProvider == null) {
                    return;
                }
                nirsitProvider.resetHemo();
            }
        });

        setIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeAddressDialog();
            }
        });

        clearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                data780TextView.setText("[d780]\n");
//                data850TextView.setText("[d850]\n");
            }
        });

        showMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isShowMore){
                    setting_container.setVisibility(View.VISIBLE);
                    openSettingAnim(setting_container,80);
                    showMore.setBackgroundResource(R.drawable.up);
                    isShowMore = true;
                }else {
                    setting_container.setVisibility(View.VISIBLE);
                    closeSettingAnim(setting_container);
                    showMore.setBackgroundResource(R.drawable.more);
                    isShowMore = false;
                }
            }
        });

        lpfSwitch.setChecked(true);
        mbllSwitch.setChecked(true);
        heartbeatSwitch.setChecked(true);
        lpfSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (nirsitProvider == null) {
                    return;
                }
                nirsitProvider.setLpf(b);
            }
        });

        mbllSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (nirsitProvider == null) {
                    return;
                }
                nirsitProvider.setMbll(b);
            }
        });
        heartbeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (nirsitProvider == null) {
                    return;
                }
                nirsitProvider.setHeartbeat(b);
            }
        });

        nirsitProvider = new NirsitProvider();
        nirsitProvider.setListener(new NirsitProvider.NirsitProviderListener() {
            @Override
            public void onReceiveData(final NirsitData data) {

                if (time < 24) {

//                    data780TextView.setText("[d780]\n" + Arrays.toString(data.getD780()));
//                    data850TextView.setText("[d850]\n" + Arrays.toString(data.getD850()));

                    // 限定时间获取3秒内的数据
                    temp_D780_data[time] = data.getD780();
                    temp_D850_data[time] = data.getD850();

                    mGraphUtils.addEntry(0, (float) data.getD780()[0]);
                    mGraphUtils.addEntry(1, (float) data.getD850()[0]);

                    time++;
                    return;
                }

                changeEmotion();


            }

            @Override
            public void onDisconnected() {
                Toast.makeText(getApplicationContext(), "onDisconnected()", Toast.LENGTH_SHORT).show();
            }
        });
        setRotation();
        playSet();
    }

    private void setRotation(){
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mEmotionImageView,"rotation",0f,359f);
        rotation.setRepeatCount(ObjectAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.setDuration(5000);
        rotation.start();
    }

    private void openSettingAnim(View v,int height) {
        v.setVisibility(View.VISIBLE);
        float mDensity = getResources().getDisplayMetrics().density;
        int mHeight = (int) (mDensity * height + 0.5);//伸展高度
        ValueAnimator animator = createAnimator(v, 0, mHeight);
        animator.start();
    }

    private void closeSettingAnim(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }


    private void startRecord() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                play();
            }
        }).start();
    }

    private void play() {
        int result = 0;
        if (mPlayResults[1] > mPlayResults[result]) {
            result = 1;
        }
        if (mPlayResults[2] > mPlayResults[result]) {
            result = 2;
        }

        if (mSongs != null) {
            mMusicBinder.loadMedia(mSongs.get(result), false, false);
            mMusicBinder.play();
        }
        mPlayResults[0] = 0;
        mPlayResults[1] = 0;
        mPlayResults[2] = 0;
    }

    private void playSet() {
        try {
            mSongs = getSongs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    void calculate(double[][] D780, double[][] D850) {
        double L[] = {1.5, 2.12, 3.0, 3.35};
        double B[] = {6.648, 4.650, 3.270, 2.860};

        int pos;

        //考虑不同的通道的距离L的值不一样
        double a = 0.1736;
        double b = 0.2543;
        double c = 0.2526;
        double d = 0.1798;

        for (int j = 0; j < 204; j++) {
            double sum = 0;
            for (int i = 0; i < 24; i++) {
//
//                if (j >= 0 && j <= 67)
//                    pos = 2;
//                else if (j >= 68 && j <= 119)
//                    pos = 0;
//                else if (j >= 120 && j <= 155)
//                    pos = 1;
//                else
//                    pos = 3;
//
//                double e = D780[i][j] / (L[pos] * B[pos]);
//                double f = D850[i][j] / (L[pos] * B[pos]);
//
//                double temp = (e * d - f * b) / ((a * d - b * c) * 100);

//                temp = butterworth[j].filter(temp);
//                HbO2[i][j] = temp;
                sum += D780[i][j];

            }

            HbO2_avg[j] = (float) (sum / 24);
        }

    }


    private void init() throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(TIME_OUT);
        socket.setReuseAddress(true);
        socket.connect(new InetSocketAddress(ip, port), TIME_OUT);
        if (socket.isConnected()) {
            nirsitProvider.setSocket(socket);
            nirsitProvider.startMonitoring();
        } else {
            Log.e("TAG", "Check Connect");
        }
    }

    /**
     * 根据数据改变表情
     */
    private void changeEmotion() {
        //首先要对780和850的数据进行IIR带通滤波处理 0.02～0.2HZ

        //再计算HbO和Hb的数值，并保存在另外的大数组里，

        //并提取斜率以及均值特征。采用
        if (time == 24) {
            calculate(temp_D780_data, temp_D850_data);

            Classifer classifer = new Classifer(getAssets(), MODEL_PATH);
            // 要不要均值，斜率。
            float[] predictions = classifer.predict(INPUT_NAME, OUTPUT_NAME, 1, 204, HbO2_avg);
            displayEmotion(predictions);

            time = 0;
        }

    }

    /**
     * 显示表情
     *
     * @param predictions 计算结果，根据最大值的index显示表情
     */
    private void displayEmotion(float[] predictions) {
        int indexOfMax = 0;
        int length = predictions.length;

        for (int i = 1; i < length; i++) {
            if (predictions[i] > predictions[indexOfMax]) {
                indexOfMax = i;
            }
        }

        mEmotionImageView.setImageResource(mEmotionsResource[indexOfMax]);
        mPlayResults[indexOfMax]++;
    }


    private void showChangeAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Nirsit IP");
        final EditText input = new EditText(this);
        input.setText(ip);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newIp = input.getText().toString();
                String validIp = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
                if (!Pattern.matches(validIp, newIp)) {
                    Toast.makeText(getApplicationContext(), "Invalid IP address", Toast.LENGTH_SHORT).show();
                } else {
                    ip = input.getText().toString();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
