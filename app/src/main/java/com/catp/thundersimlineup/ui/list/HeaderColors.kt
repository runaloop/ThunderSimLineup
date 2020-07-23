package com.catp.thundersimlineup.ui.list

import android.graphics.Color
import com.catp.model.VehicleType

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
object HeaderColors{

    val alpha = 150
    val headersColor = listOf(
        Color.argb(alpha, 0xD3, 0x2F, 0x2F),
        Color.argb(alpha, 0x30, 0x3F, 0x9F),
        Color.argb(alpha, 0x38, 0x8E, 0x3C),
        Color.argb(alpha, 0xF5, 0x7C, 0x00),
        Color.argb(alpha, 0x5D, 0x40, 0x37),
        Color.argb(alpha, 0x45, 0x5A, 0x64)
    )

    fun getByVehicleType(teamA: Boolean, type: VehicleType): Int{
        val teamN = if(teamA) 0 else 3
        return headersColor[(teamN + type.ordinal)% headersColor.size]
    }

    fun getCountry(country: String): Int{
        return getRandom()
    }
    fun getRandom(): Int{
        return headersColor.random()
    }



    /*
     * GENERAL CODE NEEDED TO WRAP AN ADAPTER
     */

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    /**
     * @return the reference to the FastAdapter
     */


}
