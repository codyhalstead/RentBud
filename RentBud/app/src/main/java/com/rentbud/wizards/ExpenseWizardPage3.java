package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewExpenseWizard;
import com.rentbud.fragments.ExpenseWizardPage2Fragment;
import com.rentbud.fragments.ExpenseWizardPage3Fragment;
import com.rentbud.helpers.MainArrayDataMethods;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ExpenseWizardPage3 extends Page {
    public static final String EXPENSE_RELATED_APT_DATA_KEY = "expense_related_apt";
    public static final String EXPENSE_RELATED_APT_TEXT_DATA_KEY = "expense_related_apt_text";
    public static final String EXPENSE_RELATED_APT_ID_DATA_KEY = "expense_related_apt_id";

    public static final String EXPENSE_RELATED_TENANT_DATA_KEY = "expense_related_tenant";
    public static final String EXPENSE_RELATED_TENANT_TEXT_DATA_KEY = "expense_related_tenant_text";
    public static final String EXPENSE_RELATED_TENANT_ID_DATA_KEY = "expense_related_tenant_id";

    public static final String EXPENSE_RELATED_LEASE_DATA_KEY = "expense_related_lease";
    public static final String EXPENSE_RELATED_LEASE_TEXT_DATA_KEY = "expense_related_lease_text";
    public static final String EXPENSE_RELATED_LEASE_ID_DATA_KEY = "expense_related_lease_id";
    public static final String WAS_PRELOADED = "expense_page_3_was_preloaded";

    public ExpenseWizardPage3(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        mData.putBoolean(WAS_PRELOADED, false);
        //ExpenseLogEntry expenseLogEntry = NewExpenseWizard.expenseToEdit;
        //if(expenseLogEntry != null){

        //    MainArrayDataMethods dataMethods = new MainArrayDataMethods();
            //TODO add empty value name
        //    String apartmentString = "";
        //    if(expenseLogEntry.getApartmentID() != 0) {
        //        Apartment apartment = dataMethods.getCachedApartmentByApartmentID(expenseLogEntry.getApartmentID());
        //        mData.putParcelable(EXPENSE_RELATED_APT_DATA_KEY, apartment);
        //        if (apartment != null) {
        //            apartmentString = apartment.getStreet1();
        //            if (apartment.getStreet2() != null) {
        //                apartmentString += " ";
        //                apartmentString += apartment.getStreet2();
        //            }
        //        }
        //    }
        //    mData.putString(EXPENSE_RELATED_APT_TEXT_DATA_KEY, apartmentString);
        //    this.notifyDataChanged();
       // }
       // Log.d(TAG, "ExpenseWizardPage3: CREATED 3");
    }

    @Override
    public Fragment createFragment() {
        return ExpenseWizardPage3Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Related Apartment", mData.getString(EXPENSE_RELATED_APT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Related Tenant", mData.getString(EXPENSE_RELATED_TENANT_TEXT_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Related Lease", mData.getString(EXPENSE_RELATED_LEASE_TEXT_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}