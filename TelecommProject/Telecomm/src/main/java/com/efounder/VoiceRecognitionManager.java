package com.efounder;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.efounder.chat.utils.ImageUtil;
import com.efounder.chat.view.voicedictate.SpeechRecognitionCallBack;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speech.util.JsonParser;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 语音识别管理.
 *
 * @author YQS 2018-06-29
 */

public class VoiceRecognitionManager {

    private static String TAG = VoiceRecognitionManager.class.getSimpleName();
    private Context mContext;


    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private Toast mToast;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //是否开启翻译
    private boolean mTranslateEnable = false;
    //是否显示dialog
    private boolean showDialog = false;
    //回调识别结果
    private SpeechRecognitionCallBack callBack;
    // 函数调用返回值
    int ret = 0;

    // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
    private String startMuteOverTime = "10000";
    // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
    private String endMuteOverTime = "5000";

    // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
    private boolean hasPunctuation = true;
    //录音文件路径
    private String audioPath ;
    public VoiceRecognitionManager(Context context) {
        mContext = context;
        initParam();
    }

    private void initParam() {
        mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);

        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
        mIatDialog = new RecognizerDialog(mContext, mInitListener);

    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败，错误码：" + code);
                if (callBack != null) {
                    callBack.onFail("初始化失败，错误码：" + code);
                }
            }
        }
    };
    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if (mTranslateEnable) {
                printTransResult(results,isLast);
            } else {
                printResult(results,isLast);
            }
            Log.d(TAG, "isLast:" + isLast);
            if (isLast) {

//                if (mTranslateEnable) {
//                    printTransResult(results);
//                } else {
//                    printResult(results);
//                }

                // islast 表示讲话结束
                if (callBack != null) {
//                    callBack.onEndOfSpeech();
                }
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                // showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
                if (callBack != null) {
                    callBack.onFail(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
                }

            } else {
                //showTip(error.getPlainDescription(true));
                if (callBack != null) {
                    callBack.onFail(error.getPlainDescription(true));
                }
            }
        }
    };

    //显示结果
    private void printResult(RecognizerResult results, boolean isLast) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        //showTip(resultBuffer.toString());
        if (callBack != null) {
            callBack.onSuccess(resultBuffer.toString() + "",isLast);
        }
    }

    //显示翻译结果
    private void printTransResult(RecognizerResult results, boolean isLast) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            // showTip("解析结果失败，请确认是否已开通翻译功能。");
            if (callBack != null) {
                callBack.onFail("解析结果失败，请确认是否已开通翻译功能。" + "");
            }
        } else {
            // showTip("原始语言:\n" + oris + "\n目标语言:\n" + trans);
            callBack.onSuccess("原始语言:\n" + oris + "\n目标语言:\n" + trans + "",isLast);
        }

    }

    /**
     * 开始识别
     */
    public void startRecognition() {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(mContext, "iat_recognize");
        mIatResults.clear();
        // 设置参数
        setParam();
        if (showDialog) {
            // 显示听写对话框
            mIatDialog.setListener(mRecognizerDialogListener);
            mIatDialog.show();
            if (callBack != null) {
                callBack.onStartOfSpeech();
            }
//            showTip(mContext.getString(R.string.text_begin));
            TextView txt = (TextView) mIatDialog.getWindow().getDecorView().findViewWithTag("textlink");
            txt.setText("");
        } else {
            // 不显示听写对话框
            ret = mIat.startListening(mRecognizerListener);
            if (ret != ErrorCode.SUCCESS) {
//                showTip("听写失败,错误码：" + ret);
                if (callBack != null) {
                    callBack.onFail("听写失败,错误码：" + ret);
                }
            } else {
//                showTip(mContext.getString(R.string.text_begin));
            }
        }
    }

    /**
     * 参数设置
     *
     * @return
     */
    public void setParam() {
        if( null == mIat ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
            return;
        }
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        if (mTranslateEnable) {
            Log.i(TAG, "启用翻译");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }

//        String lag = mSharedPreferences.getString("iat_language_preference",
//                "mandarin");
//        if (lag.equals("en_us")) {
//            // 设置语言
//            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
//            mIat.setParameter(SpeechConstant.ACCENT, null);
//
//            if( mTranslateEnable ){
//                mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
//                mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
//            }
//        } else {
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        if (mTranslateEnable) {
            mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
            mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
        }
        //  }
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, startMuteOverTime);

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, endMuteOverTime);
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, hasPunctuation ? "1" : "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        audioPath = ImageUtil.amrpath + System.currentTimeMillis()+"iat.wav";
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, audioPath);
    }


    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            ToastUtils.showShort("开始说话");
            if (callBack != null) {
                callBack.onStartOfSpeech();
                //返回录音文件的路径
                callBack.getAudioFilePath(audioPath);
            }
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                // showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
                if (callBack != null) {
                    callBack.onFail(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
                }
            } else {
                // showTip(error.getPlainDescription(true));
                if (callBack != null) {
                    callBack.onFail(error.getPlainDescription(true));
                }
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
//            Log.d(TAG, "结束说话");
            if (callBack != null) {
                callBack.onEndOfSpeech();
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
//            Log.d(TAG, results.getResultString());
            if (mTranslateEnable) {
                printTransResult(results, isLast);
            } else {
                printResult(results, isLast);
            }

            if (isLast) {
                // TODO 最后的结果

            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            Log.d(TAG, "当前正在说话，音量大小：" + volume);
//            Log.d(TAG, "返回音频数据：" + data.length);
            if(callBack != null){
                callBack.onVolumeChanged(volume,data);
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    //释放
    public void release() {
        if (null != mIat) {
            // 退出时释放连接
            mIat.stopListening();
            mIat.cancel();
            mIat.destroy();

        }
        if (mIatDialog != null) {
            if (mIatDialog.isShowing()) {
                mIatDialog.dismiss();
                mIatDialog = null;
            }
        }
    }
    //表示停止语音输入
    public void stopListening() {
        if (null != mIat) {
            // 退出时释放连接
            mIat.stopListening();

        }
        if (mIatDialog != null) {
            if (mIatDialog.isShowing()) {
                mIatDialog.dismiss();
                mIatDialog = null;
            }
        }
    }
    /**
     * 是否显示dialog
     *
     * @param show
     */
    public void setShowDialog(boolean show) {
        this.showDialog = show;
    }

    /**
     * 是否翻译
     *
     * @param enable
     */
    public void setmTranslateEnable(boolean enable) {
        this.mTranslateEnable = enable;
    }

    public void setStartMuteOverTime(String startMuteOverTime) {
        this.startMuteOverTime = startMuteOverTime;

    }


    public void setEndMuteOverTime(String endMuteOverTime) {
        this.endMuteOverTime = endMuteOverTime;

    }

    /**
     * 设置返回结果有无标点
     *
     * @param hasPunctuation
     */
    public void setHasPunctuation(boolean hasPunctuation) {
        this.hasPunctuation = hasPunctuation;

    }

    public void setVoiceRecognitionCallBack(SpeechRecognitionCallBack callBack) {
        this.callBack = callBack;
    }



}
