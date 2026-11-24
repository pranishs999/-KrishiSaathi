package com.krishisaathi.app.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CropDao_Impl implements CropDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CropEntity> __insertionAdapterOfCropEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAllCrops;

  public CropDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCropEntity = new EntityInsertionAdapter<CropEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `crops` (`id`,`name_ne`,`name_en`,`image_url`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CropEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getNameNe());
        statement.bindString(3, entity.getNameEn());
        statement.bindString(4, entity.getImageUrl());
      }
    };
    this.__preparedStmtOfClearAllCrops = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM crops";
        return _query;
      }
    };
  }

  @Override
  public List<Long> upsertCrops(final List<CropEntity> crops) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final List<Long> _result = __insertionAdapterOfCropEntity.insertAndReturnIdsList(crops);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long upsertCrop(final CropEntity crop) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfCropEntity.insertAndReturnId(crop);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public int clearAllCrops() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllCrops.acquire();
    try {
      __db.beginTransaction();
      try {
        final int _result = _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfClearAllCrops.release(_stmt);
    }
  }

  @Override
  public Flow<List<CropEntity>> getAllCrops() {
    final String _sql = "SELECT * FROM crops ORDER BY name_ne ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crops"}, new Callable<List<CropEntity>>() {
      @Override
      @NonNull
      public List<CropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNameNe = CursorUtil.getColumnIndexOrThrow(_cursor, "name_ne");
          final int _cursorIndexOfNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "name_en");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final List<CropEntity> _result = new ArrayList<CropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CropEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpNameNe;
            _tmpNameNe = _cursor.getString(_cursorIndexOfNameNe);
            final String _tmpNameEn;
            _tmpNameEn = _cursor.getString(_cursorIndexOfNameEn);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _item = new CropEntity(_tmpId,_tmpNameNe,_tmpNameEn,_tmpImageUrl);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<CropEntity> getCropById(final String cropId) {
    final String _sql = "SELECT * FROM crops WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, cropId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crops"}, new Callable<CropEntity>() {
      @Override
      @Nullable
      public CropEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNameNe = CursorUtil.getColumnIndexOrThrow(_cursor, "name_ne");
          final int _cursorIndexOfNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "name_en");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final CropEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpNameNe;
            _tmpNameNe = _cursor.getString(_cursorIndexOfNameNe);
            final String _tmpNameEn;
            _tmpNameEn = _cursor.getString(_cursorIndexOfNameEn);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _result = new CropEntity(_tmpId,_tmpNameNe,_tmpNameEn,_tmpImageUrl);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CropEntity>> searchCrops(final String query) {
    final String _sql = "SELECT * FROM crops WHERE name_ne LIKE '%' || ? || '%' OR name_en LIKE '%' || ? || '%' ORDER BY name_ne ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"crops"}, new Callable<List<CropEntity>>() {
      @Override
      @NonNull
      public List<CropEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNameNe = CursorUtil.getColumnIndexOrThrow(_cursor, "name_ne");
          final int _cursorIndexOfNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "name_en");
          final int _cursorIndexOfImageUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "image_url");
          final List<CropEntity> _result = new ArrayList<CropEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CropEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpNameNe;
            _tmpNameNe = _cursor.getString(_cursorIndexOfNameNe);
            final String _tmpNameEn;
            _tmpNameEn = _cursor.getString(_cursorIndexOfNameEn);
            final String _tmpImageUrl;
            _tmpImageUrl = _cursor.getString(_cursorIndexOfImageUrl);
            _item = new CropEntity(_tmpId,_tmpNameNe,_tmpNameEn,_tmpImageUrl);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
