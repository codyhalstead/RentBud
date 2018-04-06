package com.rentbud.helpers;

import android.content.Context;
import android.util.Pair;

import com.rentbud.activities.MainActivity;
import com.rentbud.model.Apartment;
import com.rentbud.model.ExpenseLogEntry;
import com.rentbud.model.PaymentLogEntry;
import com.rentbud.model.Tenant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Cody on 3/20/2018.
 */

public class MainArrayDataMethods {

    public MainArrayDataMethods() {
    }

    public Tenant getCachedTenantByTenantID(int tenantID) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getId() == tenantID) {
                primaryTenant = MainActivity.tenantList.get(i);
                break;
            }
        }
        return primaryTenant;
    }

    public Apartment getCachedApartmentByApartmentID(int apartmentID) {
        Apartment apartment = null;
        for (int i = 0; i < MainActivity.apartmentList.size(); i++) {
            if (MainActivity.apartmentList.get(i).getId() == apartmentID) {
                apartment = MainActivity.apartmentList.get(i);
                break;
            }
        }
        return apartment;
    }

    public Tenant getCachedPrimaryTenantByApartmentID(int apartmentID) {
        Tenant primaryTenant = null;
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID && MainActivity.tenantList.get(i).getIsPrimary()) {
                primaryTenant = MainActivity.tenantList.get(i);
                break;
            }
        }
        return primaryTenant;
    }

    public ArrayList<Tenant> getCachedSecondaryTenantsByApartmentID(int apartmentID) {
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID && !MainActivity.tenantList.get(i).getIsPrimary()) {
                secondaryTenants.add(MainActivity.tenantList.get(i));
            }
        }
        return secondaryTenants;
    }

    public Pair<Tenant, ArrayList<Tenant>> getCachedPrimaryAndSecondaryTenantsByApartmentID(int apartmentID) {
        Tenant primaryTenant = null;
        ArrayList<Tenant> secondaryTenants = new ArrayList<>();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID) {
                if (MainActivity.tenantList.get(i).getIsPrimary()) {
                    primaryTenant = MainActivity.tenantList.get(i);
                } else {
                    secondaryTenants.add(MainActivity.tenantList.get(i));
                }
            }
        }
        return new Pair<>(primaryTenant, secondaryTenants);
    }

    public Pair<Tenant, ArrayList<Tenant>> getCachedSelectedTenantAndRoomMatesByIDs(int apartmentID, int tenantID) {
        Tenant selectedTenant = null;
        ArrayList<Tenant> otherTenants = new ArrayList<>();
        for (int i = 0; i < MainActivity.tenantList.size(); i++) {
            if (MainActivity.tenantList.get(i).getApartmentID() == apartmentID) {
                if (MainActivity.tenantList.get(i).getId() == tenantID) {
                    selectedTenant = MainActivity.tenantList.get(i);
                } else {
                    otherTenants.add(MainActivity.tenantList.get(i));
                }
            }
        }
        return new Pair<>(selectedTenant, otherTenants);
    }

    public void sortMainApartmentArray() {
        Collections.sort(MainActivity.apartmentList, new Comparator<Apartment>() {
            @Override
            public int compare(Apartment apartment, Apartment t1) {
                int b1 = apartment.isRented() ? 1 : 0;
                int b2 = t1.isRented() ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = apartment.getCity();
                    String s2 = t1.getCity();
                    return s1.compareTo(s2);
                }
            }
        });
    }

    public void sortMainTenantArray() {
        Collections.sort(MainActivity.tenantList, new Comparator<Tenant>() {
            @Override
            public int compare(Tenant tenant, Tenant t1) {
                int b1 = (tenant.getApartmentID() > 0) ? 1 : 0;
                int b2 = (t1.getApartmentID() > 0) ? 1 : 0;

                int comp = b2 - b1;
                if (comp != 0) {
                    return comp;
                } else {
                    String s1 = tenant.getFirstName();
                    String s2 = tenant.getFirstName();
                    comp = s1.compareTo(s2);
                    if (comp != 0) {
                        return comp;
                    } else {
                        String os1 = tenant.getLastName();
                        String os2 = tenant.getLastName();
                        return os1.compareTo(os2);
                    }
                }
            }
        });
    }

   // public ExpenseLogEntry getCachedExpenseByID(int expenseID) {
   //     ExpenseLogEntry expense = null;
   //     for (int i = 0; i < MainActivity.expenseList.size(); i++) {
   //         if (MainActivity.expenseList.get(i).getId() == expenseID) {
   //             expense = MainActivity.expenseList.get(i);
   //             break;
   //         }
   //     }
   //     return expense;
   // }

   // public PaymentLogEntry getCachedIncomeByID(int incomeID) {
   //     PaymentLogEntry income = null;
   //     for (int i = 0; i < MainActivity.incomeList.size(); i++) {
   //         if (MainActivity.incomeList.get(i).getId() == incomeID) {
   //             income = MainActivity.incomeList.get(i);
   //             break;
   //         }
   //     }
   //     return income;
   // }

   // public void sortMainIncomeArray() {
   //     Collections.sort(MainActivity.incomeList, new Comparator<PaymentLogEntry>() {
   //         @Override
   //         public int compare(PaymentLogEntry ple, PaymentLogEntry p1) {
   //
   //             return ple.getPaymentDate().compareTo(p1.getPaymentDate());
   //         }
   //     });
   // }

   // public void sortMainExpenseArray() {
   //     Collections.sort(MainActivity.expenseList, new Comparator<ExpenseLogEntry>() {
   //         @Override
   //         public int compare(ExpenseLogEntry ele, ExpenseLogEntry e1) {

   //             return ele.getExpenseDate().compareTo(e1.getExpenseDate());
   //         }
   //     });
   // }
}
