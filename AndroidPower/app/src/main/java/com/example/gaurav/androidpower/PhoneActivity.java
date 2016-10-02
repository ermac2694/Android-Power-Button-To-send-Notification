package com.example.gaurav.androidpower;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

public class PhoneActivity extends AppCompatActivity {

    private EditText myNo, cNo1, cNo2, cNo3;
    private String myNoText, cNo1Text, cNo2Text, cNo3Text, imeiNo, token;
    private TelephonyManager telephonyManager;

    private int cd;
    private Session session;

    ProgressDialog pDialog;
//    private String registerUrl = "http://api.geektop10.xyz/app/register.php";
private String registerUrl = "http://api.geektop10.xyz/app/registerUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new Session(getApplicationContext());

        myNo = (EditText) findViewById(R.id.myPhoneEditText);
        cNo1 = (EditText) findViewById(R.id.contactOneEditText);
        cNo2 = (EditText) findViewById(R.id.contactTwoEditText);
        cNo3 = (EditText) findViewById(R.id.contactThreeEditText);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imeiNo = telephonyManager.getDeviceId();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RegisterPhone().execute(registerUrl,
                                            imeiNo,
                                            myNo.getText().toString(),
                                            cNo1.getText().toString(),
                                            cNo2.getText().toString(),
                                            cNo3.getText().toString());
            }
        });
    }

    private class RegisterPhone extends AsyncTask<String, String, Integer> {

        HttpURLConnection urlConnection;
        private String dUrl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(PhoneActivity.this);
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
                        .appendQueryParameter("imei", params[1])
                        .appendQueryParameter("pNumber", params[2])
                        .appendQueryParameter("supportContact1", params[3])
                        .appendQueryParameter("supportContact2", params[4])
                        .appendQueryParameter("supportContact3", params[5]);
                String query = builder.build().getEncodedQuery();

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

                    Toast tst = new Toast(PhoneActivity.this);
                    tst.setText("WIFI not working");
                    tst.show();
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
                Toast.makeText(PhoneActivity.this, "Created", Toast.LENGTH_SHORT).show();
                Intent go = new Intent(PhoneActivity.this, MainActivity.class);
                go.putExtra("Token", token);
                startActivity(go);
                finish();
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

            cd = user.optInt("code");

            if(cd == 1) {
                token = user.optString("token");
                Log.e("Code>>>>>>>>>>" , token);
                session.setusertoken(token);
                Intent main = new Intent(PhoneActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }

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
