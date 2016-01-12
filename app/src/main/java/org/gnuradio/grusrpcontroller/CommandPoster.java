package org.gnuradio.grusrpcontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.gnuradio.grcontrolport.RPCConnection;
import org.gnuradio.grcontrolport.RPCConnectionThrift;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CommandPoster extends Activity {

    public class RunNetworkThread implements Runnable {

        private RPCConnection conn;
        private String mHost;
        private Integer mPort;
        private Boolean mConnected;
        private String mAlias, mCmdName, mCmdVal;
        private Double mCmdVald, mUnitsVal;

        RunNetworkThread(String host, Integer port,
                         String alias, String cmdname,
                         String cmdval, Double unitsval) {
            this.mHost = host;
            this.mPort = port;
            this.mAlias = alias;
            this.mCmdName = cmdname;
            this.mCmdVal = cmdval;
            this.mConnected = false;
            this.mUnitsVal = unitsval;

            this.mCmdVald = Double.valueOf(mCmdVal);
            if(!this.mCmdName.equals("antenna") && !this.mCmdName.equals("gain")) {
                this.mCmdVald *= mUnitsVal;
            }
        }

        public void run() {
            if(!mConnected) {
                Log.d("CommandPoster", "Getting Connection (" + mHost + ":" + mPort + ")");
                conn = new RPCConnectionThrift(mHost, mPort);
                mConnected = true;
                Log.d("CommandPoster", "Got Connection");
            }

            conn.postMessage(mAlias, "command", mCmdName, mCmdVald);
        }

        public RPCConnection getConnection() {
            if(conn == null) {
                throw new IllegalStateException("connection not established");
            }
            return conn;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CommandPoster", "Called On Create");

        super.onCreate(savedInstanceState);

        Intent retIntent = new Intent(this, GrUsrpController.class);
        Intent intent = getIntent();
        final String hostName = intent.getStringExtra("org.gnuradio.grusrpcontroller.hostname");
        final String portNumber = intent.getStringExtra("org.gnuradio.grusrpcontroller.portnumber");
        final String blockName = intent.getStringExtra("org.gnuradio.grusrpcontroller.blockname");
        final String commandName = intent.getStringExtra("org.gnuradio.grusrpcontroller.commandname");
        final String commandValue = intent.getStringExtra("org.gnuradio.grusrpcontroller.commandvalue");
        final String unitsValue = intent.getStringExtra("org.gnuradio.grusrpcontroller.unitsvalue");
        final Integer port = Integer.parseInt(portNumber);

        Double unitsMult = 1.0;
        switch (unitsValue) {
            case "Hz":
                unitsMult = 1.0;
                break;
            case "kHz":
                unitsMult = 1000.0;
                break;
            case "MHz":
                unitsMult = 1000000.0;
                break;
            case "GHz":
                unitsMult = 1000000000.0;
                break;
        }

        Log.d("CommandPoster", "Connecting to: " + hostName + ":" + port);
        Log.d("CommandPoster", "Issuing command " + commandName + ": " + commandValue + " "
                +unitsValue + " (" + unitsMult + ") to block " + blockName);

        RunNetworkThread networkthread;
        networkthread = new RunNetworkThread(hostName, port, blockName,
                commandName, commandValue, unitsMult);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(networkthread);

        startActivity(retIntent);
    }

}
