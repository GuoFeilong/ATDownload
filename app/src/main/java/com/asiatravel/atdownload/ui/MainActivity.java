package com.asiatravel.atdownload.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.asiatravel.atdownload.ATConstants;
import com.asiatravel.atdownload.R;
import com.asiatravel.atdownload.db.ThreadDao;
import com.asiatravel.atdownload.db.ThreadDaoImpl;
import com.asiatravel.atdownload.entity.FileInfo;
import com.asiatravel.atdownload.entity.ThreadInfo;
import com.asiatravel.atdownload.serviece.DownloadService;
import com.orhanobut.logger.Logger;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String APP_URL = "http://appdown.atrip.com/download/Android/asiatravel_v2.0.0_44.apk";
    private static final String FILE_NAME = "asiatravel.apk";
    private Button start;
    private Button pause;
    private ProgressBar progressBar;
    private FileInfo fileInfo;
    private BroadcastReceiver broadcastReceiver;
    private ThreadDao threadDao;
    private TextView persent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        persent = (TextView) findViewById(R.id.tv_persent);

        threadDao = new ThreadDaoImpl(this);
        int finished = 0, length = 0;
        List<ThreadInfo> threads = threadDao.getThreads(APP_URL);
        if (null != threads && threads.size() > 0) {
            ThreadInfo threadInfo = threads.get(0);
            finished = threadInfo.getFinished();
            length = threadInfo.getEnd();
        }

        fileInfo = new FileInfo(0, FILE_NAME, APP_URL, 0, 0);

        start = (Button) findViewById(R.id.btn_start);
        pause = (Button) findViewById(R.id.btn_pause);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        progressBar.setMax(100);
//        progressBar.setProgress(length * 100 /finished);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra(ATConstants.FILE_NAME_FLAG, fileInfo);
                intent.setAction(DownloadService.ACTION_START);
                startService(intent);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DownloadService.class);
                intent.putExtra(ATConstants.FILE_NAME_FLAG, fileInfo);
                intent.setAction(DownloadService.ACTION_STOP);
                startService(intent);
            }
        });

        /**
         * 更新UI的广播接收
         */
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int finished = 0;
                if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                    finished = intent.getIntExtra(ATConstants.FILE_DOWN_FINISHIED_FLAG, 0);
                    progressBar.setProgress(finished);
                    fileInfo = intent.getParcelableExtra(ATConstants.FILE_NAME_FLAG);
                    Logger.e("百分比:--->>" + finished + "---->>文件ID" + fileInfo.getId());
                } else if (DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                    Logger.e("下载完毕---->>文件信息--" + fileInfo.toString());
                    finished = intent.getIntExtra(ATConstants.FILE_DOWN_FINISHIED_FLAG, 0);
                    progressBar.setProgress(100);
                }

                persent.setText(finished + "%");
            }
        };
        // 动态的注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_UPDATE);
        intentFilter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
