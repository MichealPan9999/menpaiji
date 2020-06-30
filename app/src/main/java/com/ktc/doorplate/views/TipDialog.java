package com.ktc.doorplate.views;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ktc.doorplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ktc.jkf.utils.TextUtils;

public class TipDialog extends AppCompatDialog {

    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.text_title)
    TextView tvTitle;
    @BindView(R.id.text_tip)
    TextView tvTip;

    private OnButtonListener mButtonListener;

    public TipDialog(Context context) {
        super(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip);
        ButterKnife.bind(this);
    }

    /**
     * 取消按钮
     */
    @OnClick(R.id.btn_cancel)
    void onButtonCancel(View v) {
        mButtonListener.setCancelButton();
        dismiss();
    }

    /**
     * 确定按钮
     */
    @OnClick(R.id.btn_ok)
    void onButtonOk(View v) {
        mButtonListener.setOkButton();
        dismiss();
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (tvTitle != null && !TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
    }

    /**
     * 设置提示语
     *
     * @param tip 提示语
     */
    public void setTip(String tip) {
        if (tvTip != null && !TextUtils.isEmpty(tip)) {
            tvTip.setText(tip);
        }
    }

    public void setOnButtonListener(OnButtonListener onButtonListener) {
        this.mButtonListener = onButtonListener;
    }

    public interface OnButtonListener {
        void setOkButton();
        void setCancelButton();
    }
}
