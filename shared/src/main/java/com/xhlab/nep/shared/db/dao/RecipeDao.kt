package com.xhlab.nep.shared.db.dao

import androidx.room.Dao
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.RecipeEntity

@Dao
abstract class RecipeDao : BaseDao<RecipeEntity>() {


}