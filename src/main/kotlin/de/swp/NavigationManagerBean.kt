package de.swp

import com.vaadin.navigator.Navigator
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.Page
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.spring.navigator.SpringViewProvider
import com.vaadin.ui.UI
import org.springframework.beans.factory.annotation.Autowired


@UIScope
@SpringComponent
class NavigationManagerBean : Navigator(), NavigationManager {

    @Autowired
    lateinit var viewProvider : SpringViewProvider

    @Autowired
    lateinit var viewDisplay : ViewDisplay


    fun init() {
        init(UI.getCurrent(),UriFragmentManager(Page.getCurrent()),viewDisplay)
        addProvider(viewProvider)
    }

}