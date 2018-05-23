package data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {VocabularyEntity.class}, version = 1)
public abstract class WanicchouDatabase extends RoomDatabase {
    public abstract VocabularyDao vocabularyDao();

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
