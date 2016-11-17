package de.swp.status

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.Responsive
import com.vaadin.server.ThemeResource
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import de.swp.model.MonitorId
import de.swp.model.MonitorObject
import de.swp.model.Status
import de.swp.services.MonitorService
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy


@SpringView(name = StatusView.VIEW_NAME)
class StatusView constructor(val monitorService: MonitorService, val ui: UI, val executorService: ScheduledExecutorService) : Panel(), View {


    var backgroundTask: ScheduledFuture<*>? = null
    var statusLayout = CssLayout()

    var previousBuildStatus = ArrayList<MonitorObject>()

    val alert : Audio

    companion object {
        const val VIEW_NAME = "status"

    }

    init {
        Responsive.makeResponsive(this, statusLayout)
        alert = Audio("", ThemeResource("sounds/tos-redalert.mp3")).apply {
            id = "alertAudio"
            isShowControls = false
        }
        statusLayout.apply {
            id = "statusView"
        }

        content = VerticalLayout(statusLayout,alert)
        setSizeFull()

    }


    override fun enter(event: ViewChangeListener.ViewChangeEvent?) {
        val monitorIds = event!!.parameters.split("/").map { MonitorId.fromCompleteId(it)}

        val monitorObjects = monitorService.retrieve(monitorIds)
        showBuildConfigStatus(monitorObjects)

        backgroundTask = executorService.scheduleAtFixedRate({ showBuildConfigForPush(monitorService.retrieve(monitorIds)) }, 15, 15, TimeUnit.SECONDS)
    }

    @PreDestroy
    private fun shutdown() {
        backgroundTask?.cancel(true)
    }

    private fun showBuildConfigStatus(monitorObjects: List<MonitorObject>) {
        statusLayout.removeAllComponents()
        var playAlert = false;
        monitorObjects.forEach { monitorObject ->
            val buildStatusUI = Label(monitorObject.name).apply {
                setWidthUndefined()
                styleName = "buildStatus"
                when (monitorObject.status) {
                    Status.SUCCESS -> addStyleName("success")
                    else -> {
                        addStyleName("failure")
                        playAlert = playAlert.xor(alertWithSoundIfNecessary(monitorObject)).or(playAlert)
                    }
                }
            }

            val buildStatusContainer = VerticalLayout(buildStatusUI).apply {
                setSizeUndefined()
                styleName = "buildStatusContainer"
            }
            if (playAlert) {
                alert.play()
            }
            statusLayout.addComponent(buildStatusContainer)
        }
        previousBuildStatus.clear()
        previousBuildStatus.addAll(monitorObjects)
    }

    private fun alertWithSoundIfNecessary(monitorObject: MonitorObject) : Boolean {
        val previousMonitorObject = previousBuildStatus.filter { it.name.equals(monitorObject.name) }.firstOrNull()
        if (previousMonitorObject == null) {
            return true
        }
        if (previousMonitorObject.status != monitorObject.status) {
            return true
        }
        return false
    }

    private fun showBuildConfigForPush(buildConfigStatus: List<MonitorObject>) {
        ui.access({ showBuildConfigStatus(buildConfigStatus) })
    }
}