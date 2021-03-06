package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.TenantWizardPage2Fragment;

import java.util.ArrayList;

public class TenantWizardPage2 extends Page {
    public static final String TENANT_EMERGENCY_FIRST_NAME_DATA_KEY = "tenant_emergency_first_name";
    public static final String TENANT_EMERGENCY_LAST_NAME_DATA_KEY = "tenant_emergency_last_name";
    public static final String TENANT_EMERGENCY_PHONE_DATA_KEY = "tenant_emergency_phone";
    public static final String WAS_PRELOADED = "tenant_page_2_was_preloaded";
    private Context mContext;

    public TenantWizardPage2(ModelCallbacks callbacks, String title, Context context) {
        super(callbacks, title);
        mContext = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return TenantWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.emer_contact_first_name), mData.getString(TENANT_EMERGENCY_FIRST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.emer_contact_last_name), mData.getString(TENANT_EMERGENCY_LAST_NAME_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.emer_contact_phone), mData.getString(TENANT_EMERGENCY_PHONE_DATA_KEY), getKey(), -1));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }
}
