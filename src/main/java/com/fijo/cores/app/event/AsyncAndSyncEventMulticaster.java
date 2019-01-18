/**
 * 
 */
package com.fijo.cores.app.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;

import com.fijo.cores.utils.annotations.AsyncEventListener;

/**
 * @author zhangbo
 * 
 * 在一个应用中实现同时可发布异步事件和同步事件
 * 
 * 由@AsyncEventListener 标注的Listener为移步监听，否则为同步监听
 * 
 * 参考：
 * http://stackoverflow.com/questions/26276009/how-to-configure-async-and-sync-event-publishers-using-spring
 * http://www.keyup.eu/en/blog/101-synchronous-and-asynchronous-spring-events-in-one-application
 */
public class AsyncAndSyncEventMulticaster implements ApplicationEventMulticaster {

    private ApplicationEventMulticaster asyncEventMulticaster;
    private ApplicationEventMulticaster syncEventMulticaster;

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        // choose multicaster by annotation
        if (listener.getClass().getAnnotation(AsyncEventListener.class) != null) {
            asyncEventMulticaster.addApplicationListener(listener);
        } else {
            syncEventMulticaster.addApplicationListener(listener);
        }
    }

    @Override
    public void addApplicationListenerBean(String listenerBeanName) {
        // do nothing
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        asyncEventMulticaster.removeApplicationListener(listener);
        syncEventMulticaster.removeApplicationListener(listener);
    }

    @Override
    public void removeApplicationListenerBean(String listenerBeanName) {
        // do nothing
    }

    @Override
    public void removeAllListeners() {
        syncEventMulticaster.removeAllListeners();
        asyncEventMulticaster.removeAllListeners();
    }

    @Override
    public void multicastEvent(ApplicationEvent event) {
        syncEventMulticaster.multicastEvent(event);
        asyncEventMulticaster.multicastEvent(event);
    }

    // ******************** SETTERS ********************

    public void setAsyncEventMulticaster(ApplicationEventMulticaster asyncEventMulticaster) {
        this.asyncEventMulticaster = asyncEventMulticaster;
    }

    public void setSyncEventMulticaster(ApplicationEventMulticaster syncEventMulticaster) {
        this.syncEventMulticaster = syncEventMulticaster;
    }
}

