package de.swp.services

import com.vaadin.spring.annotation.SpringComponent
import de.swp.model.MonitorId
import de.swp.model.MonitorObject
import java.util.*


@SpringComponent
class MonitorService constructor(val monitorDataServices : List<MonitorDataService>) {

    fun retrieve(monitorIds : List<MonitorId>) : List<MonitorObject> {
        val idMap = monitorIds.asSequence().groupBy { it.id }
        return idMap.keys.flatMap { key ->
            val curMonitorIds = idMap[key].orEmpty()
            val monitorDataService = findByMonitorId(curMonitorIds.first())
            monitorDataService.map { it.retrieve(curMonitorIds) }.orElse(ArrayList<MonitorObject>())
        }
    }

    //could be saved in map at postconstruct to avoid iteration every time
    private fun findByMonitorId(monitorId: MonitorId) : Optional<MonitorDataService> =
            Optional.ofNullable(monitorDataServices.firstOrNull { monitorDataService -> monitorDataService.isResponsibleFor(monitorId) })


}