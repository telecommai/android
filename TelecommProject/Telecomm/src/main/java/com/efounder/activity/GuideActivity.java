package com.efounder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.telecomm.telecomm.R;
import com.efounder.presenter.GuidePresenter;
import com.efounder.utils.EasyPermissionUtils;
import com.pansoft.openplanet.mvp.base.MvpBaseActivity;
import com.pansoft.openplanet.widget.commonviewpage.CommonViewPager;
import com.pansoft.openplanet.widget.commonviewpage.LaunchBean;
import com.pansoft.openplanet.widget.commonviewpage.Util;
import com.pansoft.openplanet.widget.commonviewpage.ViewPagerHolder;
import com.pansoft.openplanet.widget.commonviewpage.ViewPagerHolderCreator;
import com.pansoft.resmanager.ResLoadManager;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;

/**
 * @author : zzj
 * @e-mail : zhangzhijun@pansoft.com
 * @date : 2018/10/13 15:42
 * @desc : 引导页
 * @version: 1.0
 */
public class GuideActivity extends MvpBaseActivity<GuidePresenter> {
    public static final String IS_HIDE_INTO_APP_BTN = "isShowIntoAppBtn";
    private CommonViewPager commonViewPager;
    private List<LaunchBean> launchBeans = new ArrayList<>();
    private TextView tvItemViewpagerTitle;
    private FrameLayout flPicTitle;
    private ImageView ivTitle;
    /**
     * 判断是否隐藏进入app的按钮,或者是从哪个界面跳入进行逻辑判断
     */
    private boolean isHideIntoBtn;
    private TextView tvTitle;
    private ImageView iv_back;
    private RelativeLayout title;
    /**
     * 进入app按钮
     */
    private Button btnIntoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ResLoadManager.setMainProjectR(R.string.class);
        setCheckPermission(false);
        super.onCreate(savedInstanceState);
    }

    public static void start(Context context, boolean isHideIntoBtn) {
        Intent starter = new Intent(context, GuideActivity.class);
        starter.putExtra(IS_HIDE_INTO_APP_BTN, isHideIntoBtn);
        context.startActivity(starter);
    }

    @Override
    protected boolean initWidows() {
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return false;
        }
//        if (!isTaskRoot()) {
//            finish();
//            return false;
//        }

        setEnableScreenShotListen(false);
        isHideIntoBtn = getIntent().getBooleanExtra(IS_HIDE_INTO_APP_BTN, false);
        if (!isHideIntoBtn) {
            //定义全屏参数
            int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            //获得当前窗体对象
            Window window = this.getWindow();
            //设置当前窗体为全屏显示
            window.setFlags(flag, flag);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.title_Background));
        }
        return true;
    }

    @Override
    protected GuidePresenter initPresenter() {
        return new GuidePresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        //已登录和打开跳转欢迎页
        if (!isHideIntoBtn && presenter.isSameVersion() && presenter.isLogin()) {
            startIntent(Welcome1Activity.class);
        }
        initView(isHideIntoBtn);
    }

    @Override
    protected void initData() {
        launchBeans.addAll(presenter.loadGuideDataList());
        // 设置数据
        commonViewPager.setPages(launchBeans, new ViewPagerHolderCreator<ViewImageHolder>() {
            @Override
            public ViewImageHolder createViewHolder() {
                // 返回ViewPagerHolder
                return new ViewImageHolder();
            }
        });
        commonViewPager.getViewPager().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                tvItemViewpagerTitle.setText(launchBeans.get(i).getTitle());
                ivTitle.setImageResource(launchBeans.get(i).getPicTitle());
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }


    private void initView(boolean isHideIntoBtn) {
        title = (RelativeLayout) findViewById(R.id.title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getResources().getString(R.string.guide_tv_function_introduction));
        iv_back = (ImageView) findViewById(R.id.iv_back);
        btnIntoApp = (Button) findViewById(R.id.btn_into_app);
        if (isHideIntoBtn) {
            btnIntoApp.setVisibility(View.GONE);
            //设置不可滑动返回
            getSwipeBackLayout().setEnableGesture(true);
            title.setVisibility(View.VISIBLE);
        } else {
            btnIntoApp.setVisibility(View.VISIBLE);
            //设置不可滑动返回
            getSwipeBackLayout().setEnableGesture(false);
            title.setVisibility(View.GONE);
        }

        commonViewPager = (CommonViewPager) findViewById(R.id.activity_common_view_pager);
        tvItemViewpagerTitle = (TextView) findViewById(R.id.tv_item_viewpager_title);
        flPicTitle = (FrameLayout) findViewById(R.id.fl_pic_title);
        Util.setBannerViewHeight(this, flPicTitle, 2);
        ivTitle = (ImageView) findViewById(R.id.iv_title);
        initEvent();
    }

    @Override
    public void initEvent() {
        /**
         *进入app的点击事件
         */
        btnIntoApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.inToApp();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(iv_back);
            }
        });
    }


    /**
     * 提供ViewPager展示的ViewHolder
     * <P>用于提供布局和绑定数据</P>
     */
    public static class ViewImageHolder implements ViewPagerHolder<LaunchBean> {
        private ImageView ivItemViewpagerContent;
        private FrameLayout itemViewpagerFl;

        public ViewImageHolder() {
        }

        @Override
        public View createView(Context context) {
            // 返回ViewPager 页面展示的布局
            View view = LayoutInflater.from(context).inflate(R.layout.item_guide_viewpager, null);
            ivItemViewpagerContent = (ImageView) view.findViewById(R.id.iv_item_viewpager_content);
            itemViewpagerFl = view.findViewById(R.id.item_viewpager_fl);
            Util.setBannerViewHeight((Activity) context, itemViewpagerFl, 2);
            Util.setBannerViewHeight((Activity) context, ivItemViewpagerContent, 4);
            return view;
        }

        @Override
        public void onBind(Context context, int position, LaunchBean data) {
            // 数据绑定
            // 自己绑定数据，灵活度很大
            ivItemViewpagerContent.setImageResource(data.getPicContent());
        }
    }


    /**
     * 页面跳转
     *
     * @param clazz
     */

    private Intent startIntent;

    public void startIntent(Class clazz) {
        startIntent = new Intent();
        startIntent.setClass(GuideActivity.this, clazz);
        goToNext();
    }

    /**
     * 页面跳转
     */
    @AfterPermissionGranted(EasyPermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE_REQUEST_CODE)//请求码
    public void goToNext() {
        if (EasyPermissionUtils.checkWriteAndPhonePermission()) {
            startActivity(startIntent);
            GuideActivity.this.finish();
        } else {
            EasyPermissionUtils.requestStorageAndPhoneStatePermission(this);
        }
    }
}
