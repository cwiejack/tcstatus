package de.swp

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringView
import com.vaadin.ui.HorizontalLayout

@SpringView(name = "")
class AdminView : HorizontalLayout(),View {

    override fun enter(event: ViewChangeListener.ViewChangeEvent?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}