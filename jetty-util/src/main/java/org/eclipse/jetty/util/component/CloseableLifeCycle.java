//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
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

package org.eclipse.jetty.util.component;

/**
 * Use a {@link LifeCycle} as a {@link AutoCloseable}
 *
 * @param <T> the lifecycle
 */
public final class CloseableLifeCycle<T extends LifeCycle> implements AutoCloseable
{
    private T delegate;

    public CloseableLifeCycle(T lifecycle)
    {
        this.delegate = lifecycle;
        try
        {
            this.delegate.start();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to auto-start LifeCycle component: " + this.delegate.getClass(), e);
        }
    }

    @Override
    public void close()
    {
        try
        {
            this.delegate.stop();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to auto-stop LifeCycle component: " + this.delegate.getClass(), e);
        }
    }

    public T get()
    {
        return this.delegate;
    }
}
