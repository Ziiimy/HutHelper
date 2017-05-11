package com.gaop.huthelper.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gaop.huthelper.db.DBHelper;
import com.gaop.huthelper.R;
import com.gaop.huthelper.model.network.api.SubscriberOnNextListener;
import com.gaop.huthelper.net.HttpMethods;
import com.gaop.huthelper.net.ProgressSubscriber;
import com.gaop.huthelper.utils.ButtonUtils;
import com.gaop.huthelper.utils.CommUtil;
import com.gaop.huthelper.utils.ToastUtil;
import com.gaop.huthelperdao.User;


/**
 * 反馈Activity
 * Created by 高沛 on 2016/5/26.
 */
public class FeedBackActivity extends BaseActivity {

    private TextView mTvTel, mTvContent;
    private Button mFeedBk;

    @Override
    public void initParms(Bundle parms) {
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_feedback;
    }

    @Override
    public void doBusiness(Context mContext) {
        ImageButton btn = (ImageButton) findViewById(R.id.imgbtn_toolbar_back);
        TextView tv = (TextView) findViewById(R.id.tv_toolbar_title);
        tv.setText("反馈");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvContent = (TextView) findViewById(R.id.et_feedbk_content);
        mTvTel = (TextView) findViewById(R.id.et_feedbk_tel);
        mFeedBk = (Button) findViewById(R.id.btn_feedbk_ok);
        mFeedBk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ButtonUtils.isFastDoubleClick(-1, 2000)) {
                    CommUtil.hideSoftInput(FeedBackActivity.this);
                    feedBack();
                }
            }
        });
    }

    /**
     * 反馈
     */
    private void feedBack() {
        String content = mTvContent.getText().toString();
        String tel = mTvTel.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToastShort("反馈意见不能为空");
            return;
        } else {
            User user = DBHelper.getUserDao().get(0);
            content = user.getStudentKH() + " " + "Android " + content;
            SubscriberOnNextListener getData = new SubscriberOnNextListener<String>() {
                @Override
                public void onNext(String o) {
                    FeedBackActivity.this.finish();
                    ToastUtil.showToastShort("反馈成功！");
                }
            };
            HttpMethods.getInstance().feedBack(
                    new ProgressSubscriber<String>(getData, FeedBackActivity.this),
                    tel, content);
        }
    }


}
