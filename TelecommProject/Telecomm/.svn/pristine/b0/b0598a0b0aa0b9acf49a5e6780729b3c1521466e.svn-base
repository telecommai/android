package com.efounder.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import io.telecomm.telecomm.R;

import com.efounder.http.EFHttpRequest;
import com.efounder.utils.ResStringUtil;
import com.pansoft.openplanet.manager.OpenPlanetLoginManager;
import com.efounder.util.ToastUtil;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.pansoft.openplanet.activity.CreateWalletActivity;
import com.pansoft.openplanet.activity.RegisterUserActivity;
import com.pansoft.openplanet.activity.TCBaseActivity;
import com.pansoft.openplanet.activity.TabBottomActivityForTalkChain;
import com.pansoft.openplanet.util.FileUtil;
import com.pansoft.openplanet.util.TCRequestUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 注册验证短信
 * Created by will on 18-3-20.
 */

public class VerifyCodeActivity extends TCBaseActivity implements View.OnClickListener, OpenPlanetLoginManager.LoginListener {

    private String TAG = this.getClass().getSimpleName();
    private TextView tvGetCode, tvIntro;
    private EditText etNumber;
    private TimeCount timeCount;
    private String number = "";
    private String planetName = "";
    private String inviteCode;
    private boolean isLogin = false;
    private OpenPlanetLoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableScreenShotListen(false);
        setContentView(R.layout.activity_verify_code);

        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvIntro = (TextView) findViewById(R.id.tv_phone_intro);
        tvGetCode = (TextView) findViewById(R.id.tv_get_code);
        tvGetCode.setOnClickListener(this);
        Button tvNext = (Button) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(this);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().get("canBack") != null) {
                isLogin = true;
                tvNext.setText(R.string.common_text_login);
            }
            inviteCode = getIntent().getExtras().getString("inviteCode");
            number = (String) getIntent().getExtras().get("number");
            planetName = getIntent().getExtras().getString("planetName");
        }
//        tvIntro.setText("通过点击“获取验证码”按钮，向+86 " + number + " 发送短信验证码");
        tvIntro.setText(getResources().getString(R.string.openplanet_input_sms_code_tip,number));

        etNumber = (EditText) findViewById(R.id.et_number);
        timeCount = new TimeCount(60000, 1000);//构造CountDownTimer对象
        loginManager = new OpenPlanetLoginManager(this);
        loginManager.setLoginListener(this);
        //setKeyboardStateListener();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_get_code) {
//            showSoftInputFromWindow(this, etNumber);
            try {
                //请求验证码
                getVerifyCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.tv_next) {
            if (etNumber.getText().toString().equals("")) {
                ToastUtil.showToast(getApplicationContext(), R.string.openplanet_input_sms_code1);
            } else {
                if (isLogin) {
                    //登录 注意了，验证码登录功能已经去掉了，这个方法不会执行的，不用看这里
                    loginManager.login(number, etNumber.getText().toString(), "smsCode");
                } else {
                    //验证验证码（注册）
                    requestVerifyCode();
                }
            }
        } else if (id == R.id.iv_back) {
            finish();
        }
    }

    private void requestVerifyCode() {
        showLoading(R.string.common_text_please_wait);
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", number);
        params.put("code", etNumber.getText().toString());
        TCRequestUtil.getCommonRequest(TAG, "user/verifyPhoneCode", params, new TCRequestUtil.TCRequestCallback() {
            @Override
            public void onSuccess(String response) {
                dismissLoading();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {
                        Intent intent = new Intent(VerifyCodeActivity.this, RegisterUserActivity.class);
                        //邀请码
                        intent.putExtra("inviteCode", inviteCode);
                        //星球
                        intent.putExtra("planet", planetName);
                        intent.putExtra("number", number);
                        startActivity(intent);
                    } else {
                        showFailAlert(ResStringUtil.getString(R.string.openplanet_input_authentication_fail));
                    }
                } catch (JSONException e) {
                    dismissLoading();
                    e.printStackTrace();
                    showFailAlert(ResStringUtil.getString(R.string.openplanet_input_authentication_fail));
                }
            }

            @Override
            public void onFail(String error) {
                dismissLoading();
                showFailAlert(ResStringUtil.getString(R.string.openplanet_input_authentication_fail));
            }
        });
    }

    @Override
    public void tcLoginSuccess() {
        //todo 闫庆松 2018/11/8 星际通讯已经没有短信验证码登陆了，这个方法不会执行的
        if (FileUtil.isHaveWallet(number, this)) {
            Intent intent1 = new Intent(this, TabBottomActivityForTalkChain.class);
            intent1.putExtra("unDoCheck", "true");//从登录界面跳转无需再次检查是否升级
            //intent1.putExtra("listobj", (Serializable) mainMenus);
            this.startActivity(intent1);
            ActivityCompat.finishAffinity(((Activity) this));
        } else {
            Intent intent = new Intent(this, CreateWalletActivity.class);
            intent.putExtra("number", number);
            ((Activity) this).startActivity(intent);
        }
    }

    @Override
    public void showLoginLoading(String msg) {
        showLoading(msg);
    }

    @Override
    public void dismissLoginloading() {
        dismissLoading();
    }

    @Override
    public void tcLoginFail(String error) {
        showFailAlert(error);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            tvGetCode.setText(R.string.openplanet_again_send);
            tvGetCode.setClickable(true);
            tvGetCode.setTextColor(getResources().getColor(R.color.tc_orange_color));

        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            tvGetCode.setClickable(false);
            tvGetCode.setText(ResStringUtil.getString(R.string.openplanet_again_send)+"(" + millisUntilFinished / 1000 + "s)");
            tvGetCode.setTextColor(getResources().getColor(R.color.edit_hint_text_color));
        }
    }

    private void getVerifyCode() {
        showLoading(R.string.common_text_please_wait);
        etNumber.setFocusable(true);
        etNumber.setFocusableInTouchMode(true);
        etNumber.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(1, InputMethodManager.HIDE_NOT_ALWAYS);
        HashMap<String, String> params = new HashMap<>();
        params.put("phoneNumber", number);
        TCRequestUtil.getCommonRequest(TAG, "user/sendVerifyCode2mobilePhone", params, new TCRequestUtil.TCRequestCallback() {
            @Override
            public void onSuccess(String response) {
                dismissLoading();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {
//                        tvIntro.setText("已向+86 " + number + " 发送短信验证码");
                        tvIntro.setText(getResources().getString(R.string.openplanet_has_send_sms_code,number));

                        timeCount.start();
                    } else {
                        ToastUtil.showToast(getApplicationContext(), R.string.openplanet_sendsms_fail);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(getApplicationContext(),  R.string.openplanet_sendsms_fail);
                }
            }

            @Override
            public void onFail(String error) {
                dismissLoading();
                ToastUtil.showToast(getApplicationContext(),  R.string.openplanet_sendsms_fail);
            }
        });
    }

    private void showFailAlert(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.common_text_hint)
                .setMessage(message)
                .setPositiveButton(R.string.common_text_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EFHttpRequest.cancelRequest(TAG);
    }

    //Activity最外层的Layout视图
    private View rootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;
    private Spring spring;
    private SpringChain springChain;
    private ImageView iv_icon;
    private ConstraintLayout constraintLayout;

    /**
     * 添加软键盘状态监听器
     */
    protected void setKeyboardStateListener() {
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintlayout);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 4;
        //弹性动画

        SpringSystem springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        SpringConfig springConfig = new SpringConfig(100, 11);
        spring.setSpringConfig(springConfig);
        springChain = SpringChain.create();
        spring.setCurrentValue(0);
        spring.addListener(new SimpleSpringListener() {

            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                float scale = 1f - (value * 0.5f);
                Log.i("scale", scale + "");
                iv_icon.setScaleX(scale);
                iv_icon.setScaleY(scale);
            }

        });
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

                    Log.i(TAG, "onLayoutChange: 监听到软键盘弹起...");

                    spring.setEndValue(0.5);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationY", 130.0f);
                    animator.setDuration(200);
                    animator.start();
                    //按钮上移
//                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvNext, "translationY", -100.0f);
//                    animator1.setDuration(200);
//                    animator1.start();

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    Log.i(TAG, "onLayoutChange: 监听到软件盘关闭......");

                    spring.setEndValue(0);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationY", -0f);
                    animator.setDuration(200);
                    animator.start();
                    //按钮下移
//                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvNext, "translationY", 0f);
//                    animator1.setDuration(200);
//                    animator1.start();


                }
            }
        });
    }
}
