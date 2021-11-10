package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val processJson = Json {
    serializersModule = SerializersModule {
        polymorphic(Recipe::class) {
            subclass(
                ProcessRecipeSerializer.MachineRecipeViewImpl::class,
                ProcessRecipeSerializer.MachineRecipeViewImpl.serializer()
            )
            subclass(
                SupplierRecipe::class,
                ProcessRecipeSerializer.SupplierRecipeSerializer()
            )
            subclass(
                OreChainRecipe::class,
                ProcessRecipeSerializer.OreChainRecipeSerializer()
            )
            default {
                ProcessRecipeSerializer.CraftingRecipeViewImpl.serializer()
            }
        }
    }
}
