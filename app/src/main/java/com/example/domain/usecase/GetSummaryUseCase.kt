package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.domain.Calculations
import com.example.core.result.SolarResult
import com.example.core.result.SolarError
import com.example.core.logging.SolarLogger

class GetSummaryUseCase {
    operator fun invoke(loads: List<LoadEntity>): SolarResult<Calculations.Summary> {
        return try {
            val summary = Calculations.computeSummary(loads)
            SolarResult.Success(summary)
        } catch (e: Exception) {
            SolarLogger.e("GetSummaryUseCase", "Failed to compute load summary", e)
            SolarResult.Failure(
                SolarError.CalculationError(
                    message = e.localizedMessage ?: "Unknown load summary calculation error",
                    formulaName = "computeSummary"
                )
            )
        }
    }
}
