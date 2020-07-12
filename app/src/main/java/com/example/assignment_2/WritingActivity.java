package com.example.assignment_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WritingActivity extends AppCompatActivity {

    private ImageButton mBtn_save;
    private EditText mEt_memo;

    private String mTimeNow, mLettersToBeSaved;

    private long mBackKeyPressedTime = 0;
    private Toast mToast;

    private String memo_written;


    /**
     * 여기서부터는 각각의 기능 단위로 묶어놓은 메서드 입니다.
     * */

//    지금까지 사용자가 마지막으로 어떤 액티비티에 머물렀는지를 저장!! -> 화면이 꺼질 때 저장하되, 다음 액티비티가 생성되고 나서 남겨야하니까 onStop으로 가는것이 적당할 듯
    public void save_LastActivityName(){
        SharedPreferences sharedPreferences = getSharedPreferences("present_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("present_activity", "writing_activity");
        editor.apply();
    }

//    지금 시간을 String으로 return해주는 메서드
    public String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy\nMM/dd\nHH:mm");
        return dateFormat.format(date);
    }

//    지금까지 EditText에 작성한 내용을 String으로 return해주는 메서드
    public String getMemo() {
        return mEt_memo.getText().toString();
    }

//    지금까지 적은 메모와 시간을 인텐트와 함께 다음 액티비티로 넘겨주는 메서드
    public void goTo_nextActivity(){
        Intent intent = new Intent(WritingActivity.this, MainActivity.class);
        intent.putExtra("writtenLetters", mLettersToBeSaved);
        intent.putExtra("time", mTimeNow);
        startActivity(intent);
    }

//    sharedPreferences를 이용하여 메모 내용과 작성 시간을 저장해주는 메서드
    public void save_MemoAndTime(){
        SharedPreferences saving_memo = getSharedPreferences("memo_written", MODE_PRIVATE);
        SharedPreferences.Editor editor_saving_memo = saving_memo.edit();
        editor_saving_memo.putString("memo_written", mEt_memo.getText().toString());
        editor_saving_memo.apply();

        SharedPreferences saving_time = getSharedPreferences("time_written", MODE_PRIVATE);
        SharedPreferences.Editor editor_saving_time = saving_time.edit();
        editor_saving_time.putString("time_written", mTimeNow);
        editor_saving_time.apply();

        Toast.makeText(this, "저장됨", Toast.LENGTH_SHORT).show();
    }

//    과거에 작성되었던 메모를 불러와 다시 채워놓는 메서드
    public void loadMemo(){
        SharedPreferences sharedPreferences = getSharedPreferences("memo_written", MODE_PRIVATE);
        memo_written = sharedPreferences.getString("memo_written", "");
        mEt_memo.setText(memo_written);
    }

//    작성중이던 메모가 있습니다. 이어서 작성하시겠습니까? 라는 팝업을 띄워주는 메서드
    public void buildPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
        builder.setTitle("작성중이던 메모가 있습니다").setMessage("이어서 작성하시겠습니까?").setCancelable(false);
        builder.setPositiveButton("저장 후 종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save_MemoAndTime();
                finish();
            }
        }).setNegativeButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

//    뒤로가기 두 번 누르면 종료시키는 메서드
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
        setContentView(R.layout.activity_writing);

        mBtn_save = findViewById(R.id.btn_save);
        mEt_memo = findViewById(R.id.et_memo);

        String letters = getIntent().getStringExtra("savedLetters");
        if(letters != null) {
            mEt_memo.setText(letters);
        }

        mBtn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mLettersToBeSaved = getMemo();
                mTimeNow = getTime();
                goTo_nextActivity();
                finish();
            }
        });

        loadMemo();
    }


    @Override
    protected void onStart() {
        loadMemo();
        super.onStart();
    }

    @Override
    protected void onResume() {

        mEt_memo.setSelection(mEt_memo.getText().length());             //제일 오른쪽 부분에 커서가 올 수 있도록 위치 지정!!
        mEt_memo.requestFocus();                                        //사용자와 상호작용하기 편리하도록 EditText에 포커스를 주어 커서가 깜빡거리게 해주고 키보드도 올려준다.

        super.onResume();
    }

    @Override
    protected void onPause() {
        save_MemoAndTime();                 //지금까지 작성한 메모와 현재시간 저장
                                            //비록 안드로이드 개발자 문서에서는 onPause()가 아닌 onStop에서 데이터를 저장하라고 권고하고 있으나,
                                            //바로 다음 액티비티의 화면에 "메모"와 "시간"을 띄워야 하므로 불가피하게 onPause()에서 저장한다.
        super.onPause();
    }

    @Override
    protected void onStop() {               //실제로 데이터 값을 sharedPreference로 저장하는 메소드
        save_LastActivityName();            //지금까지 머무르던 액티비티의 이름 저장

        save_MemoAndTime();                 //혹시나 onPause()가 실행되지 않더라도 저장은 꼭 해 두어야 하므로
                                            //onPause()와 onStop()에 중복으로 호출한다.
        super.onStop();
    }

    @Override
    protected void onRestart() {        //작성중이던 메모가 있습니다. 이어서 작성하시겠습니까? 라는 팝업 띄우기
        buildPopup();
        super.onRestart();
    }


}
