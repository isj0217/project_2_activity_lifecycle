package com.example.assignment_2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private long mBackKeyPressedTime = 0;
    private Toast mToast;

    private ImageButton mBtn_newNote, mBtn_setting;
    private TextView mTv_summary, mTv_dateToBeFilled;

    private String memo_written, time_written;


    /**
     * 여기서부터는 각각의 기능 단위로 묶어놓은 메서드 입니다.
     * */

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "main_activity");
        editor.apply();
    }

//    앱을 완전히 종료할 때, 다음에 다시 켜서 잠금을 풀면 write activity에서 시작할 수 있도록 onDestroy()에서 호출
    public void makeWritingActivityFirst(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "writing_activity");
        editor.apply();
    }

    public void loadMemoAndTime(){
        SharedPreferences loadMemo = getSharedPreferences("memo_written", MODE_PRIVATE);
        memo_written = loadMemo.getString("memo_written", "");
        if (!memo_written.equals("")) {
            mTv_summary.setText(memo_written);
        }
        SharedPreferences loadTime = getSharedPreferences("time_written", MODE_PRIVATE);
        time_written = loadTime.getString("time_written", "");
        if (!time_written.equals("")) {
            mTv_dateToBeFilled.setText(time_written);
        }
    }

    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            mToast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            mToast.show();
        }
        else if(System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {
            finish();
            mToast.cancel();
        }
    }


    /**
     * 여기서부터는 생명주기 입니다.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv_summary = findViewById(R.id.textViewToBeFilled);
        String letters = getIntent().getStringExtra("writtenLetters");
        if(letters != null) {
            mTv_summary.setText(letters);
        }

        mTv_dateToBeFilled = findViewById(R.id.tv_dateToBeFilled);
        String time = getIntent().getStringExtra("time");
        if(time != null){
            mTv_dateToBeFilled.setText(time);
        }

        mBtn_newNote = findViewById(R.id.addNewNote);
        mBtn_newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String savedLetters = mTv_summary.getText().toString();
                Intent intent = new Intent(MainActivity.this, WritingActivity.class);
                intent.putExtra("savedLetters", savedLetters);
                startActivity(intent);
            }
        });

        mBtn_setting = findViewById(R.id.btn_setting);
        mBtn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        //사용자에게 보여지기 직전에 호출되는 영역

        SharedPreferences sharedPreferences = getSharedPreferences("memo_written", MODE_PRIVATE);
        memo_written = sharedPreferences.getString("memo_written", "");
//        Toast.makeText(this, "loadData 호출 이전의 값은 "+memo_written+ "입니다.", Toast.LENGTH_SHORT).show();

        loadMemoAndTime();
        super.onStart();
    }

    @Override
    protected void onStop() {
        save_LastActivityName();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Intent intent = new Intent(MainActivity.this, LockActivity.class);
        startActivity(intent);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        makeWritingActivityFirst();
        super.onDestroy();
    }
}
