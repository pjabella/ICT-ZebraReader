package com.example.pj.zebrareader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class settings extends AppCompatActivity {

    TextView ip;
    TextView nameoffile;
    TextView push;
    Context context;
    EditText etIP;
    EditText nof;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameoffile = (TextView) this.findViewById(R.id.textView5);
        ip = (TextView) this.findViewById(R.id.textView3);
        push = (TextView) this.findViewById(R.id.textView7);



        ip.setText(Sessions.getIpAddQr());
        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ipAlert();
            }
        });
        nameoffile.setText(Sessions.getFilename());
        nameoffile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                filenameAlert();
            }
        });



    }



    public void ipAlert(){
        etIP = new EditText(this);
        etIP.setText(Sessions.getIpAddQr());
        new AlertDialog.Builder(this)
                .setMessage("Enter server IP address")
                .setTitle("IP Address")
                .setView(etIP)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        if(!etIP.getText().toString().equals(""))
                        {
                            Sessions.setIpAddQr(etIP.getText().toString());
                            ip.setText(Sessions.getIpAddQr());
                            dialog.dismiss();
                        }
                        else {
                            Sessions.setIpAddQr("IP Address Not Set!");
                            Toast.makeText(getApplicationContext(), "Please enter IP address!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    public void filenameAlert(){
        nof = new EditText(this);
        nof.setText(Sessions.getFilename());
        new AlertDialog.Builder(this)
                .setMessage("Enter New Filename")
                .setTitle("Filename")
                .setView(nof)
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Sessions.setFilename(nof.getText().toString());
                        nameoffile.setText(Sessions.getFilename());
                        MainActivity.listItems.clear();
                        dialog.dismiss();

                    }
                })
                .show();
    }



}
