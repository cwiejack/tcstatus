package de.swp.admin

import com.vaadin.data.fieldgroup.BeanFieldGroup
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory
import com.vaadin.data.util.converter.Converter
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.*
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import de.swp.model.TCServerData
import de.swp.services.TCService
import org.jetbrains.teamcity.rest.BuildConfiguration
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@SpringView(name = "")
class AdminView constructor(val tcServerData: TCServerData, val tcService: TCService) : VerticalLayout(), View {

    val accordion: Accordion

    val selectedConfigurations = ArrayList<BuildConfiguration>()

    init {
        id = "adminView"
        accordion = Accordion()
        setSizeFull()
        Responsive.makeResponsive(this)
    }

    override fun enter(event: ViewChangeListener.ViewChangeEvent?) {
        val layout = FormLayout().apply {
            id = "serverDataForm"
        }
        val binder = BeanFieldGroup<TCServerData>(TCServerData::class.java)
        binder.setItemDataSource(tcServerData)
        binder.fieldFactory = CustomFieldFactory(DefaultFieldGroupFieldFactory.get())

        val textField = TextField("Server URL").apply {
            setConverter(URLConverter())
            icon = FontAwesome.SERVER
            nullRepresentation = ""
            setSizeFull()
        }
        binder.bind(textField, "serverUrl")

        val userNameField = binder.buildAndBind("Benutzer", "userName") as TextField
        userNameField.apply {
            icon = FontAwesome.USER
            nullRepresentation = ""
            setSizeFull()
        }

        val passwortField = binder.buildAndBind("Passwort", "password") as TextField
        passwortField.apply {
            icon = FontAwesome.USER_SECRET
            nullRepresentation = ""
            setSizeFull()
        }

        val playSound = binder.buildAndBind("Sound", "playSound").apply {
            icon = FontAwesome.MUSIC
            setSizeFull()
        }

        layout.addComponent(textField)
        layout.addComponent(userNameField)
        layout.addComponent(passwortField)
        layout.addComponents(playSound)
        val updateButton = Button("Aktualisieren", Button.ClickListener {
            binder.commit()
            showAllBuildConfigurationsIfConfigured()
        }).apply {
            styleName = ValoTheme.BUTTON_PRIMARY
        }

        val createLink = Button("Erzeuge Monitor Link", Button.ClickListener {
            createMonitorLink()
        }).apply {
            styleName = ValoTheme.BUTTON_FRIENDLY
        }

        val buttonLayout = HorizontalLayout().apply {
            isSpacing = true
            addComponents(updateButton, createLink)
        }

        layout.addComponents(buttonLayout)

        addComponents(layout, accordion)
        setExpandRatio(accordion, 1f)

        showAllBuildConfigurationsIfConfigured()

    }

    private fun createMonitorLink() {
        val parameterString = selectedConfigurations.joinToString("/", "/", "", transform = { buildConfig -> buildConfig.id.stringId })
        val resource = ExternalResource("#!status".plus(parameterString))

        val popupConentLayout = VerticalLayout().apply {
            val urlField = TextField().apply {
                setSizeFull()
                val currentLocation = Page.getCurrent().location
                value = currentLocation.toString().plus(resource.url)

            }

            val buttonContainer = HorizontalLayout().apply {
                isSpacing = true
                setSizeFull()
                val openInTabButton = Button("Im neuen Tab öffnen").apply {
                    val opener = BrowserWindowOpener(resource).apply {
                        features = ""
                    }
                    opener.extend(this)
                    styleName = ValoTheme.BUTTON_SMALL
                }
                addComponents(openInTabButton)
            }
            isSpacing = true
            addComponents(urlField, buttonContainer)
        }
        val popupWindow = Window("Monitor Url").apply {
            center()
            content = popupConentLayout
            addCloseListener { UI.getCurrent().removeWindow(this) }
            isVisible = true
            setWidth(300.0f,Sizeable.Unit.PIXELS)
        }
        UI.getCurrent().addWindow(popupWindow)




    }

    private fun showAllBuildConfigurationsIfConfigured() {
        when (tcServerData.isComplete()) {
            true -> showAllBuildConfigurations()
            else -> Notification.show("Bitte aktualisieren Sie die Zugangsdaten", Notification.Type.WARNING_MESSAGE)
        }
    }

    private fun showAllBuildConfigurations() {
        accordion.removeAllComponents()

        val configurations = tcService.retrieveAllConfigurations()

        configurations.forEach { config ->
            val contentLayout = CssLayout().apply {
                id = "projectcontent"
                config.buildConfigurations.forEach { buildConfig ->
                    val checkBox = CheckBox(buildConfig.name).apply {
                        addValueChangeListener { event ->
                            val selected = event.property.value as Boolean
                            when (selected) {
                                true -> selectedConfigurations.add(buildConfig)
                                false -> selectedConfigurations.remove(buildConfig)
                            }
                        }
                    }
                    addComponent(checkBox)
                }
            }
            accordion.addTab(contentLayout, config.project.name, FontAwesome.FOLDER)
        }

    }

    internal class CustomFieldFactory constructor(val delegate: FieldGroupFieldFactory) : FieldGroupFieldFactory {

        override fun <T : Field<*>?> createField(dataType: Class<*>?, fieldType: Class<T>?): T {
            return delegate.createField(dataType, fieldType)
        }

    }

    internal class URLConverter : Converter<String, URL> {
        override fun getModelType(): Class<URL> = URL::class.java

        override fun getPresentationType(): Class<String> = String::class.java

        override fun convertToPresentation(value: URL?, targetType: Class<out String>?, locale: Locale?): String = value.toString()

        override fun convertToModel(value: String?, targetType: Class<out URL>?, locale: Locale?): URL = try {
            URL(value)
        } catch (ex: MalformedURLException) {
            URL("http://toBeDefined")
        }
    }


}