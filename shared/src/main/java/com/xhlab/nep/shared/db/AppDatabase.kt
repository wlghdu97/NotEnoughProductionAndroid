package com.xhlab.nep.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xhlab.nep.shared.db.dao.*
import com.xhlab.nep.shared.db.entity.*
import com.xhlab.nep.shared.db.view.SearchResultView

@Database(
    entities = [
        GregtechMachineEntity::class,
        GregtechRecipeEntity::class,
        RecipeEntity::class,
        RecipeResultEntity::class,
        ElementEntity::class,
        ElementFts::class
    ],
    views = [
        SearchResultView::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getGregtechMachineDao(): GregtechMachineDao
    abstract fun getGregtechRecipeDao(): GregtechRecipeDao
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getRecipeResultDao(): RecipeResultDao
    abstract fun getElementDao(): ElementDao
}