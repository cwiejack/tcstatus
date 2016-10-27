package de.swp.navigation

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewDisplay
import com.vaadin.spring.annotation.SpringComponent
import com.vaadin.spring.annotation.UIScope
import com.vaadin.ui.Component
import com.vaadin.ui.CssLayout
import com.vaadin.ui.HorizontalLayout

@UIScope
@SpringComponent
class ViewDisplayBean : HorizontalLayout(), ViewDisplay {

    val contentArea: CssLayout;

    init {
        setSizeFull();
        contentArea = CssLayout().apply {
            setPrimaryStyleName("valo-content");
            addStyleName("v-scrollable");
            setSizeFull();
        };

        addComponent(contentArea);
        setExpandRatio(contentArea, 1.0f);
    }



    override fun showView(view: View?) {
        contentArea.removeAllComponents();
        contentArea.addComponent(Component::class.java.cast(view));
    }

}
