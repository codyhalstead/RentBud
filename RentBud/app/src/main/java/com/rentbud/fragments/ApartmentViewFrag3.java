package com.rentbud.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.activities.NewLeaseWizard;
import com.rentbud.adapters.LeaseListAdapter;
import com.rentbud.helpers.ApartmentTenantViewModel;
import com.rentbud.model.Apartment;
import com.rentbud.model.Lease;
import com.rentbud.sqlite.DatabaseHandler;

import static android.app.Activity.RESULT_OK;

public class ApartmentViewFrag3 extends android.support.v4.app.Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public ApartmentViewFrag3() {
        // Required empty public constructor
    }
    TextView noIncomeTV;
    FloatingActionButton fab;
    LinearLayout totalBarLL;
    //  EditText searchBarET;
    //  Button dateRangeStartBtn, dateRangeEndBtn;
    LeaseListAdapter leaseListAdapter;
    ColorStateList accentColor;
    ListView listView;
    //Date startDateRange, endDateRange;
    //public static boolean incomeListAdapterNeedsRefreshed;
    //  Date filterDateStart, filterDateEnd;
    //  private DatePickerDialog.OnDateSetListener dateSetFilterStartListener, dateSetFilterEndListener;
    private DatabaseHandler db;
    //private ArrayList<Lease> currentFilteredLeases;
    private Lease selectedLease;
    private Apartment apartment;
    private OnLeaseDataChangedListener mCallback;
    //private BigDecimal total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lease_view_fragment_two, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //this.noIncomeTV = view.findViewById(R.id.moneyEmptyListTV);
        this.totalBarLL = view.findViewById(R.id.moneyListTotalBarLL);
        totalBarLL.setVisibility(View.GONE);
        this.fab = view.findViewById(R.id.listFab);
        this.listView = view.findViewById(R.id.mainMoneyListView);
        this.db = new DatabaseHandler(getContext());
       // Bundle bundle = getArguments();
        //if (savedInstanceState != null) {
           // if (savedInstanceState.getParcelable("apartment") != null) {
           //     this.apartment = savedInstanceState.getParcelable("apartment");
            //}
            //if (savedInstanceState.getParcelableArrayList("filteredLeases") != null) {
            //    this.currentFilteredLeases = savedInstanceState.getParcelableArrayList("filteredLeases");
            //}
            //if (savedInstanceState.getString("totalString") != null) {
            //    String totalString = savedInstanceState.getString("totalString");
            //    this.total = new BigDecimal(totalString);
            //}
            //if (savedInstanceState.getString("startDateRange") != null) {
            //    DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            //    try {
            //        Date startDate = formatFrom.parse(savedInstanceState.getString("startDateRange"));
            //        startDateRange = startDate;
            //    } catch (ParseException e) {
            //        e.printStackTrace();
            //    }
            //}
            //if (savedInstanceState.getString("endDateRange") != null) {
            //    DateFormat formatFrom = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            //    try {
            //        Date endDate = formatFrom.parse(savedInstanceState.getString("endDateRange"));
            //        endDateRange = endDate;
            //    } catch (ParseException e) {
            //        e.printStackTrace();
            //    }
           // }

       // } else {
            this.apartment = ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getApartment().getValue();
            //endDateRange = Calendar.getInstance().getTime();
            //Calendar calendar = Calendar.getInstance();
            //calendar.setTime(endDateRange);
            //calendar.add(Calendar.YEAR, -1);
            //startDateRange = calendar.getTime();
            //currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
            //total = getTotal();
     //   }
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                intent.putExtra("preloadedApartment", apartment);
                startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
            }
        });
        //Get apartment item
       // setUpdateSelectedDateListeners();
        // getActivity().setTitle("Income View");
        // ExpenseListFragment.expenseListAdapterNeedsRefreshed = false;
        //Get current theme accent color, which is passed into the list adapter for search highlighting
        TypedValue colorValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.colorAccent, colorValue, true);
        this.accentColor = getActivity().getResources().getColorStateList(colorValue.resourceId);
        setUpListAdapter();
        setUpSearchBar();
        //setTotalTV();
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (IncomeListFragment.incomeListAdapterNeedsRefreshed) {
        //    if (this.leaseListAdapter != null) {
                //   incomeListAdapterNeedsRefreshed = false;
              //  leaseListAdapter.getFilter().filter("");
        //    }
        //}
    }

    private void setUpSearchBar() {

    }

    public interface OnLeaseDataChangedListener{
        void onLeaseDataChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnLeaseDataChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLeaseDataChangedListener");
        }
    }


    private void setUpListAdapter() {
        if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue() != null) {
            leaseListAdapter = new LeaseListAdapter(getActivity(), ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue(), accentColor, null);
            listView.setAdapter(leaseListAdapter);
            listView.setOnItemClickListener(this);
            if (ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue().isEmpty()) {
                //     noIncomeTV.setVisibility(View.VISIBLE);
                //    noIncomeTV.setText("No Current Income");
            }
        } else {
            //If MainActivity5.expenseList is null show empty list text
            //  noIncomeTV.setVisibility(View.VISIBLE);
            //  noIncomeTV.setText("Error Loading Income");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        final int position = i;
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.edit:
                        //On listView row click, launch ApartmentViewActivity passing the rows data into it.
                        Intent intent = new Intent(getActivity(), NewLeaseWizard.class);
                        selectedLease = leaseListAdapter.getFilteredResults().get(position);
                        intent.putExtra("leaseToEdit", selectedLease);
                        startActivityForResult(intent, MainActivity.REQUEST_NEW_LEASE_FORM);
                        //intent.putExtra("expenseID", expense.getId());
                        //startActivity(intent);
                        return true;

                    case R.id.remove:
                        selectedLease = leaseListAdapter.getFilteredResults().get(position);
                        showDeleteConfirmationAlertDialog();
                        return true;

                    default:
                        return false;
                }
            }
        });
        inflater.inflate(R.menu.expense_income_click_menu, popup.getMenu());
        popup.show();
    }

    public void showDeleteConfirmationAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                db.setLeaseInactive(selectedLease);
               // LeaseListFragment.leaseListAdapterNeedsRefreshed = true;

                //currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
                //leaseListAdapter.updateResults(currentFilteredLeases);
             //   leaseListAdapter.notifyDataSetChanged();
                //total = getTotal();
                //setTotalTV();
                //ExpenseViewActivity.this.finish();
                showDeleteAllRelatedMoneyAlertDialog();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showDeleteAllRelatedMoneyAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("AlertDialog");
        builder.setMessage("Remove all income/expenses related to this lease?");

        // add the buttons
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mCallback.onLeaseDataChanged();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                db.setAllExpensesRelatedToLeaseInactive(selectedLease.getId());
                db.setAllIncomeRelatedToLeaseInactive(selectedLease.getId());
                mCallback.onLeaseDataChanged();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_NEW_LEASE_FORM) {
            //If successful(not cancelled, passed validation)
            if (resultCode == RESULT_OK) {
                //currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
                //leaseListAdapter.updateResults(currentFilteredLeases);
                //leaseListAdapter.notifyDataSetChanged();
                mCallback.onLeaseDataChanged();
                //ApartmentListFragment.apartmentListAdapterNeedsRefreshed = true;
                //total = getTotal();
                //setTotalTV();
            }
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void setUpdateSelectedDateListeners() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
       // SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
      //  if (currentFilteredLeases != null) {
      //      outState.putParcelableArrayList("filteredLeases", currentFilteredLeases);
      //  }
      //  if(startDateRange != null){
      //      outState.putString("startDateRange", formatter.format(startDateRange));
      ///  }
      //  if(endDateRange != null){
      //      outState.putString("endDateRange", formatter.format(endDateRange));
      //  }
      //  if(apartment != null){
      //      outState.putParcelable("apartment", apartment);
      //  }
        //if(total != null){
        //    String totalString = total.toPlainString();
        //    outState.putString("totalString", totalString);
        //}
    }

    public void updateData(){
       // currentFilteredLeases = db.getUsersLeasesForApartment(MainActivity.user, apartment.getId());
        leaseListAdapter.updateResults(ViewModelProviders.of(getActivity()).get(ApartmentTenantViewModel.class).getLeaseArray().getValue());
        leaseListAdapter.notifyDataSetChanged();
        //this.total = getTotal();
        //setTotalTV();
    }
}