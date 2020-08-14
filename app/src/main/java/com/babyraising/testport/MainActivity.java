package com.babyraising.testport;

import android.bluetooth.BluetoothClass;
import android.serialport.SerialPortFinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aill.androidserialport.SerialPort;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnOpenSerialPortListener;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.serialportlibrary.service.impl.SerialPortBuilder;
import com.serialportlibrary.service.impl.SerialPortService;
import com.serialportlibrary.util.ByteStringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private Button test;
    private SerialPort serialPort;
    private SerialPortManager mSerialPortManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click();
            }
        });
//        test();
        test1();
    }

    private void test1() {
        String[] devicesPath = new SerialPortFinder().getDevicesPaths();
//        for(int i=0;i<devicesPath.length;i++){
//            System.out.println(devicesPath[i]);
//        }
        try {
            SerialPortService serialPortService = new SerialPortBuilder()
                    .setTimeOut(100L)
                    .setBaudrate(115200)
                    .setDevicePath("dev/ttyS0")
                    .createService();
            //发送byte数组数据

            byte[] receiveData = serialPortService.sendData("55AA0101010002");
            System.out.println("aa:" + ByteStringUtil.byteArrayToHexStr(receiveData));
            Log.e("MainActivity：", ByteStringUtil.byteArrayToHexStr(receiveData));
            serialPortService.isOutputLog(true);
        } catch (Exception e) {
            System.out.println("eeee:" + e.toString());
        }
    }

    private void click() {
        String content = "55AA0101010002";
        byte[] bytes = content.getBytes();
        boolean sendBytes = mSerialPortManager.sendBytes(bytes);
        System.out.println("send:" + sendBytes);
        if (sendBytes) {
            Toast.makeText(this, "发送信息成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "发送信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void test() {
        mSerialPortManager = new SerialPortManager();

        mSerialPortManager.setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {
                System.out.println("dd:" + device.getName());
            }

            @Override
            public void onFail(File device, Status status) {
                System.out.println("ff:" + device.getName());
                System.out.println("ffd:" + status);
//                startProcess(device);

//                SerialPort.setSuPath("/system/xbin/su");
//                try {
//                    serialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
//
//                } catch (IOException e) {
//                    System.out.println("sdds:"+e.toString());
//                    e.printStackTrace();
//                }
//
//                boolean openSerialPort = mSerialPortManager.openSerialPort(new File("/dev/ttyS1"), 115200);
//                if (openSerialPort) {
//                    Toast.makeText(getApplicationContext(), "重新打开串口成功/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "重新打开串口失败/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        mSerialPortManager.setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {
                System.out.println("ss:" + bytes.length);
                try {
                    Toast.makeText(getApplicationContext(), "你从串口接受到了：" + new String(bytes, "utf-8"), Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDataSent(byte[] bytes) {
                System.out.println("aa:" + bytes.length);
                try {
                    System.out.println("aaa:" + new String(bytes, "utf-8"));
                    Toast.makeText(getApplicationContext(), "你向串口发送了：" + new String(bytes, "utf-8"), Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        boolean openSerialPort = mSerialPortManager.openSerialPort(new File("/dev/ttyS0"), 115200);
        if (openSerialPort) {
            Toast.makeText(this, "打开串口成功/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "打开串口失败/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
        }

//        //默认su路径是/system/bin/su，有些设备su路径是/system/xbin/su
//        //在new SerialPort();之前设置su路径

//
//        new Timer().schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                //Do your task here
//                //从串口对象中获取输入流
//                System.out.println("ssssfsffs");
//                InputStream inputStream = serialPort.getInputStream();
//                //使用循环读取数据，建议放到子线程去
//                try {
//                    System.out.println(inputStream.available());
//                    if (inputStream.available() > 0) {
//                        //当接收到数据时，sleep 500毫秒（sleep时间自己把握）
//                        Thread.sleep(500);
//                        //sleep过后，再读取数据，基本上都是完整的数据
//                        byte[] buffer = new byte[inputStream.available()];
//                        int size = inputStream.read(buffer);
//                        System.out.println("size:" + size);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("eee:" + e.toString());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    System.out.println("jhh:" + e.toString());
//                }
//            }
//        }, 1000, 1000);
    }

    private void startProcess(final File device) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                /* Check access permission */  // 检查是否获取了指定串口的读写权限
                if (!device.canRead() || !device.canWrite()) {
                    try {
                        /* Missing read/write permission, trying to chmod the file */
                        // 如果没有获取指定串口的读写权限，则通过挂在到linux的方式修改串口的权限为可读写
                        Process su;
                        su = Runtime.getRuntime().exec("/system/bin/su");
                        String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                                + "exit\n";
                        su.getOutputStream().write(cmd.getBytes());
                        if ((su.waitFor() != 0) || !device.canRead()
                                || !device.canWrite()) {
                            throw new SecurityException();
                        }

                        boolean openSerialPort = mSerialPortManager.openSerialPort(new File("/dev/ttyS1"), 115200);
                        if (openSerialPort) {
                            Toast.makeText(getApplicationContext(), "重新打开串口成功/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "重新打开串口失败/dev/ttyS1,波特率115200", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "重新强制获取root权限失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        throw new SecurityException();
                    }
                }
            }
        }).start();
    }
}
