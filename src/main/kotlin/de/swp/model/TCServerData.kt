package de.swp.model

import com.vaadin.spring.annotation.SpringComponent
import java.net.URL

@SpringComponent
data class TCServerData(var serverUrl: URL? = null, var userName : String? = null, var password : String? = null) {

    //needed to be used as Spring Component
    constructor() : this(null,null,null) {

    }
}



