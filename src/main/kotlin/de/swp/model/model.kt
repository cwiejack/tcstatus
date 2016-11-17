package de.swp.model

enum class Status {
    SUCCESS,
    FAILURE
}

data class MonitorId(val systemId : String, val id: String) {

    internal companion object {
        val SEPARATOR = "!!"

        fun fromCompleteId(completeId : String) : MonitorId = MonitorId(completeId.substringBefore(SEPARATOR),completeId.substringAfter(SEPARATOR))
    }


    fun asCompleteId(): String = systemId.plus(SEPARATOR).plus(id)
}



data class MonitorObject(val id: MonitorId, val name : String, val status: Status)


