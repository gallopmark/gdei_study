package com.haoyu.app.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoyu.app.basehelper.BaseArrayRecyclerAdapter;
import com.haoyu.app.entity.CourseMobileEntity;
import com.haoyu.app.entity.MCourseRegister;
import com.haoyu.app.gdei.student.R;
import com.haoyu.app.imageloader.GlideImgManager;
import com.haoyu.app.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2017/5/23 on 16:52
 * 描述: 选课列表适配器
 * 作者:马飞奔 Administrator
 */
public class CourseRegistStateAdapter extends BaseArrayRecyclerAdapter<MCourseRegister> {

    private Context context;
    private int imageWidth;
    private int imageHeight;
    private boolean isEdit;
    private List<MCourseRegister> mSelects = new ArrayList<>();
    private OnItemSelectListener onItemSelectListener;

    public CourseRegistStateAdapter(Context context, List<MCourseRegister> mDatas) {
        super(mDatas);
        this.context = context;
        imageWidth = ScreenUtils.getScreenWidth(context) / 5 * 2 - 20;
        imageHeight = imageWidth / 3 * 2;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }

    public void selecetAll() {
        mSelects.clear();
        mSelects.addAll(mDatas);
        notifyDataSetChanged();
    }

    public void cancelAll() {
        mSelects.clear();
        notifyDataSetChanged();
    }

    @Override
    public int bindView(int viewtype) {
        return R.layout.course_regist_state_item;
    }

    @Override
    public void onBindHoder(RecyclerHolder holder, final MCourseRegister entity, final int position) {
        final CheckBox checkBox = holder.obtainView(R.id.checkBox);
        ImageView iv_img = holder.obtainView(R.id.iv_img);
        TextView tv_title = holder.obtainView(R.id.tv_title);
        TextView tv_period = holder.obtainView(R.id.tv_period);
        TextView tv_enroll = holder.obtainView(R.id.tv_enroll);
        TextView tv_type = holder.obtainView(R.id.tv_type);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, imageHeight);
        iv_img.setLayoutParams(params);
        if (isEdit) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }
        CourseMobileEntity course = entity.getmCourse();
        if (course != null && course.getImage() != null && course.getImage().length() > 0) {
            GlideImgManager.loadImage(context, course.getImage(), R.drawable.app_default, R.drawable.app_default, iv_img);
        } else {
            iv_img.setImageResource(R.drawable.app_default);
        }
        if (course != null && course.getTitle() != null) {
            tv_title.setText(course.getTitle());
        } else {
            tv_title.setText("");
        }
        if (course != null && course.getType() != null) {
            tv_type.setVisibility(View.VISIBLE);
            tv_type.setText(course.getType());
        } else {
            tv_type.setVisibility(View.GONE);
        }
        if (course != null) {
            tv_period.setVisibility(View.VISIBLE);
            tv_period.setText(String.valueOf(course.getStudyHours()) + "学时");
        } else {
            tv_period.setVisibility(View.GONE);
        }
        if (course != null) {
            tv_enroll.setText(course.getRegisterNum() + "人报读");
        } else {
            tv_enroll.setText("0人报读");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEdit) {
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                    } else {
                        checkBox.setChecked(true);
                    }
                }
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    if (!mSelects.contains(entity)) {
                        mSelects.add(entity);
                    }
                } else {
                    mSelects.remove(entity);
                }
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(mSelects);
                }
            }
        });
        checkBox.setChecked(mSelects.contains(entity));
    }

    public interface OnItemSelectListener {
        void onItemSelect(List<MCourseRegister> mSelects);
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public List<MCourseRegister> getmSelects() {
        return mSelects;
    }
}
