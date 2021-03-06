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

package com.tunjid.fingergestures.fragments


import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tunjid.androidx.core.components.args
import com.tunjid.androidx.navigation.Navigator
import com.tunjid.androidx.navigation.activityNavigatorController
import com.tunjid.androidx.recyclerview.listAdapterOf
import com.tunjid.androidx.recyclerview.verticalLayoutManager
import com.tunjid.androidx.view.util.inflate
import com.tunjid.fingergestures.R
import com.tunjid.fingergestures.baseclasses.MainActivityFragment
import com.tunjid.fingergestures.billing.PurchasesManager
import com.tunjid.fingergestures.distinctUntilChanged
import com.tunjid.fingergestures.gestureconsumers.RotationGestureConsumer
import com.tunjid.fingergestures.gestureconsumers.RotationGestureConsumer.Companion.ROTATION_APPS
import com.tunjid.fingergestures.map
import com.tunjid.fingergestures.models.AppState
import com.tunjid.fingergestures.viewholders.PackageViewHolder
import com.tunjid.fingergestures.viewmodels.AppViewModel
import com.tunjid.fingergestures.viewmodels.AppViewModel.Companion.EXCLUDED_ROTATION_LOCK
import com.tunjid.fingergestures.viewmodels.AppViewModel.Companion.ROTATION_LOCK

class PackageFragment : MainActivityFragment(R.layout.fragment_packages) {

    private val viewModel by activityViewModels<AppViewModel>()
    private val navigator by activityNavigatorController<Navigator>()
    private var preferenceName by args<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.title_bar)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        view.findViewById<RecyclerView>(R.id.options_list).apply {
            val listAdapter = listAdapterOf(
                    initialItems = viewModel.liveState.value?.installedApps ?: listOf(),
                    viewHolderCreator = { viewGroup, _ ->
                        PackageViewHolder(
                                itemView = viewGroup.inflate(R.layout.viewholder_package_vertical),
                                listener = ::onPackageClicked
                        )
                    },
                    viewHolderBinder = { holder, item, _ -> holder.bind(item) }
            )

            adapter = listAdapter
            layoutManager = verticalLayoutManager()
            addItemDecoration(divider())

            viewModel.liveState
                    .map(AppState::installedApps)
                    .distinctUntilChanged()
                    .observe(viewLifecycleOwner) {
                        if (it.isEmpty()) listAdapter.submitList(it)
                        else listAdapter.submitList(it) {
                            progressBar.visibility = View.GONE
                            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition().addTarget(progressBar))
                        }
                    }
        }

        toolbar.title = RotationGestureConsumer.instance.getAddText(preferenceName)
        viewModel.updateApps()
    }

    private fun onPackageClicked(packageName: String) {
        val added = RotationGestureConsumer.instance.addToSet(packageName, preferenceName)

        if (!added) {
            val context = requireContext()
            MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.go_premium_title)
                    .setMessage(context.getString(R.string.go_premium_body, context.getString(R.string.auto_rotate_description)))
                    .setPositiveButton(R.string.continue_text) { _, _ -> purchase(PurchasesManager.PREMIUM_SKU) }
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                    .show()
            return
        }

        toggleBottomSheet(false)
        (navigator.current as? AppFragment)?.notifyItemChanged(if (ROTATION_APPS == preferenceName) ROTATION_LOCK else EXCLUDED_ROTATION_LOCK)
    }

    companion object {

        fun newInstance(@RotationGestureConsumer.PersistedSet preferenceName: String) =
                PackageFragment().apply { this.preferenceName = preferenceName }
    }
}

