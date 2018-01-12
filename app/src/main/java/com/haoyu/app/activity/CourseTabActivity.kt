package com.haoyu.app.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.widget.Button
import android.widget.RadioGroup
import com.haoyu.app.base.BaseActivity
import com.haoyu.app.fragment.*
import com.haoyu.app.gdei.student.R
import com.haoyu.app.view.AppToolBar
import java.util.*

/**
 * 创建日期：2018/1/12.
 * 描述:课程学习首页
 * 作者:xiaoma
 */
class CourseTabActivity : BaseActivity() {
    private lateinit var context: CourseTabActivity
    private lateinit var radioGroup: RadioGroup
    private val PAGE_COURSE = 0  //学习
    private val PAGE_RESOURCES = 1    //资源
    private val PAGE_DISCUSSION = 2   //讨论
    private val PAGE_QUESTION = 3 //问答
    private val PAGE_PROGRESS = 4 //进度
    private val fragments = ArrayList<Fragment>()
    private var training: Boolean = false
    private var courseId: String? = null
    private var courseType: String? = null
    private var pageCourseFragment: PageCourseFragment? = null
    private var pageResourcesFragment: PageResourcesFragment? = null
    private var pageDiscussionFragment: PageDiscussionFragment? = null
    private var pageQuestionFragment: PageQuestionFragment? = null
    private var pageProgressFragment: PageProgressFragment? = null

    override fun setLayoutResID(): Int {
        return R.layout.activity_course_tab
    }

    override fun initView() {
        context = this;
        training = intent.getBooleanExtra("training", false)
        courseId = intent.getStringExtra("courseId");// 课程Id，通过intent获取
        courseType = intent.getStringExtra("courseType")  //课程类型（微课）
        val courseTitle = intent.getStringExtra("courseTitle")
        val toolBar = findViewById<AppToolBar>(R.id.toolBar)
        radioGroup = findViewById(R.id.radioGroup)
        val bt_download = findViewById<Button>(R.id.bt_download)
        toolBar.setTitle_text(courseTitle)
        toolBar.setOnLeftClickListener { finish() }
        setTab(PAGE_COURSE)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_section -> {
                    setTab(PAGE_COURSE)
                }
                R.id.rb_resources -> {
                    setTab(PAGE_RESOURCES)
                }
                R.id.rb_discuss -> {
                    setTab(PAGE_DISCUSSION)
                }
                R.id.rb_wenda -> {
                    setTab(PAGE_QUESTION)
                }
                R.id.rb_progress -> {
                    setTab(PAGE_PROGRESS)
                }
            }
        }
        bt_download.setOnClickListener({
            val intent = Intent()
            intent.setClass(context, AppDownloadActivity::class.java)
            startActivity(intent)
        })
    }

    private fun setTab(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (index) {
            PAGE_COURSE -> {
                if (pageCourseFragment == null) {
                    pageCourseFragment = PageCourseFragment()
                    val bundle = Bundle()
                    bundle.putBoolean("training", training)
                    bundle.putString("entityId", courseId)
                    bundle.putString("courseType", courseType)
                    pageCourseFragment!!.setArguments(bundle)
                    transaction.add(R.id.content, pageCourseFragment)
                    fragments.add(pageCourseFragment!!)
                } else {
                    transaction.show(pageCourseFragment)
                }
            }
            PAGE_RESOURCES -> {
                if (pageResourcesFragment == null) {
                    pageResourcesFragment = PageResourcesFragment()
                    val bundle = Bundle()
                    bundle.putString("entityId", courseId)
                    pageResourcesFragment!!.setArguments(bundle)
                    transaction.add(R.id.content, pageResourcesFragment)
                    fragments.add(pageResourcesFragment!!)
                } else {
                    transaction.show(pageResourcesFragment)
                }
            }
            PAGE_DISCUSSION -> {
                if (pageDiscussionFragment == null) {
                    pageDiscussionFragment = PageDiscussionFragment()
                    val bundle = Bundle()
                    bundle.putString("entityId", courseId)
                    pageDiscussionFragment!!.setArguments(bundle)
                    transaction.add(R.id.content, pageDiscussionFragment)
                    fragments.add(pageDiscussionFragment!!)
                } else {
                    transaction.show(pageDiscussionFragment)
                }
            }
            PAGE_QUESTION -> {
                if (pageQuestionFragment == null) {
                    pageQuestionFragment = PageQuestionFragment()
                    val bundle = Bundle()
                    bundle.putString("entityId", courseId)
                    pageQuestionFragment!!.setArguments(bundle)
                    transaction.add(R.id.content, pageQuestionFragment)
                    fragments.add(pageQuestionFragment!!)
                } else {
                    transaction.show(pageQuestionFragment)
                }
            }
            PAGE_PROGRESS -> {
                if (pageProgressFragment == null) {
                    pageProgressFragment = PageProgressFragment()
                    pageProgressFragment!!.setOnSelectCallBack({
                        radioGroup.check(R.id.rb_section)
                        setTab(PAGE_COURSE)
                    })
                    val bundle = Bundle()
                    bundle.putBoolean("training", training)
                    bundle.putString("entityId", courseId)
                    bundle.putString("courseType", courseType)
                    pageProgressFragment!!.setArguments(bundle)
                    transaction.add(R.id.content, pageProgressFragment)
                    fragments.add(pageProgressFragment!!)
                } else {
                    transaction.show(pageProgressFragment)
                }
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        for (fragment in fragments) {
            if (fragment != null) {
                transaction.hide(fragment)
            }
        }
    }
}