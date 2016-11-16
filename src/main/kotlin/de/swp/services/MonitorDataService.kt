package de.swp.services

import de.swp.model.MonitorId
import de.swp.model.MonitorObject


interface MonitorDataService {

    fun isResponsibleFor(monitorId: MonitorId): Boolean
    fun  retrieve(monitorIds: List<MonitorId>): List<MonitorObject>

}