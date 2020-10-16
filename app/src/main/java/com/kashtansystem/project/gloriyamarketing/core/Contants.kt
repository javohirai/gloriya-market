package com.kashtansystem.project.gloriyamarketing.core

import android.annotation.SuppressLint
import android.content.Context
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import com.kashtansystem.project.gloriyamarketing.R

enum class SoapProject(val projectName:String, val url: String){
    AVON("AVON","http://kit.gloriya.uz:5443/AVON_UT/AVON_UT.1cws"),
    EVYAP("EVYAP","http://kit.gloriya.uz:5443/EVYAP_UT/EVYAP_UT.1cws"),
    LOREAL("LOREAL","http://kit.gloriya.uz:5443/loreal_ut/loreal_ut.1cws"),
    TEST("TEST EVYAP","http://kit.gloriya.uz:5443/EVYAP_TEST/EVYAP_TEST.1cws"),
    ;
    companion object {
        fun getAllProjectsName():Array<String>{
            val array = Array(SoapProject.values().size) {
                return@Array SoapProject.values()[it].projectName
            }
            return array
        }
        fun getAllUrls():Array<String>{
            val array = Array(SoapProject.values().size) {
                return@Array SoapProject.values()[it].url
            }
            return array
        }
        fun getProjectPickerSpinnerItems():Array<String>{
            val array = Array(SoapProject.values().size+1) {
                if(SoapProject.values().size == it) return@Array "По всем проектам"
                return@Array "Проект " + SoapProject.values()[it].projectName
            }
            return array
        }
        fun getProjectByIndex(index: Int):SoapProject{
            return values()[index]
        }
    }   
}

enum class FilterDateType(@StringRes val resText: Int){
    ALL_TIME(R.string.all_time),
    TODAY(R.string.today),
    YESTERDAY(R.string.yesterday),
    WEEK(R.string.duration_week),
    MONTH(R.string.duration_month),
    CUSTOM_INTERVAL(R.string.pick_custom);
    companion object {
        fun getAllTypesName(context: Context):Array<String>{
            val array = Array(FilterDateType.values().size) {
                return@Array context.getString(FilterDateType.values()[it].resText)
            }
            return array
        }
        fun getWithoutAllTime(context: Context):Array<String>{
            val array = Array(FilterDateType.values().size-1) {
                return@Array context.getString(FilterDateType.values()[it+1].resText)
            }
            return array
        }
    }
}