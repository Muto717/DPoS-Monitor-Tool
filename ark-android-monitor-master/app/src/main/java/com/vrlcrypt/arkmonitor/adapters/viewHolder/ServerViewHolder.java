package com.vrlcrypt.arkmonitor.adapters.viewHolder;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import com.vrlcrypt.arkmonitor.adapters.viewModel.ServerViewModel;
import com.vrlcrypt.arkmonitor.databinding.ListItemServerMiniBinding;

public class ServerViewHolder extends RecyclerView.ViewHolder {

    private ListItemServerMiniBinding mBinding;

    public ServerViewHolder(ViewDataBinding mBinding) {
        super(mBinding.getRoot());

        this.mBinding = (ListItemServerMiniBinding) mBinding;
    }

    public void bind (ServerViewModel viewModel) {
        mBinding.setViewModel(
                viewModel
        );
    }

}
