package com.vrlcrypt.arkmonitor.fragments.block;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vrlcrypt.arkmonitor.MainActivity;
import com.vrlcrypt.arkmonitor.R;
import com.vrlcrypt.arkmonitor.adapters.BlocksAdapter;
import com.vrlcrypt.arkmonitor.fragments.info.HomeServerSettingFragment;
import com.vrlcrypt.arkmonitor.models.Block;
import com.vrlcrypt.arkmonitor.models.ServerSetting;
import com.vrlcrypt.arkmonitor.services.ArkService;
import com.vrlcrypt.arkmonitor.services.RequestListener;
import com.vrlcrypt.arkmonitor.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BlocksFragment extends Fragment implements RequestListener<List<Block>>{

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<Block> mBlocks = new ArrayList<>();
    private BlocksAdapter mBlocksAdapter;

    public static BlocksFragment newInstance (ServerSetting serverSetting) {
        BlocksFragment blocksFragment = new BlocksFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(HomeServerSettingFragment.ARG_SERVER_SETTING, serverSetting);

        blocksFragment.setArguments(bundle);
        return blocksFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBlocksAdapter = new BlocksAdapter(getContext(), mBlocks);

        RecyclerView rvBlocks = (RecyclerView) view.findViewById(R.id.rvBlocks);

        rvBlocks.setAdapter(mBlocksAdapter);
        rvBlocks.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.blocks_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(() -> refreshContent());

        if (Utils.isOnline(getActivity())) {
            loadBlocs();
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

    private void loadBlocs() {
        MainActivity activity = (MainActivity) getActivity();

        if (activity != null) {
            activity.showLoadingIndicatorView();
        }

        refreshContent();
    }

    @Override
    public void onFailure(final Exception e) {
        if (!isAdded()) {
            return;
        }

        getActivity().runOnUiThread(() -> {
            Utils.showMessage(getString(R.string.unable_to_retrieve_data), getView());

            mSwipeRefreshLayout.setRefreshing(false);

            MainActivity activity = (MainActivity)getActivity();
            if (activity != null) {
                activity.hideLoadingIndicatorView();
            }
        });
    }

    @Override
    public void onResponse(final List<Block> blocks) {
        if (!isAdded()) {
            return;
        }

        mBlocks.addAll(blocks);

        getActivity().runOnUiThread(() -> {
            mBlocksAdapter.setBlocks(mBlocks);
            mBlocksAdapter.notifyDataSetChanged();

            MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.hideLoadingIndicatorView();
            }

            mSwipeRefreshLayout.setRefreshing(false);
        });
    }

    private void refreshContent() {
        mBlocks.clear();
        ArkService.getInstance().requestBlocks((ServerSetting) getArguments().getSerializable(HomeServerSettingFragment.ARG_SERVER_SETTING), this);
    }

}
