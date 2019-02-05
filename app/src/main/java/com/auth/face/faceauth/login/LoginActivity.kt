package com.auth.face.faceauth.login

import android.Manifest
import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.auth.face.faceauth.R
import com.auth.face.faceauth.base.Utils
import com.auth.face.faceauth.navigation.RegisterScreenRouter
import android.support.v4.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btSignIn.setOnClickListener { v -> viewModel.login(etUserName.text.toString(), etUserPassword.text.toString()) }
        btRegister.setOnClickListener { v -> RegisterScreenRouter().route(this) }
        etUserPassword.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.login(etUserName.text.toString(), etUserPassword.text.toString())
                    true
                }
                else -> false
            }
        }

        initializeViewModel()

        requestPermissionsIfNeeded()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.getLoadingResId().observe(this, Observer(this::setLoading));
        viewModel.getRouter().observe(this, Observer { router -> router?.route(this) })
    }

    private fun setLoading(loading: Int?) {
        Utils.hideKeyboard(this)
        loading?.let {
            if (it == 0) {
                llInfoPanel.visibility = View.GONE
                llSignIn.visibility = View.VISIBLE
            } else {
                llSignIn.visibility = View.GONE
                llInfoPanel.visibility = View.VISIBLE
                tvInfoText.text = getString(loading)
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        if (!checkSelfPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_PERM)
        }
    }

    fun checkSelfPermissions(): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != RC_HANDLE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission is not granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    companion object {
        val RC_HANDLE_PERM: Int = 1000
    }

}
