package de.swp.status

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.CssLayout
import com.vaadin.ui.Label
import com.vaadin.ui.Panel
import com.vaadin.ui.UI
import de.swp.services.BuildStatus
import de.swp.services.TCService
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy


@SpringView(name = StatusView.VIEW_NAME)
class StatusView constructor(val tcService: TCService, val ui: UI, val executorService: ScheduledExecutorService) : Panel(), View {


    var backgroundTask: ScheduledFuture<*>? = null
    var layout = CssLayout()

    companion object {
        const val VIEW_NAME = "status"

    }

    init {
        layout.id = "statusView"
        content = layout
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
        layout.removeAllComponents()
        buildConfigStatus.forEach { buildStatus ->
            val buildStatusUI = Label(buildStatus.buildConfiguration.name).apply {
                setWidthUndefined()
                styleName = "buildStatus"
                when (buildStatus.latestBuild?.status) {
                    org.jetbrains.teamcity.rest.BuildStatus.SUCCESS -> addStyleName("success")
                    else -> addStyleName("failure")
                }
            }

            layout.addComponent(buildStatusUI)
        }
    }

    private fun showBuildConfigForPush(buildConfigStatus: List<BuildStatus>) {
        ui.access({ showBuildConfigStatus(buildConfigStatus) })
    }
}