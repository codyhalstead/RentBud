package com.rentbud.wizards;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rentbud.activities.NewTenantWizard;
import com.rentbud.fragments.TenantWizardPage1Fragment;
import com.rentbud.model.Tenant;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class TenantWizardPage1 extends Page {
    public static final String TENANT_FIRST_NAME_DATA_KEY = "tenant_first_name";
    public static final String TENANT_LAST_NAME_DATA_KEY = "tenant_last_name";
    public static final String TENANT_PHONE_DATA_KEY = "tenant_phone";
    public static final String TENANT_EMAIL_DATA_KEY = "tenant_email";

    public TenantWizardPage1(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
        Tenant tenant = NewTenantWizard.tenantToEdit;
       // Log.d(TAG, "TenantWizardPage1: " + tenant.getFirstName());
        if(tenant != null){
            mData.putString(TENANT_FIRST_NAME_DATA_KEY, tenant.getFirstName());
            mData.putString(TENANT_LAST_NAME_DATA_KEY, tenant.getLastName());
            mData.putString(TENANT_PHONE_DATA_KEY, tenant.getPhone());
            mData.putString(TENANT_EMAIL_DATA_KEY, tenant.getEmail());
            this.notifyDataChanged();
        }
    }

    @Override
    public Fragment createFragment() {
        return TenantWizardPage1Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("First Name", mData.getString(TENANT_FIRST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Last Name", mData.getString(TENANT_LAST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Phone", mData.getString(TENANT_PHONE_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("E-mail", mData.getString(TENANT_EMAIL_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(TENANT_FIRST_NAME_DATA_KEY)) && !TextUtils.isEmpty(mData.getString(TENANT_LAST_NAME_DATA_KEY)));
    }

    public void loadTenant(){

    }
}