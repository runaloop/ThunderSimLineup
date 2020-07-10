package com.catp.thundersimlineup.ui.lineuplist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.catp.model.VehicleType
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.data.db.entity.Lineup
import com.catp.thundersimlineup.data.db.entity.Team
import com.catp.thundersimlineup.data.db.entity.Vehicle

class LineupAdapter : RecyclerView.Adapter<ViewHolder>() {

    var dataset: List<ViewItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val id = when (viewType) {
            ViewItem.TYPE.TITLE.ordinal -> R.layout.list_item_title
            ViewItem.TYPE.VEHICLE.ordinal -> R.layout.list_item_title
            ViewItem.TYPE.VEHICLE_FAVORITE.ordinal -> R.layout.list_item_title
            else -> error("No layout specified for a type: $viewType")
        }
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_title, parent, false
        )

        return ViewHolder(view)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dataset[position].apply(holder)
    }

    fun setNewLineup(context: Context, lineup: LineupRequestInteractor.LineupForToday) {
        dataset = DataSetCreator(context).make(lineup)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return dataset[position].itemType()
    }
}

class ViewHolder(val title: View) : RecyclerView.ViewHolder(title) {
    private var titleView: TextView
    private var brView: TextView

    init {
        titleView = title.findViewById(R.id.tvItemTitle)
        brView = title.findViewById(R.id.tvItemBR)
    }

    fun setText(text: String) {
        titleView.text = text
    }

    @SuppressLint("ResourceAsColor")
    fun setTitle() {

    }

    fun setBR(br: String) {
        brView.text = br
    }

    @SuppressLint("ResourceAsColor")
    fun setFavorite(favorite: Boolean, color: Int) {
        if (favorite) {

        }
    }
}

abstract class ViewItem {
    abstract fun apply(viewHolder: ViewHolder)
    abstract fun itemType(): Int
    enum class TYPE {
        TITLE, VEHICLE, VEHICLE_FAVORITE
    }
}

class ViewTitle(val title: String) : ViewItem() {
    override fun apply(viewHolder: ViewHolder) {
        viewHolder.setText(title)
        viewHolder.setTitle()
    }

    override fun itemType(): Int = TYPE.TITLE.ordinal
}

class ViewVehicle(val vehicle: Vehicle) : ViewItem() {
    override fun apply(viewHolder: ViewHolder) {
        viewHolder.setText("${vehicle.nation} ${vehicle.title}")
        viewHolder.setBR(vehicle.br)
        viewHolder.setFavorite(vehicle.isFavorite, 0xff0000)
    }

    override fun itemType(): Int = TYPE.VEHICLE.ordinal
}

//Takes list of Lineups, fills it with view items: Titles like commands, vehicle type titles, vehicle sorted by type/favorite mode etc
class DataSetCreator(val context: Context) {
    fun make(lineup: LineupRequestInteractor.LineupForToday): List<ViewItem> {
        val data = mutableListOf<ViewItem>()
        val list = mutableListOf(lineup.lineupNow.first, lineup.lineupNow.second)
        if (lineup.timeToChange.isZero) {
            list += lineup.lineupThen.first
            list += lineup.lineupThen.second
        }
        list.filterNotNull().forEach { fillSet(it, data) }
        return data
    }

    private fun fillSet(
        lineup: Lineup,
        dataset: MutableList<ViewItem>
    ) {

        val teams = mapOf(
            lineup.teamA to context.getString(R.string.team_a_title),
            lineup.teamB to context.getString(R.string.team_b_title)
        )
        teams.keys.forEach { team ->
            fillTeam(team, dataset, "${lineup.lineupEntity.name} ${teams[team]}")
        }
    }

    private fun fillTeam(
        team: Team,
        dataset: MutableList<ViewItem>,
        title: String
    ) {
        with(team) {
            val vehicles = mapOf(
                VehicleType.TANK to context.getString(R.string.tanks_title),
                VehicleType.PLANE to context.getString(R.string.planes_title),
                VehicleType.HELI to context.getString(R.string.helis_title)
            )
            team.vehicles.groupBy { it.type }.forEach { (type, list) ->
                if (list.isNotEmpty()) {
                    dataset += ViewTitle("$title ${vehicles[type]}")
                    fillVehicleList(list, dataset)
                }
            }
        }
    }

    //Fav, A, B, Tank, Plane, Heli
    private fun fillVehicleList(vehicleList: List<Vehicle>, dataset: MutableList<ViewItem>) {
        vehicleList
            .sortedBy { it.isFavorite }
            .sortedBy { it.nation }
            .forEach { vehicle ->
                dataset += ViewVehicle(vehicle)
            }
    }
}