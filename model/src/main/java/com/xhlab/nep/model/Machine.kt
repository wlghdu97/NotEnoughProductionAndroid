package com.xhlab.nep.model

data class Machine(
    val id: Int,
    val modName: String,
    val name: String
) {
    companion object {
        const val MOD_GREGTECH = "gregtech"
        const val MOD_VANILLA = "vanilla"

        const val VANILLA_WORKBENCH = "Workbench"
    }
}