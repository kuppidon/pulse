package ua.pulse.event;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import ua.pulse.vaadin.PulseUI;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@EnableScheduling
public class Broadcaster implements Serializable {
    private static final long serialVersionUID = 1L;
    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public interface BroadcastListener {
        void receiveBroadcast(PushEvent event);
    }

    private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

    public static synchronized void register(BroadcastListener newlistener) {
        listeners.add(newlistener);
        LinkedList<BroadcastListener> removeList = new LinkedList<BroadcastListener>();
        for (final BroadcastListener listener : listeners) {
            if (listener != newlistener && !((PulseUI) listener).getPushConnection().isConnected()) {
                removeList.add(listener);
            }
        }
        for (final BroadcastListener listener : removeList) {
            listeners.remove(listener);
            //Message msg = new Message("deluser", listener.toString(), "");
            //broadcast(msg);
        }
    }


    public static synchronized void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static synchronized void broadcast(final PushEvent  message) {
        for (final BroadcastListener listener : listeners)
            executorService.execute(() -> {
                listener.receiveBroadcast(message);
            });
    }


}