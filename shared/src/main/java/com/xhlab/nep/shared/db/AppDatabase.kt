package com.xhlab.nep.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xhlab.nep.shared.db.dao.*
import com.xhlab.nep.shared.db.entity.*
import com.xhlab.nep.shared.db.view.RoomElementView

@Database(
    entities = [
        MachineEntity::class,
        MachineRecipeEntity::class,
        RecipeEntity::class,
        RecipeResultEntity::class,
        ElementEntity::class,
        ElementFts::class,
        OreDictChainEntity::class,
        ReplacementEntity::class
    ],
    views = [
        RoomElementView::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMachineDao(): MachineDao
    abstract fun getMachineRecipeDao(): MachineRecipeDao
    abstract fun getRecipeDao(): RecipeDao
    abstract fun getRecipeResultDao(): RecipeResultDao
    abstract fun getElementDao(): ElementDao
    abstract fun getOreDictChainDao(): OreDictChainDao
    abstract fun getReplacementDao(): ReplacementDao
}