package com.efounder;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;

import com.efounder.chat.activity.MobileShareActivity;
import com.efounder.chat.activity.ShareToZoneActivity;
import com.efounder.chat.model.ShareContent;
import com.efounder.chat.struct.StructFactory;
import com.efounder.chat.utils.SystemShareHelper;
import com.efounder.chat.utils.UshareHelper;
import com.efounder.message.struct.IMStruct002;
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

import org.json.JSONObject;
import io.telecomm.telecomm.R;

import static com.efounder.chat.utils.HandleShareIntentUtil.SHARE_TYPE_MALL_ITEM;


/**
 * 商城商品分享
 * @author will
 */
public class MallShareHelper implements UshareHelper {

    /**
     * @param context
     * @param url      分享的图片文件路径
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     */
    @Override
    public void shareWithOtherParams(Context context, String url, String title, int thumbDrawable, String description, Object objectParam, ShareListener listener) {
        share(context, url, title, thumbDrawable, description, objectParam, listener);
    }

    /**
     * @param context
     * @param filePath      分享的图片文件路径
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param listener      回调
     *                      final Context context, final String goodBh,
     *                                         final String goodCover, final String title, final String content
     */
    @Override
    public void share(final Context context, final String filePath, final String title
            , final int thumbDrawable, final String description, ShareListener listener) {

    }

    /**
     * @param context
     * @param url           分享的url
     * @param title         标题
     * @param thumbDrawable 缩略图
     * @param description   描述
     * @param chatMenuModel 商品信息JSON
     * @param listener      回调
     */
    public void share(final Context context, final String url, final String title
            , final int thumbDrawable, final String description, final Object chatMenuModel, ShareListener listener) {
        final UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setThumb(new UMImage(context, R.drawable.wechat_icon_launcher));
        if (description == null || "".equals(description)) {
            web.setDescription(title);//描述
        } else {
            web.setDescription(description);
        }


        //设置分享的平台
        new ShareAction((Activity) context)
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
                            } else
                                if (snsPlatform.mKeyword.equals("ushare_friend")) {
                                sendToFriend(context, chatMenuModel);

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
                            } else if (snsPlatform.mKeyword.equals("ushare_gravity_field")) {
                                shareToGraviFiled(context, (String) chatMenuModel);
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
     * @param object 商品信息
     */
    private void sendToFriend(final Context context, Object object) {
        try {
            JSONObject jsonObject = new JSONObject((String) object);
            String goodBh = jsonObject.getString("goodId");
            String goodCover = jsonObject.getString("goodCover");
            String content = jsonObject.getString("price");
            String goodTitle = jsonObject.getString("title");
            IMStruct002 good= StructFactory.getInstance().createShareGoodsStruct(goodBh, goodCover, goodTitle, content, StructFactory.TO_USER_TYPE_OFFICIAL_ACCOUNT);
            MobileShareActivity.start(true, good, String.format(context.getResources().getString(R.string.mall_share_content_goods), goodTitle));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送到引力场
     * @param object 商品信息
     */
    private void shareToGraviFiled(Context context, String object) {
        try {
            Intent intent = new Intent();
            ShareContent shareContent = new ShareContent();
            shareContent.setText("");
            shareContent.setType(SHARE_TYPE_MALL_ITEM);
            shareContent.setGoodUrl(object);
            intent.putExtra("shareContent", shareContent);
            intent.putExtra("isLocalShare", true);
            intent.setClass(context, ShareToZoneActivity.class);
            context.startActivity(intent);
        } catch (Exception e) {
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
    private void sharePlatform(final Context context, SHARE_MEDIA share_media, UMWeb umImage) {
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
        config.setShareboardBackgroundColor(context.getResources().getColor(R.color.share_board_bg));
        //修改取消分享按钮的背景颜色
        config.setCancelButtonBackground(context.getResources().getColor(R.color.frame_white_background_color));
        //修改取消分享的字体颜色
        config.setCancelButtonTextColor(context.getResources().getColor(R.color.black_text_color));
        //修改分享item的字体颜色
        config.setMenuItemTextColor(context.getResources().getColor(R.color.black_text_color));
        return config;
    }
}
