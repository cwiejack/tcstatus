package de.swp

import de.swp.model.MonitorId
import de.swp.model.MonitorObject
import de.swp.model.Status
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic

data class MonitorObjectBuilder(val systemId : String = randomAlphabetic(4), val id : String = randomAlphabetic(10), val name : String = randomAlphabetic(10), val status : Status = Status.SUCCESS) {
    fun build() = MonitorObject(MonitorId(systemId,id),name,status)
}

data class MonitorIdBuilder(val systemId: String = randomAlphabetic(4), val id : String = randomAlphabetic(10)) {
    fun build() = MonitorId(systemId, id)
}


