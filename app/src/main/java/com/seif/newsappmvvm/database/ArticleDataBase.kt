package com.seif.newsappmvvm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seif.newsappmvvm.models.Article

// data class for room always need to be abstract
@Database(
        entities = [Article::class],
        // the version is used to update our database later on (to migrate all old database to new database)
        version = 1
)

@TypeConverters(Converters::class)

abstract class ArticleDataBase : RoomDatabase() {
    // used to access our actual database functions.
    abstract fun getArticleDao():ArticleDao

    // we create companion object to be able to create our actual database.
    companion object{
        // create instance of that article database.
        // volatile means that other threads can immediately see when a thread changes this instance.
        @Volatile
        private var instance : ArticleDataBase? = null
        // we will use that to synchronize setting that instance (to make sure that there is only single instance in our database)
        private var LOCK = Any()
        // this fun will call whenever we make an instance in our database (constructor).
        // if the instance is equal to null then we have to make sure any thing happened in this block
        // can't be access by another thread at the same time.
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            // check again if it is null so we call our create database fun and
            // also set our instance to the result of our database fun.
            instance ?: createDatabase(context).also{ instance = it}

        }

        private fun createDatabase(context: Context) =
                Room.databaseBuilder(
                        context,
                        ArticleDataBase::class.java,
                        "article_db.db"
                ).build()
    }
}