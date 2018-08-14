package com.vrlcrypt.arkmonitor.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.viewHolder.ServerViewHolder;
import com.vrlcrypt.arkmonitor.adapters.viewModel.ServerViewModel;

import java.util.ArrayList;
import java.util.List;

public class MiniServerAdapter extends RecyclerView.Adapter<ServerViewHolder> {

    private List<ServerViewModel> mDataSource;

    public MiniServerAdapter() {
        this.mDataSource = new ArrayList<>();
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_server_mini, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        holder.bind(mDataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public void setData(List<ServerViewModel> dataSource) {
        this.mDataSource = dataSource;
        notifyDataSetChanged();
    }

}
