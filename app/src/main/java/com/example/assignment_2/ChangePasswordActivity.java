package com.example.assignment_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.locks.Lock;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton mBtn_btn_back_from_changing_password;

    private String next_1="";
    private String next_2="";

    private int mFlag_presentPassword = 0;
    private int mFlag_newPasswordSame = 0;

    private Button mBtn_confirm_newPassword;

    private EditText mEt_present, mEt_new_1, mEt_new_2;


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

//    비밀번호를 사용자가 원하는 번호로 변경해주는 메소드
    public void setPassword(String pw){
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PASSWORD", pw);
        editor.apply();
    }

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "changePassword_activity");
        editor.apply();
    }

//    뒤로가기 버튼을 임시로 Intent로 치환한 메서드(이미지버튼을 통해 뒤로가지 않고 시스템 버튼을 통해 뒤로가기 했을때 LockActivity로 돌아가는 오류가 발생하기 때문)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
        startActivity(intent);
    }



    /**
     * 여기서부터는 생명주기 입니다.
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mBtn_btn_back_from_changing_password = findViewById(R.id.btn_back_from_changing_password);
        mBtn_btn_back_from_changing_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        mBtn_confirm_newPassword = findViewById(R.id.btn_passwordChangeConfirm);

        mEt_present = findViewById(R.id.et_present_password);
        mEt_new_1 = findViewById(R.id.et_new_password_1);
        mEt_new_2 = findViewById(R.id.et_new_password_2);

        mBtn_confirm_newPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFlag_presentPassword = 0;
                mFlag_newPasswordSame = 0;

                checkPresentPassword();
                checkNextPasswordSame();

                if(mFlag_presentPassword + mFlag_newPasswordSame == 2){
                    setPassword(next_1);
                    Toast.makeText(ChangePasswordActivity.this, "비밀번호가 " + next_1 + "로 정상 변경됨", Toast.LENGTH_SHORT).show();
                }

                else if(mFlag_presentPassword == 0 && mFlag_newPasswordSame == 1){
                    Toast.makeText(ChangePasswordActivity.this, "현재 비밀번호를 잘못 입력하셨습니다.", Toast.LENGTH_SHORT).show();
                }

                else if(mFlag_presentPassword == 1 && mFlag_newPasswordSame == 0){
                    Toast.makeText(ChangePasswordActivity.this, "변경할 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else if(mFlag_presentPassword + mFlag_newPasswordSame == 0){
                    Toast.makeText(ChangePasswordActivity.this, "현재 비밀번호도 틀렸고,\n변경할 비밀번호도 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void checkPresentPassword() {
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD", MODE_PRIVATE);
        String present = sharedPreferences.getString("PASSWORD", "");
        if(present.equals(mEt_present.getText().toString())){
            mFlag_presentPassword = 1;
        }
    }

    public void checkNextPasswordSame() {
        next_1 = mEt_new_1.getText().toString();
        next_2 = mEt_new_2.getText().toString();

        if(next_1.equals(next_2)){
            mFlag_newPasswordSame = 1;
        }
    }

    @Override
    protected void onStop() {
        save_LastActivityName();
        super.onStop();
    }

    @Override
    protected void onRestart() {

        Intent intent = new Intent(ChangePasswordActivity.this, LockActivity.class);
        startActivity(intent);

        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        makeWritingActivityFirst();
        super.onDestroy();
    }
}
