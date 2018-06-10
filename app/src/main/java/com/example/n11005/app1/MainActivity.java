package com.example.n11005.app1;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity{

    Menu menu;
    EditText Logtext;
    EditText Pastext;
    EditText Resourcetext;
    ReadWrite note = new ReadWrite();
    EditText pin;
    ProgressBar spinnerBar;

    SharedPreferences.Editor share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resourcetext = findViewById(R.id.ResourceText);
        Logtext = findViewById(R.id.LogText);
        Pastext = findViewById(R.id.PasText);
        spinnerBar = findViewById(R.id.progressBarRound);
        spinnerBar.setVisibility(View.INVISIBLE);

        if (!note.isCorrectReadName() || !note.isCorrectReadPassword(this,"pas.txt")
                ||!note.isCorrectReadPassword(this,"resource.txt")) { //второй раз isCorrect проверяет файл ресурса(описания)
            Toast.makeText(this, "Wrong Decrypt key!",Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_items,menu);
        //MenuItem open = menu.findItem(R.id.Open);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.Save:
                String savemessage = note.writename(Logtext.getText().toString());
                String savepas = note.writeFileOnInternalStorage(getBaseContext(),Pastext.getText().toString(),"pas.txt");
                note.writeFileOnInternalStorage(getBaseContext(),Resourcetext.getText().toString(),"resource.txt");
                if (savemessage ==savepas)
                    Toast.makeText(this, savemessage,Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Saving failed!",Toast.LENGTH_SHORT).show();

                break;
            case R.id.Open:
                String anwser = "Opening Failed";
                       String[] answers = note.readname();
                       String pas = note.readFileOnInternalStorage(getBaseContext(),"pas.txt");
                       Logtext.setText(answers[1]);
                       Pastext.setText(pas);
                       Resourcetext.setText(note.readFileOnInternalStorage(getBaseContext(),"resource.txt"));
                       MenuItem save = menu.findItem(R.id.Save);
                       //save.setEnabled(true);
                       anwser = answers[0];

                Toast.makeText(this, anwser,Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                Intent ExitObj = new Intent(this, PasswordActivity.class);
                startActivity(ExitObj);
                break;
            case R.id.changepin:
                Intent ChangePinObj = new Intent(this, ChangePinActivity.class);
                startActivity(ChangePinObj);
                break;
            case R.id.sendNas:
                new MyAsyncTaskNas().execute();

                break;
            case R.id.nassettings:
                Intent NasSettingsObj = new Intent(this, NasSettings.class);
                startActivity(NasSettingsObj);
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStop() {

       PasswordActivity.BlankPin();
        super.onStop();
        this.finish();
    }

private boolean checkCorrectText (String text1,String text2){
        if (text1.contains("�") || text2.contains("�"))
            return false;
        else
            return true;
        }

    boolean sendingToNas(){
        Boolean success = false;
        boolean transferNasDone = false;

        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            File internalDir = new File(getBaseContext().getFilesDir(), "xFile");
            SendToNas send = new SendToNas();
            send.setAuthorization(share.getString("naslogin", ""),
                    share.getString("naspassword", ""),
                    share.getString("nasfolder", ""));
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
            File loginFile = new File(baseDir+File.separator+"login.txt");
            File pasFile = new File(internalDir+File.separator+"pas.txt");
            File resourceFile = new File (internalDir+File.separator+"resource.txt");

            FileInputStream file = new FileInputStream(loginFile);
            success = send.copyFiles(file, loginFile.getName());
            if (success){
                file = new FileInputStream(pasFile);
                success = send.copyFiles(file, pasFile.getName());
            }
            if (success){
                file = new FileInputStream(resourceFile);
                success = send.copyFiles(file, resourceFile.getName());
            }
        } catch (Exception e) {
            success = false;
        }
        transferNasDone = true;
        return success;
    }

    private class MyAsyncTaskNas extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean flag = true;
            try{
                TimeUnit.MILLISECONDS.sleep(1000);
                flag = sendingToNas();
            }catch (Exception ignore){
                flag = false;
            }

            return flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinnerBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            spinnerBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (result)
                Toast.makeText(getApplicationContext(), "Transfer success", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Transfer failed", Toast.LENGTH_LONG).show();
        }
    }

}
