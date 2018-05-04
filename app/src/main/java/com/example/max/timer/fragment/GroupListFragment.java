package com.example.max.timer.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.max.timer.R;
import com.example.max.timer.TDetailPageActivity;
import com.example.max.timer.adapter.GroupListAdapter;
import com.example.max.timer.bean.GroupBean;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment {

    private static final String TAG = "GroupListFragment";

    private View view;
    private RecyclerView recyclerView;
    private List<GroupBean> data;
    private GroupListAdapter groupListAdapter;
    private LinearLayoutManager linearLayoutManager;

    public GroupListFragment() {
        data = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_group_list, container, false);
        recyclerView = view.findViewById(R.id.group_list_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        if (groupListAdapter == null)
            groupListAdapter = new GroupListAdapter(getContext(), data);
        recyclerView.setAdapter(groupListAdapter);
        groupListAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(TAG,"click");
                startActivity(new Intent(getActivity().getApplicationContext(), TDetailPageActivity.class));
            }
        });
    }

    private void initData() {
        if(data==null)
            data = new ArrayList<>();

    }

    public void notifyData() {
        groupListAdapter.notifyDataSetChanged();
    }

    public void addGroup(GroupBean groupBean) {
        data.add(groupBean);
        notifyData();
    }
}
