package com.rentbud.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
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

import com.example.cody.rentbud.R;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.ExpenseLogEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/27/2018.
 */

public class ExpenseListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<ExpenseLogEntry> expenseArray;
    private ArrayList<ExpenseLogEntry> filteredResults;
    private Context context;
    private String searchText;
    private ColorStateList highlightColor;
    MainArrayDataMethods dataMethods;
    private Date todaysDate;

    public ExpenseListAdapter(Context context, ArrayList<ExpenseLogEntry> expenseArray, ColorStateList highlightColor) {
        super();
        this.expenseArray = expenseArray;
        this.filteredResults = expenseArray;
        this.context = context;
        this.searchText = "";
        this.highlightColor = highlightColor;
        this.dataMethods = new MainArrayDataMethods();
        this.todaysDate = new Date(System.currentTimeMillis());
    }

    static class ViewHolder {
        TextView amountTV;
        TextView dateTV;
        TextView typeTV;
        TextView descriptionTV;
    }

    @Override
    public int getCount() {
        return filteredResults.size();
    }

    @Override
    public ExpenseLogEntry getItem(int i) {
        return filteredResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ExpenseLogEntry expense = getItem(position);
        ExpenseListAdapter.ViewHolder viewHolder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_expense, viewGroup, false);
            viewHolder = new ExpenseListAdapter.ViewHolder();

            viewHolder.amountTV = convertView.findViewById(R.id.expenseRowAmountTV);
            viewHolder.dateTV = convertView.findViewById(R.id.expenseRowDateTV);
            viewHolder.typeTV = convertView.findViewById(R.id.expenseRowTypeTV);
            viewHolder.descriptionTV = convertView.findViewById(R.id.expenseRowDescriptionTV);
            //viewHolder.position = position;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ExpenseListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.red));
        if (expense != null) {
            BigDecimal displayVal = expense.getAmount().setScale(2, RoundingMode.HALF_EVEN);
            NumberFormat usdCostFormat = NumberFormat.getCurrencyInstance(Locale.US);
            usdCostFormat.setMinimumFractionDigits(2);
            usdCostFormat.setMaximumFractionDigits(2);

            viewHolder.amountTV.setText("-" + usdCostFormat.format(displayVal.doubleValue()));
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            viewHolder.dateTV.setText(formatter.format(expense.getDate()));
            if( todaysDate.compareTo(expense.getDate()) < 0){
                viewHolder.amountTV.setTextColor(context.getResources().getColor(R.color.text_light));
            }
            viewHolder.typeTV.setText(expense.getTypeLabel());

            setTextHighlightSearch(viewHolder.descriptionTV, expense.getDescription());
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
                ArrayList<ExpenseLogEntry> FilteredArrayNames = new ArrayList<>();
                searchText = constraint.toString().toLowerCase();
                //Perform users search
                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < expenseArray.size(); i++) {
                    ExpenseLogEntry dataNames = expenseArray.get(i);
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
                filteredResults = (ArrayList<ExpenseLogEntry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    //Retrieve filtered results
    public ArrayList<ExpenseLogEntry> getFilteredResults() {
        return this.filteredResults;
    }

    public void updateResults(ArrayList<ExpenseLogEntry> results) {
        expenseArray = results;
        filteredResults = results;

        //Triggers the list update
        notifyDataSetChanged();
    }

}
