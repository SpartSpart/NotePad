package com.example.n11005.app1;


import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity{

    Menu menu;
    EditText Logtext;
    EditText Pastext;
    ReadWrite note = new ReadWrite();
    EditText pin;

    SharedPreferences.Editor share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logtext = findViewById(R.id.LogText);

        Pastext = findViewById(R.id.PasText);

        if (!note.isCorrectReadName() || !note.isCorrectReadPassword(this)) {
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
                String savepas = note.writeFileOnInternalStorage(getBaseContext(),Pastext.getText().toString());
                if (savemessage ==savepas)
                    Toast.makeText(this, savemessage,Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Saving failed!",Toast.LENGTH_SHORT).show();

                break;
            case R.id.Open:
                String anwser = "Opening Failed";
                       String[] answers = note.readname();
                       String pas = note.readFileOnInternalStorage(getBaseContext());
                       Logtext.setText(answers[1]);
                       Pastext.setText(pas);
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
            case R.id.reset:
                String s = note.writetoNas("1");
                Toast.makeText(this, s,Toast.LENGTH_SHORT).show();


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

}
