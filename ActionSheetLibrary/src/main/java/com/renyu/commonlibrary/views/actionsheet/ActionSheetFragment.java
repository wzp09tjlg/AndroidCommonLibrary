package com.renyu.commonlibrary.views.actionsheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.blankj.utilcode.util.SizeUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.renyu.commonlibrary.views.utils.DateRangeUtils;
import com.renyu.commonlibrary.views.utils.Utils;
import com.renyu.commonlibrary.views.wheelview.LoopView;
import com.renyu.commonlibrary.views.wheelview.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Clevo on 2016/6/6.
 */
public class ActionSheetFragment extends Fragment {

    //是否已经关闭
    boolean isDismiss = true;
    FragmentManager manager = null;

    View decorView;
    //添加进入的view
    View realView;
    //添加进入的第一个view
    View pop_child_layout;
    //待添加的view
    View customerView;

    //提供类型
    public enum CHOICE {
        ITEM, GRID, TOUTIAO, TIME, DATERANGE, CUSTOMER
    }

    //是否自动关闭
    boolean canDismiss = true;

    OnItemClickListener onItemClickListener;
    OnToutiaoChoiceItemClickListener onToutiaoChoiceItemClickListener;
    OnCancelListener onCancelListener;
    OnOKListener onOKListener;

    public interface OnCancelListener {
        void onCancelClick();
    }

    public interface OnOKListener {
        void onOKClick(Object value);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnToutiaoChoiceItemClickListener {
        void onItemClick(int row, int column);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnToutiaoChoiceItemClickListener(OnToutiaoChoiceItemClickListener onToutiaoChoiceItemClickListener) {
        this.onToutiaoChoiceItemClickListener = onToutiaoChoiceItemClickListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnOKListener(OnOKListener onOKListener) {
        this.onOKListener = onOKListener;
    }

    public void setCustomerView(View customerView) {
        this.customerView = customerView;
    }

    public void setCanDismiss(boolean canDismiss) {
        this.canDismiss = canDismiss;
    }

    private static ActionSheetFragment newItemInstance(String title, int titleColor, String cancelTitle, int cancelTitleColor, String[] items, String[] subItems, int choiceIndex) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("titleColor", titleColor);
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putStringArray("items", items);
        bundle.putStringArray("subItems", subItems);
        bundle.putInt("choiceIndex", choiceIndex);
        bundle.putInt("type", 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static ActionSheetFragment newGridInstance(String title, int titleColor, String cancelTitle, int cancelTitleColor, String[] items, int[] images, int columnCount) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("titleColor", titleColor);
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putStringArray("items", items);
        bundle.putIntArray("images", images);
        bundle.putInt("columnCount", columnCount);
        bundle.putInt("type", 2);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static ActionSheetFragment newToutiaochoiceInstance(String cancelTitle, int cancelTitleColor, String[] topTitles, int[] topImages, String[] bottomTitles, int[] bottomImages) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putStringArray("topTitles", topTitles);
        bundle.putIntArray("topImages", topImages);
        bundle.putStringArray("bottomTitles", bottomTitles);
        bundle.putIntArray("bottomImages", bottomImages);
        bundle.putInt("type", 3);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static ActionSheetFragment newTimeInstance(String title, int titleColor, String okTitle, int okTitleColor, String cancelTitle, int cancelTitleColor, int hour, int minute) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("titleColor", titleColor);
        bundle.putString("okTitle", okTitle);
        bundle.putInt("okTitleColor", okTitleColor);
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putInt("hour", hour);
        bundle.putInt("minute", minute);
        bundle.putInt("type", 4);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static ActionSheetFragment newDateRangeInstance(String title, int titleColor, String okTitle, int okTitleColor, String cancelTitle, int cancelTitleColor, long startTime, long endTime, boolean isNeedHM) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("titleColor", titleColor);
        bundle.putString("okTitle", okTitle);
        bundle.putInt("okTitleColor", okTitleColor);
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putLong("startTime", startTime);
        bundle.putLong("endTime", endTime);
        bundle.putBoolean("isNeedHM", isNeedHM);
        bundle.putInt("type", 6);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static ActionSheetFragment newCustomerInstance(String title, int titleColor, String okTitle, int okTitleColor, String cancelTitle, int cancelTitleColor) {
        ActionSheetFragment fragment = new ActionSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("titleColor", titleColor);
        bundle.putString("okTitle", okTitle);
        bundle.putInt("okTitleColor", okTitleColor);
        bundle.putString("cancelTitle", cancelTitle);
        bundle.putInt("cancelTitleColor", cancelTitleColor);
        bundle.putInt("type", 7);
        fragment.setArguments(bundle);
        return fragment;
    }

    // 不需要再getActivity()了
    public Context context;

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onAttachToContext(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    protected void onAttachToContext(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realView = inflater.inflate(R.layout.view_actionsheet, container, false);
        initViews(realView);
        decorView = ((Activity) context).getWindow().getDecorView();
        ((ViewGroup) decorView).addView(realView);
        startPlay();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            isDismiss = savedInstanceState.getBoolean("isDismiss");
            FragmentActivity activity = (FragmentActivity) context;
            if (activity != null) {
                manager = activity.getSupportFragmentManager();
            }
            try {
                dismiss();
            } catch (Exception e) {

            }
        }
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager.isActive()) {
            View focusView = ((Activity) context).getCurrentFocus();
            if (focusView != null) {
                manager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    private void initViews(View view) {
        pop_child_layout = view.findViewById(R.id.pop_child_layout);
        pop_child_layout.setVisibility(View.INVISIBLE);
        pop_child_layout.setOnTouchListener((v, event) -> true);
        realView.setOnClickListener(v -> dismiss());

        String title = getArguments().getString("title");
        TextView pop_title = view.findViewById(R.id.pop_title);
        if (getArguments().getInt("titleColor", -1) != -1) {
            pop_title.setTextColor(getArguments().getInt("titleColor"));
        }
        if (TextUtils.isEmpty(title)) {
            pop_title.setVisibility(View.GONE);
        }
        else {
            pop_title.setText(title);
        }

        TextView pop_cancel1 = view.findViewById(R.id.pop_cancel1);
        if (getArguments().getInt("cancelTitleColor", -1) != -1) {
            pop_cancel1.setTextColor(getArguments().getInt("cancelTitleColor"));
        }
        String cancelTitle = getArguments().getString("cancelTitle");
        if (!TextUtils.isEmpty(cancelTitle)) {
            pop_cancel1.setText(cancelTitle);
        }

        TextView pop_ok1 = view.findViewById(R.id.pop_ok1);
        if (getArguments().getInt("okTitleColor", -1) != -1) {
            pop_ok1.setTextColor(getArguments().getInt("okTitleColor"));
        }
        String okTitle = getArguments().getString("okTitle");
        if (!TextUtils.isEmpty(okTitle)) {
            pop_ok1.setText(okTitle);
        }

        if (getArguments().getInt("type") == 1) {
            // 不需要标题栏的取消功能
            pop_cancel1.setVisibility(View.INVISIBLE);

            View view_space = view.findViewById(R.id.view_space);
            view_space.setVisibility(View.VISIBLE);
            TextView pop_cancel = view.findViewById(R.id.pop_cancel);
            if (getArguments().getInt("cancelTitleColor", -1) != -1) {
                pop_cancel.setTextColor(getArguments().getInt("cancelTitleColor"));
            }
            if (!TextUtils.isEmpty(cancelTitle)) {
                pop_cancel.setText(cancelTitle);
                pop_cancel.setVisibility(View.VISIBLE);
                pop_cancel.setOnClickListener(v -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancelClick();
                    }
                    dismiss();
                });
            }
            if (!TextUtils.isEmpty(title)) {
                LinearLayout pop_morechoice = view.findViewById(R.id.pop_morechoice);
                pop_morechoice.setVisibility(View.VISIBLE);
            }

            ListView pop_listview = view.findViewById(R.id.pop_listview);
            pop_listview.setVisibility(View.VISIBLE);
            ActionSheetAdapter adapter = new ActionSheetAdapter(context, getArguments().getStringArray("items"),
                    getArguments().getStringArray("subItems"),
                    getArguments().getInt("choiceIndex"));
            pop_listview.setAdapter(adapter);
            pop_listview.setOnItemClickListener((parent, view1, position, id) -> {
                adapter.setChoiceIndex(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                    dismiss();
                }
            });
        } else if (getArguments().getInt("type") == 2) {
            // 不需要标题栏的取消功能
            pop_cancel1.setVisibility(View.INVISIBLE);

            View view_space = view.findViewById(R.id.view_space);
            view_space.setVisibility(View.VISIBLE);
            TextView pop_cancel = view.findViewById(R.id.pop_cancel);
            if (getArguments().getInt("cancelTitleColor", -1) != -1) {
                pop_cancel.setTextColor(getArguments().getInt("cancelTitleColor"));
            }
            if (!TextUtils.isEmpty(cancelTitle)) {
                pop_cancel.setText(cancelTitle);
                pop_cancel.setVisibility(View.VISIBLE);
                pop_cancel.setOnClickListener(v -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancelClick();
                    }
                    dismiss();
                });
            }
            if (!TextUtils.isEmpty(title)) {
                LinearLayout pop_morechoice = view.findViewById(R.id.pop_morechoice);
                pop_morechoice.setVisibility(View.VISIBLE);
            }

            GridLayout pop_grid = view.findViewById(R.id.pop_grid);
            pop_grid.setVisibility(View.VISIBLE);
            int width = (Utils.getScreenWidth(context) - Utils.dp2px(context, 20)) / getArguments().getInt("columnCount");
            for (int i = 0; i < getArguments().getStringArray("items").length; i++) {
                final int i_ = i;
                View viewChild = LayoutInflater.from(context).inflate(R.layout.adapter_share, null, false);
                LinearLayout adapter_share_layout = viewChild.findViewById(R.id.adapter_share_layout);
                RxView.clicks(adapter_share_layout).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(aVoid -> {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(i_);
                    }
                    dismiss();
                });
                ImageView adapter_share_image = viewChild.findViewById(R.id.adapter_share_image);
                TextView adapter_share_text = viewChild.findViewById(R.id.adapter_share_text);
                adapter_share_image.setImageResource(getArguments().getIntArray("images")[i]);
                adapter_share_text.setText(getArguments().getStringArray("items")[i]);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.setGravity(Gravity.CENTER);
                params.width = width;
                params.height = Utils.dp2px(context, SizeUtils.dp2px(30));
                params.columnSpec = GridLayout.spec(i % getArguments().getInt("columnCount"));
                params.rowSpec = GridLayout.spec(i / getArguments().getInt("columnCount"));
                pop_grid.addView(viewChild, params);
            }
        } else if (getArguments().getInt("type") == 3) {
            TextView pop_cancel = view.findViewById(R.id.pop_cancel);
            if (getArguments().getInt("cancelTitleColor", -1) != -1) {
                pop_cancel.setTextColor(getArguments().getInt("cancelTitleColor"));
            }
            if (!TextUtils.isEmpty(cancelTitle)) {
                pop_cancel.setText(cancelTitle);
                pop_cancel.setVisibility(View.VISIBLE);
                pop_cancel.setOnClickListener(v -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancelClick();
                    }
                    dismiss();
                });
            }

            LinearLayout pop_toutiaochoice_layout = view.findViewById(R.id.pop_toutiaochoice_layout);
            pop_toutiaochoice_layout.setVisibility(View.VISIBLE);

            String[] topTitles = getArguments().getStringArray("topTitles");
            int[] topImages = getArguments().getIntArray("topImages");
            RecyclerView rv_toutiaochoice_top = view.findViewById(R.id.rv_toutiaochoice_top);
            rv_toutiaochoice_top.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rv_toutiaochoice_top.setHasFixedSize(true);
            ToutiaochoiceAdapter adapterTop = new ToutiaochoiceAdapter(topTitles, topImages, 1, onToutiaoChoiceItemClickListener);
            rv_toutiaochoice_top.setAdapter(adapterTop);

            String[] bottomTitles = getArguments().getStringArray("bottomTitles");
            int[] bottomImages = getArguments().getIntArray("bottomImages");
            RecyclerView rv_toutiaochoice_bottom = view.findViewById(R.id.rv_toutiaochoice_bottom);
            rv_toutiaochoice_bottom.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rv_toutiaochoice_bottom.setHasFixedSize(true);
            ToutiaochoiceAdapter adapterBottom = new ToutiaochoiceAdapter(bottomTitles, bottomImages, 2, onToutiaoChoiceItemClickListener);
            rv_toutiaochoice_bottom.setAdapter(adapterBottom);
        } else if (getArguments().getInt("type") == 4) {
            ArrayList<String> hours = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                hours.add(i < 10 ? "0" + i : "" + i);
            }
            ArrayList<String> minutes = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                minutes.add(i < 10 ? "0" + i : "" + i);
            }
            final int[] hourSelectedItem = {getArguments().getInt("hour")};
            final int[] minuteSelectedItem = {getArguments().getInt("minute")};

            LinearLayout pop_morechoice = view.findViewById(R.id.pop_morechoice);
            pop_morechoice.setVisibility(View.VISIBLE);
            pop_ok1.setOnClickListener(v -> {
                if (onOKListener != null) {
                    onOKListener.onOKClick(hours.get(hourSelectedItem[0]) + ":" + (minutes.get(minuteSelectedItem[0])));
                }
                dismiss();
            });
            pop_cancel1.setOnClickListener(v -> {
                if (onCancelListener != null) {
                    onCancelListener.onCancelClick();
                }
                dismiss();
            });

            LinearLayout pop_wheel_timelayout = view.findViewById(R.id.pop_wheel_timelayout);
            LoopView pop_wheel_timelayout_hour = view.findViewById(R.id.pop_wheel_timelayout_hour);
            LoopView pop_wheel_timelayout_minute = view.findViewById(R.id.pop_wheel_timelayout_minute);
            pop_wheel_timelayout.setVisibility(View.VISIBLE);
            pop_wheel_timelayout_hour.setNotLoop();
            pop_wheel_timelayout_hour.setViewPadding(Utils.dp2px(context, 30), Utils.dp2px(context, 15), Utils.dp2px(context, 30), Utils.dp2px(context, 15));
            pop_wheel_timelayout_hour.setItems(hours);
            pop_wheel_timelayout_hour.setTextSize(18);
            pop_wheel_timelayout_hour.setInitPosition(hourSelectedItem[0]);
            pop_wheel_timelayout_hour.setListener(index -> hourSelectedItem[0] = index);
            pop_wheel_timelayout_minute.setNotLoop();
            pop_wheel_timelayout_minute.setViewPadding(Utils.dp2px(context, 30), Utils.dp2px(context, 15), Utils.dp2px(context, 30), Utils.dp2px(context, 15));
            pop_wheel_timelayout_minute.setItems(minutes);
            pop_wheel_timelayout_minute.setTextSize(18);
            pop_wheel_timelayout_minute.setInitPosition(minuteSelectedItem[0]);
            pop_wheel_timelayout_minute.setListener(index -> minuteSelectedItem[0] = index);
        } else if (getArguments().getInt("type") == 6) {
            DateRangeUtils dateRangeUtils = new DateRangeUtils(getArguments().getLong("startTime"), getArguments().getLong("endTime"), getArguments().getBoolean("isNeedHM"));
            dateRangeUtils.showDateRange(this, view, onOKListener, onCancelListener);
        } else if (getArguments().getInt("type") == 7) {
            LinearLayout pop_morechoice = view.findViewById(R.id.pop_morechoice);
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(okTitle) && TextUtils.isEmpty(cancelTitle)) {
                pop_morechoice.setVisibility(View.GONE);
            } else {
                pop_morechoice.setVisibility(View.VISIBLE);
                pop_ok1.setOnClickListener(v -> {
                    if (onOKListener != null) {
                        onOKListener.onOKClick("");
                    }
                    if (canDismiss) {
                        dismiss();
                    }
                });
                pop_cancel1.setOnClickListener(v -> {
                    if (onCancelListener != null) {
                        onCancelListener.onCancelClick();
                    }
                    dismiss();
                });
            }
            LinearLayout pop_customer_layout = view.findViewById(R.id.pop_customer_layout);
            pop_customer_layout.setVisibility(View.VISIBLE);
            if (customerView != null) {
                pop_customer_layout.removeAllViews();
                if (customerView.getParent() != null) {
                    // 上一个引用customerView的地方会出现留白
                    // 这不是解决问题的正确方法，应该避免传入正在使用的同一个对象
                    ((ViewGroup) customerView.getParent()).removeAllViews();
                }
                pop_customer_layout.addView(customerView);
            }
        }
    }

    private void startPlay() {
        pop_child_layout.post(new Runnable() {
            @Override
            public void run() {
                final int moveHeight = pop_child_layout.getMeasuredHeight();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(500);
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        pop_child_layout.setVisibility(View.VISIBLE);
                    }
                });
                valueAnimator.addUpdateListener(animation -> {
                    ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                    realView.setBackgroundColor((Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(), Color.parseColor("#00000000"), Color.parseColor("#70000000")));
                    //当底部存在导航栏并且decorView获取的高度不包含底部状态栏的时候，需要去掉这个高度差
                    if (Utils.getNavBarHeight() > 0) {
                        pop_child_layout.setTranslationY((moveHeight + Utils.getNavBarHeight()) * (1 - animation.getAnimatedFraction()) - Utils.getNavBarHeight());
                    } else {
                        pop_child_layout.setTranslationY(moveHeight * (1 - animation.getAnimatedFraction()));
                    }
                });
                valueAnimator.start();
            }
        });
    }

    private void stopPlay() {
        pop_child_layout.post(() -> {
            final int moveHeight = pop_child_layout.getMeasuredHeight();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(500);
            valueAnimator.addUpdateListener(animation -> {
                ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                realView.setBackgroundColor((Integer) argbEvaluator.evaluate(animation.getAnimatedFraction(), Color.parseColor("#70000000"), Color.parseColor("#00000000")));
                if (Utils.getNavBarHeight() > 0 && decorView.getMeasuredHeight() != Utils.getScreenHeight(context)) {
                    pop_child_layout.setTranslationY((moveHeight + Utils.getNavBarHeight()) * animation.getAnimatedFraction() - Utils.getNavBarHeight());
                } else {
                    pop_child_layout.setTranslationY(moveHeight * animation.getAnimatedFraction());
                }
            });
            valueAnimator.start();
        });
    }

    private void show(FragmentActivity fragmentActivity, final String tag) {
        if (fragmentActivity.isDestroyed() || !isDismiss) {
            return;
        }
        isDismiss = false;
        manager = fragmentActivity.getSupportFragmentManager();
        new Handler().post(() -> {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(this, tag);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        });
    }

    public void dismiss() {
        try {
            if (isDismiss) {
                return;
            }
            isDismiss = true;
            if (context != null && ((Activity) context).isFinishing()) {
                return;
            }
            new Handler().post(() -> {
                if (manager != null) {
                    manager.popBackStack();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.remove(this);
                    transaction.commitAllowingStateLoss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreCustomerView(View customerView) {
        this.customerView = customerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isDismiss", isDismiss);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopPlay();
        new Handler().postDelayed(() -> ((ViewGroup) decorView).removeView(realView), 500);
    }

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {
        public Builder() {

        }

        String tag = "ActionSheetFragment";
        // 标题
        String title = "";
        int titleColor = Color.parseColor("#333333");
        // 确定
        String okTitle = "";
        int okTitleColor = Color.parseColor("#333333");
        // 取消
        String cancelTitle = "";
        int cancelTitleColor  = Color.parseColor("#333333");
        // 弹窗类型
        CHOICE choice;
        // list选中项中的选中值
        int choiceIndex = -1;
        // list或者grid选择项
        String[] items;
        String[] subItems;
        int[] images;
        // Gird每排的数量
        int columnCount = 4;
        // 是否可以点击确定之后关闭
        boolean canDismiss = true;
        // 监听事件
        OnItemClickListener onItemClickListener;
        OnToutiaoChoiceItemClickListener onToutiaoChoiceItemClickListener;
        OnCancelListener onCancelListener;
        OnOKListener onOKListener;
        // 自定义视图
        View customerView;
        // 时间范围选择
        long startTime;
        long endTime;
        // 是否展示时分
        boolean isNeedHM;
        // 时分选择器选择的时分
        int hour;
        int minute;
        // 头条选择器使用
        String[] topTitles;
        int[] topImages;
        String[] bottomTitles;
        int[] bottomImages;

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setTitle(String title, int color) {
            this.title = title;
            this.titleColor = color;
            return this;
        }

        public Builder setOkTitle(String okTitle) {
            this.okTitle = okTitle;
            return this;
        }

        public Builder setOkTitle(String okTitle, int color) {
            this.okTitle = okTitle;
            this.okTitleColor = color;
            return this;
        }

        public Builder setCancelTitle(String cancelTitle) {
            this.cancelTitle = cancelTitle;
            return this;
        }

        public Builder setCancelTitle(String cancelTitle, int color) {
            this.cancelTitle = cancelTitle;
            this.cancelTitleColor = color;
            return this;
        }

        public Builder setChoiceIndex(int choiceIndex) {
            this.choiceIndex = choiceIndex;
            return this;
        }

        public Builder setListItems(String[] items, String[] subItems, OnItemClickListener onItemClickListener) {
            this.items = items;
            this.subItems = subItems;
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public Builder setGridItems(String[] items, int[] images, int columnCount, OnItemClickListener onItemClickListener) {
            this.items = items;
            this.images = images;
            this.columnCount = columnCount;
            this.onItemClickListener = onItemClickListener;
            return this;
        }

        public Builder setChoice(CHOICE choice) {
            this.choice = choice;
            return this;
        }

        public Builder setCanDismiss(boolean canDismiss) {
            this.canDismiss = canDismiss;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.onCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnOKListener(OnOKListener onOKListener) {
            this.onOKListener = onOKListener;
            return this;
        }

        public Builder setCustomerView(View customerView) {
            this.customerView = customerView;
            return this;
        }

        public Builder setTimeRange(long startTime, long endTime, boolean isNeedHM) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.isNeedHM = isNeedHM;
            return this;
        }

        public Builder setTimeHour(int hour) {
            this.hour = hour;
            return this;
        }

        public Builder setTimeMinute(int minute) {
            this.minute = minute;
            return this;
        }

        public Builder setToutiaochoice(String[] topTitles, int[] topImages, String[] bottomTitles, int[] bottomImages,
                                        OnToutiaoChoiceItemClickListener onToutiaoChoiceItemClickListener) {
            this.topTitles = topTitles;
            this.topImages = topImages;
            this.bottomTitles = bottomTitles;
            this.bottomImages = bottomImages;
            this.onToutiaoChoiceItemClickListener = onToutiaoChoiceItemClickListener;
            return this;
        }

        public ActionSheetFragment show(FragmentActivity fragmentActivity) {
            ActionSheetFragment fragment = null;
            if (choice == CHOICE.ITEM) {
                fragment = ActionSheetFragment.newItemInstance(title, titleColor, cancelTitle, cancelTitleColor, items, subItems, choiceIndex);
                fragment.setOnItemClickListener(onItemClickListener);
                fragment.setOnCancelListener(onCancelListener);
            }
            if (choice == CHOICE.GRID) {
                fragment = ActionSheetFragment.newGridInstance(title, titleColor, cancelTitle, cancelTitleColor, items, images, columnCount);
                fragment.setOnItemClickListener(onItemClickListener);
            }
            if (choice == CHOICE.TOUTIAO) {
                fragment = ActionSheetFragment.newToutiaochoiceInstance(cancelTitle, cancelTitleColor, topTitles, topImages, bottomTitles, bottomImages);
                fragment.setOnToutiaoChoiceItemClickListener(onToutiaoChoiceItemClickListener);
                fragment.setOnCancelListener(onCancelListener);
            }
            if (choice == CHOICE.TIME) {
                fragment = ActionSheetFragment.newTimeInstance(title, titleColor, okTitle, okTitleColor, cancelTitle, cancelTitleColor, hour, minute);
                fragment.setOnOKListener(onOKListener);
                fragment.setOnCancelListener(onCancelListener);
            }
            if (choice == CHOICE.DATERANGE) {
                fragment = ActionSheetFragment.newDateRangeInstance(title, titleColor, okTitle, okTitleColor, cancelTitle, cancelTitleColor, startTime, endTime, isNeedHM);
                fragment.setOnOKListener(onOKListener);
                fragment.setOnCancelListener(onCancelListener);
            }
            if (choice == CHOICE.CUSTOMER) {
                fragment = ActionSheetFragment.newCustomerInstance(title, titleColor, okTitle, okTitleColor, cancelTitle, cancelTitleColor);
                fragment.setOnOKListener(onOKListener);
                fragment.setOnCancelListener(onCancelListener);
                fragment.setCustomerView(customerView);
            }
            fragment.setCanDismiss(canDismiss);
            fragment.show(fragmentActivity, tag);
            return fragment;
        }
    }
}
