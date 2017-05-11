package com.gaop.huthelper.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaop.huthelper.R;
import com.gaop.huthelper.db.DBHelper;
import com.gaop.huthelper.model.entity.PageData;
import com.gaop.huthelper.model.entity.Say;
import com.gaop.huthelper.model.entity.SayLikeCache;
import com.gaop.huthelper.model.network.exception.DataException;
import com.gaop.huthelper.model.entity.HttpResult;
import com.gaop.huthelper.presenter.impl.SayPresenterImpl;
import com.gaop.huthelper.utils.ButtonUtils;
import com.gaop.huthelper.utils.CommUtil;
import com.gaop.huthelper.utils.DensityUtils;
import com.gaop.huthelper.utils.ToastUtil;
import com.gaop.huthelper.view.adapter.LoadMoreAdapterItemClickListener;
import com.gaop.huthelper.view.adapter.SayRVAdapter;
import com.gaop.huthelper.view.ui.SayView;
import com.gaop.huthelperdao.User;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.gaop.huthelper.utils.ToastUtil.showToastShort;


/**
 * Created by 高沛 on 16-9-9.
 */
public class SayActivity extends BaseActivity implements SayRVAdapter.AddComment, SayView {

    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
//    @BindView(R.id.rv_saylist)
//    LRecyclerView rvSaylist;
    @BindView(R.id.rl_say_root)
    LinearLayout root;
    @BindView(R.id.imgbtn_toolbar_menu)
    ImageButton menuButton;
    @BindView(R.id.rl_empty)
    RelativeLayout rlEmpty;
    @BindView(R.id.rv_saylist)
    RecyclerView rvSaylist;
    @BindView(R.id.RefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private int COUNT = 1;
    private int CURPage = 1;
    private boolean isRefresh;
    private ArrayList<Say> Saylist = new ArrayList<>();
    private SayRVAdapter adapter;

    private PopupWindow addCommitWindow;
    private User user;

    // private LRecyclerViewAdapter mLRecyclerViewAdapter;

    private SayPresenterImpl sayPresenter;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public int bindLayout() {
        return R.layout.activity_say;
    }

    @Override
    public void doBusiness(final Context mContext) {
        ButterKnife.bind(this);

        sayPresenter = new SayPresenterImpl(this);

        tvToolbarTitle.setText("说说");

        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CURPage = 1;
                adapter.showLoading();
                sayPresenter.request(CURPage, bindUntilEvent(ActivityEvent.STOP));

            }
        });

       // rvSaylist.setEmptyView(rlEmpty);
        rvSaylist.setLayoutManager(new LinearLayoutManager(SayActivity.this, LinearLayoutManager.VERTICAL, false));
        adapter=new SayRVAdapter(mContext,Saylist,bindUntilEvent(ActivityEvent.STOP));
//        SayRVAdapter adapter = new SayRVAdapter(SayActivity.this, Saylist);
        adapter.setAddComment(this);
        adapter.setOnItemClickListener(new LoadMoreAdapterItemClickListener() {
            @Override
            public void itemClickLinstener(int position) {
            }

            @Override
            public void loadMore() {
                sayPresenter.loadMore( ++CURPage, bindUntilEvent(ActivityEvent.STOP));
            }

            @Override
            public void footViewClick() {
                sayPresenter.loadMore( ++CURPage, bindUntilEvent(ActivityEvent.STOP));
            }
        });
        rvSaylist.setAdapter(adapter);
//        mLRecyclerViewAdapter = new LRecyclerViewAdapter(this, adapter);
//        ((SayRVAdapter) mLRecyclerViewAdapter.getInnerAdapter()).setAddComment(this);
//        rvSaylist.setAdapter(mLRecyclerViewAdapter);
//
//        rvSaylist.setLScrollListener(new LRecyclerView.LScrollListener() {
//            @Override
//            public void onRefresh() {
//                CURPage = 0;
//                Saylist.clear();
//                sayPresenter.request(1, bindUntilEvent(ActivityEvent.STOP));
//            }
//
//            @Override
//            public void onScrollUp() {
//            }
//
//            @Override
//            public void onScrollDown() {
//            }
//
//            @Override
//            public void onBottom() {
//                if (!isRefresh) {
//                    if (CURPage + 1 <= COUNT) {
//                        sayPresenter.request(++CURPage, bindUntilEvent(ActivityEvent.STOP));
//                    } else {
//                        RecyclerViewStateUtils.setFooterViewState(rvSaylist, LoadingFooter.State.TheEnd);
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(int distanceX, int distanceY) {
//            }
//        });

        user = DBHelper.getUserDao().get(0);
        sayPresenter.loadLikedSays(mContext, bindUntilEvent(ActivityEvent.STOP));
    }

//    public void getSayList(final int pagenum) {
//        isRefresh = true;
//        SubscriberOnNextListener<HttpResult<PageData<Say>>> subscriberOnNextListener = new SubscriberOnNextListener<HttpResult<PageData<Say>>>() {
//            @Override
//            public void onNext(HttpResult<PageData<Say>> o) {
//                if (o.getMsg().equals("ok")) {
//                    COUNT = o.getData().getInfo().getPage_max();
//                    CURPage = Integer.valueOf(o.getData().getInfo().getPage_cur());
//                    Saylist.addAll(o.getData().getPosts());
//                    mLRecyclerViewAdapter.notifyDataSetChanged();
//                    //Log.e(TAG,"  listSize"+Saylist.size()+" "+mLRecyclerViewAdapter.getInnerAdapter().getItemCount());
//                } else {
//                    showToastShort("获取服务器数据为空");
//                }
//                isRefresh = false;
//                rvSaylist.refreshComplete();
//            }
//
////            @Override
////            public void onError(Throwable e) {
////
////            }
//
//        };
//        HttpMethods.getInstance().getSayList(
//                new ProgressSubscriber<HttpResult<PageData<Say>>>(subscriberOnNextListener, SayActivity.this)
//                , pagenum);
//    }

//    private void getLikeList() {
//        SubscriberOnNextListener<HttpResult<List<String>>> subscriberOnNextListener = new SubscriberOnNextListener<HttpResult<List<String>>>() {
//            @Override
//            public void onNext(HttpResult<List<String>> data) {
//                if ("成功获得点赞数据".equals(data.getMsg())) {
//                    SayLikeCache.setLikeList(data.getData());
//                    getSayList(1);
//                } else if (data.getMsg().equals("令牌错误")) {
//                    showToastShort("账号异地登录，请重新登录");
//                    startActivity(new Intent(SayActivity.this, ImportActivity.class));
//                } else {
//                    showToastShort(data.getMsg());
//                }
//            }
//        };
//        User user = DBHelper.getUserDao().get(0);
//        HttpMethods.getInstance().getLikedSays(new ProgressSubscriber<HttpResult<List<String>>>(subscriberOnNextListener, SayActivity.this), user.getStudentKH(), user.getRember_code());
//    }
//
//    private void hideViews() {
//        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) fab.getLayoutParams();
//        int fabBottomMargin = lp.bottomMargin;
//        fab.animate().translationY(fab.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
//    }
//
//    private void showViews() {
//        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 333:
                CURPage = 0;
                Saylist = new ArrayList<>();
                isRefresh = true;
                sayPresenter.request(1, bindUntilEvent(ActivityEvent.STOP));
                // rvSaylist.scrollToPosition(0);
                // rvSaylist.forceToRefresh();
                break;
            default:
                break;

        }
    }

    private EditText editText;
    private Button button;

//    private void showCommentWindows(final int position, final String id) {
//        if (addCommitWindow == null || button == null || editText == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            final View popupWindowLayout = layoutInflater.inflate(R.layout.popwin_addcommit, null);
//            button = (Button) popupWindowLayout.findViewById(R.id.btn_addcomment_submit);
//            editText = (EditText) popupWindowLayout.findViewById(R.id.et_addcomment_content);
//            addCommitWindow = new PopupWindow(popupWindowLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        }
//        if (editText != null) {
//            CommUtil.toggleSoftInput(SayActivity.this, editText);
//        }
//        if (button != null) {
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (!ButtonUtils.isFastDoubleClick()) {
//                        String comment = editText.getText().toString();
//
//                        if (addCommitWindow.isShowing())
//                            addCommitWindow.dismiss();
//
//                        if (TextUtils.isEmpty(comment)) {
//                            ToastUtil.showToastShort("请填写评论内容！");
//                            return;
//                        }
//                        commitComment(position, id, comment);
//                    }
//                }
//            });
//        }
//        addCommitWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                editText.setText("");
//                CommUtil.toggleSoftInput(SayActivity.this, editText);
//            }
//        });
//        addCommitWindow.setFocusable(true);
//        //设置点击外部可消失
//        addCommitWindow.setOutsideTouchable(true);
//        addCommitWindow.setBackgroundDrawable(new BitmapDrawable());
//
//        addCommitWindow.showAtLocation(root,
//                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//    }

    @Override
    public void addComment(int position, Say say) {
        LinearLayoutManager m = (LinearLayoutManager) rvSaylist.getLayoutManager();
        m.scrollToPositionWithOffset(position , 0);
        showCommentWindow(position, say.getId());
    }

//    private void commitComment(final int position, String id, final String comment) {
//
//        HttpMethods.getInstance().addComment(new Subscriber<HttpResult>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                showToastShort("评论失败！请检查网络后重试！");
//            }
//
//            @Override
//            public void onNext(HttpResult o) {
//                if (o.getMsg().equals("ok")) {
//                    Saylist.get(position).getComments().add(new Say.CommentsBean(user.getUser_id(), user.getUsername(), comment));
//                    if (editText != null)
//                        editText.setText("");
//                    mLRecyclerViewAdapter.notifyDataSetChanged();
//                    showToastShort("评论成功");
//                } else if (o.getMsg().equals("令牌错误")) {
//                    showToastShort("账号异地登录，请重新登录");
//                    startActivity(new Intent(SayActivity.this, ImportActivity.class));
//                } else {
//                    showToastShort(o.getMsg());
//                }
//            }
//        }, user, id, comment);
//    }

    protected PopupWindow menuListWindow;
    protected View popupWindowLayout;

//    private void showMenuWindows(View parent) {
//        if (menuListWindow == null) {
//            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            popupWindowLayout = layoutInflater.inflate(R.layout.popup_list_choose, null);
//
//            TextView tvMime = (TextView) popupWindowLayout.findViewById(R.id.tv_popmenu_mime);
//            TextView tvAdd = (TextView) popupWindowLayout.findViewById(R.id.tv_popmenu_add);
//            tvAdd.setText("发布说说");
//            tvMime.setText("我的发布");
//            tvAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivityForResult(AddSayActivity.class, 201);
//                }
//            });
//            tvMime.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("user_id", "my");
//                    startActivity(MySayListActivity.class, bundle);
//
//                }
//            });
//            menuListWindow = new PopupWindow(popupWindowLayout, DensityUtils.dp2px(SayActivity.this, 170),
//                    DensityUtils.dp2px(SayActivity.this, 110));
//        }
//
//        // 设置背景颜色变暗
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.alpha = 0.4f;
//        getWindow().setAttributes(lp);
//        menuListWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = getWindow().getAttributes();
//                lp.alpha = 1f;
//                getWindow().setAttributes(lp);
//            }
//        });
//        menuListWindow.setFocusable(true);
//        //设置点击外部可消失
//        menuListWindow.setOutsideTouchable(true);
//        menuListWindow.setBackgroundDrawable(new BitmapDrawable());
//        menuListWindow.showAsDropDown(parent, -DensityUtils.dp2px(SayActivity.this, 115), 20);
//    }


    @OnClick({R.id.imgbtn_toolbar_back, R.id.imgbtn_toolbar_menu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgbtn_toolbar_back:
                finish();
                break;
            case R.id.imgbtn_toolbar_menu:
                showMenuWindow(menuButton);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SayLikeCache.clear();
    }


    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void showCommentWindow(final int position, final String id) {
        if (addCommitWindow == null || button == null || editText == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View popupWindowLayout = layoutInflater.inflate(R.layout.popwin_addcommit, null);
            button = (Button) popupWindowLayout.findViewById(R.id.btn_addcomment_submit);
            editText = (EditText) popupWindowLayout.findViewById(R.id.et_addcomment_content);
            addCommitWindow = new PopupWindow(popupWindowLayout, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        if (editText != null) {
            CommUtil.toggleSoftInput(SayActivity.this, editText);
        }
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!ButtonUtils.isFastDoubleClick()) {
                        String comment = editText.getText().toString();

                        if (addCommitWindow.isShowing())
                            addCommitWindow.dismiss();

                        if (TextUtils.isEmpty(comment)) {
                            ToastUtil.showToastShort("请填写评论内容！");
                            return;
                        }
                        //commitComment(position, id, comment);
                        sayPresenter.addCommit(position, id, comment, bindUntilEvent(ActivityEvent.STOP));
                    }
                }
            });
        }
        addCommitWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                editText.setText("");
                CommUtil.toggleSoftInput(SayActivity.this, editText);
            }
        });
        addCommitWindow.setFocusable(true);
        //设置点击外部可消失
        addCommitWindow.setOutsideTouchable(true);
        addCommitWindow.setBackgroundDrawable(new BitmapDrawable());

        addCommitWindow.showAtLocation(root,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void showMenuWindow(View parent) {
        if (menuListWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popupWindowLayout = layoutInflater.inflate(R.layout.popup_list_choose, null);

            TextView tvMime = (TextView) popupWindowLayout.findViewById(R.id.tv_popmenu_mime);
            TextView tvAdd = (TextView) popupWindowLayout.findViewById(R.id.tv_popmenu_add);
            tvAdd.setText("发布说说");
            tvMime.setText("我的发布");
            tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(AddSayActivity.class, 201);
                }
            });
            tvMime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("user_id", "my");
                    startActivity(MySayListActivity.class, bundle);

                }
            });
            menuListWindow = new PopupWindow(popupWindowLayout, DensityUtils.dp2px(SayActivity.this, 170),
                    DensityUtils.dp2px(SayActivity.this, 110));
        }

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
        menuListWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        menuListWindow.setFocusable(true);
        //设置点击外部可消失
        menuListWindow.setOutsideTouchable(true);
        menuListWindow.setBackgroundDrawable(new BitmapDrawable());
        menuListWindow.showAsDropDown(parent, -DensityUtils.dp2px(SayActivity.this, 115), 20);
    }

    @Override
    public void loginAgin() {
        ToastUtil.showToastShort("账号异地登陆，请重新登录");
        startActivity(ImportActivity.class);
        finish();
    }

    @Override
    public void getSaysSuccess(HttpResult<PageData<Say>> o) {
        if (o.getMsg().equals("ok")) {
            COUNT = o.getData().getInfo().getPage_max();
            CURPage = Integer.valueOf(o.getData().getInfo().getPage_cur());
            Saylist = (ArrayList<Say>) o.getData().getPosts();
            adapter.setDataList(Saylist);
            adapter.notifyDataSetChanged();
        } else {
            showToastShort("获取服务器数据为空");
        }
    }

    @Override
    public void getSaysFail(Throwable e) {
        ToastUtil.showToastShort(e.getMessage());
        if (CURPage == 1)
            refreshLayout.setVisibility(View.GONE);
    }

    @Override
    public void commitSuccess(int position, String comment, HttpResult result) {
        if (result.getMsg().equals("ok")) {
            Saylist.get(position).getComments().add(new Say.CommentsBean(user.getUser_id(), user.getUsername(), comment));
            if (editText != null)
                editText.setText("");
            adapter.notifyDataSetChanged();
            showToastShort("评论成功");
        } else {
            showToastShort(result.getMsg());
        }
    }

    @Override
    public void commitFail(Throwable e) {
        if (e instanceof DataException) {
            showToastShort("评论失败:" + e.getMessage());
            return;
        }
        showToastShort("评论失败！请检查网络后重试！");
    }

    @Override
    public void loadMoreSuccess(HttpResult<PageData<Say>> o) {
        if (o.getMsg().equals("ok")) {
            COUNT = o.getData().getInfo().getPage_max();
            CURPage = Integer.valueOf(o.getData().getInfo().getPage_cur());
            Saylist.addAll(o.getData().getPosts());
            adapter.setDataList(Saylist);
            adapter.notifyDataSetChanged();
        } else {
            showToastShort("获取服务器数据为空");
        }
    }

    @Override
    public void loadMoreFail(Throwable e) {
        adapter.setCanLoading(true);
        adapter.setFootClickable(true);
        adapter.footShowClickText();
        CURPage--;
        ToastUtil.showToastShort(e.getMessage());
    }
}
