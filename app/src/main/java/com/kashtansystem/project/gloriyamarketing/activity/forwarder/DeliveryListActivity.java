package com.kashtansystem.project.gloriyamarketing.activity.forwarder;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.DeliveryElvAdapter;
import com.kashtansystem.project.gloriyamarketing.core.FilterDateType;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqCollectCash;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqCompleteShippingCancel;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqCompleteShippingDelivered;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqReasonsToReturn;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqShippingInTransit;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqShippingList;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by FlameKaf on 19.06.2017.
 * ----------------------------------
 * Форма доставок экспедитора
 */

public class DeliveryListActivity extends BaseActivity implements View.OnClickListener,
        OnDialogBtnsClickListener {

    public static int WAITING = 0;
    public static int COMPLETE = 1;
    public static int CANCELED = 3;

    public static long DATE_FROM = -1;
    public static long DATE_TO = -1;
    public static int filteredStatus = 0;

    public static ArrayList<DeliveryTemplate> filteredDeliveryList;
    public static ArrayList<DeliveryTemplate> allDeliveryList;
    private String[] reasonsToReturn;

    private int groupId = 0;
    private DeliveryElvAdapter adapter;
    private double TotalTakenSum = 0;

    private final byte ON_THE_WAY = 0;
    private final byte COMPLETE_DELIVERY = 1;
    private final byte TAKE_CASH_CONFIRM = 2;

    SimpleDateFormat dateFormat;

    @Override
    public String getActionBarTitle() {
        return getString(R.string.app_title_delivery_list);
    }

    @Override
    public boolean getHomeButtonEnable() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_list);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        adapter = new DeliveryElvAdapter(this, this);
        ((ExpandableListView) findViewById(R.id.elvDeliveryList)).setAdapter(adapter);
        ( findViewById(R.id.llFilter)).setVisibility(View.VISIBLE);
        ((AppCompatSpinner) findViewById(R.id.spStatus)).setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_item_tv, new String[]{"Ожидание", "Доставлено", "Отменен"}));
        ((AppCompatSpinner) findViewById(R.id.spDateType)).setAdapter(new ArrayAdapter<>(this,
                R.layout.spinner_item_tv, FilterDateType.Companion.getAllTypesName(this)));
        ((AppCompatSpinner) findViewById(R.id.spStatus)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        filteredStatus = WAITING;
                        break;
                    case 1:
                        filteredStatus = COMPLETE;
                        break;
                    case 2:
                        filteredStatus = CANCELED;
                        break;
                }
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ((AppCompatSpinner) findViewById(R.id.spDateType)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Calendar calendar = (Calendar) Calendar.getInstance();
                ((LinearLayout) findViewById(R.id.llPickInterval)).setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        DATE_FROM = -1;
                        DATE_TO = -1;
                        break;
                    case 1:
                        toBeginDay(calendar);
                        DATE_FROM = calendar.getTimeInMillis();
                        toEndDay(calendar);
                        DATE_TO = calendar.getTimeInMillis();
                        break;
                    case 2:
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        toBeginDay(calendar);
                        DATE_FROM = calendar.getTimeInMillis();
                        toEndDay(calendar);
                        DATE_TO = calendar.getTimeInMillis();
                        break;
                    case 3:
                        calendar.add(Calendar.WEEK_OF_YEAR, -1);
                        toBeginDay(calendar);
                        DATE_FROM = calendar.getTimeInMillis();
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        toEndDay(calendar);
                        DATE_TO = calendar.getTimeInMillis();
                        break;
                    case 4:
                        calendar.add(Calendar.MONTH, -1);
                        toBeginDay(calendar);
                        DATE_FROM = calendar.getTimeInMillis();
                        calendar.add(Calendar.MONTH, 1);
                        toEndDay(calendar);
                        DATE_TO = calendar.getTimeInMillis();
                        break;
                    case 5:
                        ((LinearLayout) findViewById(R.id.llPickInterval)).setVisibility(View.VISIBLE);
                        DATE_FROM = -1;
                        DATE_TO = -1;
                        ((EditText)findViewById(R.id.etFromDate)).setText(dateFormat.format(calendar.getTime()));
                        ((EditText)findViewById(R.id.etToDate)).setText(dateFormat.format(calendar.getTime()));

                        break;
                }
                filter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        findViewById(R.id.etFromDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                if(DATE_FROM != -1)
                    date.setTime(DATE_FROM);
                calendar.setTime(date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DeliveryListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        toBeginDay(calendar);
                        DATE_FROM = calendar.getTimeInMillis();
                        ((EditText)findViewById(R.id.etFromDate)).setText(dateFormat.format(calendar.getTime()));
                        filter();
                        adapter.notifyDataSetChanged();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_YEAR));
                datePickerDialog.show();
            }
        });
        findViewById(R.id.etToDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                if(DATE_TO != -1)
                    date.setTime(DATE_TO);
                calendar.setTime(date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        DeliveryListActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        toEndDay(calendar);
                        DATE_TO = calendar.getTimeInMillis();
                        ((EditText)findViewById(R.id.etToDate)).setText(dateFormat.format(calendar.getTime()));
                        filter();
                        adapter.notifyDataSetChanged();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_YEAR));
                datePickerDialog.show();
            }
        });
        new Init().execute();
    }

    private void toBeginDay(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void toEndDay(Calendar calendar) {
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
    }

    public void filter() {
        ArrayList<DeliveryTemplate> resultList = new ArrayList<>();
        if (allDeliveryList != null)
            for (DeliveryTemplate deliveryTemplate : allDeliveryList) {
                if (deliveryTemplate.getStatus() == filteredStatus) {
                    if (DATE_FROM != -1 && deliveryTemplate.getDeliveryDateLong() < DATE_FROM)
                        continue;
                    if (DATE_TO != -1 && deliveryTemplate.getDeliveryDateLong() > DATE_TO)
                        continue;
                    resultList.add(deliveryTemplate);
                }
            }
        filteredDeliveryList = resultList;
        adapter.notifyDataSetChanged();
    }

    public void clearFilter() {
        filteredDeliveryList = allDeliveryList;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forwarder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.mRefresh:
                new Init().execute();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == C.REQUEST_CODES.TAKE_CASH) {
            if (resultCode == RESULT_OK)
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        int groupId = (Integer) view.getTag();
        switch (view.getId()) {
            case R.id.ivShowDirection:
                if (isGPStatusOK()) {
                    this.groupId = groupId;
                    geoLocationPermission();
                }
                break;
            case R.id.elvDLChildAgent:
                makeCallAction(filteredDeliveryList.get(groupId).getAgentPhone());
                break;
            case R.id.elvDLChildContPerson:
                makeCallAction(filteredDeliveryList.get(groupId).getContactPersonPhone());
                break;
            case R.id.btnDLGoods:
                Intent iGoodsList = new Intent(this, DeliveryGoodsListActivity.class);
                iGoodsList.putExtra(C.KEYS.EXTRA_DATA_ID, groupId);
                startActivity(iGoodsList);
                break;
            case R.id.btnDLOnTheWay:
                new DoSomeAction(ON_THE_WAY, groupId).execute();
                break;
            case R.id.btnDLComplete:
                completeDelivery(groupId);
                break;
            case R.id.btnDLTakeCash:
                new DoSomeAction(TAKE_CASH_CONFIRM, groupId).execute();
                break;
        }
    }

    @Override
    public void onBtnClick(View view, Intent intent) {
        if (view.getId() == R.id.dialogBtn2) {
            AppCache.USER_INFO = new UserTemplate();
            if (filteredDeliveryList != null) {
                filteredDeliveryList.clear();
                filteredDeliveryList = null;
            }
            if (intent != null)
                startActivity(intent);
            finish();
        }
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result) {
        if (result && permission == AppPermissions.GeoLocation) {
            Intent iDirection = new Intent(this, DirectionActivity.class);
            iDirection.putExtra(C.KEYS.EXTRA_DATA_ID, this.groupId);
            startActivity(iDirection);
        }
    }

    /**
     * Выполнение звонка
     */
    private void makeCallAction(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum))
            Toast.makeText(this, "Номер телефона не указан", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Завершение доставки товара
     */
    private void completeDelivery(final int groupId) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_action_finish_delivering);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        final DeliveryTemplate deliveryTemplate = filteredDeliveryList.get(groupId);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reasonsToReturn);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner reasons = (AppCompatSpinner) dialog.findViewById(R.id.sReasons);
        reasons.setAdapter(adapter);

        reasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deliveryTemplate.setReason(reasonsToReturn[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (view.getId() == R.id.dialogBtn2)
                    new DoSomeAction(COMPLETE_DELIVERY, groupId).execute();
            }
        };

        RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) group.findViewById(group.getCheckedRadioButtonId());
                L.info(radioButton.getTag().toString());
                switch (group.getCheckedRadioButtonId()) {
                    case R.id.rbDelivered:
                        deliveryTemplate.setAction(1);
                        dialog.findViewById(R.id.etReason).setEnabled(false);
                        if (!deliveryTemplate.getGoodsForReturn().isEmpty())
                            deliveryTemplate.getGoodsForReturn().clear();
                        break;
                    case R.id.rbPartDelivery:
                        deliveryTemplate.setAction(2);
                        dialog.findViewById(R.id.etReason).setEnabled(true);
                        Intent intent = new Intent(DeliveryListActivity.this, SelectGoodsToReturn.class);
                        intent.putExtra(C.KEYS.EXTRA_DATA_ID, groupId);
                        startActivity(intent);
                        break;
                    case R.id.rbCancelDelivery:
                        deliveryTemplate.setAction(3);
                        dialog.findViewById(R.id.etReason).setEnabled(true);
                        if (!deliveryTemplate.getGoodsForReturn().isEmpty())
                            deliveryTemplate.getGoodsForReturn().clear();
                        break;
                }
            }
        };

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                L.info("onTextChanged " + text);
                deliveryTemplate.setReason(text.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        dialog.findViewById(R.id.dialogBtn1).setOnClickListener(listener);
        dialog.findViewById(R.id.dialogBtn2).setOnClickListener(listener);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.orderRGroup);
        radioGroup.setOnCheckedChangeListener(checkedChangeListener);
        ((EditText) dialog.findViewById(R.id.etReason)).addTextChangedListener(textWatcher);

        dialog.show();
    }

    /**
     * Загрузка данных экспедитора
     */
    @SuppressLint("StaticFieldLeak")
    private class Init extends AsyncTask<Void, Void, Double> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(DeliveryListActivity.this, getString(R.string.dialog_text_load_data));
            dialog.show();
        }

        @Override
        protected Double doInBackground(Void... params) {
            reasonsToReturn = ReqReasonsToReturn.load();
            return ReqShippingList.load();
        }

        @Override
        protected void onPostExecute(Double result) {
            dialog.cancel();
            TotalTakenSum = result;
            ((TextView) findViewById(R.id.tvFrdTotalTakenSum)).setText(String.format("%s\n%s",
                    getString(R.string.label_total_taken_sum), Util.getParsedPrice(TotalTakenSum)));
            filter();
            adapter.notifyDataSetChanged();
        }
    }
    /**
     * status = 1     - dostavlenno
     * status = 3     - otmenen
     * status = other - ojedaniya
     * */
    /**
     * Выполняет те или иные действия экспедитора: оповещение о доставке, завершение доставки и
     * сбор денежных средств.
     */
    @SuppressLint("StaticFieldLeak")
    private class DoSomeAction extends AsyncTask<Void, Void, String[]> {
        private byte action = ON_THE_WAY;
        private int groupId;
        private Dialog progress;

        DoSomeAction(byte action, int groupId) {
            this.action = action;
            this.groupId = groupId;
        }

        @Override
        protected void onPreExecute() {
            progress = getInformDialog(DeliveryListActivity.this, getString(R.string.dialog_text_sending_data));
            progress.show();
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            final DeliveryTemplate deliveryTemplate = filteredDeliveryList.get(groupId);
            switch (action) {
                case ON_THE_WAY:
                    return ReqShippingInTransit.send(deliveryTemplate.getTpCode());
                case COMPLETE_DELIVERY: {
                    String[] res;
                    switch (deliveryTemplate.getAction()) {
                        case 1:
                            res = ReqCompleteShippingDelivered.send(deliveryTemplate.getTpCode(),
                                    deliveryTemplate.getOrderCode(), deliveryTemplate.getOrderCreatedDate());
                            if (res[0].equals("0"))
                                deliveryTemplate.setStatus(1);
                            break;
                        /*case 2:
                            res = ReqCompleteShippingPartiallyDelivered.send(deliveryTemplate);
                            if (res[0].equals("0"))
                                deliveryTemplate.setStatus(3);
                        break;*/
                        case 3:
                            res = ReqCompleteShippingCancel.send(deliveryTemplate.getTpCode(), deliveryTemplate.getReason(),
                                    deliveryTemplate.getOrderCode(), deliveryTemplate.getOrderCreatedDate());
                            if (res[0].equals("0")) {
                                deliveryTemplate.setIsNeedTakeCash(false);
                                deliveryTemplate.setStatus(3);
                            }
                            break;
                        default:
                            res = new String[]{"-1", "Не выбран результат доставки"};
                            break;
                    }
                    return res;
                }
                case TAKE_CASH_CONFIRM: {
                    TotalTakenSum += deliveryTemplate.getCashToTake();
                    String[] res = ReqCollectCash.send(deliveryTemplate.getTpCode(), deliveryTemplate.getCashToTake());
                    if (res[0].equals("0"))
                        deliveryTemplate.setIsNeedTakeCash(false);
                    return res;
                }
            }
            return new String[]{"-1", "No Data to Send"};
        }

        @Override
        protected void onPostExecute(String[] result) {
            progress.cancel();
            if (result[0].equals("0")) {
                if (action == TAKE_CASH_CONFIRM) {
                    ((TextView) findViewById(R.id.tvFrdTotalTakenSum)).setText(String.format("%s\n%s",
                            getString(R.string.label_total_taken_sum), Util.getParsedPrice(TotalTakenSum)));
                }
                adapter.notifyDataSetChanged();
            }
            Toast.makeText(DeliveryListActivity.this, result[1], Toast.LENGTH_LONG).show();
        }
    }
}