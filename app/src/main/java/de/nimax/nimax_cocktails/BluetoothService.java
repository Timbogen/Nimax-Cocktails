package de.nimax.nimax_cocktails;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nimax.nimax_cocktails.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import de.nimax.nimax_cocktails.recipes.data.Bar;
import de.nimax.nimax_cocktails.recipes.data.Drink;
import de.nimax.nimax_cocktails.recipes.edit.RecipeEditAdapter;
import de.nimax.nimax_cocktails.settings.SettingsActivity;

public class BluetoothService {

    /**
     * The UUID
     */
    private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    /**
     * The bluetooth adapter
     */
    private static BluetoothAdapter bluetoothAdapter = null;
    /**
     * The socket connection
     */
    private static BluetoothSocket bluetoothSocket = null;

    /**
     * Method to guide trough the process of connecting the device
     *
     * @param activity that is currently active
     * @param view that was clicked
     */
    public static void connectDevice(Activity activity, final View view) {
        if (isConnected()) {
            makeToast(activity, activity.getString(R.string.bluetooth_connected));
            return;
        }
        // Disable the button
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(false);
                view.findViewById(R.id.settings_bluetooth_progress).setVisibility(View.VISIBLE);
            }
        });
        // Connect to the arduino in separate thread
        new Thread(new BluetoothConnection(activity, view)).start();
    }

    /**
     * Method to tell whether the bluetooth service is connected
     * @return true if device is connected to arduino
     */
    public static boolean isConnected() {
        return bluetoothSocket != null && bluetoothSocket.isConnected();
    }

    /**
     * Method to sent data to the arduino
     *
     * @param data expects the data to be sent
     */
    public static boolean sendData(String data) {
        try {
            bluetoothSocket.getOutputStream().write(("<" + data + ">").getBytes());
            return true;
        } catch (IOException e) {
            // Restart bluetooth socket
            bluetoothSocket = null;
            return false;
        }
    }

    /**
     * Method to read a command of the arduino
     */
    public static String readData() {
        try {
            StringBuilder command = new StringBuilder();
            while (true) {
                char received = (char) bluetoothSocket.getInputStream().read();
                if (received == '<') {
                    command = new StringBuilder();
                } else if (received == '>') {
                    return command.toString();
                } else {
                    command.append(received);
                }
            }
        } catch (IOException e) {
            // Restart bluetooth socket
            bluetoothSocket = null;
            return "END";
        }
    }

    /**
     * Method for displaying toasts
     *
     * @param activity that is currently active
     * @param message  to be shown
     */
    private static void makeToast(final Activity activity, final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Creates the connection to arduino
     *
     * @param activity that is currently active
     */
    private static void connectToArduino(Activity activity) {
        // Retrieve the information of the bluetooth devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Search for the arduino
        String name = null;
        String address = null;
        for (BluetoothDevice bt : pairedDevices) {
            if (bt.getName().equals("HC-05")) {
                name = bt.getName();
                address = bt.getAddress();
                break;
            }
        }

        // If the module wasn't found just return and give a notice to the user
        if (name == null || name.equals("")) {
            makeToast(activity, activity.getString(R.string.bluetooth_pair_device));
            return;
        }

        // Give a notice to the user and create the device
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
        // Cancel the adapter
        bluetoothAdapter.cancelDiscovery();

        // Create the socket and try to connect with it
        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
        } catch (IOException e) {
            makeToast(activity, activity.getString(R.string.bluetooth_no_connection));
        }
    }

    /**
     * Checks if there is a bluetooth adapter and if it is enabled
     *
     * @param activity that is currently active
     */
    private static boolean enableBluetooth(Activity activity) {
        // Get the bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Show a dialog to say that there is no bluetooth adapter
            makeToast(activity, activity.getString(R.string.bluetooth_no_bluetooth));
            return false;
        }

        if (bluetoothAdapter.isEnabled()) {
            return true;
        }
        // Ask the user to turn on bluetooth
        Intent turnBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(turnBluetoothOn, 1);

        // Test if it is turned on now
        return bluetoothAdapter.isEnabled();
    }

    /**
     * New thread for building bluetooth connection
     */
    private static class BluetoothConnection implements Runnable {

        /**
         * Currently active activity
         */
        Activity activity;
        /**
         * View that was clicked
         */
        View view;

        /**
         * Constructor
         */
        BluetoothConnection(Activity activity, View view) {
            this.activity = activity;
            this.view = view;
        }

        @Override
        public void run() {
            if (enableBluetooth(activity)) {
                // Connect to arduino
                connectToArduino(activity);
            }
            // If connection was built successfully load the data
            if (isConnected() && sendData("SETUP")) {
                String res = readData();
                // First get the non alcoholic drinks
                while (!res.equals("ALC")) {
                    // Get the right drink
                    int i = Integer.parseInt(res);
                    Drink drink = Bar.Drinks.values()[0].drinks[i];
                    // Get the amount
                    drink.amount = Integer.parseInt(readData());
                    SettingsActivity.nonAlcDrinks.add(drink);
                    // Get the next response
                    res = readData();
                }
                // Now get the alcoholic drinks
                res = readData();
                while (!res.equals("END")) {
                    // Get the right drink
                    int i = Integer.parseInt(res);
                    Drink drink = Bar.Drinks.values()[1].drinks[i];
                    // Get the amount
                    drink.amount = Integer.parseInt(readData());
                    SettingsActivity.alcDrinks.add(drink);
                    // Get the next response
                    res = readData();
                }
            }
            // Enable view
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Enable the clicked view
                    view.setEnabled(true);
                    view.findViewById(R.id.settings_bluetooth_progress).setVisibility(View.GONE);
                    // Update the status
                    TextView status = view.findViewById(R.id.settings_bluetooth_status);
                    if (isConnected()) {
                        status.setText(activity.getString(R.string.bluetooth_status_connected));
                        // Notify the user
                        makeToast(activity, activity.getString(R.string.bluetooth_connected));
                        // Make the other settings visible
                        activity.findViewById(R.id.settings_non).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.settings_alc).setVisibility(View.VISIBLE);
                    } else {
                        status.setText(activity.getString(R.string.bluetooth_status_disconnected));
                    }
                }
            });
        }
    }
}
