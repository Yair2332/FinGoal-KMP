package com.fingoal.app.data.remote

import com.fingoal.app.data.remote.ApiConstants.BASE_URL
import com.fingoal.app.data.remote.dto.HabitDto
import com.fingoal.app.data.remote.dto.UpdateHabitRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HabitApiService(private val client: HttpClient) {

    suspend fun createHabit(habit: HabitDto): HabitDto {
        return client.post("$BASE_URL/api/habits") {
            contentType(ContentType.Application.Json)
            setBody(habit)
        }.body()
    }

    suspend fun getHabits(userId: String, todayStr: String): List<HabitDto> {
        return client.get("$BASE_URL/api/habits/$userId") {
            parameter("todayStr", todayStr)
        }.body()
    }

    suspend fun toggleHabit(habitId: String, body: Map<String, String>): Map<String, Any> {
        return client.patch("$BASE_URL/api/habits/$habitId/toggle") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun updateHabit(habitId: String, request: UpdateHabitRequest): Map<String, String> {
        return client.patch("$BASE_URL/api/habits/$habitId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteHabit(habitId: String): Map<String, String> {
        return client.delete("$BASE_URL/api/habits/$habitId").body()
    }
}