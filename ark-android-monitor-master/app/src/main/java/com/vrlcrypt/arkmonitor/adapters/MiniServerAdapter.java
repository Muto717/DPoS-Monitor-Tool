package com.vrlcrypt.arkmonitor.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.viewHolder.ServerViewHolder;
import com.vrlcrypt.arkmonitor.adapters.viewModel.ServerViewModel;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

import java.util.ArrayList;
import java.util.List;

public class MiniServerAdapter extends RecyclerView.Adapter<ServerViewHolder> {

    private ServerViewHolder.ServerViewDelegate delegate;

    private List<ServerViewModel> mDataSource;

    public MiniServerAdapter(ServerViewHolder.ServerViewDelegate delegate) {
        this.mDataSource = new ArrayList<>();
        this.delegate = delegate;
    }

    @Override
    public ServerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ServerViewHolder(
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_item_server_mini, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ServerViewHolder holder, int position) {
        holder.bind(mDataSource.get(position), delegate);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public void setData(List<ServerViewModel> dataSource) {
        this.mDataSource = dataSource;
        notifyDataSetChanged();
    }

    public void updateStatus(Pair<Integer, Integer> status) {
        for (ServerViewModel viewModel : mDataSource) {
            if (status.first.equals(viewModel.getServerUID())) {
                viewModel.setCurrentStatus(status.second);
            }
        }
    }

    public void remove(ServerSetting serverSetting) {
        for (ServerViewModel viewModel : mDataSource) {
            if (viewModel.getServerUID() == serverSetting.getUId()) {
                int index = mDataSource.indexOf(viewModel);
                mDataSource.remove(viewModel);
                notifyItemRemoved(index);
                return;
            }
        }
    }

}
