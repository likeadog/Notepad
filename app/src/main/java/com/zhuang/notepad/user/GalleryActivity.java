package com.zhuang.notepad.user;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.model.Note;
import com.zhuang.notepad.network.NoteService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.network.ReturnDataList;
import com.zhuang.notepad.network.UserService;
import com.zhuang.notepad.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryActivity extends BaseActivity {

    private ZRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<String> mDatas = new ArrayList<>();
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary);

        recyclerView = (ZRecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        galleryAdapter = new GalleryAdapter();
        recyclerView.setAdapter(galleryAdapter);
        recyclerView.setOnItemClickListener(new ZRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent= new Intent(GalleryActivity.this,PhotoCheckActivity.class);
                intent.putExtra("url",mDatas.get(position));
                startActivity(intent);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshImageList();
            }
        });

        getImageList();
    }

    /**
     * 获取图片url列表
     */
    public void getImageList() {
        UserService service = RetrofitHelper.createService(UserService.class);
        Call<List<String>> call = service.getImageUrlList();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> list = response.body();
                mDatas.clear();
                mDatas.addAll(list);
                galleryAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, t.toString());
            }
        });
    }

    /**
     * 刷新图片url列表
     */
    public void refreshImageList() {
        UserService service = RetrofitHelper.createService(UserService.class);
        Call<List<String>> call = service.refreshImageUrlList();
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> list = response.body();
                mDatas.clear();
                mDatas.addAll(list);
                galleryAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, t.toString());
            }
        });
    }

    public void refreshImageClick(View view){
        swipeRefreshLayout.setRefreshing(true);
        refreshImageList();
    }

    class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    GalleryActivity.this).inflate(R.layout.item_gallery, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.imageView.setImageURI(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView imageView;

            public MyViewHolder(View view) {
                super(view);
                imageView = (SimpleDraweeView) view.findViewById(R.id.imageView);
            }
        }
    }
}
