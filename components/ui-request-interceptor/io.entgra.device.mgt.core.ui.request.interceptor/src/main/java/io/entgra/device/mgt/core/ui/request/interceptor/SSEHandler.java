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
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/mySSE"}, asyncSupported = true)
public class SSEHandler extends HttpServlet implements NotificationListener {

    public static final Queue<AsyncContext> ongoingRequests = new ConcurrentLinkedQueue<>();
    public static final Queue<String> pendingMessages = new ConcurrentLinkedQueue<>();
    private ScheduledExecutorService service;

    @Override
    public void init(ServletConfig config) {
        // register as listener
        NotificationEventBroker.registerListener(this);
        final Runnable notifier = () -> {
            List<String> messagesToSend = new ArrayList<>();
            while (!pendingMessages.isEmpty()) {
                String msg = pendingMessages.poll();
                if (msg != null) {
                    messagesToSend.add(msg);
                }
            }
            if (messagesToSend.isEmpty()) {
                return;
            }
            Iterator<AsyncContext> iterator = ongoingRequests.iterator();
            while (iterator.hasNext()) {
                AsyncContext ac = iterator.next();
                try {
                    PrintWriter out = ac.getResponse().getWriter();
                    for (String message : messagesToSend) {
                        out.write("data: " + message + "\n\n");
                    }
                    out.flush();
                    if (out.checkError()) {
                        iterator.remove();
                    }
                } catch (IOException e) {
                    iterator.remove();
                    e.printStackTrace();
                }
            }
        };
        service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(notifier, 1, 1, TimeUnit.SECONDS);
    }

    // receive messages from the broker
    @Override
    public void onMessage(String message) {
        pendingMessages.add(message);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) {
        req.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");
        final AsyncContext ac = req.startAsync();
        ac.setTimeout(0);
        ac.addListener(new AsyncListener() {
            @Override public void onComplete(AsyncEvent event) { ongoingRequests.remove(ac); }
            @Override public void onTimeout(AsyncEvent event) { ongoingRequests.remove(ac); }
            @Override public void onError(AsyncEvent event) { ongoingRequests.remove(ac); }
            @Override public void onStartAsync(AsyncEvent event) {}
        });
        ongoingRequests.add(ac);
        try {
            PrintWriter out = ac.getResponse().getWriter();
            out.write("data: Connected to SSE\n\n");
            out.flush();
        } catch (IOException e) {
            ongoingRequests.remove(ac);
            e.printStackTrace();
        }
    }
}
