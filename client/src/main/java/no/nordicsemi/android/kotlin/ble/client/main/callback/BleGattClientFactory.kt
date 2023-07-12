package no.nordicsemi.android.kotlin.ble.client.main.callback

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import no.nordicsemi.android.kotlin.ble.client.api.GattClientAPI
import no.nordicsemi.android.kotlin.ble.client.main.bonding.BondingBroadcastReceiver
import no.nordicsemi.android.kotlin.ble.client.mock.BleMockGatt
import no.nordicsemi.android.kotlin.ble.client.real.BluetoothGattClientCallback
import no.nordicsemi.android.kotlin.ble.client.real.BluetoothGattWrapper
import no.nordicsemi.android.kotlin.ble.core.MockClientDevice
import no.nordicsemi.android.kotlin.ble.core.MockServerDevice
import no.nordicsemi.android.kotlin.ble.core.RealServerDevice
import no.nordicsemi.android.kotlin.ble.core.ServerDevice
import no.nordicsemi.android.kotlin.ble.core.data.BleGattConnectOptions
import no.nordicsemi.android.kotlin.ble.logger.BlekLogger
import no.nordicsemi.android.kotlin.ble.logger.DefaultBlekLogger
import no.nordicsemi.android.kotlin.ble.mock.MockEngine

internal object BleGattClientFactory {

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(
        context: Context,
        macAddress: String,
        options: BleGattConnectOptions = BleGattConnectOptions(),
        logger: BlekLogger = DefaultBlekLogger(context),
    ): BleGattClient {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(macAddress)
        val realDevice = RealServerDevice(device)
        return connectDevice(realDevice, context, options, logger)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    suspend fun connect(
        context: Context,
        device: ServerDevice,
        options: BleGattConnectOptions = BleGattConnectOptions(),
        logger: BlekLogger = DefaultBlekLogger(context),
    ): BleGattClient {
        return when (device) {
            is MockServerDevice -> connectDevice(device, options, logger)
            is RealServerDevice -> connectDevice(device, context, options, logger)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun connectDevice(
        device: MockServerDevice,
        options: BleGattConnectOptions,
        logger: BlekLogger,
    ): BleGattClient {
        val clientDevice = MockClientDevice()
        val gatt = BleMockGatt(MockEngine, device, clientDevice, options.autoConnect)
        return BleGattClient(gatt, logger)
            .also { MockEngine.connectToServer(device, clientDevice, gatt, options) }
            .also { it.waitForConnection() }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private suspend fun connectDevice(
        device: RealServerDevice,
        context: Context,
        options: BleGattConnectOptions,
        logger: BlekLogger,
    ): BleGattClient {
        return BleGattClient(device.createConnection(context, options), logger).also {
            it.waitForConnection()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun RealServerDevice.createConnection(
        context: Context,
        options: BleGattConnectOptions,
    ): GattClientAPI {
        val gattCallback = BluetoothGattClientCallback()

        BondingBroadcastReceiver.register(context, this, gattCallback)

        val gatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            device.connectGatt(
                context,
                options.autoConnect,
                gattCallback,
                BluetoothDevice.TRANSPORT_LE,
                options.phy?.toNative() ?: 0
            )
        } else {
            device.connectGatt(context, options.autoConnect, gattCallback)
        }

        return BluetoothGattWrapper(gatt, gattCallback, options.autoConnect)
    }
}