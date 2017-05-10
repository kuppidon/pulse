package ua.pulse.view.mainforms;

import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.*;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;
import ua.pulse.PulseNavigator;
import ua.pulse.bean.*;
import ua.pulse.component.CreateEditHospitalWindow;
import ua.pulse.component.CreateEditSpecialisationWindow;
import ua.pulse.component.CreateEditUserWindow;
import ua.pulse.component.CreateEditVenueWindow;
import ua.pulse.converter.ObjectsConverterService;
import ua.pulse.event.PulseEvent;
import ua.pulse.event.PulseEventBus;
import ua.pulse.libs.VenueType;
import ua.pulse.vaadin.PulseUI;
import ua.pulse.view.PulseViewType;

import java.util.List;

/**
 * Created by Alex on 15.01.2017.
 */
public class AdminView extends VerticalLayout implements View {


    private HospitalBean hospital;
    private UserBean currentUser;
    private Grid grid;
    private final ObjectsConverterService objectsConverterService;
    private PulseViewType type;
    private Button addBtn;
    private HorizontalLayout content;
    private TreeTable tree;
    private Label title;



    public AdminView(){

        setSizeFull();
        addStyleName("postview");
        PulseEventBus.register(this);

        currentUser = ((PulseUI)PulseUI.getCurrent()).getCurrentUser();
        hospital  = currentUser.getHospital();
        objectsConverterService = PulseUI.getObjectsConverterService();

        content = new HorizontalLayout();
        content.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        content.setWidth("100%");
        content.setMargin(false);
        Responsive.makeResponsive(content);

        addBtn = new Button("Добавить", FontAwesome.PLUS);
        addBtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        addBtn.addStyleName(ValoTheme.BUTTON_TINY);
        content.addComponent(addBtn);

        title = new Label("");
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_H2);
        addComponent(title);

        tree = new TreeTable();
        tree.setSizeFull();
        tree.setImmediate(true);
        tree.setSelectable(true);
        tree.setNullSelectionAllowed(false);

        grid = new Grid();
        grid.setSizeFull();
        grid.setImmediate(true);
        Grid.SingleSelectionModel selectionModel = new Grid.SingleSelectionModel();
        selectionModel.setDeselectAllowed(false);
        grid.setSelectionModel(selectionModel);
        grid.setColumnReorderingAllowed(true);

        addComponents(content,grid,tree);
        setExpandRatio(grid,1);
        setExpandRatio(tree,1);

    }

    private void buildGridHospitals(){

        tree.setVisible(false);
        grid.setVisible(true);
        grid.removeAllColumns();

        grid.setEditorEnabled(true);
        grid.addStyleName("grid-button-delete");
        BeanContainer container = new BeanContainer(HospitalBean.class);
        container.setBeanIdProperty("id");
        container.addAll(HospitalBean.getAllHospitals());
        GeneratedPropertyContainer wrappingContainer = new GeneratedPropertyContainer(container);

        //счетчик строк
        wrappingContainer.addGeneratedProperty("rowHeader", new PropertyValueGenerator<Integer>() {
            @Override
            public Integer getValue(Item item, Object itemId, Object propertyId) {
                return container.indexOfId(itemId) + 1;
            }
            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }
        });

        //кнопка удаления
        wrappingContainer.addGeneratedProperty("delete", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "";
            }
            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        grid.setContainerDataSource(wrappingContainer);
        grid.setColumns("rowHeader","name","address","delete");
        grid.getColumn("name").setExpandRatio(1);
        grid.getColumn("address").setExpandRatio(1);

        Grid.HeaderRow header = grid.getDefaultHeaderRow();
        header.getCell("name").setHtml("Наименование");
        header.getCell("address").setHtml("Адрес");

        grid.getColumn("rowHeader").setHeaderCaption("").setHidable(false).setEditable(false).setResizable(false);//.setWidth(30);
        grid.setFrozenColumnCount(1);
        grid.getColumn("address").setResizable(false);
        grid.addColumnResizeListener(event -> {
            grid.getColumn("address").setResizable(true).setWidthUndefined().setResizable(false);
        });

        grid.setEditorSaveCaption("Сохранить");
        grid.setEditorCancelCaption("Отмена");
        grid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {}
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                try{
                    Item gpi = ((GeneratedPropertyContainer.GeneratedPropertyItem) commitEvent.getFieldBinder().getItemDataSource()).getWrappedItem();
                    Object item = ((BeanItem) gpi).getBean();
                    objectsConverterService.convertToSpringBean(((HospitalBean) item)).save();
                }catch (Exception e){
                    Notification.show("Ошика записи",e.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        grid.getColumn("delete")
                .setRenderer(new ButtonRenderer(event -> {
                    Long hospitalId = (Long) event.getItemId();
                    MessageBox.createWarning()
                            .withHtmlMessage("<font color=\"red\"> Удалить <b>\"" + ((GeneratedPropertyContainer) grid.getContainerDataSource()).getContainerProperty(hospitalId,"name") + "\"</b>?</font>")
                            .withOkButton(()->{deleteHospital(hospitalId);}, ButtonOption.caption("Удалить"), ButtonOption.style(ValoTheme.BUTTON_DANGER),ButtonOption.icon(FontAwesome.TRASH))
                            .withCancelButton(()->{},ButtonOption.caption("Отмена"))
                            .open();
                }))
                .setHeaderCaption("")
                .setHidable(false)
                .setEditable(false)
                .setResizable(false);

        //filters
        PulseUI.getPulseHelper().setRowFilters(grid,container);

    }

    private void buildGridSpecialisations(){

        tree.setVisible(false);
        grid.setVisible(true);
        grid.removeAllColumns();
        grid.addStyleName("grid-button-delete");

        grid.setEditorEnabled(true);
        BeanContainer container = new BeanContainer(SpecializationBean.class);
        container.setBeanIdProperty("id");
        container.addAll(SpecializationBean.getAllSpecializations());
        GeneratedPropertyContainer wrappingContainer = new GeneratedPropertyContainer(container);

        //счетчик строк
        wrappingContainer.addGeneratedProperty("rowHeader", new PropertyValueGenerator<Integer>() {
            @Override
            public Integer getValue(Item item, Object itemId, Object propertyId) {
                return container.indexOfId(itemId) + 1;
            }
            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }
        });

        //кнопка удаления
        wrappingContainer.addGeneratedProperty("delete", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "";
            }
            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        grid.setContainerDataSource(wrappingContainer);
        grid.setColumns("rowHeader","name","delete");
        grid.getColumn("name").setExpandRatio(1);


        Grid.HeaderRow header = grid.getDefaultHeaderRow();
        header.getCell("name").setHtml("Наименование");

        grid.getColumn("rowHeader").setHeaderCaption("").setHidable(false).setEditable(false).setResizable(false);//.setWidth(30);
        grid.setFrozenColumnCount(1);

        grid.setEditorSaveCaption("Сохранить");
        grid.setEditorCancelCaption("Отмена");
        grid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {}
            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent) throws FieldGroup.CommitException {
                try{
                    Item gpi = ((GeneratedPropertyContainer.GeneratedPropertyItem) commitEvent.getFieldBinder().getItemDataSource()).getWrappedItem();
                    Object item = ((BeanItem) gpi).getBean();
                    objectsConverterService.convertToSpringBean(((SpecializationBean) item)).saveAndFlush();
                }catch (Exception e){
                    Notification.show("Ошика записи",e.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        grid.getColumn("delete")
                .setRenderer(new ButtonRenderer(event -> {
                    Long itemId = (Long) event.getItemId();
                    MessageBox.createWarning()
                            .withHtmlMessage("<font color=\"red\"> Удалить специализацию <b>\"" + ((GeneratedPropertyContainer) grid.getContainerDataSource()).getContainerProperty(itemId,"name") + "\"</b>?</font>")
                            .withOkButton(()->{deleteSpecialization(itemId);}, ButtonOption.caption("Удалить"), ButtonOption.style(ValoTheme.BUTTON_DANGER),ButtonOption.icon(FontAwesome.TRASH))
                            .withCancelButton(()->{},ButtonOption.caption("Отмена"))
                            .open();
                }))
                .setHeaderCaption("")
                .setHidable(false)
                .setEditable(false)
                .setResizable(false);

        //filters
        PulseUI.getPulseHelper().setRowFilters(grid,container);

    }

    private void buildGridUsers(){

        tree.setVisible(false);
        grid.setVisible(true);
        grid.removeAllColumns();
        grid.addStyleName("grid-button-delete");

        grid.setEditorEnabled(false);
        BeanContainer container = new BeanContainer(UserBean.class);
        container.setBeanIdProperty("id");
        container.addAll(UserBean.findAll());
        GeneratedPropertyContainer wrappingContainer = new GeneratedPropertyContainer(container);

        //счетчик строк
        wrappingContainer.addGeneratedProperty("rowHeader", new PropertyValueGenerator<Integer>() {
            @Override
            public Integer getValue(Item item, Object itemId, Object propertyId) {
                return container.indexOfId(itemId) + 1;
            }
            @Override
            public Class<Integer> getType() {
                return Integer.class;
            }
        });

        //кнопка удаления
        wrappingContainer.addGeneratedProperty("delete", new PropertyValueGenerator<String>() {
            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "";
            }
            @Override
            public Class<String> getType() {
                return String.class;
            }
        });
        grid.setContainerDataSource(wrappingContainer);
        grid.setColumns("rowHeader","fullName","specialization","department","role","delete");
        grid.getColumn("fullName").setExpandRatio(1);
        grid.addItemClickListener(event -> {
            if (event.isDoubleClick()){
                Long id = (Long) event.getItemId();
                UserBean userBean = PulseUI.getObjectsConverterService().convertToSpringBean(UserBean.findOne(id));
                getUI().addWindow(new CreateEditUserWindow(userBean));
            }
        });

        Grid.HeaderRow header = grid.getDefaultHeaderRow();
        header.getCell("fullName").setHtml("ФИО");
        header.getCell("specialization").setHtml("Специализация");
        header.getCell("department").setHtml("Основное отделение");
        header.getCell("role").setHtml("Роль");
        header.getCell("delete").setHtml("");

        grid.getColumn("rowHeader").setHeaderCaption("").setHidable(false).setEditable(false).setResizable(false);//.setWidth(30);
        grid.setFrozenColumnCount(1);

        grid.getColumn("delete")
                .setRenderer(new ButtonRenderer(event -> {
                    Long itemId = (Long) event.getItemId();
                    MessageBox.createWarning()
                            .withHtmlMessage("<font color=\"red\"> Удалить пользователя <b>\"" + ((GeneratedPropertyContainer) grid.getContainerDataSource()).getContainerProperty(itemId,"name") + "\"</b>?</font>")
                            .withOkButton(()->{deleteUser(itemId);}, ButtonOption.caption("Удалить"), ButtonOption.style(ValoTheme.BUTTON_DANGER),ButtonOption.icon(FontAwesome.TRASH))
                            .withCancelButton(()->{},ButtonOption.caption("Отмена"))
                            .open();
                }))
                .setHidable(false)
                .setEditable(false)
                .setResizable(false);

        //filters
        PulseUI.getPulseHelper().setRowFilters(grid,container);

    }

    private HierarchicalContainer getSourceContainerVenues(){
        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty("name", String.class, null);
        hierarchicalContainer.addContainerProperty("type", String.class, null);

        List<HospitalBean> listHospitals = HospitalBean.getAllHospitals();
        listHospitals.forEach(hierarchicalContainer::addItem);

        List<VenueBean> listVenues = VenueBean.findAll();
        listVenues.forEach(hierarchicalContainer::addItem);

        //ThemeResource iconRoom = new ThemeResource("img/room.png");

        for (Object item: hierarchicalContainer.getItemIds()){
            if (item.getClass().equals(VenueBean.class)){
                VenueBean venueBean = (VenueBean) item;
                hierarchicalContainer.getItem(venueBean).getItemProperty("name").setValue(venueBean.getName());
                hierarchicalContainer.getItem(venueBean).getItemProperty("type").setValue(venueBean.getType().getView());
                if (venueBean.getType().equals(VenueType.DEPARTMEN)) {
                    hierarchicalContainer.setParent(item, venueBean.getHospital());
                    tree.setItemIcon(item, FontAwesome.HOSPITAL_O);
                }else if (venueBean.getType().equals(VenueType.HOSPITAL_ROOM)){
                    hierarchicalContainer.setParent(item,venueBean.getOwner());
                    hierarchicalContainer.setChildrenAllowed(venueBean, false);
                    tree.setItemIcon(item, FontAwesome.BED);
                }
            }
            else{
                HospitalBean hospitalBean = (HospitalBean) item;
                hierarchicalContainer.getItem(hospitalBean).getItemProperty("name").setValue(hospitalBean.getName());
                hierarchicalContainer.getItem(hospitalBean).getItemProperty("type").setValue("Госпиталь");
                tree.setItemIcon(item, FontAwesome.HOSPITAL_O);
            }
        }
        return hierarchicalContainer;
    }

    private HierarchicalContainer getSourseContainerMKB10(){

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addContainerProperty("code", String.class, null);
        hierarchicalContainer.addContainerProperty("name", String.class, null);

        MKB10Bean.findAll().forEach(hierarchicalContainer::addItem);
        for (Object item: hierarchicalContainer.getItemIds()){
            MKB10Bean mkb10Bean = (MKB10Bean) item;
            Item itemRow = hierarchicalContainer.getItem(item);
            itemRow.getItemProperty("code").setValue(mkb10Bean.getCode());
            itemRow.getItemProperty("name").setValue(mkb10Bean.getName());
            if (mkb10Bean.getParent() != null){
                hierarchicalContainer.setParent(item, mkb10Bean.getParent());
//                for (Object o: hierarchicalContainer.getItemIds()){
//                    if (o.equals(mkb10Bean.getParent())){
//                        hierarchicalContainer.setParent(item, o);
//                        //break;
//                    }
//                }
                //hierarchicalContainer.setChildrenAllowed(mkb10Bean, false);
            }
        }

        return hierarchicalContainer;
    }

    private void buildTreeVenues(){

        tree.setVisible(true);
        grid.setVisible(false);

        tree.setContainerDataSource(getSourceContainerVenues());

        tree.setColumnHeader("name","Наименование");
        tree.setColumnHeader("type","Тип");
        tree.addGeneratedColumn("btnDelete", new Table.ColumnGenerator() {
            @Override
            public Object generateCell(Table source, Object itemId, Object columnId) {
                if (itemId.getClass().equals(HospitalBean.class)){
                    return null;
                }
                Button btnDelete = new Button();
                btnDelete.setIcon(FontAwesome.TRASH);
                btnDelete.addStyleName(ValoTheme.BUTTON_DANGER);
                btnDelete.addClickListener(event -> {
                    VenueBean venueBean = (VenueBean) itemId;
                    MessageBox.createWarning()
                            .withHtmlMessage("<font color=\"red\"> Удалить " + venueBean.getType().getView() + " <b>" + venueBean.getName() + "</b>?</font>")
                            .withOkButton(()->{deleteVenue((VenueBean)itemId);}, ButtonOption.caption("Удалить"), ButtonOption.style(ValoTheme.BUTTON_DANGER),ButtonOption.icon(FontAwesome.TRASH))
                            .withCancelButton(()->{},ButtonOption.caption("Отмена"))
                            .open();
                });
                return btnDelete;
            }
        });
        tree.setColumnHeader("btnDelete","");
        tree.setColumnExpandRatio("name",1);

        tree.addItemClickListener(event -> {
            if (event.isDoubleClick()){
                if (event.getItemId().getClass().equals(HospitalBean.class)){
                    HospitalBean hospitalBean = PulseUI.getObjectsConverterService().convertToSpringBean((HospitalBean)event.getItemId());
                    getUI().addWindow(new CreateEditHospitalWindow(hospitalBean));
                }
                else {
                    VenueBean venueBean = PulseUI.getObjectsConverterService().convertToSpringBean((VenueBean)event.getItemId());
                    getUI().addWindow(new CreateEditVenueWindow(venueBean));
                }
            }
        });

    }

    private void buildTreeMKB10(){

        tree.removeAllItems();
        tree.setVisible(true);
        grid.setVisible(false);

        tree.setContainerDataSource(getSourseContainerMKB10());

        tree.setColumnHeader("name","Наименование");
        tree.setColumnHeader("code","Код");

        tree.setColumnExpandRatio("name",1);

    }


    private void updateContentByType(){

       // removeAllComponents();
        content.setVisible(true);

        if (type.equals(PulseViewType.HOSPITALS)) {
            title.setValue("Госпитали");
            addBtn.addClickListener(event -> {
                getUI().addWindow(new CreateEditHospitalWindow((HospitalBean) PulseUI.getSpringBean("hospitalBeanSpring")));
            });
            buildGridHospitals();
        }
        else if (type.equals(PulseViewType.SPECIALISATION)){
            title.setValue("Специализации персонала");
            addBtn.addClickListener(event -> {
                getUI().addWindow(new CreateEditSpecialisationWindow((SpecializationBean) PulseUI.getSpringBean(SpecializationBean.NAME_SPRING_BEAN)));
            });
            buildGridSpecialisations();
        }
        else if (type.equals(PulseViewType.USERS)){
            title.setValue("Пользователи");
            addBtn.addClickListener(event -> {
                getUI().addWindow(new CreateEditUserWindow((UserBean) PulseUI.getSpringBean(UserBean.NAME_SPRING_BEAN)));
            });
            buildGridUsers();
        }
        else if (type.equals(PulseViewType.VENUES)){
            title.setValue("Отделения и палаты");
            addBtn.addClickListener(event -> {
                getUI().addWindow(new CreateEditVenueWindow((VenueBean) PulseUI.getSpringBean(VenueBean.NAME_SPRING_BEAN)));
            });
            buildTreeVenues();
        }
        else if (type.equals(PulseViewType.MKB10)){
            title.setValue("Международный классификатор болезней");
            content.setVisible(false);
            buildTreeMKB10();
        }
    }

    private void deleteHospital(Long hospitalId){
        try {
            HospitalBean.deleteById(hospitalId);
            grid.getContainerDataSource().removeItem(hospitalId);
        } catch (Exception e) {
            Notification.show("Ошибка удаления",e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void deleteVenue(VenueBean venueBean){
        try {
            VenueBean.deleteById(venueBean.getId());
            tree.getContainerDataSource().removeItem(venueBean);
        } catch (Exception e) {
            Notification.show("Ошибка удаления",e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }



    private void deleteSpecialization(Long itemId) {
        try {
            SpecializationBean.deleteById(itemId);
            grid.getContainerDataSource().removeItem(itemId);
        } catch (Exception e) {
            Notification.show("Ошибка удаления",e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    private void deleteUser(Long itemId) {
        try {
            UserBean.deleteById(itemId);
            grid.getContainerDataSource().removeItem(itemId);
        } catch (Exception e) {
            Notification.show("Ошибка удаления",e.getMessage(), Notification.Type.ERROR_MESSAGE);
        }
    }

    @Subscribe
    public void postViewChange(final PulseEvent.PostViewChangeEvent event){
        type = ((PulseNavigator) PulseUI.getCurrent().getNavigator()).getCurrentViewType();
        updateContentByType();
    }

    @Subscribe
    public void updateHospitalsList(final PulseEvent.CreateEditHospital event){
        GeneratedPropertyContainer wrapCont = (GeneratedPropertyContainer) grid.getContainerDataSource();
        BeanContainer source = (BeanContainer) wrapCont.getWrappedContainer();
        source.addAll(HospitalBean.getAllHospitals());
        grid.setContainerDataSource(wrapCont);
    }

    @Subscribe
    public void updateSpecializationList(final PulseEvent.CreateEditSpecializationEvent event){
        GeneratedPropertyContainer wrapCont = (GeneratedPropertyContainer) grid.getContainerDataSource();
        BeanContainer source = (BeanContainer) wrapCont.getWrappedContainer();
        source.addAll(SpecializationBean.getAllSpecializations());
        grid.setContainerDataSource(wrapCont);
    }

    @Subscribe
    public void updateUsersList(final PulseEvent.CreateEditUserEvent event){
        GeneratedPropertyContainer wrapCont = (GeneratedPropertyContainer) grid.getContainerDataSource();
        BeanContainer source = (BeanContainer) wrapCont.getWrappedContainer();
        source.removeAllItems();
        source.addAll(UserBean.findAll());
        grid.setContainerDataSource(wrapCont);
        //grid.getColumn("fullName").getEditorField().focus();
        //grid.refreshRows();
    }

    @Subscribe
    public void updateVenues(final PulseEvent.CreateEditVenueEvent event){
        tree.setContainerDataSource(getSourceContainerVenues());
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {}

    @Override
    public void detach() {
        super.detach();
        // A new instance of TransactionsView is created every time it's
        // navigated to so we'll need to clean up references to it on detach.
        PulseEventBus.unregister(this);
    }

}

