package com.zhuang.notepad.network;

import com.zhuang.notepad.model.Note;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by zhuang on 2017/5/23.
 */

public interface NoteService {

    @GET("ser/note")
    Call<ReturnDataList<Note>> getNoteList();

    @GET("ser/addnote")
    Call<BaseReturnMsg> addNote(@Query("note") String note);
}