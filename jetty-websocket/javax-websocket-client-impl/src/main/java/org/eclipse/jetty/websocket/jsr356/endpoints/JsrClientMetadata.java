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

package org.eclipse.jetty.websocket.jsr356.endpoints;

import java.util.LinkedList;

import javax.websocket.ClientEndpoint;
import javax.websocket.Decoder;

import org.eclipse.jetty.websocket.api.InvalidWebSocketException;
import org.eclipse.jetty.websocket.jsr356.annotations.IJsrParamId;
import org.eclipse.jetty.websocket.jsr356.annotations.JsrMetadata;
import org.eclipse.jetty.websocket.jsr356.annotations.JsrParamIdBinaryDecoder;
import org.eclipse.jetty.websocket.jsr356.annotations.JsrParamIdTextDecoder;
import org.eclipse.jetty.websocket.jsr356.decoders.DecoderRef;
import org.eclipse.jetty.websocket.jsr356.decoders.Decoders;
import org.eclipse.jetty.websocket.jsr356.encoders.Encoders;

public class JsrClientMetadata extends JsrMetadata<ClientEndpoint>
{
    private final ClientEndpoint endpoint;

    public JsrClientMetadata(Class<?> websocket)
    {
        super(websocket);

        ClientEndpoint anno = websocket.getAnnotation(ClientEndpoint.class);
        if (anno == null)
        {
            throw new InvalidWebSocketException("Unsupported WebSocket object, missing @" + ClientEndpoint.class + " annotation");
        }

        this.endpoint = anno;
        this.encoders = new Encoders(anno.encoders());
        this.decoders = new Decoders(anno.decoders());
    }

    @Override
    public void customizeParamsOnMessage(LinkedList<IJsrParamId> params)
    {
        for (DecoderRef ref : decoders)
        {
            Class<? extends Decoder> decoder = ref.getDecoder();

            if (Decoder.Text.class.isAssignableFrom(decoder) || Decoder.TextStream.class.isAssignableFrom(decoder))
            {
                params.add(new JsrParamIdTextDecoder(ref));
                continue;
            }

            if (Decoder.Binary.class.isAssignableFrom(decoder) || Decoder.BinaryStream.class.isAssignableFrom(decoder))
            {
                params.add(new JsrParamIdBinaryDecoder(ref));
                continue;
            }

            throw new IllegalStateException("Invalid Decoder: " + decoder);
        }
    }

    @Override
    public ClientEndpoint getAnnotation()
    {
        return endpoint;
    }
}
