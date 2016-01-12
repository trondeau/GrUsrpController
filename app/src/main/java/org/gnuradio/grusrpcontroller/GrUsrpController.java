package org.gnuradio.grusrpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


public class GrUsrpController extends Activity {

    private EditText hostNameText;
    private EditText portNumberText;
    private EditText commandEditText;
    private Spinner commandSpinner;
    private Spinner blockNameSpinner;
    private Spinner unitsSpinner;
    ArrayAdapter<CharSequence> unitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gr_usrp_controller);

        hostNameText = (EditText) findViewById(R.id.hostNameEditText);
        portNumberText = (EditText) findViewById(R.id.portNumEditText);
        commandEditText = (EditText) findViewById(R.id.commandEditText);
        commandSpinner = (Spinner)findViewById(R.id.commandSpinner);
        blockNameSpinner = (Spinner)findViewById(R.id.blockNameSpinner);
        unitsSpinner = (Spinner)findViewById(R.id.unitsSpinner);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String hostName = sharedPref.getString(getString(R.string.hostname), getString(R.string.default_hostname));
        String portNumber = sharedPref.getString(getString(R.string.port), getString(R.string.default_port));
        String blockName = sharedPref.getString(getString(R.string.block_name), getString(R.string.default_block_name));
        String commandName = sharedPref.getString(getString(R.string.command), getString(R.string.default_command));
        String unitsValue = sharedPref.getString(getString(R.string.unit), getString(R.string.default_unit));
        String commandValue = getCommandValue(commandName);

        hostNameText.setText(hostName);
        portNumberText.setText(portNumber);
        commandEditText.setText(commandValue);

        blockNameSpinner = (Spinner)findViewById(R.id.blockNameSpinner);
        ArrayAdapter<CharSequence> blockNameAdapter = ArrayAdapter.createFromResource(this, R.array.blockNameSpinner, R.layout.spinner_style);
        blockNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockNameSpinner.setAdapter(blockNameAdapter);
        blockNameSpinner.setSelection(blockNameAdapter.getPosition(blockName));

        unitsSpinner = (Spinner)findViewById(R.id.unitsSpinner);
        unitsAdapter = ArrayAdapter.createFromResource(this, R.array.unitsFreqSpinner, R.layout.spinner_style);
        unitsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsAdapter);
        unitsSpinner.setSelection(unitsAdapter.getPosition(unitsValue));

        commandSpinner = (Spinner)findViewById(R.id.commandSpinner);
        ArrayAdapter<CharSequence> commandAdapter = ArrayAdapter.createFromResource(this, R.array.commandSpinner, R.layout.spinner_style);
        commandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commandSpinner.setAdapter(commandAdapter);

        commandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cmd = commandSpinner.getSelectedItem().toString();
                if (cmd.equals("gain") || cmd.equals("antenna") ) {
                    unitsSpinner.setVisibility(View.GONE);
                    unitsSpinner.setEnabled(false);
                } else {
                    unitsSpinner.setVisibility(View.VISIBLE);
                    unitsSpinner.setEnabled(true);
                }

                String commandValue = getCommandValue(cmd);
                commandEditText.setText(commandValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        commandSpinner.setSelection(commandAdapter.getPosition(commandName));
    }

    private String getCommandValue(String commandName) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String commandValue = "0";
        switch (commandName) {
            case ("freq"):
                commandValue = sharedPref.getString(getString(R.string.freq_value), getString(R.string.default_freq_value));
                break;
            case ("gain"):
                commandValue = sharedPref.getString(getString(R.string.gain_value), getString(R.string.default_gain_value));
                break;
            case ("rate"):
                commandValue = sharedPref.getString(getString(R.string.rate_value), getString(R.string.default_rate_value));
                break;
            case ("bandwidth"):
                commandValue = sharedPref.getString(getString(R.string.bandwidth_value), getString(R.string.default_bandwidth_value));
                break;
            case ("antenna"):
                commandValue = sharedPref.getString(getString(R.string.antenna_value), getString(R.string.default_antenna_value));
                break;
        }
        return commandValue;
    }

    private void putCommandValue(String commandName, String commandValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (commandName) {
            case ("freq"):
                editor.putString(getString(R.string.freq_value), commandValue);
                break;
            case ("gain"):
                editor.putString(getString(R.string.gain_value), commandValue);
                break;
            case ("rate"):
                editor.putString(getString(R.string.rate_value), commandValue);
                break;
            case ("bandwidth"):
                editor.putString(getString(R.string.bandwidth_value), commandValue);
                break;
            case ("antenna"):
                editor.putString(getString(R.string.antenna_value), commandValue);
                break;
        }
        editor.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        String hostName = hostNameText.getText().toString();
        String portNumber = portNumberText.getText().toString();
        String blockName = blockNameSpinner.getSelectedItem().toString();
        String commandName = commandSpinner.getSelectedItem().toString();
        String commandValue = commandEditText.getText().toString();
        String unitsValue = unitsSpinner.getSelectedItem().toString();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.hostname), hostName);
        editor.putString(getString(R.string.port), portNumber);
        editor.putString(getString(R.string.block_name), blockName);
        editor.putString(getString(R.string.command), commandName);
        editor.putString(getString(R.string.unit), unitsValue);
        putCommandValue(commandName, commandValue);
        editor.commit();
    }


    public void sendOnClick(View view) {
        Log.d("GrUsrpController", "sendOnClick Called");

        Intent intent = new Intent(this, CommandPoster.class);

        String hostName = hostNameText.getText().toString();
        String portNumber = portNumberText.getText().toString();
        String blockName = blockNameSpinner.getSelectedItem().toString();
        String commandName = commandSpinner.getSelectedItem().toString();
        String commandValue = commandEditText.getText().toString();
        String unitsValue = unitsSpinner.getSelectedItem().toString();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.hostname), hostName);
        editor.putString(getString(R.string.port), portNumber);
        editor.putString(getString(R.string.block_name), blockName);
        editor.putString(getString(R.string.command), commandName);
        editor.putString(getString(R.string.unit), unitsValue);
        putCommandValue(commandName, commandValue);
        editor.apply();

        Log.d("GrUsrpController", "sendOnClick Setting HostName Pref: " + hostName);
        Log.d("GrUsrpController", "sendOnClick Setting Port Num Pref: " + portNumber);

        intent.putExtra("org.gnuradio.grusrpcontroller.hostname", hostName);
        intent.putExtra("org.gnuradio.grusrpcontroller.portnumber", portNumber);
        intent.putExtra("org.gnuradio.grusrpcontroller.blockname", blockName);
        intent.putExtra("org.gnuradio.grusrpcontroller.commandname", commandName);
        intent.putExtra("org.gnuradio.grusrpcontroller.commandvalue", commandValue);
        intent.putExtra("org.gnuradio.grusrpcontroller.unitsvalue", unitsValue);

        Log.d("GrUsrpController", "Intent info " + commandName + ": " + commandValue + " "
                + unitsValue + " to block " + blockName);

        Log.d("GrUsrpController", "sendOnClick calling startActivity");
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gr_usrp_controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
