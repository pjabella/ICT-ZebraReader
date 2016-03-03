package com.example.pj.zebrareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.pj.zebrareader.IntentResult.getContents;
import static com.example.pj.zebrareader.IntentResult.getFormatName;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<String> listItems = new ArrayList<String>();
    static ArrayAdapter<String> adapter;
    public ListView mListView;
    String etUsername = "hh1001";
    String etPassword = "wingingit";
    Button refresh;
    EditText etIpQr, etIpId;
    static String con, det, hav;
    TextView count;
    SwipeRefreshLayout swipeLayout;
    static String data;
    private static final int RBS = 1000;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    //private String file = "mydata";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Openning Zxing Barcode Scanner", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                doIntent();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        refresh = (Button) findViewById(R.id.button3);
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                reload();
//            }
//        });

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeLayout.setRefreshing(false);
                        reload();
                    }
                }, 2000);
            }
        });
        //swipeLayout.setColorSchemeColors(android.R.color.holo_blue_bright,android.R.color.holo_green_light,android.R.color.holo_orange_light,android.R.color.holo_red_light);

        Sessions.setFilename("ict2016.txt");

        etIpQr = new EditText(this);
        etIpQr.setText("/nihongoict2016/qrcheck.php?id=");
        etIpId = new EditText(this);
        etIpId.setText("/nihongoict2016/idcheck.php?idno=");


        new AlertDialog.Builder(this)
                .setMessage("Enter serverIP address")
                .setTitle("IP Address for QR Checker")
                .setView(etIpQr)
                //.setView(etIpId)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        if (!etIpQr.getText().toString().equals("")) {
                            Sessions.setIpAddQr(etIpQr.getText().toString());
                            //Sessions.setIpAddId(etIpId.getText().toString());
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "QrChecker IP: " + etIpQr.getText().toString() + "ID Checker IP: " + etIpId.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Sessions.setIpAddQr("IP Address Not Set!");
                            //Sessions.setIpAddId("IP Address Not Set!");
                            Toast.makeText(getApplicationContext(), "Please enter IP address!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();

        new AlertDialog.Builder(this)
                .setMessage("Enter server IP address")
                .setTitle("IP Address For ID Checker")
                //.setView(etIpQr)
                .setView(etIpId)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                        if (!etIpId.getText().toString().equals("")) {
                            //Sessions.setIpAddQr(etIpQr.getText().toString());
                            Sessions.setIpAddId(etIpId.getText().toString());
                            dialog.dismiss();
                            //Toast.makeText(getApplicationContext(), "IdChecker IP: " + etIpId.getText().toString() + "ID Checker IP: " + etIpId.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            //Sessions.setIpAddQr("IP Address Not Set!");
                            Sessions.setIpAddId("IP Address Not Set!");
                            Toast.makeText(getApplicationContext(), "Please enter IP address!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();



        count = (TextView) findViewById(R.id.regstud);

        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.listView);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems);
        setListAdapter(adapter);
        reload();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    public void repeatCheck(){
        AlertDialog.Builder aler = new AlertDialog.Builder(this);
        aler.setTitle("ID Already Saved !");
        aler.setMessage(hav);
        aler.setNeutralButton("Ok", null);
        AlertDialog alerted = aler.create();
        alerted.show();
    }

    public void DataCatch() {
        try {
           // Toast.makeText(getApplicationContext(),"DidCome",Toast.LENGTH_SHORT).show();

            String[] idnumber = con.split("\\/");
            String idno = idnumber[4];
            String campus = idnumber[5];
            //String[] ako = con.split("=");
            URL url = new URL("http://" + Sessions.getIpAddQr() + idno + "&campus="+ campus);
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
                String status = item.getString("attended");
                //Toast.makeText(getApplicationContext(),"String : "+s,Toast.LENGTH_LONG).show();

                if (!s.equals("Student Not Registered")) {
                    hav = s;
                    if(checkre()==true){
                     if(status.equals("0")) {
                         saveCode();
                         //Toast.makeText(this, "TO Save", Toast.LENGTH_SHORT).show();
                     }else  Toast.makeText(this, "ID Already Scanned by Others !", Toast.LENGTH_SHORT).show();

                     }
                    else{
                        Toast.makeText(this, "ID Already Scanned !", Toast.LENGTH_SHORT).show();
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


 /*   private class AccessDB extends AsyncTask<String, Integer, String>
    {

        Context context;

        public AccessDB(Context context)
        {
            this.context = context;
        }
        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String result = "";
            String[] idnumber = con.split("/");
            String idno = idnumber[4];
            String campus = idnumber[5];
            //String[] ako = con.split("=");
              //  String[] res = ako[1].split("&");
              //  String idno = res[0];*

                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet("http://" + Sessions.getIpAddQr() + "&campus="+ campus );

                    HttpResponse response = client.execute(httpget);

                    result = EntityUtils.toString(response.getEntity());

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            return result;

        }

       /* @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (!result.equals("Student Not Registered")) {

                hav = result;
                if(checkre()==true){
                    saveCode();
                    //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "ID Already Scanned !", Toast.LENGTH_SHORT).show();
                }


            } else
                Toast.makeText(context, "ID not found!", Toast.LENGTH_SHORT).show();

            //Toast.makeText(context, result , Toast.LENGTH_SHORT).show();
            Log.d("test connection", result);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

    }*/


    public void reload() {
        try {
            FileInputStream fin = openFileInput(Sessions.getFilename());
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            char[] inputBuffer = new char[RBS];
            int c = 0;
            String temp = "";
            String datdat = "";
            listItems.clear();
            //String list;
/*                while((c=isr.read(inputBuffer))>0){
                    String readString = String.copyValueOf(inputBuffer,0,c);
                    temp += readString;
                    Log.d(temp," "+ temp);
                    inputBuffer = new char[RBS];
                    //temp = temp + Character.toString((char)c);
                    //list = temp;
                    String split[] = temp.split(",");

                    while(c<split.length) {
                        listItems.clear();
                        listItems.add(split[c]);
                        adapter.notifyDataSetChanged();
                        c++;
                    }
                    Toast.makeText(this, "File Loaded", Toast.LENGTH_SHORT).show();
                }*/
            if (isr != null) {
                while ((temp = reader.readLine()) != null) {
                    datdat = temp;
                    String split[] = datdat.split(">");

                    while (c < split.length) {
                        listItems.add(split[c]);
                        adapter.notifyDataSetChanged();
                        c++;
                    }

                }
            }

            count.setText("" + c);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Toast.makeText(this,"Not Redundant",Toast.LENGTH_SHORT).show();
        return true;
    }

    public void populate() {
        data = "gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>" +
                "gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>" +
                "gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko>gwapo ko";
        try {
            FileOutputStream fOut = openFileOutput(Sessions.getFilename(), this.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
            //fOut.write(data.getBytes());
            //fOut.close();
            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
            listItems.add(data);
            adapter.notifyDataSetChanged();
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveCode() {
        String dat = hav;
        data = dat;

        try {
            //Toast.makeText(getApplicationContext(),"InsideSave",Toast.LENGTH_SHORT).show();
            FileOutputStream fOut = openFileOutput(Sessions.getFilename(), this.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data + ">");
            osw.flush();
            osw.close();
            //fOut.write(data.getBytes());
            //fOut.close();
            Toast.makeText(this, "File Saved", Toast.LENGTH_SHORT).show();
            listItems.add(data);
            adapter.notifyDataSetChanged();
            reload();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.listView);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

       /* protected ListAdapter getListAdapter(){
            ListAdapter adapter = getListView().getAdapter();
            if(adapter instanceof HeaderViewListAdapter){
                return((HeaderViewListAdapter)adapter).getWrappedAdapter();
            }
            else{
                return adapter;
            }
        }*/

    public void doIntent() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
        //Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        //startActivityForResult(intent, 0);


    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            con = getContents();
            det = getFormatName();
            int i = 0;
            if (con != null) {
                //Toast.makeText(this,""+ con,Toast.LENGTH_SHORT).show();
                String[] dev = con.split("/");
                if (dev[0].equals("http:")) {
                    if(dev[2].equals("www.ictcongress2016.sturganizr.ph")) {
                        DataCatch();
                        //new AccessDB(getApplicationContext()).execute();
                        //Toast.makeText(getApplicationContext(),"For JSON Parsing",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(),"Invalid Domain",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "QR Code Syntax Invalid", Toast.LENGTH_SHORT).show();
                }
                //doIntent();
                    /*listItems.add("" + con + "-" + det);
                    adapter.notifyDataSetChanged();*/


                //Toast.makeText(this,""+ scanResult.toString(),Toast.LENGTH_LONG).show();
                // Fname = getFormatName().toString();
            } else {

                Toast.makeText(this, "Grabi Paasa!, E Click ang SCAN nya e back ra diay!", Toast.LENGTH_LONG).show();
                // Fname = null;
            }

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this, settings.class));
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan) {
            doIntent();
        } else if (id == R.id.nav_manual) {
            startActivity(new Intent(this, idChecker.class));
        } else if (id == R.id.nav_list) {
            reload();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, settings.class));
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pj.zebrareader/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.pj.zebrareader/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
