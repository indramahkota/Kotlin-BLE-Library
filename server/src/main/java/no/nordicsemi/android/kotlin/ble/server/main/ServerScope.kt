package no.nordicsemi.android.kotlin.ble.server.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val ServerScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)