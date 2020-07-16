package com.catp.thundersimlineup.ui.lineuplist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.catp.thundersimlineup.R
import com.catp.thundersimlineup.ui.vehiclelist.VehicleItem
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import java.security.SecureRandom
import kotlin.math.absoluteValue

/**
 * Created by mikepenz on 30.12.15.
 * This is a FastAdapter adapter implementation for the awesome Sticky-Headers lib by timehop
 * https://github.com/timehop/sticky-headers-recyclerview
 */
class StickyHeaderAdapter<Item : GenericItem> : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

    val rgen = SecureRandom()
    val alpha = 150
    val headersColor = listOf(
        Color.argb(alpha, 0xD3, 0x2F, 0x2F),
        Color.argb(alpha, 0x30, 0x3F, 0x9F),
        Color.argb(alpha, 0x38, 0x8E, 0x3C),
        Color.argb(alpha, 0xF5, 0x7C, 0x00),
        Color.argb(alpha, 0x5D, 0x40, 0x37),
        Color.argb(alpha, 0x45, 0x5A, 0x64)
    )

    /*
     * GENERAL CODE NEEDED TO WRAP AN ADAPTER
     */

    //private AbstractAdapter mParentAdapter;
    //keep a reference to the FastAdapter which contains the base logic
    /**
     * @return the reference to the FastAdapter
     */
    var fastAdapter: FastAdapter<Item>? = null
        private set

    override fun getHeaderId(position: Int): Long {
        //in our sample we want a separate header per first letter of our items
        //this if is not necessary for your code, we only use it as this sticky header is reused for different item implementations
        val item = getItem(position) as VehicleItem
        return item.header?.hashCode()?.absoluteValue?.toLong() ?: 0
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        //we create the view for the header
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_title, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView as TextView

        textView.text = when (val item = getItem(position)) {
            is VehicleItem -> item.header?.toString()
            // Fallback to same value
            else -> textView.text
        }
        holder.itemView.setBackgroundColor(headersColor.random())
    }

    /**
     * Wrap the FastAdapter with this AbstractAdapter and keep its reference to forward all events correctly
     *
     * @param fastAdapter the FastAdapter which contains the base logic
     * @return this
     */
    fun wrap(fastAdapter: FastAdapter<Item>): StickyHeaderAdapter<Item> {
        //this.mParentAdapter = abstractAdapter;
        this.fastAdapter = fastAdapter
        return this
    }

    /**
     * overwrite the registerAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    override fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.registerAdapterDataObserver(observer)
        fastAdapter?.registerAdapterDataObserver(observer)
    }

    /**
     * overwrite the unregisterAdapterDataObserver to correctly forward all events to the FastAdapter
     *
     * @param observer
     */
    override fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
        super.unregisterAdapterDataObserver(observer)
        fastAdapter?.unregisterAdapterDataObserver(observer)
    }

    /**
     * overwrite the getItemViewType to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return fastAdapter?.getItemViewType(position) ?: 0
    }

    /**
     * overwrite the getItemId to correctly return the value from the FastAdapter
     *
     * @param position
     * @return
     */
    override fun getItemId(position: Int): Long {
        return fastAdapter?.getItemId(position) ?: 0
    }

    /**
     * make sure we return the Item from the FastAdapter so we retrieve the item from all adapters
     *
     * @param position
     * @return
     */
    fun getItem(position: Int): Item? {
        return fastAdapter?.getItem(position)
    }

    /**
     * make sure we return the count from the FastAdapter so we retrieve the count from all adapters
     *
     * @return
     */
    override fun getItemCount(): Int {
        return fastAdapter?.itemCount ?: 0
    }

    /**
     * the onCreateViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val fastAdapter = this.fastAdapter
            ?: throw RuntimeException("A adapter needs to be wrapped")
        return fastAdapter.onCreateViewHolder(parent, viewType)
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        fastAdapter?.onBindViewHolder(holder, position)
    }

    /**
     * the onBindViewHolder is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @param position
     * @param payloads
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        fastAdapter?.onBindViewHolder(holder, position, payloads)
    }

    /**
     * the setHasStableIds is managed by the FastAdapter so forward this correctly
     *
     * @param hasStableIds
     */
    override fun setHasStableIds(hasStableIds: Boolean) {
        fastAdapter?.setHasStableIds(hasStableIds)
    }

    /**
     * the onViewRecycled is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        fastAdapter?.onViewRecycled(holder)
    }

    /**
     * the onFailedToRecycleView is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     * @return
     */
    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return fastAdapter?.onFailedToRecycleView(holder) ?: false
    }

    /**
     * the onViewDetachedFromWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        fastAdapter?.onViewDetachedFromWindow(holder)
    }

    /**
     * the onViewAttachedToWindow is managed by the FastAdapter so forward this correctly
     *
     * @param holder
     */
    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        fastAdapter?.onViewAttachedToWindow(holder)
    }

    /**
     * the onAttachedToRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        fastAdapter?.onAttachedToRecyclerView(recyclerView)
    }

    /**
     * the onDetachedFromRecyclerView is managed by the FastAdapter so forward this correctly
     *
     * @param recyclerView
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        fastAdapter?.onDetachedFromRecyclerView(recyclerView)
    }
}
