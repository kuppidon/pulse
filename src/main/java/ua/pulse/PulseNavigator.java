package ua.pulse;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import ua.pulse.bean.UserBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.vaadin.PulseUI;
import ua.pulse.view.PulseViewType;

@SuppressWarnings("serial")
public class PulseNavigator extends Navigator {

    private PulseViewType ERROR_VIEW;// = PulseViewType.CURRENT;
    private ViewProvider errorViewProvider;
    private PulseViewType currentViewType;

    public PulseNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);

        initViewChangeListener();
        initViewProviders();

    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                currentViewType = PulseViewType.getByViewName(event
                        .getViewName());

                // Appropriate events get fired after the view is changed.
                PulseEventBus.post(new PulseEvent.PostViewChangeEvent(currentViewType));
                PulseEventBus.post(new PulseEvent.BrowserResizeEvent());
                PulseEventBus.post(new PulseEvent.CloseOpenWindowsEvent());

            }
        });
    }

    private void initViewProviders() {

        final UserBean user = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        // A dedicated view provider is added for each separate view type
        for (final PulseViewType viewType : PulseViewType.values()) {

            if (!viewType.getRoles().contains(user.getRole())) {
                continue;
            }

            ViewProvider viewProvider = new ClassBasedViewProvider(
                    viewType.getViewName(), viewType.getViewClass()) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private View cachedInstance;

                @Override
                public View getView(final String viewName) {
                    View result = null;
                    if (viewType.getViewName().equals(viewName)) {
                        if (viewType.isStateful()) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = super.getView(viewType
                                        .getViewName());
                            }
                            result = cachedInstance;
                        } else {
                            // Non-stateful views get instantiated every time
                            // they're navigated to
                            result = super.getView(viewType.getViewName());
                        }
                    }
                    return result;
                }
            };

            if (viewType.isErrorView()){
                ERROR_VIEW = viewType;
                errorViewProvider = viewProvider;
            }

            addProvider(viewProvider);

        }

            setErrorProvider(new ViewProvider() {
                @Override
                public String getViewName(final String viewAndParameters) {
                    return ERROR_VIEW.getViewName();
                }

                @Override
                public View getView(final String viewName) {
                    return errorViewProvider.getView(ERROR_VIEW.getViewName());
                }
            });
        }

    public PulseViewType getCurrentViewType() {
        return currentViewType;
    }
}

