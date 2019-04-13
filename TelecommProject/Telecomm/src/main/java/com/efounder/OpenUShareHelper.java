package com.efounder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;

import com.efounder.chat.activity.MobileShareActivity;
import com.efounder.chat.activity.ShareNameCardActivity;
import com.efounder.chat.struct.StructFactory;
import com.efounder.chat.utils.SystemShareHelper;
import com.efounder.chat.utils.UshareHelper;
import io.telecomm.telecomm.R;
import com.efounder.utils.JfResourceUtil;
import com.efounder.utils.ResStringUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.utilcode.util.ToastUtils;

import static com.utilcode.util.ActivityUtils.startActivity;

/**
 * 友盟分享辅助实现类
 */
public class OpenUShareHelper implements UshareHelper {
    /**
     * @param context
     * @param url           分享的url
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     */
    @Override
    public void share(final Context context, final String url, final String title
            , final int thumbDrawable, final String description, ShareListener listener) {
        share(context, url, title, thumbDrawable, description, null, listener);
    }

    /**
     * @param context
     * @param url           分享的url
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     */
    public void share(final Context context, final String url, final String title
            , final int thumbDrawable, final String description, final Object chatMenuModel, ShareListener listener) {
        final UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setThumb(new UMImage(context, com.efounder.chat.R.drawable.wechat_icon_launcher));
        if (description == null || "".equals(description)) {
            web.setDescription(title);//描述
        } else {
            web.setDescription(description);
        }
        //设置分享的平台
        new ShareAction((Activity) context)
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_contacts)
                        , "ushare_friend", "wechat_share_friend", "wechat_share_friend")
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
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_copy_url)
                        , "ushare_copy_url", "wechatview_share_copy_url", "wechatview_share_copy_url")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_share_system)
                        , "ushare_system", "wechatview_share_system", "wechatview_share_system")
                .addButton(context.getResources().getString(R.string.openplanet_share_helper_open_browber)
                        , "ushare_open_browber", "wechatview_share_browser", "wechatview_share_browser")
                .setShareboardclickCallback(new ShareBoardlistener() {
                    @Override
                    public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                        if (share_media == null) {
                            if (snsPlatform.mKeyword.equals("ushare_copy_url")) {
                                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                // 将文本内容放到系统剪贴板里。
                                cm.setText(url);
                                ToastUtils.showShort(R.string.chat_copy_to_clipboard);
                            } else if (snsPlatform.mKeyword.equals("ushare_system")) {
                                SystemShareHelper.shareText(context, title + " " + url, title, url);
                            } else if (snsPlatform.mKeyword.equals("ushare_open_browber")) {
                                Uri uri = Uri.parse(url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                try {
                                    context.startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                }
                            } else if (snsPlatform.mKeyword.equals("ushare_friend")) {
                                sendToFriend(context, url, title
                                        , thumbDrawable, description, chatMenuModel);

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
                            }
                        } else {
                            sharePlatform(context, share_media, web);
                        }


                    }
                })
                .open(configShareBoard(context));

    }

    @Override
    public void shareWithOtherParams(Context context, String url, String title, int thumbDrawable, String description, Object objectParam, ShareListener listener) {

    }


    /**
     * 发送给联系人
     *
     * @param context
     * @param url
     * @param title
     * @param thumbDrawable
     * @param description
     * @param groupId
     */
    private void sendToFriend(final Context context, final String url, final String title
            , final int thumbDrawable, final String description, Object groupId) {
        //分享通用群名片
        if (context.getClass().toString().contains("com.efounder.chat.activity.ChatGroupSettingActivity")) {
            try {
                Intent intent = new Intent(context, ShareNameCardActivity.class);
                intent.putExtra("id", (int) groupId);
                intent.putExtra("shareAddress", "group_setting");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { //分享web页面
            String desc = "";
            if (description.equals("")) {
                desc = url;
            } else {
                desc = description;
            }
            Intent intent = new Intent(context, MobileShareActivity.class);
            intent.putExtra("sendImStruct002", true);
            intent.putExtra("imStruct002", StructFactory.getInstance()
                    .createShareWebStruct(url, "", title, desc, StructFactory.TO_USER_TYPE_PERSONAL));
            intent.putExtra("shareExplain", title);
            startActivity(intent);
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
     * @param web
     */
    private void sharePlatform(final Context context, SHARE_MEDIA share_media, UMWeb web) {
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
                .withMedia(web)
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
        config.setIndicatorVisibility(true);
        config.setCancelButtonText(ResStringUtil.getString(com.efounder.mobilenews.R.string.common_text_cancel));
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
