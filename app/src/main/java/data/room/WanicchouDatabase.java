package data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import data.room.rel.RelatedWordDao;
import data.room.rel.RelatedWordEntity;
import data.room.voc.VocabularyDao;
import data.room.voc.VocabularyEntity;

// TODO: What's a schema export for?
@Database(
        entities = {VocabularyEntity.class,
                RelatedWordEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters(
        {Converters.class}
)
/**
 * Database object using the Room Persistence Library.
 * Singleton design to avoid multiple instances of database
 * connection when it is not needed.
 */
public abstract class WanicchouDatabase extends RoomDatabase {
    public abstract VocabularyDao vocabularyDao();
    public abstract RelatedWordDao relatedWordDao();

    // Singleton to avoid multiple DB connections
    private static WanicchouDatabase INSTANCE;
    public static WanicchouDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (WanicchouDatabase.class){
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WanicchouDatabase.class, "WanicchouDatabase").build();
                }
            }
        }
        return INSTANCE;
    }
}
