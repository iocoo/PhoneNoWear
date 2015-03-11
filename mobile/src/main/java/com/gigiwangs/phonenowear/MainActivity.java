package com.gigiwangs.phonenowear;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

import com.gigiwangs.phonenowear.database.DBHelper;
import android.util.Log;



public class MainActivity extends ActionBarActivity {

    private TextView TextView2;
    private TextView TextView3;
    private Button Button1;
    private static DBHelper helper;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView2 = (TextView) findViewById(R.id.textView2);
        TextView3 = (TextView) findViewById(R.id.textView3);
        //Button1=(Button)findViewById(R.id.btncommit);
        // 初次使用将准备好的数据库文件考入到系统目录
        DBHelper.copyDB(getBaseContext());
        // 获得数据库连接
        helper = DBHelper.getInstance(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBtnCommit(View v) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String phoneNumber = editText.getText().toString();
        String phoneNumberShort="0";
        // 去掉非数字字符
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        boolean pmFlag = ((phoneNumber.charAt(0) == '0') ? true : false);
        //固定电话和手机号码 ture/false
        PhoneArea phoneArea;
        if (pmFlag) {
            TextView3.setText(phoneNumber);
            if(phoneNumber.charAt(1)=='1'||phoneNumber.charAt(1)=='2') {
                phoneNumberShort = phoneNumber.substring(1, 3);
            }
            else
            {
                phoneNumberShort = phoneNumber.substring(1, 4);
            }

            Log.d("MAIN","FLAG IS"+pmFlag);
            if ((phoneArea = helper.findPhoneArea(new String[]{phoneNumberShort
                    .toString()})) != null) {

                TextView2.setText(phoneArea.getArea());

            } else
                TextView2.setText(R.string.none_area);
        }
        else
        {
            if (phoneNumber.length() > 7) {
                phoneNumberShort = phoneNumber.substring(0, 7);
                TextView3.setText(phoneNumber);
            }else
            {
                TextView3.setText(R.string.format_err);
                editText.setText("");
            }
            TextView2.setText(R.string.loading);

            editText.setText("");

            // 查询数据库

            if ((phoneArea = helper.findMobileArea(new String[]{phoneNumberShort
                    .toString()})) != null) {

                TextView2.setText(phoneArea.getArea());

            } else
                TextView2.setText(R.string.none_area);
        }


    }


}
