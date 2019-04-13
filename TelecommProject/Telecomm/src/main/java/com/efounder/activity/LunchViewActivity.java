package com.efounder.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.efounder.chat.model.Constant;
import com.efounder.constant.EnvironmentVariable;

import io.telecomm.telecomm.R;
import com.efounder.util.ImageLoaderUtil;
import com.efounder.util.StorageUtil;
import com.pansoft.appcontext.AppConstant;
import com.pansoft.openplanet.activity.TabBottomActivityForTalkChain;

import java.io.File;

import static com.efounder.frame.utils.Constants.CHAT_PASSWORD;
import static com.efounder.frame.utils.Constants.CHAT_USER_ID;

//import com.efounder.util.LoginAction.LoginCallback;

/**
 * 启动展示页面
 *
 * @author cherise
 *
 */
public class LunchViewActivity extends Activity {

	private ImageView lauchImageView;
	private String imagePath;
	Animation  animation;

	//微信聊天id 和password
	private String chatUserID;
	private String chatPassword;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lunchview);
		lauchImageView = findViewById(R.id.destopImage);
		String imagePath = AppConstant.APP_ROOT + "/res/unzip_res" + "/Image/"+ "destop";
		File file = new File(imagePath);
		if (file.exists()) {
			System.out.println("首页图片路径" + imagePath);
		/*	BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			Bitmap bm = BitmapFactory.decodeFile(imagePath, options);*/
			Bitmap bm = ImageLoaderUtil.getBitmapfromFilewithoutfixes(imagePath, 2);
			lauchImageView.setImageBitmap(bm);
			//bm.recycle();

		} else {
			// lauchImageView.setBackground(getResources().getDrawable(R.drawable.lunch));
		}

		animation=AnimationUtils.loadAnimation(LunchViewActivity.this, R.anim.lunchimagescale);
		animation.setFillAfter(true);
		lauchImageView.startAnimation(animation);

		StorageUtil storageUtil =  new StorageUtil(getApplicationContext(), "storage");
		Boolean isAuto =  storageUtil.getBoolean("isAuto", false);

		Handler x = new Handler();
		if(isAuto){
			x.postDelayed(new lunchhandler(), 0);
		}else{
			x.postDelayed(new lunchhandler(), 700);
		}

	}

	class lunchhandler implements Runnable {

		public void run() {
			StorageUtil storageUtil =  new StorageUtil(getApplicationContext(), "storage");
			Boolean isAuto =  storageUtil.getBoolean("isAuto", false);
			String userName = EnvironmentVariable.getUserName();
			String passWord = EnvironmentVariable.getPassword();

			if(isAuto){
//				LoginAction loginMethod = new LoginAction(LunchViewActivity.this);
//				loginMethod.loginBegin(LunchViewActivity.this, userName, passWord);
//				loginMethod.setLoginCallback(new LoginCallback() {
//
//					@Override
//					public void success() {
//						// TODO Auto-generated method stub
//					//	lauchImageView.clearAnimation();
//					}
//					@Override
//					public void error() {
//						// TODO Auto-generated method stub
//						Intent intent = new Intent();
//         				intent.setClass(LunchViewActivity.this, Login_withTitle.class);
//         				LunchViewActivity.this.startActivity(intent);
//         				LunchViewActivity.this.finish();
//					}
//				});
//

			}else{
				chatUserID = EnvironmentVariable.getProperty(CHAT_USER_ID);
				chatPassword = EnvironmentVariable.getProperty(CHAT_PASSWORD);
				String pingtaiPassword = EnvironmentVariable.getPassword();

				//TODO 这里要判断本地是否存在本地权限，如果没有，扔要跳转
				//如果第一次登陆，需要跳转登陆界面；如果需要升级资源文件，需要跳转登陆界面
				// 判断espmobile路径是否存在,下面会用到
				String path = Constant.appSdcardLocation;
				File ESPMobileForWeChat = new File(path);

				//如果没有基本的配置文件
				boolean isNeedUpdateRES = ESPMobileForWeChat.exists();
				if(null==chatUserID||"".equals(chatUserID)||!isNeedUpdateRES||null==chatPassword||"".equals(chatPassword)||null==pingtaiPassword||"".equals(pingtaiPassword)){
					Intent intent = new Intent();
					intent.setClass(LunchViewActivity.this,Login_withTitle.class);

					startActivity(intent);
					LunchViewActivity.this.finish();
					return ;
				}else{
					Intent intent = new Intent();
					intent.setClass(LunchViewActivity.this,TabBottomActivityForTalkChain.class);
					startActivity(intent);
					LunchViewActivity.this.finish();
				}

			}
		}

	}


}
