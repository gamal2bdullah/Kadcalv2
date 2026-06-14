package com.example.domain.usecase

import com.example.data.LoadEntity
import com.example.domain.ValidationRules
import com.example.domain.ValidationRules.RuleIssue

class ValidateAllLoadsUseCase {
    operator fun invoke(loads: List<LoadEntity>): List<RuleIssue> {
        return ValidationRules.validateAllLoads(loads)
    }
}
