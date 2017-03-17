package com.ethan.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ethan.menu.lib.BottomMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mList;
    private List<String> datas;
    private BottomMenu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = (RecyclerView) findViewById(R.id.list);
        mList.setLayoutManager(new LinearLayoutManager(this));
        datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("test" + i);
        }
        mList.setAdapter(new MyAdapter());
        mMenu = (BottomMenu) findViewById(R.id.home_menu);
        mMenu.setOnMenuItemClickListener(new BottomMenu.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int pos) {
                Toast.makeText(MainActivity.this, "item" + pos, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = MainActivity.this.getLayoutInflater().inflate(R.layout.item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.view.setText(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView view;

            public MyViewHolder(View itemView) {
                super(itemView);
                view = (TextView) itemView.findViewById(R.id.str);
            }
        }
    }

}
