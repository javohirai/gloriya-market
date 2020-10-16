package com.kashtansystem.project.gloriyamarketing.activity.warehousemanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.CanceledOrdersForReceivingAdapter;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageDetailTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 05.12.2017.
 * ----------------------------------
 * Отменённые заказы, которые экспедитор должен будет вернуть обратно на склад, а зав.склада
 * должен подтвердить возврат указанного количества товара.
 */

public class CanceledOrdersForReceivingActivity extends BaseActivity implements View.OnClickListener,
    OnDialogBtnsClickListener
{
    private CanceledOrdersForReceivingAdapter adapter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.label_canceled_orders);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trading_points);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));

        adapter = new CanceledOrdersForReceivingAdapter(this, this);
        ((ExpandableListView) findViewById(R.id.elvTPoints)).setAdapter(adapter);

        // test begin
        List<PackageTemplate> items = new ArrayList<>(1);
        PackageTemplate packageTemplate = new PackageTemplate();
        packageTemplate.setDeliveryDate("05.12.2017");
        packageTemplate.setCustomer("OOO Micro");
        packageTemplate.setCapacity(7.5);
        packageTemplate.setWeight(12.5);
        packageTemplate.setPackerName("Холтураве Мурад");

            List<PackageDetailTemplate> childItems = new ArrayList<>(1);
            PackageDetailTemplate packageDetailTemplate = new PackageDetailTemplate();
            packageDetailTemplate.setVendorCode("T700-4563-801");
            packageDetailTemplate.setName("Test");
            packageDetailTemplate.setMeasure("------");
            packageDetailTemplate.setCount(5);
            packageDetailTemplate.setWeight(0.75);
            packageDetailTemplate.setCapacity(0.1);
            childItems.add(packageDetailTemplate);

        packageTemplate.setDetails(childItems);
        items.add(packageTemplate);
        adapter.setItems(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.forwarder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.mRefresh:
            break;
            case R.id.mLogout:
                DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
                    getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit),
                    new Intent(this, LoginActivity.class), this);
            break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        L.info("Group Id: " + view.getTag());
    }

    @Override
    public void onBtnClick(View view, Intent intent)
    {
        if (view.getId() == R.id.dialogBtn2)
        {
            AppCache.USER_INFO = new UserTemplate();
            if (intent != null)
                startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed()
    {
        DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
            getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit), null, this);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            return null;
        }
    }
}