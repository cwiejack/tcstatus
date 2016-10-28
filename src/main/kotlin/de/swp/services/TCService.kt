package de.swp.services

import com.vaadin.spring.annotation.SpringComponent
import de.swp.model.TCServerData
import org.jetbrains.teamcity.rest.BuildConfiguration
import org.jetbrains.teamcity.rest.Project
import org.jetbrains.teamcity.rest.TeamCityInstance

data class ProjectBuildConfigurations(val project: Project, val buildConfigurations : List<BuildConfiguration>)

@SpringComponent
class TCService constructor(val tcServerData: TCServerData){

    private fun retreiveAllProjects() : List<Project> {
        val projects = createTeamcityInstance().rootProject().fetchChildProjects()
        return projects
    }

    fun retreiveAllConfigurations() : List<ProjectBuildConfigurations> {
        val allProjects = retreiveAllProjects()
        val projectBuildConfigurations = allProjects.map { p ->
            val buildConfigurations = createTeamcityInstance().project(p.id).fetchBuildConfigurations()
            ProjectBuildConfigurations(p, buildConfigurations)
        }
        return projectBuildConfigurations
    }



    private fun createTeamcityInstance() = TeamCityInstance.httpAuth(tcServerData.serverUrl.toString(), tcServerData.userName!!, tcServerData.password!!)


}