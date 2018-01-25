package com.mwg.goupon.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by mwg on 2018/1/22.
 */

public class PinYinUtil {
    /**
     * 利用PinYin4J将参数中的内容转化为汉语拼音
     *
     * @param name
     * @return
     */
    public static String getPinYin(String name) {
        try {
            String result = "";
            //1、设定汉语拼音的格式（大小写等）
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

            //2、根据设定好的格式，逐字进行汉字到拼音的转化
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                //汉字里面有多音字（单、重）
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (pinyin.length>0){
                    sb.append(pinyin[0]);
                }
            }
            result = sb.toString();
            return result;

        } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
            badHanyuPinyinOutputFormatCombination.printStackTrace();
            throw new RuntimeException("不正确的汉语拼音格式！");
        }
    }
}
