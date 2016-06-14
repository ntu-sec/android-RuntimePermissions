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

package sg.edu.ntu.testperm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewAnimator;

import java.util.Locale;

public class RTPermActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = "RTPermActivity";

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_CONTACTS = 1;

    private View mLayout;

    public void showCamera(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPerm(Manifest.permission.CAMERA, REQUEST_CAMERA);

        } else {
            Log.i(TAG, "CAMERA perm granted. Displaying camera...");
            showCameraPreview();
        }
    }

    private void requestCameraPerm(final String perm, final int requestCode) {
        Log.i(TAG, "CAMERA permission NOT granted. Requesting...");
        Log.d(TAG, String.format(Locale.getDefault(), "version %d", Build.VERSION.SDK_INT));
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
            Snackbar.make(mLayout, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(RTPermActivity.this,
                            new String[]{perm}, requestCode);
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
        }
    }

    public void showContacts(View v) {
        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            requestContactsPermissions(Manifest.permission.READ_CONTACTS, REQUEST_CONTACTS);

        } else {
            showContactDetails();
        }
    }

    private void requestContactsPermissions(final String perm, final int requestCode) {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_CONTACTS)) {

            Snackbar.make(mLayout, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(RTPermActivity.this, new String[]{perm},
                                            requestCode);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{perm}, requestCode);
        }
    }

    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_rtperm_sample, CameraPreviewFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }

    private void showContactDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_rtperm_sample, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();

            }
        } else if (requestCode == REQUEST_CONTACTS) {
            if (verifyPermissions(grantResults)) {
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length <= 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

//--------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);

        return super.onPrepareOptionsMenu(menu);
    }

    public void onBackClick(View view) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtperm);
        mLayout = findViewById(R.id.layout_rtperm);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RTPermFragment fragment = new RTPermFragment();
            transaction.replace(R.id.fragment_rtperm_sample, fragment);
            transaction.commit();
        }

    }
}
