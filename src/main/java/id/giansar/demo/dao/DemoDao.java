package id.giansar.demo.dao;

import id.giansar.demo.entity.HostServer;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class DemoDao {
    @Transactional
    public void saveOrUpdateHostServer(HostServer hostServer) throws Exception {
        HostServer hostServerExisting = HostServer.find("host", hostServer.host).firstResult();
        if (hostServerExisting == null) {
            hostServer.persist();
        } else {
            hostServerExisting.server = hostServer.server;
            hostServerExisting.dateInquiry = hostServer.dateInquiry;
            hostServerExisting.persist();
        }
    }
}
