package com.mwg.goupon.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mwg on 2018/1/22.
 */

@DatabaseTable
public class CityNameBean {
    @DatabaseField(id=true)
    String cityName;//城市的中文名称
    @DatabaseField
    String pyName;//城市名称的拼音
    @DatabaseField
    char letter;//拼音名称的首字母

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPyName() {
        return pyName;
    }

    public void setPyName(String pyName) {
        this.pyName = pyName;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return "CityNameBean{" +
                "cityName='" + cityName + '\'' +
                ", pyName='" + pyName + '\'' +
                ", letter=" + letter +
                '}';
    }
}
