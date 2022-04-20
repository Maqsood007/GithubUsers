package com.task.githubusers.utils.extension

import android.view.View
import android.widget.ViewFlipper

/**
 * Created by Muhammad Maqsood on 20/04/2022.
 */
fun View.show() = let { visibility = View.VISIBLE }

fun View.hide() = let { visibility = View.INVISIBLE }

fun View.gone() = let { visibility = View.GONE }

fun ViewFlipper.progress() = let { displayedChild = 0 }

fun ViewFlipper.message() = let { displayedChild = 1 }

fun ViewFlipper.empty() = let { displayedChild = 2 }