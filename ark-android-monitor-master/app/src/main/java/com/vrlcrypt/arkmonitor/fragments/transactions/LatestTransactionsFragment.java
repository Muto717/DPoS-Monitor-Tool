package com.vrlcrypt.arkmonitor.fragments.transactions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.adapters.TransactionAdapter;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Transaction;
import com.vrlcrypt.arkmonitor.services.ArkService;
import com.vrlcrypt.arkmonitor.services.RequestListener;
import com.vrlcrypt.arkmonitor.utils.Utils;

import java.util.List;

import static com.vrlcrypt.arkmonitor.fragments.info.HomeServerSettingFragment.ARG_SERVER_SETTING;

public class LatestTransactionsFragment extends Fragment implements RequestListener<List<Transaction>> {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static LatestTransactionsFragment newInstance (ServerSetting serverSetting) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SERVER_SETTING, serverSetting);

        LatestTransactionsFragment homeServerSettingFragment = new LatestTransactionsFragment();
        homeServerSettingFragment.setArguments(bundle);

        return homeServerSettingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_transactions, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = view.findViewById(R.id.latest_transactions_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        if (Utils.isOnline(getActivity())) {
            loadTransactions();
        } else {
            Utils.showMessage(getResources().getString(R.string.internet_off), view);
        }
    }


    @Override
    public void onDestroy() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.hideLoadingIndicatorView();
        }
        super.onDestroy();
    }

    private void loadTransactions() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.showLoadingIndicatorView();
        }

        refreshContent();
    }

    private void refreshContent() {
        ServerSetting serverSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);
        ArkService.getInstance().requestLatestTransactions(serverSetting, this);
    }

    @Override
    public void onFailure(final Exception e) {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());

                mSwipeRefreshLayout.setRefreshing(false);

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.hideLoadingIndicatorView();
                }
            }
        });
    }

    @Override
    public void onResponse(final List<Transaction> transactions) {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getView();

                RecyclerView rvTransactions = (RecyclerView) view.findViewById(R.id.rvLatestTransactions);

                TransactionAdapter adapter = new TransactionAdapter(getContext(), transactions);
                rvTransactions.setAdapter(adapter);
                rvTransactions.setLayoutManager(new LinearLayoutManager(getActivity()));

                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.hideLoadingIndicatorView();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

}
