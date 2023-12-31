package com.example.ble_beacon

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ParcelUuid
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.util.UUID

class SendBeacon : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)


    }

    @SuppressLint("MissingPermission")
    private fun sendBeacon(uuid: String) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        val registerForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
            }
        }

        // 檢查藍牙是否可用
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "藍牙不可用", Toast.LENGTH_SHORT).show()
            return
        }

        // 檢查藍牙是否已經開啟
        if (!bluetoothAdapter.isEnabled) {
            // 如果藍牙未開啟，可以啟動藍牙
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForResult.launch(enableBtIntent)
        }

        // 將字串轉換為 UUID
        val beaconUUID = try {
            UUID.fromString(uuid)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "無效的 UUID 字串", Toast.LENGTH_SHORT).show()
            return
        }

        // 創建 Beacon 數據
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .addServiceUuid(ParcelUuid(beaconUUID)) // 替換成您的 Beacon UUID
            .build()

        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? = bluetoothAdapter.bluetoothLeAdvertiser
        if (bluetoothLeAdvertiser == null) {
            Toast.makeText(this, "不支持 Beacon", Toast.LENGTH_SHORT).show()
            return
        }

        // 開始廣告 Beacon 數據
        bluetoothLeAdvertiser.startAdvertising(settings, data, advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Toast.makeText(this@SendBeacon, "Beacon 廣告已開始", Toast.LENGTH_SHORT).show()
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Toast.makeText(this@SendBeacon, "Beacon 廣告開始失敗，錯誤碼: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }
}