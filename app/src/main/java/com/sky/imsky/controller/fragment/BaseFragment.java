package com.sky.imsky.controller.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/1/18 0018.
 */

public abstract class BaseFragment extends Fragment {

    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(getResource(), container, false);
        initView(view);
        return view;
    }

    /**
     * 初始化资源文件
     *
     * @return
     */
    protected abstract int getResource();

    /**
     * 初始化组件
     *
     * @param view
     */
    protected abstract void initView(View view);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 加载数据
     */
    protected void initData() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startDestroy();
        System.gc();
    }

    /**
     * 销毁数据,释放内存
     */
    protected abstract void startDestroy();
}
