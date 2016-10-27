package de.swp.admin

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.HorizontalLayout
import de.swp.model.TCServerData

@SpringView(name = "")
class AdminView constructor(val tcServerData: TCServerData) : HorizontalLayout(), View {


    override fun enter(event: ViewChangeListener.ViewChangeEvent?) {
        println("params ".plus(event?.parameters))
    }
}