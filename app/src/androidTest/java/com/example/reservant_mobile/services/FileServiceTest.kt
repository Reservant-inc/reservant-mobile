package com.example.reservant_mobile.services

import com.example.reservant_mobile.data.services.FileService
import com.google.common.truth.Truth.assertThat
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

    @Test
    fun getImageTest() = runTest {
        val res = service.getImage("/uploads/test-jd.png")
        assertThat(res.value).isNotNull()
    }

}