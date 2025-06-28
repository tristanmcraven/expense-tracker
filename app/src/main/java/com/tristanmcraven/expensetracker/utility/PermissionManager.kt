package com.tristanmcraven.expensetracker.utility

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object PermissionManager {
    /**
     * For Fragments: returns a function you call with a permission string.
     */
    fun createRequester(
        owner: Fragment,
        onDenied: (permission: String) -> Unit
    ): (permission: String) -> Boolean {
        // Holds the last permission we asked for
        var pendingPerm: String? = null

        val launcher = owner.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                pendingPerm?.let { onDenied(it) }
            }
            // clear it so we don’t leak
            pendingPerm = null
        }

        return { permission ->
            val ctx = owner.requireContext()
            if (ContextCompat.checkSelfPermission(ctx, permission)
                == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                pendingPerm = permission
                launcher.launch(permission)
                false
            }
        }
    }

    /**
     * For AppCompatActivities: same idea.
     */
    fun createRequester(
        owner: AppCompatActivity,
        onDenied: (permission: String) -> Unit
    ): (permission: String) -> Boolean {
        var pendingPerm: String? = null

        val launcher = owner.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (!granted) {
                pendingPerm?.let { onDenied(it) }
            }
            pendingPerm = null
        }

        return { permission ->
            val ctx = owner
            if (ContextCompat.checkSelfPermission(ctx, permission)
                == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                pendingPerm = permission
                launcher.launch(permission)
                false
            }
        }
    }
}
