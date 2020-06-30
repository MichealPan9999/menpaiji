package com.ktc.doorplate.views;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ktc.doorplate.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ktc.jkf.utils.TextUtils;

public class EditDialog extends AppCompatDialog {

    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_ok)
    Button btnOk;
    @BindView(R.id.text_title)
    TextView tvTitle;
    @BindView(R.id.text_setting_name)
    TextView tvSettingName;
    @BindView(R.id.text_setting_value)
    EditText etSettingValue;
    @BindView(R.id.linear_edit_part)
    LinearLayout linearEditPart;
    private String prevValue = "";
    private String name;
    private String value;
    private OnSetValueListener listener;

    public EditDialog(Context context) {
        super(context, R.style.Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit);
        ButterKnife.bind(this);
    }

    public void setValue(String value) {
        this.value = value;
        etSettingValue.setText(value);
        etSettingValue.setSelection(value.length());
    }

    public String getValue() {
        return this.value;
    }

    public void setName(String name) {
        if (TextUtils.isEmpty(name)) {
            linearEditPart.setVisibility(View.GONE);
            return;
        }
        this.name = name;
        tvSettingName.setText(name);
    }

    public String getName() {
        return this.name;
    }

    /**
     * 取消按钮
     */
    @OnClick(R.id.btn_cancel)
    void onButtonCancel(View v) {
        if (listener != null) {
            listener.cancel();
        }
        dismiss();
    }

    /**
     * 确定按钮
     */
    @OnClick(R.id.btn_ok)
    void onButtonOk(View view) {
        this.value = etSettingValue.getText().toString();
        if (listener != null) {
            listener.setValue(value);
        }
        this.dismiss();
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    /**
     * 赋值接口
     */
    public interface OnSetValueListener {
        void setValue(String value);

        void cancel();
    }

    public void setValueListener(OnSetValueListener listener) {
        this.listener = listener;
    }
}
