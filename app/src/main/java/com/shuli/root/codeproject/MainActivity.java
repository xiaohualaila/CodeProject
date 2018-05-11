package com.shuli.root.codeproject;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;



import hardware.Hardware;

public class MainActivity extends AppCompatActivity {
    private TextView recv_view;


    private Button open;
    private Button close;
    private Button exit;
    private Button clear;
    private Button send;
    private CheckBox hex_disply;
    private ScrollView scrollview;
    private Hardware hardware;
    private int fd_serial = -1;
    private int recv_flag = 1;
    private boolean flag = true;

    private String qianCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hardware = new Hardware();

        scrollview = (ScrollView)findViewById(R.id.scroll_view);

        open = (Button)findViewById(R.id.open);
        open.setText("OpenSerial");
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open.setClickable(false);
                recv_flag = 1;

                fd_serial = hardware.openSerialPort("/dev/ttyS3",115200,8,1);
                if(fd_serial > 0)
                {
                    recv_view.append("open success\n");
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    new readThread().start();//开始串口的监听线程
                }
                else
                {
                    recv_view.append("open error\n");
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }
        });

        close = (Button)findViewById(R.id.close);
        close.setText("CloseSerial");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recv_flag = 0;
                hardware.closeSerialPort(fd_serial);
                recv_view.append("close serial\n");
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                open.setClickable(true);
            }
        });

        exit = (Button)findViewById(R.id.exit);
        exit.setText("Exit");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recv_flag = 0;
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });

        clear = (Button)findViewById(R.id.clear);
        clear.setText("Clear");
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recv_view.setText("");
            }
        });

        send = (Button)findViewById(R.id.send);
        hex_disply = (CheckBox)findViewById(R.id.hex_disply);
        recv_view = (TextView)findViewById(R.id.recv_view);



    }

    public byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        public void handleMessage(Message msg) {

            byte[] buf= new byte[512];
            int len = hardware.readSerialPort(fd_serial, buf, 1000);

            if(len >0) {
                    byte[] newbuf = new byte[len];
                    for (int i = 0; i < len; i++) {
                        newbuf[i] = buf[i];
                    }

                String str ="";
                String str1 = "";
                String str2 = "";
                if(flag){
                    str1 =new String(newbuf);
                    qianCode = str1;
                    flag =panduan(str1,len);
                }else {
                    str2 =new String(newbuf);
                    str = qianCode + str2;
                    panduan(str,len);
                    qianCode = "";
                    flag = true;
                }
            }
        }
};

    public boolean panduan(String str, int len ){
        String s = str.substring(str.length()-1,str.length());
        String s1 = str.substring(0,1);
        if(s.equals("&") && s1.equals("&")){
            String  code = str.substring(1,str.length()-2);
            recv_view.append("len:"+len + "\n");
            recv_view.append("接收：" +code +  "\n");
            Log.i("sss",code);
            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            return true;
        }else {
            return false;
        }
    }


    class readThread extends Thread//监听串口信息线程
    {
        public void run() {

            while (recv_flag == 1) {
                if (hardware.select(fd_serial, 1, 0) == 1) {

                    //System.out.println(">>>>>>>>>sendMessage");

                    Message msg = new Message();
                    handler.sendMessage(msg);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        }
    }
}
