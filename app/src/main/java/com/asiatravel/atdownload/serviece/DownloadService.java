package com.asiatravel.atdownload.serviece;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.asiatravel.atdownload.ATDownLoadConstants;
import com.asiatravel.atdownload.entity.FileInfo;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jsion on 16/8/11.
 */

public class DownloadService extends Service {
    public static final String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downpath/";
    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_UPDATE = "action_update";
    public static final String ACTION_FINISH = "action_finish";

    public static final int MSG_INT = 99;
    private Map<Integer, DownloadTask> downloadTasks = new LinkedHashMap<>();
    // 定义线程池
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_INT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Logger.e("----->>>HANDER--" + fileInfo);
                    // 拿到结果后启动下载任务
                    DownloadTask downloadTask = new DownloadTask(fileInfo, DownloadService.this, ATDownLoadConstants.DOWNLOAD_THREAD_COUNT);
                    downloadTask.download();
                    // 把下载任务添加到集合
                    downloadTasks.put(fileInfo.getId(), downloadTask);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.endsWith(intent.getAction())) {
            FileInfo fileInfo = intent.getParcelableExtra(ATDownLoadConstants.FILE_NAME_FLAG);
            Logger.e(ACTION_START + "---->>>>" + fileInfo);
            DownloadService.executorService.execute(new InitThread(fileInfo));
        } else if (ACTION_STOP.endsWith(intent.getAction())) {
            FileInfo fileInfo = intent.getParcelableExtra(ATDownLoadConstants.FILE_NAME_FLAG);
            Logger.e(ACTION_STOP + "---->>>>>" + fileInfo);
            // 从下载集合中取出下载任务,根据id取出,
            DownloadTask downloadTask = downloadTasks.get(fileInfo.getId());
            if (downloadTask != null) {
                downloadTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class InitThread extends Thread {
        private FileInfo fileInfo;

        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection httpURLConnection = null;
            RandomAccessFile raf = null;
            try {
                URL url = new URL(fileInfo.getUrl());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                int length;
                if (ATDownLoadConstants.HTTP_SC_OK == httpURLConnection.getResponseCode()) {
                    length = httpURLConnection.getContentLength();
                    if (length <= 0) {
                        return;
                    }

                    File dir = new File(DOWNLOAD_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    File file = new File(dir, fileInfo.getFileName());
                    raf = new RandomAccessFile(file, "rwd");
                    raf.setLength(length);
                    fileInfo.setLength(length);
                    handler.obtainMessage(MSG_INT, fileInfo).sendToTarget();
                }


            } catch (Exception e) {
                Logger.e(e.toString());
            } finally {
                try {
                    if (null != raf) {
                        raf.close();
                    }
                    if (null != httpURLConnection) {
                        httpURLConnection.disconnect();
                    }
                } catch (IOException e) {
                    Logger.e(e.toString());
                }
            }

        }
    }
}
