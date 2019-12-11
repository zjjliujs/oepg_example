package com.openglesbook;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.ljs.android.oepg_ch6.R;
import com.ljs.android.oepg_ch6.databinding.ActivityMainBinding;
import com.openglesbook.base.MyBaseActivity;

public class MainActivity extends MyBaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.demoList.setLayoutManager(layoutManager);
        binding.demoList.setAdapter(new MainRVAdapter(this));
        binding.demoList.setItemAnimator(new DefaultItemAnimator());
    }
}
