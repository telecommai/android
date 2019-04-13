package com.efounder.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.efounder.http.EFHttpRequest;
import io.telecomm.telecomm.R;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.pansoft.openplanet.activity.TCBaseActivity;
import com.pansoft.openplanet.util.TCRequestUtil;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * 输入邀请码界面
 * Created by will on 18-3-23.
 */
@Deprecated
public class VerifyInviteCodeActivity extends TCBaseActivity implements View.OnClickListener {

    private String TAG = this.getClass().getSimpleName();
    private Button tvLogin;
    private String number = "";
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_invite_code);
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            number = (String) getIntent().getExtras().get("number");
        }
        etPassword = (EditText) findViewById(R.id.et_password);

        tvLogin = (Button) findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        // setKeyboardStateListener();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        } else if (id == R.id.tv_login) {
            if (!etPassword.getText().toString().equals("")) {
                showLoading("正在校验邀请码");
                HashMap<String, String> params = new HashMap<>();
                params.put("inviteCode", etPassword.getText().toString());
                TCRequestUtil.getCommonRequest(TAG, "user/verifyInviteCodeAvailabilityCount", params, new TCRequestUtil.TCRequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        dismissLoading();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("result") && jsonObject.getString("result").equals("success")) {
                                if (jsonObject.getInt("inviteCodeAvailabilityCount") == -1) {
                                    showFailAlert("邀请码不存在");
                                } else if (jsonObject.getInt("inviteCodeAvailabilityCount") == 0) {
                                    showFailAlert("邀请码已达到最大使用上限");
                                } else {
//                                    new AlertDialog.Builder(VerifyInviteCodeActivity.this)
//                                            .setTitle(R.string.common_text_hint)
//                                            .setMessage("验证成功，邀请码剩余" + jsonObject.getInt("inviteCodeAvailabilityCount") + "次")
//                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                }
//                                            })
//                                            .create().show();
                                    Intent intent = new Intent(VerifyInviteCodeActivity.this, PlanetChooseActivity.class);
                                    intent.putExtra("inviteCode", etPassword.getText().toString());
                                    intent.putExtra("number", number);
                                    startActivity(intent);
                                }
                            } else {
                                dismissLoading();
                                if (jsonObject.getString("msg") != null) {
                                    showFailAlert(jsonObject.getString("msg"));
                                    return;
                                }
                                showFailAlert("邀请码错误");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            dismissLoading();
                            showFailAlert("网络错误");
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        dismissLoading();
                        showFailAlert("网络错误");
                    }
                });
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login_withTitle.class);
        intent.putExtra("number", number);
        startActivity(intent);
        finish();
    }

    private void showFailAlert(String message) {
        new AlertDialog.Builder(VerifyInviteCodeActivity.this)
                .setTitle(R.string.common_text_hint)
                .setMessage(message)
                .setPositiveButton("确定", null)
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
//                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvLogin, "translationY", -100.0f);
//                    animator1.setDuration(200);
//                    animator1.start();

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                    Log.i(TAG, "onLayoutChange: 监听到软件盘关闭......");

                    spring.setEndValue(0);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationY", -0f);
                    animator.setDuration(200);
                    animator.start();
                    //按钮下移
//                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvLogin, "translationY", 0f);
//                    animator1.setDuration(200);
//                    animator1.start();


                }
            }
        });
    }
}
