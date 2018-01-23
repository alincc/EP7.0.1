package com.elasticpath.cm.status;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.repo.health.monitoring.ServerStatusChecker;
import com.elasticpath.repo.health.monitoring.StatusChecker;
import org.eclipse.rap.rwt.service.ServiceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatusServiceHandler implements ServiceHandler {

    private static final Integer REFRESH_INTERVAL_SECONDS_DEFAULT = 10;

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        StatusChecker statusChecker = ServiceLocator.getService("statusChecker");
        ServerStatusChecker serverStatusChecker = ServiceLocator.getService("serverStatusChecker");

        serverStatusChecker.getServerStatus(REFRESH_INTERVAL_SECONDS_DEFAULT, statusChecker, request, response);
    }
}
