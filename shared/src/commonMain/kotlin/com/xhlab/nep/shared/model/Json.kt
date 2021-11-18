package com.xhlab.nep.shared.model

import com.xhlab.nep.model.PlainRecipeElement
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepoImpl
import com.xhlab.nep.shared.data.recipe.RecipeRepoImpl
import com.xhlab.nep.shared.parser.process.OreChainRecipeSerializer
import com.xhlab.nep.shared.parser.process.SupplierRecipeSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val defaultJson = Json {
    // this is for RecipeElement.type
    classDiscriminator = "classType"
    serializersModule = SerializersModule {
        polymorphic(RecipeElement::class) {
            subclass(PlainRecipeElement::class, PlainRecipeElement.serializer())
            subclass(OreDictElement::class, OreDictElement.serializer())
        }
        polymorphic(Recipe::class) {
            subclass(
                RecipeRepoImpl.RecipeViewImpl::class,
                RecipeRepoImpl.RecipeViewImpl.serializer()
            )
            subclass(
                MachineRecipeRepoImpl.MachineRecipeViewImpl::class,
                MachineRecipeRepoImpl.MachineRecipeViewImpl.serializer()
            )
            subclass(OreChainRecipe::class, OreChainRecipeSerializer)
            subclass(SupplierRecipe::class, SupplierRecipeSerializer)
        }
    }
}
