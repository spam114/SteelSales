package com.symbol.steelsales.Activity;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.symbol.steelsales.Application.ApplicationClass;


public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        applySystemBarInsets();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        applySystemBarInsets();
    }

    private void applySystemBarInsets() {
        View content = findViewById(android.R.id.content);

        ViewCompat.setOnApplyWindowInsetsListener(content, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    bars.left,
                    v.getPaddingTop(),   // 상단은 유지
                    bars.right,
                    bars.bottom          // 하단 가상키 영역
            );
            return insets;
        });
    }


    public int checkTagState(String tag) {
        return ApplicationClass.getInstance().checkTagState(tag);
    }

    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
    }

    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON(this, message, handler);
    }

    public void progressOFF(String className) {
        ApplicationClass.getInstance().progressOFF(className);
    }

    public void progressOFF2(String className) {
        ApplicationClass.getInstance().progressOFF2(className);
    }

    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }
}