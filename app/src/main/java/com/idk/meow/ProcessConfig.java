package com.idk.meow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ProcessConfig implements Runnable {

    private static final int SCHEDULE_SIZE = Runtime.getRuntime().availableProcessors();

    private ScheduledExecutorService scheduledExecutorService;

    private String character = "utf-8";

    private InputStream inputStream;

    private String errStr;

    public String getErrStr() {
        return errStr;
    }


    public void setErrStr(String errStr) {
        this.errStr = errStr;
    }


    public ProcessConfig(InputStream inputStream) {
        this.inputStream = inputStream;
        scheduledExecutorService = null;
    }

    /**
     * 以utf-8的编码格式去读取文件
     */
    @Override
    public void run() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, character));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            errStr = sb.toString();
        } catch (UnsupportedEncodingException e) {
            //Log.error("执行解IO操作异常！" + e);
        } catch (IOException e) {
            //log.error("执行解IO操作异常！" + e);
        }finally{
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    //log.error("执行解IO操作异常！" + e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //log.error("执行解IO操作异常！" + e);
                }
            }
        }
    }

    public void start() {
        scheduledExecutorService = new ScheduledThreadPoolExecutor(SCHEDULE_SIZE,
                r -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    thread.setName("");
                    return thread;
                });
    }

}
