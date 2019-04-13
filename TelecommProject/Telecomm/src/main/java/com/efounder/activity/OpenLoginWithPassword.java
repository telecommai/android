package com.efounder.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.efounder.constant.EnvironmentVariable;
import io.telecomm.telecomm.R;
import com.efounder.util.ToastUtil;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringChain;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.pansoft.openplanet.activity.CreateWalletActivity;
import com.pansoft.openplanet.activity.ResetPasswordActivity;
import com.pansoft.openplanet.activity.TCBaseActivity;
import com.pansoft.openplanet.activity.TabBottomActivityForTalkChain;
import com.pansoft.openplanet.bean.Account;
import com.pansoft.openplanet.db.AccountDao;
import com.pansoft.openplanet.manager.OpenPlanetLoginManager;
import com.pansoft.openplanet.util.FileUtil;
import com.pansoft.openplanet.util.TCAccountManager;
import com.utilcode.util.AppUtils;

import static com.efounder.frame.utils.Constants.CHAT_USER_ID;

/**
 * 密码登录
 * Created by will on 18-3-20.
 */

public class OpenLoginWithPassword extends TCBaseActivity implements View.OnClickListener
        , OpenPlanetLoginManager.LoginListener {

    private String TAG = this.getClass().getSimpleName();
    private Button tvLogin;
    private String number = "";
    private EditText etPassword;
    private OpenPlanetLoginManager loginManager;
    private TextView tvPhoneZone;
    private View viewDivider;
    private TextView tvNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnableScreenShotListen(false);
        setContentView(R.layout.activity_password_login);


        loginManager = new OpenPlanetLoginManager(this);
        loginManager.setLoginListener(this);

        initView();
        initData();
        //setKeyboardStateListener();

    }

    private void initView() {
        ImageView ivBack = (ImageView) findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        etPassword = (EditText) findViewById(R.id.et_password);
        TextView tvSmsLogin = (TextView) findViewById(R.id.tv_phone_intro);
        tvSmsLogin.setOnClickListener(this);
        TextView forgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        forgetPassword.setOnClickListener(this);
        tvLogin = (Button) findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        tvPhoneZone = (TextView) findViewById(R.id.tv_phone_zone);
        viewDivider = (View) findViewById(R.id.view_divider);
    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            number = (String) getIntent().getExtras().get("number");
        }
        tvNumber.setText(number);
//        if (RegexUtils.isEmail(number)) {
            tvPhoneZone.setVisibility(View.GONE);
            viewDivider.setVisibility(View.GONE);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back) {
            onBackPressed();
        }
        // //短信验证码登录已经去掉了
//        else if (id == R.id.tv_phone_intro) {
//            Intent intent = new Intent(this, VerifyCodeActivity.class);
//            intent.putExtra("canBack", true);
//            intent.putExtra("number", number);
//            startActivity(intent);
//        }

        else if (id == R.id.tv_login) {
            loginRequest();
        } else if (id == R.id.tv_forget_password) {
            //重置密码
            ResetPasswordActivity.start(this, number, false);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login_withTitle.class);
        intent.putExtra("number", number);
        startActivity(intent);
        finish();
    }

    private void loginRequest() {
        if (etPassword.getText() == null) {
            ToastUtil.showToast(getApplicationContext(), R.string.common_text_please_inputpwd);
            return;
        }
        if (etPassword.getText().toString().trim().equals("")) {
            ToastUtil.showToast(getApplicationContext(), R.string.common_text_please_inputpwd);
            return;
        }
        loginManager.login(number, etPassword.getText().toString(), "password");
    }


    @Override
    public void tcLoginSuccess() {

        String address = EnvironmentVariable.getProperty("tc_ethAddress", "");
        if ("".equals(address)) {
            Intent intent = new Intent(this, CreateWalletActivity.class);
            intent.putExtra("number", number);
            ((Activity) this).startActivity(intent);
            return;
        }


        if (FileUtil.isHaveWallet(number, this)) {

//            List<WalletBean> locaLbeans = CompatibleOldVersionUtil.getWallet(this);
//            if (locaLbeans == null) {
//                Intent intent = new Intent(this, CreateWalletActivity.class);
//                intent.putExtra("number", number);
//                ((Activity) this).startActivity(intent);
//                return;
//            }
//            if (locaLbeans.size() == 0) {
//                Intent intent = new Intent(this, CreateWalletActivity.class);
//                intent.putExtra("number", number);
//                ((Activity) this).startActivity(intent);
//                return;
//            }
            Account account = new AccountDao(this).getMainAccount();
            if (account == null) {
                Intent intent = new Intent(this, CreateWalletActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("loginPwd", etPassword.getText().toString());
                ((Activity) this).startActivity(intent);
                return;
            }

            if (!account.getAddress().equalsIgnoreCase(EnvironmentVariable.getProperty("tc_ethAddress", ""))) {
                Intent intent = new Intent(this, CreateWalletActivity.class);
                intent.putExtra("number", number);
                intent.putExtra("localAddress", account.getAddress());
                intent.putExtra("loginPwd", etPassword.getText().toString());
                ((Activity) this).startActivity(intent);
                return;
            }

//            if (!locaLbeans.get(0).getAddress().toLowerCase().equals(EnvironmentVariable.getProperty("tc_ethAddress","").toLowerCase())) {
//                Intent intent = new Intent(this, CreateWalletActivity.class);
//                intent.putExtra("number", number);
//                intent.putExtra("localAddress", locaLbeans.get(0).getAddress());
//                ((Activity) this).startActivity(intent);
//                return;
//            }
            //跳转首页
            gotoMainPage();

        } else {
            Intent intent = new Intent(this, CreateWalletActivity.class);
            intent.putExtra("number", number);
            intent.putExtra("loginPwd", etPassword.getText().toString());
            ((Activity) this).startActivity(intent);
        }
    }

    private void gotoMainPage() {
        String versionName = AppUtils.getAppVersionName();
        if (!"1.0.63".equals(versionName)) {
            //检查一下主账户，避免出错
            Account mainAccount = new AccountDao(this).getMainAccount();
            if (mainAccount == null) {
                mainAccount = new AccountDao(this).getAccount(EnvironmentVariable.getProperty("tc_ethAddress"));
                if (mainAccount != null) {
                    mainAccount.setMaster(true);
                    mainAccount.setEnable(true);
                    mainAccount.setImUserId(EnvironmentVariable.getProperty(CHAT_USER_ID));
                    new AccountDao(this).insertOrReplace(mainAccount);
                }
            }
            startMain();
        } else {
            showLoading(R.string.openplanet_is_sync_wallet);
            TCAccountManager tcAccountManager = new TCAccountManager(this);
            tcAccountManager.restoreChildWallet(EnvironmentVariable.getProperty("tc_ethAddress", ""), new TCAccountManager.AccountManagerListener() {
                @Override
                public void onSuccess(Object object) {
                    dismissLoading();
                    ToastUtil.showToast(OpenLoginWithPassword.this, R.string.common_text_sync_success);
                    startMain();
                }

                @Override
                public void onFail(String fail) {
                    dismissLoading();
                    ToastUtil.showToast(OpenLoginWithPassword.this, R.string.common_text_sync_fail);
                    startMain();
                }
            });
        }

    }

    private void startMain() {
        Intent intent1 = new Intent(this, TabBottomActivityForTalkChain.class);
        intent1.putExtra("unDoCheck", "true");//从登录界面跳转无需再次检查是否升级
        //intent1.putExtra("listobj", (Serializable) mainMenus);
        this.startActivity(intent1);
        ActivityCompat.finishAffinity(((Activity) this));
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

    private void showFailAlert(String message) {
        new AlertDialog.Builder(OpenLoginWithPassword.this)
                .setTitle(R.string.common_text_hint)
                .setMessage(message)
                .setPositiveButton(R.string.common_text_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create().show();
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

//                    Log.i(TAG, "onLayoutChange: 监听到软键盘弹起...");

                    spring.setEndValue(0.5);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationY", 130.0f);
                    animator.setDuration(200);
                    animator.start();
                    //按钮上移
//                    ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvLogin, "translationY", -30.0f);
//                    animator1.setDuration(200);
//                    animator1.start();

                } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
//                    Log.i(TAG, "onLayoutChange: 监听到软件盘关闭......");

                    spring.setEndValue(0);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(constraintLayout, "translationY", 0f);
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
