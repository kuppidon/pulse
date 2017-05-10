package ua.pulse.view;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.bean.UserBean;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.vaadin.PulseUI;


/**
 * A responsive menu component providing user information and the controls for
 * primary navigation between the views.
 */
@SuppressWarnings({ "serial", "unchecked" })
public final class PulseMenu extends CustomComponent {

    public static final String ID = "pulse-menu";
    public static final String FUTURE_BADGE_ID = "pulse-menu-future-badge";
    public static final String CURRENT_BADGE_ID = "pulse-menu-current-badge";
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    private Label currentBadge;
    private Label futureBadge;
    private MenuItem settingsItem;

    public PulseMenu() {
        setPrimaryStyleName("valo-menu");
        setId(ID);
        setSizeUndefined();

        // There's only one PulseMenu per UI so this doesn't need to be
        // unregistered from the UI-scoped PulseEventBus.
        PulseEventBus.register(this);

        setCompositionRoot(buildContent());
    }

    private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    private Component buildTitle() {
        Label logo = new Label(" <strong>\"ПУЛЬС\"</strong>",
                ContentMode.HTML);
        logo.setSizeUndefined();
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        logoWrapper.addStyleName("valo-menu-title");
        return logoWrapper;
    }


    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar();

        settings.addStyleName("user-menu");
        final UserBean user = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        settingsItem = settings.addItem("",
                new ThemeResource("img/pulse.gif"), null);
        updateUserName(null);
        settingsItem.addItem("Выход", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                PulseEventBus.post(new PulseEvent.UserLoggedOutEvent());
            }
        });
        return settings;
    }

    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button("Меню", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                if (getCompositionRoot().getStyleName()
                        .contains(STYLE_VISIBLE)) {
                    getCompositionRoot().removeStyleName(STYLE_VISIBLE);
                } else {
                    getCompositionRoot().addStyleName(STYLE_VISIBLE);
                }
            }
        });
        valoMenuToggleButton.setIcon(FontAwesome.LIST);
        valoMenuToggleButton.addStyleName("valo-menu-toggle");
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        return valoMenuToggleButton;
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        final UserBean user = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        for (final PulseViewType view : PulseViewType.values()) {

            if (!view.getRoles().contains(user.getRole())) {
                continue;
            }

            Component menuItemComponent = new ValoMenuItemButton(view);
            if (view == PulseViewType.FUTURE) {
                futureBadge = new Label();
                futureBadge.setId(FUTURE_BADGE_ID);
                menuItemComponent = buildBadgeWrapper(menuItemComponent,futureBadge);
            }
            if (view == PulseViewType.CURRENT) {
                currentBadge = new Label();
                currentBadge.setId(CURRENT_BADGE_ID);
                menuItemComponent = buildBadgeWrapper(menuItemComponent,currentBadge);
            }
            menuItemsLayout.addComponent(menuItemComponent);
       }
        return menuItemsLayout;

    }

    private Component buildBadgeWrapper(final Component menuItemButton,
                                        final Component badgeLabel) {
        CssLayout dashboardWrapper = new CssLayout(menuItemButton);
        dashboardWrapper.addStyleName("badgewrapper");
        dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
        badgeLabel.addStyleName(ValoTheme.MENU_BADGE);
        badgeLabel.setWidthUndefined();
        badgeLabel.setVisible(false);
        dashboardWrapper.addComponent(badgeLabel);
        return dashboardWrapper;
    }

    @Override
    public void attach() {
        super.attach();
        //updateNotificationsCount(null);
    }

    @Subscribe
    public void postViewChange(final PulseEvent.PostViewChangeEvent event) {
        // After a successful view change the menu can be hidden in mobile view.
        getCompositionRoot().removeStyleName(STYLE_VISIBLE);
    }

    @Subscribe
    public void updateFutureCount(final PulseEvent.FutureCountUpdatedEvent event) {
       //int unreadNotificationsCount = PulseUI.getDataProvider().getUnreadNotificationsCount();
//        currentBadge.setValue(String.valueOf(unreadNotificationsCount));
//        currentBadge.setVisible(unreadNotificationsCount > 0);
        currentBadge.setValue(String.valueOf(2));
        currentBadge.setVisible(true);
    }

    @Subscribe
    public void updateCurrentCount(final PulseEvent.CurrentCountUpdatedEvent event) {
        //currentBadge.setValue(String.valueOf(event.getCount()));
        //currentBadge.setVisible(event.getCount() > 0);
    }

    @Subscribe
    public void updateUserName(final PulseEvent.ProfileUpdatedEvent event) {
        UserBean user = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        settingsItem.setText(user.getFullName());
    }

    public final class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected";

        private final PulseViewType view;

        public ValoMenuItemButton(final PulseViewType view) {
            this.view = view;
            setPrimaryStyleName("valo-menu-item");
            setIcon(view.getIcon());
            setCaption(view.getViewName().substring(0, 1).toUpperCase()
                    + view.getViewName().substring(1));
            PulseEventBus.register(this);
            addClickListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    UI.getCurrent().getNavigator()
                            .navigateTo(view.getViewName());
                }
            });
        }

        @Subscribe
        public void postViewChange(final PulseEvent.PostViewChangeEvent event) {
            removeStyleName(STYLE_SELECTED);
            if (event.getView().equals(view)) {
                addStyleName(STYLE_SELECTED);
                PulseEventBus.post(new PulseEvent.ChangeSectionMenu(view));
            }
        }
    }
}
