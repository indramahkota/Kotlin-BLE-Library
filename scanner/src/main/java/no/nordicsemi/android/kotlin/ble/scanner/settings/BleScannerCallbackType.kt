/*
 * Copyright (c) 2022, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.kotlin.ble.scanner.settings

import android.bluetooth.le.ScanSettings
import android.os.Build
import androidx.annotation.RequiresApi

enum class BleScannerCallbackType(internal val value: Int) {
    /**
     * Trigger a callback for every Bluetooth advertisement found that matches the filter criteria.
     * If no filter is active, all advertisement packets are reported.
     */
    CALLBACK_TYPE_ALL_MATCHES(1),

    /**
     * A result callback is only triggered for the first advertisement packet received that matches
     * the filter criteria.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    CALLBACK_TYPE_FIRST_MATCH(2),

    /**
     * Receive a callback when advertisements are no longer received from a device that has been
     * previously reported by a first match callback.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    CALLBACK_TYPE_MATCH_LOST(4);

    @RequiresApi(Build.VERSION_CODES.M)
    fun toNative(): Int {
        return when (this) {
            CALLBACK_TYPE_ALL_MATCHES -> ScanSettings.CALLBACK_TYPE_ALL_MATCHES
            CALLBACK_TYPE_FIRST_MATCH -> ScanSettings.CALLBACK_TYPE_FIRST_MATCH
            CALLBACK_TYPE_MATCH_LOST -> ScanSettings.CALLBACK_TYPE_MATCH_LOST
        }
    }
}