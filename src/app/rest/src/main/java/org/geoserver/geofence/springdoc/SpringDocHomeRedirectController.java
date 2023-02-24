package org.geoserver.geofence.springdoc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
class SpringDocHomeRedirectController {

    @Value("${springdoc.swagger-ui.path}")
    private String basePath;

    @RequestMapping(value = "/")
    public String redirectToSwaggerUI() {
        return "redirect:/api/v2/swagger-ui/index.html";
    }
}
