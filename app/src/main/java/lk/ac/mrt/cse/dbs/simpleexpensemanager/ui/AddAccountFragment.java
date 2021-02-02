/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.R;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants.EXPENSE_MANAGER;
/**
 *
 */
public class AddAccountFragment extends Fragment implements View.OnClickListener {
    private ExpenseManager currentExpenseManager;
    private Spinner accountSelector;
    private Button deleteAccountButton;

    private EditText accountNumber;
    private EditText bankName;
    private EditText accountHolderName;
    private EditText initialBalance;
    private Button addAccount;

    public static AddAccountFragment newInstance(ExpenseManager expenseManager) {
        AddAccountFragment addAccountFragment = new AddAccountFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXPENSE_MANAGER, expenseManager);
        addAccountFragment.setArguments(args);
        return addAccountFragment;
    }

    public void refreshing(){
        View rootView = getView();
        accountSelector = (Spinner) rootView.findViewById(R.id.account_selector);
        final List<String> accountsList = currentExpenseManager.getAccountNumbersList();
        ArrayAdapter<String> adapter = null;
        if (currentExpenseManager != null) {
            adapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item,
                                     accountsList);
        }
        accountSelector.setAdapter(adapter);


    }

    public AddAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_account, container, false);
        accountNumber = (EditText) rootView.findViewById(R.id.account_num);
        bankName = (EditText) rootView.findViewById(R.id.bank_name);
        accountHolderName = (EditText) rootView.findViewById(R.id.account_holder_name);
        initialBalance = (EditText) rootView.findViewById(R.id.initial_balance);
        addAccount = (Button) rootView.findViewById(R.id.add_account);
        addAccount.setOnClickListener(this);

        accountSelector = (Spinner) rootView.findViewById(R.id.account_selector);

        currentExpenseManager = (ExpenseManager) getArguments().get(EXPENSE_MANAGER);

        final List<String> accountsList = currentExpenseManager.getAccountNumbersList();

        ArrayAdapter<String> adapter = null;
        if (currentExpenseManager != null) {
            adapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item,
                    accountsList);
        }
        accountSelector.setAdapter(adapter);

        deleteAccountButton = (Button) rootView.findViewById(R.id.delete_account);

        deleteAccountButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_account:
                String accountNumStr = accountNumber.getText().toString();
                String bankNameStr = bankName.getText().toString();
                String accountHolderStr = accountHolderName.getText().toString();
                String initialBalanceStr = initialBalance.getText().toString();


                if (accountNumStr.isEmpty()) {
                    accountNumber.setError(getActivity().getString(R.string.err_acct_number_empty));
                    break;
                }

                if (bankNameStr.isEmpty()) {
                    bankName.setError(getActivity().getString(R.string.err_bank_name_empty));
                    break;
                }

                if (accountHolderStr.isEmpty()) {
                    accountHolderName.setError(getActivity().getString(R.string.err_acct_holder_empty));
                    break;
                }

                if (initialBalanceStr.isEmpty()) {
                    initialBalance.setError(getActivity().getString(R.string.err_init_balance_empty));
                    break;
                }

                if (currentExpenseManager != null) {
                    currentExpenseManager.addAccount(accountNumStr, bankNameStr, accountHolderStr,
                            Double.parseDouble(initialBalanceStr));
                }
                cleanUp();
                this.refreshing();
                break;

            case R.id.delete_account:
                //delete the selected account
                String selectedAccount = (String) accountSelector.getSelectedItem();

                if (currentExpenseManager != null) {
                    try {
                        currentExpenseManager.removeAccount(selectedAccount);
                        this.refreshing();
                    } catch (InvalidAccountException e) {
                        new AlertDialog.Builder(this.getActivity())
                                .setTitle(this.getString(R.string.msg_account_delete_unable) + selectedAccount)
                                .setMessage(e.getMessage())
                                .setNeutralButton(this.getString(R.string.msg_ok),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setIcon(android.R.drawable.ic_dialog_alert).show();

                    }
                }
                break;


        }
    }

    private void cleanUp() {
        accountNumber.getText().clear();
        bankName.getText().clear();
        accountHolderName.getText().clear();
        initialBalance.getText().clear();
    }
}