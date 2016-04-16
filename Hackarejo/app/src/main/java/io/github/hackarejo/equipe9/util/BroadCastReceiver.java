package io.github.hackarejo.equipe9.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import io.github.hackarejo.equipe9.MainActivity;
import io.github.hackarejo.equipe9.R;

/**
 * Created by paulosilvestre on 16/04/16.
 */
public class BroadCastReceiver  extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("Script", "-> Alarme");
//        ConectWifi connect = new ConectWifi(context);
//        connect.setConnection();

        String ssid = "Ampernet Hackarejo2016";
        String key = "cit@2016";


        WifiManager mainWifiObj;
        mainWifiObj = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        boolean statusWifi = mainWifiObj.isWifiEnabled();

        if (!statusWifi){
            mainWifiObj.setWifiEnabled(true);
        }

        boolean connect = false;
        List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
        for (ScanResult lista : wifiScanList){
            Log.i("REDE ", lista.toString());
            String[] data = lista.toString().split(",");

            String rede = data[0].toString().replace("SSID:","").trim();  //SSID:
            if (rede.equalsIgnoreCase(ssid)){
                Log.i("REDE", "Encontrei a Rede a Conectar");
                connect = true;
                break;
            }
        }

        if (connect){

            Log.i("REDE ATIVANDO", "INICIANDO ATIVAÇÃO WIFI");

            WifiConfiguration wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", ssid);
            wifiConfig.preSharedKey = String.format("\"%s\"", key);

            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            int netId = wifiManager.addNetwork(wifiConfig);

            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();

            //EFETUAR O PROCESSO DE ENVIO DE DADOS PARA SABER QUE O CLIENTE CONECTOU NA SUA WIFI
            Log.i("REDE COMUNICANDO", "ENVIANDO COMUNICADO AO LOJISTA");
            //FINAL DO PROCESSO DE ENVIAR COMUNICADO DIZENDO QUE SE CONECTOU A REDE

            if (!statusWifi){
                wifiManager.setWifiEnabled(false);
            }

            Log.i("REDE DESATIVANDO", "FINALIZANDO A CONEXÃO COM A WIFI");

        }



        //dispara a busca de informação no servidor baseado no alertmanager
        //gerarNotificacao(context, new Intent(context, MainActivity.class), "Nova mensagem", "Título", "Descrição nova mensagem");

    }

    public void gerarNotificacao(Context context, Intent intent, CharSequence ticker, CharSequence titulo, CharSequence descricao){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(titulo);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
        builder.setContentIntent(p);

        Notification n = builder.build();
        n.vibrate = new long[]{150, 300, 150, 600};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.ic_launcher, n);

        try{
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        }
        catch(Exception e){}
    }


}