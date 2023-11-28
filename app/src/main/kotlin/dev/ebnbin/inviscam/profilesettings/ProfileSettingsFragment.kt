package dev.ebnbin.inviscam.profilesettings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dev.ebnbin.android.core.AdHelper
import dev.ebnbin.android.core.PermissionFragment
import dev.ebnbin.android.core.PermissionFragment.Companion.requestPermissions
import dev.ebnbin.android.core.PermissionFragment.Companion.setPermissionResultListener
import dev.ebnbin.android.core.SDK_27_O_8_1
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.materialAttr
import dev.ebnbin.android.core.setNavigationBarColorAttr
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.databinding.ProfileSettingsFragmentBinding
import dev.ebnbin.inviscam.service.InvisCamService
import dev.ebnbin.inviscam.type.GestureAction
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.type.ProfileSettingsPreference
import dev.ebnbin.inviscam.util.AnalyticsHelper
import dev.ebnbin.inviscam.util.PrefManager

class ProfileSettingsFragment : Fragment() {
    private lateinit var binding: ProfileSettingsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ProfileSettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var tabLayoutMediator: TabLayoutMediator

    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback

    private var snackbar: Snackbar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        if (Build.VERSION.SDK_INT >= SDK_27_O_8_1) {
            requireActivity().window?.setNavigationBarColorAttr(requireContext(), materialAttr.colorSurfaceContainer)
        }

        childFragmentManager.setPermissionResultListener(
            requestKey = KEY_PERMISSIONS,
            lifecycleOwner = viewLifecycleOwner,
            onPermissionResult = { resultType ->
                if (resultType == PermissionFragment.ResultType.DENIED) {
                    return@setPermissionResultListener
                }
                InvisCamService.start(
                    context = requireContext(),
                    where = AnalyticsHelper.StartServiceWhere.MAIN,
                )
                snackbar?.let { snackbar ->
                    snackbar.dismiss()
                    this.snackbar = null
                    PrefManager.firstTimeHint.apply(false)
                }
            },
        )

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.setText(ProfileSettingsPreference.entries[position].titleId)
            tab.setIcon(ProfileSettingsPreference.entries[position].iconId)
        }
        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                PrefManager.profileSettingsPage.apply(position)
            }
        }

        binding.viewPager.apply {
            registerOnPageChangeCallback(onPageChangeCallback)
        }
        binding.spinner.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val profile = Profile.entries[position]
                    if (InvisCamService.isRunning.get()) {
                        InvisCamService.start(
                            context = requireContext(),
                            profile = profile,
                            where = AnalyticsHelper.StartServiceWhere.PROFILE_SETTINGS,
                        )
                    } else {
                        PrefManager.profile.apply(profile)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            adapter = ProfileSettingsSpinnerAdapter(
                context = requireContext(),
                profileList = Profile.entries,
            )
        }

        PrefManager.profile.observe(viewLifecycleOwner) { profile ->
            val index = Profile.entries.indexOf(profile)
            binding.spinner.setSelection(index, false)

            if (tabLayoutMediator.isAttached) {
                tabLayoutMediator.detach()
            }
            binding.viewPager.adapter = ProfileSettingsAdapter(
                fragment = this@ProfileSettingsFragment,
                profile = profile,
            )
            binding.viewPager.setCurrentItem(PrefManager.profileSettingsPage.get(), false)
            tabLayoutMediator.attach()
        }
        InvisCamService.isRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                binding.fab.setImageResource(GestureAction.STOP_SERVICE.iconId)
                binding.fab.setOnClickListener {
                    InvisCamService.stop(
                        context = requireContext(),
                        where = AnalyticsHelper.StopServiceWhere.MAIN,
                    )
                    binding.fab.isEnabled = false
                    binding.fab.postDelayed(50L) {
                        binding.fab.isEnabled = true
                    }
                }
            } else {
                binding.fab.setImageResource(R.drawable.app_logo_24)
                binding.fab.setOnClickListener {
                    childFragmentManager.requestPermissions(
                        requestKey = KEY_PERMISSIONS,
                        permissions = InvisCamService.PERMISSIONS,
                        optionalPermissions = InvisCamService.OPTIONAL_PERMISSIONS,
                    )
                    binding.fab.isEnabled = false
                    binding.fab.postDelayed(50L) {
                        binding.fab.isEnabled = true
                    }
                }
            }
        }

        if (PrefManager.firstTimeHint.get()) {
            snackbar = Snackbar.make(binding.root, R.string.first_time_hint, Snackbar.LENGTH_INDEFINITE).apply {
                setAnchorView(binding.fab)
                show()
            }
        }

        AdHelper.adaptiveBanner(binding.adContainer, "ca-app-pub-9007431044515157/9670059196")
    }

    override fun onDestroyView() {
        binding.viewPager.apply {
            unregisterOnPageChangeCallback(onPageChangeCallback)
        }
        if (tabLayoutMediator.isAttached) {
            tabLayoutMediator.detach()
        }
        super.onDestroyView()
    }

    companion object {
        private const val KEY_PERMISSIONS = "permissions"
    }
}
