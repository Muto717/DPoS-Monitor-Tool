package com.vrlcrypt.arkmonitor.fragments.info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thorcom.testapp.subscription.SubscriptionManager;
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
import com.vrlcrypt.arkmonitor.services.ExchangeServiceV2;
import com.vrlcrypt.arkmonitor.services.RequestListener;
import com.vrlcrypt.arkmonitor.utils.Utils;

import io.reactivex.functions.Consumer;

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

    public static HomeServerSettingFragment newInstance(ServerSetting serverSetting) {
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

        mSwipeRefreshLayout.setOnRefreshListener(this::loadRequests);

        mServerSetting = (ServerSetting) getArguments().getSerializable(ARG_SERVER_SETTING);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Utils.isOnline(getActivity())) {
            showLoadingIndicatorView();
            loadRequests();
        } else {
            if (HomeServerSettingFragment.this.isAdded())
                Utils.showMessage(getResources().getString(R.string.internet_off), getView());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Utils.isOnline(getActivity()))
            SubscriptionManager.getInstance().putSubscription(mServerSetting.getServerName(),
                    ((MainActivity) getActivity()).getExchangeService().btcPriceTickers().subscribe(priceTickers -> {
                        if (priceTickers.getBtcEur() != null) HomeServerSettingFragment.this.bitcoinEURValue = priceTickers.getBtcEur().getLast();
                        if (priceTickers.getBtcUsd() != null) HomeServerSettingFragment.this.bitcoinUSDValue = priceTickers.getBtcUsd().getLast();
                        if (priceTickers.getBtcUsd() != null) HomeServerSettingFragment.this.arkBTCValue = priceTickers.getBtc().getLast();

                        calculateEquivalentInBitcoinUSDandEUR();
                    }), false);
    }

    @Override
    public void onPause() {
        super.onPause();

        ArkService.getInstance().cancelCall(mServerSetting.getServerName());
        SubscriptionManager.getInstance().dispose(mServerSetting.getServerName());
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
    }

    private void loadLastForgedBlock() {
        ArkService.getInstance().requestLastBlockForged(mServerSetting, new RequestListener<Block>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading last forged block", e);

                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });
            }

            @Override
            public void onResponse(final Block block) {
                if (!isAdded()) {
                    return;
                }

                final CharSequence timeAgo = (block != null && block.getTimestamp() > 0) ? Utils.getTimeAgo(block.getTimestamp()) : getString(R.string.not_forging);

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();

                    mSwipeRefreshLayout.setRefreshing(false);
                    lastBlockForgedTextView.setText(timeAgo);
                });
            }
        });
    }

    private void loadDelegate() {
        if (Utils.validateUsername(mServerSetting.getServerName())) {
            usernameTextview.setText(mServerSetting.getServerName());
        }

        ArkService.getInstance().requestDelegate(mServerSetting.getServerName(), mServerSetting, new RequestListener<Delegate>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading delegate", e);

                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });

                loadForging();
                loadAccount();
                loadDelegate();
            }

            @Override
            public void onResponse(final Delegate delegate) {
                if (!isAdded()) {
                    return;
                }

                String status = delegate.getRate() <= 51 ? getString(R.string.active) : getString(R.string.standby);
                String productivity = delegate.getProductivity() + getString(R.string.percent_symbol);
                Long producedBlocks = delegate.getProducedblocks();
                Long missedblocks = delegate.getMissedblocks();
                String approval = delegate.getApproval() + getString(R.string.percent_symbol);

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);

                    rankTextview.setText(getString(R.string.ranking_status_value,
                            String.valueOf(delegate.getRate()),
                            status));

                    productivityTextview.setText(productivity);

                    forgedMissedBlocksTextview.setText(getString(R.string.forged_missed_value,
                            String.valueOf(producedBlocks),
                            String.valueOf(missedblocks)));

                    delegateApprovalTextView.setText(approval);
                });

                loadForging();
                loadAccount();
            }
        });
    }

    private void loadPeerVersion() {
        ArkService.getInstance().requestPeerVersion(mServerSetting.getServerName(), mServerSetting, new RequestListener<PeerVersion>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR", "Error loading peer version", e);

                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });
            }

            @Override
            public void onResponse(final PeerVersion peerVersion) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    versionTextview.setText(peerVersion.getVersion());
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

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });
            }

            @Override
            public void onResponse(final Status status) {
                if (!isAdded()) {
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);

                    heightTextview.setText(String.valueOf(status.getHeight()));
                    blocksTextview.setText(String.valueOf(status.getBlocks()));
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

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });
            }

            @Override
            public void onResponse(final Forging forging) {
                if (!isAdded()) {
                    return;
                }

                String fees = null;
                String rewards = null;
                String forged = null;

                if (forging != null) {
                    if (forging.getFees() != null) fees = Utils.formatDecimal(forging.getFees());
                    if (forging.getRewards() != null)
                        rewards = String.valueOf(Utils.convertToArkBase(forging.getRewards()));
                    if (forging.getForged() != null)
                        forged = Utils.formatDecimal(forging.getForged());
                } else {
                    Log.e("ERROR", "FORGING OBJECT NULL");
                }

                String finalFees = fees;
                String finalRewards = rewards;
                String finalForged = forged;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        hideLoadingIndicatorView();
                        mSwipeRefreshLayout.setRefreshing(false);

                        feesTextview.setText(finalFees);
                        rewardsTextview.setText(finalRewards);
                        forgedTextview.setText(finalForged);
                    });
                }
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

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);

                    if (HomeServerSettingFragment.this.isAdded())
                        Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());
                });
            }

            @Override
            public void onResponse(final Account account) {
                if (!isAdded()) {
                    return;
                }

                HomeServerSettingFragment.this.balance = account.getBalance();
                String balance = Utils.formatDecimal(HomeServerSettingFragment.this.balance);

                getActivity().runOnUiThread(() -> {
                    hideLoadingIndicatorView();
                    mSwipeRefreshLayout.setRefreshing(false);

                    addressTextview.setText(account.getAddress());
                    balanceTextview.setText(balance);
                });

                calculateEquivalentInBitcoinUSDandEUR();
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
        if (HomeServerSettingFragment.this.balance > 0 && HomeServerSettingFragment.this.arkBTCValue > 0) {
            double balanceBtcEquivalent = HomeServerSettingFragment.this.balance * HomeServerSettingFragment.this.arkBTCValue;

            double balanceUSDEquivalent = -1;
            double balanceEurEquivalent = -1;

            if (HomeServerSettingFragment.this.bitcoinUSDValue > 0) {
                balanceUSDEquivalent = balanceBtcEquivalent * HomeServerSettingFragment.this.bitcoinUSDValue;
            } else if (HomeServerSettingFragment.this.bitcoinEURValue > 0) {
                balanceEurEquivalent = balanceBtcEquivalent * HomeServerSettingFragment.this.bitcoinEURValue;
            }

            String btcEquivalent = Utils.formatDecimal(balanceBtcEquivalent);
            String usdEquivalent = Utils.formatDecimal(balanceUSDEquivalent);
            String eurEquivalent = Utils.formatDecimal(balanceEurEquivalent);

            getActivity().runOnUiThread(() -> {
                balanceBtcEquivalentTextview.setText(btcEquivalent);

                if (!eurEquivalent.equals("-1"))
                    balanceEurEquivalentTextview.setText(eurEquivalent);

                if (!usdEquivalent.equals("-1"))
                    balanceUsdEquivalentTextview.setText(usdEquivalent);
            });
        }
    }

    public String getTitle() {
        if (mServerSetting != null)
            return mServerSetting.getServerName();
        else return "";
    }


}
