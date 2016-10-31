package de.swp.services

import com.vaadin.spring.annotation.SpringComponent
import de.swp.model.TCServerData
import org.jetbrains.teamcity.rest.*

data class ProjectBuildConfigurations(val project: Project, val buildConfigurations : List<BuildConfiguration>)

data class BuildStatus(val buildConfiguration: BuildConfiguration, val latestBuild: Build?)

@SpringComponent
class TCService constructor(val tcServerData: TCServerData){

    private fun retreiveAllProjects() : List<Project> {
        val projects = createTeamcityInstance().rootProject().fetchChildProjects()
        return projects
    }

    fun retrieveAllConfigurations() : List<ProjectBuildConfigurations> {
        val allProjects = retreiveAllProjects()
        val projectBuildConfigurations = allProjects.map { p ->
            val buildConfigurations = createTeamcityInstance().project(p.id).fetchBuildConfigurations()
            ProjectBuildConfigurations(p, buildConfigurations)
        }
        return projectBuildConfigurations
    }

    fun retrieveBuildConfigurations(buildConfigIds: List<String>) : List<BuildStatus> {
        val resultList = buildConfigIds.map { buildConfigId ->
            val buildConfiguration = createTeamcityInstance().buildConfiguration(BuildConfigurationId(buildConfigId))
            val latestBuild = createTeamcityInstance().builds().fromConfiguration(buildConfiguration.id).withAnyStatus().latest()
            BuildStatus(buildConfiguration, latestBuild)
        }
        return resultList
    }


    private fun createTeamcityInstance() = TeamCityInstance.httpAuth(tcServerData.serverUrl.toString(), tcServerData.userName!!, tcServerData.password!!)


}