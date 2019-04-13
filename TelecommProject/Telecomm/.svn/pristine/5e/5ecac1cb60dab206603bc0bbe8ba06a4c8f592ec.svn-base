package com.efounder.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.efounder.constant.EnvironmentVariable;
import io.telecomm.telecomm.R;
import com.efounder.util.AbStrUtil;
import com.efounder.util.StorageUtil;
import com.efounder.util.ToastUtil;
import com.efounder.utils.JfResourceUtil;
import com.efounder.utils.ResStringUtil;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.pansoft.openplanet.activity.TCBaseActivity;
import com.pansoft.openplanet.util.TCAccountManager;
import com.pansoft.openplanet.util.TCRequestUtil;
import com.pansoft.openplanet.widget.TCClearEditText;
import com.utilcode.util.RegexUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author zhenglaikun 2014.11.26 登陆界面 初始化设置信息 后台连接 250,447行 调试处
 */
public class Login_withTitle extends TCBaseActivity implements OnClickListener {

    private static final String TAG = "Login_withTitle";

    private Button tvNext;
    private TCClearEditText etNumber;
    private TextView tvLoginChange;
    private StorageUtil storageUtil;
    private ImageView iv_icon;
    private ConstraintLayout constraintLayout;

    private Spring spring;
    private SpringChain springChain;
    //1手机号登录 2 邮箱
    private int loginType = 1;

    private TextView tvPhoneZone;
    private View viewDivider;
    private TextView tvPhoneIntro;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 自定义标题栏，requestWindowFeature必须在setContent之前
        setEnableScreenShotListen(false);
        setContentView(R.layout.activity_login);
        etNumber = (TCClearEditText) findViewById(R.id.et_number);
        etNumber.setTextColor(JfResourceUtil.getSkinColor(R.color.wechat_white_or_black));
        tvNext = (Button) findViewById(R.id.tv_next);
        tvNext.setOnClickListener(this);
        etNumber.setButton(tvNext);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        tvLoginChange = (TextView) findViewById(R.id.tv_login_change);
        tvLoginChange.setOnClickListener(this);
        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintlayout);
        tvPhoneZone = (TextView) findViewById(R.id.tv_phone_zone);
        viewDivider = (View) findViewById(R.id.view_divider);


        tvPhoneIntro = (TextView) findViewById(R.id.tv_phone_intro);

        if (!"".equals(EnvironmentVariable.getUserName())) {
//            if (RegexUtils.isEmail(EnvironmentVariable.getUserName())) {
//                changeEmailLogin();
//            } else {
//                changePhoneLogin();
//            }
            etNumber.setText(EnvironmentVariable.getUserName());
            etNumber.setSelection(etNumber.getText().toString().length());
        }


        SpringSystem springSystem = SpringSystem.create();
        spring = springSystem.createSpring();
        SpringConfig springConfig = new SpringConfig(100, 11);
        spring.setSpringConfig(springConfig);
        springChain = SpringChain.create();

        storageUtil = new StorageUtil(getApplicationContext(), "storage");
        //setKeyboardStateListener();

        storageUtil.putBoolean("isSave", true);

        //intent中含有手机号
        if (getIntent() != null && getIntent().hasExtra("number")) {
//            if (RegexUtils.isEmail(getIntent().getStringExtra("number"))) {
//                changeEmailLogin();
//            } else {
//                changePhoneLogin();
//            }
            etNumber.setText(getIntent().getStringExtra("number"));
            etNumber.setSelection(etNumber.getText().toString().length());
        }
        TCAccountManager.release();

    }


    /*
     * 设置标题返回键，登陆按钮监听事件
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.tv_next) {

            if(AbStrUtil.isMobileNo(etNumber.getText().toString())) {
                loginType = 1;
            }
            if(RegexUtils.isEmail(etNumber.getText().toString())){
                loginType = 2;
            }
            if (loginType == 1 && (etNumber.getText().toString().equals("") ||
                    !AbStrUtil.isMobileNo(etNumber.getText().toString()))) {
                ToastUtil.showToast(getApplicationContext(), R.string.openplanet_please_input_correct_account);
                return;
            }
            if (loginType == 2 && (etNumber.getText().toString().equals("") ||
                    !RegexUtils.isEmail(etNumber.getText().toString()))) {
                ToastUtil.showToast(getApplicationContext(), R.string.openplanet_please_input_correct_account);
                return;
            }
            showLoading(R.string.common_text_loading);
            HashMap<String, String> params = new HashMap<>();
            params.put("phoneNumber", etNumber.getText().toString());
            TCRequestUtil.getCommonRequest(TAG, "user/login", params, new TCRequestUtil.TCRequestCallback() {
                @Override
                public void onSuccess(String response) {
                    dismissLoading();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equals("success")) {

                            //未注册，邀请码登录
//                                if (jsonObject.optString("userStatus", "").equals("0")) {
//                                    Intent intent = new Intent(Login_withTitle.this, VerifyInviteCodeActivity.class);
//                                    intent.putExtra("number", etNumber.getText().toString());
//                                    EnvironmentVariable.setProperty("old_user", "0");
//                                    startActivity(intent);
//                                    finish();
//                                    //已注册，跳密码登录
//                                } else
                            if (jsonObject.optString("userStatus", "").equals("1")
                                    ) {
                                Intent intent = new Intent(Login_withTitle.this, OpenLoginWithPassword.class);
                                intent.putExtra("canBack", true);
                                intent.putExtra("number", etNumber.getText().toString());
                                EnvironmentVariable.setProperty("old_user", "1");
                                startActivity(intent);
                                finish();
                            } else if (jsonObject.optString("userStatus", "").equals("-1")
                                    || jsonObject.optString("userStatus", "").equals("0")) {
                                //未注册，跳过邀请码，直接验证短信码
                                //手机号注册和邮箱注册区分
                                Intent intent = new Intent(Login_withTitle.this,
                                        PlanetChooseActivity.class);
                                intent.putExtra("isPhone", loginType == 1);
                                intent.putExtra("number", etNumber.getText().toString());
                                EnvironmentVariable.setProperty("old_user", "-1");
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            showFailAlert(ResStringUtil.getString(R.string.common_text_http_request_fail));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        dismissLoading();
                        showFailAlert(ResStringUtil.getString(R.string.common_text_http_request_fail));
                    }
                }

                @Override
                public void onFail(String error) {
                    dismissLoading();
                    showFailAlert(ResStringUtil.getString(R.string.common_text_http_request_fail));
                }
            });

        } else if (id == R.id.tv_login_change) {
            if (loginType == 1) {
                changeEmailLogin();
            } else {
                changePhoneLogin();

            }
        }
    }

    private void changePhoneLogin() {
        loginType = 1;
        tvLoginChange.setText(R.string.op_login_email);
        etNumber.setHint(R.string.common_text_please_input_number);
        etNumber.setText("");
        tvPhoneZone.setVisibility(View.VISIBLE);
        viewDivider.setVisibility(View.VISIBLE);
        tvPhoneIntro.setText(R.string.openplanet_welcome_use1);
    }

    private void changeEmailLogin() {
        loginType = 2;
        tvLoginChange.setText(R.string.op_login_phone);
        etNumber.setHint(R.string.common_text_please_input_email);
        etNumber.setText("");
        tvPhoneZone.setVisibility(View.GONE);
        viewDivider.setVisibility(View.GONE);
        tvPhoneIntro.setText(R.string.openplanet_welcome_use_email);
    }


    private void showFailAlert(String message) {
        if (isFinishing()||isDestroyed()){
            return;
        }
        new android.app.AlertDialog.Builder(this)
                .setTitle(R.string.common_text_hint)
                .setMessage(message)
                .setPositiveButton(R.string.common_text_confirm, null)
                .create().show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // EFHttpRequest.cancelRequest(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //Activity最外层的Layout视图
    private View rootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    /**
     * 添加软键盘状态监听器
     */
    protected void setKeyboardStateListener() {

        rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 4;
        //弹性动画
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
