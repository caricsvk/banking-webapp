package milo.banking;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.MediaType;

@ApplicationPath("/api")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class RestApplication extends Application {
}