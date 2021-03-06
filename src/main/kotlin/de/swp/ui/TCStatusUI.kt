package de.swp.ui

import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.navigator.ViewDisplay
import com.vaadin.server.Responsive
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Component
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import de.swp.navigation.NavigationManagerBean
import org.springframework.beans.factory.annotation.Autowired

@Push
@SpringUI
@Theme("tcstatus")
class TCStatusUI : UI() {

    @Autowired
    lateinit var viewDisplay: ViewDisplay

    @Autowired
    lateinit var navigationManager : NavigationManagerBean

    override fun init(request: VaadinRequest?) {
        addStyleName(ValoTheme.UI_WITH_MENU)
        Responsive.makeResponsive(this)

        content = viewDisplay as Component
        navigator = navigationManager
        navigationManager.init()

    }
}