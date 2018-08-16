package su.edu.kax.mobilepad.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import su.edu.kax.mobilepad.Constants;
import su.edu.kax.mobilepad.R;
import su.edu.kax.mobilepad.fragments.CommandControllFragment;
import su.edu.kax.mobilepad.fragments.MouseControllFragment;
import su.edu.kax.mobilepad.services.BluetoothPadService;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ControllActivity extends Activity /**/ {

    public final static UUID UUID_RX =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "BluetoothPadActivity";
    private static final int REQUEST_ENABLE_BT = 3;

    private final String ERROR_DEVICE_NOT_FOUND = "Device not found problem";


    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Context context = getApplicationContext();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothPadService.STATE_CONNECTED:
                            break;
                        case BluetoothPadService.STATE_CONNECTING:
                            break;
                        case BluetoothPadService.STATE_LISTEN:
                        case BluetoothPadService.STATE_NONE:
                            break;
                    }
                    break;
            }
            invalidateOptionsMenu();
        }
    };


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("Controll", action);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED) {
                } else if (bondState == BluetoothDevice.BOND_BONDING) {
                } else {
                }
                invalidateOptionsMenu();
            }

        }
    };


    private BluetoothSocket bluetoothSocket=null;
    private BluetoothDevice mainDevice;
    private BluetoothPadService padService = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();




    private int position=0;
    HashMap<Integer,Fragment> fragmentMap=new HashMap<>();
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction;


    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controll);
        Intent intent = getIntent();
        mainDevice = intent.getParcelableExtra(String.valueOf(R.string.name_bluetooth_intent));
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

    }


    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        padService = new BluetoothPadService(getApplicationContext(), mHandler);
        initializeFragments();
        updateFragment();
        setupPad();
    }

    @Override
    public void onStop(){
        super.onStop();
        padService.stop();
    }

    private int channel = 0;

    private void createConnectionToDevice() throws IOException {
        int bondedState = mainDevice.getBondState();
        if (bondedState == BluetoothDevice.BOND_NONE) {
            mainDevice.createBond();
            return;
        }

        if (bondedState == BluetoothDevice.BOND_BONDING) {
            return;
        }


        if (bondedState == BluetoothDevice.BOND_BONDED) {
            if (bluetoothSocket != null) {
                if (bluetoothSocket.isConnected()) {
                    bluetoothSocket.close();
                } else {
                    bluetoothSocket = mainDevice.createInsecureRfcommSocketToServiceRecord(UUID_RX);
                    for (ParcelUuid uuid : mainDevice.getUuids())
                        System.out.println(uuid);
                   setupPad();
                }

            } else {
                bluetoothSocket = mainDevice.createInsecureRfcommSocketToServiceRecord(UUID_RX);
                setupPad();
            }
        }

    }

    private BluetoothSocket getNextSocket() {
        try {
            return mainDevice.createInsecureRfcommSocketToServiceRecord(UUID_RX);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (padService != null) {
             padService.stop();
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (padService != null && bluetoothSocket!=null && bluetoothSocket.isConnected()) {
            if (padService.getState() == BluetoothPadService.STATE_NONE) {
                padService.start();
            }
        }else {
            setupPad();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connected, menu);
        if (mainDevice != null) {
            int bondedState = mainDevice.getBondState();
            if (bondedState == BluetoothDevice.BOND_BONDED) {
                if(bluetoothSocket!=null) {
                    if (bluetoothSocket.isConnected()) {
                menu.findItem(R.id.menu_connection_name).setTitle(mainDevice.getName());
                menu.findItem(R.id.menu_connection_state).setActionView(null);
                menu.findItem(R.id.menu_connection_state).setIcon(R.drawable.ic_bluetooth_enabled);
                return true;
                    }
                }
            } else if (bondedState == BluetoothDevice.BOND_BONDING) {
                menu.findItem(R.id.menu_connection_name).setTitle(mainDevice.getName());
                menu.findItem(R.id.menu_connection_state).setActionView(R.layout.actionbar_scanning_progress);
                menu.findItem(R.id.menu_connection_state).setIcon(null);
                return true;
            }
        }

        menu.findItem(R.id.menu_connection_name).setTitle(mainDevice.getName());
        menu.findItem(R.id.menu_connection_state).setActionView(null);
        menu.findItem(R.id.menu_connection_state).setIcon(R.drawable.ic_bluetooth_disabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connection_state:
                try {
                    createConnectionToDevice();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    private void setupPad() {
        Log.d(TAG, "setupPad()");
        if(bluetoothSocket==null){
            Toast.makeText(getApplicationContext(),"Nie mozna utworzyc polaczenia z urzadzeniem",Toast.LENGTH_LONG).show();
            Log.e(TAG,"Nie mozna utworzyc polaczenia");
        }
        else {
            new Thread() {
                public void run() {
            try
                {
                    bluetoothSocket.connect();
                } catch(
                IOException e)
                {
                    e.printStackTrace();
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    BluetoothSocket bluetoothSocket1 = getNextSocket();
                    if (bluetoothSocket1 == null)
                        return;
                    else {
                        bluetoothSocket = bluetoothSocket1;
                        return;
                    }
                }
            Log.d(TAG,"Tworze polaczenie");
                padService.setBluetoothSocket(bluetoothSocket);
            padService.start();
            }
        }.start();
        }
    }



    private void handleIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            onBackPressed();
            Toast.makeText(getApplicationContext(), ERROR_DEVICE_NOT_FOUND, Toast.LENGTH_LONG).show();
            return;
        } else {
            BluetoothDevice bluetoothDevice;
            bluetoothDevice = intent.getParcelableExtra(String.valueOf(R.string.name_bluetooth_intent));
            if (bluetoothDevice != null) {
                mainDevice = bluetoothDevice;
                Toast.makeText(this, bluetoothDevice.getAddress(), Toast.LENGTH_LONG).show();
            } else {
                this.finishActivity(123);
                Toast.makeText(this, "Nie paniatna", Toast.LENGTH_LONG).show();
            }


        }
    }


    private void updateFragment(){
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.commandLayout,fragmentMap.get(1));
        fragmentTransaction.commit();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mouseLayout,fragmentMap.get(0));
        fragmentTransaction.commit();
    }


    private void initializeFragments(){
        fragmentMap.put(0,new MouseControllFragment());
        fragmentMap.put(1,new CommandControllFragment());
    }


}

