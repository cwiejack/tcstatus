package de.swp.services

import com.vaadin.spring.annotation.SpringComponent
import de.swp.model.MonitorId
import de.swp.model.MonitorObject
import de.swp.model.Status
import de.swp.model.TCServerData
import org.jetbrains.teamcity.rest.*

data class ProjectBuildConfigurations(val project: Project, val buildConfigurations : List<BuildConfiguration>)


@SpringComponent
class TCDataService constructor(val tcServerData: TCServerData) : MonitorDataService {

    private val SYSTEM_ID = "TC"
    private val NAME = "Teamcity"

    override fun getName(): String = NAME

    override fun getSystemId(): String = SYSTEM_ID

    override fun isResponsibleFor(monitorId: MonitorId): Boolean = SYSTEM_ID.contentEquals(monitorId.systemId)

    //abstaction ends here, currently ui needs to know about this method and return values
    fun allAvailableBuildConfigurations(): List<ProjectBuildConfigurations> {
        val allProjects = retreiveAllProjects()
        val projectBuildConfigurations = allProjects.map { p ->
            val buildConfigurations = createTeamcityInstance().project(p.id).fetchBuildConfigurations()
            ProjectBuildConfigurations(p, buildConfigurations)
        }
        return projectBuildConfigurations
    }

    override fun retrieve(monitorIds: List<MonitorId>): List<MonitorObject> {
        val resultList = monitorIds.sortedBy { it.id }.map { monitorId ->
            val buildConfiguration = createTeamcityInstance().buildConfiguration(BuildConfigurationId(monitorId.id))
            val latestBuild = createTeamcityInstance().builds().fromConfiguration(buildConfiguration.id).withAnyStatus().latest()
            MonitorObject(monitorId, buildConfiguration.name, toStatus(latestBuild))
        }
        return resultList
    }

    private fun  toStatus(build: Build?): Status {
        if (build == null) {
            return Status.FAILURE
        }
        return when(build.status) {
            BuildStatus.SUCCESS -> Status.SUCCESS
            else -> Status.FAILURE
        }
    }

    private fun createTeamcityInstance() = TeamCityInstance.httpAuth(tcServerData.serverUrl.toString(), tcServerData.userName!!, tcServerData.password!!)

    private fun retreiveAllProjects() : List<Project> {
        val projects = createTeamcityInstance().rootProject().fetchChildProjects()
        return projects
    }

    fun playSound(): Boolean = tcServerData.playSound

    fun toMonitorId(buildConfig : BuildConfiguration) : MonitorId = MonitorId(SYSTEM_ID, buildConfig.id.stringId)
}
