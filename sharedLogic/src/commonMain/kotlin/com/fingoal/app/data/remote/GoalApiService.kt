package com.fingoal.app.data.remote

import com.fingoal.app.data.remote.ApiConstants.BASE_URL
import com.fingoal.app.data.remote.dto.ContributionRequest
import com.fingoal.app.data.remote.dto.GoalDto
import com.fingoal.app.data.remote.dto.GoalRequest
import com.fingoal.app.data.remote.dto.UpdateGoalRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GoalApiService(private val client: HttpClient) {

    suspend fun getGoals(userId: String): List<GoalDto> {
        return client.get("$BASE_URL/api/goals/$userId").body()
    }

    suspend fun createGoal(request: GoalRequest): GoalDto {
        return client.post("$BASE_URL/api/goals") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun addContribution(request: ContributionRequest): Map<String, String> {
        return client.post("$BASE_URL/api/goals/contribution") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun updateGoal(goalId: String, request: UpdateGoalRequest): GoalDto {
        return client.patch("$BASE_URL/api/goals/$goalId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun deleteGoal(goalId: String): Map<String, String> {
        return client.delete("$BASE_URL/api/goals/$goalId").body()
    }

    suspend fun withdraw(request: ContributionRequest): Map<String, String> {
        return client.post("$BASE_URL/api/goals/withdraw") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}