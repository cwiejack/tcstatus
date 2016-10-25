package de.swp

import com.vaadin.navigator.ViewChangeListener
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy

@UIScope
@SpringComponent
class ViewChangeListenerBean : ViewChangeListener {

    @Autowired
    @Lazy
    lateinit var navigationManager : NavigationManager


    override fun beforeViewChange(event: ViewChangeListener.ViewChangeEvent?): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun afterViewChange(event: ViewChangeListener.ViewChangeEvent?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}