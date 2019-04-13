package com.efounder.presenter;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.efounder.activity.GuideActivity;
import com.efounder.activity.Login_withTitle;
import com.efounder.chat.model.Constant;
import com.efounder.constant.EnvironmentVariable;
import io.telecomm.telecomm.R;
import com.efounder.util.FileDeleteUtil;
import com.efounder.util.LogOutUtil;
import com.efounder.util.PackgeFileCheckUtil;
import com.pansoft.openplanet.activity.TabBottomActivityForTalkChain;
import com.pansoft.openplanet.bean.Account;
import com.pansoft.openplanet.constant.TalkChainConstant;
import com.pansoft.openplanet.db.AccountDao;
import com.pansoft.openplanet.mvp.base.BasePresenterImpl;
import com.pansoft.openplanet.util.CompatibleOldVersionUtil;
import com.pansoft.openplanet.util.FileUtil;
import com.pansoft.openplanet.widget.commonviewpage.LaunchBean;
import com.utilcode.util.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.efounder.frame.utils.Constants.CHAT_PASSWORD;
import static com.efounder.frame.utils.Constants.CHAT_USER_ID;

/**
 * @author : zzj
 * @e-mail : zhangzhijun@pansoft.com
 * @date : 2018/10/1610:34
 * @desc : 引导页的presenter
 * @version: 1.0
 */
public class GuidePresenter extends BasePresenterImpl<GuideActivity> {

    private String chatUserID;
    private String chatPassword;
    private String userNumber;

    public GuidePresenter(GuideActivity view) {
        super(view);
    }

    /**
     * 加载引导页的图片和数据列表
     * @return
     */
    public List<LaunchBean> loadGuideDataList() {
        List<LaunchBean> launchBeans = new ArrayList<>();
        launchBeans.add(new LaunchBean(view.getResources().getString(R.string.guide_tv_quanlian)
                , R.drawable.launch01_1, R.drawable.launch01_2));
        launchBeans.add(new LaunchBean(view.getResources().getString(R.string.guide_tv_all_platform)
                , R.drawable.launch02_1, R.drawable.launch02_2));
        launchBeans.add(new LaunchBean(view.getResources().getString(R.string.guide_tv_mining)
                , R.drawable.launch03_1, R.drawable.launch03_2));
        launchBeans.add(new LaunchBean(view.getResources().getString(R.string.guide_tv_encrypted_communication)
                , R.drawable.launch04_1, R.drawable.launch04_2));
        launchBeans.add(new LaunchBean(view.getResources().getString(R.string.guide_tv_digital_asset)
                , R.drawable.launch05_1, R.drawable.launch05_2));
        return launchBeans;
    }

    /**
     * 判断是否登录
     * @return
     */
    public boolean isLogin() {
        chatUserID = EnvironmentVariable.getProperty(CHAT_USER_ID);
        chatPassword = EnvironmentVariable.getProperty(CHAT_PASSWORD);
        String pingtaiPassword = EnvironmentVariable.getPassword();
        userNumber = EnvironmentVariable.getUserName();
        String path = Constant.appSdcardLocation;
        File ESPMobileForWeChat = new File(path);
        //配置文件是否完整
        boolean isFileFull = PackgeFileCheckUtil.checkFileIsFull(view);
        //如果没有基本的配置文件
        boolean isNeedUpdateRES = ESPMobileForWeChat.exists();
        if (null == chatUserID || "".equals(chatUserID) || !isFileFull
                || !isNeedUpdateRES || null == chatPassword || "".equals(chatPassword)
                || null == pingtaiPassword || "".equals(pingtaiPassword)
                || null == userNumber || "".equals(userNumber)) {
            LogOutUtil.clearLoginInfo();
            return false;
        }
        return true;
    }

    /**
     * 进入app执行的方法
     */
    public void inToApp() {
        EnvironmentVariable.setProperty("app_version_for_launch", AppUtils.getAppVersionName());
        //如果第一次登陆，需要跳转登陆界面；如果需要升级资源文件，需要跳转登陆界面
        // 判断espmobile路径是否存在,下面会用到
        if (!isLogin()) {
            view.startIntent(Login_withTitle.class);
        } else {
            Account mainAccount = new AccountDao(view).getMainAccount();
            //判断本地是否已有主账户基地账户，如果没有，不允许进入,跳入登录界面
            //或者mainaccount=null  未走完创建流程 或者未创建
            if (!FileUtil.isHaveWallet(userNumber, view)
                    || mainAccount == null) {
                EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                //EnvironmentVariable.setUserName("");
                EnvironmentVariable.setPassword("");
                view.startIntent(Login_withTitle.class);
            } else {
                //判断用户是否走完备份助记词的流程，如果没有 清空用户 已生成的钱包，让用户重新登录
                // String isBackUpWord = EnvironmentVariable.getProperty(userNumber + "backUpMnemonic", "false");
//                if ("false".equals(isBackUpWord)) {
//                    //没有备份助记词
//                    EnvironmentVariable.setProperty(CHAT_USER_ID, "");
//                    EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
//                    EnvironmentVariable.setUserName("");
//                    EnvironmentVariable.setPassword("");
//                    Intent intent = new Intent(GuideActivity.this, Login_withTitle.class);
//                    startActivity(intent);
//                    GuideActivity.this.finish();
//
//                    return;
//                }
//                //判断是否正在走创建或者恢复钱包的状态
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getCreateWalletStateKey(userNumber), ""))) {
//                    Intent intent = new Intent(GuideActivity.this, Login_withTitle.class);
//                    //intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    GuideActivity.this.finish();
//                    return;
//                }
//
//                //如果在注册流程 并且没有设置头像
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsRegisterKey(userNumber), ""))
//                        && "true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsUpdateAvatarKey(userNumber), ""))) {
//                    Intent intent = new Intent(GuideActivity.this, SetupAvatarActivity.class);
//                    intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    GuideActivity.this.finish();
//                    return;
//                }
//                //如果在注册流程 并且没有导入联系人
//                if ("true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsRegisterKey(userNumber), ""))
//                        && "true".equals(EnvironmentVariable.getProperty(LoginFlowManager.getIsImportFansKey(userNumber), ""))) {
//                    Intent intent = new Intent(GuideActivity.this, ImportFansActivity.class);
//                    intent.putExtra("number", userNumber);
//                    startActivity(intent);
//                    GuideActivity.this.finish();
//                    return;
//                }

                //本地有钱包，但是env的 tc_ethAddress 没有值 说明没有存储到服务器，需要跳转登录 跳转登录
                if ("".equals(EnvironmentVariable.getProperty("tc_ethAddress", ""))) {
                    //删除主账户的目录
                    File dirFile = new File(TalkChainConstant.getDataFilePath(view) + File.separator + userNumber);
                    FileDeleteUtil.delete(dirFile);
                    //从数据库中移除
                    new AccountDao(view).deleteMainAccount();
                    EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                    EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                    // EnvironmentVariable.setUserName("");
                    EnvironmentVariable.setPassword("");
                    view.startIntent(Login_withTitle.class);
                    return;
                }
                try {
                    if (!CompatibleOldVersionUtil.
                            compareLocalFileEthAddressWithServer(view, EnvironmentVariable
                                    .getProperty("tc_ethAddress", ""))) {
                        //如果本地有钱包，但是地址跟服务器比对不相符，需要提示跳转登录
                        new AlertDialog.Builder(view)
                                .setTitle(R.string.common_text_hint)
                                .setCancelable(false)
                                .setMessage(view.getResources().getString(R.string.guide_dialog__check_baseid_error_hint))
                                .setPositiveButton(R.string.common_text_confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AccountDao(view).deleteByCurentImuserId();
                                        File dirFile = new File(TalkChainConstant
                                                .getDataFilePath(view) + File.separator + userNumber);
                                        FileDeleteUtil.delete(dirFile);
                                        new AccountDao(view).deleteMainAccount();

                                        EnvironmentVariable.setProperty(CHAT_USER_ID, "");
                                        EnvironmentVariable.setProperty(CHAT_PASSWORD, "");
                                        // EnvironmentVariable.setUserName("");
                                        EnvironmentVariable.setPassword("");

                                        view.startIntent(Login_withTitle.class);
                                    }
                                })
                                .create()
                                .show();

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                view.startIntent(TabBottomActivityForTalkChain.class);

//                CompatibleOldVersionUtil.checkLocalInfoIsFull(GuideActivity.this, new CompatibleOldVersionUtil.CheckListener() {
//                    @Override
//                    public void checkSuccess() {
//                        Intent intent = new Intent();
//                        intent.setClass(GuideActivity.this, TabBottomActivityForTalkChain.class);
//                        startActivity(intent);
//                        GuideActivity.this.finish();
//                    }
//
//                    @Override
//                    public void checkFail() {
//                        ToastUtil.showToast(GuideActivity.this, "数据初始化失败，请关闭并重新打开应用");
//                    }
//                });

            }
        }
    }


    public boolean isSameVersion() {
        String latestAppVersion = EnvironmentVariable.getProperty("app_version_for_launch", "");
        try {
            String appVersion = AppUtils.getAppVersionName();
            return appVersion.equals(latestAppVersion);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
