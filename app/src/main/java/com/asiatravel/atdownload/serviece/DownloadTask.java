package com.asiatravel.atdownload.serviece;

import android.content.Context;
import android.content.Intent;

import com.asiatravel.atdownload.ATConstants;
import com.asiatravel.atdownload.db.ThreadDao;
import com.asiatravel.atdownload.db.ThreadDaoImpl;
import com.asiatravel.atdownload.entity.FileInfo;
import com.asiatravel.atdownload.entity.ThreadInfo;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jsion on 16/8/11.
 */

public class DownloadTask {
    private FileInfo fileInfo;
    private Context context;
    private ThreadDao dao;

    private int finished;
    public boolean isPause;
    private int threadCount = 1; // 线程数量
    private List<DownLoadThread> threadList;


    public DownloadTask(FileInfo fileInfo, Context context, int threadCount) {
        this.fileInfo = fileInfo;
        this.context = context;
        this.threadCount = threadCount;
        dao = new ThreadDaoImpl(context);
    }

    public void download() {
        // 从数据库读取数据库的线程信息
        List<ThreadInfo> threadInfos = dao.getThreads(fileInfo.getUrl());
        // 第一次没有,自己实例化对象
        ThreadInfo threadInfo;
        if (threadInfos == null) return;
        if (threadInfos.size() == 0) {
            // 获取每隔线程下载的长度
            int length = fileInfo.getLength() / threadCount;
            // 创建线程信息
            for (int i = 0; i < threadCount; i++) {
                ThreadInfo tempThinfo = new ThreadInfo(i, fileInfo.getUrl(), length * i, (1 + i) * length - 1, 0);
                if (i == threadCount - 1) {
                    // 最后一个线程的结束长度为文件的长度,可能除不尽
                    tempThinfo.setEnd(fileInfo.getLength());
                }
                threadInfos.add(tempThinfo);
                // 向数据库中插入一条线程信息,如果线程不存在,插入数据库,如果存在更新
                dao.intsertThread(tempThinfo);
            }
        }
        threadList = new ArrayList<>();
        // 启动多个线程下载
        for (ThreadInfo info : threadInfos) {
            DownLoadThread thread = new DownLoadThread(info);
            DownloadService.executorService.execute(thread);
            threadList.add(thread);
        }
    }

    private class DownLoadThread extends Thread {
        private ThreadInfo threadInfo;
        public boolean isFinished;

        DownLoadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            super.run();
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            // 设置下载位置
            try {
                URL url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //初始的开始位置加上现成的完成位置
                int start = threadInfo.getStart() + threadInfo.getFinished();
                // 设置下载位置范围
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                File file = new File(DownloadService.DOWNLOAD_PATH, fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);
                // 定义intent更新进度,总文件
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                finished += fileInfo.getFinished();

                // 开始下载
                if (ATConstants.HTTP_SC_RAF_OK == conn.getResponseCode()) {
                    // 获取文件流
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    // 设置刷新频率
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        // 写入随机文件
                        raf.write(buffer, 0, len);
                        // 累加整个文件进度
                        finished += len;
                        // 累加每个线程的进度
                        threadInfo.setFinished(threadInfo.getFinished() + len);

                        if (System.currentTimeMillis() - time > 500) {
                            time = System.currentTimeMillis();
                            // 把下载进度发送广播通知activity
                            intent.putExtra(ATConstants.FILE_DOWN_FINISHIED_FLAG, finished * 100 / fileInfo.getLength());
                            fileInfo.setFinished(finished);
                            intent.putExtra(ATConstants.FILE_NAME_FLAG, fileInfo);
                            context.sendBroadcast(intent);
                        }
                        // 下载暂停的时候,记录下载的位置 保存进度到数据库
                        if (isPause) {
                            // 每个线程把当前进度保存
                            dao.updateTherad(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
                            return;
                        }
                    }
                    // 标示线程执行完毕
                    isFinished = true;
                    checkAllThreadFinished();
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            } finally {
                // 关闭数据流对象
                try {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (null != raf) {
                        raf.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    Logger.e(e.toString());
                }
            }

        }
    }

    /**
     * 检查所有的线程是否下载完毕
     *
     * @return
     */
    private synchronized void checkAllThreadFinished() {
        boolean allFinished = true;
        for (DownLoadThread thread : threadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            // 删除线程信息,下载完成
            dao.deleteThread(fileInfo.getUrl());
            // 像activity中发送广播告诉UI下载完毕
            Intent intent = new Intent(DownloadService.ACTION_FINISH);
            intent.putExtra(ATConstants.FILE_NAME_FLAG, fileInfo);
            intent.putExtra(ATConstants.FILE_DOWN_FINISHIED_FLAG, 100);
            fileInfo.setFinished(finished);
            context.sendBroadcast(intent);
        }
    }

}
