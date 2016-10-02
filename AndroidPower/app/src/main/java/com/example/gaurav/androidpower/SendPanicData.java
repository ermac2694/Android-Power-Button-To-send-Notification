package com.example.gaurav.androidpower;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Gaurav on 6/19/2016.
 */

public class SendPanicData extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Session session;

    private Location location;

    TextView numberView, imeiView, intentView;
    String nVText, iVText, token;
    private TelephonyManager telephonyManager;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MainActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    ProgressDialog pDialog;
    private String panicUrl = "http://api.geektop10.xyz/app/panicRequest.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        session = new Session(getApplicationContext());

        if(session.isset() == true) {
            String value = session.getusertoken();
            token = session.getusertoken();
            //intentView.setText(value);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20000)
                .setFastestInterval(1000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent in = new Intent(this, PhoneActivity.class);
            startActivity(in);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        Log.i("onConfigurationChanged", "Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Connectttteddd!!!!!!!!!");



        Intent in = getIntent();
        String from = in.getStringExtra("openfrom");

        if(from != null) {
            Log.e("yippi>>>>>>>>>>>>>>", from);
        }
        String Avail = LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).toString();
        Log.e("Location == " , Avail);
        if(location == null){
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//            numberView.setText("Location == " + Avail);
            Log.e("Location == " , Avail);
        }
        else{
            handleNewLocation(location);
        }
        Log.e("Token>>>>>>>", token);
        Log.e("Token>>>>>>>", token);
        new sendPanicRequest().execute(panicUrl,
                token,
                telephonyManager.getDeviceId(),
                String.valueOf(location.getLongitude()),
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getAccuracy()));
    }

    private void handleNewLocation(Location location) {
        Log.e("LOCATION>>>>>>>>>>>", location.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Pleassssseeee ReeeeConnecttt!!!!!!!!!");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                //Start an Activity that tries to solve error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.i(TAG, "Location Services Connection failed. Error Code: " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    private class sendPanicRequest extends AsyncTask<String, String, Integer> {

        HttpURLConnection urlConnection;
        private String dUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(SendPanicData.this);
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            InputStream inputStream = null;

            Integer result = 0;

            try {
                //dUrl = URLEncoder.encode(dUrl, "UTF-8");
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("token", params[1])
                        .appendQueryParameter("imei", params[2])
                        .appendQueryParameter("longitude", params[3])
                        .appendQueryParameter("latitude", params[4])
                        .appendQueryParameter("accuracy", params[5]);
                String query = builder.build().getEncodedQuery();

                Log.d("helo string", builder.toString());
                Log.d("helo string", query);
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                urlConnection.connect();

                int statusCode = urlConnection.getResponseCode();
                /* 200 represents HTTP OK */
                if (statusCode ==  200) {

                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);

                    if(parseResult(response) == true) {

                        result = 1; // Successful
                    }

                }else{
                    result = 0; //"Failed to fetch data!";

                    pDialog.dismiss();
                    Toast.makeText(SendPanicData.this, "WIFI not working", Toast.LENGTH_SHORT).show();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            pDialog.dismiss();
            if (result == 1) {
                Toast.makeText(SendPanicData.this, "Deal Created", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {

        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));

        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null){
            result += line;
        }

            /* Close Stream */
        if(null!=inputStream){
            inputStream.close();
        }

        Log.e("JSON DATA", result);

        return result;
    }
    private boolean parseResult(String result) {

        try{
//            JSONArray arr = new JSONArray();

            JSONObject user = new JSONObject(result);

            Log.e("Values>>>>>>>>>>" , user.toString());

            String message = user.optString("message");
            int code = user.optInt("code");
            if(code == 1) {
//                ToastHandler mToastHandler = new ToastHandler(getApplicationContext());
//                mToastHandler.showToast("Panic Request Sent!!!!", Toast.LENGTH_LONG);
                Intent back = new Intent(this, MainActivity.class);
                startActivity(back);
            }
            if(code == 0) {
//                ToastHandler mToastHandler = new ToastHandler(getApplicationContext());
//                mToastHandler.showToast("Error!!!!", Toast.LENGTH_LONG);
                //Toast.makeText(getApplicationContext(), "Error!!!!", Toast.LENGTH_LONG).show();
                Intent back = new Intent(this, MainActivity.class);
                startActivity(back);
            }

//            if(cd == 1) {
//                token = user.optString("token");
//                Log.e("Code>>>>>>>>>>" , token);
//                session.setusertoken(token);
//                Intent main = new Intent(PhoneActivity.this, MainActivity.class);
//                startActivity(main);
//                finish();
//            }

            //JSONArray activated = deals.optJSONArray("activatedDeals");
//            JSONArray updl = dealup.optJSONArray("res");
//
//            for(int i=0; i< updl.length();i++ ){
//                JSONObject Dealupdate = updl.optJSONObject(i);
//                int value = Dealupdate.optInt("value");
//                String msg = Dealupdate.optString("message");
//                if (value == 1 && msg.contains("created")){
//                    return true;
//                }
//            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        return false;
    }
}

