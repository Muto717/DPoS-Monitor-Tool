package com.vrlcrypt.arkmonitor.adapters.viewHolder;

import android.app.AlarmManager;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.callback.ServerSettingListDelegate;
import com.vrlcrypt.arkmonitor.adapters.viewModel.SettingViewModel;
import com.vrlcrypt.arkmonitor.databinding.ListItemServerBinding;
import com.vrlcrypt.arkmonitor.models.Server;

public class ServerSettingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ListItemServerBinding mBinding;
    private ServerSettingListDelegate delegate;

    public ServerSettingViewHolder(ViewDataBinding mBinding, ServerSettingListDelegate delegate) {
        super(mBinding.getRoot());

        this.mBinding = (ListItemServerBinding) mBinding;
        this.delegate = delegate;
    }

    public void bind (SettingViewModel viewModel) {
        mBinding.setModel(viewModel);
        mBinding.setOnClick(this);
        mBinding.setOnItemSelected(this);
    }

    @Override
    public void onClick(View v) {
        SettingViewModel viewModel = mBinding.getModel();

        switch (v.getId()) {
            case R.id.save_btn: {
                viewModel.updateServerSettings(getEnteredValue(R.id.username), getEnteredValue(R.id.fld_address), getEnteredValue(R.id.fld_public_key), getEnteredValue(R.id.ip_address, "0.0.0.0"), getStringAsInt(getEnteredValue(R.id.port)),
                        ((CheckBox) mBinding.getRoot().findViewById(R.id.ssl_enabled)).isChecked(), Server.fromId(((Spinner) mBinding.getRoot().findViewById(R.id.servers)).getSelectedItemPosition()),
                        getInterval(((Spinner) mBinding.getRoot().findViewById(R.id.notification_interval)).getSelectedItemPosition()));
                break;
            }
            case R.id.btn_delete: {
                viewModel.deleteServerSetting();
                delegate.removeServerSettingViewModel(viewModel);
                break;
            }
        }
    }

    private String getEnteredValue (int viewId) {
        return ((EditText)mBinding.getRoot().findViewById(viewId)).getText().toString();
    }

    private String getEnteredValue (int viewId, String def) {
        String str = ((EditText)mBinding.getRoot().findViewById(viewId)).getText().toString();

        if (str.equals(""))
            return def;
        else
            return str;
    }

    private long getInterval(int position) {
        switch (position) {
            case 1:
                return  AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 2:
                return AlarmManager.INTERVAL_HALF_HOUR;
            default:
                return 420000L;
        }
    }

    private int getStringAsInt (String str) {
        if (str == null || str.equals(""))
            return 4001;
        else
            return Integer.valueOf(str);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.servers: {
                if (parent.getChildCount() == position) mBinding.getModel().isCustomServer.set(true);
                else mBinding.getModel().isCustomServer.set(false);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
