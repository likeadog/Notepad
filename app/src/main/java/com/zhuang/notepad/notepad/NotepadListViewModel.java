package com.zhuang.notepad.notepad;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.zhuang.notepad.BR;
import com.zhuang.notepad.model.Note;
import com.zhuang.notepad.network.BaseReturnMsg;
import com.zhuang.notepad.network.NoteService;
import com.zhuang.notepad.network.RetrofitHelper;
import com.zhuang.notepad.network.ReturnDataList;
import com.zhuang.notepad.view.ZRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zhuang on 2017/5/23.
 */

public class NotepadListViewModel extends BaseObservable {
    public NoteListAdapter adapter;
    private List<Note> noteList = new ArrayList<>();
    private String TAG = getClass().getSimpleName();
    private Activity context;
    public boolean refreshing = true;
    public final static int UPDATE = 12;

    public NotepadListViewModel(Activity context) {
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
                if (list.getCode() == 0) {
                    noteList.clear();
                    noteList.addAll(list.getData());
                    adapter.notifyDataSetChanged();
                    setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<ReturnDataList<Note>> call, Throwable t) {
                Log.e(TAG, t.toString());
                setRefreshing(false);
            }
        });
    }

    /**
     * 删除日记
     */
    public void deleteNote(String ids) {
        NoteService service = RetrofitHelper.createServiceWidthToken(NoteService.class);
        Call<BaseReturnMsg> call = service.deleteNote(ids);
        call.enqueue(new Callback<BaseReturnMsg>() {
            @Override
            public void onResponse(Call<BaseReturnMsg> call, Response<BaseReturnMsg> response) {

            }

            @Override
            public void onFailure(Call<BaseReturnMsg> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getNoteList();
        }
    };

    public ZRecyclerView.OnLongItemClickListener longItemClickListener = new ZRecyclerView.OnLongItemClickListener() {
        @Override
        public void OnLongItemClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Note note = noteList.get(position);
                        deleteNote(note.getId()+"");
                        noteList.remove(position);
                        if (adapter.getItemCount()>0) {
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, adapter.getItemCount()-position);
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            builder.create().show();
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
            intent.putExtra("note", note.getNote());
            intent.putExtra("time", note.getTime());
            intent.putExtra("noteId", note.getId());
            intent.putExtra("position",position);
            context.startActivityForResult(intent,UPDATE);
        }
    };

    public void updateNote(int position,String content){
        Note note = noteList.get(position);
        note.setNote(content);
        adapter.notifyDataSetChanged();
    }

    @Bindable
    public boolean getRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
        notifyPropertyChanged(BR.refreshing);
    }

    public void refresh() {
        setRefreshing(true);
        noteList.clear();
        getNoteList();
    }
}
