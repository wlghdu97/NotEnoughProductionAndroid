package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Ignore
import com.xhlab.nep.shared.domain.item.model.ElementView

@DatabaseView(
    viewName = "element_view",
    value = """
        SELECT 
        element.id, 
        CASE
            WHEN element.type = 3 /*ore dict chain*/ THEN (
                SELECT
                CASE
                    WHEN COUNT(ore_element.unlocalized_name) = 1 THEN (
                        SELECT 
                        CASE 
                            WHEN COUNT(replacement.name) = 1 THEN (
                                SELECT element.localized_name FROM element
                                WHERE element.id = replacement.element_id
                            )
                            ELSE "Ore Chain"
                        END
                        FROM element
                        INNER JOIN replacement ON replacement.name = element.unlocalized_name
                        WHERE element.unlocalized_name = ore_element.unlocalized_name
                    )
                    ELSE "Ore Chain"
                END 
                FROM element AS ore_element
                INNER JOIN ore_dict_chain 
                ON ore_dict_chain.chain_element_id = element.unlocalized_name
                WHERE ore_element.id = ore_dict_chain.element_id
            )
            ELSE element.localized_name 
        END localized_name,
        CASE
            WHEN element.type = 3 /*ore dict chain*/ THEN (
                SELECT
                CASE
                    WHEN COUNT(ore_element.unlocalized_name) = 1 THEN (
                        SELECT 
                        CASE 
                            WHEN COUNT(replacement.name) = 1 THEN (
                                SELECT element.unlocalized_name FROM element
                                WHERE element.id = replacement.element_id
                            )
                            ELSE element.unlocalized_name 
                        END
                        FROM element
                        INNER JOIN replacement ON replacement.name = element.unlocalized_name
                        WHERE element.unlocalized_name = ore_element.unlocalized_name
                    )
                    ELSE GROUP_CONCAT(ore_element.unlocalized_name, ", ")
                END 
                FROM element AS ore_element
                INNER JOIN ore_dict_chain
                ON ore_dict_chain.chain_element_id = element.unlocalized_name
                WHERE ore_element.id = ore_dict_chain.element_id
            )
            ELSE element.unlocalized_name
        END unlocalized_name, 
        element.type
        FROM element
    """
)
data class RoomElementView(
    override val id: Long,
    @ColumnInfo(name = "localized_name")
    override val localizedName: String,
    @ColumnInfo(name = "unlocalized_name")
    override val unlocalizedName: String,
    override val type: Int,
    @ColumnInfo(name = "meta_data")
    override val metaData: String?
) : ElementView() {
    @Ignore
    override val amount: Int = 0
}