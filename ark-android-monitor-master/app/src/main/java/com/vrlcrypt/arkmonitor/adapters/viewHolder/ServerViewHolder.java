package com.vrlcrypt.arkmonitor.adapters.viewHolder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.vrlcrypt.arkmonitor.adapters.viewModel.ServerViewModel;
import com.vrlcrypt.arkmonitor.databinding.ListItemServerMiniBinding;
import com.vrlcrypt.arkmonitor.models.ServerSetting;

public class ServerViewHolder extends RecyclerView.ViewHolder {

    private ListItemServerMiniBinding mBinding;

    public ServerViewHolder(ViewDataBinding mBinding) {
        super(mBinding.getRoot());
        this.mBinding = (ListItemServerMiniBinding) mBinding;
    }

    public void bind (ServerViewModel viewModel, ServerViewDelegate delegate) {
        mBinding.setViewModel(viewModel);
        mBinding.btnDelete.setOnClickListener(v -> delegate.onDelete(viewModel.getServer()));
    }

    public interface ServerViewDelegate {
        void onDelete(ServerSetting serverSetting);
    }

}
