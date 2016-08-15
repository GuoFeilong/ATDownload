package com.asiatravel.atdownload.db;

import com.asiatravel.atdownload.entity.ThreadInfo;

import java.util.List;

/**
 * Created by jsion on 16/8/11.
 */

public interface ThreadDao {
    /**
     * 插入线程信息
     */
    void intsertThread(ThreadInfo threadInfo);

    /**
     * 删除线程信息
     */
    void deleteThread(String url);

    /**
     * 更新线程信息
     */
    void updateTherad(String url, int thread_id, int finished);

    /**
     * 查询所有线程信息
     */
    List<ThreadInfo> getThreads(String url);

    /**
     * 线程信息是否存在,如果存在就更新
     */
    boolean isExistsTherad(String url, int thread_id);
}
