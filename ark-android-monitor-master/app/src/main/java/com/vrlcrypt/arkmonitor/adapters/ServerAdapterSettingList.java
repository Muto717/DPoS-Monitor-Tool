package com.vrlcrypt.arkmonitor.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.callback.ServerSettingListDelegate;
import com.vrlcrypt.arkmonitor.adapters.viewHolder.ServerSettingViewHolder;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;

import java.util.ArrayList;
import java.util.List;

public class ServerAdapterSettingList extends RecyclerView.Adapter<ServerSettingViewHolder> implements ServerSettingListDelegate {

    private List<SettingViewModel> mDataSource;

    public ServerAdapterSettingList() {
        this.mDataSource = new ArrayList<>();
    }

    @Override
    public ServerSettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerSettingViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_server, parent, false),
                this
        );
    }

    @Override
    public void onBindViewHolder(ServerSettingViewHolder holder, int position) {
        holder.bind(mDataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public void insertNew(SettingViewModel viewModel) {
        mDataSource.add(0, viewModel);
        notifyItemInserted(0);
    }

    public List<SettingViewModel> getDataSource() {
        return mDataSource;
    }

    @Override
    public void removeServerSettingViewModel(SettingViewModel viewModel) {
        int index = mDataSource.indexOf(viewModel);

        if (index != 1) {
            mDataSource.remove(index);
            notifyItemRemoved(index);
        }
    }

}
