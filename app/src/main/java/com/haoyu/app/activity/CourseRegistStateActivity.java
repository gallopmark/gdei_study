package com.haoyu.app.activity;

import android.app.AlertDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haoyu.app.adapter.CourseRegistStateAdapter;
import com.haoyu.app.base.BaseActivity;
import com.haoyu.app.base.BaseResponseResult;
import com.haoyu.app.basehelper.BaseRecyclerAdapter;
import com.haoyu.app.dialog.MaterialDialog;
import com.haoyu.app.entity.CourseRegistStateResultBase;
import com.haoyu.app.entity.MCourseRegister;
import com.haoyu.app.entity.MTrainRegister;
import com.haoyu.app.gdei.student.R;
import com.haoyu.app.rxBus.MessageEvent;
import com.haoyu.app.rxBus.RxBus;
import com.haoyu.app.utils.Action;
import com.haoyu.app.utils.Constants;
import com.haoyu.app.utils.OkHttpClientManager;
import com.haoyu.app.view.AppToolBar;
import com.haoyu.app.view.LoadFailView;
import com.haoyu.app.view.LoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;

/**
 * 创建日期：2017/5/19 on 15:46
 * 描述:课程报读情况
 * 作者:马飞奔 Administrator
 */
public class CourseRegistStateActivity extends BaseActivity implements View.OnClickListener {
    private CourseRegistStateActivity context;
    @BindView(R.id.toolBar)
    AppToolBar toolBar;
    @BindView(R.id.rl_edit)
    RelativeLayout rl_edit;
    @BindView(R.id.tv_select)
    TextView tv_select;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;
    @BindView(R.id.loadingView)
    LoadingView loadingView;
    @BindView(R.id.loadFailView)
    LoadFailView loadFailView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tv_empty;
    @BindView(R.id.fl_bottom)
    FrameLayout fl_bottom;
    @BindView(R.id.ll_tips)
    LinearLayout ll_tips;
    @BindView(R.id.tv_tips)
    TextView tv_tips;
    @BindView(R.id.bt_submit)
    Button bt_submit;
    @BindView(R.id.layout_opreate)
    LinearLayout layout_opreate;
    @BindView(R.id.tv_selected)
    TextView tv_selected;
    @BindView(R.id.tv_unregister)
    TextView tv_unregister;  //取消选课
    private String trainId;
    private boolean isNoLimit;
    private CourseRegistStateAdapter adapter;
    private List<MCourseRegister> mDatas = new ArrayList<>();
    private String trainRegisterId;   //培训报名id
    private String text_edit = "编辑", text_selectAll = "全选", text_cancelAll = "全不选", text_selected = "已选", text_cancel = "取消";
    private int selectType = 1;

    @Override
    public int setLayoutResID() {
        return R.layout.activity_course_regist_state;
    }

    @Override
    public void initView() {
        context = this;
        setToolBar();
        tv_select.setText(text_selectAll);
        tv_cancel.setText(text_cancel);
        tv_selected.setText(text_selected);
        trainId = getIntent().getStringExtra("trainId");
        isNoLimit = getIntent().getBooleanExtra("isNoLimit", false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CourseRegistStateAdapter(context, mDatas);
        recyclerView.setAdapter(adapter);
    }

    private void setToolBar() {
        toolBar.setTitle_text("已选课程");
        toolBar.setRight_button_text(text_edit);
        toolBar.setShow_right_button(false);
        toolBar.setOnTitleClickListener(new AppToolBar.TitleOnClickListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onRightClick(View view) {
                setEdit();
            }
        });
    }

    private void setEdit() {
        toolBar.setShow_left_button(false);
        toolBar.setShow_right_button(false);
        adapter.setEdit(true);
        rl_edit.setVisibility(View.VISIBLE);
        ll_tips.setVisibility(View.GONE);
        layout_opreate.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        getCourseList();
    }

    /*获取选课情况*/
    private void getRegistState() {
        String url = Constants.OUTRT_NET + "/m/course_center/my_register_state?trainId=" + trainId;
        addSubscription(OkHttpClientManager.getAsyn(context, url, new OkHttpClientManager.ResultCallback<CourseRegistStateResultBase>() {

            @Override
            public void onError(Request request, Exception e) {
                onNetWorkError(context);
            }

            @Override
            public void onResponse(CourseRegistStateResultBase response) {
                if (response != null && response.getResponseData() != null) {
                    fl_bottom.setVisibility(View.VISIBLE);
                    updateUI(response.getResponseData());
                }
            }
        }));
    }

    /*获取已选课列表*/
    private void getCourseList() {
        String url = Constants.OUTRT_NET + "/m/course_register?relation.id=" + trainId + "&user.id=" + getUserId();
        addSubscription(OkHttpClientManager.getAsyn(context, url, new OkHttpClientManager.ResultCallback<BaseResponseResult<List<MCourseRegister>>>() {
            @Override
            public void onBefore(Request request) {
                loadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Request request, Exception e) {
                loadingView.setVisibility(View.GONE);
                loadFailView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(BaseResponseResult<List<MCourseRegister>> response) {
                loadingView.setVisibility(View.GONE);
                if (response != null && response.getResponseData() != null && response.getResponseData().size() > 0) {
                    updateUI(response.getResponseData());
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tv_empty.setVisibility(View.VISIBLE);
                }
                getRegistState();
                getTrainState();
            }
        }));
    }

    /*获取培训报名情况*/
    private void getTrainState() {
        final String url = Constants.OUTRT_NET + "/m/train_register/get_by_trainId?trainId=" + trainId;
        addSubscription(OkHttpClientManager.getAsyn(context, url, new OkHttpClientManager.ResultCallback<BaseResponseResult<MTrainRegister>>() {
            @Override
            public void onError(Request request, Exception e) {
                onNetWorkError(context);
            }

            @Override
            public void onResponse(BaseResponseResult<MTrainRegister> response) {
                if (response != null && response.getResponseData() != null) {
                    updateUI(response.getResponseData());
                }
            }
        }));
    }

    private void updateUI(CourseRegistStateResultBase.CourseRegistStateData mData) {
        if (mDatas.size() > 0) {
            int registedCourseNum = mData.getRegistedCourseNum();
            int totalHours = mData.getRequireStudyHours();
            int registHours = mData.getRegistedStudyHours();
            String text;
            SpannableString ss;
            if (isNoLimit) {
                text = "已选" + registedCourseNum + "门课共" + registHours + "学时";
                ss = new SpannableString(text);
                int start = text.indexOf("选") + 1;
                int end = text.indexOf("门");
                int color = ContextCompat.getColor(context, R.color.darkorange);
                ss.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = text.indexOf("共") + 1;
                end = text.lastIndexOf("学");
                ss.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                text = "要求" + totalHours + "学时" + "，已选" + registedCourseNum + "门课共" + registHours + "学时";
                ss = new SpannableString(text);
                int start = text.indexOf("求") + 1;
                int end = text.indexOf("，") - 2;
                int color = ContextCompat.getColor(context, R.color.darkorange);
                ss.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = text.indexOf("选") + 1;
                end = text.indexOf("门");
                ss.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = text.indexOf("共") + 1;
                end = text.lastIndexOf("学");
                ss.setSpan(new ForegroundColorSpan(color), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            tv_tips.setText(ss);
        } else {
            tv_tips.setText("暂时没有选课，请前往选课中心选课！");
            bt_submit.setVisibility(View.GONE);
        }
    }

    private void updateUI(List<MCourseRegister> list) {
        toolBar.setShow_right_button(true);
        recyclerView.setVisibility(View.VISIBLE);
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void updateUI(MTrainRegister mTrainRegister) {
        trainRegisterId = mTrainRegister.getId();
        if (mDatas.size() > 0) {
            bt_submit.setVisibility(View.VISIBLE);
        }
        if (mTrainRegister.getChooseCourseState() != null && mTrainRegister.getChooseCourseState().equals("submited")) {
            if (isNoLimit) {
                bt_submit.setText("提交选课");
                bt_submit.setBackgroundColor(ContextCompat.getColor(context, R.color.defaultColor));
                bt_submit.setEnabled(true);
            } else {
                bt_submit.setText("选课已提交");
                bt_submit.setEnabled(false);
                bt_submit.setBackgroundColor(ContextCompat.getColor(context, R.color.gray));
            }
        } else {
            bt_submit.setText("提交选课");
            bt_submit.setEnabled(true);
            bt_submit.setBackgroundColor(ContextCompat.getColor(context, R.color.defaultColor));
            bt_submit.setEnabled(true);
        }
    }

    @Override
    public void setListener() {
        tv_select.setOnClickListener(context);
        tv_cancel.setOnClickListener(context);
        tv_unregister.setOnClickListener(context);
        bt_submit.setOnClickListener(context);
        loadFailView.setOnRetryListener(new LoadFailView.OnRetryListener() {
            @Override
            public void onRetry(View v) {
                getCourseList();
            }
        });
        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(BaseRecyclerAdapter adapter, BaseRecyclerAdapter.RecyclerHolder holder, View view, int position) {
                setEdit();
            }
        });
        adapter.setOnItemSelectListener(new CourseRegistStateAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(List<MCourseRegister> mSelects) {
                if (mSelects.size() == mDatas.size()) {
                    tv_select.setText(text_cancelAll);
                    selectType = 2;
                } else {
                    tv_select.setText(text_selectAll);
                    selectType = 1;
                }
                if (mSelects.size() > 0) {
                    tv_selected.setText(text_selected + "(" + mSelects.size() + ")");
                } else {
                    tv_selected.setText(text_selected);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select:
                if (selectType == 1) {
                    adapter.selecetAll();
                    tv_select.setText(text_cancelAll);
                    selectType = 2;
                } else {
                    adapter.cancelAll();
                    tv_select.setText(text_selectAll);
                    selectType = 1;
                }
                break;
            case R.id.tv_cancel:
                cancelEdit();
                break;
            case R.id.tv_unregister:
                if (adapter.getmSelects().size() > 0) {
                    MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle("提示");
                    dialog.setMessage("您确定取消选课吗？");
                    dialog.setNegativeButton("确定", new MaterialDialog.ButtonClickListener() {
                        @Override
                        public void onClick(View v, AlertDialog dialog) {
                            List<MCourseRegister> mSelects = adapter.getmSelects();
                            unRegistCourses(mSelects);
                        }
                    });
                    dialog.setNegativeButton("取消", null);
                    dialog.show();
                } else {
                    showMaterialDialog("提示", "请先勾选课程");
                }
                break;
            case R.id.bt_submit:
                showSubmitDialog();
                break;
        }
    }

    private void cancelEdit() {
        toolBar.setShow_left_button(true);
        if (mDatas.size() == 0) {
            toolBar.setShow_right_button(false);
        } else {
            toolBar.setShow_right_button(true);
        }
        adapter.cancelAll();
        adapter.setEdit(false);
        rl_edit.setVisibility(View.GONE);
        ll_tips.setVisibility(View.VISIBLE);
        layout_opreate.setVisibility(View.GONE);
    }

    private void unRegistCourses(List<MCourseRegister> mSelects) {
        showTipDialog();
        addSubscription(Flowable.just(mSelects).map(new Function<List<MCourseRegister>, List<MCourseRegister>>() {
            @Override
            public List<MCourseRegister> apply(List<MCourseRegister> mSelects) {
                return unRegistCourse(mSelects);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<MCourseRegister>>() {
            @Override
            public void accept(List<MCourseRegister> list) throws Exception {
                hideTipDialog();
                MessageEvent ev = new MessageEvent();
                ev.action = Action.CHOOSE_COURSE_STATE;
                RxBus.getDefault().post(ev);
                mDatas.removeAll(list);
                adapter.notifyDataSetChanged();
                if (mDatas.size() == 0) {
                    cancelEdit();
                    recyclerView.setVisibility(View.GONE);
                    tv_empty.setVisibility(View.VISIBLE);
                }
                getRegistState();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                hideTipDialog();
                onNetWorkError(context);
            }
        }));
    }

    /*取消选课*/
    private List<MCourseRegister> unRegistCourse(List<MCourseRegister> mSelects) {
        List<MCourseRegister> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("_method", "delete");
        for (int i = 0; i < mSelects.size(); i++) {
            try {
                MCourseRegister register = mSelects.get(i);
                String registerId = register.getId();
                String url = Constants.OUTRT_NET + "/unique_uid_" + getUserId() + "/m/course_register/delete/" + registerId;
                String json = OkHttpClientManager.postAsString(context, url, map);
                BaseResponseResult result = new Gson().fromJson(json, BaseResponseResult.class);
                if (result != null && result.getResponseCode() != null && result.getResponseCode().equals("00")) {
                    list.add(register);
                }
            } catch (Exception e) {
                continue;
            }
        }
        return list;
    }

    private void showSubmitDialog() {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle("温馨提示");
        dialog.setMessage("提交选课信息后，不允许改选课程或取消选课，请情重提交！");
        dialog.setPositiveTextColor(ContextCompat.getColor(context, R.color.defaultColor));
        dialog.setNegativeTextColor(ContextCompat.getColor(context, R.color.gray));
        dialog.setPositiveButton("确定提交", new MaterialDialog.ButtonClickListener() {
            @Override
            public void onClick(View v, AlertDialog dialog) {
                submitCourse();
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    /*提交选课*/
    private void submitCourse() {
        String url = Constants.OUTRT_NET + "/m/course_center/submit_choose_course";
        Map<String, String> map = new HashMap<>();
        map.put("id", trainRegisterId);
        map.put("train.id", trainId);
        addSubscription(OkHttpClientManager.postAsyn(context, url, new OkHttpClientManager.ResultCallback<BaseResponseResult>() {
            @Override
            public void onBefore(Request request) {
                showTipDialog();
            }

            @Override
            public void onError(Request request, Exception e) {
                hideTipDialog();
                onNetWorkError(context);
            }

            @Override
            public void onResponse(BaseResponseResult response) {
                hideTipDialog();
                if (response != null && response.getResponseCode() != null && response.getResponseCode().equals("00")) {
                    MessageEvent ev = new MessageEvent();
                    ev.action = Action.CHOOSE_COURSE_STATE;
                    RxBus.getDefault().post(ev);
                    finish();
                } else if (response != null && response.getResponseCode() != null && response.getResponseCode().equals("03")) {
                    showMaterialDialog("温馨提示", "提交失败，选课学时未达标，请查看选课要求！");
                } else if (response != null && response.getResponseCode() != null && response.getResponseCode().equals("01")) {
                    showMaterialDialog("温馨提示", "您的选课已提交，请不要重复提交！");
                } else {
                    toast(context, "提交失败，请稍后再试");
                }
            }
        }, map));
    }

}
