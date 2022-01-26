package com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.pinandsecurityquestion;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.MainActivity;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.R;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.SetThemeColor;
import com.hidephoto.hidevideo.lockphoto.secretphoto.safe.free.introduce.IntroduceActivity;

public class SecurityQuestionActivity extends AppCompatActivity {
    private AutoCompleteTextView securityQuestion;
    private TextInputEditText edtAnswer;
    private TextView btnSave, txtYourPin, txtPin, txtCreate;
    private long backPressTime;
    int status;
    String question, answer, pin, keyPin="KEY_PIN", pinFirst;
    View drawerLayout;
    private int statusApp=0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetThemeColor.setThemeColor(Color.WHITE, ContextCompat.getColor(this,R.color.button),true,false, SecurityQuestionActivity.this);
        setContentView(R.layout.activity_security_question);
        addControls();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtAnswer.getText().toString().isEmpty()){
                    Toast.makeText(SecurityQuestionActivity.this,R.string.pleaseWriteYourAnswer,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (answer.isEmpty()){
                    SharedPreferences preferences=getSharedPreferences("QUESTION",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("SECURITY_QUESTION",securityQuestion.getText().toString());
                    editor.commit();

                    SharedPreferences preferences1=getSharedPreferences("ANSWER",MODE_PRIVATE);
                    SharedPreferences.Editor editor1=preferences1.edit();
                    editor1.putString("SECURITY_ANSWER",edtAnswer.getText().toString());
                    editor1.commit();

                    SharedPreferences preferences2=getSharedPreferences(keyPin,MODE_PRIVATE);
                    SharedPreferences.Editor editor2=preferences2.edit();
                    editor2.putString("PIN",pinFirst);
                    editor2.commit();

                    if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q){
                        SharedPreferences sharedPreferences3 = getSharedPreferences("CAPTURE_IMAGE",MODE_PRIVATE);
                        SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                        editor3.putInt("ON_OFF",1);
                        editor3.commit();
                    }

                    Intent intent = new Intent(SecurityQuestionActivity.this, MainActivity.class);
                    startActivity(intent);
                }else if (status==0){
                        SharedPreferences preferences=getSharedPreferences("QUESTION",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("SECURITY_QUESTION",securityQuestion.getText().toString());
                        editor.commit();

                        SharedPreferences preferences1=getSharedPreferences("ANSWER",MODE_PRIVATE);
                        SharedPreferences.Editor editor1=preferences1.edit();
                        editor1.putString("SECURITY_ANSWER",edtAnswer.getText().toString());
                        editor1.commit();

                        Toast.makeText(SecurityQuestionActivity.this,R.string.changeQuestionSuccessfully,Toast.LENGTH_SHORT).show();
                        statusApp=1;
                        finish();
                    }else if (!edtAnswer.getText().toString().equals(answer)){
                            Toast.makeText(SecurityQuestionActivity.this,R.string.answerIsWrong,Toast.LENGTH_SHORT).show();
                        }else {
                            txtYourPin.setVisibility(View.VISIBLE);
                            txtPin.setVisibility(View.VISIBLE);
                            txtPin.setText(pin);
                        }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addControls() {
        edtAnswer = findViewById(R.id.edtAnswer);
        btnSave = findViewById(R.id.btnSave);
        securityQuestion = findViewById(R.id.securityQuestion);
        txtPin = findViewById(R.id.txtPin);
        txtYourPin = findViewById(R.id.txtYourPin);
        drawerLayout = findViewById(R.id.drawerLayout);
        txtCreate = findViewById(R.id.txtCreate);

        SharedPreferences sharedPreferences =getSharedPreferences(keyPin,MODE_PRIVATE);
        pin =sharedPreferences.getString("PIN","");

        SharedPreferences preferencesQuestion = getSharedPreferences("QUESTION",MODE_PRIVATE);
        question = preferencesQuestion.getString("SECURITY_QUESTION","");

        SharedPreferences preferencesAnswer = getSharedPreferences("ANSWER",MODE_PRIVATE);
        answer = preferencesAnswer.getString("SECURITY_ANSWER","");

        String[] options = {getString(R.string.securityQuestion1), getString(R.string.securityQuestion2),getString(R.string.securityQuestion3),getString(R.string.securityQuestion4),getString(R.string.securityQuestion5)};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.item_options,options);

        if (answer.isEmpty()){
            securityQuestion.setText(arrayAdapter.getItem(0).toString(),false);
            securityQuestion.setAdapter(arrayAdapter);
            drawerLayout.setVisibility(View.GONE);
            txtCreate.setVisibility(View.VISIBLE);
        }else {
            securityQuestion.setText(question);
            securityQuestion.setAdapter(arrayAdapter);
            txtCreate.setVisibility(View.GONE);
        }


        Intent intent = getIntent();
        status = intent.getIntExtra("FORGET_PIN",0);

        if(status==1){
            btnSave.setText(R.string.ok);
        }

        Intent intent1 = getIntent();
        pinFirst = intent1.getStringExtra("PIN");

    }

    public void clickBack(View view){
        statusApp=1;
        finish();
    }

    @Override
    public void onBackPressed() {
        if (answer.isEmpty()){
            if (backPressTime + 2000 > System.currentTimeMillis()){
                finishAffinity();
            }else {
                Toast.makeText(SecurityQuestionActivity.this, R.string.haveToCreateQuestion,Toast.LENGTH_SHORT).show();
            }
            backPressTime = System.currentTimeMillis();
        }else {
            statusApp=1;
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (statusApp==0){
            finishAffinity();
        }
    }
}