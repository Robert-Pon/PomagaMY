package com.name.social_helper_r_p.user;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.name.social_helper_r_p.R;
import com.name.social_helper_r_p.connections.Data;
import com.name.social_helper_r_p.connections.Fetch;
import com.name.social_helper_r_p.connections.Request;
import com.name.social_helper_r_p.connections.RequestData;
import com.name.social_helper_r_p.connections.Return;
import com.name.social_helper_r_p.connections.URLS;
import com.name.social_helper_r_p.user.add.AddBottomFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment {

    AddBottomFragment addBottomFragment = new AddBottomFragment();

    CardView add;
    CardView refresh;

    URLS urls = new URLS();
    Fetch fetch = new Fetch();
    SupportMapFragment mapFragment;

    boolean firstTime = false;

    CardView filters;

    FiltersBottom filtersBottom = new FiltersBottom();

    GoogleMap googleMapOut;

    TextView search;

    SearchBottom searchBottom = new SearchBottom();
    List<Integer> types = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});

    Animation animation = null;

    ImageView restart;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map, container, false);

        add = v.findViewById(R.id.image);
        filters = v.findViewById(R.id.type);
        refresh = v.findViewById(R.id.refresh);
        restart = v.findViewById(R.id.restart);

        animation = AnimationUtils.loadAnimation(getContext(), R.anim.rotation);
        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);



            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            firstTime = true;
            search = v.findViewById(R.id.search);
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {

                    if (savedInstanceState != null) {
                        CameraUpdate cameraPos = CameraUpdateFactory.newCameraPosition(
                                (CameraPosition)(savedInstanceState.getParcelable("mapview_bundle_key")));
                        googleMap.moveCamera(cameraPos);
                    }else{
                        CameraPosition position = new CameraPosition.Builder().target(new LatLng(51.9189046, 19.1343786)).zoom(5).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                    }

                    System.out.println(googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude);

                    googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            googleMapOut = googleMap;
                            RequestData data = new RequestData();
                            data.setData(new Data("up", String.valueOf(googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude)));
                            data.setData(new Data("down", String.valueOf(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.latitude)));
                            data.setData(new Data("left", String.valueOf(googleMap.getProjection().getVisibleRegion().latLngBounds.southwest.longitude)));
                            data.setData(new Data("right", String.valueOf(googleMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude)));
                            restart.setAnimation(animation);
                            Thread send = new Thread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void run() {

                                    Request request = new Request(urls.URL(), urls.getAllFromMap(), "POST", "POST");


                                    Return response = fetch.fetch(request, data);

                                    switch (response.getType()) {
                                        case 0:
                                            JSONObject json = (JSONObject) response.getObject();
                                            try {
                                                JSONObject selected = json.getJSONObject("selected");

                                                try {
                                                    JSONArray array = selected.getJSONArray("type_1");
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int i = 0; i < array.length(); i++) {
                                                                try {
                                                                    JSONObject object = (JSONObject) array.get(i);
                                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                                                    Bundle data = new Bundle();
                                                                    data.putString("id", object.getString("_id"));
                                                                    data.putString("type", "1");
                                                                    data.putString("types", types.toString());

                                                                    marker.setTag(data);

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e){

                                                }
                                               try {
                                                    JSONArray array = selected.getJSONArray("type_2");
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int i = 0; i < array.length(); i++) {
                                                                try {
                                                                    JSONObject object = (JSONObject) array.get(i);
                                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                                    Bundle data = new Bundle();
                                                                    data.putString("id", object.getString("_id"));
                                                                    data.putString("type", "2");
                                                                    data.putString("types", types.toString());

                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                                    marker.setTag(data);

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e){

                                                }
                                               try {
                                                    JSONArray array = selected.getJSONArray("type_3");
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int i = 0; i < array.length(); i++) {
                                                                try {
                                                                    JSONObject object = (JSONObject) array.get(i);
                                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                                    Bundle data = new Bundle();
                                                                    data.putString("id", object.getString("_id"));
                                                                    data.putString("type", "3");
                                                                    marker.setTag(data);
                                                                    data.putString("types", types.toString());

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e) {

                                               }
                                                try {
                                                    JSONArray array = selected.getJSONArray("type_4");
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int i = 0; i < array.length(); i++) {
                                                                try {
                                                                    JSONObject object = (JSONObject) array.get(i);
                                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                                    Bundle data = new Bundle();
                                                                    data.putString("id", object.getString("_id"));
                                                                    data.putString("type", "4");
                                                                    marker.setTag(data);
                                                                    data.putString("types", types.toString());

                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e){

                                                }
                                                try {
                                                    JSONArray array = selected.getJSONArray("type_5");
                                                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            for (int i = 0; i < array.length(); i++) {
                                                                try {
                                                                    JSONObject object = (JSONObject) array.get(i);
                                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                                    Bundle data = new Bundle();
                                                                    data.putString("id", object.getString("_id"));
                                                                    data.putString("type", "5");
                                                                    data.putString("types", types.toString());

                                                                    marker.setTag(data);
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }catch (Exception e){

                                                }


                                            } catch (Exception e) {

                                            }

                                            System.out.println(response.getObject());
                                            break;
                                    }
                                    if(getContext()!=null){
                                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                animation.cancel();
                                            }
                                        });
                                    }


                                }
                            });
                            send.start();
                        }
                    });


                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker marker) {
                                    AnnouncementsBottom announcementsBottom = new AnnouncementsBottom();
                                    System.out.println(marker.getId());
                                    System.out.println(marker.getTag().toString());
                                    Bundle data = (Bundle) marker.getTag();
                                    announcementsBottom.setArguments(data);
                                    announcementsBottom.show(getChildFragmentManager(), "D");
                                    return false;
                                }
                            });



                }
            });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilters(types);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBottomFragment.show(getChildFragmentManager(), "d");
            }
        });

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtersBottom.show(getChildFragmentManager(), "d");


            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchBottom.show(getChildFragmentManager(), "d");
            }
        });

        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setFilters(List<Integer> types) {
        if(googleMapOut!=null) {
            this.types = types;
            restart.startAnimation(animation);
            JSONObject filters = new JSONObject();
            JSONArray array = new JSONArray();
            types.stream().forEach(e -> {
                array.put(e);
            });
            try {
                filters.put("main_types", array);
            } catch (Exception e) {

            }
            RequestData data = new RequestData();
            data.setData(new Data("up", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.northeast.latitude)));
            data.setData(new Data("down", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.southwest.latitude)));
            data.setData(new Data("left", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.southwest.longitude)));
            data.setData(new Data("right", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.northeast.longitude)));
            data.setData(new Data("filters", filters.toString()));
            Thread send = new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {

                    Request request = new Request(urls.URL(), urls.getWithFilters(), "POST", "POST");


                    Return response = fetch.fetch(request, data);

                    switch (response.getType()) {
                        case 0:
                            JSONObject json = (JSONObject) response.getObject();
                            System.out.println(json);
                            try {
                                JSONObject selected = json.getJSONObject("selected");

                                ((Activity) getContext()).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        googleMapOut.clear();

                                        try {
                                            JSONArray array1 = selected.getJSONArray("type_1");
                                            for (int i = 0; i < array1.length(); i++) {
                                                try {
                                                    JSONObject object = array1.getJSONObject(i);
                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                    Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                                    Bundle data = new Bundle();
                                                    data.putString("id", object.getString("_id"));
                                                    data.putString("type", "1");
                                                    data.putString("types", types.toString());
                                                    marker.setTag(data);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
                                        try {
                                            JSONArray array1 = selected.getJSONArray("type_2");
                                            for (int i = 0; i < array1.length(); i++) {
                                                try {
                                                    JSONObject object = array1.getJSONObject(i);
                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                    Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                    Bundle data = new Bundle();
                                                    data.putString("id", object.getString("_id"));
                                                    data.putString("type", "2");
                                                    data.putString("types", types.toString());
                                                    marker.setTag(data);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
                                        try {
                                            JSONArray array1 = selected.getJSONArray("type_3");
                                            for (int i = 0; i < array1.length(); i++) {
                                                try {
                                                    JSONObject object = array1.getJSONObject(i);
                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                    Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                    Bundle data = new Bundle();
                                                    data.putString("id", object.getString("_id"));
                                                    data.putString("type", "3");
                                                    data.putString("types", types.toString());

                                                    marker.setTag(data);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
                                        try {
                                            JSONArray array1 = selected.getJSONArray("type_4");
                                            for (int i = 0; i < array1.length(); i++) {
                                                try {
                                                    JSONObject object = array1.getJSONObject(i);
                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                    Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                    Bundle data = new Bundle();
                                                    data.putString("id", object.getString("_id"));
                                                    data.putString("type", "4");
                                                    data.putString("types", types.toString());
                                                    marker.setTag(data);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
                                        try {
                                            JSONArray array1 = selected.getJSONArray("type_5");
                                            for (int i = 0; i < array1.length(); i++) {
                                                try {
                                                    JSONObject object = array1.getJSONObject(i);
                                                    LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                    Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                    Bundle data = new Bundle();
                                                    data.putString("id", object.getString("_id"));
                                                    data.putString("type", "5");
                                                    data.putString("types", types.toString());
                                                    marker.setTag(data);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } catch (Exception e) {

                                        }
//                                    for (int i = 0; i < array.length(); i++) {
//                                        try {
//                                            JSONObject object = (JSONObject) array.get(i);
//                                            LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
//                                            Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position));
//                                            marker.setTag(object.getString("_id"));
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }

                                        animation.cancel();
                                    }
                                });

                            } catch (Exception e) {

                            }

                            System.out.println(response.getObject());
                            break;
                    }

                }
            });
            send.start();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendSearch(JSONObject object){
        RequestData data = new RequestData();
        data.setData(new Data("up", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.northeast.latitude)));
        data.setData(new Data("down", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.southwest.latitude)));
        data.setData(new Data("left", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.southwest.longitude)));
        data.setData(new Data("right", String.valueOf(googleMapOut.getProjection().getVisibleRegion().latLngBounds.northeast.longitude)));
        data.setData(new Data("search", object.toString()));
try {
    String tA = object.getString("types").substring(1, object.getString("types").length()-1);
    types = new ArrayList<>();
    Arrays.asList(tA.split(",")).stream().forEach(e->{
        types.add(Integer.valueOf(e));
    });
}catch (Exception e){

}
        Thread send = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {

                Request request = new Request(urls.URL(), urls.search(), "POST", "POST");


                Return response = fetch.fetch(request, data);

                switch (response.getType()) {
                    case 0:
                        JSONObject json = (JSONObject) response.getObject();
                        System.out.println(json);
                        try {
                            JSONObject selected = json.getJSONObject("selected");

                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    googleMapOut.clear();

                                    try {
                                        JSONArray array1  = selected.getJSONArray("type_1");
                                        for (int i = 0; i < array1.length(); i++) {
                                            try {
                                                JSONObject object = array1.getJSONObject(i);
                                                LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                                Bundle data = new Bundle();
                                                data.putString("id", object.getString("_id"));
                                                data.putString("type", "1");
                                                data.putString("types", types.toString());
                                                marker.setTag(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }catch (Exception e){

                                    }
                                    try {
                                        JSONArray array1  = selected.getJSONArray("type_2");
                                        for (int i = 0; i < array1.length(); i++) {
                                            try {
                                                JSONObject object = array1.getJSONObject(i);
                                                LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                Bundle data = new Bundle();
                                                data.putString("id", object.getString("_id"));
                                                data.putString("type", "2");
                                                data.putString("types", types.toString());
                                                marker.setTag(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }catch (Exception e){

                                    }
                                    try {
                                        JSONArray array1  = selected.getJSONArray("type_3");
                                        for (int i = 0; i < array1.length(); i++) {
                                            try {
                                                JSONObject object = array1.getJSONObject(i);
                                                LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                                                Bundle data = new Bundle();
                                                data.putString("id", object.getString("_id"));
                                                data.putString("type", "3");
                                                data.putString("types", types.toString());
                                                marker.setTag(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }catch (Exception e){

                                    }
                                    try {
                                        JSONArray array1  = selected.getJSONArray("type_4");
                                        for (int i = 0; i < array1.length(); i++) {
                                            try {
                                                JSONObject object = array1.getJSONObject(i);
                                                LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                Bundle data = new Bundle();
                                                data.putString("id", object.getString("_id"));
                                                data.putString("type", "4");
                                                data.putString("types", types.toString());
                                                marker.setTag(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }catch (Exception e){

                                    }
                                    try {
                                        JSONArray array1  = selected.getJSONArray("type_5");
                                        for (int i = 0; i < array1.length(); i++) {
                                            try {
                                                JSONObject object = array1.getJSONObject(i);
                                                LatLng position = new LatLng(object.getDouble("lat"), object.getDouble("lon"));
                                                Marker marker = googleMapOut.addMarker(new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                Bundle data = new Bundle();
                                                data.putString("id", object.getString("_id"));
                                                data.putString("type", "5");
                                                data.putString("types", types.toString());
                                                marker.setTag(data);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }catch (Exception e){

                                    }
                                }
                            });

                        } catch (Exception e) {

                        }

                        System.out.println(response.getObject());
                        break;
                }

            }
        });
        send.start();
    }

}
