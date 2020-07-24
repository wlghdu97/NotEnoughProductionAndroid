package com.xhlab.nep.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.text.LineBreaker
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import org.jetbrains.anko.textResource
import java.text.NumberFormat
import java.util.*

open class ElementViewHolder(itemView: View)
    : BindableViewHolder<Element>(itemView) {

    private val integerFormat = NumberFormat.getIntegerInstance(Locale.getDefault())

    protected val icon: ImageView = itemView.findViewById(R.id.icon)
    protected val name: TextView = itemView.findViewById(R.id.name)
    protected val unlocalizedName: TextView = itemView.findViewById(R.id.unlocalized_name)
    protected val type: TextView? = itemView.findViewById(R.id.type)

    protected val context: Context
        get() = itemView.context

    init {
        if (Build.VERSION.SDK_INT >= 23) {
            @SuppressLint("WrongConstant")
            unlocalizedName.breakStrategy = LineBreaker.BREAK_STRATEGY_BALANCED
        }
    }

    override fun bindNotNull(model: Element) {
        if (model is ElementView) {
            type?.textResource = when (model.type) {
                ElementEntity.ITEM -> R.string.txt_item
                ElementEntity.FLUID -> R.string.txt_fluid
                else -> R.string.txt_unknown
            }
        }
        val nameText = getNameText(model)
        name.text = when (model.amount == 0) {
            true -> nameText
            false -> context.formatString(
                R.string.form_item_with_amount,
                integerFormat.format(model.amount),
                nameText
            )
        }
        with (unlocalizedName) {
            maxLines = when (model is ElementView && model.type == ElementEntity.ORE_CHAIN) {
                true -> 2
                false -> 1
            }
            text = when (model.unlocalizedName.isEmpty()) {
                true -> context.getString(R.string.txt_unnamed)
                false -> model.unlocalizedName
            }
        }
    }

    protected fun getNameText(element: Element): String {
        var metaData = ""
        if (element is ElementView) {
            metaData = when (!element.metaData.isNullOrEmpty()) {
                true -> " : ${element.metaData}"
                false -> ""
            }
        }
        val localizedName = when (element.localizedName.isEmpty()) {
            true -> context.getString(R.string.txt_unnamed)
            false -> element.localizedName.trim()
        }
        return "$localizedName$metaData"
    }
}