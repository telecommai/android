package com.efounder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efounder.chat.model.Constant;
import com.efounder.constant.EnvironmentVariable;
import com.efounder.frame.language.MultiLanguageUtil;
import io.telecomm.telecomm.R;
import com.efounder.util.FileDeleteUtil;
import com.efounder.util.LogOutUtil;
import com.efounder.util.PackgeFileCheckUtil;
import com.efounder.utils.EasyPermissionUtils;
import com.efounder.utils.ResStringUtil;
import com.pansoft.openplanet.activity.TabBottomActivityForTalkChain;
import com.pansoft.openplanet.bean.Account;
import com.pansoft.openplanet.constant.TalkChainConstant;
import com.pansoft.openplanet.db.AccountDao;
import com.pansoft.openplanet.util.CompatibleOldVersionUtil;
import com.pansoft.openplanet.util.FileUtil;
import com.utilcode.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.efounder.frame.utils.Constants.CHAT_PASSWORD;
import static com.efounder.frame.utils.Constants.CHAT_USER_ID;

//import com.efounder.deviceadmin.PansoftDeviceAdminManager;
//import com.efounder.deviceadmin.PansoftDeviceAdminReceiver;

/**
 * Created by densakai on 17/3/1.
 */

public class Welcome1Activity extends Activity implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final String TAG = "Welcome1Activity";

    private ViewPager mViewPager;

    //    private List<Bitmap> pics;
    private int[] pics = new int[]{R.drawable.lunch_background1};

    private List<View> views;

    // 底部小点的图片
    private LinearLayout llPoint;

    //进入按钮
    private TextView textView;

    private int CurrIndex = 0;

    //微信聊天id 和password
    private String chatUserID;
    private String chatPassword;

    private ImageView lauchImageView;
    private String imagePath;
    Animation animation;

    //倒计时
    private LinearLayout ll_timer;
    private TextView time;

    private Timer timer;
    private int losttime = 3;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.setLocal(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = Welcome1Activity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
//        PansoftDeviceAdminManager.initDPM(this);
//        PansoftDeviceAdminManager.initComponent(this, PansoftDeviceAdminReceiver.class);
//        if (!PansoftDeviceAdminManager.isActiveAdmin()) {
//            PansoftDeviceAdminManager.EnableDeviceAdmin(this);
//        }else {
        Future_Start();
//        }

    }

    private void Future_Start() {

        setContentView(R.layout.activity_welcome);


        mViewPager = findViewById(R.id.viewpager);
        llPoint = findViewById(R.id.points);

        textView = findViewById(R.id.tv_gotoapp);

        ll_timer = findViewById(R.id.timer);
        time = findViewById(R.id.time);

//        InitPicture();
        InitView();
        addPoint();


        String userID = EnvironmentVariable.getUserID();
        String userNumber = EnvironmentVariable.getUserName();
//        if (userNumber != null && FileUtil.isHaveWallet(userNumber, Welcome1Activity.this)) {
//            if (null != userID && !"".equals(userID)) {
//                startService(new Intent(this, MessageService.class));
//                if (!ServiceUtils.isServiceRunning(getApplicationContext(), SystemInfoService.class.getCanonicalName())) {
//                    startService(new Intent(this, SystemInfoService.class));
//                }
//                if (!ServiceUtils.isServiceRunning(getApplicationContext(), OSPService.class.getCanonicalName())) {
//                    startService(new Intent(this, OSPService.class));
//                }
//            }
//        }


        timer = new Timer();
        timer.schedule(tt, 1000, 1000);
    }


    TimerTask tt = new TimerTask() {
        @Override
        public void run() {
            myhandler.sendEmptyMessage(0);
        }
    };

    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Future_Start();
            }
            if (losttime > 1) {
                losttime--;
                time.setText("" + losttime);
            } else {
                timer.cancel();
                checkPermissionOrGoToNext();
            }
            super.handleMessage(msg);
        }
    };

    private void InitView() {
        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            views.add(iv);
        }

        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(View arg0, int arg1) {

                ((ViewPager) arg0).addView(views.get(arg1), 0);

                return views.get(arg1);
            }

            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(views.get(arg1));
            }

        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (ll_timer.getVisibility() == View.VISIBLE) {
                    timer.cancel();
                    ll_timer.setVisibility(View.GONE);
                }

                for (int i = 0; i < pics.length; i++) {
                    if (i == position) {
                        llPoint.getChildAt(position).setBackgroundResource(R.drawable.point_select);
                    } else {
                        llPoint.getChildAt(i).setBackgroundResource(R.drawable.point_normal);
                    }
                }

                CurrIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                checkPermissionOrGoToNext();
            }
        });
    }


    private void InToApp() {
        chatUserID = EnvironmentVariable.getProperty(CHAT_USER_ID);
        chatPassword = EnvironmentVariable.getProperty(CHAT_PASSWORD);
        String pingtaiPassword = EnvironmentVariable.getPassword();
        final String userNumber = EnvironmentVariable.getUserName();

        //TODO 这里要判断本地是否存在本地权限，如果没有，扔要跳转
        //如果第一次登陆，需要跳转登陆界面；如果需要升级资源文件，需要跳转登陆界面
        // 判断espmobile路径是否存在,下面会用到
        String path = Constant.appSdcardLocation;
        File ESPMobileForWeChat = new File(path);
        //配置文件是否完整
        boolean isFileFull = PackgeFileCheckUtil.checkFileIsFull(this);
        //如果没有基本的配置文件
        boolean isNeedUpdateRES = ESPMobileForWeChat.exists();
        if (null == chatUserID || "".equals(chatUserID) || !isFileFull
                || !isNeedUpdateRES || null == chatPassword || "".equals(chatPassword)
                || null == pingtaiPassword || "".equals(pingtaiPassword)
                || null == userNumber || "".equals(userNumber)) {
            LogOutUtil.clearLoginInfo();
            Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
            startActivity(intent);
            Welcome1Activity.this.finish();
        } else {
            Account mainAccount = new AccountDao(Welcome1Activity.this).getMainAccount();
            //判断本地是否已有主账户基地账户，如果没有，不允许进入,跳入登录界面
            //或者mainaccount=null  未走完创建流程 或者未创建
            if (!FileUtil.isHaveWallet(userNumber, Welcome1Activity.this)
                    || mainAccount == null) {
                EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                //EnvironmentVariable.setUserName("");
                EnvironmentVariable.setPassword("");
                Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
                startActivity(intent);
                Welcome1Activity.this.finish();
            } else {
                //判断用户是否走完备份助记词的流程，如果没有 清空用户 已生成的钱包，让用户重新登录
                // String isBackUpWord = EnvironmentVariable.getProperty(userNumber + "backUpMnemonic", "false");
//                if ("false".equals(isBackUpWord)) {
//                    //没有备份助记词
//                    EnvironmentVariable.setProperty(CHAT_USER_ID, "");
//                    EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
//                    EnvironmentVariable.setUserName("");
//                    EnvironmentVariable.setPassword("");
//                    Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
//                    startActivity(intent);
//                    Welcome1Activity.this.finish();
//
//                    return;
//                }


//                //判断是否正在走创建或者恢复钱包的状态
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getCreateWalletStateKey(userNumber), ""))) {
//                    Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
//                    //intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    Welcome1Activity.this.finish();
//                    return;
//                }
//
//                //如果在注册流程 并且没有设置头像
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsRegisterKey(userNumber), ""))
//                        && "true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsUpdateAvatarKey(userNumber), ""))) {
//                    Intent intent = new Intent(Welcome1Activity.this, SetupAvatarActivity.class);
//                    intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    Welcome1Activity.this.finish();
//                    return;
//                }
//                //如果在注册流程 并且没有导入联系人
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsRegisterKey(userNumber), ""))
//                        && "true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsImportFansKey(userNumber), ""))) {
//                    Intent intent = new Intent(Welcome1Activity.this, ImportFansActivity.class);
//                    intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    Welcome1Activity.this.finish();
//                    return;
//                }

                //本地有钱包，但是env的 tc_ethAddress 没有值 说明没有存储到服务器，需要跳转登录 跳转登录
                if ("".equals(EnvironmentVariable.getProperty("tc_ethAddress", ""))) {
                    //删除主账户的目录
                    File dirFile = new File(TalkChainConstant.getDataFilePath(this) + File.separator + userNumber);
                    FileDeleteUtil.delete(dirFile);
                    //从数据库中移除
                    new AccountDao(Welcome1Activity.this).deleteMainAccount();
                    EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                    EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                    // EnvironmentVariable.setUserName("");
                    EnvironmentVariable.setPassword("");
                    Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
                    startActivity(intent);
                    Welcome1Activity.this.finish();
                    return;
                }


                try {
                    if (!CompatibleOldVersionUtil.
                            compareLocalFileEthAddressWithServer(this, EnvironmentVariable
                                    .getProperty("tc_ethAddress", ""))) {
                        //如果本地有钱包，但是地址跟服务器比对不相符，需要提示跳转登录
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.common_text_hint)
                                .setCancelable(false)
                                .setMessage(ResStringUtil.getString(R.string.guide_dialog__check_baseid_error_hint))
                                .setPositiveButton(R.string.common_text_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AccountDao(Welcome1Activity.this).deleteByCurentImuserId();
                                        File dirFile = new File(TalkChainConstant
                                                .getDataFilePath(Welcome1Activity.this) + File.separator + userNumber);
                                        FileDeleteUtil.delete(dirFile);
                                        new AccountDao(Welcome1Activity.this).deleteMainAccount();

                                        EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                                        EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                                        // EnvironmentVariable.setUserName("");
                                        EnvironmentVariable.setPassword("");

                                        Intent intent = new Intent(Welcome1Activity.this, Login_withTitle.class);
                                        startActivity(intent);
                                        Welcome1Activity.this.finish();
                                    }
                                })
                                .create()
                                .show();

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.setClass(Welcome1Activity.this, TabBottomActivityForTalkChain.class);
                startActivity(intent);
                Welcome1Activity.this.finish();

//                CompatibleOldVersionUtil.checkLocalInfoIsFull(Welcome1Activity.this, new CompatibleOldVersionUtil.CheckListener() {
//                    @Override
//                    public void checkSuccess() {
//                        Intent intent = new Intent();
//                        intent.setClass(Welcome1Activity.this, TabBottomActivityForTalkChain.class);
//                        startActivity(intent);
//                        Welcome1Activity.this.finish();
//                    }
//
//                    @Override
//                    public void checkFail() {
//                        ToastUtil.showToast(Welcome1Activity.this, "数据初始化失败，请关闭并重新打开应用");
//                    }
//                });

            }
        }
    }


    private void addPoint() {
        // 1.根据图片多少，添加多少小圆点
        for (int i = 0; i < pics.length; i++) {
            LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(dip2px(30), dip2px(30));
            if (i < 1) {
                pointParams.setMargins(0, 0, 0, 0);
            } else {
                pointParams.setMargins(dip2px(10), 0, 0, 0);
            }
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(pointParams);
            iv.setBackgroundResource(R.drawable.point_normal);
            llPoint.addView(iv);
        }

        llPoint.getChildAt(0).setBackgroundResource(R.drawable.point_select);
    }

    public int dip2px(float dpValue) {
        final float scale = Welcome1Activity.this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @AfterPermissionGranted(EasyPermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE)//请求码
    private void checkPermissionOrGoToNext() {
        if (EasyPermissionUtils.checkWriteAndPhonePermission()) {
            InToApp();
        } else {
            EasyPermissionUtils.requestStorageAndPhoneStatePermission(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRationale(R.string.permission_write_stroge_phone_state).build().show();
        } else {
            ToastUtils.showShort(ResStringUtil.getString(R.string.open_planet_welcome_permission_denied));
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
    }

    @Override
    public void onRationaleDenied(int requestCode) {
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
//
//            case PansoftDeviceAdminManager.REQUEST_CODE_ENABLE_ADMIN:
//                if (resultCode == RESULT_CANCELED) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(Welcome1Activity.this);
//                    builder.setMessage("为了更好地保护您的数据安全，请激活管理员权限");
//                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            PansoftDeviceAdminManager.EnableDeviceAdmin(Welcome1Activity.this);
//                        }
//                    });
//                    builder.setCancelable(false);
//                    builder.show();
//                }else if (resultCode == RESULT_OK){
//                    myhandler.sendEmptyMessage(1);
//                }
//            break;
//            default:
//                break;
//        }
//    }
}
