package su.edu.kax.mobilepad.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import su.edu.kax.mobilepad.R;
import su.edu.kax.mobilepad.adapters.DeviceListAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * Activity responsible for listing bluetooth devices and getting thier
 * hardware bluetooth infromations
 */
public class ConnectionActivity extends ListActivity {

    private final int PERMISSION_REQUEST_CODE=966;
    private final int BLUETOOTH_REQUEST_CODE=967;
    private final int REQUEST_ENABLE_BT=968;
    private final int SCAN_PERIOD=20000;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private DeviceListAdapter mDeviceListAdapter;

    private boolean mScanning=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpPermissions();
        setUpBluetooth();
        mHandler=new Handler(); //Nowy Handler Taskow
    }


    /**
     * Broadcast receiver for checking found bluetooth devices and responding properly to theird
     * state change
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//Jesli znaleziono nowe urzadzenie
            Log.e("Scan", action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceListAdapter.addDevice(device);  //Dodaj do adaptera nowe urzadzenie i jego mac
                mDeviceListAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        mDeviceListAdapter = new DeviceListAdapter(this);
        setListAdapter(mDeviceListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanDevice(false);
        mDeviceListAdapter.clear();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * Sets up permisssions demanded for proper use of application
     */
    private void setUpPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsList=new LinkedList<>();
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);  //
            }
            if(permissionsList.size()!=0)
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), PERMISSION_REQUEST_CODE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connections, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_scanning_progress);
        }
        return true;
    }

    /**
     * Sets up bluetooth adapters and configurations properies
     */
    private void setUpBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        } else {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mDeviceListAdapter.clear();
                scanDevice(true);
                break;
            case R.id.menu_stop:
                scanDevice(false);
                break;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {// Jesli nie przydzielono wszystkim to wyjdz
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, R.string.error_permissions_not_granted, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                return;
            }
        }
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (mDeviceListAdapter != null) {
            BluetoothDevice bluetoothDevice;
            bluetoothDevice = mDeviceListAdapter.getDevice(position);
            Intent intent = new Intent(this, ControllActivity.class);
            intent.putExtra(String.valueOf(R.string.name_bluetooth_intent), bluetoothDevice);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (requestCode == BLUETOOTH_REQUEST_CODE && resultCode != Activity.RESULT_CANCELED) {
            scanDevice(true);
            return;
        } else if (requestCode == BLUETOOTH_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, R.string.error_bluetooth_not_enabled, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

    }

    /**
     * Function for setting adapter ability for scanning and finding devices
     * @param enable Scanning disabled/enabled
     */
    private void scanDevice(final boolean enable) {

        if (enable) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices())
                mDeviceListAdapter.addDevice(device);
            invalidateOptionsMenu();
            mHandler.postDelayed(new Runnable() {// Ustaw taska ktory zakonczy skanowanie po okreslonym czasie
                @Override                       //Skanowanie to ardzo kosztowna operacja
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.cancelDiscovery();
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startDiscovery();
        } else {
            mScanning = false;
            mBluetoothAdapter.cancelDiscovery();
        }
        invalidateOptionsMenu();
    }


}



