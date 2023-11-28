package dev.ebnbin.android.core

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import dev.ebnbin.android.core.AlertDialogFragment.Companion.setAlertDialogResultListener

class PermissionFragment : Fragment() {
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkPermissions(type = CheckPermissionType.ON_REQUEST_PERMISSION_RESULT)
        }

    private val requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (requireContext().arePermissionsGranted(optionalPermissions.toList())) {
                onPermissionResult(resultType = ResultType.GRANTED)
            } else {
                onPermissionResult(resultType = ResultType.OPTIONAL_DENIED)
            }
        }

    private val openSettingsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkPermissions(type = CheckPermissionType.ON_OPEN_SETTINGS_RESULT)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setAlertDialogResultListener(
            requestKey = KEY_REQUEST_PERMISSION,
            lifecycleOwner = this,
        ) { result ->
            val permission = result.resultExtras.valueOrThrow<String>(KEY_PERMISSION)
            when (result.type) {
                AlertDialogFragment.ResultType.POSITIVE -> {
                    if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                        openSettingsForSystemAlertWindowPermission()
                    } else {
                        openSettingsForRuntimePermission()
                    }
                }
                AlertDialogFragment.ResultType.NEGATIVE -> {
                    onPermissionResult(resultType = ResultType.DENIED)
                }
                else -> Unit
            }
        }

        if (savedInstanceState == null) {
            initPermissionList()
            checkPermissions(type = CheckPermissionType.FIRST_TIME)
        }
    }

    private fun getPermissionName(permission: String): String {
        return when (permission) {
            Manifest.permission.CAMERA -> getString(R.string.permission_fragment_camera)
            Manifest.permission.POST_NOTIFICATIONS -> getString(R.string.permission_fragment_post_notifications)
            Manifest.permission.RECORD_AUDIO -> getString(R.string.permission_fragment_record_audio)
            Manifest.permission.SYSTEM_ALERT_WINDOW -> getString(R.string.permission_fragment_system_alert_window)
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> getString(R.string.permission_fragment_write_external_storage)
            else -> permission
        }
    }

    private val permissionList: MutableList<String> = mutableListOf()
    private lateinit var optionalPermissions: Array<String>

    private fun initPermissionList() {
        permissionList.clear()
        permissionList.addAll(argumentOrThrow<Array<String>>(KEY_PERMISSIONS).toSet())
        optionalPermissions = argumentOrThrow(KEY_OPTIONAL_PERMISSIONS)
    }

    private fun checkPermissions(type: CheckPermissionType) {
        var firstTime = type == CheckPermissionType.FIRST_TIME
        permissionList.toList().forEach { permission ->
            if (requireContext().isPermissionGranted(permission)) {
                permissionList.remove(permission)
                firstTime = true
                return@forEach
            }
            if (firstTime) {
                if (permission == Manifest.permission.SYSTEM_ALERT_WINDOW) {
                    showRequestPermissionDialog(permission)
                } else {
                    requestPermissionLauncher.launch(permission)
                }
            } else {
                when (type) {
                    CheckPermissionType.ON_REQUEST_PERMISSION_RESULT -> {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            onPermissionResult(resultType = ResultType.DENIED)
                        } else {
                            showRequestPermissionDialog(permission)
                        }
                    }
                    CheckPermissionType.ON_OPEN_SETTINGS_RESULT -> {
                        onPermissionResult(resultType = ResultType.DENIED)
                    }
                    else -> Unit
                }
            }
            return
        }
        requestMultiplePermissionsLauncher.launch(optionalPermissions)
    }

    private fun showRequestPermissionDialog(permission: String) {
        AlertDialogFragment.Builder(
            context = requireContext(),
            requestKey = KEY_REQUEST_PERMISSION,
        )
            .title(R.string.permission_fragment_dialog_title)
            .message(getString(R.string.permission_fragment_dialog_message, getPermissionName(permission)))
            .positiveText(R.string.permission_fragment_dialog_positive)
            .negativeText(R.string.permission_fragment_dialog_negative)
            .cancelable(AlertDialogFragment.Cancelable.NOT_CANCELABLE)
            .resultExtras(KEY_PERMISSION to permission)
            .show(childFragmentManager)
    }

    private fun openSettingsForSystemAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        if (Build.VERSION.SDK_INT < SDK_30_R_11) {
            intent.setData("package:${requireContext().packageName}".toUri())
        }
        openSettingsLauncher.launch(intent)
    }

    private fun openSettingsForRuntimePermission() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData("package:${requireContext().packageName}".toUri())
        openSettingsLauncher.launch(intent)
    }

    private fun onPermissionResult(resultType: ResultType) {
        val requestKey = argumentOrThrow<String>(KEY_REQUEST_KEY)
        setFragmentResult(requestKey, bundleOf(KEY_RESULT_TYPE to resultType))
        remove()
    }

    private enum class CheckPermissionType {
        FIRST_TIME,
        ON_REQUEST_PERMISSION_RESULT,
        ON_OPEN_SETTINGS_RESULT,
        ;
    }

    enum class ResultType {
        GRANTED,
        OPTIONAL_DENIED,
        DENIED,
        ;
    }

    companion object {
        private const val KEY_REQUEST_KEY = "request_key"
        private const val KEY_PERMISSIONS = "permissions"
        private const val KEY_OPTIONAL_PERMISSIONS = "optional_permissions"
        private const val KEY_RESULT_TYPE = "result_type"
        private const val KEY_REQUEST_PERMISSION = "request_permission"
        private const val KEY_PERMISSION = "permission"

        fun FragmentManager.setPermissionResultListener(
            requestKey: String,
            lifecycleOwner: LifecycleOwner,
            onPermissionResult: (ResultType) -> Unit,
        ) {
            setFragmentResultListener(requestKey, lifecycleOwner) { _, result ->
                onPermissionResult(result.valueOrThrow(KEY_RESULT_TYPE))
            }
        }

        fun FragmentManager.requestPermissions(
            requestKey: String,
            permissions: List<String> = emptyList(),
            optionalPermissions: List<String> = emptyList(),
        ) {
            commit(allowStateLoss = true) {
                add(
                    PermissionFragment::class.java,
                    bundleOf(
                        KEY_REQUEST_KEY to requestKey,
                        KEY_PERMISSIONS to permissions.toTypedArray(),
                        KEY_OPTIONAL_PERMISSIONS to optionalPermissions.toTypedArray(),
                    ),
                    requestKey,
                )
            }
        }
    }
}
