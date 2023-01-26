package com.comsats.aistoryteller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class TextExtractor extends AppCompatActivity {
    private Button btnOpen, btnExtract;
    String data, fileName, result;
    Activity activity;
    TextView tvFileName, tvExtractedText;
    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_extractor);
        btnOpen = findViewById(R.id.btn_Open);
        btnExtract = findViewById(R.id.btn_extract);
        tvFileName = findViewById(R.id.fileName);
        tvExtractedText = findViewById(R.id.extractedText);
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
         btnExtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_stvfileName = tvFileName.getText().toString();
                String s_tvExtractedText = tvExtractedText.getText().toString();
                if (s_stvfileName.contains("File Name:")) {
                    Toast.makeText(getApplicationContext(), "PLease Select A file First", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (s_tvExtractedText.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "InValid Text", Toast.LENGTH_SHORT).show();
                    return;
                }
                startttSpeach(s_tvExtractedText);
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Dexter.withContext(TextExtractor.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                openFolder();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

            }
        /*    public void storeUserData(String data , String fileName, Activity activity){
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(activity.openFileOutput(fileName+".txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write(data);
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            public String readUserData(Activity activity, String fileName){
                String result = "";
                try {
                    InputStream inputStream = activity.openFileInput(fileName+".txt");
                    if (inputStream!=null){
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String tempString = "";
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((tempString = bufferedReader.readLine())!=null){
                            stringBuilder.append(tempString);
                        }
                        inputStream.close();
                        result = stringBuilder.toString();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }*/

        });
    }

    private void startttSpeach(String s_tvExtractedText) {
        if (t1.isSpeaking())
        {
            btnExtract.setText("Start");
            t1.stop();
        }
        else
        {
            btnExtract.setText("Stop");
            t1.speak(s_tvExtractedText, TextToSpeech.QUEUE_FLUSH, null);

        }
    }

    public void openFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "PLease Select a File"), 1);
    }

    @Override
    protected void onStop() {
        t1.stop();
        super.onStop();
    }

    @Override
    protected void onPause() {
        t1.stop();

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            Log.e("extracted: ", String.valueOf(filepath));
            Cursor returnCursor =
                    getContentResolver().query(filepath, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();


            String filename = returnCursor.getString(nameIndex);
            Toast.makeText(this, "You Have Selected File" + filename, Toast.LENGTH_SHORT).show();
            tvFileName.setText(filename);
            etractTextFromPDF(filepath);
        }
    }

    InputStream inputStream;

    private void etractTextFromPDF(Uri uri) {
        try {
            inputStream = TextExtractor.this.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.e("etractTextFromPDF: ", e.getMessage());

            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {


                String filecontent = "";
                StringBuilder builder = new StringBuilder();
                PdfReader reader = null;
                try {
                    reader = new PdfReader(inputStream);
                    int pages = reader
                            .getNumberOfPages();
                    Log.e("run: ", "pages" + pages);
                    // Toast.makeText(MainActivity.this, "a"+pages, Toast.LENGTH_SHORT).show();
                    for (int i = 1; i <= 1; i++) {
                        filecontent = filecontent + PdfTextExtractor.getTextFromPage(reader, i).trim() + "\n";
                    }
                    builder.append(filecontent);
                    reader.close();
                    runOnUiThread(() -> tvExtractedText.setText(builder.toString()));
                } catch (IOException e) {
                    tvExtractedText.setText(e.getMessage());
                    Log.e("etractTextFromPDF: ", e.getMessage());
                }
            }
        }).start();

    }

    private void extractPDF(String filepath) {
        try {
            // creating a string for
            // storing our extracted text.
            String extractedText = "";

            // creating a variable for pdf reader
            // and passing our PDF file in it.
            PdfReader reader = new PdfReader(filepath);

            // below line is for getting number
            // of pages of PDF file.
            int n = reader.getNumberOfPages();

            // running a for loop to get the data from PDF
            // we are storing that data inside our string.
            for (int i = 0; i < n; i++) {
                extractedText = extractedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n";
                // to extract the PDF content from the different pages
            }

            // after extracting all the data we are
            // setting that string value to our text view.
            tvExtractedText.setText(extractedText);

            // below line is used for closing reader.
            reader.close();
        } catch (Exception e) {
            // for handling error while extracting the text file.
            Log.e("extractPDF: ", e.getMessage());
            tvExtractedText.setText("Error found is : \n" + e);
        }
    }

}
