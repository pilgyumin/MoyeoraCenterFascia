package com.example.student.pad1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv_temp,tv_speed,tv_rpm,tv_battery,tv_temphope,tv_light;
    ImageView img_network,iv_battery,speedo_image,rpmmeter,rpmarrow;
    ImageButton temp_down,temp_up,light_up,light_down;
    RotateAnimation anim;
    Server server;
    Client client;
    int start,end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        anim = new RotateAnimation(0, 62,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        anim.setDuration(1000);
                        anim.setFillAfter(true);
                        //

                        tv_rpm.setText("0");
                        rpmarrow.startAnimation(anim);

                        start = 62;
                    }
                });
            }
        };
        new Thread(runnable).start();

        try {
            server = new Server();
            server.start();
            client = new Client();
            client.start();
            start  = 0;
            end = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        makeui();
    }


    private void makeui() {
        tv_temp = findViewById(R.id.tv_temp);
        tv_speed = findViewById(R.id.tv_speed);
        tv_rpm = findViewById(R.id.tv_rpm);
        tv_battery = findViewById(R.id.tv_battery);
        iv_battery = findViewById(R.id.iv_battery);
        img_network = findViewById(R.id.iv_network);

        temp_up = findViewById(R.id.temp_up);
        temp_down = findViewById(R.id.temp_down);

        tv_light = findViewById(R.id.tv_light);
        tv_temphope = findViewById(R.id.tv_temphope);

        light_up = findViewById(R.id.light_up);
        light_down = findViewById(R.id.light_down);
        speedo_image = findViewById(R.id.speedo_image);
        rpmmeter = findViewById(R.id.rpmmeter);
        rpmarrow = findViewById(R.id.rpmarrow);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.temp_up){
            int temp = Integer.parseInt(tv_temphope.getText().toString());

            temp = temp+1;
            tv_temphope.setText(String.valueOf(temp));
            if(temp < 10){
                server.sendMsg("00020009000000000000000"+temp);
            }
            else if(temp >= 10){
                server.sendMsg("0002000900000000000000"+temp);
            }
        }
        else if(v.getId() == R.id.temp_down){
            int temp = Integer.parseInt(tv_temphope.getText().toString());
            int presetemp = Integer.parseInt(tv_temp.getText().toString());

            temp = temp-1;
            tv_temphope.setText(String.valueOf(temp));
            if(temp < 10){
                server.sendMsg("00020009000000000000000"+temp);
            }
            else if(temp >= 10){
                server.sendMsg("0002000900000000000000"+temp);
            }
        }
        else if(v.getId() == R.id.light_up){
            int light = Integer.parseInt(tv_light.getText().toString());
            light += 1;

            if(light>3){
                Toast.makeText(this, "최대 조명 밝기입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            tv_light.setText(String.valueOf(light));
            server.sendMsg("00020005000000000000000"+light);
        }
        else if(v.getId() == R.id.light_down) {
            int light = Integer.parseInt(tv_light.getText().toString());
            light -= 1;

            if (light <= 0) {
                Toast.makeText(this, "최저 조명 밝기입니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            tv_light.setText(String.valueOf(light));
            server.sendMsg("00020005000000000000000"+light);
        }

    }

    public void setTemp(final String data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String d = data;
                        tv_temp.setText(d);
                    }
                });
            }
        };
        new Thread(runnable).start();
    } // end setTemp


    public void setLight(final String data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_speed.setText(data);
                    }
                });
            }
        };
        new Thread(runnable).start();
    }
    public void setSpeed(final String data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_speed.setText(data);

                        int intdata = Integer.parseInt(data);
                        if(intdata >= 0 && intdata <= 10)
                            speedo_image.setImageResource(R.drawable.cluster1);
                        else if(intdata > 10 && intdata <=20)
                            speedo_image.setImageResource(R.drawable.cluster2);
                        else if(intdata > 20 && intdata <=30)
                            speedo_image.setImageResource(R.drawable.cluster3);
                        else if(intdata > 30 && intdata <=40)
                            speedo_image.setImageResource(R.drawable.cluster4);
                        else if(intdata > 40 && intdata <=50)
                            speedo_image.setImageResource(R.drawable.cluster5);
                        else if(intdata > 50 && intdata <=60)
                            speedo_image.setImageResource(R.drawable.cluster6);
                        else if(intdata > 60 && intdata <=70)
                            speedo_image.setImageResource(R.drawable.cluster7);
                        else if(intdata > 70 && intdata <=80)
                            speedo_image.setImageResource(R.drawable.cluster8);
                        else if(intdata > 80 && intdata <=90)
                            speedo_image.setImageResource(R.drawable.cluster9);
                        else if(intdata > 90 && intdata <=100)
                            speedo_image.setImageResource(R.drawable.cluster10);
                        else if(intdata > 100 && intdata <=110)
                            speedo_image.setImageResource(R.drawable.cluster11);
                        else if(intdata > 110 && intdata <=120)
                            speedo_image.setImageResource(R.drawable.cluster12);
                        else if(intdata > 120 && intdata <=130)
                            speedo_image.setImageResource(R.drawable.cluster13);
                        else if(intdata > 130 && intdata <=140)
                            speedo_image.setImageResource(R.drawable.cluster14);
                        else if(intdata > 140 && intdata <=150)
                            speedo_image.setImageResource(R.drawable.cluster15);
                        else if(intdata > 150 && intdata <=160)
                            speedo_image.setImageResource(R.drawable.cluster16);
                        else if(intdata > 160 && intdata <=170)
                            speedo_image.setImageResource(R.drawable.cluster17);
                        else if(intdata > 170 && intdata <=180)
                            speedo_image.setImageResource(R.drawable.cluster18);
                        else if(intdata > 180 && intdata <=190)
                            speedo_image.setImageResource(R.drawable.cluster19);
                        else if(intdata > 190 && intdata <=200)
                            speedo_image.setImageResource(R.drawable.cluster20);
                        else if(intdata > 200 && intdata <=210)
                            speedo_image.setImageResource(R.drawable.cluster21);
                        else
                            speedo_image.setImageResource(R.drawable.cluster0);
                    }
                });
            }
        };
        new Thread(runnable).start();
    } // end setSpeed

    public void setRpm(final String data){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        end = Integer.parseInt(data)/250;
                        end*=7.5;
                        end+=62;

                        anim = new RotateAnimation(start,end,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        anim.setDuration(1000);
                        anim.setFillAfter(true);
                       //

                        tv_rpm.setText(data);
                        rpmarrow.startAnimation(anim);

                        start = end;
                    }
                });
            }
        };
        new Thread(runnable).start();
    } // end setRpm


    public void setBattery(final String data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       tv_battery.setText(data);
                        if(Integer.parseInt(data)>0 && Integer.parseInt(data)<=10)
                            iv_battery.setImageResource(R.drawable.b10);
                        else if(Integer.parseInt(data)>10 && Integer.parseInt(data)<=20)
                            iv_battery.setImageResource(R.drawable.b20);
                        else if(Integer.parseInt(data)>20 && Integer.parseInt(data)<=30)
                            iv_battery.setImageResource(R.drawable.b30);
                        else if(Integer.parseInt(data)>30 && Integer.parseInt(data)<=40)
                            iv_battery.setImageResource(R.drawable.b40);
                        else if(Integer.parseInt(data)>40 && Integer.parseInt(data)<=50)
                            iv_battery.setImageResource(R.drawable.b50);
                        else if(Integer.parseInt(data)>50 && Integer.parseInt(data)<=60)
                            iv_battery.setImageResource(R.drawable.b60);
                        else if(Integer.parseInt(data)>60 && Integer.parseInt(data)<=70)
                            iv_battery.setImageResource(R.drawable.b70);
                        else if(Integer.parseInt(data)>70 && Integer.parseInt(data)<=80)
                            iv_battery.setImageResource(R.drawable.b80);
                        else if(Integer.parseInt(data)>80 && Integer.parseInt(data)<=90)
                            iv_battery.setImageResource(R.drawable.b90);
                        else if(Integer.parseInt(data)>90 && Integer.parseInt(data)<=100)
                            iv_battery.setImageResource(R.drawable.b100);
                    }
                });
            }
        };
        new Thread(runnable).start();
    } // end setBattery


    public void setNetwork(final String data){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(data.equals("1")){
                            img_network.setImageResource(R.drawable.connect);
                        }else{
                            img_network.setImageResource(R.drawable.disconnect);
                        }
                    }
                });
            }
        };
        new Thread(runnable).start();
    } // end setBattery

    public void display(String msg){
        String carid = msg.substring(0,4);
        int cmd = Integer.parseInt(msg.substring(4,8));

        String vv = msg.substring(8);
        long num = Long.parseLong(vv);
        String value = String.valueOf(num);

        switch(cmd) {
            case 1:
                setRpm(value);
                break;
            case 2:
                setSpeed(value);
                break;
            case 3:
                setTemp(value);
                break;
            case 4:
                break;
            case 5:
                setLight(value);
                break;
            case 6:
                setBattery(value);
                break;

        }

        client.sendMsg(msg);


    }

    // Server Class
    public class Server extends Thread{

        ServerSocket serverSocket;
        int port = 8888;

        boolean flag = true;

        ArrayList<DataOutputStream> list;

        String client;
        Socket socket = null;

        public Server() throws IOException {
            list = new ArrayList<>();
            serverSocket = new ServerSocket(port);
        }

        public void run() {
            while(flag) {
                try {
                    socket = serverSocket.accept();
                    client = socket.getInetAddress().getHostAddress();
                    setNetwork("1");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, client+"에서 접속하셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    new Receiver(socket, client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void end(){
            if(socket!=null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMsg(String msg) {
            // BroadCast Msg
            Sender sender = new Sender();
            sender.setMsg(msg);
            sender.start();
        }


        class Receiver extends Thread{

            InputStream is;
            DataInputStream dis;
            String client;

            // For Sender..
            OutputStream os;
            DataOutputStream dos;

            public Receiver(Socket socket,String client) throws IOException {
                this.client = client;
                is = socket.getInputStream();
                dis = new DataInputStream(is);

                os = socket.getOutputStream();
                dos = new DataOutputStream(os);
                list.add(dos);
            }

            @Override
            public void run() {
                while(dis != null) {
                    try {
                        System.out.println("disutf");
                        String msg = dis.readUTF();
                        System.out.println(msg);
                        display(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                } // end while

                try {
                    setNetwork("2");
                    list.remove(dos);
                    Thread.sleep(100);
                    if(dis != null) {
                        dis.close();
                    }
                    if(dos !=null){
                        dos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        class Sender extends Thread{

            String msg;
            public void setMsg(String msg) {
                this.msg = msg;
            }

            @Override
            public void run() {
                if(list.size() == 0) {
                    return;
                }
                for(DataOutputStream out:list) {
                    if(out != null) {
                        try {
                            out.writeUTF(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    public class Client extends Thread{

        String host = "70.12.245.130";
        int port = 9999;

        Socket socket;
        OutputStream os;
        InputStream is;
        DataInputStream dis;
        DataOutputStream dos;

        boolean flag = true;
        Sender sender;

        public void run(){
            while(flag) {
                try {
                    socket = new Socket(host, port);
                    if(socket.isConnected()) {
                        os = socket.getOutputStream();
                        dos = new DataOutputStream(os);

                        is = socket.getInputStream();
                        dis = new DataInputStream(is);

                        break;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "connected..", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "retry..", Toast.LENGTH_SHORT).show();

                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            } // end while

            // Ready Receiver ...
                new Receiver().start();
        }

        class Receiver extends Thread{



            public Receiver() {
            }


            @Override
            public void run() {
                while(dis != null) {
                    final String msg;

                    try {
                        msg = dis.readUTF();
                        String id = msg.substring(3,11);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "apptocar"+msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.i("ssibal",id);
                        server.sendMsg(msg);
                       // ss.sendSerial(msg,id);


                        //setTextView2(msg);
                    } catch (IOException e) {

                        break;
                    }
                } // end while
                try {
                    Thread.sleep(1000);
                    if(socket != null) {
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMsg(String msg) {
            // BroadCast Msg
            Sender sender =  new Sender();
                sender.setMsg(msg);
                sender.start();


        }

        class Sender extends Thread{


            String msg;

            public Sender() {}

            public void setMsg(String msg) {
                this.msg = msg;
            }
            @Override
            public void run() {
                if(dos != null) {
                    try {
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

        public void end(){
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    //Client End
    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.end();
        client.end();
        System.exit(0);
    }
}

