package com.bsofts.quicksearchcontactstest.model;


import android.support.annotation.NonNull;

import com.bsofts.quicksearchcontactstest.util.PinyinUtils;

public class Person implements Comparable<Person>{

    public String name;
    public String phoneNum;

    public Person(String name,String phoneNum){
        this.name = name;
        this.phoneNum = phoneNum;
    }

    @Override
    public int compareTo(@NonNull Person another) {
        return PinyinUtils.getPinyin(name)
                .compareTo(PinyinUtils.getPinyin(another.name));
    }
}
