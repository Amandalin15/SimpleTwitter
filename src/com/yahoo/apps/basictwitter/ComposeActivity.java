package com.yahoo.apps.basictwitter;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

public class ComposeActivity extends Activity {
    static final int MAX_LENGTH = 140;

    EditText etInput;
    TextView tvCount;
    Button btnTweet;
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose);
        client = TwitterApplication.getRestClient();
        etInput = (EditText) findViewById(R.id.etInput);
        tvCount = (TextView) findViewById(R.id.tvWordCount);
        btnTweet = (Button) findViewById(R.id.btnTweet);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCount.setText(String.valueOf(MAX_LENGTH - etInput.getText().length()));
            }
        });
    }

    public void onTweet(View v) {
        if(etInput.getText().toString().equals("")){
            Toast.makeText(getBaseContext(),"Please input some words",Toast.LENGTH_SHORT).show();
            return;
        }else {
            client.compose(etInput.getText().toString(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(JSONObject jsonObject) {
                    Toast.makeText(getBaseContext(), "Successfully post new tweet", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onFailure(Throwable e, String s) {
                    Log.d("debug", e.toString());
                    Log.d("debug", s);
                }
            });
        }

    }
}