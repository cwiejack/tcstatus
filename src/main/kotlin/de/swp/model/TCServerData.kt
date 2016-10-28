package de.swp.model

import com.vaadin.spring.annotation.SpringComponent
import java.net.URL

@SpringComponent
data class TCServerData(var serverUrl: URL?, var userName : String?, var password : String?) {

    //needed to be used as Spring Component
    constructor() : this(null, null, null) {

    }

    fun isComplete() : Boolean {
        return serverUrl != null && userName.isNullOrBlank().not() && password.isNullOrBlank().not()
    }
}



