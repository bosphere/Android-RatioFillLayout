package com.bosphere.ratiofilllayoutdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bosphere.ratiofilllayout.RatioFillLayout;

public class MainActivity extends AppCompatActivity {

    private RatioFillLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLayout = (RatioFillLayout) findViewById(R.id.percentage_fill_layout);

        RadioGroup fg = (RadioGroup) findViewById(R.id.rg_orientation);
        fg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_horizontal) {
                    mLayout.setOrientation(RatioFillLayout.HORIZONTAL);
                } else {
                    mLayout.setOrientation(RatioFillLayout.VERTICAL);
                }
            }
        });
    }

    public void addItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Content");

        FrameLayout parent = new FrameLayout(this);
        int pad = getResources().getDimensionPixelSize(R.dimen.padding_common);
        parent.setPadding(pad, 0, pad, 0);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        parent.addView(input);
        builder.setView(parent);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = input.getText().toString();
                TextView tv = new TextView(MainActivity.this);
                tv.setBackgroundResource(R.color.colorAccent);
                tv.setText(content);

                RatioFillLayout.LayoutParams lp = new RatioFillLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                int margin = getResources().getDimensionPixelSize(R.dimen.margin_common);
                lp.leftMargin = lp.rightMargin = lp.topMargin = lp.bottomMargin = margin;
                mLayout.addView(tv, lp);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void deleteItem(View view) {
        int count = mLayout.getChildCount();
        if (count <= 0) {
            return;
        }

        mLayout.removeViewAt(count - 1);
    }
}
