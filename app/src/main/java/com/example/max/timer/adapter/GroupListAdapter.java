package com.example.max.timer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.max.timer.R;
import com.example.max.timer.bean.GroupBean;
import com.example.max.timer.fragment.GroupListFragment;

import org.w3c.dom.Text;

import java.security.acl.Group;
import java.util.List;

/**
 * Created by 贺石骞 on 2018/5/2.
 *
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupHolder> {

    private List<GroupBean> data;
    private Context context;

    public GroupListAdapter(Context context, List<GroupBean> data){
        this.context=context;
        this.data=data;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.list_items_group_item, parent, false);
        GroupHolder holder=new GroupHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        holder.groupName.setText(data.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if(data==null)
            return 0;
        return data.size();
    }

    public class GroupHolder extends RecyclerView.ViewHolder{

        private TextView groupName;

        public GroupHolder(View itemView) {
            super(itemView);
            groupName=itemView.findViewById(R.id.tv_group_name);
        }
    }



}
