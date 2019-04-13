package com.efounder.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.efounder.chat.adapter.PoiAdapter;
import com.efounder.chat.model.PoiLocation;
import com.efounder.chat.utils.GroupLocationHttpRequest;
import com.efounder.chat.utils.ImageUtil;
import com.efounder.frame.language.MultiLanguageUtil;
import io.telecomm.telecomm.R;
import com.efounder.util.LoadingDataUtilBlack;
import com.efounder.util.ToastUtil;
import com.efounder.utils.ResStringUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.pansoft.library.CloudDiskBasicOperation;
import com.utilcode.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.efounder.chat.activity.GaoDeLocationActivity.LOCATION_RESULT_CODE;

/**
 * An activity that displays a map showing the place at the device's current location.
 * @author will
 */
public class MapsActivityCurrentPlace extends AppCompatActivity
        implements OnMapReadyCallback, AdapterView.OnItemClickListener, GoogleMap.OnMyLocationButtonClickListener, View.OnClickListener {

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private PullToRefreshListView pullToRefreshListView;
    private PoiAdapter adapter;
    private ArrayList<PoiLocation> poiList;//周边建筑物列表
    private boolean isSelectList;
    private String address;//具体地址
    private String title;//位置
    private double latitude = -1;//选中条目的纬度
    private double longitude = -1;//选中条目的经度
    private int PLACE_PICKER_REQUEST = 1;
    private int GPS_REQUEST_CODE = 10;
    private SupportMapFragment mapFragment;

    private int groupId;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-1, -1);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Locale locale = MultiLanguageUtil.getSetLanguageLocale(this);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        Intent intent = getIntent();
        groupId = intent.getIntExtra("groupid", -1);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initView();
        poiList = new ArrayList<>();
        adapter = new PoiAdapter(this, poiList);

        mapFragment.getMapAsync(this);

    }

    private void initView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_poi);
        pullToRefreshListView.setAdapter(adapter);
        pullToRefreshListView.setOnItemClickListener(this);
        pullToRefreshListView.setEnabled(false);
        LinearLayout leftLayout = (LinearLayout) findViewById(R.id.leftbacklayout);
        ImageView backBtn = (ImageView) findViewById(R.id.backButton);
        TextView title = (TextView) findViewById(R.id.fragmenttitle);
        TextView right = (TextView) findViewById(R.id.meeting_date);
        right.setVisibility(View.VISIBLE);
        title.setText(com.efounder.chat.R.string.chat_location);
        right.setText(com.efounder.chat.R.string.common_text_confirm);
        right.setTextSize(COMPLEX_UNIT_SP, 16);
        leftLayout.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(this);
        right.setOnClickListener(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        showCurrentPlace();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            if (!isLocationEnabled()) {
                openGPSSettings();
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                    (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                // Set the count, handling cases where less than 5 entries are returned.
                                int count;
                                if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                    count = likelyPlaces.getCount();
                                } else {
                                    count = M_MAX_ENTRIES;
                                }

                                int i = 0;
                                mLikelyPlaceNames = new String[count];
                                mLikelyPlaceAddresses = new String[count];
                                mLikelyPlaceAttributions = new String[count];
                                mLikelyPlaceLatLngs = new LatLng[count];
                                Map<Integer, Boolean> map = new HashMap<>();
                                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                    // Build a list of likely places to show the user.
                                    mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                    mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                            .getAddress();
                                    mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                            .getAttributions();
                                    mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();
                                    PoiLocation pl = new PoiLocation();
                                    map.put(i + 1, false);
                                        pl.setTitle(mLikelyPlaceNames[i]);
                                        pl.setAddress(mLikelyPlaceAddresses[i]);
                                    pl.setLatitude(mLikelyPlaceLatLngs[i].latitude);
                                    pl.setLongitude(mLikelyPlaceLatLngs[i].longitude);
                                    pl.setStateMap(map);
                                    poiList.add(pl);

                                    i++;
                                    if (i > (count - 1)) {
                                        break;
                                    }
                                }
                                if (poiList.size() > 0) {
                                    title = poiList.get(0).getTitle();
                                    address = poiList.get(0).getAddress();
                                    latitude = poiList.get(0).getLatitude();
                                    longitude = poiList.get(0).getLongitude();
                                }
                                adapter.notifyDataSetChanged();

                                // Release the place likelihood buffer, to avoid memory leaks.
                                likelyPlaces.release();

                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * capture screen, save photo , send msg and finish
     */
    public void captureScreen() {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                if(!NetworkUtils.isConnected()){
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MapsActivityCurrentPlace.this);
                    alertDialogBuilder.setTitle(R.string.wrchatview_prompt).setMessage(ResStringUtil.getString(R.string.network_isnot_available)).setPositiveButton(ResStringUtil.getString(R.string.common_text_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setNegativeButton(ResStringUtil.getString(R.string.common_text_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                    return;
                }
                if (null == snapshot) {
                    ToastUtil.showToast(getApplicationContext(), R.string.chat_get_location_fail_retry);
                    return;
                }
                String filePath = null;
                String scale = null;
                try {
                    String path = ImageUtil.chatpath + ".mapImage";
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    filePath = path + File.separator + sdf.format(new Date()) + ".png";
                    FileOutputStream fos = new FileOutputStream(filePath);
                    boolean b = snapshot.compress(Bitmap.CompressFormat.PNG, 70, fos);
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    StringBuffer buffer = new StringBuffer();
                    if (b)
                        buffer.append(ResStringUtil.getString(R.string.chat_screen_success));
                    else {
                        buffer.append(ResStringUtil.getString(R.string.chat_screen_fail));
                    }


                    scale = ImageUtil.getPicScale(filePath);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("latitude", String.valueOf(latitude));
                    jsonObject.put("longitude", String.valueOf(longitude));
                    jsonObject.put("scale", scale);
                    jsonObject.put("name", title);
                    jsonObject.put("specific", address);
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();

                    bundle.putString("imagePath", filePath);
                    bundle.putString("content", jsonObject.toString());

                    data.putExtras(bundle);
                    setResult(LOCATION_RESULT_CODE, data);
                    finish();
                    overridePendingTransition(com.efounder.chat.R.anim.push_top_in, com.efounder.chat.R.anim.push_bottom_out);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        mMap.snapshot(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            //做需要做的事情，比如再次检测是否打开GPS了 或者定位
            if (isLocationEnabled()) {
                mapFragment.getMapAsync(this);
            } else {
                openGPSSettings();
            }
        }

        if (resultCode == RESULT_OK && requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(data, this);
            title = place.getName().toString();
            address = place.getAddress().toString();
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
            poiList.clear();
            PoiLocation poiLocation = new PoiLocation();
            poiLocation.setAddress(address);
            poiLocation.setTitle(title);
            poiLocation.setLatitude(latitude);
            poiLocation.setLongitude(longitude);
            poiList.add(poiLocation);
            adapter.notifyDataSetChanged();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),
                    DEFAULT_ZOOM));
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .title(title)
                    .position(place.getLatLng())
                    .snippet(""));
        }
    }

    /**
     * set group address
     */
    private void setGroupAddress() {
        LoadingDataUtilBlack.show(this);
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("address", address);
        data.putExtras(bundle);
        setResult(RESULT_OK, data);
//            向后台传经纬度
//            new CloudDiskBasicOperation().cloudDiskGetNextLevelPanFileByFileId();
        GroupLocationHttpRequest request = new GroupLocationHttpRequest();
        request.addGroupPosition("" + groupId, "" + longitude, "" + latitude, address, title, new CloudDiskBasicOperation.ReturnBack() {
            @Override
            public void onReturnBack(String result) throws JSONException {
                Log.e("addGroupPosition", result + ",currentThread=" + Thread.currentThread().getName());
                JSONObject object = new JSONObject(result);
                String result1 = object.getString("result");
                String info = "";
                if ("success".equals(result1)) {
                    info = ResStringUtil.getString(R.string.wrchatview_add_group_address_suc);
//                        Toast.makeText(ChatGroupAddressActivity.this, info, Toast.LENGTH_LONG).show();
                } else {
                    String msg = object.getString("msg");
                    if (null != msg) {
                        if ("the position has exist!".equals(msg)) {
                            info = ResStringUtil.getString(R.string.wrchatview_group_address_already);
//                                Toast.makeText(ChatGroupAddressActivity.this, info, Toast.LENGTH_LONG).show();
                        } else {
                            info = ResStringUtil.getString(R.string.wrchatview_add_group_address_fail);
//                                Toast.makeText(ChatGroupAddressActivity.this, info, Toast.LENGTH_LONG).show();
                        }
                    }
                }
                Toast.makeText(MapsActivityCurrentPlace.this, info, Toast.LENGTH_LONG).show();
                LoadingDataUtilBlack.dismiss();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.backButton) {
//            取消
            setResult(LOCATION_RESULT_CODE, null);
            finish();
            overridePendingTransition(com.efounder.chat.R.anim.push_top_in, com.efounder.chat.R.anim.push_bottom_out);

        } else if (i == R.id.meeting_date) {
            //确定--返回地址
            if (groupId == -1) {
                captureScreen();
            } else {
                setGroupAddress();
            }

        } else if (i == R.id.ll_chat_group_search) {
            //搜索
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(MapsActivityCurrentPlace.this), PLACE_PICKER_REQUEST);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //当前未选中时
        if (adapter.index != position - 1) {
            adapter.index = position - 1;
            isSelectList = true;
            adapter.notifyDataSetChanged();
//            更新定位图标位置
            double latitude = poiList.get(position - 1).getLatitude();
            double longitude = poiList.get(position - 1).getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            // Add a marker for the selected place, with an info window
            // showing information about that place.
            String markerSnippet = mLikelyPlaceAddresses[position - 1];
            if (mLikelyPlaceAttributions[position - 1] != null) {
                markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[position - 1];
            }
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .title(poiList.get(position - 1).getTitle())
                    .position(latLng)
                    .snippet(markerSnippet));

            // Position the map's camera at the location of the marker.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,
                    DEFAULT_ZOOM));
        }
        title = poiList.get(position - 1).getTitle();
        address = poiList.get(position - 1).getAddress();
        latitude = poiList.get(position - 1).getLatitude();
        longitude = poiList.get(position - 1).getLongitude();
    }


    /**
     * 判断定位服务是否开启
     *
     * @param
     * @return true 表示开启
     */
    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     * 检测GPS是否打开
     *
     * @return
     */
    private boolean checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        //没有打开则弹出对话框
        new AlertDialog.Builder(this)
                .setTitle(R.string.wrchatview_prompt)
                .setMessage(R.string.gpsNotifyMsg)
                // 拒绝, 退出应用
                .setNegativeButton(R.string.common_text_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })

                .setPositiveButton(R.string.setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //跳转GPS设置界面
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, GPS_REQUEST_CODE);
                            }
                        })

                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (!isLocationEnabled()) {
            openGPSSettings();
        }
        return false;
    }
}
