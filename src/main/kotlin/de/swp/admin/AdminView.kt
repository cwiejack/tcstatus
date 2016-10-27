package de.swp.admin

import com.vaadin.data.fieldgroup.BeanFieldGroup
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory
import com.vaadin.data.fieldgroup.FieldGroupFieldFactory
import com.vaadin.data.util.converter.Converter
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.Field
import com.vaadin.ui.FormLayout
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.TextField
import de.swp.model.TCServerData
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@SpringView(name = "")
class AdminView constructor(val tcServerData: TCServerData) : HorizontalLayout(), View {

    init {
        id = "adminView"
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
        binder.bind(textField,"serverUrl")

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

        layout.addComponent(textField)
        layout.addComponent(userNameField)
        layout.addComponent(passwortField)

        addComponent(layout)
    }

    internal class CustomFieldFactory constructor(val delegate: FieldGroupFieldFactory) : FieldGroupFieldFactory {

        override fun <T : Field<*>?> createField(dataType: Class<*>?, fieldType: Class<T>?): T {
            return delegate.createField(dataType,fieldType)
        }

    }

    internal class URLConverter : Converter<String, URL> {
        override fun getModelType(): Class<URL> = URL::class.java

        override fun getPresentationType(): Class<String>  = String::class.java

        override fun convertToPresentation(value: URL?, targetType: Class<out String>?, locale: Locale?): String  = value.toString()

        override fun convertToModel(value: String?, targetType: Class<out URL>?, locale: Locale?): URL =  try {
            URL(value)
        } catch (ex : MalformedURLException) {
            URL("http://toBeDefined")
        }
    }
}