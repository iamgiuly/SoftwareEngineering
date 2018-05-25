package com.ids.ids.toServer.ServerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.ids.ids.notifica.RegistrationTokenService;
import com.ids.ids.utils.Parametri;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Task per l invio del broadcast al server in ascolto
 */
public class Discover  extends AsyncTask<Void, Void, String> {

    private ProgressDialog configurazione_ip;
    private Context context;

    public Discover(Context contxt) {

        context = contxt;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
        configurazione_ip = new ProgressDialog(context);
        configurazione_ip.setIndeterminate(true);
        configurazione_ip.setCancelable(false);
        configurazione_ip.setCanceledOnTouchOutside(false);
        configurazione_ip.setMessage("Ricerca ip server in corso..");
        configurazione_ip.show();

    }

    // tutto il codice da eseguire in modo asincrono deve essere inserito nel metodo doInBackground
    // Quando l'operazione termina il risultato viene restituito tramite il metodo onPostExecute.
    @Override
    protected String doInBackground(Void... arg0) {

        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            DatagramSocket c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 4849);
                c.send(sendPacket);
                System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
               // System.out.println("i");
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp())
                    continue; // Don't want to broadcast to the loopback interface

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {

                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 4849);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            String message = new String(receivePacket.getData()).trim();

            if (message.equals("DISCOVER_FUIFSERVER_RESPONSE")) {
                //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                //System.out.println(receivePacket.getAddress());
                return receivePacket.getAddress().toString();
            }

            //Close the port!
            c.close();

        } catch (IOException ex) {
            Log.i("errore", "errore");
        }
       return null;
    }

    @Override
    protected void onProgressUpdate(Void... arg0) {

    }

    @Override
    protected void onPostExecute(String result) {

        super.onPostExecute(result);

        if(result != null){
            configurazione_ip.dismiss();
            System.out.println(result);
            Parametri.setPath(result);

            Intent intent = new Intent(context, RegistrationTokenService.class);
            context.startService(intent);
        }
    }
}
