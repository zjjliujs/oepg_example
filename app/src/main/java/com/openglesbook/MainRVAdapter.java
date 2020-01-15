package com.openglesbook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.ljs.android.oepg_ch6.R;
import com.openglesbook.ch6_example_3.Example6_3Activity;
import com.openglesbook.ch6_example_6.Example6_6Activity;
import com.openglesbook.ch6_map_buffers.MapBuffersActivity;
import com.openglesbook.ch6_vao.VAOActivity;
import com.openglesbook.ch6_vbo.VBOActivity;
import com.openglesbook.ch9_texture2d.SimpleTexture2DActivity;
import com.openglesbook.ch9_texture_wrap.TextureWrapActivity;

import java.util.ArrayList;
import java.util.List;

class MainRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<Pair<Integer, Class>> data;

    public MainRVAdapter(Context context) {
        this.context = context;
        initData();
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(new Pair<>(R.string.example_ch2_hello_triangle, Example6_3Activity.class));
        data.add(new Pair<>(R.string.example_6_3, Example6_3Activity.class));
        data.add(new Pair<>(R.string.example_6_6, Example6_6Activity.class));
        data.add(new Pair<>(R.string.ch6_map_buffer, MapBuffersActivity.class));
        data.add(new Pair<>(R.string.ch6_vao, VAOActivity.class));
        data.add(new Pair<>(R.string.ch6_vbo, VBOActivity.class));
        data.add(new Pair<>(R.string.ch9_simple_texture2d, SimpleTexture2DActivity.class));
        data.add(new Pair<>(R.string.ch9_texture_wrap, TextureWrapActivity.class));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHolder(LayoutInflater.from(context).inflate(R.layout.rv_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        vHolder.titleView.setText(context.getString(data.get(position).first));
        vHolder.titleView.setOnClickListener(v -> {
            Class clazz = data.get(position).second;
            Intent intent = new Intent(context, clazz);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHolder extends RecyclerView.ViewHolder {
        TextView titleView;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
        }
    }
}
