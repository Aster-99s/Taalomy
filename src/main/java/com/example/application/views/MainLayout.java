package com.example.application.views;

import com.example.application.components.appnav.AppNav;
import com.example.application.components.appnav.AppNavItem;
import com.example.application.data.entity.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.addstudent.addStudentView;
import com.example.application.views.books.booksView;
import com.example.application.views.crossforums.crossForumsView;
import com.example.application.views.studentlist.studentListView;
import com.example.application.views.noting.notingView;
import com.example.application.views.chats.chatsView;
import com.example.application.views.collaborations.collaborationsView;
import com.example.application.views.admindashboard.adminDashboardView;
import com.example.application.views.teacherdashboard.teacherDashboardView;
import com.vaadin.flow.component.Direction;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        //RTL Support
        final UI ui = UI.getCurrent();
            ui.setDirection(Direction.RIGHT_TO_LEFT);
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();

    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");
        toggle.getThemeNames().set("dark", true);
        toggle.setId("header");
        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        addToNavbar(true,toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("تعلمي");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());
        addToDrawer(header, scroller, createFooter());
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        if (accessChecker.hasAccess(booksView.class)) {
            nav.addItem(new AppNavItem("مكتبتي", booksView.class ,new Icon(VaadinIcon.OPEN_BOOK)));

        }
        if (accessChecker.hasAccess(teacherDashboardView.class)) {
            nav.addItem(new AppNavItem("لوحة تحكم الأستاذ", teacherDashboardView.class, new Icon(VaadinIcon.CHART_TIMELINE)));

        }
        if (accessChecker.hasAccess(crossForumsView.class)) {
            nav.addItem(new AppNavItem("الأحداث والنقاشات العامة", crossForumsView.class, new Icon(VaadinIcon.MEGAPHONE)));

        }

        if (accessChecker.hasAccess(studentListView.class)) {
            nav.addItem(new AppNavItem("التلاميذ", studentListView.class, new Icon(VaadinIcon.LIST_SELECT)));

        }
        if (accessChecker.hasAccess(notingView.class)) {
            nav.addItem(new AppNavItem("التنقيط", notingView.class, new Icon(VaadinIcon.FILE_TABLE)));

        }
        if (accessChecker.hasAccess(chatsView.class)) {
            nav.addItem(new AppNavItem("المحادثات", chatsView.class, new Icon(VaadinIcon.COMMENTS)));

        }
        if (accessChecker.hasAccess(adminDashboardView.class)) {
            nav.addItem(new AppNavItem("لوحة التحكم ", adminDashboardView.class, new Icon(VaadinIcon.COGS)));

        }
        if (accessChecker.hasAccess(collaborationsView.class)) {
            nav.addItem(new AppNavItem("قاعة التعاونات", collaborationsView.class, new Icon(VaadinIcon.GROUP)));

        }
        if (accessChecker.hasAccess(addStudentView.class)) {
            nav.addItem(new AppNavItem("إضافة تلميذ", addStudentView.class, new Icon(VaadinIcon.EDIT)));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
