package com.auth.face.faceauth.login

import android.Manifest
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import android.content.pm.PackageManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.auth.face.faceauth.R
import com.auth.face.faceauth.base.Utils
import com.auth.face.faceauth.navigation.TermsScreenRouter

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btSignIn.setOnClickListener { v -> viewModel.login(etUserName.text.toString(), etUserPassword.text.toString()) }
        btRegister.setOnClickListener { v -> TermsScreenRouter().route(this) }
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
        viewModel.requestCurrentLocation()
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
        if (!Utils.checkSelfPermissions(permissions, this)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_PERM)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != RC_HANDLE_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission is not granted", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel.requestCurrentLocation()
    }

    companion object {
        val RC_HANDLE_PERM: Int = 1000
    }

}
