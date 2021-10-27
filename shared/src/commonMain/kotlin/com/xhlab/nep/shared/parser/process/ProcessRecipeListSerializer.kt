package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.Recipe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer

object ProcessRecipeListSerializer :
    KSerializer<List<Recipe>> by ListSerializer(ProcessRecipeSerializer)
