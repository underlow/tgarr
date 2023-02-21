package me.underlow.tgarr.clients


sealed interface ActionResult{
    val message: String
}

data class Success(override val message: String) : ActionResult
data class Error(override val message: String) : ActionResult
