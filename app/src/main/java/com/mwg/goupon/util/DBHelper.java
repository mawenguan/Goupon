package com.mwg.goupon.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mwg.goupon.bean.CityNameBean;

import java.sql.SQLException;

/**
 * 在创建数据库是，帮助创建数据表的工具类
 * Created by mwg on 2018/1/23.
 */

/**
 * ORM: Object Relation Mapper
 * Object JAVA
 * Relation DB
 * Mappter 映射
 * <p>
 * 1.一个JAVA类对应了数据库中的一种数据表 Person类 <-----> Person表
 * 2.该类中的属性对应数据表中的字段
 * int age <-----> integer age
 * String name <-----> text name
 * 3.每一个类型的对象对应数据表中的一条数据记录  p1(35,"王五")<----->age:35,name:"王五"
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static DBHelper INSTANCE;

    public static DBHelper getINSTANCE(Context context){

        if (INSTANCE==null){
            synchronized (DBHelper.class){
                if (INSTANCE==null){
                    INSTANCE = new DBHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    private DBHelper(Context context) {
        super(context, "city.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        //在第一次创建city.db数据库时，该方法会被调用
        //创建存储数据的数据表
        try {
            TableUtils.createTableIfNotExists(connectionSource, CityNameBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

        try {
            TableUtils.dropTable(connectionSource, CityNameBean.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
