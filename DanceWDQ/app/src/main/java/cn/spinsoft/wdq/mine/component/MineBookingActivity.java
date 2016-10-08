package cn.spinsoft.wdq.mine.component;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;

import cn.spinsoft.wdq.R;
import cn.spinsoft.wdq.base.BaseActivity;
import cn.spinsoft.wdq.bean.SimpleResponse;
import cn.spinsoft.wdq.effect.AnimatorEffect;
import cn.spinsoft.wdq.enums.BookingState;
import cn.spinsoft.wdq.mine.biz.BookingListWithInfo;
import cn.spinsoft.wdq.mine.biz.MineParser;
import cn.spinsoft.wdq.mine.widget.MineBookingAdapter;
import cn.spinsoft.wdq.utils.Constants;
import cn.spinsoft.wdq.widget.EmptyView;
import cn.spinsoft.wdq.widget.RadioGroup;
import cn.spinsoft.wdq.widget.RecyclerItemClickListener;

/**
 * Created by hushujun on 16/1/5.
 */
public class MineBookingActivity extends BaseActivity implements RecyclerItemClickListener,
        PullToRefreshLayout.OnPullListener {
    private static final String TAG = MineBookingActivity.class.getSimpleName();

    private int watcherUserId = -1;
    private int orgId = -1;
    private BookingState mState = BookingState.ALL;
    private int pageIdx = 1;
    private boolean isOrg = false;

    private TextView mTitleTx;
    private PullToRefreshLayout mPtrl;
    private MineBookingAdapter mBookingAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine_booking;
    }

    @Override
    protected void initHandler() {
        watcherUserId = getIntent().getIntExtra(Constants.Strings.USER_ID, -1);
        if (watcherUserId <= 0) {
            isOrg = true;
            orgId = getIntent().getIntExtra(Constants.Strings.ORG_ID, -1);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isOrg) {
            mTitleTx.setText("预约订单");
        }else {
            mTitleTx.setText("我的预约");
        }
        mPtrl.autoRefresh();
    }

    @Override
    protected void initViewAndListener(Bundle savedInstanceState) {
        TextView mBackTx = (TextView) findViewById(R.id.base_title_back);
        mTitleTx = (TextView) findViewById(R.id.base_title_name);
        RadioGroup mBookingLabels = (RadioGroup) findViewById(R.id.mine_booking_labels);
        final TextView mSliderTx = (TextView) findViewById(R.id.mine_booking_slider);
        mPtrl = (PullToRefreshLayout) findViewById(R.id.mine_booking_content);
        mPtrl.setEmptyView(new EmptyView(this));
        mPtrl.setOnPullListener(this);
        RecyclerView mContentRv = (RecyclerView) mPtrl.getPullableView();
        mContentRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBookingAdapter = new MineBookingAdapter(null, this, isOrg);
        mContentRv.setAdapter(mBookingAdapter);

        mBackTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBookingLabels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View view = group.findViewById(checkedId);
                AnimatorEffect.smoothHorizontalSlideTo(mSliderTx, view);
                BookingState state;
                switch (checkedId) {
                    case R.id.mine_booking_confirmed:
                        state = BookingState.CONFIRMED;
                        break;
                    case R.id.mine_booking_unconfirm:
                        state = BookingState.UNCONFIRM;
                        break;
                    case R.id.mine_booking_reject:
                        state = BookingState.REJECTED;
                        break;
                    case R.id.mine_booking_all:
                    default:
                        state = BookingState.ALL;
                        break;
                }
                mState = state;
                mPtrl.autoRefresh();
            }
        });
    }

    @Override
    public void onItemClicked(RecyclerView.Adapter adapter, View view, int position) {
        int bookId = mBookingAdapter.getItem(position).getBookId();
        BookingState state = BookingState.UNCONFIRM;
        switch (view.getId()) {
            case R.id.mine_booking_item_person_cancel://取消预约
                state = BookingState.CANCELED;
                break;
            case R.id.booking_item_org_confirm:
                state = BookingState.CONFIRMED;
                break;
            case R.id.booking_item_org_refuse:
                state = BookingState.REJECTED;
                break;
            default:
                break;
        }
        new AsyncOptionBooking().execute(bookId, state.getValue());
        mBookingAdapter.notifyBookingStateChanged(position, state);
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx = 1;
        if (isOrg) {
            new AsyncOrgBookingList().execute(orgId, mState.getValue(), pageIdx);
        } else {
            new AsyncPersonBookingList().execute(watcherUserId, mState.getValue(), pageIdx);
        }
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
        pageIdx++;
        if (isOrg) {
            new AsyncOrgBookingList().execute(orgId, mState.getValue(), pageIdx);
        } else {
            new AsyncPersonBookingList().execute(watcherUserId, mState.getValue(), pageIdx);
        }
    }

    class AsyncPersonBookingList extends AsyncTask<Integer, Integer, BookingListWithInfo> {

        @Override
        protected BookingListWithInfo doInBackground(Integer... params) {
            return MineParser.getMineBookingList(params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(BookingListWithInfo listWithInfo) {
            if (listWithInfo != null) {
                SimpleResponse response = listWithInfo.getResponse();
                if (pageIdx == 1) {
                    mBookingAdapter.setAdapterDataList(listWithInfo.getDataList());
                    mPtrl.refreshFinish(PullToRefreshLayout.SUCCEED);
                } else {
                    mBookingAdapter.addAdapterDataList(listWithInfo.getDataList());
                    mPtrl.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                }
            } else {
                if (pageIdx == 1) {
                    mPtrl.refreshFinish(PullToRefreshLayout.FAIL);
                    mBookingAdapter.setAdapterDataList(null);
                } else {
                    mPtrl.loadmoreFinish(PullToRefreshLayout.FAIL);
                }
            }
            if (mBookingAdapter.getItemCount() <= 0) {
                mPtrl.showEmptyView(true);
            } else {
                mPtrl.showEmptyView(false);
            }
        }
    }

    class AsyncOrgBookingList extends AsyncPersonBookingList {

        @Override
        protected BookingListWithInfo doInBackground(Integer... params) {
            return MineParser.getOrgBookingList(params[0], params[1], params[2]);
        }
    }

    class AsyncOptionBooking extends AsyncTask<Integer, Integer, SimpleResponse> {

        @Override
        protected SimpleResponse doInBackground(Integer... params) {
            return MineParser.bookingOption(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(SimpleResponse simpleResponse) {
            if (simpleResponse != null) {
//                Toast.makeText(MineBookingActivity.this, simpleResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
