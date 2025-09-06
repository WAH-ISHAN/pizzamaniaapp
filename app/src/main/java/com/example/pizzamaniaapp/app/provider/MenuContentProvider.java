package com.example.pizzamaniaapp.app.provider;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.example.pizzamaniaapp.app.data.local.AppDatabase;

public class MenuContentProvider extends ContentProvider {
    public static final String AUTH = "com.pizzamania.app.provider";
    private static final int MENU = 1;
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    static { MATCHER.addURI(AUTH, "menu", MENU); }

    @Override public boolean onCreate() { return true; }

    @Nullable @Override
    public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (MATCHER.match(uri) == MENU) {
            return AppDatabase.get(getContext()).getOpenHelper().getReadableDatabase()
                    .query("SELECT id, name, description, category, price, imageUrl, veg FROM menu_items");
        }
        throw new UnsupportedOperationException("Unknown URI " + uri);
    }

    @Nullable @Override public String getType(@Nullable Uri uri) { return null; }
    @Nullable @Override public Uri insert(@Nullable Uri uri, @Nullable ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(@Nullable Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) { throw new UnsupportedOperationException(); }
    @Override public int update(@Nullable Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) { throw new UnsupportedOperationException(); }
}
