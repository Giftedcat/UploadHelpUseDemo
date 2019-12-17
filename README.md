# 前言

因为有涉及到静默安装，主要是针对已经root的设备，应需求，是在ARM的主板上开发的，所以对于常规手机仅能做到轮询下载，做不到静默安装。

# 效果图

![image](https://upload-images.jianshu.io/upload_images/20395467-ffb2cfbd0de88f43.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![image](https://upload-images.jianshu.io/upload_images/20395467-a4d97e8e5fd55a59.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

用的鸿洋大神的百分比布局，各个分辨率都完美适配，小case

# 工作流程图

还是得上一下流程图，方便理解，对自己也是一种总结

![image](https://upload-images.jianshu.io/upload_images/20395467-a030763e85e3a881.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# Dialog内部实现代码解析

#### 1.重写dialog的show函数，入参加一个字符串的列表，这样就可以获取到需要下载的apk的地址集合了
```
    /**
     * 获取需要下载的文件地址，显示弹窗
     */
    public void show(List<String> apkUrls) {
        show();
        content = "";
        this.apkUrls = apkUrls;
        //开始现在第一个app
        downloadApp(0);
    }
```

#### 2.判断索引index是否大于list的size，，超出了则说明已经全部下载完成了，可以结束轮询，关闭dialog了
```
    if (index == apkUrls.size()) {
        //index已超出范围，说明说有app已经下载完成
        setContent("\n全部下载完成!" + index + "/" + apkUrls.size() + "(3秒后关闭)");
        txt_dec.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
        return;
    }
```

#### 3.判断下载地址不为空，之后根据下载地址创建一个本地文件，并删除本地原有的同名文件（防止直接跳过了）
```
        String apkUrl = apkUrls.get(index);
        if (TextUtils.isEmpty(apkUrl)) {
            //如果apk的地址为空，则跳过，开始下载下一个
            downNextApp(index);
            return;
        }
        final String name = UrlUtils.getUrlFileName(apkUrl);
        final String localPath = Environment.getExternalStoragePublicDirectory("") + "/Download/" + name + ".apk";
        //如果已有同名文件将其删除
        deleteFile(localPath);
```

#### 4.调用HttpTools的下载函数，在下载完成回调内执行静默安装，并开始下一次轮询
```
        httpTools.download(apkUrl, localPath, true, new HttpCallback());
```
文件下载完成后回调函数内实现静默安装，并开始下一次轮询
```
        boolean installStatus = false;
        if (DeviceUtil.install(localPath)) {
            //安装成功
            installStatus = true;
        } else {
            //安装失败
            installStatus = false;
        }
        //完成后删除文件
        deleteFile(localPath);
        if (listener != null) {
            //回调给使用者
            listener.downloadFinish(installStatus, localPath);
        }
```

# 使用Dialog

#### 1.初始化Dialog
```
        DownLoadAppDialog downLoadAppDialog = new DownLoadAppDialog(mContext);
```
#### 2.增加下载完成的监听回调（可省略）
```
        downLoadAppDialog.setOnDownLoadListener(new DownLoadAppListener() {
            @Override
            public void downloadFinish(boolean installStatus, String path) {
                if (installStatus){
                    //安装成功
                    LogUtil.e( path + "安装成功");
                }else {
                    //安装失败
                    LogUtil.e( path + "安装失败");
                }
            }
        });
```
#### 3.在需要使用的时候，调用我们自己写的show函数
```
downLoadAppDialog.show(apkUrls);
```
