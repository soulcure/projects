package com.taku.safe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.taku.safe.db.bean.NoticeMsg;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTICE_MSG".
*/
public class NoticeMsgDao extends AbstractDao<NoticeMsg, Long> {

    public static final String TABLENAME = "NOTICE_MSG";

    /**
     * Properties of entity NoticeMsg.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, Long.class, "time", false, "TIME");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
    }


    public NoticeMsgDao(DaoConfig config) {
        super(config);
    }
    
    public NoticeMsgDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTICE_MSG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TIME\" INTEGER," + // 1: time
                "\"TITLE\" TEXT," + // 2: title
                "\"CONTENT\" TEXT);"); // 3: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTICE_MSG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NoticeMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(2, time);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NoticeMsg entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(2, time);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NoticeMsg readEntity(Cursor cursor, int offset) {
        NoticeMsg entity = new NoticeMsg( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // time
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // content
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NoticeMsg entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NoticeMsg entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NoticeMsg entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(NoticeMsg entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
