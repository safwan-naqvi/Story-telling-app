package com.comsats.aistoryteller;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfigurable;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.google.android.material.textfield.TextInputLayout;
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
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AmazonPolly extends Activity {
    private static final String TAG = "PollyDemo";

    private static final String KEY_SELECTED_VOICE_POSITION = "SelectedVoicePosition";
    private static final String KEY_VOICES = "Voices";
    private static final String KEY_SAMPLE_TEXT = "SampleText";
    SpinnerVoiceAdapter spinnerVoiceAdapter;
    View progressBar;
    // Media player
    MediaPlayer mediaPlayer;
    // Backend resources
    private AmazonPollyPresigningClient client;
    private List<Voice> voices;
    // UI
    private Spinner voicesSpinner;
    private TextInputLayout sampleTextEditText;
    private Button playButton;
    private ImageButton defaultTextButton;
    private int selectedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_amazon_polly);
        Log.d(TAG, "onCreate: ");

        playButton = findViewById(R.id.readButton);
        progressBar = findViewById(R.id.voicesProgressBar);
        voicesSpinner = findViewById(R.id.voicesSpinner);
        defaultTextButton = findViewById(R.id.defaultTextButton);
        sampleTextEditText = findViewById(R.id.sampleText);

        progressBar.setVisibility(View.VISIBLE);

        if (savedInstanceState != null) {
            restoreInstanceState(savedInstanceState);
        }
        setupVoiceSpinner();
        initPollyClient();
        setupNewMediaPlayer();
        setupSampleTextEditText();
        setupDefaultTextButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_SELECTED_VOICE_POSITION, voicesSpinner.getSelectedItemPosition());
        outState.putSerializable(KEY_VOICES, (Serializable) voices);
        outState.putString(KEY_SAMPLE_TEXT, sampleTextEditText.getEditText().getText().toString());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        super.onPause();
    }
    void restoreInstanceState(Bundle savedInstanceState) {
        selectedPosition = savedInstanceState.getInt(KEY_SELECTED_VOICE_POSITION);
        voices = (List<Voice>) savedInstanceState.getSerializable(KEY_VOICES);

        String sampleText = savedInstanceState.getString(KEY_SAMPLE_TEXT);
        if (sampleText.isEmpty()) {
            defaultTextButton.setVisibility(View.GONE);
        } else {
            sampleTextEditText.getEditText().setText(sampleText);
            defaultTextButton.setVisibility(View.VISIBLE);
        }
    }

    private void setupVoiceSpinner() {
        spinnerVoiceAdapter = new SpinnerVoiceAdapter(AmazonPolly.this, new ArrayList<Voice>());
        voicesSpinner.setAdapter(spinnerVoiceAdapter);
        voicesSpinner.setVisibility(View.VISIBLE);

        voicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    Log.e( "setVoices: ",voices.get(position).toString() );

                    setDefaultTextForSelectedVoice();
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.reset();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    void initPollyClient() {
// Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "ap-southeast-1:521997a3-d05e-47d5-a8a3-d8b8e2c16993", // Identity pool ID
                Regions.AP_SOUTHEAST_1 // Region
        );
        /*AWSConfiguration awsConfiguration = new AWSConfiguration(this);
        awsConfiguration.setConfiguration("Stage");*/
        AWSMobileClient.getInstance().initialize(this, new Callback<UserStateDetails>() {
            @Override
            public void onResult(UserStateDetails result) {
                // Create a client that supports generation of presigned URLs.
                client = new AmazonPollyPresigningClient(AWSMobileClient.getInstance());
                Log.d(TAG, "onResult: Created polly pre-signing client");

                if (voices == null) {
                    // Create describe voices request.
                    DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

                    try {
                        // Synchronously ask the Polly Service to describe available TTS voices.
                        DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);

                        // Get list of voices from the result.
                        voices = describeVoicesResult.getVoices();

                        // Log a message with a list of available TTS voices.
                        Log.i(TAG, "Available Polly voices: " + voices);
                    } catch (RuntimeException e) {
                        Log.e(TAG, "Unable to get available voices.", e);
                        return;
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinnerVoiceAdapter.setVoices(voices);
                        voicesSpinner.setSelection(selectedPosition);
                        progressBar.setVisibility(View.INVISIBLE);
                        playButton.setEnabled(true);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError: Initialization error", e);
            }
        });
    }

    void setupSampleTextEditText() {
        sampleTextEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                defaultTextButton.setVisibility(sampleTextEditText.getEditText().getText().toString().isEmpty() ?
                        View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        sampleTextEditText.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                playButton.performClick();
                return false;
            }
        });
    }

    void setupNewMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                setupNewMediaPlayer();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                playButton.setEnabled(true);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                playButton.setEnabled(true);
                return false;
            }
        });
    }

    String getSampleText(Voice voice) {
        if (voice == null) {
            return "";
        }

        String resourceName = "sample_" +
                voice.getLanguageCode().replace("-", "_").toLowerCase() + "_" +
                voice.getId().toLowerCase();
        int sampleTextResourceId =
                getResources().getIdentifier(resourceName, "string", getPackageName());
        if (sampleTextResourceId == 0)
            return "";

        return getString(sampleTextResourceId);
    }

    void setDefaultTextForSelectedVoice() {
        Voice selectedVoice = (Voice) voicesSpinner.getSelectedItem();
        if (selectedVoice == null) {
            return;
        }

        String sampleText = getSampleText(selectedVoice);

        sampleTextEditText.setHint(sampleText);
    }

    void setupDefaultTextButton() {
        defaultTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sampleTextEditText.getEditText().setText(null);
            }
        });
    }

    public void playVoice(View view) {
      //  playButton.setEnabled(false);

        Voice selectedVoice = (Voice) voicesSpinner.getSelectedItem();

        String textToRead = sampleTextEditText.getEditText().getText().toString();

        // Use voice's sample text if user hasn't provided any text to read.
        if (textToRead.trim().isEmpty()) {
            textToRead = getSampleText(selectedVoice);
            Toast.makeText(this, "No text Selected", Toast.LENGTH_SHORT).show();
        }

        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        // Set text to synthesize.
                        .withText(textToRead)
                        // Set voice selected by the user.
                        .withVoiceId(selectedVoice.getId())
                        // Set format to MP3.
                        .withOutputFormat(OutputFormat.Mp3);

        // Get the presigned URL for synthesized speech audio stream.
        URL presignedSynthesizeSpeechUrl =
                client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);

        Log.i(TAG, "Playing speech from presigned URL: " + presignedSynthesizeSpeechUrl);

        // Create a media player to play the synthesized audio stream.
//        if (mediaPlayer.isPlaying()) {
//            setupNewMediaPlayer();
//        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            Log.i(TAG, "playing " + presignedSynthesizeSpeechUrl);

           // mediaPlayer.stop();
            // Set media player's data source to previously obtained URL.
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        // Start the playback asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();
    }

    public void extractTxt(View view) {
        Dexter.withContext(AmazonPolly.this)
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
    public void openFolder() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "PLease Select a File"), 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri filepath = data.getData();
            Log.e("extracted: ", String.valueOf(filepath));
            Cursor returnCursor =
                    getContentResolver().query(filepath, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();


            String filename = returnCursor.getString(nameIndex);
            Toast.makeText(this, "You Have Selected File" + filename, Toast.LENGTH_SHORT).show();
           // tvFileName.setText(filename);
            etractTextFromPDF(filepath);
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    InputStream inputStream;

    private void etractTextFromPDF(Uri uri) {
        try {
            inputStream = AmazonPolly.this.getContentResolver().openInputStream(uri);
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
                    runOnUiThread(() -> sampleTextEditText.getEditText().setText(builder.toString()));
                } catch (IOException e) {
                    sampleTextEditText.getEditText().setText(e.getMessage());
                    Log.e("etractTextFromPDF: ", e.getMessage());
                }
            }
        }).start();

    }
}
