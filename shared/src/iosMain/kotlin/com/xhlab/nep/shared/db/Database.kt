package com.xhlab.nep.shared.db

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
val nep: Nep = NepDriverFactory().createDatabase()

@SharedImmutable
val nepProcess: NepProcess = ProcessDriverFactory().createDatabase()
