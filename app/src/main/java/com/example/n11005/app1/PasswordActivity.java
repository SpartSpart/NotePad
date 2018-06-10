package com.example.n11005.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PasswordActivity extends AppCompatActivity {

    EditText pas;
    static EditText pin;
    String pasword;
    String pincode;
    Button enterBtn;

    public static void BlankPin(){
        pin.setText(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        pas = findViewById(R.id.PasswordText);
        pin = findViewById(R.id.PinCode);
        enterBtn = findViewById(R.id.button);
        enterBtn.setEnabled(false);
        setParamstoReset();
        pas.addTextChangedListener(new changeListener());
        pin.addTextChangedListener(new changeListener());



    }
    public void newScreen(View v) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String s = sharedPreferences.getString("PIN","0000");
        String preferencePin = getPin();
        pasword = pas.getText().toString();
        pincode = pin.getText().toString();


        if(pasword.equals(getParamstoReset()) && pincode.equals(getParamstoReset())) {
            ReadWrite note = new ReadWrite();
            note.deleteFiles(this,"resource.txt");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("PIN","0000");
            editor.commit();
            Toast.makeText(this, "Reset success, PIN = "+sharedPreferences.getString("PIN","????"),Toast.LENGTH_SHORT).show();
        }else
            if (pincode.equals(s))
            {
                Intent intObj = new Intent(this, MainActivity.class);
                ReadWrite.refreshPas(pasword, pincode, preferencePin);
                startActivity(intObj);

            }else
            Toast.makeText(this, "Wrong PIN",Toast.LENGTH_SHORT).show();



    }

    private String getPin(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String  data = sharedPreferences.getString("PIN", "0000") ;
        //Toast.makeText(this,data, Toast.LENGTH_LONG).show();
        return data;
    }

    private void setParamstoReset() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ResetPassword","0000");
        editor.commit();

    }

    private String getParamstoReset(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString("ResetPassword","0000");
    }


    private class changeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if ((pas.getText().length()==7&&pin.getText().length()==4)||
               (pas.getText().length()==4&&pin.getText().length()==4)&&pas.getText().toString().equals(getParamstoReset())&&
               pin.getText().toString().equals(getParamstoReset()))
                enterBtn.setEnabled(true);
            else enterBtn.setEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}
