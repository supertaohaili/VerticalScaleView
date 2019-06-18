# VerticalScaleView介绍
实现竖着的滑动刻度尺，支持自定义初始值、最大值、最小值，支持惯性滑动……


# 效果图

 <img src="https://github.com/supertaohaili/VerticalScaleView/blob/master/20190618.jpg" width="300">


# 使用
``` java

        verticalRullView.setMin(0);   //设置刻度尺最小值
        verticalRullView.setMax(255);   //设置刻度尺最大值
        verticalRullView.setInterval(10);   //设置刻度尺的间距
        verticalRullView.setTextOffset(20); //根据显示的数字自主调节刻度尺数字的左右位置
        verticalRullView.setRuleListener(new VerticalScaleView.ScaleCallback() {
            @Override
            public void onScaleSelected(int length, int value) {
                //获得即时显示的数字
                tVertical.setText(String.valueOf(value));
            }
        });
```



### Known Issues
If you have any questions/queries/Bugs/Hugs please mail @
taohailili@gmail.com