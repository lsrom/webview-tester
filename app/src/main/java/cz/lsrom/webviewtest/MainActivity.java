package cz.lsrom.webviewtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.fabric.sdk.android.Fabric;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ITabChanger {
    private static final String URL_TAG = "url";
    private static final int PERMISSION_TAG_QR = 132;
    private static final int PERMISSION_TAG_MAIL = 435;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private ViewPagerAdapter adapter;
    private MenuItem menuBtn;
    private String url;

    public static Intent getStartIntent (@NonNull String url, @NonNull Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(URL_TAG, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        // don't warn about leaking URI of the log file
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this);

        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(URL_TAG)){
            url = getIntent().getStringExtra(URL_TAG);
        }

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        menuBtn = menu.getItem(0);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_bar_more:
                showPopupMenu();
                break;
            case R.id.action_bar_reload:
                reloadOrQr();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadOrQr (){
        if (menuBtn == null){
            return;
        }
        if (menuBtn.getTitle().equals(getString(R.string.action_bar_btn_add))){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSION_TAG_QR);
            } else {
                startActivity(QrScannerActivity.getStartIntent(this));
            }
        } else {
            FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(viewPager.getCurrentItem());
            fragmentToShow.reloadWebView();
        }
    }

    private void showPopupMenu (){
        View menuItemView = findViewById(R.id.action_bar_more);
        final PopupMenu popupMenu = new PopupMenu(this, menuItemView);
        popupMenu.inflate(R.menu.popup_menu);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_bar_more_send:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSION_TAG_MAIL);
                        } else {
                            ILogSender sender = (ILogSender) adapter.getItem(0);
                            sender.sendLogs();
                        }

                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_TAG_MAIL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    ILogSender sender = (ILogSender) adapter.getItem(0);
                    sender.sendLogs();
                }
                break;
            case PERMISSION_TAG_QR:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startActivity(QrScannerActivity.getStartIntent(this));
                }
                break;
        }
    }

    public String getUrl (){
        return url;
    }

    @Override
    public void showWebViewTab() {
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        if (tab == null){
            return;
        }
        tab.select();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SettingsFragment(), getString(R.string.tab_settings));
        adapter.addFragment(new WebViewFragment(), getString(R.string.tab_webview));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                FragmentLifecycle fragmentToShow = (FragmentLifecycle)adapter.getItem(position);
                fragmentToShow.onResumeFragment();

                if (fragmentToShow instanceof WebViewFragment){
                    hideKeyboard();
                    fragmentToShow.reloadWebView();
                    if (menuBtn != null){
                        menuBtn.setTitle(R.string.action_bar_btn_realod);
                    }
                } else {
                    if (menuBtn != null){
                        menuBtn.setTitle(R.string.action_bar_btn_add);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void hideKeyboard (){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
