package com.example.assignment_2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.locks.Lock;

public class LockActivity extends AppCompatActivity {

    private String password = null;

    private EditText mEt_password;
    private Button mBtn_enterPassword;

    private long mBackKeyPressedTime = 0;
    private Toast mToast;


    /**
     * 여기서부터는 각각의 기능 단위로 묶어놓은 메서드 입니다.
     * */

//    sharedPreference를 활용하여 현재 진짜 비밀번호를 꺼내오는 메서드 (onClick 리스너를 달면서 비밀번호가 맞는지 확인하기 때문에, 그 전에 실제 비밀번호를 불러와야 하므로 onCreate()에서 호출함)
    public void loadPassword(){
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD", MODE_PRIVATE);
        String PASSWORD = sharedPreferences.getString("PASSWORD", "");
        if(!PASSWORD.equals("")){
        password = PASSWORD;
        }else{
            password = "0000";              //앱을 설치하고 처음 실행할 때 한 번은, SharedPreference를 통해 꺼내온 값이 없으므로 비밀번호를 "0000"으로 초기화 해준다!!!
            SharedPreferences initPassword = getSharedPreferences("PASSWORD", MODE_PRIVATE);
            SharedPreferences.Editor editor = initPassword.edit();
            editor.putString("PASSWORD", password);
            editor.apply();
        }
    }

//    사용자가 입력중인 비밀번호 sharedPreference로 보관!! (데이터를 저장하는 기능이니까 onStop()에서 해야겠다. onPause()에서 하면 문자메시지가 오거나 멀티윈도우일 때도 불필요하게 저장할 수 있음)
    public void saveInputtingPassword(){
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD_INPUTTING", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        mEt_password = findViewById(R.id.et_password);
        editor.putString("PASSWORD_INPUTTING", mEt_password.getText().toString());
        editor.apply();
    }

//    사용자가 입력중이던 비밀번호 sharedPreference에서 다시 불러오기!! (다시 onCreate된다는 보장이 없고, 사용자에게 보여지기 전에 해야하니까 onStart()에서 하자)
    public void loadInputtingPassword(){
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD_INPUTTING", MODE_PRIVATE);
        String password_inputting = sharedPreferences.getString("PASSWORD_INPUTTING", "");
        mEt_password = findViewById(R.id.et_password);
        mEt_password.setText(password_inputting);
    }

//    사용자가 입력중이던 비밀번호를 SharedPreference에서 싹 지우기!! (화면이 꺼졌다가 켜질 때는 비밀번호가 남아있어야 하고, 다른 액티비티로 넘어가거나 앱이 다시 켜질때는 지워져야 하므로 onCreate에서 하자)
    public void eraseInputtingPassword(){
        SharedPreferences sharedPreferences = getSharedPreferences("PASSWORD_INPUTTING", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PASSWORD_INPUTTING", "");
        editor.apply();
    }

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "writing_activity");
        editor.apply();
    }

//    잠금화면이 끝나면, 원래 있던 액티비티로 돌려보내줘야지!! -> 비밀번호가 일치할 때 onClick에서 하면 될듯. ---> 생명주기는? 보이기 직전인 onStart? 아니면 onCreate? - 리스너 달아줄 때 해야하니까 onCreate
    public void sendUserToLastActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        String recentActivity = sharedPreferences.getString("present_activity", "");

        switch (recentActivity) {

            case "":
            case "lock_activity":
            case "writing_activity":
                Intent goTo_write = new Intent(LockActivity.this, WritingActivity.class);
                startActivity(goTo_write);
                break;

            case "main_activity":
                Intent goTo_main = new Intent(LockActivity.this, MainActivity.class);
                startActivity(goTo_main);
                break;

            case "setting_activity":
                Intent goTo_setting = new Intent(LockActivity.this, SettingActivity.class);
                startActivity(goTo_setting);
                break;

            case "changePassword_activity":
                Intent goTo_changePassword = new Intent(LockActivity.this, ChangePasswordActivity.class);
                startActivity(goTo_changePassword);
                break;

            case "changeLockOption_activity":
                Intent goTo_changLockOption = new Intent(LockActivity.this, ChangeLockOptionActivity.class);
                startActivity(goTo_changLockOption);
                break;

            default:
                Toast.makeText(this, "알 수 없는 오류: Recent Activity의 위치를 찾을 수 없음", Toast.LENGTH_SHORT).show();
                break;
        }

    }

//    뒤로가기 두 번 누르면 종료시키는 메소드
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
        setContentView(R.layout.activity_lock);

        loadPassword();             //현재 진짜 비밀번호를 sharedPreference로 꺼내옴
        eraseInputtingPassword();   //사용자가 입력하다가 말았던 비밀번호 날려버림

        mEt_password = findViewById(R.id.et_password);
        mBtn_enterPassword = findViewById(R.id.btn_confirm);

        mBtn_enterPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = mEt_password.getText().toString();

                if(input.equals(password)){
                    sendUserToLastActivity();
                    finish();
                }else{
                    Toast.makeText(LockActivity.this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        loadInputtingPassword();    //사용자가 비밀번호를 써놓은 만큼은 다시 복구해주자. 사용자에게 화면이 보이기 전에는 비밀번호를 load해서 채워놓아야 하므로 resume이 아니라 onStart로 가게 된다.
                                    // 단, 앱을 완전히 껐을 때는 날려주어야 하므로 onCreate에서는 ""로 초기화해주자.
        super.onStart();
    }

    @Override
    protected void onStop() {
        saveInputtingPassword();        //액티비티가 백그라운드로 가면서 호출되는 메서드이므로 이때까지 사용자가 입력한 비밀번호를 저장하자
        save_LastActivityName();        //현재 액티비티가 어디였인지, 그 이름을 저장하자
                                        //이 두가지 모두 onPause에서 실행된다면, 멀티윈도우나 화면 회전을 지원하지 못할 뿐만 아니라, 팝업창이 뜨거나 문자가 왔을 때에도 불필요하게 데이터를 저장하게 된다.
        super.onStop();
    }

}
