package com.rba18.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rba18.R;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import hirondelle.date4j.DateTime;

public class CustomCalendarAdapter extends CaldroidGridAdapter {
    //Key = date, value = amount
    private HashMap<String, Integer> mLeaseStartDatesHM;
    private HashMap<String, Integer> mLeaseEndDatesHM;
    private HashMap<String, Integer> mExpenseDatesHM;
    private HashMap<String, Integer> mIncomeDatesHM;


    public CustomCalendarAdapter(Context context, int month, int year,
                                 Map<String, Object> caldroidData,
                                 Map<String, Object> extraData,
                                 HashMap<String, Integer> leaseStartDatesAndAmounts,
                                 HashMap<String , Integer> leaseEndDatesAndAmounts,
                                 HashMap<String, Integer> expenseDatesAndAmounts,
                                 HashMap<String, Integer> incomeDatesAndAmounts) {
        super(context, month, year, caldroidData, extraData);
        mLeaseStartDatesHM = leaseStartDatesAndAmounts;
        mLeaseEndDatesHM = leaseEndDatesAndAmounts;
        mExpenseDatesHM = expenseDatesAndAmounts;
        mIncomeDatesHM = incomeDatesAndAmounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.custom_calendar_cell, parent, false);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();
    
        TextView dateTV = cellView.findViewById(R.id.customCalCellDateTV);
        TextView leaseBeginAmountTV = cellView.findViewById(R.id.customCalCellLeaseBeginAmountTV);
        TextView leaseEndAmountTV = cellView.findViewById(R.id.customCalCellLeaseEndAmountTV);
        TextView expenseAmountTV = cellView.findViewById(R.id.customCalCellExpenseAmountTV);
        TextView incomeAmountTV = cellView.findViewById(R.id.customCalCellIncomeAmountTV);
        TextView spacer1 = cellView.findViewById(R.id.spacer1);
        TextView spacer2 = cellView.findViewById(R.id.spacer2);
        TextView spacer3 = cellView.findViewById(R.id.spacer3);
        TextView spacer4 = cellView.findViewById(R.id.spacer4);
        dateTV.setTextColor(Color.BLACK);
        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Date date = new Date(dateTime.getYear() - 1900, dateTime.getMonth() - 1, dateTime.getDay(), 0, 0);
        Resources resources = context.getResources();
        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            dateTV.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
            leaseBeginAmountTV.setVisibility(View.INVISIBLE);
            leaseEndAmountTV.setVisibility(View.INVISIBLE);
            expenseAmountTV.setVisibility(View.INVISIBLE);
            incomeAmountTV.setVisibility(View.INVISIBLE);
            spacer1.setVisibility(View.GONE);
            spacer2.setVisibility(View.GONE);
            spacer3.setVisibility(View.GONE);
            spacer4.setVisibility(View.GONE);
        }
        int amount;
        if (mLeaseStartDatesHM != null) {
            if (mLeaseStartDatesHM.containsKey(date.toString())) {
                leaseBeginAmountTV.setVisibility(View.VISIBLE);
                amount = mLeaseStartDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    leaseBeginAmountTV.setText(amountString);
                } else {
                    leaseBeginAmountTV.setText(R.string.over99);
                }
                spacer1.setVisibility(View.GONE);
            } else {
                leaseBeginAmountTV.setVisibility(View.GONE);
                spacer1.setVisibility(View.INVISIBLE);
            }
        }
        if (mLeaseEndDatesHM != null) {
            if (mLeaseEndDatesHM.containsKey(date.toString())) {
                leaseEndAmountTV.setVisibility(View.VISIBLE);
                amount = mLeaseEndDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    leaseEndAmountTV.setText(amountString);
                } else {
                    leaseEndAmountTV.setText(R.string.over99);
                }
                spacer2.setVisibility(View.GONE);
            } else {
                leaseEndAmountTV.setVisibility(View.GONE);
                spacer2.setVisibility(View.INVISIBLE);
            }
        }
        if (mExpenseDatesHM != null) {
            if (mExpenseDatesHM.containsKey(date.toString())) {
                expenseAmountTV.setVisibility(View.VISIBLE);
                amount = mExpenseDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    expenseAmountTV.setText(amountString);
                } else {
                    expenseAmountTV.setText(R.string.over99);
                }
                spacer3.setVisibility(View.GONE);
            } else {
                expenseAmountTV.setVisibility(View.GONE);
                spacer3.setVisibility(View.INVISIBLE);
            }
        }
        if (mIncomeDatesHM != null) {
            if (mIncomeDatesHM.containsKey(date.toString())) {
                incomeAmountTV.setVisibility(View.VISIBLE);
                amount =  mIncomeDatesHM.get(date.toString());
                if (amount <= 99) {
                    String amountString = amount + "";
                    incomeAmountTV.setText(amountString);
                } else {
                    incomeAmountTV.setText(R.string.over99);
                }
                spacer4.setVisibility(View.GONE);
            } else {
                incomeAmountTV.setVisibility(View.GONE);
                spacer4.setVisibility(View.INVISIBLE);
            }
        }
        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;
        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {
            dateTV.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }
        } else {
            shouldResetDiabledView = true;
        }
        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources
                    .getColor(com.caldroid.R.color.caldroid_sky_blue));
            dateTV.setTextColor(Color.BLACK);
        } else {
            shouldResetSelectedView = true;
        }
        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }
        //Set text
        String dateTimeString = "" + dateTime.getDay();
        dateTV.setText(dateTimeString);
        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);
        // Set custom color if required
        setCustomResources(dateTime, cellView, dateTV);
        return cellView;
    }

    public void updateDateData(HashMap<String, Integer> leaseStartDatesAndAmounts,
                               HashMap<String, Integer> leaseEndDatesAndAmounts,
                               HashMap<String, Integer> expenseDatesAndAmounts,
                               HashMap<String, Integer> incomeDatesAndAmounts) {
        mLeaseStartDatesHM = leaseStartDatesAndAmounts;
        mLeaseEndDatesHM = leaseEndDatesAndAmounts;
        mExpenseDatesHM = expenseDatesAndAmounts;
        mIncomeDatesHM = incomeDatesAndAmounts;
        this.notifyDataSetChanged();
    }

}