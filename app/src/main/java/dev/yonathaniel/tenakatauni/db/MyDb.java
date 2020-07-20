package dev.yonathaniel.tenakatauni.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import dev.yonathaniel.tenakatauni.models.UserModel;

public class MyDb extends SQLiteOpenHelper {
    public  static  final  String DATABASE_NAME="Crud.db";
    public  static  final  String TABLE_NAME="Crud_table";

    //DEFINE COLUMNS
    public  static  final  String COL_1="ID";
    public  static  final  String COL_2="PHOTO";
    public  static  final  String COL_3="NAME";
    public  static  final  String COL_4="MARITALSTATUS";
    public  static  final  String COL_5="GENDER";
    public  static  final  String COL_6="AGE";
    public  static  final  String COL_7="HEIGHT";
    public  static  final  String COL_8="IQTEST";


    public MyDb(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT,PHOTO TEXT,NAME TEXT,MARITALSTATUS TEXT,GENDER TEXT,AGE INTEGER,HEIGHT INTEGER,IQTEST INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
    }

    public  boolean INSERT_DATA(@NonNull UserModel userModel){

        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,userModel.getPhoto());
        contentValues.put(COL_3,userModel.getName());
        contentValues.put(COL_4,userModel.getMaritalStatus());
        contentValues.put(COL_5,userModel.getGender());
        contentValues.put(COL_6,userModel.getAge());
        contentValues.put(COL_7,userModel.getHeight());
        contentValues.put(COL_8,userModel.getIqTest());

        long result=database.insert(TABLE_NAME,null,contentValues);
        database.close();
        return result != -1;
    }


    public Cursor READ_DATA(){
        SQLiteDatabase database=this.getWritableDatabase();
        return database.rawQuery("select * from "+TABLE_NAME,null);
    }


    public  boolean UPDATE_DATA(@NonNull UserModel userModel,String id){

        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL_2,userModel.getPhoto());
        contentValues.put(COL_3,userModel.getName());
        contentValues.put(COL_4,userModel.getMaritalStatus());
        contentValues.put(COL_5,userModel.getGender());
        contentValues.put(COL_6,userModel.getAge());
        contentValues.put(COL_7,userModel.getHeight());
        contentValues.put(COL_8,userModel.getIqTest());

        int result=database.update(TABLE_NAME,contentValues,"ID =?",new String[]{id});
        return result > 0;
    }

    public Integer DELETE_DATA(String id){
        SQLiteDatabase database=this.getWritableDatabase();
        return database.delete(TABLE_NAME,"ID =?",new String[]{id});
    }

}
