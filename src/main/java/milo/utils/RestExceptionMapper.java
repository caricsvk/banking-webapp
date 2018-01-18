package milo.utils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

	@Context
	private HttpServletRequest request;

	private static final Logger log = Logger.getLogger(RestExceptionMapper.class.getName());

	@Override
	public Response toResponse(Throwable exception) {
		WebApplicationException webApplicationException = findWebApplicationException(exception);
		if (webApplicationException != null) {
			Response response = webApplicationException.getResponse();
			// log only not expected exceptions
			if (response.getStatus() < 400 || response.getStatus() > 499) {
				log.log(Level.WARNING, exception.getMessage(), exception);
			}
			// if there is entity use it, otherwise build entity from status info
			if (webApplicationException.getResponse().getEntity() == null) {
				return Response.status(response.getStatus()).entity("{\"code\":\"" +
						response.getStatusInfo().toString() + "\"}").build();

			} else if (webApplicationException.getResponse().getEntity() instanceof String) {
				return Response.status(response.getStatus()).entity("{\"code\":\"" +
						webApplicationException.getResponse().getEntity() + "\"}").build();
			} else {
				return webApplicationException.getResponse();
			}

		} else {
			log.log(Level.WARNING, "RestExceptionMapper caught exception at " + getUrl() + " : "
					+ exception.getMessage(), exception);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Internal server error, check logs for more info.").build();
		}
	}

	private String getUrl() {
		return request != null ? request.getRequestURI() : "Unknown URI";
	}

	private WebApplicationException findWebApplicationException(Throwable throwable) {
		if (throwable instanceof WebApplicationException) {
			return (WebApplicationException) throwable;
		} else if (throwable.getCause() != null) {
			return findWebApplicationException(throwable.getCause());
		} else {
			return null;
		}
	}
}