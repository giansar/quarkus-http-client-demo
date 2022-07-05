package id.giansar.demo.resource;

import id.giansar.demo.service.DemoService;
import id.giansar.demo.dto.DemoRequestDto;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/demo")
public class DemoResource {

    @Inject
    DemoService demoService;

    @POST
    @Path("/inquiry/server")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServer(DemoRequestDto requestDto) {
        return demoService.getServer(requestDto);
    }
}
