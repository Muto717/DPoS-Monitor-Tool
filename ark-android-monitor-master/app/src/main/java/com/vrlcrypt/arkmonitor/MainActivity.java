package com.vrlcrypt.arkmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vrlcrypt.arkmonitor.fragments.block.BlocksContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.delegates.DelegateContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.info.HomeContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.peers.PeersContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.SettingsV2Fragment;
import com.vrlcrypt.arkmonitor.fragments.voters.VoterContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.votes.VotesContainerFragment;
import com.vrlcrypt.arkmonitor.fragments.transactions.TransactionsContainerFragment;
import com.vrlcrypt.arkmonitor.scheduler.ForgingAlarmReceiver;
import com.vrlcrypt.arkmonitor.services.DelegateStatusPool;
import com.vrlcrypt.arkmonitor.services.ExchangeServiceV2;
import com.vrlcrypt.arkmonitor.utils.Utils;
import com.wang.avi.AVLoadingIndicatorView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final ForgingAlarmReceiver mAlarm = new ForgingAlarmReceiver();
    private AVLoadingIndicatorView mLoadingIndicatorView;
    private ActionBarDrawerToggle mToggle;
    private Menu mMenu;

    private ExchangeServiceV2 exchangeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, StatusService.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        exchangeService = new ExchangeServiceV2();

        mLoadingIndicatorView = findViewById(R.id.loadingIndicator);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        onNavigationItemSelected(navigationView.getMenu().getItem(NavItem.HOME.getIndex()));
    }

    public void showLoadingIndicatorView() {
        runOnUiThread(() -> mLoadingIndicatorView.setVisibility(View.VISIBLE));
    }

    public void hideLoadingIndicatorView() {
        runOnUiThread(() -> mLoadingIndicatorView.setVisibility(View.GONE));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_alarm) {

            boolean alarmEnabled = Utils.alarmEnabled(this);
            if (Utils.enableAlarm(this, !alarmEnabled)) {

                MenuItem menuItem = mMenu.findItem(R.id.action_alarm);

                alarmEnabled = Utils.alarmEnabled(this);

                if (alarmEnabled) {
                    menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_alarm_on_white_24dp));
                    mAlarm.setAlarm(this);
                } else {
                    menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_alarm_off_white_24dp));
                    mAlarm.cancelAlarm(this);
                }
            }

            View view = this.findViewById(android.R.id.content);

            int stringMessageId = alarmEnabled ? R.string.alarm_on : R.string.alarm_off;
            Utils.showMessage(getResources().getString(stringMessageId), view);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_info) {
            fragment = new HomeContainerFragment();
            setTitle("Information");
        } else if (id == R.id.nav_forged_blocks) {
            fragment = new BlocksContainerFragment();
            setTitle(R.string.nav_forged_blocks);
        } else if (id == R.id.nav_latest_transactions) {
            fragment = new TransactionsContainerFragment();
            setTitle(R.string.nav_latest_transactions);
        } else if (id == R.id.nav_peers) {
            fragment = new PeersContainerFragment();
            setTitle(R.string.nav_peers);
        } else if (id == R.id.nav_delegates) {
            fragment = new DelegateContainerFragment();
            setTitle(R.string.nav_delegates);
        } else if (id == R.id.nav_home) {
            fragment = new SettingsV2Fragment();
            setTitle(R.string.nav_settings);
        } else if (id == R.id.nav_votes_made) {
            fragment = new VotesContainerFragment();
            setTitle(R.string.nav_votes_made);
        } else if (id == R.id.nav_votes_received) {
            fragment = new VoterContainerFragment();
            setTitle(R.string.nav_votes_received);
        }

        if (fragment != null) {
            showFragment(fragment);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();

        fragmentTransaction
                .replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void selectMenuItem (int menuItem) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(menuItem);
        onNavigationItemSelected(navigationView.getMenu().findItem(menuItem));
    }

    public ExchangeServiceV2 getExchangeService() {
        return exchangeService;
    }

    enum NavItem {
        HOME(0),
        LATEST_BLOCKS(1),
        LATEST_TRANSACTIONS(2),
        PEERS(3),
        DELEGATES(4),
        VOTES(5),
        VOTERS(6),
        SETTINGS(7);

        private final int index;

        NavItem(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

}