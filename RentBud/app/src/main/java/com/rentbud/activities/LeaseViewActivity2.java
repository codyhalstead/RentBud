package com.rentbud.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.cody.rentbud.R;
import com.rentbud.fragments.ApartmentListFragment;
import com.rentbud.fragments.LeaseListFragment;
import com.rentbud.fragments.LeaseViewFrag1;
import com.rentbud.fragments.LeaseViewFrag2;
import com.rentbud.fragments.TenantListFragment;
import com.rentbud.model.Lease;
import com.rentbud.sqlite.DatabaseHandler;

import java.util.List;

public class LeaseViewActivity2 extends BaseActivity {
    private Lease lease;
    private DatabaseHandler databaseHandler;
    ViewPager viewPager;
    LeaseViewActivity2.ViewPagerAdapter adapter;
    LinearLayout dateSelectorLL;
    private LeaseViewFrag1 frag1;
    private LeaseViewFrag2 frag2;
    //private LeaseViewFrag3 frag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUserAppTheme(MainActivity.curThemeChoice);
        setContentView(R.layout.activity_lease_view_actual);
        dateSelectorLL = findViewById(R.id.moneyDateSelecterLL);
        dateSelectorLL.setVisibility(View.GONE);
        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.databaseHandler = new DatabaseHandler(this);
        // Add Fragments to adapter one by one
        Bundle bundle = getIntent().getExtras();
        int leaseID = bundle.getInt("leaseID");
        this.lease = databaseHandler.getLeaseByID(MainActivity.user, leaseID);
        bundle.putParcelable("lease", lease);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#000000"));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#4d4c4b"));
        tabLayout.setupWithViewPager(viewPager);
        setupBasicToolbar();
        toolbar.setTitle("Lease View");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Get options menu
        getMenuInflater().inflate(R.menu.lease_view_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    //Handle option menu actions
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editLease:
                Intent intent = new Intent(this, NewLeaseWizard.class);
                intent.putExtra("leaseToEdit", lease);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                return true;

            case R.id.deleteLease:
                showDeleteConfirmationAlertDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage("Are you sure you want to remove this lease?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                databaseHandler.setLeaseInactive(lease);
                TenantListFragment.tenantListAdapterNeedsRefreshed = true;
                ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
                MainActivity.apartmentList = databaseHandler.getUsersApartmentsIncludingInactive(MainActivity.user);
                MainActivity.tenantList = databaseHandler.getUsersTenantsIncludingInactive(MainActivity.user);
                //LeaseViewActivity2.this.finish();
                showDeleteAllRelatedMoneyAlertDialog();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteAllRelatedMoneyAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("AlertDialog");
        builder.setMessage("Remove all income/expenses related to this lease?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LeaseViewActivity2.this.finish();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               databaseHandler.setAllExpensesRelatedToLeaseInactive(lease.getId());
               databaseHandler.setAllIncomeRelatedToLeaseInactive(lease.getId());
                LeaseViewActivity2.this.finish();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //Re-query cached apartment array to update cache and refresh current fragment to display new data
                //int leaseID = data.getIntExtra("editedLeaseID", 0);
                this.lease = databaseHandler.getLeaseByID(MainActivity.user, lease.getId());
                //this.apartment = dataMethods.getCachedApartmentByApartmentID(lease.getApartmentID());
                //Pair<Tenant, ArrayList<Tenant>> tenants = dataMethods.getCachedPrimaryAndSecondaryTenantsByLease(lease);
                //this.primaryTenant = tenants.first;
                //this.secondaryTenants = tenants.second;
                //fillTextViews();
                LeaseListFragment.leaseListAdapterNeedsRefreshed = true;
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                if (fragments != null) {
                    for (Fragment fragment : fragments) {
                        if (fragment != null) {
                            //android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            //fragmentTransaction.detach(fragment);
                            //fragmentTransaction.attach(fragment);
                            //fragmentTransaction.commit();
                            fragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }
                }
            }
        } else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment != null) {
                        fragment.onActivityResult(requestCode, resultCode, data);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("lease", lease);
            switch (position) {
                case 0:
                    LeaseViewFrag1 frg1 = new LeaseViewFrag1();
                    frg1.setArguments(bundle);
                    return frg1;
                case 1:
                    LeaseViewFrag2 frg2 = new LeaseViewFrag2();
                    frg2.setArguments(bundle);
                    return frg2;
                //case 2:
                //    LeaseViewFrag3 frg3 = new LeaseViewFrag3();
                //    frg3.setArguments(bundle);
                //    return frg3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    frag1 = (LeaseViewFrag1) createdFragment;
                    break;
                case 1:
                    frag2 = (LeaseViewFrag2) createdFragment;
                    break;
                //case 2:
                //    frag3 = (LeaseViewFrag3) createdFragment;
                //    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Info";
                case 1:
                    return "Payments";
                // case 2:
                //     return "Expenses";
            }
            return "";
        }
    }
}


