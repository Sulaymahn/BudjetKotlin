package com.unghostdude.budjet.utilities

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.regex.Pattern

class FormGroup(
    private val controls: Collection<FormControl>
) {
    init {
        for (control in controls) {
            control.updateCallback = {
                isValid = check()
            }
        }
    }

    var isValid by mutableStateOf(check())

    private fun check(): Boolean {
        var valid = true
        for (control in controls) {
            if (!control.isValid) {
                valid = false
                break
            }
        }

        return valid
    }
}

class FormControl(
    private val initialValue: String = "",
    private val validateOnInitializing: Boolean = false,
    private val validators: Collection<FormValidator> = listOf()
) {
    var updateCallback: (newValue: Boolean) -> Unit = {}
    var errors by mutableStateOf(listOf<String>())
    var currentValue by mutableStateOf(initialValue)
    var isValid by mutableStateOf(
        if(validateOnInitializing) checkValidity() else true
    )



    private fun checkValidity(): Boolean {
        val errs = mutableListOf<String>()
        validators.forEach { validator ->
            val res = validator.isValid(currentValue)
            if (!res.valid) {
                errs.add(res.errorMessage)
            }
        }
        errors = errs
        return errors.isEmpty()
    }

    fun setValue(newValue: String) {
        currentValue = newValue
        isValid = checkValidity()
        updateCallback(isValid)
    }
}

abstract class FormValidator(val errorMessage: String = "") {
    abstract fun isValid(value: String): ValidationResult
}

data class ValidationResult(
    val valid: Boolean,
    val errorMessage: String
)

sealed class Validators {
    class Required(errorMessage: String = "Field is required") : FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            return if (value.isNotEmpty()) ValidationResult(true, "") else ValidationResult(
                false,
                errorMessage
            )
        }
    }

    class MinLength(
        private val min: Int,
        errorMessage: String = "Field length must be at least $min"
    ) : FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            return if (value.length >= min) ValidationResult(true, "") else ValidationResult(
                false,
                errorMessage
            )
        }
    }

    class MaxLength(private val max: Int, errorMessage: String = "Must be less than $max characters") : FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            return if (value.length < max) ValidationResult(true, "") else ValidationResult(
                false,
                errorMessage
            )
        }
    }

    class In(private val range: List<String>, errorMessage: String = "") :
        FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            return if (range.contains(value)) ValidationResult(
                true,
                ""
            ) else ValidationResult(false, errorMessage)
        }
    }

    class Letters(errorMessage: String = "Only letters are allowed") :
        FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            val regex =
                Pattern.compile("^\\p{L}+\$", Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)
            return if (regex.matcher(value).matches()) ValidationResult(
                true,
                ""
            ) else ValidationResult(false, errorMessage)
        }
    }

    class LettersAndSpace(errorMessage: String = "Only letters and space are allowed") :
        FormValidator(errorMessage) {
        override fun isValid(value: String): ValidationResult {
            val regex =
                Pattern.compile("^\\p{L}+\$", Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE)
            return if (regex.matcher(value).matches()) ValidationResult(
                true,
                ""
            ) else ValidationResult(false, errorMessage)
        }
    }
}