package com.rba18.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.rba18.R;
import com.rba18.helpers.DateAndCurrencyDisplayer;
import com.rba18.helpers.MainArrayDataMethods;
import com.rba18.model.ExpenseLogEntry;
import com.rba18.model.MoneyLogEntry;
import com.rba18.model.PaymentLogEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MoneyListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<MoneyLogEntry> moneyArray;
    private ArrayList<MoneyLogEntry> filteredResults;
    private SharedPreferences preferences;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;
    private Date todaysDate;
    private Date dateToHighlight;
    private int dateFormatCode, moneyFormatCode;
    private boolean greyOutEnabled;

    public MoneyListAdapter(Context context, ArrayList<MoneyLogEntry> moneyArray, ColorStateList highlightColor, @Nullable Date dateToHighlight, boolean greyOutEnabled) {
        super();
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.moneyArray = moneyArray;
        this.filteredResults = moneyArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.todaysDate = new Date(System.currentTimeMillis());
        this.dateToHighlight = dateToHighlight;
        this.dateFormatCode = preferences.getInt("dateFormat", DateAndCurrencyDisplayer.DATE_MMDDYYYY);
        this.moneyFormatCode = preferences.getInt("currency", DateAndCurrencyDisplayer.CURRENCY_US);
        this.greyOutEnabled = greyOutEnabled;
    }

    static class ViewHolder {
        TextView amountTV;
        TextView dateTV;
        TextView typeTV;
        TextView descriptionTV;
        TextView wasCompletedTV;
    }

    @Override
    public int getCount() {
        if(filteredResults != null) {
            return filteredResults.size();
        }
        return 0;
    }

    @Override
    public MoneyLogEntry getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        MoneyLogEntry moneyEntry = getItem(position);
        MoneyListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_expense, viewGroup, false);
            viewHolder = new MoneyListAdapter.ViewHolder();

            viewHolder.amountTV = convertView.findViewById(R.id.expenseRowAmountTV);
            viewHolder.dateTV = convertView.findViewById(R.id.expenseRowDateTV);
            viewHolder.typeTV = convertView.findViewById(R.id.expenseRowTypeTV);
            viewHolder.descriptionTV = convertView.findViewById(R.id.expenseRowDescriptionTV);
            viewHolder.wasCompletedTV = convertView.findViewById(R.id.expenseRowWasPaidTV);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (MoneyListAdapter.ViewHolder) convertView.getTag();
        }
        if (moneyEntry != null) {
            viewHolder.dateTV.setText(DateAndCurrencyDisplayer.getDateToDisplay(dateFormatCode, moneyEntry.getDate()));
            if(dateToHighlight != null){
                if(moneyEntry.getDate().equals(dateToHighlight)){
                    viewHolder.dateTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
                } else {
                    viewHolder.dateTV.setTextColor(context.getResources().getColor(R.color.text_light));
                }
            }
            if(greyOutEnabled) {
                if (todaysDate.compareTo(moneyEntry.getDate()) < 0) {
                    convertView.setBackgroundColor(convertView.getResources().getColor(R.color.rowDarkenedBackground));
                } else {
                    convertView.setBackgroundColor(convertView.getResources().getColor(R.color.white));
                }
            }
            if(moneyEntry instanceof ExpenseLogEntry){
                ExpenseLogEntry expense = (ExpenseLogEntry)moneyEntry;
                viewHolder.amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, expense.getAmount()));
                viewHolder.typeTV.setText(expense.getTypeLabel());
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.red));
                if(expense.getIsCompleted()) {
                    viewHolder.wasCompletedTV.setText(R.string.paid);
                    viewHolder.wasCompletedTV.setTextColor(convertView.getResources().getColor(R.color.caldroid_black));
                } else {
                    viewHolder.wasCompletedTV.setText(R.string.not_paid);
                    viewHolder.wasCompletedTV.setTextColor(convertView.getResources().getColor(R.color.red));
                }
            } else if(moneyEntry instanceof PaymentLogEntry){
                PaymentLogEntry income = (PaymentLogEntry) moneyEntry;
                viewHolder.amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, income.getAmount()));
                viewHolder.typeTV.setText(income.getTypeLabel());
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.green_colorPrimaryDark));
                if(income.getIsCompleted()) {
                    viewHolder.wasCompletedTV.setText(R.string.received);
                    viewHolder.wasCompletedTV.setTextColor(convertView.getResources().getColor(R.color.caldroid_black));
                } else {
                    viewHolder.wasCompletedTV.setText(R.string.not_received);
                    viewHolder.wasCompletedTV.setTextColor(convertView.getResources().getColor(R.color.red));
                }
            } else {
                viewHolder.amountTV.setText(DateAndCurrencyDisplayer.getCurrencyToDisplay(moneyFormatCode, moneyEntry.getAmount()));
                viewHolder.typeTV.setText("");
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.caldroid_darker_gray));
            }

            setTextHighlightSearch(viewHolder.descriptionTV, moneyEntry.getDescription());
        }
        return convertView;
    }

    //Used to change color of any text matching search
    private void setTextHighlightSearch(TextView textView, String theTextToSet) {
        //If user has any text in the search bar
        if (searchText != null && !searchText.isEmpty()) {
            int startPos = theTextToSet.toLowerCase(Locale.US).indexOf(searchText.toLowerCase(Locale.US));
            int endPos = startPos + searchText.length();
            if (startPos != -1) {
                //If theTextToSet contains match, highlight match
                Spannable spannable = new SpannableString(theTextToSet);
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, highlightColor, null);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannable);
            } else {
                //Set regular text if not
                textView.setText(theTextToSet);
            }
        } else {
            //Set regular text if search bar is empty
            textView.setText(theTextToSet);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<MoneyLogEntry> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < moneyArray.size(); i++) {
                    MoneyLogEntry dataNames = moneyArray.get(i);
                    //If users search matches any part of any apartment value, add to new filtered list
                    if (dataNames.getDescription().toLowerCase().contains(constraint.toString())) {
                        FilteredArrayNames.add(dataNames);
                    }
                }
                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredResults = (ArrayList<MoneyLogEntry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //Retrieve filtered results
    public ArrayList<MoneyLogEntry> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<MoneyLogEntry> results) {
        moneyArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

}
