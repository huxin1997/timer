package com.example.max.timer.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.max.timer.R;
import com.example.max.timer.TDetailPageActivity;
import com.example.max.timer.TeamTimerActivity;
import com.example.max.timer.adapter.GroupListAdapter;
import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.tool.SystemConfig;
import com.example.max.timer.tool.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupListFragment extends Fragment {

    private static final String TAG = "GroupListFragment";

    private View view;
    private RecyclerView recyclerView;
    private List<GroupBean> data = new ArrayList<>();
    private GroupListAdapter groupListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private static Context mContext;

    public GroupListFragment() {
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
        mContext= SystemConfig.getInstanceContext();
        if (groupListAdapter == null)
            groupListAdapter = new GroupListAdapter(mContext, data);
        recyclerView.setAdapter(groupListAdapter);
        groupListAdapter.setOnItemClickListener(new GroupListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e(TAG, "click");
                Intent intent = new Intent(mContext, TDetailPageActivity.class);
                intent.putExtra("bean",data.get(position));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        if (data == null)
            data = new ArrayList<>();

        @SuppressLint("HandlerLeak") final Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                notifyData();
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody=RequestBody.create(SystemConfig.JSON,"");
                Request request=new Request.Builder()
                        .url("http://118.89.22.131:8080/v1/groups/my")
                        .post(requestBody)
                        .build();
                try {
                    Response execute = SystemConfig.client.newCall(request).execute();
                    String j = execute.body().string();
                    Log.e(TAG,j);
                    JSONArray jsonArray=new JSONArray(j);
                    data.clear();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String groupName = jsonObject.getString("groupName");
                        GroupBean groupBean=new GroupBean();
                        groupBean.setId(id);
                        groupBean.setName(groupName);
                        groupBean.setHash(Tool.MD5(groupName+System.currentTimeMillis()));
                        data.add(groupBean);
                    }
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void notifyData() {
        if (groupListAdapter == null)
            groupListAdapter = new GroupListAdapter(mContext, data);
        groupListAdapter.notifyDataSetChanged();
    }

    public void addGroup(GroupBean groupBean) {
        data.add(groupBean);
        notifyData();
    }
}
