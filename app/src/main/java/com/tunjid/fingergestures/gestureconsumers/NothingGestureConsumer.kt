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

class NothingGestureConsumer private constructor() : GestureConsumer {

    override fun accepts(@GestureConsumer.GestureAction gesture: Int): Boolean {
        return gesture == GestureConsumer.DO_NOTHING
    }

    override fun onGestureActionTriggered(@GestureConsumer.GestureAction gestureAction: Int) {
        // Do nothing
    }

    companion object {

        val instance: NothingGestureConsumer by lazy { NothingGestureConsumer() }

    }
}

