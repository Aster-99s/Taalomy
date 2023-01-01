package com.example.application.views.addstudent;

import com.example.application.data.entity.SamplePerson;
import com.example.application.data.service.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.Locale;

@PageTitle("إضافة تلميذ")
@Route(value = "Student-addition", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class addStudentView extends Div {

    Locale FRlocal = new Locale("fr", "FR");

    private TextField firstName = new TextField("الأسم");
    private TextField lastName = new TextField("اللقب");
    private EmailField email = new EmailField("البريد الالكتروني");
    private DatePicker dateOfBirth = new DatePicker("تاريخ الميلاد");
    private PhoneNumberField phone = new PhoneNumberField("رقم الهاتف");
    private TextField occupation = new TextField("المستوى");

    private Button cancel = new Button("إلغاء");
    private Button save = new Button("حفظ");

    private Binder<SamplePerson> binder = new Binder<>(SamplePerson.class);

    public addStudentView(SamplePersonService personService) {
        //RTL Support
        final UI ui = UI.getCurrent();
        ui.setDirection(Direction.RIGHT_TO_LEFT);

        dateOfBirth.setLocale(Locale.FRANCE);


        addClassName("إضافةتلميذ-view");
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        binder.bindInstanceFields(this);
        clearForm();

        cancel.addClickListener(e -> clearForm());
        save.addClickListener(e -> {
            personService.update(binder.getBean());
            Notification.show(binder.getBean().getClass().getSimpleName() + " تم الحفظ ");
            clearForm();
        });
    }

    private void clearForm() {
        binder.setBean(new SamplePerson());
    }

    private Component createTitle() {
        return new H3("المعلومات الشخصية للتلميذ");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        email.setErrorMessage("الرجاء إدخال بريد إلكتروني صحيح !");
        formLayout.add(firstName, lastName, dateOfBirth, phone, email, occupation);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save);
        buttonLayout.add(cancel);
        return buttonLayout;
    }

    private static class PhoneNumberField extends CustomField<String> {
        private ComboBox<String> countryCode = new ComboBox<>();
        private TextField number = new TextField();

        public PhoneNumberField(String label) {
            setLabel(label);
            number.setAllowedCharPattern("\\d");
            HorizontalLayout layout = new HorizontalLayout( number);
            layout.setFlexGrow(1.0, number);
            add(layout);
        }

        @Override
        protected String generateModelValue() {
            if (number.getValue() != null) {
                String s = number.getValue();
                return s;
            }
            return "";
        }

        @Override
        protected void setPresentationValue(String phoneNumber) {
            String[] parts = phoneNumber != null ? phoneNumber.split(" ", 2) : new String[0];
            if (parts.length == 1) {
                countryCode.clear();
                number.setValue(parts[0]);
            } else if (parts.length == 2) {
                countryCode.setValue(parts[0]);
                number.setValue(parts[1]);
            } else {
                countryCode.clear();
                number.clear();
            }
        }
    }

}
