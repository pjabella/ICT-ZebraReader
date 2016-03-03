package com.example.pj.zebrareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class idChecker extends AppCompatActivity {

    Button save, cancel;
    EditText etIdNo;
    String idNum;
    String result;
    TextView ip;
    EditText pS;
    String hav;
    private static final int RBS = 1000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_checker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        etIdNo = (EditText) this.findViewById(R.id.editText);
        save = (Button) this.findViewById(R.id.button);
        cancel = (Button) this.findViewById(R.id.button2);
        ip = (TextView) this.findViewById(R.id.textView8);

        ip.setText(Sessions.getIpAddId());




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String haha = ip.getText().toString();
                idNum = etIdNo.getText().toString();
                if (haha.equals("IP Address Not Set!")) {
                    noIp();
                } else
                    DataCatch();
                    //new AccessDB(getApplicationContext()).execute();
            }
        });

        Toast.makeText(this, Sessions.getIpAddId(), Toast.LENGTH_SHORT).show();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    public void DataCatch() {
        try {
            Toast.makeText(getApplicationContext(),"DidCome",Toast.LENGTH_SHORT).show();

            String idno = idNum;

            URL url = new URL("http://" + Sessions.getIpAddId() + idno );
            //Toast.makeText(getApplicationContext(),url.toString(),Toast.LENGTH_LONG).show();

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            int c = 0;
            StringBuffer sb = new StringBuffer();

            while ((c = is.read()) != -1) {
                sb.append((char) c);
            }
            is.close();
            conn.disconnect();

            Log.d("json", sb.toString());

            JSONObject json = new JSONObject(sb.toString());

            JSONArray jarr = json.getJSONArray("student");

            for (int i = 0; i < jarr.length(); i++) {

                JSONObject item = jarr.getJSONObject(i);
                String s = item.getString("ticketNo") + " " + item.getString("familyName") + " " + item.getString("firstName")+" - "+item.getString("campus") ;

                //Toast.makeText(getApplicationContext(),"String : "+s,Toast.LENGTH_LONG).show();

                if (!s.equals("Student Not Registered")) {
                    hav = s;
                    if(checkre()==true){

                        alertshow();
                        saveCode();
                        Toast.makeText(this, "TO Save", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        repeatCheck();
                    }

                } else
                    Toast.makeText(this, "ID not found!", Toast.LENGTH_SHORT).show();


            }


        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException m) {
            // TODO Auto-generated catch block
            m.printStackTrace();
        } catch (JSONException s) {
            // TODO Auto-generated catch block
            s.printStackTrace();
        }
    }//end of DataCatch

 /*   private class AccessDB extends AsyncTask<String, Integer, String> {

        Context context;

        public AccessDB(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            result = "";

            //etIdNo.getText().toString()
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://" + Sessions.getIpAddId() + idNum);

                HttpResponse response = client.execute(httpget);

                result = EntityUtils.toString(response.getEntity());
                //Toast.makeText(getApplicationContext(),"COnnected?",Toast.LENGTH_SHORT).show();

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            //Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            String[] err = result.split("\\ ");
            String error = err[0];


            if (!(result.equals("")||result.equals("Student Not Registered"))){
                 //str = result.split(",");
                hav = result;
                if(checkre()==true){
                    alertshow();
                    saveCode();
                    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    repeatCheck();
                }


                //Toast.makeText(getApplicationContext(),idNum,Toast.LENGTH_SHORT).show();
                Toast.makeText(context,  "Check Success", Toast.LENGTH_SHORT).show();
            }
            else if(result.equals("")){
                etIdNo.setError("ID Number is Required");
            }
            else
            Toast.makeText(context, "ID not found!", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(),idNum,Toast.LENGTH_SHORT).show();
            Log.d("test connection", result);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

    }*/

    public void alertshow(){
        AlertDialog.Builder aler = new AlertDialog.Builder(this);
        aler.setTitle("Success ID Saved");
        //aler.setMessage("content 0");
        aler.setMessage(hav);
        /*aler.setMessage("content 1");
        aler.setMessage(str[1].toString());
        aler.setMessage("content 2");
        aler.setMessage(str[2].toString());
        aler.setMessage("content 3");
        aler.setMessage(str[3].toString());
        aler.setMessage("content 4");
        aler.setMessage(str[3].toString());*/
        aler.setNeutralButton("Ok", null);
        AlertDialog alerted = aler.create();
        alerted.show();
    }
    public void repeatCheck(){
        AlertDialog.Builder aler = new AlertDialog.Builder(this);
        aler.setTitle("ID Already Saved !");
        aler.setMessage(hav);
        aler.setNeutralButton("Ok", null);
        AlertDialog alerted = aler.create();
        alerted.show();
    }

    public void noIp(){
        pS = new EditText(this);
        AlertDialog.Builder aler = new AlertDialog.Builder(this);
        aler.setTitle("Please Set IP Address !");
        aler.setMessage("IP Address was not set! Please provide the server IP here of on the Settings.");
        aler.setView(pS);
        aler.setCancelable(false);
        aler.setNeutralButton("Ok",  new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!pS.getText().toString().equals(""))
                {
                    Sessions.setIpAddId(pS.getText().toString());
                    ip.setText(Sessions.getIpAddId());
                    dialog.dismiss();
                }
                else {
                    Sessions.setIpAddId("IP Address Not Set! ");
                    Toast.makeText(getApplicationContext(), "Please enter IP address!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), settings.class));
                }
            }
        });
        AlertDialog alerted = aler.create();
        alerted.show();
    }

    public boolean checkre() {
        try {
            FileInputStream fin = openFileInput(Sessions.getFilename());
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            char[] inputBuffer = new char[RBS];
            int c = 0;
            String temp = "";
            String datdat = "";

            if (isr != null) {
                while ((temp = reader.readLine()) != null) {
                    datdat = temp;
                    String split[] = datdat.split(">");

                    while (c < split.length) {
                        //listItems.add(split[c]);
                        if (split[c].equals(hav)) {
                            return false;
                        }
                        c++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Not Redundant", Toast.LENGTH_SHORT).show();
        return true;
    }

    public void saveCode(){
        String dat = hav;
        String data = dat;

        try {
            FileOutputStream fOut = openFileOutput(Sessions.getFilename(), this.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data+">");
            osw.flush();
            osw.close();
            //fOut.write(data.getBytes());
            //fOut.close();
            Toast.makeText(this,"File Saved", Toast.LENGTH_SHORT).show();
            MainActivity.listItems.add(data);
            MainActivity.adapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
