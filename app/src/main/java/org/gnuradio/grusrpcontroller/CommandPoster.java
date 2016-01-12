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

            if(this.mCmdName != "antenna") {
                this.mCmdVald = Double.valueOf(mCmdVal);
                this.mCmdVald *= mUnitsVal;
            }
            this.mCmdVald = Double.valueOf(mCmdVal);
            this.mCmdVald = this.mCmdVald * this.mUnitsVal;
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

    private RunNetworkThread networkthread;

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
        if(unitsValue.equals(new String("Hz"))) {
            unitsMult = 1.0;
        }
        else if(unitsValue.equals(new String("kHz"))) {
            unitsMult = 1000.0;
        }
        else if(unitsValue.equals(new String("MHz"))) {
            unitsMult = 1000000.0;
        }
        else if(unitsValue.equals(new String("GHz"))) {
            unitsMult = 1000000000.0;
        }

        Log.d("CommandPoster", "Connecting to: " + hostName + ":" + port);
        Log.d("CommandPoster", "Issuing command " + commandName + ": " + commandValue + " "
                +unitsValue + " (" + unitsMult + ") " + " to block " + blockName);


        networkthread = new RunNetworkThread(hostName, port, blockName,
                commandName, commandValue, unitsMult);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(networkthread);

        startActivity(retIntent);
    }

}
