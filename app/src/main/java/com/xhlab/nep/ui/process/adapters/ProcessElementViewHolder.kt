package com.xhlab.nep.ui.process.adapters

import android.content.res.ColorStateList
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.ui.adapters.ElementViewHolder
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.imageResource
import kotlin.math.min

abstract class ProcessElementViewHolder(itemView: View) : ElementViewHolder(itemView) {
    protected var connectionStatus = Process.Connection(Process.ConnectionStatus.UNCONNECTED, null)

    protected abstract val showConnection: Boolean
    protected abstract val isIconVisible: Boolean

    override fun bindNotNull(model: Element) {
        super.bindNotNull(when (model) {
            is ElementConnection -> model.element
            else -> model
        })

        connectionStatus = when (model is ElementConnection) {
            true -> model.connections[min(adapterPosition, model.connections.size - 1)]
            false -> Process.Connection(Process.ConnectionStatus.UNCONNECTED, null)
        }

        if (!showConnection && isIconVisible) {
            icon.setIcon(model.unlocalizedName)
            icon.imageTintList = null
            icon.rotation = 0f
            icon.scaleX = 1f
            icon.scaleY = 1f
        } else {
            when (connectionStatus.status) {
                Process.ConnectionStatus.CONNECTED_TO_CHILD -> {
                    icon.imageResource = R.drawable.ic_power_24dp
                    icon.imageTintList = getColorStateList(R.color.colorPluggedToChild)
                    icon.rotation = 180f
                    icon.scaleX = 1f
                    icon.scaleY = 1f
                }
                Process.ConnectionStatus.CONNECTED_TO_PARENT -> {
                    icon.imageResource = R.drawable.ic_power_24dp
                    icon.imageTintList = getColorStateList(R.color.colorPluggedToParent)
                    icon.rotation = 0f
                    icon.scaleX = 1f
                    icon.scaleY = 1f
                }
                Process.ConnectionStatus.UNCONNECTED -> {
                    icon.imageResource = R.drawable.ic_outlet_24dp
                    icon.imageTintList = getColorStateList(R.color.colorUnplugged)
                    icon.rotation = 0f
                    icon.scaleX = 0.8f
                    icon.scaleY = 0.8f
                }
                Process.ConnectionStatus.FINAL_OUTPUT -> {
                    icon.imageResource = R.drawable.ic_flag_24dp
                    icon.imageTintList = getColorStateList(R.color.colorFinalOutput)
                    icon.rotation = 0f
                    icon.scaleX = 1f
                    icon.scaleY = 1f
                }
                Process.ConnectionStatus.NOT_CONSUMED -> {
                    icon.imageResource = R.drawable.ic_power_off_24dp
                    icon.imageTintList = getColorStateList(R.color.colorInfinite)
                    icon.rotation = 180f
                    icon.scaleX = 1f
                    icon.scaleY = 1f
                }
            }
        }
    }

    private fun getColorStateList(@ColorRes color: Int): ColorStateList {
        return ColorStateList.valueOf(ContextCompat.getColor(context, color))
    }
}