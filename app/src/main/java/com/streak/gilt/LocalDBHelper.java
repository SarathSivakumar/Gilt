package com.streak.gilt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.core.content.ContextCompat;

public class LocalDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String CREATE_TABLE_ORDERDETAILS="CREATE TABLE `orderdetails` (\n" +
            " `customername` varchar(500) NOT NULL,\n" +
            " `mobilenumber` varchar(15) NOT NULL,\n" +
            " `orderid` int(11) NOT NULL AUTO_INCREMENT,\n" +
            " `modelid` int(11) NOT NULL,\n" +
            " `factoryid` int(11) NOT NULL,\n" +
            " `weight` float NOT NULL,\n" +
            " `size` int(11) NOT NULL,\n" +
            " `option1` varchar(500) NOT NULL,\n" +
            " `option2` varchar(500) NOT NULL,\n" +
            " `sealid` int(11) NOT NULL,\n" +
            " `advance` float NOT NULL,\n" +
            " `due` bigint(20) NOT NULL,\n" +
            " `comments` varchar(2000) NOT NULL,\n" +
            " `stageid` int(11) NOT NULL,\n" +
            " `imagepath` varchar(500) NOT NULL,\n" +
            " `activities` varchar(5000) NOT NULL,\n" +
            " `extracolumn3` int(11) NOT NULL,\n" +
            " `creationdate` date NOT NULL DEFAULT current_timestamp(),\n" +
            " `audiopath` varchar(500) DEFAULT NULL,\n" +
            " PRIMARY KEY (`orderid`)\n" +
            ")";

    private static final String CREATE_TABLE_MODELMAPPING="CREATE TABLE modelmapping (`modelid` integer, `modelname` varchar(50) NOT NULL,`syncstatus` int(11) NOT NULL) ";

    public LocalDBHelper(Context context){
        super(context,DbNaming.DB_NAME,null,DATABASE_VERSION);
    }

    public static final String DROP_TABLE_MODELMAPPING="DROP TABLE IF EXISTS MODELMAPPING";
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MODELMAPPING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_MODELMAPPING);
        onCreate(db);
    }

    public void saveToLocalDatabase(String name, int sync_status, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        //contentValues.put("modelid",21);
        contentValues.put("modelname",name);
        contentValues.put("syncstatus",sync_status);
        db.insert(DbNaming.MODELMAPPING_TABLE_NAME,null,contentValues);
    }
    public Cursor readFromLocalDatabase(SQLiteDatabase db){
        String[] projection={"modelname","syncstatus","modelid"};
        return (db.query(DbNaming.MODELMAPPING_TABLE_NAME,projection,null,null,null,null,null));
    }
    public void updateLocalDatabase(String modelname,int sync_status, SQLiteDatabase db){
        ContentValues contentValues=new ContentValues();
        contentValues.put("syncstatus",sync_status);
        String selection="modelname like ?";
        String[] selection_args={modelname};
        db.update(DbNaming.MODELMAPPING_TABLE_NAME,contentValues,selection,selection_args);
    }


}
