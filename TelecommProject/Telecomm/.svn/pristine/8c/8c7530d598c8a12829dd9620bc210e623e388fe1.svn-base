package com.efounder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.efounder.chat.activity.ShareToZoneActivity;
import com.efounder.chat.model.ShareContent;
import com.efounder.chat.utils.HandleShareIntentUtil;
import com.efounder.chat.utils.SystemShareHelper;
import com.efounder.chat.utils.UshareHelper;
import io.telecomm.telecomm.R;
import com.efounder.utils.JfResourceUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import java.io.File;
import java.util.ArrayList;

import static com.utilcode.util.ActivityUtils.startActivity;

//import io.telecomm.telecomm.R;

/**
 * 友盟分享屏幕截图辅助实现类
 *
 * @author YQS
 * @date 2018/11/5
 */
public class UShareScreenShotHelper implements UshareHelper {
    @Override
    public void shareWithOtherParams(Context context, String url, String title, int thumbDrawable, String description, Object objectParam, ShareListener listener) {

    }

    /**
     * @param context
     * @param filePath      分享的图片文件路径
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     */
    @Override
    public void share(final Context context, final String filePath, final String title
            , final int thumbDrawable, final String description, ShareListener listener) {
        share(context, filePath, title, thumbDrawable, description, null, listener);
    }

    /**
     * @param context
     * @param filePath      分享的图片路径
     * @param title         注意了 title 在分享截图里放的是原始截图文件路径
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     */
    public void share(final Context context, final String filePath, final String title
            , final int thumbDrawable, final String description, final Object chatMenuModel, ShareListener listener) {
        final UMImage web = new UMImage(context, new File(filePath));
//        web.setTitle(title);
//        web.setThumb(new UMImage(context, com.efounder.chat.R.drawable.wechat_icon_launcher));
//        if (description == null || "".equals(description)) {
//            web.setDescription(title);//描述
//        } else {
//            web.setDescription(description);
//        }


        //设置分享的平台
        new ShareAction((Activity) context)
//                .setDisplayList(SHARE_MEDIA.SINA,
//                        SHARE_MEDIA.WEIXIN,
//                        SHARE_MEDIA.WEIXIN_CIRCLE,
//                        SHARE_MEDIA.WEIXIN_FAVORITE,
//                        SHARE_MEDIA.QQ,
//                        SHARE_MEDIA.QZONE)
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_contacts)
                        , "ushare_friend", "wechat_share_friend", "wechat_share_friend")
                .addButton(context.getResources().getString(R.string.zone_gravity_field)
                        , "ushare_gravity_field", "wechat_share_zone", "wechat_share_zone")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_sina)
                        , "sina", "umeng_socialize_sina", "umeng_socialize_sina")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_wechat)
                        , "wechat", "umeng_socialize_wechat", "umeng_socialize_wechat")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_wxcircle)
                        , "wxcircle", "umeng_socialize_wxcircle", "umeng_socialize_wxcircle")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_wechatfavorite)
                        , "wechatfavorite", "umeng_socialize_fav", "umeng_socialize_fav")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_qq)
                        , "qq", "umeng_socialize_qq", "umeng_socialize_qq")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_qzone)
                        , "qzone", "umeng_socialize_qzone", "umeng_socialize_qzone")
//                .addButton(context.getResources().getString(R.string.openplanet_share_helper_copy_url)
//                        , "ushare_copy_url", "wechatview_share_copy_url", "wechatview_share_copy_url")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_share_system)
                        , "ushare_system", "wechatview_share_system", "wechatview_share_system")
//                .addButton(context.getResources().getString(R.string.openplanet_share_helper_open_browber)
//                        , "ushare_open_browber", "wechatview_share_browser", "wechatview_share_browser")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (share_media == null) {
//                            if (snsPlatform.mKeyword.equals("ushare_copy_url")) {
//                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                                // 将文本内容放到系统剪贴板里。
//                                cm.setText(url);
//                                ToastUtil.showToast(context.getApplicationContext(), "已复制到剪贴板");
//                            } else if (snsPlatform.mKeyword.equals("ushare_system")) {
//                                SystemShareHelper.shareText(context, title + " " + url, title, url);
//                            } else if (snsPlatform.mKeyword.equals("ushare_open_browber")) {
//                                Uri uri = Uri.parse(url);
//                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
//                                try {
//                                    context.startActivity(intent);
//                                } catch (ActivityNotFoundException e) {
//                                }
//                            } else

                            if (snsPlatform.mKeyword.equals("ushare_friend")) {
                                sendToFriend(context, filePath, title, description, chatMenuModel);

                            } else if (snsPlatform.mKeyword.equals("sina")) {
                                sharePlatform(context, SHARE_MEDIA.SINA, web);
                            } else if (snsPlatform.mKeyword.equals("wechat")) {
                                sharePlatform(context, SHARE_MEDIA.WEIXIN, web);

                            } else if (snsPlatform.mKeyword.equals("wxcircle")) {
                                sharePlatform(context, SHARE_MEDIA.WEIXIN_CIRCLE, web);

                            } else if (snsPlatform.mKeyword.equals("wechatfavorite")) {
                                sharePlatform(context, SHARE_MEDIA.WEIXIN_FAVORITE, web);

                            } else if (snsPlatform.mKeyword.equals("qq")) {
                                sharePlatform(context, SHARE_MEDIA.QQ, web);

                            } else if (snsPlatform.mKeyword.equals("qzone")) {
                                sharePlatform(context, SHARE_MEDIA.QZONE, web);
                            } else if (snsPlatform.mKeyword.equals("ushare_system")) {
                                SystemShareHelper.shareOneImage(context, filePath);
                            } else if (snsPlatform.mKeyword.equals("ushare_gravity_field")) {
                                ShareContent shareContent = new ShareContent();
                                shareContent.setType(HandleShareIntentUtil.SHARE_TYPE_IMAGE);
                                ArrayList<String> pictureList = new ArrayList<>();
                                pictureList.add(title);
                                shareContent.setPictureList(pictureList);
                                Intent intent = new Intent(context, ShareToZoneActivity.class);
                                intent.putExtra("imageMinorCompress", true);
                                intent.putExtra("isLocalShare", true);
                                intent.putExtra("shareContent", shareContent);
                                startActivity(intent);
                            }
                        } else {
                            sharePlatform(context, share_media, web);
                        }


                    }
                })
                .open(configShareBoard(context));

    }


    /**
     * 发送给联系人（发送原始图片文件）
     *
     * @param context
     * @param filePath
     * @param title       原始截图文件
     * @param description
     * @param groupId
     */
    private void sendToFriend(final Context context, final String filePath, final String title
            , final String description, Object groupId) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(title)));

            //通过主工程配置 可以获取到要跳转的界面
            String className = "com.efounder.chat.activity.MobileShareActivity";
            Class clazz = Class.forName(className);
            intent.setClass(context, clazz);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分享结果回调
     *
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(context).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void release(Context context) {
        UMShareAPI.get(context).release();

    }

    /**
     * 分享自带的平台方法
     *
     * @param context
     * @param share_media
     * @param umImage
     */
    private void sharePlatform(final Context context, SHARE_MEDIA share_media, UMImage umImage) {
        new ShareAction((Activity) context).setPlatform(share_media).setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
//                Toast.makeText(context, "分享成功!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//                Toast.makeText(context, "分享失败!" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
//                Toast.makeText(context, "取消分享!", Toast.LENGTH_SHORT).show();

            }
        })
                //.withText("分享")
                .withMedia(umImage)
                .share();
    }

    /**
     * 配置分享面板的ui样式
     *
     * @return
     */
    private ShareBoardConfig configShareBoard(Context context) {
        //设置弹出框的标题与页数指示
        ShareBoardConfig config = new ShareBoardConfig();
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_NONE);
        config.setTitleVisibility(false);
        config.setIndicatorVisibility(false);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_ROUNDED_SQUARE, 8);
        //修改分享面板背景颜色
        config.setShareboardBackgroundColor(JfResourceUtil.getSkinColor(R.color.share_board_bg));
        //修改取消分享按钮的背景颜色
        config.setCancelButtonBackground(JfResourceUtil.getSkinColor(R.color.frame_white_background_color));
        //修改取消分享的字体颜色
        config.setCancelButtonTextColor(JfResourceUtil.getSkinColor(R.color.black_text_color));
        //修改分享item的字体颜色
        config.setMenuItemTextColor(JfResourceUtil.getSkinColor(R.color.black_text_color));
        return config;
    }
}
