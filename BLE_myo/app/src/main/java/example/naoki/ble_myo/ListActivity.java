package example.naoki.ble_myo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ListActivity extends ActionBarActivity implements BluetoothAdapter.LeScanCallback {
    public static final int MENU_SCAN = 0;
    public static final int LIST_DEVICE_MAX = 5;

    public static String TAG = "BluetoothList";

    /** Device Scanning Time (ms) */
    private static final long SCAN_PERIOD = 5000;

    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt    mBluetoothGatt;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private String myoName = null;

    private ArrayAdapter<String> adapter;
    private String[] listMembers = new String[LIST_DEVICE_MAX];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mHandler = new Handler();

        ListView lv = (ListView) findViewById(R.id.listView1);

        Arrays.fill(listMembers,"-");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_expandable_list_item_1, listMembers);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                if (item.equals("-")) {
                    Toast.makeText(getApplicationContext(), "Check your device & Scan device", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), item + " connect", Toast.LENGTH_SHORT).show();
                    myoName = item;

                    Intent intent;
                    intent = new Intent(getApplicationContext() , MainActivity.class );

                    intent.putExtra( TAG, myoName );

                    startActivity(intent);

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_list, menu);
        menu.add(0, MENU_SCAN, 0, "Scan");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == MENU_SCAN) {
            scanDevice();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickScan(View v) {
        scanDevice();
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    // Device Log
        ParcelUuid[] uuids = device.getUuids();
        String uuid = "";
        if (uuids != null) {
            for (ParcelUuid puuid : uuids) {
                uuid += puuid.toString() + " ";
            }
        }

        String msg = "name=" + device.getName() + ", bondStatus="
                + device.getBondState() + ", address="
                + device.getAddress() + ", type" + device.getType()
                + ", uuids=" + uuid;
        Log.d("BLEActivity", msg);

        deviceNames.add(device.getName());
    }

    public void scanDevice() {
        deviceNames = new ArrayList<>();
        // Scanning Time out by Handler.
        // The device scanning needs high energy.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(ListActivity.this);

                int item_num = LIST_DEVICE_MAX;
                if (deviceNames.size()<item_num){
                    item_num = deviceNames.size();
                }

                for (int i_item = 0;i_item <item_num;i_item++) {
                    String device = deviceNames.get(i_item);
                    if (device == null){
                        device = "-";
                    }
                    listMembers[i_item] = device;
                }

                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Stop Device Scan", Toast.LENGTH_SHORT).show();

            }
        }, SCAN_PERIOD);
        mBluetoothAdapter.startLeScan(ListActivity.this);
    }

}