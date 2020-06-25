/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.support.supportBaseClass

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.support.R
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.jvm.internal.Intrinsics.checkNotNull

/**
 * This provides methods to help Activities load their UI.
 */
object ActivityUtils {

    /**
     * The `fragment` is added to the container view with id `frameId`. The operation is
     * performed by the `fragmentManager`.
     *
     */
    fun addFragmentToActivity(
        fragmentManager: FragmentManager,
        fragment: Fragment, frameId: Int
    ) {
        checkNotNull(fragmentManager)
        checkNotNull(fragment)
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        /*transaction.addToBackStack("DASH");*/
        transaction.replace(frameId, fragment)
        transaction.commit()
    }

    fun addFragmentStackToActivity(
        fragmentManager: FragmentManager,
        fragment: Fragment, frameId: Int
    ) {

        for (fragmentDatum in fragmentManager.fragments) {

            if (fragmentDatum != null && fragmentDatum.fragmentManager != null) {
                fragmentDatum.fragmentManager!!.popBackStackImmediate()
            }

        }

        checkNotNull(fragmentManager)
        checkNotNull(fragment)


        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        transaction.addToBackStack("data")
        transaction.replace(frameId, fragment)
        transaction.commitAllowingStateLoss()
    }

    fun isPortOpen(ip: String, port: Int, timeout: Int): Boolean {

        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), timeout)
            socket.close()
            return true
        } catch (ce: ConnectException) {
            ce.printStackTrace()
            return false
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }

    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}


