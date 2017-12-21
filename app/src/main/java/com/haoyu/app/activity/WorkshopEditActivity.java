package com.haoyu.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoyu.app.adapter.DictEntryAdapter;
import com.haoyu.app.base.BaseActivity;
import com.haoyu.app.base.BaseResponseResult;
import com.haoyu.app.dialog.MaterialDialog;
import com.haoyu.app.entity.DictEntryMobileEntity;
import com.haoyu.app.entity.DictEntryResult;
import com.haoyu.app.entity.FileUploadResult;
import com.haoyu.app.entity.MFileInfo;
import com.haoyu.app.entity.WorkShopMobileEntity;
import com.haoyu.app.filePicker.LFilePicker;
import com.haoyu.app.gdei.student.R;
import com.haoyu.app.pickerlib.MediaOption;
import com.haoyu.app.pickerlib.MediaPicker;
import com.haoyu.app.utils.Common;
import com.haoyu.app.utils.Constants;
import com.haoyu.app.utils.OkHttpClientManager;
import com.haoyu.app.utils.ScreenUtils;
import com.haoyu.app.view.AppToolBar;
import com.haoyu.app.view.RoundRectProgressBar;
import com.haoyu.app.view.ScrollEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;
import okhttp3.Request;

/**
 * 创建日期：2017/5/24 on 15:22
 * 描述:创建个人工作坊
 * 作者:马飞奔 Administrator
 */
public class WorkshopEditActivity extends BaseActivity implements View.OnClickListener {
    private WorkshopEditActivity context;
    @BindView(R.id.toolBar)
    AppToolBar toolBar;
    /******************************/
    @BindView(R.id.fl_imageLayout)
    FrameLayout fl_imageLayout;   //添加封面布局
    @BindView(R.id.ll_addImage)
    LinearLayout ll_addImage;
    @BindView(R.id.fl_image)
    FrameLayout fl_image;
    @BindView(R.id.iv_image)
    ImageView iv_image;     //显示封面
    @BindView(R.id.ll_imageProgress)
    LinearLayout ll_imageProgress;
    @BindView(R.id.tv_imageName)
    TextView tv_imageName;
    @BindView(R.id.imageProgressBar)
    RoundRectProgressBar imageProgressBar;
    @BindView(R.id.tv_imageProgress)
    TextView tv_imageProgress;
    @BindView(R.id.tv_imageError)
    TextView tv_imageError;
    @BindView(R.id.iv_deleteImage)
    ImageView iv_deleteImage;
    /******************************/
    @BindView(R.id.et_name)
    EditText et_name;   //工作坊名称
    @BindView(R.id.et_content)
    ScrollEditText et_content;  //工作坊内容
    @BindView(R.id.tv_stage)
    TextView tv_stage;   //选择学段学科
    @BindView(R.id.tv_subject)
    TextView tv_subject;
    /******************************/
    @BindView(R.id.ll_addFile)
    LinearLayout ll_addFile;   //上传研修方案
    @BindView(R.id.fl_file)
    FrameLayout fl_file;
    @BindView(R.id.ll_fileProgress)
    LinearLayout ll_fileProgress;
    @BindView(R.id.iv_fileType)
    ImageView iv_fileType;
    @BindView(R.id.tv_fileName)
    TextView tv_fileName;
    @BindView(R.id.fileProgressBar)
    RoundRectProgressBar fileProgressBar;
    @BindView(R.id.tv_fileProgress)
    TextView tv_fileProgress;
    @BindView(R.id.tv_fileError)
    TextView tv_fileError;
    @BindView(R.id.iv_deleteFile)
    ImageView iv_deleteFile;
    private String trainId;
    private List<DictEntryMobileEntity> stageList, subjectList;//学段集合,学科集合
    private String stageId, subjectId;
    private int stageIndex = -1, subjectIndex = -1;
    private Disposable mImagePosable, mFilePosable;
    private boolean isUploadImage, isUploadFile;
    private MFileInfo mImageInfo, mFileInfo;
    private final int CODE_IMAGE = 1, CODE_FILE = 2;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_IMAGE:
                    Bundle bundle = msg.getData();
                    long totalBytes = bundle.getLong("totalBytes");
                    long remainingBytes = bundle.getLong("remainingBytes");
                    imageProgressBar.setMax((int) totalBytes);
                    imageProgressBar.setProgress((int) (totalBytes - remainingBytes));
                    long progress = (totalBytes - remainingBytes) * 100 / totalBytes;
                    tv_imageProgress.setText("上传中" + "\u2000" + progress + "%");
                    break;
                case CODE_FILE:
                    Bundle b = msg.getData();
                    long max = b.getLong("totalBytes");
                    long remain = b.getLong("remainingBytes");
                    fileProgressBar.setMax((int) max);
                    fileProgressBar.setProgress((int) (max - remain));
                    long seek = (max - remain) * 100 / max;
                    tv_fileProgress.setText("上传中" + "\u2000" + seek + "%");
                    break;
            }
        }
    };

    @Override
    public int setLayoutResID() {
        return R.layout.activity_ws_edit;
    }

    @Override
    public void initView() {
        context = this;
        trainId = getIntent().getStringExtra("trainId");
        int imageHeight = ScreenUtils.getScreenHeight(context) / 4;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, imageHeight);
        fl_imageLayout.setLayoutParams(params);
    }

    @Override
    public void setListener() {
        toolBar.setOnTitleClickListener(new AppToolBar.TitleOnClickListener() {
            @Override
            public void onLeftClick(View view) {
                finish();
            }

            @Override
            public void onRightClick(View view) {
                if (checkOut()) {
                    commit();
                }
            }
        });
        ll_addImage.setOnClickListener(context);
        iv_deleteImage.setOnClickListener(context);
        tv_stage.setOnClickListener(context);
        tv_subject.setOnClickListener(context);
        ll_addFile.setOnClickListener(context);
        iv_deleteFile.setOnClickListener(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_addImage:
                pickerPicture();
                break;
            case R.id.iv_deleteImage:
                MaterialDialog videoDialog = new MaterialDialog(context);
                videoDialog.setTitle("提示");
                videoDialog.setMessage("确定删除此封面吗？");
                videoDialog.setPositiveButton("确定", new MaterialDialog.ButtonClickListener() {
                    @Override
                    public void onClick(View v, AlertDialog dialog) {
                        deleteImage();
                    }
                });
                videoDialog.setNegativeButton("取消", null);
                videoDialog.show();
                break;
            case R.id.tv_stage:
                if (stageList == null) {
                    initStage();
                } else {
                    showPopWindow(1, tv_stage, stageList);
                }
                break;
            case R.id.tv_subject:
                if (subjectList == null) {
                    initSubject();
                } else {
                    showPopWindow(2, tv_subject, subjectList);
                }
                break;
            case R.id.ll_addFile:
                openFilePicker();
                break;
            case R.id.iv_deleteFile:
                MaterialDialog fileDialog = new MaterialDialog(context);
                fileDialog.setTitle("提示");
                fileDialog.setMessage("确定删除此附件吗？");
                fileDialog.setPositiveButton("确定", new MaterialDialog.ButtonClickListener() {
                    @Override
                    public void onClick(View v, AlertDialog dialog) {
                        deleteFile();
                    }
                });
                fileDialog.setNegativeButton("取消", null);
                fileDialog.show();
                break;
        }
    }

    private void pickerPicture() {
        MediaOption option = new MediaOption.Builder()
                .setSelectType(MediaOption.TYPE_IMAGE)
                .setShowCamera(true)
                .build();
        MediaPicker.getInstance().init(option).selectMedia(context, new MediaPicker.onSelectMediaCallBack() {
            @Override
            public void onSelected(String path) {
                setImageFile(path);
            }
        });
    }

    private void setImageFile(String path) {
        File image = new File(path);
        ll_addImage.setVisibility(View.GONE);
        fl_image.setVisibility(View.VISIBLE);
        Glide.with(context).load(path).centerCrop().into(iv_image);
        tv_imageName.setText(image.getName());
        uploadImage(image);
    }

    private void uploadImage(final File image) {
        String url = Constants.OUTRT_NET + "/m/file/uploadFileInfoRemote";
        mImagePosable = OkHttpClientManager.postAsyn(context, url, new OkHttpClientManager.ResultCallback<FileUploadResult>() {
            @Override
            public void onBefore(Request request) {
                isUploadImage = true;
                if (ll_imageProgress.getVisibility() != View.VISIBLE) {
                    ll_imageProgress.setVisibility(View.VISIBLE);
                }
                if (tv_imageError.getVisibility() != View.GONE) {
                    tv_imageError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                setImageError(image);
            }

            @Override
            public void onResponse(FileUploadResult response) {
                isUploadImage = false;
                handler.removeMessages(CODE_IMAGE);
                if (response != null && response.getResponseData() != null) {
                    mImageInfo = response.getResponseData();
                    tv_imageProgress.setText("已上传");
                }
            }
        }, image, image.getName(), new OkHttpClientManager.ProgressListener() {
            @Override
            public void onProgress(final long totalBytes, final long remainingBytes, boolean done, File file) {
                Message msg = handler.obtainMessage(CODE_IMAGE);
                Bundle bundle = new Bundle();
                bundle.putLong("totalBytes", totalBytes);
                bundle.putLong("remainingBytes", remainingBytes);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
    }

    private void setImageError(final File image) {
        isUploadImage = false;
        ll_imageProgress.setVisibility(View.GONE);
        tv_imageError.setVisibility(View.VISIBLE);
        if (mImagePosable != null && !mImagePosable.isDisposed()) {
            mImagePosable.dispose();
        }
        handler.removeMessages(CODE_IMAGE);
        tv_imageError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(image);
            }
        });
    }

    private void deleteImage() {
        isUploadImage = false;
        mImageInfo = null;
        iv_image.setImageResource(0);
        fl_image.setVisibility(View.GONE);
        ll_addImage.setVisibility(View.VISIBLE);
        if (mImagePosable != null) {
            mImagePosable.dispose();
        }
        handler.removeMessages(CODE_IMAGE);
    }

    /**
     * 访问学段条目
     */
    private void initStage() {
        String url = Constants.OUTRT_NET + "/m/textBook?textBookTypeCode=STAGE";
        addSubscription(OkHttpClientManager.getAsyn(context, url, new OkHttpClientManager.ResultCallback<DictEntryResult>() {

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
            public void onResponse(DictEntryResult response) {
                hideTipDialog();
                if (response != null && response.getResponseData() != null) {
                    stageList = new ArrayList<>();
                    DictEntryMobileEntity entity = new DictEntryMobileEntity();
                    entity.setTextBookName("所有学段");
                    stageList.add(entity);
                    stageList.addAll(response.getResponseData());
                    showPopWindow(1, tv_stage, stageList);
                }
            }
        }));
    }

    /*获取学科 */
    private void initSubject() {
        String url = Constants.OUTRT_NET + "/m/textBook?textBookTypeCode=SUBJECT";
        addSubscription(OkHttpClientManager.getAsyn(context, url, new OkHttpClientManager.ResultCallback<DictEntryResult>() {
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
            public void onResponse(DictEntryResult response) {
                hideTipDialog();
                if (response != null && response.getResponseData() != null) {
                    subjectList = new ArrayList<>();
                    DictEntryMobileEntity entity = new DictEntryMobileEntity();
                    entity.setTextBookName("所有学科");
                    subjectList.add(entity);
                    subjectList.addAll(response.getResponseData());
                    showPopWindow(2, tv_subject, subjectList);
                }
            }
        }));
    }

    private void showPopWindow(final int type, final TextView tv, final List<DictEntryMobileEntity> mDatas) {
        Drawable shouqi = ContextCompat.getDrawable(context, R.drawable.course_dictionary_shouqi);
        shouqi.setBounds(0, 0, shouqi.getMinimumWidth(), shouqi.getMinimumHeight());
        final Drawable zhankai = ContextCompat.getDrawable(context, R.drawable.course_dictionary_xiala);
        zhankai.setBounds(0, 0, zhankai.getMinimumWidth(), zhankai.getMinimumHeight());
        tv.setCompoundDrawables(null, null, shouqi, null);
        ListView listView = new ListView(context);
        listView.setDivider(null);
        listView.setBackgroundResource(R.drawable.dictionary_background);
        final PopupWindow popupWindow = new PopupWindow(listView, tv.getWidth(), LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        DictEntryAdapter adapter;
        if (type == 1) {
            adapter = new DictEntryAdapter(context, mDatas, stageIndex);
        } else {
            adapter = new DictEntryAdapter(context, mDatas, subjectIndex);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type == 1) {
                    stageIndex = position;
                    stageId = mDatas.get(position).getTextBookValue();
                } else {
                    subjectIndex = position;
                    subjectId = mDatas.get(position).getTextBookValue();
                }
                popupWindow.dismiss();
                tv.setText(mDatas.get(position).getTextBookName());
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv.setCompoundDrawables(null, null, zhankai, null);
            }
        });
        popupWindow.showAsDropDown(tv);
    }

    private void openFilePicker() {
        //文件格式必须为doc,docx,wps,pdf
        new LFilePicker()
                .withActivity(context)
                .withRequestCode(1)
                .withMutilyMode(false)
                .withFileFilter(new String[]{"doc", "docx", "wps", "pdf", "pdf"})
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            List<String> list = data.getStringArrayListExtra("paths");
            if (list != null && list.size() > 0 && new File(list.get(0)).exists()) {
                String filePath = list.get(0);
                setFileInfo(filePath);
            } else {
                showMaterialDialog("提示", "选择的文件不存在");
            }
        }
    }

    private void setFileInfo(String filePath) {
        if (new File(filePath).exists()) {
            ll_addFile.setVisibility(View.GONE);
            fl_file.setVisibility(View.VISIBLE);
            Common.setFileType(filePath, iv_fileType);
            tv_fileName.setText(Common.getFileName(filePath));
            uploadFile(new File(filePath));
        } else {
            toast(context, "选择的文件不存在");
        }
    }

    private void uploadFile(final File mFile) {
        String url = Constants.OUTRT_NET + "/m/file/uploadTemp";
        mFilePosable = OkHttpClientManager.postAsyn(context, url, new OkHttpClientManager.ResultCallback<FileUploadResult>() {
            @Override
            public void onBefore(Request request) {
                isUploadFile = true;
                if (ll_fileProgress.getVisibility() != View.VISIBLE) {
                    ll_fileProgress.setVisibility(View.VISIBLE);
                }
                if (tv_fileError.getVisibility() != View.GONE) {
                    tv_fileError.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Request request, Exception e) {
                onNetWorkError(context);
                setFileError(mFile);
            }

            @Override
            public void onResponse(FileUploadResult response) {
                isUploadFile = false;
                handler.removeMessages(CODE_FILE);
                if (response != null && response.getResponseData() != null) {
                    mFileInfo = response.getResponseData();
                    tv_fileProgress.setText("已上传");
                }
            }
        }, mFile, mFile.getName(), new OkHttpClientManager.ProgressListener() {
            @Override
            public void onProgress(final long totalBytes, final long remainingBytes, boolean done, File file) {
                Message message = new Message();
                message.what = CODE_FILE;
                Bundle bundle = new Bundle();
                bundle.putLong("totalBytes", totalBytes);
                bundle.putLong("remainingBytes", remainingBytes);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    private void setFileError(final File mFile) {
        isUploadFile = false;
        ll_fileProgress.setVisibility(View.GONE);
        tv_fileError.setVisibility(View.VISIBLE);
        if (mFilePosable != null && !mFilePosable.isDisposed()) {
            mFilePosable.dispose();
        }
        handler.removeMessages(CODE_FILE);
        tv_fileError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(mFile);
            }
        });
    }

    private void deleteFile() {
        isUploadFile = false;
        mFileInfo = null;
        fl_file.setVisibility(View.GONE);
        ll_addFile.setVisibility(View.VISIBLE);
        if (mFilePosable != null) {
            mFilePosable.dispose();
        }
        handler.removeMessages(CODE_FILE);
    }

    private boolean checkOut() {
        String name = et_name.getText().toString().trim();
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            showMaterialDialog("提示", "请输入工作坊名称");
            return false;
        } else if (TextUtils.isEmpty(content)) {
            showMaterialDialog("提示", "请输入工作坊简介");
            return false;
        } else if (stageId == null) {
            showMaterialDialog("提示", "请选择学段");
            return false;
        } else if (subjectId == null) {
            showMaterialDialog("提示", "请选择学科");
            return false;
        } else if (isUploadImage) {
            showMaterialDialog("提示", "请等待封面上传完毕或者删除封面");
            return false;
        } else if (isUploadFile) {
            showMaterialDialog("提示", "请等待研修方案上传完毕或者删除研修方案");
            return false;
        }
        return true;
    }

    private void commit() {
        String title = et_name.getText().toString().trim();
        String summary = et_content.getText().toString().trim();
        String url = Constants.OUTRT_NET + "/m/workshop";
        Map<String, String> map = new HashMap<>();
        if (trainId != null) {
            map.put("workshopRelation.relation.id", trainId);
        }
        map.put("title", title);
        map.put("summary", summary);
        map.put("stage", stageId);
        map.put("subject", subjectId);
        if (mImageInfo != null) {
            map.put("image.id", mImageInfo.getId());
            map.put("image.url", mImageInfo.getUrl());
            map.put("image.fileName", mImageInfo.getFileName());
        }
        if (mFileInfo != null) {
            map.put("solutions[0].id", mFileInfo.getId());
            map.put("solutions[0].url", mFileInfo.getUrl());
            map.put("solutions[0].fileName", mFileInfo.getFileName());
        }
        addSubscription(OkHttpClientManager.postAsyn(context, url, new OkHttpClientManager.ResultCallback<BaseResponseResult<WorkShopMobileEntity>>() {
            @Override
            public void onBefore(Request request) {
                showLoadingDialog("正在提交");
            }

            @Override
            public void onError(Request request, Exception e) {
                hideLoadingDialog();
                onNetWorkError(context);
            }

            @Override
            public void onResponse(BaseResponseResult<WorkShopMobileEntity> response) {
                hideLoadingDialog();
                if (response != null && response.getResponseData() != null) {
                    Intent intent = new Intent(context, WSHomePageActivity.class);
                    intent.putExtra("workshopId", response.getResponseData().getId());
                    intent.putExtra("workshopTitle", response.getResponseData().getTitle());
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                } else if (response != null && response.getResponseCode() != null && response.getResponseCode().equals("01")) {
                    tipDialog();
                } else {
                    toastFullScreen("创建失败", false);
                }
            }
        }, map));
    }

    private void tipDialog() {
        MaterialDialog dialog = new MaterialDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setTitle("提交结果");
        dialog.setMessage("您已经创建过个人工作坊");
        dialog.setPositiveTextColor(ContextCompat.getColor(context, R.color.defaultColor));
        dialog.setPositiveButton("我知道了", new MaterialDialog.ButtonClickListener() {
            @Override
            public void onClick(View v, AlertDialog dialog) {
                finish();
            }
        });
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mImagePosable != null) {
            mImagePosable.dispose();
        }
        if (mFilePosable != null) {
            mFilePosable.dispose();
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
