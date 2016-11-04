package de.swp.status

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.Responsive
import com.vaadin.server.ThemeResource
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import de.swp.services.BuildStatus
import de.swp.services.TCService
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy


@SpringView(name = StatusView.VIEW_NAME)
class StatusView constructor(val tcService: TCService, val ui: UI, val executorService: ScheduledExecutorService) : Panel(), View {


    var backgroundTask: ScheduledFuture<*>? = null
    var statusLayout = CssLayout()

    var previousBuildStatus = ArrayList<BuildStatus>()
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
        val buildConfigIds = event!!.parameters.split("/")

        val buildConfigStatus = tcService.retrieveBuildConfigurations(buildConfigIds)
        showBuildConfigStatus(buildConfigStatus)

        backgroundTask = executorService.scheduleAtFixedRate({ showBuildConfigForPush(tcService.retrieveBuildConfigurations(buildConfigIds)) }, 15, 15, TimeUnit.SECONDS)
    }

    @PreDestroy
    private fun shutdown() {
        backgroundTask?.cancel(true)
    }

    private fun showBuildConfigStatus(buildConfigStatus: List<BuildStatus>) {
        statusLayout.removeAllComponents()
        var playAlert = false;
        buildConfigStatus.forEach { buildStatus ->
            val buildStatusUI = Label(buildStatus.buildConfiguration.name).apply {
                setWidthUndefined()
                styleName = "buildStatus"
                when (buildStatus.latestBuild?.status) {
                    org.jetbrains.teamcity.rest.BuildStatus.SUCCESS -> addStyleName("success")
                    else -> {
                        addStyleName("failure")
                        playAlert = playAlert.xor(alertWithSoundIfNecessary(buildStatus)).or(playAlert)
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
        previousBuildStatus.addAll(buildConfigStatus)
    }

    private fun alertWithSoundIfNecessary(buildStatus: BuildStatus) : Boolean {
        if (tcService.playSound().not()) {
            return false
        }

        val prevStatus = previousBuildStatus.filter { it.buildConfiguration.name.equals(buildStatus.buildConfiguration.name) }.firstOrNull()
        if (prevStatus == null) {
            return true
        }
        if (prevStatus?.latestBuild?.status != buildStatus.latestBuild?.status) {
            return true
        }
        return false
    }

    private fun showBuildConfigForPush(buildConfigStatus: List<BuildStatus>) {
        ui.access({ showBuildConfigStatus(buildConfigStatus) })
    }
}