package com.android.newsapp.utils

import androidx.room.TypeConverter
import com.android.newsapp.data.network.response.Source


class Converters {

    @TypeConverter
    fun fromSource(source: Source) : String{
        return source.name
    }

    @TypeConverter
    fun toSource(name:String) :Source{

        return Source(name,name)
    }

}