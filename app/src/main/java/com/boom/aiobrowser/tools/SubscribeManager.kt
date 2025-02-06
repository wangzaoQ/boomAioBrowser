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
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
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
//            .enablePrepaidPlans()
        BillingClient.newBuilder(APP.instance).enablePendingPurchases(builder.build())
            .setListener(purchasesUpdatedListener).build()
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, list ->
            showTemp("启动购买回调 debugMessage:${billingResult.debugMessage}")
            if (list != null && list.size > 0) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    for (purchase in list) {
                        showTemp("验证购买状态 :${purchase.purchaseState}")
                        if (purchase == null || purchase.purchaseState != Purchase.PurchaseState.PURCHASED) {
                            continue
                        }

                        //通知服务器支付成功，服务端验证后，消费商品
//                        VerifyProduct(purchase)
                        //TODO客户端同步回调支付成功
                        acknowledged(purchase);  //非消耗性商品 确认交易
//                        consumePurchase(purchase) //消费商品  消费商品后才能进行下一次购买
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

    fun subscribeShop(reference:WeakReference<BaseActivity<*>>,productId: String) {
        billingclient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // 连接断开
                showTemp("onBillingServiceDisconnected: 连接断开1")
//                retryBillingServiceConnection(productId);
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                // 连接成功
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

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
                    //查询商品
                    billingclient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                        // check billingResult
                        // process returned productDetailsList
                        showTemp("查询商品成功 dataList:${toJson(productDetailsList)}")
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
                            val productDetailsParamsList = listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                    .setProductDetails(tempDetail)
                                    // For One-time products, "setOfferToken" method shouldn't be called.
                                    // For subscriptions, to get an offer token, call ProductDetails.subscriptionOfferDetails()
                                    // for a list of offers that are available to the user
//                                    .setOfferToken(selectedOfferToken)
                                    .build()
                            )
                            val billingFlowParams = BillingFlowParams.newBuilder()
                                .setProductDetailsParamsList(productDetailsParamsList)
                                .build()
                            var activity = reference.get()
                            if (activity!=null && activity.isFinishing.not() && activity.isDestroyed.not()){
                                showTemp("启动购买")
                                // Launch the billing flow
                                val billingResult = billingclient.launchBillingFlow(activity, billingFlowParams)
                            }
                        }
                    }
                } else {
                    // TODO 连接失败
                    showTemp("onBillingServiceDisconnected: 连接断开2")
                }
            }
        });
    }

    private fun showTemp(content: String) {
        AppLogs.dLog(TAG, content)
        ToastUtils.showLong(content)
        Logger.writeLog(APP.instance,content)
    }


    /**
     * 非消耗品 确认购买
     */
    fun acknowledged(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams =
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                billingclient.acknowledgePurchase(acknowledgePurchaseParams,
                    AcknowledgePurchaseResponseListener { })
            }
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


}