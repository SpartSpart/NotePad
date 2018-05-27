package com.example.n11005.app1;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


import javax.crypto.Cipher;

public class ReadWrite extends AppCompatActivity {


    private static String EnteredPin = "";
    public static String key = "q";// use 16 chars
    private String answer = "Something was wrong";
    private String FILE_NAME = "file.txt";
    private Crypto crypto = new Crypto();
    private static boolean EnabledMarker = false;


    public static void refreshPas(String password, String Pin, String preferencePin) {
        EnteredPin = Pin;
        key = "KaWaSaK7#e68&9HFl134%" + EnteredPin + password;
        EnabledMarker = Crypto.Enabled(Pin, preferencePin);
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return false;
        }
        return true;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }


    //Write to SDCard
    public String writename(String text) {

        if (EnabledMarker) {

            answer = "Save failed";

            if (isExternalStorageAvailable() && isExternalStorageReadOnly()) {
                String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
                File directory = new File(baseDir);
                try {
                    if (!directory.exists())
                        directory.mkdirs();
                } catch (Exception ignore) {
                }

                try {
                    File file = new File(baseDir, FILE_NAME);

                    FileWriter writer = new FileWriter(file);
                    writer.write(text);
                    writer.close();
                    crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, file, file);
                    answer = "Saved!";
                } catch (IOException e) {
                    answer = e.toString();
                }
            }
        }

        return answer;
    }

    //Read from SDCard
    public String[] readname() {
        String[] answers = new String[2];
        answers[0] = "Open failed";
        answers[1] = "";
        StringBuilder text = new StringBuilder();
        if (isExternalStorageAvailable() && isExternalStorageReadOnly()) {
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";

            File file = null;
            try {
                file = new File(baseDir, FILE_NAME);
            } catch (Exception e) {
                return answers;
            }

            crypto.fileProcessor(Cipher.DECRYPT_MODE, key, file, file);

            String line = "";

            try {
                FileReader fReader = new FileReader(file);
                BufferedReader bReader = new BufferedReader(fReader);

                while ((line = bReader.readLine()) != null) {
                    text.append(line + "\n");
                }
                answers[0] = "Opened";
            } catch (IOException e) {
                answer = e.toString();
                crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, file, file);
                return answers;
            }

            crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, file, file);
        }

        answers[1] = text.toString();
        return answers;
    }

    public String writeFileOnInternalStorage(Context mcoContext, String sBody) {

        if (EnabledMarker) {
            String answer = "";
            String sFileName = "pas.txt";
            File file = new File(mcoContext.getFilesDir(), "xFile");
            if (!file.exists()) {
                file.mkdir();
            }

            try {
                File pasfile = new File(file, sFileName);
                FileWriter writer = new FileWriter(pasfile);
                writer.append(sBody);
                writer.flush();
                writer.close();
                crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, pasfile, pasfile);
                answer = "Saved!";

            } catch (Exception e) {
                e.printStackTrace();
                answer = "Failed";

            }
        }

        return answer;
    }

    public String readFileOnInternalStorage(Context mcoContext) {
        String sFileName = "pas.txt";
        File file = new File(mcoContext.getFilesDir(), "xFile");
        StringBuilder text = new StringBuilder();
        String answer = "";
        File pasfile = null;
        try {
            pasfile = new File(file, sFileName);
            crypto.fileProcessor(Cipher.DECRYPT_MODE, key, pasfile, pasfile);

            try {
                BufferedReader br = new BufferedReader(new FileReader(pasfile));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
                crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, pasfile, pasfile);

                answer = text.toString();
            } catch (IOException e) {
                answer = e.toString();
                crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, pasfile, pasfile);
            }

        } catch (Exception e) {
            answer = e.toString();
            crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, pasfile, pasfile);


        }


        return answer;
    }

    public boolean isCorrectReadName()  {
        boolean emptyFile = false;
        boolean readname = false;

        StringBuilder text = new StringBuilder();
        if (isExternalStorageAvailable() && isExternalStorageReadOnly()) {
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";

            File file = null;
            File tempfile = null;
            try {
                file = new File(baseDir, FILE_NAME);
                tempfile = new File(baseDir, "temp.temp");
                if (!file.exists()) {
                    try {
                        file.createNewFile();

                    } catch (IOException ignore) {}
                }

            } catch (Exception e) {
                tempfile.delete();
                return false;
            }

            if (isEmptyFile(file)) {
                readname = true;
                return readname;
            }


            crypto.fileProcessor(Cipher.DECRYPT_MODE, key, file, tempfile);

            String line = "";

            try {
                FileReader fReader = new FileReader(tempfile);
                BufferedReader bReader = new BufferedReader(fReader);

                while ((line = bReader.readLine()) != null) {
                    text.append(line + "\n");
                }

            } catch (IOException e) {
                answer = e.toString();
                tempfile.delete();
                return false;
            }

            tempfile.delete();
        }
        if (!text.toString().contains("�") || text.toString().equals(""))
            readname = true;


        return readname;
    }

    public boolean isCorrectReadPassword(Context mcoContext) {
        String sFileName = "pas.txt";
        File file = new File(mcoContext.getFilesDir(), "xFile");
        StringBuilder text = new StringBuilder();
        File pasfile = null;
        File tempfile = null;
        boolean readpas = false;

        try {
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";
            tempfile = new File(baseDir, "temp.temp");
            pasfile = new File(file, sFileName);

            if (!pasfile.exists()) {
                try {
                    pasfile.createNewFile();

                } catch (IOException ignore) {
                }

            }


            if (isEmptyFile(pasfile)) {
                readpas = true;
                return readpas;
            }
                crypto.fileProcessor(Cipher.DECRYPT_MODE, key, pasfile, tempfile);

                try {
                    BufferedReader br = new BufferedReader(new FileReader(tempfile));
                    String line;


                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();

                } catch (IOException e) {

                    tempfile.delete();
                    readpas = false;

                }

            } catch(Exception e){

                tempfile.delete();
                readpas = false;

            }

            tempfile.delete();

            if (!text.toString().contains("�") || text.toString().equals(""))
                readpas = true;


        return readpas;
    }

    public void deleteFiles(Context mcoContext) {
        String sFileName = "pas.txt";
        File filepasdir = new File(mcoContext.getFilesDir(), "xFile");
        File pasfile = null;


        if (isExternalStorageAvailable() && isExternalStorageReadOnly()) {
            String baseDir = Environment.getExternalStorageDirectory() + File.separator + "Files";

            File file = null;

            try {
                file = new File(baseDir, FILE_NAME);
                pasfile = new File(filepasdir, sFileName);
                file.delete();
                pasfile.delete();

            } catch (Exception ignore) {}


        }
    }

    private boolean isEmptyFile(File file){
    boolean isEmpty = false;
        if(file.length() == 0)
            isEmpty = true;
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader(file.getName()));
//            if (br.readLine() == null) {
//                isEmpty = true;
//            }
//            } catch (Exception e) {
//            isEmpty = true;
//        }
        return isEmpty;
    }

    public String writetoNas(String text) {

            answer = "Save failed";
        File testfile=null;

                //ile directory;// = new File(baseDir);
                try {
                    String file = "smb:\\\\192.168.1.51\\Volume_1\\TEST\\TEST.txt";
                    testfile= new File(file);

                } catch (Exception ignore) {
                    answer=ignore.toString()+"1";
                }

//                try {
////                    File file = new File(baseDir, "TEST.txt");
////                    if (!file.exists())
////                        file.createNewFile();
//
//                    FileWriter writer = new FileWriter(testfile);
//                    writer.write("TEST");
//                    writer.close();
//                    //crypto.fileProcessor(Cipher.ENCRYPT_MODE, key, file, file);
//                    answer = "Luck!";
//                } catch (IOException e) {
//                    answer = e.toString()+"2";
//                }

        StringBuilder ttext= new StringBuilder();

        FileReader fReader = null;
        try {
            fReader = new FileReader(testfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bReader = new BufferedReader(fReader);

        String line="";
        try {
            while ((line = bReader.readLine()) != null) {
                ttext.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        answer = ttext.toString();



        return answer;
    }

//    private String ReadFilefromNAS(String s, String Username, String Password) {
//        //s is the filename path on the NAS
//        String file = "";
//        SmbFile dir;
//        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, Username, Password);
//        dir = new SmbFile(FilePath + s, auth);
//        //If your not using auth use the below
//        //dir = new SmbFile(FilePath + s);
//        InputStream in = dir.getInputStream();
//        BufferedReader q= new BufferedReader(new InputStreamReader(in));
//        String line;
//        int i = 0;
//        while ((line = q.readLine()) != null) {
//            if(i==0){
//                file+=line;
//            }else {
//                file += "\n" + line;
//            }
//            i+=1;
//        }
//    }catch(Exception e){
//        Log.e("ERROR",e.toString());
//    }
//    return file;
//}

}





