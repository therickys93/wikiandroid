package it.therickys93.wiki;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AIActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageButton button;
    private TypeWriter inputMessageTypeWriter;
    private TypeWriter outputMessageTypeWriter;
    private TextToSpeech tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        MainActivity.createAppContext(getApplicationContext());

        tts = new TextToSpeech(this, this);

        inputMessageTypeWriter = (TypeWriter) findViewById(R.id.inputMessageTypeWriter);
        outputMessageTypeWriter = (TypeWriter)findViewById(R.id.outputMessageTypeWriter);
        button = (ImageButton) findViewById(R.id.btnSpeak);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDialogSettings();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ai_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.main:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.writeToWikiServer:
                showDialogInputRequest();
                return true;
            case R.id.ai:
                intent = new Intent(this, AIActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.macro:
                intent = new Intent(this, MacroActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showDialogInputRequest()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.input_request_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setHint("Scrivi qui");

        dialogBuilder.setTitle("Richiesta WikiServer");
        dialogBuilder.setMessage("Scrivi la tua richiesta");
        dialogBuilder.setPositiveButton("Invia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String request = edt.getText().toString();
                showMessageInEditText(inputMessageTypeWriter, request);
                performRequest(request);
            }
        });
        dialogBuilder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // non fare nulla
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void showDialogSettings()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.ai_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        SharedPreferences settings = getSharedPreferences(Wiki.AI.Settings.NAME, 0);
        String url = settings.getString(Wiki.AI.Settings.SERVER, Wiki.AI.DEFAULT_URL);
        edt.setText(url);

        final EditText edt1 = (EditText) dialogView.findViewById(R.id.edit2);
        String user_id = settings.getString(Wiki.AI.Settings.USER_ID, Wiki.AI.DEFAULT_USERID);
        edt1.setText(user_id);

        dialogBuilder.setTitle("Wiki Server");
        dialogBuilder.setMessage("Wiki Server URL");
        dialogBuilder.setPositiveButton("Fatto", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SharedPreferences settings = getSharedPreferences(Wiki.AI.Settings.NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Wiki.AI.Settings.SERVER, edt.getText().toString());
                editor.putString(Wiki.AI.Settings.USER_ID, edt1.getText().toString());
                editor.commit();
            }
        });
        dialogBuilder.setNegativeButton("Cancella", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // non fare nulla
            }
        });
        dialogBuilder.setNeutralButton("Qr Code", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(AIActivity.this, QRCodeScannerActivity.class);
                intent.putExtra(Wiki.QRCode.SETTINGS, Wiki.AI.Settings.NAME);
                intent.putExtra(Wiki.QRCode.URL, Wiki.AI.Settings.SERVER);
                startActivity(intent);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALIAN);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parla");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Text not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String request = result.get(0);
                    FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "WIKIAI Request: " + request);
                    showMessageInEditText(inputMessageTypeWriter, request);
                    performRequest(result.get(0));

                }
                break;
            }

        }
    }

    private void showMessageInEditText(TypeWriter typeWriter, String message)
    {
        typeWriter.setCharacterDelay(50);
        typeWriter.animateText(message);
    }

    private void outputResponse(String message)
    {
        outputMessageTypeWriter.setCharacterDelay(50);
        outputMessageTypeWriter.animateText(message);
        FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "WIKIAI Response: " + message);
        speakOut(message);
    }

    private void performRequest(String message)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("request", message);

        SharedPreferences settings = getSharedPreferences(Wiki.AI.Settings.NAME, 0);
        String url = settings.getString(Wiki.AI.Settings.SERVER, Wiki.AI.DEFAULT_URL);
        FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "Wiki Server URL: "+ url);

        String user_id = settings.getString(Wiki.AI.Settings.USER_ID, Wiki.AI.DEFAULT_USERID);
        FileUtils.appendToFile(MainActivity.getAppContext(), Wiki.Controller.LOG_FILENAME, "Wiki Server USERID: "+ user_id);

        params.put("user_id", user_id);

        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            outputResponse(response.getString("response"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errMsg = error.getMessage();
                if (errMsg != null) {
                    VolleyLog.v("Error: %s", error.getMessage());
                    Log.v("WIKI", error.getMessage());
                }
                outputResponse("Errore di connessione");
            }
        });
        Volley.newRequestQueue(this).add(req);
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ITALIAN);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                button.setEnabled(true);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
