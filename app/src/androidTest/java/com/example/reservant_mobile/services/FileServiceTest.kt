package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.services.FileService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FileServiceTest: ServiceTest() {
    private val service: FileService = FileService()

    @Before
    fun setup() = runBlocking {
        loginUser()
    }

    @Test
    fun getRestaurantLogoTest() = runTest {
        service.getFile("/uploads/test-jd.png")
    }

}