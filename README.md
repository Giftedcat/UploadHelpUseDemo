# Android中实现异步轮询上传文件
# 前言

前段时间要求项目中需要实现一个刷卡考勤的功能，因为涉及到上传图片文件，为加快考勤的速度，封装了一个异步轮询上传文件的帮助类

# 效果 

先上效果图

![image](https://upload-images.jianshu.io/upload_images/20395467-26c5a479336da416.gif?imageMogr2/auto-orient/strip)

# 设计思路

![image](https://upload-images.jianshu.io/upload_images/20395467-9a0cdad785df42df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

数据库使用的框架是GreenDao，一个非常好用的东西

# 先创建一个GreenDao的数据表的实体
来保存我们的考勤记录，我这边只写了一下几个参数，方便大家观看，使用的时候大家记得要编译一下来生成Dao文件跟get，set方法
```
@Entity
public class Attendance {

    @Id(autoincrement = true)
    public Long id;

    /**
     * 是否已上传
     * */
    public Boolean isUpload;

    /**
     * 文件路径
     * */
    public String path;

    /**
     * 姓名
     * */
    private String name;

    /**
     * 考勤时间
     * */
    private Date attendanceDate;

}
```

# 帮助类的实现

首先是轮询线程判断是否运行

```
    /**
     * 开启上传线程
     */
    public void startUpThread() {
        if (!isRun) {
            return;
        }
        singleThreadExecutor.execute(upRunnable);
    }
```

线程需要注意内存泄露，这个是必须的

```
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
```

先查询队列判断是否有数据需要上传

没有需要上传的数据延迟两秒后从数据库查询并填充队列

开始下一次的轮询

```
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
```

查询数据库的代码

```
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
```

# 帮助类的使用

首先是先在初始化帮助类

```
        UploadHelper uploadHelper = new UploadHelper(this);
        uploadHelper.setOnUploadListener(new OnUploadListener() {
            @Override
            public void onUpload(Attendance attendance) {
                //有需要上传的文件
                uploadToServer(attendance);
            }
        });
        uploadHelper.startUpThread();
```

接口调用成功后标记成功，开始下一次的轮询

```
uploadHelper.uploadSuccess(dataModel);
```

在打卡回调中添加数据库记录，这样轮询线程就会查到

```
//数据库
Attendance attendance = new Attendance();
attendance.setPath(Environment.getExternalStorageDirectory() + "/" + "Images/picture.png");
attendance.setIsUpload(false);
attendance.setName("张三");
attendance.setAttendanceDate(new Date());
uploadHelper.addRecord(attendance);
updateDataList();
```

最后再贴上源码：[https://github.com/Giftedcat/UploadHelpUseDemo](https://github.com/Giftedcat/UploadHelpUseDemo)
