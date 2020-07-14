package com.xhlab.nep.ui.process.editor

import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.core.view.isGone
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.process.Process.ConnectionStatus.*
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.ui.process.adapters.ElementConnection
import com.xhlab.nep.ui.process.adapters.ProcessElementAdapter
import com.xhlab.nep.ui.process.adapters.ProcessElementViewHolder

class ElementAdapter(
    private val processEditListener: ProcessEditListener? = null
) : ProcessElementAdapter() {

    override fun onCreateElementHolder(itemView: View): ProcessElementViewHolder {
        return ElementViewHolder(itemView)
    }

    inner class ElementViewHolder(itemView: View)
        : ProcessElementViewHolder(itemView), PopupMenu.OnMenuItemClickListener {
        private val menuButton: ImageButton = itemView.findViewById(R.id.btn_menu)

        private val popupMenu = PopupMenu(context, menuButton)
        private val disconnect: MenuItem
        private val connectToParent: MenuItem
        private val connectToChild: MenuItem
        private val markNotConsumed: MenuItem

        override val isIconVisible: Boolean
            get() = this@ElementAdapter.isIconVisible
        override val showConnection: Boolean
            get() = this@ElementAdapter.showConnection

        init {
            with (popupMenu) {
                setOnMenuItemClickListener(this@ElementViewHolder)
                inflate(R.menu.process_element_edit)
                disconnect = menu.findItem(R.id.menu_disconnect)
                connectToParent = menu.findItem(R.id.menu_connect_to_parent)
                connectToChild = menu.findItem(R.id.menu_connect_to_child)
                markNotConsumed = menu.findItem(R.id.menu_mark_not_consumed)
            }
            menuButton.setOnClickListener {
                popupMenu.show()
            }
        }

        override fun bindNotNull(model: Element) {
            super.bindNotNull(model)
            if (recipeNode?.node?.recipe is SupplierRecipe) {
                name.text = model.localizedName
            }

            connectionStatus.status.let {
                val degree = recipeNode?.degree ?: 0
                menuButton.isGone = (it == FINAL_OUTPUT || it == REFERENCE)
                disconnect.isVisible = (it == CONNECTED_TO_PARENT || it == CONNECTED_TO_CHILD)
                connectToParent.isVisible = (degree != 0 && (it != FINAL_OUTPUT || it != NOT_CONSUMED))
                connectToChild.isVisible = (it != FINAL_OUTPUT || it != NOT_CONSUMED)
                markNotConsumed.isVisible = (it == UNCONNECTED || it == NOT_CONSUMED)
                markNotConsumed.setTitle(when (it == UNCONNECTED) {
                    true -> R.string.menu_mark_not_consumed
                    false -> R.string.menu_mark_consumed
                })
            }
        }

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            val to = recipeNode?.node?.recipe
            val element = when (model) {
                is ElementConnection -> (model as ElementConnection).element
                else -> model
            }
            return if (to != null && element != null) {
                when (menuItem.itemId) {
                    R.id.menu_disconnect -> {
                        val from = connectionStatus.connectedRecipe
                        val reversed = connectionStatus.reversed
                        if (from != null) {
                            processEditListener?.onDisconnect(from, to, element, reversed)
                        }
                    }
                    R.id.menu_connect_to_parent -> {
                        val recipe = recipeNode?.node?.recipe
                        val degree = recipeNode?.degree
                        val view = element as ElementView
                        if (recipe != null && degree != null) {
                            processEditListener?.onConnectToParent(recipe, view, degree)
                        }
                    }
                    R.id.menu_connect_to_child -> {
                        val recipe = recipeNode?.node?.recipe
                        val degree = recipeNode?.degree
                        val view = element as ElementView
                        if (recipe != null && degree != null) {
                            processEditListener?.onConnectToChild(recipe, view, degree)
                        }
                    }
                    R.id.menu_mark_not_consumed -> {
                        val consume = connectionStatus.status == NOT_CONSUMED
                        processEditListener?.onMarkNotConsumed(to, element, consume)
                    }
                }
                true
            } else {
                false
            }
        }
    }
}