package com.madhram.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    // for GET request
    private String code;
    private int pageNumber = 1;
    private static String GET_URL = "https://www.alarstudios.com/test/data.cgi?code=%s&p=%s";
    private final static Integer PORT = 443;

    RecyclerView recyclerView;
    Adapter adapter;
    List<MapPointsClass> mapPointsClass = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        code = Objects.requireNonNull(getIntent().getExtras()).getString(LoginActivity.EXTRA_CODE);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, recyclerView, mapPointsClass);
        recyclerView.setAdapter(adapter);
        sendGETRequest();

        adapter.setLoadMore(() -> {
            mapPointsClass.add(null);
            recyclerView.post(() -> adapter.notifyItemInserted(mapPointsClass.size() - 1));
            sendGETRequest();
        });
    }

    private void sendGETRequest(){
        new Thread(() -> {
            try {
                URL urlObject = new URL(String.format(GET_URL, code, pageNumber));

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

                            for (int i=0; i < obj.getJSONArray("data").length(); i++) {
                                JSONObject mapItem = obj.getJSONArray("data").getJSONObject(i);
                                mapPointsClass.add(new MapPointsClass(
                                        mapItem.getString("id"),
                                        mapItem.getString("name"),
                                        mapItem.getString("country"),
                                        mapItem.getDouble("lat"),
                                        mapItem.getDouble("lon")));
                            }

                            handler.post(() -> {
                                if (mapPointsClass.size() > 10) {
                                    mapPointsClass.remove(mapPointsClass.size() - 11);
                                    adapter.notifyItemRemoved(mapPointsClass.size());
                                }
                                adapter.notifyDataSetChanged();
                                adapter.setLoaded();
                            });
                        }
                        break;
                    }
                    responseStr = response.readLine();
                }
                pageNumber++;
                response.close();
                printWriter.close();
                socket.close();
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}