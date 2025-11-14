package com.love.compatibility.ui.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.love.compatibility.R
import com.love.compatibility.core.extensions.gone
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.strings
import com.love.compatibility.core.extensions.visible
import com.love.compatibility.core.helper.DateHelper
import com.love.compatibility.data.model.status.Quadruple
import com.love.compatibility.data.model.status.StatusLoveTestModel
import com.love.compatibility.databinding.ItemStatusBinding

class StatusAdapter(val context: Context) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    var item = StatusLoveTestModel()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        return StatusViewHolder(ItemStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        holder.bind(item, position)
    }

    override fun getItemCount(): Int = 2

    inner class StatusViewHolder(val binding: ItemStatusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StatusLoveTestModel, position: Int) {
            binding.apply {
                if (position == 0) {
                    ctlTab1.visible()
                    ctlTab2.gone()

                    val shortSlogan = if (item.countLove.shotSlogan == "") "xxxx" else item.countLove.shotSlogan
                    val (countDay, startDate) = if (item.countLove.startDate == -1L || DateHelper.checkDateValid(item.countLove.startDate)){
                        context.strings(R.string.zero_day) to
                                "xxxx"
                    } else{
                        context.getString(R.string.value_days, DateHelper.daysFrom(item.countLove.startDate).toString())to
                                context.getString(R.string.value_start_date, DateHelper.formatDot(item.countLove.startDate))
                    }

                    tvShortSlogan.select()
                    tvCountDay.select()
                    tvStartDate.select()

                    tvShortSlogan.text = shortSlogan
                    tvCountDay.text = countDay
                    tvStartDate.text = startDate
                } else {
                    ctlTab1.gone()
                    ctlTab2.visible()

                    val date = DateHelper.diffExactYearMonthWeekDay(item.countLove.startDate)

                    tvValueYear.select()
                    tvValueMonth.select()
                    tvValueWeek.select()
                    tvValueDay.select()

                    tvYear.select()
                    tvMonth.select()
                    tvWeek.select()
                    tvDay.select()

                    tvValueYear.text = date.first.toString()
                    tvValueMonth.text = date.second.toString()
                    tvValueWeek.text = date.third.toString()
                    tvValueDay.text = date.fourth.toString()
                    tvStartDate2.text = if (item.countLove.startDate == -1L) "xxxx" else "# ${DateHelper.formatSlash(item.countLove.startDate)} #"

                    tvLongSlogan.text = "\"${context.getString(item.longSlogan)}\""
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(getItem: StatusLoveTestModel){
        item = getItem
        notifyDataSetChanged()
    }
}