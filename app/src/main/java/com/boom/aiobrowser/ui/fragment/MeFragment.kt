package com.boom.aiobrowser.ui.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.base.BaseFragment
import com.boom.aiobrowser.data.UserData
import com.boom.aiobrowser.databinding.NewsFragmentMeBinding
import com.boom.aiobrowser.firebase.FirebaseConfig
import com.boom.aiobrowser.model.APPUserViewModel
import com.boom.aiobrowser.other.LoginConfig.SIGN_LOGIN
import com.boom.aiobrowser.other.LoginConfig.SIGN_LOGIN_ONE_TAP
import com.boom.aiobrowser.other.ShortManager
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.boom.aiobrowser.tools.AppLogs
import com.boom.aiobrowser.tools.BrowserManager
import com.boom.aiobrowser.tools.CacheManager
import com.boom.aiobrowser.tools.GlideManager
import com.boom.aiobrowser.tools.JumpDataManager
import com.boom.aiobrowser.tools.SubscribeManager
import com.boom.aiobrowser.tools.UIManager
import com.boom.aiobrowser.tools.toJson
import com.boom.aiobrowser.ui.activity.AboutActivity
import com.boom.aiobrowser.ui.activity.DownloadActivity
import com.boom.aiobrowser.ui.activity.HistoryActivity
import com.boom.aiobrowser.ui.pop.ClearPop
import com.boom.aiobrowser.ui.pop.ConfigPop
import com.boom.aiobrowser.ui.pop.DefaultPop
import com.boom.aiobrowser.ui.pop.LoadingPop
import com.boom.aiobrowser.ui.pop.SubInfoPop
import com.boom.aiobrowser.ui.pop.SubManagePop
import com.boom.aiobrowser.ui.pop.SubPop
import com.boom.aiobrowser.ui.pop.TabPop
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import pop.basepopup.BasePopupWindow.OnDismissListener
import java.lang.ref.WeakReference

class MeFragment : BaseFragment<NewsFragmentMeBinding>() {

    private val viewModel by lazy {
        viewModels<APPUserViewModel>()
    }

    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var oneTapClient: SignInClient

    val firebaseAuth by lazy{ FirebaseAuth.getInstance()}


    override fun startLoadData() {
        if (APP.isDebug){
            fBinding.llRoot.apply {
                addView(AppCompatTextView(rootActivity).apply {
                    text = "启动配置弹窗"
                    setOneClick {
                        ConfigPop(rootActivity).createPop()
                    }
                })
            }
        }
    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }

    var tempTag = "loginTAG"

    override fun setListener() {
        fBinding?.apply {
            llLogin.setOneClick {
                ivUser.performClick()
            }
            ivUser.setOneClick {
                var user = CacheManager.getUser()
                if (user == null){
                    login()
                }else{
                    var builder =  AlertDialog.Builder(rootActivity);
                    builder.setTitle(getString(R.string.app_logout_title))
//                builder.setMessage(getString(R.string.now_heart_error_tips2))
                    builder.setCancelable(true);
//                    builder.setNegativeButton(getString(R.string.app_logout_clear)) { dialog, which ->
//                        viewModel.deleteUser()
//                    }
                    builder.setNeutralButton(getString(R.string.app_logout)) { dialog, which ->
                        outUser()
                    }

                    var deleteDialog = builder.create()
                    deleteDialog!!.show()
//                    var button2 = deleteDialog!!.getButton(AlertDialog.BUTTON_NEGATIVE)
//                    button2.isAllCaps = false
//                    button2.setTextColor(ContextCompat.getColor(attachActivity,R.color.black_152940))
                    var button1 = deleteDialog!!.getButton(AlertDialog.BUTTON_NEUTRAL)
                    button1.isAllCaps = false
                    button1.setTextColor(ContextCompat.getColor(rootActivity,R.color.color_blue_0066FF))
                }
            }
            llNewTab.setOneClick {
                showTabPop()
                PointEvent.posePoint(PointEventKey.profile_newtab)
            }
            llClearData.setOneClick {
                clearData()
                PointEvent.posePoint(PointEventKey.profile_cleardate)
            }
            llHistory.setOneClick {
                if (context is BaseActivity<*>) {
                    (context as BaseActivity<*>).startActivity(
                        Intent(
                            context,
                            HistoryActivity::class.java
                        )
                    )
                }
                PointEvent.posePoint(PointEventKey.profile_history)
            }
            llWidget.setOneClick {
                PointEvent.posePoint(PointEventKey.profile_add_widget)
                ShortManager.addWidgetToLaunch(rootActivity, true)
            }
            llAbout.setOneClick {
                if (context is BaseActivity<*>) {
                    (context as BaseActivity<*>).startActivity(
                        Intent(
                            context,
                            AboutActivity::class.java
                        )
                    )
                }
                PointEvent.posePoint(PointEventKey.profile_about)
            }
            llDownload.setOneClick {
                if (context is BaseActivity<*>) {
                    DownloadActivity.startActivity(context as BaseActivity<*>,"home_more_pop")
                }
                PointEvent.posePoint(PointEventKey.profile_download)
            }
            llSub.setOneClick {
                if (CacheManager.isSubscribeMember.not()){
                    SubPop(rootActivity).createPop{
                        updateVIPUI()
                    }
                }else{
                    SubInfoPop(rootActivity).createPop()
                }
            }

//            llvip1.setOneClick {
//                if (CacheManager.isSubscribeMember.not()){
//                    SubscribeManager.subscribeShop(WeakReference(rootActivity),"vip_weekly")
//                }else{
//                    ToastUtils.showLong("当前已有订阅")
//                }
//            }
//            llvip2.setOneClick {
//                if (CacheManager.isSubscribeMember.not()){
//                    SubscribeManager.subscribeShop(WeakReference(rootActivity),"vip_monthly")
//                }else{
//                    ToastUtils.showLong("当前已有订阅")
//                }
//            }
//            llvip3.setOneClick {
//                if (CacheManager.isSubscribeMember.not()){
//                    SubscribeManager.subscribeShop(WeakReference(rootActivity),"vip_quarterly")
//                }else{
//                    ToastUtils.showLong("当前已有订阅")
//                }
//            }
            updateUI()
        }
        viewModel.value.uerLiveData.observe(this){
            pop?.dismiss()
            updateUI()
            PointEvent.posePoint(PointEventKey.login_suc_net)
        }
        viewModel.value.failLiveData.observe(this){
            pop?.dismiss()
            updateUI()
            PointEvent.posePoint(PointEventKey.login_fail_net)
        }
    }

    fun outUser(){
        if (firebaseAuth!=null){
            firebaseAuth.signOut()
        }
        CacheManager.saveUser(null)
        updateUI()
    }

    private fun login() {
        var isShow = pop?.isShowing?:false
        if (isShow.not()){
            pop = LoadingPop(rootActivity)
            pop!!.createPop()
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val googleSignInClient = GoogleSignIn.getClient(rootActivity, gso)

        //启动登录，在onActivityResult方法回调
        val signInIntent = googleSignInClient.signInIntent
        rootActivity.startActivityForResult(signInIntent, SIGN_LOGIN)
    }

    fun updateUI() {
        fBinding?.apply {
            var isDefault = BrowserManager.isDefaultBrowser()
            if (APP.instance.clickSetBrowser) {
                APP.instance.clickSetBrowser = false
                PointEvent.posePoint(if (isDefault) PointEventKey.default_pop_set_s else PointEventKey.default_pop_set_f)
            }
            updateVIPUI()
            if (isDefault) {
                llBrowser.visibility = View.GONE
            } else {
                llBrowser.visibility = View.VISIBLE
                switchBrowser.setChecked(isDefault)
                switchBrowser.isClickable = false
                llBrowser.setOnClickListener {
                    PointEvent.posePoint(PointEventKey.profile_setdefault)
                    if (isDefault.not()) {
                        var pop = DefaultPop(rootActivity)
                        pop.createPop()
                    }
                }
            }
            var user = CacheManager.getUser()
            if (user == null){
                fBinding.apply {
                    tvUser.text = getString(R.string.app_login)
                    ivLogin.visibility = View.VISIBLE
                    ivUser.setImageResource(R.mipmap.ic_default_user)
                }
            }else{
                fBinding.apply {
                    tvUser.text = user.name
                    ivLogin.visibility = View.GONE
                    GlideManager.loadImg(this@MeFragment,ivUser,user.url)
                }
            }
            var data = CacheManager.locationData
            if (data == null || data.locationArea.isNullOrEmpty()){
                fBinding.tvLocation.text = getString(R.string.app_location)
            }else{
                fBinding.tvLocation.text = data.locationCity
            }
        }
    }

    private fun updateVIPUI() {
        if (CacheManager.isSubscribeMember){
            fBinding.llBg.setBackgroundResource(R.drawable.shape_bg_vip)
            fBinding.ivVipTips.visibility = View.VISIBLE
            fBinding.tvAD.text = rootActivity.getString(R.string.app_vip_user)
        }else{
            fBinding.llBg.setBackgroundResource(R.drawable.shape_bg_no_vip)
            fBinding.ivVipTips.visibility = View.GONE
            fBinding.tvAD.text = rootActivity.getString(R.string.app_no_ads)
        }
    }

    fun clearData() {
        ClearPop(rootActivity).createPop {
            CacheManager.clearAll()
            JumpDataManager.toMain()
        }
    }

    fun showTabPop() {
        var tabPop = TabPop(rootActivity)
        tabPop.createPop()
        tabPop.setOnDismissListener(object : OnDismissListener() {
            override fun onDismiss() {
            }
        })
    }

    var pop:LoadingPop?=null

    override fun setShowView() {
//        oneTapClient = Identity.getSignInClient(rootActivity)
//        signInRequest = BeginSignInRequest.builder()
//            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build())
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    .setFilterByAuthorizedAccounts(false)
//                    .build()
//            ).setAutoSelectEnabled(true).build()
////https://developers.google.com/identity/one-tap/android/get-saved-credentials
//        oneTapClient.beginSignIn(signInRequest)
//            .addOnSuccessListener(rootActivity) { result ->
//                try {
//                    startIntentSenderForResult(
//                        result.pendingIntent.intentSender, SIGN_LOGIN_ONE_TAP,
//                        null, 0, 0, 0, null
//                    )
//                } catch (e: IntentSender.SendIntentException) {
//                    AppLogs.eLog(tempTag, "Couldn't start One Tap UI: ${e.localizedMessage}")
//                    loginError()
//                }
//            }
//            .addOnFailureListener(rootActivity) { e ->
//                // No saved credentials found. Launch the One Tap sign-up flow, or
//                // do nothing and continue presenting the signed-out UI.
//                AppLogs.eLog(tempTag, "Couldn't start One Tap UI: ${e.localizedMessage}")
//                loginError()
//            }
    }

    private fun saveGoogleUser(user: FirebaseUser?, idToken: String) {
        if (user != null) {
//            val uid = user.uid
//            val displayName = user.displayName
//            val email = user.email
//            NowLogs.dLog(acTAG,"saveUser_uid:${uid} displayName:${displayName} email:${email}")
            PointEvent.posePoint(PointEventKey.login_suc)
            viewModel.value.createUser(UserData().apply {
                from = "Google"
                otherId = user.uid
                name = user.displayName?:""
                email = user.email?:""
                url = user.photoUrl?.toString()?:""
                token = idToken
            })
        }else{
            AppLogs.eLog(tempTag,"saveGoogleUser:user = null")
            loginError()
        }
    }


    /**
     * firebase 验证 google 登录
     *
     * @param idToken google 授权成功 token
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(rootActivity, object : OnCompleteListener<AuthResult?> {
                    override fun onComplete(p0: Task<AuthResult?>) {
                        if (p0.isSuccessful) {
                            //验证成功，请求我们服务端保存用户信息
                            val user: FirebaseUser? = firebaseAuth.currentUser
                            saveGoogleUser(user,idToken)
                        }else{
                            AppLogs.eLog(tempTag,"firebaseAuthWithGoogle:${p0.exception}")
                            loginError()
                        }
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
            AppLogs.eLog(tempTag,"firebaseAuthWithGoogle_start:${e.localizedMessage}")
            loginError()
        }
    }

    fun result(requestCode: Int, resultCode: Int, data: Intent?){
        when (requestCode) {
            SIGN_LOGIN -> {
                var  task = GoogleSignIn.getSignedInAccountFromIntent(data);
                if (task == null) {
                    AppLogs.eLog(tempTag,"task：null");
                    loginError()
                    return
                }
                kotlin.runCatching {
                    val account = task.getResult(ApiException::class.java)
                    if (account != null) {
                        //firebase验证google登录
                        firebaseAuthWithGoogle(account.idToken?:"")
                    }else{
                        loginError()
                    }
                    AppLogs.eLog(tempTag,"Id:" + account?.getId() + "|Email:" + account?.getEmail() + "|IdToken:" + account?.getIdToken())
                }.onFailure {
                    AppLogs.eLog(tempTag,it.stackTraceToString())
                    loginError()
                }
            }
            SIGN_LOGIN_ONE_TAP ->{
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            AppLogs.dLog(tempTag, "Got ID token:" + idToken)
                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            firebaseAuth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(rootActivity) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        AppLogs.dLog(tempTag, "signInWithCredential:success")
                                        val user = firebaseAuth.currentUser
                                        user?.getIdToken(true)
                                            ?.addOnCompleteListener(OnCompleteListener<GetTokenResult?> { task ->
                                                if (task.isSuccessful) {
                                                    val idToken: String? = task.result.token
                                                    saveGoogleUser(user, idToken?:"")
                                                } else {
                                                    loginError()
                                                    AppLogs.eLog(tempTag, "Handle error:"+task.exception?.localizedMessage)
                                                }
                                            })
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        AppLogs.eLog(tempTag, "signInWithCredential:failure:"+task.exception?.localizedMessage)
                                        loginError()
                                    }
                                }
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            AppLogs.dLog(tempTag, "Got password.")
                            loginError()
                        }
                        else -> {
                            // Shouldn't happen.
                            AppLogs.dLog(tempTag, "No ID token or password!")
                            loginError()
                        }
                    }
                } catch (e: ApiException) {
                    AppLogs.eLog(tempTag,e.stackTraceToString())
                    loginError()
                    // ...
                }

            }
            else -> {}
        }
    }

    private fun loginError() {
        pop?.dismiss()
        PointEvent.posePoint(PointEventKey.login_fail)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): NewsFragmentMeBinding {
        return NewsFragmentMeBinding.inflate(layoutInflater)
    }
}