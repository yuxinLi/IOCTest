package com.example.ioctest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ioc.ViewInjector;
import com.example.ioc.lib.InjectView;

public class MainActivity extends AppCompatActivity {


    @InjectView(R.id.tv)
    TextView mTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewInjector.inject(this);

        if(mTV != null){
            mTV.setText("xxx");
        }else {
            Toast.makeText(this, " kong ", Toast.LENGTH_SHORT ).show();
        }
    }
}
