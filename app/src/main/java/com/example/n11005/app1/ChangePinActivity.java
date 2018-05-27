package com.example.n11005.app1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePinActivity extends AppCompatActivity {


    private EditText oldPas;
    private EditText newPass1;
    private EditText newPass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        oldPas = findViewById(R.id.oldPas);
        newPass1 = findViewById(R.id.newPas1);
        newPass2 = findViewById(R.id.newPas2);


    }

    public void changePin(View v) {
        SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //Toast.makeText(this, sharedPreferences2.getString("PIN","???"), Toast.LENGTH_SHORT).show();
        String oldP, newP1, newP2, prefPin;
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            oldP = oldPas.getText().toString();
            newP1 = newPass1.getText().toString();
            newP2 = newPass2.getText().toString();
            prefPin = sharedPreferences.getString("PIN", "0000");
            if (oldP.equals(prefPin)) {
                if (!newP1.equals("") && newP1.equals(newP2)) {
                    editor.putString("PIN", newP1);
                    editor.commit();
                    ReadWrite note = new ReadWrite();
                    note.deleteFiles(this);
                    Toast.makeText(this, "PIN was changed", Toast.LENGTH_SHORT).show();

                    this.finish();
                }
                else
                Toast.makeText(this, "Wrong new PIN", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Wrong current PIN", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

}
