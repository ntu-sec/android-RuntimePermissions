/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.system.runtimepermissions.contacts;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.system.runtimepermissions.R;

import java.util.ArrayList;

// https://developer.android.com/training/contacts-provider/retrieve-names.html

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static int counter = 0;

    private static final String TAG = "Contacts";
    private TextView mMessageText = null;

    @SuppressWarnings("FieldCanBeLocal")
    private static String DUMMY_CONTACT_NAME = "__DUMMY__";

    private static final String[] PROJECTION = {ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};

    private static final String ORDER = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC";

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        mMessageText = (TextView) rootView.findViewById(R.id.contact_message);

        // Register a listener to add a dummy contact when a button is clicked.
        Button button = (Button) rootView.findViewById(R.id.contact_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDummyContact();
            }
        });

        // Register a listener to display the first contact when a button is clicked.
        button = (Button) rootView.findViewById(R.id.contact_load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadContact();
            }
        });
        return rootView;
    }

    private void loadContact() {
        getLoaderManager().restartLoader(24, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, PROJECTION,
                null, null, ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor != null) {
            final int totalCount = cursor.getCount();
            if (totalCount > 0) {
                cursor.moveToFirst();
                String name = cursor
                        .getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mMessageText.setText(
                        getResources().getString(R.string.contacts_string, totalCount, name));
                Log.d(TAG, "First contact loaded: " + name);
                Log.d(TAG, "Total number of contacts: " + totalCount);
            } else {
                Log.d(TAG, "List of contacts is empty.");
                mMessageText.setText(R.string.contacts_empty);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMessageText.setText(R.string.contacts_empty);
    }

    private void insertDummyContact() {
        // Two operations are needed to insert a new contact.
        ArrayList<ContentProviderOperation> operations = new ArrayList<>(2);

        // First, set up a new raw contact.
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(op.build());

        // Next, set the name for the contact.
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        DUMMY_CONTACT_NAME + String.valueOf(counter));
        operations.add(op.build());

        // Apply the operations.
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        }
    }
}
