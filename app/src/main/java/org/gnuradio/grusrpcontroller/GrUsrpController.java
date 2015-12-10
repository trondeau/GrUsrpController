package org.gnuradio.grusrpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


public class GrUsrpController extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gr_usrp_controller);

        Spinner commandSpinner = (Spinner)findViewById(R.id.commandSpinner);
        ArrayAdapter<CharSequence> commandAdapter = ArrayAdapter.createFromResource(this, R.array.commandSpinner, R.layout.spinner_style);
        commandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commandSpinner.setAdapter(commandAdapter);

        Spinner blockNameSpinner = (Spinner)findViewById(R.id.blockNameSpinner);
        ArrayAdapter<CharSequence> blockNameAdapter = ArrayAdapter.createFromResource(this, R.array.blockNameSpinner, R.layout.spinner_style);
        blockNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blockNameSpinner.setAdapter(blockNameAdapter);
    }

    public void sendOnClick(View view) {
        Log.d("GrUsrpController", "sendOnClick Called");

        Intent intent = new Intent(this, CommandPoster.class);

        EditText hostNameText = (EditText) findViewById(R.id.hostNameEditText);
        EditText portNumberText = (EditText) findViewById(R.id.portNumEditText);
        EditText commandEditText = (EditText) findViewById(R.id.commandEditText);
        Spinner commandSpinner = (Spinner)findViewById(R.id.commandSpinner);
        Spinner blockNameSpinner = (Spinner)findViewById(R.id.blockNameSpinner);

        String hostName = hostNameText.getText().toString();
        String portNumber = portNumberText.getText().toString();
        String blockName = blockNameSpinner.getSelectedItem().toString();
        String commandName = commandSpinner.getSelectedItem().toString();
        String commandValue = commandEditText.getText().toString();

        intent.putExtra("org.gnuradio.grusrpcontroller.hostname", hostName);
        intent.putExtra("org.gnuradio.grusrpcontroller.portnumber", portNumber);
        intent.putExtra("org.gnuradio.grusrpcontroller.blockname", blockName);
        intent.putExtra("org.gnuradio.grusrpcontroller.commandname", commandName);
        intent.putExtra("org.gnuradio.grusrpcontroller.commandvalue", commandValue);

        Log.d("GrUsrpControlloer", "Intent info " + commandName + ": " + commandValue + " to block " + blockName);

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
