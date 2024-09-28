package com.example.vpnanuj;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class splash extends AppCompatActivity implements Runnable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000); // Sleep for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Use splash.this to reference the context of the splash activity
                Intent intent = new Intent(splash.this, next.class);
                startActivity(intent);
                finish(); // Close splash activity
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public void run() {
        // You can implement other actions here if needed
    }
}
