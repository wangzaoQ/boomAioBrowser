package com.boom.aiobrowser.data

/*

- 控制参数
pnvdskmb：广告展示频次控制参数，用户单日内观看广告频次超过该上限后，不再展示广告
qwmkszbx：广告点击频次控制参数，单日内观看广告频次超过该上限后，不再展示广告
- 广告单元内参数
ktygzdzn：广告单元ID
tybxumpn：广告单元对应的广告平台【admob、max、topon、tradplus】
pxdtzgho：广告类型【op为开屏类型，int为插屏类型，nat为原生类型】
swpuzhhv：广告库存过期时间，单位秒【建议开屏类型设置为13800，其他类型设为3000】
npxotusg：广告层级权重，数值越大优先级越高
*/

data class AioADData(
    val pnvdskmb: Int,
    val qwmkszbx: Int,
    val aobws_launch: MutableList<AioRequestData>?=null,
    val aobws_main_one: MutableList<AioRequestData>?=null,
    val aobws_detail_bnat: MutableList<AioRequestData>?=null,
    val aobws_download_bnat: MutableList<AioRequestData>?=null
)

data class AioRequestData(
    val ktygzdzn: String,
    val tybxumpn: String,
    val pxdtzgho: String,
    val swpuzhhv: Int,
    val npxotusg: Int
)


