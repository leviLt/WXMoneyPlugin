package com.wechatplugin.scropio.wxmoneyplugin.http.net_coroutines.response

import java.io.Serializable

/**
 * 实际业务返回的固定字段, 根据需求来定义，
 */
open class ResultResponse<T> constructor() : Serializable {
    var code = 0
    var message: String? = null
    var data: T? = null
    var status: Status? = null

    constructor(
        code: Int = 0,
        message: String? = "",
        data: T? = null,
        status: Status? = null
    ) : this() {
        this.code = code
        this.message = message
        this.data = data
        this.status = status
    }

    /** 对结果先预处理 */
    fun parseResult() {
//        when (code) {
//            200 -> {
//                status = Status.SUCCESS
//            }
//            500 -> {
//                status = Status.ERROR
//                message = "请求失败"
//            }
//            404 -> {
//                status = Status.ERROR
//                message = "访问资源不存在"
//            }
//            22 -> {
//                status = Status.ERROR
//                message = "token异常，退出登录后重进"
//            }
//            23 -> {
//                status = Status.ERROR
//                message = "账号被禁用或不存在"
//            }
//            else -> {
//                status = Status.ERROR
//            }
//        }
        //判断列表是否为空
        if (status == Status.SUCCESS && data != null) {
            val cast = data as Any
            //判断是否是列表
            if (List::class.java.isAssignableFrom(cast::class.java)) {
                if ((cast as List<*>).isEmpty()) {
                    status = Status.EMPTY
                }
            }
        }
    }

    override fun toString(): String {
        return "BaseResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", status=" + status
        '}'
    }

    fun isSuccessful(): Boolean {
        return status == Status.SUCCESS
    }

    fun isEmpty(): Boolean {
        return status == null || status == Status.EMPTY
    }

    fun isError(): Boolean {
        return status == Status.ERROR
    }


    fun isLastPage(pageSize: Int): Boolean {
        return pageSize > (data as? List<*>)?.size ?: 0
    }
}