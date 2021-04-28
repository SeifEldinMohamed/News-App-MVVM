package com.seif.newsappmvvm.database

import androidx.room.TypeConverter
import com.seif.newsappmvvm.models.Source

// we made this class as room can't deal with custom classes data types (only primitive data types).
class Converters {
    // to convert from source to string.
    @TypeConverter
    fun fromSource(source: Source):String{
        return source.name
    }
    // to convert from string to Source.
    @TypeConverter
    fun ToSource(name: String):Source {
        return Source(name, name)
    }
}