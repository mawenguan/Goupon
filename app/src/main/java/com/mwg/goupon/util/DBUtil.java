package com.mwg.goupon.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.Dao;
import com.mwg.goupon.bean.CityBean;
import com.mwg.goupon.bean.CityNameBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by mwg on 2018/1/23.
 */

public class DBUtil {

    Dao<CityNameBean, String> dao;
    DBHelper dbHelper;

    public DBUtil(Context context) {
        dbHelper = DBHelper.getINSTANCE(context);
        try {
            dao = dbHelper.getDao(CityNameBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(CityNameBean cityNameBean) {
        try {
            dao.createIfNotExists(cityNameBean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertAll(List<CityNameBean> list) {

        for (CityNameBean bean : list) {
            insert(bean);
        }
    }

    //批量插入数据
    public void insertBatch(final List<CityNameBean> list) {
        //建立连接后，一次性将数据全部写入，再断开连接
        try {
            dao.callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    for (CityNameBean bean : list) {
                        insert(bean);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CityNameBean> query() {
        try {
            List<CityNameBean> cityNameBeanList = dao.queryForAll();
            return cityNameBeanList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询数据库时出现异常");
        }
    }
}
