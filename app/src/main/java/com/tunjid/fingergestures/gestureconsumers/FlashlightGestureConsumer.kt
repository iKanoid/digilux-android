/*
 * Copyright (c) 2017, 2018, 2019 Adetunji Dahunsi.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tunjid.fingergestures.gestureconsumers

import android.annotation.SuppressLint
import android.hardware.camera2.CameraManager

import com.tunjid.fingergestures.App

class FlashlightGestureConsumer private constructor() : GestureConsumer {

    private var isCallbackRegistered: Boolean = false
    private var isTorchOn: Boolean = false

    init {
        isCallbackRegistered = registerTorchCallback(App.instance)
    }

    @SuppressLint("SwitchIntDef")
    override fun accepts(@GestureConsumer.GestureAction gesture: Int): Boolean {
        return gesture == GestureConsumer.TOGGLE_FLASHLIGHT
    }

    @SuppressLint("SwitchIntDef")
    override fun onGestureActionTriggered(@GestureConsumer.GestureAction gestureAction: Int) {
        when (gestureAction) {
            GestureConsumer.TOGGLE_FLASHLIGHT -> {
                val app = App.instance ?: return
                if (!isCallbackRegistered) isCallbackRegistered = registerTorchCallback(app)
                if (!isCallbackRegistered) return

                val cameraManager = app.getSystemService(CameraManager::class.java) ?: return

                try {
                    cameraManager.setTorchMode(cameraManager.cameraIdList[0], !isTorchOn)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun registerTorchCallback(app: App?): Boolean {
        if (app == null) return false

        val cameraManager = app.getSystemService(CameraManager::class.java) ?: return false

        cameraManager.registerTorchCallback(object : CameraManager.TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                isTorchOn = enabled
            }
        }, null)

        return true
    }

    companion object {

        val instance: FlashlightGestureConsumer by lazy { FlashlightGestureConsumer() }

    }

    //     try {
    //        for (String id : cameraManager.getCameraIdList()) {
    //            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
    //            Boolean flag = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
    //            // Turn on the flash if camera has one
    //            if (flag != null && flag) {
    //                cameraManager.setTorchMode(id, gestureAction == FLASHLIGHT_ON);
    //            }
    //        }
    //
    //    }
}

