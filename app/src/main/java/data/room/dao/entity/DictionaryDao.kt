package data.room.dao.entity

import androidx.room.Dao
import androidx.room.Query
import data.room.dao.BaseDao
import data.room.dbo.entity.Dictionary

@Dao
interface DictionaryDao : BaseDao<Dictionary>