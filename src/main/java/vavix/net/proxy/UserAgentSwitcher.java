/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import vavi.util.Debug;


/**
 * UserAgentSwitcher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
public class UserAgentSwitcher {

    /** */
    private UserAgentDao userAgentDao;

    /** */
    public void setUserAgentDao(UserAgentDao userAgentDao) {
        this.userAgentDao = userAgentDao;
    }

    /** */
    private Random random = new Random(System.currentTimeMillis());

    /** */
    public String getUserAgent() {
        List<String> userAgents = userAgentDao.getUserAgents();
        String userAgent = userAgents.get(random.nextInt(userAgents.size()));
Debug.println(Level.FINER, "userAgent: " + userAgent);
        return userAgent;
    }
}
