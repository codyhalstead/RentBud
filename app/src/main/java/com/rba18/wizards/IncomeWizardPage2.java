package com.rba18.wizards;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.example.android.wizardpager.wizard.model.ModelCallbacks;
import com.example.android.wizardpager.wizard.model.Page;
import com.example.android.wizardpager.wizard.model.ReviewItem;
import com.rba18.R;
import com.rba18.fragments.IncomeWizardPage2Fragment;

import java.util.ArrayList;

public class IncomeWizardPage2 extends Page {
    public static final String INCOME_DESCRIPTION_DATA_KEY = "income_description";
    public static final String INCOME_RECEIPT_PIC_DATA_KEY = "income_receipt_pic";
    public static final String INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY = "income_was_receipt_pic_added";
    public static final String WAS_PRELOADED = "income_page_2_was_preloaded";
    private boolean mIsEdit;
    private Context mContext;

    public IncomeWizardPage2(ModelCallbacks callbacks, String title, boolean isEdit, Context context) {
        super(callbacks, title);
        mIsEdit = isEdit;
        mContext = context;
        mData.putBoolean(WAS_PRELOADED, false);
    }

    @Override
    public Fragment createFragment() {
        return IncomeWizardPage2Fragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(mContext.getResources().getString(R.string.description), mData.getString(INCOME_DESCRIPTION_DATA_KEY), getKey(), -1));
        if (!mIsEdit) {
            dest.add(new ReviewItem(mContext.getResources().getString(R.string.receipt_pic), mData.getString(INCOME_WAS_RECEIPT_PIC_ADDED_DATA_KEY), getKey(), -1));
        }
    }

    @Override
    public boolean isCompleted() {
        return (!TextUtils.isEmpty(mData.getString(INCOME_DESCRIPTION_DATA_KEY)));
    }
}
