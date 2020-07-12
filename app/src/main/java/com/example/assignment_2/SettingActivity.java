package com.example.assignment_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    private Button mBtn_changePassword, mBtn_changeLockOption;
    private ImageButton mBtn_back_from_settings;


    /**
     * 여기서부터는 각각의 기능 단위로 묶어놓은 메서드 입니다.
     * */

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "setting_activity");
        editor.apply();
    }

//    셋팅 액티비티에서 뒤로가기 버튼을 눌렀을 때, 인텐트를 통해 메인액티비티로 이동하도록 치환해주는 메서드
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
    }

//    앱을 완전히 종료할 때, 다음에 다시 켜서 잠금을 풀면 write activity에서 시작할 수 있도록 onDestroy()에서 호출
    public void makeWritingActivityFirst(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "writing_activity");
        editor.apply();
    }


    /**
     * 여기서부터는 생명주기 입니다.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mBtn_changePassword = findViewById(R.id.btn_changePassword);
        mBtn_changeLockOption = findViewById(R.id.btn_changeLockOption);
        mBtn_back_from_settings = findViewById(R.id.btn_back_from_settings);

        mBtn_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });


        mBtn_changeLockOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangeLockOptionActivity.class);
                startActivity(intent);
            }
        });

        mBtn_back_from_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStop() {
        save_LastActivityName();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Intent intent = new Intent(SettingActivity.this, LockActivity.class);
        startActivity(intent);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        makeWritingActivityFirst();
        super.onDestroy();
    }
}
