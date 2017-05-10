package ua.pulse.component;

import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;

import java.util.Date;

/**
 * Created by Alex on 25.12.2016.
 */
public class SelectPeriodWindow extends Window {

    private DateField startDate;
    private DateField endDate;

    public SelectPeriodWindow(Date startDate, Date endDate){

        setModal(true);
        setClosable(false);
        setResizable(false);
        Responsive.makeResponsive(this);
        center();
        setCaption("<b>Выбор периода</b>");
        setCaptionAsHtml(true);
        setSizeUndefined();

        VerticalLayout mainGroup = new VerticalLayout();
        setContent(mainGroup);
        mainGroup.setSpacing(true);
        mainGroup.setMargin(true);

        GridLayout matrixGrid = new GridLayout(2,2);
        matrixGrid.setSizeFull();
        matrixGrid.setSizeUndefined(); // This isn't enough

        Label titleStart = new Label("Начало периода:", ContentMode.HTML);
        titleStart.addStyleName(ValoTheme.LABEL_COLORED);
        titleStart.setWidth("165px");
        this.startDate = new DateField(null,startDate);
        this.startDate.setResolution(Resolution.DAY);
        this.startDate.setDateFormat("dd.MM.yyyy");
        this.startDate.setWidth("130px");
        this.startDate.addStyleName(ValoTheme.DATEFIELD_SMALL);

        Label titleEnd = new Label("Окончание периода:", ContentMode.HTML);
        titleEnd.addStyleName(ValoTheme.LABEL_COLORED);
        titleEnd.setWidth("165px");
        this.endDate = new DateField(null,endDate);
        this.endDate.setWidth("130px");
        this.endDate.setResolution(Resolution.DAY);
        this.endDate.setDateFormat("dd.MM.yyyy");
        this.endDate.addStyleName(ValoTheme.DATEFIELD_SMALL);

        matrixGrid.setSpacing(true);
        matrixGrid.addComponent(titleStart);
        matrixGrid.addComponent(this.startDate);
        matrixGrid.addComponent(titleEnd);
        matrixGrid.addComponent(this.endDate);

        mainGroup.addComponent(matrixGrid);

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Button ok = new Button("OK");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addStyleName(ValoTheme.BUTTON_SMALL);
        ok.addClickListener(event -> {
            //оповещаем всех слущателей об изменении периода
            PulseEventBus.post(new PulseEvent.SelectPeriod(this.startDate.getValue(),this.endDate.getValue()));
            close();
        });

        Button cancel = new Button("Отмена");
        cancel.addStyleName(ValoTheme.BUTTON_DANGER);
        cancel.addStyleName(ValoTheme.BUTTON_SMALL);
        cancel.addClickListener((Button.ClickListener) event -> close());

        footer.addComponents(ok,cancel);
        footer.setExpandRatio(ok, 1);
        footer.setComponentAlignment(ok, Alignment.TOP_RIGHT);

        mainGroup.addComponent(footer);
    }
}
