package com.rba18.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.android.wizardpager.wizard.ui.PageFragmentCallbacks;
import com.rba18.R;
import com.rba18.model.Tenant;
import com.rba18.wizards.TenantWizardPage3;

public class TenantWizardPage3Fragment extends android.support.v4.app.Fragment {

    private static final String ARG_KEY = "key";
    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private TenantWizardPage3 mPage;
    private EditText mNotesET;


    public static TenantWizardPage3Fragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        TenantWizardPage3Fragment fragment = new TenantWizardPage3Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    public TenantWizardPage3Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (TenantWizardPage3) mCallbacks.onGetPage(mKey);
        Bundle extras = mPage.getData();
        if (extras != null) {
            Tenant tenantToEdit = extras.getParcelable("tenantToEdit");
            if (tenantToEdit != null) {
                loadDataForEdit(tenantToEdit);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tenant_wizard_page_3, container, false);
        (rootView.findViewById(android.R.id.title)).setVisibility(View.GONE);

        mNotesET = (rootView.findViewById(R.id.tenantWizardNotesET));
        mNotesET.setText(mPage.getData().getString(TenantWizardPage3.TENANT_NOTES_DATA_KEY));
        mNotesET.setSelection(mNotesET.getText().length());

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
        mNotesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(TenantWizardPage3.TENANT_NOTES_DATA_KEY, editable.toString());
                mPage.notifyDataChanged();
            }
        });
    }


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        // In a future update to the support library, this should override setUserVisibleHint
        // instead of setMenuVisibility.
        if (mNotesET != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void loadDataForEdit(Tenant tenantToEdit) {
        if (!mPage.getData().getBoolean(TenantWizardPage3.WAS_PRELOADED)) {
            //Notes
            mPage.getData().putString(TenantWizardPage3.TENANT_NOTES_DATA_KEY, tenantToEdit.getNotes());
            mPage.getData().putBoolean(TenantWizardPage3.WAS_PRELOADED, true);
        }
    }
}
