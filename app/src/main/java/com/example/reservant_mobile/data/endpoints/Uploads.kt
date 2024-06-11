package com.example.reservant_mobile.data.endpoints

import io.ktor.resources.Resource

@Resource("/uploads")
class Uploads(){
    @Resource("{fileName}")
    class FileName(val parent: Uploads = Uploads(), val fileName: String)
}