package com.efounder.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.efounder.chat.activity.BaseActivity;
import com.efounder.frame.language.MultiLanguageUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import io.telecomm.telecomm.R;

import java.util.Locale;

/**
 * google map view detail
 * @author will
 */
public class GMapViewLocationActivity extends BaseActivity implements OnMapReadyCallback,
    View.OnClickListener{

    public static final int LOCATION_RESULT_CODE = 41;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private String address;//具体地址
    private String title;//位置


    private Handler handler = new Handler();

    private double latitude = -1;//选中条目的纬度
    private double longitude = -1;//选中条目的经度


    private LatLng latLng;


    private String otherLocationAddress;
    private String otherLocationname;
    private String otherLocationlatitude;
    private String otherLocationlongitude;

    private TextView tvname;
    private TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = MultiLanguageUtil.getSetLanguageLocale(this);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_gmap_location_view);
        initView();
        mapFragment.getMapAsync(this);
    }

    private void initView() {
        // Build the map.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        LinearLayout leftLayout = (LinearLayout) findViewById(R.id.leftbacklayout);
        ImageView backBtn = (ImageView) findViewById(R.id.backButton);
        TextView title = (TextView) findViewById(R.id.fragmenttitle);
        TextView right = (TextView) findViewById(R.id.meeting_date);
        leftLayout.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        right.setOnClickListener(this);
        tvname = (TextView) findViewById(R.id.tv_name);
        tvAddress = (TextView) findViewById(R.id.tv_address);

        otherLocationAddress = getIntent().getStringExtra("address");
        otherLocationname = getIntent().getStringExtra("name");
        otherLocationlatitude = getIntent().getStringExtra("latitude");
        otherLocationlongitude = getIntent().getStringExtra("longitude");
        latLng = new LatLng(Double.valueOf(otherLocationlatitude),
                Double.valueOf(otherLocationlongitude));
        tvname.setText(otherLocationname);
        tvAddress.setText(otherLocationAddress);
        title.setText(R.string.chat_location_detail);
        title.setMaxEms(10);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap == null) {
            return;
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                15));
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.backButton) {
            finish();
        }
    }
}
