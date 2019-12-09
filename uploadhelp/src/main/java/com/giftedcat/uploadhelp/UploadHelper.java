package com.giftedcat.uploadhelp;

import android.app.Activity;

import com.giftedcat.uploadhelp.dao.AttendanceDao;
import com.giftedcat.uploadhelp.db.Attendance;
import com.giftedcat.uploadhelp.listener.OnUploadListener;
import com.giftedcat.uploadhelp.utils.FileUtil;
import com.giftedcat.uploadhelp.utils.LogUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UploadHelper {

    private static Queue<Attendance> queue;
    private static ExecutorService singleThreadExecutor;
    private static boolean isRun;
    private OnUploadListener<Attendance> onUploadListener;
    private Runnable upRunnable;

    public UploadHelper(Activity activity) {
        upRunnable = new UpRunnable(activity);
        queue = new LinkedList<>();
        isRun = true;
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 开启上传线程
     */
    public void startUpThread() {
        if (!isRun) {
            return;
        }
        singleThreadExecutor.execute(upRunnable);
    }

    /**
     * 自建一个Runnable判断activity是否销毁，防止内存泄露
     * */
    private class UpRunnable implements Runnable {

        private WeakReference<Activity> activityWeakReference;

        public UpRunnable(Activity activity) {
            //使用弱引用赋值
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            //判断activity是否已销毁
            if (activityWeakReference.get() != null){
                upRecord();
            }
        }
    }

    private void upRecord() {
        Attendance Attendance = queue.poll();
        if (null == Attendance) {
            //没有需要上传的文件
            LogUtils.d("上传队列为空 2秒后开始 检查是否存在上报");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handleLocalAttendance();
            startUpThread();
        } else {
            //有需要上传的文件，回调给页面
            if (onUploadListener != null) {
                onUploadListener.onUpload(Attendance);
            } else {
                startUpThread();
            }
        }
    }

    /**
     * 查询是否有上传任务
     */
    private void handleLocalAttendance() {
        List<Attendance> attendances = DBHelper.getInstance().getSession().getAttendanceDao()
                .queryBuilder().where(AttendanceDao.Properties.IsUpload.eq(false))
                .list();
        if (null != attendances && attendances.size() > 0) {
            queue.addAll(attendances);
        }
    }

    /**
     * 将记录标记为上传成功
     */
    private void upLocatAttenOk(Attendance Attendance) {
        Attendance.setIsUpload(true);
        DBHelper.getInstance().getSession().getAttendanceDao().update(Attendance);
    }

    /**
     * 上传文件成功回调
     */
    public void uploadSuccess(Attendance Attendance) {
        LogUtils.d("上传成功，开始检测队列是否为空");
        //删除文件
        FileUtil.deleteFile(new File(Attendance.getPath()));
        //上传成功 将本条记录 标记为成功
        upLocatAttenOk(Attendance);
        //开始下一次的上传
        startUpThread();
    }

    /**
     * 上传文件失败回调
     */
    public void uploadFaild() {
        //开始下一次的上传
        startUpThread();
    }

    /**
     * 设置检测需要上传文件的回调
     */
    public void setOnUploadListener(OnUploadListener onUploadListener) {
        this.onUploadListener = onUploadListener;
    }

    /**
     * 添加记录
     * */
    public void addRecord(Attendance data){
        DBHelper.getInstance().getSession().getAttendanceDao().insert(data);
    }

    /**
     * 结束轮询线程
     */
    public void stopUpThread() {
        isRun = false;
    }

}
