package cn.spinsoft.wdq.video.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.browse.biz.BrowseHandler;
import cn.spinsoft.wdq.browse.biz.BrowseParser;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.biz.WalletRecodeListWithInfo;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.utils.SharedPreferencesUtil;
import cn.spinsoft.wdq.widget.ConfirmDialog;

/**
 * Created by hushujun on 15/11/23.
 */
public class TipsChoiceDialog extends Dialog implements View.OnClickListener, ConfirmDialog.OnConfirmClickListenter {
    private static final String TAG = TipsChoiceDialog.class.getSimpleName();
    public static String rewardType = Constants.Strings.REWARD_WAY_DICE;
    private String rewardPerson;
    private float walletBalance = 0;//余额

    public String getRewardPerson() {
        return rewardPerson;
    }

    public void setRewardPerson(String rewardPerson) {
        this.rewardPerson = rewardPerson;
    }

    private OnTipsChoiceListener mChoiceListener;
    private TextView mTipsTx, mNickNameTx;
    private RadioGroup mRewardWayRg;
    private RadioButton mDiceWayRb;
    private float tipsQuantity = 1;//100分
    private Random mRandom;
    private Button confirmBtn;

    public interface OnTipsChoiceListener {
        void tipsSelected(float tips);
    }

    public TipsChoiceDialog(Context context, OnTipsChoiceListener listener) {
        this(context, R.style.DialogWithTransparentBackground);
        this.mChoiceListener = listener;
    }

    protected TipsChoiceDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_tips_choice);

        mTipsTx = (TextView) findViewById(R.id.tips_quantity);
        mNickNameTx = (TextView) findViewById(R.id.dia_reward_nickname);
        mRewardWayRg = (RadioGroup) findViewById(R.id.dia_reward_way_rg);
        mDiceWayRb = (RadioButton) findViewById(R.id.dia_reward_way_dice);
        ImageButton diceIB = (ImageButton) findViewById(R.id.tips_dice);
        confirmBtn = (Button) findViewById(R.id.tips_confirm);
        diceIB.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);

        mRandom = new Random();

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        mRewardWayRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dia_reward_way_dice) {
                    rewardType = Constants.Strings.REWARD_WAY_DICE;
                } else if (checkedId == R.id.dia_reward_way_wx) {
                    rewardType = Constants.Strings.REWARD_WAY_WX;
                }
            }
        });
        //初始化
        tipsQuantity = mRandom.nextFloat() * 10;
        new BalanceAsync().execute(SharedPreferencesUtil.getInstance(getContext()).getLoginUser().getUserId());
        mTipsTx.setText(String.format("￥ %.2f", tipsQuantity));
        if (rewardPerson != null && mNickNameTx != null) {
            mNickNameTx.setText("打赏" + rewardPerson);
        }
    }


    @Override
    public void show() {
        new BalanceAsync().execute(SharedPreferencesUtil.getInstance(getContext()).getLoginUser().getUserId());
        if (rewardPerson != null && mNickNameTx != null) {
            mNickNameTx.setText("打赏" + rewardPerson);
        }
        if (mTipsTx != null) {
            tipsQuantity = mRandom.nextFloat() * 10;
            mTipsTx.setText(String.format("￥ %.2f", tipsQuantity));
        }
        super.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tips_dice:
                tipsQuantity = mRandom.nextFloat() * 10;
                mTipsTx.setText(String.format("￥ %.2f", tipsQuantity));
                break;
            case R.id.tips_confirm:
                if (mChoiceListener != null) {
                    ConfirmDialog confirmDialog = new ConfirmDialog(getContext(), ConfirmDialog.Type.REWARD, this);
                    confirmDialog.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfirmClick(View v) {
        if (v.getId() == R.id.dia_confirm_confirm) {
            if (rewardType == Constants.Strings.REWARD_WAY_DICE) {
                if (tipsQuantity > walletBalance) {
                    Toast.makeText(getContext(), "余额不足", Toast.LENGTH_SHORT).show();
                } else {
                    new DiceOrderAsync().execute(String.valueOf(Math.round(tipsQuantity * 100)));
                    dismiss();
                }
            } else {
                mChoiceListener.tipsSelected(tipsQuantity);
                dismiss();
            }
        }
    }

    class BalanceAsync extends AsyncTask<Integer, String, WalletRecodeListWithInfo> {

        @Override
        protected WalletRecodeListWithInfo doInBackground(Integer... params) {
            return MineParser.getWalletRecode(params[0], 1);
        }

        @Override
        protected void onPostExecute(WalletRecodeListWithInfo walletInfo) {
            if (walletInfo != null) {
                if (TextUtils.isEmpty(walletInfo.getBalance())) {
                    mDiceWayRb.setText("零钱(剩余0)");
                } else {
                    mDiceWayRb.setText("零钱(剩余" + walletInfo.getBalance() + ")");
                    walletBalance = Float.valueOf(walletInfo.getBalance());
                }
            }
        }
    }

    class DiceOrderAsync extends AsyncTask<String, String, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(String... params) {
            return BrowseParser.diceOrder(BrowseHandler.Status.Video.videoId, BrowseHandler.watcherUserId
                    , BrowseHandler.Status.Video.ownerUserId, params[0]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
                if (simpleResponse.getCode() == SimpleResponse.SUCCESS) {
                    Toast.makeText(getContext(), "打赏成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
