package com.xhlab.nep.model.process

abstract class ProcessSummary {
    abstract val processId: String
    abstract val name: String
    abstract val unlocalizedName: String
    abstract val localizedName: String
    abstract val amount: Int
    abstract val nodeCount: Int
}
