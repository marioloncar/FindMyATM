package com.mario.findmyatm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mario on 12/20/14.
 */

public class MapsActivity extends AppCompatActivity {
    private String[] mBankNames;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;  //title of the app
    private ActionBarDrawerToggle mDrawerToggle;

    LatLng rijeka = new LatLng(45.335028, 14.442368);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mario.findmyatm.R.layout.activity_maps);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        assert getSupportActionBar() != null;

        //get bank names from String.xml
        mBankNames = getResources().getStringArray(com.mario.findmyatm.R.array.banks_array);
        //navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(com.mario.findmyatm.R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(com.mario.findmyatm.R.id.left_drawer);

        //ActionBar title
        mTitle = getTitle();

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                com.mario.findmyatm.R.layout.drawer_list_item, mBankNames));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                com.mario.findmyatm.R.string.drawer_open,
                com.mario.findmyatm.R.string.drawer_close) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Choose a bank");
                checkConnectivity();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.openDrawer(mDrawerList);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Swaps fragments in the main content view
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mBankNames[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        switch (position) {
            case 0: //Banco Popolare
                mMap.clear();
                LatLng camera_banco = new LatLng(45.326666, 14.444808);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_banco, 17));
                AlertDialog.Builder builderBanco = new AlertDialog.Builder(this);
                builderBanco.setCancelable(true);
                builderBanco.setTitle("Did you know?");
                builderBanco.setMessage(R.string.bancoPopolare_alert);
                builderBanco.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builderBanco.create();
                dialog.show();
                Banks banco = new Banks();
                banco.bancoPopolare();
                break;

            case 1: //BKS
                mMap.clear();
                LatLng camera_bks = new LatLng(45.326806, 14.444040);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_bks, 17));
                Banks bksBank = new Banks();
                bksBank.bks();
                break;

            case 2: //Croatia banka
                mMap.clear();
                LatLng camera_cro = new LatLng(45.325389, 14.442115);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_cro, 17));
                Banks croatiaBank = new Banks();
                croatiaBank.croatia();
                break;

            case 3: //Erste bank
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks ersteBank = new Banks();
                ersteBank.erste();
                break;

            case 4: //Postanska banka
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks postanska = new Banks();
                postanska.hpb();
                break;

            case 5: //Hypo Alpe Adria Bank
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks hypoAlpe = new Banks();
                hypoAlpe.hypo();
                break;

            case 6: //Imex bank
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                AlertDialog.Builder builderImex = new AlertDialog.Builder(this);
                builderImex.setCancelable(true);
                builderImex.setTitle("Did you know?");
                builderImex.setMessage(R.string.imex_alert);
                builderImex.setPositiveButton("Choose a bank included in the list above", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDrawerLayout.openDrawer(mDrawerList);
                        dialog.dismiss();
                    }
                });
                AlertDialog dialogImex = builderImex.create();
                dialogImex.show();
                break;

            case 7: //Istarska Kreditna Banka
                mMap.clear();
                LatLng camera_ikb = new LatLng(45.326305, 14.444366);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_ikb, 17));
                Banks istarska = new Banks();
                istarska.ikb();
                break;

            case 8: //Karlovačka banka
                mMap.clear();
                LatLng camera_kar = new LatLng(45.329878, 14.438692);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_kar, 17));
                Banks karlovacka = new Banks();
                karlovacka.kaba();
                break;

            case 9: //Kreditna Banka Zagreb
                mMap.clear();
                LatLng camera_kbz = new LatLng(45.332805, 14.434752);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_kbz, 14));
                Banks kreditnaZg = new Banks();
                kreditnaZg.kbz();
                break;

            case 10: //OTP banka
                mMap.clear();
                LatLng camera_otp = new LatLng(45.332805, 14.434752);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_otp, 14));
                Banks otpBank = new Banks();
                otpBank.otp();
                break;

            case 11: //Partner banka
                mMap.clear();
                LatLng camera_par = new LatLng(45.325237, 14.445017);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_par, 17));
                AlertDialog.Builder builderPartner = new AlertDialog.Builder(this);
                builderPartner.setCancelable(true);
                builderPartner.setTitle("Did you know?");
                builderPartner.setMessage(R.string.paba_alert);
                builderPartner.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialogPartner = builderPartner.create();
                dialogPartner.show();
                Banks partner = new Banks();
                partner.paba();
                break;

            case 12: //Podravska banka
                mMap.clear();
                LatLng camera_poba = new LatLng(45.332805, 14.434752);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_poba, 14));
                Banks podravska = new Banks();
                podravska.poba();
                break;

            case 13: //Privredna Banka Zagreb
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks privredna = new Banks();
                privredna.pbz();
                break;


            case 14: //Raiffeisen bank
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks raiffeisen = new Banks();
                raiffeisen.rba();
                break;

            case 15: //Sberbank
                mMap.clear();
                LatLng camera_sber = new LatLng(45.327424, 14.443966);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_sber, 17));
                Banks sber = new Banks();
                sber.sberbank();
                break;

            case 16: //Slatinska banka
                mMap.clear();
                LatLng camera_slat = new LatLng(45.325371, 14.445601);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_slat, 17));
                Banks slatinskaB = new Banks();
                slatinskaB.slatinska();
                break;


            case 17: //Splitska banka
                mMap.clear();
                LatLng camera_st = new LatLng(45.327864, 14.447906);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_st, 14));
                Banks split = new Banks();
                split.splitska();
                break;

            case 18: //Veneto banka
                mMap.clear();
                LatLng camera_ven = new LatLng(45.327231, 14.438525);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(camera_ven, 17));
                Banks venetoBanka = new Banks();
                venetoBanka.veneto();
                break;

            case 19: //Zagrebačka banka
                mMap.clear();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
                Banks zagrebacka = new Banks();
                zagrebacka.zaba();
                break;
        }
    }

    //method for setting title
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater meni = getMenuInflater();
        meni.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //If location services are disabled, this AlertDialog will pop up
    private void location() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location services disabled");
        builder.setMessage("Google Maps needs access to your location. Please turn on location access to get your current location.");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                //builder.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Method for checking connectivity
    public void checkConnectivity() {
        if (isNetworkAvailable()) {
            setUpMapIfNeeded();
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        //All location services are disabled
                        location();
                    }
                    return false;
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("No internet connection");
            builder.setMessage("Please check your internet connection and try again.");
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkConnectivity(); //ponovno provjeravanje
                    dialog.dismiss();

                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNeutralButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private boolean isNetworkAvailable() {
        // Using ConnectivityManager to check for Network Connection
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(com.mario.findmyatm.R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rijeka, 14));
    }

    //get data from Parse, go through list, set markers
    public class Banks {
        private void bancoPopolare() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("kEqLoZmn8Z", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.banco))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Banco Popolare", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void bks() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("xHkyrXjBTB", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.bks))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("BKS", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void croatia() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM"); //spajanje s klasom na Parseu
            query.getInBackground("RBuBuEu2bT", new GetCallback<ParseObject>() { //dohvacanje objekta prema ID-u
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo"); //dohvaca JSON polje sa Parsea
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString()); //spremanje dohvacenih podataka
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) { //prolazi kroz svaki String liste
                            String[] s2 = s.split(","); //razlamanje po zarezima
                            String adresa = s2[0]; //spremanje imena
                            Float lat = Float.parseFloat(s2[1]); //spremanje sirine, parsiranje string u float
                            Float lng = Float.parseFloat(s2[2]); //spremanje duzine,, parsiranje string u float
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.croatia))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Croatia banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void erste() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("U0IZhZLpwT", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.erste))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Erste banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void hpb() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("7L5pwcUylL", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.hpb))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("HPB", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void hypo() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("sSHwl21Dvr", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.hypo))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Hypo Alpe Adria Bank", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void ikb() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("KFhRxYgGmX", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.ikb))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Istarska Kreditna Banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void kaba() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("AKLfmoD6qW", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.kaba))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Karlovačka banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void kbz() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("4DCOoZve2U", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.kbz))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Kreditna banka Zagreb", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void otp() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("egT8IgXc5R", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.otp))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("OTP banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void paba() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("CvMQ4N35E2", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.paba))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Partner banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void pbz() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("UOEc9OaCIm", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.pbz))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Privredna Banka Zagreb", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void poba() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("MRB5rSImOl", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.poba))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Podravska banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void rba() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("R3p6mKOu2U", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.raiffeisen))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Raiffeisen bank", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void slatinska() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("JmGNzNbcAk", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.slatinska))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Slatinska banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void splitska() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("adSDpX3AIA", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.splitska))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Splitska banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void sberbank() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("GKYsoU95c7", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.sberbank))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Sberbank", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void veneto() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("kVUv0tiGch", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.veneto))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Veneto banka", "Error: " + e.getMessage());
                    }
                }
            });
        }

        private void zaba() {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("FindMyATM");
            query.getInBackground("L2IssXYgJM", new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ArrayList<String> dohvaceno = new ArrayList<String>();
                        JSONArray poljeParse = object.getJSONArray("addr_coo");
                        if (poljeParse != null) {
                            for (int i = 0; i < poljeParse.length(); i++) {
                                try {
                                    dohvaceno.add(poljeParse.get(i).toString());
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        for (String s : dohvaceno) {
                            String[] s2 = s.split(",");
                            String adresa = s2[0];
                            Float lat = Float.parseFloat(s2[1]);
                            Float lng = Float.parseFloat(s2[2]);
                            LatLng koo = new LatLng(lat, lng);
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(com.mario.findmyatm.R.drawable.zaba))
                                    .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                    .title(adresa)
                                    .position(koo));
                        }
                    } else {
                        Log.d("Zagrebačka banka", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
