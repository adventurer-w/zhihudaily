package com.example.zhihudaily;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zhihudaily.SharedPreferencesUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Button bt_load;
    private EditText et_account;
    private EditText et_cipher;
    private TextView textView;
    private SharedPreferencesUtil check;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorMain));
        setContentView(R.layout.layout_login);

        initUI();

        et_account = findViewById(R.id.et_1);
        et_cipher = findViewById(R.id.et_2);
        bt_load = findViewById(R.id.load);
        textView = findViewById(R.id.to_register);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView.getPaint().setAntiAlias(true);
        bt_load.setOnClickListener(this);
        textView.setOnClickListener(this);

    }

    private void initUI() {
        check = SharedPreferencesUtil.getInstance(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        int flag =0;
        switch (view.getId()) {
            case R.id.to_register:
                Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_register);
                break;
            case R.id.load:

                String account_query = et_account.getText().toString();
                String cipher_compare = et_cipher.getText().toString();
                if (account_query.length()==0)
                {flag=1; Toast.makeText(LoginActivity.this, "请输入用户名！", Toast.LENGTH_SHORT).show(); return; }
                else if (cipher_compare.length()==0)
                {flag=1; Toast.makeText(LoginActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show(); return; }
                else {
                    MydataBaseHelper dataBaseHelper = new MydataBaseHelper(LoginActivity.this);
                    SQLiteDatabase database = dataBaseHelper.getReadableDatabase();


                    Cursor cursor = database.query("user", new String[]{"account", "cipher"}, "account=?", new String[]{account_query}, null, null, null);
                    if (cursor.moveToFirst()) {


                        String cipher = cursor.getString(cursor.getColumnIndex("cipher"));

                        cursor.close();
                        database.close();


                        if (cipher.equals(cipher_compare)) {
                            flag = 1;
                            Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_LONG).show();

                            MydataBaseHelper dataBaseHelper_1 = new MydataBaseHelper(LoginActivity.this);
                            SQLiteDatabase database_1 = dataBaseHelper_1.getReadableDatabase();


                            Cursor cursor_1 = database_1.query("user", new String[]{"account_id"}, "account=?", new String[]{account_query}, null, null, null);
                            if (cursor_1.moveToFirst()) {
                                Intent intent_load = new Intent(LoginActivity.this, MainActivity.class);

                                String account_id = cursor_1.getString(cursor_1.getColumnIndex("account_id"));
                                cursor_1.close();
                                database_1.close();

                                startActivity(intent_load);

                                check.setLogin(true);
                                check.setAccountId(account_id);

                                finish();
                            }
                        }
                        else {
                            Toast.makeText(this, "你输入的账号或密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                        if(flag == 0) {
                            Toast.makeText(this, "你输入的账号或密码不正确", Toast.LENGTH_SHORT).show();
                            return;
                        }
                }
        }
    }
}


