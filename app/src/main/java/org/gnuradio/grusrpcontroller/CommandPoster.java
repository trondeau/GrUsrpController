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

        RunNetworkThread(String host, Integer port,
                         String alias, String cmdname, String cmdval) {
            this.mHost = host;
            this.mPort = port;
            this.mAlias = alias;
            this.mCmdName = cmdname;
            this.mCmdVal = cmdval;
            this.mConnected = false;
        }

        public void run() {
            if(!mConnected) {
                Log.d("CommandPoster", "Getting Connection (" + mHost + ":" + mPort + ")");
                conn = new RPCConnectionThrift(mHost, mPort);
                mConnected = true;
                Log.d("CommandPoster", "Got Connection");
            }

            conn.postMessage(mAlias, "command", mCmdName, Double.valueOf(mCmdVal));
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
        //setContentView(R.layout.activity_gr_usrp_controller);

        Intent retIntent = new Intent(this, GrUsrpController.class);
        Intent intent = getIntent();
        final String hostName = intent.getStringExtra("org.gnuradio.grusrpcontroller.hostname");
        final String portNumber = intent.getStringExtra("org.gnuradio.grusrpcontroller.portnumber");
        final String blockName = intent.getStringExtra("org.gnuradio.grusrpcontroller.blockname");
        final String commandName = intent.getStringExtra("org.gnuradio.grusrpcontroller.commandname");
        final String commandValue = intent.getStringExtra("org.gnuradio.grusrpcontroller.commandvalue");

        final Integer port = Integer.parseInt(portNumber);
        Log.d("CommandPoster", "Connecting to: " + hostName + ":" + port);
        Log.d("CommandPoster", "Issuing command " + commandName + ": " + commandValue + " to block " + blockName);

        networkthread = new RunNetworkThread(hostName, port, blockName, commandName, commandValue);

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(networkthread);

        startActivity(retIntent);
    }

}
