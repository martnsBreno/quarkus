package io.github.martnsbreno;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import javax.ws.rs.core.Application;
@OpenAPIDefinition(
        info = @Info(
                title = "Quarkus Social Application",
                version = "1.0",
                contact = @Contact(
                        name = "Breno Martins",
                        url = "https://github.com/martnsBreno",
                        email = "brenojpmartins2002@gmail.com")))
public class QuarkussocialSwagger extends Application {
}
