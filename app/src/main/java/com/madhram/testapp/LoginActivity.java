package com.madhram.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.SSLSocketFactory;

public class LoginActivity extends AppCompatActivity {

    EditText loginEditText;
    EditText passwordEditText;
    Button loginButton;
    private String login = "";
    private String password = "";

    private static String GET_URL = "https://www.alarstudios.com/test/auth.cgi?username=%s&password=%s";
    public static final String EXTRA_CODE = "com.madhram.testapp.code";
    private final static Integer PORT = 443;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginEditText = findViewById(R.id.loginEditText);
        loginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordEditText = findViewById(R.id.passwordEditText);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            if (!login.isEmpty() && !password.isEmpty()){
                sendGETRequest(login, password);
            } else if (login.isEmpty()){
                loginEditText.setError("Login field is empty");
            } else {
                passwordEditText.setError("Password field is empty");
            }
        });
    }

    private void sendGETRequest(String correctLogin, String correctPassword){

        new Thread(() -> {
            try {
                URL urlObject = new URL(String.format(GET_URL, correctLogin, correctPassword));
                String code = "-1";

                Socket socket = SSLSocketFactory.getDefault().createSocket(urlObject.getHost(), PORT);

                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.println("GET " + urlObject.getFile() + " HTTP/1.0\r\nHost: " + urlObject.getHost()+ "\r\n\r\n");
                printWriter.flush();

                BufferedReader response = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String responseStr = response.readLine();
                while (responseStr != null && responseStr != "") {
                    if (responseStr.contains("status")){
                        JSONObject obj = new JSONObject(responseStr);
                        if (obj.get("status").equals("ok")){
                            code = obj.get("code").toString();
                        }
                        break;
                    }
                    responseStr = response.readLine();
                }
                response.close();
                printWriter.close();
                socket.close();

                if (code != "-1"){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(EXTRA_CODE, code);
                    startActivity(intent);
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "You entered wrong login or password", Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}