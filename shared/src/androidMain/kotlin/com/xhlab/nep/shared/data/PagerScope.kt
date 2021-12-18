package com.xhlab.nep.shared.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual val pagerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
