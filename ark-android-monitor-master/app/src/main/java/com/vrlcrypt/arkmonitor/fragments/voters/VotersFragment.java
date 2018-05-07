package com.vrlcrypt.arkmonitor.fragments.voters;

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
import com.vrlcrypt.arkmonitor.adapters.VotersAdapter;
import com.vrlcrypt.arkmonitor.models.Account;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Voters;
import com.vrlcrypt.arkmonitor.services.ArkService;
import com.vrlcrypt.arkmonitor.services.RequestListener;
import com.vrlcrypt.arkmonitor.utils.Utils;

import static com.vrlcrypt.arkmonitor.fragments.home.HomeServerSettingFragment.ARG_SERVER_SETTING;

public class VotersFragment extends Fragment implements RequestListener<Voters> {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static VotersFragment newInstance (ServerSetting serverSetting) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SERVER_SETTING, serverSetting);

        VotersFragment homeServerSettingFragment = new VotersFragment();
        homeServerSettingFragment.setArguments(bundle);

        return homeServerSettingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_voters, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSwipeRefreshLayout = view.findViewById(R.id.voters_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);

        mSwipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        if (Utils.isOnline(getActivity())) {
            loadVoters();
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

    private void loadVoters() {
        showLoadingIndicatorView();

        if (getArguments() != null) {
            ServerSetting serverSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);

            if (Utils.validatePublicKey(serverSetting.getPublicKey())) {
                ArkService.getInstance().requestVoters(serverSetting, this);
            } else {
                loadAccount();
            }
        }
    }

    @Override
    public void onFailure(Exception e) {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());

                hideLoadingIndicatorView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResponse(final Voters voters) {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = getView();

                RecyclerView rvVoters = (RecyclerView) view.findViewById(R.id.rvVoters);

                VotersAdapter adapter = new VotersAdapter(getContext(), voters);
                rvVoters.setAdapter(adapter);
                rvVoters.setLayoutManager(new LinearLayoutManager(getActivity()));

                hideLoadingIndicatorView();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshContent() {
        ServerSetting serverSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);

        if (Utils.validatePublicKey(serverSetting.getPublicKey())) {
            ArkService.getInstance().requestVoters(serverSetting, this);
        } else {
            loadAccount();
        }
    }

    private void loadAccount() {
        ServerSetting serverSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);

        ArkService.getInstance().requestAccount(serverSetting, new RequestListener<Account>() {
            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                    }
                });
            }

            @Override
            public void onResponse(final Account account) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showLoadingIndicatorView() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.showLoadingIndicatorView();
        }
    }

    private void hideLoadingIndicatorView() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.hideLoadingIndicatorView();
        }
    }

}
