//
//  ========================================================================
//  Copyright (c) 1995-2013 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.websocket.jsr356.annotations;

import java.lang.reflect.Method;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.OnClose;
import javax.websocket.Session;

import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.jsr356.annotations.Param.Role;

/**
 * Callable for {@link OnClose} annotated methods
 */
public class OnCloseCallable extends JsrCallable
{
    private int idxCloseReason = -1;

    public OnCloseCallable(Class<?> pojo, Method method)
    {
        super(pojo,method);
    }

    public void call(Object endpoint, CloseInfo close)
    {
        this.call(endpoint,close.getStatusCode(),close.getReason());
    }

    public void call(Object endpoint, int statusCode, String reason)
    {
        // Close Reason is an optional parameter
        if (idxCloseReason >= 0)
        {
            // convert to javax.websocket.CloseReason
            CloseReason jsrclose = new CloseReason(CloseCodes.getCloseCode(statusCode),reason);
            super.args[idxCloseReason] = jsrclose;
        }
        super.call(endpoint,super.args);
    }

    public OnCloseCallable copy()
    {
        OnCloseCallable copy = new OnCloseCallable(pojo,method);
        super.copyTo(copy);
        copy.idxCloseReason = this.idxCloseReason;
        return copy;
    }

    @Override
    public void init(Session session, Map<String, String> pathParams)
    {
        idxCloseReason = findIndexForRole(Role.CLOSE_REASON);
        super.init(session,pathParams);
    }
}
