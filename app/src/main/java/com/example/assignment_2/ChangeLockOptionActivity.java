package com.example.assignment_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangeLockOptionActivity extends AppCompatActivity {

    private ImageButton mBtn_btn_back_from_changing_lock_option;
    private Switch mSwitch_always_ask_password;

    SharedPreferences sharedPreferences;


    /**
     * 여기서부터는 각각의 기능 단위로 묶어놓은 메서드 입니다.
     * */

//    앱을 완전히 종료할 때, 다음에 다시 켜서 잠금을 풀면 write activity에서 시작할 수 있도록 onDestroy()에서 호출
    public void makeWritingActivityFirst(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "writing_activity");
        editor.apply();
    }

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "changeLockOption_activity");
        editor.apply();
    }

//    뒤로가기 버튼을 임시로 Intent로 치환한 메서드(이미지버튼을 통해 뒤로가지 않고 시스템 버튼을 통해 뒤로가기 했을때 LockActivity로 돌아가는 오류가 발생하기 때문)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChangeLockOptionActivity.this, SettingActivity.class);
        startActivity(intent);
    }


    /**
     * 여기서부터는 생명주기 입니다.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_lock_option);

        mBtn_btn_back_from_changing_lock_option = findViewById(R.id.btn_back_from_changing_lock_option);
        mBtn_btn_back_from_changing_lock_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeLockOptionActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        mSwitch_always_ask_password = findViewById(R.id.switch_alwaysAskPassword);
        mSwitch_always_ask_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(ChangeLockOptionActivity.this, "다른 작업을 하다가 돌아왔을 때마다 잠금을 표시합니다.", Toast.LENGTH_SHORT).show();
                }else if(!isChecked){
                    Toast.makeText(ChangeLockOptionActivity.this, "앱을 실행할때 첫 화면에만 잠금을 표시합니다.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onStart() {
        sharedPreferences = getSharedPreferences("switchStatus", MODE_PRIVATE);
        boolean onOff = sharedPreferences.getBoolean("switchStatus", true);
        mSwitch_always_ask_password.setChecked(onOff);
        super.onStart();
    }

    @Override
    protected void onStop() {                                                               //스위치의 상태(on/off) 저장
        sharedPreferences = getSharedPreferences("switchStatus", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switchStatus", mSwitch_always_ask_password.isChecked());
        editor.apply();

        save_LastActivityName();
        super.onStop();
    }

    @Override
    protected void onRestart() {

        Intent intent = new Intent(ChangeLockOptionActivity.this, LockActivity.class);
        startActivity(intent);
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        makeWritingActivityFirst();
        super.onDestroy();
    }
}
