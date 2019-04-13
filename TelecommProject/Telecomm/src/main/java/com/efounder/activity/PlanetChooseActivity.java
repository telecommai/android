package com.efounder.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.efounder.frame.language.MultiLanguageUtil;
import io.telecomm.telecomm.R;
import com.pansoft.openplanet.activity.RegisterUserActivity;
import com.pansoft.openplanet.widget.BackgroundViewPager;

public class PlanetChooseActivity extends Activity {

    //手机号
    private String number = "";
    //邀请码
    private String inviteCode;
    private String planet = "mercury";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MultiLanguageUtil.setLocal(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        if (getIntent().getExtras() != null && getIntent().getExtras().get("number") != null) {
            number = (String) getIntent().getExtras().get("number");
            if (getIntent().getExtras().get("inviteCode") == null) {
                inviteCode = "";
            } else {
                inviteCode = getIntent().getExtras().getString("inviteCode");
            }
        }

        final TextView tvPlanet = findViewById(R.id.tv_planet);
        Button btnConfirm = findViewById(R.id.btn_confirm);
        final TextView tvPlanetIntro = findViewById(R.id.tv_planet_description);
        ImageView ivSun = findViewById(R.id.iv_sun);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotation_sun);
        if (animation != null) {
            ivSun.startAnimation(animation);
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择星球之后如果是手机号跳转验证码输入界面，否则跳入
                if (getIntent().getBooleanExtra("isPhone", true)) {
                    Intent intent = new Intent(PlanetChooseActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("inviteCode", inviteCode);
                    intent.putExtra("number", number);
                    intent.putExtra("planetName", planet);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PlanetChooseActivity.this, RegisterUserActivity.class);
                    //邀请码
                    intent.putExtra("inviteCode", inviteCode);
                    //星球
                    intent.putExtra("planet", planet);
                    intent.putExtra("number", number);
                    startActivity(intent);
                }
            }
        });
        tvPlanet.setText(R.string.mercury_name);
        BackgroundViewPager viewPager = findViewById(R.id.flowViewPager1);
        viewPager.setAdapter(new PlanetPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        tvPlanet.setText(R.string.mercury_name);
                        tvPlanetIntro.setText(R.string.mercury_intro);
                        planet = getString(R.string.mercury_name);
                        break;
                    case 1:
                        tvPlanet.setText(R.string.venus_name);
                        tvPlanetIntro.setText(R.string.venus_intro);
                        planet = getString(R.string.venus_name);
                        break;
                    case 2:
                        tvPlanet.setText(R.string.earth_name);
                        tvPlanetIntro.setText(R.string.earth_intro);
                        planet = getString(R.string.earth_name);
                        break;
                    case 3:
                        tvPlanet.setText(R.string.mars_name);
                        tvPlanetIntro.setText(R.string.mars_intro);
                        planet = getString(R.string.mars_name);
                        break;
                    case 4:
                        tvPlanet.setText(R.string.jupiter_name);
                        tvPlanetIntro.setText(R.string.jupiter_intro);
                        planet = getString(R.string.jupiter_name);
                        break;
                    case 5:
                        tvPlanet.setText(R.string.saturn_name);
                        tvPlanetIntro.setText(R.string.saturn_intro);
                        planet = getString(R.string.saturn_name);
                        break;
                    case 6:
                        tvPlanet.setText(R.string.neptune_name);
                        tvPlanetIntro.setText(R.string.uranus_intro);
                        planet = getString(R.string.uranus_name);
                        break;
                    case 7:
                        tvPlanet.setText(R.string.neptune_name);
                        tvPlanetIntro.setText(R.string.neptune_intro);
                        planet = getString(R.string.neptune_name);
                        break;
                    case 8:
                        tvPlanet.setText(R.string.pluto_name);
                        tvPlanetIntro.setText(R.string.pluto_intro);
                        planet = getString(R.string.pluto_name);
                        break;
                    default:
                        tvPlanet.setText(R.string.mercury_name);
                        tvPlanetIntro.setText(R.string.mercury_intro);
                        planet = getString(R.string.mercury_name);
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login_withTitle.class);
        intent.putExtra("number", number);
        startActivity(intent);
        finish();
    }

    public class PlanetPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 9;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.item_pager_planet, container, false);
            ImageView imageView = view.findViewById(R.id.iv_planet);
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.mercury);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.venus);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.earth);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.mars);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.jupiter);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.saturn);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.uranus);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.neptune);
                    break;
                case 8:
                    imageView.setImageResource(R.drawable.pluto);
                    break;
                default:
                    imageView.setImageResource(R.drawable.mercury);
                    break;
            }

            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            return view;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
