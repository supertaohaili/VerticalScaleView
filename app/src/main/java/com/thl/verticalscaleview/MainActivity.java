package com.thl.verticalscaleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    private VerticalScaleView verticalRullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verticalRullView = (VerticalScaleView) findViewById(R.id.rulerView_vertical);

        verticalRullView.setMin(0);   //设置刻度尺最小值
        verticalRullView.setMax(255);   //设置刻度尺最大值
        verticalRullView.setInterval(10);   //设置刻度尺的间距
        verticalRullView.setTextOffset(20); //根据显示的数字自主调节刻度尺数字的左右位置
        verticalRullView.setRuleListener(new VerticalScaleView.ScaleCallback() {
            @Override
            public void onRulerSelected(int length, int value) {

            }
        });

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalRullView.setValue(170);    //设置刻度尺第一次显示的位置
            }
        });
    }

}
