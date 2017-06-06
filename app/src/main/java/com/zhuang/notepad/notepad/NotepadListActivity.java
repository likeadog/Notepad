package com.zhuang.notepad.notepad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.zhuang.notepad.BaseActivity;
import com.zhuang.notepad.R;
import com.zhuang.notepad.databinding.ActivityNoteListBinding;
import com.zhuang.notepad.databinding.NavHeaderNoteListBinding;
import com.zhuang.notepad.login.LoginActivity;
import com.zhuang.notepad.user.UpdatePasswordActivity;
import com.zhuang.notepad.user.UserActivity;

public class NotepadListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityNoteListBinding binding;
    private NotepadListViewModel viewModel;
    public static final int REQUEST_ADD_NOTE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);
        NavHeaderNoteListBinding.bind(binding.navView.getHeaderView(0));
        viewModel = new NotepadListViewModel(this);
        binding.setViewModel(viewModel);
    }

    /**
     * 跳转到用户资料界面
     *
     * @param view
     */
    public void gotoUser(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_password) {
            updatePassword();
        } else if (id == R.id.nav_login_out) {
            loginOut();
        } else if (id == R.id.nav_theme) {

        } else if (id == R.id.nav_setting) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updatePassword() {
        Intent intent = new Intent(this, UpdatePasswordActivity.class);
        startActivity(intent);
    }

    /**
     * 退出登录
     */
    private void loginOut() {
        SharedPreferences sp = getSharedPreferences("notepad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("token");
        editor.remove("refreshtoken");
        editor.commit();

        //跳转到登陆页
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void gotoAddNote(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NOTE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ADD_NOTE) {
                Log.e(TAG,"refresh");
                viewModel.refresh();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
