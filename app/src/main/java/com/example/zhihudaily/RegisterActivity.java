package com.example.zhihudaily;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText account;
    private EditText cipher;
    private EditText cipher2;
    private EditText name;
    private Button register;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
        setContentView(R.layout.layout_register);

        initUI();

        register.setOnClickListener(this);
    }

    private void initUI() {

        account = findViewById(R.id.et_account);
        cipher = findViewById(R.id.et_cipher);
        name = findViewById(R.id.et_name);
        cipher2 = findViewById(R.id.et_cipher_2);
        register = findViewById(R.id.register);

    }

    @Override
    public void onClick(View v) {
        String str_account = account.getText().toString();
        String str_cipher = cipher.getText().toString();
        String str_name = name.getText().toString();
        String str_cipher2= cipher2.getText().toString();

        MydataBaseHelper dataBaseHelper = new MydataBaseHelper(RegisterActivity.this);

        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();

        Cursor cursor = database.query("user",new String[]{"account"},null, null,null,null,null);

        if (cursor.moveToFirst()){
            String account_exist = cursor.getString(cursor.getColumnIndex("account"));
            if (str_account.equals(account_exist))
            {{ Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show(); return;}}

            while (cursor.moveToNext()) {
                String account_exist1  = cursor.getString(cursor.getColumnIndex("account"));
                if (str_account.equals(account_exist1))
                {{ Toast.makeText(RegisterActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show(); return;}}
            }
        }



        if (str_account.length()<4)
        { Toast.makeText(RegisterActivity.this, "用户名至少4个数字！", Toast.LENGTH_SHORT).show(); return;}
        else if (str_cipher.length()<4)
        { Toast.makeText(RegisterActivity.this, "密码至少4个字符！", Toast.LENGTH_SHORT).show(); return;}
        else if (str_name.length()==0)
        { Toast.makeText(RegisterActivity.this, "昵称不能为空！", Toast.LENGTH_SHORT).show(); return;}
        else if (str_name.length()>8)
        {Toast.makeText(RegisterActivity.this, "昵称最多输入8个字符！", Toast.LENGTH_SHORT).show(); return;}
        else if (!str_cipher.equals(str_cipher2))
        {Toast.makeText(RegisterActivity.this, "两次密码输入不一致！", Toast.LENGTH_SHORT).show(); return;}
        else{
            database.execSQL("insert into user(account,cipher,name) values('" + str_account + "','" + str_cipher + "','" + str_name + "');");
            cursor.close();
            database.close();
            Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}