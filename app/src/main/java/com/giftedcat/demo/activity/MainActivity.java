package com.giftedcat.demo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.giftedcat.demo.R;
import com.giftedcat.demo.adapter.AttendanceAdapter;
import com.giftedcat.demo.http.HttpManager;
import com.giftedcat.uploadhelp.DBHelper;
import com.giftedcat.uploadhelp.UploadHelper;
import com.giftedcat.uploadhelp.db.Attendance;
import com.giftedcat.uploadhelp.listener.OnUploadListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    UploadHelper uploadHelper;

    Unbinder unbinder;

    AttendanceAdapter adapter;
    List<Attendance> list;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        initUploadHelp();
        initList();
    }

    /**
     * 初始化列表
     * */
    private void initList() {
        list = new ArrayList<>();
        adapter = new AttendanceAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        updateDataList();
    }

    /**
     * 更新列表记录
     * */
    private void updateDataList(){
        List<Attendance> dataModels = DBHelper.getInstance().getSession().getAttendanceDao()
                .queryBuilder()
                .list();
        list.clear();
        list.add(new Attendance());
        list.addAll(dataModels);
        adapter.notifyDataSetChanged();
    }

    /**
     * 初始化上传工具
     * */
    private void initUploadHelp() {
        uploadHelper = new UploadHelper(this);
        uploadHelper.setOnUploadListener(new OnUploadListener() {
            @Override
            public void onUpload(Attendance attendance) {
                //有需要上传的文件
                uploadToServer(attendance);
            }
        });
        uploadHelper.startUpThread();
    }

    /**
     * 模拟调用服务端接口将上传至服务器
     * */
    private void uploadToServer(final Attendance dataModel){
        addSubscribe(HttpManager.upload()
                .observeOn(AndroidSchedulers.mainThread()) //线程调度
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        //文件上传成功回调
                        uploadHelper.uploadSuccess(dataModel);
                        updateDataList();
                    }
                }));
    }

    @OnClick({R.id.btn_add})
    public void OnCLickView(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                //添加上传记录
                Attendance attendance = new Attendance();
                attendance.setPath(Environment.getExternalStorageDirectory() + "/" + "Images/picture.png");
                attendance.setIsUpload(false);
                attendance.setName("张三");
                attendance.setAttendanceDate(new Date());
                uploadHelper.addRecord(attendance);
                updateDataList();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        uploadHelper.stopUpThread();
        unbinder.unbind();
    }
}
