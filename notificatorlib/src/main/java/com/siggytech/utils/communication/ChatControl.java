package com.siggytech.utils.communication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siggytech.utils.communication.audio.AudioRecorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.siggytech.utils.communication.Utils.getDateName;

/**
 * @author SIGGI Tech
 */
public class ChatControl extends RelativeLayout {
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int SELECT_FILE = 100;
    private ListView mConversationView;
    private EditText mOutEditText;
    private EditText mServerAddress;
    private LinearLayout mSendButton;
    private LinearLayout mAddFile;
    private LinearLayout mAudio;
    private TextView mAudioText;
    private AudioRecorder ar;
    private CountDownTimer t;


    private ArrayAdapter<String> mConversationArrayAdapter;

    private int cnt;
    public String imei;
    public String name;
    public String api_key;
    public String userName;
    public int idGroup;
    private final Context context;
    private ChatListView abc;
    private String messageTittle;
    private String messageText;
    private String packageName;
    private int resIcon;
    private String notificationMessage;
    private Activity mActivity;

    public ChatControl(Context context, int idGroup, String API_KEY, String nameClient, String userName,
                       String messageTittle, String messageText, String packageName, int resIcon, String notificationMessage, Activity activity){
        super(context);
        this.context = context;
        this.idGroup = idGroup;
        this.api_key = API_KEY;
        this.name = nameClient;
        this.imei = getIMEINumber();
        this.userName = userName;
        this.messageTittle = messageTittle;
        this.messageText = messageText;
        this.packageName = packageName;
        this.resIcon = resIcon;
        this.notificationMessage = notificationMessage;
        this.mActivity = activity;

        initLayout(context);
    }

    @SuppressWarnings("deprecation")
    private String getIMEINumber() {
        String IMEINumber = "";
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                IMEINumber = telephonyMgr.getImei();
            } else {
                IMEINumber = telephonyMgr.getDeviceId();
            }
        }
        return IMEINumber;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initLayout(final Context context) {
        int idContent = Utils.generateViewId();

        ViewGroup.LayoutParams root_LayoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root_LayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        root_LayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.setLayoutParams(root_LayoutParams);

        RelativeLayout rl = new RelativeLayout(context);

        abc = new ChatListView(context, idGroup,
                api_key,
                name,
                messageTittle,
                messageText,
                packageName,
                resIcon);

        abc.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        abc.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout.LayoutParams abc_LayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        abc_LayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        abc_LayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        abc_LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        abc_LayoutParams.addRule(ABOVE,idContent);
        rl.addView(abc);
        this.addView(rl);
        rl.setLayoutParams(abc_LayoutParams);
        rl.setId(Utils.generateViewId());

        mOutEditText = new EditText(context);
        mOutEditText.setId(Utils.generateViewId());
        mOutEditText.setMaxLines(5);
        mOutEditText.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        mOutEditText.setBackgroundResource(R.drawable.gradientbg);

        mSendButton = new LinearLayout(context);
        mSendButton.setLayoutParams(new LayoutParams(100,100));
        mSendButton.setId(Utils.generateViewId());
        mSendButton.setBackgroundResource(R.drawable.send_selector);
        mSendButton.setGravity(Gravity.CENTER);
        if(!Conf.CHAT_BASIC) mSendButton.setVisibility(GONE);

        ImageView iv = new ImageView(context);
        iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_24dp));
        mSendButton.addView(iv);

        mAudio = new LinearLayout(context);
        mAudio.setLayoutParams(new LayoutParams(120,120));
        mAudio.setId(Utils.generateViewId());
        mAudio.setGravity(Gravity.CENTER);

        ImageView ivMic = new ImageView(context);
        ivMic.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none_24dp));
        mAudio.addView(ivMic);

        mAudioText = new TextView(context);
        mAudioText.setText("00:00:00");
        mAudioText.setTextColor(getResources().getColor(R.color.bt_dark_gray));
        mAudioText.setTextSize(TypedValue.COMPLEX_UNIT_SP,17);
        mAudioText.setPadding(20,0,0,0);
        mAudioText.setVisibility(GONE);

        mAddFile = new LinearLayout(context);
        mAddFile.setLayoutParams(new LayoutParams(120,120));
        mAddFile.setId(Utils.generateViewId());
        mAddFile.setGravity(Gravity.CENTER);

        final ImageView ivAdd = new ImageView(context);
        ivAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_24dp));
        mAddFile.addView(ivAdd);

        final ImageView ivMic2 = new ImageView(context);
        ivMic2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_none_gray_24dp));
        ivMic2.setVisibility(GONE);
        mAddFile.addView(ivMic2);

        RelativeLayout.LayoutParams mContentParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        mContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        LinearLayout lnContent = new LinearLayout(context);
        lnContent.setId(idContent);
        lnContent.setLayoutParams(mContentParams);
        lnContent.setBackgroundColor(getResources().getColor(R.color.light_grey));
        lnContent.setPadding(10,10,10,10);

        LinearLayout lnSend = getLnContentSum(6);
        LinearLayout lnH0 = getLnWeight(1);
        lnH0.setVerticalGravity(Gravity.CENTER|Gravity.BOTTOM);
        LinearLayout lnH1 = getLnWeight(Conf.CHAT_BASIC?5:4);
        lnH1.setVerticalGravity(Gravity.CENTER);
        LinearLayout lnH2 = getLnWeight(1);
        lnH2.setGravity(Gravity.CENTER|Gravity.BOTTOM);

        lnH0.addView(mAddFile);
        lnH1.addView(mOutEditText);
        lnH1.addView(mAudioText);
        lnH2.addView(mSendButton);
        if(!Conf.CHAT_BASIC) {
            lnH2.addView(mAudio);
            lnSend.addView(lnH0);
        }
        lnSend.addView(lnH1);
        lnSend.addView(lnH2);
        lnContent.addView(lnSend);

        this.addView(lnContent);

        if(!Conf.CHAT_BASIC) {
            TextWatcher excludeTW;
            excludeTW = new TextWatcher(){
                @Override
                public void afterTextChanged(Editable s) {}
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    if (count>0) {
                        mAudio.setVisibility(GONE);
                        mSendButton.setVisibility(VISIBLE);
                    } else {
                        mAudio.setVisibility(VISIBLE);
                        mSendButton.setVisibility(GONE);
                    }
                }
            };
            mOutEditText.addTextChangedListener(excludeTW);

            t = new CountDownTimer( Long.MAX_VALUE , 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    cnt++;
                    long millis = cnt;
                    int seconds = (int) (millis / 60);
                    int minutes = seconds / 60;
                    seconds     = seconds % 60;
                    mAudioText.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));
                }
                @Override
                public void onFinish() {}
            };

            mAudio.setOnTouchListener(new View.OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event)
                {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ChatControl.this.setFocusable(true);
                            ChatControl.this.requestFocus();

                            ivAdd.setVisibility(GONE);
                            ivMic2.setVisibility(VISIBLE);
                            mOutEditText.setVisibility(GONE);
                            mAudioText.setVisibility(VISIBLE);

                            String path = Conf.ROOT_PATH + getDateName() + ".3gp";
                            ar = new AudioRecorder(path);

                            audioRecording(true);

                            return true;
                        }
                        case MotionEvent.ACTION_UP: {

                            audioRecording(false);
                            cnt = 0;
                            mAudioText.setText("00:00:00");

                            //TODO aca se tiene que enviar al chat

                            ivAdd.setVisibility(VISIBLE);
                            ivMic2.setVisibility(GONE);
                            mOutEditText.setVisibility(VISIBLE);
                            mAudioText.setVisibility(GONE);
                            return true;
                        }
                    }

                    return false;
                }
            });








        }

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!"".equals(mOutEditText.getText().toString().trim())) {
                        String m = AESUtils.encrypt(mOutEditText.getText().toString());
                        SimpleDateFormat sdf;
                        switch (Conf.DATE_FORMAT) {
                            case 0:
                                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                                break;
                            case 1:
                                sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
                                break;
                            default:
                                sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
                                break;
                        }

                        Date now = new Date();
                        String strDate = sdf.format(now);
                        abc.sendMessage(userName, m, strDate, "text");
                        mOutEditText.setText("");
                    }
                } catch(Exception e){e.printStackTrace();}
            }
        });

        mAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mActivity.startActivity(new Intent(context, UtilActivity.class));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private LinearLayout getLnContentSum(float weight){
        LinearLayout lnContent2 = new LinearLayout(context);
        lnContent2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        lnContent2.setOrientation(LinearLayout.HORIZONTAL);
        lnContent2.setWeightSum(weight);
        return  lnContent2;
    }

    private LinearLayout getLnWeight(float weight){
        LinearLayout ln = new LinearLayout(context);
        ln.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,weight));

        return ln;
    }

    public EditText getmOutEditText() {
        return mOutEditText;
    }

    public LinearLayout getmSendButton() {
        return mSendButton;
    }


    /**
     * Metodo que inicia o para una grabacion.
     * @param start si true signafica que inicia grabacion, de lo contrario la detiene
     */
    private void audioRecording(boolean start){
        if(start){
            try {
                ar.start();
                cnt=0;
                t.start();
            } catch (Exception e) {
                Log.e("Exception in start", "" + e);
            }
        }else{
            try {
                ar.stop();
                t.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

