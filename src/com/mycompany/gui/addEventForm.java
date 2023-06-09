/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gui;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.ScaleImageLabel;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.ButtonGroup;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.util.Resources;
import com.mycomany.entities.Event;
import com.mycompany.services.EventService;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author fatha
 */
public class addEventForm extends BaseForm {

    Form current;

    public addEventForm(Resources res) {
        super("Newsfeed", BoxLayout.y()); //herigate men Newsfeed w l formulaire vertical

        Toolbar tb = new Toolbar(true);
        current = this;
        setToolbar(tb);
        getTitleArea().setUIID("Container");
        Form previous = Display.getInstance().getCurrent();
        tb.setBackCommand("", e -> previous.showBack());

        setTitle("Add Event");
        setLayout(BoxLayout.y());
        getContentPane().setScrollVisible(false);
        super.addSideMenu(res);
        tb.addSearchCommand(e -> {

        });

        Tabs swipe = new Tabs();

        Label s1 = new Label();
        Label s2 = new Label();

        addTab(swipe, s1, res.getImage("back-logo.jpeg"), "", "", res);

        //
        swipe.setUIID("Container");
        swipe.getContentPane().setUIID("Container");
        swipe.hideTabs();

        ButtonGroup bg = new ButtonGroup();
        int size = Display.getInstance().convertToPixels(1);
        Image unselectedWalkthru = Image.createImage(size, size, 0);
        Graphics g = unselectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAlpha(100);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        Image selectedWalkthru = Image.createImage(size, size, 0);
        g = selectedWalkthru.getGraphics();
        g.setColor(0xffffff);
        g.setAntiAliased(true);
        g.fillArc(0, 0, size, size, 0, 360);
        
        RadioButton[] rbs = new RadioButton[swipe.getTabCount()];
        FlowLayout flow = new FlowLayout(Component.CENTER);
        flow.setValign(BOTTOM);
        Container radioContainer = new Container(flow);
        for (int iter = 0; iter < rbs.length; iter++) {
            rbs[iter] = RadioButton.createToggle(unselectedWalkthru, bg);
            rbs[iter].setPressedIcon(selectedWalkthru);
            rbs[iter].setUIID("Label");
            radioContainer.add(rbs[iter]);
        }

        rbs[0].setSelected(true);
        swipe.addSelectionListener((i, ii) -> {
            if (!rbs[ii].isSelected()) {
                rbs[ii].setSelected(true);
            }
        });

        Component.setSameSize(radioContainer, s1, s2);
        add(LayeredLayout.encloseIn(swipe, radioContainer));

        ButtonGroup barGroup = new ButtonGroup();
        RadioButton mesListes = RadioButton.createToggle("My Events", barGroup);
        mesListes.setUIID("SelectBar");
        RadioButton liste = RadioButton.createToggle("Other", barGroup);
        liste.setUIID("SelectBar");
        RadioButton partage = RadioButton.createToggle("Add Event", barGroup);
        partage.setUIID("SelectBar");
        Label arrow = new Label(res.getImage("news-tab-down-arrow.png"), "Container");

        mesListes.addActionListener((e) -> {
            InfiniteProgress ip = new InfiniteProgress();
            final Dialog ipDlg = ip.showInifiniteBlocking();

            //  ListReclamationForm a = new ListReclamationForm(res);
            //  a.show();
            refreshTheme();
        });

        add(LayeredLayout.encloseIn(
                GridLayout.encloseIn(3, mesListes, liste, partage),
                FlowLayout.encloseBottom(arrow)
        ));

        partage.setSelected(true);
        arrow.setVisible(false);
        addShowListener(e -> {
            arrow.setVisible(true);
            updateArrowPosition(partage, arrow);
        });
        bindButtonSelection(mesListes, arrow);
        bindButtonSelection(liste, arrow);
        bindButtonSelection(partage, arrow);
        // special case for rotation
        addOrientationListener(e -> {
            updateArrowPosition(barGroup.getRadioButton(barGroup.getSelectedIndex()), arrow);
        });

        //
        TextField eventName = new TextField("", "Enter Event Name!!");
        eventName.setUIID("TextFieldBlack");
        addStringValue("eventName", eventName);

        TextField description = new TextField("", "entrer description!!");
        description.setUIID("TextFieldBlack");
        addStringValue("Description", description);

        TextField location = new TextField("", "Enter Location!!");
        location.setUIID("TextFieldBlack");
        addStringValue("Location", location);

        TextField maxAttendees = new TextField("", "Enter Max Attendees!!");
        maxAttendees.setUIID("TextFieldBlack");
        addStringValue("MaxAttendees", maxAttendees);

        Button btnAjouter = new Button("Ajouter");
        addStringValue("", btnAjouter);
        eventName.refreshTheme();
        description.refreshTheme();
        location.refreshTheme();
        maxAttendees.refreshTheme();

        //onclick button event 
        btnAjouter.addActionListener((e) -> {

            try {

                if (eventName.getText().equals("") || description.getText().equals("")) {
                    Dialog.show("Please verify your information", "", "Cancel", "OK");
                } else {
                    InfiniteProgress ip = new InfiniteProgress(); //Loading  after insert data

                    final Dialog iDialog = ip.showInfiniteBlocking();

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                    //njibo iduser men session (current user)
                    Event event = new Event(String.valueOf(eventName.getText()
                    ).toString(),
                            String.valueOf(description.getText()).toString(),
                            
                            format.format(new Date()),
                            format.format(new Date()),
                            String.valueOf(location.getText()).toString(),
                            Integer.parseInt(String.valueOf(maxAttendees.getText())),
                            
                            format.format(new Date())
                    );

                    System.out.println("data  event == " + event);

                    //appelle methode ajouterReclamation mt3 service Reclamation bch nzido données ta3na fi base 
                    EventService.getInstance().addEvent(event);

                    iDialog.dispose(); //na7io loading ba3d ma3mlna ajout

                    //ba3d ajout net3adaw lel ListREclamationForm
                //    new EventListForm(res).show();
                    refreshTheme();//Actualisation

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        });
    }

    private void addStringValue(String s, Component v) {

        add(BorderLayout.west(new Label(s, "PaddedLabel"))
                .add(BorderLayout.CENTER, v));
        add(createLineSeparator(0xeeeeee));
    }

    private void addTab(Tabs swipe, Label spacer, Image image, String string, String text, Resources res) {
        if (swipe == null ) {
            System.out.println("swipe is null");
            return; // do nothing and return
        }
if (image == null ) {
            System.out.println("image is null");
            return; // do nothing and return
        }
        int size = Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());

        if (image.getHeight() < size) {
            image = image.scaledHeight(size);
        }

        if (image.getHeight() > Display.getInstance().getDisplayHeight() / 2) {
            image = image.scaledHeight(Display.getInstance().getDisplayHeight() / 2);
        }

        ScaleImageLabel imageScale = new ScaleImageLabel(image);
        imageScale.setUIID("Container");
        imageScale.setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);

        Label overLay = new Label("", "ImageOverlay");

        Container page1 = LayeredLayout.encloseIn(
                imageScale,
                overLay,
                BorderLayout.south(
                        BoxLayout.encloseY(
                                new SpanLabel(text, "LargeWhiteText"),
                                spacer
                        )
                )
        );

        swipe.addTab("", res.getImage("back-logo.jpeg"), page1);
    }

    public void bindButtonSelection(Button btn, Label l) {

        btn.addActionListener(e -> {
            if (btn.isSelected()) {
                updateArrowPosition(btn, l);
            }
        });
    }

    private void updateArrowPosition(Button btn, Label l) {

        l.getUnselectedStyle().setMargin(LEFT, btn.getX() + btn.getWidth() / 2 - l.getWidth() / 2);
        l.getParent().repaint();
    }
}
