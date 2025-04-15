/*
 * Copyright (c) 2018 - 2025, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.entgra.device.mgt.core.ui.request.interceptor;

import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationEventBroker;
import io.entgra.device.mgt.core.device.mgt.common.notification.mgt.NotificationListener;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@WebServlet(urlPatterns = {"/ConnectSSE"}, asyncSupported = true)
public class SSEHandler extends HttpServlet implements NotificationListener {

    // map to store list of AsyncContexts per user
    private static final Map<String, List<AsyncContext>> userStreams = new ConcurrentHashMap<>();

    @Override
    public void init(ServletConfig config) {
        // register as a notification listener
        NotificationEventBroker.registerListener(this);
    }

    // called by NotificationEventBroker when a message should be delivered
    @Override
    public void onMessage(String message, List<String> usernames) {
        for (String username : usernames) {
            List<AsyncContext> contexts = userStreams.get(username);
            if (contexts != null) {
                for (AsyncContext ac : new ArrayList<>(contexts)) {
                    try {
                        PrintWriter out = ac.getResponse().getWriter();
                        out.write("data: " + message + "\n\n");
                        out.flush();
                        if (out.checkError()) {
                            contexts.remove(ac);
                        }
                    } catch (IOException e) {
                        contexts.remove(ac);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) {
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");
        final AsyncContext ac = req.startAsync();
        ac.setTimeout(0);
        String username = req.getParameter("user");
        if (username != null) {
            userStreams.computeIfAbsent(username, k -> new CopyOnWriteArrayList<>()).add(ac);
        }
        ac.addListener(new AsyncListener() {
            @Override
            public void onComplete(AsyncEvent event) {
                removeContext(ac);
            }
            @Override
            public void onTimeout(AsyncEvent event) {
                removeContext(ac);
            }
            @Override
            public void onError(AsyncEvent event) {
                removeContext(ac);
            }
            @Override
            public void onStartAsync(AsyncEvent event) {
            }
        });
        try {
            PrintWriter out = ac.getResponse().getWriter();
            out.write("data: You’re now connected to the real-time notification service.\n\n");
            out.flush();
        } catch (IOException e) {
            removeContext(ac);
            e.printStackTrace();
        }
    }

    private void removeContext(AsyncContext ac) {
        for (List<AsyncContext> contextList : userStreams.values()) {
            contextList.remove(ac);
        }
    }
}
