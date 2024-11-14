/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.net.proxy;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Random;

import static java.lang.System.getLogger;


/**
 * UserAgentSwitcher.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071004 nsano initial version <br>
 */
public class UserAgentSwitcher {

    private static final Logger logger = getLogger(UserAgentSwitcher.class.getName());

    /** */
    private UserAgentDao userAgentDao;

    /** */
    public void setUserAgentDao(UserAgentDao userAgentDao) {
        this.userAgentDao = userAgentDao;
    }

    /** */
    private final Random random = new Random(System.currentTimeMillis());

    /** */
    public String getUserAgent() {
        List<String> userAgents = userAgentDao.getUserAgents();
        String userAgent = userAgents.get(random.nextInt(userAgents.size()));
logger.log(Level.TRACE, "userAgent: " + userAgent);
        return userAgent;
    }
}
