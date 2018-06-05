package su.edu.kax.mobilepad;

import android.app.Activity;
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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import su.edu.kax.mobilepad.services.BluetoothPadService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class ControllActivity extends Activity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    public final static UUID UUID_RX =
            UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String TAG = "BluetoothPadActivity";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final String DEBUG_TAG = "Gestures";
    private final String ERROR_DEVICE_NOT_FOUND = "Device not found problem";
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Context context = getApplicationContext();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothPadService.STATE_CONNECTED:
                            Toast.makeText(context, "Utworzono połączenie", Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothPadService.STATE_CONNECTING:
                            Toast.makeText(context, "Laczenie", Toast.LENGTH_LONG).show();
                            break;
                        case BluetoothPadService.STATE_LISTEN:
                        case BluetoothPadService.STATE_NONE:
                            Toast.makeText(context, "Utracono połączenie", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//Jesli znaleziono nowe urzadzenie
            Log.e("Controll", action);
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = device.getBondState();
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(context, "Powiązano", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                } else if (bondState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "Wiąże", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();
                } else {
                    Toast.makeText(context, "Nie powiazano", Toast.LENGTH_SHORT).show();
                    invalidateOptionsMenu();

                }

            }

        }
    };
    BluetoothSocket bluetoothSocket = null;
    BluetoothListeningThread bluetoothListeningThread;
    private GestureDetector mDetector;
    private Toast toast = null;
    private BluetoothDevice mainDevice;
    private BluetoothPadService padService = null;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controll);
        mDetector = new GestureDetector(this, this);
        mDetector.setOnDoubleTapListener(this);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onStart() {
        super.onStart();
        toast = Toast.makeText(this, "elo", Toast.LENGTH_SHORT);

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else if (padService == null) {
            setupPad();
        }


        Intent intent = getIntent();
        mainDevice = intent.getParcelableExtra(String.valueOf(R.string.name_bluetooth_intent));
    }

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
                    bluetoothSocket = mainDevice.createInsecureRfcommSocketToServiceRecord(mainDevice.getUuids()[0].getUuid());
                    for (ParcelUuid uuid : mainDevice.getUuids())
                        System.out.println(uuid);
                    bluetoothListeningThread = new BluetoothListeningThread(bluetoothSocket);
                    bluetoothListeningThread.start();
                }

            } else {
                bluetoothSocket = mainDevice.createInsecureRfcommSocketToServiceRecord(mainDevice.getUuids()[0].getUuid());
                bluetoothListeningThread = new BluetoothListeningThread(bluetoothSocket);
                bluetoothListeningThread.start();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (padService != null) {
            // padService.stop();
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (padService != null) {
            if (padService.getState() == BluetoothPadService.STATE_NONE) {
                padService.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_connected, menu);
        if (mainDevice != null) {
            int bondedState = mainDevice.getBondState();
            if (bondedState == BluetoothDevice.BOND_BONDED) {
//                if(bluetoothSocket!=null) {
//                    if (bluetoothSocket.isConnected()) {
                menu.findItem(R.id.menu_connection_name).setTitle(mainDevice.getName());
                menu.findItem(R.id.menu_connection_state).setActionView(null);
                menu.findItem(R.id.menu_connection_state).setIcon(R.drawable.ic_bluetooth_enabled);
                return true;
//                    }
//                }
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
            case R.id.menu_connection_state: //Skan, resetuj adapter i skanuj
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


        //padService = new BluetoothPadService(this, mHandler);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());

        if (velocityX > 30 || velocityY > 30) {
            String elo = "nie wiadomo gdzie";
            if (Math.abs(velocityX) > Math.abs(velocityY))
                elo = "w poziomie";
            else
                elo = "w pionie";
            toast.cancel();
            toast = Toast.makeText(getApplicationContext(), "Machniecie " + elo, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        toast.cancel();
        toast = Toast.makeText(getApplicationContext(), "Dlugie przytrzymanie", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
//        if(distanceX>10 || distanceY>10)
//        {
//       String elo="nie wiadomo gdzie";
//       if(distanceX>distanceY)
//           elo="w poziomie";
//       else
//           elo="w pionie";
//       toast.cancel();
//        toast=Toast.makeText(getApplicationContext(),"Scrollowanie "+elo,Toast.LENGTH_SHORT);
//        toast.show();
//        return true;}

        return true;

    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        toast.cancel();
        toast = Toast.makeText(getApplicationContext(), "Podwojne klikniecie", Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        toast.cancel();
        toast = Toast.makeText(getApplicationContext(), "Podwojne klikniecie", Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        toast.cancel();
        toast = Toast.makeText(getApplicationContext(), "Pojedyncze klikniecie", Toast.LENGTH_SHORT);
        toast.show();
        return true;
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

    public class BluetoothListeningThread extends Thread {

        BluetoothSocket bluetoothSocket;


        BluetoothListeningThread(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;

        }

        @Override
        public void run() {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bluetoothSocket.getInputStream()));
                String buffer;
                while (bluetoothSocket.isConnected() && (buffer = bufferedReader.readLine()) != null) {
                    Log.i(TAG, buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}

