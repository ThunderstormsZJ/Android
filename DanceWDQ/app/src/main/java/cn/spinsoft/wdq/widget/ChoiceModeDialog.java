package cn.spinsoft.wdq.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleItemData;
import cn.spinsoft.wdq.utils.SimpleItemDataUtils;

/**
 * Created by hushujun on 16/1/7.
 */
public class ChoiceModeDialog extends Dialog {
    private static final String TAG = ChoiceModeDialog.class.getSimpleName();

    private OnItemCheckedListener checkedListener;
    private boolean mMultiChoice = false;
    private List<String> checkedDanceType = new ArrayList<String>();

    public void setCheckedDanceType(List<String> checkedDanceType) {
        this.checkedDanceType = checkedDanceType;
    }

    public interface OnItemCheckedListener {
        void onItemChecked(List<SimpleItemData> checkedItems);
    }

    private RecyclerView contentRv;
    private Button confirmBtn;
    private SimpleChoiceModeAdapter multiChoiceAdapter;

    public ChoiceModeDialog(Context context, OnItemCheckedListener listener) {
        this(context, listener, false);
    }

    public ChoiceModeDialog(Context context, OnItemCheckedListener listener, boolean multiChoice) {
        super(context, R.style.DialogWithTransparentBackground);
        this.checkedListener = listener;
        mMultiChoice = multiChoice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_multi_choice);
        contentRv = (RecyclerView) findViewById(R.id.multi_choice_content);
        confirmBtn = (Button) findViewById(R.id.multi_choice_confirm);
        contentRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        List<SimpleItemData> danceTypes = SimpleItemDataUtils.getDanceTypeList(false, getContext());
        for (SimpleItemData danceType : danceTypes) {
            if ("不限".equals(danceType.getName())) {
                danceTypes.remove(danceType);
                break;
            }
        }
        multiChoiceAdapter = new SimpleChoiceModeAdapter(danceTypes, mMultiChoice);
        multiChoiceAdapter.setCheckedDanceType(checkedDanceType);
        contentRv.setAdapter(multiChoiceAdapter);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedListener != null) {
                    checkedListener.onItemChecked(multiChoiceAdapter.getCheckedItems());
                }
                dismiss();
            }
        });

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    @Deprecated
    public void setItemList(List<SimpleItemData> itemDataList) {
//        multiChoiceAdapter.setAdapterDataList(itemDataList);
    }

}
