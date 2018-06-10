package com.example.n11005.app1;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NasSettings extends AppCompatActivity {

    Button saveBtn;
    EditText nasFolder,
             nasLogin,
             nasPassword;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nas_settings);
        saveBtn = findViewById(R.id.save_Btn);
        nasFolder= findViewById(R.id.nas_folder);
        nasLogin= findViewById(R.id.nas_login);
        nasPassword= findViewById(R.id.nas_password);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            nasFolder.setText(sharedPreferences.getString("nasfolder", ""));
            nasLogin.setText(sharedPreferences.getString("naslogin", ""));
            nasPassword.setText(sharedPreferences.getString("naspassword", ""));
        }catch (Exception ignore){}

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  stringNasFolder,
                        stringNasLogin,
                        stringNasPassword;
                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                stringNasFolder = nasFolder.getText().toString();
                stringNasLogin = nasLogin.getText().toString();
                stringNasPassword = nasPassword.getText().toString();
                editor.putString("nasfolder",stringNasFolder);
                editor.putString("naslogin",stringNasLogin);
                editor.putString("naspassword",stringNasPassword);
                editor.commit();
                finishactivity();
            }
        });
    }

    void finishactivity(){
        this.finish();
    }
}
