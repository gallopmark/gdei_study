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

/**
 * 创建日期：2018/1/12.
 * 描述:课程学习首页
 * 作者:xiaoma
 */
class CourseTabActivity : BaseActivity() {
    private lateinit var context: CourseTabActivity
    private lateinit var radioGroup: RadioGroup
    private var training: Boolean = false
    private var courseId: String? = null
    private var courseType: String? = null
    private val tabs = intArrayOf(1, 2, 3, 4, 5) //学习、资源、讨论、问答、进度
    private val fragments = arrayOfNulls<Fragment>(5)

    override fun setLayoutResID(): Int {
        return R.layout.activity_course_tab
    }

    override fun initView() {
        context = this
        training = intent.getBooleanExtra("training", false)
        courseId = intent.getStringExtra("courseId")// 课程Id，通过intent获取
        courseType = intent.getStringExtra("courseType")  //课程类型（微课）
        val courseTitle = intent.getStringExtra("courseTitle")
        val toolBar = findViewById<AppToolBar>(R.id.toolBar)
        radioGroup = findViewById(R.id.radioGroup)
        val btDownload = findViewById<Button>(R.id.bt_download)
        toolBar.setTitle_text(courseTitle)
        toolBar.setOnLeftClickListener { finish() }
        setTab(tabs[0])
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_section -> {
                    setTab(tabs[0])
                }
                R.id.rb_resources -> {
                    setTab(tabs[1])
                }
                R.id.rb_discuss -> {
                    setTab(tabs[2])
                }
                R.id.rb_wenda -> {
                    setTab(tabs[3])
                }
                R.id.rb_progress -> {
                    setTab(tabs[4])
                }
            }
        }
        btDownload.setOnClickListener({
            val intent = Intent()
            intent.setClass(context, AppDownloadActivity::class.java)
            startActivity(intent)
        })
    }

    private fun setTab(index: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragments(transaction)
        when (index) {
            tabs[0] -> {
                if (fragments[0] == null) {
                    fragments[0] = PageCourseFragment()
                    fragments[0]?.let {
                        val bundle = Bundle()
                        bundle.putBoolean("training", training)
                        bundle.putString("entityId", courseId)
                        bundle.putString("courseType", courseType)
                        it.arguments = bundle
                        transaction.add(R.id.content, it)
                    }
                } else {
                    transaction.show(fragments[0])
                }
            }
            tabs[1] -> {
                if (fragments[1] == null) {
                    fragments[1] = PageResourcesFragment()
                    fragments[1]?.let {
                        val bundle = Bundle()
                        bundle.putString("entityId", courseId)
                        it.arguments = bundle
                        transaction.add(R.id.content, it)
                    }
                } else {
                    transaction.show(fragments[1])
                }
            }
            tabs[2] -> {
                if (fragments[2] == null) {
                    fragments[2] = PageDiscussionFragment()
                    fragments[2]?.let {
                        val bundle = Bundle()
                        bundle.putString("entityId", courseId)
                        it.arguments = bundle
                        transaction.add(R.id.content, it)
                    }
                } else {
                    transaction.show(fragments[2])
                }
            }
            tabs[3] -> {
                if (fragments[3] == null) {
                    fragments[3] = PageQuestionFragment()
                    fragments[3]?.let {
                        val bundle = Bundle()
                        bundle.putString("entityId", courseId)
                        it.arguments = bundle
                        transaction.add(R.id.content, it)
                    }
                } else {
                    transaction.show(fragments[3])
                }
            }
            tabs[4] -> {
                if (fragments[4] == null) {
                    fragments[4] = PageProgressFragment()
                    fragments[4]?.let {
                        (it as PageProgressFragment).setOnSelectCallBack(object : PageProgressFragment.OnSelectCallBack {
                            override fun onClickCallBack() {
                                radioGroup.check(R.id.rb_section)
                                setTab(tabs[0])
                            }
                        })
                        val bundle = Bundle()
                        bundle.putBoolean("training", training)
                        bundle.putString("entityId", courseId)
                        bundle.putString("courseType", courseType)
                        it.arguments = bundle
                        transaction.add(R.id.content, it)
                    }
                } else {
                    transaction.show(fragments[4])
                }
            }
        }
        transaction.commit()
    }

    private fun hideFragments(transaction: FragmentTransaction) {
        for (i in 0 until fragments.size) {
            fragments[i]?.let {
                transaction.hide(it)
            }
        }
    }
}