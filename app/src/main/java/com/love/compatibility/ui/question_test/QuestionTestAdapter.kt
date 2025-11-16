package com.love.compatibility.ui.question_test

import android.annotation.SuppressLint
import android.content.Context
import com.love.compatibility.R
import com.love.compatibility.core.base.BaseAdapter
import com.love.compatibility.core.extensions.select
import com.love.compatibility.core.extensions.setOnSingleClick
import com.love.compatibility.core.utils.key.ValueKey
import com.love.compatibility.data.model.QuestionModel
import com.love.compatibility.databinding.ItemQuestionTestBinding

class QuestionTestAdapter(val context: Context) :
    BaseAdapter<QuestionModel, ItemQuestionTestBinding>(ItemQuestionTestBinding::inflate) {
    var state: Int = -1
    var onChooseAnswer: ((positionQuestion: Int, positionAnswer: Int) -> Unit) = { _, _ -> }

    override fun onBind(binding: ItemQuestionTestBinding, item: QuestionModel, position: Int) {
        binding.apply {
            val tvAnswerList = arrayListOf(tvAnswer1, tvAnswer2, tvAnswer3, tvAnswer4)

            tvQuestion.text = "${position + 1}. ${item.question}"

            tvAnswerList.forEachIndexed { index, tv ->
                tv.text = item.answerList[index]
                tv.select()
            }

            when (state) {
                ValueKey.STATE_1 -> {
                    if (item.positionMyAnswer == -1) {
                        tvAnswerList.forEach { tv ->
                            tv.setBackgroundResource(R.drawable.bg_12_stroke_pink_solid_white)
                            tv.setTextColor(context.getColor(R.color.pink_ff))
                        }
                    } else {
                        tvAnswerList.forEachIndexed { index, tv ->
                            val (res, color) = if (index == item.positionMyAnswer) {
                                R.drawable.bg_12_solid_pink_fd to R.color.white
                            } else {
                                R.drawable.bg_12_stroke_pink_solid_white to R.color.pink_ff
                            }
                            tv.setBackgroundResource(res)
                            tv.setTextColor(context.getColor(color))
                        }
                    }
                    tvAnswerList.forEachIndexed { index, tv ->
                        tv.setOnSingleClick {
                            onChooseAnswer.invoke(position, index)
                            item.positionMyAnswer = index
                            notifyItemChanged(position)
                        }
                    }
                }

                ValueKey.STATE_2 -> {
                    if (item.positionYouAnswer == -1) {
                        tvAnswerList.forEach { tv ->
                            tv.setBackgroundResource(R.drawable.bg_12_stroke_pink_solid_white)
                            tv.setTextColor(context.getColor(R.color.pink_ff))
                        }
                    } else {
                        tvAnswerList.forEachIndexed { index, tv ->
                            val (res, color) = if (index == item.positionYouAnswer) {
                                R.drawable.bg_12_solid_pink_fd to R.color.white
                            } else {
                                R.drawable.bg_12_stroke_pink_solid_white to R.color.pink_ff
                            }
                            tv.setBackgroundResource(res)
                            tv.setTextColor(context.getColor(color))
                        }
                    }

                    tvAnswerList.forEachIndexed { index, tv ->
                        tv.setOnSingleClick {
                            onChooseAnswer.invoke(position, index)
                            item.positionYouAnswer = index
                            notifyItemChanged(position)
                        }
                    }
                }

                else -> {
                    if ((item.positionMyAnswer == item.positionYouAnswer) && item.positionMyAnswer != -1){
                        tvAnswerList.forEachIndexed { index, tv ->
                            val (res, color) = if (index == item.positionMyAnswer) {
                                R.drawable.bg_12_solid_green to R.color.white
                            } else {
                                R.drawable.bg_12_stroke_pink_solid_white to R.color.pink_ff
                            }

                            tv.setBackgroundResource(res)
                            tv.setTextColor(context.getColor(color))
                        }
                    }else{
                        tvAnswerList.forEachIndexed { index, tv ->
                            val (res, color) = if (item.positionMyAnswer == index){
                                R.drawable.bg_12_solid_blue to R.color.white
                            }else if (item.positionYouAnswer == index){
                                R.drawable.bg_12_solid_red to R.color.white
                            }else{
                                R.drawable.bg_12_stroke_pink_solid_white to R.color.pink_ff
                            }

                            tv.setBackgroundResource(res)
                            tv.setTextColor(context.getColor(color))
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<QuestionModel>, state: Int) {
        items.clear()
        items.addAll(list)
        this.state = state
        notifyDataSetChanged()
    }

}