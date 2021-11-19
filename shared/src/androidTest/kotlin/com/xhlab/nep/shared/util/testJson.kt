package com.xhlab.nep.shared.util

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.model.defaultJson
import com.xhlab.test.shared.ProcessData
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic

val testJson = Json {
    classDiscriminator = "classType"
    serializersModule = defaultJson.serializersModule + SerializersModule {
        polymorphic(RecipeElement::class) {
            subclass(
                ProcessData.RecipeElementImpl::class,
                ProcessData.RecipeElementImpl.serializer()
            )
        }
        polymorphic(Recipe::class) {
            subclass(
                ProcessData.MachineRecipeViewImpl::class,
                ProcessData.MachineRecipeViewImpl.serializer()
            )
            subclass(
                ProcessData.CraftingRecipeViewImpl::class,
                ProcessData.CraftingRecipeViewImpl.serializer()
            )
        }
    }
}
