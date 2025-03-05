package com.boom.aiobrowser.tools

import android.os.Bundle
//import com.android.billingclient.api.AcknowledgePurchaseParams
//import com.android.billingclient.api.BillingClient
//import com.android.billingclient.api.BillingClient.SkuType
//import com.android.billingclient.api.BillingClientStateListener
//import com.android.billingclient.api.BillingFlowParams
//import com.android.billingclient.api.BillingResult
//import com.android.billingclient.api.InAppMessageParams
//import com.android.billingclient.api.InAppMessageResponseListener
//import com.android.billingclient.api.InAppMessageResult
//import com.android.billingclient.api.PendingPurchasesParams
//import com.android.billingclient.api.ProductDetails
//import com.android.billingclient.api.Purchase
//import com.android.billingclient.api.PurchasesResponseListener
//import com.android.billingclient.api.PurchasesUpdatedListener
//import com.android.billingclient.api.QueryProductDetailsParams
//import com.android.billingclient.api.QueryPurchasesParams
import com.blankj.utilcode.util.ToastUtils
import com.boom.aiobrowser.APP
import com.boom.aiobrowser.R
import com.boom.aiobrowser.ad.AioADDataManager
import com.boom.aiobrowser.base.BaseActivity
import com.boom.aiobrowser.point.PointEvent
import com.boom.aiobrowser.point.PointEventKey
import com.google.common.collect.ImmutableList
import java.lang.ref.WeakReference


class SubscribeManager(var successBack: () -> Unit,var failBack: (content:String) -> Unit) {

    var TAG = "SubscribeManager"

//    val billingclient by lazy {
//        var builder = PendingPurchasesParams.newBuilder()
//            .enableOneTimeProducts()
//            .enablePrepaidPlans()
//        BillingClient.newBuilder(APP.instance).enablePendingPurchases(builder.build())
//            .setListener(purchasesUpdatedListener).build()
//    }
//
//    val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, list ->
//            showTemp("启动购买回调 debugMessage:${billingResult.debugMessage}")
//            if (list != null && list.size > 0) {
//                var listCount = 0
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    for (purchase in list) {
//                        showTemp("验证购买状态 :${purchase.purchaseState}")
//                        //通知服务器支付成功，服务端验证后，消费商品
////                        VerifyProduct(purchase)
//                        //TODO客户端同步回调支付成功
//                        acknowledged(purchase,tag="UpdatedListener"){
//                            listCount++
//                            if (it !=null){
//                                successBack.invoke()
//                                showTemp("验证购买状态 : 成功 变为会员")
//                                CacheManager.isSubscribeMember = true
//                                AioADDataManager.clearAllAD()
//                                PointEvent.posePoint(PointEventKey.payment_success)
//                            }else{
//                                failBack.invoke("0")
//                                showTemp("验证购买状态 : 失败 不是会员")
//                                CacheManager.isSubscribeMember = false
//                                PointEvent.posePoint(PointEventKey.payment_failed)
//                            }
//                            if (listCount == list.size){
//                                billingComplete()
//                            }
//                        }
//                    }
//                }
//            } else {
//                when (billingResult.responseCode) {
//                    //取消
//                    BillingClient.BillingResponseCode.USER_CANCELED -> {
//                        showTemp("启动购买回调 BillingResponseCode.USER_CANCELED")
//                        failBack.invoke("1")
//                    }
//                    BillingClient.BillingResponseCode.NETWORK_ERROR -> {
//                        showTemp("启动购买回调 BillingResponseCode.NETWORK_ERROR")
//                        failBack.invoke("0")
//                    }
//                    BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> {
//                        showTemp("启动购买回调 BillingResponseCode.FEATURE_NOT_SUPPORTED")
//                        failBack.invoke("0")
//                    }
//                    //服务未连接
//                    BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
//                        showTemp("启动购买回调 BillingResponseCode.SERVICE_DISCONNECTED")
//                        failBack.invoke("0")
//                    }
//
//                    //服务不可用
//                    BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> {
//                        showTemp("启动购买回调 BillingResponseCode.SERVICE_UNAVAILABLE")
//                        failBack.invoke("0")
//                    }
//                    //购买不可用
//                    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> {
//                        showTemp("启动购买回调 BillingResponseCode.BILLING_UNAVAILABLE")
//                        failBack.invoke("0")
//                    }
//                    //商品不存在
//                    BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
//                        showTemp("启动购买回调 BillingResponseCode.ITEM_UNAVAILABLE")
//                        failBack.invoke("0")
//                    }
//                    //提供给 API 的无效参数
//                    BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
//                        showTemp("启动购买回调 BillingResponseCode.DEVELOPER_ERROR")
//                        failBack.invoke("0")
//                    }
//                    //错误
//                    BillingClient.BillingResponseCode.ERROR -> {
//                        showTemp("启动购买回调 BillingResponseCode.ERROR")
//                        failBack.invoke("0")
//                    }
//                    //未消耗掉
//                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
//                        showTemp("启动购买回调 BillingResponseCode.ITEM_ALREADY_OWNED")
//                        failBack.invoke("0")
//                    }
//                    //不可购买
//                    BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> {
//                        showTemp("启动购买回调 BillingResponseCode.ITEM_NOT_OWNED")
//                        failBack.invoke("0")
//                    }
//                    else ->{
//                        showTemp("启动购买回调 ${billingResult.debugMessage}")
//                        failBack.invoke("0")
//                    }
//                }
//                PointEvent.posePoint(PointEventKey.payment_failed)
//                billingComplete()
//            }
//        }

    fun billingComplete() {
//        runCatching {
//            billingclient.endConnection()
//        }
    }

    fun queryShop(){
//        billingclient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                // 连接断开
//                showTemp("onBillingServiceDisconnected: 连接断开1 queryShop")
////                retryBillingServiceConnection(productId);
//                failBack.invoke("2")
//                billingComplete()
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                // 连接成功
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    showTemp("连接成功")
//                    val params =
//                        QueryPurchasesParams.newBuilder()
//                            .setProductType(BillingClient.ProductType.SUBS).build()
//
//                    //仅返回有效订阅和未消费的一次性购买。
//                    billingclient.queryPurchasesAsync(params,object :PurchasesResponseListener{
//                        override fun onQueryPurchasesResponse(p0: BillingResult, p1: MutableList<Purchase>) {
//                            showTemp("查询商品成功 dataList:${toJson(p1)}")
//                            var allount = 0
//                            var successCount = 0
//                            if (p1.isNullOrEmpty()){
//                                showTemp("queryShop 不是会员 无订阅数据1")
//                                CacheManager.isSubscribeMember = false
//                                billingComplete()
//                                failBack.invoke("-1")
//                            }else{
//                                for (i in 0 until p1.size){
//                                    var purchase = p1[i]
//                                    acknowledged(purchase, tag = "queryShop"){
//                                        allount++
//                                        if (it!=null){
//                                            successCount++
//                                            showTemp("queryShop 是会员")
//                                            successBack.invoke()
//                                            CacheManager.isSubscribeMember = true
//                                            AioADDataManager.clearAllAD()
//                                        }else{
//                                            if (allount == p1.size && successCount == 0){
//                                                failBack.invoke("-1")
//                                                showTemp("queryShop 不是会员 无有效订阅2")
//                                                CacheManager.isSubscribeMember = false
//                                            }
//                                        }
//                                        if (allount == p1.size){
//                                            billingComplete()
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    })
//                } else {
//                    // TODO 连接失败
//                    showTemp("onBillingServiceDisconnected: 连接断开2 queryShop")
//                    failBack.invoke("2")
//                    billingComplete()
//                }
//            }
//        })
    }

    fun subscribeShop(reference: WeakReference<BaseActivity<*>>, productId: String,fromType:Int = 0) {
//        PointEvent.posePoint(PointEventKey.subscribe_click, Bundle().apply {
//            putString("type",productId)
//            putString("from",if (fromType == 0) "subscribe_impression" else "subscribe_pop")
//        })
//        billingclient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                // 连接断开
//                showTemp("onBillingServiceDisconnected: 连接断开1 queryShop")
////                retryBillingServiceConnection(productId);
//                failBack.invoke("2")
//                billingComplete()
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                // 连接成功
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    showTemp("连接成功")
//                    //仅返回有效订阅和未消费的一次性购买。
//                    val params =
//                        QueryPurchasesParams.newBuilder()
//                            .setProductType(BillingClient.ProductType.SUBS).build()
//
//                    billingclient.queryPurchasesAsync(params,object :PurchasesResponseListener{
//                        override fun onQueryPurchasesResponse(p0: BillingResult, p1: MutableList<Purchase>) {
//                            showTemp("查询商品成功 dataList:${toJson(p1)}")
//                            var allount = 0
//                            var successCount = 0
//                            if (p1.isNullOrEmpty()){
//                                showTemp("queryShop 不是会员 开始订阅1")
//                                sub(reference,productId)
//                            }else{
//                                p1.forEach {
//                                    acknowledged(it, tag = "queryShop"){
//                                        allount++
//                                        if (it!=null){
//                                            successCount++
//                                            showTemp("queryShop 是会员")
//                                            successBack.invoke()
//                                            CacheManager.isSubscribeMember = true
//                                            AioADDataManager.clearAllAD()
//                                            PointEvent.posePoint(PointEventKey.payment_success)
//                                        }else{
//                                            if (allount == p1.size && successCount == 0){
//                                                showTemp("queryShop 不是会员 开始订阅2")
//                                                sub(reference,productId)
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    })
//
//                } else {
//                    failBack.invoke("2")
//                    // TODO 连接失败
//                    showTemp("onBillingServiceDisconnected: 连接断开2 subscribeShop")
//                    billingComplete()
//                }
//            }
//        })
    }

    fun getSubPrice(contentBack: (dataMap:HashMap<String,String>) -> Unit){
//        var map = HashMap<String,String>()
//        map.put("vip_weekly","RF 31245")
//        map.put("vip_monthly","RF 3124523123")
//        map.put("vip_quarterly","RF 124633")
//        contentBack.invoke(map)

//        billingclient.startConnection(object : BillingClientStateListener {
//            override fun onBillingServiceDisconnected() {
//                // 连接断开
//                showTemp("onBillingServiceDisconnected: 连接断开1 getSubPrice")
////                retryBillingServiceConnection(productId);
//                failBack.invoke("2")
//                billingComplete()
//            }
//
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                // 连接成功
//                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
//                    showTemp("连接成功")
//                    val queryProductDetailsParams =
//                        QueryProductDetailsParams.newBuilder()
//                            .setProductList(
//                                ImmutableList.of(
//                                    QueryProductDetailsParams.Product.newBuilder()
//                                        .setProductId("vip_weekly")
//                                        .setProductType(BillingClient.ProductType.SUBS)
//                                        .build(),
//                                    QueryProductDetailsParams.Product.newBuilder()
//                                        .setProductId("vip_monthly")
//                                        .setProductType(BillingClient.ProductType.SUBS)
//                                        .build()
////                                    QueryProductDetailsParams.Product.newBuilder()
////                                        .setProductId("vip_quarterly")
////                                        .setProductType(BillingClient.ProductType.SUBS)
////                                        .build()
//                                )
//                            )
//                            .build()
//                    //查询商品详情
//                    billingclient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
//                        // check billingResult
//                        // process returned productDetailsList
//                        showTemp("查询所有商品详情成功 dataList:${toJson(productDetailsList)}")
//                        var map = HashMap<String,String>()
//                        for (i in 0 until productDetailsList.size){
//                            var data = productDetailsList.get(i)
////                            var price = data.oneTimePurchaseOfferDetails?.formattedPrice
////                            showTemp("id:${data.productId} oneTimePurchaseOfferDetails?.formattedPrice:${price}")
//                            showTemp("id:${data.productId} subscriptionOfferDetails:${toJson(data.subscriptionOfferDetails)}")
//                            data.subscriptionOfferDetails?.forEach {
//                                if (it.pricingPhases.pricingPhaseList.size>0){
//                                    map.put(data.productId,it.pricingPhases.pricingPhaseList.get(0).formattedPrice)
//                                }
////                                showTemp("id:${data.productId} pricingPhases:${toJson(it.pricingPhases)}")
////                                it.pricingPhases.pricingPhaseList.forEachIndexed { index, pricingPhase ->
////                                    showTemp("id:${data.productId} pricingPhase:${toJson(pricingPhase)}")
////                                    showTemp("id:${data.productId} pricingPhases.price:${pricingPhase.formattedPrice}")
////                                }
//                            }
//                        }
//                        contentBack.invoke(map)
//                        billingComplete()
//                    }
//                } else {
//                    failBack.invoke("2")
//                    // TODO 连接失败
//                    showTemp("onBillingServiceDisconnected: 连接断开2 getSubPrice")
//                    billingComplete()
//                }
//            }
//        })
    }


    private fun sub(reference: WeakReference<BaseActivity<*>>, productId: String){

//        val queryProductDetailsParams =
//            QueryProductDetailsParams.newBuilder()
//                .setProductList(
//                    ImmutableList.of(
//                        QueryProductDetailsParams.Product.newBuilder()
//                            .setProductId(productId)
//                            .setProductType(BillingClient.ProductType.SUBS)
//                            .build()
//                    )
//                )
//                .build()
//        //查询商品详情
//        billingclient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
//            // check billingResult
//            // process returned productDetailsList
//            showTemp("查询商品详情成功 dataList:${toJson(productDetailsList)}")
//            var tempDetail:ProductDetails?=null
//            for (i in 0 until productDetailsList.size){
//                var data = productDetailsList.get(i)
//                if (productId == data.productId){
//                    //找到商品
//                    tempDetail = data
//                    break
//                }
//            }
//            if (tempDetail == null){
//                billingComplete()
//                failBack.invoke("4")
//            }
//            tempDetail?.apply {
//                showTemp("找到对应商品")
//                runCatching {
//                    showTemp("启动购买1",false)
//                    val productDetailsParamsList = listOf(
//                        BillingFlowParams.ProductDetailsParams.newBuilder()
//                            // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
//                            .setProductDetails(tempDetail)
//                            // For One-time products, "setOfferToken" method shouldn't be called.
//                            // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
//                            // for a list of offers that are available to the user
//                            .setOfferToken(subscriptionOfferDetails?.get(0)?.offerToken ?:"")
//                            .build()
//                    )
//                    showTemp("启动购买2",false)
//                    val billingFlowParams = BillingFlowParams.newBuilder()
//                        .setProductDetailsParamsList(productDetailsParamsList)
//                        .build()
//                    var activity = reference.get()
//                    showTemp("启动购买3",false)
//                    // Launch the billing flow
//                    val billingResult = billingclient.launchBillingFlow(activity!!, billingFlowParams)
//                    showTemp("启动购买4",false)
//                }.onFailure {
//                    failBack.invoke("0")
//                    billingComplete()
//                    showTemp("error 对应商品 ${it.stackTraceToString()}")
//                }
//            }
//        }
    }

    private fun showTemp(content: String,showToast:Boolean = true) {
        AppLogs.dLog(TAG, content)
        if (showToast){
            ToastUtils.showLong(content)
        }
        Logger.writeLog(APP.instance,"${TimeManager.getADTime()}: ${content}")
    }

//
//    /**
//     * 确认购买
//     */
//    fun acknowledged(purchase: Purchase,tag:String,callBack: (purchase:Purchase?) -> Unit) {
//        showTemp("acknowledged tag:${tag} purchaseState:${purchase.purchaseState} isAcknowledged:${purchase.isAcknowledged}",false)
//        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
//            if (!purchase.isAcknowledged) {
//                var acknowledgePurchaseParams =
//                AcknowledgePurchaseParams.newBuilder()
//                    .setPurchaseToken(purchase.getPurchaseToken())
//                    .build();
//                billingclient.acknowledgePurchase(acknowledgePurchaseParams) {
//                    showTemp("确认购买 code:${it.responseCode} msg:${it.debugMessage}")
//                    if (it.responseCode == BillingClient.BillingResponseCode.OK){
//                        callBack.invoke(purchase)
//                    }else{
//                        callBack.invoke(null)
//                    }
//                }
//            }else{
//                showTemp("确认购买 当前已确认过")
//                callBack.invoke(purchase)
//            }
//        }else{
//            showTemp("确认购买 状态错误")
//            callBack.invoke(null)
//        }
//    }

//    fun showNF(reference:WeakReference<BaseActivity<*>>) {
//        var activity = reference.get()
//        if (activity!=null && activity.isFinishing.not() && activity.isDestroyed.not()){
//            val inAppMessageParams = InAppMessageParams.newBuilder()
//                .addInAppMessageCategoryToShow(InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
//                .build()
//
//            billingclient.showInAppMessages(activity,
//                inAppMessageParams,
//                object : InAppMessageResponseListener {
//                    override fun onInAppMessageResponse(inAppMessageResult: InAppMessageResult) {
//                        if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
//                            // The flow has finished and there is no action needed from developers.
//                        } else if (inAppMessageResult.responseCode == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED) {
//                            // The subscription status changed. For example, a subscription
//                            // has been recovered from a suspend state. Developers should
//                            // expect the purchase token to be returned with this response
//                            // code and use the purchase token with the Google Play
//                            // Developer API.
//                        }
//                    }
//                })
//        }
//    }



}