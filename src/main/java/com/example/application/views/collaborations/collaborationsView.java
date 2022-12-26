package com.example.application.views.collaborations;

import com.example.application.data.entity.Student;
import com.example.application.data.service.StudentService;
import com.example.application.views.MainLayout;
import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("قاعة التعاونات")
@Route(value = "Collaboration-room/:studentID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("USER")
public class collaborationsView extends Div implements BeforeEnterObserver {

    private final String STUDENT_ID = "studentID";
    private final String STUDENT_EDIT_ROUTE_TEMPLATE = "Collaboration-room/%s/edit";

    private final Grid<Student> grid = new Grid<>(Student.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField current_class;
    private TextField studentid;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final CollaborationBinder<Student> binder;

    private Student student;

    private final StudentService studentService;

    public collaborationsView(StudentService studentService) {
        this.studentService = studentService;
        addClassNames("قاعةالتعاونات-view");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("current_class").setAutoWidth(true);
        grid.addColumn("studentid").setAutoWidth(true);
        grid.setItems(query -> studentService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(STUDENT_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(collaborationsView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Student.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(studentid, String.class).withConverter(new StringToUuidConverter("Invalid UUID"))
                .bind("studentid");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.student == null) {
                    this.student = new Student();
                }
                binder.writeBean(this.student);
                studentService.update(this.student);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(collaborationsView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> studentId = event.getRouteParameters().get(STUDENT_ID).map(Long::parseLong);
        if (studentId.isPresent()) {
            Optional<Student> studentFromBackend = studentService.get(studentId.get());
            if (studentFromBackend.isPresent()) {
                populateForm(studentFromBackend.get());
            } else {
                Notification.show(String.format("The requested student was not found, ID = %d", studentId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(collaborationsView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        dateOfBirth = new DatePicker("Date Of Birth");
        current_class = new TextField("Current_class");
        studentid = new TextField("Studentid");
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, current_class, studentid);

        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Student value) {
        this.student = value;
        String topic = null;
        if (this.student != null && this.student.getId() != null) {
            topic = "student/" + this.student.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.student);
        avatarGroup.setTopic(topic);

    }
}
