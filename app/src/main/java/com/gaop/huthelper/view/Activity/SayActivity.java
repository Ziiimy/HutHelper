package com.gaop.huthelper.view.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaop.huthelper.Model.HttpResult;
import com.gaop.huthelper.Model.Say;
import com.gaop.huthelper.Model.SayData;
import com.gaop.huthelper.R;
import com.gaop.huthelper.adapter.SayRVAdapter;
import com.gaop.huthelper.jiekou.SubscriberOnNextListener;
import com.gaop.huthelper.net.HttpMethods;
import com.gaop.huthelper.net.ProgressSubscriber;
import com.gaop.huthelper.utils.ToastUtil;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.util.RecyclerViewStateUtils;
import com.github.jdsjlzx.view.LoadingFooter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by gaop on 16-9-9.
 */
public class SayActivity extends BaseActivity {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    private int COUNT = 0;
    private int CURPage = 0;
    private boolean isRefresh;
    List<Say> Saylist = new ArrayList<>();


    @BindView(R.id.rv_saylist)
    LRecyclerView rvSaylist;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    LRecyclerViewAdapter mLRecyclerViewAdapter;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_say;
    }

    @Override
    public void doBusiness(Context mContext) {
        ButterKnife.bind(this);
        tvToolbarTitle.setText("说说");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AddGoodsActivity.class, 201);
            }
        });

        getSayList(1);
        rvSaylist.setLayoutManager(new LinearLayoutManager(SayActivity.this, LinearLayoutManager.VERTICAL, false));
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, new SayRVAdapter(SayActivity.this, Saylist));
        rvSaylist.setAdapter(mLRecyclerViewAdapter);
        rvSaylist.setLScrollListener(new LRecyclerView.LScrollListener() {
            @Override
            public void onRefresh() {
                CURPage = 0;
                Saylist = new ArrayList<>();
                isRefresh = true;
                getSayList(1);
            }

            @Override
            public void onScrollUp() {
                int firstVisibleItem = ((LinearLayoutManager) rvSaylist.getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    return;
                }
                hideViews();
            }

            @Override
            public void onScrollDown() {
                showViews();
            }

            @Override
            public void onBottom() {
                if (CURPage + 1 <= COUNT) {
                    ++CURPage;
                    getSayList(CURPage);
                } else
                    RecyclerViewStateUtils.setFooterViewState(rvSaylist, LoadingFooter.State.TheEnd);
            }

            @Override
            public void onScrolled(int distanceX, int distanceY) {
            }

        });


    }

    public void getSayList(final int pagenum) {
        SubscriberOnNextListener<HttpResult<SayData>> subscriberOnNextListener = new SubscriberOnNextListener<HttpResult<SayData>>() {
            @Override
            public void onNext(HttpResult<SayData> o) {
                if (o.getMsg().equals("ok")) {
                    COUNT = o.getData().getInfo().getPage_max();
                    CURPage = Integer.valueOf(o.getData().getInfo().getPage_cur());
                    Saylist.addAll(o.getData().getPosts());
                    if (pagenum == 1) {
                        mLRecyclerViewAdapter = new LRecyclerViewAdapter(SayActivity.this, new SayRVAdapter(SayActivity.this, Saylist));
                        rvSaylist.setAdapter(mLRecyclerViewAdapter);
                        return;
                    }
                    mLRecyclerViewAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToastShort("获取服务器数据为空");
                }
                rvSaylist.refreshComplete();
                mLRecyclerViewAdapter.notifyDataSetChanged();

            }
        };
        HttpMethods.getInstance().getSayList(
                new ProgressSubscriber<HttpResult<SayData>>(subscriberOnNextListener, SayActivity.this)
                , pagenum);
    }

    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fab.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 333:
                CURPage = 0;
                Saylist = new ArrayList<>();
                isRefresh = true;
                getSayList(1);
                // rvSaylist.scrollToPosition(0);
                // rvSaylist.forceToRefresh();
                Log.e("ds", "ds");
                break;
            default:
                break;

        }
    }

    @OnClick({R.id.imgbtn_toolbar_back, R.id.iv_say_my, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_toolbar_back:
                finish();
                break;
            case R.id.iv_say_my:
                if (fastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", "my");
                    startActivity(MySayListActivity.class, bundle);
                }
                break;
            case R.id.fab:
                startActivityForResult(AddSayActivity.class, 202);
                break;
        }
    }
}
