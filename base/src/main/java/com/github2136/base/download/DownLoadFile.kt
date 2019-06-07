package com.github2136.base.download

/**
 * Created by YB on 2019/6/6
 */

data class DownLoadFile(
    var id: Long,//主键
    var fileUrl: String,//文件下载路径
    var filePath: String,//文件本地路径
    var fileSize: Long,//已下载文件大小
    var fileTotal: Long,//文件大小
    var complete: Boolean//下载以完成
)