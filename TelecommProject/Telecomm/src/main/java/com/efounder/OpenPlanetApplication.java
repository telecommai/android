package com.efounder;

import com.clj.fastble.BleManager;
import com.efounder.forwechat.BaseApp;
import com.iflytek.cloud.SpeechUtility;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * 放用于分享到各平台的key
 * Created by slp on 2018/1/29.
 */

public class OpenPlanetApplication extends BaseApp {
    {
        PlatformConfig.setWeixin("wx2f4c2daedd19b1a9", "987113512cfe2f17e742fe059ed5e10f");
        PlatformConfig.setQQZone("1106937158", "CDijJjdAkLLdRpfP");
        PlatformConfig.setSinaWeibo("1458637286", "a784b85297d0912d182c4aecba14300a", "");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Config.DEBUG = false;

        try {
            // initOkGo();
            //友盟分享
            UMShareAPI.get(this);
            //讯飞语音
            SpeechUtility.createUtility(this, "appid=" + getString(com.iflytek.voicedemo.R.string.xuefei_appid));
            //ble蓝牙
            BleManager.getInstance().init(this);
            BleManager.getInstance()
                    .enableLog(true)
                    .setReConnectCount(1, 5000)
                    .setConnectOverTime(20000)
                    .setOperateTimeout(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public void initOkGo() throws Exception {
//        SPCookieStore   mSPCookieStore = new SPCookieStore(this);
//        HttpHeaders headers  = new HttpHeaders();
//        if (new SPCookieStore(this).getAllCookie().size() != 0) {
//
//            headers.put("Set-Cookie", String.valueOf(mSPCookieStore.getCookie(HttpUrl.parse(BaseUrl.HTTP_Get_code_auth))));
//        }
//        headers.put("version", "3.0");
//        headers.put("uid", "6f1a8e0eb24afb7ddc829f96f9f74e9d");
//
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //log相关
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkHttp");
//        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
//        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
//        builder.addInterceptor(loggingInterceptor);                                 //添加OkGo默认debug日志
//        //超时时间设置
//        builder.readTimeout(10000, TimeUnit.MILLISECONDS);      //全局的读取超时时间
//        builder.writeTimeout(10000, TimeUnit.MILLISECONDS);     //全局的写入超时时间
//        builder.connectTimeout(10000, TimeUnit.MILLISECONDS);   //全局的连接超时时间
//        builder.cookieJar(new CookieJarImpl(mSPCookieStore));            //使用sp保持cookie，如果cookie不过期，则一直有效
//
//
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(getAssets().open("server.cer"));
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
////        //配置https的域名匹配规则，使用不当会导致https握手失败
//        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
//
//        // 其他统一的配置
//        OkGo.getInstance().init(this)                           //必须调用初始化
//                .setOkHttpClient(builder.build())               //必须设置OkHttpClient
//                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
//                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
//                .setRetryCount(3)          //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers);              //全局公共头
////                .addCommonParams(httpParams);                       //全局公共参数
//
//    }
}
