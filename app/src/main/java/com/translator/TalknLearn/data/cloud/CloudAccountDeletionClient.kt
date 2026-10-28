package com.translator.TalknLearn.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudAccountDeletionClient @Inject constructor(
    private val functions: FirebaseFunctions
) {
    private companion object {
        const val TIMEOUT_SECONDS = 120L
    }

    suspend fun deleteAccountAndData(): Result<Unit> = try {
        functions
            .getHttpsCallable("deleteAccountAndData")
            .withTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .call(emptyMap<String, Any>())
            .await()
        Result.success(Unit)
    } catch (e: FirebaseFunctionsException) {
        Result.failure(IllegalStateException(e.message ?: "Account deletion failed.", e))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
