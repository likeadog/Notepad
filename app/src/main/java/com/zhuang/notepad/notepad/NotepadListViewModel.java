package com.zhuang.notepad.notepad;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.zhuang.notepad.BR;
import com.zhuang.notepad.model.Note;
import com.zhuang.notepad.network.NoteService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.network.ReturnDataList;
import com.zhuang.notepad.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhuang on 2017/5/23.
 */

public class NotepadListViewModel extends BaseObservable{
    public NoteListAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private String TAG = getClass().getSimpleName();
    private Context context;
    public boolean refreshing = true;

    public NotepadListViewModel(Context context) {
        this.context = context;
        adapter = new NoteListAdapter(noteList);
        getNoteList();
    }

    /**
     * 获取日记列表
     */
    public void getNoteList() {
        NoteService service = RetrofitHelper.createServiceWidthToken(NoteService.class);
        Call<ReturnDataList<Note>> call = service.getNoteList();
        call.enqueue(new Callback<ReturnDataList<Note>>() {
            @Override
            public void onResponse(Call<ReturnDataList<Note>> call, Response<ReturnDataList<Note>> response) {
                ReturnDataList<Note> list = response.body();
                //成功
                if(list.getCode() == 0){
                    noteList.addAll(list.getData());
                    adapter.notifyDataSetChanged();
                    setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ReturnDataList<Note>> call, Throwable t) {
                Log.e(TAG,t.toString());
                setRefreshing(false);
            }
        });
    }

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            noteList.clear();
            getNoteList();
        }
    };

    /**
     * 列表点击事件
     */
    public ZRecyclerView.OnItemClickListener itemClickListener = new ZRecyclerView.OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Note note = noteList.get(position);
            Intent intent = new Intent(context, NotepadDetailActivity.class);
            intent.putExtra("note",note.getNote());
            intent.putExtra("time",note.getTime());
            context.startActivity(intent);
        }
    };

    @Bindable
    public boolean getRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    public void refresh(){
        setRefreshing(true);
        noteList.clear();
        getNoteList();
    }
}
