package com.boom.aiobrowser.data

/**
 * "area": "1111934878093335", // asilve 注：空字符串是合法的结果，表示没有找到合理的位置
 *         "name": "L", // nsand
 *         "admin": { // acoat  州、省等上级行政区划
 *             "area": "2111934878093335", // asilve
 *             "name": "X" // nsand
 *         }
 */
class AreaData {
    var asilve:String=""
    var nsand:String=""
    var acoat:AreaDataChild ?= null
}

class AreaDataChild{
    var asilve = ""
    var nsand = ""
}