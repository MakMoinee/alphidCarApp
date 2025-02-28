package com.thesis.alphidcar.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.MakMoinee.library.common.MapForm;
import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.services.HashPass;
import com.thesis.alphidcar.models.Users;
import com.thesis.alphidcar.repository.LocalSQL;

public class UserServices {

    private final String TABLE_USER = "users";

    private LocalSQL localSQL;
    private HashPass hashPass = new HashPass();

    public UserServices(Context mContext) {
        localSQL = new LocalSQL(mContext);
    }

    /**
     * insertUserOnly
     *
     * @param users    - User Object to be insert uniquely in sqlLite database
     * @param listener - handles the success and error response
     */
    public void insertUserOnly(Users users, DefaultBaseListener listener) {
        this.findUserByLogin(users.getUsername(), users.getPassword(), new DefaultBaseListener() {
            @Override
            public <T> void onSuccess(T any) {
                listener.onError(new Error("username exists"));
            }

            @Override
            public void onError(Error error) {
                insertUser(users, listener);
            }
        });
    }

    /**
     * @param users
     * @param listener
     */
    public void insertUser(Users users, DefaultBaseListener listener) {
        SQLiteDatabase db = localSQL.getWritableDatabase();
        String hashStrPass = hashPass.makeHashPassword(users.getPassword());
        users.setPassword(hashStrPass);
        ContentValues values = MapForm.toContentValues(users);
        values.remove("id");
        try {
            long count = db.insert(TABLE_USER, null, values);
            if (count != -1) {
                listener.onSuccess("success add");
            } else {
                listener.onError(new Error("failed to add error"));
            }
        } catch (Exception e) {
            Log.e("error_insert", e.getLocalizedMessage());
            listener.onError(new Error(e.getMessage()));
        } finally {
            db.close();
        }
    }

    /**
     * findUser
     *
     * @param username
     * @param password
     * @param listener
     */
    @SuppressLint("Range")
    public void findUserByLogin(String username, String password, DefaultBaseListener listener) {
        Users users = null;
        SQLiteDatabase db = localSQL.getWritableDatabase();
        String[] columns = {"id", "firstName", "middleName", "lastName", "username", "password"};
        String selection = "username=?";
        String[] selectionArgs = {username};
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String uname = cursor.getString(cursor.getColumnIndex("username"));
                    String pass = cursor.getString(cursor.getColumnIndex("password"));
                    String firstName = cursor.getString(cursor.getColumnIndex("firstName"));
                    String middleName = cursor.getString(cursor.getColumnIndex("middleName"));
                    String lastName = cursor.getString(cursor.getColumnIndex("lastName"));
                    Log.e("retrieve_n", lastName);
                    if (hashPass.verifyPassword(password, pass)) {
                        users = new Users.UserBuilder()
                                .setId(id)
                                .setFirstName(firstName)
                                .setMiddleName(middleName)
                                .setLastName(lastName)
                                .setUsername(uname)
                                .build();
                        break;
                    }


                } while (cursor.moveToNext());
            } else {

                listener.onError(new Error("Invalid username or password."));
            }

        } catch (Exception e) {
            listener.onError(new Error("Error checking user: " + e.getMessage()));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();

            if (users != null) {
                listener.onSuccess(users);
            } else {
                listener.onError(new Error("empty"));
            }
        }
    }
}
