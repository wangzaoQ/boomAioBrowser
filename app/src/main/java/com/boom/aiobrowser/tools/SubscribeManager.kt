package com.boom.aiobrowser.tools

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.InAppMessageResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.base.BaseActivity
import com.google.common.collect.ImmutableList
import com.ironsource.ac
import com.ironsource.da
import java.lang.ref.WeakReference


object SubscribeManager {

    var TAG = "SubscribeManager"

    val billingclient by lazy {
        var builder = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
        BillingClient.newBuilder(APP.instance).enablePendingPurchases(builder.build())
            .setListener(purchasesUpdatedListener).build()
    }

    val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, list ->
            showTemp("启动购买回调 debugMessage:${billingResult.debugMessage}")
            if (list != null && list.size > 0) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    for (purchase in list) {
                        showTemp("验证购买状态 :${purchase.purchaseState}")
                        if (purchase == null) {
                            continue
                        }
                        //通知服务器支付成功，服务端验证后，消费商品
//                        VerifyProduct(purchase)
                        //TODO客户端同步回调支付成功
                        acknowledged(purchase,tag="UpdatedListener"){
                            if (it !=null){
                                showTemp("验证购买状态 : 成功 变为会员")
                                CacheManager.isSubscribeMember = true
                            }else{
                                showTemp("验证购买状态 : 失败 不是会员")
                                CacheManager.isSubscribeMember = false
                            }
                        }
//                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED){
//                            billingComplete()
//                        }else{
//                            showTemp("启动购买回调 支付失败")
//                        }
                    }
                }
            } else {
                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.NETWORK_ERROR -> {
                        showTemp("启动购买回调 BillingResponseCode.NETWORK_ERROR")
                    }
                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
                        showTemp("启动购买回调 BillingResponseCode.FEATURE_NOT_SUPPORTED")
                    }
                    //服务未连接
                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                        showTemp("启动购买回调 BillingResponseCode.SERVICE_DISCONNECTED")
                    }
                    //取消
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        showTemp("启动购买回调 BillingResponseCode.USER_CANCELED")
                    }
                    //服务不可用
                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
                        showTemp("启动购买回调 BillingResponseCode.SERVICE_UNAVAILABLE")
                    }
                    //购买不可用
                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
                        showTemp("启动购买回调 BillingResponseCode.BILLING_UNAVAILABLE")
                    }
                    //商品不存在
                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                        showTemp("启动购买回调 BillingResponseCode.ITEM_UNAVAILABLE")
                    }
                    //提供给 API 的无效参数
                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                        showTemp("启动购买回调 BillingResponseCode.DEVELOPER_ERROR")
                    }
                    //错误
                    BillingClient.BillingResponseCode.ERROR -> {
                        showTemp("启动购买回调 BillingResponseCode.ERROR")
                    }
                    //未消耗掉
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        showTemp("启动购买回调 BillingResponseCode.ITEM_ALREADY_OWNED")
                    }
                    //不可购买
                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
                        showTemp("启动购买回调 BillingResponseCode.ITEM_NOT_OWNED")
                    }
                }
            }
        }

    private fun billingComplete() {
        billingclient.endConnection()
    }

//    {
//        if (it == 0){
//            isMatch = true
//            showTemp("验证购买状态 : 成功 变为会员")
//        }else{
//            showTemp("验证购买状态 : 失败 不是会员")
//        }
//    }
    fun queryShop(callBack: () -> Unit){
        billingclient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // 连接断开
                showTemp("onBillingServiceDisconnected: 连接断开1 queryShop")
//                retryBillingServiceConnection(productId);
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                // 连接成功
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    showTemp("连接成功")

                    val params =
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()

                    //仅返回有效订阅和未消费的一次性购买。
                    billingclient.queryPurchasesAsync(params,object :PurchasesResponseListener{
                        override fun onQueryPurchasesResponse(p0: BillingResult, p1: MutableList<Purchase>) {
                            showTemp("查询商品成功 dataList:${toJson(p1)}")
                            var allount = 0
                            var successCount = 0
                            if (p1.isNullOrEmpty()){
                                showTemp("queryShop 不是会员 无订阅数据")
                                CacheManager.isSubscribeMember = false
                                callBack.invoke()
                            }else{
                                p1.forEach {
                                    acknowledged(it, tag = "queryShop"){
                                        allount++
                                        if (it!=null){
                                            successCount++
                                            showTemp("queryShop 是会员")
                                            CacheManager.isSubscribeMember = true
                                            callBack.invoke()
                                        }else{
                                            if (allount == p1.size && successCount == 0){
                                                showTemp("queryShop 不是会员 无有效订阅")
                                                CacheManager.isSubscribeMember = false
                                                callBack.invoke()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    })
                } else {
                    // TODO 连接失败
                    showTemp("onBillingServiceDisconnected: 连接断开2 queryShop")

                }
            }
        })
    }

    fun subscribeShop(reference: WeakReference<BaseActivity<*>>, productId: String) {
        billingclient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // 连接断开
                showTemp("onBillingServiceDisconnected: 连接断开1 queryShop")
//                retryBillingServiceConnection(productId);
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                // 连接成功
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showTemp("连接成功")
                    val queryProductDetailsParams =
                        QueryProductDetailsParams.newBuilder()
                            .setProductList(
                                ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                        .setProductId(productId)
                                        .setProductType(BillingClient.ProductType.SUBS)
                                        .build()
                                )
                            )
                            .build()
                    //查询商品详情
                    billingclient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                        // check billingResult
                        // process returned productDetailsList
                        showTemp("查询商品详情成功 dataList:${toJson(productDetailsList)}")
                        var tempDetail:ProductDetails?=null
                        for (i in 0 until productDetailsList.size){
                            var data = productDetailsList.get(i)
                            if (productId == data.productId){
                                //找到商品
                                tempDetail = data
                                break
                            }
                        }
                        tempDetail?.apply {
                            showTemp("找到对应商品")
                            runCatching {
                                showTemp("启动购买1",false)
                                val productDetailsParamsList = listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                        .setProductDetails(tempDetail)
                                        // For One-time products, "setOfferToken" method shouldn't be called.
                                        // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                        // for a list of offers that are available to the user
                                        .setOfferToken(subscriptionOfferDetails?.get(0)?.offerToken ?:"")
                                        .build()
                                )
                                showTemp("启动购买2",false)
                                val billingFlowParams = BillingFlowParams.newBuilder()
                                    .setProductDetailsParamsList(productDetailsParamsList)
                                    .build()
                                var activity = reference.get()
                                showTemp("启动购买3",false)
                                // Launch the billing flow
                                val billingResult = billingclient.launchBillingFlow(activity!!, billingFlowParams)
                                showTemp("启动购买4",false)
                            }.onFailure {
                                showTemp("error 对应商品 ${it.stackTraceToString()}")
                            }
                        }
                    }
                } else {
                    // TODO 连接失败
                    showTemp("onBillingServiceDisconnected: 连接断开2 subscribeShop")
                }
            }
        })

    }

    private fun showTemp(content: String,showToast:Boolean = true) {
        AppLogs.dLog(TAG, content)
        if (showToast){
            ToastUtils.showLong(content)
        }
        Logger.writeLog(APP.instance,content)
    }


    /**
     * 确认购买
     */
    fun acknowledged(purchase: Purchase,tag:String,callBack: (purchase:Purchase?) -> Unit) {
        showTemp("acknowledged tag:${tag}",false)
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                var acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
                billingclient.acknowledgePurchase(acknowledgePurchaseParams) {
                    showTemp("确认购买 code:${it.responseCode} msg:${it.debugMessage}")
                    if (it.responseCode == BillingClient.BillingResponseCode.OK){
                        callBack.invoke(purchase)
                    }else{
                        callBack.invoke(null)
                    }
                }
            }else{
                showTemp("确认购买 当前已确认过")
                callBack.invoke(purchase)
            }
        }else{
            showTemp("确认购买 状态错误")
            callBack.invoke(null)
        }
    }

    fun showNF(reference:WeakReference<BaseActivity<*>>) {
        var activity = reference.get()
        if (activity!=null && activity.isFinishing.not() && activity.isDestroyed.not()){
            val inAppMessageParams = InAppMessageParams.newBuilder()
                .addInAppMessageCategoryToShow(InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                .build()

            billingclient.showInAppMessages(activity,
                inAppMessageParams,
                object : InAppMessageResponseListener {
                    override fun onInAppMessageResponse(inAppMessageResult: InAppMessageResult) {
                        if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                            // The flow has finished and there is no action needed from developers.
                        } else if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED) {
                            // The subscription status changed. For example, a subscription
                            // has been recovered from a suspend state. Developers should
                            // expect the purchase token to be returned with this response
                            // code and use the purchase token with the Google Play
                            // Developer API.
                        }
                    }
                })
        }
    }


    fun unsubscribe(){
        runCatching {
            billingclient.endConnection()
        }
    }


}