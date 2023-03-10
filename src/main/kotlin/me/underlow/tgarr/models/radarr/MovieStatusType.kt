/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package me.underlow.tgarr.models.radarr


/**
 *
 *
 * Values: tba,announced,inCinemas,released,deleted
 */

enum class MovieStatusType(val value: kotlin.String) {

    tba("tba"),

    announced("announced"),

    inCinemas("inCinemas"),

    released("released"),

    deleted("deleted");

    /**
     * Override toString() to avoid using the enum variable name as the value, and instead use
     * the actual value defined in the API spec file.
     *
     * This solves a problem when the variable name and its value are different, and ensures that
     * the client sends the correct enum values to the server always.
     */
    override fun toString(): String = value

    companion object {
        /**
         * Converts the provided [data] to a [String] on success, null otherwise.
         */
        fun encode(data: kotlin.Any?): kotlin.String? = if (data is MovieStatusType) "$data" else null

        /**
         * Returns a valid [MovieStatusType] for [data], null otherwise.
         */
        fun decode(data: kotlin.Any?): MovieStatusType? = data?.let {
          val normalizedData = "$it".lowercase()
          values().firstOrNull { value ->
            it == value || normalizedData == "$value".lowercase()
          }
        }
    }
}

