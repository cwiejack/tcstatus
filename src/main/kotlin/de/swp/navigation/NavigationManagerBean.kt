package de.swp.navigation

import com.vaadin.navigator.Navigator
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.Page
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.spring.navigator.SpringViewProvider
import com.vaadin.ui.UI


@UIScope
@SpringComponent
class NavigationManagerBean constructor(val viewProvider: SpringViewProvider, val viewDisplay: ViewDisplay): Navigator(), NavigationManager {


    fun init() {
        init(UI.getCurrent(), UriFragmentManager(Page.getCurrent()),viewDisplay)
        addProvider(viewProvider)
    }

}