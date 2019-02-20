package com.auth.face.faceauth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.auth.face.faceauth.R
import com.auth.face.faceauth.base.Utils
import com.matilock.mati_kyc_sdk.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: RegisterViewModel

    private val callbackManager = MatiCallbackManager.createNew()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        btRegister.setOnClickListener { v -> viewModel.register(etUserName.text.toString(), etEmail.text.toString(), etUserPassword.text.toString(), etUserPasswordConfirm.text.toString()) }
        etUserPasswordConfirm.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.register(etUserName.text.toString(), etEmail.text.toString(), etUserPassword.text.toString(), etUserPasswordConfirm.text.toString())
                    true
                }
                else -> false
            }
        }

        initializeViewModel()
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        viewModel.getLoadingResId().observe(this, Observer(this::setLoading));
        viewModel.getUniqueId().observe(this, Observer(this::handleUniqueId));
    }

    private fun handleUniqueId(uniqueId: String?) {
        Log.v("FaceAuth", "Unique id = " + uniqueId)
        val metadata = Metadata.Builder()
                .with("userId", uniqueId)
                .with("type", 2)
                .build()
        Mati.getInstance().metadata = metadata

        MatiLoginManager.getInstance().registerCallback(callbackManager, object : MatiCallback {
            override fun onSuccess(pLoginResult: LoginResult) {
                finish()
            }

            override fun onCancel() {
                // do nothing
            }

            override fun onError(pLoginError: LoginError) {
                // do nothing
            }
        })

        MatiLoginManager.getInstance().login(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setLoading(loading: Int?) {
        Utils.hideKeyboard(this)
        loading?.let {
            if (it == 0) {
                llInfoPanel.visibility = View.GONE
                llRegister.visibility = View.VISIBLE
            } else {
                llRegister.visibility = View.GONE
                llInfoPanel.visibility = View.VISIBLE
                tvInfoText.text = getString(loading)
            }
        }
    }

}