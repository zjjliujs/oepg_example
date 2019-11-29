package com.ljs.android.oepg_ch6;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.ljs.android.oepg_ch6.databinding.ActivityMainBinding;
import com.openglesbook.example6_3.Example6_3;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        binding.example63Btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, Example6_3.class);
            startActivity(intent);
        });
    }
}
