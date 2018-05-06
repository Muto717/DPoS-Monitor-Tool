package com.vrlcrypt.arkmonitor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.models.Account;
import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.Delegate;
import com.vrlcrypt.arkmonitor.models.Forging;
import com.vrlcrypt.arkmonitor.models.PeerVersion;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.models.Status;
import com.vrlcrypt.arkmonitor.models.Ticker;
import com.vrlcrypt.arkmonitor.services.ExchangeService;
import com.vrlcrypt.arkmonitor.services.ArkService;
import com.vrlcrypt.arkmonitor.services.RequestListener;
import com.vrlcrypt.arkmonitor.utils.Utils;

public class HomeServerSettingFragment extends Fragment {

    public static final String ARG_SERVER_SETTING = "SERVER_SETTING";

    private TextView usernameTextview;
    private TextView addressTextview;
    private TextView balanceTextview;
    private TextView balanceBtcEquivalentTextview;
    private TextView balanceUsdEquivalentTextview;
    private TextView balanceEurEquivalentTextview;
    private TextView rankTextview;
    private TextView productivityTextview;
    private TextView forgedMissedBlocksTextview;
    private TextView feesTextview;
    private TextView rewardsTextview;
    private TextView forgedTextview;
    private TextView versionTextview;
    private TextView blocksTextview;
    private TextView heightTextview;
    private TextView lastBlockForgedTextView;
    private TextView delegateApprovalTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private double balance = -1;
    private double arkBTCValue = -1;
    private double bitcoinUSDValue = -1;
    private double bitcoinEURValue = -1;

    private ServerSetting mServerSetting;

    public static HomeServerSettingFragment newInstance (ServerSetting serverSetting) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_SERVER_SETTING, serverSetting);

        HomeServerSettingFragment homeServerSettingFragment = new HomeServerSettingFragment();
        homeServerSettingFragment.setArguments(bundle);

        return homeServerSettingFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameTextview = (TextView) view.findViewById(R.id.account_username);
        addressTextview = (TextView) view.findViewById(R.id.account_address);
        balanceTextview = (TextView) view.findViewById(R.id.account_balance);
        balanceBtcEquivalentTextview = (TextView) view.findViewById(R.id.balance_btc_equivalent);
        balanceUsdEquivalentTextview = (TextView) view.findViewById(R.id.balance_usd_equivalent);
        balanceEurEquivalentTextview = (TextView) view.findViewById(R.id.balance_eur_equivalent);
        rankTextview = (TextView) view.findViewById(R.id.delegate_rank);
        productivityTextview = (TextView) view.findViewById(R.id.delegate_productivity);
        forgedMissedBlocksTextview = (TextView) view.findViewById(R.id.delegate_forged_missed_blocks);
        feesTextview = (TextView) view.findViewById(R.id.forgin_fees);
        rewardsTextview = (TextView) view.findViewById(R.id.forging_rewards);
        forgedTextview = (TextView) view.findViewById(R.id.forgin_forged);
        versionTextview = (TextView) view.findViewById(R.id.peer_version);
        blocksTextview = (TextView) view.findViewById(R.id.sync_blocks);
        heightTextview = (TextView) view.findViewById(R.id.sync_height);
        lastBlockForgedTextView = (TextView) view.findViewById(R.id.delegate_last_block_forged);
        delegateApprovalTextView = (TextView) view.findViewById(R.id.delegate_approval);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.main_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRequests();
            }
        });

        mServerSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Utils.isOnline(getActivity())) {
            showLoadingIndicatorView();
            loadRequests();
        } else {
            Utils.showMessage(getResources().getString(R.string.internet_off), getView());
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

    private void loadRequests() {
        loadDelegate();
        loadPeerVersion();
        loadStatus();
        loadLastForgedBlock();
        loadTicker();
    }

    private void loadLastForgedBlock() {
        ArkService.getInstance().requestLastBlockForged(mServerSetting, new RequestListener<Block>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading last forged block", e);

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
            public void onResponse(final Block block) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (block != null && block.getTimestamp() > 0) {
                            lastBlockForgedTextView.setText(Utils.getTimeAgo(block.getTimestamp()));
                        } else {
                            lastBlockForgedTextView.setText(R.string.not_forging);
                        }
                    }
                });
            }
        });
    }

    private void loadDelegate() {
        if (Utils.validateUsername(mServerSetting.getUsername())) {
            usernameTextview.setText(mServerSetting.getUsername());
        }

        ArkService.getInstance().requestDelegate(mServerSetting, new RequestListener<Delegate>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading delegate", e);

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
            public void onResponse(final Delegate delegate) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        String status = delegate.getRate() <= 51 ? getString(R.string.active) : getString(R.string.standby);

                        rankTextview.setText(getString(R.string.ranking_status_value,
                                String.valueOf(delegate.getRate()),
                                status));

                        String productivity = delegate.getProductivity() + getString(R.string.percent_symbol);
                        productivityTextview.setText(productivity);

                        Long producedBlocks = delegate.getProducedblocks();
                        Long missedblocks = delegate.getMissedblocks();

                        forgedMissedBlocksTextview.setText(getString(R.string.forged_missed_value,
                                String.valueOf(producedBlocks),
                                String.valueOf(missedblocks)));

                        String approval = delegate.getApproval() + getString(R.string.percent_symbol);
                        delegateApprovalTextView.setText(approval);

                        loadForging();
                        loadAccount();
                    }
                });
            }
        });
    }

    private void loadPeerVersion() {
        ArkService.getInstance().requestPeerVersion(mServerSetting, new RequestListener<PeerVersion>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading peer version", e);

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
            public void onResponse(final PeerVersion peerVersion) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        versionTextview.setText(peerVersion.getVersion());
                    }
                });
            }
        });
    }

    private void loadStatus() {
        ArkService.getInstance().requestStatus(mServerSetting, new RequestListener<Status>() {
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
            public void onResponse(final Status status) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        heightTextview.setText(String.valueOf(status.getHeight()));
                        blocksTextview.setText(String.valueOf(status.getBlocks()));
                    }
                });
            }
        });
    }

    private void loadForging() {
        ArkService.getInstance().requestForging(mServerSetting, new RequestListener<Forging>() {
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
            public void onResponse(final Forging forging) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        String fees = "";
                        String rewards = "";
                        String forged = "";

                        if (forging != null) {
                            if (forging.getFees() != null) fees = Utils.formatDecimal(forging.getFees());
                            if (forging.getRewards() != null) rewards = String.valueOf(Utils.convertToArkBase(forging.getRewards()));
                            if (forging.getForged() != null) forged = Utils.formatDecimal(forging.getForged());

                            feesTextview.setText(fees);
                            rewardsTextview.setText(rewards);
                            forgedTextview.setText(forged);
                        } else {
                            Log.e("ERR", "FORGING OBJECT NULL");
                        }
                    }
                });
            }
        });
    }

    private void loadAccount() {
        ArkService.getInstance().requestAccount(mServerSetting, new RequestListener<Account>() {
            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.balance = -1;

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

                HomeServerSettingFragment.this.balance = account.getBalance();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        addressTextview.setText(account.getAddress());
                        balanceTextview.setText(Utils.formatDecimal(HomeServerSettingFragment.this.balance));

                        calculateEquivalentInBitcoinUSDandEUR();
                    }
                });
            }
        });
    }

    private void loadTicker() {
        ExchangeService.getInstance().requestTicker(new RequestListener<Ticker>() {
            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.arkBTCValue = -1;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);
                        balanceBtcEquivalentTextview.setText(getString(R.string.undefined));

                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                    }
                });
            }

            @Override
            public void onResponse(final Ticker ticker) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.arkBTCValue = ticker.getLast();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        calculateEquivalentInBitcoinUSDandEUR();
                    }
                });
            }
        });


        ExchangeService.getInstance().requestBitcoinUSDTicker(new RequestListener<Ticker>() {
            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.bitcoinUSDValue = -1;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);
                        balanceUsdEquivalentTextview.setText(getString(R.string.undefined));

                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                    }
                });
            }

            @Override
            public void onResponse(Ticker ticker) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.bitcoinUSDValue = ticker.getLast();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        calculateEquivalentInBitcoinUSDandEUR();
                    }
                });
            }
        });


        ExchangeService.getInstance().requestBitcoinEURTicker(new RequestListener<Ticker>() {
            @Override
            public void onFailure(Exception e) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.bitcoinEURValue = -1;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);
                        balanceEurEquivalentTextview.setText(getString(R.string.undefined));

                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                    }
                });
            }

            @Override
            public void onResponse(Ticker ticker) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.bitcoinEURValue = ticker.getLast();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        calculateEquivalentInBitcoinUSDandEUR();
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

    private void calculateEquivalentInBitcoinUSDandEUR() {
        if (HomeServerSettingFragment.this != null && HomeServerSettingFragment.this.balance > 0 && HomeServerSettingFragment.this.arkBTCValue > 0) {
            double balanceBtcEquivalent = HomeServerSettingFragment.this.balance * HomeServerSettingFragment.this.arkBTCValue;
            balanceBtcEquivalentTextview.setText(Utils.formatDecimal(balanceBtcEquivalent));

            if (HomeServerSettingFragment.this.bitcoinUSDValue > 0) {
                double balanceUSDEquivalent = balanceBtcEquivalent * HomeServerSettingFragment.this.bitcoinUSDValue;
                balanceUsdEquivalentTextview.setText(Utils.formatDecimal(balanceUSDEquivalent));
            }

            if (HomeServerSettingFragment.this.bitcoinEURValue > 0) {
                double balanceUSDEquivalent = balanceBtcEquivalent * HomeServerSettingFragment.this.bitcoinEURValue;
                balanceEurEquivalentTextview.setText(Utils.formatDecimal(balanceUSDEquivalent));
            }
        }
    }

}
