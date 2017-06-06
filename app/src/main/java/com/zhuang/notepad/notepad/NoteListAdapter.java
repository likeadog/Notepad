package com.zhuang.notepad.notepad;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zhuang.notepad.R;
import com.zhuang.notepad.databinding.ItemNoteBinding;
import com.zhuang.notepad.model.Note;

import java.util.List;

/**
 * Created by zhuang on 2017/6/2.
 */

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<Note> mList;

    public NoteListAdapter(List<Note> list) {
        this.mList = list;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private ItemNoteBinding binding;

        public NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {
            binding.setNote(note);
            binding.executePendingBindings();
        }
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemNoteBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_note,
                parent,
                false);
        return new NoteViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        final Note note = mList.get(position);
        holder.bind(note);
    }
}
