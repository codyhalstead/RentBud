package com.rba18.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.activities.MainActivity;
import com.rba18.helpers.CustomDatePickerDialogLauncher;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.wizards.ExpenseWizardPage1;
import com.rba18.sqlite.DatabaseHandler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpenseWizardPage1Fragment extends android.support.v4.app.Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private ExpenseWizardPage1 mPage;
    private TextView mDateTV;
    private EditText mAmountET;
    private Spinner mTypeSpinner;
    private Button mAddNewTypeBtn;
    private ArrayAdapter<String> mAdapter;
    private BigDecimal mAmount;
    private Date mExpenseDate;
    private DatabaseHandler mDBHandler;
    private boolean mIsEdit;
    private CustomDatePickerDialogLauncher mDatePickerDialogLauncher;
    private int mDateFormatCode, mMoneyFormatCode;
    private AlertDialog mAlertDialog;

    public static ExpenseWizardPage1Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ExpenseWizardPage1Fragment fragment = new ExpenseWizardPage1Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseWizardPage1Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (ExpenseWizardPage1) mCallbacks.onGetPage(mKey);
        mDBHandler = new DatabaseHandler(getContext());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mDateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        mMoneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        mIsEdit = false;
        Bundle extras = mPage.getData();
        if (extras != null) {
            ExpenseLogEntry expenseToEdit = extras.getParcelable("expenseToEdit");
            if (expenseToEdit != null) {
                loadDataForEdit(expenseToEdit);
                mIsEdit = true;
            } else {
                preloadData(extras);
            }
        } else {
            mExpenseDate = null;
            mAmount = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mAmount);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, mAmount.toPlainString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense_wizard_page_1, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        mDateTV = rootView.findViewById(R.id.expenseWizardDateTV);
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY) != null) {
            String dateString = mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mExpenseDate = formatFrom.parse(dateString);
                mDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mExpenseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mAmountET = rootView.findViewById(R.id.expenseWizardAmountET);
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY) != null) {
            mAmountET.setText(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY));
        }
        mAmountET.setSelection(mAmountET.getText().length());

        mTypeSpinner = rootView.findViewById(R.id.expenseWizardTypeSpinner);
        mTypeSpinner.setSelection(mPage.getData().getInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY));

        mAddNewTypeBtn = rootView.findViewById(R.id.expenseWizardAddNewTypeBtn);

        TextView newExpenseHeaderTV = rootView.findViewById(R.id.expenseWizardPageOneHeader);
        if (mIsEdit) {
            newExpenseHeaderTV.setText(R.string.edit_expense_info);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDatePickerDialogLauncher = new CustomDatePickerDialogLauncher(mExpenseDate, false, getContext());
        mDatePickerDialogLauncher.setDateSelectedListener(new CustomDatePickerDialogLauncher.DateSelectedListener() {
            @Override
            public void onStartDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onEndDateSelected(Date startDate, Date endDate) {

            }

            @Override
            public void onDateSelected(Date date) {
                mExpenseDate = date;
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                mDateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mExpenseDate));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mExpenseDate));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, formatter.format(mExpenseDate));
                mPage.notifyDataChanged();
            }
        });
        populateExpenseTypeSpinner();
        mDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatePickerDialogLauncher.launchSingleDatePickerDialog();
            }
        });
        mAmountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mAmountET == null) return;
                String s = editable.toString();
                if (s.isEmpty()) return;
                mAmountET.removeTextChangedListener(this);
                String cleanString = DateAndCurrencyDisplayer.cleanMoneyString(s);
                mAmount = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
                String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mAmount);
                mAmountET.setText(formatted);
                mAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mAmountET.getText().length(), mMoneyFormatCode));
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, mAmount.toPlainString());
                mPage.notifyDataChanged();
                mAmountET.addTextChangedListener(this);
            }
        });
        mAmountET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAmountET.setSelection(DateAndCurrencyDisplayer.getEndCursorPositionForMoneyInput(mAmountET.getText().length(), mMoneyFormatCode));
            }
        });

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String type = mTypeSpinner.getSelectedItem().toString();
                int typeID = 0;
                if(MainActivity.sExpenseTypeLabels.get(type) != null) {
                    typeID = MainActivity.sExpenseTypeLabels.get(type);
                }
                mPage.getData().putInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY, typeID);
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY, type);

                if (getUserVisibleHint()) {
                    mPage.notifyDataChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mAddNewTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(getContext());
                int maxLength = 25;
                editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

                // create the AlertDialog as final
                 mAlertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.create_new_type)
                        .setView(editText)

                        // Set the action buttons
                        .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mDBHandler.addNewExpenseType(editText.getText().toString());
                                MainActivity.sExpenseTypeLabels = mDBHandler.getExpenseTypeLabelsTreeMap();
                                updateExpenseTypeSpinner();
                                int spinnerPosition = mAdapter.getPosition(editText.getText().toString());
                                mTypeSpinner.setSelection(spinnerPosition);
                            }
                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // removes the AlertDialog in the screen
                            }
                        })
                        .create();

                // set the focus change listener of the EditText
                // this part will make the soft keyboard automatically visible
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                    }
                });
                mAlertDialog.show();
            }
        });
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY) != null) {
            int spinnerPosition = mAdapter.getPosition(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
            if (mIsEdit && spinnerPosition == -1) {
                mAdapter.add(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
                spinnerPosition = mAdapter.getPosition(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY));
            }
            mTypeSpinner.setSelection(spinnerPosition);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mDateTV != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatePickerDialogLauncher.dismissDatePickerDialog();
        if(mAlertDialog != null){
            mAlertDialog.dismiss();
        }
    }

    private void populateExpenseTypeSpinner() {
        //Create state array from MainActivity5.stateMap
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.sExpenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        //Create ArrayAdapter with state array
        mAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Set ArrayAdapter to stateSpinner
        mTypeSpinner.setAdapter(mAdapter);
    }

    public void updateExpenseTypeSpinner() {
        List<String> spinnerArray = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : MainActivity.sExpenseTypeLabels.entrySet()) {
            spinnerArray.add(entry.getKey());
        }
        mAdapter.clear();
        mAdapter.addAll(spinnerArray);
    }

    private void preloadDate(Bundle bundle) {
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY) != null) {
            //If date exists (Was reloaded)
            String dateString = mPage.getData().getString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mExpenseDate = formatFrom.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (bundle.getString("preloadedDate") != null) {
            //Date does not exist, check if need to preload
            String dateString = bundle.getString("preloadedDate");
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, dateString);
            DateFormat formatFrom = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            try {
                mExpenseDate = formatFrom.parse(dateString);
                mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mExpenseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            mExpenseDate = null;
        }
    }

    private void preloadAmount(Bundle bundle) {
        if (mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY) != null) {
            mAmount = new BigDecimal(mPage.getData().getString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY)).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        } else {
            mAmount = new BigDecimal(0);
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, mAmount);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, mAmount.toPlainString());
        }
    }

    private void preloadType(Bundle bundle) {

    }

    private void preloadData(Bundle bundle) {
        preloadDate(bundle);
        preloadAmount(bundle);
        preloadType(bundle);
    }

    private void loadDataForEdit(ExpenseLogEntry expenseToEdit) {
        if (!mPage.getData().getBoolean(ExpenseWizardPage1.WAS_PRELOADED)) {
            //Date
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String dateString = formatter.format(expenseToEdit.getDate());
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_DATA_KEY, dateString);
            mExpenseDate = expenseToEdit.getDate();
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_DATE_STRING_FORMATTED_DATA_KEY, DateAndCurrencyDisplayer.getDateToDisplay(mDateFormatCode, mExpenseDate));
            //Amount
            BigDecimal amountBD = expenseToEdit.getAmount().multiply(new BigDecimal(-1));
            String formatted = DateAndCurrencyDisplayer.getCurrencyToDisplay(mMoneyFormatCode, amountBD);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_FORMATTED_STRING_DATA_KEY, formatted);
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_AMOUNT_STRING_DATA_KEY, amountBD.toPlainString());
            mAmount = expenseToEdit.getAmount();
            //Type
            mPage.getData().putInt(ExpenseWizardPage1.EXPENSE_TYPE_ID_DATA_KEY, expenseToEdit.getTypeID());
            mPage.getData().putString(ExpenseWizardPage1.EXPENSE_TYPE_DATA_KEY, expenseToEdit.getTypeLabel());
            mPage.getData().putBoolean(ExpenseWizardPage1.WAS_PRELOADED, true);
        } else {
            preloadData(mPage.getData());
        }
    }
}