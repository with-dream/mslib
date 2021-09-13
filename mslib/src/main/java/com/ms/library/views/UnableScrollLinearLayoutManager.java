package com.ms.library.views;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 是否禁止RecycleView的滑动
 */
public class UnableScrollLinearLayoutManager extends LinearLayoutManager {
    private boolean enableVertical = true, enableHorizontal = true;

    public UnableScrollLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean canScrollHorizontally() {
        return enableHorizontal ? super.canScrollHorizontally() : false;
    }

    @Override
    public boolean canScrollVertically() {
        return enableVertical ? super.canScrollVertically() : false;
    }

    public void setEnableVertical(boolean enableVertical) {
        this.enableVertical = enableVertical;
    }

    public void setEnableHorizontal(boolean enableHorizontal) {
        this.enableHorizontal = enableHorizontal;
    }

    public void setUnable() {
        this.enableVertical = false;
        this.enableHorizontal = false;
    }
}
